package com.guowei.qrscanner.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.guowei.qrscanner.R;
import com.guowei.qrscanner.adapter.HistoryAdapter;
import com.guowei.qrscanner.adapter.RecyclerItemTouchHelper;
import com.guowei.qrscanner.bean.HistoryBean;
import com.guowei.qrscanner.db.DBOperater;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private DBOperater dbOperater;
    private List<HistoryBean> historyList;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dbOperater = new DBOperater(this,1);
        historyList = dbOperater.queryAllHistory();
        historyRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_history);
        adapter = new HistoryAdapter(historyList,dbOperater);
        historyRecyclerView.setAdapter(adapter);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper.Callback callback=new RecyclerItemTouchHelper(adapter);
        ItemTouchHelper touchHelper=new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(historyRecyclerView);
        historyRecyclerView.addItemDecoration(new DividerItemDecoration());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_clean:
                dbOperater.cleanHistory();
                historyList.clear();
                historyList=dbOperater.queryAllHistory();
                adapter.notifyDataSetChanged();
                break;
        }
        return true;
    }
}
