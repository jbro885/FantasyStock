package com.fantasystock.fantasystock.Helpers;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fantasystock.fantasystock.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wilsonsu on 3/6/16.
 */
public class Utils {
    public static Date timeStampConverter(int timestamp) {
        return new Date((long) timestamp*1000);
    }

    public static String converTimetoRelativeTime(Date time) {
        if (time == null) return "";
        String relativeDate = DateUtils.getRelativeTimeSpanString(time.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_TIME).toString();
        relativeDate.replace("hour", "h");
        relativeDate.replace("minute", "min");
        return relativeDate;
    }
    public static Calendar convertDateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String numberConverter(int n) {
        String text;
        if (n>1000000) {
            text = n/1000000 + "M";
        } else if (n>1000) {
            text = n/1000 + "k";
        } else {
            text = n + "";
        }
        return text;
    }

    public static String moneyConverter(double n) {
        return Double.toString(Math.round(n * 100)/100);
    }

    public static void breathAnimationGenerator(final View view) {
        breathAnimationGenerator(view, true, 3000, 1000, 3000, 10000);
    }

    public static void breathAnimationGenerator(final View view, boolean startBreathIn,
                                                final int breathIn, final int holdBreath,
                                                final int breathOut, final int rest) {
        final AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation1.setDuration(breathIn);
        animation2.setDuration(breathOut);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                animation2.setStartOffset(holdBreath);
                view.startAnimation(animation2);
            }
        });

        //animation2 AnimationListener
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                animation1.setStartOffset(rest);
                view.startAnimation(animation1);
            }
        });
        if (startBreathIn) {
            view.startAnimation(animation1);
        } else {
            view.startAnimation(animation2);
        }
    }

    public static void repeatAnimationGenerator(final View view, final CallBack callBack) {
        final AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation1.setDuration(500);
        animation2.setDuration(500);
        animation2.setStartOffset(5000);

        //animation1 AnimationListener
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start animation2 when animation1 ends (continue)
                view.startAnimation(animation2);
            }
        });

        //animation2 AnimationListener
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start animation1 when animation2 ends (repeat)
                callBack.task();
                view.startAnimation(animation1);
            }
        });
        view.startAnimation(animation1);
    }

    public static void fadeIneAnimation(final View view) {
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        view.startAnimation(animation);
    }
    public static void fadeInAndOutAnimationGenerator(final View view, final CallBack callBack) {
        fadeInAndOutAnimationGenerator(view, callBack, null);
    }
    public static void fadeInAndOutAnimationGenerator(final View view, final CallBack callBack, final CallBack completionCallBack) {
        final AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        final AlphaAnimation animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation1.setDuration(500);
        animation2.setDuration(500);

        //animation1 AnimationListener
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (completionCallBack != null) {
                    completionCallBack.task();
                }
            }
        });

        //animation2 AnimationListener
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start animation1 when animation2 ends (repeat)
                if (callBack!=null) {
                    callBack.task();
                }
                view.startAnimation(animation1);
            }
        });
        view.startAnimation(animation2);
    }

    public static void setHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (height == -1) {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            params.height = height;
        }

        view.setLayoutParams(params);
    }

    public static void setWidth(View view, boolean isMachtParent) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = isMachtParent?ViewGroup.LayoutParams.MATCH_PARENT:ViewGroup.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(params);
    }

    public static Gson gsonForParseQuery() {
        return new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            }).create();


    }

    public static Gson gsonCreatorForNewsDateFormater() {
        // Mon Feb 15 17:31:31 +0000 2016
//        return new GsonBuilder().setDateFormat("EEE MMM dd HH:mm:ss zzzzz yyyy").create();
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ssz").create();
    }

    public static void setupProfileImage(ImageView view, String url) {
        Context context = view.getContext();
        if (url.startsWith("avatar_")) {
            int resourceId = context.getResources().getIdentifier(url, "drawable",  context.getPackageName());
            view.setImageResource(resourceId);
        } else if (context!=null) {
            Glide.with(context).load(url).fitCenter().placeholder(R.drawable.ic_profile).into(view);
        }
    }
}
