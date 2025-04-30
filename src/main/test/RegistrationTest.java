import com.example.cab302tailproject.controller.RegistrationController;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.application.Platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;


// Currently overcomplicated by using javafx stuff - might be worth updating the controller
public class RegistrationTest {

    private static final String VALID_EMAIL = "user@example.com";
    private static final String VALID_PASSWORD = "<PASSWORD>";
    private static final String INVALID_EMAIL = "invalidEmail";
    private static final String INVALID_PASSWORD = "<PASSWORD>";
    private static final String EMPTY_STRING = "";

    private RegistrationController controller;

    private TextField firstNameTextField;
    private TextField lastNameTextField;
    private TextField setEmailTextField;
    private PasswordField registrationPasswordField;
    private PasswordField registrationConfirmPasswordField;

    @BeforeEach
    void setUp() throws Exception {
        controller = new RegistrationController();

        setEmailTextField = new TextField();
        registrationPasswordField = new PasswordField();
        registrationConfirmPasswordField = new PasswordField();

        // Get private fields from the controller
        Field emailfield = RegistrationController.class.getDeclaredField("setEmailTextField");
        emailfield.setAccessible(true); // Allow access to private fields
        // assign to controller's fields
        emailfield.set(controller, setEmailTextField); // Assign mock textfield
    }

    @Test
    void testVerifyEmail_validEmail() {
        setEmailTextField.setText(VALID_EMAIL);
        assertTrue(controller.verifyEmail(), "Valid email should return true"); // TODO: update tests when database is fully functional
    }

}
