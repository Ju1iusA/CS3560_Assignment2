package MiniTwitter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AdminControlPanel extends JFrame {
	
    //Singleton instance
    private static AdminControlPanel instance; 

    //Tracks user hierarchy
    private UserGroup rootGroup;
    private Map<String, UserComponent> allEntries; 

    private JTree userTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    //Private constructor for Singleton pattern
    private AdminControlPanel() {
    	//Window setup
        setTitle("Mini Twitter - Admin Control Panel");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); 

        //Initializes user database and root system
        allEntries = new HashMap<>();
        rootGroup = new UserGroup("Root");
        allEntries.put("Root", rootGroup);

        //Builds folder tree UI element
        rootNode = new DefaultMutableTreeNode(rootGroup);
        treeModel = new DefaultTreeModel(rootNode);
        userTree = new JTree(treeModel);
        JScrollPane treeScrollPane = new JScrollPane(userTree);

        JPanel rightControls = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        //Row 1: Add user section
        JTextField txtUserId = new JTextField(15);
        JButton btnAddUser = new JButton("Add User");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.1;
        rightControls.add(txtUserId, gbc);
        gbc.gridx = 1;
        rightControls.add(btnAddUser, gbc);

        //Row 2: Add group section
        JTextField txtGroupId = new JTextField(15);
        JButton btnAddGroup = new JButton("Add Group");
        gbc.gridx = 0; gbc.gridy = 1;
        rightControls.add(txtGroupId, gbc);
        gbc.gridx = 1;
        rightControls.add(btnAddGroup, gbc);

        //Row 3: Open user view section
        JButton btnOpenUserView = new JButton("Open User View");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weighty = 0.2;
        rightControls.add(btnOpenUserView, gbc);

        //New buttons
        JPanel analyticsGrid = new JPanel(new GridLayout(3, 2, 10, 10));
        JButton btnUserTotal = new JButton("Show User Total");
        JButton btnGroupTotal = new JButton("Show Group Total");
        JButton btnMsgTotal = new JButton("Show Messages Total");
        JButton btnPositivePerc = new JButton("Show Positive Percentage");
        
        JButton btnValidateIds = new JButton("Validate IDs");
        JButton btnLastUpdatedUser = new JButton("Find Last Updated User");
        
        analyticsGrid.add(btnUserTotal);
        analyticsGrid.add(btnGroupTotal);
        analyticsGrid.add(btnMsgTotal);
        analyticsGrid.add(btnPositivePerc);
        
        analyticsGrid.add(btnValidateIds);
        analyticsGrid.add(btnLastUpdatedUser);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weighty = 0.6;
        rightControls.add(analyticsGrid, gbc);

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.insets = new Insets(10, 10, 10, 10);
        mainGbc.weighty = 1.0;

        mainGbc.gridx = 0; mainGbc.gridy = 0; mainGbc.weightx = 0.4;
        add(treeScrollPane, mainGbc);

        mainGbc.gridx = 1; mainGbc.weightx = 0.6;
        add(rightControls, mainGbc);

        btnAddUser.addActionListener(e -> {
            String id = txtUserId.getText().trim();
            if (validateNewId(id)) {
                User newUser = new User(id);
                insertComponent(newUser);
                txtUserId.setText("");
            }
        });

        btnAddGroup.addActionListener(e -> {
            String id = txtGroupId.getText().trim();
            if (validateNewId(id)) {
                UserGroup newGroup = new UserGroup(id);
                insertComponent(newGroup);
                txtGroupId.setText("");
            }
        });

        btnOpenUserView.addActionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) userTree.getLastSelectedPathComponent();
            
            if (selectedNode == null) {
                JOptionPane.showMessageDialog(null, "Please select an element from the tree first.");
                return;
            }
            
            Object userObject = selectedNode.getUserObject();
            
            if (userObject instanceof User) {
                User selectedUser = (User) userObject;
                
                UserViewframe userWindow = new UserViewframe(selectedUser);
                
                userWindow.setVisible(true);
                
            } else {
                JOptionPane.showMessageDialog(null, "Cannot open User View for a Group. Please select a User.");
            }
        });

        btnUserTotal.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Total Users: " + runAnalytics().getUserCount());
        });

        btnGroupTotal.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Total Groups: " + runAnalytics().getGroupCount());
        });

        btnMsgTotal.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Total Messages: " + runAnalytics().getMessageCount());
        });

        btnPositivePerc.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, String.format("Positive Tweets: %.2f%%", runAnalytics().getPositivePercentage()));
        });
        
        //ID validation system
        btnValidateIds.addActionListener(e -> {
            boolean valid = runAnalytics().areAllIdsValid();
            if (valid) {
                JOptionPane.showMessageDialog(null, "All IDs are valid.");
            } else {
                JOptionPane.showMessageDialog(null, "Validation failed. Spaces or Duplicates found.");
            }
        });

        //Pulls the ID of the user who made the most recent timestamp update
        btnLastUpdatedUser.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Last Updated User ID: " + runAnalytics().getLastUpdatedUser());
        });

    }

    //Entry method for Singleton
    public static AdminControlPanel getInstance() {
        if (instance == null) {
            instance = new AdminControlPanel();
        }
        return instance;
    }

    //Global hashmap database
    public UserComponent findComponentById(String id) {
        return allEntries.get(id);
    }

    //Checks string input
    private boolean validateNewId(String id) {
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID cannot be empty.");
            return false;
        }
        return true;
    }

    //Inserts node into selected folder
    private void insertComponent(UserComponent newComponent) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) userTree.getLastSelectedPathComponent();
        if (selectedNode == null) {
            selectedNode = rootNode;
        }
        if (selectedNode.getUserObject() instanceof User) {
            selectedNode = (DefaultMutableTreeNode) selectedNode.getParent();
        }

        UserGroup targetGroup = (UserGroup) selectedNode.getUserObject();
        targetGroup.add(newComponent);
        allEntries.put(newComponent.getId(), newComponent);

        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newComponent);
        treeModel.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
        userTree.scrollPathToVisible(new TreePath(newNode.getPath()));
    }

    //Runs data analytics
    private AnalysisVisitor runAnalytics() {
        AnalysisVisitor visitor = new AnalysisVisitor();
        calculateTotals(rootGroup, visitor);
        return visitor;
    }

    //Helper for tree traversal
    private void calculateTotals(UserComponent component, AnalysisVisitor visitor) {
        if (component instanceof User user) {
            visitor.visitUser(user);
        } else if (component instanceof UserGroup group) {
            visitor.visitUserGroup(group);
            group.getChildren().forEach(child -> calculateTotals(child, visitor));
        }
    }

}
