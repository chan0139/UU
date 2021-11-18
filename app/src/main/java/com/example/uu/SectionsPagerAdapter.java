package com.example.uu;

import static android.view.MotionEvent.ACTION_UP;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SectionsPagerAdapter extends PagerAdapter {
    private static final int FIRST_STEP=0;
    private static final int SECOND_STEP=1;
    private static final int FINAL_STEP=2;

    private final Context mContext;
    private final FitTestData targetCrew;

    private List<String> selectedPriority=new ArrayList<>();

    private OnPageListener onPageListener;
    interface OnPageListener {
        void OnFitTestClose();
        void OnNextClicked(List<Integer> checkChips);
        void OnGobackToFirstClicked();
        void OnCalculateFitnessClicked();
    }

    public SectionsPagerAdapter(Context mContext,FitTestData targetCrew) {
        super();
        this.mContext=mContext;
        this.targetCrew=targetCrew;
        onPageListener=(OnPageListener) mContext;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view=null;
        LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (position){
            case FIRST_STEP:
                view=inflater.inflate(R.layout.fittest_stepfirst,container,false);
                TextView targetCrewName=view.findViewById(R.id.targetCrewName);
                targetCrewName.setText(targetCrew.getCrewName());

                ChipGroup chipGroup = view.findViewById(R.id.priorityChipGroup);

                view.findViewById(R.id.fittestClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //callback close
                        onPageListener.OnFitTestClose();
                    }
                });

                View finalView = view;
                view.findViewById(R.id.fittestGotoSecond).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(chipGroup.getCheckedChipIds().size()==0){
                            Toast.makeText(mContext.getApplicationContext(), "Check at least One item",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //callback show second page
                            updateSelectedPriority(finalView);
                            notifyDataSetChanged();
                            onPageListener.OnNextClicked(chipGroup.getCheckedChipIds());
                        }
                    }
                });
                break;
            case SECOND_STEP:
                view=inflater.inflate(R.layout.fittest_stepsecond,container,false);
                RecyclerView recyclerView=view.findViewById(R.id.selectedPriority);
                RecyclerAdapterForPriorityString recyclerAdapterForPriorityString=new RecyclerAdapterForPriorityString(selectedPriority);

                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.setAdapter(recyclerAdapterForPriorityString);

                RecyclerView recyclerView1=view.findViewById(R.id.priorityInt);
                RecyclerAdapterForPriorityNum recyclerAdapterForPriorityNum = new RecyclerAdapterForPriorityNum(selectedPriority);

                recyclerView1.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView1.setAdapter(recyclerAdapterForPriorityNum);

                DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(dividerItemDecoration);
                recyclerView1.addItemDecoration(dividerItemDecoration);

                ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);

                view.findViewById(R.id.fittestGotoFirst).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPageListener.OnGobackToFirstClicked();
                    }
                });

                view.findViewById(R.id.fittestCalculate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //return some Object from listener
                        onPageListener.OnCalculateFitnessClicked();

                        notifyDataSetChanged();
                    }
                });

                break;
            case FINAL_STEP:
                view=inflater.inflate(R.layout.fittest_stepfirst,container,false);
                break;
        }
        container.addView(view);
        return view;
    }

    public void updateSelectedPriority(View view){
        Chip chip=view.findViewById(R.id.chip0);
        if(chip.isChecked()){
            selectedPriority.add("Average Running Time");
        }
        chip=view.findViewById(R.id.chip1);
        if(chip.isChecked()){
            selectedPriority.add("Average Running Distance");
        }
        chip=view.findViewById(R.id.chip2);
        if(chip.isChecked()){
            selectedPriority.add("Average Running Days per Week");
        }
        chip=view.findViewById(R.id.chip3);
        if(chip.isChecked()){
            selectedPriority.add("What Day do you usually run");
        }
        chip=view.findViewById(R.id.chip4);
        if(chip.isChecked()){
            selectedPriority.add("What Time do you usually run");
        }
        chip=view.findViewById(R.id.chip5);
        if(chip.isChecked()){
            selectedPriority.add("Where do you usually run");
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.START|ItemTouchHelper.END,0) {
        @SuppressWarnings("deprecation")
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(selectedPriority,fromPosition,toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }
}