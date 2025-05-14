import com.example.cab302tailproject.model.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void constructor_withPassword_shouldSetAllFields() {
        Student student = new Student("Luca", "Stovell", "luca@example.com", "securePass");

        assertEquals("Luca", student.getFirstName());
        assertEquals("Stovell", student.getLastName());
        assertEquals("luca@example.com", student.getEmail());
        assertEquals("securePass", student.getPassword());
    }

    @Test
    void constructor_withoutPassword_shouldSetNullPassword() {
        Student student = new Student("Luca", "Stovell", "luca@example.com");

        assertEquals("Luca", student.getFirstName());
        assertEquals("Stovell", student.getLastName());
        assertEquals("luca@example.com", student.getEmail());
        assertNull(student.getPassword());
    }

    @Test
    void getAndSetStudentID_shouldWorkCorrectly() {
        Student student = new Student("Luca", "Stovell", "luca@example.com");
        student.setStudentID(1234);

        assertEquals(1234, student.getStudentID());
    }

    @Test
    void getAndSetTeacherEmail_shouldWorkCorrectly() {
        Student student = new Student("Luca", "Stovell", "luca@example.com");
        student.setTeacherEmail("teacher@example.com");

        assertEquals("teacher@example.com", student.getTeacherEmail());
    }

    @Test
    void getAndSetClassroomID_shouldWorkCorrectly() {
        Student student = new Student("Luca", "Stovell", "luca@example.com");
        student.setClassroomID(5678);

        assertEquals(5678, student.getClassroomID());
    }

    @Test
    void getFullName_shouldReturnFirstNameAndLastName() {
        Student student = new Student("Luca", "Stovell", "luca@example.com");

        assertEquals("Luca Stovell", student.getFullName());
    }

    @Test
    void toString_shouldReturnFullName() {
        Student student = new Student("Luca", "Stovell", "luca@example.com");

        assertEquals("Luca Stovell", student.toString());
    }
}
