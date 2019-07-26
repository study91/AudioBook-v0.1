package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.study91.audiobook.R;

/**
 * 目录活动
 */
public class CatalogActivity extends Activity implements View.OnClickListener {
    private UI ui = new UI(); //私有界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置为竖屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //不关屏

        setContentView(R.layout.activity_catalog);

        //返回按钮
        ui.backButton = (Button) findViewById(R.id.backButton);
        ui.backButton.setOnClickListener(this);

        //缩小字体按钮
        ui.smallFontButton = (Button) findViewById(R.id.smallFontButton);
        ui.smallFontButton.setOnClickListener(this);

        //放大字体按钮
        ui.largeFontButton = (Button) findViewById(R.id.largeFontButton);
        ui.largeFontButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton: //返回按钮
                finish();
                break;
            case R.id.smallFontButton: //缩小字体按钮
                break;
            case R.id.largeFontButton: //放大字体按钮
                break;
        }
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 返回按钮
         */
        Button backButton;

        /**
         * 缩小字体按钮
         */
        Button smallFontButton;

        /**
         * 放大字体按钮
         */
        Button largeFontButton;
    }
}
