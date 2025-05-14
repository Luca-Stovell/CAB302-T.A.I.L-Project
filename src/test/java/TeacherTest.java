import com.example.cab302tailproject.model.Classroom;
import com.example.cab302tailproject.model.Student;
import com.example.cab302tailproject.model.Teacher;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeacherTest {

    @Test
    void constructor_shouldInitializeUserFieldsAndEmptyLists() {
        Teacher teacher = new Teacher("Luca", "Stovell", "luca@example.com", "securePass");

        assertEquals("Luca", teacher.getFirstName());
        assertEquals("Stovell", teacher.getLastName());
        assertEquals("luca@example.com", teacher.getEmail());
        assertEquals("securePass", teacher.getPassword());

        assertNotNull(teacher.getStudents());
        assertTrue(teacher.getStudents().isEmpty());

        assertNotNull(teacher.getClassrooms());
        assertTrue(teacher.getClassrooms().isEmpty());
    }

    @Test
    void addClassroom_shouldAddClassroomToList() {
        Teacher teacher = new Teacher("Luca", "Stovell", "luca@example.com", "securePass");
        Classroom classroom = new Classroom("luca@example.com");

        teacher.addClassroom(classroom);

        assertTrue(teacher.getClassrooms().contains(classroom));
        assertEquals(1, teacher.getClassrooms().size());
    }

    @Test
    void setAndGetStudents_shouldUpdateListCorrectly() {
        Teacher teacher = new Teacher("Luca", "Stovell", "luca@example.com", "securePass");
        List<Student> newStudents = new ArrayList<>();
        newStudents.add(new Student("Alice", "Smith", "alice@example.com"));

        teacher.setStudents(newStudents);

        assertEquals(1, teacher.getStudents().size());
        assertEquals("Alice", teacher.getStudents().get(0).getFirstName());
    }

    @Test
    void setAndGetClassrooms_shouldUpdateListCorrectly() {
        Teacher teacher = new Teacher("Luca", "Stovell", "luca@example.com", "securePass");
        List<Classroom> newClassrooms = new ArrayList<>();
        Classroom c1 = new Classroom("luca@example.com");
        newClassrooms.add(c1);

        teacher.setClassrooms(newClassrooms);

        assertEquals(1, teacher.getClassrooms().size());
        assertEquals(c1, teacher.getClassrooms().get(0));
    }
}
