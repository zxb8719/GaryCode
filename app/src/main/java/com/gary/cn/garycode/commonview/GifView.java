package com.gary.cn.garycode.commonview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.gary.cn.garycode.R;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/1/8.
 */
public class GifView extends ImageView implements View.OnClickListener {
    /**
     * 播放GIF动画的关键类
     */
    private Movie mMovie;

    /**
     * 开始播放按钮图片
     */
    private Bitmap mStartBtn;

    /**
     * 记录动画开始的时间
     */
    private long mMovieStart;

    /**
     * GIF图片的宽度
     */
    private int mImageWidth;

    /**
     * GIF图片的高度
     */
    private int mImageHeight;

    /**
     * 图片是否正在播放
     */
    private boolean isPlaying;

    /**
     * 是否允许自动播放
     */
    private boolean isAutoPlay;

    public GifView(Context context) {
        super(context);
    }
    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //设置禁用view级别的硬件加速
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GifView);
        int resourceId = getResourceId(a,context,attrs);


        //获取资源ID的另一种方法
       /* for (int i = 0; i < attrs.getAttributeCount(); i++) {
            Log.i("gary=====",attrs.getAttributeName(i)+"-=-=-=");
            if(attrs.getAttributeName(i).equals("src")) {
                System.out.println(attrs.getAttributeResourceValue(i, 0)+"=========");
                //return attrs.getAttributeResourceValue(i, 0);
                resourceId = attrs.getAttributeResourceValue(i, 0);
            }
        }*/


        if(resourceId != 0)
        {
            //当资源ID不为0时，就会获取该资源的流
            InputStream is = getResources().openRawResource(resourceId);
            //使用Movie类对流进行解码
            mMovie = Movie.decodeStream(is);
            if(mMovie != null)
            {
                //如果返回值不等于null，就说明这是一个GIF图片，下面获取是否自动播放的属性
                isAutoPlay = a.getBoolean(R.styleable.GifView_auto_play, false);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                mImageHeight = bitmap.getHeight();
                mImageWidth = bitmap.getWidth();
                bitmap.recycle();
                if(!isAutoPlay)
                {
                    //当不允许自动播放时，得到开始播放按钮的图片，并注册点击事件
                   mStartBtn = BitmapFactory.decodeResource(getResources(),R.drawable.btn_login);
                   setOnClickListener(this);
                }
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mMovie != null)
        {
            //如果是GIF图片则重设定GifView的大小
            setMeasuredDimension(mImageWidth,mImageHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mMovie == null) {
            // mMovie等于null，说明是张普通的图片，则直接调用父类的onDraw()方法
            super.onDraw(canvas);
        } else {
            // mMovie不等于null，说明是张GIF图片
            if (isAutoPlay) {
                // 如果允许自动播放，就调用playMovie()方法播放GIF动画
                playMovie(canvas);
                invalidate();
            } else {
                // 不允许自动播放时，判断当前图片是否正在播放
                if (isPlaying) {
                    // 正在播放就继续调用playMovie()方法，一直到动画播放结束为止
                    if (playMovie(canvas)) {
                        isPlaying = false;
                    }
                    invalidate();
                } else {
                    // 还没开始播放就只绘制GIF图片的第一帧，并绘制一个开始按钮
                    mMovie.setTime(0);
                    mMovie.draw(canvas, 0, 0);
                    int offsetW = (mImageWidth - mStartBtn.getWidth()) / 2;
                    int offsetH = (mImageHeight - mStartBtn.getHeight()) / 2;
                    canvas.drawBitmap(mStartBtn, offsetW, offsetH, null);
                }
            }
        }
    }

    /**
     *  开始播放GIF动画，播放完成返回true,未完成返回false
     * @param canvas
     * @return
     */
    private boolean playMovie(Canvas canvas)
    {

        long now = SystemClock.uptimeMillis();
        if(mMovieStart == 0)
        {
            mMovieStart = now;
        }
        int duration = mMovie.duration();

        if(duration == 0)
        {
            duration = 1000;
        }
        int relTime = (int)((now - mMovieStart)% duration);
        mMovie.setTime(relTime);
        mMovie.draw(canvas,0,0);
        if((now - mMovieStart) >= duration)
        {
            mMovieStart = 0;
            return  true;
        }
        return  false;
    }

    /**
     * 通过java反射，获取到src指定图片资源所对应的ID
     * @param a
     * @param context
     * @param attrs
     * @return  返回布局文件中指定图片资源所对应的id，没有指定任何图片资源就返回0
     *      此资源文件是指自定义的 GifView
     */
    private int getResourceId(TypedArray a,Context context,AttributeSet attrs)
    {
        try {
            Field field = TypedArray.class.getDeclaredField("mValue");
            field.setAccessible(true);
            TypedValue typedValueObject = (TypedValue)field.get(a);
            return typedValueObject.resourceId;
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally {
            if(a != null)
            {
                a.recycle();
            }
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() ==getId())
        {
            //当用户点击图片时，开始播放GIF动画
            Log.i("gary=====","invalidate");
            isPlaying = true;
            invalidate();
        }
    }
}
