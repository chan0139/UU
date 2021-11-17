package com.example.uu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SectionsPagerAdapter extends PagerAdapter {
    private static final int FIRST_STEP=0;
    private static final int SECOND_STEP=1;
    private static final int FINAL_STEP=2;

    private final Context mContext;
    private final FitTestData targetCrew;

    public SectionsPagerAdapter(Context mContext,FitTestData targetCrew) {
        super();
        this.mContext=mContext;
        this.targetCrew=targetCrew;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view=null;
        LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (position){
            case FIRST_STEP:
                view=inflater.inflate(R.layout.fragment_fittest_stepfirst,container,false);
                TextView targetCrewName=view.findViewById(R.id.targetCrewName);
                targetCrewName.setText(targetCrew.getCrewName());
                break;
            case SECOND_STEP:
                view=inflater.inflate(R.layout.fragment_fittest_stepfirst,container,false);
                break;
            case FINAL_STEP:
                view=inflater.inflate(R.layout.fragment_fittest_stepfirst,container,false);
                break;
        }
        container.addView(view);
        return view;
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