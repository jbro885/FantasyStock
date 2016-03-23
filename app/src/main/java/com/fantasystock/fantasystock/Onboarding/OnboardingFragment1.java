package com.fantasystock.fantasystock.Onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.Activities.SignupActivity;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/22/16.
 */
public class OnboardingFragment1 extends Fragment{
    @Bind(R.id.ivBackgroundBlurred) ImageView ivBackgroundBlurred;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarding_fragment_1, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }



    @OnClick(R.id.tvSkip)
    public void onSkip() {
        startActivity(new Intent(getContext(), SignupActivity.class));
    }
}
