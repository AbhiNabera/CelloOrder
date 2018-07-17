package com.example.wonglab.jmorder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wonglab.jmorder.Database.Item;

import java.util.List;

public class OrderListRecyclerAdapter extends RecyclerView.Adapter<OrderListRecyclerAdapter.ViewHolder> {

    //private List<String> values_name;
    //private List<String> values_qty;
    private List<Item> dataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itemName;
        public TextView quantity;
        public View layout;
        public LinearLayout foregroundLayout;
        public RelativeLayout backgroundLayout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            itemName = (TextView) v.findViewById(R.id.itemName);
            quantity = (TextView) v.findViewById(R.id.quantity);
            foregroundLayout = (LinearLayout) v.findViewById(R.id.foregorundLayout);
            backgroundLayout = (RelativeLayout) v.findViewById(R.id.backgroundLayout);
        }
    }

    public void add(int position, String itemName, String qty) {
        values_name.add(position, itemName);
        //dataset.add()
        values_qty.add(position, qty);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values_name.remove(position);
        values_qty.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderListRecyclerAdapter(List<Item> myDataset) {
        values_name = myDatasetItem;
        values_qty = myDatasetQty;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrderListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.listview_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String name = values_name.get(position);
        final String qty = values_qty.get(position);
        holder.itemName.setText(name);
        holder.quantity.setText(qty);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values_name.size();
    }

}
