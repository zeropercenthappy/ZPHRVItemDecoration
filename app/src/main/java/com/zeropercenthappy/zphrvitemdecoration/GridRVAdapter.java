package com.zeropercenthappy.zphrvitemdecoration;

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

public class GridRVAdapter extends RecyclerView.Adapter<GridRVAdapter.ViewHolder> {
    private List<String> entityList;

    public List<String> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<String> entityList) {
        this.entityList = entityList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_grid, parent, false);
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
