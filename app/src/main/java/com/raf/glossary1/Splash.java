package com.raf.glossary1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		int secondsDelay = 4;
		new Handler().postDelayed(new Runnable() {
			
			public void run() {
				startActivity(new Intent(Splash.this, MainActivity.class));
				finish();
			}
			
		}, secondsDelay * 1000);
	}
}