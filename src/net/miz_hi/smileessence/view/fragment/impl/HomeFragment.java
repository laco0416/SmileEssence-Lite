package net.miz_hi.smileessence.view.fragment.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import net.miz_hi.smileessence.R;
import net.miz_hi.smileessence.listener.TimelineRefreshListener;
import net.miz_hi.smileessence.listener.TimelineScrollListener;
import net.miz_hi.smileessence.statuslist.StatusListManager;
import net.miz_hi.smileessence.util.CustomListAdapter;
import net.miz_hi.smileessence.view.fragment.NamedFragment;

public class HomeFragment extends NamedFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout page = (LinearLayout) inflater.inflate(R.layout.listpage_refresh_layout, container, false);
        PullToRefreshListView listView = (PullToRefreshListView) page.findViewById(R.id.listpage_listview);
        CustomListAdapter<?> adapter = StatusListManager.getAdapter(StatusListManager.getHomeTimeline());
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new TimelineScrollListener(adapter));
        listView.setOnRefreshListener(new TimelineRefreshListener(StatusListManager.getHomeTimeline()));
        return page;
    }

    @Override
    public String getTitle()
    {
        return "Home";
    }

}
