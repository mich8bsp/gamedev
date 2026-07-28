package com.github.generations;

import com.badlogic.gdx.graphics.Color;

public class GameUnit {

    public static final int MAX_AGE_FOR_SICK = 5;

    private boolean isAlive = true;
    private Color color;
    private int age = 0;
    private boolean isSick = false;

    public GameUnit(Color color){
        this.color = color;
    }

    public GameUnit(Color color, boolean isSick, int age){
        this.color = color;
        this.isSick = isSick;
        this.age = age;
    }

    public GameUnit(int colorR, int colorG, int colorB){
        setColor(colorR, colorG, colorB);
    }

    public void kill(){
        this.isAlive = false;
    }

    public void revive(){
        this.isAlive = true;
    }

    public boolean isAlive(){
        return isAlive;
    }

    private void setColor(int colorR, int colorG, int colorB) {
        this.color = new Color(((float)colorR)/255, ((float)colorG)/255, ((float)colorB)/255, 1f);
    }

    public Color getColor() {
        return color;
    }

    public GameUnit copy() {
        return new GameUnit(this.color, this.isSick, this.age);
    }

    public void addGeneration() {
        this.age = this.age+1;
        if(isSick && age > MAX_AGE_FOR_SICK){
            //kill();
        }
    }
}
