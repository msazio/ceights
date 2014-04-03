package maggie.eights;

import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Play the game
 * @author Maggie
 *
 */
public class GameScreen extends Activity implements View.OnClickListener{
    Vector<Player> players = new Vector<Player>(); //Vector holding all of the player objects
    Vector<Card> deck = new Vector<Card>(); //Vector holding each card object that has not yet been dealt or played
    Vector<Card> discardPile = new Vector<Card>();//Vector holding each card object that has been played
    char currentSuit;
    int handSize = 7; //Keeps track of initial hand size
    Card top, stackTop; //Card objects representing the top of the deck and the top of the discard pile
    char[] suits = {'c', 'd', 'h', 's'}; //representation for each suit
    String[] suitN = {"Clubs", "Diamonds", "Hearts", "Spades"}; //full name of each suit
    char[] values = {'a', '2', '3', '4', '5', '6', '7', '8', '9', 'x', 'j', 'q', 'k'}; //values of every card
    boolean hasDrawn = false; //keeps track of whether or not a player has drawn this turn
    int playerCount = 2;
    final String[] options = {suitN[0], suitN[1], suitN[2], suitN[3]};
    Card clickedCard, playCard;
    Bitmap spadeI, heartI, diamondI, clubI;
    ImageView img;
    int currentICount;
	char newSuit;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        players.addElement(new Player(false, true)); //human player
        players.addElement(new Player(true, false)); //AI player
              
