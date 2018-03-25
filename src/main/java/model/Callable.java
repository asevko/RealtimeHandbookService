package model;

public interface Callable {

    void completion(Object obj, String event);
    void error(String error);
    void change(Object obj,  String event);
    void remove(Object obj, String event);

}
