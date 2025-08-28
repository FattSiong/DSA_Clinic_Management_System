/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package boundary;

import adt.ListInterface;
import java.util.Iterator;
import entity.Patient;
import control.PatientControl;
import control.QueueControl;
import utility.ButtonColumn;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author hangg
 */
public class PatientListUI extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PatientListUI.class.getName());

    private PatientControl patientControl;
    private QueueControl queueControl;
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * Creates new form PatientListUI
     */
    public PatientListUI() {
        // It's better to pass the controllers in if this UI is opened from a main menu
        // For now, we instantiate them here.
        this.patientControl = new PatientControl();
        this.queueControl = new QueueControl();

        initComponents();
        setupTable();
        setupEventListeners();
    }

    // Constructor to be used if opened from a main application window
    public PatientListUI(PatientControl patientControl, QueueControl queueControl) {
        this.patientControl = patientControl;
        this.queueControl = queueControl;

        initComponents();
        setupTable();
        setupEventListeners();
    }

    private void setupTable() {
        populateTable();
        // Setup the button column for queue actions
        ButtonColumn buttonColumn = new ButtonColumn(tblPatient, queueAction, 8);
        // Setup the sorter for the search functionality
        DefaultTableModel model = (DefaultTableModel) tblPatient.getModel();
        sorter = new TableRowSorter<>(model);
        tblPatient.setRowSorter(sorter);
    }

    private void setupEventListeners() {
        // Listener for the search text field
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        // Listener for double-click to edit a patient
        tblPatient.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblPatient.getSelectedColumn() != 8) {
                    openEditPatientWindow();
                }
            }
        });

        // Listener to refresh the table when the window becomes active
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                populateTable();
            }
        });
    }

    private void filterTable() {
        String searchText = txtSearch.getText();
        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void populateTable() {
        DefaultTableModel model = (DefaultTableModel) tblPatient.getModel();
        model.setRowCount(0);

        ListInterface<Patient> patientList = patientControl.getPatientList();
        Iterator<Patient> iterator = patientList.getIterator();
        while (iterator.hasNext()) {
            Patient patient = iterator.next();
            // Use the new method from QueueControl to check the patient's status
            String queueStatus = queueControl.isPatientInQueue(patient) ? "Dequeue" : "Add to Queue";
            Object[] rowData = {
                patient.getPatientID(), patient.getName(), patient.getGender(),
                patient.getAge(), patient.getIcNumber(), patient.getContactNumber(),
                patient.getEmail(), patient.getMedicalHistory(), queueStatus
            };
            model.addRow(rowData);
        }
    }

    private void openEditPatientWindow() {
        int viewRow = tblPatient.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = tblPatient.convertRowIndexToModel(viewRow);
        String patientId = (String) tblPatient.getModel().getValueAt(modelRow, 0);
        Patient patientToEdit = patientControl.findPatientById(patientId);

        if (patientToEdit != null) {
            AddPatientUI editUI = new AddPatientUI(patientToEdit, patientControl);
            editUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent we) {
                    populateTable();
                }
            });
            editUI.setVisible(true);
        }
    }

    // This action is triggered by the button in the "Queue Action" column
    Action queueAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int modelRow = Integer.valueOf(e.getActionCommand());
            String patientId = (String) tblPatient.getModel().getValueAt(modelRow, 0);
            Patient patient = patientControl.findPatientById(patientId);

            if (patient != null) {
                if (queueControl.isPatientInQueue(patient)) {
                    // If patient is in queue, confirm and then dequeue
                    int response = JOptionPane.showConfirmDialog(
                            tblPatient,
                            "Are you sure you want to remove " + patient.getName() + " from the queue?",
                            "Confirm Dequeue", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        queueControl.dequeuePatient(patient);
                    }
                } else {
                    // If patient is not in queue, enqueue them
                    queueControl.enqueuePatient(patient);
                }
                // Refresh the table to show the updated button text
                populateTable();
            }
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrTblPatient = new javax.swing.JScrollPane();
        tblPatient = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnQueue = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnReport = new javax.swing.JButton();
        cmbReport = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblPatient.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Gender", "Age", "IC Number", "Contact Number", "Email", "Medical History", "Queue Action"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrTblPatient.setViewportView(tblPatient);

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnQueue.setText("Queue");
        btnQueue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQueueActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Search:");

        btnReport.setText("Report");
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        cmbReport.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PATIENT DEMOGRAPHICS REPORT", "OVERALL CLINIC PEAK HOURS REPORT" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrTblPatient, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1092, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmbReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReport)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnQueue)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAdd)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrTblPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnQueue)
                    .addComponent(btnDelete)
                    .addComponent(btnReport)
                    .addComponent(cmbReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        AddPatientUI addUI = new AddPatientUI(patientControl);
        addUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                populateTable();
            }
        });
        addUI.setVisible(true);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnQueueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQueueActionPerformed
        // This button now opens the main queue control panel
        // It's important that only ONE instance of QueueControl is used across the app
        new QueueControlUI(this.queueControl).setVisible(true);
    }//GEN-LAST:event_btnQueueActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int viewRow = tblPatient.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.", "No Patient Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = tblPatient.convertRowIndexToModel(viewRow);
        String patientId = (String) tblPatient.getModel().getValueAt(modelRow, 0);
        String patientName = (String) tblPatient.getModel().getValueAt(modelRow, 1);

        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete " + patientName + "?\nThis also removes them from the queue.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            Patient patientToDelete = patientControl.findPatientById(patientId);
            if (patientToDelete != null && queueControl.isPatientInQueue(patientToDelete)) {
                queueControl.dequeuePatient(patientToDelete);
            }

            if (patientControl.deletePatient(patientId)) {
                JOptionPane.showMessageDialog(this, "Patient " + patientName + " has been deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                populateTable();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not delete the patient.", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        String selectedReport = (String) cmbReport.getSelectedItem();

        // 2. Create a new instance of your PatientReportUI, passing it the
        //    selected report type and the necessary data controllers.
        new PatientReportUI(selectedReport, patientControl, queueControl).setVisible(true);    }//GEN-LAST:event_btnReportActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        //java.awt.EventQueue.invokeLater(() -> new PatientListUI().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnQueue;
    private javax.swing.JButton btnReport;
    private javax.swing.JComboBox<String> cmbReport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane scrTblPatient;
    private javax.swing.JTable tblPatient;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
