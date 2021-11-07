package com.example.uu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

        return view;
    }


}
