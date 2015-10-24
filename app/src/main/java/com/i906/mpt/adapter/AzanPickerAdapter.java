package com.i906.mpt.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.i906.mpt.R;
import com.i906.mpt.model.Tone;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Noorzaini Ilhami on 19/10/2015.
 */
public class AzanPickerAdapter extends RecyclerView.Adapter<AzanPickerAdapter.ViewHolder> {

    private List<Tone> mToneList;
    private String mNoneText;
    private String mSelectedTone;
    private AzanListener mListener;

    public AzanPickerAdapter(Context context) {
        mToneList = new ArrayList<>();
        mNoneText = context.getResources().getString(R.string.label_none);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_tone, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(AzanPickerAdapter.ViewHolder holder, int position) {
        Tone t = getItem(position);

        holder.tone = t;
        holder.selected.setText(t.getName());

        if (mSelectedTone != null) {
            holder.selected.setChecked(mSelectedTone.equals(t.getUri()));
        } else {
            holder.selected.setChecked(t.getUri() == null);
        }
    }

    @Override
    public int getItemCount() {
        return mToneList.size();
    }

    public Tone getItem(int position) {
        return mToneList.get(position);
    }

    public void setToneList(List<Tone> list) {
        Tone none = new Tone();
        none.setName(mNoneText);
        mToneList = list;
        mToneList.add(0, none);
        notifyDataSetChanged();
    }

    @Nullable
    public String getSelectedToneUri() {
        return mSelectedTone;
    }

    public void setSelectedTone(Tone tone) {
        setSelectedTone(tone.getUri());
        if (mListener != null) mListener.onToneSelected(tone);
    }

    public void setSelectedTone(String uri) {
        mSelectedTone = uri;
        notifyDataSetChanged();
    }

    public void setListener(AzanListener listener) {
        this.mListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private AzanPickerAdapter adapter;
        private Tone tone;

        @Bind(R.id.rb_selected)
        protected RadioButton selected;

        public ViewHolder(View v, AzanPickerAdapter adapter) {
            super(v);
            ButterKnife.bind(this, v);
            this.adapter = adapter;
        }

        @OnClick(R.id.list_item)
        protected void onToneSelected() {
            adapter.setSelectedTone(tone);
        }
    }

    public interface AzanListener {
        void onToneSelected(Tone tone);
    }
}
