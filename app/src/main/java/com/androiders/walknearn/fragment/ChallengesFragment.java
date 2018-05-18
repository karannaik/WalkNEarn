package com.androiders.walknearn.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.androiders.walknearn.Main2Activity;
import com.androiders.walknearn.R;
import com.androiders.walknearn.adapter.ChallengeAdapter;
import com.androiders.walknearn.model.User;
import com.androiders.walknearn.model.UserLocalStore;

import java.util.ArrayList;

public class ChallengesFragment extends Fragment {

    private View view;
    ListView ChallengesList;


    public ChallengesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_challenges, container, false);
        ChallengesList = view.findViewById(R.id.challengesList);
        final ArrayList<String> ChallengeItemsList = new ArrayList<>();

        UserLocalStore userLocalStore = new UserLocalStore(getActivity());

        final User user = userLocalStore.getLoggedInUser();

        ChallengeItemsList.add("Earn 300 walkcoins on walking 1000 in the last 12 hours");
        ChallengeItemsList.add("If walked at least 7500 steps yesterday, claim to earn 500 walkcoins");
        ChallengeItemsList.add("Claim 250 walkcoins if covered at least 100 metres/day in any of the last 7 days");
        ChallengeItemsList.add("Gain 1000 walkcoins on walking 6000 steps/day in a row");
        ChallengeItemsList.add("500 walkcoins on burning 1000 calories in the last 8 hours");
        ChallengeItemsList.add("Claim 750 walkcoins on covering a total of 1000 metres in the last week");

        final ChallengeAdapter challengeAdapter = new ChallengeAdapter(getActivity(),R.layout.challenge_list_item,ChallengeItemsList);
        ChallengesList.setAdapter(challengeAdapter);

        ChallengesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                    builder.setTitle("Congratulations!!")
                        .setMessage("Won 300 walkcoins!!")
                        .setNeutralButton("Ok", null)
                        .setIcon(R.drawable.ic_mood_black_24dp)
                        .show();
                    user.setWalkCoins(user.getWalkCoins()+300);
                    ChallengeItemsList.remove(0);
                    challengeAdapter.notifyDataSetChanged();
                    ((Main2Activity)getActivity()).updateWalkcoins(user.getWalkCoins());
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                    builder.setTitle("Incomplete!!")
                        .setMessage("Task incomplete!! Try again later!!")
                        .setNeutralButton("Ok", null)
                        .setIcon(R.drawable.ic_priority_high_black_24dp)
                        .show();
                }
            }
        });

        return view;
    }
}
