/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package boundary;

import adt.ListInterface;
import java.util.Iterator;
import entity.Queue;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author hangg
 */
public class QueueDisplayUI extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(QueueDisplayUI.class.getName());

    /**
     * Creates new form QueueDisplayUI2
     */
    public QueueDisplayUI() {
        initComponents();
        initializeDisplay();
        startClock();
    }

    private void initializeDisplay() {
        lblQueueNo.setText("----");
        lblName.setText("Welcome");
        lblRoomNo.setText("Room --");
        // Call with null for each room to show they are empty on startup
        updateRoomStatusPanel(null, null, null, null);
        DefaultTableModel model = (DefaultTableModel) tblQueueList.getModel();
        model.setRowCount(0);
    }

    private void startClock() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

        Timer timer = new Timer(1000, (ActionEvent e) -> {
            LocalDateTime now = LocalDateTime.now();
            lblTime.setText(now.format(timeFormatter));
            lblDay.setText(now.format(dayFormatter));
            lblDate.setText(now.format(dateFormatter));
        });
        timer.start();
    }

    /**
     * Updates all components of the display with new data.
     *
     * @param calledPatient The patient just called.
     * @param roomNumber The room they were called to.
     * @param waitingList The list of patients still waiting.
     * @param roomStatuses An array where the index is the room number and the
     * value is the Queue object of the patient being served.
     * @param scheduleStatuses An array with the schedule status (e.g., "On
     * Leave").
     */
    public void updateAll(
            Queue calledPatient, int roomNumber, ListInterface<Queue> waitingList,
            Queue room1Patient,
            Queue room2Patient,
            Queue room3Patient,
            Queue room4Patient
    ) {
        updateMainDisplay(calledPatient, roomNumber);
        updateWaitingTable(waitingList);
        updateRoomStatusPanel(
                room1Patient,
                room2Patient,
                room3Patient,
                room4Patient
        );
    }

    public void updateMainDisplay(Queue calledPatient, int roomNumber) {
        if (calledPatient != null) {
            lblQueueNo.setText(String.format("%04d", calledPatient.getQueueId()));
            lblName.setText(calledPatient.getPatient().getName());
            lblRoomNo.setText("Room " + roomNumber);
        }
    }

    private void updateWaitingTable(ListInterface<Queue> waitingList) {
        DefaultTableModel model = (DefaultTableModel) tblQueueList.getModel();
        model.setRowCount(0);

        if (waitingList != null) {
            Iterator<Queue> iterator = waitingList.getIterator();
            while (iterator.hasNext()) {
                Queue queueItem = iterator.next();
                Object[] rowData = {
                    String.format("%04d", queueItem.getQueueId()),
                    queueItem.getPatient().getName()
                };
                model.addRow(rowData);
            }
        }
    }

    private void updateRoomStatusPanel(
            Queue room1Patient,
            Queue room2Patient,
            Queue room3Patient,
            Queue room4Patient
    ) {
        // Room 1
        if (room1Patient != null) {
            lblRoom1Status.setText("Serving: " + String.format("%04d", room1Patient.getQueueNumber()));
        } else {
            lblRoom1Status.setText("----");
        }

        // Room 2
        if (room2Patient != null) {
            lblRoom2Status.setText("Serving: " + String.format("%04d", room2Patient.getQueueNumber()));
        } else {
            lblRoom2Status.setText("----");
        }

        // Room 3
        if (room3Patient != null) {
            lblRoom3Status.setText("Serving: " + String.format("%04d", room3Patient.getQueueNumber()));
        } else {
            lblRoom3Status.setText("----");
        }

        // Room 4
        if (room4Patient != null) {
            lblRoom4Status.setText("Serving: " + String.format("%04d", room4Patient.getQueueNumber()));
        } else {
            lblRoom4Status.setText("----");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblClinicName = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        lblDay = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        pnlRoom1 = new javax.swing.JPanel();
        lblRoom1 = new javax.swing.JLabel();
        lblRoom1Status = new javax.swing.JLabel();
        pnlRoom2 = new javax.swing.JPanel();
        lblRoom2 = new javax.swing.JLabel();
        lblRoom2Status = new javax.swing.JLabel();
        pnlRoom3 = new javax.swing.JPanel();
        lblRoom3 = new javax.swing.JLabel();
        lblRoom3Status = new javax.swing.JLabel();
        pnlRoom4 = new javax.swing.JPanel();
        lblRoom4 = new javax.swing.JLabel();
        lblRoom4Status = new javax.swing.JLabel();
        splQueueDisplay = new javax.swing.JSplitPane();
        pnlCurrentQueue = new javax.swing.JPanel();
        lblQueueNo = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblRoomPrefix = new javax.swing.JLabel();
        lblRoomNo = new javax.swing.JLabel();
        lblRoomSuffix = new javax.swing.JLabel();
        scrQueueList = new javax.swing.JScrollPane();
        tblQueueList = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblClinicName.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        lblClinicName.setText("Community Care Clinic");
        lblClinicName.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lblDate.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        lblDate.setText("Date");

        lblDay.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        lblDay.setText("Day of Week");

        lblTime.setFont(new java.awt.Font("Monospaced", 1, 22)); // NOI18N
        lblTime.setText("Time");

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblClinicName, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                        .addComponent(lblDay, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblClinicName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addComponent(lblDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDay)
                            .addComponent(lblTime))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        lblRoom1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblRoom1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoom1.setText("Room 1");

        lblRoom1Status.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        lblRoom1Status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoom1Status.setText("Serving: ----");

        javax.swing.GroupLayout pnlRoom1Layout = new javax.swing.GroupLayout(pnlRoom1);
        pnlRoom1.setLayout(pnlRoom1Layout);
        pnlRoom1Layout.setHorizontalGroup(
            pnlRoom1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoom1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRoom1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRoom1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRoom1Status, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlRoom1Layout.setVerticalGroup(
            pnlRoom1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoom1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRoom1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRoom1Status, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblRoom2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblRoom2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoom2.setText("Room 2");

        lblRoom2Status.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        lblRoom2Status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoom2Status.setText("Serving: ----");

        javax.swing.GroupLayout pnlRoom2Layout = new javax.swing.GroupLayout(pnlRoom2);
        pnlRoom2.setLayout(pnlRoom2Layout);
        pnlRoom2Layout.setHorizontalGroup(
            pnlRoom2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoom2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRoom2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRoom2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRoom2Status, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlRoom2Layout.setVerticalGroup(
            pnlRoom2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoom2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRoom2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRoom2Status, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblRoom3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblRoom3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoom3.setText("Room 3");

        lblRoom3Status.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        lblRoom3Status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoom3Status.setText("Serving: ----");

        javax.swing.GroupLayout pnlRoom3Layout = new javax.swing.GroupLayout(pnlRoom3);
        pnlRoom3.setLayout(pnlRoom3Layout);
        pnlRoom3Layout.setHorizontalGroup(
            pnlRoom3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoom3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRoom3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRoom3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRoom3Status, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlRoom3Layout.setVerticalGroup(
            pnlRoom3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoom3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRoom3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRoom3Status, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblRoom4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblRoom4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoom4.setText("Room 4");

        lblRoom4Status.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        lblRoom4Status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoom4Status.setText("Serving: ----");

        javax.swing.GroupLayout pnlRoom4Layout = new javax.swing.GroupLayout(pnlRoom4);
        pnlRoom4.setLayout(pnlRoom4Layout);
        pnlRoom4Layout.setHorizontalGroup(
            pnlRoom4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoom4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRoom4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRoom4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRoom4Status, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlRoom4Layout.setVerticalGroup(
            pnlRoom4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRoom4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRoom4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRoom4Status, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        splQueueDisplay.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        splQueueDisplay.setDividerLocation(300);
        splQueueDisplay.setDividerSize(2);
        splQueueDisplay.setResizeWeight(0.4);

        pnlCurrentQueue.setPreferredSize(new java.awt.Dimension(600, 568));

        lblQueueNo.setFont(new java.awt.Font("Segoe UI", 1, 72)); // NOI18N
        lblQueueNo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQueueNo.setText("0001");

        lblName.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblName.setText("Patient Name");

        lblRoomPrefix.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblRoomPrefix.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblRoomPrefix.setText("Please go to");

        lblRoomNo.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblRoomNo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoomNo.setText("Room ?");

        lblRoomSuffix.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblRoomSuffix.setText("to have a consultation");

        javax.swing.GroupLayout pnlCurrentQueueLayout = new javax.swing.GroupLayout(pnlCurrentQueue);
        pnlCurrentQueue.setLayout(pnlCurrentQueueLayout);
        pnlCurrentQueueLayout.setHorizontalGroup(
            pnlCurrentQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCurrentQueueLayout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addGroup(pnlCurrentQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblQueueNo, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlCurrentQueueLayout.createSequentialGroup()
                        .addComponent(lblRoomPrefix, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRoomNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRoomSuffix, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(156, 156, 156))
        );
        pnlCurrentQueueLayout.setVerticalGroup(
            pnlCurrentQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCurrentQueueLayout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(lblQueueNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblName)
                .addGap(18, 18, 18)
                .addGroup(pnlCurrentQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblRoomNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRoomPrefix, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRoomSuffix, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(173, Short.MAX_VALUE))
        );

        splQueueDisplay.setRightComponent(pnlCurrentQueue);

        scrQueueList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Queuing...", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 24))); // NOI18N

        tblQueueList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Queue No", "Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblQueueList.getTableHeader().setReorderingAllowed(false);
        scrQueueList.setViewportView(tblQueueList);
        if (tblQueueList.getColumnModel().getColumnCount() > 0) {
            tblQueueList.getColumnModel().getColumn(0).setMaxWidth(100);
            tblQueueList.getColumnModel().getColumn(1).setMaxWidth(400);
        }

        splQueueDisplay.setLeftComponent(scrQueueList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splQueueDisplay, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(pnlRoom1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(pnlRoom2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(pnlRoom3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(pnlRoom4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlRoom1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlRoom3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlRoom2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlRoom4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(splQueueDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        java.awt.EventQueue.invokeLater(() -> new QueueDisplayUI().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblClinicName;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDay;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblQueueNo;
    private javax.swing.JLabel lblRoom1;
    private javax.swing.JLabel lblRoom1Status;
    private javax.swing.JLabel lblRoom2;
    private javax.swing.JLabel lblRoom2Status;
    private javax.swing.JLabel lblRoom3;
    private javax.swing.JLabel lblRoom3Status;
    private javax.swing.JLabel lblRoom4;
    private javax.swing.JLabel lblRoom4Status;
    private javax.swing.JLabel lblRoomNo;
    private javax.swing.JLabel lblRoomPrefix;
    private javax.swing.JLabel lblRoomSuffix;
    private javax.swing.JLabel lblTime;
    private javax.swing.JPanel pnlCurrentQueue;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlRoom1;
    private javax.swing.JPanel pnlRoom2;
    private javax.swing.JPanel pnlRoom3;
    private javax.swing.JPanel pnlRoom4;
    private javax.swing.JScrollPane scrQueueList;
    private javax.swing.JSplitPane splQueueDisplay;
    private javax.swing.JTable tblQueueList;
    // End of variables declaration//GEN-END:variables
}
