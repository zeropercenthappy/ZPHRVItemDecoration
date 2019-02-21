package com.zeropercenthappy.zphrvitemdecoration;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.zeropercenthappy.decorationlibrary.FullLLRVDecoration;
import com.zeropercenthappy.decorationlibrary.FullVerGLRVDecoration;
import com.zeropercenthappy.decorationlibrary.NormalLLRVDecoration;
import com.zeropercenthappy.decorationlibrary.NormalVerGLRVDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLinear;
    private Button btnLinearFullWrap;
    private Button btnGrid;
    private Button btnGridFullWrap;
    private Button btnAdd;
    private RecyclerView rv;

    private GridRVAdapter gridRVAdapter;
    private LinearRVAdapter linearRVAdapter;
    private List<String> entityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initParam();
    }

    private void initData() {
        entityList = new ArrayList<>();
        entityList.add("");
    }

    private void initView() {
        rv = findViewById(R.id.rv);
        btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
        btnLinear = findViewById(R.id.btn_linear);
        btnLinear.setOnClickListener(this);
        btnLinearFullWrap = findViewById(R.id.btn_linear_full_wrap);
        btnLinearFullWrap.setOnClickListener(this);
        btnGrid = findViewById(R.id.btn_grid);
        btnGrid.setOnClickListener(this);
        btnGridFullWrap = findViewById(R.id.btn_grid_full_wrap);
        btnGridFullWrap.setOnClickListener(this);
    }

    private void initParam() {
        gridRVAdapter = new GridRVAdapter();
        gridRVAdapter.setEntityList(entityList);
        linearRVAdapter = new LinearRVAdapter();
        linearRVAdapter.setEntityList(entityList);
    }

    @Override
    public void onClick(View v) {
        if (v == btnAdd) {
            entityList.add("");
            if (rv.getAdapter() != null) {
                rv.getAdapter().notifyDataSetChanged();
            }
        } else if (v == btnLinear) {
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            if (rv.getItemDecorationCount() > 0) {
                rv.removeItemDecoration(rv.getItemDecorationAt(0));
            }
            rv.addItemDecoration(new NormalLLRVDecoration(this, 10, R.color.colorAccent));
//            rv.addItemDecoration(new NormalLLRVDecoration(this, 10, 5, R.color.colorAccent));
            rv.setAdapter(linearRVAdapter);
        } else if (v == btnLinearFullWrap) {
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            if (rv.getItemDecorationCount() > 0) {
                rv.removeItemDecoration(rv.getItemDecorationAt(0));
            }
            rv.addItemDecoration(new FullLLRVDecoration(this, 10, R.color.colorAccent));
//            rv.addItemDecoration(new FullLLRVDecoration(this, 10, 5, R.color.colorAccent));
            rv.setAdapter(linearRVAdapter);
        } else if (v == btnGrid) {
            rv.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
            if (rv.getItemDecorationCount() > 0) {
                rv.removeItemDecoration(rv.getItemDecorationAt(0));
            }
            rv.addItemDecoration(new NormalVerGLRVDecoration(this, 10, R.color.colorAccent));
            rv.setAdapter(gridRVAdapter);
        } else if (v == btnGridFullWrap) {
            rv.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
            if (rv.getItemDecorationCount() > 0) {
                rv.removeItemDecoration(rv.getItemDecorationAt(0));
            }
            rv.addItemDecoration(new FullVerGLRVDecoration(this, 10, R.color.colorAccent));
            rv.setAdapter(gridRVAdapter);
        }
    }
}
