package com.awmdev.purecloudkiosk.Decorator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Reese on 12/25/2015.
 */
public class RecyclerListSeparator extends RecyclerView.ItemDecoration
{
    private Drawable divider;

    public RecyclerListSeparator(Context context)
    {
        TypedArray styledAttributes = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        divider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
    }

    public RecyclerListSeparator(Context context, int resID)
    {
        divider = ContextCompat.getDrawable(context,resID);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        //grab the left and right bounds of the parent
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        //loop through all of the views in the recycler view
        for(int i = 0; i < parent.getChildCount(); ++i)
        {
            //grab the view from parent
            View view = parent.getChildAt(i);
            //grab the params from the layout
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            //grab the top and bottom of the view
            int top = view.getBottom() + layoutParams.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();
            //set the bounds of the divider
            divider.setBounds(left,top,right,bottom);
            //draw the divider on the canvas
            divider.draw(c);
        }
    }
}
