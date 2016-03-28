package com.fantasystock.fantasystock.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fantasystock.fantasystock.Fragments.WatchlistFragment;
import com.fantasystock.fantasystock.Helpers.CallBack;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivity extends AppCompatActivity {

    @Bind(R.id.ivCoverPhoto)
    ImageView ivCoverPhoto;
    @Bind(R.id.ivCoverBlurredPhoto) ImageView ivCoverBlurredPhoto;
    @Bind(R.id.ivCoverPhotoTitleBar) ImageView ivCoverPhotoTitleBar;
    @Bind(R.id.ivCoverBlurredPhotoTitleBar) ImageView ivCoverBlurredPhotoTitleBar;
    @Bind(R.id.ivUserProfile) ImageView ivUserProfile;
    @Bind(R.id.tvProfileDescription)
    TextView tvProfileDescription;
    @Bind(R.id.tvProfileName) TextView tvProfileName;
    @Bind(R.id.tvProfileUsername) TextView tvProfileUsername;
    @Bind(R.id.ablProfileAppBar)
    AppBarLayout ablProfileAppBar;
    @Bind(R.id.rlTitleBar)
    RelativeLayout rlTitleBar;
    @Bind(R.id.tvTweetsCount) TextView tvStocksCount;
    @Bind(R.id.tvFollowingsCount) TextView tvFollowingsCount;
    @Bind(R.id.tvCoverTitleTitleBar) TextView tvCoverTitleTitleBar;
    @Bind(R.id.tvCoverScreenTextTitleBar) TextView tvCoverScreenTextTitleBar;
    @Bind(R.id.ivCoverUserProfile) ImageView ivCoverUserProfile;
    @Bind(R.id.rlCoverTitleBar) RelativeLayout rlCoverTitleBar;
    @Bind(R.id.rlDim) RelativeLayout rlDim;
    @Bind(R.id.ibFollowButton) ImageButton followButton;

    private WatchlistFragment watchlistFragment;
    private User user;
    private float userProfileOriginY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.bind(this);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        if (userId==null) {
            userId = User.currentUser.id;
            user = User.currentUser;
        } else if (User.getUser(userId)!=null) {
            user = User.getUser(userId);
            setUser(user);
        } else {
            User.queryUser(userId, new CallBack(){
                @Override
                public void userCallBack(User user) {
                    setUser(user);
                }
            });
        }
        userProfileOriginY = Float.MIN_VALUE;

        ablProfileAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                handleScrolling(appBarLayout, verticalOffset);
            }
        });
        watchlistFragment = WatchlistFragment.newInstance(userId);
        getSupportFragmentManager().beginTransaction().replace(R.id.flWatchListHolder, watchlistFragment).commit();
        setUser(user);

    }

    private void handleScrolling(AppBarLayout appBarLayout, int verticalOffset) {
        float scale = 1.0f + (verticalOffset / 400.0f);
        if (scale < 0.5f) scale = 0.5f;
        ivUserProfile.setScaleX(scale);
        ivUserProfile.setScaleY(scale);

        if (userProfileOriginY == Float.MIN_VALUE) {
            userProfileOriginY = ivUserProfile.getY();
        }
        float coverTitleBarOffSet = tvProfileUsername.getY() + verticalOffset + 175.0f;

        if (coverTitleBarOffSet < 270) {
            coverTitleBarOffSet = 270;
        }
        rlCoverTitleBar.setY(coverTitleBarOffSet);
        if (scale == 0.5f) {
            ivUserProfile.setY(userProfileOriginY + (verticalOffset + 400.0f * (1 - scale)) / 2);
            rlTitleBar.setVisibility(View.VISIBLE);
        } else {
            rlTitleBar.setVisibility(View.INVISIBLE);
        }
        if (verticalOffset > -270) {
            rlTitleBar.setY(verticalOffset + 87.5f);
        }
        float alpha = Math.abs(verticalOffset / 400.0f);
        if (alpha > 1 ) alpha = 1.0f;
        ivCoverBlurredPhoto.setAlpha(alpha);
        ivCoverBlurredPhotoTitleBar.setAlpha(alpha);
        rlDim.setAlpha(Math.abs((verticalOffset + 200.0f) / 400.0f));

    }

    private void setUser(User user) {
        this.user = user;
        if (user == null) return;
        // setup View
        tvProfileName.setText(user.username);
        tvCoverTitleTitleBar.setText(user.username);
        tvProfileUsername.setText("$"+Utils.moneyConverter(user.totalValue));
//        tvProfileDescription.setText(Util.checkStringEmpty(user.description));
        tvFollowingsCount.setText(user.followings.size()+"");
        tvStocksCount.setText(user.investingStocks.size()+"");
        if (User.currentUser.id != user.id) {
            boolean following = (User.currentUser.followings.contains(user.id));
            followButton.setImageResource(following?R.drawable.ic_following:R.drawable.ic_follow);
        } else {
            followButton.setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(user.profileCoverPhotoUrl)) {
            ivCoverBlurredPhoto.setAlpha(0.0f);
            Glide.with(ivCoverPhoto.getContext()).load(user.profileCoverPhotoUrl).placeholder(R.drawable.ic_profile).into(ivCoverPhoto);
            Utils.blurrLoadingImage(ivCoverBlurredPhoto, user.profileCoverPhotoUrl);
            Glide.with(ivCoverPhotoTitleBar.getContext()).load(user.profileCoverPhotoUrl).placeholder(R.drawable.ic_profile).into(ivCoverPhotoTitleBar);
            Utils.blurrLoadingImage(ivCoverBlurredPhotoTitleBar, user.profileCoverPhotoUrl);
        } else {
            ivCoverPhoto.setImageResource(R.drawable.league_profile);
            ivCoverBlurredPhoto.setImageResource(R.drawable.league_profile_blurred);
            ivCoverPhotoTitleBar.setImageResource(R.drawable.league_profile);
            ivCoverBlurredPhotoTitleBar.setImageResource(R.drawable.league_profile_blurred);
        }

        if (!TextUtils.isEmpty(user.profileImageUrl)) {
            Utils.setupProfileImage(ivUserProfile, user.profileImageUrl);
            Utils.setupProfileImage(ivCoverUserProfile, user.profileImageUrl);
        } else {
            String url = user.profileImageUrl;
            Context context = ivUserProfile.getContext();
            Glide.with(context).load(url).fitCenter().placeholder(R.drawable.ic_profile).into(ivUserProfile);
            Glide.with(ivCoverUserProfile.getContext()).load(url).fitCenter().placeholder(R.drawable.ic_profile).into(ivCoverUserProfile);
        }
    }

    @OnClick(R.id.ibFollowButton)
    public void onFollowButton() {
        followButton.setAlpha(0.5f);
        User.currentUser.followUser(user, new CallBack(){
            @Override
            public void done() {
                followButton.setAlpha(1.0f);
                boolean following = (User.currentUser.followings.contains(user.id));
                followButton.setImageResource(following ? R.drawable.ic_following : R.drawable.ic_follow);
            }
        });
    }


}
