import javax.swing.*;
import java.awt.*;
//import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

class Earnings {

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
    }     
}