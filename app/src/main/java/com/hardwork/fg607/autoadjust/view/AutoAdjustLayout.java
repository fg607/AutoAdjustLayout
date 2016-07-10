package com.hardwork.fg607.autoadjust.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by fg607 on 16-7-10.
 */
public class AutoAdjustLayout extends ViewGroup {

    private final static String TAG = "AutoAdjustLayout";

    private int mChildSize;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    //布局过程中当前行的高度
    private int mCurrentLineHeight=0;

    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;
    private int mPaddingTop = 0;
    private int mPaddingBottom = 0;

    /**
     * Line类用于保存每一行容纳的子view数据和其布局信息。
     * layoutLineChildren方法用于对每一行中的子view进行布局。
     */
    public class Line{

        ArrayList<View> lineChildren;

        //每行view总共所占的实际宽度
        int lineWidth;
        //每行占据的高度
        int lineHeight;
        //行顶部位置
        int lineTop;

        public Line(ArrayList<View> lineChildren,int lineWidth,int lineHeight,int lineTop){

            this.lineChildren = lineChildren;
            this.lineWidth = lineWidth;
            this.lineHeight = lineHeight;
            this.lineTop = lineTop;
        }

        /**
         * 根据测量宽度mMeasuredWidth对每一行的view进行布局。
         * 如果AutoAdjustLayout的宽度大于每一行所有view实际所占的宽度，
         * 会将剩余的宽度平均分配到除第一个view以外的其它view的左边距中去，
         * 从而使每一行所有view水平居中占满AutoAdjustLayout。
         */
        public void layoutLineChildren(){

            int left = mPaddingLeft;

            //垂直居中偏移参数(每行的高度lineHeight等于子view中所占高度最大的值)
            int topOffset;

            //水平居中偏移参数
            int leftOffset;

            //当每一行的子view数量大于１个时，设置水平居中偏移参数。
            if(lineChildren.size()>1){

                leftOffset = (mMeasuredWidth-mPaddingLeft-mPaddingRight-lineWidth)/(lineChildren.size()-1);

            }else {

                leftOffset = 0;
            }


            for(View child:lineChildren){

                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();

                //每一个子view按照每行的高度进行居中
                topOffset = (lineHeight-height-params.topMargin-params.bottomMargin)/2;

                child.layout(left+params.leftMargin,lineTop+params.topMargin+topOffset,
                        left+params.leftMargin+width,lineTop+params.topMargin+topOffset+height);

                left = left+params.leftMargin+width+params.rightMargin+leftOffset;

                if(mCurrentLineHeight<lineTop+params.topMargin+topOffset+height+params.bottomMargin){

                    mCurrentLineHeight = lineTop+params.topMargin+topOffset+height+params.bottomMargin;
                }

            }

        }
    }

    public AutoAdjustLayout(Context context) {
        super(context);
    }

