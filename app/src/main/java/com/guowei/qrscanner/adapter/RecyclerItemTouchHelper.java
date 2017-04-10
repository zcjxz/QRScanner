package com.guowei.qrscanner.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;


public class RecyclerItemTouchHelper extends ItemTouchHelper.Callback{

    private final ItemTouchHelperAdapterCallback mCallback;

    /**
     * 对外的接口。当拖拽变化，滑动变化时调用该接口。
     * adapter 要实现这个接口，根据变化，刷新显示的数据
     */
    public interface ItemTouchHelperAdapterCallback{
        /**
         * 拖拽变化接口
         */
        void onItemMove(int fromPosition,int toPosition);

        /**
         * 删除接口
         * @param position
         */
        void onItemDismiss(int position);
    }

    public RecyclerItemTouchHelper(ItemTouchHelperAdapterCallback callback) {
        mCallback = callback;
    }

    /**
     *  指定支持的拖放和滑动。
     *  使用helperItemTouchHelper.makeMovementFlags(int, int)来构造返回的flag
     * @return  返回支持的拖放和滑动
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mCallback.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mCallback.onItemDismiss(viewHolder.getAdapterPosition());
    }

    /**
     * 长按是否开始进入拖拽模式
     * @return 长按是否进入拖拽模式
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 是否支持 拖拽 （也就是说默认就是拖拽，滑动不了）
     * 也可以主动调用ItemTouchHelper.startSwipe(RecyclerView.ViewHolder) 来开始滑动操作
     * @return  是否支持拖拽
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return super.isItemViewSwipeEnabled();
    }
}
