package com.devsoftzz.numfacts.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.devsoftzz.numfacts.R;
import com.devsoftzz.numfacts.models.Fact;
import com.devsoftzz.numfacts.utils.myUtils;

import java.util.ArrayList;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    private ArrayList<Integer> ColorSet = myUtils.getColorSet();
    private ArrayList<Fact> mFacts;

    public HomeRecyclerAdapter(ArrayList<Fact> facts) {
        mFacts = facts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(mFacts.get(position).getText());
        holder.textView.setTextColor(ColorSet.get(position % ColorSet.size()));
    }

    @Override
    public int getItemCount() {
        return mFacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_recycler_item);
            textView = itemView.findViewById(R.id.textview_recycler);
        }
    }
}
