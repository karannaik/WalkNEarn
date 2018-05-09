package com.androiders.walknearn.adapter;

import android.content.Context;
import android.graphics.drawable.RippleDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.androiders.walknearn.R;

import java.util.ArrayList;

// Custom list adapter for the settings list
public class ListButtonAdapter extends ArrayAdapter<String>{
    private Context context;
    private ArrayList<String> settingsList = null;
    private static final int IMAGE_VIEW = 0, TEXT_VIEW = 1, SWITCH_VIEW = 2;

    public ListButtonAdapter(Context context, int resource, ArrayList<String> settingsList) {
        super(context, resource,settingsList);
        this.context = context;
        this.settingsList = settingsList;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        String settingsListItem = settingsList.get(position);
        if(convertView == null){
            if(getItemViewType(position) == IMAGE_VIEW)
                convertView = LayoutInflater.from(context).inflate(R.layout.listview_image_type,parent,false);
            else if(getItemViewType(position) == TEXT_VIEW)
                convertView = LayoutInflater.from(context).inflate(R.layout.listview_text_type,parent,false);
            else
                convertView = LayoutInflater.from(context).inflate(R.layout.listview_switch_type,parent,false);
        }
        if(getItemViewType(position) == IMAGE_VIEW){
            // Setting image view to one defined in layout file
            ImageView changeProfilePic = convertView.findViewById(R.id.ChngProfilePic);
            changeProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ListView) parent).performItemClick(view,position,0);
                }
            });
        }
        else if(getItemViewType(position) == TEXT_VIEW) {
            TextView settingsButton = convertView.findViewById(R.id.listViewButton);
            settingsButton.setText(settingsListItem);
        }
        else{
            //setting switch view
            final Switch switchView = convertView.findViewById(R.id.listViewSwitch);
            switchView.setText(settingsListItem);
            switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                        ((ListView) parent).performItemClick(switchView,position,1);
                    else
                        ((ListView) parent).performItemClick(switchView,position,2);
                }
            });
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:
                return IMAGE_VIEW;
            case 3:
                return SWITCH_VIEW;
            default:
                return TEXT_VIEW;
        }
    }
}
