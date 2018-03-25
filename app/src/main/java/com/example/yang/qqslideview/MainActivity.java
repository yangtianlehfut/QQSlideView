package com.example.yang.qqslideview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity{
    private SlideView slideView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slideView = findViewById(R.id.scroll_view);
        slideView.setOnClickListener(new SlideView.OnClickListener() {
            @Override
            public void onLeftClick(View v) {
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCenterClick(View v) {
                Toast.makeText(MainActivity.this, "center", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightClick(View v) {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
