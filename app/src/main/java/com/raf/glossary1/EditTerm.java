package com.raf.glossary1;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class EditTerm extends Activity {
	EditText termEnglish;
	EditText termSerbian;
	EditText termDescription;	
	
	DBController controller = new DBController(this);	
	
	 @Override
	 public void onBackPressed() {
			finish();
	}
	
	@Override
	    public void onCreate(Bundle savedInstanceState) {
		 	super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_edit_term);
			termEnglish = (EditText) findViewById(R.id.termEnglish);
			termSerbian = (EditText) findViewById(R.id.termSerbian);
			termDescription = (EditText) findViewById(R.id.termDescription);
			
			// prevents keyboard popup
			this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
			Intent i = getIntent();
			String termID = i.getStringExtra("termID");
			HashMap<String, String> termsList = controller.getTermInfo(termID);
			if(termsList.size()!=0) {
				termEnglish.setText(termsList.get("termEnglish"));
				termSerbian.setText(termsList.get("termSerbian"));
				termDescription.setText(termsList.get("termDescription"));				
			}
	 }
	 
	 public void editTerm(View view) {
		HashMap<String, String> queryValues =  new  HashMap<String, String>();		
		termEnglish = (EditText) findViewById(R.id.termEnglish);
		termSerbian = (EditText) findViewById(R.id.termSerbian);
		termDescription = (EditText) findViewById(R.id.termDescription);
		
		Intent i = getIntent();
		String termID = i.getStringExtra("termID");
		queryValues.put("termID", termID);
		queryValues.put("termEnglish", termEnglish.getText().toString());
		queryValues.put("termSerbian", termSerbian.getText().toString());
		queryValues.put("termDescription", termDescription.getText().toString());
		
		controller.updateTerm(queryValues);
		this.callHomeActivity(view);
		
	}
	public void removeTerm(View view) {
		Intent i = getIntent();
		String termID = i.getStringExtra("termID");
		controller.deleteTerm(termID);
		this.callHomeActivity(view);		
	}
	
	public void callHomeActivity(View view) {
	 	Intent  i = new Intent(getApplicationContext(),MainActivity.class);
		startActivity(i);
		finish();
	}
}

