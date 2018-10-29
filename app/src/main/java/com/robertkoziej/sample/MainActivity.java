package com.robertkoziej.sample;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.robertkoziej.forwardnavigiation.ForwardNavigation;

public class MainActivity extends AppCompatActivity {
    private ForwardNavigation forwardNavigation;
    private TextView stepTextView;
    private int currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        stepTextView = findViewById(R.id.stepTextView);
        setStepText(currentView);

        forwardNavigation = findViewById(R.id.fwNav);
        forwardNavigation.inflate();

        forwardNavigation.setOnClickListener(new ForwardNavigation.OnClickListener() {
            @Override
            public void onNextButtonClicked() {
                currentView++;
                if (currentView > forwardNavigation.getLastViewNumber())
                    switchActivity();
                else {
                    setStepText(currentView);
                    forwardNavigation.setNavigationAttrs(ForwardNavigation.ViewCount.SEVEN, currentView);
                    forwardNavigation.inflate();
                }
            }

            @Override
            public void onBackButtonClicked() {
                currentView--;
                setStepText(currentView);
                forwardNavigation.setNavigationAttrs(ForwardNavigation.ViewCount.SEVEN, currentView);
                forwardNavigation.inflate();
            }
        });

        Switch switchMode = findViewById(R.id.switchMode);
        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    forwardNavigation.setColorsAttrs(
                            ContextCompat.getColor(getBaseContext(), (R.color.colorPrimaryDark)),
                            Color.WHITE,
                            Color.WHITE,
                            ContextCompat.getColor(getBaseContext(), (R.color.colorPrimaryLight)));
                    forwardNavigation.inflate();
                } else {
                    forwardNavigation.setColorsAttrs(
                            Color.WHITE,
                            ContextCompat.getColor(getBaseContext(), (R.color.colorPrimaryDark)),
                            ContextCompat.getColor(getBaseContext(), (R.color.colorPrimaryDark)),
                            ContextCompat.getColor(getBaseContext(), (R.color.colorPrimaryLight)));
                    forwardNavigation.inflate();
                }
            }
        });
    }

    private void setStepText(int currentView) {
        String text = getString(R.string.step) + " " + Integer.toString(currentView);
        stepTextView.setText(text);
    }

    private void switchActivity() {
        Intent intent = new Intent(getBaseContext(), NextActivity.class);
        startActivity(intent);
    }
}
