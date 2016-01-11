package com.awmdev.purecloudkiosk.Decorator;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Reese on 12/25/2015.
 */
public class VerticalSpacingDecorator extends RecyclerView.ItemDecoration
{
    private final int verticalSpacingHeight;

    public VerticalSpacingDecorator(int verticalSpacingHeight)
    {
        this.verticalSpacingHeight = verticalSpacingHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        if(parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() -1 )
            outRect.bottom = verticalSpacingHeight;
    }
}
