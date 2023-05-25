package com.example.mealmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.mealmaster.fragment.HomeFragment;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences onBoardingScreenPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            onBoardingScreenPreferences = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);

            boolean isFirstTime = onBoardingScreenPreferences.getBoolean("firstTime",true);

            if (isFirstTime){

                SharedPreferences.Editor editor = onBoardingScreenPreferences.edit();
                editor.putBoolean("firstTime",false);
                editor.commit();

                Intent intent = new Intent(SplashScreen.this, OnBoarding.class);
                startActivity(intent);
                finish();
            }
            else if (isFirstTime){
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }
}