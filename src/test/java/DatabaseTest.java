import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    @Test
    void testDatabaseConnection() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        assertNotNull(connection);

        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE test (id INTEGER PRIMARY KEY, name TEXT)");
        stmt.execute("INSERT INTO test (name) VALUES ('John Doe')");
        var rs = stmt.executeQuery("SELECT * FROM test WHERE name='John Doe'");
        assertTrue(rs.next());

        connection.close();
    }
}
