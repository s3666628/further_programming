package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.callback.Callback;

import model.card.Card;
import model.card.Deck;
import model.card.DeckImpl;
import model.card.Hand;
import model.card.HandImpl;
import model.card.Suit;
import view.GameCallback;
import view.GameCallbackCollection;
import model.bet.ScoreBetImpl;
import model.bet.SuitBetImpl;

public class GameEngineImpl implements GameEngine, GameCallbackCollection {

	// hashmap for storing players (key = playerID, value = player object)

	HashMap<String, Player> players = new HashMap<String, Player>();
	// collection for the callback logger
	List<GameCallback> callBackList = new ArrayList<>();

	// this should give us a shuffled deck of cards - will be used to deal cards to
	// the player
	Deck mydeck = DeckImpl.createShuffledDeck();

	private GameCallback getCallBack() {
		GameCallback callback = callBackList.get(0);
		return callback;
	}

	// no argument constructor
	public GameEngineImpl() {
	}

	@Override
	public void registerCallback(GameCallback callback) {
		// callback - the GameCallback to be added to the collection.
		callBackList.add(callback);

	}

	@Override
	public void removeCallback(GameCallback callback) {
//		Removes supplied GameCallback from the collection of registered callbacks.
		callBackList.remove(callback);

	}

	@Override
	public void addPlayer(Player player) throws NullPointerException, IllegalArgumentException {
//		//adds new player to the hashmap 
//		key = playerID, value = player object)
		players.put(player.getId(), player);
		// logg information
		GameCallback callback = getCallBack();
		callback.addPlayer(player);
	}

	@Override
	public void removePlayer(String playerId) throws NullPointerException, IllegalArgumentException {
		// this should work - removes playerId that is passed in from the hash map
		Player player = players.get(playerId);// get player object from array via playerId string
		GameCallback callback = getCallBack();
		// NB: make sure logg removing player before removing player otherwise it won't
		// work
		callback.removePlayer(player);
		players.remove(playerId);

	}

	@Override
	public Collection<Player> getAllPlayers() {
		// TODO this has not been worked out properly
		Collection<Player> allPlayers = (Collection<Player>) players.clone();
		return allPlayers;
	}

	@Override
	public void placeBet(String playerId, int amount) throws NullPointerException, IllegalArgumentException {
		// this will create a new score bet
		Player player = players.get(playerId);
		ScoreBetImpl Scorebet = new ScoreBetImpl(players.get(playerId), amount);
		player.assignBet(Scorebet);
		GameCallback callback = getCallBack();
		callback.betUpdated(player);

	}

	@Override
	public void placeBet(String playerId, int amount, Suit suit) throws NullPointerException, IllegalArgumentException {
		// this will create a new suit bet
		Player player = players.get(playerId);
		SuitBetImpl Suitbet = new SuitBetImpl(players.get(playerId), amount, suit);
		player.assignBet(Suitbet);
		GameCallback callback = getCallBack();
		callback.betUpdated(player);

	}

//	@Override
//	public void dealPlayer(String playerId, int delay)
//			throws NullPointerException, IllegalArgumentException, IllegalStateException {
//		
//		// prolly need loop here for each player do the following
//		Player currentPlayer = players.get(playerId);
//		GameCallback callback = getCallBack();
//		callback.newDeck(mydeck);
//	
//		// a card is removed from the deck and dealt to the player
////		HandImpl playerHand = new HandImpl();
//		// 1 get the next card from the deck
//		Card bustCard = null;
//	
////		// 2 get a hand for the current player
//		Hand currentPlayerHand = currentPlayer.getHand();
//		// 3 check the score of the hand	
//		
//		// if card does not bust pla
//		while (currentPlayerHand.getScore() <= currentPlayerHand.BUST_SCORE) {
//			
//			System.out.println("current hand score: "+currentPlayerHand.getScore());
//			currentPlayerHand.getScore();
//			Card nextCard = mydeck.removeNextCard();
//			
//			// keep dealing cards until the player is bust
//			currentPlayerHand.dealCard(nextCard);
//			callback.playerCard(currentPlayer, nextCard);
//			//get score players hand
//			//update bust card so we know which card they went bust on
//			
//			bustCard = nextCard;
//			
//			
//			
//		}
//		callback.playerBust(currentPlayer, bustCard);
	// 4 add the current card to the players hand

//		if nextCard.getValue();		

//		Deals a hand to the specified player.
//
//		Cards are dealt to the specified player in the following manner
//
//		    a card is removed from the deck and dealt to the player
//		    a delay occurs, of the specified time in ms, between the card being removed from the deck and it being dealt to the player
//		        if the card does not bust in player's hand:
//		            the GameCallback.playerCard(Player, model.card.Card) is invoked on all the GameCallback objects that have been registered, notifying the player and the card dealt (all get the same information!)
//		            the process is then repeated for the next card until a bust occurs 
//		        if the card does bust in the player's hand
//		            the GameCallback.playerBust(Player, model.card.Card) is invoked on all the GameCallback objects that have been registered, notifying the player and the card that was attempted to be dealt to the player 
//
//		Note: A bust card is burnt, and not returned to the deck or dealt to any subsequent player.
//		
//		hand.dealCard();

//	}

