package com.team4.capstone.voiceofcau;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.core.SignInStateChangeListener;
import com.amazonaws.mobile.auth.ui.AuthUIConfiguration;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

public class AuthenticatorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        // Add a call to initialize AWSMobileClient
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("Connection", "Created");
            }
        }).execute();
        // Sign-in listener
        IdentityManager.getDefaultIdentityManager().addSignInStateChangeListener(new SignInStateChangeListener() {
            @Override
            public void onUserSignedIn() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Context context = getApplicationContext();
                CognitoUserPool userPool = new CognitoUserPool(context, new AWSConfiguration(context));
                String UserID = userPool.getCurrentUser().getUserId();
                intent.putExtra("UserID", UserID);
                Log.d("USERID",  UserID);
                startActivity(intent);
                finish();
            }
            // Sign-out listener
            @Override
            public void onUserSignedOut() {
                Log.d("LOG_TAG", "User Signed Out");
                showSignIn();
            }
        });
    }
    /*
     * Display the AWS SDK sign-in/sign-up UI
     */
    private void showSignIn() {
        AuthUIConfiguration config =
                new AuthUIConfiguration.Builder()
                        .userPools(true)  // true? show the Email and Password UI
                        .logoResId(R.drawable.intro1) // Change the logo
                        .isBackgroundColorFullScreen(true) // Full screen backgroundColor the backgroundColor full screen
                        .fontFamily("sans-serif-light") // Apply sans-serif-light as the global font
                        .build();

        SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(AuthenticatorActivity.this, SignInUI.class);
        signin.login(AuthenticatorActivity.this, MainActivity.class).authUIConfiguration(config).execute();
    }
}
