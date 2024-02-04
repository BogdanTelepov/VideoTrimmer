package kg.dev.videoeditor.widgets;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class EmptySpaceDecorator extends RecyclerView.ItemDecoration {

    private int space;

    public EmptySpaceDecorator(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        outRect.left = 0;
        outRect.right = 0;
    }
}
