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
public class OnboardingFragment2 extends Fragment implements OnboardingActivity.onSelectedAction{
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvSubTitle) TextView tvSubTitle;
    private OnboardingAction actionHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarding_fragment_2, parent, false);
        ButterKnife.bind(this, view);
        tvTitle.setAlpha(0.0f);
        tvSubTitle.setAlpha(0.0f);

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
    public void onAnimation() {
        ObjectAnimator animatorTitle = ObjectAnimator.ofFloat(tvTitle, "alpha", 1.0f);
        animatorTitle.setDuration(1000);
        animatorTitle.setStartDelay(0);


        ObjectAnimator animatorSubtitle = ObjectAnimator.ofFloat(tvSubTitle, "alpha", 1.0f);
        animatorSubtitle.setDuration(1000);
        animatorSubtitle.setStartDelay(1000);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorTitle, animatorSubtitle);
        set.start();
    }

    @OnClick(R.id.tvStart)
    public void onStartClick() {
        actionHandler.onNext();
    }
}
