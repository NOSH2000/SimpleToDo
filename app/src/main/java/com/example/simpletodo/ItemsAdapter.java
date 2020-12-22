package com.example.simpletodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Responsible for displaying data from the model into a tow in the recycler view
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder>{

    public interface OnClickListener {
        void OnItemClicked(int position);
    }
    public interface OnLongClickListener {
        /*
        The class implementing this method, MainActivity.java, needs to know the position of where we did the
        long press so it can notify the adapter its position at which we should delete
         */
        void OnItemLongClicked(int position);
    }

    List<String> items;
    OnLongClickListener longClickedListener;
    OnClickListener onClickListener;

    public ItemsAdapter(List<String> items, OnLongClickListener longClickedListener, OnClickListener onClickListener){
        this.items = items;
        this.longClickedListener = longClickedListener;
        this.onClickListener = onClickListener;
    } // Constructor

    @NonNull
    @Override
    /**
     * Responsible for creating each view
     */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use layout inflator to inflate the view
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        // wrap it inside a view holder and return it
        return new ViewHolder(todoView);
    }

    @Override
    /**
     * Responsible for taking data at a particular position and putting it into a view holder
     * binding data to a particular view holder
     */
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Grab the item at position
        String item = items.get(position);
        // Bind the item into the specified view holder
        holder.bind(item);
    }

    @Override
    /**
     * Tells the RecyclerView how many items are in the list
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Container to provide easy access to views that represent each row of the list
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(android.R.id.text1);
        } // Constructor

        /**
         * update the view inside of the view holder with item value
         * @param item
         */
        public void bind(String item) {
            tvItem.setText(item);
            tvItem.setOnClickListener(new View.OnClickListener(){
                @Override
                 public void onClick(View view) {
                    onClickListener.OnItemClicked(getAdapterPosition());
                }
            });

            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // Notify the listener at which position there was a long press
                    longClickedListener.OnItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
