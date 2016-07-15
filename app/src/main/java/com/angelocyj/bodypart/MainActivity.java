package com.angelocyj.bodypart;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angelocyj.bodypart.region.RegionView;
import com.angelocyj.bodypart.view.HumanBodyWidget;
import com.angelocyj.bodypart.view.WaveEffectLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private WaveEffectLayout container;
    private HumanBodyWidget bodyWidget;
    private ImageView manIv, womanIv;
    private TextView manTv, womanTv, flipFrontTv, flipBackTv;

    private ArrayList<MyTouchListener> mTouchListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setTitle("身体部位");
        initViews(savedInstanceState);

    }

    public void initViews(Bundle savedInstanceState){
        setContentView(R.layout.activity_main);
        container = (WaveEffectLayout) findViewById(R.id.container);
        manIv = (ImageView) findViewById(R.id.man_icon);
        manTv = (TextView) findViewById(R.id.man_text);
        womanIv = (ImageView) findViewById(R.id.woman_icon);
        womanTv = (TextView) findViewById(R.id.woman_text);
        flipFrontTv = (TextView) findViewById(R.id.flipFront);
        flipBackTv = (TextView) findViewById(R.id.flipBack);

        bodyWidget = new HumanBodyWidget(this, container, savedInstanceState);
        container.setRegionView(new RegionView(container, this));

    }



    public void registerTouchListener(MyTouchListener listener){
        mTouchListeners.add(listener);
    }

    public void unRegisterTouchListener(MyTouchListener listener){
        mTouchListeners.remove(listener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        for(MyTouchListener listener : mTouchListeners){
            listener.onTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    public void genderClick(View view){
        switch (view.getId()){
            case R.id.man_btn:
                if(bodyWidget.toggleBodyGenderImage(true)) {
                    findViewById(R.id.man_btn).setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
                    findViewById(R.id.woman_btn).setBackgroundColor(Color.TRANSPARENT);
                    manIv.setImageResource(R.mipmap.icon_man_pressed);
                    manTv.setTextColor(Color.WHITE);
                    womanIv.setImageResource(R.mipmap.icon_woman);
                    womanTv.setTextColor(getResources().getColor(R.color.colorLightBlue));
                }
                break;
            case R.id.woman_btn:
                if(bodyWidget.toggleBodyGenderImage(false)) {
                    findViewById(R.id.man_btn).setBackgroundColor(Color.TRANSPARENT);
                    findViewById(R.id.woman_btn).setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
                    manIv.setImageResource(R.mipmap.icon_man);
                    manTv.setTextColor(getResources().getColor(R.color.colorLightBlue));
                    womanIv.setImageResource(R.mipmap.icon_woman_pressed);
                    womanTv.setTextColor(Color.WHITE);
                }
                break;
        }
    }

    public void sideClick(View view){

        switch (view.getId()){
            case R.id.flipFront:
                if(bodyWidget.flipBody(false)) {
                    flipFrontTv.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
                    flipBackTv.setBackgroundColor(Color.TRANSPARENT);
                    flipFrontTv.setTextColor(Color.WHITE);
                    flipBackTv.setTextColor(getResources().getColor(R.color.colorLightBlue));
                }
                break;
            case R.id.flipBack:
                if(bodyWidget.flipBody(true)) {
                    flipFrontTv.setBackgroundColor(Color.TRANSPARENT);
                    flipBackTv.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
                    flipFrontTv.setTextColor(getResources().getColor(R.color.colorLightBlue));
                    flipBackTv.setTextColor(Color.WHITE);
                }
                break;
        }
    }

    public interface MyTouchListener
    {
        void onTouchEvent(MotionEvent event);
    }
}
