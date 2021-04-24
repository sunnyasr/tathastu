package com.tathastushop.app.Models;

import java.io.Serializable;

public class LevelModel implements Serializable {

    private String level;
    private String count;


    public LevelModel() {
    }

    public LevelModel(String level, String count) {
        this.level = level;
        this.count = count;

    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
