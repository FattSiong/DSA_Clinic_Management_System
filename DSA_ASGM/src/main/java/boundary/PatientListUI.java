/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package boundary;

import adt.DoublyLinkedList;
import adt.ListInterface;
import java.util.Iterator;
import entity.Patient;
import control.PatientControl;
import control.QueueControl;
import utility.ButtonColumn;
import utility.PatientComparators;
import utility.ShowMessage;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author hangg
 */
public class PatientListUI extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PatientListUI.class.getName());

    private PatientControl patientControl;
    private QueueControl queueControl;

    /**
     * Creates new form PatientListUI
     * 
     * @param patientControl
     * @param queueControl
     */
    public PatientListUI(PatientControl patientControl, QueueControl queueControl) {
        this.patientControl = patientControl;
        this.queueControl = queueControl;

        initComponents();
        setupTable();
        setupEventListeners();
    }

    private void setupEventListeners() {
        tblPatient.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblPatient.getSelectedColumn() != 8) {
                    openEditPatientWindow();
                }
            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                populateTable();
            }
        });
    }

    private void setupTable() {
        patientControl.getPatientList().sort(new PatientComparators.SortById());
        populateTable();

        ButtonColumn buttonColumn = new ButtonColumn(tblPatient, queueAction, 8);
    }

    private void populateTable() {
        populateTable(patientControl.getPatientList());
    }

    private void populateTable(ListInterface<Patient> patientsToDisplay) {
        DefaultTableModel model = (DefaultTableModel) tblPatient.getModel();
        model.setRowCount(0);

        if (patientsToDisplay == null || patientsToDisplay.isEmpty()) {
            return;
        }

        Iterator<Patient> iterator = patientsToDisplay.getIterator();
        while (iterator.hasNext()) {
            Patient patient = iterator.next();
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

    Action queueAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int modelRow = Integer.valueOf(e.getActionCommand());
            String patientId = (String) tblPatient.getModel().getValueAt(modelRow, 0);
            Patient patient = patientControl.findPatientById(patientId);

            if (patient != null) {
                if (queueControl.isPatientInQueue(patient)) {
                    int response = ShowMessage.showConfirm(
                            tblPatient,
                            "Are you sure you want to remove " + patient.getName() + " from the queue?",
                            "Confirm Dequeue");
                    if (response == JOptionPane.YES_OPTION) {
                        queueControl.dequeuePatient(patient);
                    }
                } else {
                    queueControl.enqueuePatient(patient);
                }
                populateTable();
            }
        }
    };

    private void performSearch() {
        String searchTerm = txtSearch.getText().trim();
        String searchBy = (String) cmbSearch.getSelectedItem();

        if (searchTerm.isEmpty()) {
            populateTable();
            return;
        }

        ListInterface<Patient> results = new DoublyLinkedList<>();

        switch (searchBy) {
            case "ID":
                Patient foundById = patientControl.findPatientByIdBinarySearch(searchTerm);
                if (foundById != null) {
                    results.add(foundById);
                }
                break;

            case "Name":
                results = patientControl.findPatientsByName(searchTerm);
                break;

            case "Age":
                try {
                    int searchAge = Integer.parseInt(searchTerm);
                    results = patientControl.findPatientsByAge(searchAge);
                } catch (NumberFormatException e) {
                    ShowMessage.showError(this, "Please enter a valid number for age.");
                }
                break;

            case "IC Number":
                Patient foundByIc = patientControl.findPatientByIc(searchTerm);
                if (foundByIc != null) {
                    results.add(foundByIc);
                }
                break;

            case "Contact Number":
                Patient foundByContact = patientControl.findPatientByPhoneNumber(searchTerm);
                if (foundByContact != null) {
                    results.add(foundByContact);
                }
                break;

            case "Email":
                Patient foundByEmail = patientControl.findPatientByEmail(searchTerm);
                if (foundByEmail != null) {
                    results.add(foundByEmail);
                }
                break;
        }

        populateTable(results);

        if (results.isEmpty()) {
            ShowMessage.showInfo(this, "No patients found matching your criteria.", "Search Results");
        }
    }

    private void performSort() {
        String sortBy = (String) cmbSort.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) tblPatient.getModel();
        ListInterface<Patient> currentList = new DoublyLinkedList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String patientId = (String) model.getValueAt(i, 0);
            currentList.add(patientControl.findPatientById(patientId));
        }

        switch (sortBy) {
            case "Sort by ID":
                currentList.sort(new PatientComparators.SortById());
                break;
            case "Sort by Name":
                currentList.sort(new PatientComparators.SortByName());
                break;
            case "Sort by Age":
                currentList.sort(new PatientComparators.SortByAge());
                break;
        }

        populateTable(currentList);
    }

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
        cmbReport = new javax.swing.JComboBox<>();
        btnReport = new javax.swing.JButton();
        pnlSearchSort = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        lblBy = new javax.swing.JLabel();
        cmbSearch = new javax.swing.JComboBox<>();
        btnSearch = new javax.swing.JButton();
        cmbSort = new javax.swing.JComboBox<>();
        btnSort = new javax.swing.JButton();

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

        cmbReport.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PATIENT DEMOGRAPHICS REPORT", "OVERALL CLINIC PEAK HOURS REPORT" }));

        btnReport.setText("Report");
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        lblSearch.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblSearch.setText("Search:");

        lblBy.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblBy.setText("By");

        cmbSearch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID", "Name", "Age", "IC Number", "Contact Number", "Email" }));

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        cmbSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort by ID", "Sort by Name", "Sort by Age" }));

        btnSort.setText("Sort");
        btnSort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSortActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSearchSortLayout = new javax.swing.GroupLayout(pnlSearchSort);
        pnlSearchSort.setLayout(pnlSearchSortLayout);
        pnlSearchSortLayout.setHorizontalGroup(
            pnlSearchSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchSortLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBy, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmbSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSort)
                .addContainerGap())
        );
        pnlSearchSortLayout.setVerticalGroup(
            pnlSearchSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchSortLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSearchSortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBy, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(cmbSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSort))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlSearchSort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrTblPatient, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cmbReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQueue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdd)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlSearchSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrTblPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnQueue)
                    .addComponent(btnDelete)
                    .addComponent(cmbReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReport))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
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
        new QueueControlUI(this.queueControl).setVisible(true);
    }//GEN-LAST:event_btnQueueActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int viewRow = tblPatient.getSelectedRow();
        if (viewRow == -1) {
            ShowMessage.showInfo(this, "Please select a patient to delete.", "No Patient Selected");
            return;
        }

        int modelRow = tblPatient.convertRowIndexToModel(viewRow);
        String patientId = (String) tblPatient.getModel().getValueAt(modelRow, 0);
        String patientName = (String) tblPatient.getModel().getValueAt(modelRow, 1);

        int response = ShowMessage.showConfirm(this,
                "Are you sure you want to permanently delete " + patientName + "?\nThis also removes them from the queue.",
                "Confirm Deletion");

        if (response == JOptionPane.YES_OPTION) {
            Patient patientToDelete = patientControl.findPatientById(patientId);
            if (patientToDelete != null && queueControl.isPatientInQueue(patientToDelete)) {
                queueControl.dequeuePatient(patientToDelete);
            }

            if (patientControl.deletePatient(patientId)) {
                ShowMessage.showSuccess(this, "Patient " + patientName + " has been deleted.");
                populateTable();
            } else {
                ShowMessage.showError(this, "Error: Could not delete the patient.");
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        String selectedReport = (String) cmbReport.getSelectedItem();
        new PatientReportUI(selectedReport, patientControl, queueControl).setVisible(true);
    }//GEN-LAST:event_btnReportActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        performSearch();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSortActionPerformed
        performSort();
    }//GEN-LAST:event_btnSortActionPerformed

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
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSort;
    private javax.swing.JComboBox<String> cmbReport;
    private javax.swing.JComboBox<String> cmbSearch;
    private javax.swing.JComboBox<String> cmbSort;
    private javax.swing.JLabel lblBy;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JPanel pnlSearchSort;
    private javax.swing.JScrollPane scrTblPatient;
    private javax.swing.JTable tblPatient;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
