import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

class Main {
    public static void main(String[] args) {

        JFrame main_window = new JFrame();
        main_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_window.setTitle("Scooply");
        main_window.setSize(1200, 720);
        main_window.setVisible(true);  
        main_window.setResizable(false);
        main_window.setLayout(null);

        Border black_line_border = BorderFactory.createLineBorder(Color.BLACK, 3);

        JPanel menu_panel = new JPanel();
        menu_panel.setBackground(new Color(0xc2b19c));
        menu_panel.setBounds(0, 0, 300, 720);
        menu_panel.setLayout(null);
        main_window.add(menu_panel);

        JLabel menu_title_label = new JLabel("Scooply");
        menu_title_label.setBackground(new Color(0x473c38));
        menu_title_label.setBounds(0, 0, 300, 100);
        menu_title_label.setOpaque(true);
        menu_title_label.setFont(new Font("Consolas", Font.PLAIN, 30));
        menu_title_label.setForeground(Color.WHITE);
        menu_title_label.setVerticalAlignment(JLabel.CENTER);
        menu_title_label.setHorizontalAlignment(JLabel.CENTER);
        menu_panel.add(menu_title_label);

        JLabel menu_label_btn = new JLabel("Main Menu");
        menu_label_btn.setBounds(20, 260, 250, 50);
        menu_label_btn.setBorder(black_line_border);
        menu_label_btn.setFont(new Font("Consolas", Font.PLAIN, 20));
        menu_label_btn.setVerticalAlignment(JLabel.CENTER);
        menu_label_btn.setHorizontalAlignment(JLabel.CENTER);
        menu_panel.add(menu_label_btn);

        JLabel transaction_label_btn = new JLabel("Transaction History");
        transaction_label_btn.setBounds(20, 340, 250, 50);
        transaction_label_btn.setBorder(black_line_border);
        transaction_label_btn.setFont(new Font("Consolas", Font.PLAIN, 20));
        transaction_label_btn.setVerticalAlignment(JLabel.CENTER);
        transaction_label_btn.setHorizontalAlignment(JLabel.CENTER);
        menu_panel.add(transaction_label_btn);

        JLabel earnings_label_btn = new JLabel("Total Earnings");
        earnings_label_btn.setBounds(20, 420, 250, 50);
        earnings_label_btn.setBorder(black_line_border);
        earnings_label_btn.setFont(new Font("Consolas", Font.PLAIN, 20));
        earnings_label_btn.setVerticalAlignment(JLabel.CENTER);
        earnings_label_btn.setHorizontalAlignment(JLabel.CENTER);
        menu_panel.add(earnings_label_btn);


    }
}