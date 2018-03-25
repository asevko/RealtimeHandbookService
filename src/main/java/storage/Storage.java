package storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.google.firebase.database.*;
import model.CustomPair;
import model.Author;
import model.Chapter;
import model.Handbook;
import org.apache.log4j.Logger;


public class Storage {


    private enum Section {
        authors("authors"), chapters("chapters"), name("name");

        String str;

        Section(String str) {
            this.str = str;
        }
    }

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
    private FileInputStream serviceAccount;
    private FirebaseOptions options;

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
        serviceAccount = new FileInputStream("src/main/resources/realtimehandbookservce-firebase-adminsdk-ty3to-24c90c1cac.json");

        options = new FirebaseOptions.Builder()
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

    private ArrayList<Author> getAuthorsFrom(DataSnapshot dataSnapshot) {
        ArrayList<Author> authors = new ArrayList<>();
        dataSnapshot.getChildren()
                .forEach(snap -> {
                    Author author = snap.getValue(Author.class);
                    author.setUid(snap.getKey());
                    authors.add(author);
                });
        return authors;
    }

    private ArrayList<Chapter> getChaptersFrom(DataSnapshot dataSnapshot) {
        ArrayList<Chapter> chapters = new ArrayList<>();
        dataSnapshot.getChildren()
                .forEach(snap -> {
                    Chapter chapter = snap.getValue(Chapter.class);
                    chapter.setUid(snap.getKey());
                    chapters.add(chapter);
                });
        return  chapters;
    }

    public void handleBooks(Callable callable) {
//        DatabaseReference reference= ref.push();
//        reference.child("name").setValueAsync("C++Handbook");
//        DatabaseReference authRef = reference.child("authors");
//        authRef.push().setValueAsync(new Author("Alexei", "Siauko"));
//        authRef.push().setValueAsync(new Author("Ivan", "Ivanov"));
//        DatabaseReference chpRef = reference.child("chapters");
//        chpRef.push().setValueAsync(new Chapter("Chapter 1", "Just intro", "Welcome to our world"));\

        ref.child("-L8ENetYYOSk5YKOzfd8")
                .child("chapters")
                .child("-L8ENeteZdWM8I47Tkh5")
                .setValueAsync(new Chapter("Chapter 1", "Just intro", "Welcome to our world"));

        ArrayList<Author> authors = new ArrayList<>();
        authors.add(new Author("Check", "Iren"));
        authors.add(new Author("Alexei", "Siauko"));

        ArrayList<Chapter> chapters = new ArrayList<>();
        chapters.add(new Chapter("Chapter 1", "Just intro", "Welcome to our world"));

        Handbook handbook = new Handbook("JavaHandbook", authors, chapters);

        saveBook(handbook);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                dataSnapshot.getChildren()
                        .forEach(snap -> {
                            if (snap.getKey().equals("name")) {
                                    callable.completion(snap.getValue(), null);
                            }
                        });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveBook(Handbook book) {
        DatabaseReference reference= ref.push();
        book.setUid(ref.getKey());
        reference.child("name").setValueAsync(book.getName());
        DatabaseReference authorsRef = reference.child("authors");
        book.getAuthors()
                .forEach(author -> {
                    DatabaseReference currentAuthorRef =  authorsRef.push();
                    author.setUid(currentAuthorRef.getKey());
                    currentAuthorRef.setValueAsync(author);
                });
        DatabaseReference chaptersRef = reference.child("chapters");
        book.getChapters()
                .forEach(chapter -> {
                    DatabaseReference currentChapterRef = chaptersRef.push();
                    chapter.setUid(currentChapterRef.getKey());
                    currentChapterRef.push().setValueAsync(chapter);
                });
    }


    public void getBookByUid(String uid, Callable callback) {
        Handbook handbook = new Handbook();
        handbook.setUid(uid);

        DatabaseReference bookRef = ref.child(uid);
        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren()
                        .forEach(snap -> {
                            switch (snap.getKey()) {
                                case "authors":
                                    ArrayList<Author> authors = getAuthorsFrom(snap);
                                    handbook.setAuthors(authors);
                                    break;
                                case "chapters":
                                    ArrayList<Chapter> chapters = getChaptersFrom(snap);
                                    handbook.setChapters(chapters);
                                    break;
                                case "name":
                                    String name = (String) snap.getValue();
                                    handbook.setName(name);
                                    break;
                            }
                        });
                callback.completion(handbook, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.error(databaseError.getMessage());
            }
        });
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

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    }

}
