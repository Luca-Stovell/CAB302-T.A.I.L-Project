import com.example.cab302tailproject.LearningCards.LearningCardDeck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LearningCardDeckTest {
// Things to test: parser, current/next functionality, button shuffling (queue)
    private LearningCardDeck deck;

    @BeforeEach
    public void setup(){
        deck = new LearningCardDeck(
                new ArrayList<>() {
                    {
                        add(new String[]{"question1", "answer1"});
                        add(new String[]{"question2", "answer2"});
                        add(new String[]{"question3", "answer3"});
                        add(new String[]{"question4", "answer4"});
                        add(new String[]{"question5", "answer5"});
                        add(new String[]{"question6", "answer6"});
                        add(new String[]{"question7", "answer7"});
                        add(new String[]{"question8", "answer8"});
                    }
                }
        );

    }

    @Test
    public void testTurnStringIntoReadableFormat(){
        String input = "question:answer,question2:answer2,question3:answer3";
        List<String[]> expected = new ArrayList<String[]>() {
            {
                add(new String[]{"question", "answer"});
                add(new String[]{"question2", "answer2"});
                add(new String[]{"question3", "answer3"});
            }
        };
        // This constructor is the thing being tested
        LearningCardDeck actual = new LearningCardDeck(input);
        // This seems to be dependent on too many things
        for (int i = 0; i < 3; i++){
            assertEquals(expected.get(i)[0], actual.getCurrentCard());
            actual.flip();
            assertEquals(expected.get(i)[1], actual.getCurrentCard());
            actual.next();
        }

    }

    @Test
    public void testDefaultCardOutput(){
        String actual = deck.getCurrentCard();
        assertEquals("question1", actual);
    }

    @Test
    public void testCardOutputAfterFlip(){
        deck.flip();
        String actual = deck.getCurrentCard();
        assertEquals("answer1", actual);
    }

    @Test
    public void testFlipMultipleTimes(){
        deck.flip();
        deck.flip();
        assertEquals("question1", deck.getCurrentCard());
        deck.flip();
        assertEquals("answer1", deck.getCurrentCard());
    }

    @Test
    public void testCardOutputAfterDefaultNext(){
        deck.next();
        String actual = deck.getCurrentCard();
        assertEquals("question2", actual);
    }

    @Test
    public void testMediumNext(){
        deck.next(0.5);
        for (int i = 0; i < 3; i++) {
            deck.next();
        }
        assertEquals("question1", deck.getCurrentCard());
    }

    @Test
    public void testFlipStatusResetsProperly(){
        deck.flip();
        deck.next(0); // probably shouldn't be able to do this in the real program
        assertEquals("question1", deck.getCurrentCard());
    }


}
