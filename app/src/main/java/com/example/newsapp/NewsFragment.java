package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewsFragment extends Fragment {

    public NewsFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_fragment,container,false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ProfileActivity.actionBar.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        ProfileActivity.actionBar.hide();
        TextView title= Objects.requireNonNull(getActivity()).findViewById(R.id.news_title);
        title.setText(ProfileActivity.selectedNews.getTitle());
        TextView content = Objects.requireNonNull(getActivity()).findViewById(R.id.news_content);
        content.setText(ProfileActivity.selectedNews.getContent());
        TextView like=getActivity().findViewById(R.id.like);
        TextView likesCount=getActivity().findViewById(R.id.likesCount);
        like.setOnClickListener(v->{
            if(like.getText().equals("Like"))
            {
                like.setText("Liked");
                like.setTextColor(Color.BLUE);
                int count=Integer.parseInt(likesCount.getText().toString());
                count++;
                likesCount.setText(""+count);
                Map<String,Object> map=new HashMap<>();
                map.put(FirebaseAuth.getInstance().getUid(),count);
                FirebaseFirestore.getInstance().collection("likes").document(ProfileActivity.selectedNews
                        .getTitle()).get().addOnSuccessListener(documentSnapshot -> {
                            if(documentSnapshot.exists())
                            {
                                FirebaseFirestore.getInstance().collection("likes").
                                        document(ProfileActivity.selectedNews.getTitle()).update(map);
                            }
                            else
                            {
                                FirebaseFirestore.getInstance().collection("likes").
                                        document(ProfileActivity.selectedNews.getTitle()).set(map);
                            }
                        });
            }
            else
            {
                like.setText("Like");
                like.setTextColor(Color.WHITE);
                int count=Integer.parseInt(likesCount.getText().toString());
                count--;
                likesCount.setText(""+count);
                Map<String,Object> map=new HashMap<>();
                map.put(FirebaseAuth.getInstance().getUid(), FieldValue.delete());
                FirebaseFirestore.getInstance().collection("likes").
                        document(ProfileActivity.selectedNews.getTitle()).update(map);
            }
        });
        FirebaseFirestore.getInstance().collection("likes").document(ProfileActivity.selectedNews.getTitle())
                .get().addOnSuccessListener(documentSnapshot -> {
                    like.setEnabled(true);
                    if(documentSnapshot.exists())
                    {
                        if(Objects.requireNonNull(documentSnapshot.getData()).containsKey(FirebaseAuth.getInstance().getUid()))
                        {
                            like.setText("Liked");
                            like.setTextColor(Color.BLUE);
                        }
                        else
                        {
                            like.setTextColor(Color.WHITE);
                        }
                        likesCount.setText(""+documentSnapshot.getData().size());
                    }
                    else
                    {
                        like.setTextColor(Color.WHITE);
                        likesCount.setText("0");
                    }
                });
        LinearLayout comments=getActivity().findViewById(R.id.comments);
        comments.removeAllViews();
        FirebaseFirestore.getInstance().collection("comments").document(ProfileActivity.selectedNews.getTitle())
                .get().addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists())
                    {
                        Object[] keys= Objects.requireNonNull(documentSnapshot.getData()).keySet().toArray();
                        for(int i=0;i<keys.length;i++)
                        {
                            TextView comment=new TextView(getContext());
                            String text=keys[i].toString()+" : "+documentSnapshot.get(keys[i].toString());
                            comment.setText(text);
                            comment.setTextSize(20);
                            comment.setTextColor(Color.WHITE);
                            comment.setGravity(Gravity.CENTER);
                            comments.addView(comment);
                        }
                    }
                });
        Button post=getActivity().findViewById(R.id.post);
        post.setOnClickListener(v -> FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get().addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists())
                    {
                        EditText comment = Objects.requireNonNull(getActivity()).findViewById(R.id.comment);
                        if(!TextUtils.isEmpty(comment.getText().toString()))
                        {
                            String name = Objects.requireNonNull(documentSnapshot.get("FirstName")).toString();
                            name+=" "+ Objects.requireNonNull(documentSnapshot.get("LastName")).toString();
                            Map<String,Object> map=new HashMap<>();
                            map.put(name,comment.getText().toString());

                            FirebaseFirestore.getInstance().collection("comments").document(ProfileActivity.selectedNews.getTitle())
                                    .get().addOnSuccessListener(documentSnapshot1 -> {
                                        if(documentSnapshot1.exists())
                                        {
                                            FirebaseFirestore.getInstance().collection("comments").document(ProfileActivity.selectedNews.getTitle())
                                                    .update(map);
                                        }
                                        else
                                        {
                                            FirebaseFirestore.getInstance().collection("comments").document(ProfileActivity.selectedNews.getTitle())
                                                    .set(map);
                                        }
                                    });
                            TextView textView=new TextView(getContext());
                            String text=name+ " : "+comment.getText().toString();
                            textView.setText(text);
                            textView.setTextSize(20);
                            textView.setTextColor(Color.WHITE);
                            textView.setGravity(Gravity.CENTER);
                            comments.addView(textView);
                        }
                    }
                }));
    }
}