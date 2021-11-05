package com.example.uu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CrewPagerAdapter extends FragmentStateAdapter {

    public CrewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if(position == 0){
            return new fragment_recruitment_crew();
        }
        else return new fragment_recruitment_crewSchedule();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
