package com.cards.model;

import java.util.ArrayList;
import java.util.List;

public class CardCollection {
	private List<Card> cards;
	
	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	public static CardCollection getEmptyCardCollection(){
		CardCollection collection = new CardCollection();
		collection.setCards(new ArrayList<Card>());
		return collection;
	}
}
