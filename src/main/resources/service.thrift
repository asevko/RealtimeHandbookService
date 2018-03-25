namespace java thrift

struct Handbook {
    1: string name;
    2: list<Chapter> chapters;
}

struct Chapter {
    1: string name;
    2: string description
    3: string text;
}

struct CustomPair {
    1: string key;
    2: string value;
}

service Callable {
    void completion(1: binary obj, 2: string event);
    void error(1: string error);
    void change(1: binary obj, 2: string event);
    void remove(1: binary obj, 2: string event);
}

service Storage {

    Storage shared();
    void getBookList(1: Callable callback);
    void getBookChapters(1: string bookUid, 2: Callable callback);
    void renameBook(1: CustomPair newValue);
    void renameBookChapter(1: string bookUid, 2: CustomPair newValue);
    void getChapter(1: string bookUid, 2: string chapterUid, 3: Callable callback);
    void updateChapter(1: string bookUid, 2: string chapterUid, 3: Chapter chapter);
    void removeBook(1: string bookUid);
    void removeChapter(1: string bookUid, 2: string chapterUid);
    void addBook(1: string bookName);
    void addChapter(1: string bookUid, 2: string chapterName)

}





