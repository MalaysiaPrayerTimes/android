package com.i906.mpt.settings.azanpicker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.common.BaseDialogFragment;
import com.i906.mpt.util.RingtoneHelper;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Noorzaini Ilhami on 18/10/2015.
 */
public class AzanPickerFragment extends BaseDialogFragment implements AzanPickerAdapter.AzanListener {

    public static final String IS_NOTIFICATION = "IS_NOTIFICATION";
    public static final String PRAYER_ID = "PRAYER_ID";
    public static final String TONE_URI = "TONE_URI";

    private int mPrayerId = -1;
    private String mToneUri;
    private boolean isNotification;

    private Subscription mSubscription;
    private AzanPickerAdapter mAdapter;
    private MediaPlayer mMediaPlayer;
    private AzanListener mAzanListener;

    @Inject
    RingtoneHelper mRingtoneHelper;

    @BindView(R.id.recyclerview)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.progress_container)
    protected View mProgressContainer;

    @BindView(R.id.error_container)
    protected View mErrorContainer;

    @BindView(R.id.tv_error)
    protected TextView mErrorMessageView;

    @BindView(R.id.recycler_container)
    protected View mListContainer;

    @BindView(R.id.swipe_container)
    protected SwipeRefreshLayout mSwipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph().inject(this);

        mAdapter = new AzanPickerAdapter(getActivity());
        mAdapter.setListener(this);
        mMediaPlayer = new MediaPlayer();
        Bundle args = getArguments();

        if (args != null) {
            mPrayerId = args.getInt(PRAYER_ID);
            mToneUri = args.getString(TONE_URI, null);
            isNotification = args.getBoolean(IS_NOTIFICATION, true);
        }

        mAdapter.setSelectedTone(mToneUri);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_recycler, null);
        ButterKnife.bind(this, v);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.label_alarm)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String t = mAdapter.getSelectedToneUri();
                        mAzanListener.onToneSelected(mPrayerId, t, isNotification);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeContainer.setEnabled(false);
        setupRecyclerView();
        showProgress();
    }

    protected void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        if (mRecyclerView.getAdapter() == null) mRecyclerView.setAdapter(mAdapter);
    }

    protected void showContent() {
        mSwipeContainer.setRefreshing(false);
        setContentVisibility(true, true);
        setProgressVisibility(false, true);
        setErrorVisibility(false, true);
    }

    protected void showProgress() {
        setContentVisibility(false, true);
        setProgressVisibility(true, true);
        setErrorVisibility(false, true);
    }

    protected void showError(@StringRes int resId) {
        showError(getString(resId));
    }

    protected void showError(String message) {
        mErrorMessageView.setText(message);
        setContentVisibility(false, true);
        setProgressVisibility(false, true);
        setErrorVisibility(true, true);
    }

    protected void setContentVisibility(boolean visible, boolean animate) {
        setViewVisibility(mListContainer, visible, animate);
    }

    protected void setProgressVisibility(boolean visible, boolean animate) {
        setViewVisibility(mProgressContainer, visible, animate);
    }

    protected void setErrorVisibility(boolean visible, boolean animate) {
        setViewVisibility(mErrorContainer, visible, animate);
    }

    protected void setViewVisibility(View view, boolean visible, boolean animate) {
        if (view.getVisibility() == View.VISIBLE && visible) return;
        if (view.getVisibility() == View.GONE && !visible) return;

        if (visible) {
            if (animate) {
                view.startAnimation(
                        AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
            view.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                view.startAnimation(
                        AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            } else {
                view.clearAnimation();
            }
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mSubscription = mRingtoneHelper.getToneList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Tone>>() {
                    @Override
                    public void call(List<Tone> tones) {
                        mAdapter.setToneList(tones);
                        showContent();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        e.printStackTrace();
                        showError(R.string.error_unexpected);
                    }
                });
    }

    @Override
    public void onToneSelected(Tone tone) {
        startPlayingTone(tone.getUri());
    }

    private void startPlayingTone(String uri) {
        try {
            mMediaPlayer.reset();
            if (uri == null) return;
            mMediaPlayer.setDataSource(uri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopPlayingTone() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public AzanPickerFragment setListener(AzanListener listener) {
        mAzanListener = listener;
        return this;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPlayingTone();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public interface AzanListener {
        void onToneSelected(int prayer, String toneUri, boolean isNotification);
    }

    public static AzanPickerFragment newInstance(int prayerId, String toneUri, boolean notification) {
        Bundle args = new Bundle();
        args.putInt(PRAYER_ID, prayerId);
        args.putString(TONE_URI, toneUri);
        args.putBoolean(IS_NOTIFICATION, notification);

        AzanPickerFragment f = new AzanPickerFragment();
        f.setArguments(args);
        return f;
    }
}
