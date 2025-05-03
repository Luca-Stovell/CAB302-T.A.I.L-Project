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
        // Set a valid name
        controller.setFirstName(VALID_FIRST_NAME);
        // Verify name validation logic works
        assertTrue(controller.verifyName(new TextField(controller.getFirstName())),
                "Name validation failed for valid input");
    }

    @Test
    void testVerifyName_withInvalidName() {
        // Set an invalid name
        controller.setFirstName(INVALID_FIRST_NAME);
        // Verify name validation logic works
        assertFalse(controller.verifyName(new TextField(controller.getFirstName())),
                "Name validation passed for invalid input (numbers)");
    }

    @Test
    void testVerifyPassword_withMatchingPasswords() {
        // Set matching passwords
        controller.setPassword(VALID_PASSWORD);
        controller.setConfirmPassword(VALID_PASSWORD);
        // Verify password validation logic works
        assertTrue(controller.verifyPassword(
                new PasswordField() {{ setText(controller.getPassword()); }},
                new PasswordField() {{ setText(controller.getConfirmPassword()); }}
        ), "Password verification failed for matching passwords");
    }

    @Test
    void testVerifyPassword_withNonMatchingPasswords() {
        // Set non-matching passwords
        controller.setPassword(VALID_PASSWORD);
        controller.setConfirmPassword(MISMATCHED_PASSWORD);
        // Verify password validation logic works
        assertFalse(controller.verifyPassword(
                new PasswordField() {{ setText(controller.getPassword()); }},
                new PasswordField() {{ setText(controller.getConfirmPassword()); }}
        ), "Password verification passed for non-matching passwords");
    }

    @Test
    void testVerifyEmail_withValidEmail() {
        // Set a valid email
        controller.setEmail(VALID_EMAIL);
        // Verify email validation logic works
        assertTrue(controller.verifyEmail(new TextField(controller.getEmail())),
                "Email validation failed for valid input");
    }

    @Test
    void testVerifyEmail_withInvalidEmail() {
        // Set an invalid email
        controller.setEmail(INVALID_EMAIL);
        // Verify email validation logic works
        assertFalse(controller.verifyEmail(new TextField(controller.getEmail())),
                "Email validation passed for invalid input");
    }


}
