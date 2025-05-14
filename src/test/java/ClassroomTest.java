import com.example.cab302tailproject.*;
import com.example.cab302tailproject.model.Classroom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.cab302tailproject.model.Classroom;
import com.example.cab302tailproject.model.Student;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClassroomTest {

    @BeforeEach
    void clearClassrooms() {
        // Clear the static list before each test for isolation
        Classroom.getClassrooms().clear();
    }

    @Test
    void constructor_shouldSetTeacherAndAddToStaticList() {
        Classroom classroom = new Classroom("teacher@example.com");

        assertEquals("teacher@example.com", classroom.getTeacher());
        assertTrue(Classroom.getClassrooms().contains(classroom));
        assertEquals(1, Classroom.getClassrooms().size());
    }

    @Test
    void setAndGetClassroomID_shouldWorkCorrectly() {
        Classroom classroom = new Classroom("teacher@example.com");
        classroom.setClassroomID(42);

        assertEquals(42, classroom.getClassroomID());
    }

    @Test
    void setAndGetTeacher_shouldUpdateCorrectly() {
        Classroom classroom = new Classroom("initial@example.com");
        classroom.setTeacher("updated@example.com");

        assertEquals("updated@example.com", classroom.getTeacher());
    }

    @Test
    void addStudent_shouldIncludeStudentInList() {
        Classroom classroom = new Classroom("teacher@example.com");
        Student student = new Student("Alice", "Smith", "alice@example.com");

        classroom.addStudent(student);

        assertTrue(classroom.getStudents().contains(student));
    }

    @Test
    void removeStudent_shouldRemoveStudentFromList() {
        Classroom classroom = new Classroom("teacher@example.com");
        Student student = new Student("Bob", "Brown", "bob@example.com");

        classroom.addStudent(student);
        classroom.removeStudent(student);

        assertFalse(classroom.getStudents().contains(student));
    }

    @Test
    void getClassrooms_shouldReturnStaticList() {
        Classroom c1 = new Classroom("teacher1@example.com");
        Classroom c2 = new Classroom("teacher2@example.com");

        List<Classroom> classrooms = Classroom.getClassrooms();

        assertEquals(2, classrooms.size());
        assertTrue(classrooms.contains(c1));
        assertTrue(classrooms.contains(c2));
    }

    @Test
    void toString_shouldIncludeClassroomID() {
        Classroom classroom = new Classroom("teacher@example.com");
        classroom.setClassroomID(101);

        assertEquals("Classroom 101", classroom.toString());
    }
}


