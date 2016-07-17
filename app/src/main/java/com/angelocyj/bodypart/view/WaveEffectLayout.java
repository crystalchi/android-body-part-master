package com.angelocyj.bodypart.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.angelocyj.bodypart.R;
import com.angelocyj.bodypart.UIUtil;
import com.angelocyj.bodypart.region.Region;
import com.angelocyj.bodypart.region.RegionParam;
import com.angelocyj.bodypart.region.RegionPathView;
import com.angelocyj.bodypart.region.RegionView;

import java.util.ArrayList;
import java.util.Map;

/**
 * 根据人体部位图测量距离以及图片显示大小
 与实际图片大小的百分比比率
 计算适配点击图片上的点击点在屏幕上的位于屏幕上的绝对坐标。
 根据坐标判断点击的人体部位区。
 然后做相应的详细部位显示。
 * Created by angelo on 2015/2/15.
 */
public class WaveEffectLayout extends FrameLayout implements Runnable {

    private static final String TAG = WaveEffectLayout.class.getSimpleName();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mTargetWidth;
    private int mTargetHeight;

    private static final int mMaxRevealRadius = 24; //40
    private static final int mRevealRadiusGap = 3; //5
    private int mRevealRadius = 0;
    private float mCenterX;
    private float mCenterY;
    private int[] mLocationInScreen = new int[2]; //存储整个手机屏幕的绝对坐标，包括手机通知栏

    private boolean mShouldDoAnimation = false;
    private boolean mIsPressed = false;
    private String mTag;
    private int INVALIDATE_DURATION = 40;

    private View mTouchTarget;
    private DispatchUpTouchEventRunnable mDispatchUpTouchEventRunnable = new DispatchUpTouchEventRunnable();

    private RegionView regionView;
    private RegionPathView regionPathView;
    private ImageView bodyImageView;
    private int regionType = -1;

    private static int mHeadY, mHandX1, mHandX2, mChestY, mWaistY, mBackHeadY, mUpperPartY, mMiddlePartY,
            mNakedness, mLeftLowerExtremity, mRightLowerExtremity, mRleLeft, mRleTop, mRleRight, mRleBottom,
            mNdLeft, mNdTop, mNdRight, mNdBottom;

    private static int bodyImageViewHeight = 0;

    private FrameLayout mFrameLayout;
    private Context mContext;
    private ImageView floatImageView;
    private Rect mRleRect = new Rect();
    private Rect mNakednessRect = new Rect();

    public WaveEffectLayout(Context context) {
        super(context);
        init(context);
    }

    public WaveEffectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public WaveEffectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mPaint.setColor(getResources().getColor(R.color.reveal_color));
        this.mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.getLocationOnScreen(mLocationInScreen); //获取整个手机屏幕的绝对坐标

        int regionW = (int) getResources().getDimension(R.dimen.region_width);
        RegionParam.OFFSET_Y =  regionW + UIUtil.dip2px(RegionParam.STANDARD_OFFSET_Y) ;
        RegionParam.LEFT_REGION_X = UIUtil.dip2px(RegionParam.REGION_WIDTH) / 2 + this.getPaddingLeft();
        RegionParam.RIGHT_REGION_X = this.getWidth() - RegionParam.LEFT_REGION_X - UIUtil.dip2px(20f); // besure 10f

        regionPathView = new RegionPathView(this);

    }

    private void initParametersForChild(MotionEvent event, View view) {
        mCenterX = event.getX() ;
        mCenterY = event.getY() ;
        mTargetWidth = view.getMeasuredWidth();
        mTargetHeight = view.getMeasuredHeight();
        mRevealRadius = 0;
        mShouldDoAnimation = true;
        mIsPressed = true;
        mTag = (String) view.getTag();

    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!mShouldDoAnimation || mTargetWidth <= 0 || mTouchTarget == null || !"root".equals(mTag)) {
            return;
        }

        mRevealRadius += mRevealRadiusGap;

        this.getLocationOnScreen(mLocationInScreen);
        int[] location = new int[2];
        mTouchTarget.getLocationOnScreen(location);
        int left = location[0] - mLocationInScreen[0];
        int top = location[1] - mLocationInScreen[1];
        int right = left + mTouchTarget.getMeasuredWidth();
        int bottom = top + mTouchTarget.getMeasuredHeight();

        canvas.save();
