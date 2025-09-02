package boundary;

import entity.Consultation;
import entity.Medicine;
import entity.Treatment;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class TreatmentDetailDialog extends JDialog {

    public TreatmentDetailDialog(Frame owner, Treatment treatment) {
        super(owner, "Treatment Record Details", true);
        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initComponents(treatment);
    }

    private void initComponents(Treatment treatment) {
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Treatment Details: " + treatment.getTreatmentID());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        Consultation consultation = treatment.getConsultation();
        JPanel consultationPanel = createSectionPanel("Consultation Info");
        consultationPanel.add(createDetailRow("Consultation ID:", consultation.getConsultationID()));
        consultationPanel.add(createDetailRow("Patient Name:", consultation.getPatient().getName()));
        consultationPanel.add(createDetailRow("Patient IC:", consultation.getPatient().getIcNumber()));
        consultationPanel.add(createDetailRow("Doctor Name:", consultation.getDoctor().getName()));
        consultationPanel.add(createDetailRow("Date:", consultation.getConsultationDate().toString()));
        consultationPanel.add(createDetailRow("Diagnosis:", consultation.getDiagnosis()));
        mainPanel.add(consultationPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        
        JPanel treatmentPanel = createSectionPanel("Treatment Info");
        JTextArea notesArea = new JTextArea(treatment.getTreatmentDetails());
        notesArea.setEditable(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(this.getBackground());
        notesArea.setFont(new Font("Arial", Font.PLAIN, 12));
        treatmentPanel.add(createDetailRow("Treatment Notes:", new JScrollPane(notesArea)));
        mainPanel.add(treatmentPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        
        JPanel medicinePanel = createSectionPanel("Prescribed Medicines");
        DefaultListModel<String> medicineListModel = new DefaultListModel<>();
        Iterator<Medicine> medIt = treatment.getPrescribedMedicine().getIterator();
        while (medIt.hasNext()) {
            medicineListModel.addElement("- " + medIt.next().getName());
        }
        JList<String> medicineList = new JList<>(medicineListModel);
        medicinePanel.add(new JScrollPane(medicineList));
        mainPanel.add(medicinePanel);

        add(mainPanel, BorderLayout.CENTER);

        
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        footerPanel.add(btnClose);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }
    
    
    private JPanel createDetailRow(String labelText, String valueText) {
        JPanel rowPanel = new JPanel(new BorderLayout(5, 0));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setPreferredSize(new Dimension(120, 20));
        rowPanel.add(label, BorderLayout.WEST);
        rowPanel.add(new JLabel(valueText), BorderLayout.CENTER);
        return rowPanel;
    }

    
    private JPanel createDetailRow(String labelText, Component component) {
        JPanel rowPanel = new JPanel(new BorderLayout(5, 0));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setPreferredSize(new Dimension(120, 20));
        rowPanel.add(label, BorderLayout.NORTH);
        rowPanel.add(component, BorderLayout.CENTER);
        return rowPanel;
    }
}

