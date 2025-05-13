import com.example.cab302tailproject.model.Lesson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

public class LessonTest {
    private static final String TOPIC1 = "Modern history";
    private static final String TOPIC2 = "Algebra";
    private static final String INVALID_TOPIC = null;
    private static final String LESSON_CONTENT_ONE = "Modern history content, which is a bit long and may include all kinds of (4352) topics and $other things & too.";
    private static final String LESSON_CONTENT_TWO = "Algebra content, which is a bit long and may include all kinds of (4352) topics and $other things & too.";
    private static final String INVALID_CONTENT = null;
    private static final int VALID_ID1 = 12345678;
    private static final int VALID_ID2 = 62;
    private static final int VALID_ID3 = 552;
    private static final int INVALID_ID = -1;
    private static final Instant VALID_DATE_TIME_1 = Instant.parse("2023-10-01T12:00:00Z");
    private static final Instant VALID_DATE_TIME_2 = Instant.parse("2026-03-10T15:51:35Z");


    private Lesson lesson1;
    private Lesson lesson2;

    @BeforeEach
    public void setUp() {
        lesson1 = new Lesson(TOPIC1, LESSON_CONTENT_ONE, VALID_DATE_TIME_1, VALID_ID1, VALID_ID2, VALID_ID3);
        lesson2 = new Lesson(TOPIC2, LESSON_CONTENT_TWO, VALID_ID1, VALID_ID2);
    }

    // Testing the full constructor (all parameters)
    @Test
    public void testGetTopic() {
        assertEquals(TOPIC1, lesson1.getTopic());
    }

    @Test
    public void testSetTopic() {
        lesson1.setTopic(TOPIC2);
        assertEquals(TOPIC2, lesson1.getTopic());
    }

    @Test
    public void testGetContent() {
        assertEquals(LESSON_CONTENT_ONE, lesson1.getContent());
    }

    @Test
    public void testSetContent() {
        lesson1.setContent(LESSON_CONTENT_TWO);
        assertEquals(LESSON_CONTENT_TWO, lesson1.getContent());
    }

    @Test
    public void testGetTeacherID() {
        assertEquals(VALID_ID1, lesson1.getTeacherID());
    }

    @Test
    public void testSetTeacherID(){
        lesson1.setTeacherID(VALID_ID3);
        assertEquals(VALID_ID3, lesson1.getTeacherID());
    }

    @Test
    public void testGetClassroomID() {
        assertEquals(VALID_ID2, lesson1.getClassroomID());
    }

    @Test
    public void testSetClassroomID(){
        lesson1.setClassroomID(VALID_ID3);
        assertEquals(VALID_ID3, lesson1.getClassroomID());
    }

    @Test
    public void testGetMaterialID() {
        assertEquals(VALID_ID3, lesson1.getMaterialID());
    }

    @Test
    public void testSetMaterialID() {
        lesson1.setMaterialID(VALID_ID1);
        assertEquals(VALID_ID1, lesson1.getMaterialID());
    }

    @Test
    public void testGetLastModified() { assertEquals(VALID_DATE_TIME_1, lesson1.getLastModifiedDate());    }

    @Test
    public void testSetLastModifiedDate() {
        lesson1.setLastModifiedDate(VALID_DATE_TIME_2);
        assertEquals(VALID_DATE_TIME_2, lesson1.getLastModifiedDate());
    }

    // Testing the 4-parameter constructor
    @Test
    public void testGetTopic_Lesson2() {
        assertEquals(TOPIC2, lesson2.getTopic());
    }

    @Test
    public void testGetContent_Lesson2() {
        assertEquals(LESSON_CONTENT_TWO, lesson2.getContent());
    }

    @Test
    public void testGetTeacherID_Lesson2() {
        assertEquals(VALID_ID1, lesson2.getTeacherID());
    }

    @Test
    public void testGetClassroomID_Lesson2() {
        assertEquals(VALID_ID2, lesson2.getClassroomID());
    }

    @Test
    public void testGetMaterialID_Lesson2() {
        assertEquals(0, lesson2.getMaterialID(), "Material ID should default to 0 if not initialised with SQLite");
    }

    @Test
    public void testGetLastModified_Lesson2() {
        assertNull(lesson2.getLastModifiedDate(), "Last modified date should default to null if not initialised with SQLite");
    }

    @Test       // Try setting a valid time to a lesson initialised with null
    public void testSetLastModified_Lesson2() {
        lesson2.setLastModifiedDate(VALID_DATE_TIME_1);
        assertEquals(VALID_DATE_TIME_1, lesson2.getLastModifiedDate());
    }

    @Test       // Try setting a valid material ID to a lesson initialised without one
    public void testSetMaterialID_Lesson2() {
        lesson2.setMaterialID(VALID_ID1);
        assertEquals(VALID_ID1, lesson2.getMaterialID());
    }

    // Test for invalid values

    @Test
    public void testSetTopic_BadValue() {
        lesson1.setTopic(TOPIC1);
        lesson1.setTopic(INVALID_TOPIC);
        assertNotEquals(INVALID_TOPIC, lesson1.getTopic());
    }

    @Test
    public void testSetContent_BadValue() {
        lesson1.setContent(LESSON_CONTENT_ONE);
        lesson1.setContent(INVALID_CONTENT);
        assertNotEquals(INVALID_CONTENT, lesson1.getContent());
    }

    @Test
    public void testSetTeacherID_BadValue() {
        lesson1.setTeacherID(VALID_ID1);
        lesson1.setTeacherID(INVALID_ID);
        assertNotEquals(INVALID_ID, lesson1.getTeacherID());
    }

    @Test
    public void testSetClassroomID_BadValue() {
        lesson1.setClassroomID(VALID_ID1);
        lesson1.setClassroomID(INVALID_ID);
        assertNotEquals(INVALID_ID, lesson1.getClassroomID());
    }
}
