package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment,container,false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ProfileActivity.actionBar.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        ProfileActivity.actionBar.hide();
        SwitchCompat health= Objects.requireNonNull(getActivity()).findViewById(R.id.healthSwitch);
        onoff(health,"health");
        configure(health,"Health","health");
        SwitchCompat policy=getActivity().findViewById(R.id.policySwitch);
        configure(policy,"Policy","policy");
        onoff(policy,"policy");
        SwitchCompat sports=getActivity().findViewById(R.id.sportsSwitch);
        configure(sports,"Sports","sports");
        onoff(sports,"sports");
        SwitchCompat technology=getActivity().findViewById(R.id.technologySwitch);
        configure(technology,"Technology","technology");
        onoff(technology,"technology");
        SwitchCompat notifications=getActivity().findViewById(R.id.notificationsSwitch);
        onoff(notifications,"notification");
        notifications.setOnClickListener(v -> {
            SwitchCompat s=(SwitchCompat) v;
            SharedPreferences sharedPreferences=getActivity().
                    getSharedPreferences(FirebaseAuth.getInstance().getUid()+"switches",Context.MODE_PRIVATE);
            SharedPreferences.Editor e=sharedPreferences.edit();
            if(!s.isChecked())
            {
                e.putBoolean("notification",false);
                e.putBoolean("policy",false);
                e.putBoolean("sports",false);
                e.putBoolean("technology",false);
                e.putBoolean("health",false);
                health.setChecked(false);
                policy.setChecked(false);
                sports.setChecked(false);
                technology.setChecked(false);
                health.setEnabled(false);
                policy.setEnabled(false);
                sports.setEnabled(false);
                technology.setEnabled(false);
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Health");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Policy");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Sports");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("Technology");
            }
            else
            {
                e.putBoolean("notification",true);
                e.putBoolean("policy",true);
                e.putBoolean("sports",true);
                e.putBoolean("technology",true);
                e.putBoolean("health",true);
                health.setEnabled(true);
                policy.setEnabled(true);
                sports.setEnabled(true);
                technology.setEnabled(true);
                health.setChecked(true);
                policy.setChecked(true);
                sports.setChecked(true);
                technology.setChecked(true);
                FirebaseMessaging.getInstance().subscribeToTopic("Health");
                FirebaseMessaging.getInstance().subscribeToTopic("Policy");
                FirebaseMessaging.getInstance().subscribeToTopic("Sports");
                FirebaseMessaging.getInstance().subscribeToTopic("Technology");
            }
            e.apply();
        });
        Button logoutButton=getActivity().findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            startActivity(new Intent(getContext(),MainActivity.class));
            getActivity().finish();
        });
    }

    public void configure(SwitchCompat s,String topic,String name)
    {
        s.setOnClickListener(v -> {
            SharedPreferences sharedPreferences= Objects.requireNonNull(getActivity()).
                    getSharedPreferences(FirebaseAuth.getInstance().getUid()+"switches",Context.MODE_PRIVATE);
            SharedPreferences.Editor e=sharedPreferences.edit();
            if(s.isChecked())
            {
                e.putBoolean(name,true);
                FirebaseMessaging.getInstance().subscribeToTopic(topic);
            }
            else
            {
                e.putBoolean(name,false);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
            }
            e.apply();
        });
    }

    public void onoff(SwitchCompat s,String name)
    {
        SharedPreferences sharedPreferences= Objects.requireNonNull(getActivity()).
                getSharedPreferences(FirebaseAuth.getInstance().getUid()+"switches", Context.MODE_PRIVATE);
        boolean ison=sharedPreferences.getBoolean(name,false);
        s.setChecked(ison);
    }
}