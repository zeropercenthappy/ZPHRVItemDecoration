package com.zeropercenthappy.zphrvitemdecoration;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class StaggeredRVAdapter extends RecyclerView.Adapter<StaggeredRVAdapter.ViewHolder> {
    // 瀑布流暂无分割线实现，用交替色区分相邻item的边界
    private static final int[] ITEM_COLORS = {
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
    };

    private List<String> entityList;
    private int orientation = LinearLayoutManager.VERTICAL;

    public List<String> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<String> entityList) {
        this.entityList = entityList;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_staggered, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 用position制造高度差，展示瀑布流效果
        float density = holder.itemView.getResources().getDisplayMetrics().density;
        int base = (int) (100 * density);
        int size = base + (position % 3) * base / 2;
        ImageView iv = (ImageView) holder.getView(R.id.iv);
        ViewGroup.LayoutParams lp = iv.getLayoutParams();
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            lp.width = size;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = size;
        }
        iv.setLayoutParams(lp);
        iv.setImageResource(ITEM_COLORS[position % ITEM_COLORS.length]);
    }

    @Override
    public int getItemCount() {
        return entityList == null ? 0 : entityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public View getView(int viewId) {
            return itemView.findViewById(viewId);
        }
    }
}
