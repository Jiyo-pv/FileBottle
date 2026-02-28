/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;
/**
 *
 * @author jiyop
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
public class LoginRegister extends javax.swing.JFrame {
boolean isLogin = true;
    /**
     * Creates new form LoginRegister
     */
    public LoginRegister() {
    initComponents();
     BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
Graphics2D g = img.createGraphics();

g.setColor(new Color(41, 121, 255));
g.fillRoundRect(0, 0, 32, 32, 8, 8);

g.setColor(Color.WHITE);
g.setFont(new Font("Segoe UI", Font.BOLD, 30));
g.drawString("F", 8, 24);

g.dispose();

setIconImage(img);
  getContentPane().setBackground(new Color(245,245,245));
setResizable(false);
setSize(400, 450);
setLocationRelativeTo(null);
    setupLoginMode();
setupPlaceholders();
    styleTextField(txtUsername);
    stylePasswordField(txtPassword);

    styleButton(btnSubmit);
    styleLinkButton(btnToggle);

    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
}
    private void styleTextField(javax.swing.JTextField field) {
    field.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200,200,200)));
    field.setBackground(Color.WHITE);
    field.setFont(new Font("Segoe UI", Font.PLAIN,16));
}
    private void styleLinkButton(javax.swing.JButton button) {
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setForeground(new Color(51, 102, 255));
    button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
}
    private void stylePasswordField(javax.swing.JPasswordField field) {
    field.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200,200,200)));
    field.setBackground(Color.WHITE);
    field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
}
    String serverIP;
private void setupPlaceholders() {

    // USERNAME
    txtUsername.setText("Username");
    txtUsername.setForeground(Color.GRAY);

    txtUsername.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (txtUsername.getText().equals("Username")) {
                txtUsername.setText("");
                txtUsername.setForeground(Color.BLACK);
            }
        }

        public void focusLost(java.awt.event.FocusEvent evt) {
            if (txtUsername.getText().isEmpty()) {
                txtUsername.setText("Username");
                txtUsername.setForeground(Color.GRAY);
            }
        }
    });

    // PASSWORD
    txtPassword.setEchoChar((char) 0); // show text initially
    txtPassword.setText("Password");
    txtPassword.setForeground(Color.GRAY);

    txtPassword.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (String.valueOf(txtPassword.getPassword()).equals("Password")) {
                txtPassword.setText("");
                txtPassword.setForeground(Color.BLACK);
                txtPassword.setEchoChar('•'); // hide password
            }
        }

        public void focusLost(java.awt.event.FocusEvent evt) {
            if (String.valueOf(txtPassword.getPassword()).isEmpty()) {
                txtPassword.setEchoChar((char) 0);
                txtPassword.setText("Password");
                txtPassword.setForeground(Color.GRAY);
            }
        }
    });
}
public LoginRegister(String ip) {
    BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
Graphics2D g = img.createGraphics();

g.setColor(new Color(41, 121, 255));
g.fillRoundRect(0, 0, 32, 32, 8, 8);

g.setColor(Color.WHITE);
g.setFont(new Font("Segoe UI", Font.BOLD, 30));
g.drawString("F", 8, 24);

g.dispose();

setIconImage(img);
    initComponents();              // 🔥 MUST BE FIRST
    this.serverIP = ip;            // store server IP
setResizable(false);
setSize(380, 400);
setLocationRelativeTo(null);
   
  getContentPane().setBackground(new Color(245,245,245));

    setupLoginMode();
setupPlaceholders();
    styleTextField(txtUsername);
    stylePasswordField(txtPassword);

    styleButton(btnSubmit);
    styleLinkButton(btnToggle);

    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
}
   private void setupLoginMode() {

    String displayIP = (serverIP == null) ? "" : " (" + serverIP + ")";

    lblTitle.setText("Login" + displayIP);
    btnSubmit.setText("Login");
    btnToggle.setText("Go to Register");

    txtUsername.setText("");
    txtPassword.setText("");
}
    private void styleButton(javax.swing.JButton button) {
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setBackground(new java.awt.Color(51, 102, 255));
    button.setForeground(java.awt.Color.WHITE);
    button.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        btnSubmit = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        btnToggle = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        txtPassword.setText("jPasswordField1");

        btnSubmit.setText("submit");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        lblTitle.setText("Title");

        btnToggle.setText("Login/register");
        btnToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(btnToggle))
                            .addComponent(txtUsername)
                            .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(lblTitle))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(74, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnToggle)
                .addGap(67, 67, 67)
                .addComponent(lblTitle)
                .addGap(18, 18, 18)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(btnSubmit)
                .addContainerGap(110, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleActionPerformed
 if (isLogin) {
        lblTitle.setText("Register ( "+serverIP+" )");
        btnSubmit.setText("Register");
        btnToggle.setText("Go to Login");
    } else {
        lblTitle.setText("Login ("+serverIP+")");
        btnSubmit.setText("Login");
        btnToggle.setText("Go to Register");
    }

    isLogin = !isLogin;        // TODO add your handling code here:
    }//GEN-LAST:event_btnToggleActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
 String username = txtUsername.getText();
    String password = txtPassword.getText();

    if (username.equals("Username") || 
    password.equals("Password") || 
    username.isEmpty() || 
    password.isEmpty()) {

    JOptionPane.showMessageDialog(this, "Fill all fields");
    return;
}

    if (isLogin) {
        try {

    java.sql.Connection con = DBConnection.getConnection(serverIP);

    String sql = "SELECT * FROM users WHERE username=? AND password=?";
    java.sql.PreparedStatement pst = con.prepareStatement(sql);

    pst.setString(1, username);
    pst.setString(2, Hasher.hashPassword(password));

    java.sql.ResultSet rs = pst.executeQuery();

    if (rs.next()) {

    int id = rs.getInt("user_id");

    
    logLoginActivity(id);

    new Dashboard(id, serverIP).setVisible(true);
    dispose();
} else {
        JOptionPane.showMessageDialog(this, "Invalid Credentials");
    }

    con.close();

} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
}
    } else {
        try {

    java.sql.Connection con = DBConnection.getConnection(serverIP);

    String sql = "INSERT INTO users VALUES (user_seq.NEXTVAL, ?, ?, SYSDATE)";
    java.sql.PreparedStatement pst = con.prepareStatement(sql);

    pst.setString(1, username);
    pst.setString(2, Hasher.hashPassword(password));

    pst.executeUpdate();

    JOptionPane.showMessageDialog(this, "Registered Successfully!");

    con.close();

} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
}
    }        // TODO add your handling code here:
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginRegister().setVisible(true);
            }
        });
    }
private void logLoginActivity(int userId) {

    try {

        Connection con = DBConnection.getConnection(serverIP);

        String sql = "INSERT INTO activity_log " +
                "(log_id, user_id, action, filename, action_time) " +
                "VALUES (activity_seq.NEXTVAL, ?, ?, ?, SYSDATE)";

        PreparedStatement pst = con.prepareStatement(sql);

        pst.setInt(1, userId);
        pst.setString(2, "Logged In");
        pst.setString(3, "-");

        pst.executeUpdate();
        con.close();

    } catch (Exception e) {
        System.out.println("Login log failed");
    }
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnToggle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
