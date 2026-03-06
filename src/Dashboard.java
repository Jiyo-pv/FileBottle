import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;
public class Dashboard extends JFrame {
    private static boolean serverStarted = false;
    private String serverIP;
    private int userId;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private JButton btnFiles;
    private JButton btnShared;
    private JButton btnTrash;
    private JButton btnActivity;
    private JButton btnEditPass;
private JTable fileTable;
private DefaultTableModel tableModel;
private JTable trashTable;
private DefaultTableModel trashModel;
private DefaultTableModel sharedModel;
private DefaultTableModel activityModel;
private String username;
    public Dashboard(int userId,String serverIP) {
       
BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
Graphics2D g = img.createGraphics();

g.setColor(new Color(41, 121, 255));
g.fillRoundRect(0, 0, 32, 32, 8, 8);

g.setColor(Color.WHITE);
g.setFont(new Font("Segoe UI", Font.BOLD, 30));
g.drawString("F", 8, 24);

g.dispose();

setIconImage(img);
        this.userId = userId;
         this.serverIP = serverIP;
          this.username = getUsernameById(userId);
        setTitle("FileBottle");
        setSize(1920,1080);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        // ===== SIDEBAR =====
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(ModernPalette.SIDEBAR_BG);
        leftPanel.setPreferredSize(new Dimension(250, 650));
        leftPanel.setLayout(new BorderLayout());

        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        // App Name
        JLabel logo = new JLabel("FileBottle");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        JLabel nameLabel = new JLabel("Welcome, " + username);
        nameLabel.setForeground(ModernPalette.TEXT_LIGHT);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(5));
        logoPanel.add(nameLabel);

        leftPanel.add(logoPanel, BorderLayout.NORTH);

        // Menu Section
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        btnFiles = createNavButton("Your Files");
        btnShared = createNavButton("Shared Items");
        btnTrash = createNavButton("Trash Bin");
        btnActivity = createNavButton("Recent Activity");
        btnEditPass = createNavButton("Security Settings");
        JButton btnLogout = createNavButton("Sign Out");

        menuPanel.add(btnFiles);
        menuPanel.add(btnShared);
        menuPanel.add(btnTrash);
        menuPanel.add(btnActivity);
        menuPanel.add(btnEditPass);
        menuPanel.add(Box.createVerticalStrut(50));
        menuPanel.add(btnLogout);

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to sign out?",
                    "Sign Out",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                logActivity("Logged Out", "-");
                notifyServerLogout();
                new LoginRegister(serverIP).setVisible(true);
                dispose();
            }
        });

        leftPanel.add(menuPanel, BorderLayout.CENTER);

        // ===== CONTENT AREA =====
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);contentPanel.setBackground(new Color(248, 249, 252));

     contentPanel.add(createYourFilesPanel(), "files");
     contentPanel.add(createSharedPanel(), "shared");
     contentPanel.add(createTrashPanel(), "trash");
     
     contentPanel.add(createActivityPanel(), "activity");
     contentPanel.add(createEditPasswordPanel(), "editpass");

        // ===== BUTTON ACTIONS =====
        btnFiles.addActionListener(e -> {
            loadUserFiles();
            cardLayout.show(contentPanel, "files");
            setActiveButton(btnFiles);
        });

        btnShared.addActionListener(e -> {
            loadSharedFiles();
            cardLayout.show(contentPanel, "shared");
            setActiveButton(btnShared);
        });

        btnTrash.addActionListener(e -> {
            loadTrashFiles();
            cardLayout.show(contentPanel, "trash");
            setActiveButton(btnTrash);
        });

        btnActivity.addActionListener(e -> {
            loadActivityData();
            cardLayout.show(contentPanel, "activity");
            setActiveButton(btnActivity);
        });

        btnEditPass.addActionListener(e -> {
            cardLayout.show(contentPanel, "editpass");
            setActiveButton(btnEditPass);
        });

        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Default selection
        loadUserFiles();
        setActiveButton(btnFiles);
    }

    // ===== CREATE NAV BUTTON =====
    private JButton createNavButton(String text) {
        return new JButton(text) {
            private float alpha = 0f;
            private Timer timer;

            {
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setHorizontalAlignment(SwingConstants.LEFT);
                setForeground(ModernPalette.TEXT_LIGHT);
                setFont(ModernPalette.NAV_FONT);
                setPreferredSize(new Dimension(220, 50));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        startAnimation(true);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        if (getBackground() != ModernPalette.SIDEBAR_ACTIVE) {
                            startAnimation(false);
                        }
                    }
                });
            }

            private void startAnimation(boolean forward) {
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new Timer(20, e -> {
                    if (forward) {
                        alpha += 0.1f;
                        if (alpha >= 1f) { alpha = 1f; timer.stop(); }
                    } else {
                        alpha -= 0.1f;
                        if (alpha <= 0f) { alpha = 0f; timer.stop(); }
                    }
                    repaint();
                });
                timer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getBackground() == ModernPalette.SIDEBAR_ACTIVE) {
                    g2.setColor(ModernPalette.SIDEBAR_ACTIVE);
                    g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10);
                    setForeground(Color.WHITE);
                } else if (alpha > 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2.setColor(ModernPalette.SIDEBAR_HOVER);
                    g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10);
                    setForeground(Color.WHITE);
                } else {
                    setForeground(ModernPalette.TEXT_LIGHT);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
private JPanel createYourFilesPanel() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(ModernPalette.CONTENT_BG);

    JPanel header = createModernHeader("Your Files", "Manage and access your uploaded documents");
    mainPanel.add(header, BorderLayout.NORTH);

    JPanel content = new JPanel(new BorderLayout());
    content.setOpaque(false);
    content.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

    // Toolbar
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    toolbar.setOpaque(false);

    JButton btnUpload = new JButton("Upload New");
    styleActionButton(btnUpload);
    JButton btnView = new JButton("View");
    styleActionButton(btnView);
    JButton btnDownload = new JButton("Download");
    styleActionButton(btnDownload);
    JButton btnRename = new JButton("Rename");
    styleActionButton(btnRename);
    JButton btnShare = new JButton("Share");
    styleActionButton(btnShare);
    JButton btnManageShare = new JButton("Manage Shares");
    styleActionButton(btnManageShare);
    JButton btnDelete = new JButton("Move to Trash");
    styleActionButton(btnDelete);
    JButton btnRefresh = new JButton("Refresh");
    styleActionButton(btnRefresh);

    toolbar.add(btnUpload);
    toolbar.add(btnView);
    toolbar.add(btnDownload);
    toolbar.add(btnRename);
    toolbar.add(btnShare);
    toolbar.add(btnManageShare);
    toolbar.add(btnDelete);
    toolbar.add(btnRefresh);

    content.add(toolbar, BorderLayout.NORTH);

    // Table
    String[] columns = {"File ID", "Filename", "Type", "Size (KB)", "Created On"};
    tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    fileTable = new JTable(tableModel);
    modernizeTable(fileTable);
    
    JPanel tableWrapper = createRoundedTablePanel(fileTable);
    content.add(tableWrapper, BorderLayout.CENTER);

    mainPanel.add(content, BorderLayout.CENTER);

    // Actions
    btnUpload.addActionListener(e -> uploadFile());
    btnView.addActionListener(e -> viewFile());
    btnDownload.addActionListener(e -> downloadFile());
    btnRename.addActionListener(e -> renameFile());
    btnShare.addActionListener(e -> shareFile());
    btnManageShare.addActionListener(e -> manageShares());
    btnDelete.addActionListener(e -> moveToTrash());
    btnRefresh.addActionListener(e -> loadUserFiles());

    loadUserFiles();
    return mainPanel;
}
private void styleActionButton(JButton btn) {
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setContentAreaFilled(false);
    btn.setOpaque(true);
    btn.setBackground(ModernPalette.ACCENT_COLOR);
    btn.setForeground(Color.WHITE);
    btn.setFont(ModernPalette.BODY_FONT);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            btn.setBackground(ModernPalette.ACCENT_COLOR.brighter());
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            btn.setBackground(ModernPalette.ACCENT_COLOR);
        }
    });
}
private void viewFile() {

    int selectedRow = fileTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a file first");
        return;
    }

    String filename = (String) tableModel.getValueAt(selectedRow, 1);

    try {

        // 🔹 Download file from server to temp file
        Socket socket = new Socket(serverIP, 5000);

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());
        DataInputStream in =
                new DataInputStream(socket.getInputStream());

        out.writeUTF("DOWNLOAD");
        out.writeUTF("user_" + userId);
        out.writeUTF(filename);

        long fileSize = in.readLong();

        if (fileSize == -1) {
            JOptionPane.showMessageDialog(this, "File not found on server");
            socket.close();
            return;
        }

        File tempFile = File.createTempFile("filebottle_", "_" + filename);
        tempFile.deleteOnExit();

        FileOutputStream fos = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        long remaining = fileSize;

        while (remaining > 0 &&
               (bytesRead = in.read(buffer, 0,
                 (int)Math.min(buffer.length, remaining))) > 0) {

            fos.write(buffer, 0, bytesRead);
            remaining -= bytesRead;
        }

        fos.close();
        socket.close();
