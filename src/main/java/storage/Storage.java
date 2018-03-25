package storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.firebase.database.*;
import model.*;
import org.apache.log4j.Logger;


public class Storage {

    private static Storage instance;
    private static final Object lock = new Object();

    public static Storage shared() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new Storage();
                }
            }
        }
        return instance;
    }

    private final static Logger logger = Logger.getLogger(Storage.class);

    private DatabaseReference ref;

    private UpdateChapterListListener updateChapterListListener;
    private UpdateChapterListener updateChapterListener;

    private Storage() {
        try {
            initializeApp();
            ref = FirebaseDatabase.getInstance()
                    .getReference().child("books");
            createEventListeners();
        } catch (IOException e) {
            logger.error("Error in app initialization: " + e.getLocalizedMessage());
        }
    }

    private void initializeApp() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/realtimehandbookservce-firebase-adminsdk-ty3to-24c90c1cac.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://realtimehandbookservce.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);

        logger.info("Application initialized successfully");
    }

    private void createEventListeners() {
        updateChapterListListener = new UpdateChapterListListener(null);
        updateChapterListener = new UpdateChapterListener(null);
    }

    public void getBookList(Callable callback) {
        logger.info("Called getBookList(" + callback.toString() + ")");
        ref.addChildEventListener(new UpdateBookListListener(callback));
    }

    public void getBookChapters(String bookUid, Callable callback) {
        logger.info("Called getBookChapters(" + bookUid + ", " + callback.toString() + ")");
        DatabaseReference currentBookChaptersRef = ref.child(bookUid).child("chapters");
        currentBookChaptersRef.removeEventListener(updateChapterListListener);
        updateChapterListListener = new UpdateChapterListListener(callback);
        currentBookChaptersRef.addChildEventListener(updateChapterListListener);
    }

    public void renameBook(CustomPair<String, String> newValue) {
        String key = newValue.getKey();
        String newName = newValue.getValue();
        ref.child(key)
                .child("name")
                .setValueAsync(newName);
    }

    public void renameBookChapter(String bookUid, CustomPair<String, String> newValue) {
        String key = newValue.getKey();
        String newName = newValue.getValue();
        ref.child(bookUid)
                .child("chapters")
                .child(key)
                .child("name")
                .setValueAsync(newName);
    }

    public void getChapter(String bookUid, String chapterUid, Callable callback) {
        DatabaseReference chaptersRef =  ref.child(bookUid)
                .child("chapters")
                .child(chapterUid);
        chaptersRef.removeEventListener(updateChapterListener);
        updateChapterListener = new UpdateChapterListener(callback);
        chaptersRef.addChildEventListener(updateChapterListener);
    }

    public void updateChapter(String bookUid, String chapterUid, Chapter chapter) {
        ref.child(bookUid)
                .child("chapters")
                .child(chapterUid)
                .setValueAsync(chapter);
    }

    public void removeBook(String bookUid) {
        ref.child(bookUid)
                .removeValueAsync();
    }


    public void removeChapter(String bookUid, String chapterUid) {
        ref.child(bookUid)
                .child("chapters")
                .child(chapterUid)
                .removeValueAsync();
    }

    private class UpdateBookListListener implements ChildEventListener {

        private final Callable callback;

        UpdateBookListListener(Callable callback) {
            this.callback  = callback;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String name = (String) dataSnapshot.child("name").getValue();
            String key = dataSnapshot.getKey();
            CustomPair<String, String> entry = new CustomPair<>(key, name);
            logger.info("Added book: " + name);
            callback.completion(entry, "books");
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String name = (String) dataSnapshot.child("name").getValue();
            String key = dataSnapshot.getKey();
            logger.info("Changed book name: " + name + ", key: " + key);
            CustomPair<String, String> entry = new CustomPair<>(key, name);
            callback.change(entry, "books");
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String name = (String) dataSnapshot.child("name").getValue();
            String key = dataSnapshot.getKey();
            logger.info("Removed book name: " + name + ", key: " + key);
            CustomPair<String, String> entry = new CustomPair<>(key, name);
            callback.remove(entry, "books");
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.error(databaseError.getMessage());
        }
    }

    private class UpdateChapterListListener implements ChildEventListener {

        private final Callable callback;

        UpdateChapterListListener(Callable callback) {
            this.callback  = callback;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Chapter chapter = dataSnapshot.getValue(Chapter.class);
            String key = dataSnapshot.getKey();
            String name = chapter.getName();
            logger.info("Added chapter: " + name);
            CustomPair<String, String> entry = new CustomPair<>(key, name);
            callback.completion(entry, "chapters");
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String name = (String) dataSnapshot.child("name").getValue();
            String key = dataSnapshot.getKey();
            logger.info("Changed chapter name: " + name + ", key: " + key);
            CustomPair<String, String> entry = new CustomPair<>(key, name);
            callback.change(entry, "chapters");
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String name = (String) dataSnapshot.child("name").getValue();
            String key = dataSnapshot.getKey();
            logger.info("Removed chapter name: " + name + ", key: " + key);
            CustomPair<String, String> entry = new CustomPair<>(key, name);
            callback.remove(entry, "chapters");
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.error(databaseError.getMessage());
        }
    }

    private class UpdateChapterListener implements ChildEventListener {

        private final Callable callback;
        private String description;
        private String name;
        private String text;

        UpdateChapterListener(Callable callback) {
            this.callback  = callback;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            switch (dataSnapshot.getKey()) {
                case "description":
                    description = (String) dataSnapshot.getValue();
                    break;
                case "text":
                    text = (String) dataSnapshot.getValue();
                    break;
                case "name":
                    name = (String) dataSnapshot.getValue();
                    break;
            }
            boolean canSend = name != null &&
                    description != null &&
                    text != null;
            if (canSend) {
                callback.completion(new Chapter(name, description, text), null);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String value = (String) dataSnapshot.getValue();
            callback.change(value, dataSnapshot.getKey());
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String value = (String) dataSnapshot.getValue();
            callback.remove(value, dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.error(databaseError.getMessage());
        }

    }

}
