package ui;

import model.Handbook;
import storage.Callable;
import storage.Storage;

import javax.swing.*;

public class SomeArea extends JTextArea implements Callable {

    Storage storage;

    public SomeArea() {
        super();
        storage = Storage.shared();
        this.setText("This is very interesting");
    }

    public void subscribe() {
        //storage.handleBooks(this);
        storage.getBookByUid("-L8EISHNcwp9SMkOCL4S", this);
    }


    @Override
    public void completion(Object obj, String event) {
        Handbook handbook = (Handbook)obj;
        this.append("\n" + handbook.getUid() + " " + handbook.getName() + "\n");
        handbook.getAuthors()
                .forEach(author -> this.append(author.getUid() + " " + author.getName() + " " + author.getSurname() + "\n"));
        handbook.getChapters()
                .forEach(chapter -> this.append(chapter.getUid() + " " + chapter.getName() + " " + chapter.getDescription() + " "+ chapter.getDescription() + "\n"));
        this.append("\n");
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void change(Object obj,  String event) {

    }

    @Override
    public void remove(Object obj, String event) {

    }
}
