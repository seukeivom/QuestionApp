package com.seu.qapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seu.qapp.R;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<String> mDataAddr, mDataPage;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    public CustomAdapter(Context context, ArrayList<String> mDataAddr, ArrayList<String> mDataPage) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataAddr = mDataAddr;
        this.mDataPage = mDataPage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        File f = new File(mDataAddr.get(position));
        String file_name = f.getName();
        Log.d(TAG, "onBindViewHolder: file name" + file_name);
        Log.d(TAG, "onBindViewHolder: " + position);
        String page = mDataPage.get(position);
        holder.textView1.setText(file_name);
        holder.textView2.setText(page);
    }

    @Override
    public int getItemCount() {
        return mDataPage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView1, textView2;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    String getItem(int id) {
        return mDataPage.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
