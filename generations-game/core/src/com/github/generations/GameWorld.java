package com.github.generations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

public class GameWorld extends Actor implements IGameInfoSupplier {

    private int currentGeneration = 1;
    private float timeInCurrentGen = 0f;
    private final int cols;
    private final int rows;
    private GameUnit[][] units;
    private ECellType[][] cellTypeMapping;
    private static final int CELL_SIZE = 30;
    private static final double SICKNESS_SDV_LIMIT = 0.1;
    private boolean running = false;
    private Texture cellTexture;
    private int totalScore = 0;
    public static final int LAST_GENERATION = 1000;
    private ISpeedManager speedManager;


    public GameWorld(int rows, int cols){
        this.cellTexture = new Texture(Gdx.files.internal("cell.png"));
        this.rows = rows;
        this.cols = cols;
        units = new GameUnit[rows][cols];
        cellTypeMapping = new ECellType[rows][cols];
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                cellTypeMapping[i][j]= ECellType.EMPTY;
            }
        }

    }

    public void populateCell(int row, int col, ECellType cellType, GameUnit unit){
        if(cellType.equals(ECellType.GAME_UNIT)){
            this.units[row][col] = unit;
        }
        this.cellTypeMapping[row][col] = cellType;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public ECellType getCellType(int row, int col) {
        return cellTypeMapping[row][col];
    }

    public GameUnit getGameUnit(int row, int col) {
        return units[row][col];
    }

    public int getScore(){
        return (currentGeneration > 0) ? totalScore / currentGeneration : 0;
    }
    public int getCurrentGenerationScore() {
        int score = 0;
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(units[i][j]!=null && units[i][j].isAlive()){
                    score++;
                }
            }
        }
        return score;
    }

    @Override
    public int getGeneration() {
        return currentGeneration;
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        int rows = getRows();
        int cols = getCols();
        for(int i=0; i< rows; i++){
            for(int j=0; j< cols; j++){
                ECellType cellType = getCellType(i,j);
                if(cellType == ECellType.EMPTY){
                    drawCell(batch, i, j, Color.GRAY);
                    continue;
                }
                if(cellType == ECellType.BLOCKER){
                    drawCell(batch, i, j, Color.DARK_GRAY);
                }else if(cellType == ECellType.GAME_UNIT){
                    GameUnit unit = getGameUnit(i, j);
                    Color c = unit.getColor();
                    if(unit.isAlive()) {
                        drawCell(batch, i, j, c);
                    }
                }
            }
        }
    }

    private void drawCell(Batch batch, int i, int j, Color c){
        Color prevColor = batch.getColor();
        batch.setColor(c);
        batch.draw(cellTexture, j*CELL_SIZE, i*CELL_SIZE, CELL_SIZE, CELL_SIZE);
        batch.setColor(prevColor);
    }

    public void toggleRunning(){
        this.running = !this.running;
    }


    @Override
    public void act(float dt){
        super.act(dt);
        if(Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            this.running = true;
        }
        if(currentGeneration>LAST_GENERATION){
            currentGeneration = LAST_GENERATION;
        }
        if(running && currentGeneration<LAST_GENERATION) {
            double generationLifespan = (speedManager.getSpeed()==0) ? 0.1 : 0.1 / speedManager.getSpeed();
            float timeSinceCurrGen = this.timeInCurrentGen + dt;
            int newGeneration = this.currentGeneration + (int) (timeSinceCurrGen / generationLifespan);
            this.timeInCurrentGen = timeSinceCurrGen % (float)generationLifespan;

            if (newGeneration > currentGeneration) {
                for (int i = 0; i < (newGeneration - currentGeneration); i++) {
                    advanceGeneration();
                    totalScore += getCurrentGenerationScore();
                }
            }
            this.currentGeneration = newGeneration;
        }
    }

    private void advanceGeneration(){
        GameUnit[][] newGeneration = new GameUnit[rows][cols];
        for(int i=0;i<getRows();i++){
            for(int j=0;j<getCols();j++){
                if(getCellType(i,j) == ECellType.BLOCKER){
                    continue;
                }
                List<GameUnit> neighbors = getNeighbors(i, j);
                int numN = neighbors.size();
                if(getCellType(i,j) == ECellType.EMPTY && numN == 3){
                    newGeneration[i][j] = reproduceFromNeighbors(neighbors);
                }else if(getCellType(i, j) == ECellType.GAME_UNIT){
                    if(units[i][j].isAlive() && (numN > 3 || numN <2)){
                        newGeneration[i][j] = units[i][j].copy();
                        newGeneration[i][j].kill();
                    }else if(!units[i][j].isAlive() && numN == 3){
                        newGeneration[i][j] = reproduceFromNeighbors(neighbors);
                    }else{
                        newGeneration[i][j] = units[i][j];
                        newGeneration[i][j].addGeneration();
                    }
                }
            }
        }
        this.units = newGeneration;
        updateCellTypes();
    }

    private GameUnit reproduceFromNeighbors(List<GameUnit> neighbors){
        float sumR = 0f;
        float sumG = 0f;
        float sumB = 0f;
        int n = neighbors.size();
        for(GameUnit unit : neighbors){
            sumR += unit.getColor().r;
            sumG += unit.getColor().g;
            sumB += unit.getColor().b;
        }
        float avgR = sumR/n;
        float avgG = sumG/n;
        float avgB = sumB/n;
        boolean isSick = determineGeneticSickness(neighbors, avgR, avgG, avgB);
        return new GameUnit(new Color(avgR, avgG, avgB, 1f), isSick, 0);
    }

    private boolean determineGeneticSickness(List<GameUnit> neighbors,
                                             float avgR, float avgG, float avgB){
        double variance = 0;
        int n = neighbors.size();
        for(GameUnit unit : neighbors){
            variance += Math.pow(unit.getColor().r - avgR, 2)/n;
            variance += Math.pow(unit.getColor().g - avgG, 2)/n;
            variance += Math.pow(unit.getColor().b - avgB, 2)/n;
        }
        double sdv = Math.sqrt(variance);
        return sdv < SICKNESS_SDV_LIMIT;
    }

    private void updateCellTypes(){
        for(int i=0;i<getRows();i++){
            for(int j=0;j<getCols();j++){
                if(units[i][j]!=null){
                    cellTypeMapping[i][j]=ECellType.GAME_UNIT;
                }else if(cellTypeMapping[i][j]==ECellType.GAME_UNIT){
                    cellTypeMapping[i][j]=ECellType.EMPTY;
                }
            }
        }
    }

    private List<GameUnit> getNeighbors(int i, int j){
        List<GameUnit> neighbors = new ArrayList<>();
        for(int ni=i-1; ni<i+2;ni++){
            for(int nj=j-1;nj<j+2;nj++){
                if(ni==i && nj==j){
                    continue;
                }
                if(ni<0 || nj<0 || ni>=getRows() || nj>=getCols()){
                    continue;
                }
                if(units[ni][nj]!=null && units[ni][nj].isAlive()){
                    neighbors.add(units[ni][nj]);
                }
            }
        }
        return neighbors;
    }

    public void setSpeedManager(ISpeedManager speedManager) {
       this.speedManager = speedManager;
    }
}
