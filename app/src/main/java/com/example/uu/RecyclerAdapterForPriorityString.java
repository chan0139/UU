package com.example.uu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapterForPriorityString extends RecyclerView.Adapter<RecyclerAdapterForPriorityString.ViewHolder> {
    List<String> selectedPriority;

    public RecyclerAdapterForPriorityString(List<String> selectedPriority) {
        this.selectedPriority = selectedPriority;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_item_selectedpriority_string, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.priorityString.setText(selectedPriority.get(position));
    }

    @Override
    public int getItemCount() {
        return selectedPriority.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView priorityString;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            priorityString = itemView.findViewById(R.id.priorityString);
        }

    }
}
