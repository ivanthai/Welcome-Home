package com.welcomehome.app;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class WelcomeHomeActivity extends Activity {
    /** Called when the activity is first created. */
	
	
	private static final String callbackURL = "https://dev.tendrilinc.com";
	private static final String client_id = "33c7f50fa1562ce313e73f390efeee2b";
	private static final String client_secret = "7ca5b4fa2854a9a48f1143f2fcc8382e";
	private static final String extendedPermissions = "account,billing,consumption";
	private static final int refreshThreshold = 5;
	private static final String x_route = "sandbox";
	
	
	SharedPreferences prefs;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Handler handler = new Handler();
        
        
        Net.initHttp();
        NetworkCallbackRunnable<JSONObject> regCallback = new NetworkCallbackRunnable<JSONObject>() {
			@Override
			public void onSuccess(JSONObject input) {
				try {
					if (input.getBoolean("success")) {
						
						Toast.makeText(getBaseContext(), "Yayyyy", Toast.LENGTH_LONG).show();

						
						
					} else {
						Toast.makeText(getBaseContext(), "Awwww", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getBaseContext(), "Booo", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Throwable e) {
				Log.i("Stream Request Failed", "" + e.getMessage());
				Toast.makeText(getBaseContext(), "Dawwww", Toast.LENGTH_LONG).show();
			}
		};
		Net.getTendril(handler,
				regCallback);
	
    }
}