package com.example.cab302tailproject.LearningCards;

public class LearningCard { // Changed from 'class' to 'public class'
    private final String question;
    private final String answer;
    private boolean isFlipped = false;
    private double cardEntropy = 2;

    LearningCard(String question, String answer){
        this.question = question;
        this.answer = answer;
    }

    public String getCard() {
        if (isFlipped){
            return answer;
        }
        return question;
    }

    // getter for the question text
    public String getQuestion() {
        return question;
    }

    // getter for the answer text (optional, but good practice)
    public String getAnswer() {
        return answer;
    }

    // getter for isFlipped status
    public boolean isFlipped() {
        return isFlipped;
    }

    public boolean isActive(){
        return cardEntropy > 0;
    }

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