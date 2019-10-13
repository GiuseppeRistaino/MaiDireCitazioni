package com.secondopianorcost.theoffice.maidirecitazioni.Entities;

/**
 * Created by giuse on 24/12/2017.
 */

public class Citation {

    private String characterName;
    private String name;
    private int flag;

    public Citation(String characterName, String name, int flag) {
        this.characterName = characterName;
        this.name = name;
        this.flag = flag;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
