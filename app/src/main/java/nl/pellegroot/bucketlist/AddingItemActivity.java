package nl.pellegroot.bucketlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddingItemActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String curUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_item);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users");

        final EditText inputName = findViewById(R.id.input_name);
        final EditText inputDescription = findViewById(R.id.input_description);
        EditText inputlocation = findViewById(R.id.input_location);

        Button btnAddToList = findViewById(R.id.btn_add_item_to_list);

        btnAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            bucketListItem newItem = new bucketListItem(inputName.getText().toString(), inputDescription.getText().toString());
            newItem.addNewItemToDB();
            }
        });
    }

    // TODO: Make a connection to the database.child(curUserId) and create a the new BL item on submit
}
