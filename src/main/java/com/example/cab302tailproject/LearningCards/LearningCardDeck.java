package com.example.cab302tailproject.LearningCards;

import java.util.ArrayList;
import java.util.List;
// import java.util.Arrays; // Not strictly needed for current implementation

public class LearningCardDeck {
    private static final double MEDIUM = 0.5;
    private static final double HARD = 0.3;
    public static final String EMPTY_MESSAGE = "Congratulations, you have finished the deck!";

    private List<LearningCard> DeckContent = new ArrayList<>();

    /**
     * Constructor that takes a list of String arrays to create cards.
     * Each String array should contain [question, answer].
     * @param cardContent List of String arrays representing cards.
     */
    public LearningCardDeck(List<String[]> cardContent) {
        if (cardContent == null) {
            System.err.println("Warning: cardContent List<String[]> is null. Initializing empty deck.");
            return;
        }
        for (String[] strings : cardContent) {
            if (strings != null && strings.length >= 2) {
                // Ensure question and answer are not null or empty before creating card
                String question = strings[0];
                String answer = strings[1];
                if (question != null && !question.trim().isEmpty() && answer != null && !answer.trim().isEmpty()) {
                    DeckContent.add(new LearningCard(question, answer));
                } else {
                    System.err.println("Warning: Skipping card due to empty question or answer in List<String[]> constructor. Data: [" + (question != null ? question : "NULL_Q") + ", " + (answer != null ? answer : "NULL_A") + "]");
                }
            } else {
                System.err.println("Warning: Skipping malformed card data (null or insufficient length) in List<String[]> constructor.");
            }
        }
    }

    /**
     * Constructor that takes a single string where cards are separated by
     * double newlines, and question/answer within a card are separated by a single newline.
     * @param cardContent String containing all card data.
     */
    public LearningCardDeck(String cardContent){
        try {
            if (cardContent == null || cardContent.trim().isEmpty()) {
                System.err.println("Warning: Card content string is null or empty. Initializing empty deck.");
                return; // Initialize an empty deck
            }
            // Regex to split by two or more newlines (Windows or Unix style)
            String[] cardsBlocks = cardContent.trim().split("(\\r?\\n){2,}");

            for (String block : cardsBlocks) {
                if (block.trim().isEmpty()) {
                    continue; // Skip empty blocks
                }
                // Split into 2 parts: question and answer
                String[] lines = block.split("\\r?\\n", 2);

                if (lines.length == 2) {
                    String question = lines[0].trim();
                    String answer = lines[1].trim();
                    if (!question.isEmpty() && !answer.isEmpty()) {
                        DeckContent.add(new LearningCard(question, answer));
                    } else {
                        System.err.println("Warning: Skipped card due to empty question or answer after parsing block: [" + block + "]");
                    }
                } else {
                    // Log if a block doesn't split into question and answer
                    System.err.println("Warning: Malformed card block, expected 2 lines (Question, Answer) but found " + lines.length + " in block: [" + block + "]");
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in LearningCardDeck(String cardContent) constructor: " + e.getMessage());
            e.printStackTrace();
            // Ensure DeckContent is at least initialized if an error occurs
            if (DeckContent == null) {
                DeckContent = new ArrayList<>();
            }
        }
    }

    /**
     * Gets the content of the current card (question or answer based on flip state).
     * @return The current card's content or EMPTY_MESSAGE if deck is empty.
     */
    public String getCurrentCard() {
        if (!DeckContent.isEmpty()) {
            return DeckContent.getFirst().getCard();
        }
        return EMPTY_MESSAGE;
    }

    /**
     * Gets the question text of the current card.
     * @return The question text, or null if deck is empty.
     */
    public String getCurrentQuestionText() {
        if (!DeckContent.isEmpty()) {
            return DeckContent.getFirst().getQuestion();
        }
        return null; // Or a specific "no question" message
    }

    /**
     * Checks if the current card is flipped.
     * @return true if flipped, false otherwise or if deck is empty.
     */
    public boolean isCurrentCardFlipped() {
        if (!DeckContent.isEmpty()) {
            return DeckContent.getFirst().isFlipped();
        }
        return false;
    }

    /**
     * Checks if the deck is empty.
     * @return true if deck has no cards, false otherwise.
     */
    public boolean isEmpty() {
        return DeckContent.isEmpty();
    }

    /**
     * Flips the current card. Does nothing if the deck is empty.
     */
    public void flip() {
        if (!DeckContent.isEmpty()) {
            DeckContent.getFirst().setFlipped();
        }
    }

    /**
     * Helper method for Easy, Medium, Hard buttons.
     * Processes the current card based on difficulty, removes it,
     * and re-inserts it if it's still active.
     * @param difficulty The difficulty rating for the card.
     */
    private void revisedNext(double difficulty) {
        if (DeckContent.isEmpty()) {
            return;
        }
        LearningCard processedCard = DeckContent.getFirst();
        processedCard.resetFlip(); // Reset flip state before potential re-insertion
        processedCard.reduceCard(difficulty); // Update card's internal state (e.g., correctStreak)

        DeckContent.removeFirst(); // Remove card from the front

        if (processedCard.isActive()) { // Check if card should be re-inserted
            int newIndex = (int) (DeckContent.size() * difficulty);
            // Ensure index is within bounds [0, DeckContent.size()]
            newIndex = Math.max(0, Math.min(newIndex, DeckContent.size()));
            DeckContent.add(newIndex, processedCard); // Re-insert the card
        }
        // If card is not active, it's effectively removed from the session for these buttons
    }

    /**
     * Processes the card as "Easy". Uses spaced repetition logic.
     * @return The content of the next card or empty message.
     */
    public String easyNext(){
        revisedNext(1.0); // High difficulty means easy, likely to master
        return getCurrentCard();
    }

    /**
     * Processes the card as "Medium". Uses spaced repetition logic.
     * @return The content of the next card or empty message.
     */
    public String mediumNext(){
        revisedNext(MEDIUM);
        return getCurrentCard();
    }

    /**
     * Processes the card as "Hard". Uses spaced repetition logic.
     * @return The content of the next card or empty message.
     */
    public String hardNext(){
        revisedNext(HARD); // Low difficulty means hard, likely to see again sooner
        return getCurrentCard();
    }
}
