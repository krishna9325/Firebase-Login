package krishnaapps.com.loginfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    TextView login_launch;
    Button btnRegister;
    EditText name, email, password, passwordCheck;

    ProgressBar progressBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Database
        login_launch = findViewById(R.id.login_launch);
        name = findViewById(R.id.register_name);
        email =findViewById(R.id.register_mail);
        password = findViewById(R.id.register_password);
        passwordCheck = findViewById(R.id.register_password_recheck);
        btnRegister = findViewById(R.id.register_btn);
        progressBar = findViewById(R.id.progress_bar_signup);

        name.setOnFocusChangeListener(this);
        email.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        passwordCheck.setOnFocusChangeListener(this);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData()) {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()) {
                                        User user = new User(email.getText().toString().trim(),
                                                name.getText().toString().trim());

                                        FirebaseUser firebaseUserAuth = FirebaseAuth.getInstance().getCurrentUser();

                                        FirebaseDatabase.getInstance().getReference()
                                                .child(firebaseUserAuth.getUid())
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()) {
                                                    progressBar.setVisibility(View.GONE);
                                                        firebaseUserAuth.sendEmailVerification();
                                                        Toast.makeText(SignupActivity.this, "Registered User successfully. Verify the email then login In!!",
                                                                Toast.LENGTH_LONG).show();

                                                } else{
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(SignupActivity.this, "Failed to register! Try to login if already registered..",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(SignupActivity.this, "Failed to register the user!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        login_launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private boolean validateData() {
        if(name.getText().toString().trim().isEmpty()){
            name.setError("Name is Required!");
            name.requestFocus();
            return false;
        }

        if(email.getText().toString().trim().isEmpty()){
            email.setError("Email is Required!");
            email.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            email.setError("Not a valid Email!");
            email.requestFocus();
            return false;
        }

        if(password.getText().toString().trim().isEmpty()){
            password.setError("password is Required!");
            password.requestFocus();
            return false;
        }

        if(passwordCheck.getText().toString().trim().isEmpty()){
            passwordCheck.setError("Re-Enter the password!");
            passwordCheck.requestFocus();
            return false;
        }

        if(!password.getText().toString().trim().equals(passwordCheck.getText().toString().trim())) {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void removeEdittextFocus(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(!b) {
            removeEdittextFocus(view);
        }
    }
}