String filetype = (String) tableModel.getValueAt(selectedRow, 2);
     openInternalViewer(tempFile,
                   filename,
                   filetype,
                   true,       // owner can edit
                   userId);    // ownerId

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "View Error: " + ex.getMessage());
    }
}
private void downloadFile() {

    int selectedRow = fileTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a file first");
        return;
    }

    String filename = (String) tableModel.getValueAt(selectedRow, 1);

    try {

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(filename));

        int result = chooser.showSaveDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) return;

        File destFile = chooser.getSelectedFile();

        Socket socket = new Socket(serverIP, 5000);

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());
        DataInputStream in =
                new DataInputStream(socket.getInputStream());

        out.writeUTF("DOWNLOAD");
        out.writeUTF("user_" + userId);
        out.writeUTF(filename);

        long fileSize = in.readLong();

        if (fileSize == -1) {
            JOptionPane.showMessageDialog(this, "File not found on server");
            socket.close();
            return;
        }

        FileOutputStream fos = new FileOutputStream(destFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        long remaining = fileSize;

        while (remaining > 0 &&
               (bytesRead = in.read(buffer, 0,
                 (int)Math.min(buffer.length, remaining))) > 0) {

            fos.write(buffer, 0, bytesRead);
            remaining -= bytesRead;
        }

        fos.close();
        socket.close();

        logActivity("Downloaded File", filename);
        JOptionPane.showMessageDialog(this, "Download complete!");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Download Error: " + ex.getMessage());
    }
}
private void openInternalViewer(File tempFile,
                                String filename,
                                String filetype,
                                boolean canEdit,
                                int ownerId) {

    JFrame viewer = new JFrame("File Viewer - " + filename);
    viewer.setSize(1000, 750);
    viewer.setLocationRelativeTo(this);
    viewer.setLayout(new BorderLayout());
    viewer.getContentPane().setBackground(ModernPalette.CONTENT_BG);

    JPanel header = createModernHeader(filename, filetype.toUpperCase() + " File");
    viewer.add(header, BorderLayout.NORTH);

    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.setOpaque(false);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

    try {
        // ================= IMAGE =================
        if (filetype.equalsIgnoreCase("jpg") ||
            filetype.equalsIgnoreCase("png") ||
            filetype.equalsIgnoreCase("jpeg") ||
            filetype.equalsIgnoreCase("gif") ||
            filetype.equalsIgnoreCase("bmp")) {

            ImageIcon icon = new ImageIcon(tempFile.getAbsolutePath());
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            RoundedPanel wrapper = new RoundedPanel(15, Color.WHITE);
            wrapper.setLayout(new BorderLayout());
            wrapper.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
            
            contentPanel.add(wrapper, BorderLayout.CENTER);
            viewer.add(contentPanel, BorderLayout.CENTER);
            viewer.setVisible(true);
            return;
        }

        // ================= PDF =================
        if (filetype.equalsIgnoreCase("pdf")) {
            Desktop.getDesktop().open(tempFile);
            viewer.dispose();
            return;
        }

        // ================= TEXT =================
        byte[] fileBytes = java.nio.file.Files.readAllBytes(tempFile.toPath());
        String originalContent = new String(fileBytes, java.nio.charset.StandardCharsets.UTF_8);

        if (originalContent.contains("\0")) {
            JLabel msg = new JLabel("Preview not supported for binary files.");
            msg.setFont(ModernPalette.SUBTITLE_FONT);
            msg.setHorizontalAlignment(SwingConstants.CENTER);
            contentPanel.add(msg, BorderLayout.CENTER);
            viewer.add(contentPanel, BorderLayout.CENTER);
            viewer.setVisible(true);
            return;
        }

        JTextArea textArea = new JTextArea(originalContent);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);

        RoundedPanel textWrapper = new RoundedPanel(15, Color.WHITE);
        textWrapper.setLayout(new BorderLayout());
        textWrapper.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(textWrapper, BorderLayout.CENTER);

        JButton btnSave = new JButton("Save Changes");
        styleActionButton(btnSave);
        btnSave.setEnabled(false);

        if (!canEdit) {
            btnSave.setVisible(false);
        }

        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void checkChange() {
                if (canEdit) {
                    btnSave.setEnabled(!textArea.getText().equals(originalContent));
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkChange(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkChange(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkChange(); }
        });

        btnSave.addActionListener(e -> {
            try {
                java.nio.file.Files.write(tempFile.toPath(), 
                    textArea.getText().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                sendFileToServer(tempFile, filename, ownerId);
                logActivity("Edited File", filename);
                String editorName = getUsernameById(userId);
                logOwnerActivity(ownerId, editorName + " edited", filename);
                JOptionPane.showMessageDialog(viewer, "Saved to server successfully!");
                viewer.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(viewer, "Save Error: " + ex.getMessage());
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnSave);

        viewer.add(contentPanel, BorderLayout.CENTER);
        viewer.add(bottomPanel, BorderLayout.SOUTH);
        viewer.setVisible(true);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Viewer Error: " + ex.getMessage());
    }
}
private void loadUserFiles() {

    tableModel.setRowCount(0);

    try {
        Connection con = DBConnection.getConnection(serverIP);

        String sql = "SELECT file_id, filename, filetype, filesize, created_on " +
                     "FROM files WHERE owner_id=? AND status='ACTIVE'";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, userId);

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {

            tableModel.addRow(new Object[]{
                rs.getInt("file_id"),
                rs.getString("filename"),
                rs.getString("filetype"),
                rs.getDouble("filesize"),
                rs.getDate("created_on")
            });
        }

        con.close();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading files: " + ex.getMessage());
    }
}
private void uploadFile() {

    JFileChooser chooser = new JFileChooser();
    int result = chooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {

        File selectedFile = chooser.getSelectedFile();

        try {

            Socket socket = new Socket(serverIP, 5000);

            DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream());
            DataInputStream in =
                    new DataInputStream(socket.getInputStream());

            // SEND COMMAND
            out.writeUTF("UPLOAD");
            out.writeUTF("user_" + userId);
            out.writeUTF(selectedFile.getName());
            out.writeLong(selectedFile.length());

            // SEND FILE BYTES
            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            fis.close();

            String response = in.readUTF();
            socket.close();

            if (!response.equals("SUCCESS")) {
                JOptionPane.showMessageDialog(this, "Upload failed!");
                return;
            }

            // INSERT INTO DB
            Connection con = DBConnection.getConnection(serverIP);

            String serverPath = "FileBottleServer/user_"
                    + userId + "/" + selectedFile.getName();

            String sql = "INSERT INTO files " +
                    "(file_id, filename, filepath, filetype, filesize, created_on, updated_on, owner_id, status) " +
                    "VALUES (file_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE, SYSDATE, ?, 'ACTIVE')";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, selectedFile.getName());
            pst.setString(2, serverPath);
            pst.setString(3, getFileExtension(selectedFile.getName()));
            pst.setDouble(4, selectedFile.length() / 1024.0);
            pst.setInt(5, userId);

            pst.executeUpdate();
            con.close();

            loadUserFiles();
            logActivity("Uploaded File", selectedFile.getName());

            JOptionPane.showMessageDialog(this,
                    "File uploaded via server successfully!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Upload Error: " + ex.getMessage());
        }
    }
}
private String getFileExtension(String name) {
    int index = name.lastIndexOf('.');
    if (index > 0) {
        return name.substring(index + 1);
    }
    return "unknown";
}
private JPanel createTrashPanel() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(ModernPalette.CONTENT_BG);

    JPanel header = createModernHeader("Trash Bin", "Recover or permanently delete your files");
    mainPanel.add(header, BorderLayout.NORTH);

    JPanel content = new JPanel(new BorderLayout());
    content.setOpaque(false);
    content.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

    // Toolbar
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    toolbar.setOpaque(false);

    JButton btnRestore = new JButton("Restore File");
    styleActionButton(btnRestore);
    JButton btnDeletePermanent = new JButton("Empty Trash");
    styleActionButton(btnDeletePermanent);
    JButton btnRefresh = new JButton("Refresh");
    styleActionButton(btnRefresh);

    toolbar.add(btnRestore);
    toolbar.add(btnDeletePermanent);
    toolbar.add(btnRefresh);
    content.add(toolbar, BorderLayout.NORTH);

    // Table
    String[] columns = {"File ID", "Filename", "Type", "Size (KB)", "Deleted On"};
    trashModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    trashTable = new JTable(trashModel);
    modernizeTable(trashTable);
    
    JPanel tableWrapper = createRoundedTablePanel(trashTable);
    content.add(tableWrapper, BorderLayout.CENTER);
    mainPanel.add(content, BorderLayout.CENTER);

    btnRestore.addActionListener(e -> restoreFile());
    btnDeletePermanent.addActionListener(e -> deleteFilePermanent());
    btnRefresh.addActionListener(e -> loadTrashFiles());

    loadTrashFiles();
    return mainPanel;
}
private void loadTrashFiles() {

    trashModel.setRowCount(0);

    try {
        Connection con = DBConnection.getConnection(serverIP);

        String sql = "SELECT file_id, filename, filetype, filesize, updated_on " +
                     "FROM files WHERE owner_id=? AND status='TRASH'";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, userId);

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            trashModel.addRow(new Object[]{
                rs.getInt("file_id"),
                rs.getString("filename"),
                rs.getString("filetype"),
                rs.getDouble("filesize"),
                rs.getDate("updated_on")
            });
        }

        con.close();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading trash: " + ex.getMessage());
    }
}
private void restoreFile() {

    int selectedRow = trashTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a file first");
        return;
    }

    int fileId = (int) trashModel.getValueAt(selectedRow, 0);

    try {
        Connection con = DBConnection.getConnection(serverIP);

        String sql = "UPDATE files SET status='ACTIVE' WHERE file_id=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, fileId);
        pst.executeUpdate();

        con.close();
        logActivity("Restored File", 
    (String) trashModel.getValueAt(selectedRow, 1));
        loadTrashFiles();
        loadUserFiles();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Restore Error: " + ex.getMessage());
    }
}
private void deleteFilePermanent() {

    int selectedRow = trashTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a file first");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to permanently delete?",
            "Confirm",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) return;

    int fileId = (int) trashModel.getValueAt(selectedRow, 0);
    String filename = (String) trashModel.getValueAt(selectedRow, 1);

    try {

        // ================================
        // STEP 1: SEND DELETE TO SERVER
        // ================================

        Socket socket = new Socket(serverIP, 5000);

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());
        DataInputStream in =
                new DataInputStream(socket.getInputStream());

        out.writeUTF("DELETE");
        out.writeUTF("user_" + userId);
        out.writeUTF(filename);

        String response = in.readUTF();
        socket.close();

        if (!response.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this,
                    "Server delete failed.");
            return;
        }

       // ================================
