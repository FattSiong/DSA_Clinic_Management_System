package boundary;

import adt.ListInterface;
import control.ScheduleControl;
import entity.Doctor;
import entity.Schedule;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleManagementUI extends JFrame {

    private ScheduleControl controller;
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cmbSearchBy;

    public ScheduleManagementUI(ScheduleControl controller) {
        this.controller = controller;
        setTitle("Consultation & Appointment Dashboard");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyle.MAIN_BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyle.HEADER_BACKGROUND);
        JLabel lblTitle = UIStyle.createHeaderLabel("Schedule Management");
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 10));
        centerContainer.setBackground(UIStyle.MAIN_BACKGROUND);
        centerContainer.setBorder(new EmptyBorder(10, 10, 0, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(UIStyle.MAIN_BACKGROUND);
        
        JLabel lblSearch = new JLabel("Search For:");
        txtSearch = new JTextField(20);
        String[] searchOptions = {"By Doctor", "By Date (YYYY-MM-DD)", "By Availability", "By Time (HH:mm)"};
        cmbSearchBy = new JComboBox<>(searchOptions);
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(cmbSearchBy);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);

        centerContainer.add(searchPanel, BorderLayout.NORTH);

        JPanel tablePanel = UIStyle.createTitledPanel("Weekly Schedules");
        model = new DefaultTableModel(new String[]{"ID", "Doctor", "Date", "Start", "End", "Availability"}, 0);
        table = new JTable(model);
        UIStyle.styleTable(table);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        centerContainer.add(tablePanel, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        btnPanel.setBackground(UIStyle.MAIN_BACKGROUND);
        
        JButton btnAdd = UIStyle.createStyledButton("Add Schedule", UIStyle.PRIMARY_ACTION_COLOR);
        JButton btnUpd = UIStyle.createStyledButton("Update Selected", UIStyle.SECONDARY_ACTION_COLOR);
        JButton btnDel = UIStyle.createStyledButton("Delete Selected", UIStyle.DESTRUCTIVE_ACTION_COLOR);
        JButton btnRefresh = UIStyle.createStyledButton("Refresh Status", UIStyle.SECONDARY_ACTION_COLOR);
        JButton btnGenerateReport = UIStyle.createStyledButton("Generate Report", UIStyle.SECONDARY_ACTION_COLOR);
        JButton btnBack = UIStyle.createStyledButton("Back", new Color(120, 120, 120));
        JButton btnBackMain = UIStyle.createStyledButton("Back to Main", new Color(120, 120, 120));

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpd);
        btnPanel.add(btnDel);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnGenerateReport);
        btnPanel.add(btnBack);
        btnPanel.add(btnBackMain);
        add(btnPanel, BorderLayout.SOUTH);

        // Action listeners that call the controller
        btnSearch.addActionListener(e -> controller.performSearch(txtSearch.getText(), (String) cmbSearchBy.getSelectedItem()));
        btnClear.addActionListener(e -> controller.loadInitialSchedules());
        btnAdd.addActionListener(e -> controller.addSchedule());
        btnUpd.addActionListener(e -> controller.updateSchedule());
        btnDel.addActionListener(e -> controller.deleteSchedule());
        btnRefresh.addActionListener(e -> controller.refreshDoctorAvailability());
        btnGenerateReport.addActionListener(e -> controller.generateWorkloadReport());
        btnBack.addActionListener(e -> controller.close());
        btnBackMain.addActionListener(e -> controller.closeToMain());
    }
    
    public void displaySchedules(ListInterface<Schedule> schedules) {
        model.setRowCount(0);
        if (schedules == null) return;
        for (int i = 1; i <= schedules.getLength(); i++) {
            Schedule s = schedules.getEntry(i);
            model.addRow(new Object[]{
                s.getScheduleID(),
                s.getDoctor() != null ? s.getDoctor().getName() : "N/A",
                s.getDutyDate(),
                s.getStartTime(),
                s.getEndTime(),
                s.getAvailability()
            });
        }
    }

    public void clearSearch() {
        txtSearch.setText("");
    }
    
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    public void showReport(String reportContent, String title) {
        JTextArea textArea = new JTextArea(reportContent);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 250));
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public String getSelectedScheduleId() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Please select a schedule from the table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) model.getValueAt(row, 0);
    }
    
    public Schedule showScheduleInputDialog(Schedule scheduleToUpdate, ListInterface<Doctor> doctorList) {
        JTextField idField = new JTextField();
        JComboBox<Doctor> doctorBox = new JComboBox<>();
        for (int i = 1; i <= doctorList.getLength(); i++) {
            doctorBox.addItem(doctorList.getEntry(i));
        }
        doctorBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Doctor) {
                    setText(((Doctor) value).getName());
                }
                return this;
            }
        });
        
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField startField = new JTextField("09:00");
        JTextField endField = new JTextField("17:00");

        Object[] fields;
        if (scheduleToUpdate != null) {
            idField.setText(scheduleToUpdate.getScheduleID());
            idField.setEditable(false);
            doctorBox.setSelectedItem(scheduleToUpdate.getDoctor());
            doctorBox.setEnabled(false);
            dateField.setText(scheduleToUpdate.getDutyDate().toString());
            startField.setText(scheduleToUpdate.getStartTime().toString());
            endField.setText(scheduleToUpdate.getEndTime().toString());
            fields = new Object[] { "Date:", dateField, "Start Time:", startField, "End Time:", endField };
        } else {
            fields = new Object[] { "ID:", idField, "Doctor:", doctorBox, "Date:", dateField, "Start Time:", startField, "End Time:", endField };
        }

        int option = JOptionPane.showConfirmDialog(this, fields, 
            scheduleToUpdate == null ? "Add New Schedule" : "Update Schedule",
            JOptionPane.OK_CANCEL_OPTION);
            
        if (option == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText().trim();
                Doctor doc = (Doctor) doctorBox.getSelectedItem();
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                LocalTime start = LocalTime.parse(startField.getText().trim());
                LocalTime end = LocalTime.parse(endField.getText().trim());
                return new Schedule(id, doc, date, start, end, "Available");
            } catch (Exception ex) {
                showMessage("Invalid input. Please check the formats.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}