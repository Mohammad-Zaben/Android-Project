package com.example.androidproject.LoginAndRegister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidproject.R;
import com.example.androidproject.home.HomeActivity;
import com.example.androidproject.model.Student;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ImageView regesterImg;
    private static final int CHOOSE_IMAGE = 101;
    Uri uriImg;
    Button button;
    ArrayList<Student> studentList = new ArrayList<>();
    private RequestQueue queue;
    private EditText studentid;
    private EditText pass;
    private CheckBox remCh;
    public static String id;


    private ShapeableImageView userImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        studentid=findViewById(R.id.studentID);
        pass=findViewById(R.id.pass);
        remCh=findViewById(R.id.rememberMe);
        queue = Volley.newRequestQueue(this);

        setupSharedPrefs();
        checkRememberMe();
        LoadData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData();
    }

    public void ActionLogin(View view) {
        int isExist= Check(); // to check the input data if exist or no
        if(isExist==1){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            Toast.makeText(LoginActivity.this, "يا هلا و مرحبا، نورت 😁",
                    Toast.LENGTH_LONG).show();
        }else if(isExist==0){
            Toast.makeText(LoginActivity.this, "الرقم السري خطأ يا غالي 💔",
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(LoginActivity.this, "فش عنا حساب بهاض الرقم للاسف 😒",
                    Toast.LENGTH_LONG).show();
        }

        if (remCh.isChecked()) {
            editor.putString("ID",studentid.getText().toString() );
            editor.putString("PASS", pass.getText().toString());
            editor.putBoolean("FLAG", true);
            editor.commit();
        }
    }

    public void ActionRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, Register.class);
        startActivity(intent);

    }

    private void LoadData() {

        String url = "http://10.0.2.2:5000/getStudent";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject studentJson = response.getJSONObject(i);

                                String name = studentJson.getString("studentName");
                                 id = studentJson.getString("studentID");
                                String email = studentJson.getString("studentEmail");
                                String pass = studentJson.getString("studentPassword");

                                Student student = new Student(name, id, email, pass);

                                studentList.add(student);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        queue.add(jsonArrayRequest);


    }
    private int Check(){
        String EditTxtID=studentid.getText().toString();
        String EditTxtPass=pass.getText().toString();
        for(Student student:studentList){
            if(student.getStudentID().equals(EditTxtID)) {
                if (student.getPass().equals(EditTxtPass)) {
                    return 1;
                }
                return 0;
            }
        }
        return -1;
    }

    private void setupSharedPrefs() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
    }

    private void checkRememberMe() {
        boolean flag = prefs.getBoolean("FLAG", false);

        if (flag) {
            String name = prefs.getString("ID", "");
            String password = prefs.getString("PASS", "");
            studentid.setText(name);
            pass.setText(password);
            remCh.setChecked(true);
        }
    }
}