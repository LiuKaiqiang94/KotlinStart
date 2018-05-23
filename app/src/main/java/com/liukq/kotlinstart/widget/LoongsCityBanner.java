package com.liukq.kotlinstart.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liukq.kotlinstart.R;
import com.nineoldandroids.view.ViewHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 1.支持图片无限轮播控件;
 * 2.支持自定义指示器的背景和两种状态指示点;
 * 3.支持隐藏指示器、设置是否轮播、设置轮播时间间隔;
 * 4.支持设置图片描述;
 * 5.支持自定义图片切换动画、以及设置图片切换速度.
 * 6.支持设置提示性文字  不需要的时候直接设置提示性文字数据为null即可;
 */
public class LoongsCityBanner extends RelativeLayout implements LoongsCityBannerViewPager.AutoPlayDelegate, ViewPager.OnPageChangeListener {
    private static final int RMP = LayoutParams.MATCH_PARENT;
    private static final int RWC = LayoutParams.WRAP_CONTENT;
    private static final int LWC = LinearLayout.LayoutParams.WRAP_CONTENT;

    private static final int VEL_THRESHOLD = 400;
    private int mPageScrollPosition;
    private float mPageScrollPositionOffset;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnItemClickListener mOnItemClickListener;

    //指示点位置
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    private AutoSwitchTask mAutoSwitchTask;

    private LinearLayout mPointRealContainerLl;

    private LoongsCityBannerViewPager mViewPager;

    //指示点左右内间距
    private int mPointLeftRightPading;

    //指示点上下内间距
    private int mPointTopBottomPading;

    //指示点容器左右内间距
    private int mPointContainerLeftRightPadding;

    //资源集合
    private List<?> mModels;

    //视图集合
    private List<View> mViews;

    //是否只有一张图片
    private boolean mIsOneImg = false;

    //是否开启自动轮播
    private boolean mIsAutoPlay = true;

    //是否正在播放
    private boolean mIsAutoPlaying = false;

    //自动播放时间
    private int mAutoPlayTime = 5000;

    //是否允许用户滑动
    private boolean mIsAllowUserScroll = true;

    //viewpager从最后一张到第一张的动画效果
    private int mSlideScrollMode = OVER_SCROLL_ALWAYS;

    //指示点位置
    private int mPointPosition = CENTER;

    //正常状态下的指示点
    private Drawable mPointNormal;

    //选中状态下的指示点
    private Drawable mPointSelected;

    //默认指示点资源
    private int mPointDrawableResId = R.drawable.banner_point_selector;

    //指示容器背景
    private Drawable mPointContainerBackgroundDrawable;

    //指示容器布局规则
    private LayoutParams mPointRealContainerLp;

    //提示语
    private TextView mTipTv;

    //提示文案数据
    private List<String> mTipData;

    //提示语字体颜色
    private int mTipTextColor;

    //指示点是否可见
    private boolean mPointsIsVisible = true;

    //提示语字体大小
    private int mTipTextSize;

    //指示器容器位置
    public static final int TOP = 10;
    public static final int BOTTOM = 12;

    private int mPointContainerPosition = BOTTOM;

    private XBannerAdapter mAdapter;

    //指示器容器
    private LayoutParams mPointContainerLp;

    //是否是数字指示器
    private boolean mIsNumberIndicator = false;
    private TextView mNumberIndicatorTv;

    //数字指示器背景
    private Drawable mNumberIndicatorBackground;

    //只有一张图片时是否显示指示点
    private boolean isShowIndicatorOnlyOne = false;

    //默认图片切换速度为1s
    private int mPageChangeDuration = 1000;

    public void setmAdapter(XBannerAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }


    public LoongsCityBanner(Context context) {
        this(context, null);
    }

