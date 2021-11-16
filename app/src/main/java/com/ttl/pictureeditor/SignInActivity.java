package com.ttl.pictureeditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    TextView continue_to_home;
    private ProgressBar signInProgress;
    FirebaseUser currentUser;

    FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    ProgressDialog dialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseApp.initializeApp(this);

        dialogue = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        findViewById(R.id.textSignUp).setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        inputEmail    = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn  = findViewById(R.id.buttonSignIn);
        continue_to_home = findViewById(R.id.textContinue);

        continue_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMainActivity();
            }
        });

        buttonSignIn.setOnClickListener(view -> {
            signIn();
        });

    }

    private void signIn() {

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            inputEmail.setError("Enter Valid Email");
            inputEmail.requestFocus();
            return;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("Enter Valid Email");
            inputEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)){
            inputPassword.setError("Enter Valid Password");
            inputPassword.requestFocus();
            return;
        }else if (password.length()<6){
            inputPassword.setError("Enter more than 6 character");
            inputPassword.requestFocus();
            return;
        }

        dialogue.setTitle("Signing in...");
        dialogue.setMessage("Just a moment, Logging you in Picture Editor");
        dialogue.setCanceledOnTouchOutside(true);
        dialogue.show();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(SignInActivity.this, ""+currentUser, Toast.LENGTH_SHORT).show();

                if (task.isSuccessful()){
                   
                    Toast.makeText(SignInActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    dialogue.dismiss();
                    sendToMainActivity();
                }else{
                    dialogue.dismiss();
                    String err = task.getException().toString();
                    AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
                    alertDialog.setTitle("Login Failed");
                    alertDialog.setMessage("Your entered Email or Password is incorrect. Please try again.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    inputPassword.setText("");
                }
            }
        });

    }

    private void sendToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}