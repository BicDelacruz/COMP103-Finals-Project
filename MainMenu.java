import javax.print.DocFlavor.STRING;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.text.DecimalFormat;
//import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainMenu {

    final String JDBC_URL = "jdbc:sqlite:scooply_db.db";

    private JFrame frame;
    private JPanel menuPanel, itemsPanel, receiptPanel;
    private JTextArea receiptArea;
    private JLabel subTotalLabelPhp, totalDueLabelPhp, changeLabelPhp;
    private JTextField cashFieldPhp; // For cash input
    private double totalDuePhp = 0.0, subTotalPhp = 0.0, changePhp = 0.0;

    //Direct Peso Prices & Configuration
    private final double[] itemPricesPhp = {
            50.00, // Vanilla Ice Cream
            65.00, // Chocolate Ice Cream
            60.00, // Strawberry Ice Cream
            55.00, // Banana Ice Cream
            60.00, // Coconut Ice Cream
            70.00, // Kiwi Ice Cream
            65.00, // Mango Ice Cream
            75.00, // Ube Ice Cream (NEW)
            80.00  // Special Mixed Ice Cream
    };
    private final String CURRENCY_SYMBOL = "₱";
    private final DecimalFormat df = new DecimalFormat("0.00");

    private JTextField[] quantityFields; // Changed to JTextField for direct input
    private JLabel[] nameAndPriceLabels;
    private JLabel[] itemTotalLabelsPhp;
    private int[] itemQuantities;

    String[] itemNames = {
            "Vanilla Ice Cream", "Chocolate Ice Cream", "Strawberry Ice Cream", "Banana Ice Cream",
            "Coconut Ice Cream", "Kiwi Ice Cream", "Mango Ice Cream", "Ube Ice Cream",
            "Special Mixed Ice Cream"
    };

    //Unloading all the images here
    String[] imagePaths = {
            "images/menu_pics/Vanilla_ice_cream.png",
            "images/menu_pics/Chocolate_ice_cream.png",
            "images/menu_pics/Strawberry_ice_cream.png",
            "images/menu_pics/Banana_ice_cream.png",
            "images/menu_pics/Coconut_ice_cream.png",
            "images/menu_pics/Kiwifruit_ice_cream.png",
            "images/menu_pics/Mango_ice_cream.png",
            "images/menu_pics/Ube_ice_cream.png",
            "images/menu_pics/Special_mixed_icecream.png"
    };

    // Icon paths
    private final String MINUS_ICON_PATH = "images/icons/minus_icon.png";
    private final String PLUS_ICON_PATH = "images/icons/plus_icon.png";
    private final int ICON_SIZE = 20; // Desired icon size
    private final String LOGO_ICON_PATH = "images/logo/ice_cream_logo.png";

    // Receipt column widths
    private final int RECEIPT_ITEM_COL_WIDTH = 25; // Wider for wrapping
    private final int RECEIPT_QTY_COL_WIDTH = 5;
    private final int RECEIPT_TOTAL_COL_WIDTH = 10;
    private final int RECEIPT_TOTAL_WIDTH = RECEIPT_ITEM_COL_WIDTH + RECEIPT_QTY_COL_WIDTH + RECEIPT_TOTAL_COL_WIDTH + 4;

    ArrayList<ArrayList<String>> orderedItemsArrayList = new ArrayList<>();

    public MainMenu() {
        if (itemPricesPhp.length != itemNames.length || itemNames.length != imagePaths.length) {
            JOptionPane.showMessageDialog(null,
                    "Configuration error: Mismatch between number of items, prices, and images.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        initialize();
    }

    private void initialize() {

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

        // CENTER PANEL (Holds MENU label and itemsPanel)
        JPanel centerContainerPanel = new JPanel();
        centerContainerPanel.setLayout(new BorderLayout());
        centerContainerPanel.setBackground(new Color(245, 241, 235));
        centerContainerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // MENU Label
        JLabel menuTitleLabel = new JLabel("MENU");
        menuTitleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        menuTitleLabel.setForeground(new Color(71, 60, 56));
        menuTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        menuTitleLabel.setBorder(new EmptyBorder(0, 0, 15, 0)); // Padding below MENU
        centerContainerPanel.add(menuTitleLabel, BorderLayout.NORTH);


        // ITEMS MENU PANEL (CENTER)
        itemsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        itemsPanel.setBackground(new Color(245, 241, 235));
        // Removed direct border, now handled by centerContainerPanel's padding
        // itemsPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // No longer needed here

        itemQuantities = new int[itemNames.length];
        quantityFields = new JTextField[itemNames.length]; // Initialized as JTextField
        nameAndPriceLabels = new JLabel[itemNames.length];
        itemTotalLabelsPhp = new JLabel[itemNames.length];

        // Load icons for quantity buttons
        ImageIcon minusIconRaw = new ImageIcon(MINUS_ICON_PATH);
        ImageIcon plusIconRaw = new ImageIcon(PLUS_ICON_PATH);
        ImageIcon minusIcon = null;
        ImageIcon plusIcon = null;

        if (minusIconRaw.getImage().getWidth(null) == -1) {
            System.err.println("Warning: Minus icon not found at " + MINUS_ICON_PATH);
        } else {
            minusIcon = new ImageIcon(minusIconRaw.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
        }
        if (plusIconRaw.getImage().getWidth(null) == -1) {
            System.err.println("Warning: Plus icon not found at " + PLUS_ICON_PATH);
        } else {
            plusIcon = new ImageIcon(plusIconRaw.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
        }


        for (int i = 0; i < itemNames.length; i++) {
            final int index = i;
            itemQuantities[i] = 0;

            JPanel itemCard = new JPanel();
            itemCard.setLayout(new BoxLayout(itemCard, BoxLayout.Y_AXIS)); // Y_AXIS for text on top
            itemCard.setBackground(new Color(250, 248, 240));
            itemCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(71, 60, 56), 2),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            itemCard.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Name and Price Label (on top)
            nameAndPriceLabels[i] = new JLabel(itemNames[i] + " (" + CURRENCY_SYMBOL + df.format(itemPricesPhp[i]) + ")");
            nameAndPriceLabels[i].setFont(new Font("SansSerif", Font.BOLD, 15));
            nameAndPriceLabels[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            nameAndPriceLabels[i].setBorder(new EmptyBorder(0, 0, 8, 0)); // Padding below name

            // Image (Increased size)
            ImageIcon icon = new ImageIcon(imagePaths[i]);
            Image img = icon.getImage().getScaledInstance(150, 130, Image.SCALE_SMOOTH); // Increased image size
            JLabel imgLabel = new JLabel(new ImageIcon(img));
            imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            imgLabel.setBorder(BorderFactory.createLineBorder(new Color(100, 80, 70), 1));

            // Quantity Controls Panel
            JPanel qtyControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            qtyControlPanel.setBackground(itemCard.getBackground());

            JButton decreaseBtn = new JButton();
            if (minusIcon != null) decreaseBtn.setIcon(minusIcon); else decreaseBtn.setText("-");
            decreaseBtn.setMargin(new Insets(0, 0, 0, 0)); // Adjust margin for icons
            decreaseBtn.setPreferredSize(new Dimension(ICON_SIZE + 20, ICON_SIZE + 20)); // Adjust size for icons

            // Quantity JTextField for direct input
            quantityFields[i] = new JTextField(String.format("%02d", itemQuantities[i]));
            quantityFields[i].setFont(new Font("Arial", Font.BOLD, 22));
            quantityFields[i].setHorizontalAlignment(SwingConstants.CENTER);
            quantityFields[i].setBorder(new EmptyBorder(0, 5, 0, 5));
            quantityFields[i].setColumns(3); // Set preferred width for 3 digits

            JButton increaseBtn = new JButton();
            if (plusIcon != null) increaseBtn.setIcon(plusIcon); else increaseBtn.setText("+");
            increaseBtn.setMargin(new Insets(0, 0, 0, 0)); // Adjust margin for icons
            increaseBtn.setPreferredSize(new Dimension(ICON_SIZE + 20, ICON_SIZE + 20)); // Adjust size for icons

            qtyControlPanel.add(decreaseBtn);
            qtyControlPanel.add(quantityFields[i]);
            qtyControlPanel.add(increaseBtn);

            // Item Total Label
            itemTotalLabelsPhp[i] = new JLabel("Total: " + CURRENCY_SYMBOL + "0.00");
            itemTotalLabelsPhp[i].setFont(new Font("SansSerif", Font.BOLD, 14));
            itemTotalLabelsPhp[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            itemTotalLabelsPhp[i].setBorder(new EmptyBorder(8, 0, 5, 0));

            // Action Listeners for quantity buttons
            decreaseBtn.addActionListener(_ -> {
                if (itemQuantities[index] > 0) {
                    itemQuantities[index]--;
                    quantityFields[index].setText(String.format("%02d", itemQuantities[index])); // Update text field
                    updateItemDisplay(index);

                    String itemName = itemNames[index];
                    String itemQty = String.valueOf(itemQuantities[index]);
                    String total = itemTotalLabelsPhp[index].getText().substring(8);

                for (int x = orderedItemsArrayList.size() - 1; x >= 0; x--) {
                    if (orderedItemsArrayList.get(x).get(0).equals(itemName)) {
                        orderedItemsArrayList.get(x).set(1, itemQty);
                        orderedItemsArrayList.get(x).set(2, total);
                    }
                    if (orderedItemsArrayList.get(x).get(1).equals("0")) {
                        orderedItemsArrayList.remove(x);
                    }
                }

                    updateReceiptArea(); // Update receipt immediately
                }
            });

            increaseBtn.addActionListener(_ -> {
                if (itemQuantities[index] < 999) { // Max quantity 999
                    itemQuantities[index]++;
                    quantityFields[index].setText(String.format("%02d", itemQuantities[index])); // Update text field
                    updateItemDisplay(index);

                    String itemName = itemNames[index];
                    String itemQty = String.valueOf(itemQuantities[index]);
                    String total = itemTotalLabelsPhp[index].getText().substring(8);

                    ArrayList<String> order = new ArrayList<>();
                    boolean itemInList = false;

                    for (int x = 0; x < orderedItemsArrayList.size(); x++) {
                        if (orderedItemsArrayList.get(x).get(0) == itemName) itemInList = true;
                    }

                    if (itemInList) {
                        for (int x = 0; x < orderedItemsArrayList.size(); x++) {
                            if (orderedItemsArrayList.get(x).get(0) == itemName) {
                                orderedItemsArrayList.get(x).set(1, itemQty);
                                orderedItemsArrayList.get(x).set(2, total);
                            }
                        }
                    } else {
                        order.add(itemName);
                        order.add(itemQty);
                        order.add(total);
                        orderedItemsArrayList.add(order);
                    }

                    updateReceiptArea(); // Update receipt immediately
                }
            });

            // DocumentListener for direct quantity input with validation
            quantityFields[i].getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateQuantityFromField(index);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateQuantityFromField(index);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    // Plain text documents don't fire this
                }

                private void updateQuantityFromField(int idx) {
                    String text = quantityFields[idx].getText().trim();
                    if (text.isEmpty()) {
                        itemQuantities[idx] = 0;
                        quantityFields[idx].setBackground(Color.WHITE); // Reset background if empty
                    } else {
                        try {
                            int newQty = Integer.parseInt(text);
                            if (newQty >= 0 && newQty <= 999) {
                                itemQuantities[idx] = newQty;
                                quantityFields[idx].setBackground(Color.WHITE); // Valid input, reset color
                            } else {
                                // Quantity out of range
                                quantityFields[idx].setBackground(new Color(255, 200, 200)); // Light red for error
                                itemQuantities[idx] = -1; // Indicate invalid state for quantity
                            }
                        } catch (NumberFormatException ex) {
                            // Non-numeric input
                            quantityFields[idx].setBackground(new Color(255, 200, 200)); // Light red for error
                            itemQuantities[idx] = -1; // Indicate invalid state for quantity
                        }
                    }
                    if (itemQuantities[idx] != -1) { // Only update display if quantity is not in an error state
                        updateItemDisplay(idx);
                    }
                    updateReceiptArea(); // Update receipt immediately
                }
            });

            itemCard.add(nameAndPriceLabels[i]); // Name and Price on top
            itemCard.add(Box.createVerticalStrut(5));
            itemCard.add(imgLabel);
            itemCard.add(Box.createVerticalStrut(5));
            itemCard.add(qtyControlPanel);
            itemCard.add(itemTotalLabelsPhp[i]);
            itemCard.add(Box.createVerticalGlue());

            itemsPanel.add(itemCard);
        }
        JScrollPane itemsScrollPane = new JScrollPane(itemsPanel);
        itemsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerContainerPanel.add(itemsScrollPane, BorderLayout.CENTER); // Add scroll pane to center container
        frame.add(centerContainerPanel, BorderLayout.CENTER); // Add center container to frame

        // RECEIPT PANEL (RIGHT)
        receiptPanel = new JPanel();
        receiptPanel.setBackground(new Color(230, 230, 230));
        receiptPanel.setPreferredSize(new Dimension(380, 0));
        receiptPanel.setLayout(new BorderLayout(5, 5));
        receiptPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel clockLabel = new JLabel();
        clockLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        clockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        receiptPanel.add(clockLabel, BorderLayout.NORTH);
        updateClock(clockLabel);

        receiptArea = new JTextArea();
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        receiptPanel.add(scrollPane, BorderLayout.CENTER);

        // Summary Panel (SubTotal, Cash, Change, Total Due)
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // 4 rows now
        summaryPanel.setBackground(new Color(210, 210, 210));
        summaryPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // SubTotal
        summaryPanel.add(new JLabel("SubTotal (" + CURRENCY_SYMBOL + "):"));
        subTotalLabelPhp = new JLabel(df.format(0.00));
        subTotalLabelPhp.setFont(new Font("Monospaced", Font.BOLD, 14));
        summaryPanel.add(subTotalLabelPhp);

        // Cash (Input)
        summaryPanel.add(new JLabel("Cash (" + CURRENCY_SYMBOL + "):"));
        cashFieldPhp = new JTextField("0.00");
        cashFieldPhp.setFont(new Font("Monospaced", Font.BOLD, 14));
        cashFieldPhp.setHorizontalAlignment(JTextField.RIGHT);
        // Add DocumentListener to cashFieldPhp for real-time updates
        cashFieldPhp.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCashAndChangeDisplay();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCashAndChangeDisplay();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not used for plain text documents
            }
        });
        // Add FocusListener to ensure "0.00" is correctly handled on focus gain/loss
        cashFieldPhp.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (cashFieldPhp.getText().equals("0.00")) {
                    cashFieldPhp.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (cashFieldPhp.getText().trim().isEmpty()) {
                    cashFieldPhp.setText("0.00");
                }
                updateCashAndChangeDisplay(); // Ensure update on lost focus
            }
        });
        summaryPanel.add(cashFieldPhp);

        // Change
        summaryPanel.add(new JLabel("Change (" + CURRENCY_SYMBOL + "):"));
        changeLabelPhp = new JLabel(df.format(0.00));
        changeLabelPhp.setFont(new Font("Monospaced", Font.BOLD, 14));
        summaryPanel.add(changeLabelPhp);

        // Total Due
        summaryPanel.add(new JLabel("Total Due (" + CURRENCY_SYMBOL + "):"));
        totalDueLabelPhp = new JLabel(df.format(0.00));
        totalDueLabelPhp.setFont(new Font("Monospaced", Font.BOLD, 14));
        summaryPanel.add(totalDueLabelPhp);

        receiptPanel.add(summaryPanel, BorderLayout.SOUTH);
        frame.add(receiptPanel, BorderLayout.EAST);

        // CONTROL BUTTONS (BOTTOM)
        JPanel bottomControlPanel = new JPanel();
        bottomControlPanel.setBackground(new Color(250, 245, 235));
        bottomControlPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton confirmBtn = new JButton("Confirm Order and Print Receipt");
        confirmBtn.setBackground(new Color(0, 191, 99));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Consolas", Font.BOLD, 18));
        confirmBtn.addActionListener(_ -> confirmOrderAndPrintReceipt()); // New method for confirm logic
        bottomControlPanel.add(confirmBtn);

        JButton resetBtn = new JButton("Reset Order");
        resetBtn.setBackground(new Color(255, 49, 49));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFont(new Font("Consolas", Font.BOLD, 18));
        resetBtn.addActionListener(_ -> resetOrder());
        bottomControlPanel.add(resetBtn);

        frame.add(bottomControlPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Initial receipt area update
        updateReceiptArea();
    }

    private void updateItemDisplay(int index) {
        double itemTotal = itemQuantities[index] * itemPricesPhp[index];
        itemTotalLabelsPhp[index].setText("Total: " + CURRENCY_SYMBOL + df.format(itemTotal));
    }

    private void updateClock(JLabel label) {
        Timer timer = new Timer(1000, _ -> {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a EEEE, M-d-yyyy");
            label.setText(sdf.format(new Date()));
        });
        timer.start();
    }

    // New method to update the receipt area in real-time as items are added/removed
    private void updateReceiptArea() {
        subTotalPhp = 0.0;
        StringBuilder receiptPreview = new StringBuilder();
        receiptPreview.append("**************** Scooply Order ****************\n\n");
        receiptPreview.append(String.format("%-" + RECEIPT_ITEM_COL_WIDTH + "s %" + RECEIPT_QTY_COL_WIDTH + "s %" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                                             "Item (Price)", "Qty", "Total (" + CURRENCY_SYMBOL + ")"));
        receiptPreview.append(generateSeparatorLine(RECEIPT_TOTAL_WIDTH, '-') + "\n");

        boolean itemsSelected = false;
        boolean hasInvalidQuantity = false;

        String itemNameWithPrice = "";
        int qty = 0;
        double itemTotal = 0;

        for (int i = 0; i < itemNames.length; i++) {
            if (itemQuantities[i] == -1) { // Check for the invalid quantity marker
                hasInvalidQuantity = true;
                continue; // Skip this item for receipt calculation if its quantity is invalid
            }
            if (itemQuantities[i] > 0) {
                itemsSelected = true;
                qty = itemQuantities[i];
                double itemPrice = itemPricesPhp[i];
                itemTotal = qty * itemPrice;

                itemNameWithPrice = itemNames[i] + " (" + CURRENCY_SYMBOL + df.format(itemPrice) + ")";
                List<String> wrappedLines = wrapText(itemNameWithPrice, RECEIPT_ITEM_COL_WIDTH);

                // First line with quantity and total
                receiptPreview.append(String.format("%-" + RECEIPT_ITEM_COL_WIDTH + "s %" + RECEIPT_QTY_COL_WIDTH + "d %" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                                                     wrappedLines.get(0), qty, df.format(itemTotal)));

                
                // Subsequent lines (if any) for wrapping, indented
                for (int j = 1; j < wrappedLines.size(); j++) {
                    receiptPreview.append(String.format("%-" + RECEIPT_ITEM_COL_WIDTH + "s %" + RECEIPT_QTY_COL_WIDTH + "s %" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                                                         wrappedLines.get(j), "", "")); // Empty qty/total for wrapped lines
                }
                subTotalPhp += itemTotal;
            }
        }
    
        if (!itemsSelected && !hasInvalidQuantity) { // Only show "No items selected" if truly empty and no input errors
            receiptPreview.append("\n          No items selected yet.\n");
        } else if (hasInvalidQuantity) {
             receiptPreview.append("\n  Please correct highlighted quantity errors.\n");
        }


        receiptPreview.append(generateSeparatorLine(RECEIPT_TOTAL_WIDTH, '-') + "\n");

        int labelWidth = RECEIPT_ITEM_COL_WIDTH + RECEIPT_QTY_COL_WIDTH + 1; // +1 for the space
        receiptPreview.append(String.format("%-" + labelWidth + "s%" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                "SubTotal:", CURRENCY_SYMBOL + df.format(subTotalPhp)));
        totalDuePhp = subTotalPhp; // No tax, so total due is subtotal

        subTotalLabelPhp.setText(df.format(subTotalPhp));
        totalDueLabelPhp.setText(df.format(totalDuePhp));

        // Update change and cash display if cash field has a value
        updateCashAndChangeDisplay();

        receiptArea.setText(receiptPreview.toString());
    }

    // Helper method to update cash and change display based on current cashFieldPhp value
    private void updateCashAndChangeDisplay() {
        double cashReceived = 0.0;
        try {
            // Remove currency symbol and trim before parsing
            String cashText = cashFieldPhp.getText().replace(CURRENCY_SYMBOL, "").trim();
            if (!cashText.isEmpty()) {
                cashReceived = Double.parseDouble(cashText);
                if (cashReceived < 0) {
                    throw new NumberFormatException("Negative cash not allowed.");
                }
            } else {
                cashReceived = 0.0; // Treat empty as 0
            }
            cashFieldPhp.setBackground(Color.WHITE); // Reset background if valid
        } catch (NumberFormatException ex) {
            // Keep cashFieldPhp background red if invalid, but set cashReceived to 0 for calculations
            cashReceived = 0.0;
            cashFieldPhp.setBackground(new Color(255, 200, 200)); // Light red for error
            // Don't show JOptionPane here, as it would pop up on every invalid keystroke.
            // Error message will be shown on confirm.
        }

        changePhp = cashReceived - totalDuePhp;
        changeLabelPhp.setText(df.format(changePhp));
    }

    private void confirmOrderAndPrintReceipt() {
        // First, check for any invalid quantities in the item fields
        for (int i = 0; i < itemNames.length; i++) {
            String text = quantityFields[i].getText().trim();
            try {
                int qty = Integer.parseInt(text);
                if (qty < 0 || qty > 999) {
                    JOptionPane.showMessageDialog(frame,
                            "Please correct the quantity for '" + itemNames[i] + "'. It must be between 0 and 999.",
                            "Quantity Error", JOptionPane.ERROR_MESSAGE);
                    quantityFields[i].setBackground(new Color(255, 200, 200)); // Highlight
                    return; // Prevent confirmation
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame,
                        "Please correct the quantity for '" + itemNames[i] + "'. Only numbers are allowed.",
                        "Quantity Error", JOptionPane.ERROR_MESSAGE);
                quantityFields[i].setBackground(new Color(255, 200, 200)); // Highlight
                return; // Prevent confirmation
            }
        }


        if (subTotalPhp == 0.0) {
            JOptionPane.showMessageDialog(frame, "Please select at least one item before confirming.", "No Items Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double cashReceived = 0.0;
        try {
            String cashText = cashFieldPhp.getText().replace(CURRENCY_SYMBOL, "").trim();
            if (cashText.isEmpty()) {
                throw new NumberFormatException("Cash field is empty.");
            }
            cashReceived = Double.parseDouble(cashText);
            if (cashReceived < 0) {
                throw new NumberFormatException("Negative cash not allowed.");
            }
            cashFieldPhp.setBackground(Color.WHITE); // Reset background if valid
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Invalid cash amount entered. Please enter a valid non-negative number.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            cashFieldPhp.setBackground(new Color(255, 200, 200)); // Highlight error
            return;
        }

        if (cashReceived < totalDuePhp) {
            JOptionPane.showMessageDialog(frame,
                    "Cash received (" + CURRENCY_SYMBOL + df.format(cashReceived) + ") is less than the total amount due (" + CURRENCY_SYMBOL + df.format(totalDuePhp) + ").",
                    "Insufficient Cash", JOptionPane.WARNING_MESSAGE);
            return; // Do not proceed to print receipt
        }

        // --- All validations passed, finalize and print receipt ---
        changePhp = cashReceived - totalDuePhp; // Recalculate one last time to be sure

        StringBuilder finalReceipt = new StringBuilder();
        finalReceipt.append("*************** Scooply Receipt ***************\n\n");
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a dd-MM-yyyy");
        finalReceipt.append("Time: ").append(sdf.format(new Date())).append("\n\n");
        finalReceipt.append(String.format("%-" + RECEIPT_ITEM_COL_WIDTH + "s %" + RECEIPT_QTY_COL_WIDTH + "s %" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                                          "Item (Price)", "Qty", "Total (" + CURRENCY_SYMBOL + ")"));
        finalReceipt.append(generateSeparatorLine(RECEIPT_TOTAL_WIDTH, '-') + "\n");

        for (int i = 0; i < itemNames.length; i++) {
            if (itemQuantities[i] > 0) {
                int qty = itemQuantities[i];
                double itemPrice = itemPricesPhp[i];
                double itemTotal = qty * itemPrice;

                String itemNameWithPrice = itemNames[i] + " (" + CURRENCY_SYMBOL + df.format(itemPrice) + ")";
                List<String> wrappedLines = wrapText(itemNameWithPrice, RECEIPT_ITEM_COL_WIDTH);

                // First line with quantity and total
                finalReceipt.append(String.format("%-" + RECEIPT_ITEM_COL_WIDTH + "s %" + RECEIPT_QTY_COL_WIDTH + "d %" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                                                  wrappedLines.get(0), qty, df.format(itemTotal)));

                // Subsequent lines (if any) for wrapping, indented
                for (int j = 1; j < wrappedLines.size(); j++) {
                    finalReceipt.append(String.format("%-" + RECEIPT_ITEM_COL_WIDTH + "s %" + RECEIPT_QTY_COL_WIDTH + "s %" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                                                      wrappedLines.get(j), "", "")); // Empty qty/total for wrapped lines
                }
            }
        }

        finalReceipt.append(generateSeparatorLine(RECEIPT_TOTAL_WIDTH, '-') + "\n");
       
        // Calculate the width for the label part (Item + Qty + Space)
        int labelWidth = RECEIPT_ITEM_COL_WIDTH + RECEIPT_QTY_COL_WIDTH + 1; // +1 for the space

        // Format each line ensuring the value part is right-aligned
        finalReceipt.append(String.format("%-" + labelWidth + "s%" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                "SubTotal:", CURRENCY_SYMBOL + df.format(subTotalPhp)));
        finalReceipt.append(String.format("%-" + labelWidth + "s%" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                "Total Due:", CURRENCY_SYMBOL + df.format(totalDuePhp)));
        finalReceipt.append(String.format("%-" + labelWidth + "s%" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                "Cash Paid:", CURRENCY_SYMBOL + df.format(cashReceived)));
        finalReceipt.append(String.format("%-" + labelWidth + "s%" + RECEIPT_TOTAL_COL_WIDTH + "s\n",
                "Change:", CURRENCY_SYMBOL + df.format(changePhp)));

        finalReceipt.append("\n************ Thank You! Come Again! ************\n");

        receiptArea.setText(finalReceipt.toString());

        JOptionPane.showMessageDialog(frame, "Order Confirmed! Receipt Printed (displayed in receipt area).", "Order Finalized", JOptionPane.INFORMATION_MESSAGE);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String order_time = timeFormat.format(new Date());
        String order_date = dateFormat.format(new Date());
        //totalDuePhp
        //cashReceived
        //changePhp

        insertTransaction(order_time, order_date, totalDuePhp, cashReceived, changePhp);

        for (int i = 0; i < orderedItemsArrayList.size(); i++) {
            insertTransactionItems(order_date, order_time, orderedItemsArrayList.get(i).get(0), orderedItemsArrayList.get(i).get(1), orderedItemsArrayList.get(i).get(2));
        }

        // Removed the prompt for a new order. The application will now stay on the final receipt.
    }

    private void resetOrder() {
        orderedItemsArrayList.clear();
        for (int i = 0; i < itemNames.length; i++) {
            itemQuantities[i] = 0;
            quantityFields[i].setText(String.format("%02d", itemQuantities[i])); // Reset text field
            quantityFields[i].setBackground(Color.WHITE); // Reset background color
            updateItemDisplay(i); // Update item total labels
        }
        receiptArea.setText("");
        subTotalLabelPhp.setText(df.format(0.00));
        cashFieldPhp.setText("0.00"); // Reset cash field
        cashFieldPhp.setBackground(Color.WHITE); // Reset cash field background
        changeLabelPhp.setText(df.format(0.00)); // Reset change label
        totalDueLabelPhp.setText(df.format(0.00)); // Reset total due label

        subTotalPhp = 0.0;
        totalDuePhp = 0.0;
        changePhp = 0.0;
        updateReceiptArea(); // Refresh empty receipt
    }

    /**
     * Helper method to wrap text into multiple lines based on a maximum width.
     * Each line is padded to the maxWidth to maintain column alignment.
     * @param text The text to wrap.
     * @param maxWidth The maximum width for each line.
     * @return A list of strings, where each string is a wrapped line.
     */
    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add(String.format("%-" + maxWidth + "s", "")); // Add an empty, padded line
            return lines;
        }

        StringBuilder currentLine = new StringBuilder();
        // Split by space, but keep spaces for re-joining if needed
        String[] words = text.split(" ");

        for (String word : words) {
            // Check if adding the next word (plus a space if not the first word on line) exceeds maxWidth
            if (currentLine.length() + word.length() + (currentLine.length() > 0 ? 1 : 0) <= maxWidth) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                // Current word doesn't fit, so add currentLine to lines and start a new line with the word
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }
        lines.add(currentLine.toString()); // Add the last line

        // Pad each line to maxWidth for consistent column formatting
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() < maxWidth) {
                lines.set(i, String.format("%-" + maxWidth + "s", line));
            } else if (line.length() > maxWidth) {
                // This case should ideally not happen with proper word wrapping,
                // but as a fallback, truncate if a single word is longer than maxWidth
                lines.set(i, line.substring(0, maxWidth));
            }
        }
        return lines;
    }

    /**
     * Helper method to generate a separator line of a given length.
     * @param length The desired length of the separator line.
     * @param character The character to use for the separator (e.g., '-').
     * @return A string consisting of the specified character repeated 'length' times.
     */
    private String generateSeparatorLine(int length, char character) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(character);
        }
        return sb.toString();
    }

    private void insertTransaction(String order_time, String order_date, double order_total, double cash_given, double change_given ) {
        try {
            Connection conn = DriverManager.getConnection(JDBC_URL);
            String sqlQuery = "INSERT INTO transactions_tbl (time, date, order_total, cash_given, change_given) VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            
            preparedStatement.setString(1, order_time);
            preparedStatement.setString(2, order_date);
            preparedStatement.setDouble(3, order_total);
            preparedStatement.setDouble(4, cash_given);
            preparedStatement.setDouble(5, change_given);

            preparedStatement.executeUpdate();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertTransactionItems(String date, String time, String item_name, String quantity, String item_total) {
        try {
            Connection conn = DriverManager.getConnection(JDBC_URL);
            String sqlQuery = "INSERT INTO transaction_items_tbl (date, time, item_name, item_quantity, item_subtotal) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);

            preparedStatement.setString(1, date);
            preparedStatement.setString(2, time);
            preparedStatement.setString(3, item_name);
            preparedStatement.setDouble(4, Double.parseDouble(quantity));
            preparedStatement.setDouble(5, Double.parseDouble(item_total));

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