    public LoongsCityBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoongsCityBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultAttrs(context);
        initCustomAttrs(context, attrs);
        initView(context);
    }

    //设置指示灯的样式
    public void setmPointDrawableResId(int mPointDrawableResId) {
        this.mPointDrawableResId = mPointDrawableResId;
    }

    private void initDefaultAttrs(Context context) {
        mAutoSwitchTask = new AutoSwitchTask(this);
        mPointLeftRightPading = LoongsCityBannerUtil.INSTANCE.dp2px(context, 3);
        mPointTopBottomPading = LoongsCityBannerUtil.INSTANCE.dp2px(context, 6);
        mPointContainerLeftRightPadding = LoongsCityBannerUtil.INSTANCE.dp2px(context, 10);
        mTipTextSize = LoongsCityBannerUtil.INSTANCE.sp2px(context, 10);
        //设置默认提示语字体颜色
        mTipTextColor = Color.WHITE;
        //设置指示器背景
        mPointContainerBackgroundDrawable = new ColorDrawable(Color.parseColor("#44aaaaaa"));
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoongsCityBanner);
        if (typedArray != null) {
            mIsAutoPlay = typedArray.getBoolean(R.styleable.LoongsCityBanner_isAutoPlay, true);
            mAutoPlayTime = typedArray.getInteger(R.styleable.LoongsCityBanner_AutoPlayTime, 5000);
            mPointsIsVisible = typedArray.getBoolean(R.styleable.LoongsCityBanner_pointsVisibility, true);
            mPointPosition = typedArray.getInt(R.styleable.LoongsCityBanner_pointsPosition, CENTER);
            mPointContainerLeftRightPadding = typedArray.getDimensionPixelSize(R.styleable.LoongsCityBanner_pointContainerLeftRightPadding, mPointContainerLeftRightPadding);
            mPointLeftRightPading = typedArray.getDimensionPixelSize(R.styleable.LoongsCityBanner_pointLeftRightPadding, mPointLeftRightPading);
            mPointTopBottomPading = typedArray.getDimensionPixelSize(R.styleable.LoongsCityBanner_pointTopBottomPadding, mPointTopBottomPading);
            mPointContainerPosition = typedArray.getInt(R.styleable.LoongsCityBanner_pointContainerPosition, BOTTOM);
            mPointContainerBackgroundDrawable = typedArray.getDrawable(R.styleable.LoongsCityBanner_pointsContainerBackground);
            mPointNormal = typedArray.getDrawable(R.styleable.LoongsCityBanner_pointNormal);
            mPointSelected = typedArray.getDrawable(R.styleable.LoongsCityBanner_pointSelect);
            mTipTextColor = typedArray.getColor(R.styleable.LoongsCityBanner_tipTextColor, mTipTextColor);
            mTipTextSize = typedArray.getDimensionPixelSize(R.styleable.LoongsCityBanner_tipTextSize, mTipTextSize);
            mIsNumberIndicator = typedArray.getBoolean(R.styleable.LoongsCityBanner_isShowNumberIndicator, mIsNumberIndicator);
            mNumberIndicatorBackground = typedArray.getDrawable(R.styleable.LoongsCityBanner_numberIndicatorBacgroud);
            isShowIndicatorOnlyOne = typedArray.getBoolean(R.styleable.LoongsCityBanner_isShowIndicatorOnlyOne, isShowIndicatorOnlyOne);
            mPageChangeDuration = typedArray.getInt(R.styleable.LoongsCityBanner_pageChangeDuration, mPageChangeDuration);
            typedArray.recycle();
        }
    }

    private void initView(Context context) {

        //设置指示器背景容器
        RelativeLayout pointContainerRl = new RelativeLayout(context);
        pointContainerRl.setBackground(mPointContainerBackgroundDrawable);

        //设置内边距
        pointContainerRl.setPadding(mPointContainerLeftRightPadding, mPointTopBottomPading, mPointContainerLeftRightPadding, mPointTopBottomPading);

        //设定指示器容器布局及位置
        mPointContainerLp = new LayoutParams(RMP, RWC);
        mPointContainerLp.addRule(mPointContainerPosition);
        addView(pointContainerRl, mPointContainerLp);
        mPointRealContainerLp = new LayoutParams(RWC, RWC);
        //设置指示器容器
        if (mIsNumberIndicator) {
            mNumberIndicatorTv = new TextView(context);
            mNumberIndicatorTv.setId(R.id.xbanner_pointId);
            mNumberIndicatorTv.setGravity(Gravity.CENTER);
            mNumberIndicatorTv.setSingleLine(true);
            mNumberIndicatorTv.setEllipsize(TextUtils.TruncateAt.END);
            mNumberIndicatorTv.setTextColor(mTipTextColor);
            mNumberIndicatorTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipTextSize);
            mNumberIndicatorTv.setVisibility(View.INVISIBLE);
            if (mNumberIndicatorBackground != null) {
                mNumberIndicatorTv.setBackground(mNumberIndicatorBackground);
            }
            pointContainerRl.addView(mNumberIndicatorTv, mPointRealContainerLp);
        } else {
            mPointRealContainerLl = new LinearLayout(context);
            mPointRealContainerLl.setOrientation(LinearLayout.HORIZONTAL);
            mPointRealContainerLl.setId(R.id.xbanner_pointId);
            pointContainerRl.addView(mPointRealContainerLl, mPointRealContainerLp);
        }

        //设置指示器是否可见
        if (mPointRealContainerLl != null) {
            if (mPointsIsVisible) {
                mPointRealContainerLl.setVisibility(View.VISIBLE);
            } else {
                mPointRealContainerLl.setVisibility(View.GONE);
            }
        }

        //设置提示语
        LayoutParams tipLp = new LayoutParams(RMP, RWC);
        tipLp.addRule(CENTER_VERTICAL);
        mTipTv = new TextView(context);
        mTipTv.setGravity(Gravity.CENTER_VERTICAL);
        mTipTv.setSingleLine(true);
        mTipTv.setEllipsize(TextUtils.TruncateAt.END);
        mTipTv.setTextColor(mTipTextColor);
        mTipTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipTextSize);
        pointContainerRl.addView(mTipTv, tipLp);

        //设置指示器布局位置
        if (CENTER == mPointPosition) {
            mPointRealContainerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            tipLp.addRule(RelativeLayout.LEFT_OF, R.id.xbanner_pointId);
        } else if (LEFT == mPointPosition) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            tipLp.addRule(RelativeLayout.RIGHT_OF, R.id.xbanner_pointId);
            mTipTv.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        } else if (RIGHT == mPointPosition) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tipLp.addRule(RelativeLayout.LEFT_OF, R.id.xbanner_pointId);
        }

    }

    /**
     * 设置banner数据
     */
    public void setData(List<View> views, List<? extends Object> data, List<String> tips) {

        this.mModels = data;
        this.mTipData = tips;
        this.mViews = views;

        mIsOneImg = data.size() <= 1;
        //初始化ViewPager
        if (!data.isEmpty())
            initViewPager();
    }

    public void setData(@LayoutRes int layoutResId, List<?> models, List<String> tips) {
        mViews = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            mViews.add(View.inflate(getContext(), layoutResId, null));
        }
        setData(mViews, models, tips);
    }

    /**
     * 设置指示点是否可见
     */
    public void setPointsIsVisible(boolean isVisible) {
        if (null != mPointRealContainerLl) {
            if (isVisible) {
                mPointRealContainerLl.setVisibility(View.VISIBLE);
            } else {
                mPointRealContainerLl.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 对应三个位置 CENTER,RIGHT,LEFT
     */
    public void setPoinstPosition(int position) {
        //设置指示器布局位置
        if (CENTER == position) {
            mPointRealContainerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else if (LEFT == position) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (RIGHT == position) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

    /**
     * 设置指示器容器的位置  TOP,BOTTOM
     */
    public void setmPointContainerPosition(int position) {
        if (BOTTOM == position) {
            mPointContainerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else if (TOP == position) {
            mPointContainerLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }
    }

    private void initViewPager() {
        //添加ViewPager
        if (mViewPager != null && this.equals(mViewPager.getParent())) {
            removeView(mViewPager);
            mViewPager = null;
        }
        mViewPager = new LoongsCityBannerViewPager(getContext());

        if (mPointRealContainerLl != null) {
            mPointRealContainerLl.removeAllViews();
        }
        //当图片多于1张时添加指示点
        if (isShowIndicatorOnlyOne || (!mIsOneImg)) {
            addPoints();
        }
        //初始化ViewPager
        mViewPager.setAdapter(new XBannerPageAdapter());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOverScrollMode(mSlideScrollMode);
        mViewPager.setIsAllowUserScroll(mIsAllowUserScroll);
        setPageChangeDuration(mPageChangeDuration);
        addView(mViewPager, 0, new LayoutParams(RMP, RMP));

        //当图片多于1张时开始轮播
        if (!mIsOneImg && mIsAutoPlay) {
            mViewPager.setAutoPlayDelegate(this);
            int zeroItem = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % getRealCount();
            mViewPager.setCurrentItem(zeroItem, false);
            startAutoPlay();
        } else {
            switchToPoint(0);
        }
    }

    /**
     * 获取广告页面数量
     */
    public int getRealCount() {
        return mModels == null ? 0 : mModels.size();
    }

    public LoongsCityBannerViewPager getViewPager() {
        return mViewPager;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        mPageScrollPosition = position;
        mPageScrollPositionOffset = positionOffset;

        if (mTipTv != null && mTipData != null) {
            if (positionOffset > .5) {
                mTipTv.setText(mTipData.get((position + 1) % mTipData.size()));
                ViewHelper.setAlpha(mTipTv, positionOffset);
            } else {
                mTipTv.setText(mTipData.get(position % mTipData.size()));
                ViewHelper.setAlpha(mTipTv, 1 - positionOffset);
            }
        }

        if (null != mOnPageChangeListener)
            mOnPageChangeListener.onPageScrolled(position % getRealCount(), positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        position = position % getRealCount();
        switchToPoint(position);

        if (mOnPageChangeListener != null)
            mOnPageChangeListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null)
            mOnPageChangeListener.onPageScrollStateChanged(state);

    }

    @Override
    public void handleAutoPlayActionUpOrCancel(float xVelocity) {
        assert mViewPager != null;
        if (mPageScrollPosition < mViewPager.getCurrentItem()) {
            // 往右滑
            if (xVelocity > VEL_THRESHOLD || (mPageScrollPositionOffset < 0.7f && xVelocity > -VEL_THRESHOLD)) {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition);
            } else {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition + 1);
            }
        } else {
            // 往左滑
            if (xVelocity < -VEL_THRESHOLD || (mPageScrollPositionOffset > 0.3f && xVelocity < VEL_THRESHOLD)) {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition + 1);
            } else {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition);
            }
        }
    }

    private class XBannerPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            //当只有一张图片时返回1
            if (mIsOneImg) {
                return 1;
            }
            return mIsAutoPlay ? Integer.MAX_VALUE : getRealCount();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            final int realPosition = position % getRealCount();
            View view;

            view = mViews.get(realPosition);
            if (container.equals(view.getParent())) {
                container.removeView(view);
            }

            if (mOnItemClickListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(LoongsCityBanner.this, realPosition);
                    }
                });
            }

            if (null != mAdapter && mModels.size() != 0) {
                mAdapter.loadBanner(LoongsCityBanner.this, view, realPosition);
            }

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    /**
     * 添加指示点
     */
    private void addPoints() {
        if (mPointRealContainerLl != null) {
            mPointRealContainerLl.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LWC, LWC);
            lp.setMargins(mPointLeftRightPading, mPointTopBottomPading, mPointLeftRightPading, mPointTopBottomPading);
            ImageView imageView;
            for (int i = 0; i < getRealCount(); i++) {
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(lp);
                if (mPointNormal != null && mPointSelected != null) {
                    imageView.setImageDrawable(LoongsCityBannerUtil.INSTANCE.getSelector(mPointNormal, mPointSelected));
                } else {
                    imageView.setImageResource(mPointDrawableResId);
                }
                mPointRealContainerLl.addView(imageView);
            }
        }

        if (mNumberIndicatorTv != null) {
            if (isShowIndicatorOnlyOne || (!mIsOneImg)) {
                mNumberIndicatorTv.setVisibility(View.VISIBLE);
            } else {
                mNumberIndicatorTv.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 切换指示器
     */
    private void switchToPoint(final int currentPoint) {
        if (mPointRealContainerLl != null & mModels != null && getRealCount() > 1) {
            for (int i = 0; i < mPointRealContainerLl.getChildCount(); i++) {
                mPointRealContainerLl.getChildAt(i).setEnabled(false);
            }
            mPointRealContainerLl.getChildAt(currentPoint).setEnabled(true);
        }

        if (mTipTv != null && mTipData != null) {
            mTipTv.setText(mTipData.get(currentPoint));
        }

        if (mNumberIndicatorTv != null && mViews != null && (isShowIndicatorOnlyOne || (!mIsOneImg))) {
            mNumberIndicatorTv.setText((currentPoint + 1) + "/" + mViews.size());
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mIsAutoPlay && !mIsOneImg) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stopAutoPlay();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    startAutoPlay();
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 开始播放
     */
    public void startAutoPlay() {
        if (mIsAutoPlay && !mIsAutoPlaying) {
            mIsAutoPlaying = true;
            postDelayed(mAutoSwitchTask, mAutoPlayTime);
        }
    }

    /**
     * 停止播放
     */
    public void stopAutoPlay() {
        if (mIsAutoPlay && mIsAutoPlaying) {
            mIsAutoPlaying = false;
            removeCallbacks(mAutoSwitchTask);
        }
    }

    /**
     * 添加ViewPager滚动监听器
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    /**
     * 设置图片从最后一张滚动到第一张的动画效果
     */
    public void setSlideScrollMode(int slideScrollMode) {
        mSlideScrollMode = slideScrollMode;
        if (null != mViewPager) {
            mViewPager.setOverScrollMode(slideScrollMode);
        }
    }

    /**
     * 设置是否允许用户手指滑动
     *
     * @param allowUserScrollable true表示允许跟随用户触摸滑动，false反之
     */
    public void setAllowUserScrollable(boolean allowUserScrollable) {
        mIsAllowUserScroll = allowUserScrollable;
        if (null != mViewPager) {
            mViewPager.setIsAllowUserScroll(allowUserScrollable);
        }
    }

    /**
     * 设置是否自动轮播
     */
    public void setmAutoPlayAble(boolean mAutoPlayAble) {
        this.mIsAutoPlay = mAutoPlayAble;
    }

    /**
     * 设置自动轮播时间间隔
     */
    public void setmAutoPalyTime(int mAutoPalyTime) {
        this.mAutoPlayTime = mAutoPalyTime;
    }

    /**
     * 自定义翻页动画效果
     */
    public void setCustomPageTransformer(ViewPager.PageTransformer transformer) {
        if (transformer != null && mViewPager != null) {
            mViewPager.setPageTransformer(true, transformer);
        }
    }

    /**
     * 设置ViewPager切换速度
     */
    public void setPageChangeDuration(int duration) {
        if (mViewPager != null) {
            mViewPager.setScrollDuration(duration);
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (VISIBLE == visibility) {
            startAutoPlay();
        } else if (INVISIBLE == visibility) {
            stopAutoPlay();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoPlay();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAutoPlay();
    }

    private static class AutoSwitchTask implements Runnable {
        private final WeakReference<LoongsCityBanner> mXBanner;

        private AutoSwitchTask(LoongsCityBanner mLoongsCityBanner) {
            this.mXBanner = new WeakReference<>(mLoongsCityBanner);
        }

        @Override
        public void run() {
            LoongsCityBanner banner = mXBanner.get();
            if (banner != null) {
                banner.switchNextPage();
                banner.postDelayed(banner.mAutoSwitchTask, banner.mAutoPlayTime);
            }
        }
    }


    private void switchNextPage() {
        try {
            if (mViewPager != null) {
                int currentItem = mViewPager.getCurrentItem() + 1;
                mViewPager.setCurrentItem(currentItem);
            }
        } catch (Exception ignored) {
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(LoongsCityBanner banner, int position);
    }

    public interface XBannerAdapter {
        void loadBanner(LoongsCityBanner banner, View view, int position);
    }
}
