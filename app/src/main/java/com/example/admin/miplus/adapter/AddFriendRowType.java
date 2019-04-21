package com.example.admin.miplus.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class AddFriendRowType implements RowType {

    public AddFriendRowType() {
    }

    public View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
    }

    @Override
    public int getItemViewType() {
        return RowType.ADD_ROW_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
    }
}
