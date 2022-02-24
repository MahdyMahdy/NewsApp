package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    private RequestQueue mRequestQue;
    private final String URL = "https://fcm.googleapis.com/fcm/send";
    private final String key ="AAAAO8mrJO4:APA91bEES8tMbKwiDR2UKpsZ5avNfRZXIaIWEjpCZLxPRBeZZOTXhdv49AXWD79FhumdNw1mUCiJQn4gNON3YVuT5vvZaoJuAiXzswaKME7xiwXy_u2sLtYwJU3pnt00AzJzd8jXG__w";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar myToolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(myToolbar);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String[] category = {"Health", "Policy", "Sports", "Technology"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, category);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        EditText titleEditText = findViewById(R.id.title);
        EditText contentEditText = findViewById(R.id.Text);
        Button send = findViewById(R.id.Send);
        mRequestQue = Volley.newRequestQueue(this);
        send.setOnClickListener(v-> {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                String cat = spinner.getSelectedItem().toString();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(this, "please enter a title", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(this, "please write in the body", Toast.LENGTH_LONG).show();
                    return;
                }
                titleEditText.setText("");
                contentEditText.setText("");
                Toast.makeText(this, "Data sent", Toast.LENGTH_LONG).show();
                firebaseDatabase.getReference(cat).child(title).child("content").setValue(content).addOnCompleteListener(task -> sendNotification(title,content,cat));
        });
        Button make=findViewById(R.id.make);
        make.setOnClickListener(v1->{
            EditText emailET=findViewById(R.id.newAdmin);
            String email=emailET.getText().toString().trim();
            if(!TextUtils.isEmpty(email))
            {
                FirebaseFirestore.getInstance().collection("users").get().
                        addOnSuccessListener(queryDocumentSnapshots -> {
                            for(int i=0;i<queryDocumentSnapshots.size();i++)
                            {
                                if(Objects.equals(queryDocumentSnapshots.getDocuments().get(i).get("Email").toString(), email))
                                {
                                    String id=queryDocumentSnapshots.getDocuments().get(i).getId();
                                    FirebaseFirestore.getInstance().collection("users").document(id).update("Role","admin");
                                }
                            }
                        });
            }
        });
    }
    private void sendNotification(String title,String body,String topic){
        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + topic);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", title);
            notificationObj.put("body", body);
            json.put("notification", notificationObj);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json,
                    response -> Log.d("MUR", "onResponse: "), error -> Log.d("MUR", "onError: " + error.networkResponse)) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key="+key);
                    return header;
                }
            };
            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.adminmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.adminLogout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else
        {
            startActivity(new Intent(this,ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);

    }
}