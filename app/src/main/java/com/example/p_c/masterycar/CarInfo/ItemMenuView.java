package com.example.p_c.masterycar.CarInfo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.example.p_c.masterycar.R;

/**
 * Created by p-c on 2016/7/24.
 */


public class ItemMenuView extends FrameLayout {


    private Animation mExpandAnimation;
    private Animation mCollapseAnimation;
    private boolean mIsExpand;
    private  View view = null;

    public ItemMenuView(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }
    public ItemMenuView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        // TODO Auto-generated constructor stub
    }
    public ItemMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initExpandView();
    }
    private void initExpandView() {
        LayoutInflater.from(getContext()).inflate(R.layout.car_item_menu, this, true);

        mExpandAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.expand);
        mExpandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.VISIBLE);
            }
        });

        mCollapseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.collapse);
        mCollapseAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
            }
        });

    }
    public void collapse() {
        if (mIsExpand) {
            mIsExpand = false;
            clearAnimation();
            startAnimation(mCollapseAnimation);
        }
    }

    public void expand() {
        if (!mIsExpand) {
            mIsExpand = true;
            clearAnimation();
            startAnimation(mExpandAnimation);
        }
    }

    public boolean isExpand() {
        return mIsExpand;
    }

    public void setContentView(){
        view = LayoutInflater.from(getContext()).inflate(R.layout.car_item_menu, null);
        removeAllViews();
        addView(view);
    }

    public View getContentView(){
        return view;
    }
}