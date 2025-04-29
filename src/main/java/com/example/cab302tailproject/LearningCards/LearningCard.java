package com.example.cab302tailproject.LearningCards;

class LearningCard {
    private String question;
    private String answer;
    private boolean isFlipped = false;

    /**
     * Learning card Constructor
     * @param question The cards question
     * @param answer The cards Answer
     */
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
    void setFlipped(){
        isFlipped = !isFlipped;
    }
    void resetFlip(){
        isFlipped = false;
    }
}
