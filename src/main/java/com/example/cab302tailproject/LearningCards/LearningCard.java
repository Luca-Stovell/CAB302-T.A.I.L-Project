package com.example.cab302tailproject.LearningCards;

class LearningCard {
    private final String question;
    private final String answer;
    private boolean isFlipped = false;
    /**
     * Represents how many times a card can be read before being removed from the deck
     */
    // Name might not make sense
    private double cardEntropy = 2;

    /**
     * Learning card Constructor
     * @param question The cards question
     * @param answer The cards Answer
     */
    LearningCard(String question, String answer){
        this.question = question;
        this.answer = answer;
    }

    /**
     * returns a card's forward facing face.
     * @return String containing the card contents
     */
    public String getCard() {
        if (isFlipped){
            return answer;
        }
        return question;
    }

    public boolean isActive(){
        return cardEntropy > 0;
    }

    /**
     * Reduces a cards cardEntropy by a specified amount
     * @param difficulty Amount reduced by, designed to usually be one of the constants from learningCardDeck
     */
    public void reduceCard(double difficulty){
        cardEntropy -= difficulty;
    }

    void setFlipped(){
        isFlipped = !isFlipped;
    }
    void resetFlip(){
        isFlipped = false;
    }
}
