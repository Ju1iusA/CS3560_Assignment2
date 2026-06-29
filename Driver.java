package MiniTwitter;

import javax.swing.SwingUtilities;

//Entry point that launches main admin interface
public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminControlPanel admin = AdminControlPanel.getInstance();
            admin.setVisible(true);
        });
    }
}
