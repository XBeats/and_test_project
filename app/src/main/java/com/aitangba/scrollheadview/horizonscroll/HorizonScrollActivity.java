package com.aitangba.scrollheadview.horizonscroll;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.aitangba.scrollheadview.R;

/**
 * Created by fhf11991 on 2016/6/22.
 */
public class HorizonScrollActivity extends AppCompatActivity implements
        MultiViewGroup.OnViewChangeListener {

    private MultiViewGroup multiViewGroup;
    private int allScreen;
    private int currentScreen = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiview);
        multiViewGroup = (MultiViewGroup) findViewById(R.id.screenParent);
        multiViewGroup.SetOnViewChangeListener(this);
        allScreen = multiViewGroup.getChildCount() - 1;
    }

    public void nextScreen(View view) {
        if (currentScreen < allScreen) {
            currentScreen++;
        } else {
            currentScreen = 0;

        }
        multiViewGroup.snapToScreen(currentScreen);
    }

    public void preScreen(View view) {
        if (currentScreen > 0) {
            currentScreen--;
        } else {
            currentScreen = allScreen;
        }
        multiViewGroup.snapToScreen(currentScreen);
    }

    @Override
    public void OnViewChange(int page) {
        Toast.makeText(getApplicationContext(),"page = " + page, Toast.LENGTH_SHORT).show();
    }
}