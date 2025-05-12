import com.example.cab302tailproject.controller.RegistrationController;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.application.Platform;

import static org.junit.jupiter.api.Assertions.*;


// Currently overcomplicated by using javafx stuff - might be worth updating the controller
public class RegistrationTest {

    private static final String VALID_FIRST_NAME = "John";
    private static final String INVALID_FIRST_NAME = "12345";
    private static final String VALID_EMAIL = "user@example.com";
    private static final String INVALID_EMAIL = "invalid-email";
    private static final String VALID_PASSWORD = "Password123";
    private static final String MISMATCHED_PASSWORD = "Password456";

    private RegistrationController controller;

    @BeforeAll
    static void initToolkit() {
        // Initialize the JavaFX toolkit to avoid "Toolkit not initialized" errors
        Platform.startup(() -> {});
    }


    @BeforeEach
    void setUp() {
        controller = new RegistrationController();
    }

    @Test
    void testVerifyName_withValidName() {
        // Verify name validation logic works
        assertTrue(controller.verifyName(new TextField(VALID_FIRST_NAME)),
                "Name validation failed for valid input");
    }

    @Test
    void testVerifyName_withInvalidName() {
        //controller.setFirstName(INVALID_FIRST_NAME);
        // Verify name validation logic works
        assertFalse(controller.verifyName(new TextField(INVALID_FIRST_NAME)),
                "Name validation passed for invalid input (numbers)");
    }

    @Test
    void testVerifyPassword_withMatchingPasswords() {
        // Verify password validation logic works
        assertTrue(controller.verifyPassword(
                new PasswordField() {{ setText(VALID_PASSWORD); }},
                new PasswordField() {{ setText(VALID_PASSWORD); }}
        ), "Password verification failed for matching passwords");
    }

    @Test
    void testVerifyPassword_withNonMatchingPasswords() {
        // Verify password validation logic works
        assertFalse(controller.verifyPassword(
                new PasswordField() {{ setText(VALID_PASSWORD); }},
                new PasswordField() {{ setText(MISMATCHED_PASSWORD); }}
        ), "Password verification passed for non-matching passwords");
    }

    @Test
    void testVerifyPasswordWhenPasswordIsEmpty() {
        // Verify password validation logic works
        assertFalse(controller.verifyPassword(
                new PasswordField() {{ setText(""); }},
                new PasswordField() {{ setText(MISMATCHED_PASSWORD); }}
        ), "Password verification passed for empty password");
    }

    @Test
    void testVerifyEmail_withValidEmail() {
        // Verify email validation logic works
        assertTrue(controller.verifyEmail(new TextField(VALID_EMAIL)),
                "Email validation failed for valid input");
    }

    @Test
    void testVerifyEmail_withInvalidEmail() {
        // Verify email validation logic works
        assertFalse(controller.verifyEmail(new TextField(INVALID_EMAIL)),
                "Email validation passed for invalid input");
    }

    @Test
    void testVerifyEmailWithEmptyEmail() {
        // Verify email validation logic works
        assertFalse(controller.verifyEmail(new TextField("")),
                "Email validation passed for invalid input");
    }
}
