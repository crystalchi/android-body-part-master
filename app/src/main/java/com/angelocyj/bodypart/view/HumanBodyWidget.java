package com.angelocyj.bodypart.view;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.angelocyj.bodypart.BodyFragment;
import com.angelocyj.bodypart.R;
import com.angelocyj.bodypart.region.Region;
import com.angelocyj.bodypart.region.RegionPathView;

/**
 * Created by angelo on 2015/2/13.
 */

public class HumanBodyWidget {

    static public boolean isAPI11 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    public static boolean mShowingBack = false;
    public static boolean isMan = true;

    private BodyFrontFragment bodyFrontFragment = new BodyFrontFragment();
    private BodyBackFragment bodyBackFragment = new BodyBackFragment();
//    private FragmentManager.OnBackStackChangedListener onBackStackChangedListener;

    private AppCompatActivity activity;
    private static WaveEffectLayout container;

    // api < 11
    private LayoutInflater inflater;
    private ImageView frontView, backView;
    private AbsoluteLayout leftRegionLayout, rightRegionLayout;

    public HumanBodyWidget(Context context, FrameLayout container, Bundle savedInstanceState) {
        init(context, container, savedInstanceState);
    }

    private void init(Context context, FrameLayout container, Bundle savedInstanceState) {
        this.activity = (AppCompatActivity) context;
        this.container = (WaveEffectLayout) container;
        mShowingBack = false;
        isMan = true;

        initViews();

        if (isAPI11) {
            initFragment(savedInstanceState);

        } else {
            inflater = LayoutInflater.from(context);
            frontView = (ImageView) inflater.inflate(R.layout.fragment_body_front, null);
            backView = (ImageView) inflater.inflate(R.layout.fragment_body_back, null);
            this.container.addView(frontView);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            activity.getFragmentManager()
                    .beginTransaction()
                    .add(R.id.body_container, bodyFrontFragment)//container.getId()
                    .commit();
        } else {
            mShowingBack = (activity.getFragmentManager().getBackStackEntryCount() > 0);
        }
        activity.getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                mShowingBack = (activity.getFragmentManager().getBackStackEntryCount() > 0);
            }
        });
    }

    private void initViews() {
        leftRegionLayout = (AbsoluteLayout) container.findViewById(R.id.left_region_layout);
        rightRegionLayout = (AbsoluteLayout) container.findViewById(R.id.right_region_layout);
    }


    public boolean flipBody(boolean isShowingBack) {

        if (this.mShowingBack == isShowingBack)
            return false;

        clearRegionView();
        if (isAPI11) {

            performFragmentFlipAnimation();

        } else {

            container.removeAllViews();
            if (mShowingBack) {
                container.addView(frontView);
            } else {
                container.addView(backView);
            }
            mShowingBack = !mShowingBack;
        }

        return true;

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean performFragmentFlipAnimation() {
        if (mShowingBack) {
            activity.getFragmentManager().popBackStack();
            return true;
        }

        activity.getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.body_flip_right_in, R.anim.body_flip_right_out,
                        R.anim.body_flip_left_in, R.anim.body_flip_left_out)
                .replace(R.id.body_container, bodyBackFragment)
                .addToBackStack(null)
                .commit();

        return true;
    }

