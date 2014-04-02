package maggie.eights;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class HelpScreen extends Activity{
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.help);
	}
	
	public void goBack(View view){
		super.onBackPressed();
	}

}
