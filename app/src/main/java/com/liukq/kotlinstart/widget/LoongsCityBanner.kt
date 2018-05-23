package com.liukq.kotlinstart.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.LayoutRes
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.liukq.kotlinstart.R
import com.nineoldandroids.view.ViewHelper
import java.lang.ref.WeakReference
import java.util.*

/**
 * 1.支持图片无限轮播控件;
 * 2.支持自定义指示器的背景和两种状态指示点;
 * 3.支持隐藏指示器、设置是否轮播、设置轮播时间间隔;
 * 4.支持设置图片描述;
 * 5.支持自定义图片切换动画、以及设置图片切换速度.
 * 6.支持设置提示性文字  不需要的时候直接设置提示性文字数据为null即可;
 */
class LoongsCityBanner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr), LoongsCityBannerViewPager.AutoPlayDelegate, ViewPager.OnPageChangeListener {
    private var mPageScrollPosition: Int = 0
    private var mPageScrollPositionOffset: Float = 0.toFloat()

    private var mOnPageChangeListener: ViewPager.OnPageChangeListener? = null
    private var mOnItemClickListener: OnItemClickListener? = null

    private var mAutoSwitchTask: AutoSwitchTask? = null

    private var mPointRealContainerLl: LinearLayout? = null

    var viewPager: LoongsCityBannerViewPager? = null
        private set

    //指示点左右内间距
    private var mPointLeftRightPading: Int = 0

    //指示点上下内间距
    private var mPointTopBottomPading: Int = 0

    //指示点容器左右内间距
    private var mPointContainerLeftRightPadding: Int = 0

    //资源集合
    private var mModels: List<*>? = null

    //视图集合
    private var mViews: MutableList<View>? = null

    //是否只有一张图片
    private var mIsOneImg = false

    //是否开启自动轮播
    private var mIsAutoPlay = true

    //是否正在播放
    private var mIsAutoPlaying = false

    //自动播放时间
    private var mAutoPlayTime = 5000

    //是否允许用户滑动
    private var mIsAllowUserScroll = true

    //viewpager从最后一张到第一张的动画效果
    private var mSlideScrollMode = View.OVER_SCROLL_ALWAYS

    //指示点位置
    private var mPointPosition = CENTER

    //正常状态下的指示点
    private var mPointNormal: Drawable? = null

    //选中状态下的指示点
    private var mPointSelected: Drawable? = null

    //默认指示点资源
    private var mPointDrawableResId = R.drawable.banner_point_selector

    //指示容器背景
    private var mPointContainerBackgroundDrawable: Drawable? = null

    //指示容器布局规则
    private var mPointRealContainerLp: RelativeLayout.LayoutParams? = null

    //提示语
    private var mTipTv: TextView? = null

    //提示文案数据
    private var mTipData: List<String>? = null

    //提示语字体颜色
    private var mTipTextColor: Int = 0

    //指示点是否可见
    private var mPointsIsVisible = true

    //提示语字体大小
    private var mTipTextSize: Int = 0

    private var mPointContainerPosition = BOTTOM

    private var mAdapter: XBannerAdapter? = null

    //指示器容器
    private var mPointContainerLp: RelativeLayout.LayoutParams? = null

    //是否是数字指示器
    private var mIsNumberIndicator = false
    private var mNumberIndicatorTv: TextView? = null

    //数字指示器背景
    private var mNumberIndicatorBackground: Drawable? = null

    //只有一张图片时是否显示指示点
    private var isShowIndicatorOnlyOne = false

    //默认图片切换速度为1s
    private var mPageChangeDuration = 1000

    /**
     * 获取广告页面数量
     */
    val realCount: Int
        get() = if (mModels == null) 0 else mModels!!.size

    fun setmAdapter(mAdapter: XBannerAdapter) {
        this.mAdapter = mAdapter
    }

    init {
        initDefaultAttrs(context)
        initCustomAttrs(context, attrs)
        initView(context)
    }

    //设置指示灯的样式
    fun setmPointDrawableResId(mPointDrawableResId: Int) {
        this.mPointDrawableResId = mPointDrawableResId
    }

    private fun initDefaultAttrs(context: Context) {
        mAutoSwitchTask = AutoSwitchTask(this)
        mPointLeftRightPading = LoongsCityBannerUtil.dp2px(context, 3f)
        mPointTopBottomPading = LoongsCityBannerUtil.dp2px(context, 6f)
        mPointContainerLeftRightPadding = LoongsCityBannerUtil.dp2px(context, 10f)
        mTipTextSize = LoongsCityBannerUtil.sp2px(context, 10f)
        //设置默认提示语字体颜色
        mTipTextColor = Color.WHITE
        //设置指示器背景
        mPointContainerBackgroundDrawable = ColorDrawable(Color.parseColor("#44aaaaaa"))
    }

