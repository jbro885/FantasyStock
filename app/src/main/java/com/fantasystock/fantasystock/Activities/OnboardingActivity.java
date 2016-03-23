package com.fantasystock.fantasystock.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fantasystock.fantasystock.Onboarding.OnboardingFragment1;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OnboardingActivity extends AppCompatActivity {
    @Bind(R.id.vpViewPager) ViewPager vpViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        OnboardingPagerAdapter onboardingPagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        vpViewPager.setAdapter(onboardingPagerAdapter);
    }

    private static class OnboardingPagerAdapter extends FragmentPagerAdapter {

        public OnboardingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return new OnboardingFragment1();
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
