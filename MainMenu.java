import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

public class MainMenu {

    MainMenu() {
        /*------------------------ MAIN WINDOW --------------------------*/

        JFrame main_window = new JFrame();
        main_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_window.setTitle("Scooply");
        main_window.setSize(1300, 720);
        main_window.setVisible(true);  
        main_window.setResizable(false);
        main_window.setLayout(null);

        /*-------------------------------------------------------------*/

        Border black_line_border = BorderFactory.createLineBorder(Color.BLACK, 3);

        /*-------------------------- MAIN MENU ----------------------------*/

        JPanel menu_panel = new JPanel();
        menu_panel.setBackground(new Color(0xc2b19c));
        menu_panel.setBounds(0, 0, 250, 720);
        menu_panel.setLayout(null);
        main_window.add(menu_panel);

        JLabel menu_title_label = new JLabel("Scooply");
        menu_title_label.setBackground(new Color(0x473c38));
        menu_title_label.setBounds(0, 0, 250, 100);
        menu_title_label.setOpaque(true);
        menu_title_label.setFont(new Font("Consolas", Font.PLAIN, 30));
        menu_title_label.setForeground(Color.WHITE);
        menu_title_label.setVerticalAlignment(JLabel.CENTER);
        menu_title_label.setHorizontalAlignment(JLabel.CENTER);
        menu_panel.add(menu_title_label);

        JLabel menu_label_btn = new JLabel("Main Menu");
        menu_label_btn.setBounds(20, 260, 200, 50);
        menu_label_btn.setBorder(black_line_border);
        menu_label_btn.setFont(new Font("Consolas", Font.PLAIN, 20));
        menu_label_btn.setVerticalAlignment(JLabel.CENTER);
        menu_label_btn.setHorizontalAlignment(JLabel.CENTER);
        menu_panel.add(menu_label_btn);

        JLabel transaction_label_btn = new JLabel("Transaction History");
        transaction_label_btn.setBounds(20, 340, 200, 50);
        transaction_label_btn.setBorder(black_line_border);
        transaction_label_btn.setFont(new Font("Consolas", Font.PLAIN, 17));
        transaction_label_btn.setVerticalAlignment(JLabel.CENTER);
        transaction_label_btn.setHorizontalAlignment(JLabel.CENTER);
        menu_panel.add(transaction_label_btn);

        JLabel earnings_label_btn = new JLabel("Total Earnings");
        earnings_label_btn.setBounds(20, 420, 200, 50);
        earnings_label_btn.setBorder(black_line_border);
        earnings_label_btn.setFont(new Font("Consolas", Font.PLAIN, 20));
        earnings_label_btn.setVerticalAlignment(JLabel.CENTER);
        earnings_label_btn.setHorizontalAlignment(JLabel.CENTER);
        menu_panel.add(earnings_label_btn);

        /*---------------------------------------------------------------*/

        /*-------------------------- ITEMS MENU -------------------------*/

        JPanel items_menu_panel = new JPanel();
        items_menu_panel.setBackground(new Color(0xFFFFFF));
        items_menu_panel.setBounds(270, 20, 700, 570);
        items_menu_panel.setLayout(null);
        main_window.add(items_menu_panel);

        /*----------------------------------------------------------------*/

        /*---------------------- ORDER BUTTONS -------------------------*/

        JLabel confirm_label_btn = new JLabel("Confirm Order and Print Reciept");
        confirm_label_btn.setBounds(270, 600, 330, 50);
        confirm_label_btn.setBackground(new Color(0x00bf63));
        confirm_label_btn.setOpaque(true);
        confirm_label_btn.setFont(new Font("Consolas", Font.PLAIN, 17));
        confirm_label_btn.setVerticalAlignment(JLabel.CENTER);
        confirm_label_btn.setHorizontalAlignment(JLabel.CENTER);
        main_window.add(confirm_label_btn);

        JLabel reset_label_btn = new JLabel("Reset Order");
        reset_label_btn.setBounds(620, 600, 200, 50);
        reset_label_btn.setBackground(new Color(0xff3131));
        reset_label_btn.setOpaque(true);
        reset_label_btn.setFont(new Font("Consolas", Font.PLAIN, 20));
        reset_label_btn.setVerticalAlignment(JLabel.CENTER);
        reset_label_btn.setHorizontalAlignment(JLabel.CENTER);
        main_window.add(reset_label_btn);

        /*--------------------------------------------------------------*/
    }
    
        
}