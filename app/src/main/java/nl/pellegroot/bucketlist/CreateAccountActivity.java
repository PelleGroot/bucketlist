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
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
    in this activity the user can create a new account based on their email and password
 */

public class CreateAccountActivity extends AppCompatActivity {

    private EditText emailaddressField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private FirebaseDatabase database;
    private String curUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // get connection to the database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");

        //set the buttons and fields
        emailaddressField = (EditText) findViewById(R.id.ca_email);
        passwordField = (EditText) findViewById(R.id.ca_password);
        confirmPasswordField = (EditText) findViewById(R.id.ca_confirm_password);
        createAccountButton = (Button) findViewById(R.id.ca_create_account);

        // set on click lilstener on the create account button
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount(){

        // get all the needed information from the user
        String emailAddress = emailaddressField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        // check if all the fields are filled in
        if (TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(CreateAccountActivity.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
        }

        // check if the user gave two same passwords
        else if(!password.equals(confirmPassword)){
            Toast.makeText(CreateAccountActivity.this, "Passwords don't match", Toast.LENGTH_LONG).show();
        }

        // if all the checks above are correct, continue creating user
        else{
            mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        FirebaseAuthException e = (FirebaseAuthException)task.getException();
                        Toast.makeText(CreateAccountActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    // if task is successful, create account and log in
                    if(task.isSuccessful()){
                        FirebaseUser User = mAuth.getCurrentUser();
                        curUserId = User.getUid();
                        userRef.setValue(curUserId);
                        userRef.child(curUserId).setValue("bucketlist");

                        startActivity(new Intent(CreateAccountActivity.this, BucketlistActivity.class));
                    }
                }
            });
        }
    }
}