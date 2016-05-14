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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {
    private Button signup;
    private Button login;
    private EditText mail;
    private EditText pass;
    private String mail_;
    private String pass_;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = getSharedPreferences("uber", 0);
        editor = sharedPref.edit();

        signup =(Button) findViewById(R.id.signupButton);
        login = (Button) findViewById(R.id.loginButton);
        mail = (EditText) findViewById(R.id.mail);
        pass = (EditText) findViewById(R.id.pass);
        //    Toast.makeText(getApplicationContext(), sharedPref.getString("name", null), Toast.LENGTH_LONG).show();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail_ = mail.getText().toString();
                pass_ = pass.getText().toString();
                if(!mail_.equals("")){
                    if(!pass_.equals("")){
                        loginUser loginuser = new loginUser();
                        loginuser.execute();

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please your password",
                                Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please enter your email",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, SignUp.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }
    class loginUser extends AsyncTask<Void, Void, Void> {
        String myUrl = "http://uberlikeapp-ad3rhy2.rhcloud.com/api/user/login";
        StringBuilder stringBuilder;
        String jsonId;
        boolean valid;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(myUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("email", mail_);
                    urlConnection.setRequestProperty("password", pass_);
                    urlConnection.setRequestProperty("type", "driver");

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
                    jsonId= stringBuilder.toString();
                    setID();
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

        protected void setID() throws JSONException {
            JSONObject jObject = new JSONObject(jsonId);
            valid = jObject.getBoolean("valid");
            if(valid == true){
                id = jObject.getString("driver_id");
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // sharedPref.getString("name", null);
            if(valid) {
                editor.putBoolean("loggedIn", true);
                editor.putString("id", id);
                editor.commit();
                Intent i = new Intent(Login.this, Profile.class);
                startActivity(i);
                //loginName login_name = new loginName();
                //login_name.execute();
            }
            else{
                Toast.makeText(getApplicationContext(), stringBuilder, Toast.LENGTH_LONG).show();
            }
        }
    }

    class loginName extends AsyncTask<Void, Void, Void> {
        String myUrl = "http://uberlikeapp-ad3rhy2.rhcloud.com/api/driver/getDriverData";
        StringBuilder stringBuilder;
        String jsonId;
        String driver_name;
        boolean valid;
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                URL url = new URL(myUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("GET");
                    //  Toast.makeText(getApplicationContext(), id , Toast.LENGTH_LONG).show();
                    urlConnection.setRequestProperty("driver_id", "57360f02f0d0d86819181e4c");

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    jsonId= stringBuilder.toString();
                    setID();
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

        protected void setID() throws JSONException {
            JSONObject jObject = new JSONObject(jsonId);
            valid = jObject.getBoolean("valid");
            if(valid == true){
                JSONObject userData = new JSONObject(jObject.getString("user_data"));
                driver_name= userData.getString("name");
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // sharedPref.getString("name", null);
            if(valid) {
                editor.putString("name", driver_name);
                editor.commit();

                Toast.makeText(getBaseContext(), stringBuilder, Toast.LENGTH_LONG).show();
                Intent i = new Intent(Login.this, Profile.class);
                startActivity(i);
            }
            else{
                Toast.makeText(getBaseContext(), stringBuilder, Toast.LENGTH_LONG).show();
            }
        }
    }
}