package common;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDatabase {
    ResultSet executeQuery(String query) throws SQLException;

    default ResultSet executeQuery(String query, Object... arg) throws SQLException {
        for (Object o : arg) {
            if (!(o instanceof String)) {
                continue;
            }
        }
        return executeQuery(String.format(query, arg));
    }
}
