package com.zeropercenthappy.zphrvitemdecoration;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * @author ybq
 * @date 2017/12/26
 */

public class LinearRVAdapter extends RecyclerView.Adapter<LinearRVAdapter.ViewHolder> {
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
    public int getItemViewType(int position) {
        return orientation;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = viewType == LinearLayoutManager.HORIZONTAL
                ? R.layout.item_rv_linear_horizontal
                : R.layout.item_rv_linear;
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView iv = (ImageView) holder.getView(R.id.iv);
        iv.setImageResource(R.color.colorPrimary);
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
