package com.i906.mpt.mosque.ui;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.api.foursquare.Mosque;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class MosqueAdapter extends RecyclerView.Adapter<MosqueAdapter.ViewHolder> {

    private List<Mosque> mList;
    private MosqueListener mListener;

    MosqueAdapter() {
        mList = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_mosque, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mosque m = getItem(position);
        String address = TextUtils.join(", ", m.getAddress());
        long d = m.getDistance();
        String distance;

        if (d < 1000) {
            distance = String.format("%d m", d);
        } else {
            distance = String.format("%.01f km", (float) d / 1000);
        }

        holder.name.setText(m.getName());
        holder.address.setText(address);
        holder.distance.setText(distance);
        holder.mosque = m;
    }

    public List<Mosque> getMosqueList() {
        return mList;
    }

    public void setMosqueList(List<Mosque> list) {
        mList = list;
        Collections.sort(mList);
        notifyDataSetChanged();
    }

    public void setMosqueListener(MosqueListener listener) {
        mListener = listener;
    }

    public void removeMosqueListener(MosqueListener listener) {
        mListener = null;
    }

    public Mosque getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId().hashCode();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    protected void onMosqueSelected(Mosque mosque) {
        if (mListener != null) {
            mListener.onMosqueSelected(mosque);
        }
    }

    public interface MosqueListener {
        void onMosqueSelected(Mosque mosque);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Mosque mosque;
        private MosqueAdapter adapter;

        @BindView(R.id.tv_name)
        protected TextView name;

        @BindView(R.id.tv_address)
        protected TextView address;

        @BindView(R.id.tv_distance)
        protected TextView distance;

        ViewHolder(View itemView, MosqueAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        @OnClick(R.id.list_item)
        protected void onMosqueSelected() {
            if (adapter != null) {
                adapter.onMosqueSelected(mosque);
            }
        }
    }
}
