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

    int customersTotal = 0;
    int incomeToday = 0;
    int itemsSold = 0;
    int totalIncome = 0;

    final String JDBC_URL = "jdbc:sqlite:scooply_db.db";

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

        customersTotal = totalIncomeArrList.size(); // A person who thinks all the time...
        for (int num : totalIncomeArrList) {totalIncome+=num; }

        for (int i = 0; i < earningsCardTitles.length; i++) {
            JPanel earningsCardPanel = new JPanel();
            earningsCardPanel.setLayout(new BorderLayout());
            earningsCardPanel.setPreferredSize(new Dimension(450, 200));
            earningsCardPanel.setBackground(new Color(194, 177, 156));
            earningsPanel.add(earningsCardPanel);

            JLabel earningCardTitleLabel = new JLabel(earningsCardTitles[i], JLabel.CENTER);
            earningCardTitleLabel.setFont(new Font("Consolas", Font.PLAIN, 30));
            earningCardTitleLabel.setBorder(new EmptyBorder(30, 0, 0, 0));
            earningsCardPanel.add(earningCardTitleLabel, BorderLayout.NORTH);

            ImageIcon img = new ImageIcon(earningsCardImgURL[i]);
            ImageIcon imgScaled = new ImageIcon(img.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH));

            JLabel earningsCardContentLabel = new JLabel("000000000", JLabel.CENTER);
            earningsCardContentLabel.setIcon(imgScaled);
            earningsCardContentLabel.setIconTextGap(30);
            earningsCardContentLabel.setFont(new Font("Consolas", Font.BOLD, 35));
            earningsCardPanel.add(earningsCardContentLabel, BorderLayout.CENTER);

            switch (i) { 
                case 0: // Number of Customers
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

        }
        
        JPanel buttonsPanel = new JPanel();
        centerContainerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        JButton clearDatabaseBtn = new JButton("Clear Database");
        clearDatabaseBtn.setBackground(Color.RED);
        clearDatabaseBtn.setForeground(Color.WHITE);
        clearDatabaseBtn.setPreferredSize(new Dimension(200, 50));
        clearDatabaseBtn.setFont(new Font("Consolas", Font.BOLD, 20));
        buttonsPanel.add(clearDatabaseBtn, BorderLayout.SOUTH);

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
        });
    }    
    // END OF CONSTRUCTOR
}