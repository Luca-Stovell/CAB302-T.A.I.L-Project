package com.example.cab302tailproject.LearningCards;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays; // Added for stream operations if needed, and for robust splitting

public class LearningCardDeck {
    // Constants:
    // constants subject to change, really depends on what feels right when using the card reader
    private static final double MEDIUM = 0.6;
    private static final double HARD = 0.3;
    private static final String EMPTY_MESSAGE = "Congratulations, you have finished the deck!";

    private List<LearningCard> DeckContent = new ArrayList<>();

    // Used for testing, and nothing else right now. Probably could be used in the card creator
    // This constructor assumes the old format or a pre-parsed format.
    public LearningCardDeck(List<String[]> cardContent) {
        for (String[] strings : cardContent) {
            if (strings != null && strings.length >= 2) { // Basic validation
                DeckContent.add(new LearningCard(strings[0], strings[1]));
            } else {
                System.err.println("Warning: Skipping malformed card data in List<String[]> constructor.");
            }
        }
    }

    /**
     * Learning card deck constructor, that reads a string fetched from the database
     * in the new format:
     * Question1
     * Answer1
     * <blank line>
     * Question2
     * Answer2
     * <blank line>
     * ...
     * @param cardContent string containing all the card data as stored in the database
     */
    public LearningCardDeck(String cardContent){
        try {
            if (cardContent == null || cardContent.trim().isEmpty()) {
                System.err.println("Warning: Card content string is null or empty.");
                return;
            }

            // Split by one or more blank lines (handles Windows \r\n and Unix \n)
            // Regex: (\r?\n){2,} matches two or more newline sequences.
            String[] cardsBlocks = cardContent.trim().split("(\\r?\\n){2,}");

            for (String block : cardsBlocks) {
                if (block.trim().isEmpty()) { // Skip any genuinely empty blocks if they occur
                    continue;
                }
                // Split each block into lines. Expecting Question on line 1, Answer on line 2.
                String[] lines = block.split("\\r?\\n", 2); // Limit to 2 parts: Question, Answer

                if (lines.length == 2) {
                    String question = lines[0].trim();
                    String answer = lines[1].trim();
                    if (!question.isEmpty() && !answer.isEmpty()) {
                        DeckContent.add(new LearningCard(question, answer));
                    } else {
                        System.err.println("Warning: Skipped card due to empty question or answer after parsing block: [" + block + "]");
                    }
                } else {
                    System.err.println("Warning: Malformed card block, expected 2 lines (Question, Answer) but found " + lines.length + " in block: [" + block + "]");
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in LearningCardDeck(String cardContent) constructor: " + e.getMessage());
            e.printStackTrace(); // For detailed debugging
        }
    }

    /**
     * A function used to view the contents of a learning card deck
     * @return The top facing string of the first card in the queue, or a message if deck is empty
     */
    public String getCurrentCard() {
        if (!DeckContent.isEmpty()) {
            return DeckContent.getFirst().getCard();
        }
        return EMPTY_MESSAGE;
    }

    /**
     * toggles the output of the current card between question and answer (initial state: question)
     */
    public void flip() {
        if (!DeckContent.isEmpty()) {
            DeckContent.getFirst().setFlipped();
        }
    }

    // should probably be private, identical to easyNext
    // it's used in the unit tests
    public void next(){
        next(1.0); // Ensure this is a double
    }

    /**
     * sends the current card to a place in the deck and reduces it's lifespan depending on an input double.
     * If the card has no more life left, removes it from the deck
     * @param difficulty double that determines where the current card is sent
     */
    private void next(double difficulty) {
        if (!DeckContent.isEmpty()) {
            DeckContent.getFirst().resetFlip();
            DeckContent.getFirst().reduceCard(difficulty);
            if (DeckContent.getFirst().isActive()) {

                int newIndex = (int) (DeckContent.size() * difficulty);
                if (DeckContent.size() > 1) { // Only reposition if there are other cards
                    newIndex = Math.max(0, Math.min(newIndex, DeckContent.size() -1)); // Clamp to valid index for insertion among existing
                    LearningCard current = DeckContent.removeFirst(); // Remove first before adding
                    DeckContent.add(newIndex, current); // Add it back at the calculated position
                } else {
                }


            }
            // If the card is no longer active OR if it was the only card and its active status was checked
            if (!DeckContent.getFirst().isActive() || DeckContent.size() == 1 && !DeckContent.getFirst().isActive()) {
                DeckContent.removeFirst();
            } else if (DeckContent.size() == 1 && DeckContent.getFirst().isActive()){
            } else if (DeckContent.size() > 1 && DeckContent.getFirst().isActive()) {
            }
        }
    }
    // Revised next method based on thought process above for clarity
    private void revisedNext(double difficulty) {
        if (DeckContent.isEmpty()) {
            return;
        }
        LearningCard processedCard = DeckContent.getFirst();
        processedCard.resetFlip();
        processedCard.reduceCard(difficulty);

        DeckContent.removeFirst(); // Always remove the card from the front first

        if (processedCard.isActive()) {
            // Calculate newIndex based on the size of the deck *after* removing the card
            int newIndex = (int) (DeckContent.size() * difficulty);
            // Clamp newIndex to be within valid bounds for add(index, element) which is [0, size()]
            newIndex = Math.max(0, Math.min(newIndex, DeckContent.size()));
            DeckContent.add(newIndex, processedCard); // Add the card back at the calculated position
        }
        // If card is not active, it's already removed and not added back.
    }


    /**
     * moves the current card to the end of the deck
     */
    public String easyNext(){

        revisedNext(1.0);
        return getCurrentCard();
    }

    /**
     * moves the current card to the middle of the deck
     */
    public String mediumNext(){
        revisedNext(MEDIUM);
        return getCurrentCard();
    }


    /**
     * moves the current card close to the start of the deck
     */
    public String hardNext(){
        revisedNext(HARD);
        return getCurrentCard();
    }


}
