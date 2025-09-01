// Name: <Your Name>
package boundary;

import javax.swing.*;
import java.awt.*;

public class ReportDisplayDialog extends JDialog {

    public ReportDisplayDialog(Frame owner, String title, String reportText) {
        super(owner, title, true);

        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- Report Display Area (CENTER) ---
        JTextArea taReportArea = new JTextArea(reportText);
        taReportArea.setEditable(false);
        taReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        taReportArea.setMargin(new Insets(5, 5, 5, 5));
        
        // Scroll to the top of the report
        taReportArea.setCaretPosition(0); 

        JScrollPane scrollPane = new JScrollPane(taReportArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);

        // --- Close Button (SOUTH) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
