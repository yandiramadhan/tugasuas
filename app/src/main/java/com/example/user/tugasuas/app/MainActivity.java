package com.example.user.tugasuas.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.tugasuas.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView biorc;
    TextView txt_username;
    Intent intent;
    String username;
    Button btn_logout;
    SharedPreferences sharedpreferences;
    public final static String TAG_USERNAME = "username";

    private RequestQueue requestQueue;
    private StringRequest stringRequest;

    ArrayList<HashMap<String, String>> list_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        biorc = (RecyclerView) findViewById(R.id.biorec);
        btn_logout=(Button)findViewById(R.id.btn_logout) ;
        txt_username=(TextView)findViewById(R.id.txt_username) ;
        username =getIntent().getStringExtra(TAG_USERNAME);
        txt_username.setText("USERNAME:"+  username);
        sharedpreferences=getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // todo auto generated methode sub
                //update login session ke False dan mengosongkan nilai id dan username
                SharedPreferences.Editor  editor = sharedpreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.putString(TAG_USERNAME, null);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, Login.class);
                finish();
                startActivity(intent);
            }
        });
        String url = "http://sasmitoh.nitarahmawati.my.id/getdata.php";


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        biorc.setLayoutManager(llm);

        requestQueue = Volley.newRequestQueue(MainActivity.this);

        list_data = new ArrayList<HashMap<String, String>>();

        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("biodata");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("id", json.getString("uid"));
                        map.put("image_path", json.getString("image_path"));
                        map.put("image_name", json.getString("image_name"));
                        map.put("nim", json.getString("nim"));
                        map.put("nohp", json.getString("nohp"));
                        map.put("tanggal", json.getString("tanggal"));
                        list_data.add(map);
                        AdapterList adapter = new AdapterList(MainActivity.this, list_data);
                        biorc.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }


    public void tambah (View view){
        Intent intent=new Intent(MainActivity.this,Isi_biodata.class);
        intent.putExtra(TAG_USERNAME,username);
        finish();
        startActivity(intent);
    }

    public void main(View view) {
        Intent intent=new Intent (MainActivity.this,Isi_biodata.class);
        intent.putExtra(TAG_USERNAME, username);
        finish();
        startActivity(intent);
    }
}
