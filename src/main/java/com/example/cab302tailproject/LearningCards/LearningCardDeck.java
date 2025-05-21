package com.example.cab302tailproject.LearningCards;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class LearningCardDeck {
    private static final double MEDIUM = 0.5;
    private static final double HARD = 0.3;
    public static final String EMPTY_MESSAGE = "Congratulations, you have finished the deck!";

    private List<LearningCard> DeckContent = new ArrayList<>();

    public LearningCardDeck(List<String[]> cardContent) {
        for (String[] strings : cardContent) {
            if (strings != null && strings.length >= 2) {
                DeckContent.add(new LearningCard(strings[0], strings[1]));
            } else {
                System.err.println("Warning: Skipping malformed card data in List<String[]> constructor.");
            }
        }
    }

    public LearningCardDeck(String cardContent){
        try {
            if (cardContent == null || cardContent.trim().isEmpty()) {
                System.err.println("Warning: Card content string is null or empty.");
                return;
            }
            String[] cardsBlocks = cardContent.trim().split("(\\r?\\n){2,}");

            for (String block : cardsBlocks) {
                if (block.trim().isEmpty()) {
                    continue;
                }
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
                    System.err.println("Warning: Malformed card block, expected 2 lines (Question, Answer) but found " + lines.length + " in block: [" + block + "]");
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in LearningCardDeck(String cardContent) constructor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getCurrentCard() {
        if (!DeckContent.isEmpty()) {
            return DeckContent.getFirst().getCard();
        }
        return EMPTY_MESSAGE;
    }

    public String getCurrentQuestionText() {
        if (!DeckContent.isEmpty()) {
            return DeckContent.getFirst().getQuestion();
        }
        return null;
    }

    public boolean isCurrentCardFlipped() {
        if (!DeckContent.isEmpty()) {
            return DeckContent.getFirst().isFlipped();
        }
        return false;
    }

    public boolean isEmpty() {
        return DeckContent.isEmpty();
    }


    public void flip() {
        if (!DeckContent.isEmpty()) {
            DeckContent.getFirst().setFlipped();
        }
    }

    /**
     * Advances to the next card state, typically by moving the current card to the end of the deck.
     * This method is public and intended for use by tests or other parts of the application
     * that need a simple "next" operation without specifying difficulty.
     */
    public void next(){ // This is the public next() method your tests are looking for
        revisedNext(1.0); // Delegates to revisedNext, treating it as "easy"
    }

    /**
     * This was the original private next(double difficulty) method.
     * It's preserved here as per instructions not to remove methods but is not directly
     * called by easyNext, mediumNext, hardNext if they use revisedNext.
     * Its logic was more complex and potentially had issues with card removal/repositioning.
     * @param difficulty The difficulty factor for repositioning the card.
     */
    @Deprecated // Marking as deprecated as revisedNext is preferred
    private void originalNextLogic(double difficulty) {
        if (!DeckContent.isEmpty()) {
            DeckContent.getFirst().resetFlip();
            DeckContent.getFirst().reduceCard(difficulty);
            if (DeckContent.getFirst().isActive()) {
                int newIndex = (int) (DeckContent.size() * difficulty);
                if (DeckContent.size() > 1) {
                    newIndex = Math.max(0, Math.min(newIndex, DeckContent.size() -1));
                    LearningCard current = DeckContent.removeFirst();
                    DeckContent.add(newIndex, current);
                }
            }
            // This part of the logic was potentially problematic and might lead to unexpected behavior
            // if the card at getFirst() changed due to the repositioning above.
            if (!DeckContent.getFirst().isActive() || DeckContent.size() == 1 && !DeckContent.getFirst().isActive()) {
                DeckContent.removeFirst();
            } else if (DeckContent.size() == 1 && DeckContent.getFirst().isActive()){
                // Do nothing, keep the only active card
            }
        }
    }

    /**
     * Revised logic for processing the current card and moving to the next.
     * The current card is processed (entropy reduced), removed from the front,
     * and if still active, re-inserted into the deck based on difficulty.
     * @param difficulty A factor (0.0 to 1.0) determining where the card is re-inserted.
     * 1.0 typically means end of the deck (easy).
     */
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

    public String easyNext(){
        revisedNext(1.0);
        return getCurrentCard();
    }

    public String mediumNext(){
        revisedNext(MEDIUM);
        return getCurrentCard();
    }

    public String hardNext(){
        revisedNext(HARD);
        return getCurrentCard();
    }
}
