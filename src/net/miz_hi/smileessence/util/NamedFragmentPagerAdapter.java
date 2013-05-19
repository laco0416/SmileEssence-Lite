package net.miz_hi.smileessence.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.miz_hi.smileessence.view.IRemovable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

public class NamedFragmentPagerAdapter extends FragmentStatePagerAdapter
{

	private ArrayList<NamedFragment> pageList = new ArrayList<NamedFragment>();
		
	public NamedFragmentPagerAdapter(FragmentManager fm, Collection<NamedFragment> fragments)
	{
		super(fm);
		pageList.addAll(fragments);
	}
	
	public NamedFragmentPagerAdapter(FragmentManager fm)
	{
		super(fm);
	}
	
	@Override
	public synchronized CharSequence getPageTitle(int position)
	{
		return pageList.get(position).getTitle();
	}

	@Override
	public synchronized int getCount()
	{
		return pageList.size();
	}
	
	public List<NamedFragment> getList()
	{
		return pageList;
	}
	
	public synchronized void add(NamedFragment element)
	{
		pageList.add(element);
	}
	
	public synchronized void set(NamedFragment element, int index)
	{
		pageList.add(index, element);
	}
	
	public synchronized void remove(NamedFragment element)
	{
		pageList.remove(element);
		if(element instanceof IRemovable)
		{
			((IRemovable)element).onRemoved();
		}
	}
	
	public synchronized void remove(int i)
	{
		NamedFragment fragment = pageList.get(i);
		remove(fragment);
	}

	@Override
	public Fragment getItem(int position)
	{
		return pageList.get(position);
	}

	@Override
	public int getItemPosition(Object object) 
	{
		int index = pageList.indexOf(object);
		return index != -1 ? index : POSITION_NONE;
	}

}