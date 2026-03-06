/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.BorderLayout;
import java.sql.*;
/**
 *
 * @author jiyop
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LoginRegister extends javax.swing.JFrame {
    // ===== MODERN PALETTE =====
    public static class ModernPalette {
        public static final Color SIDEBAR_BG = new Color(28, 31, 38);
        public static final Color SIDEBAR_ACTIVE = new Color(41, 121, 255);
        public static final Color CONTENT_BG = new Color(245, 247, 251);
        public static final Color ACCENT_BLUE = new Color(41, 121, 255);
        public static final Color ACCENT_EMERALD = new Color(16, 185, 129);
        public static final Color TEXT_DARK = new Color(31, 41, 55);
        public static final Color TEXT_LIGHT = new Color(156, 163, 175);
        
        public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
        public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
        public static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    }

    // ===== ROUNDED PANEL =====
    public static class RoundedPanel extends javax.swing.JPanel {
        private int radius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    boolean isLogin = true;
    String serverIP;
    public LoginRegister() {
        this(null);
    }

    public LoginRegister(String ip) {
        this.serverIP = ip;
        initComponents();
        setupModernUI();
        
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(41, 121, 255));
        g.fillRoundRect(0, 0, 32, 32, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 30));
        g.drawString("F", 8, 24);
        g.dispose();
        setIconImage(img);
    }

    private void setupModernUI() {
        setTitle("FileBottle - Sign In");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Background container
        JPanel bgPanel = new JPanel(new GridBagLayout());
        bgPanel.setBackground(ModernPalette.CONTENT_BG);
        setContentPane(bgPanel);

        // Main Card
        RoundedPanel card = new RoundedPanel(25, Color.WHITE);
        card.setPreferredSize(new Dimension(450, 550));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        // Header
        JPanel header = new JPanel(new java.awt.GridLayout(2, 1, 0, 5));
        header.setOpaque(false);
        lblTitle.setFont(ModernPalette.TITLE_FONT);
        lblTitle.setForeground(ModernPalette.TEXT_DARK);
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        
        String displayIP = (serverIP == null) ? "FileBottle" : "Server: " + serverIP;
        header.add(lblTitle);
        
        JLabel lblSub = new JLabel(displayIP);
        lblSub.setFont(ModernPalette.SUBTITLE_FONT);
        lblSub.setForeground(ModernPalette.TEXT_LIGHT);
        lblSub.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        header.add(lblSub);
        
        card.add(header, BorderLayout.NORTH);

        // Body (Form)
        JPanel form = new JPanel(new java.awt.GridLayout(4, 1, 0, 20));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(40, 0, 20, 0));

        styleTextField(txtUsername);
        stylePasswordField(txtPassword);
        styleButton(btnSubmit);
        styleLinkButton(btnToggle);

        form.add(txtUsername);
        form.add(txtPassword);
        form.add(btnSubmit);
        form.add(btnToggle);
        
        card.add(form, BorderLayout.CENTER);
        bgPanel.add(card);

        setupLoginMode();
        setupPlaceholders();
    }

    private void styleTextField(javax.swing.JTextField field) {
        field.setBackground(new Color(243, 244, 246));
        field.setFont(ModernPalette.UI_FONT);
        field.setForeground(ModernPalette.TEXT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void stylePasswordField(javax.swing.JPasswordField field) {
        field.setBackground(new Color(243, 244, 246));
        field.setFont(ModernPalette.UI_FONT);
        field.setForeground(ModernPalette.TEXT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void styleButton(javax.swing.JButton button) {
        button.setBackground(ModernPalette.ACCENT_BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(37, 99, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ModernPalette.ACCENT_BLUE);
            }
        });
    }

    private void styleLinkButton(javax.swing.JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(ModernPalette.ACCENT_BLUE);
        button.setFont(ModernPalette.SUBTITLE_FONT);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
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
    private void setupLoginMode() {
        lblTitle.setText(isLogin ? "Sign In" : "Create Account");
        btnSubmit.setText(isLogin ? "Log In" : "Register");
        btnToggle.setText(isLogin ? "Don't have an account? Register" : "Already have an account? Sign In");

        txtUsername.setText("Username");
        txtUsername.setForeground(Color.GRAY);
        txtPassword.setText("Password");
        txtPassword.setEchoChar((char)0);
        txtPassword.setForeground(Color.GRAY);
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
        isLogin = !isLogin;
        setupLoginMode();
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
        notifyServerLogin();
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
private void notifyServerLogin() {
    try {
        Socket socket = new Socket(serverIP, 5000);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());

        out.writeUTF("LOGIN_NOTIFY");
        in.readUTF(); // wait for OK

        socket.close();
    } catch (Exception e) {
        e.printStackTrace();
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
