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
        JPanel leftPanel = new JPanel();leftPanel.setBackground(new Color(20, 24, 32));
        leftPanel.setPreferredSize(new Dimension(230, 650));
        leftPanel.setLayout(new BorderLayout());
JPanel logoPanel = new JPanel();
logoPanel.setBackground(new Color(28, 31, 38));
logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

// App Name
JLabel logo = new JLabel("FileBottle");
logo.setForeground(Color.WHITE);
logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
logo.setAlignmentX(Component.CENTER_ALIGNMENT);

// Username (clean + bold)
JLabel nameLabel = new JLabel(username);
nameLabel.setForeground(new Color(180, 190, 210));  // soft grey-blue
nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

// Add spacing
logoPanel.add(logo);
logoPanel.add(Box.createVerticalStrut(10));
logoPanel.add(nameLabel);

leftPanel.add(logoPanel, BorderLayout.NORTH);
        // Menu Section
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(28, 31, 38));
        menuPanel.setLayout(new GridLayout(8, 1, 0, 8));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        btnFiles = createNavButton("Your Files");
        btnShared = createNavButton("Shared");
        btnTrash = createNavButton("Trash");
        btnActivity = createNavButton("Activity");
        btnEditPass = createNavButton("Edit Password");

        menuPanel.add(btnFiles);
        menuPanel.add(btnShared);
        menuPanel.add(btnTrash);
        menuPanel.add(btnActivity);
        menuPanel.add(btnEditPass);
        JButton btnLogout = createNavButton("Logout");
