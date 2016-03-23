package com.fantasystock.fantasystock.Onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasystock.fantasystock.R;

import butterknife.ButterKnife;

/**
 * Created by wilsonsu on 3/23/16.
 */
public class OnboardingFragment5 extends OnboardingFragment2{
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarding_fragment_5, parent, false);
        ButterKnife.bind(this, view);
        tvTitle.setAlpha(0.0f);
        tvSubTitle.setAlpha(0.0f);

        return view;
    }
}
