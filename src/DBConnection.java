import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection(String ip) throws Exception {

        String url = "jdbc:oracle:thin:@" + ip + ":1521:xe";
        String username = "user";        // change if needed
        String password = "pass"; // your oracle password

        Class.forName("oracle.jdbc.driver.OracleDriver");

        return DriverManager.getConnection(url, username, password);
    }
}