package com.fantasystock.fantasystock.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantasystock.fantasystock.Activities.UserProfileActivity;
import com.fantasystock.fantasystock.Helpers.Utils;
import com.fantasystock.fantasystock.Models.User;
import com.fantasystock.fantasystock.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chengfu_lin on 3/31/16.
 */
public class UserViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.ivUserProfile)
    ImageView ivUserProfile;
    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvRank) TextView tvRank;
    @Bind(R.id.tvPortfolio) TextView tvPortfolio;
    @Bind(R.id.ibChampion)
    ImageButton ibChampion;
    @Bind(R.id.rlItem)
    RelativeLayout rlItem;
    private Context context;

    public UserViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
    }
    public void setUser(final User user, int place) {
        if (user == null) return;

        tvName.setText(user.username);
        tvRank.setText(Integer.toString(place));
        tvPortfolio.setText(Utils.moneyConverter(user.totalValue));

        if (user.profileImageUrl == null) {
            ivUserProfile.setImageResource(R.drawable.ic_profile);
        }
        else {
            ivUserProfile.setImageResource(0);
            Utils.setupProfileImage(ivUserProfile, user.profileImageUrl);
        }
        // Mark the first place
        if(place == 1) {
            ibChampion.setAlpha((float) 1.0);
        }
        else {
            ibChampion.setAlpha((float) 0.0);
        }
        // Mark current user
        setItemTheme(user.id.equals(User.currentUser.id));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userId", user.id);
                context.startActivity(intent);
            }
        });
    }

    private void setItemTheme(boolean isDarkTheme) {
        if (isDarkTheme) {
            rlItem.setBackgroundColor(ContextCompat.getColor(context, R.color.darkBlue));
            tvName.setTextColor(Color.WHITE);
            tvRank.setTextColor(Color.WHITE);
            tvPortfolio.setTextColor(Color.LTGRAY);
        } else {
            rlItem.setBackgroundColor(Color.WHITE);
            tvName.setTextColor(Color.BLACK);
            tvRank.setTextColor(ContextCompat.getColor(context, R.color.darkBlue));
            tvPortfolio.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }

    }

}