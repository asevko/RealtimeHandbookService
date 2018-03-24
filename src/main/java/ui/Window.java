package ui;

import viewModel.BookVewModel;

import javax.swing.*;
import java.awt.*;

public class Window {
    private static SomeArea area;
    private static InformationView bookList;

    private Window() {
        JFrame frame = new JFrame("Async");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setSize(1000, 800);

        area  = new SomeArea();
        frame.add(area, BorderLayout.CENTER);

        BookVewModel bookVewModel = new BookVewModel();
        bookList = new InformationView(bookVewModel);
        frame.add(bookList.getPanel(), BorderLayout.WEST);

        frame.setFocusable(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Window::new);
        try {
            Thread.sleep(5000);
            area.subscribe();
            bookList.requestBookList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
