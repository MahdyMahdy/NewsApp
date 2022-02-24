package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login_Fragment extends Fragment implements View.OnClickListener {

    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login__fragment,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar= Objects.requireNonNull(getActivity()).findViewById(R.id.progress);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()) {

                String id=FirebaseAuth.getInstance().getUid();
                assert id != null;
                FirebaseFirestore.getInstance().collection("users").document(id).get().
                        addOnSuccessListener(documentSnapshot -> {
                            String role= Objects.requireNonNull(documentSnapshot.get("Role")).toString();
                            if(role.equals("") || role.equals("user"))
                            {
                                startActivity(new Intent(getContext(),ProfileActivity.class));
                                Objects.requireNonNull(getActivity()).finish();

                            }
                            else if(role.equals("admin"))
                            {
                                startActivity(new Intent(getContext(),AdminActivity.class));
                                Objects.requireNonNull(getActivity()).finish();
                            }
                        });
            }
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
        Button loginButton= getActivity().findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        TextView register = getActivity().findViewById(R.id.register);
        register.setOnClickListener(v -> {
            FragmentManager manager= Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            FragmentTransaction transaction=manager.beginTransaction();
            SignupFragment signupFragment=new SignupFragment();
            transaction.replace(R.id.start,signupFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public void onClick(View v) {
        EditText emailEditText= Objects.requireNonNull(getActivity()).findViewById(R.id.email);
        String email=emailEditText.getText().toString().trim();
        EditText passwordEditText= getActivity().findViewById(R.id.password);
        String password=passwordEditText.getText().toString().trim();
        TextView enterYourEmail= Objects.requireNonNull(getActivity()).findViewById(R.id.enterEmail);
        TextView enterYourPassword=getActivity().findViewById(R.id.enterPassword);
        if(TextUtils.isEmpty(email)) {
            enterYourEmail.setVisibility(TextView.VISIBLE);
            return; }
        else
            enterYourEmail.setVisibility(TextView.INVISIBLE);
        if(TextUtils.isEmpty(password)) {
            enterYourPassword.setVisibility(TextView.VISIBLE);
            return; }
        else
            enterYourPassword.setVisibility(TextView.INVISIBLE);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()) {
                            String id=FirebaseAuth.getInstance().getUid();
                            assert id != null;
                            FirebaseFirestore.getInstance().collection("users").document(id).get().
                                    addOnSuccessListener(documentSnapshot -> {
                                        String role= Objects.requireNonNull(documentSnapshot.get("Role")).toString();
                                        if(role.equals("user"))
                                        {
                                            startActivity(new Intent(getContext(),ProfileActivity.class));
                                            Objects.requireNonNull(getActivity()).finish();
                                        }
                                        else if(role.equals("admin"))
                                        {
                                            startActivity(new Intent(getContext(),AdminActivity.class));
                                            Objects.requireNonNull(getActivity()).finish();
                                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                                        }
                                    });

                        }
                        else {
                            FirebaseAuth.getInstance().signOut();
                            AlertDialog.Builder alert=new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            alert.setMessage("your email is not verified");
                            alert.setTitle("Please verify your email");
                            alert.setPositiveButton("ok",null);
                            alert.show();
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                        }
                    }
                    else {
                        TextView forgot= Objects.requireNonNull(getActivity()).findViewById(R.id.forgot);
                        forgot.setVisibility(TextView.VISIBLE);
                        forgot.setEnabled(true);
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        forgot.setOnClickListener(v1 -> {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email);
                            AlertDialog.Builder alert=new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            alert.setTitle("Reset your password");
                            alert.setMessage("Check your email inbox to reset the password");
                            alert.setPositiveButton("ok",null);
                            alert.show();
                        });
                    }
                });

    }

}