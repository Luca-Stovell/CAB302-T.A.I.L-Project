import com.example.cab302tailproject.model.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MaterialTest {
    private static final String TOPIC1 = "Modern history";
    private static final String TOPIC2 = "Algebra";
    private static final String WORKSHEET_CONTENT = "**Basketball Worksheet**\n\n**Part 1: Multiple Choice Questions**\n\nChoose the correct answer for each question.\n\n1. Who is credited with inventing the game of basketball?\nA) Dr. James Naismith\nB) Michael Jordan\nC) Magic Johnson\nD) Stephen Curry\n\n2. Which part of the basketball court has the three-point line?\nA) Backboard\nB) Free throw line\nC) Mid-court line\nD) Center line\n\n3. What is the name of the rule that requires players to dribble the ball only with their fingertips?\nA) Traveling\nB) Double dribble\nC) Foul\nD) Three-second rule\n\n4. Which basketball player is known for his iconic \"Flu Game\" in the 1997 NBA Finals?\nA) Michael Jordan\nB) Kobe Bryant\nC) LeBron James\nD) Shaquille O'Neal\n\n5. What is the name of the shot in basketball where a player shoots the ball while being defended by two players who are standing next to each other, without dribbling or passing the ball to anyone?\nA) Layup\nB) Jump shot\nC) Dunk\nD) Free throw\n\n**Part 2: Short Answer Questions**\n\nWrite a short answer for each question.\n\n1. Describe the basic objective of basketball and how players score points. (approx. 50-75 words)\n\n2. Explain the concept of a \"full court press\" in basketball defense. What are the strategies used to apply this type of pressure? (approx. 50-75 words)\n\n**Part 3: Essay Question**\n\nChoose one of the following essay questions and write a well-supported response.\n\n1. Discuss the importance of teamwork and communication in basketball. How do players work together as a team, and what are some examples of effective teamwork strategies used by professional teams?\n\n2. Analyze the impact of player motivation and mindset on performance in basketball. What role does confidence, focus, and resilience play in achieving success at this level? Provide examples from your own knowledge or experience.\n\n**Grading:**\n\n* Multiple Choice Questions (Part 1): 30 points\n* Short Answer Questions (Part 2): 40 points\n* Essay Question (Part 3): 30 points";
    private static final String LESSON_CONTENT = "**Lesson Title:** Exploring Magnets\n\n**Grade Level:** 3-6\n\n**Objectives:**\n\n* Students will understand the basic properties of magnets.\n* Students will be able to identify different types of magnets and their characteristics.\n* Students will learn how to create a magnetic field using various materials.\n\n**Materials:**\n\n* Magnets (strong and weak)\n* Bar magnets\n* Iron filings\n* Copper wire\n* Needle magnet\n* Paper clips\n* Scissors\n* Pencils\n* Chart paper\n\n**Lesson Plan:**\n\n**Introduction (10 minutes)**\n\n1. Begin by asking students if they have ever seen or interacted with magnets before.\n2. Show a few examples of different types of magnets, such as bar magnets, iron filings, and needle magnets.\n3. Explain that magnets are objects that produce a force when placed near other magnets.\n\n**Direct Instruction (15 minutes)**\n\n1. Show students how to create a magnetic field using various materials.\n2. Demonstrate the concept of electromagnetism by using copper wire to create a simple electric current.\n3. Explain how magnets can be used to attract and repel objects.\n4. Use bar magnets to demonstrate the strength of different types of magnets.\n\n**Guided Practice (15 minutes)**\n\n1. Distribute iron filings to students and have them spread it out in a circle around the magnets.\n2. Ask students to observe what happens when they try to move the needle magnet over the iron filings without touching it.\n3. Have students record their observations on chart paper.\n\n**Independent Practice (20 minutes)**\n\n1. Provide each student with a few materials, such as paper clips and scissors, and ask them to create a simple magnetic system using these materials.\n2. Encourage students to experiment and observe how the magnets interact with each other.\n\n**Assessment:**\n\n* Observe students during guided and independent practice activities to assess their understanding of magnet properties and behavior.\n* Review student charts for accuracy and completeness.\n\n**Conclusion (5 minutes)**\n\n1. Summarize the key concepts learned in the lesson, including the basic properties of magnets and how they interact with each other.\n2. Ask students to share any questions or observations they have about magnets.\n\n**Extensions:**\n\n* Create a magnet museum in the classroom or school hallway.\n* Conduct further experiments to explore the properties of different types of magnets.\n* Research real-world applications of magnets, such as electric motors and magnetic resonance imaging (MRI) machines.\n\n**Interactive Fun Activities:**\n\n* \"Pin the Magnets on the Board\": Adapt the classic game to use magnets instead of paper clips.\n* \"Magnet Scavenger Hunt\": Hide different types of magnets around the classroom or school hallway and have students find them using a list of characteristics.\n* \"Magnetic Maze\": Create a maze with magnetic shapes and challenge students to navigate through it without touching the walls.";
    private static final int VALID_ID1 = 12345678;
    private static final int VALID_ID2 = 62;
    private static final int VALID_ID3 = 552;
    private static final Instant VALID_DATE_TIME_1 = Instant.parse("2023-10-01T12:00:00Z");
    private static final Instant VALID_DATE_TIME_2 = Instant.parse("2026-03-10T15:51:35Z");
    private static final String TYPE_WS = "worksheet";
    private static final String TYPE_LP = "lesson";
    private static final String TYPE_LC = "learningCard";
    private static final int WEEK1 = 1;


    private Material material1;
    private Material material2;
    private Material material3;

    @BeforeEach
    public void setUp() {
        material1 = new Material(TOPIC1, WORKSHEET_CONTENT, VALID_ID3, TYPE_WS, VALID_ID1, VALID_ID2, WEEK1, VALID_DATE_TIME_1);
        material2 = new Material(TOPIC2, LESSON_CONTENT, VALID_ID2, TYPE_LP);
        material3 = new Material(VALID_ID1, TYPE_LC);
    }
    // Testing the full constructor (all parameters)
    @Test
    public void testGetTopic() {
        assertEquals(TOPIC1, material1.getTopic());
    }

    @Test
    public void testSetTopic() {
        material1.setTopic(TOPIC2);
        assertEquals(TOPIC2, material1.getTopic());
    }

    @Test
    public void testGetContent() {
        assertEquals(WORKSHEET_CONTENT, material1.getContent());
    }

    @Test
    public void testSetContent() {
        material1.setContent(LESSON_CONTENT);
        assertEquals(LESSON_CONTENT, material1.getContent());
    }

    @Test
    public void testGetTeacherID() {
        assertEquals(VALID_ID3, material1.getTeacherID());
    }

    @Test
    public void testSetTeacherID(){
        material1.setTeacherID(VALID_ID2);
        assertEquals(VALID_ID2, material1.getTeacherID());
    }

    @Test
    public void testGetMaterialID() {
        assertEquals(VALID_ID1, material1.getMaterialID());
    }

    @Test
    public void testSetMaterialID() {
        material1.setMaterialID(VALID_ID1);
        assertEquals(VALID_ID1, material1.getMaterialID());
    }

    @Test
    public void testGetLastModified() { assertEquals(VALID_DATE_TIME_1, material1.getLastModifiedDate());    }

    @Test
    public void testSetLastModifiedDate() {
        material1.setLastModifiedDate(VALID_DATE_TIME_2);
        assertEquals(VALID_DATE_TIME_2, material1.getLastModifiedDate());
    }

    // Testing the 4-parameter constructor
    @Test
    public void testGetTopic_Lesson2() {
        assertEquals(TOPIC2, material2.getTopic());
    }

    @Test
    public void testGetContent_Lesson2() {
        assertEquals(LESSON_CONTENT, material2.getContent());
    }

    @Test
    public void testGetTeacherID_Lesson2() {
        assertEquals(VALID_ID2, material2.getTeacherID());
    }

    @Test
    public void testGetMaterialID_Lesson2() {
        assertEquals(0, material2.getMaterialID(), "Material ID should default to 0 if not initialised with SQLite");
    }

    @Test
    public void testGetLastModified_Lesson2() {
        assertNull(material2.getLastModifiedDate(), "Last modified date should default to null if not initialised with SQLite");
    }

    @Test       // Try setting a valid time to a lesson initialised with null
    public void testSetLastModified_Lesson2() {
        material2.setLastModifiedDate(VALID_DATE_TIME_1);
        assertEquals(VALID_DATE_TIME_1, material2.getLastModifiedDate());
    }

    @Test       // Try setting a valid material ID to a lesson initialised without one
    public void testSetMaterialID_Lesson2() {
        material2.setMaterialID(VALID_ID1);
        assertEquals(VALID_ID1, material2.getMaterialID());
    }

    @Test
    void testConstructorWithMinimalArguments() {
        Material material = new Material(1, "lesson");
        assertEquals(1, material.getMaterialID());
        assertEquals("lesson", material.getMaterialType());
    }

    @Test
    void testConstructorWithFullArguments() {
        Instant now = Instant.now();
        Material material = new Material("Math", "Algebra content", 101, "worksheet", 1, 123, 12, now);
        assertEquals("Math", material.getTopic());
        assertEquals("Algebra content", material.getContent());
        assertEquals(101, material.getTeacherID());
        assertEquals("worksheet", material.getMaterialType());
        assertEquals(1, material.getMaterialID());
        assertEquals(123, material.getClassroomID());
        assertEquals(12, material.getWeek());
        assertEquals(now, material.getLastModifiedDate());
    }

    @Test
    void testSetClassroomID() {
        Material material = new Material(1, "lesson");
        material.setClassroomID(1001);
        assertEquals(1001, material.getClassroomID());
    }


    @Test
    void testSetMaterialType() {
        Material material = new Material(1, "lesson");
        material.setMaterialType("worksheet");
        assertEquals("worksheet", material.getMaterialType());
    }

    @Test
    void testSetWeekWithValidValue() {
        Material material = new Material(1, "lesson");
        material.setWeek(10);
        assertEquals(10, material.getWeek());
    }

    @Test
    public void BadMaterialID() {
        assertThrows(IllegalArgumentException.class, () -> material3.setMaterialID(-1));
    }

    @Test
    public void BadMaterialType() {
        assertThrows(IllegalArgumentException.class, () -> material3.setMaterialType("badType"));
    }

    @Test
    public void BadClassroomID() {
        assertThrows(IllegalArgumentException.class, () -> material3.setClassroomID(-1));
    }

    @Test
    public void BadWeek() {
        assertThrows(IllegalArgumentException.class, () -> material3.setWeek(-1));
    }

    @Test
    public void BadTeacherID() {
        assertThrows(IllegalArgumentException.class, () -> material3.setTeacherID(-1));
    }
}