import com.example.cab302tailproject.DAO.LoginPage;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class LoginPageTest {

    private LoginPage loginPage;
    private LoginPage[] logins = {
            new LoginPage("user@example.com", "password123"),
            new LoginPage("admin@example.com", "adminpass"),
            new LoginPage("test@example.com", "testpass")
    };

    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage("user@example.com", "password123");
    }

    @Test
    public void testGetEmail() {
        assertEquals("user@example.com", loginPage.getEmail());
    }

    @Test
    public void testSetEmail() {
        loginPage.setEmail("newuser@example.com");
        assertEquals("newuser@example.com", loginPage.getEmail());
    }

    @Test
    public void testGetPassword() {
        assertEquals("password123", loginPage.getPassword());
    }

    @Test
    public void testSetPassword() {
        loginPage.setPassword("newpassword");
        assertEquals("newpassword", loginPage.getPassword());
    }

    @Test
    public void testIsEmptyWhenFieldsAreEmpty() {
        LoginPage emptyLogin = new LoginPage("", "");
        assertTrue(emptyLogin.isEmpty());
    }

    @Test
    public void testIsEmptyWhenEmailIsEmpty() {
        LoginPage login = new LoginPage("", "password123");
        assertTrue(login.isEmpty());
    }

    @Test
    public void testIsEmptyWhenPasswordIsEmpty() {
        LoginPage login = new LoginPage("user@example.com", "");
        assertTrue(login.isEmpty());
    }

    @Test
    public void testIsNotEmptyWhenFieldsAreFilled() {
        assertFalse(loginPage.isEmpty());
    }

    @Test
    public void testValidLoginCorrectCredentials() {
        assertTrue(loginPage.isValidLogin());
    }

    @Test
    public void testInvalidLoginWrongEmail() {
        LoginPage login = new LoginPage("wrong@example.com", "password123");
        assertFalse(login.isValidLogin());
    }

    @Test
    public void testInvalidLoginWrongPassword() {
        LoginPage login = new LoginPage("user@example.com", "wrongpassword");
        assertFalse(login.isValidLogin());
    }

    @Test
    public void testInvalidLoginWrongEmailAndPassword() {
        LoginPage login = new LoginPage("wrong@example.com", "wrongpassword");
        assertFalse(login.isValidLogin());
    }

    @Test
    public void testMultipleLoginsNotAffectEachOther() {
        for (LoginPage login : logins) {
            assertNotNull(login.getEmail());
            assertNotNull(login.getPassword());
        }
    }
}
