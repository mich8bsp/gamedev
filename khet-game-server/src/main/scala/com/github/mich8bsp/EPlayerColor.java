package com.github.mich8bsp;

public enum EPlayerColor {
    GREY, RED;

    public EPlayerColor other(){
        if(this == GREY){
            return RED;
        }else{
            return GREY;
        }
    }
}
