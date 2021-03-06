package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/*
    this is the login page where the user gets checked if he already signed in or not
    also signing in is possible by google or own email and password combination
 */

public class LoginActivity extends AppCompatActivity {

private EditText mEmailfield;
private EditText mPassword;
private Button mloginButton;
private Button createAccountButton;
private Button loginWithGoogleButton;
private FirebaseAuth mAuth;
private FirebaseAuth.AuthStateListener mAuthListener;
private GoogleSignInClient mGoogleSignInClient;
private GoogleSignInAccount userAccount;
private static final String TAG = "GoogleActivity";
private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        // check if the user is not already logged in
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(LoginActivity.this, BucketlistActivity.class));
                }
            }
        };

        // set the fields
        mEmailfield = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mloginButton = (Button) findViewById(R.id.signIn);
        loginWithGoogleButton = (Button) findViewById(R.id.GoogleSignIn);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);

        mloginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startSignIn();
            }
        });

        loginWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if the user want to login with google, use that intent
                startGoogleSignIn();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if the users wants to create a new user using email and password, this new intent is started
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        // check if the user is already logged in
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSignIn(){

        String emailAddress = mEmailfield.getText().toString();
        String password = mPassword.getText().toString();

        // check if everything is filled
        if(TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
        }else{

            // listens if the result is valid
            mAuth.signInWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Sign in error", Toast.LENGTH_LONG).show();
                    }
                    if(task.isSuccessful()){
                        startActivity(new Intent(LoginActivity.this, BucketlistActivity.class));
                    }
                }
            });
        }
    }

    private void startGoogleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                // google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, BucketlistActivity.class));
                        } else {

                            // if sign in fails, display a message to the user
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