// STEP 2: DELETE SHARE RECORDS FIRST
// ================================

Connection con = DBConnection.getConnection(serverIP);

// 🔥 Remove from file_shares first
String deleteShares =
        "DELETE FROM file_shares WHERE file_id=?";

PreparedStatement pstShare =
        con.prepareStatement(deleteShares);

pstShare.setInt(1, fileId);
pstShare.executeUpdate();

// 🔥 Now delete actual file record
String deleteFile =
        "DELETE FROM files WHERE file_id=?";

PreparedStatement pstFile =
        con.prepareStatement(deleteFile);

pstFile.setInt(1, fileId);
pstFile.executeUpdate();

con.close();

        // ================================
        // STEP 3: LOG + REFRESH
        // ================================

        logActivity("Deleted Permanently", filename);

        loadTrashFiles();
        loadUserFiles();

        JOptionPane.showMessageDialog(this,
                "File permanently deleted.");

    } catch (Exception ex) {

        JOptionPane.showMessageDialog(this,
                "Delete Error: " + ex.getMessage());
    }
}
private void moveToTrash() {

    int selectedRow = fileTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a file first");
        return;
    }

    int fileId = (int) tableModel.getValueAt(selectedRow, 0);

    try {
        Connection con = DBConnection.getConnection(serverIP);

        String sql = "UPDATE files SET status='TRASH' WHERE file_id=?";
        PreparedStatement pst = con.prepareStatement(sql);

        pst.setInt(1, fileId);
        pst.executeUpdate();

        con.close();
        logActivity("Moved to Trash", 
    (String) tableModel.getValueAt(selectedRow, 1));
        loadUserFiles();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
    }
}
    // ===== ACTIVE BUTTON HIGHLIGHT =====
    private void setActiveButton(JButton active) {
        JButton[] buttons = {btnFiles, btnShared, btnTrash, btnActivity, btnEditPass};
        for (JButton btn : buttons) {
            if (btn == active) {
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                btn.setBackground(ModernPalette.SIDEBAR_ACTIVE);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setForeground(ModernPalette.TEXT_LIGHT);
                btn.setBackground(ModernPalette.SIDEBAR_BG);
            }
        }
    }

    private void logActivity(String action, String filename) {
        try {
            Connection con = DBConnection.getConnection(serverIP);
            String sql = "INSERT INTO activity_log " +
                         "(log_id, user_id, action, filename, action_time) " +
                         "VALUES (activity_seq.NEXTVAL, ?, ?, ?, SYSDATE)";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setString(2, action);
            pst.setString(3, filename);

            pst.executeUpdate();
            con.close();
            
            // 🔥 Refresh the Activity panel if it has been initialized
            loadActivityData();
        } catch (Exception e) {
            System.out.println("Logging failed: " + e.getMessage());
        }
    }
