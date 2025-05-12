import com.example.cab302tailproject.DAO.LoginPage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage("user@example.com", "password123");
    }

    @Test
    void testGetEmail() {
        assertEquals("user@example.com", loginPage.getEmail());
    }

    @Test
    void testSetEmail() {
        loginPage.setEmail("newuser@example.com");
        assertEquals("newuser@example.com", loginPage.getEmail());
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", loginPage.getPassword());
    }

    @Test
    void testSetPassword() {
        loginPage.setPassword("newpassword123");
        assertEquals("newpassword123", loginPage.getPassword());
    }

    @Test
    void testIsEmptyFields() {
        LoginPage emptyLogin = new LoginPage("", "");
        assertTrue(emptyLogin.isEmpty());

        LoginPage emailOnlyLogin = new LoginPage("user@example.com", "");
        assertTrue(emailOnlyLogin.isEmpty());

        LoginPage passwordOnlyLogin = new LoginPage("", "password123");
        assertTrue(passwordOnlyLogin.isEmpty());

        LoginPage fullLogin = new LoginPage("user@example.com", "password123");
        assertFalse(fullLogin.isEmpty());
    }

    @Test
    void testIsValidLogin() {
        assertTrue(loginPage.isValidLogin());

        LoginPage wrongEmail = new LoginPage("wrong@example.com", "password123");
        assertFalse(wrongEmail.isValidLogin());

        LoginPage wrongPassword = new LoginPage("user@example.com", "wrongpassword");
        assertFalse(wrongPassword.isValidLogin());

        LoginPage completelyWrong = new LoginPage("wrong@example.com", "wrongpassword");
        assertFalse(completelyWrong.isValidLogin());
    }
}
