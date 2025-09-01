package boundary;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIStyle {

    // Color Palette
    public static final Color HEADER_BACKGROUND = new Color(0, 92, 152); // Dark Blue
    public static final Color MAIN_BACKGROUND = new Color(240, 240, 240); // Light Grey
    public static final Color PRIMARY_ACTION_COLOR = new Color(76, 175, 80);  // Green
    public static final Color SECONDARY_ACTION_COLOR = new Color(30, 136, 229); // Blue
    public static final Color DESTRUCTIVE_ACTION_COLOR = new Color(251, 140, 0); // Orange
    public static final Color HEADER_TEXT_COLOR = Color.WHITE;
    public static final Color BUTTON_TEXT_COLOR = Color.WHITE;

    // Fonts
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_TITLE_BORDER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_BODY = new Font("Segoe UI", Font.PLAIN, 12);

    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(FONT_HEADER);
        label.setForeground(HEADER_TEXT_COLOR);
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        return label;
    }

    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(backgroundColor);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(140, 35)); // Give a preferred size
        return button;
    }
    
    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE_BODY);
        table.setRowHeight(25);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(SECONDARY_ACTION_COLOR);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.BLACK);
        header.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
    }

    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BACKGROUND);
        
        Border lineBorder = BorderFactory.createLineBorder(new Color(200, 200, 200));
        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, title);
        titledBorder.setTitleFont(FONT_TITLE_BORDER);
        titledBorder.setTitleColor(HEADER_BACKGROUND);

        Border padding = new EmptyBorder(10, 10, 10, 10);
        panel.setBorder(BorderFactory.createCompoundBorder(titledBorder, padding));

        return panel;
    }
}