
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String host = System.getenv("MYSQL_HOST");
        String port = System.getenv("MYSQL_PORT");
        String database = System.getenv("MYSQL_DATABASE");
        String user = System.getenv("MYSQL_USER");
        String password = System.getenv("MYSQL_PASSWORD");

        if (host == null || port == null || database == null
                || user == null || password == null) {
            throw new Exception(
                "Database environment variables are not configured."
            );
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?sslMode=REQUIRED";

        return DriverManager.getConnection(url, user, password);
    }
}
