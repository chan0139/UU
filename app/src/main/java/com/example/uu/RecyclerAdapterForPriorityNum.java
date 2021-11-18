package com.example.uu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapterForPriorityNum extends RecyclerView.Adapter<RecyclerAdapterForPriorityNum.ViewHolder> {
    List<String> selectedPriority;

    public RecyclerAdapterForPriorityNum(List<String> selectedPriority) {
        this.selectedPriority = selectedPriority;
    }

    @NonNull
    @Override
    public RecyclerAdapterForPriorityNum.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_item_selectedpriority_num, parent, false);
        RecyclerAdapterForPriorityNum.ViewHolder viewHolder = new RecyclerAdapterForPriorityNum.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterForPriorityNum.ViewHolder holder, int position) {
        holder.priorityNum.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return selectedPriority.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView priorityNum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            priorityNum = itemView.findViewById(R.id.priorityNum);
        }

    }
}
