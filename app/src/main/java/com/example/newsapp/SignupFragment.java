package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupFragment extends Fragment {

    public SignupFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.signup_fragment,container,false);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {

        super.onStart();

        EditText emailEditText   =   Objects.requireNonNull(getActivity()).findViewById(R.id.emailRegister);
        EditText password1EditText    =   getActivity().findViewById(R.id.passwordRegister1);
        EditText password2EditText   =   getActivity().findViewById(R.id.passwordRegister2);
        ProgressBar progressBar     =   getActivity().findViewById(R.id.signupprogress);
        EditText firstNameEditText   =   getActivity().findViewById(R.id.firstname);
        EditText lastNameEditText  =   getActivity().findViewById(R.id.lastname);
        EditText phoneNumberEditText   =   getActivity().findViewById(R.id.phone);
        TextView passwordErrorEditText   =   getActivity().findViewById(R.id.passworderror);
        TextView emailErrorEditText      =   getActivity().findViewById(R.id.emailerror);
        Button signupButton            =   getActivity().findViewById(R.id.signup);
        TextView passwordError1EditText  =   getActivity().findViewById(R.id.passworderror1);

        signupButton.setOnClickListener(v -> {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            String firstName=firstNameEditText.getText().toString().trim();
            String lastName=lastNameEditText.getText().toString().trim();
            String phoneNumber=phoneNumberEditText.getText().toString().trim();
            String email=emailEditText.getText().toString().trim();
            String password1=password1EditText.getText().toString().trim();
            String password2=password2EditText.getText().toString().trim();
            if(email.equals("") || !email.contains("@") || !email.contains(".com")) {
                emailErrorEditText.setText("Please Enter a valid email address");
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                return;
            }
            else {
                emailErrorEditText.setText("");
            }
            if(password1.equals(""))
            {
                passwordError1EditText.setText("Password can not be null");
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                return;
            }
            else
            {
                passwordError1EditText.setText("");
            }
            if(!password1.equals(password2))
            {
                passwordErrorEditText.setText("The 2 Passwords must be the same");
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                return;
            }
            else
            {
                passwordErrorEditText.setText("");
            }
            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,password1).
                    addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            DocumentReference documentReference=FirebaseFirestore.getInstance().collection("users").
                                    document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                            Map<String,Object> user = new HashMap<>();
                            user.put("FirstName",firstName);
                            user.put("LastName",lastName);
                            user.put("Email",email);
                            user.put("PhoneNumber",phoneNumber);
                            user.put("Role","user");
                            documentReference.set(user);
                            Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification().
                                    addOnCompleteListener(task1 -> {
                                        AlertDialog.Builder alert=new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                                        alert.setTitle("Verification email sent to your inbox");
                                        alert.setMessage("Verify your email an login to continue");
                                        alert.setPositiveButton("ok",null);
                                        alert.show();
                                    });
                        }
                        else
                        {
                            AlertDialog.Builder alert=new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            alert.setMessage("Email already registered");
                            alert.setPositiveButton("ok",null);
                            alert.show();
                        }
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    });
        });
    }
}