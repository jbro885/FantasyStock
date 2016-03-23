package com.fantasystock.fantasystock.Onboarding;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.OnboardingActivity;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/23/16.
 */
public class OnboardingFragment3 extends OnboardingFragment2{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarding_fragment_3, parent, false);
        ButterKnife.bind(this, view);
        tvTitle.setAlpha(0.0f);
        tvSubTitle.setAlpha(0.0f);

        return view;
    }

}
