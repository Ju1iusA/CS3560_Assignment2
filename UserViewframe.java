package MiniTwitter;

import javax.swing.*;
import java.awt.*;

public class UserViewframe extends JFrame {
    private final User currentUser;
    private final DefaultListModel<String> followingModel = new DefaultListModel<>();
    private final DefaultListModel<String> newsFeedModel = new DefaultListModel<>();
    
    private final JLabel lblTimestamps = new JLabel();

    public UserViewframe(User user) {
        currentUser = user;
        
        //User View Window
        setTitle("User View - " + currentUser.getId());
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        //Time Display Section
        lblTimestamps.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTimestamps.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(lblTimestamps);

        //Follow User Section
        JTextField txtFollow = new JTextField();
        JButton btnFollow = new JButton("Follow User");
        btnFollow.addActionListener(e -> handleFollow(txtFollow));
        add(createRowPanel(txtFollow, btnFollow));

        //Follower list
        add(createScrollList(followingModel, "Current Following"));

        //Tweet Section
        JTextField txtTweet = new JTextField();
        JButton btnPost = new JButton("Post Tweet");
        btnPost.addActionListener(e -> { 
            if (!txtTweet.getText().trim().isEmpty()) currentUser.postTweet(txtTweet.getText().trim()); 
            txtTweet.setText(""); 
        });
        add(createRowPanel(txtTweet, btnPost));

        //News Feed list
        add(createScrollList(newsFeedModel, "News Feed"));

        //Updates data to UI
        currentUser.addUiListener(() -> refreshUiLists());
        refreshUiLists();
    }

    //Sets up buttons and text fields in horizontal rows
    private JPanel createRowPanel(JTextField field, JButton button) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(field, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return panel;
    }

    //Creates scrollable box for a list
    private JScrollPane createScrollList(DefaultListModel<String> model, String title) {
        JScrollPane scroll = new JScrollPane(new JList<>(model));
        scroll.setBorder(BorderFactory.createTitledBorder("List View (" + title + ")"));
        return scroll;
    }

    //When a user is followed
    private void handleFollow(JTextField input) {
        String targetId = input.getText().trim();
        if (targetId.isEmpty() || targetId.equals(currentUser.getId())) {
            JOptionPane.showMessageDialog(null, "Invalid User ID.");
            return;
        }

        if (AdminControlPanel.getInstance().findComponentById(targetId) instanceof User targetUser) {
            currentUser.follow(targetUser);
            input.setText("");
            refreshUiLists();
        } else {
            JOptionPane.showMessageDialog(null, "User ID '" + targetId + "' not found.");
        }
    }

    //Refreshes text in both UI lists
    private void refreshUiLists() {
        SwingUtilities.invokeLater(() -> {
            followingModel.clear();
            currentUser.getFollowingsIds().forEach(id -> followingModel.addElement("- " + id));
            newsFeedModel.clear();
            currentUser.getNewsFeed().forEach(tweet -> newsFeedModel.addElement("- " + tweet));
            
            lblTimestamps.setText("Created: " + currentUser.getCreationTime() + " | Updated: " + currentUser.getLastUpdateTime());
        });
    }
}
