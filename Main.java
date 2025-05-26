import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:scooply_db.db");
            String sqlQuery = "SELECT * FROM transaction_items_tbl";

            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(sqlQuery);


            while (res.next()) {
                System.out.println(res.getInt(1) + " | " + res.getString(2) + " | " 
                + res.getString(3) + " | " + res.getString(4) + " | " + res.getString(5));

                System.out.println("_-_-_");

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}