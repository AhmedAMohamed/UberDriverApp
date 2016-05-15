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

public class SignUp extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private Button signup;
    private EditText name;
    private EditText mail;
    private EditText pass;
    private EditText mob;
    private EditText model;
    private EditText number;
    private EditText color;

    private String name_;
    private String mail_;
    private String pass_;
    private String mob_;
    private String model_;
    private String number_;
    private String color_;
    String lat ="31";
    String lng = "31";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        sharedPref =getSharedPreferences("uber", 0);
        editor = sharedPref.edit();

        signup = (Button) findViewById(R.id.button);
        name = (EditText) findViewById(R.id.name);
        mail =(EditText) findViewById(R.id.mail);
        pass = (EditText) findViewById(R.id.pass);
        mob = (EditText) findViewById(R.id.mob);
        model = (EditText) findViewById(R.id.model);
        number = (EditText) findViewById(R.id.number);
        color = (EditText) findViewById(R.id.color);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_ = name.getText().toString();
                mail_ = mail.getText().toString();
                pass_ = pass.getText().toString();
                mob_ = mob.getText().toString();
                model_ = model.getText().toString();
                number_ = number.getText().toString();
                color_ = color.getText().toString();

                if (!name_.equals("") && !mail_.equals("") &&
                        !pass_.equals("") &&
                        !mob_.equals("") &&
                        !model_.equals("") &&
                        !number_.equals("") &&
                        !color_.equals("")) {

                    ///// inner class instance
                    registerUser register = new registerUser();
                    register.execute();

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your data",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
    class registerUser extends AsyncTask<Void, Void, Void> {
        String myUrl = "http://uberlikeapp-ad3rhy2.rhcloud.com/api/user/signup";
        StringBuilder stringBuilder;
        boolean valid;
        String id;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(myUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("fullName", name_);
                    urlConnection.setRequestProperty("email", mail_);
                    urlConnection.setRequestProperty("password", pass_);
                    urlConnection.setRequestProperty("mobile", mob_);
                    urlConnection.setRequestProperty("lng", lng);
                    urlConnection.setRequestProperty("lat", lat);
                    urlConnection.setRequestProperty("type", "driver");
                    urlConnection.setRequestProperty("color", color_);
                    urlConnection.setRequestProperty("carNumber", number_);
                    urlConnection.setRequestProperty("model", model_);

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
                    String jsonId = stringBuilder.toString();
                    setID(jsonId);
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
        protected void setID(String jsonId) throws JSONException {
            JSONObject jObject = new JSONObject(jsonId);
            valid = jObject.getBoolean("valid");
            if(valid){
                id = jObject.getString("user_id");
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(valid) {
                editor.putString("name", name_);
                editor.putBoolean("loggedIn", true);
                editor.putString("id", id);
                editor.commit();

                Toast.makeText(getBaseContext(), stringBuilder, Toast.LENGTH_LONG).show();
                Intent i = new Intent(SignUp.this, Profile.class);
                //i.putExtra("name", name_);
                startActivity(i);
                finish();
            }
            else{
                Toast.makeText(getBaseContext(), stringBuilder, Toast.LENGTH_LONG).show();
            }
        }
    }
}
