/*
 * Copyright (C) 2018 Robert Koziej
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.robertkoziej.forwardnavigiation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public final class ForwardNavigation extends View {
    public static String TAG = ForwardNavigation.class.getSimpleName();
    private Context mContext;
    private int mLayoutResource = 0;
    private int mInflatedId;
    private int mCurrentView;
    private int mViewCount;
    private int mNavigationIndicatorActiveColor;
    private int mNavigationIndicatorDefaultColor;
    private int mButtonTextColor;
    private int mBackgroundColor;
    private Button mBackButton;
    private Button mNextButton;
    private String mLastViewButtonText;
    private WeakReference<View> mInflatedViewRef;
    private LayoutInflater mInflater;
    private OnInflateListener mInflateListener;
    private OnClickListener mOnClickListener;

    public class ViewCount {
        public final static int TWO = 2;
        public final static int THREE = 3;
        public final static int FOUR = 4;
        public final static int FIVE = 5;
        public final static int SIX = 6;
        public final static int SEVEN = 7;
    }

    public ForwardNavigation(Context context) {
        super(context);
        initialize(context);
    }

    public ForwardNavigation
            (Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForwardNavigation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForwardNavigation,
                defStyle, 0);
        mInflatedId = a.getResourceId(R.styleable.ForwardNavigation_fn_inflatedId, NO_ID);
        mViewCount = a.getInt(R.styleable.ForwardNavigation_fn_viewCount, ViewCount.TWO);
        mCurrentView = a.getInt(R.styleable.ForwardNavigation_fn_currentView, 0);
        mButtonTextColor = a.getColor(R.styleable.ForwardNavigation_fn_buttonTextColor,
                ContextCompat.getColor(context, R.color.fn_buttonTextColor));
        mLastViewButtonText = a.getString(R.styleable.ForwardNavigation_fn_lastViewNextButtonText);
        mBackgroundColor = a.getColor(R.styleable.ForwardNavigation_fn_backgroundColor,
                ContextCompat.getColor(context, R.color.fn_backgroundColor));
        mNavigationIndicatorActiveColor = a.getColor(R.styleable.ForwardNavigation_fn_navigationIndicatorActiveColor,
                ContextCompat.getColor(context, R.color.fn_navigationIndicatorActiveColor));
        mNavigationIndicatorDefaultColor = a.getColor(R.styleable.ForwardNavigation_fn_navigationIndicatorDefaultColor,
                ContextCompat.getColor(context, R.color.fn_navigationIndicatorDefaultColor));
        a.recycle();
        initialize(context);
    }

    private void initialize(Context context) {
        mContext = context;
        mLayoutResource = getNavLayout(mViewCount);
        setLayoutResource(mLayoutResource);
        setVisibility(GONE);
        setWillNotDraw(true);
    }

    private void initializeBackground(View view) {
        ConstraintLayout forwardNavigation = view.findViewById(R.id.forward_navigation_container);
        ConstraintLayout mIndicatorContainer = view.findViewById(R.id.forward_navigation_indicator_container);

        forwardNavigation.setBackgroundColor(mBackgroundColor);
        mIndicatorContainer.setBackgroundColor(mBackgroundColor);
        mBackButton.setBackgroundColor(mBackgroundColor);
        mNextButton.setBackgroundColor(mBackgroundColor);
    }

    private void initializeIndicators(View view) {
        ImageView c0 = view.findViewById(R.id.forward_navigation_c0);
        ImageView c1 = view.findViewById(R.id.forward_navigation_c1);
        ImageView c2 = view.findViewById(R.id.forward_navigation_c2);
        ImageView c3 = view.findViewById(R.id.forward_navigation_c3);
        ImageView c4 = view.findViewById(R.id.forward_navigation_c4);
        ImageView c5 = view.findViewById(R.id.forward_navigation_c5);
        ImageView c6 = view.findViewById(R.id.forward_navigation_c6);
        List<ImageView> indicators = Arrays.asList(c0, c1, c2, c3, c4, c5, c6);

        for (int i = 0; i < mViewCount; i++) {
            if (indicators.get(i) != null) {
                if (i == mCurrentView)
                    indicators.get(i).getDrawable().setColorFilter(mNavigationIndicatorActiveColor, PorterDuff.Mode.SRC);
                else
                    indicators.get(i).getDrawable().setColorFilter(mNavigationIndicatorDefaultColor, PorterDuff.Mode.SRC);
            }
        }
    }

    private void initializeButtons(View view) {
        mBackButton = view.findViewById(R.id.forward_navigation_backButton);
        mBackButton.setTextColor(mButtonTextColor);
        mBackButton.getCompoundDrawables()[0].setColorFilter(mButtonTextColor, PorterDuff.Mode.SRC_IN);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null && mBackButton.getVisibility() == VISIBLE) {
                    mOnClickListener.onBackButtonClicked();
                }
            }
        });

        mNextButton = view.findViewById(R.id.forward_navigation_nextButton);
        mNextButton.setTextColor(mButtonTextColor);
        mNextButton.getCompoundDrawables()[2].setColorFilter(mButtonTextColor, PorterDuff.Mode.SRC_IN);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null && mNextButton.getVisibility() == VISIBLE) {
                    mOnClickListener.onNextButtonClicked();
                }
            }
        });

        handleNextButtonLastViewButtonText(mNextButton, mLastViewButtonText);
        handleBackButtonVisibility(mBackButton, mCurrentView);
    }

    private void handleNextButtonLastViewButtonText(Button nextButton, String lastViewButtonText) {
        if (getCurrentView() == getLastViewNumber() && lastViewButtonText != null)
            nextButton.setText(lastViewButtonText);
    }

    private void handleBackButtonVisibility(Button backButton, int currentView) {
        if (currentView == 0)
            backButton.setVisibility(INVISIBLE);
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    public int getCurrentView() {
        return mCurrentView;
    }

    public void setCurrentView(int currentView) {
        try {
            if (currentView >= 0 && currentView < mViewCount)
                this.mCurrentView = currentView;
            else
                throw new IllegalArgumentException("setCurrentView():" +
                        " argument must not exceed permitted range (0-6)");
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public int getViewCount() {
        return mViewCount;
    }

    public void setViewCount(int viewCount) {
        try {
            if (viewCount >= ViewCount.TWO && viewCount <= ViewCount.SEVEN) {
                mViewCount = viewCount;
                setLayoutResource(getNavLayout(mViewCount));
            } else
                throw new IllegalArgumentException("setViewCount():" +
                        " argument must not exceed ViewCount range (2-7). Argument should always be a ViewCount field, eg. ViewCount.TWO");
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public int getInflatedId() {
        return mInflatedId;
    }

    public void setInflatedId(int inflatedId) {
        mInflatedId = inflatedId;
    }

    private int getLayoutResource() {
        return mLayoutResource;
    }

    private void setLayoutResource(int layoutResource) {
        mLayoutResource = layoutResource;
    }

    public void setLayoutInflater(LayoutInflater inflater) {
        mInflater = inflater;
    }

    public LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    public int getNavigationIndicatorActiveColor() {
        return mNavigationIndicatorActiveColor;
    }

    public void setNavigationIndicatorActiveColor(int activeColor) {
        this.mNavigationIndicatorActiveColor = activeColor;
    }

    public int getNavigationIndicatorDefaultColor() {
        return mNavigationIndicatorDefaultColor;
    }

    public void setNavigationIndicatorDefaultColor(int defaultColor) {
        this.mNavigationIndicatorDefaultColor = defaultColor;
    }

    public int getButtonTextColor() {
        return mButtonTextColor;
    }

    public void setButtonTextColor(int buttonTextColor) {
        this.mButtonTextColor = buttonTextColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
    }

    @Override
    public void setVisibility(int visibility) {
        if (mInflatedViewRef != null) {
            View view = mInflatedViewRef.get();
            if (view != null) {
                view.setVisibility(visibility);
            } else {
                throw new IllegalStateException("setVisibility called on un-referenced view");
            }
        } else {
            super.setVisibility(visibility);
            if (visibility == VISIBLE || visibility == INVISIBLE) {
                inflate();
            }
        }
    }

    private int getNavLayout(int viewCount) {
        switch (viewCount) {
            case ViewCount.TWO:
                return R.layout.forward_navigation_nav_2;
            case ViewCount.THREE:
                return R.layout.forward_navigation_nav_3;
            case ViewCount.FOUR:
                return R.layout.forward_navigation_nav_4;
            case ViewCount.FIVE:
                return R.layout.forward_navigation_nav_5;
            case ViewCount.SIX:
                return R.layout.forward_navigation_nav_6;
            case ViewCount.SEVEN:
                return R.layout.forward_navigation_nav_7;
            default:
                return R.layout.forward_navigation_nav_2;
        }
    }

    public int getLastViewNumber() {
        return mViewCount - 1;
    }

    public void setLastViewButtonText(String text) {
        mLastViewButtonText = text;
    }

    public void setNavigationAttrs(int viewCount, int currentView) {
        setViewCount(viewCount);
        setCurrentView(currentView);
    }

    public void setColorsAttrs(int backgroundColor, int buttonTextColor,
                               int navigationIndicatorActiveColor, int navigationIndicatorDefaultColor) {
        setBackgroundColor(backgroundColor);
        setButtonTextColor(buttonTextColor);
        setNavigationIndicatorActiveColor(navigationIndicatorActiveColor);
        setNavigationIndicatorDefaultColor(navigationIndicatorDefaultColor);
    }

    public View inflate() {
        final ViewParent viewParent = getParent();
        if (viewParent != null && viewParent instanceof ViewGroup) {
            if (mLayoutResource != 0 && mViewCount != 0) {
                final ViewGroup parent = (ViewGroup) viewParent;
                final LayoutInflater factory;
                if (mInflater != null) {
                    factory = mInflater;
                } else {
                    factory = LayoutInflater.from(mContext);
                }
                final View view = factory.inflate(mLayoutResource, parent,
                        false);
                if (mInflatedId != NO_ID) {
                    view.setId(mInflatedId);
                }
                final int index = parent.indexOfChild(this);
                final ViewGroup.LayoutParams layoutParams = getLayoutParams();
                if (layoutParams != null) {
                    parent.addView(view, index, layoutParams);
                } else {
                    parent.addView(view, index);
                }
                initializeButtons(view);
                initializeIndicators(view);
                initializeBackground(view);
                mInflatedViewRef = new WeakReference<View>(view);
                if (mInflateListener != null) {
                    mInflateListener.onInflate(this, view);
                }
                return view;
            } else {
                throw new IllegalArgumentException("ForwardNavigation" +
                        " must have a valid layoutResource");
            }
        } else {
            throw new IllegalStateException("ForwardNavigation" +
                    " must have a non-null ViewGroup viewParent");
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public static interface OnClickListener {
        void onNextButtonClicked();

        void onBackButtonClicked();
    }

    public void setOnInflateListener(OnInflateListener inflateListener) {
        mInflateListener = inflateListener;
    }

    public static interface OnInflateListener {
        void onInflate(ForwardNavigation fn, View inflated);
    }
}