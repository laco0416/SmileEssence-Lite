package net.miz_hi.smileessence.listener;

import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.R;
import net.miz_hi.smileessence.menu.StatusMenu;
import net.miz_hi.smileessence.status.StatusModel;
import net.miz_hi.smileessence.util.UiHandler;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class StatusOnClickListener implements OnClickListener
{
	private Activity activity;
	private StatusModel model;
	private static boolean wasTouched = false;

	public StatusOnClickListener(Activity activity, StatusModel model)
	{
		this.activity = activity;
		this.model = model;
	}

	@Override
	public void onClick(final View v)
	{
		v.setBackgroundColor(Client.getColor(R.color.MetroBlue));
		v.invalidate();
		new UiHandler()
		{

			@Override
			public void run()
			{
				v.setBackgroundColor(model.backgroundColor);
				new StatusMenu(activity, model).create().show();
			}
		}.postDelayed(20);
		
	}
	

}
