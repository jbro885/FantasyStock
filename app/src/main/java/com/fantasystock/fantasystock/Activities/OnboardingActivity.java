package com.fantasystock.fantasystock.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fantasystock.fantasystock.Helpers.DataCenter;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.Onboarding.OnboardingAction;
import com.fantasystock.fantasystock.Onboarding.OnboardingFragment1;
import com.fantasystock.fantasystock.Onboarding.OnboardingFragment2;
import com.fantasystock.fantasystock.Onboarding.OnboardingFragment3;
import com.fantasystock.fantasystock.Onboarding.OnboardingFragment4;
import com.fantasystock.fantasystock.Onboarding.OnboardingFragment5;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OnboardingActivity extends AppCompatActivity implements OnboardingAction {
    private OnboardingPagerAdapter onboardingPagerAdapter;
    @Bind(R.id.vpViewPager) ViewPager vpViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        onboardingPagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        vpViewPager.setAdapter(onboardingPagerAdapter);
        vpViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                onboardingPagerAdapter.fragments[position].onAnimation();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onSkip() {
        finish();
        // Parse User
        DataCenter.getInstance(); // to get and setup current user
        if(!User.isLogin()) {
            startActivity(new Intent(getApplicationContext(), SignupActivity.class));
        } else {
            Toast.makeText(this, "Welcome " + User.currentUser.username, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onNext() {
        int currentItem = vpViewPager.getCurrentItem();
        if (currentItem == onboardingPagerAdapter.getCount()-1) {
            onSkip();
        } else {
            vpViewPager.setCurrentItem(currentItem + 1, true);
        }
    }

    private static class OnboardingPagerAdapter extends FragmentPagerAdapter {
        public onSelectedAction[] fragments;
        public OnboardingPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new onSelectedAction[getCount()];
        }

        @Override
        public Fragment getItem(int position) {
            if (fragments[position] == null) {
                if (position==0)
                    fragments[position] = new OnboardingFragment1();
                else if (position==1)
                    fragments[position] = new OnboardingFragment2();
                else if (position==2)
                    fragments[position] = new OnboardingFragment3();
                else if (position==3)
                    fragments[position] = new OnboardingFragment4();
                else
                    fragments[position] = new OnboardingFragment5();

            }
            return (Fragment) fragments[position];
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    public interface onSelectedAction {
        public void onAnimation();
    }
}
