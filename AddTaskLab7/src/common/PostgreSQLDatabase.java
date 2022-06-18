package common;


import java.sql.*;

public class PostgreSQLDatabase implements IDatabase{
    private static final String URL = "jdbc:postgresql://%s:%d/%s" +
            "?autoReconnect=true" +
            "&useSSL=false" +
            "&useUnicode=true" +
            "&characterEncoding=utf-8";

    private final String host;
    private final int port;
    private final String database, username, password;
    private Connection connection;
    private PreparedStatement statement;

    public PostgreSQLDatabase(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(String.format(URL, host, port, database),
                    username, password);
            System.out.println("Соединение с базой данных успешно установлено.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Соединение с базой данных не было установлено: отключение.");
            System.exit(0);
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        ResultSet result;
        Statement statement = this.connection.createStatement();
        if (query.contains("SELECT")) {
            result = statement.executeQuery(query);
        } else {
            statement.executeUpdate(query);
            result = statement.getGeneratedKeys();
        }
        return result;
    }

    public void disconnect() {
        if (connection == null) return;
        try {
            connection.close();
            System.out.println("Соединение с базой данных закрыто");
        } catch (SQLException ex) {
        }
    }

    public ResultSet executeQueryStatement() throws SQLException {
        ResultSet resultSet;
        statement.executeUpdate();
        resultSet = statement.getGeneratedKeys();
        return resultSet;
    }

    public void setStatement(String sql) {
        try {
            this.statement = this.connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement getStatement() {
        return statement;
    }
}