    public AutoAdjustLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoAdjustLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int parentHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int parentWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec);


        /**
         *  当使用ScrollView包裹AutoAdjustLayout时,
         *  由于在API版本小于23的情况下传入的高度为0,会导致AutoAdjustLayout不可见，
         *  所以需要重新设置heightMeasureSpec。
         */
        if(parentHeightSpecSize==0){

            parentHeightSpecSize = calculateHeight(parentWidthSpecSize);

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeightSpecSize,MeasureSpec.AT_MOST);


        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        mChildSize = getChildCount();

        measureChildren(widthMeasureSpec,heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.d(TAG,"heightSpecMode="+heightSpecMode);

        //解决width设置为具体数据并且大于屏幕宽度导致不能正常显示的问题。
        if(widthSpecSize>getResources().getDisplayMetrics().widthPixels){

            widthSpecSize = getResources().getDisplayMetrics().widthPixels;
        }

        //设置默认的测量宽高
        mMeasuredWidth = widthSpecSize;
        mMeasuredHeight = heightSpecSize;


        //每行view中占据宽度最大值
        int maxChildWidth=0;

        int wrapContentWidth=0;
        int wrapContentHeight=0;

        for(int i =0;i<mChildSize;i++){

            View child = getChildAt(i);

            int childWidth = child.getMeasuredWidth();

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();

            maxChildWidth = Math.max(childWidth+params.leftMargin+params.rightMargin,maxChildWidth);

        }

        //宽度设置为wrap_content时，测量宽度为子view中所占宽度最大(子view的左右边距加上其宽度)加上AutoRankLayout的左右padding
        wrapContentWidth=getPaddingLeft()+maxChildWidth+getPaddingRight();

        if(mChildSize==0){

            mMeasuredWidth=mPaddingLeft+mPaddingRight;
            mMeasuredHeight=mPaddingTop+mPaddingBottom;

            setMeasuredDimension(mMeasuredWidth,mMeasuredHeight);

        }else if(widthSpecMode==MeasureSpec.AT_MOST){

            mMeasuredWidth=wrapContentWidth;

            wrapContentHeight = calculateHeight(wrapContentWidth);

            mMeasuredHeight=wrapContentHeight;


            setMeasuredDimension(wrapContentWidth,wrapContentHeight);

        }else if(heightSpecMode==MeasureSpec.AT_MOST){

            mMeasuredWidth = widthSpecSize;

            wrapContentHeight = calculateHeight(widthSpecSize);

            mMeasuredHeight = wrapContentHeight;


            setMeasuredDimension(widthSpecSize,wrapContentHeight);

        } else if (heightSpecMode == MeasureSpec.UNSPECIFIED) {

            /**
             *  当使用ScrollView包裹AutoAdjustLayout时,
             *  由于在API版本小于23的情况下传入的高度为0,会导致AutoAdjustLayout不可见，
             *  所以需要处理MeasureSpec.UNSPECIFIED的情况，重新计算高度即可。
             */
            mMeasuredWidth = widthSpecSize;

            wrapContentHeight = calculateHeight(widthSpecSize);

            mMeasuredHeight = wrapContentHeight;

            setMeasuredDimension(widthSpecSize, wrapContentHeight);

        }



    }

    /**
     * calculateHeight 根据当前测量宽度计算出AutoAdjustLayout所占的实际高度
     * @param measuredWidth　当前测量宽度
     * @return
     */
    private int calculateHeight(int measuredWidth) {

        int wrapContentHeight = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        for(int i=0;i<mChildSize;i++){

            View child = getChildAt(i);

            int childWidth = child.getMeasuredWidth();

            int childHeight = child.getMeasuredHeight();

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();

            //一行的高度等于子view中高度和上下边距之和的最大值

            if(lineWidth+childWidth+params.leftMargin+params.rightMargin<measuredWidth-mPaddingLeft-mPaddingRight){

                lineWidth = lineWidth + childWidth+params.leftMargin+params.rightMargin;

                if(lineHeight<childHeight+params.topMargin+params.bottomMargin){

                    lineHeight = childHeight+params.topMargin+params.bottomMargin;
                }


            }else {

                wrapContentHeight+=lineHeight;

                lineWidth = childWidth+params.leftMargin+params.rightMargin;;
                lineHeight = childHeight+params.topMargin+params.bottomMargin;

            }


        }

        //AutoAdjustLayout实际占据的总高度等于所有行的高度加上AutoAdjustLayout的上下padding
        wrapContentHeight=mPaddingTop+wrapContentHeight+mPaddingBottom;



        return wrapContentHeight;

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mCurrentLineHeight = mPaddingTop;

        Line line;
        ArrayList<View> lineChildren =null;

        int lineWidth = 0;
        int lineHeight = 0;


        for(int i=0;i<mChildSize;i++){

            View child = getChildAt(i);

            int childWidth = child.getMeasuredWidth();

            int childHeight = child.getMeasuredHeight();

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();

            //一行的高度设置为子view的高度和上下边距最大值计算

            if(lineWidth+childWidth+params.leftMargin+params.rightMargin<mMeasuredWidth-mPaddingLeft-mPaddingRight){

                if(lineChildren == null){

                    lineChildren = new ArrayList<>();
                }

                lineWidth = lineWidth + childWidth+params.leftMargin+params.rightMargin;

                if(lineHeight<childHeight+params.topMargin+params.bottomMargin){

                    lineHeight = childHeight+params.topMargin+params.bottomMargin;
                }

                lineChildren.add(child);

            }else {

                //创建新行，并对行内所有子view进行布局
                if(lineChildren!=null){

                    line = new Line(lineChildren,lineWidth,lineHeight,mCurrentLineHeight);

                    line.layoutLineChildren();

                    lineChildren = null;
                }

                lineWidth =  childWidth+params.leftMargin+params.rightMargin;
                lineHeight = childHeight+params.topMargin+params.bottomMargin;

                if(lineChildren == null){

                    lineChildren = new ArrayList<>();
                }

                lineChildren.add(child);


            }


        }


    }
}
