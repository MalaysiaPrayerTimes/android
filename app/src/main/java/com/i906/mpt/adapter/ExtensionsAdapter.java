package com.i906.mpt.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.i906.mpt.MptApplication;
import com.i906.mpt.R;
import com.i906.mpt.extension.ExtensionInfo;
import com.i906.mpt.extension.ExtensionInfo.Screen;
import com.i906.mpt.util.preference.GeneralPrefs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExtensionsAdapter extends RecyclerView.Adapter<ExtensionsAdapter.ViewHolder> {

    @Inject
    protected GeneralPrefs mPrefs;

    protected List<ExtensionInfo> mList;
    protected List<ScreenHolder> mScreenList;

    protected String mSelectedScreen;
    private ExtensionListener mListener;

    public ExtensionsAdapter(Context context) {
        MptApplication.component(context).inject(this);
        mScreenList = new ArrayList<>();
        mSelectedScreen = mPrefs.getSelectedPrayerView();
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_extension, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScreenHolder sh = getItem(position);
        holder.setScreen(sh.screen);
        holder.setExtension(sh.extension);
        holder.selected.setChecked(mSelectedScreen.equals(sh.screen.getView()));
    }

    public void setExtensionList(List<ExtensionInfo> list) {
        mList = list;
        mScreenList = new ArrayList<>();

        for (ExtensionInfo ei : mList) {
            for (Screen s : ei.getScreens()) {

                if (s == null) continue;
                if (s.getView() == null) continue;

                ScreenHolder sh = new ScreenHolder();
                sh.extension = ei;
                sh.screen = s;
                mScreenList.add(sh);
            }
        }

        notifyDataSetChanged();
    }

    public ScreenHolder getItem(int position) {
        return mScreenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).screen.getView().hashCode();
    }

    @Override
    public int getItemCount() {
        return mScreenList.size();
    }

    public boolean isEmpty() {
        return mScreenList.isEmpty();
    }

    public void setListener(ExtensionListener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected ExtensionsAdapter adapter;
        protected ExtensionInfo extension;
        protected Screen screen;

        @Bind(R.id.rb_selected)
        protected RadioButton selected;

        @Bind(R.id.tv_name)
        protected TextView name;

        @Bind(R.id.tv_author)
        protected TextView author;

        @Bind(R.id.btn_more)
        protected ImageButton more;

        public ViewHolder(View itemView, ExtensionsAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
        }

        public void setExtension(ExtensionInfo item) {
            extension = item;
            author.setText(item.getAuthor());
        }

        public void setScreen(Screen item) {
            screen = item;
            name.setText(item.getName());

            if (screen.isNative() && !screen.hasSettings()) {
                more.setVisibility(View.GONE);
            } else {
                more.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.list_item)
        protected void onScreenSelected() {
            adapter.mSelectedScreen = screen.getView();
            adapter.notifyDataSetChanged();

            if (adapter.mListener != null) {
                adapter.mListener.onScreenSelected(screen);
            }
        }

        @OnClick(R.id.btn_more)
        protected void onMenuClicked() {
            PopupMenu menu = new PopupMenu(itemView.getContext(), more);
            more.setOnTouchListener(menu.getDragToOpenListener());

            menu.inflate(R.menu.view_extension_more);
            menu.setOnMenuItemClickListener(menuClickListener);

            MenuItem uninstall = menu.getMenu().findItem(R.id.action_uninstall);
            if (screen.isNative()) uninstall.setVisible(false);

            menu.show();
        }

        private PopupMenu.OnMenuItemClickListener menuClickListener = item -> {
            switch (item.getItemId()) {
                case R.id.action_uninstall:
                    if (adapter.mListener != null) adapter.mListener.onExtensionUninstall(extension);
                    return true;
            }
            return false;
        };
    }

    public interface ExtensionListener {
        void onScreenSelected(Screen screen);
        void onExtensionUninstall(ExtensionInfo extension);
    }

    static class ScreenHolder {
        public ExtensionInfo extension;
        public Screen screen;
    }
}
