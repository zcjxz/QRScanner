package com.guowei.qrscanner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guowei.qrscanner.R;
import com.guowei.qrscanner.bean.HistoryBean;
import com.guowei.qrscanner.db.DBOperater;
import com.guowei.qrscanner.utils.CopyUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> implements RecyclerItemTouchHelper.ItemTouchHelperAdapterCallback{

    private Context mContext;
    private final List<HistoryBean> data;
    private DBOperater dbOperater;

    public HistoryAdapter(List<HistoryBean> data,DBOperater dbOperater) {
        if (data==null){
            this.data=new ArrayList<>();
        }else{
            this.data=data;
        }
        this.dbOperater=dbOperater;
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.item_history, parent, false);
        return new HistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        final HistoryBean historyBean = data.get(position);
        holder.historyText.setText(historyBean.getHistory());
        holder.timeText.setText(historyBean.getTime());
        holder.btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CopyUtil.copy(historyBean.getHistory());
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
    //位置发生变化
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //调换顺序
        Collections.swap(data,fromPosition,toPosition);
        //刷新
        notifyItemMoved(fromPosition,toPosition);
    }
    //删除了某条数据
    @Override
    public void onItemDismiss(int position) {
        dbOperater.deleteHistory(data.get(position).getHistory());
        data.remove(position);
        notifyItemRemoved(position);
    }

    static class HistoryHolder extends RecyclerView.ViewHolder{
        TextView historyText;
        TextView timeText;
        ImageView btn_copy;

        public HistoryHolder(View itemView) {
            super(itemView);
            historyText=(TextView) itemView.findViewById(R.id.text_history);
            timeText= (TextView) itemView.findViewById(R.id.text_time);
            btn_copy = (ImageView) itemView.findViewById(R.id.btn_copy);
            btn_copy.setClickable(true);
        }
    }
}
