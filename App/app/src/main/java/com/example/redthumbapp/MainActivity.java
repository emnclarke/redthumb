package com.example.redthumbapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int requestFlag = 0;
    ArrayList<PlantFeedData> plantFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Attempt to load view based on plantFeed
        loadPlantDataView();

        //Enable refresh on pull-down
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                loadPlantDataView();
            }
        });

    }

    private void getPotList() {
        //Get pot list
        requestFlag = 0; //Set the response flag to be expecting a PotList response.
        HTTPGetRequest request = new HTTPGetRequest();
        request.execute("?request=requestPots");
    }

    private void getPotsData(JSONArray potList) {
        requestFlag = 1; //Set the response flag to be expecting a potData response.
        for (Object p : potList) {
            if (p instanceof org.json.simple.JSONObject) {
                JSONObject pot = (JSONObject) p;
                HTTPGetRequest requestPotData = new HTTPGetRequest();
                requestPotData.execute("?request=requestCompleteDataPot&arg1=" + pot.get("pot_id"));
            }else{
                Context context = getApplicationContext();
                CharSequence text = "Data load error";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return;
            }
        }

    }


    private void getPotData(JSONObject plantData) {
        System.out.println(plantData.get("potData"));
        PlantData newPlantData = new PlantData((JSONArray) plantData.get("plantData"), (JSONObject) plantData.get("plantTypeData"), (JSONObject) plantData.get("potData"));
        plantFeed.add(new PlantFeedData(newPlantData));
    }

    private void loadPlantDataView() {

        RecyclerView plantDataRV = findViewById(R.id.plantDataRV);

        //Attempt to load plant data;
        getPotList();

        if (plantFeed != null) {
            PlantDataAdapter adapter = new PlantDataAdapter(plantFeed);
            plantDataRV.setAdapter(adapter);
            plantDataRV.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Data load error";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Switch statement for convention only.
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View v) {
    }

    public void httpResponse(String data) {
        if (data.equals("error")) {
            Context context = getApplicationContext();
            CharSequence text = "Data could not be loaded";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            //Expecting pot List
            try {
                if (data.endsWith("end")) {
                    data = data.substring(0, data.length() - 3);
                } else {
                    System.out.println("Data is truncated!");
                    return;
                }
                org.json.simple.parser.JSONParser parser = new JSONParser();
                Object p = parser.parse(data);
                if (p instanceof org.json.simple.JSONArray) {
                    org.json.simple.JSONArray object = (JSONArray) p;
                    if (requestFlag == 0) {
                        getPotsData(object);
                        return;
                    }
                } else if (p instanceof org.json.simple.JSONObject) {
                    org.json.simple.JSONObject object = (JSONObject) p;
                    getPotData(object);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return;
        }

    }


    /**
     * A asyncTask which calls a http GET request.
     */
    public class HTTPGetRequest extends AsyncTask<String, String, String> {
        private static final String HUB_SERVER = "http://192.168.43.85:3000/";//FIXME:
        static final String REQUEST_METHOD = "GET";
        static final int READ_TIMEOUT = 5000;
        static final int CONNECTION_TIMEOUT = 15000;


        @Override
        protected String doInBackground(String... request) {
            String data;
            String inputLine;

            try {
                //Connect to server
                URL hubURL = new URL(HUB_SERVER + request[0]);
                HttpURLConnection connection = (HttpURLConnection) hubURL.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();

                //Get data
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                bufferedReader.close();
                streamReader.close();
                data = stringBuilder.toString();

            } catch (IOException e) {
                data = "error";
            }
            return data;
        }

        protected void onPostExecute(String data) {
            httpResponse(data);
            super.onPostExecute(data);
        }
    }



}

