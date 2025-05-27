import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

class Earnings {

    // int variables to be reassigend later by SQL query results and added to JLabel earningsCardContentLabel as text
    int customersTotal = 0;
    int incomeToday = 0;
    int itemsSold = 0;
    int totalIncome = 0;

    final String JDBC_URL = "jdbc:sqlite:scooply_db.db"; // "File path" of SQLite3 database

    private JFrame frame;
    private JPanel menuPanel, centerContainerPanel; // Added centerContainerPanel as instance variable

    private final String LOGO_ICON_PATH = "images/logo/ice_cream_logo.png";

    Earnings() {
        /*------------------------ MAIN WINDOW --------------------------*/

        frame = new JFrame("Scooply POS");
        //Set the Frame Icon
        ImageIcon logoIcon = new ImageIcon(LOGO_ICON_PATH);
        frame.setIconImage(logoIcon.getImage());
        //frame.setSize(1920, 1080);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        ((JPanel) frame.getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // LEFT MENU PANEL
        menuPanel = new JPanel();
        menuPanel.setBackground(new Color(194, 177, 156));
        menuPanel.setPreferredSize(new Dimension(250, 0));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JPanel titleContainer = new JPanel();
        // SET LAYOUT TO NULL FOR MANUAL POSITIONING
        titleContainer.setLayout(null);
        titleContainer.setBackground(new Color(71, 60, 56)); // Dark brown background
        titleContainer.setOpaque(true);
        titleContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        int titleContainerHeight = 130; // Example height, adjust as needed
        titleContainer.setPreferredSize(new Dimension(250, titleContainerHeight)); // Width from menuPanel, manual height
        titleContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleContainerHeight));

        JLabel titleLabel = new JLabel("Scooply");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        Dimension titlePreferredSize = titleLabel.getPreferredSize();
        titleLabel.setBounds(20, 35, titlePreferredSize.width, titlePreferredSize.height);

        JLabel taglineLabel = new JLabel("\"Happiness in a Scoop\"");
        taglineLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        taglineLabel.setForeground(Color.WHITE);
        Dimension taglinePreferredSize = taglineLabel.getPreferredSize();
        // x=20 (align with title), y = titleLabel's y + titleLabel's height + some_spacing
        // Example: y = 10 (titleY) + titlePreferredSize.height + 5 (spacing)
        taglineLabel.setBounds(20, 35 + titlePreferredSize.height + 5, taglinePreferredSize.width, taglinePreferredSize.height);

        titleContainer.add(titleLabel);
        titleContainer.add(taglineLabel);
        menuPanel.add(titleContainer);
        //menuPanel.add(Box.createVerticalStrut(15));

        //This is the buttons
        String[] menuOptions = {"Main Menu", "Transact History", "Total Earnings"};
        for (String option : menuOptions) {
            JButton button = new JButton(option);
            button.setFont(new Font("Consolas", Font.PLAIN, 18));
            button.setMaximumSize(new Dimension(200, 40));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Add action listeners for these buttons (These is the logic of window transitions using button)
            button.addActionListener(_ -> {
                if (option.equals("Main Menu")) {
                    frame.dispose(); // Close the window
                    Main.main(new String[]{}); //run the code in main method of Main class from another class
                } else if (option.equals("Total Earnings")) {
                    frame.dispose();
                    SwingUtilities.invokeLater(Earnings::new);
                } else if (option.equals("Transact History")) {
                    frame.dispose();
                    SwingUtilities.invokeLater(Transactions::new);
                }
            });

            // Add action listeners for these buttons if functionality is implemented
            menuPanel.add(Box.createVerticalStrut(20));
            menuPanel.add(button);
        }
        frame.add(menuPanel, BorderLayout.WEST);

        // CENTER PANEL 
        centerContainerPanel = new JPanel();
        centerContainerPanel.setLayout(new BorderLayout());
        centerContainerPanel.setBackground(new Color(245, 241, 235));
        centerContainerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Total earnings Label
        JLabel transactTitleLabel = new JLabel("Total Earnings");
        transactTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        transactTitleLabel.setForeground(new Color(71, 60, 56));
        transactTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        transactTitleLabel.setBorder(new EmptyBorder(30, 0, 15, 0));
        centerContainerPanel.add(transactTitleLabel, BorderLayout.NORTH);
        frame.add(centerContainerPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        JPanel earningsPanel = new JPanel();
        earningsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 70));
        centerContainerPanel.add(earningsPanel, BorderLayout.CENTER);

        String[] earningsCardTitles = {"Number of Customers:", "Todays Income:", "Total Income:", "Number of Products Sold:"};
        String[] earningsCardImgURL = {"images/earnings_img/customers.png", "images/earnings_img/income_today.png", "images/earnings_img/total_income.png", "images/earnings_img/items_sold.png" };
        ArrayList<Integer> totalIncomeArrList = new ArrayList<>();

        /* try-catch block breakdown:
         * 1. Get connection to database, 
         * 2. First query ("SELECT * FROM transactions_tbl") gets the rows at column 4 ("order_total") as int and adds them to totalIncomeArrList (totalIncomeArrList.add(res.getInt(4));),
         * 3. Second query ("SELECT SUM(order_total) FROM transactions_tbl WHERE date = ?") returns 1 row and column: get every row from 
         *    "order_total" column if that rows value at "date" column is that same as todays date (dd-MM-yyyy). Set incomeToday = the returned row (res2.getInt())
         * 4. Third query ("SELECT SUM(item_quantity) FROM transaction_items_tbl;") returns 1 row and column: the sum of the 
         *     column "item_quantity". Set itemsSold = the returned row (res3.getInt())
         * 5. All text values earningsCardContentLabel are now reassigned, except totalIncome its reassigned at ln 170
         */
        try {
            Connection conn = DriverManager.getConnection(JDBC_URL);            

            Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM transactions_tbl");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String order_date = dateFormat.format(new Date());

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT SUM(order_total) FROM transactions_tbl WHERE date = ?");
            preparedStatement.setString(1, order_date);
            ResultSet res2 = preparedStatement.executeQuery(); 

            Statement statement2 = conn.createStatement();
            ResultSet res3 = statement2.executeQuery("SELECT SUM(item_quantity) FROM transaction_items_tbl;");  

            while (res.next()) {
                totalIncomeArrList.add(res.getInt(4)); 
            }

            incomeToday = res2.getInt(1);

            itemsSold = res3.getInt(1);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        customersTotal = totalIncomeArrList.size(); // Number of customers is the same as totalIncomeArrList.size() (the arraylist contains the paymentns of each transaction) 
        for (int num : totalIncomeArrList) totalIncome+=num; // Get the sum of all the int elements
        
        // Summary of "for (int i = 0; i < earningsCardTitles.length; i++) {...}":
        // Creates 4 (earningsCardTitles.length) "horizontal cards" with a JLabel as a "title" of the card whose text will be set from earningsCardTitles
        // along with an ImageIcon for styling, and finally a JLabel for the text beside the ImageIcon (the text is dependent on the context of the current index of earningsCardTitles)

        for (int i = 0; i < earningsCardTitles.length; i++) {
            // 1. Creates a JPanel as the "horizontal card" to place the ImageIcon and 2 JLabels
            JPanel earningsCardPanel = new JPanel();
            earningsCardPanel.setLayout(new BorderLayout());
            earningsCardPanel.setPreferredSize(new Dimension(450, 200));
            earningsCardPanel.setBackground(new Color(194, 177, 156));
            earningsPanel.add(earningsCardPanel);

            // 2. Creates a JLabel for the title of the "horizontal card", add to earningsCardPanel
            JLabel earningCardTitleLabel = new JLabel(earningsCardTitles[i], JLabel.CENTER);
            earningCardTitleLabel.setFont(new Font("Consolas", Font.PLAIN, 30));
            earningCardTitleLabel.setBorder(new EmptyBorder(30, 0, 0, 0));
            earningsCardPanel.add(earningCardTitleLabel, BorderLayout.NORTH);
        
            // 3. Creates ImageIcon img and set its FileName in constructor, create another ImageIcon and scale it up in the constructor
            ImageIcon img = new ImageIcon(earningsCardImgURL[i]);
            ImageIcon imgScaled = new ImageIcon(img.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH));
        
            // 4. Creates the final JLabel which is the text besides the ImageIcon (this uses the scaled one)
            JLabel earningsCardContentLabel = new JLabel("000000000", JLabel.CENTER); // Placeholder text for now
            earningsCardContentLabel.setIcon(imgScaled);
            earningsCardContentLabel.setIconTextGap(30);
            earningsCardContentLabel.setFont(new Font("Consolas", Font.BOLD, 35));
            earningsCardPanel.add(earningsCardContentLabel, BorderLayout.CENTER);
        
            // 5. Creates a switch for the setting of earningsCardContentLabel text, 
            switch (i) { 
                case 0: // 0 would be Number of Customers, 1 would be Todays Income, and so on (refer to String[] earningsCardTitles)
                    earningsCardContentLabel.setText(String.valueOf(customersTotal));
                    break;
                case 1: // Todays Income
                    earningsCardContentLabel.setText("₱" + incomeToday);
                    break;
                case 2: // Total Income
                    earningsCardContentLabel.setText("₱" + totalIncome);
                    break;
                case 3: // Number of Products Sold
                    earningsCardContentLabel.setText(String.valueOf(itemsSold));
                    break;
                default:
                    break;
            }

        } // END of for (int i = 0; i < earningsCardTitles.length; i++)
        
        // Adding a button directly at BorderLayout.SOUTH of centerContainerPanel makes it take up all the space and prevents size modification
        JPanel buttonsPanel = new JPanel();
        centerContainerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        JButton clearDatabaseBtn = new JButton("Clear Database");
        clearDatabaseBtn.setBackground(Color.RED);
        clearDatabaseBtn.setForeground(Color.WHITE);
        clearDatabaseBtn.setPreferredSize(new Dimension(200, 50));
        clearDatabaseBtn.setFont(new Font("Consolas", Font.BOLD, 20));
        buttonsPanel.add(clearDatabaseBtn);

        // Breakdown of clearDatabaseBtn.addActionListener()
        // 1. Show JOptionPane.inputDialog, user must type in "CONFIRM" to proceed with the database clearing
        // 2. If user types "CONFIRM" clear totalIncomeArrList and set variables at ln 19-22 to 0
        // 3. Get connection to database, execute querires: DELETE FROM transactions_tbl and DELETE FROM transaction_items_tbl
        // 4. Refresh window by disposing it and invoking it again
        // 5. If user clicks cancel or types something else on the JOptionPane.inputDialog, show a JOptionPane.messageDialog w message "Operation Canceled" 
        clearDatabaseBtn.addActionListener(_ -> {
            String response = JOptionPane.showInputDialog("This action cannot be undone. Type \"CONFIRM\".");
            if (response.equals("CONFIRM")) {
                totalIncomeArrList.clear();
                customersTotal = 0;
                itemsSold = 0;
                incomeToday = 0; 
                totalIncome = 0;
                try {
                    Connection conn = DriverManager.getConnection(JDBC_URL);
                    Statement statement = conn.createStatement();
                    
                    statement.executeUpdate("DELETE FROM transactions_tbl;");
                    statement.executeUpdate("DELETE FROM transaction_items_tbl;");
    
                    statement.close();
                    conn.close();

                    JOptionPane.showMessageDialog(null, "Operation Succesful", "Clear Database", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    SwingUtilities.invokeLater(Earnings::new);
    
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null,  "Operation Canceled", "Clear Database", JOptionPane.CANCEL_OPTION);
            }
        }); // END OF clearDatabaseBtn.addActionListener()
    }    
    // END OF CONSTRUCTOR
}