package com.todo.checked.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lenovo on 4/24/2017.
 */

public class SpaceDecorationStaggard extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public SpaceDecorationStaggard(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = verticalSpaceHeight;
        outRect.top = verticalSpaceHeight;
        outRect.right = verticalSpaceHeight;
        outRect.left = verticalSpaceHeight;

    }


}
