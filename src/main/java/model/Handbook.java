package model;

import java.util.ArrayList;

public class Handbook {

    private String uid;
    private String name;
    private ArrayList<Author> authors;
    private ArrayList<Chapter> chapters;

    public Handbook() {}

    public Handbook(String name, ArrayList<Author> authors, ArrayList<Chapter> chapters) {
        this.name = name;
        this.authors = authors;
        this.chapters = chapters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
