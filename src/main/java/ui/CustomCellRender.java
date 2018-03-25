package ui;


import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

public class CustomCellRender extends DefaultTreeCellRenderer{

    private String iconURL;
    private ImageIcon icon;

    CustomCellRender(String iconURL){
        this.iconURL = iconURL;
        createImageIcon();
    }

    private void createImageIcon() {
        try {
            URL url = new URL(iconURL);
            this.icon = new ImageIcon(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        setIcon(icon);
        return this;
    }

}
