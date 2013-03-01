package net.miz_hi.smileessence.data;

import java.util.Collection;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.R;
import net.miz_hi.smileessence.core.CustomListAdapter;
import net.miz_hi.smileessence.listener.TemplateOnClickListener;

public class TemplateListAdapter extends CustomListAdapter<Template>
{

	public TemplateListAdapter(Activity activity)
	{
		super(activity, Integer.MAX_VALUE);
	}
	
	@Override
	public View getView(int position, View convertedView, ViewGroup parent)
	{
		if(convertedView == null)
		{
			convertedView = getInflater().inflate(R.layout.menuitem_layout, null);
		}		
		
		convertedView.setBackgroundColor(Client.getColor(R.color.LightGray));
		
		Template template = (Template)getItem(position);
		
		TextView viewText = (TextView)convertedView.findViewById(R.id.textView_menuItem);		
		viewText.setText(template.getText());
		viewText.setTextColor(Client.getColor(R.color.Black));
		
		TemplateOnClickListener listener = new TemplateOnClickListener(this, getActivity(), template);
		
		convertedView.setOnClickListener(listener);
		convertedView.setOnLongClickListener(listener);
		
		return convertedView;
	}
}