private void loadActivityData() {
    if (activityModel == null) return;
    activityModel.setRowCount(0);
    try {
        Connection con = DBConnection.getConnection(serverIP);
        String sql = "SELECT action, filename, action_time FROM activity_log WHERE user_id=? ORDER BY action_time DESC";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            activityModel.addRow(new Object[]{
                rs.getString("action"),
                rs.getString("filename"),
                rs.getTimestamp("action_time")
            });
        }
        con.close();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Activity Load Error: " + e.getMessage());
    }
}

private JPanel createActivityPanel() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(ModernPalette.CONTENT_BG);

    JPanel header = createModernHeader("Recent Activity", "Monitor all file operations and account changes");
    mainPanel.add(header, BorderLayout.NORTH);

    JPanel content = new JPanel(new BorderLayout());
    content.setOpaque(false);
    content.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

    // Toolbar
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    toolbar.setOpaque(false);
    JButton btnRefresh = new JButton("Refresh");
    styleActionButton(btnRefresh);
    toolbar.add(btnRefresh);
    content.add(toolbar, BorderLayout.NORTH);

    String[] columns = {"Action", "Filename", "Time"};
    activityModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    JTable table = new JTable(activityModel);
    modernizeTable(table);
    
    JPanel tableWrapper = createRoundedTablePanel(table);
    content.add(tableWrapper, BorderLayout.CENTER);
    mainPanel.add(content, BorderLayout.CENTER);

    btnRefresh.addActionListener(e -> loadActivityData());

    loadActivityData();
    return mainPanel;
}
private void sendFileToServer(File file,
                              String filename,
                              int ownerId) throws Exception {

    Socket socket = new Socket(serverIP, 5000);

    DataOutputStream out =
            new DataOutputStream(socket.getOutputStream());
    DataInputStream in =
            new DataInputStream(socket.getInputStream());

    out.writeUTF("UPLOAD");
    out.writeUTF("user_" + ownerId);   // 🔥 IMPORTANT
    out.writeUTF(filename);
    out.writeLong(file.length());

    FileInputStream fis = new FileInputStream(file);

    byte[] buffer = new byte[4096];
    int bytesRead;

    while ((bytesRead = fis.read(buffer)) > 0) {
        out.write(buffer, 0, bytesRead);
    }

    fis.close();

    String response = in.readUTF();
    socket.close();

    if (!response.equals("SUCCESS")) {
        throw new Exception("Server save failed");
    }

    // Update DB timestamp
    Connection con = DBConnection.getConnection(serverIP);

    String sql = "UPDATE files SET updated_on = SYSDATE " +
                 "WHERE filename=? AND owner_id=?";

    PreparedStatement pst = con.prepareStatement(sql);
    pst.setString(1, filename);
    pst.setInt(2, ownerId);
    pst.executeUpdate();

    con.close();
}
private void renameFile() {

    int selectedRow = fileTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a file first");
        return;
    }

    String oldFilename =
            (String) tableModel.getValueAt(selectedRow, 1);

    String filetype =
            (String) tableModel.getValueAt(selectedRow, 2);

    String newName = JOptionPane.showInputDialog(
            this,
            "Enter new filename:",
            oldFilename
    );

    if (newName == null || newName.trim().isEmpty()) {
        return;
    }

    // Keep extension safe
    if (!newName.contains(".")) {
        newName = newName + "." + filetype;
    }

    try {

        // 🔹 Send rename request to server
        Socket socket = new Socket(serverIP, 5000);

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());
        DataInputStream in =
                new DataInputStream(socket.getInputStream());

        out.writeUTF("RENAME");
        out.writeUTF("user_" + userId);
        out.writeUTF(oldFilename);
        out.writeUTF(newName);

        String response = in.readUTF();
        socket.close();

        if (!response.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this,
                    "Rename failed on server.");
            return;
        }

        // 🔹 Update DB
        Connection con = DBConnection.getConnection(serverIP);

        String newPath = "FileBottleServer/user_"
                + userId + "/" + newName;

        String sql = "UPDATE files SET filename=?, filepath=?, updated_on=SYSDATE " +
                     "WHERE filename=? AND owner_id=?";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, newName);
        pst.setString(2, newPath);
        pst.setString(3, oldFilename);
        pst.setInt(4, userId);

        pst.executeUpdate();
        con.close();

        logActivity("Renamed File", oldFilename + " → " + newName);

        loadUserFiles();

        JOptionPane.showMessageDialog(this,
                "File renamed successfully!");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Rename Error: " + ex.getMessage());
    }
}
private void shareFile() {

    int selectedRow = fileTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a file first");
        return;
    }

    int fileId = (int) tableModel.getValueAt(selectedRow, 0);
    String filename = (String) tableModel.getValueAt(selectedRow, 1);

    try {

        Connection con = DBConnection.getConnection(serverIP);

        // 🔹 Get users who DO NOT already have access
        String sql = "SELECT user_id, username FROM users " +
                     "WHERE user_id != ? AND user_id NOT IN " +
                     "(SELECT shared_with FROM file_shares WHERE file_id=?)";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, userId);
        pst.setInt(2, fileId);

        ResultSet rs = pst.executeQuery();

        DefaultComboBoxModel<String> comboModel =
                new DefaultComboBoxModel<>();

        java.util.Map<String, Integer> userMap =
                new java.util.HashMap<>();

        while (rs.next()) {
            String username = rs.getString("username");
            int id = rs.getInt("user_id");

            comboModel.addElement(username);
            userMap.put(username, id);
        }

        if (comboModel.getSize() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No available users to share with.");
            return;
        }

        JComboBox<String> userDropdown =
                new JComboBox<>(comboModel);
        userDropdown.setFont(ModernPalette.BODY_FONT);

        JCheckBox downloadBox = new JCheckBox("Allow Download");
        downloadBox.setFont(ModernPalette.BODY_FONT);
        downloadBox.setOpaque(false);

        JCheckBox editBox = new JCheckBox("Allow Edit");
        editBox.setFont(ModernPalette.BODY_FONT);
        editBox.setOpaque(false);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setOpaque(false);
        panel.add(new JLabel("Select User:"));
        panel.add(userDropdown);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Permissions:"));
        panel.add(downloadBox);
        panel.add(editBox);

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Share File",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) return;

        String selectedUser =
                (String) userDropdown.getSelectedItem();

        int sharedUserId = userMap.get(selectedUser);

        // 🔹 Insert share record
        String insertSql = "INSERT INTO file_shares " +
                "(share_id, file_id, owner_id, shared_with, can_download, can_edit, shared_on) " +
                "VALUES (share_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE)";

        PreparedStatement insertPst =
                con.prepareStatement(insertSql);

        insertPst.setInt(1, fileId);
        insertPst.setInt(2, userId);
        insertPst.setInt(3, sharedUserId);
        insertPst.setInt(4, downloadBox.isSelected() ? 1 : 0);
        insertPst.setInt(5, editBox.isSelected() ? 1 : 0);

        insertPst.executeUpdate();
        con.close();

        logActivity("Shared File", filename);

        JOptionPane.showMessageDialog(this,
                "File shared successfully!");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Share Error: " + ex.getMessage());
    }
}
private void loadSharedFiles() {
    if (sharedModel == null) return;
    sharedModel.setRowCount(0);
    try {
        Connection con = DBConnection.getConnection(serverIP);
        String sql =
            "SELECT f.file_id, f.filename, u.username AS owner_name, s.owner_id, s.can_download, s.can_edit " +
            "FROM files f JOIN file_shares s ON f.file_id = s.file_id " +
            "JOIN users u ON s.owner_id = u.user_id " +
            "WHERE s.shared_with=? AND f.status='ACTIVE'";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            sharedModel.addRow(new Object[]{
                rs.getInt("file_id"),
                rs.getString("filename"),
                rs.getString("owner_name"),
                rs.getInt("owner_id"),
                rs.getInt("can_download") == 1 ? "Yes" : "No",
                rs.getInt("can_edit") == 1 ? "Yes" : "No"
            });
        }
        con.close();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Shared Load Error: " + e.getMessage());
    }
}

