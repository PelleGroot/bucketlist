package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

private EditText mEmailfield;
private EditText mPassword;
private Button mloginButton;
private Button createAccountButton;
private FirebaseAuth mAuth;
private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmailfield = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mloginButton = (Button) findViewById(R.id.signIn);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null){
//                    startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
                }
            }
        };

        mloginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startSignIn();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSignIn(){

        String emailAddress = mEmailfield.getText().toString();
        String password = mPassword.getText().toString();

        if(TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
        }else{
            mAuth.signInWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Sign in error", Toast.LENGTH_LONG).show();
                    }
                    if(task.isSuccessful()){
                        startActivity(new Intent(LoginActivity.this, bucketlistActivity.class));
                    }
                }
            });
        }
    }

}
