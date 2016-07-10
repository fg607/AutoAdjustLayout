package com.hardwork.fg607.autoadjust;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hardwork.fg607.autoadjust.view.AutoAdjustLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private AutoAdjustLayout mAutoAdjustLayout;

    private int mRed,mGreen,mBlue,mColor;
    private Random mRandom = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAutoRankLayout();


    }

    private void initAutoRankLayout() {

        mAutoAdjustLayout = (AutoAdjustLayout) findViewById(R.id.mylayout);

        for(int i =0;i<50;i++){

            TextView textView = new TextView(this);

            textView.setText(i+"");

            textView.setGravity(Gravity.CENTER);

            textView.setTextColor(Color.WHITE);

            mColor = getRandomColor();

            GradientDrawable colorDrawable = createDrawable(mColor, mColor, 20);

            textView.setBackground(colorDrawable);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getRandomWidth(),70);


            int padding = 10;
            params.setMargins(padding,padding,padding,padding);

            textView.setLayoutParams(params);


            mAutoAdjustLayout.addView(textView);
        }

    }


    public  int getRandomColor() {

        // 随机颜色的范围0x202020~0xefefef
        mRed = 32 + mRandom.nextInt(208);
        mGreen = 32 + mRandom.nextInt(208);
        mBlue = 32 + mRandom.nextInt(208);
        return Color.rgb(mRed, mGreen, mBlue);
    }

    public int getRandomWidth(){

        return 50+mRandom.nextInt(300);
    }


    public static GradientDrawable createDrawable(int contentColor, int strokeColor, int radius) {
        GradientDrawable drawable = new GradientDrawable(); // 生成Shape
        drawable.setGradientType(GradientDrawable.RECTANGLE); // 设置矩形
        drawable.setColor(contentColor);// 内容区域的颜色
        drawable.setStroke(1, strokeColor); // 四周描边,描边后四角真正为圆角，不会出现黑色阴影。如果父窗体是可以滑动的，需要把父View设置setScrollCache(false)
        drawable.setCornerRadius(radius); // 设置四角都为圆角
        return drawable;
    }
}
