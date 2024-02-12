package kg.dev.videoeditor.widgets;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public class VideoThumbSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;
    private int mThumbnailsCount;

    public VideoThumbSpacingItemDecoration(int space, int thumbnailsCount) {
        mSpace = space;
        mThumbnailsCount = thumbnailsCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.left = mSpace;
            outRect.right = 0;
        } else if (mThumbnailsCount > 10 && position == mThumbnailsCount - 1) {
            outRect.left = 0;
            outRect.right = mSpace;
        } else {
            outRect.left = 0;
            outRect.right = 0;
        }
    }
}
