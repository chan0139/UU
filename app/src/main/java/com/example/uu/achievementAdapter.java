package com.example.uu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class achievementAdapter extends BaseAdapter {
    public ArrayList<achievementObject> items=new ArrayList<achievementObject>();

    public void addItem(achievementObject item){
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        achievementObject item=items.get(i);

        if(view==null)
        {
            LayoutInflater inflater=(LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.list_achievement,viewGroup,false);
        }

        TextView title=view.findViewById(R.id.achievementTitle);
        title.setText(item.getObjName());



        // 아이템별 달성 목표 여부를 확인해서 달성했으면 달성 아이콘 표시/
        if(item.getFlag() == true) {
            ImageView imageView=view.findViewById(R.id.achievement);
            imageView.setImageResource(R.drawable.ic_achievement);
        }
        else{
            ImageView imageView=view.findViewById(R.id.achievement);
            imageView.setImageResource(R.drawable.ic_achievement_incomplete);
        }

        return view;
    }


}
