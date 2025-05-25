import com.example.cab302tailproject.model.Lesson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

public class LessonTest {
    private static final String TOPIC1 = "Modern history";
    private static final String TOPIC2 = "Algebra";
    private static final String LESSON_CONTENT_ONE = "**Lesson Title:** Exploring Magnets\n\n**Grade Level:** 3-6\n\n**Objectives:**\n\n* Students will understand the basic properties of magnets.\n* Students will be able to identify different types of magnets and their characteristics.\n* Students will learn how to create a magnetic field using various materials.\n\n**Materials:**\n\n* Magnets (strong and weak)\n* Bar magnets\n* Iron filings\n* Copper wire\n* Needle magnet\n* Paper clips\n* Scissors\n* Pencils\n* Chart paper\n\n**Lesson Plan:**\n\n**Introduction (10 minutes)**\n\n1. Begin by asking students if they have ever seen or interacted with magnets before.\n2. Show a few examples of different types of magnets, such as bar magnets, iron filings, and needle magnets.\n3. Explain that magnets are objects that produce a force when placed near other magnets.\n\n**Direct Instruction (15 minutes)**\n\n1. Show students how to create a magnetic field using various materials.\n2. Demonstrate the concept of electromagnetism by using copper wire to create a simple electric current.\n3. Explain how magnets can be used to attract and repel objects.\n4. Use bar magnets to demonstrate the strength of different types of magnets.\n\n**Guided Practice (15 minutes)**\n\n1. Distribute iron filings to students and have them spread it out in a circle around the magnets.\n2. Ask students to observe what happens when they try to move the needle magnet over the iron filings without touching it.\n3. Have students record their observations on chart paper.\n\n**Independent Practice (20 minutes)**\n\n1. Provide each student with a few materials, such as paper clips and scissors, and ask them to create a simple magnetic system using these materials.\n2. Encourage students to experiment and observe how the magnets interact with each other.\n\n**Assessment:**\n\n* Observe students during guided and independent practice activities to assess their understanding of magnet properties and behavior.\n* Review student charts for accuracy and completeness.\n\n**Conclusion (5 minutes)**\n\n1. Summarize the key concepts learned in the lesson, including the basic properties of magnets and how they interact with each other.\n2. Ask students to share any questions or observations they have about magnets.\n\n**Extensions:**\n\n* Create a magnet museum in the classroom or school hallway.\n* Conduct further experiments to explore the properties of different types of magnets.\n* Research real-world applications of magnets, such as electric motors and magnetic resonance imaging (MRI) machines.\n\n**Interactive Fun Activities:**\n\n* \"Pin the Magnets on the Board\": Adapt the classic game to use magnets instead of paper clips.\n* \"Magnet Scavenger Hunt\": Hide different types of magnets around the classroom or school hallway and have students find them using a list of characteristics.\n* \"Magnetic Maze\": Create a maze with magnetic shapes and challenge students to navigate through it without touching the walls.";
    private static final String LESSON_CONTENT_TWO = "Algebra content, which is a bit long and may include all kinds of (4352) topics and $other things & too.";
    private static final int VALID_ID1 = 12345678;
    private static final int VALID_ID2 = 62;
    private static final int VALID_ID3 = 552;
    private static final Instant VALID_DATE_TIME_1 = Instant.parse("2023-10-01T12:00:00Z");
    private static final Instant VALID_DATE_TIME_2 = Instant.parse("2026-03-10T15:51:35Z");


    private Lesson lesson1;
    private Lesson lesson2;

    @BeforeEach
    public void setUp() {
        lesson1 = new Lesson(TOPIC1, LESSON_CONTENT_ONE, VALID_DATE_TIME_1, VALID_ID1, VALID_ID2);
        lesson2 = new Lesson(TOPIC2, LESSON_CONTENT_TWO, VALID_ID1);
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
    public void testGetMaterialID() {
        assertEquals(VALID_ID2, lesson1.getMaterialID());
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

    @Test
    public void BadMaterialID() {
        assertThrows(IllegalArgumentException.class, () -> lesson2.setMaterialID(-1));
    }

    @Test
    public void BadTeacherID() {
        assertThrows(IllegalArgumentException.class, () -> lesson2.setTeacherID(-1));
    }
}
