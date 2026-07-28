package com.github.generations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;

public class GameWorldParser {

    public static GameWorld parse(String layoutPath) {
        FileHandle levelLayout = Gdx.files.internal(layoutPath);
        BufferedReader reader = new BufferedReader(levelLayout.reader());
        String line;
        GameWorld world = null;
        try{
            while((line = reader.readLine())!=null){
                String[] lineSpl = line.split(",");
                if(world == null){
                    world = new GameWorld(Integer.parseInt(lineSpl[0]), Integer.parseInt(lineSpl[1]));
                }else{
                    int row = Integer.parseInt(lineSpl[0]);
                    int col = Integer.parseInt(lineSpl[1]);
                    if(lineSpl[2].equals("X")){
                        world.populateCell(row, col, ECellType.BLOCKER, null);
                    }else{
                        int colR = Integer.parseInt(lineSpl[2]);
                        int colG = Integer.parseInt(lineSpl[3]);
                        int colB = Integer.parseInt(lineSpl[4]);
                        world.populateCell(row, col, ECellType.GAME_UNIT, new GameUnit(colR, colG, colB));
                    }
                }
            }
        }catch (Exception e){
            System.out.println("shit");
        }

        return world;
    }

}
