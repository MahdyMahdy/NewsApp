package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Objects;

public class CategoryFragment extends Fragment {

    public CategoryFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_fragment,container,false);
    }


    @Override
    public void onStart() {
        super.onStart();
        Button heathButton = Objects.requireNonNull(getActivity()).findViewById(R.id.health);
        Button sportsButton = getActivity().findViewById(R.id.sport);
        Button technologyButton=getActivity().findViewById(R.id.technology);
        Button policyButton=getActivity().findViewById(R.id.policy);
        refresher(heathButton,ProfileActivity.health);
        refresher(sportsButton,ProfileActivity.sports);
        refresher(technologyButton,ProfileActivity.technology);
        refresher(policyButton,ProfileActivity.policy);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    public void refresher(Button b, ArrayList<News> news)
    {
        LinearLayout linearLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.news);
        b.setOnClickListener(v -> {
            linearLayout.removeAllViews();
            for(int i=news.size()-1;i>=0;i--)
            {
                TextView title=new TextView(getContext());
                title.setText(" "+news.get(i).getTitle());
                title.setTextSize(20);
                title.setTextColor(Color.BLACK);
                title.setBackground(getResources().getDrawable(R.color.white));
                linearLayout.addView(title);
                int finalI = i;
                title.setOnClickListener(v1 -> {
                    ProfileActivity.selectedNews=news.get(finalI);
                    FragmentManager manager=getFragmentManager();
                    assert manager != null;
                    FragmentTransaction transaction=manager.beginTransaction();
                    transaction.replace(R.id.cat,new NewsFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                });
                TextView body=new TextView(getContext());
                body.setTextSize(15);
                body.setTextColor(Color.GRAY);
                body.setBackground(getResources().getDrawable(R.color.white));
                String text;
                if(news.get(i).getContent().length()<45){
                    text=news.get(i).getContent();
                }
                else {
                    text = news.get(i).getContent().substring(0, 45)+"...";
                }
                body.setText(" "+text);
                linearLayout.addView(body);
                body.setOnClickListener(v1 -> {
                    ProfileActivity.selectedNews=news.get(finalI);
                    FragmentManager manager=getFragmentManager();
                    assert manager != null;
                    FragmentTransaction transaction=manager.beginTransaction();
                    transaction.replace(R.id.cat,new NewsFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                });
                View line=new View(getContext());
                line.setMinimumHeight(10);
                linearLayout.addView(line);
            }
        });
    }

    public static ArrayList<News> getNews(String category)
    {
        ArrayList<News> news= new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(category).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                News n=new News(Objects.requireNonNull(snapshot.getKey()), Objects.requireNonNull(snapshot.child("content").getValue()).toString());
                news.add(n);
                ProfileActivity.news.add(n);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        return news;
    }
}