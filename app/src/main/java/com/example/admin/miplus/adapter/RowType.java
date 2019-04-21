package com.example.admin.miplus.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface RowType {
    int FRIEND_ROW_TYPE = 0;
    int ADD_ROW_TYPE = 1;

    int getItemViewType();

    void onBindViewHolder(RecyclerView.ViewHolder viewHolder);
}

