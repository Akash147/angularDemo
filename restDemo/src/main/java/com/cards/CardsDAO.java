package com.cards;

import com.akash.db.KeyValue;
import com.akash.db.KeyValueDAO;
import com.cards.model.Card;
import com.cards.model.CardCollection;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	public static CardCollection getPublishedCards() throws ParseException {
		CardCollection publishedCollection = CardCollection.getEmptyCardCollection();
		CardCollection collection = getStoredCards();
        Date today = new Date();
		for(Card card : collection.getCards()){
            if(card.getMeta().containsKey("published") &&
                    card.getMeta().get("published").equalsIgnoreCase("true")) {
//                if(card.getMeta().containsKey("startTime") && card.getMeta().containsKey("endTime")){
//                    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
//                    if( fmt.format(today).equals(fmt.format(parseDate(card.getMeta().get("startTime")))) )
//                        publishedCollection.getCards().add(card);
//                }
//                else {
                    publishedCollection.getCards().add(card);
//                }
            }
		}
		return publishedCollection;
	}
	
	
	public static CardCollection getStoredCards(){
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

    public static void deleteCard(Card card) {
        System.out.println(card.toString());
        if(card!=null) {
            CardCollection collection = getStoredCards();
            if(collection.getCards().contains(card)) {
                int index = collection.getCards().indexOf(card);
                collection.getCards().remove(index);
                KeyValueDAO.insertKeyValue(SERVER_KEY, new Gson().toJson(collection));
            }
        }
    }

    private static Date parseDate( String input ) throws java.text.ParseException {

        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );

        //this is zero time so we need to add that TZ indicator for
        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );

            input = s0 + "GMT" + s1;
        }

        return df.parse( input );

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
