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
    private onCardListener mOnCardListener;

    public HomeRecyclerAdapter(ArrayList<Fact> facts, onCardListener onCardListener) {
        mFacts = facts;
        mOnCardListener = onCardListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_item_home, parent, false), mOnCardListener);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        private CardView cardView;
        private TextView textView;
        private onCardListener onCardListener;

        ViewHolder(@NonNull View itemView, onCardListener onCardListener) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_recycler_item);
            textView = itemView.findViewById(R.id.textview_recycler);
            this.onCardListener = onCardListener;

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCardListener.onCardClick();
        }

        @Override
        public boolean onLongClick(View v) {
            onCardListener.onCardLongClick(getAdapterPosition());
            return true;
        }
    }

    public interface onCardListener{
        void onCardLongClick(int position);
        void onCardClick();
    }

}