    	setContentView(R.layout.game);       
    	generateDeck(); //create a full deck of 52 cards
    	deal(); //deal cards
               
	}
	

	/**
     * Create a full deck with 52 Card objects
     */
    public void generateDeck(){
        Bitmap cardB = null;
        Bitmap tmp = null;
        char currentValue;
        
        WindowManager w = this.getWindowManager();
        Display d = w.getDefaultDisplay();
        
        Point size = new Point();
        d.getSize(size);

        
        /*Create suit images and resize them*/
        Bitmap spadetmp = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("spade", "drawable", "maggie.eights"));
        spadeI = Bitmap.createScaledBitmap(spadetmp, size.x/12, size.x/11, true);
        Bitmap hearttmp = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("heart", "drawable", "maggie.eights"));
        heartI = Bitmap.createScaledBitmap(hearttmp, size.x/12, size.x/11, true);
        Bitmap diamondtmp = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("diamond", "drawable", "maggie.eights"));
        diamondI = Bitmap.createScaledBitmap(diamondtmp, size.x/12, size.x/11, true);
        Bitmap clubtmp = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("club", "drawable", "maggie.eights"));
        clubI = Bitmap.createScaledBitmap(clubtmp, size.x/12, size.x/11, true);
    	
        
        /*create the image used as the card backs and scale it to the correct size*/
        int backID = getResources().getIdentifier("bback", "drawable", "maggie.eights");
        tmp = BitmapFactory.decodeResource(getResources(), backID);
        
        cardB = Bitmap.createScaledBitmap(tmp, size.x/12, size.x/9, true);
       
        /*Create all 52 cards*/
        for(int j = 0; j < 4; j++){
            currentSuit = suits[j];
            for(int i=0;i<13;i++){ //0->A, 13->K
                currentValue = values[i];
                /*create the face image for the current card and scale it down*/
             
                int test2 = getResources().getIdentifier("" + currentSuit + currentValue, "drawable", "maggie.eights");
                tmp = BitmapFactory.decodeResource(getResources(), test2);
                
                Bitmap cardTMP = Bitmap.createScaledBitmap(tmp, size.x/12, size.x/9, true);
                                     
                /*create a new card object using the face image, back image, suit and value
                 and add the card to the deck*/
                deck.addElement(new Card(cardTMP, cardB, currentSuit, currentValue, j*13+i, this));
            }
        }        
      
        /*create card to always act as top of deck */
        top = new Card(cardB, cardB,'x','x', -1, this);
        top.setEnabled(true);
        LinearLayout middleCards = (LinearLayout) findViewById(R.id.middleCards);
        top.setOnClickListener(this);
        middleCards.addView(top, 0);
    }
    
    /**
     * Restart the game after a player wins/loses
     */
    public void restart(){
    	Card tmpC;
    	LinearLayout bottomCards = (LinearLayout)findViewById(R.id.bottomCards);
        LinearLayout middleCards = (LinearLayout)findViewById(R.id.middleCards);
        LinearLayout topCards = (LinearLayout)findViewById(R.id.topCards);
        
        /*Empty all cards from layouts*/
        bottomCards.removeAllViews();
        middleCards.removeViewAt(1); //remove top of stack
        topCards.removeAllViews();
        
        /*Set card visible*/
  	  	Card temp = (Card)discardPile.elementAt(0);
  	  	temp.setVisibility(View.VISIBLE);
        
        /*Empty cards from the both players' hands*/
        for(int i=0; i<((Player)players.elementAt(0)).hand.size(); i++)
        {
        	tmpC = ((Player)players.elementAt(0)).hand.elementAt(i);
        	discardPile.add(tmpC);
        }
        ((Player)players.elementAt(0)).hand.removeAllElements();
        ((Player)players.elementAt(0)).winner=false;
        ((Player)players.elementAt(0)).turn=true;
        
        for(int i=0; i<((Player)players.elementAt(1)).hand.size(); i++)
        {
        	tmpC = ((Player)players.elementAt(1)).hand.elementAt(i);
        	discardPile.add(tmpC);
        }
        ((Player)players.elementAt(1)).hand.removeAllElements();
        ((Player)players.elementAt(1)).winner=false;
        ((Player)players.elementAt(1)).turn=false;
       
        /*Move cards from discard pile back to the deck*/
        for(int i=0; i<discardPile.size(); i++)
        {
        	tmpC = discardPile.elementAt(i);
        	deck.add(tmpC);
        }
        discardPile.removeAllElements();
        
        /*Ensure that the 8's are associated with the correct suit*/
        for(int i=0; i<deck.size();i++)
        {
        	if(((Card)deck.elementAt(i)).place==7)
        		deck.elementAt(i).suit='c';
        	if(((Card)deck.elementAt(i)).place==20)
        		deck.elementAt(i).suit='d';
        	if(((Card)deck.elementAt(i)).place==33)
        		deck.elementAt(i).suit='h';
        	if(((Card)deck.elementAt(i)).place==46)
        		deck.elementAt(i).suit='s';
        }
                
        deal();   	
    }
	
    /**
     * Deal cards
     */
	public void deal(){
        Card tmpC;
        img = new ImageView(this);
        int ran;
        Random rand =  new Random();
        LinearLayout bottomCards = (LinearLayout)findViewById(R.id.bottomCards);
        LinearLayout topCards = (LinearLayout)findViewById(R.id.topCards);
        LinearLayout middleCards = (LinearLayout)findViewById(R.id.middleCards);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.tableTop);
        
        int[] coords = new int[2];
        top.getLocationInWindow(coords); //try using coords[0] instead of padding
        int[] endCoords = new int[2];
        
        int xStart = top.getPaddingLeft(); 
        int yStart = 250 - top.getPaddingTop() - top.getPaddingBottom();
        int xEnd = top.getPaddingLeft();

        DealerListener dealer = new DealerListener();
        
        /* Deal cards into all hands until each player has cards equal to the max hand size*/
        for(int i = 0; i < handSize; i++){
            for(int j = 0; j < players.size(); j++){
                ran = rand.nextInt(deck.size()); //get a random card
                tmpC = (Card)deck.elementAt(ran);
                deck.removeElementAt(ran);
                if(j==0){ //host player
                    tmpC.flipCardUp();
                    tmpC.setEnabled(false);
                    tmpC.setFocusable(true);
                    tmpC.setOnClickListener(this);
                    

                    tmpC.setVisibility(View.INVISIBLE);
                    bottomCards.addView(tmpC);
                    

                    
                }
                else{
                	tmpC.flipCardDown();
                	tmpC.setEnabled(false);
                	tmpC.setFocusable(false);
                	tmpC.setVisibility(View.INVISIBLE);
                	topCards.addView(tmpC);
                	

                }
                
            	tmpC.getLocationInWindow(endCoords);
                
                /*Start animation*/
                img.setImageBitmap(tmpC.currentImg);
                img.setImageBitmap(top.getCurrentImage());
            	img.setPadding(1, 1, 1, 1);
                img.setVisibility(View.VISIBLE);
                rl.addView(img);
        		Animation deal = new TranslateAnimation(xStart, endCoords[0], yStart, endCoords[1]);
        		dealer.chooseCardEnd(j, i);
        		deal.setDuration(300);
        		deal.setAnimationListener(dealer);
        		img.startAnimation(deal);
           
                
                ((Player)players.elementAt(j)).drawCard(tmpC);    
            }
        }
        
        ran = rand.nextInt(deck.size()); //get a random card
        tmpC = (Card)deck.elementAt(ran);
        deck.removeElementAt(ran);
        tmpC.flipCardUp();
        discardPile.addElement(tmpC);
        stackTop = tmpC; //stack top is equal to the random card
        stackTop.setEnabled(false);
        stackTop.setFocusable(false);
        stackTop.setVisibility(View.INVISIBLE);
        currentSuit = stackTop.suit;
        middleCards.addView(stackTop, 1); //display the stack top
        

        /*Start animation*/
        img.setImageBitmap(stackTop.upCard);
        img.setVisibility(View.VISIBLE);
		Animation deal = new TranslateAnimation(xStart, (float) (stackTop.getLeft() + stackTop.getPaddingLeft()*1.5), yStart, yStart);
		dealer.chooseCardEnd(2, 0);
		deal.setDuration(300);
		deal.setAnimationListener(dealer);
		img.startAnimation(deal);
        
		for(int i=0; i<handSize; i++)
        {
        	(((Player)players.elementAt(0)).hand.elementAt(i)).setEnabled(true);
        }
		img.setImageBitmap(null);
		setSuitImage();
	}
	
	
	/**
     * This allows a player to play a card out of their hand
     */
    public void play(){ 
        LinearLayout bottomCards = (LinearLayout)findViewById(R.id.bottomCards);
        
        /*If the clicked card is eligible to be played, play it*/
        if((clickedCard.suit == stackTop.suit) || (clickedCard.value == stackTop.value)){
            int xEnd = top.getPaddingLeft() + stackTop.getLeft(); //Ending x position of card on stack
            int xStart = clickedCard.getLeft() + clickedCard.getPaddingLeft(); //Starting x position of card in hand
            int yEnd = top.getCurrentImage().getHeight()*2 - 60 + top.getPaddingTop()*2; //Ending y position of card on stack
            int yStart = (top.getCurrentImage().getHeight() + top.getPaddingBottom() + top.getPaddingTop())*3 - 60; //Starting y position of card in hand
            
            /*If it's the last card, leave one card invisible in layout so buttons don't move*/
            if(players.elementAt(0).hand.size()==1)
       	 	{
       		 Card temp = (Card)discardPile.elementAt(0);
       		 temp.setVisibility(View.INVISIBLE);
       		 bottomCards.addView(temp);
       	 	}
            
            /*Remove a card from the player's hand*/
            ((Player)players.elementAt(0)).playCard(clickedCard);
            currentSuit = clickedCard.suit;

            /*Setup stack top*/
            clickedCard.setEnabled(false);
            clickedCard.setFocusable(false);
            discardPile.addElement(clickedCard);
            clickedCard.setVisibility(View.INVISIBLE);
            
            /*Setup and start animation*/
            Animation playAnimateBottom = new TranslateAnimation(xStart, xEnd, yStart, yEnd);
            playAnimateBottom.setDuration(500);
            img.setImageBitmap(clickedCard.getCurrentImage());
            img.startAnimation(playAnimateBottom);
            
            /*Finish animation by removing card, etc after animation ends*/
            Handler postAnimation = new Handler();
            postAnimation.postDelayed(new Runnable(){
            	public void run() {
					playFinish();
				}
            	
            }, 500);
            
           /*Failsafe in case animation fails*/
            Handler failsafe = new Handler();
            failsafe.postDelayed(new Runnable(){
            	public void run() {
					runHumanFailsafe();
				}
            }, 900);
        }
        /*If card is invalid, display error message*/
        else{
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Invalid play");
            builder.setMessage("Invalid Card Selection");
            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        	   
        }
    }

    /**
     * Ensure that card is played from human hand properly even if animation fails
     */
    void runHumanFailsafe(){
    	RelativeLayout rl = (RelativeLayout)findViewById(R.id.tableTop);
    	if((players.elementAt(0).turn == true) && (clickedCard.getVisibility() == View.INVISIBLE)) 
    	{
    		playFinish();
    		rl.invalidate(); //redraw the screen
    	}
    }
    
    /**
     * Ensure that card is played from AI hand properly even if animation fails
     */
    void runAIFailsafe(){
    	RelativeLayout rl = (RelativeLayout)findViewById(R.id.tableTop);
    	if((players.elementAt(1).turn == true) && (playCard.getVisibility() == View.INVISIBLE))
    	{
    		playFinish();
    		rl.invalidate(); //redraw the screen
    	}
    }
    
    /**
     * Change the suit to be played after a player has played an 8
     * @param item the suit selection
     */
    public void changeSuit(int item){
    	LinearLayout bottomCards = (LinearLayout)findViewById(R.id.bottomCards);
    	LinearLayout middleCards = (LinearLayout)findViewById(R.id.middleCards);
    	ScrollView wholeLayout = (ScrollView)findViewById(R.id.scrollView2);
    	
    	/*Remove a card from the player's hand*/
        ((Player)players.elementAt(0)).playCard(clickedCard);
        
        /* Determine if there is a winner */
        if(((Player)players.elementAt(0)).hand.size() == 0)
        {
            ((Player)players.elementAt(0)).winner = true;
            gameOver(false);
            return;
        }
        
        /*Setup stack top*/
        discardPile.addElement(clickedCard);
        clickedCard.setFocusable(false);
        bottomCards.removeView(clickedCard);
        middleCards.removeView(stackTop);
        stackTop = clickedCard;
        middleCards.addView(stackTop, 1);
           
        /*Determine which suit has been chosen*/
    	if(item==0)
    		currentSuit='c';
    	else if(item==1)
    		currentSuit='d';
    	else if(item==2)
    		currentSuit='h';
    	else if(item==3)
    		currentSuit='s';   	
        
        stackTop.suit = currentSuit;   
        
        setSuitImage(); //change suit display picture
        wholeLayout.invalidate(); //redraw the screen

        /*Change turns*/
        ((Player)players.elementAt(0)).turn = false;
        ((Player)players.elementAt(1)).turn = true;
        hasDrawn = false;
                
        /*start AI's turn*/
      	AIPlay();       
    }

    /**
     * This allows a player to pass their turn provided they have already drawn a card
     * @param isAI This is whether or not the current player is an AI player
     */
    public void pass(boolean isAI){
        /*Make sure that a player has drawn before they may pass*/
        if((hasDrawn == false)&&(isAI==false))
        {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Invalid play");
            builder.setMessage("You must draw a card before you pass");
            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }
        hasDrawn = false;
        
        /*Change the turns. If it is not the AI's turn, call AIplay()*/
        if(isAI)
        {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Opponent says:");
            builder.setMessage("Pass. Your turn");
            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            
            ((Player)players.elementAt(1)).turn = false;
            ((Player)players.elementAt(0)).turn = true;
        }
        else
        {
            ((Player)players.elementAt(0)).turn = false;
            ((Player)players.elementAt(1)).turn = true;
            AIPlay();
        }
    }
    
    /**
     * This allows a player to play an 8 card out of their hand
     */
    public void play8(){       
    	LinearLayout bottomCards = (LinearLayout)findViewById(R.id.bottomCards);
    	LinearLayout middleCards = (LinearLayout)findViewById(R.id.middleCards);
    	ScrollView wholeLayout = (ScrollView)findViewById(R.id.scrollView2);
    	
        /*Allow the player to change the suit after playing an 8*/
        if(clickedCard.value == '8'){
        	if(((Player)players.elementAt(0)).hand.size()==1)
        	{
        		/*Play 8 and then declare winner*/
        		((Player)players.elementAt(0)).playCard(clickedCard);
        		clickedCard.setEnabled(false);
                clickedCard.setFocusable(false);
                discardPile.addElement(clickedCard);
                bottomCards.removeView(clickedCard);
                middleCards.removeView(stackTop);
                stackTop = clickedCard;
                middleCards.addView(stackTop, 1);
                wholeLayout.invalidate(); //redraw the screen
        		gameOver(false);
        		return;
        	}
 
        	/*Setup dialog asking what suit to change to*/
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.suitselector);
    	    dialog.setTitle("Choose a suit");
    	    
    	    /*Setup listeners for the suit buttons*/
            ImageButton clubB = (ImageButton)dialog.findViewById(R.id.clubsButton);
            clubB.setOnClickListener(new OnClickListener(){
            	public void onClick(View suit) {
					changeSuit(0);
					dialog.dismiss();
				}
            });
            ImageButton diamondB = (ImageButton)dialog.findViewById(R.id.diamondsButton);
            diamondB.setOnClickListener(new OnClickListener(){
            	public void onClick(View suit) {
					changeSuit(1);
					dialog.dismiss();
				}
            });
            ImageButton heartB = (ImageButton)dialog.findViewById(R.id.heartsButton);
            heartB.setOnClickListener(new OnClickListener(){
            	public void onClick(View suit) {
					changeSuit(2);
					dialog.dismiss();
				}
            });
            ImageButton spadeB = (ImageButton)dialog.findViewById(R.id.spadesButton);
            spadeB.setOnClickListener(new OnClickListener(){
            	public void onClick(View suit) {
					changeSuit(3);
					dialog.dismiss();
				}
            });
            /*Cancel the dialog is the player doesn't want to play an 8*/
            ImageButton cancelB = (ImageButton)dialog.findViewById(R.id.cancelButton);
            cancelB.setOnClickListener(new OnClickListener(){
            	public void onClick(View suit) {
					dialog.dismiss();
				}
            });

            /*Display the dialog*/
    	    dialog.show();
        }        
    }
    
    /**
     * This determines what the AI player should do during their turn
     */
     public void AIPlay(){
         Card tmpC = null;
         playCard = null;
         int[] suitCounts = {0,0,0,0}; //clubs, diamonds, hearts, spades
         boolean canPlay = false;
         newSuit = 'x';
         int max = 0;
         LinearLayout topCards = (LinearLayout)findViewById(R.id.topCards);
         
         /*Check each card in the AI's hand to see if it's playable*/
         for(int i =0; i < ((Player)players.elementAt(1)).hand.size() ; i++)
         {
             tmpC = (Card)((Player)players.elementAt(1)).hand.elementAt(i);
             
             /*Count the number of cards of each suit unless the card is an 8*/
             if(tmpC.value != '8'){
                 if(tmpC.suit == 'c')
                     suitCounts[0]++;
                 else if(tmpC.suit == 'd')
                     suitCounts[1]++;
                 else if(tmpC.suit == 'h')
                     suitCounts[2]++;
                 else if(tmpC.suit == 's')
                     suitCounts[3]++;
             }
             
             /*Check if a card that is not an 8 shares a suit or value with the stack top*/    
             if(((tmpC.suit == currentSuit) || (tmpC.value == stackTop.value)) && (tmpC.value != '8'))
             {
                 canPlay = true;
                 playCard = tmpC;
             }
         }
         
         /*If a suitable card has not been found, play an 8 if possible*/
         if(canPlay == false){
             for(int i =0; i < ((Player)players.elementAt(1)).hand.size() ; i++)
             {
                 tmpC = (Card)((Player)players.elementAt(1)).hand.elementAt(i);
                 /*Play an 8 and change the suit based on which suit has the most cards*/
                 if(tmpC.value == '8')
                 {
                     for(int j = 0; j < 4; j++)
                     {
                         if(suitCounts[j] > max)
                         max = j;
                     }
                     newSuit = suits[max];
                     canPlay = true;
                     playCard = tmpC;
                 }   
             }
         }
         
         /*If a card can be played, play the card and make it the stack top*/
         if(canPlay == true)
         {
             currentSuit = playCard.suit;
        	 if(players.elementAt(1).hand.size()==1)
        	 {
        		 Card temp = (Card)discardPile.elementAt(0);
        		 temp.setVisibility(View.INVISIBLE);
        		 topCards.addView(temp);
        	 }
        	 
        	 /*Start and end x and y values for card being played*/
             int xEnd = top.getPaddingLeft()*3 + top.getCurrentImage().getWidth(); 
             int xStart = playCard.getLeft() + playCard.getPaddingLeft();
             int yEnd = top.getCurrentImage().getHeight()*2 - 60 + top.getPaddingTop()*2;
             int yStart = top.getPaddingTop();
        	 
             ((Player)players.elementAt(1)).playCard(playCard);
             playCard.flipCardUp();
             discardPile.addElement(playCard);
             playCard.setVisibility(View.INVISIBLE);

             /*Setup and start animation*/
             Animation playAnimateTop = new TranslateAnimation(xStart, xEnd, yStart, yEnd);
             playAnimateTop.setDuration(500);
             img.setImageBitmap(playCard.getCurrentImage());
             img.startAnimation(playAnimateTop);
             
             if(hasDrawn == true)
                 hasDrawn = false;
             
             /*Finish play by removing card from layout, etc. after animation ends*/
             Handler postAnimation = new Handler();
             postAnimation.postDelayed(new Runnable(){
            	 public void run() {
 					playFinish();
 				}
             }, 500);
             
             /*Failsafe in case animation fails*/
             Handler failsafe = new Handler();
             failsafe.postDelayed(new Runnable(){
            	 public void run() {
 					runAIFailsafe();
 				}
             }, 900);
         }
         /*If no card is playable, either draw a card or pass*/
         else
         {
             if(hasDrawn == false)
             {
                 draw(true);
             }
             else
             {
                 hasDrawn = false;
                 pass(true);
                 return;
             }
         }
     }
     
     /**
      * This draws a random card from the deck and adds it to playerNum's hand
      * @param isAI: true if AI, false if human
      */
     public void draw(boolean isAI){
    	 ScrollView wholeLayout = (ScrollView)findViewById(R.id.scrollView2);
         int ran;
         Random rand = new Random();
         hasDrawn = true;
         LinearLayout bottomCards = (LinearLayout)findViewById(R.id.bottomCards);
         LinearLayout topCards = (LinearLayout)findViewById(R.id.topCards);
         
         /*If the deck is empty, move cards from discard pile to the deck*/
         if(deck.size()==0)
         {
             for(int i=0; i<discardPile.size(); i++)
             {
                 deck.addElement(discardPile.elementAt(i));
             }
             discardPile.removeAllElements();
         }
         
         ran = rand.nextInt(deck.size()); //retrieve a random card
         
         Card tmpC = (Card)deck.elementAt(ran);
         
         /*Starting and ending x and y values for drawing a card*/
         int xStart = top.getPaddingLeft();
         int xEndB = (top.getCurrentImage().getWidth() + top.getPaddingLeft()*2) * players.elementAt(0).hand.size();
         int xEndT = (top.getCurrentImage().getWidth() + top.getPaddingLeft()*2) * players.elementAt(1).hand.size();
         int yStart = top.getCurrentImage().getHeight()*2 - 60 + top.getPaddingTop()*2;
         int yEnd = (top.getCurrentImage().getHeight() + top.getPaddingBottom() + top.getPaddingTop())*3 - 60;
        
         /*If the draw request is from the human player, turn the card face up*/
         if(isAI == false)
         {
             tmpC.setFocusable(true);
             tmpC.setEnabled(true);
             tmpC.flipCardUp();
             tmpC.setOnClickListener(this);
             tmpC.setVisibility(View.INVISIBLE);
             bottomCards.addView(tmpC);
             ((Player)players.elementAt(0)).drawCard(tmpC); //add a random card from the deck to playerNum's hand
             deck.removeElementAt(ran);
             
             /*Setup and start animation*/
             Animation drawAnimateBottom = new TranslateAnimation(xStart, xEndB, yStart, yEnd);
             drawAnimateBottom.setDuration(500);
             img.setImageBitmap(top.getCurrentImage());
             img.startAnimation(drawAnimateBottom);
             
             /*Finish draw by adding card to hand*/
             Handler postDraw = new Handler();
             postDraw.postDelayed(new Runnable(){
				public void run() {
					drawFinish();
				}
             }, 500);
         }
         /*Draw request from AI*/
         else
         {
        	 tmpC.setEnabled(false);
        	 tmpC.setFocusable(false);
        	 tmpC.flipCardDown();
        	 tmpC.setVisibility(View.INVISIBLE);
             topCards.addView(tmpC);
             ((Player)players.elementAt(1)).drawCard(tmpC); //add a random card from the deck to playerNum's hand
             deck.removeElementAt(ran);
             
             /*Setup and start animation for draw*/
             Animation drawAnimateTop = new TranslateAnimation(xStart, xEndT, yStart, top.getPaddingTop());
             drawAnimateTop.setDuration(500);
             img.setImageBitmap(top.getCurrentImage());
             img.startAnimation(drawAnimateTop);
             
             /*Finish draw to AI hand*/
             Handler postDraw = new Handler();
             postDraw.postDelayed(new Runnable(){
				public void run() {
					drawFinish();
				}
             }, 500);
         }
         wholeLayout.invalidate(); //redraw screen
         return;
     }

     /**
      * This is called when a player runs out of cards in their hand
      * @param AI: true if AI, false if human
      */
      public void gameOver(boolean isAI){
    	  ImageView suitI = (ImageView)findViewById(R.id.suitImage);
      	  TextView suitT = (TextView)findViewById(R.id.suitText);
      	  
      	  /*Clear suit image and text*/
      	  suitI.setImageBitmap(null);
      	  suitT.setText("");
      	
    	  hasDrawn = false;
          
    	  /*Setup text depending on if human wins or loses*/
    	  String text;
          if(isAI == false)
              text = "You Win! ";
          else
        	  text = "You Lose. ";
          
          /*Inform human game is over, ask to play again*/
          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setTitle("Game Over");
          builder.setMessage(text + "\nDo you want to play again?");
          builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int item) {
                  dialog.dismiss();
            	  restart();
              }
          });
          builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				backToMenu();
				
			}
          });
          AlertDialog alert = builder.create();
          alert.show();
      }
    
      /**
       * Go back to the main menu
       */
      public void backToMenu(){
    	  this.finish();
      }

      /**
       * Click listener for buttons
       */
      public void onClick(View view) {  
		LinearLayout bottomCards = (LinearLayout)findViewById(R.id.bottomCards);
		ScrollView wholeLayout = (ScrollView)findViewById(R.id.scrollView2);
		
		/*make sure it's the player's turn*/
        if(((Player)players.elementAt(0)).turn == true){
            Player tmpP;
            
            /* If the field is top, the player is trying to draw a card from the deck*/
            if(view == top){
                if(hasDrawn == true)
                {
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Invalid play");
                    builder.setMessage("You can only draw once per turn. Pass if unable to play");
                    builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }
                else{
                	draw(false);
                	return;
                }
            }
            
            /*If the field is passButton, the player is trying to pass their turn*/
            if(view == findViewById(R.id.passButton)){
                pass(false);
                wholeLayout.invalidate();
                return;
            }
            
            /*If the field is sortButton, sort the player's hand*/
            else if(view == findViewById(R.id.sortButton)){
            	Vector<Card> hand = ((Player)players.elementAt(0)).sortHand();
            	bottomCards.removeAllViews();
            	for(int i=0; i< hand.size(); i++){
            		bottomCards.addView(hand.elementAt(i));
            	}
            	wholeLayout.invalidate();
            	return;
            }
            
            /*If the field is helpButton, view help*/
            else if(view == findViewById(R.id.menuHelpButton)){
            	wholeLayout.invalidate();
            	startActivity(new Intent("HELP"));
            	return;
            }
        
            /*If the field is not top or passButton, the field must be a card that the player is trying to play*/
            tmpP = (Player)players.elementAt(0);
            for(int i = 0; i < tmpP.hand.size(); i++){
                if(tmpP.hand.elementAt(i) == view){
                    clickedCard = (Card)tmpP.hand.elementAt(i);
                    if(clickedCard.value=='8')
                    	play8();
                    else
                    	play();
                }
            }
       }
        
        /*Inform human that the card is not valid to be played*/
        else{
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Invalid play");
            builder.setMessage("Please wait for your turn");
            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }	
	}
      
    /**
     * Change the image and text indicating the suit
     */
    public void setSuitImage()
    {
    	ImageView suitI = (ImageView)findViewById(R.id.suitImage);
    	TextView suitT = (TextView)findViewById(R.id.suitText);
    	
    	/*Set suit identifiers*/
        if(currentSuit=='c')
        {
        	suitI.setImageBitmap(clubI);
        	suitT.setText("Clubs");
        }
        else if(currentSuit=='d')
        {
        	suitI.setImageBitmap(diamondI);
        	suitT.setText("Diamonds");
        }
        else if(currentSuit=='h')
        {
        	suitI.setImageBitmap(heartI);
        	suitT.setText("Hearts");
        }
        else if(currentSuit=='s')
        {
        	suitI.setImageBitmap(spadeI);
        	suitT.setText("Spades");
        }
        return;
    }
    
 
    
    void dealAnimation2(){
    	DealerListener dealerListener = new DealerListener();
    	img = new ImageView(this);
    	int cardHeight = top.getCurrentImage().getHeight();
    	
    	/*Setup animation variables*/
    	int[] coords = new int[2];
		top.getLocationOnScreen(coords);
		  
		int xStart = top.getPaddingLeft(); int yStart = coords[1] - top.getPaddingTop() - top.getPaddingBottom();
		int xEnd = top.getPaddingLeft();
		int yEnd = (top.getPaddingTop() + top.getPaddingBottom() + cardHeight)*3;
		 
		
  	  	/*Deal to human*/
    	RelativeLayout rl = (RelativeLayout)findViewById(R.id.tableTop);
    	img.setImageBitmap(top.getCurrentImage());
    	img.setPadding(1, 1, 1, 1);
    	rl.addView(img);
	
    	/*Start animation*/
		Animation deal = new TranslateAnimation(xStart, xEnd, yStart, yEnd);
		deal.setDuration(300);
		deal.setRepeatCount(8);
		deal.setAnimationListener(dealerListener);
		img.startAnimation(deal);
    }
    
    /**
     * One and only animation listener attempt
     */
    class DealerListener implements AnimationListener{
    	int pl = -1;
    	int cc;
    	RelativeLayout rl = (RelativeLayout)findViewById(R.id.tableTop);
    	
    	    	
    	public void chooseCardEnd(int player, int cardCount)
    	{
    		pl=player;
    		cc = cardCount;
    	}

		public void onAnimationStart(Animation animation) {
			
		}

		public void onAnimationEnd(Animation animation) {
			rl.removeViewAt(rl.getChildCount()-1);
			switch(pl){
			//deal card to human
			case(0): ((Card)((Player)players.elementAt(0)).hand.elementAt(cc)).setVisibility(View.VISIBLE);
				break;
			//deal card to AI
			case(1):((Card)((Player)players.elementAt(1)).hand.elementAt(cc)).setVisibility(View.VISIBLE);
				break;
			//deal card to stack top
			case(2): stackTop.setVisibility(View.VISIBLE);
				break;
			}
		}

		public void onAnimationRepeat(Animation animation) {
			
		}
    	
    }

 
	
	/**

	/**
	 * finish the draw animation
	 */
	void drawFinish(){
		Card tmpC;
		for(int i=0; i<2; i++)
		{
			tmpC = (Card)((Player)players.elementAt(i)).hand.lastElement();
			tmpC.setVisibility(View.VISIBLE);
		}
		img.setImageBitmap(null);
		img.clearAnimation();
		
		/*Start AI turn*/
		if(players.elementAt(1).turn == true)
			AIPlay();
	}
	
	/**
	 * finish the play card animation
	 */
	void playFinish()
	{
		LinearLayout topCards = (LinearLayout)findViewById(R.id.topCards);
		LinearLayout middleCards = (LinearLayout)findViewById(R.id.middleCards);
		LinearLayout bottomCards = (LinearLayout)findViewById(R.id.bottomCards);
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.tableTop);
		
		img.setImageBitmap(null);
		img.clearAnimation();
        middleCards.removeView(stackTop);
		
		/*Human player*/
		if(((Player)players.elementAt(0)).turn == true)
		{
			bottomCards.removeView(clickedCard);
            clickedCard.setVisibility(View.VISIBLE);
			
			stackTop = clickedCard;
	        middleCards.addView(stackTop, 1);
	        setSuitImage();
	        
	        /*Change turns*/
            ((Player)players.elementAt(0)).turn = false;
            ((Player)players.elementAt(1)).turn = true;
            hasDrawn = false;  
			
			/* Determine if there is a winner */
            if(((Player)players.elementAt(0)).hand.size() == 0)
            {
                ((Player)players.elementAt(0)).winner = true;
                gameOver(false);
                return;
            }
            
            /*Change the suit of the stack top if an 8 was played*/
            if(stackTop.value == '8')
                stackTop.suit = currentSuit;
                
            rl.invalidate();
			
            /*start AI's turn if no winner*/
            if(((Player)players.elementAt(0)).winner == false)
            	AIPlay();
		}
		/*AI player*/
		else if(((Player)players.elementAt(1)).turn == true)
		{
			topCards.removeView(playCard);
	        playCard.setVisibility(View.VISIBLE);
	        stackTop = playCard;
	        currentSuit=stackTop.suit;
	        middleCards.addView(stackTop, 1);
 
	        /*Change the turns*/
		    ((Player)players.elementAt(1)).turn = false;
		    ((Player)players.elementAt(0)).turn = true;
		    hasDrawn = false;
		    setSuitImage();
		    
		    /*If the AI  player has no more cards, declare them the winner*/
		    if(((Player)players.elementAt(1)).hand.size() == 0)
		    {
		        ((Player)players.elementAt(1)).winner = true;
		        gameOver(true);
		        return;
		    }
		    
		    rl.invalidate(); //redraw the screen
		    
		    /*Change the suit*/
		    if(stackTop.value == '8')
		    {
		       AISuitChangeAlert();
		    }  
		}
	}

	/**
	 * Inform human player that AI has changed suit
	 */
	void AISuitChangeAlert()
	{
		 stackTop.suit = newSuit;
         currentSuit = newSuit;
         setSuitImage();
         
         /*Setup dialog to inform human of new suit*/
         final Dialog dialog = new Dialog(this);
         dialog.setContentView(R.layout.aisuitindicator);
 	     dialog.setTitle("New suit is: ");
 	     ImageView suitV = (ImageView)dialog.findViewById(R.id.suitAIButton);
 	     Bitmap b = null;
 	   
 	     /*Change image based on new suit*/
 	     if(currentSuit == 'c')
 	    	 b = BitmapFactory.decodeResource(getResources(), R.drawable.cluboption);
 	     else if(currentSuit == 'd')
 	    	 b = BitmapFactory.decodeResource(getResources(), R.drawable.diamondoption);
 	     else if(currentSuit == 'h')
 	    	 b = BitmapFactory.decodeResource(getResources(), R.drawable.heartoption);
 	     else if(currentSuit == 's')
			 b = BitmapFactory.decodeResource(getResources(), R.drawable.spadeoption);
 		   
 	     suitV.setImageBitmap(b);
 	    
 	     /*Dismiss dialog when ok button clicked*/
         ImageButton okB = (ImageButton)dialog.findViewById(R.id.okButton);
         okB.setOnClickListener(new OnClickListener(){
         	public void onClick(View suit) {
					dialog.dismiss();
				}
         });
         
         /*Show dialog*/
         dialog.show();
	}
}
