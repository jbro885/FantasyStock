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

import android.widget.ImageView;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.OnboardingActivity;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wilsonsu on 3/22/16.
 */
public class OnboardingFragment1 extends Fragment implements OnboardingActivity.onSelectedAction {
    @Bind(R.id.ivBackgroundBlurred) ImageView ivBackgroundBlurred;
    @Bind(R.id.tvTitle) TextView tvTitle;
    @Bind(R.id.tvSubTitle) TextView tvSubTitle;
    private OnboardingAction actionHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarding_fragment_1, parent, false);
        ButterKnife.bind(this, view);
//        tvTitle.setAlpha(0.0f);
        tvSubTitle.setAlpha(0.0f);
        ivBackgroundBlurred.setAlpha(0.0f);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnboardingAction) {
            actionHandler = (OnboardingAction)context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onAnimation();
    }

    @OnClick(R.id.tvSkip)
    public void onSkipClick() {
        actionHandler.onSkip();
    }

    @OnClick(R.id.tvStart)
    public void onStartClick() {
        actionHandler.onNext();
    }

    @Override
    public void onAnimation() {


        ObjectAnimator animatorSubtitle = ObjectAnimator.ofFloat(tvSubTitle, "alpha", 1.0f);
        animatorSubtitle.setDuration(1000);
        animatorSubtitle.setStartDelay(0);


        ObjectAnimator animatorBackground = ObjectAnimator.ofFloat(ivBackgroundBlurred, "alpha", 1.0f);
        animatorBackground.setDuration(2000);
        animatorBackground.setStartDelay(0);

        AnimatorSet set = new AnimatorSet();
        set.playTogether( animatorSubtitle, animatorBackground);
        set.start();
    }
}
