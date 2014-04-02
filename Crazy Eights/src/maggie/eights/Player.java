/*
 * Player.java
 *
 * © Maggie Sazio, 2011
 * Confidential and proprietary.
 */

package maggie.eights;


import java.util.Vector;

/**
 *  This class contains all the information about a specific player
 */
public class Player{
    boolean turn;
    Vector<Card> hand;
    boolean winner;
    boolean AI;
    
    /**
     * Constructor
     * @param isAI false for the human player, true for the AI player
     * @param isTurn true when it is this player's turn to play
     */
    Player(boolean isAI, boolean isTurn) {
        winner = false;
        AI = isAI;
        turn = isTurn;
        hand = new Vector<Card>();    }
    
    /**
     * Removes a single card, specified by the player from the player's hand
     * @param card This is the card to be removed and played on the stack
     */
    public void playCard(Card card){
        hand.removeElement(card);
    }
    
    /**
     * Add a card to the player's hand.
     * @param card This is the card to be added to the hand
     */
    public void drawCard(Card card){
        hand.addElement(card);
    }
    
    /**
     * Sort the cards in a player's hand first by suit, then by value
     * @return Return the sorted cards as a vector
     */
    public Vector<Card> sortHand(){
        Card low; //current lowest card based on value
        Vector<Card> cards = new Vector<Card>();
        
        while(hand.size() != 0)
        {
            low = (Card)(hand.elementAt(0)); 
            for(int j=1; j<hand.size(); j++)
                {
                    if(((Card)hand.elementAt(j)).place < ((Card)low).place)
                        low = (Card)(hand.elementAt(j));
                }
            cards.addElement(low);
            hand.removeElement(low);
        }
      
        hand = cards;
        return hand;
    }
} 

