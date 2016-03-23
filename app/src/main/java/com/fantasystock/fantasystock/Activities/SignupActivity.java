package com.fantasystock.fantasystock.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Instruction for facebook login:
 * https://developers.facebook.com/docs/facebook-login/android
 */

public class SignupActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private String profileImageUrl;

    @Bind(R.id.login_button) LoginButton loginButton;
    @Bind(R.id.btnSignIn) Button signInButton;
    @Bind(R.id.btnSignUp) Button signUpButton;
    @Bind(R.id.etEmail) EditText etEmail;
    @Bind(R.id.etPassword) EditText etPassword;
    @Bind(R.id.tvWarning) TextView tvWarning;
    @Bind(R.id.prLoadingSpinner) RelativeLayout prLoadingSpinner;
    @Bind(R.id.ibAvatar) ImageButton ibAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Parse User
        DataCenter.getInstance(); // to get and setup current user
        if(User.isLogin()) {
            //Toast.makeText(this, User.currentUser.username, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Initial facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Set content and bind views
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        prLoadingSpinner.setVisibility(View.INVISIBLE);

        // Initial facebook callback manager
        callbackManager = CallbackManager.Factory.create();
        // Ask facebook permission if needed
        // example:
        // loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                finish();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "You must login", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Login failed, please try again", Toast.LENGTH_LONG).show();
            }
        });
        this.onClickAvatar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @OnClick(R.id.btnSignUp)
    public void onSignUp() {
        if (!checkEmailPassword()) {
            return;
        }
        ParseUser user = new ParseUser();
        user.setUsername(etEmail.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setEmail(etEmail.getText().toString());
        user.put(User.USER_PROFILE_IMAGE_URL, profileImageUrl);

        // other fields can be set just like with ParseObject
        prLoadingSpinner.setVisibility(View.VISIBLE);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    DataCenter.getInstance().setUser(ParseUser.getCurrentUser());
                    // DataCenter.getInstance().signUpUser(ParseUser.getCurrentUser(), profileImageUrl);
                    finish();

                    // Hooray! Let them use the app now.
                } else {
                    tvWarning.setText("Fail to sign up");
                    Utils.fadeIneAnimation(tvWarning);
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
                prLoadingSpinner.setVisibility(View.INVISIBLE);
            }
        });

    }

    @OnClick(R.id.btnSignIn)
    public void onSignIn() {
        if (!checkEmailPassword()) {
            return;
        }
        prLoadingSpinner.setVisibility(View.VISIBLE);
        ParseUser.logInInBackground(etEmail.getText().toString(), etPassword.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    DataCenter.getInstance().setUser(user);
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    tvWarning.setText("Fail to sign in");
                    Utils.fadeIneAnimation(tvWarning);
                }
                prLoadingSpinner.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean checkEmailPassword() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (email.length()>0 && password.length()>0) {
            return true;
        }
        tvWarning.setText("password or email is not filled in");
        Utils.fadeIneAnimation(tvWarning);

        return false;
    }

    @OnClick(R.id.ibAvatar)
    public void onClickAvatar() {
        String newProfileImageUrl = this.profileImageUrl;
        // Make sure it changes to a different avatar
        while (TextUtils.isEmpty(newProfileImageUrl) || newProfileImageUrl.equals(this.profileImageUrl)) {
            int rand = (int) (Math.random() * 30);
            newProfileImageUrl = "avatar_" + rand;
        }
        this.profileImageUrl = newProfileImageUrl;
        Context context = ibAvatar.getContext();
        int resourceId = context.getResources().getIdentifier(profileImageUrl, "drawable",  context.getPackageName());
        ibAvatar.setImageResource(resourceId);
    }
}
