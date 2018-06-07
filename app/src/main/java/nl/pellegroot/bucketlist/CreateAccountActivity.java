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

public class CreateAccountActivity extends AppCompatActivity {

    private EditText emailaddressField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();

        emailaddressField = (EditText) findViewById(R.id.ca_email);
        passwordField = (EditText) findViewById(R.id.ca_password);
        confirmPasswordField = (EditText) findViewById(R.id.ca_confirm_password);

        createAccountButton = (Button) findViewById(R.id.ca_create_account);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount(){
        String emailAddress = emailaddressField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(CreateAccountActivity.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
        }
        else{
            if(!password.equals(confirmPassword)){
                Toast.makeText(CreateAccountActivity.this, "Passwords don't match", Toast.LENGTH_LONG).show();
            }else{
                mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(CreateAccountActivity.this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
                        }
                        if(task.isSuccessful()){
                            FirebaseUser User = mAuth.getCurrentUser();
                            startActivity(new Intent(CreateAccountActivity.this, bucketlistActivity.class));
                        }
                    }
                });
            }
        }

    }
}
