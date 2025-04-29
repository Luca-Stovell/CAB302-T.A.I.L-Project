package com.example.cab302tailproject.LearningCards;

import java.util.ArrayList;
import java.util.List;


// Assumptions/ Rules:
// learning cards are stored as CSVs and should be parsed into lists of cards
// Format clarification: cards delimited by  "," , question/answers delimited by ";"
// For now, delimiters can be simple TODO change this if cards need to use the delimiters
// queue system to look at cards
public class LearningCardDeck {
    private List<LearningCard> DeckContent = new ArrayList<>();


    public LearningCardDeck(List<String[]> cardContent) {
        for (String[] strings : cardContent) {
            DeckContent.add(new LearningCard(strings[0], strings[1]));
        }
    }
    public LearningCardDeck(String cardContent){
        String[] cards = cardContent.split(",");
        for (String strings : cards){
            String[] card = strings.split(":");
            DeckContent.add(new LearningCard(card[0], card[1]));
        }
    }

    public String getCurrentCard() {
        return DeckContent.getFirst().getCard();
    }

    public void flip() {
        DeckContent.getFirst().setFlipped();
    }

    public void next(){
        next(1);
    }
    public void next(double difficulty) {
        DeckContent.getFirst().resetFlip();
        int newIndex = (int) (DeckContent.size()*difficulty);
        DeckContent.add(newIndex, DeckContent.getFirst());
        DeckContent.removeFirst();
    }
}
