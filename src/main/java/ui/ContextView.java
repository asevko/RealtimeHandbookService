package ui;

import model.Chapter;
import model.Callable;
import viewModel.BookVewModel;

import javax.swing.*;
import java.awt.*;

public class ContextView implements Callable{

    private JPanel panel;
    private BookVewModel bookVewModel;
    private JTextArea chapterDescription;
    private JTextArea chapterText;
    private JButton saveButton;
    private GridBagConstraints constraints = new GridBagConstraints(3,
                                                                2,
                                                                6,
                                                                1,
                                                                0.5,
                                                                0.01,
                                                                GridBagConstraints.PAGE_START,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets(5,5,5,5),
                                                                0,
                                                                50);
    private boolean changedMyself = false;

    ContextView(BookVewModel bookVewModel) {
        this.bookVewModel = bookVewModel;
        bookVewModel.registerChapterCallback(this);
        createPanel();
        addElementsToPanel();
    }

    private void createPanel() {
        this.panel = new JPanel();
        panel.setLayout(new GridBagLayout());
    }

    private void addElementsToPanel() {
        JLabel description = new JLabel("Description");
        panel.add(description, constraints);

        chapterDescription = new JTextArea();
        constraints.ipady = 0;
        constraints.gridy = 3;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 14;
        constraints.gridheight = 5;
        constraints.weighty = 0.27;
        constraints.weightx = 0.3;
        panel.add(new JScrollPane(chapterDescription), constraints);

        JLabel context = new JLabel("Context");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 11;
        constraints.weightx = 0.5;
        constraints.weighty = 0.01;
        constraints.gridheight = 1;
        constraints.gridwidth = 6;
        panel.add(context, constraints);

        chapterText = new JTextArea();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 12;
        constraints.weighty = 0.47;
        constraints.weightx = 0.3;
        constraints.gridwidth = 14;
        constraints.gridheight = 10;
        panel.add(new JScrollPane(chapterText), constraints);

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String descr = chapterDescription.getText();
            String txt = chapterText.getText();
            bookVewModel.updateChapter(descr, txt);
        });
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = 23;
        constraints.weighty = 0.1;
        constraints.weightx = 0.03;
        constraints.gridheight = 1;
        constraints.gridwidth = 6;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(saveButton, constraints);
    }

    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void completion(Object obj, String event) {
        Chapter chapter = (Chapter)obj;
        chapterDescription.setText(chapter.getDescription());
        chapterText.setText(chapter.getText());
    }

    @Override
    public void error(String error) {
        JOptionPane.showConfirmDialog(panel.getParent(), error, "Error",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void change(Object obj, String event) {
        String value = (String)obj;
        switch (event) {
            case "text":
                chapterText.setText(value);
                break;
            case "description":
                chapterDescription.setText(value);
                break;
        }
    }

    @Override
    public void remove(Object obj, String event) {
        switch (event) {
            case "text":
                chapterText.setText("");
                break;
            case "description":
                chapterDescription.setText("");
                break;
        }
    }
}