//    @Override
//    public void onBackStackChanged() {
//        mShowingBack = (activity.getFragmentManager().getBackStackEntryCount() > 0);
//
//    }


    public Boolean toggleBodyGenderImage(Boolean isMan) {
        if (this.isMan == isMan)
            return false;

        clearRegionView();
        this.isMan = isMan;
        if (isAPI11) {
            if (mShowingBack) {
                bodyFrontFragment.setMan(isMan);
                bodyBackFragment.setShowImage(isMan);
            } else {
                bodyFrontFragment.setShowImage(isMan);
                bodyBackFragment.setMan(isMan);
            }

        } else {
            if (isMan) {
                frontView.setImageResource(R.mipmap.man_front);
                backView.setImageResource(R.mipmap.man_back);
            } else {
                frontView.setImageResource(R.mipmap.woman_front);
                backView.setImageResource(R.mipmap.woman_back);
            }
        }

        return true;
    }

    public static class BodyFrontFragment extends BodyFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_body_front, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            bodyImageView = (ImageView) view.findViewById(R.id.body_front);
            bodyImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(container.getFloatImageView() != null){
                        if(container.getFloatImageView().getTag() != null){
                            Region region = (Region) container.getFloatImageView().getTag();
                            if(region.getValue() == 1){
                                Toast.makeText(getActivity(), "头部" , Toast.LENGTH_LONG).show();
                            }else if(region.getValue() == 9){
                                Toast.makeText(getActivity(), "上肢" , Toast.LENGTH_LONG).show();
                            }else if(region.getValue() == 11){
                                Toast.makeText(getActivity(), "胸部" , Toast.LENGTH_LONG).show();
                            }else if(region.getValue() == 13){
                                Toast.makeText(getActivity(), "腹部" , Toast.LENGTH_LONG).show();
                            }else if(region.getValue() == 14){
                                Toast.makeText(getActivity(), "下体" , Toast.LENGTH_LONG).show();
                            }else if(region.getValue() == 15){
                                Toast.makeText(getActivity(), "下肢" , Toast.LENGTH_LONG).show();
                            }
                        }
                        container.getmFrameLayout().removeView(container.getFloatImageView());
                    }
                }
            });
        }

        @Override
        public void setShowImage(Boolean isMan) {
            if (bodyImageView == null)
                return;

            setMan(isMan);
            if (isMan) {
                bodyImageView.setImageResource(R.mipmap.man_front);
            } else {
                bodyImageView.setImageResource(R.mipmap.woman_front);
            }
        }

        @Override
        public void monitorTouchEvent(MotionEvent event) {
            /*int touchSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
            int downX = 0, downY = 0;
            int moveX = 0, moveY = 0;
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downX = (int) event.getX();
                    downY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    int disX = moveX - downX;
                    int disY = moveY - downY;
                    int distanceX = Math.abs(disX);
                    int distanceY = Math.abs(disY);
                    if(container.getFloatImageView() != null){
                        if(container.getFloatImageView().getTag() != null){
                            Region region = (Region) container.getFloatImageView().getTag();
                            if(region.getValue() == 1){
                                Toast.makeText(getActivity(), "头部" , Toast.LENGTH_LONG).show();
                            }else if(region.getValue() == 15){
                                Toast.makeText(getActivity(), "大腿" , Toast.LENGTH_LONG).show();
                            }
                        }
                        *//*boolean flag = false;
                        if(distanceX > distanceY){
                            if(distanceX > touchSlop){
                                flag = true;
                            }
                        }else{
                            if(distanceY > touchSlop){
                                flag = true;
                            }
                        }
                        if(!flag){ //移动过，未超过最小触摸距离，可认为是点击状态
                           *//* *//*Toast.makeText(getActivity(), "go......" , Toast.LENGTH_LONG).show();
                        }*//*
                        container.getmFrameLayout().removeView(container.getFloatImageView());

                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = (int) event.getX();
                    moveY = (int) event.getY();
                    break;
            }*/
        }
    }

    public static class BodyBackFragment extends BodyFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_body_back, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            bodyImageView = (ImageView) view.findViewById(R.id.body_back);
        }

        @Override
        public void setShowImage(Boolean isMan) {
            if (bodyImageView == null)
                return;

            setMan(isMan);
            if (isMan) {
                bodyImageView.setImageResource(R.mipmap.man_back);
            } else {
                bodyImageView.setImageResource(R.mipmap.woman_back);
            }
        }

        @Override
        public void monitorTouchEvent(MotionEvent event) {

        }
    }

    private void clearRegionView() {
        container.setRegionType(-1);
        RegionPathView.pathView.setToClear(true);
        RegionPathView.pathView.invalidate();
        leftRegionLayout.removeAllViews();
        rightRegionLayout.removeAllViews();
    }


}
