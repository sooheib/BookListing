package com.example.android.booklisting;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHOR = "author";
    private static String URLString;
    public TextView emptyTextView;
    ListView listView;
    ArrayList<HashMap<String, String>> book_list = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.search_button);
        assert button != null;
        emptyTextView = (TextView) findViewById(R.id.no_data);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.search_input);
                String input = editText.getText().toString().replace(" ", "+");
                String APIKEY = "&key=AIzaSyBnVXaIVbj8kXE98FJeyLuP8JZt5phkdrg";
                URLString = "https://www.googleapis.com/books/v1/volumes?q=" + input+"&orderBy=newest";
                Log.v("URL: ", URLString);
                new JSONprocessor().execute(URLString);
            }
        });

    }

    private class JSONprocessor extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "loading data", Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(String... strings) {
            String stream = null;
            String URLString = strings[0];
            DataHandler dataHandler = new DataHandler();
            stream = dataHandler.getStreamData(URLString);
            return stream;
        }

        protected void onPostExecute(String stream) {
            listView = (ListView) findViewById(R.id.book_list);
            if (stream != null) {
                try {
                    JSONObject jsonObjectReader = new JSONObject(stream);
                    int totalItems = jsonObjectReader.getInt("totalItems");
                    if (totalItems == 0) {
                        listView.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Search returned no results", Toast.LENGTH_SHORT).show();
                    } else {
                        emptyTextView.setText("");
                        JSONArray booksArray = jsonObjectReader.getJSONArray("items");
                        for (int i = 0; i < booksArray.length(); i++) {
                            JSONObject bookObject = booksArray.getJSONObject(i);
                            String title, author;
                            JSONObject bookDetails = bookObject.getJSONObject("volumeInfo");
                            if (bookDetails.has("authors")) {
                                author = (bookDetails.getString("authors")).replace("[", "").replace("]", "");
                            } else {
                                author = "";
                            }
                            title = bookDetails.getString("title");
                            Log.v(TAG_TITLE, title);
                            Log.v(TAG_AUTHOR, author);

                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put(TAG_TITLE, title);
                            hashMap.put(TAG_AUTHOR, author);
                            book_list.add(hashMap);

                            ListAdapter listAdapter = new SimpleAdapter(MainActivity.this,
                                    book_list,
                                    R.layout.listview_searchresult_layout,
                                    new String[]{TAG_TITLE, TAG_AUTHOR},
                                    new int[]{R.id.book_title, R.id.book_author});

                            listView.setAdapter(listAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent,
                                                        View view,
                                                        int position,
                                                        long id) {
                                    Toast.makeText(MainActivity.this, "Description:\n" + book_list.get(+position), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
