package maggie.eights;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends Activity{
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
	}
	
	/**
	 * Start a new single player game
	 * @param view
	 */
	public void startGame(View view) {
		startActivity(new Intent("PLAY_GAME"));
	}
	
	/**
	 * Open the help screen
	 * @param view
	 */
	public void viewHelp(View view) {
		startActivity(new Intent("HELP"));
	}
	
	/**
	 * Exit the application
	 * @param view
	 */
	public void exit(View view){
		this.finish();
	}

}
