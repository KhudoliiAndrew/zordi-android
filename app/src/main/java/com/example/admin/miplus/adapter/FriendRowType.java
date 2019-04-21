package com.example.admin.miplus.adapter;

import android.support.v7.widget.RecyclerView;

public class FriendRowType implements RowType {

    public FriendRowType() {
    }

    @Override
    public int getItemViewType() {
        return RowType.FRIEND_ROW_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
    }
}