private JPanel createSharedPanel() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(ModernPalette.CONTENT_BG);

    JPanel header = createModernHeader("Shared Items", "Files shared with you by other users");
    mainPanel.add(header, BorderLayout.NORTH);

    JPanel content = new JPanel(new BorderLayout());
    content.setOpaque(false);
    content.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

    // Toolbar
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    toolbar.setOpaque(false);

    JButton btnView = new JButton("View Shared");
    styleActionButton(btnView);
    JButton btnDownload = new JButton("Download Copy");
    styleActionButton(btnDownload);
    JButton btnRefresh = new JButton("Refresh");
    styleActionButton(btnRefresh);

    toolbar.add(btnView);
    toolbar.add(btnDownload);
    toolbar.add(btnRefresh);
    content.add(toolbar, BorderLayout.NORTH);

    // Table
    String[] columns = {"File ID", "Filename", "Shared By", "Owner ID", "Download", "Edit"};
    sharedModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    JTable table = new JTable(sharedModel);
    modernizeTable(table);
    
    JPanel tableWrapper = createRoundedTablePanel(table);
    content.add(tableWrapper, BorderLayout.CENTER);
    mainPanel.add(content, BorderLayout.CENTER);

    btnView.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select file first");
            return;
        }
        String filename = (String) sharedModel.getValueAt(row, 1);
        int ownerId = (int) sharedModel.getValueAt(row, 3);
        boolean canEdit = sharedModel.getValueAt(row, 5).equals("Yes");
        downloadAndOpenSharedFile(filename, ownerId, canEdit);
    });

    btnDownload.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select file first");
            return;
        }
        if (sharedModel.getValueAt(row, 4).equals("No")) {
            JOptionPane.showMessageDialog(this, "Download not allowed.");
            return;
        }
        String filename = (String) sharedModel.getValueAt(row, 1);
        int ownerId = (int) sharedModel.getValueAt(row, 3);
        downloadSharedFile(filename, ownerId);
    });

    btnRefresh.addActionListener(e -> loadSharedFiles());

    loadSharedFiles();
    return mainPanel;
}
private void downloadAndOpenSharedFile(String filename,
                                       int ownerId,
                                       boolean canEdit) {

    try {

        Socket socket = new Socket(serverIP, 5000);

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());
        DataInputStream in =
                new DataInputStream(socket.getInputStream());

        out.writeUTF("DOWNLOAD");
        out.writeUTF("user_" + ownerId);
        out.writeUTF(filename);

        long fileSize = in.readLong();

        if (fileSize == -1) {
            JOptionPane.showMessageDialog(this,
                    "File not found on server");
            return;
        }

        File tempFile =
                File.createTempFile("shared_", "_" + filename);

        FileOutputStream fos =
                new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        long remaining = fileSize;

        while (remaining > 0 &&
               (bytesRead = in.read(buffer, 0,
                 (int)Math.min(buffer.length, remaining))) > 0) {

            fos.write(buffer, 0, bytesRead);
            remaining -= bytesRead;
        }

        fos.close();
        socket.close();

        // 🔥 Log view for owner
    String viewerName = getUsernameById(userId);

logOwnerActivity(ownerId,
        viewerName + " viewed",
        filename);

        String filetype = filename.contains(".")
                ? filename.substring(filename.lastIndexOf('.') + 1)
                : "unknown";

        openInternalViewer(
                tempFile,
                filename,
                filetype,
                canEdit,
                ownerId
        );

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage());
    }
}
private void downloadSharedFile(String filename, int ownerId) {

    try {

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(filename));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File destFile = chooser.getSelectedFile();

        Socket socket = new Socket(serverIP, 5000);

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());
        DataInputStream in =
                new DataInputStream(socket.getInputStream());

        out.writeUTF("DOWNLOAD");
        out.writeUTF("user_" + ownerId);  // 🔥 IMPORTANT
        out.writeUTF(filename);

        long fileSize = in.readLong();

        FileOutputStream fos =
                new FileOutputStream(destFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        long remaining = fileSize;

        while (remaining > 0 &&
               (bytesRead = in.read(buffer, 0,
                 (int)Math.min(buffer.length, remaining))) > 0) {

            fos.write(buffer, 0, bytesRead);
            remaining -= bytesRead;
        }

        fos.close();
        socket.close();

        JOptionPane.showMessageDialog(this,
                "Download complete!");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage());
    }
}
private void manageShares() {

    int selectedRow = fileTable.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select file first");
        return;
    }

    int fileId = (int) tableModel.getValueAt(selectedRow, 0);

    try {

        Connection con = DBConnection.getConnection(serverIP);

        String sql =
            "SELECT s.share_id, u.username, s.can_download, s.can_edit " +
            "FROM file_shares s " +
            "JOIN users u ON s.shared_with = u.user_id " +
            "WHERE s.file_id=?";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, fileId);

        ResultSet rs = pst.executeQuery();
