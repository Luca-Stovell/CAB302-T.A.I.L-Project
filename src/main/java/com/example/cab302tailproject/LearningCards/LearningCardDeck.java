package com.example.cab302tailproject.LearningCards;

import java.util.ArrayList;
import java.util.List;


// Assumptions/ Rules:
// learning cards are stored as CSVs (strings in the database) and should be parsed into lists of cards
// Format clarification: cards delimited by  ",," , question/answers delimited by "::"
// For now, delimiters can be simple TODO change this if cards need to use the delimiters
// queue system to look at cards
public class LearningCardDeck {
    // Constants:
    // constants subject to change, really depends on what feels right when using the card reader
    private static final double MEDIUM = 0.6;
    private static final double HARD = 0.3;
    private static final String EMPTY_MESSAGE = "Congratulations, you have finished the deck!";

    private List<LearningCard> DeckContent = new ArrayList<>();

// Used for testing, and nothing else right now. Probably could be used in the card creator
    public LearningCardDeck(List<String[]> cardContent) {
        for (String[] strings : cardContent) {
            DeckContent.add(new LearningCard(strings[0], strings[1]));
        }
    }

    /**
     * Learning card deck constructor, that reads a string fetched from the database
     * @param cardContent string containing all the card data as stored in the database
     */
    public LearningCardDeck(String cardContent){
        try {
            String[] cards = cardContent.split(",,");
            for (String strings : cards) {
                String[] card = strings.split("::");
                DeckContent.add(new LearningCard(card[0], card[1]));
            }
        } catch (Exception e) { // for testing.TODO Replace with real exception handling later
            System.out.println("exception in card deck constructor");
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
        next(1);
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
                DeckContent.add(newIndex, DeckContent.getFirst());

            }
            DeckContent.removeFirst();
        }
    }
    //

    /**
     * moves the current card to the end of the deck
     */
    public String easyNext(){
        next();
        return getCurrentCard();
    }

    /**
     * moves the current card to the middle of the deck
     */
    public String mediumNext(){
        next(MEDIUM);
        return getCurrentCard();
    }


    /**
     * moves the current card close to the start of the deck
     */
    public String hardNext(){
        next(HARD);
        return getCurrentCard();
    }


    /// Optional extra functionality: shuffle function. Not mentioned in the GUI or any planning documents, but might be nice to have

}
