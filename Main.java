import javax.swing.*;
import java.awt.*;

class Main {
    public static void main(String[] args) {

        JFrame main_window = new JFrame();
        main_window.setTitle("Scooply");
        main_window.setSize(1200, 720);
        main_window.setVisible(true);  
        main_window.setResizable(false);
        main_window.setLayout(null);

        JPanel menu_panel = new JPanel();
        menu_panel.setBackground(new Color(0xc2b19c));
        menu_panel.setBounds(0, 0, 300, 720);
        main_window.add(menu_panel);

        JLabel menu_title_label = new JLabel();
        menu_title_label.setBackground(new Color(0x473c38));
        menu_title_label.setBounds(0,0,300, 100);
        menu_title_label.setOpaque(true);
        menu_title_label.setText("Scooply");
        menu_title_label.setFont(new Font("Consolas", Font.PLAIN, 30));
        menu_title_label.setForeground(Color.WHITE);
        menu_title_label.setVerticalAlignment(JLabel.CENTER);
        menu_title_label.setHorizontalAlignment(JLabel.CENTER);
        menu_panel.add(menu_title_label);

    }
}