menuPanel.add(btnLogout);
btnLogout.addActionListener(e -> {

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        logActivity("Logged Out", "-");
         notifyServerLogout();
        new LoginRegister(serverIP).setVisible(true);
        dispose();
    }
});
btnActivity.addActionListener(e -> {
    contentPanel.add(createActivityPanel(), "activity");
    cardLayout.show(contentPanel, "activity");
    setActiveButton(btnActivity);
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
            cardLayout.show(contentPanel, "files");
            setActiveButton(btnFiles);
        });

        btnShared.addActionListener(e -> {
   
    cardLayout.show(contentPanel, "shared");
    setActiveButton(btnShared);
});

        btnTrash.addActionListener(e -> {
            cardLayout.show(contentPanel, "trash");
            setActiveButton(btnTrash);
        });

        btnActivity.addActionListener(e -> {
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
        setActiveButton(btnFiles);
    }

    // ===== CREATE NAV BUTTON =====
    private JButton createNavButton(String text) {

        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBackground(new Color(30, 36, 48));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
private JPanel createYourFilesPanel() {

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(245, 245, 245));

    // Top bar with buttons
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanel.setBackground(new Color(245, 245, 245));

    JButton btnUpload = new JButton("Upload");
    styleActionButton(btnUpload);
    JButton btnDelete = new JButton("Move to Trash");
    styleActionButton(btnDelete);
    JButton btnRefresh = new JButton("Refresh");
    styleActionButton(btnRefresh);
JButton btnView = new JButton("View");
styleActionButton(btnView);
JButton btnDownload = new JButton("Download");
styleActionButton(btnDownload);
 topPanel.add(btnUpload);
 
    topPanel.add(btnView);
    topPanel.add(btnDownload);
    JButton btnRename = new JButton("Rename");
    styleActionButton(btnRename);
topPanel.add(btnRename);
JButton btnShare = new JButton("Share");
styleActionButton(btnShare);
topPanel.add(btnShare);
btnShare.addActionListener(e -> shareFile());
JButton btnManageShare = new JButton("Manage Shares");
styleActionButton(btnManageShare);
topPanel.add(btnManageShare);
btnManageShare.addActionListener(e -> manageShares());
btnRename.addActionListener(e -> renameFile());
    topPanel.add(btnDelete);
    topPanel.add(btnRefresh);

btnDownload.addActionListener(e -> downloadFile());


btnView.addActionListener(e -> viewFile());
   

    panel.add(topPanel, BorderLayout.NORTH);

    // Table
    String[] columns = {"File ID", "Filename", "Type", "Size (KB)", "Created On"};
tableModel = new DefaultTableModel(columns, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
    fileTable = new JTable(tableModel);
fileTable.setRowHeight(30);
fileTable.setShowGrid(false);
fileTable.setIntercellSpacing(new Dimension(0, 0));
fileTable.setSelectionBackground(new Color(41,121,255));
fileTable.setSelectionForeground(Color.WHITE);
fileTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
fileTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
fileTable.getTableHeader().setReorderingAllowed(false);
    JScrollPane scrollPane = new JScrollPane(fileTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Button Actions
    btnUpload.addActionListener(e -> uploadFile());
    btnDelete.addActionListener(e -> moveToTrash());
    btnRefresh.addActionListener(e -> loadUserFiles());

    loadUserFiles();

    return panel;
}
private void styleActionButton(JButton btn) {
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setBackground(new Color(41,121,255));
    btn.setForeground(Color.WHITE);
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

    JFrame viewer = new JFrame("Viewing: " + filename);
    viewer.setSize(900, 650);
    viewer.setLocationRelativeTo(this);
    viewer.setLayout(new BorderLayout());

    try {

        // ================= IMAGE =================
        if (filetype.equalsIgnoreCase("jpg") ||
            filetype.equalsIgnoreCase("png") ||
            filetype.equalsIgnoreCase("jpeg") ||
            filetype.equalsIgnoreCase("gif") ||
            filetype.equalsIgnoreCase("bmp")) {

            ImageIcon icon =
                new ImageIcon(tempFile.getAbsolutePath());

            JLabel imageLabel = new JLabel(icon);
            imageLabel.setHorizontalAlignment(
                SwingConstants.CENTER);

            viewer.add(new JScrollPane(imageLabel),
                       BorderLayout.CENTER);

            viewer.setVisible(true);
            return;
        }

        // ================= PDF =================
        if (filetype.equalsIgnoreCase("pdf")) {
            Desktop.getDesktop().open(tempFile);
            return;
        }

        // ================= TEXT =================
        byte[] fileBytes =
            java.nio.file.Files.readAllBytes(tempFile.toPath());

        String originalContent =
            new String(fileBytes,
                java.nio.charset.StandardCharsets.UTF_8);

        if (originalContent.contains("\0")) {

            JLabel msg = new JLabel(
                "Preview not supported for binary files.");
            msg.setHorizontalAlignment(
                SwingConstants.CENTER);

            viewer.add(msg);
            viewer.setVisible(true);
            return;
        }

        JTextArea textArea =
            new JTextArea(originalContent);

        textArea.setFont(
            new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scrollPane =
            new JScrollPane(textArea);

        JButton btnSave = new JButton("Save");
        btnSave.setEnabled(false);

        // 🔥 Permission control
        if (!canEdit) {
            btnSave.setVisible(false);
        }

        textArea.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                private void checkChange() {
                    if (canEdit) {
                        btnSave.setEnabled(
                            !textArea.getText()
                             .equals(originalContent));
                    }
                }

                public void insertUpdate(
                    javax.swing.event.DocumentEvent e)
                    { checkChange(); }

                public void removeUpdate(
                    javax.swing.event.DocumentEvent e)
                    { checkChange(); }

                public void changedUpdate(
                    javax.swing.event.DocumentEvent e)
                    { checkChange(); }
            });

        btnSave.addActionListener(e -> {

            try {

                // Save locally first
                java.nio.file.Files.write(
                    tempFile.toPath(),
                    textArea.getText().getBytes(
                        java.nio.charset.StandardCharsets.UTF_8)
                );

                // 🔥 Send updated file back to OWNER folder
                sendFileToServer(tempFile, filename, ownerId);

                logActivity("Edited File", filename);
               String editorName = getUsernameById(userId);

logOwnerActivity(ownerId,
        editorName + " edited",
        filename);
                JOptionPane.showMessageDialog(
                    viewer,
                    "Saved to server successfully!");

                viewer.dispose();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    viewer,
                    "Save Error: " + ex.getMessage());
            }
        });

        JPanel bottomPanel =
            new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnSave);

        viewer.add(scrollPane, BorderLayout.CENTER);
        viewer.add(bottomPanel, BorderLayout.SOUTH);

        viewer.setVisible(true);

    } catch (Exception ex) {

        JOptionPane.showMessageDialog(this,
            "Viewer Error: " + ex.getMessage());
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

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(245,245,245));

    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanel.setBackground(new Color(245,245,245));

    JButton btnRestore = new JButton("Restore");
    JButton btnDeletePermanent = new JButton("Delete Permanently");
    JButton btnRefresh = new JButton("Refresh");
    styleActionButton(btnRefresh);
    styleActionButton(btnRestore);
    styleActionButton(btnDeletePermanent);
    topPanel.add(btnRestore);
    topPanel.add(btnDeletePermanent);
    topPanel.add(btnRefresh);

    panel.add(topPanel, BorderLayout.NORTH);

    String[] columns = {"File ID", "Filename", "Type", "Size (KB)", "Deleted On"};
    trashModel = new DefaultTableModel(columns, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
    trashTable = new JTable(trashModel);
    trashTable.setRowHeight(30);
trashTable.setShowGrid(false);
trashTable.setIntercellSpacing(new Dimension(0, 0));
trashTable.setSelectionBackground(new Color(41,121,255));
trashTable.setSelectionForeground(Color.WHITE);
trashTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
trashTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
trashTable.getTableHeader().setReorderingAllowed(false);

    JScrollPane scrollPane = new JScrollPane(trashTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    btnRestore.addActionListener(e -> restoreFile());
    btnDeletePermanent.addActionListener(e -> deleteFilePermanent());
    btnRefresh.addActionListener(e -> loadTrashFiles());

    loadTrashFiles();

    return panel;
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
            btn.setBackground(new Color(45, 49, 58));
        }

        active.setBackground(new Color(70, 130, 180)); // Highlight color
    }

    // ===== CONTENT LABEL TEMPLATE =====
    private JPanel createContentLabel(String text) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(label, BorderLayout.CENTER);

        return panel;
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

    } catch (Exception e) {
        System.out.println("Logging failed: " + e.getMessage());
    }
}
private JPanel createActivityPanel() {

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(245,245,245));

    String[] columns = {"Action", "Filename", "Time"};
    DefaultTableModel model = new DefaultTableModel(columns, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
    JTable table = new JTable(model);
table.setRowHeight(30);
table.setShowGrid(false);
table.setIntercellSpacing(new Dimension(0, 0));
table.setSelectionBackground(new Color(41,121,255));
table.setSelectionForeground(Color.WHITE);
table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
table.getTableHeader().setReorderingAllowed(false);
    try {
        Connection con = DBConnection.getConnection(serverIP);

        String sql = "SELECT action, filename, action_time " +
                     "FROM activity_log WHERE user_id=? " +
                     "ORDER BY action_time DESC";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, userId);

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("action"),
                rs.getString("filename"),
                rs.getTimestamp("action_time")
            });
        }

        con.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Activity Load Error: " + e.getMessage());
    }

    panel.add(new JScrollPane(table), BorderLayout.CENTER);

    return panel;
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

        JCheckBox downloadBox = new JCheckBox("Allow Download");
        JCheckBox editBox = new JCheckBox("Allow Edit");

        Object[] message = {
                "Select User:",
                userDropdown,
                "Permissions:",
                downloadBox,
                editBox
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
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
private JPanel createSharedPanel() {

    JPanel panel = new JPanel(new BorderLayout());

   String[] columns = {
    "File ID",
    "Filename",
    "Shared By",
    "Owner ID",
    "Download",
    "Edit"
};
DefaultTableModel model = new DefaultTableModel(columns, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};

    JTable table = new JTable(model);
table.setRowHeight(30);
table.setShowGrid(false);
table.setIntercellSpacing(new Dimension(0, 0));
table.setSelectionBackground(new Color(41,121,255));
table.setSelectionForeground(Color.WHITE);
table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
table.getTableHeader().setReorderingAllowed(false);
    JButton btnView = new JButton("View");
    styleActionButton(btnView);
    JButton btnDownload = new JButton("Download");
    styleActionButton(btnDownload);
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanel.add(btnView);
    topPanel.add(btnDownload);

    panel.add(topPanel, BorderLayout.NORTH);
    panel.add(new JScrollPane(table), BorderLayout.CENTER);

    try {

        Connection con = DBConnection.getConnection(serverIP);

        String sql =
            "SELECT f.file_id,\n" +
"       f.filename,\n" +
"       u.username AS owner_name,\n" +
"       s.owner_id,\n" +
"       s.can_download,\n" +
"       s.can_edit\n" +
"FROM files f\n" +
"JOIN file_shares s ON f.file_id = s.file_id\n" +
"JOIN users u ON s.owner_id = u.user_id\n" +
"WHERE s.shared_with=? AND f.status='ACTIVE'";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, userId);

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {

           model.addRow(new Object[]{
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
        JOptionPane.showMessageDialog(this,
                "Shared Load Error: " + e.getMessage());
    }

    // ===== VIEW BUTTON =====
    btnView.addActionListener(e -> {

    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select file first");
        return;
    }

    String filename = (String) model.getValueAt(row, 1);
    int ownerId = (int) model.getValueAt(row, 3);

    String editPermission = (String) model.getValueAt(row, 5);
    boolean canEdit = editPermission.equals("Yes");

    downloadAndOpenSharedFile(filename, ownerId, canEdit);
});

    // ===== DOWNLOAD BUTTON =====
    btnDownload.addActionListener(e -> {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select file first");
            return;
        }

        String permission = (String) model.getValueAt(row, 4);

        if (permission.equals("No")) {
            JOptionPane.showMessageDialog(this,
                    "Download not allowed.");
            return;
        }

        String filename = (String) model.getValueAt(row, 1);
        int ownerId = (int) model.getValueAt(row, 3);

        downloadSharedFile(filename, ownerId);
    });

    return panel;
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

        int option = JOptionPane.showConfirmDialog(
                this,
                new JScrollPane(table),
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

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBackground(new Color(248,249,252));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10,10,10,10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel lblTitle = new JLabel("Change Password");
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

    JPasswordField txtOld = new JPasswordField(15);
    JPasswordField txtNew = new JPasswordField(15);
    JPasswordField txtConfirm = new JPasswordField(15);

    JButton btnSave = new JButton("Update Password");
    styleActionButton(btnSave);

    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(lblTitle, gbc);

    gbc.gridy++;
    panel.add(new JLabel("Old Password"), gbc);

    gbc.gridy++;
    panel.add(txtOld, gbc);

    gbc.gridy++;
    panel.add(new JLabel("New Password"), gbc);

    gbc.gridy++;
    panel.add(txtNew, gbc);

    gbc.gridy++;
    panel.add(new JLabel("Confirm Password"), gbc);

    gbc.gridy++;
    panel.add(txtConfirm, gbc);

    gbc.gridy++;
    panel.add(btnSave, gbc);

    btnSave.addActionListener(e -> {

        String oldPass = new String(txtOld.getPassword());
        String newPass = new String(txtNew.getPassword());
        String confirmPass = new String(txtConfirm.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match");
            return;
        }

        try {

            Connection con = DBConnection.getConnection(serverIP);

            String checkSql =
                "SELECT * FROM users WHERE user_id=? AND password=?";

            PreparedStatement pst =
                con.prepareStatement(checkSql);

            pst.setInt(1, userId);
            pst.setString(2,Hasher.hashPassword(oldPass));

            ResultSet rs = pst.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "Old password incorrect");
                con.close();
                return;
            }

            String updateSql =
                "UPDATE users SET password=? WHERE user_id=?";

            PreparedStatement updatePst =
                con.prepareStatement(updateSql);

            updatePst.setString(1, Hasher.hashPassword(newPass));
            updatePst.setInt(2, userId);
            updatePst.executeUpdate();

            con.close();

            JOptionPane.showMessageDialog(this,
                    "Password updated successfully");
            logActivity("Changed Password", "-");
            txtOld.setText("");
            txtNew.setText("");
            txtConfirm.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage());
        }
    });

    return panel;
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
}
