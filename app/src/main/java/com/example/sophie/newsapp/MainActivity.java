package com.example.sophie.newsapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sophie.newsapp.data.NewsObject;
import com.example.sophie.newsapp.utils.JsonLoader;
import com.example.sophie.newsapp.utils.NewsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<NewsObject> testHistory = new ArrayList<>();
    Button update_button;
    String serverResponse;
    TextView noDataToShow;

    //check if device is connected
    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find my views
        update_button = findViewById(R.id.update_button);
        noDataToShow = findViewById(R.id.no_data_message);
        //check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        }
        //get my data
        if (isNetworkAvailable()) {
            fetchData();
        } else {
            noDataToShow.setText(R.string.no_connection);
        }

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });

    }

    private void fetchData() {
        NewsAsyncTask task = new NewsAsyncTask(this, getString(R.string.url));
        task.execute(getString(R.string.url));
    }

    public void parseGuardianJson(String firstJson) {
        String title = "";
        String title2 = "";
        String articleAuthor = "";
        String text = "";
        String date = "";
        String url = "";
        String separator = getString(R.string.separator);
        try {
            JSONObject firstObject = new JSONObject(firstJson);
            JSONObject response = firstObject.optJSONObject("response");
            JSONArray resultJSON = response.optJSONArray("results");
            //get object from each array element
            for (int i = 0; i < resultJSON.length(); i++) {
                JSONObject finalValues = resultJSON.getJSONObject(i);
                title = finalValues.getString("webTitle");
                //separate the author name
                if (title.contains(separator)) {
                    int separatorIndex = title.indexOf(separator);
                    //get my title
                    for (int t = 0; t < separatorIndex; t++) {
                        title2 += title.charAt(t);
                    }
                    //get my author
                    for (int r = (separatorIndex + 1); r < title.length(); r++) {
                        articleAuthor += title.charAt(r);
                    }
                    Log.d("xyz", title2);
                    Log.d("xyz", articleAuthor);
                } else {
                    Log.d("xyz", "Only has title");
                }
                //get other values
                text = finalValues.getString("sectionId");
                date = finalValues.getString("webPublicationDate");
                url = finalValues.getString("webUrl");
                Log.d("xyz", String.valueOf(title));
                Log.d("xyz", String.valueOf(text));
                /**
                 * found out it always has a date but kept the possibility of not having it in the rest of the code for
                 * the purpose of fulfilling all the criteria
                 */
                Log.d("xyz", String.valueOf(date));
                //add all my info into the app
                int hasAuthor = title2.length();
                if (hasAuthor > 0) {
                    testHistory.add((new NewsObject(title2, text, articleAuthor, date, url)));
                } else {
                    testHistory.add((new NewsObject(title, text, date, 2, url)));
                }
                title2 = "";
                articleAuthor = "";
            }
            ;

            Toast.makeText(this, R.string.success, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            errorMessage();
            Log.d("xyz", e.toString());
        }
    }

    public void errorMessage() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
    }

    class NewsAsyncTask extends AsyncTask {
        Context context;
        String url;

        public NewsAsyncTask(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            JsonLoader jsonLoader = new JsonLoader(context, url);
            String result = jsonLoader.loadInBackground();
            serverResponse = result;
            return serverResponse;
        }


        @Override
        protected void onPostExecute(Object o) {
            noDataToShow.setVisibility(View.GONE);

            parseGuardianJson(serverResponse);

            RecyclerView myListView = findViewById(R.id.list);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

            myListView.setLayoutManager(layoutManager);

            NewsAdapter adapter = new NewsAdapter(getApplicationContext(), testHistory);

            myListView.setAdapter(adapter);

            super.onPostExecute(o);
        }
    }
}
