package com.androiders.walknearn.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androiders.walknearn.R;

import java.util.ArrayList;

public class ChallengeAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> challengeList;

    public ChallengeAdapter(@NonNull Context context, int resource, ArrayList<String> challengeList) {
        super(context, resource, challengeList);
        this.context = context;
        this.challengeList = challengeList;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        String challengeListItem = challengeList.get(position);
        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.challenge_list_item,parent,false);
        TextView task = convertView.findViewById(R.id.task);
        task.setText(challengeListItem);
        Button button = convertView.findViewById(R.id.claim_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v,position,position);
            }
        });
        return convertView;
    }

}
