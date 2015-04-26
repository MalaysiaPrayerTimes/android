package com.i906.mpt.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.model.Mosque;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MosqueAdapter extends RecyclerView.Adapter<MosqueAdapter.ViewHolder> {

    protected List<Mosque> mList;
    protected MosqueListener mListener;

    public MosqueAdapter() {
        mList = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_mosque, parent, false);
        return new ViewHolder(view);
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
        holder.listener = mListener;
    }

    public void setMosqueList(List<Mosque> list) {
        mList = list;
        Collections.sort(mList);
        notifyDataSetChanged();
    }

    public void setOnMosqueSelectedListener(MosqueListener listener) {
        mListener = listener;
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

    public interface MosqueListener {
        void onMosqueSelected(Mosque mosque);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected Mosque mosque;
        protected MosqueListener listener;

        @InjectView(R.id.tv_name)
        protected TextView name;

        @InjectView(R.id.tv_address)
        protected TextView address;

        @InjectView(R.id.tv_distance)
        protected TextView distance;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @OnClick(R.id.list_item)
        protected void onMosqueSelected() {
            if (listener != null) {
                listener.onMosqueSelected(mosque);
            }
        }
    }
}
