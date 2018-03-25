package model;

import java.util.ArrayList;

public class Handbook {

    private String name;
    private ArrayList<Chapter> chapters;

    public Handbook() {}

    public Handbook(String name, ArrayList<Chapter> chapters) {
        this.name = name;
        this.chapters = chapters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }
}
