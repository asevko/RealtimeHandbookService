package viewModel;

import model.CustomPair;
import model.Chapter;
import model.Callable;
import storage.Storage;

import java.util.HashMap;

public class BookVewModel {

    private Storage storage;
    private HashMap<String, String> booksHash;
    private HashMap<String, String> chaptersHash;
    private String activeBookUid;
    private String activeChapterUid;
    private Callable chapterCallback;

    public BookVewModel() {
        storage = Storage.shared();
        booksHash = new HashMap<>();
        chaptersHash = new HashMap<>();
    }

    public void renameBook(CustomPair<String, String> newValue) {
        storage.renameBook(newValue);
    }

    public void renameBookChapter(CustomPair<String, String> newValue) {
        storage.renameBookChapter(activeBookUid, newValue);
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

    public void requestChapter(String chapterUid) {
        storage.getChapter(activeBookUid, chapterUid, chapterCallback);
    }

    public void registerChapterCallback(Callable chapterCallback) {
        this.chapterCallback = chapterCallback;
    }

    public void updateChapter(String description, String text) {
        String chapterName = chaptersHash.get(activeChapterUid);
        storage.updateChapter(activeBookUid, activeChapterUid, new Chapter(chapterName, description, text));
    }

    public void removeChapter(String chapterUid) {
        chaptersHash.remove(chapterUid);
        storage.removeChapter(activeBookUid, chapterUid);
    }

    public void removeBook(String bookUid) {
        booksHash.remove(bookUid);
        storage.removeBook(bookUid);
    }

    public void addBook(String bookName) {
        storage.addBook(bookName);
    }

    public void addChapter(String chapterName, Callable callback) {
        if (activeBookUid != null) {
            storage.addChapter(activeBookUid, chapterName);
        } else  {
            callback.error("Please, choose for which book you are adding a chapter and try again");
        }
    }

}