	public void dealPlayer(String playerId, int delay)
			throws NullPointerException, IllegalArgumentException, IllegalStateException {

		// prolly need loop here for each player do the following
		Player currentPlayer = players.get(playerId); // get player object
		GameCallback callback = getCallBack(); // create callback object
		callback.newDeck(mydeck); // log the new deck

		Card bustCard = null;
//		// 2 get a hand for the current player
		Hand currentPlayerHand = currentPlayer.getHand();
		int loopCheck = 0;
		// 3 check the score of the hand
		int bustScore = 21;
		// if card does not bust pla
		while (loopCheck <= bustScore && loopCheck == 0) {

//			System.out.println("current hand score: "+currentPlayerHand.getScore());

			// see what the next card is
			Card nextCard = mydeck.removeNextCard();
			// these are to hold some values
			int valueOfNextCard = nextCard.getValue();
			int valueOfScore = currentPlayerHand.getScore();
			int potentialScore = valueOfNextCard + valueOfScore;
			// check if next card will bust player or not
			if (potentialScore >= bustScore) {
				// if next card will bust player then update loop check to 1 to stop running
				bustCard = nextCard;
				// get the score for the current hand
				currentPlayerHand.getScore();
				loopCheck = 1;
			} else {
				currentPlayerHand.dealCard(nextCard);
				callback.playerCard(currentPlayer, nextCard);
			}

		}
		callback.playerBust(currentPlayer, bustCard);

	}

	@Override
	public void dealHouse(int delay) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
		// prolly need loop here for each player do the following
//		Player currentPlayer = players.get(playerId); // get player object
		GameCallback callback = getCallBack(); // create callback object
		callback.newDeck(mydeck); // log the new deck

		Card bustCard = null;
//		// 2 get a hand for the current player
		Hand houseHand = currentPlayer.getHand();
		int loopCheck = 0;
		// 3 check the score of the hand
		int bustScore = 21;
		// if card does not bust pla
		while (loopCheck <= bustScore && loopCheck == 0) {

//			System.out.println("current hand score: "+currentPlayerHand.getScore());

			// see what the next card is
			Card nextCard = mydeck.removeNextCard();
			// these are to hold some values
			int valueOfNextCard = nextCard.getValue();
			int valueOfScore = currentPlayerHand.getScore();
			int potentialScore = valueOfNextCard + valueOfScore;
			// check if next card will bust player or not
			if (potentialScore >= bustScore) {
				// if next card will bust player then update loop check to 1 to stop running
				bustCard = nextCard;
				// get the score for the current hand
				currentPlayerHand.getScore();
				loopCheck = 1;
			} else {
				currentPlayerHand.dealCard(nextCard);
				callback.playerCard(currentPlayer, nextCard);
			}

		}
		callback.playerBust(currentPlayer, bustCard);

	}

	}

	@Override
	public void resetAllBetsAndHands() {
		// TODO Auto-generated method stub

	}

}
