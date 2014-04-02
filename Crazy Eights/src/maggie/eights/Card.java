/*
 * Card.java
 *
 * © Maggie Sazio, 2011
 * Confidential and proprietary.
 */


package maggie.eights;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageButton;

/**
 * Card is a specialized field class that allows a card image to act as a button
 */
public class Card extends ImageButton{
    char suit; //C=Clubs, D=Diamonds, H=Hearts, S=Spades
    char value; //A=Ace, ... , J=Jack, Q=Queen, K=King
    int place; //place in a new deck
    Bitmap downCard;
    Bitmap upCard;
    Bitmap currentImg;
    boolean f = false;
    
    /**
     * Default constructor
     * @param up This is the bitmap of the card face
     * @param down This is the bitmap of the card back
     * @param s This is the card's suit
     * @param v This is the card's value ie. A, 2, 3, ... X, J, Q, K
     * @param p This is the card's place in an unshuffled deck
     * @param style This is the current style
     */
    Card(Bitmap up, Bitmap down, char s, char v, int p, Context style) {
        super(style);
        int width = up.getWidth();
        this.setPadding(width/6, width/6, width/6, width/6);
        this.setBackgroundResource(0);
        downCard = down;
        upCard = up;
        suit = s;
        value = v;
        place = p;
        this.setImageBitmap(downCard);
        currentImg = downCard;
    }
            
    /**
     * Change the image displayed on the screen to show the card face
     */
    public void flipCardUp(){
        this.setImageBitmap(upCard);
        currentImg = upCard;
    }
    
    /**
     * Change the image displayed on the screen to back of the card 
     */
    public void flipCardDown(){
        this.setImageBitmap(downCard);
        currentImg = downCard;
    }
    
    /**
     * Return the current image being displayed
     * @return the image currently being displayed
     */
    public Bitmap getCurrentImage(){
    	return currentImg;
    }
    

} 
