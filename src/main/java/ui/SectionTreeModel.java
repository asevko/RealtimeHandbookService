package ui;



import model.CustomPair;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class SectionTreeModel implements TreeModel {

    private  DefaultTreeModel treeModel;

    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

    SectionTreeModel() {
        treeModel = new DefaultTreeModel(root);
    }

    public void addSection(CustomPair<String, String> entry){
        int index = root.getChildCount();
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(entry);
        treeModel.insertNodeInto(newNode, root, index);
        treeModel.reload();
    }

    public void replaceSection(CustomPair<String, String> oldValue, CustomPair<String, String> newValue){
        int index = 0;
        int childCount = root.getChildCount();
        while (index < childCount) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeModel.getChild(root, index);
            CustomPair<String, String> currentValue = (CustomPair<String, String>)node.getUserObject();
            if (currentValue.equals(oldValue)) {
                DefaultMutableTreeNode updatedNode = new DefaultMutableTreeNode(newValue);
                treeModel.insertNodeInto(updatedNode, root, index);
                root.remove(index + 1);
                treeModel.reload(root);
                return;
            }
            index++;
        }
    }

    public void removeSection(CustomPair<String, String> value) {
        int index = 0;
        int childCount = root.getChildCount();
        while (index < childCount) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeModel.getChild(root, index);
            CustomPair<String, String> currentValue = (CustomPair<String, String>)node.getUserObject();
            if (currentValue.equals(value)) {
                root.remove(index);
                treeModel.reload(root);
                return;
            }
            index++;
        }
    }

    public void removeAllChild() {
        int index = 0;
        int childCount = root.getChildCount();
        while (index < childCount) {
            root.remove(0);
            index++;
        }
        treeModel.reload(root);
    }

    @Override
    public Object getRoot() {
        return treeModel.getRoot();
    }

    @Override
    public Object getChild(Object parent, int index) {
        return treeModel.getChild(parent, index);
    }

    @Override
    public int getChildCount(Object parent) {
        return treeModel.getChildCount(parent);
    }

    @Override
    public boolean isLeaf(Object node) {
        return treeModel.isLeaf(node);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        treeModel.valueForPathChanged(path, newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return treeModel.getIndexOfChild(parent, child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModel.addTreeModelListener(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModel.removeTreeModelListener(l);
    }
}
