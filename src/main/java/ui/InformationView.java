package ui;

import model.CustomPair;
import org.apache.log4j.Logger;
import storage.Callable;
import viewModel.BookVewModel;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.MouseEvent;

public class InformationView implements Callable {

    private static String bookImageURL = "https://sun1-2.userapi.com/c824203/v824203830/f69da/7LVCg1aOTAs.jpg";
    private static String chapterImageURL = "https://pp.userapi.com/c846019/v846019830/9c12/5B4VL4ozUJc.jpg";

    private final static Logger logger = Logger.getLogger(InformationView.class);

    private static int width = 150;

    private JPanel panel;
    private SectionTreeModel bookTreeModel;
    private SectionTreeModel chapterTreeModel;
    private BookVewModel bookVewModel;
    private GridBagConstraints constraints = new GridBagConstraints(0, 0,
                                                            1, 1,
                                                            0, 0.03,
                                                            GridBagConstraints.PAGE_START,
                                                            GridBagConstraints.BOTH,
                                                            new Insets(5, 5, 5, 5),
                                                            0, 0);


    InformationView(BookVewModel bookVewModel) {
        this.bookVewModel = bookVewModel;
        createPanel();
        addLabelsToPanel();
        createTreeView();
    }

    private void createPanel() {
        this.panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(width * 2, 600));
    }

    private void addLabelsToPanel() {
        JLabel books = new JLabel("-- Available books --", SwingConstants.CENTER);
        books.setPreferredSize(new Dimension(width,50));
        panel.add(books, constraints);

        JLabel chapters = new JLabel("----- Chapters -----", SwingConstants.CENTER);
        chapters.setPreferredSize(new Dimension(width,50));
        constraints.gridx = 1;
        panel.add(chapters, constraints);
    }

    private void createTreeView() {
        bookTreeModel = new SectionTreeModel();
        constraints.weighty= 0.97;

        final JTree bookList = new JTree(bookTreeModel);
        bookList.setOpaque(true);
        bookList.setCellRenderer(new CustomCellRender(bookImageURL));
        bookList.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    super.mouseClicked(e);
                    DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) bookList.getLastSelectedPathComponent();
                    CustomPair<String, String> entry = (CustomPair<String, String>) clickedNode.getUserObject();
                    String bookUid = entry.getKey();
                    chapterTreeModel.removeAllChild();
                    requestBookChapters(bookUid);
                    bookVewModel.setActiveBookUid(bookUid);
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = bookList.getClosestRowForLocation(e.getX(), e.getY());
                    bookList.setSelectionRow(row);
                    DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) bookList.getLastSelectedPathComponent();
                    CustomPair<String, String> entry = (CustomPair<String, String>) clickedNode.getUserObject();
                    String key = entry.getKey();
                    String oldName = entry.getValue();
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem rename = new JMenuItem("Rename");
                    rename.addActionListener(event -> {
                        String newName = JOptionPane.showInputDialog(panel.getParent(),
                                "Enter new book name", oldName);
                        if (!newName.equals("")) {
                            bookVewModel.renameBook(new CustomPair<>(key, newName));
                        }
                    });
                    popupMenu.add(rename);
                    popupMenu.add(new JPopupMenu.Separator());
                    JMenuItem delete = new JMenuItem("Delete");
                    //add deleting book
                    popupMenu.add(delete);
                    bookList.setComponentPopupMenu(popupMenu);
                }
            }
        });
        bookList.setRootVisible(false);
        bookList.setShowsRootHandles(true);
        constraints.gridx = 0;
        constraints.gridy = 1;
        JScrollPane scrollingBookTreeView = new JScrollPane(bookList);
        scrollingBookTreeView.setPreferredSize(new Dimension(width,550));
        panel.add(scrollingBookTreeView, constraints);

        chapterTreeModel = new SectionTreeModel();
        JTree chapterLists = new JTree(chapterTreeModel);
        chapterLists.setCellRenderer(new CustomCellRender(chapterImageURL));
        chapterLists.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    super.mouseClicked(e);
                    DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) chapterLists.getLastSelectedPathComponent();
                    CustomPair<String, String> entry = (CustomPair<String, String>) clickedNode.getUserObject();
                    String chapterUid = entry.getKey();
                    bookVewModel.requestChapter(chapterUid);
                    bookVewModel.setActiveChapterUid(chapterUid);
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = chapterLists.getClosestRowForLocation(e.getX(), e.getY());
                    chapterLists.setSelectionRow(row);
                    DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) chapterLists.getLastSelectedPathComponent();
                    CustomPair<String, String> entry = (CustomPair<String, String>) clickedNode.getUserObject();
                    String key = entry.getKey();
                    String oldName = entry.getValue();
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem rename = new JMenuItem("Rename");
                    rename.addActionListener(event -> {
                        String newName = JOptionPane.showInputDialog(panel.getParent(),
                                "Enter new chapter name", oldName);
                        if (!newName.equals("")) {
                            bookVewModel.renameBookChapter(new CustomPair<>(key, newName));
                        }
                    });
                    popupMenu.add(rename);
                    popupMenu.add(new JPopupMenu.Separator());
                    JMenuItem delete = new JMenuItem("Delete");
                    //add deleting book
                    popupMenu.add(delete);
                    chapterLists.setComponentPopupMenu(popupMenu);
                }
            }
        });
        chapterLists.setOpaque(true);
        chapterLists.setRootVisible(false);
        chapterLists.setShowsRootHandles(true);
        constraints.gridx = 1;
        JScrollPane scrollingChapterTreeView = new JScrollPane(chapterLists);
        scrollingChapterTreeView.setPreferredSize(new Dimension(width,550));
        panel.add(scrollingChapterTreeView, constraints);
    }


    private void requestBookChapters(String bookUid){
        bookVewModel.getBookChapters(bookUid, this);
    }

    public void requestBookList() {
        bookVewModel.requestBookList(this);
    }

    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void completion(Object obj, String event) {
        logger.info("InformationView completion(" + obj + ", " + event + ")");
        CustomPair<String, String> entry = (CustomPair<String, String>)obj;
        switch (event) {
            case "books":
                bookTreeModel.addSection(entry);
                bookVewModel.addBookHash(entry);
                break;
            case "chapters":
                chapterTreeModel.addSection(entry);
                bookVewModel.addChapterHash(entry);
                break;
        }

    }

    @Override
    public void error(String error) {
        JOptionPane.showConfirmDialog(panel.getParent(), error, "Error",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void change(Object obj,  String event) {
        logger.info("InformationView change(" + obj + ", " + event + ")");
        CustomPair<String, String> newEntry = (CustomPair<String, String>)obj;
        String key = newEntry.getKey();
        String oldName;
        CustomPair<String, String> oldEntry;
        switch (event) {
            case "books":
                oldName = bookVewModel.getBookNameByKey(key);
                bookVewModel.updateBookHash(newEntry);
                oldEntry = new CustomPair<>(key, oldName);
                bookTreeModel.replaceSection(oldEntry, newEntry);
                break;
            case "chapters":
                oldName = bookVewModel.getChapterNameByKey(key);
                bookVewModel.updateChapterHash(newEntry);
                oldEntry = new CustomPair<>(key, oldName);
                chapterTreeModel.replaceSection(oldEntry, newEntry);
                break;
        }
    }

    @Override
    public void remove(Object obj, String event) {

    }
}
