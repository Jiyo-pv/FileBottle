import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class FileServer implements Runnable {
private ServerSocket serverSocket;
private volatile boolean running = true;
private static int connectedClients = 0;
    private static final int PORT = 5000;
    private String serverRoot;
private static int loggedInUsers = 0;

public static synchronized void userLoggedIn() {
    loggedInUsers++;
}

public static synchronized void userLoggedOut() {
    if (loggedInUsers > 0) {
        loggedInUsers--;
    }
}

public static int getLoggedInUsers() {
    return loggedInUsers;
}
    public FileServer(String serverRoot) {
        this.serverRoot = serverRoot;
    }
    public static int getConnectedClients() {
    return connectedClients;
}
    public void stopServer() {
    try {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    @Override
    public void run() {

    try {
        serverSocket = new ServerSocket(5000);
       

        while (running) {

            Socket socket = serverSocket.accept();

            connectedClients++;

            

            new Thread(() -> {
                handleClient(socket);
                connectedClients--;
                
            }).start();
        }

    } catch (Exception e) {
        if (running) {
            e.printStackTrace();
        }
    }
}

    private void handleClient(Socket socket) {

        try (
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {

            String command = in.readUTF();

            if (command.equals("UPLOAD")) {

                String userFolder = in.readUTF();
                String filename = in.readUTF();
                serverLog("UPLOAD - " + filename + " to " + userFolder);
                long fileSize = in.readLong();

                File folder = new File(serverRoot + File.separator + userFolder);
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                File file = new File(folder, filename);

                FileOutputStream fos = new FileOutputStream(file);

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
                out.writeUTF("SUCCESS");
            }
            else if (command.equals("LOGIN_NOTIFY")) {
                loggedInUsers++;
                 out.writeUTF("OK");
                 serverLog("USER LOGGED IN");
}
else if (command.equals("LOGOUT_NOTIFY")) {
    if (loggedInUsers > 0) {
        loggedInUsers--;
    }
    serverLog("USER LOGGED OUT");
    out.writeUTF("OK");
}
            else if (command.equals("RENAME")) {

    String userFolder = in.readUTF();
    String oldName = in.readUTF();
    String newName = in.readUTF();
serverLog("RENAME - " + oldName + " to " + newName + " in " + userFolder);
    File oldFile = new File(serverRoot + File.separator
            + userFolder + File.separator + oldName);

    File newFile = new File(serverRoot + File.separator
            + userFolder + File.separator + newName);

    if (!oldFile.exists()) {
        out.writeUTF("FAIL");
        return;
    }

    boolean renamed = oldFile.renameTo(newFile);

    if (renamed) {
        out.writeUTF("SUCCESS");
    } else {
        out.writeUTF("FAIL");
    }
            }
            else if (command.equals("DELETE")) {

    String userFolder = in.readUTF();
    String filename = in.readUTF();
serverLog("DELETE - " + filename + " from " + userFolder);
    File file = new File(serverRoot + File.separator
            + userFolder + File.separator + filename);

    if (!file.exists()) {
        out.writeUTF("FAIL");
        return;
    }

    try {
        java.nio.file.Files.delete(file.toPath());
        out.writeUTF("SUCCESS");
    } catch (Exception e) {
        e.printStackTrace();
        out.writeUTF("FAIL");
    }
}
            else if (command.equals("DOWNLOAD")) {

                String userFolder = in.readUTF();
                String filename = in.readUTF();
               serverLog("DOWNLOAD - " + filename + " from " + userFolder);
                File file = new File(serverRoot + File.separator
                        + userFolder + File.separator + filename);
               
                if (!file.exists()) {
                    out.writeLong(-1);
                    return;
                }

                out.writeLong(file.length());

                FileInputStream fis = new FileInputStream(file);

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }

                fis.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void serverLog(String action) {

    try {

        Connection con = DBConnection.getConnection("127.0.0.1"); 
        // or use serverIP if needed

        String sql = "INSERT INTO server_log " +
                     "(log_id, action, log_time) " +
                     "VALUES (server_log_seq.NEXTVAL, ?, SYSDATE)";

        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, action);
        pst.executeUpdate();

        con.close();

    } catch (Exception e) {
        System.out.println("Server log failed: " + e.getMessage());
    }
}
}