package com.example.myapplication;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int verticalSpaceHeight;

    public SpacingItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = verticalSpaceHeight;

        // Optional: Add top spacing for the first item
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = verticalSpaceHeight;
        }
    }
}