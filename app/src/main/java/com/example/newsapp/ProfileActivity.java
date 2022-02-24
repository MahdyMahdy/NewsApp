package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ProfileActivity extends AppCompatActivity {

    static ArrayList<News> news;
    static ArrayList<News> health;
    static ArrayList<News> sports;
    static ArrayList<News> policy;
    static ArrayList<News> technology;
    static News selectedNews;
    static ActionBar actionBar;
    static int n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ProfileActivity.n==0) {
            ProfileActivity.n = 1;
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        if(health==null)
        {
            ProfileActivity.news=new ArrayList<>();
            health = CategoryFragment.getNews("Health");
            policy = CategoryFragment.getNews("Policy");
            technology = CategoryFragment.getNews("Technology");
            sports = CategoryFragment.getNews("Sports");
        }
        setContentView(R.layout.activity_profile);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.cat,new CategoryFragment());
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        if(item.getItemId()==R.id.search)
        {
            transaction.replace(R.id.cat,new SearchFragment());
        }
        else
        {
            transaction.replace(R.id.cat,new SettingsFragment());
        }
        transaction.addToBackStack(null);
        transaction.commit();
        return super.onOptionsItemSelected(item);
    }
}