DefaultTableModel model =
    new DefaultTableModel(
        new String[]{"Share ID","Username","Download","Edit"},0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

        while (rs.next()) {

            model.addRow(new Object[]{
                rs.getInt("share_id"),
                rs.getString("username"),
                rs.getInt("can_download")==1?"Yes":"No",
                rs.getInt("can_edit")==1?"Yes":"No"
            });
        }

        JTable table = new JTable(model);
        modernizeTable(table);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(500, 300));
        panel.setBackground(Color.WHITE);

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Manage Shares",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) return;

        int row = table.getSelectedRow();
        if (row == -1) return;

        int shareId = (int) model.getValueAt(row,0);

        String[] choices = {"Toggle Download",
                            "Toggle Edit",
                            "Remove Access"};

        int action = JOptionPane.showOptionDialog(
                this,
                "Choose action",
                "Edit Share",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                choices,
                choices[0]
        );

        if (action == 2) {

            PreparedStatement del =
                con.prepareStatement(
                "DELETE FROM file_shares WHERE share_id=?");
            del.setInt(1, shareId);
            del.executeUpdate();

        } else if (action == 0) {

            PreparedStatement upd =
                con.prepareStatement(
                "UPDATE file_shares SET can_download = " +
                "CASE WHEN can_download=1 THEN 0 ELSE 1 END " +
                "WHERE share_id=?");
            upd.setInt(1, shareId);
            upd.executeUpdate();

        } else if (action == 1) {

            PreparedStatement upd =
                con.prepareStatement(
                "UPDATE file_shares SET can_edit = " +
                "CASE WHEN can_edit=1 THEN 0 ELSE 1 END " +
                "WHERE share_id=?");
            upd.setInt(1, shareId);
            upd.executeUpdate();
        }

        con.close();

        JOptionPane.showMessageDialog(this,
                "Updated successfully");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                ex.getMessage());
    }
}
private void logOwnerActivity(int ownerId,
                              String action,
                              String filename) {

    try {

        Connection con =
            DBConnection.getConnection(serverIP);

        String sql =
            "INSERT INTO activity_log " +
            "(log_id, user_id, action, filename, action_time) " +
            "VALUES (activity_seq.NEXTVAL, ?, ?, ?, SYSDATE)";

        PreparedStatement pst =
            con.prepareStatement(sql);

        pst.setInt(1, ownerId);
        pst.setString(2, action);
        pst.setString(3, filename);

        pst.executeUpdate();
        con.close();

    } catch (Exception e) {
        System.out.println("Owner log failed");
    }
}
private String getUsernameById(int id) {

    String username = "Unknown";

    try {
        Connection con = DBConnection.getConnection(serverIP);

        String sql = "SELECT username FROM users WHERE user_id=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, id);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            username = rs.getString("username");
        }

        con.close();

    } catch (Exception e) {
        System.out.println("Username fetch failed");
    }

    return username;
}
private JPanel createEditPasswordPanel() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(ModernPalette.CONTENT_BG);

    JPanel header = createModernHeader("Security Settings", "Update your account password and security preferences");
    mainPanel.add(header, BorderLayout.NORTH);

    JPanel cardWrapper = new JPanel(new GridBagLayout());
    cardWrapper.setOpaque(false);

    RoundedPanel card = new RoundedPanel(20, Color.WHITE);
    card.setPreferredSize(new Dimension(450, 450));
    card.setLayout(new GridBagLayout());
    card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 0, 10, 0);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.weightx = 1.0;

    JLabel lblTitle = new JLabel("Change Password");
    lblTitle.setFont(ModernPalette.SUBTITLE_FONT);
    lblTitle.setForeground(ModernPalette.TEXT_PRIMARY);
    gbc.gridy = 0;
    card.add(lblTitle, gbc);

    gbc.gridy++;
    card.add(Box.createVerticalStrut(10), gbc);

    JLabel lblOld = new JLabel("Current Password");
    lblOld.setFont(new Font("Segoe UI", Font.BOLD, 12));
    lblOld.setForeground(ModernPalette.TEXT_SECONDARY);
    gbc.gridy++;
    card.add(lblOld, gbc);

    JPasswordField txtOld = new JPasswordField();
    txtOld.setPreferredSize(new Dimension(300, 40));
    txtOld.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, ModernPalette.BORDER_COLOR),
        BorderFactory.createEmptyBorder(5, 0, 5, 0)
    ));
    gbc.gridy++;
    card.add(txtOld, gbc);

    JLabel lblNew = new JLabel("New Password");
    lblNew.setFont(new Font("Segoe UI", Font.BOLD, 12));
    lblNew.setForeground(ModernPalette.TEXT_SECONDARY);
    gbc.gridy++;
    card.add(lblNew, gbc);

    JPasswordField txtNew = new JPasswordField();
    txtNew.setPreferredSize(new Dimension(300, 40));
    txtNew.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, ModernPalette.BORDER_COLOR),
        BorderFactory.createEmptyBorder(5, 0, 5, 0)
    ));
    gbc.gridy++;
    card.add(txtNew, gbc);

    JLabel lblConfirm = new JLabel("Confirm New Password");
    lblConfirm.setFont(new Font("Segoe UI", Font.BOLD, 12));
    lblConfirm.setForeground(ModernPalette.TEXT_SECONDARY);
    gbc.gridy++;
    card.add(lblConfirm, gbc);

    JPasswordField txtConfirm = new JPasswordField();
    txtConfirm.setPreferredSize(new Dimension(300, 40));
    txtConfirm.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, ModernPalette.BORDER_COLOR),
        BorderFactory.createEmptyBorder(5, 0, 5, 0)
    ));
    gbc.gridy++;
    card.add(txtConfirm, gbc);

    gbc.gridy++;
    card.add(Box.createVerticalStrut(20), gbc);

    JButton btnSave = new JButton("Update Password");
    styleActionButton(btnSave);
    gbc.gridy++;
    card.add(btnSave, gbc);

    cardWrapper.add(card);
    mainPanel.add(cardWrapper, BorderLayout.CENTER);

    btnSave.addActionListener(e -> {
        String oldPass = new String(txtOld.getPassword());
        String newPass = new String(txtNew.getPassword());
        String confirmPass = new String(txtConfirm.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match");
            return;
        }

        try {
            Connection con = DBConnection.getConnection(serverIP);
            String checkSql = "SELECT * FROM users WHERE user_id=? AND password=?";
            PreparedStatement pst = con.prepareStatement(checkSql);
            pst.setInt(1, userId);
            pst.setString(2, Hasher.hashPassword(oldPass));
            ResultSet rs = pst.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Current password incorrect");
                con.close();
                return;
            }

            String updateSql = "UPDATE users SET password=? WHERE user_id=?";
            PreparedStatement updatePst = con.prepareStatement(updateSql);
            updatePst.setString(1, Hasher.hashPassword(newPass));
            updatePst.setInt(2, userId);
            updatePst.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Password updated successfully!");
            logActivity("Changed Password", "-");
            txtOld.setText("");
            txtNew.setText("");
            txtConfirm.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    });

    return mainPanel;
}
private void notifyServerLogout() {
    try {
        Socket socket = new Socket(serverIP, 5000);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());

        out.writeUTF("LOGOUT_NOTIFY");
        in.readUTF();

        socket.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// ==========================================
// MODERN UI COMPONENTS & STYLING
// ==========================================

public static class ModernPalette {
    public static final Color SIDEBAR_BG = new Color(28, 31, 38);
    public static final Color SIDEBAR_HOVER = new Color(40, 45, 55);
    public static final Color SIDEBAR_ACTIVE = new Color(41, 121, 255);
    public static final Color CONTENT_BG = new Color(248, 249, 252);
    public static final Color ACCENT_COLOR = new Color(41, 121, 255);
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    public static final Color TEXT_LIGHT = new Color(180, 190, 210);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(233, 236, 239);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font NAV_FONT = new Font("Segoe UI", Font.BOLD, 15);
}

public static class RoundedPanel extends JPanel {
    private int radius;
    private Color backgroundColor;

    public RoundedPanel(int radius, Color bgColor) {
        this.radius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(radius, radius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundColor != null) {
            graphics.setColor(backgroundColor);
        } else {
            graphics.setColor(getBackground());
        }
        graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
    }
}

public static class ModernButton extends JButton {
    private float alpha = 0f;
    private Timer timer;

    public ModernButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(Color.WHITE);
        setFont(ModernPalette.BODY_FONT);
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { startAnimation(true); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { startAnimation(false); }
        });
    }

    private void startAnimation(boolean forward) {
        if (timer != null && timer.isRunning()) timer.stop();
        timer = new Timer(20, e -> {
            if (forward) {
                alpha += 0.1f;
                if (alpha >= 1f) { alpha = 1f; timer.stop(); }
            } else {
                alpha -= 0.1f;
                if (alpha <= 0f) { alpha = 0f; timer.stop(); }
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color baseColor = ModernPalette.ACCENT_COLOR;
        if (getModel().isPressed()) {
            g2.setColor(baseColor.darker());
        } else {
            g2.setColor(baseColor);
            if (alpha > 0) {
                Color hoverColor = baseColor.brighter();
                int r = (int)(baseColor.getRed() + (hoverColor.getRed() - baseColor.getRed()) * alpha);
                int gr = (int)(baseColor.getGreen() + (hoverColor.getGreen() - baseColor.getGreen()) * alpha);
                int b = (int)(baseColor.getBlue() + (hoverColor.getBlue() - baseColor.getBlue()) * alpha);
                g2.setColor(new Color(r, gr, b));
            }
        }
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}

private JPanel createModernHeader(String title, String subtitle) {
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

    JLabel lblTitle = new JLabel(title);
    lblTitle.setFont(ModernPalette.TITLE_FONT);
    lblTitle.setForeground(ModernPalette.TEXT_PRIMARY);

    JLabel lblSub = new JLabel(subtitle);
    lblSub.setFont(ModernPalette.BODY_FONT);
    lblSub.setForeground(ModernPalette.TEXT_SECONDARY);

    header.add(lblTitle, BorderLayout.NORTH);
    header.add(lblSub, BorderLayout.SOUTH);
    return header;
}

private void modernizeTable(JTable table) {
    table.setRowHeight(40);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setSelectionBackground(new Color(230, 240, 255));
    table.setSelectionForeground(ModernPalette.ACCENT_COLOR);
    table.setFont(ModernPalette.BODY_FONT);
    table.setGridColor(ModernPalette.BORDER_COLOR);
    
    table.getTableHeader().setBackground(Color.WHITE);
    table.getTableHeader().setForeground(ModernPalette.TEXT_SECONDARY);
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernPalette.BORDER_COLOR));
    table.getTableHeader().setPreferredSize(new Dimension(0, 40));
}

private JPanel createRoundedTablePanel(JTable table) {
    RoundedPanel wrapper = new RoundedPanel(15, Color.WHITE);
    wrapper.setLayout(new BorderLayout());
    wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(null);
    scrollPane.getViewport().setBackground(Color.WHITE);
    
    wrapper.add(scrollPane, BorderLayout.CENTER);
    return wrapper;
}
}
