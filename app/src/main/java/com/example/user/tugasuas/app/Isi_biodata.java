package com.example.user.tugasuas.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import android.widget.DatePicker;
import javax.net.ssl.HttpsURLConnection;
import android.app.DatePickerDialog;

import com.example.user.tugasuas.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class Isi_biodata extends AppCompatActivity {
    Button btn_logout, GetImageFromGalleryButton, UploadImageOnServerButton, bt_datepicker;
    ImageView ShowSelectedImage;
    EditText imageName,nim,nohp,tgl_result ;
    TextView txt_username;
    Intent intent;
    String username;
    SharedPreferences sharedpreferences;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    Bitmap FixBitmap;
    String ImageTag = "image_tag" ;
    String ImageName = "image_data" ;
    String Nim = "nim" ;
    String NoHp = "nohp";
    String Tanggal = "tanggal";
    ProgressDialog progressDialog ;
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;
    String GetImageNameFromEditText;
    String GetNimFromEditText;
    String GetNoHpFromEditText;
    String GetTanggalEditText;

    HttpURLConnection httpURLConnection ;
    URL url; OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC ;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY = 1, CAMERA = 2;
    public static final String TAG_USERNAME = "username";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.isi_biodata);

        txt_username = (TextView) findViewById(R.id.txt_username);
        username = getIntent().getStringExtra(TAG_USERNAME);
        txt_username.setText("USERNAME : " + username);

        GetImageFromGalleryButton = (Button) findViewById(R.id.buttonSelect);
        UploadImageOnServerButton = (Button) findViewById(R.id.buttonUpload);
        ShowSelectedImage = (ImageView) findViewById(R.id.imageView);
        imageName = (EditText) findViewById(R.id.imageName);
        byteArrayOutputStream = new ByteArrayOutputStream();
        nim = (EditText) findViewById(R.id.nim);
        nohp = (EditText) findViewById(R.id.nohp);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        tgl_result = (EditText) findViewById(R.id.tgl_result);
        bt_datepicker = (Button) findViewById(R.id.bt_datepicker);

        btn_logout = (Button) findViewById(R.id.btn_logout);
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });
        bt_datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo auto generated methode sub
                //update login session ke False dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.putString(TAG_USERNAME, null);
                editor.commit();

                Intent intent = new Intent(Isi_biodata.this, Login.class);
                finish();
                startActivity(intent);
            }
        });
        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetImageNameFromEditText = imageName.getText().toString();
                GetNimFromEditText = nim.getText().toString();
                GetNoHpFromEditText = nohp.getText().toString();
                GetTanggalEditText = tgl_result.getText().toString();
                UploadImageToServer();
            }
        });
        if (ContextCompat.checkSelfPermission(Isi_biodata.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
            }
        }
    }
    private void showDateDialog(){
        /**
         * Calender untuk menempatkan tanggal sekatrang
         */
        Calendar newCalendar=Calendar.getInstance();
        /**
         * Initiate datetime picker
         */
        datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                /**
                 * methode ini dipanggil setelah kita memilih tanggal didate picker
                 */
                /**
                 * set calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                /**
                 * Update Textview dengan tanggal yang kita pilih
                 */
                tgl_result.setText(dateFormatter.format(newDate.getTime()));
            }
        } ,newCalendar.get(Calendar.YEAR),newCalendar.get(Calendar.MONTH),newCalendar.get(Calendar.DAY_OF_MONTH));
        /**
         * tampil data picker
         */
        datePickerDialog.show();
    }
    private void  showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Camera"};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: takePhotoFromCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }
    private void takePhotoFromCamera(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED){
            return;
        } if (requestCode == CAMERA){
            FixBitmap = (Bitmap) data.getExtras().get("data");
            ShowSelectedImage.setImageBitmap(FixBitmap);
            UploadImageOnServerButton.setVisibility(View.VISIBLE);
            //Toast.makeText(ShadiRegistrationPart5.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }
    public void UploadImageToServer(){
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        class AsyncTaskUploadClass extends  AsyncTask<Void,Void,String>{
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                progressDialog=ProgressDialog.show(Isi_biodata.this,"Data sedang dikirim" ,"Please Wait",false,false);
            }
            @Override
            protected void  onPostExecute(String string1){
                super.onPostExecute(string1);
                progressDialog.dismiss();
                Toast.makeText(Isi_biodata.this,string1,Toast.LENGTH_LONG).show();
                nim.setText("");
                nohp.setText("");
                imageName.setText("");
                tgl_result.setText("");
                ShowSelectedImage.setImageBitmap(null);
            }
            @Override
            protected String doInBackground(Void...Params){
                ImageProccessClass imageProcessClass = new ImageProccessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put(ImageTag, GetImageNameFromEditText);
                HashMapParams.put(Nim, GetNimFromEditText);
                HashMapParams.put(NoHp, GetNoHpFromEditText);
                HashMapParams.put(Tanggal, GetTanggalEditText);
                HashMapParams.put(ImageName, ConvertImage);
                String FinalData = imageProcessClass.ImageHttpRequest("http://sasmitoh.nitarahmawati.my.id/upload-image-to-server.php/", HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ =new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }
    public class ImageProccessClass {
        public String ImageHttpRequest(String requestURL,HashMap<String,String>PData){
            StringBuilder stringBuilder = new StringBuilder();
            try{
                url = new URL(requestURL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                outputStream = httpURLConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(bufferedWriterDataFN(PData));
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                RC = httpURLConnection.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK){
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReader.readLine()) != null){
                        stringBuilder.append(RC2);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }
        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException{
            stringBuilder = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()){
                if (check) check = false;
                else
                    stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF8"));


            }return stringBuilder.toString();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //now user should be able to use camera
            }else {
                Toast.makeText(Isi_biodata.this, "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();

            }

        }
    }

    public void main(View view){
        Intent intent=new Intent (Isi_biodata.this,MainActivity.class);
        intent.putExtra(TAG_USERNAME, username);
        finish();
        startActivity(intent);
    }
}
