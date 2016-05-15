package com.example.ahmeda.uberdriverapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Profile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    Button req;
    String status;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getItemAtPosition(position).toString().equals("Available")){
            status = "available";
            Toast.makeText(this.getApplicationContext(), "Available mode Activated", Toast.LENGTH_LONG).show();

        }
        else{
            status = "busy";
            Toast.makeText(this.getApplicationContext(),"Busy mode Activated", Toast.LENGTH_LONG).show();

        }
        setDriverStatus setStatus = new setDriverStatus();
        setStatus.execute();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        req = (Button) findViewById(R.id.button1);
        sharedPref = getSharedPreferences("uber", 0);
        editor = sharedPref.edit();

            TextView text = (TextView) findViewById(R.id.textView);
            text.setText(sharedPref.getString("name", null));

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            spinner.setOnItemSelectedListener(this);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.statuses, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            req.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.item1){
            editor.clear().commit();
            // editor.commit();
            Intent i = new Intent(Profile.this, Login.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class setDriverStatus extends AsyncTask<Void, Void, Void> {
        String myUrl = "http://uberlikeapp-ad3rhy2.rhcloud.com/api/driver/updateDriverStatus";
        StringBuilder stringBuilder;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(myUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.setRequestProperty("status", status);
                    urlConnection.setRequestProperty("driver_id", sharedPref.getString("id", null));

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);

            }
            return null;
        }

        // private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(getBaseContext(), stringBuilder, Toast.LENGTH_LONG).show();

        }
    }
}
