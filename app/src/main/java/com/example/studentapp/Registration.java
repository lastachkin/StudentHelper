package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentapp.Model.Student;
import com.example.studentapp.Remote.RestAPI;
import com.example.studentapp.Remote.RetrofitClient;
import com.example.studentapp.Remote.SendMailTLS;

import java.security.MessageDigest;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class Registration extends AppCompatActivity {
    private String mail;
    private boolean errorFlag;
    private CompositeDisposable disposables;
    EditText nameView, loginView, passwordView, mailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameView = findViewById(R.id.name);
        loginView = findViewById(R.id.login);
        passwordView = findViewById(R.id.password);
        mailView = findViewById(R.id.mail);

        disposables = new CompositeDisposable();
    }

    void register() {
        errorFlag = false;

        String password = generateMD5Hash(passwordView.getText().toString());
        mail = mailView.getText().toString();

        checkCredentials(loginView.getText().toString(), nameView.getText().toString(), mail);
        checkPassword(passwordView.getText().toString());

        if(!errorFlag) {
            Student student = new Student(UUID.randomUUID().toString(),
                                          loginView.getText().toString(),
                                          password,
                                          nameView.getText().toString(),
                                          mail);

            //insert into local db
            App.getInstance().getDatabase().studentDao().insert(student);

            //insert into remote db
            disposables.add(App.getInstance().getRestAPI().registerUser(student)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        Log.i(App.tag, response);
                    }, throwable -> {
                        Log.e(App.tag, throwable.getMessage());
                    })
            );
        }
    }

    void checkPassword(String pass) {
        //Check the regular expression
        if(!pass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{5,15}$")) {
            errorFlag = true;
            popupText(getResources().getString(R.string.pass_fail));
            return;
        }
        //Check the password is not empty
        if(pass.length() == 0) {
            errorFlag = true;
            popupText(getResources().getString(R.string.empty_fields));
            return;
        }
    }

    void checkCredentials(String login, String name, String mail) {
        //Check the uniqueness of login
        Student student = App.getInstance().getDatabase().studentDao().loginUniqueness(login);
        if(student != null) {
            errorFlag = true;
            popupText(getResources().getString(R.string.unique_user));
            return;
        }

        //Check the fields are not empty
        if(login.trim().length() == 0 || name.trim().length() == 0 || mail.trim().length() == 0) {
            errorFlag = true;
            popupText(getResources().getString(R.string.empty_fields));
            return;
        }

        if(!mail.matches("^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$")) {
            errorFlag = true;
            popupText(getResources().getString(R.string.mail_fail));
            return;
        }
    }

    private void popupText(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static String generateMD5Hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] initialPassword = password.getBytes("UTF-8");
            byte[] digest = md.digest(initialPassword);
            StringBuffer generatedPassword = new StringBuffer();
            for (byte aByteData : digest) {
                generatedPassword.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            return generatedPassword.toString();
        }
        catch (Exception ex) {
            Log.e(App.tag, ex.getMessage());
        }
        return null;
    }

    public void startLoginPage(View view) {
        register();
        if(!errorFlag) {
            try {
                String message = getResources().getString(R.string.msg_body);
                SendMailTLS sm = new SendMailTLS(this, mail, getResources().getString(R.string.msg_subject), message);
                sm.execute();
            }
            catch (Exception ex) {
                Log.e(App.tag, ex.getMessage());
            }

            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
        else
            return;
    }
}
