import javax.swing.SwingUtilities;
import javax.swing.UIManager;

class Main {
    public static void main(String[] args) {

        //This block ensures the MainMenu looks clean and runs safely, by using the Nimbus Look and Feel (if it exists) and launching the GUI on the correct thread.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            //Default L&F if Nimbus is not available
            e.printStackTrace();
        }
        //Run the MainMenu GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(MainMenu::new);
    }
}