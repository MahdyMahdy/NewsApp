package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

public class SearchFragment extends Fragment {

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ProfileActivity.actionBar.hide();
        EditText search= Objects.requireNonNull(getActivity()).findViewById(R.id.searchEditText);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    LinearLayout linearLayout= Objects.requireNonNull(getActivity()).findViewById(R.id.linear);
                    linearLayout.removeAllViews();
                    for(int i=0;i<ProfileActivity.news.size();i++)
                    {
                        if(!TextUtils.isEmpty(s) && ProfileActivity.news.get(i).getSearchTitle().contains(s.toString().toLowerCase()))
                        {
                            TextView title=new TextView(getContext());
                            linearLayout.addView(title);
                            View line=new View(getContext());
                            line.setMinimumHeight(10);
                            linearLayout.addView(line);
                            title.setMinimumHeight(100);
                            title.setText(ProfileActivity.news.get(i).getTitle());
                            title.setTextSize(20);
                            title.setTextColor(Color.BLACK);
                            title.setBackground(getResources().getDrawable(R.color.white));
                            int finalI = i;
                            title.setOnClickListener(v -> {
                                ProfileActivity.selectedNews=ProfileActivity.news.get(finalI);
                                FragmentManager manager=getFragmentManager();
                                assert manager != null;
                                FragmentTransaction transaction=manager.beginTransaction();
                                transaction.replace(R.id.cat,new NewsFragment());
                                transaction.addToBackStack(null);
                                transaction.commit();
                            });
                        }

                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ProfileActivity.actionBar.show();
    }

}