    private fun initCustomAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoongsCityBanner)
        if (typedArray != null) {
            mIsAutoPlay = typedArray.getBoolean(R.styleable.LoongsCityBanner_isAutoPlay, true)
            mAutoPlayTime = typedArray.getInteger(R.styleable.LoongsCityBanner_AutoPlayTime, 5000)
            mPointsIsVisible = typedArray.getBoolean(R.styleable.LoongsCityBanner_pointsVisibility, true)
            mPointPosition = typedArray.getInt(R.styleable.LoongsCityBanner_pointsPosition, CENTER)
            mPointContainerLeftRightPadding = typedArray.getDimensionPixelSize(R.styleable.LoongsCityBanner_pointContainerLeftRightPadding, mPointContainerLeftRightPadding)
            mPointLeftRightPading = typedArray.getDimensionPixelSize(R.styleable.LoongsCityBanner_pointLeftRightPadding, mPointLeftRightPading)
            mPointTopBottomPading = typedArray.getDimensionPixelSize(R.styleable.LoongsCityBanner_pointTopBottomPadding, mPointTopBottomPading)
            mPointContainerPosition = typedArray.getInt(R.styleable.LoongsCityBanner_pointContainerPosition, BOTTOM)
            mPointContainerBackgroundDrawable = typedArray.getDrawable(R.styleable.LoongsCityBanner_pointsContainerBackground)
            mPointNormal = typedArray.getDrawable(R.styleable.LoongsCityBanner_pointNormal)
            mPointSelected = typedArray.getDrawable(R.styleable.LoongsCityBanner_pointSelect)
            mTipTextColor = typedArray.getColor(R.styleable.LoongsCityBanner_tipTextColor, mTipTextColor)
            mTipTextSize = typedArray.getDimensionPixelSize(R.styleable.LoongsCityBanner_tipTextSize, mTipTextSize)
            mIsNumberIndicator = typedArray.getBoolean(R.styleable.LoongsCityBanner_isShowNumberIndicator, mIsNumberIndicator)
            mNumberIndicatorBackground = typedArray.getDrawable(R.styleable.LoongsCityBanner_numberIndicatorBacgroud)
            isShowIndicatorOnlyOne = typedArray.getBoolean(R.styleable.LoongsCityBanner_isShowIndicatorOnlyOne, isShowIndicatorOnlyOne)
            mPageChangeDuration = typedArray.getInt(R.styleable.LoongsCityBanner_pageChangeDuration, mPageChangeDuration)
            typedArray.recycle()
        }
    }

    private fun initView(context: Context) {

        //设置指示器背景容器
        val pointContainerRl = RelativeLayout(context)
        pointContainerRl.background = mPointContainerBackgroundDrawable

        //设置内边距
        pointContainerRl.setPadding(mPointContainerLeftRightPadding, mPointTopBottomPading, mPointContainerLeftRightPadding, mPointTopBottomPading)

        //设定指示器容器布局及位置
        mPointContainerLp = RelativeLayout.LayoutParams(RMP, RWC)
        mPointContainerLp!!.addRule(mPointContainerPosition)
        addView(pointContainerRl, mPointContainerLp)
        mPointRealContainerLp = RelativeLayout.LayoutParams(RWC, RWC)
        //设置指示器容器
        if (mIsNumberIndicator) {
            mNumberIndicatorTv = TextView(context)
            mNumberIndicatorTv!!.id = R.id.xbanner_pointId
            mNumberIndicatorTv!!.gravity = Gravity.CENTER
            mNumberIndicatorTv!!.setSingleLine(true)
            mNumberIndicatorTv!!.ellipsize = TextUtils.TruncateAt.END
            mNumberIndicatorTv!!.setTextColor(mTipTextColor)
            mNumberIndicatorTv!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipTextSize.toFloat())
            mNumberIndicatorTv!!.visibility = View.INVISIBLE
            if (mNumberIndicatorBackground != null) {
                mNumberIndicatorTv!!.background = mNumberIndicatorBackground
            }
            pointContainerRl.addView(mNumberIndicatorTv, mPointRealContainerLp)
        } else {
            mPointRealContainerLl = LinearLayout(context)
            mPointRealContainerLl!!.orientation = LinearLayout.HORIZONTAL
            mPointRealContainerLl!!.id = R.id.xbanner_pointId
            pointContainerRl.addView(mPointRealContainerLl, mPointRealContainerLp)
        }

        //设置指示器是否可见
        if (mPointRealContainerLl != null) {
            if (mPointsIsVisible) {
                mPointRealContainerLl!!.visibility = View.VISIBLE
            } else {
                mPointRealContainerLl!!.visibility = View.GONE
            }
        }

        //设置提示语
        val tipLp = RelativeLayout.LayoutParams(RMP, RWC)
        tipLp.addRule(RelativeLayout.CENTER_VERTICAL)
        mTipTv = TextView(context)
        mTipTv!!.gravity = Gravity.CENTER_VERTICAL
        mTipTv!!.setSingleLine(true)
        mTipTv!!.ellipsize = TextUtils.TruncateAt.END
        mTipTv!!.setTextColor(mTipTextColor)
        mTipTv!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipTextSize.toFloat())
        pointContainerRl.addView(mTipTv, tipLp)

        //设置指示器布局位置
        if (CENTER == mPointPosition) {
            mPointRealContainerLp!!.addRule(RelativeLayout.CENTER_HORIZONTAL)
            tipLp.addRule(RelativeLayout.LEFT_OF, R.id.xbanner_pointId)
        } else if (LEFT == mPointPosition) {
            mPointRealContainerLp!!.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            tipLp.addRule(RelativeLayout.RIGHT_OF, R.id.xbanner_pointId)
            mTipTv!!.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        } else if (RIGHT == mPointPosition) {
            mPointRealContainerLp!!.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            tipLp.addRule(RelativeLayout.LEFT_OF, R.id.xbanner_pointId)
        }

    }

    /**
     * 设置banner数据
     */
    private fun setData(views: List<View>, data: List<Any>, tips: List<String>?) {
        this.mModels = data
        this.mTipData = tips
        this.mViews = views as ArrayList

        mIsOneImg = data.size <= 1
        //初始化ViewPager
        if (!data.isEmpty())
            initViewPager()
    }

    fun setData(@LayoutRes layoutResId: Int, models: List<Any>, tips: List<String>) {
        mViews = ArrayList()
        for (i in models.indices) {
            mViews!!.add(View.inflate(context, layoutResId, null))
        }
        setData(mViews as ArrayList, models, if (tips.isNotEmpty()) tips else null)
    }

    /**
     * 设置指示点是否可见
     */
    fun setPointsIsVisible(isVisible: Boolean) {
        if (null != mPointRealContainerLl) {
            if (isVisible) {
                mPointRealContainerLl!!.visibility = View.VISIBLE
            } else {
                mPointRealContainerLl!!.visibility = View.GONE
            }
        }
    }

    /**
     * 对应三个位置 CENTER,RIGHT,LEFT
     */
    fun setPoinstPosition(position: Int) {
        //设置指示器布局位置
        if (CENTER == position) {
            mPointRealContainerLp!!.addRule(RelativeLayout.CENTER_HORIZONTAL)
        } else if (LEFT == position) {
            mPointRealContainerLp!!.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        } else if (RIGHT == position) {
            mPointRealContainerLp!!.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        }
    }

    /**
     * 设置指示器容器的位置  TOP,BOTTOM
     */
    fun setmPointContainerPosition(position: Int) {
        if (BOTTOM == position) {
            mPointContainerLp!!.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        } else if (TOP == position) {
            mPointContainerLp!!.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        }
    }

    private fun initViewPager() {
        //添加ViewPager
        if (viewPager != null && this == viewPager!!.parent) {
            removeView(viewPager)
            viewPager = null
        }
        viewPager = LoongsCityBannerViewPager(context)

        if (mPointRealContainerLl != null) {
            mPointRealContainerLl!!.removeAllViews()
        }
        //当图片多于1张时添加指示点
        if (isShowIndicatorOnlyOne || !mIsOneImg) {
            addPoints()
        }
        //初始化ViewPager
        viewPager!!.adapter = XBannerPageAdapter()
        viewPager!!.offscreenPageLimit = 1
        viewPager!!.addOnPageChangeListener(this)
        viewPager!!.overScrollMode = mSlideScrollMode
        viewPager!!.setIsAllowUserScroll(mIsAllowUserScroll)
        setPageChangeDuration(mPageChangeDuration)
        addView(viewPager, 0, RelativeLayout.LayoutParams(RMP, RMP))

        //当图片多于1张时开始轮播
        if (!mIsOneImg && mIsAutoPlay) {
            viewPager!!.setAutoPlayDelegate(this)
            val zeroItem = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % realCount
            viewPager!!.setCurrentItem(zeroItem, false)
            startAutoPlay()
        } else {
            switchToPoint(0)
        }
    }


    override fun onPageScrolled(position: Int, positionOffset: Float,
                                positionOffsetPixels: Int) {
        mPageScrollPosition = position
        mPageScrollPositionOffset = positionOffset

        if (mTipTv != null && mTipData != null) {
            if (positionOffset > .5) {
                mTipTv!!.text = mTipData!![(position + 1) % mTipData!!.size]
                ViewHelper.setAlpha(mTipTv!!, positionOffset)
            } else {
                mTipTv!!.text = mTipData!![position % mTipData!!.size]
                ViewHelper.setAlpha(mTipTv!!, 1 - positionOffset)
            }
        }

        if (null != mOnPageChangeListener)
            mOnPageChangeListener!!.onPageScrolled(position % realCount, positionOffset, positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        var position = position
        position = position % realCount
        switchToPoint(position)

        if (mOnPageChangeListener != null)
            mOnPageChangeListener!!.onPageSelected(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (mOnPageChangeListener != null)
            mOnPageChangeListener!!.onPageScrollStateChanged(state)

    }

    override fun handleAutoPlayActionUpOrCancel(xVelocity: Float) {
        assert(viewPager != null)
        if (mPageScrollPosition < viewPager!!.currentItem) {
            // 往右滑
            if (xVelocity > VEL_THRESHOLD || mPageScrollPositionOffset < 0.7f && xVelocity > -VEL_THRESHOLD) {
                viewPager!!.setBannerCurrentItemInternal(mPageScrollPosition)
            } else {
                viewPager!!.setBannerCurrentItemInternal(mPageScrollPosition + 1)
            }
        } else {
            // 往左滑
            if (xVelocity < -VEL_THRESHOLD || mPageScrollPositionOffset > 0.3f && xVelocity < VEL_THRESHOLD) {
                viewPager!!.setBannerCurrentItemInternal(mPageScrollPosition + 1)
            } else {
                viewPager!!.setBannerCurrentItemInternal(mPageScrollPosition)
            }
        }
    }

    private inner class XBannerPageAdapter : PagerAdapter() {

        override fun getCount(): Int {
            //当只有一张图片时返回1
            if (mIsOneImg) {
                return 1
            }
            return if (mIsAutoPlay) Integer.MAX_VALUE else realCount
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val realPosition = position % realCount
            val view: View

            view = mViews!![realPosition]
            if (container == view.parent) {
                container.removeView(view)
            }
            if (mOnItemClickListener != null) {
                view.setOnClickListener { mOnItemClickListener!!.onItemClick(this@LoongsCityBanner, realPosition) }
            }
            if (null != mAdapter && mModels!!.size != 0) {
                mAdapter!!.loadBanner(this@LoongsCityBanner, view, realPosition)
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    /**
     * 添加指示点
     */
    private fun addPoints() {
        if (mPointRealContainerLl != null) {
            mPointRealContainerLl!!.removeAllViews()
            val lp = LinearLayout.LayoutParams(LWC, LWC)
            lp.setMargins(mPointLeftRightPading, mPointTopBottomPading, mPointLeftRightPading, mPointTopBottomPading)
            var imageView: ImageView
            for (i in 0 until realCount) {
                imageView = ImageView(context)
                imageView.layoutParams = lp
                if (mPointNormal != null && mPointSelected != null) {
                    imageView.setImageDrawable(LoongsCityBannerUtil.getSelector(mPointNormal!!, mPointSelected!!))
                } else {
                    imageView.setImageResource(mPointDrawableResId)
                }
                mPointRealContainerLl!!.addView(imageView)
            }
        }

        if (mNumberIndicatorTv != null) {
            if (isShowIndicatorOnlyOne || !mIsOneImg) {
                mNumberIndicatorTv!!.visibility = View.VISIBLE
            } else {
                mNumberIndicatorTv!!.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * 切换指示器
     */
    private fun switchToPoint(currentPoint: Int) {
        if ((mPointRealContainerLl != null) and (mModels != null) && realCount > 1) {
            for (i in 0 until mPointRealContainerLl!!.childCount) {
                mPointRealContainerLl!!.getChildAt(i).isEnabled = false
            }
            mPointRealContainerLl!!.getChildAt(currentPoint).isEnabled = true
        }

        if (mTipTv != null && mTipData != null) {
            mTipTv!!.text = mTipData!![currentPoint]
        }

        if (mNumberIndicatorTv != null && mViews != null && (isShowIndicatorOnlyOne || !mIsOneImg)) {
            mNumberIndicatorTv!!.text = (currentPoint + 1).toString() + "/" + mViews!!.size
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mIsAutoPlay && !mIsOneImg) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> stopAutoPlay()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> startAutoPlay()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 开始播放
     */
    fun startAutoPlay() {
        if (mIsAutoPlay && !mIsAutoPlaying) {
            mIsAutoPlaying = true
            postDelayed(mAutoSwitchTask, mAutoPlayTime.toLong())
        }
    }

    /**
     * 停止播放
     */
    fun stopAutoPlay() {
        if (mIsAutoPlay && mIsAutoPlaying) {
            mIsAutoPlaying = false
            removeCallbacks(mAutoSwitchTask)
        }
    }

    /**
     * 添加ViewPager滚动监听器
     */
    fun setOnPageChangeListener(onPageChangeListener: ViewPager.OnPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener
    }

    /**
     * 设置图片从最后一张滚动到第一张的动画效果
     */
    fun setSlideScrollMode(slideScrollMode: Int) {
        mSlideScrollMode = slideScrollMode
        if (null != viewPager) {
            viewPager!!.overScrollMode = slideScrollMode
        }
    }

    /**
     * 设置是否允许用户手指滑动
     *
     * @param allowUserScrollable true表示允许跟随用户触摸滑动，false反之
     */
    fun setAllowUserScrollable(allowUserScrollable: Boolean) {
        mIsAllowUserScroll = allowUserScrollable
        if (null != viewPager) {
            viewPager!!.setIsAllowUserScroll(allowUserScrollable)
        }
    }

    /**
     * 设置是否自动轮播
     */
    fun setmAutoPlayAble(mAutoPlayAble: Boolean) {
        this.mIsAutoPlay = mAutoPlayAble
    }

    /**
     * 设置自动轮播时间间隔
     */
    fun setmAutoPalyTime(mAutoPalyTime: Int) {
        this.mAutoPlayTime = mAutoPalyTime
    }

    /**
     * 自定义翻页动画效果
     */
    fun setCustomPageTransformer(transformer: ViewPager.PageTransformer?) {
        if (transformer != null && viewPager != null) {
            viewPager!!.setPageTransformer(true, transformer)
        }
    }

    /**
     * 设置ViewPager切换速度
     */
    fun setPageChangeDuration(duration: Int) {
        if (viewPager != null) {
            viewPager!!.setScrollDuration(duration)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (View.VISIBLE == visibility) {
            startAutoPlay()
        } else if (View.INVISIBLE == visibility) {
            stopAutoPlay()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoPlay()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAutoPlay()
    }

    private class AutoSwitchTask(mLoongsCityBanner: LoongsCityBanner) : Runnable {
        private val mXBanner: WeakReference<LoongsCityBanner>

        init {
            this.mXBanner = WeakReference(mLoongsCityBanner)
        }

        override fun run() {
            val banner = mXBanner.get()
            if (banner != null) {
                banner.switchNextPage()
                banner.postDelayed(banner.mAutoSwitchTask, banner.mAutoPlayTime.toLong())
            }
        }
    }


    private fun switchNextPage() {
        try {
            if (viewPager != null) {
                val currentItem = viewPager!!.currentItem + 1
                viewPager!!.currentItem = currentItem
            }
        } catch (ignored: Exception) {
        }

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(banner: LoongsCityBanner, position: Int)
    }

    interface XBannerAdapter {
        fun loadBanner(banner: LoongsCityBanner, view: View, position: Int)
    }

    companion object {
        private val RMP = RelativeLayout.LayoutParams.MATCH_PARENT
        private val RWC = RelativeLayout.LayoutParams.WRAP_CONTENT
        private val LWC = LinearLayout.LayoutParams.WRAP_CONTENT

        private val VEL_THRESHOLD = 400

        //指示点位置
        val LEFT = 0
        val CENTER = 1
        val RIGHT = 2

        //指示器容器位置
        val TOP = 10
        val BOTTOM = 12
    }
}
