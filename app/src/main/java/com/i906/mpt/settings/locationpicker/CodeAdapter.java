package com.i906.mpt.settings.locationpicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.api.prayer.PrayerCode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Noorzaini Ilhami
 */
class CodeAdapter extends RecyclerView.Adapter<CodeAdapter.ViewHolder> {

    private List<PrayerCode> mList;
    private CodeListener mListener;

    CodeAdapter() {
        mList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_code, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PrayerCode c = getItem(position);

        holder.name.setText(c.getCity());
        holder.address.setText(c.getState());
        holder.code = c;
    }

    void setCodeList(List<PrayerCode> list) {
        mList = list;
        notifyDataSetChanged();
    }

    boolean isEmpty() {
        return mList.isEmpty();
    }

    private PrayerCode getItem(int position) {
        return mList.get(position);
    }

    public void setCodeListener(CodeListener listener) {
        mListener = listener;
    }

    private void onCodeSelected(PrayerCode code) {
        if (mListener != null) {
            mListener.onCodeSelected(code);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    interface CodeListener {
        void onCodeSelected(PrayerCode code);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private CodeAdapter adapter;
        private PrayerCode code;

        @BindView(R.id.tv_name)
        TextView name;

        @BindView(R.id.tv_address)
        TextView address;

        ViewHolder(View itemView, CodeAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick(R.id.list_item)
        void onCodeSelected() {
            adapter.onCodeSelected(code);
        }
    }
}
