package com.raf.glossary1;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NewTerm extends Activity{
	EditText termEnglish;
	EditText termSerbian;
	EditText termDescription;
	
	DBController controller = new DBController(this);
	
	@Override
	 public void onBackPressed() {
		 Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(objIntent);
			finish();
	 }
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.add_new_term);
	        termEnglish = (EditText) findViewById(R.id.termEnglish);
	        termSerbian = (EditText) findViewById(R.id.termSerbian);
	        termDescription = (EditText) findViewById(R.id.termDescription);
	 }
	 
	public void addNewTerm(View view) {
		HashMap<String, String> queryValues =  new  HashMap<String, String>();
		queryValues.put("termEnglish", termEnglish.getText().toString());
		queryValues.put("termSerbian", termSerbian.getText().toString());
		queryValues.put("termDescription", termDescription.getText().toString());
				
		controller.insertTerm(queryValues);
		this.callHomeActivity(view);
	}
	public void callHomeActivity(View view) {
		Intent  i = new Intent(getApplicationContext(),MainActivity.class);
		startActivity(i);
		finish();
	}	
}
