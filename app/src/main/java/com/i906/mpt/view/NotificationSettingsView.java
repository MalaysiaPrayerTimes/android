package com.i906.mpt.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.i906.mpt.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Noorzaini Ilhami on 17/10/2015.
 */
public class NotificationSettingsView extends CardView {

    protected boolean isCardExpanded = true;
    protected int mOriginalHeight = 0;

    private OnClickListener mPrayerOnClickListener;
    private OnClickListener mCardExpandListener;

    @Bind(R.id.v_notification)
    protected View settings;

    @Bind(R.id.tv_prayer_name)
    protected TextView prayer;

    @Bind(R.id.sw_prayer)
    protected SwitchCompat mainSwitch;

    @Bind(R.id.cb_notification)
    protected CheckBox notification;

    @Bind(R.id.cb_vibrate)
    protected CheckBox vibrate;

    @Bind(R.id.btn_reminder)
    protected Button reminderTone;

    @Bind(R.id.btn_notification)
    protected Button notificationTone;

    public NotificationSettingsView(Context context) {
        this(context, null);
    }

    public NotificationSettingsView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.notificationSettingsStyle);
    }

    public NotificationSettingsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.card_notification, this, true);
        ButterKnife.bind(this);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOnClickListener(v -> setCardExpanded(!isCardExpanded, true));

        getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                mOriginalHeight = settings.getHeight();
                                if (!isCardExpanded) settings.setVisibility(GONE);
                            }
                        });
    }

    public void setSettingsEnabled(boolean enabled) {
        prayer.setEnabled(enabled);
        notification.setEnabled(enabled);
        vibrate.setEnabled(enabled);
        reminderTone.setEnabled(enabled);
        notificationTone.setEnabled(enabled);
    }

    public void setPrayerName(String name) {
        prayer.setText(name);
    }

    public boolean isPrayerEnabled() {
        return mainSwitch.isChecked();
    }

    public void setPrayerEnabled(boolean enabled) {
        mainSwitch.setChecked(enabled);
        setSettingsEnabled(enabled);
    }

    public void setPrayerOnClickListener(OnClickListener listener) {
        mPrayerOnClickListener = listener;
    }

    public boolean isNotificationEnabled() {
        return notification.isChecked();
    }

    public void setNotificationEnabled(boolean enabled) {
        notification.setChecked(enabled);
    }

    public boolean isVibrationEnabled() {
        return vibrate.isChecked();
    }

    public void setVibrateEnabled(boolean enabled) {
        vibrate.setChecked(enabled);
    }

    public boolean isCardExpanded() {
        return isCardExpanded;
    }

    public void setCardExpanded(boolean expanded, boolean animate) {
        if (mOriginalHeight == 0) {
            mOriginalHeight = settings.getHeight();
        }

        if (!animate) {
            if (expanded) {
                settings.setAlpha(1);
                settings.setVisibility(VISIBLE);

                if (mOriginalHeight != 0) {
                    settings.getLayoutParams().height = mOriginalHeight;
                }

                isCardExpanded = true;
            } else {
                settings.setAlpha(0);

                if (mOriginalHeight != 0) {
                    settings.setVisibility(GONE);
                }

                isCardExpanded = false;
            }

            return;
        }

        if (expanded == isCardExpanded) return;
        if (mOriginalHeight == 0) return;

        ValueAnimator valueAnimator;

        if (expanded) {
            valueAnimator = ValueAnimator.ofInt(0, mOriginalHeight);
            isCardExpanded = true;
        } else {
            valueAnimator = ValueAnimator.ofInt(mOriginalHeight, 0);
            isCardExpanded = false;
        }

        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            settings.getLayoutParams().height = value;
            settings.setAlpha((float) value / mOriginalHeight);
            settings.requestLayout();
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (expanded) {
                    settings.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!expanded) {
                    settings.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();

        if (mCardExpandListener != null) mCardExpandListener.onClick(this);
    }

    public void setCardExpandListener(OnClickListener listener) {
        mCardExpandListener = listener;
    }

    @OnClick(R.id.sw_prayer)
    protected void onPrayerSwitchClicked() {
        boolean c = mainSwitch.isChecked();
        setCardExpanded(c, true);
        setSettingsEnabled(c);
        if (mPrayerOnClickListener != null) mPrayerOnClickListener.onClick(mainSwitch);
    }
}
