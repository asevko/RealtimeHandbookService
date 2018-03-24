package model;

public class Chapter {

    private String uid;
    private String name;
    private String description;
    private String text;

    public Chapter() {}

    public Chapter(String name, String description, String text) {
        this.name = name;
        this.description = description;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getText() {
        return text;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
