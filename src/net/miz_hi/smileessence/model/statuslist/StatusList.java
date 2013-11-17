package net.miz_hi.smileessence.model.statuslist;

import net.miz_hi.smileessence.model.status.IStatusModel;
import net.miz_hi.smileessence.statuslist.StatusListManager;

import java.util.ArrayList;


public abstract class StatusList
{

    private final ArrayList<IStatusModel> list = new ArrayList<IStatusModel>();

    public synchronized void addToTop(IStatusModel status)
    {
        if (!list.contains(status))
        {
            list.add(0, status);
        }
    }

    public synchronized void addToBottom(IStatusModel status)
    {
        if (!list.contains(status))
        {
            list.add(list.size(), status);
        }
    }

    public synchronized IStatusModel getStatus(int index)
    {
        return list.get(index);
    }

    public synchronized int getStatusIndex(IStatusModel status)
    {
        return list.indexOf(status);
    }

    public synchronized void remove(int index)
    {
        list.remove(index);
    }

    public synchronized void remove(IStatusModel status)
    {
        list.remove(status);
    }

    public synchronized IStatusModel[] getStatusList()
    {
        return list.toArray(new IStatusModel[list.size()]);
    }

    public synchronized void apply()
    {
        StatusListManager.getAdapter(this).notifyAdapter();
    }

    public synchronized void applyForce()
    {
        StatusListManager.getAdapter(this).forceNotifyAdapter();
    }

    public abstract boolean checkStatus(IStatusModel status);

}
