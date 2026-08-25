package com.zeropercenthappy.zphrvitemdecoration;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.zeropercenthappy.divider.GridLayoutManagerDivider;
import com.zeropercenthappy.divider.LinearLayoutManagerDivider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String KEY_TYPE = "key_type";
    private static final String KEY_ORIENTATION = "key_orientation";
    private static final String KEY_FULL_WRAP = "key_full_wrap";
    private static final String KEY_ITEM_COUNT = "key_item_count";

    private static final int TYPE_LINEAR = 0;
    private static final int TYPE_GRID = 1;
    private static final int TYPE_STAGGERED = 2;

    private static final int SPAN_COUNT = 3;
    private static final int DIVIDER_COLOR = Color.parseColor("#e69310");
    private static final int DIVIDER_SIZE = 10;

    private Button btnLinear;
    private Button btnGrid;
    private Button btnStaggered;
    private Button btnVertical;
    private Button btnHorizontal;
    private Button btnFullWrap;
    private Button btnMinus;
    private Button btnAdd;
    private TextView tvStatus;
    private RecyclerView rv;

    private GridRVAdapter gridRVAdapter;
    private LinearRVAdapter linearRVAdapter;
    private StaggeredRVAdapter staggeredRVAdapter;
    private List<String> entityList;

    private int currentType = TYPE_LINEAR;
    private int currentOrientation = LinearLayoutManager.VERTICAL;
    private boolean fullWrap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        if (savedInstanceState != null) {
            // 横竖屏旋转后恢复旋转前的配置
            currentType = savedInstanceState.getInt(KEY_TYPE, TYPE_LINEAR);
            currentOrientation = savedInstanceState.getInt(KEY_ORIENTATION, LinearLayoutManager.VERTICAL);
            fullWrap = savedInstanceState.getBoolean(KEY_FULL_WRAP, false);
            restoreItemCount(savedInstanceState.getInt(KEY_ITEM_COUNT, entityList.size()));
        }
        applyLayout();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TYPE, currentType);
        outState.putInt(KEY_ORIENTATION, currentOrientation);
        outState.putBoolean(KEY_FULL_WRAP, fullWrap);
        outState.putInt(KEY_ITEM_COUNT, entityList.size());
    }

    private void initData() {
        entityList = new ArrayList<>();
        entityList.add("");
        gridRVAdapter = new GridRVAdapter();
        gridRVAdapter.setEntityList(entityList);
        linearRVAdapter = new LinearRVAdapter();
        linearRVAdapter.setEntityList(entityList);
        staggeredRVAdapter = new StaggeredRVAdapter();
        staggeredRVAdapter.setEntityList(entityList);
    }

    private void initView() {
        rv = findViewById(R.id.rv);
        tvStatus = findViewById(R.id.tv_status);
        btnLinear = findViewById(R.id.btn_linear);
        btnLinear.setOnClickListener(this);
        btnGrid = findViewById(R.id.btn_grid);
        btnGrid.setOnClickListener(this);
        btnStaggered = findViewById(R.id.btn_staggered);
        btnStaggered.setOnClickListener(this);
        btnVertical = findViewById(R.id.btn_vertical);
        btnVertical.setOnClickListener(this);
        btnHorizontal = findViewById(R.id.btn_horizontal);
        btnHorizontal.setOnClickListener(this);
        btnFullWrap = findViewById(R.id.btn_full_wrap);
        btnFullWrap.setOnClickListener(this);
        btnMinus = findViewById(R.id.btn_minus);
        btnMinus.setOnClickListener(this);
        btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
    }

    private void restoreItemCount(int targetCount) {
        while (entityList.size() < targetCount) {
            entityList.add("");
        }
        while (entityList.size() > targetCount) {
            entityList.remove(entityList.size() - 1);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnMinus) {
            if (!entityList.isEmpty()) {
                entityList.remove(entityList.size() - 1);
                notifyDataSetChanged();
            }
        } else if (v == btnAdd) {
            entityList.add("");
            notifyDataSetChanged();
        } else if (v == btnLinear) {
            currentType = TYPE_LINEAR;
            applyLayout();
        } else if (v == btnGrid) {
            currentType = TYPE_GRID;
            applyLayout();
        } else if (v == btnStaggered) {
            currentType = TYPE_STAGGERED;
            applyLayout();
        } else if (v == btnVertical) {
            currentOrientation = LinearLayoutManager.VERTICAL;
            applyLayout();
        } else if (v == btnHorizontal) {
            currentOrientation = LinearLayoutManager.HORIZONTAL;
            applyLayout();
        } else if (v == btnFullWrap) {
            fullWrap = !fullWrap;
            applyLayout();
        }
    }

    private void applyLayout() {
        while (rv.getItemDecorationCount() > 0) {
            rv.removeItemDecoration(rv.getItemDecorationAt(0));
        }
        switch (currentType) {
            case TYPE_GRID:
                gridRVAdapter.setOrientation(currentOrientation);
                rv.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT, currentOrientation, false));
                rv.setAdapter(gridRVAdapter);
                if (currentOrientation == LinearLayoutManager.VERTICAL) {
                    rv.addItemDecoration(new GridLayoutManagerDivider(DIVIDER_COLOR, DIVIDER_SIZE, fullWrap));
                }
                // library暂不支持横向GridLayoutManager的分割线，留空
                break;
            case TYPE_STAGGERED:
                staggeredRVAdapter.setOrientation(currentOrientation);
                rv.setLayoutManager(new StaggeredGridLayoutManager(SPAN_COUNT, currentOrientation));
                rv.setAdapter(staggeredRVAdapter);
                // library暂不支持StaggeredGridLayoutManager的分割线，留空
                break;
            default:
                linearRVAdapter.setOrientation(currentOrientation);
                rv.setLayoutManager(new LinearLayoutManager(this, currentOrientation, false));
                rv.setAdapter(linearRVAdapter);
                rv.addItemDecoration(new LinearLayoutManagerDivider(DIVIDER_COLOR, DIVIDER_SIZE, fullWrap));
                break;
        }
        refreshStatus();
    }

    private void notifyDataSetChanged() {
        if (rv.getAdapter() != null) {
            rv.getAdapter().notifyDataSetChanged();
        }
        refreshStatus();
    }

    private void refreshStatus() {
        String type;
        switch (currentType) {
            case TYPE_GRID:
                type = "Grid";
                break;
            case TYPE_STAGGERED:
                type = "Staggered";
                break;
            default:
                type = "Linear";
                break;
        }
        String orientation = currentOrientation == LinearLayoutManager.VERTICAL ? "纵向" : "横向";
        String divider;
        if (currentType == TYPE_LINEAR
                || (currentType == TYPE_GRID && currentOrientation == LinearLayoutManager.VERTICAL)) {
            divider = fullWrap ? "FullWrap" : "NotFullWrap";
        } else {
            divider = "暂无分割线实现";
        }
        btnFullWrap.setText(fullWrap ? "FullWrap: on" : "FullWrap: off");
        tvStatus.setText(String.format("%s / %s / %s / item: %d",
                type, orientation, divider, entityList.size()));
    }
}
