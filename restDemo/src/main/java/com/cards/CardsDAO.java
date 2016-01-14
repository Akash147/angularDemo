package com.cards;

import com.akash.db.KeyValue;
import com.akash.db.KeyValueDAO;
import com.cards.model.Card;
import com.cards.model.CardCollection;
import com.google.gson.Gson;

public class CardsDAO {
	private static final String SERVER_KEY = "server_cards_storage";
	private static final String CLIENT_KEY = "server_cards";
	private static CardCollection storedCards;
	
	public static void insertCard(Card card){
		if(card!=null) {
			CardCollection collection = getStoredCards();
			if(!collection.getCards().contains(card)) {
                card.assignNewId();
                collection.getCards().add(card);
            }
            else {
				int index = collection.getCards().indexOf(card);
				collection.getCards().set(index, card);
			}
			
			KeyValueDAO.insertKeyValue(SERVER_KEY, new Gson().toJson(collection));
		}
	}
	
	public static CardCollection getPublishedCards(){
		CardCollection publishedCollection = CardCollection.getEmptyCardCollection();
		CardCollection collection = getStoredCards();
		for(Card card : collection.getCards()){
			publishedCollection.getCards().add(card);
		}
		return publishedCollection;
	}
	
	
	private static CardCollection getStoredCards(){
//		String cards = (String) getCache().get(getCacheKey());
		
//		if(cards==null) {
			KeyValue kv = KeyValueDAO.getKeyValue(SERVER_KEY);
			if(kv!=null){
				String cards = kv.getValue();
				if(cards!=null && !cards.isEmpty())
					storedCards = new Gson().fromJson(cards, CardCollection.class); 
			}
//		}
			if(storedCards==null){
				storedCards = CardCollection.getEmptyCardCollection();
			}
		return storedCards;
	}
/*	
	private static MemcacheService getCache(){
		return MemcacheServiceFactory.getMemcacheService();
	}
	
	private static String getCacheKey(){
		return "cards_cache_key";
	}
*/
}
