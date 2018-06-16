package com.raf.glossary1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;



public class MainActivity extends Activity {

    Intent intent;
    TextView termID;
    static String filterText;
    SearchView sv;
    DBController db = new DBController(this);
    public String result1;
    public ProgressDialog pDialog;

    ListView lv;
    ListAdapter adapter;

    ArrayList<HashMap<String, String>> termsListDb;
    ArrayList<HashMap<String, String>> termsListWeb;

    protected ArrayList<HashMap<String, String>> jsonParser(String result) throws JSONException {
        JSONObject termsObj = new JSONObject(result);
        JSONArray termsArr = termsObj.getJSONArray("terms");

        for (int i = 0; i < termsArr.length(); i++) {
            JSONObject terms = termsArr.getJSONObject(i);

            String termEnglish = terms.getString("termEnglish");
            String termSerbian = terms.getString("termSerbian");
            String termDescription = terms.getString("termDescription");

            HashMap<String, String> map = new HashMap<>();

            map.put("termEnglish", termEnglish);
            map.put("termSerbian", termSerbian);
            map.put("termDescription", termDescription);

            termsListWeb.add(map);
        }
        return termsListWeb;
    }

    private void setupSearchView() {
        sv.setIconifiedByDefault(false);
        sv.setSubmitButtonEnabled(false);
        sv.setQueryHint("Search term ...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        lv = findViewById(R.id.list);
        sv = (SearchView) findViewById(R.id.searchView);
        sv.setFocusable(false);

        termsListDb = new ArrayList<>();
        termsListDb = db.getAllTerms();
        termsListWeb = new ArrayList<>();

        adapter = new SimpleAdapter
                (MainActivity.this, termsListDb, R.layout.view_term_entry,
                        new String[]{"termID", "termEnglish", "termSerbian", "termDescription"},
                        new int[]{R.id.termID, R.id.termEnglish, R.id.termSerbian, R.id.termDescription});

        lv.setAdapter(adapter);
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                termID = findViewById(R.id.termID);
                String valTermId = termID.getText().toString();
                Intent  i = new Intent(getApplicationContext(),EditTerm.class);
                i.putExtra("termID", valTermId);
                startActivity(i);
                //finish();

            }
        });

        setupSearchView();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    lv.clearTextFilter();
                } else {
                    lv.setFilterText(newText.toString());
                    filterText = newText;
                }
                return false;
            }
        });

        int searchCloseButtonId = sv.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.sv.findViewById(searchCloseButtonId);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv.setQuery("", false);
                sv.setFocusable(false);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            WebService task = new WebService(MainActivity.this,
                    "http://www.raf1.in.rs/terms/get_all_terms.php", new OnTaskDoneListener() {

                @Override
                public void onTaskDone(String responseData) {
                    try {
                        termsListWeb = jsonParser(responseData);
                        db.populate(termsListWeb);
                        callHomeActivity();

                        adapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError() {

                }
            });

            task.execute();
            return true;

        } else if (item.getItemId() == R.id.about) {
            Intent i = new Intent(getApplicationContext(), Splash.class);
            startActivity(i);
        }


        return super.onOptionsItemSelected(item);
    }

    public void showAddForm(View view) {
        Intent i = new Intent(getApplicationContext(), NewTerm.class);
        startActivity(i);
        finish();
    }

    public void callHomeActivity() {
        Intent  i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
    }

    public class WebService extends AsyncTask<String, Void, String> {
        private Context mContext;
        private OnTaskDoneListener onTaskDoneListener;
        private String urlStr = "";

        public WebService(Context context, String url, OnTaskDoneListener onTaskDoneListener) {
            this.mContext = context;
            this.urlStr = url;
            this.onTaskDoneListener = onTaskDoneListener;
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                URL mUrl = new URL(urlStr);
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Content-length", "0");
                httpConnection.setUseCaches(false);
                httpConnection.setAllowUserInteraction(false);
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);

                httpConnection.connect();

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading glossary. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            lv.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (onTaskDoneListener != null && s != null) {
                onTaskDoneListener.onTaskDone(s);
            } else {
                onTaskDoneListener.onError();
            }
            pDialog.hide();
            lv.setVisibility(View.VISIBLE);

        }
    }

    public void showAbout() {
        Intent  objIntent = new Intent(getApplicationContext(),Splash.class);
        Bundle b = new Bundle();
        b.putInt("s", 5);
        startActivity(objIntent);
        finish();
    }
}



