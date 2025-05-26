import javax.swing.*;
import java.awt.*;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.border.EmptyBorder;

class Transactions {

    private JFrame frame;
    private JPanel menuPanel, centerContainerPanel; // Added centerContainerPanel as instance variable

    private final String LOGO_ICON_PATH = "images/logo/ice_cream_logo.png";

    final String JDBC_URL = "jdbc:sqlite:scooply_db.db";

    Transactions() {
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

        // Transaction History Label
        JLabel transactTitleLabel = new JLabel("Transaction History");
        transactTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        transactTitleLabel.setForeground(new Color(71, 60, 56));
        transactTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        transactTitleLabel.setBorder(new EmptyBorder(30, 0, 15, 0));
        centerContainerPanel.add(transactTitleLabel, BorderLayout.NORTH);    
        frame.add(centerContainerPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        JPanel transactionsPanel = new JPanel();
        transactionsPanel.setLayout(new GridLayout(0,1,10,10));

        JPanel orderedItemsPanel = new JPanel();
        orderedItemsPanel.setLayout(new GridLayout(0, 1, 10, 10));
        orderedItemsPanel.setPreferredSize(new Dimension(700, 0));
        orderedItemsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        centerContainerPanel.add(orderedItemsPanel, BorderLayout.EAST);

        try {
            Connection conn = DriverManager.getConnection(JDBC_URL);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM transactions_tbl");

            while (resultSet.next()) {
                JPanel transactionCardPanel = new JPanel();
                transactionCardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                transactionCardPanel.setLayout(new GridLayout(0,1,0,5));
                transactionCardPanel.setPreferredSize(new Dimension(0, 200));
                transactionCardPanel.setBackground(new Color(0xCDD8DC));
                transactionCardPanel.setOpaque(true);
                transactionsPanel.add(transactionCardPanel);

                JLabel dateLabel = new JLabel("Date Ordered: " + resultSet.getString("date"));
                dateLabel.setHorizontalAlignment(JLabel.CENTER);
                dateLabel.setFont(new Font("Consolas", Font.PLAIN, 21));
                dateLabel.setBackground(new Color(245, 245, 245));
                dateLabel.setOpaque(true);
                transactionCardPanel.add(dateLabel);

                JLabel timeLabel = new JLabel("Time Ordered: " + resultSet.getString("time"));
                timeLabel.setHorizontalAlignment(JLabel.CENTER);
                timeLabel.setFont(new Font("Consolas", Font.PLAIN, 21));
                timeLabel.setBackground(new Color(245, 245, 245));
                timeLabel.setOpaque(true);
                transactionCardPanel.add(timeLabel);
                
                JLabel orderTotalLabel = new JLabel("Order Total: " + resultSet.getString("order_total"));
                orderTotalLabel.setHorizontalAlignment(JLabel.CENTER);
                orderTotalLabel.setFont(new Font("Consolas", Font.PLAIN, 21));
                orderTotalLabel.setBackground(new Color(245, 245, 245));
                orderTotalLabel.setOpaque(true);
                transactionCardPanel.add(orderTotalLabel);

                JLabel cashGivenLabel = new JLabel("Cash Received: " + resultSet.getString("cash_given"));
                cashGivenLabel.setHorizontalAlignment(JLabel.CENTER);
                cashGivenLabel.setFont(new Font("Consolas", Font.PLAIN, 21));
                cashGivenLabel.setBackground(new Color(245, 245, 245));
                cashGivenLabel.setOpaque(true);
                transactionCardPanel.add(cashGivenLabel);

                JLabel changeGivenLabel = new JLabel("Change Given: " + resultSet.getString("change_given"));
                changeGivenLabel.setFont(new Font("Consolas", Font.PLAIN, 21));
                changeGivenLabel.setHorizontalAlignment(JLabel.CENTER);
                changeGivenLabel.setBackground(new Color(245, 245, 245));
                changeGivenLabel.setOpaque(true);
                transactionCardPanel.add(changeGivenLabel);

                JButton viewOrderedItemsBtn = new JButton("View Ordered Items");                
                viewOrderedItemsBtn.setFont(new Font("Consolas", Font.BOLD, 17));
                transactionCardPanel.add(viewOrderedItemsBtn);
                
                viewOrderedItemsBtn.addActionListener(e -> {
                try {
                        String time = timeLabel.getText().substring(14);
                        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM transaction_items_tbl WHERE time = ?");
                        preparedStatement.setString(1, time);
                        ResultSet res = preparedStatement.executeQuery(); 

                        JPanel orderedItemsPanel2 = new JPanel();
                        orderedItemsPanel2.setLayout(new GridLayout(0, 1, 10, 10));
                        orderedItemsPanel2.setPreferredSize(new Dimension(700, 0));
                        orderedItemsPanel2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        centerContainerPanel.add(orderedItemsPanel2, BorderLayout.EAST);
                        centerContainerPanel.remove(orderedItemsPanel);

                        boolean firstRow = true;

                        while (res.next()) {
                            

                            if (firstRow) {
                                JLabel dateOrderedLabel = new JLabel(res.getString("date") + "  |  " + res.getString("time"));
                                dateOrderedLabel.setFont(new Font("Consolas", Font.BOLD, 25));
                                dateOrderedLabel.setHorizontalAlignment(JLabel.CENTER);
                                dateOrderedLabel.setBackground(new Color(71, 60, 56));
                                dateOrderedLabel.setForeground(Color.WHITE);
                                dateOrderedLabel.setOpaque(true);
                                orderedItemsPanel2.add(dateOrderedLabel);

                                firstRow = false;
                            }
                            
                            JLabel itemOrderedJLabel = new JLabel(res.getString("item_name") + "  |   x" + res.getInt("item_quantity")
                            + "   |   "  +  res.getInt("item_subtotal"));
                            itemOrderedJLabel.setFont(new Font("Consolas", Font.PLAIN, 25));
                            itemOrderedJLabel.setHorizontalAlignment(JLabel.CENTER);
                            itemOrderedJLabel.setBackground(new Color(194, 177, 156));
                            itemOrderedJLabel.setOpaque(true);
                            orderedItemsPanel2.add(itemOrderedJLabel);
                            
                            
                            orderedItemsPanel2.revalidate();
                            orderedItemsPanel2.repaint();
                        }
                    } catch (SQLException x) {
                        x.printStackTrace();
                    }
                });

            }
            

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane itemsScrollPane = new JScrollPane(transactionsPanel);
        itemsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerContainerPanel.add(itemsScrollPane, BorderLayout.CENTER);
    }
    // END OF CONSTRUCTOR
}
