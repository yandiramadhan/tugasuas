package com.example.user.tugasuas.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;
import com.example.user.tugasuas.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by hendra purwandana on 29/07/2018.
 */

public class Login extends AppCompatActivity{
    ProgressDialog pDialog;
    Button btn_register, btn_login;
    EditText txt_username, txt_password;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL +"login_json.php";

    private static final String TAG = Login.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_USERNAME = "username";
    public final static String TAG_ID ="id";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedPreferences;
    Boolean session = false;
    String id, username;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        conMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo()!=null
                    &&
                    conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()){

            }else{
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

            }
        }
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_password = (EditText) findViewById(R.id.txt_password);

        sharedPreferences = getSharedPreferences(my_shared_preferences, Context .MODE_PRIVATE);
        session = sharedPreferences.getBoolean(session_status, false);
        id = sharedPreferences.getString(TAG_ID, null);
        username = sharedPreferences.getString(TAG_USERNAME, null);

        if (session){
            Intent intent = new Intent(Login.this,MainActivity.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_USERNAME, username);
            finish();
            startActivity(intent);
        }
        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String username = txt_username.getText().toString();
                String password = txt_password.getText().toString();

                if (username.trim().length() > 0 && password.trim().length() > 0){
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()){
                        checklogin (username, password);
                    }else{
                        Toast.makeText(getApplicationContext(),"No Internet Connection",
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText (getApplicationContext(), "Kolom Tidak Boleh Kosong",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                intent = new Intent(Login.this, Register.class);
                finish();
                startActivity(intent);
            }
        });
    }
    private void checklogin(final String username, final String password){
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Login in....");
        showDialog();

        StringRequest strReq  = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response){
                        Log.e(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        try {
                            JSONObject jobj= new JSONObject(response);
                            success = jobj.getInt(TAG_SUCCESS);

                            if (success == 1){
                                String username = jobj.getString(TAG_USERNAME);
                                String id = jobj.getString(TAG_ID);
                                Log.e("Succesfully Login", jobj.toString());

                                Toast.makeText(getApplicationContext(),
                                        jobj.getString(TAG_MESSAGE),Toast.LENGTH_LONG).show();

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean (session_status, true);
                                editor.putString (TAG_ID, id);
                                editor.putString (TAG_USERNAME, username);
                                editor.commit ();

                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra(TAG_ID, id);
                                intent.putExtra(TAG_USERNAME, username);
                                finish();
                                startActivity(intent);
                            }else {
                                Toast.makeText(getApplicationContext(), jobj.getString(TAG_MESSAGE),
                                        Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG,"Login Error" + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(),
                        Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }
        ){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    private void showDialog(){
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog(){
        if (!pDialog.isShowing())
            pDialog.dismiss();
    }
}

