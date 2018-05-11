/*
 * Copyright (C) 2018 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.application.mylevelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by wanglu on 2018/5/10.
 */
public class MyLevelView extends View {

    private Context thisContext;
    private float mRadius=5;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;
    private int mWidth;
    private int mHeight;
    private int noneCircleRadius;//节点圆圈大小
    private int progressHeight;//进度条的高度
    private int measuredWidth;
    private int measuredHeight;

    private float buttomTextSizeDefault;//底部文字默认大小
    private float buttomTextSizeSelected;//底部文字选中大小
    private int buttomTextColorSelected;
    private int buttomTextColorDefault;
    private float bottomTextMarginTop;

    private float topTextSizeDefault;//底部文字默认大小
    private float topTextSizeSelected;//底部文字选中大小
    private int topTextColorSelected;
    private int topTextColorDefault;
    private float topTextMarginTop;
    private List<LevelTextBean> mDataList;
    private int viewBackground;//进度条底色
    private int progressColor;
    private int noneColor;

    public MyLevelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyLevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        thisContext = context;
        initPaint();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyLevelView, 0, 0);
        try {
            mRadius=a.getDimension(R.styleable.MyLevelView_mRadius,dp2px(thisContext,4));
            buttomTextSizeDefault=a.getDimension(R.styleable.MyLevelView_buttomTextSizeDefault,10);
            buttomTextSizeSelected=a.getDimension(R.styleable.MyLevelView_buttomTextSizeSelected,12);
            buttomTextColorSelected=a.getColor(R.styleable.MyLevelView_buttomTextColorSelected, Color.parseColor("#364252"));
            buttomTextColorDefault=a.getColor(R.styleable.MyLevelView_buttomTextColorDefault, Color.parseColor("#c9cbcf"));
            bottomTextMarginTop=a.getDimension(R.styleable.MyLevelView_bottomTextMarginTop,16);


            topTextSizeDefault=a.getDimension(R.styleable.MyLevelView_topTextSizeDefault,10);
            topTextSizeSelected=a.getDimension(R.styleable.MyLevelView_topTextSizeSelected,12);
            topTextColorSelected=a.getColor(R.styleable.MyLevelView_topTextColorSelected, Color.parseColor("#364252"));
            topTextColorDefault=a.getColor(R.styleable.MyLevelView_topTextColorDefault, Color.parseColor("#c9cbcf"));
            topTextMarginTop=a.getDimension(R.styleable.MyLevelView_topTextMarginTop,16);

            viewBackground=a.getColor(R.styleable.MyLevelView_viewBackground, Color.parseColor("#e2ebee"));
            progressColor=a.getColor(R.styleable.MyLevelView_progressColor, Color.parseColor("#67c2f9"));

            noneColor=a.getColor(R.styleable.MyLevelView_noneColor, Color.parseColor("#b6d0d7"));
            noneCircleRadius=a.getDimensionPixelSize(R.styleable.MyLevelView_noneCircleRadius,dp2px(thisContext,2));
            progressHeight=a.getDimensionPixelSize(R.styleable.MyLevelView_progressHeight,8);
        }catch (Exception e){
            mRadius=dp2px(thisContext,4);//背景的两边圆角角度
            buttomTextSizeDefault=10;//底部文字默认大小
            buttomTextSizeSelected=12;//底部文字选中大小
            buttomTextColorSelected = Color.parseColor("#364252");//底部文字选中颜色
            buttomTextColorDefault = Color.parseColor("#c9cbcf");//底部文字默认颜色
            bottomTextMarginTop = 16;//底部文字顶部分割

            topTextSizeDefault=10;//顶部文字默认大小
            topTextSizeSelected=12;//顶部文字选中大小
            topTextColorSelected = Color.parseColor("#364252");//顶部文字选中颜色
            topTextColorDefault = Color.parseColor("#c9cbcf");//顶部文字默认颜色
            topTextMarginTop = 16;//底部文字顶部分割

            viewBackground = Color.parseColor("#e2ebee");//view背景颜色
            progressColor = Color.parseColor("#67c2f9");

            noneColor = Color.parseColor("#b6d0d7");
            noneCircleRadius =dp2px(thisContext,2) ;
            progressHeight=8;
        } finally{
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
        mWidth = measuredWidth -paddingLeft-paddingRight;//实际绘制区域宽度
        mHeight = measuredHeight -paddingTop-paddingBottom;//实际绘制区域高度
        calculationProgress();
    }

    private float totalProgress=100;//总进度，默认100
    private float currentProgress=0;//当前进度
    private float proportionProgress=0;//比例值
    private float levelNoneProgress=0;//当前节点


    /**
     * 计算进度相关
     */
    private void calculationProgress() {
        if(getTotalProgress()==0){
            return;
        }
        float i = mWidth / getTotalProgress();
        setProportionProgress(i);
    }

    private void initPaint(){

    }

    private boolean isDrawProgress=true;//是否是绘制进度的
    private boolean isCanvasInstructions=true;//是否是绘制游标的
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景颜色
        canvasBackground(canvas);
        //绘制节点
        canvasNone(canvas);
        //绘制进度
        if(isDrawProgress){
            canvasCurrentProgress(canvas);
        }
        if(isCanvasInstructions){
//            isCanvasInstructions=false;
            //绘制指示器
            canvasInstructions(canvas);
        }
        canvasTopText(canvas);
        canvasBottmText(canvas);
    }

    public void setData(List<LevelTextBean> list){
        if(list==null || list.size()<=0){
            return;
        }
        mDataList=list;
        invalidate();
    }

    /**
     * 顶部文字
     * @param canvas
     */
    private void canvasTopText(Canvas canvas) {
        List<LevelTextBean> topTextLists = mDataList;
        if(topTextLists==null || topTextLists.size()<=0){
            return;
        }
        for(int i=0;i<topTextLists.size();i++){
            LevelTextBean levelTextBean = topTextLists.get(i);
            if(levelTextBean.stageValue==-1){
                continue;
            }
            int currentProgress = getCurrentProgress();
            boolean isCurrent=false;
            //是否是当前进度 小于肯定不是
            //大于等于当前，小于下一级别的
            if(currentProgress>=levelTextBean.progress){
                if(i+1==topTextLists.size()){
                    //最后一个
                    isCurrent=true;
                }else if(currentProgress<topTextLists.get(i+1).progress){
                    isCurrent=true;
                }
            }
            Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextAlign(Paint.Align.LEFT);
            if(isCurrent){
                mPaint.setTextSize(topTextSizeSelected);
                mPaint.setColor(topTextColorSelected);
            }else{
                mPaint.setTextSize(topTextSizeDefault);
                mPaint.setColor(topTextColorDefault);
            }
            String stageValue="0";
            try {
                stageValue = String.valueOf(levelTextBean.stageValue);
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
            float v = mPaint.measureText(stageValue);
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            float backgroundBottom=measuredHeight/2+progressHeight/2;
            float baseline = backgroundBottom- Math.abs(fontMetrics.bottom )- Math.abs(fontMetrics.top)-topTextMarginTop+ Math.abs(fontMetrics.ascent);
            //两个进度段中间的位置
            if(i+1==topTextLists.size()){
                //最后一个item不参与这个
                return;
            }
            LevelTextBean levelTextBean1 = topTextLists.get(i + 1);
            float middleProgress = (levelTextBean.progress + levelTextBean1.progress)/2;//两个阶段中间值
            float x=middleProgress*getProportionProgress()-v/2+paddingLeft;
            canvas.drawText(stageValue,x, baseline, mPaint);
        }

    }

    public static class LevelTextBean{
        public LevelTextBean(String name, int progress) {
            this.name = name;
            this.progress = progress;
        }
        public LevelTextBean(String name, int progress, int stageValue) {
            this.name = name;
            this.progress = progress;
            this.stageValue = stageValue;
        }
        public String name="";//名称
        public int progress=0;//进度
        public int stageValue=-1;//会员阶段的值，指示这一阶段与下一阶段的值  -1是无效
    }
    /**
     * 底部指示文字
     * @param canvas
     */
    private void canvasBottmText(Canvas canvas) {
        List<LevelTextBean> bottomTextLists = mDataList;
        if(bottomTextLists==null || bottomTextLists.size()<=0){
            return;
        }
        for(int i=0;i<bottomTextLists.size();i++){
            LevelTextBean levelTextBean = bottomTextLists.get(i);
            int currentProgress = getCurrentProgress();
            boolean isCurrent=false;
            //是否是当前进度 小于肯定不是
            //大于等于当前，小于下一级别的
            if(currentProgress>=levelTextBean.progress){
                if(i+1==bottomTextLists.size()){
                    //最后一个
                    isCurrent=true;
                }else if(currentProgress<bottomTextLists.get(i+1).progress){
                    isCurrent=true;
                }
            }
            Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextAlign(Paint.Align.LEFT);
            if(isCurrent){
                mPaint.setTextSize(buttomTextSizeSelected);
                mPaint.setColor(buttomTextColorSelected);
            }else{
                mPaint.setTextSize(buttomTextSizeDefault);
                mPaint.setColor(buttomTextColorDefault);
            }
            float v = mPaint.measureText(levelTextBean.name);
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            float backgroundBottom=measuredHeight/2+progressHeight/2;
            float baseline = backgroundBottom+ Math.abs(fontMetrics.bottom )+ Math.abs(fontMetrics.top)+bottomTextMarginTop- Math.abs(fontMetrics.ascent);
//            float baseline = backgroundBottom+Math.abs(fontMetrics.descent )+Math.abs(fontMetrics.ascent);
            float x=levelTextBean.progress*getProportionProgress()-v/2+paddingLeft;
            if(levelTextBean.progress<=0){//第一个与view左边对齐
                x=x+v/2-noneCircleRadius;
            }
            if(levelTextBean.progress>=getTotalProgress()){//最后一个与view右边对齐
                x=x-v/2+noneCircleRadius;
            }
            canvas.drawText(levelTextBean.name,x, baseline, mPaint);
        }
    }


    /**
     * 绘制背景颜色
     * @param canvas
     */
    private void canvasBackground(Canvas canvas) {
        Paint backgroundPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(viewBackground);
        float backgroundTop=measuredHeight/2-progressHeight/2;
        float backgroundBottom=measuredHeight/2+progressHeight/2;
        RectF backgroundRec = new RectF(paddingLeft,backgroundTop, getMeasuredWidth()-paddingRight, backgroundBottom);
        canvas.drawRoundRect(backgroundRec, mRadius, mRadius, backgroundPaint);

    }

    /**
     * 绘制进度
     * @param canvas
     */
    private void canvasCurrentProgress(Canvas canvas) {
        isDrawProgress=false;
        Paint progressPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(progressColor);
        float backgroundTop=measuredHeight/2-progressHeight/2;
        float backgroundBottom=measuredHeight/2+progressHeight/2;
        float rightProgress=getCurrentProgress()*getProportionProgress()+paddingLeft;
        RectF progresRec = new RectF(paddingLeft-noneCircleRadius,backgroundTop,rightProgress,backgroundBottom);
        canvas.drawRoundRect(progresRec, mRadius, mRadius, progressPaint);
    }

    /**
     * 绘制指示器
     * @param canvas
     */
    private void canvasInstructions(Canvas canvas) {
        Paint p = new Paint(Paint.FILTER_BITMAP_FLAG);
        p.setAntiAlias(true);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_member_level_current);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float left=getLevelNoneProgress()*getProportionProgress()-(width/2)+paddingLeft;
        float top=measuredHeight/2-height/2;
        canvas.drawBitmap(bitmap,left,top,p);
    }

    /**
     * 绘制节点
     * @param canvas
     */
    private void canvasNone(Canvas canvas) {
        List<LevelTextBean> noneLists = mDataList;
        if(noneLists ==null || noneLists.size()<=0){
            return;
        }
        for(int i = 0; i< noneLists.size(); i++){
            LevelTextBean levelTextBean = noneLists.get(i);
            int integer=levelTextBean.progress;
            if(integer>getTotalProgress()){
                continue;
            }
            float horizontalCenter=integer*getProportionProgress()+paddingLeft;
            int verticalCenter    =  mHeight / 2;
            Paint paint = new Paint();
            paint.setAntiAlias(true);//抗锯齿
            paint.setColor(noneColor);
            canvas.drawCircle( horizontalCenter,verticalCenter , noneCircleRadius, paint);// 绘制圆，参数一是中心点的x轴，参数二是中心点的y轴，参数三是半径，参数四是paint对象；
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    public float getTotalProgress() {
        return totalProgress;
    }

    public void setTotalProgress(float totalProgress) {
        this.totalProgress = totalProgress;
        calculationProgress();
    }

    public int getCurrentProgress() {
        return (int) currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        if(currentProgress>totalProgress){
            return;
        }
        this.currentProgress = currentProgress;
        isDrawProgress=true;
        isCanvasInstructions=true;
        invalidate();
    }

    public float getProportionProgress() {
        return proportionProgress;
    }

    private void setProportionProgress(float proportionProgress) {
        this.proportionProgress = proportionProgress;
    }

    public boolean isDrawProgress() {
        return isDrawProgress;
    }

    public void setDrawProgress(boolean drawProgress) {
        isDrawProgress = drawProgress;
    }

    public float getLevelNoneProgress() {
        List<LevelTextBean> noneLists = mDataList;
        if(noneLists==null || noneLists.size()<=0){
            return 0;
        }
        int currentProgress = getCurrentProgress();
        for(int i=0;i<noneLists.size();i++){
            int level=noneLists.get(i).progress;
            if(i==noneLists.size()-1 && currentProgress>=level){//最后一个
                return levelNoneProgress=level;
            }
            if(currentProgress>=level && currentProgress<noneLists.get(i+1).progress){
                return levelNoneProgress=level;
            }
        }
        return 0;
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }


    public static class MyLevelTextBean{
        List<String> topText;
        List<String> bottomText;
    }
}
