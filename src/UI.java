import java.io.IOException;
import javax.swing.text.DefaultCaret;

/*
 * UI.java
 *
 * Created on __DATE__, __TIME__
 */

/**
 *
 * @author  __PAROID__
 */
public class UI extends javax.swing.JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /** Creates new form UI */
    public UI() {
        initComponents();
        DefaultCaret caret = (DefaultCaret) jTextArea1.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    //GEN-BEGIN:initComponents
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jButton_start = new javax.swing.JButton();
        jButton_stop = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField_sip = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_port = new javax.swing.JTextField();
        jPasswordField_pw = new javax.swing.JPasswordField();
        jTextField_username = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel1.setFont(new java.awt.Font("Î¢ÈíÑÅºÚ", 1, 14));
        jLabel1.setForeground(new java.awt.Color(0, 51, 204));
        jLabel1.setText("\u6570\u636e\u5e93\u52a0\u5bc6\u7cfb\u7edf\u5ba2\u6237\u7aef");
        jButton_start.setText("\u542f\u52a8\u670d\u52a1");
        jButton_start.setActionCommand("jButton_start");
        jButton_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_startActionPerformed(evt);
            }
        });
        jButton_stop.setText("\u505c\u6b62\u670d\u52a1");
        jButton_stop.setActionCommand("jButton_stop");
        jButton_stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_stopActionPerformed(evt);
            }
        });
        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(new java.awt.Font("Î¢ÈíÑÅºÚ", 0, 14));
        jTextArea1.setForeground(new java.awt.Color(0, 153, 153));
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);
        jTextField_sip.setFont(new java.awt.Font("Consolas", 0, 14));
        jTextField_sip.setText("127.0.0.1");
        jLabel2.setFont(new java.awt.Font("Î¢ÈíÑÅºÚ", 0, 14));
        jLabel2.setText("\u6570\u636e\u5e93 IP\uff1a");
        jLabel3.setFont(new java.awt.Font("Î¢ÈíÑÅºÚ", 0, 14));
        jLabel3.setText("\u670d\u52a1\u7aef\u53e3\uff1a");
        jTextField_port.setFont(new java.awt.Font("Î¢ÈíÑÅºÚ", 0, 14));
        jTextField_port.setText("1990");
        jPasswordField_pw.setFont(new java.awt.Font("Î¢ÈíÑÅºÚ", 0, 14));
        jPasswordField_pw.setText("paroid");
        jTextField_username.setFont(new java.awt.Font("Î¢ÈíÑÅºÚ", 0, 14));
        jTextField_username.setText("root");
        jLabel4.setFont(new java.awt.Font("Î¢ÈíÑÅºÚ", 0, 14));
        jLabel4.setText("\u7528\u6237\u540d\uff1a");
        jLabel5.setFont(new java.awt.Font("Î¢ÈíÑÅºÚ", 0, 14));
        jLabel5.setText("\u5bc6\u7801\uff1a");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
            getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                                  .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                  .addGroup(
                                      layout.createSequentialGroup()
                                      .addGroup(
                                          layout.createParallelGroup(
                                              javax.swing.GroupLayout.Alignment.LEADING)
                                          .addGroup(
                                              layout.createSequentialGroup()
                                              .addContainerGap()
                                              .addComponent(
                                                      jScrollPane1,
                                                      javax.swing.GroupLayout.DEFAULT_SIZE,
                                                      474,
                                                      Short.MAX_VALUE))
                                          .addGroup(
                                              layout.createSequentialGroup()
                                              .addGap(19, 19,
                                                      19)
                                              .addGroup(
                                                      layout.createParallelGroup(
                                                              javax.swing.GroupLayout.Alignment.LEADING)
                                                      .addComponent(
                                                              jLabel2)
                                                      .addComponent(
                                                              jLabel4))
                                              .addPreferredGap(
                                                      javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                              .addGroup(
                                                      layout.createParallelGroup(
                                                              javax.swing.GroupLayout.Alignment.LEADING,
                                                              false)
                                                      .addGroup(
                                                              javax.swing.GroupLayout.Alignment.TRAILING,
                                                              layout.createSequentialGroup()
                                                              .addComponent(
                                                                      jTextField_sip,
                                                                      javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                      128,
                                                                      javax.swing.GroupLayout.PREFERRED_SIZE)
                                                              .addPreferredGap(
                                                                      javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                              .addComponent(
                                                                      jLabel3)
                                                              .addGap(18,
                                                                      18,
                                                                      18)
                                                              .addComponent(
                                                                      jTextField_port,
                                                                      javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                      63,
                                                                      javax.swing.GroupLayout.PREFERRED_SIZE))
                                                      .addGroup(
                                                              javax.swing.GroupLayout.Alignment.TRAILING,
                                                              layout.createSequentialGroup()
                                                              .addComponent(
                                                                      jTextField_username)
                                                              .addPreferredGap(
                                                                      javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                              .addComponent(
                                                                      jLabel5)
                                                              .addPreferredGap(
                                                                      javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                              .addComponent(
                                                                      jPasswordField_pw,
                                                                      javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                      100,
                                                                      javax.swing.GroupLayout.PREFERRED_SIZE)))
                                              .addPreferredGap(
                                                      javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                      17,
                                                      Short.MAX_VALUE)
                                              .addGroup(
                                                      layout.createParallelGroup(
                                                              javax.swing.GroupLayout.Alignment.TRAILING)
                                                      .addComponent(
                                                              jButton_stop)
                                                      .addComponent(
                                                              jButton_start)))
                                          .addGroup(
                                              layout.createSequentialGroup()
                                              .addGap(169,
                                                      169,
                                                      169)
                                              .addComponent(
                                                      jLabel1,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE,
                                                      156,
                                                      javax.swing.GroupLayout.PREFERRED_SIZE)))
                                      .addContainerGap()));
        layout.setVerticalGroup(layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(
                                        layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(
                                            jLabel2,
                                            javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(
                                            jTextField_sip,
                                            javax.swing.GroupLayout.Alignment.TRAILING,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(
                                            jLabel3,
                                            javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(
                                            jTextField_port,
                                            javax.swing.GroupLayout.Alignment.TRAILING,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            22,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(
                                            jButton_start,
                                            javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addGap(9, 9, 9)
                                    .addGroup(
                                        layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(
                                            jTextField_username,
                                            javax.swing.GroupLayout.Alignment.TRAILING,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(
                                            jLabel4,
                                            javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(
                                            jLabel5,
                                            javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(
                                            jPasswordField_pw,
                                            javax.swing.GroupLayout.Alignment.TRAILING,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(
                                            jButton_stop,
                                            javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane1,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            246, Short.MAX_VALUE)
                                    .addGap(25, 25, 25)));
        layout.linkSize(javax.swing.SwingConstants.VERTICAL,
                        new java.awt.Component[] { jButton_stop, jLabel1, jLabel4,
                                jLabel5, jPasswordField_pw, jTextField_username
                                                 });
        layout.linkSize(javax.swing.SwingConstants.VERTICAL,
                        new java.awt.Component[] { jButton_start, jLabel2, jLabel3,
                                jTextField_port, jTextField_sip
                                                 });
        pack();
    }// </editor-fold>
    //GEN-END:initComponents

    private void jButton_stopActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        clientThread.stop = true;
        jTextArea1.append("Service Stopped!\n");
        try {
            //clientThread.net.client.close();
            clientThread.net.ss.close();
            clientThread.net.ss = null;
            clientThread.net.client.close();
            clientThread.net.client = null;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void jButton_startActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        clientThread = new Client_UI();
        clientThread.uiFrame = this;
        clientThread.stop = false;
        clientThread.start();
        jTextArea1.append("Service Started!\n");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UI().setVisible(true);
            }
        });
    }

    private Client_UI clientThread;

    //GEN-BEGIN:variables
    // Variables declaration - do not modify
    private javax.swing.JButton jButton_start;
    private javax.swing.JButton jButton_stop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    protected javax.swing.JPasswordField jPasswordField_pw;
    private javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JTextArea jTextArea1;
    protected javax.swing.JTextField jTextField_port;
    protected javax.swing.JTextField jTextField_sip;
    protected javax.swing.JTextField jTextField_username;
    // End of variables declaration//GEN-END:variables

}
