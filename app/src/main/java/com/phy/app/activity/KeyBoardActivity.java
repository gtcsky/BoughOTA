package com.phy.app.activity;

import android.widget.Button;

import com.phy.app.R;

/**
 * KeyBoardActivity
 *
 * @author:zhoululu
 * @date:2018/4/15
 */

public class KeyBoardActivity extends EventBusBaseActivity {

    Button[][] buttons = new Button[4][4];

    @Override
    public void initComponent() {
        setTitle(R.string.label_keyboard);

        Button button1 = findViewById(R.id.button_1);
        buttons[0][0] = button1;
        Button button2 = findViewById(R.id.button_2);
        buttons[0][1] = button2;
        Button button3 = findViewById(R.id.button_3);
        buttons[0][2] = button3;
        Button button4 = findViewById(R.id.button_4);
        buttons[0][3] = button4;

        Button button5 = findViewById(R.id.button_5);
        buttons[1][0] = button5;
        Button button6 = findViewById(R.id.button_6);
        buttons[1][1] = button6;
        Button button7 = findViewById(R.id.button_7);
        buttons[1][2] = button7;
        Button button8 = findViewById(R.id.button_8);
        buttons[1][3] = button8;

        Button button9 = findViewById(R.id.button_9);
        buttons[2][0] = button9;
        Button button10 = findViewById(R.id.button_10);
        buttons[2][1] = button10;
        Button button11 = findViewById(R.id.button_11);
        buttons[2][2] = button11;
        Button button12 = findViewById(R.id.button_12);
        buttons[2][3] = button12;

        Button button13 = findViewById(R.id.button_13);
        buttons[3][0] = button13;
        Button button14 = findViewById(R.id.button_14);
        buttons[3][1] = button14;
        Button button15 = findViewById(R.id.button_15);
        buttons[3][2] = button15;
        Button button16 = findViewById(R.id.button_16);
        buttons[3][3] = button16;

    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_keyboard;
    }


}
