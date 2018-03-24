package viewModel;

import extensions.CustomPair;
import model.Chapter;
import rx.Observable;
import storage.Callable;
import storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;

public class BookVewModel {

    private Storage storage;
    private HashMap<String, String> booksHash;
    private HashMap<String, String> chaptersHash;
    private String activeBookUid;
    private String activeChapterUid;

    public BookVewModel() {
        storage = Storage.shared();
        booksHash = new HashMap<>();
        chaptersHash = new HashMap<>();
    }

    public void getBookChapters(String bookUid, Callable callable) {
        storage.getBookChapters(bookUid, callable);
    }


    public void requestBookList(Callable callable) {
        storage.getBookList(callable);
    }

    public void setActiveBookUid(String activeBookUid) {
        this.activeBookUid = activeBookUid;
    }

    public void setActiveChapterUid(String activeChapterUid) {
        this.activeChapterUid = activeChapterUid;
    }

    public void addBookHash(CustomPair<String, String> entry) {
        String key = entry.getKey();
        String value = entry.getValue();
        booksHash.put(key, value);
    }

    public void addChapterHash(CustomPair<String, String> entry) {
        String key = entry.getKey();
        String value = entry.getValue();
        chaptersHash.put(key, value);
    }

    public void removeBookHash(String key) {
        booksHash.remove(key);
    }

    public void removeChapterHash(String key) {
        chaptersHash.remove(key);
    }

    public void updateBookHash(CustomPair<String, String> entry) {
        String key = entry.getKey();
        String value = entry.getValue();
        booksHash.replace(key, value);
    }

    public void updateChapterHash(CustomPair<String, String> entry) {
        String key = entry.getKey();
        String value = entry.getValue();
        chaptersHash.replace(key, value);
    }

    public String getBookNameByKey(String key) {
        return booksHash.get(key);
    }

    public String getChapterNameByKey(String key) {
        return chaptersHash.get(key);
    }

}