//        canvas.clipRect(left, top, right, bottom);
        canvas.drawCircle(mCenterX, mCenterY, mRevealRadius, mPaint);
        canvas.restore();

        if (mRevealRadius <= mMaxRevealRadius) {
            postInvalidateDelayed(INVALIDATE_DURATION, left, top, right, bottom);
        } else if (!mIsPressed) {
            mShouldDoAnimation = false;
            postInvalidateDelayed(INVALIDATE_DURATION, left, top, right, bottom);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        Log.d(TAG, "x , y  is " + x + " , " + y);

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

            View touchTarget = getTouchTarget(this, x, y);

            String tag = (String) touchTarget.getTag();
            if(!"root".equals(tag)) { //"region".equals(tag)
                touchTarget.performClick();
                return super.dispatchTouchEvent(event);
            }
            bodyImageView = getBodyImageView();
            getBodyFrameLayout();

            if (isTouchPointInTransparent(x,y)) {
                regionType = -1;
                if(floatImageView != null){
                    floatImageView.setTag(null);
                }
            } else {
                int newRegionType = touchPointInRegion(x, y);
                if (newRegionType == regionType) {
                    regionType = -1;
                } else {
                    regionType = newRegionType;
                }
            }
            //refresh(regionType);  //注释掉，此处暂时不做显示人体部位关节。

            if (touchTarget != null && touchTarget.isClickable() && touchTarget.isEnabled()) {
                mTouchTarget = touchTarget;
                initParametersForChild(event, touchTarget);
                postInvalidateDelayed(INVALIDATE_DURATION);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            mIsPressed = false;
            postInvalidateDelayed(INVALIDATE_DURATION);
            mDispatchUpTouchEventRunnable.event = event;
            postDelayed(mDispatchUpTouchEventRunnable, 40);
            return true;
        } else if (action == MotionEvent.ACTION_CANCEL) {
            mIsPressed = false;
            postInvalidateDelayed(INVALIDATE_DURATION);
        }

        return super.dispatchTouchEvent(event);
    }

    private View getTouchTarget(View view, int x, int y) {
        View target = null;
        ArrayList<View> touchableViews = view.getTouchables();

        touchableViews.remove(view);
        if(touchableViews.size() > 2){
            for (View child : touchableViews) {
                if(!"root".equals(child.getTag())) {
                    if (isTouchPointInView(child, x, y)) {
                        target = child;
                        break;
                    }
                }
            }
        }

        if(target == null){
            for (View child : touchableViews) {
                if (isTouchPointInView(child, x, y)) {
                    target = child;
                    break;
                }
            }
        }
        if(target == null)
            target = view;

        return target;
    }

    private boolean isTouchPointInView(View view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (view.isClickable() && y >= top && y <= bottom
                && x >= left && x <= right) {
            return true;
        }
        return false;
    }

    @Override
    public boolean performClick() {
        postDelayed(this, 400);
        return true;
    }

    @Override
    public void run() {
        super.performClick();
    }

    private class DispatchUpTouchEventRunnable implements Runnable {
        public MotionEvent event;

        @Override
        public void run() {
            if (mTouchTarget == null || !mTouchTarget.isEnabled()) {
                return;
            }

            if (isTouchPointInView(mTouchTarget, (int)event.getRawX(), (int)event.getRawY())) {
                mTouchTarget.performClick();
            }
        }
    };

    private void initParametersForRegion() {

        if(bodyImageView == null)
            return;

        if(bodyImageViewHeight != bodyImageView.getHeight()) {

            bodyImageViewHeight = bodyImageView.getHeight();
            int paddingTop = this.getPaddingTop();

            /*mHeadY = (*//*219*//* 212 + RegionParam.standardOffsetY) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mHandX1 = *//*200*//*214 * bodyImageView.getWidth() / RegionParam.standardWidth + bodyImageView.getLeft() + mLocationInScreen[0];
            mHandX2 = (RegionParam.standardWidth - 214) * bodyImageView.getWidth() / RegionParam.standardWidth  + bodyImageView.getLeft() + mLocationInScreen[0];
            mChestY = (212 + RegionParam.standardOffsetY + 232) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mWaistY = (212 + RegionParam.standardOffsetY + 232 + 248) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;

            mBackHeadY = (221 + RegionParam.standardOffsetY) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mUpperPartY = (221 + RegionParam.standardOffsetY + 365) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mMiddlePartY = (221 + RegionParam.standardOffsetY + 365 + 190) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;*/

            //微医人体图片
            mHeadY = (155 + RegionParam.standardOffsetY) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mHandX1 = 138 * bodyImageView.getWidth() / RegionParam.standardWidth + bodyImageView.getLeft() + mLocationInScreen[0];
            mHandX2 = (RegionParam.standardWidth - 120) * bodyImageView.getWidth() / RegionParam.standardWidth  + bodyImageView.getLeft() + mLocationInScreen[0];
            mChestY = (155 + RegionParam.standardOffsetY + 118) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mWaistY = (155 + RegionParam.standardOffsetY + 118 + 136) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mNakedness = (155 +  RegionParam.standardOffsetY + 118 + 136 + 72) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mLeftLowerExtremity = 225 * bodyImageView.getWidth() / RegionParam.standardWidth + bodyImageView.getLeft() + mLocationInScreen[0];
            mRightLowerExtremity = (RegionParam.standardWidth - 225) * bodyImageView.getWidth() / RegionParam.standardWidth + + bodyImageView.getLeft() + mLocationInScreen[0];

            mRleLeft = 227 * bodyImageView.getWidth() / RegionParam.standardWidth + bodyImageView.getLeft() + mLocationInScreen[0];;
            mRleTop = (480 + RegionParam.standardOffsetY) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mRleRight = (227 + 130) * bodyImageView.getWidth() / RegionParam.standardWidth + bodyImageView.getLeft() + mLocationInScreen[0];
            mRleBottom = (480 + RegionParam.standardOffsetY + 471) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mRleRect.set(mRleLeft, mRleTop, mRleRight, mRleBottom); //设置右下肢的坐标范围

            mNdLeft = 95 * bodyImageView.getWidth() / RegionParam.standardWidth + bodyImageView.getLeft() + mLocationInScreen[0];
            mNdTop = (396 + RegionParam.standardOffsetY) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mNdRight = (95 + 261) * bodyImageView.getWidth() / RegionParam.standardWidth + bodyImageView.getLeft() + mLocationInScreen[0];
            mNdBottom = (396 + RegionParam.standardOffsetY + 85) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mNakednessRect.set(mNdLeft, mNdTop, mNdRight, mNdBottom);


            mBackHeadY = (221 + RegionParam.standardOffsetY) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mUpperPartY = (221 + RegionParam.standardOffsetY + 365) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;
            mMiddlePartY = (221 + RegionParam.standardOffsetY + 365 + 190) * bodyImageViewHeight / RegionParam.standardHeight + mLocationInScreen[1] + paddingTop;

            initParametersForRegionLocation();

        }
    }

    private void initParametersForRegionLocation(){
        int middleAlignmentX = this.getWidth() / 2 - this.getPaddingLeft(); //+ mLocationInScreen[0]

        for(Map.Entry<Integer,Region[]> item : RegionParam.regionItems.entrySet()){
            Region[] regions = item.getValue();
            for(int i = 0; i < regions.length; i++){
                initLocationForRegion(regions[i], middleAlignmentX);
            }
        }

        initLocationForRegion(Region.SKIN, middleAlignmentX);

    }

    private void initLocationForRegion(Region region, int middleAlignmentX){
        if(Region.LayoutSide.LEFT == region.getLayoutSide())
            region.setStartX(middleAlignmentX - (region.getOffsetSX() * bodyImageViewHeight / RegionParam.standardHeight));
        else
            region.setStartX(middleAlignmentX + (region.getOffsetSX() * bodyImageViewHeight / RegionParam.standardHeight));

        region.setStartY(region.getOffsetSY() * bodyImageViewHeight / RegionParam.standardHeight + RegionParam.standardOffsetY);
        region.setDestinationY( region.getOffsetDY() * bodyImageViewHeight / RegionParam.standardHeight + (RegionParam.OFFSET_Y * region.getOffSetNum()) );

    }

    private int touchPointInRegion(int x, int y){
        initParametersForRegion();
        Log.d(TAG, "mHandX1 is " + mHandX1);
        Log.d(TAG, "mHandX2 is " + mHandX2);
        Log.d(TAG, "mHeadY is " + mHeadY);
        Log.d(TAG, "mChestY is " + mChestY);
        Log.d(TAG, "mWaistY is " + mWaistY);
        Log.d(TAG, "mNakedness is " + mNakedness);
        /*Log.d(TAG, "bodyImageViewHeight is " + bodyImageViewHeight);
        Log.d(TAG, "bodyImageView.getWidth() is " + bodyImageView.getWidth());
        Log.d(TAG, "bodyImageView.getLeft() is " + bodyImageView.getLeft());*/
        Log.d(TAG, "mLeftLowerExtremity is " + mLeftLowerExtremity);
        /*Log.d(TAG, "mRightLowerExtremity is " + mRightLowerExtremity);*/
        Log.d(TAG, "rect.contains is " + mRleRect.contains(x, y));
        Log.d(TAG, "rect.toString is " + mRleRect.toString());

        if(floatImageView != null){
            mFrameLayout.removeView(floatImageView);
        }
        floatImageView = new ImageView(mContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        floatImageView.setLayoutParams(layoutParams);

        if(HumanBodyWidget.mShowingBack){
            if(x < mHandX1 || x > mHandX2)
                return RegionParam.REGION_BACK_UPPER_PART;
            else if (y < mBackHeadY)
                return RegionParam.REGION_BACK_HEAD;
            else if(y < mUpperPartY)
                return RegionParam.REGION_BACK_UPPER_PART;
            else if(y < mMiddlePartY)
                return RegionParam.REGION_BACK_MIDDLE_PART;
            else
                return RegionParam.REGION_BACK_LOWER_PART;

        }else {
            if(mRleRect.contains(x , y)){ //右下肢
                floatImageView.setImageResource(R.mipmap.diagnose_man_right_leg);
                floatImageView.setTag(Region.LEG);
                mFrameLayout.addView(floatImageView);
                return RegionParam.REGION_FRONT_LEG;
            }
            if(mNakednessRect.contains(x, y)){ //下体
                floatImageView.setImageResource(R.mipmap.diagnose_man_middle);
                floatImageView.setTag(Region.BACKPELVIC);
                mFrameLayout.addView(floatImageView);
                return RegionParam.REGION_FRONT_NAKEDNESS;
            }
            if(x < mHandX1){ //左上肢
                floatImageView.setImageResource(R.mipmap.diagnose_man_left_arm);
                floatImageView.setTag(Region.HAND);
                mFrameLayout.addView(floatImageView);
                return RegionParam.REGION_FRONT_HAND;
            }else if(x > mHandX2){ //右上肢
                floatImageView.setImageResource(R.mipmap.diagnose_man_right_arm);
                floatImageView.setTag(Region.HAND);
                mFrameLayout.addView(floatImageView);
                return RegionParam.REGION_FRONT_HAND;
            }else if (y < mHeadY){ //头部
                //Drawable drawable = getResources().getDrawable(R.drawable.body_parts_head_selector);
                /*bodyImageView.setBackgroundDrawable(addStateDrawable(mContext, R.mipmap.man_front, R.mipmap.diagnose_man_head, R.mipmap.diagnose_man_head));*/
                floatImageView.setImageResource(R.mipmap.diagnose_man_head);
                floatImageView.setTag(Region.HEAD);
                mFrameLayout.addView(floatImageView);
                return RegionParam.REGION_FRONT_HEAD;
            }
            else if(y < mChestY){ //胸部
                floatImageView.setImageResource(R.mipmap.diagnose_man_chest);
                floatImageView.setTag(Region.CHEST);
                mFrameLayout.addView(floatImageView);
                return RegionParam.REGION_FRONT_CHEST;
            }
            else if(y < mWaistY){ //腹部
                floatImageView.setImageResource(R.mipmap.diagnose_man_belly);
                floatImageView.setTag(Region.ABDOMEN);
                mFrameLayout.addView(floatImageView);
                return RegionParam.REGION_FRONT_WAIST;
            }
            /*else if(y < mNakedness){ //下体 nekedness
                floatImageView.setImageResource(R.mipmap.diagnose_man_middle);
                mFrameLayout.addView(floatImageView);
                return RegionParam.REGION_FRONT_NAKEDNESS;
            }*/else{ //下肢
                if(x < mLeftLowerExtremity /*&& x >= mHandX1*/){ //点击处为左下肢(左腿)区域
                    floatImageView.setImageResource(R.mipmap.diagnose_man_left_leg);
                    floatImageView.setTag(Region.LEG);
                    mFrameLayout.addView(floatImageView);
                }/*else if(x > mRightLowerExtremity*//* && x <= mHandX2*//*){ //点击处为右下肢(右腿)区域
                    floatImageView.setImageResource(R.mipmap.diagnose_man_right_leg);
                }*/
                return RegionParam.REGION_FRONT_LEG;
            }
        }

    }

    private boolean isTouchPointInTransparent(int x, int y){
        if(bodyImageView == null) {
            return true;
        }
        int rootLeft = mLocationInScreen[0];
        int rootTop = mLocationInScreen[1];
        int imageLeft = bodyImageView.getLeft(); //获取bodyImageView相对于其父级(FrameLayout)的左边距坐标
        int imageTop = bodyImageView.getTop(); //获取bodyImageView相对于其腹肌(FrameLayout)的上边距坐标
        int imageHeight = bodyImageView.getHeight(); //获取bodyImageView在屏幕上(布局中)显示的高度
        int imageWidth = bodyImageView.getWidth(); //获取bodyImageView在屏幕上(布局中)显示的宽度

        if (imageWidth == 0 || imageHeight == 0) {
            return true;
        }

        //获得ImageView设置的图片
        Drawable drawable = bodyImageView.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        //图片实际大小
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int intrinsicWidth = drawable.getIntrinsicWidth();

        //locationInBitmapX、locationInBitmapY取的是实际大小图片上的坐标
        //(intrinsicWidth / imageWidth) = (实际大小 / 手机显示大小)（比率）,
        // "intrinsicWidth / imageWidth" 不加括号是因为java中的 "/"取的是整数，小数都失去了，精确度不够。
        int locationInBitmapX = (x - rootLeft - imageLeft - this.getPaddingLeft()) * intrinsicWidth / imageWidth;
        int locationInBitmapY = (y - rootTop - imageTop - this.getPaddingTop()) * intrinsicHeight / imageHeight;

        try {
            //获取点击处在实际图片上的颜色代码值，如果色值是透明的，代表点击处不是人体部分。
            //如果色值非透明，代表点击处是人体部分。
            //除人体外，其他区域均为透明色值。
            int pixel = bitmap.getPixel(locationInBitmapX, locationInBitmapY);
            Log.d(TAG, "pixel is " + pixel);
            if (Color.TRANSPARENT == pixel)
                return true; //表示点击处是非人体部分
        }catch (IllegalArgumentException e){
            return true;
        }
        return false;
    }

    private ImageView getBodyImageView(){
        ImageView imageView;
        if (HumanBodyWidget.mShowingBack)
            imageView = (ImageView) this.findViewById(R.id.body_back);
        else
            imageView = (ImageView) this.findViewById(R.id.body_front);

        return imageView;
    }

    private void getBodyFrameLayout(){
        mFrameLayout = (FrameLayout) this.findViewById(R.id.body_container);
    }

    private void refresh(int regionType){
        regionPathView.setAdapter(regionType);
        regionView.setAdapter(regionType);
    }

    public void setRegionView(RegionView regionView) {
        this.regionView = regionView;
    }

    public void setRegionType(int regionType) {
        this.regionType = regionType;
    }

    private StateListDrawable addStateDrawable(Context context, int idNormal, int idPressed, int idFocused) {
        StateListDrawable sd = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
        Drawable focus = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
        //注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
        //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
        sd.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focus);
        sd.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        sd.addState(new int[]{android.R.attr.state_focused}, focus);
        sd.addState(new int[]{android.R.attr.state_pressed}, pressed);
        sd.addState(new int[]{android.R.attr.state_enabled}, normal);
        sd.addState(new int[]{}, normal);
        return sd;
    }

    public FrameLayout getmFrameLayout() {
        return mFrameLayout;
    }

    public ImageView getFloatImageView() {
        return floatImageView;
    }
}
