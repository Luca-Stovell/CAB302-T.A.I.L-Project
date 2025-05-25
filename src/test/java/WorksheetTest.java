import com.example.cab302tailproject.model.Worksheet;
import org.junit.jupiter.api.*;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

public class WorksheetTest {
    private static final String TOPIC1 = "Modern history";
    private static final String TOPIC2 = "Algebra";
    private static final String WORKSHEET_CONTENT_ONE = "**Basketball Worksheet**\n\n**Part 1: Multiple Choice Questions**\n\nChoose the correct answer for each question.\n\n1. Who is credited with inventing the game of basketball?\nA) Dr. James Naismith\nB) Michael Jordan\nC) Magic Johnson\nD) Stephen Curry\n\n2. Which part of the basketball court has the three-point line?\nA) Backboard\nB) Free throw line\nC) Mid-court line\nD) Center line\n\n3. What is the name of the rule that requires players to dribble the ball only with their fingertips?\nA) Traveling\nB) Double dribble\nC) Foul\nD) Three-second rule\n\n4. Which basketball player is known for his iconic \"Flu Game\" in the 1997 NBA Finals?\nA) Michael Jordan\nB) Kobe Bryant\nC) LeBron James\nD) Shaquille O'Neal\n\n5. What is the name of the shot in basketball where a player shoots the ball while being defended by two players who are standing next to each other, without dribbling or passing the ball to anyone?\nA) Layup\nB) Jump shot\nC) Dunk\nD) Free throw\n\n**Part 2: Short Answer Questions**\n\nWrite a short answer for each question.\n\n1. Describe the basic objective of basketball and how players score points. (approx. 50-75 words)\n\n2. Explain the concept of a \"full court press\" in basketball defense. What are the strategies used to apply this type of pressure? (approx. 50-75 words)\n\n**Part 3: Essay Question**\n\nChoose one of the following essay questions and write a well-supported response.\n\n1. Discuss the importance of teamwork and communication in basketball. How do players work together as a team, and what are some examples of effective teamwork strategies used by professional teams?\n\n2. Analyze the impact of player motivation and mindset on performance in basketball. What role does confidence, focus, and resilience play in achieving success at this level? Provide examples from your own knowledge or experience.\n\n**Grading:**\n\n* Multiple Choice Questions (Part 1): 30 points\n* Short Answer Questions (Part 2): 40 points\n* Essay Question (Part 3): 30 points";
    private static final String WORKSHEET_CONTENT_TWO = "Algebra content, which is a bit long and may include all kinds of (4352) topics and $other things & too.";
    private static final int VALID_ID1 = 12345678;
    private static final int VALID_ID2 = 62;
    private static final int VALID_ID3 = 552;
    private static final Instant VALID_DATE_TIME_1 = Instant.parse("2023-10-01T12:00:00Z");
    private static final Instant VALID_DATE_TIME_2 = Instant.parse("2026-03-10T15:51:35Z");


    private Worksheet worksheet1;
    private Worksheet worksheet2;

    @BeforeEach
    public void setUp() {
        worksheet1 = new Worksheet(TOPIC1, WORKSHEET_CONTENT_ONE, VALID_DATE_TIME_1, VALID_ID1, VALID_ID2);
        worksheet2 = new Worksheet(TOPIC2, WORKSHEET_CONTENT_TWO, VALID_ID1);
    }

    // Testing the full constructor (all parameters)
    @Test
    public void testGetTopic() {
        assertEquals(TOPIC1, worksheet1.getTopic());
    }

    @Test
    public void testSetTopic() {
        worksheet1.setTopic(TOPIC2);
        assertEquals(TOPIC2, worksheet1.getTopic());
    }

    @Test
    public void testGetContent() {
        assertEquals(WORKSHEET_CONTENT_ONE, worksheet1.getContent());
    }

    @Test
    public void testSetContent() {
        worksheet1.setContent(WORKSHEET_CONTENT_TWO);
        assertEquals(WORKSHEET_CONTENT_TWO, worksheet1.getContent());
    }

    @Test
    public void testGetTeacherID() {
        assertEquals(VALID_ID1, worksheet1.getTeacherID());
    }

    @Test
    public void testSetTeacherID(){
        worksheet1.setTeacherID(VALID_ID3);
        assertEquals(VALID_ID3, worksheet1.getTeacherID());
    }

    @Test
    public void testGetMaterialID() {
        assertEquals(VALID_ID2, worksheet1.getMaterialID());
    }

    @Test
    public void testSetMaterialID() {
        worksheet1.setMaterialID(VALID_ID1);
        assertEquals(VALID_ID1, worksheet1.getMaterialID());
    }

    @Test
    public void testGetLastModified() { assertEquals(VALID_DATE_TIME_1, worksheet1.getLastModifiedDate());    }

    @Test
    public void testSetLastModifiedDate() {
        worksheet1.setLastModifiedDate(VALID_DATE_TIME_2);
        assertEquals(VALID_DATE_TIME_2, worksheet1.getLastModifiedDate());
    }

    // Testing the 4-parameter constructor
    @Test
    public void testGetTopic_Lesson2() {
        assertEquals(TOPIC2, worksheet2.getTopic());
    }

    @Test
    public void testGetContent_Lesson2() {
        assertEquals(WORKSHEET_CONTENT_TWO, worksheet2.getContent());
    }

    @Test
    public void testGetTeacherID_Lesson2() {
        assertEquals(VALID_ID1, worksheet2.getTeacherID());
    }

    @Test
    public void testGetMaterialID_Lesson2() {
        assertEquals(0, worksheet2.getMaterialID(), "Material ID should default to 0 if not initialised with SQLite");
    }

    @Test
    public void testGetLastModified_Lesson2() {
        assertNull(worksheet2.getLastModifiedDate(), "Last modified date should default to null if not initialised with SQLite");
    }

    @Test       // Try setting a valid time to a lesson initialised with null
    public void testSetLastModified_Lesson2() {
        worksheet2.setLastModifiedDate(VALID_DATE_TIME_1);
        assertEquals(VALID_DATE_TIME_1, worksheet2.getLastModifiedDate());
    }

    @Test       // Try setting a valid material ID to a lesson initialised without one
    public void testSetMaterialID_worksheet2() {
        worksheet2.setMaterialID(VALID_ID1);
        assertEquals(VALID_ID1, worksheet2.getMaterialID());
    }

    @Test
    public void BadMaterialID() {
        assertThrows(IllegalArgumentException.class, () -> worksheet2.setMaterialID(-1));
    }

    @Test
    public void BadTeacherID() {
        assertThrows(IllegalArgumentException.class, () -> worksheet2.setTeacherID(-1));
    }
}
