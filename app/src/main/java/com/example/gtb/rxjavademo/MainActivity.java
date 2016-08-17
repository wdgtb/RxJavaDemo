package com.example.gtb.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initFragment();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.main_content, new MainFragment(), MainFragment.class.getName())
            .commit();
    }

}
