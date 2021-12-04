package krishnaapps.com.loginfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {

    TextView signupLaunch, forgetPassword;

    private Button loginBtn;

    private ProgressBar progressBar;

    private EditText emailTextLogin, passwordEditTextLogin;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signupLaunch = findViewById(R.id.sign_up_launch);
        forgetPassword = findViewById(R.id.forget_password);
        progressBar = findViewById(R.id.progress_bar_login);

        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.login_btn);

        emailTextLogin = findViewById(R.id.login_mail);
        passwordEditTextLogin = findViewById(R.id.login_password);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailTextLogin.getText().toString().trim();
                String password = passwordEditTextLogin.getText().toString().trim();

                if(validateData(email, password)){
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {

                                        FirebaseUser userAuth = FirebaseAuth.getInstance().getCurrentUser();
                                        if(userAuth.isEmailVerified()) {
                                            progressBar.setVisibility(View.GONE);
                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "Check the Email!! Verification sent already. Verify and login!!!", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Failed to login!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });


        signupLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(i);
            }
        });
    }

    private boolean validateData(String email, String password) {
        if(email.isEmpty()){
            emailTextLogin.setError("Please enter email");
            emailTextLogin.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextLogin.setError("Please enter valid email");
            emailTextLogin.requestFocus();
            return false;
        }

        if(password.isEmpty()){
            passwordEditTextLogin.setError("password is Required!");
            passwordEditTextLogin.requestFocus();
            return false;
        }

        if(password.length() < 6){
            passwordEditTextLogin.setError("Invalid password.");
            passwordEditTextLogin.requestFocus();
            return false;
        }

        return true;
    }
}