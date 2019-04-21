package com.example.admin.miplus.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.admin.miplus.R;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<RowType> dataSet;
    private ItemClickListener clickListener;

    public MyRecyclerViewAdapter(List<RowType> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getItemViewType();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {

        public FriendViewHolder(View view) {
            super(view);
        }

    }

    public class AddFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public AddFriendViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case RowType.FRIEND_ROW_TYPE:
                View buttonTypeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_friends, parent, false);
                return new MyRecyclerViewAdapter.FriendViewHolder(buttonTypeView);
            case RowType.ADD_ROW_TYPE:
                View textTypeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_add_friends, parent, false);
                return new MyRecyclerViewAdapter.AddFriendViewHolder(textTypeView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        dataSet.get(position).onBindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


}


