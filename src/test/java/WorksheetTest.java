import com.example.cab302tailproject.model.Worksheet;
import org.junit.jupiter.api.*;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

public class WorksheetTest {
    private static final String TOPIC1 = "Modern history";
    private static final String TOPIC2 = "Algebra";
    private static final String WORKSHEET_CONTENT_ONE = "Modern history content, which is a bit long and may include all kinds of (4352) topics and $other things & too.";
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
}
