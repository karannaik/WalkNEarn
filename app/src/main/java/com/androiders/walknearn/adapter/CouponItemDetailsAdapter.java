package com.androiders.walknearn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androiders.walknearn.R;

import java.util.ArrayList;

public class CouponItemDetailsAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private ArrayList<String> offers = null;

    public CouponItemDetailsAdapter(Context context, int resource, ArrayList<String> offers) {
        super(context,resource,offers);
        this.context = context;
        this.resource = resource;
        this.offers = offers;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        String Offer = offers.get(position);
        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(resource,parent,false);
        TextView OfferValue = convertView.findViewById(R.id.offer_value);
        TextView OfferPercent = convertView.findViewById(R.id.offer_value_percent);
        Button Redeem = convertView.findViewById(R.id.redeem);
        String displayOffer[] = Offer.split(",");
        OfferValue.setText(displayOffer[0]);
        OfferPercent.setText(displayOffer[1]);
        Redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view,position,0);
            }
        });
        return convertView;
    }

}
