package net.miz_hi.smileessence.event;

import net.miz_hi.smileessence.core.UiHandler;
import net.miz_hi.smileessence.util.CountUpInteger;
import net.miz_hi.smileessence.view.MainActivity;
import twitter4j.User;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class ToastManager
{

	private Activity activity;
	private Toast toast;
	private View viewToastBase;
	private User enemyUser;
	private long lastUserId = -1;
	private long lastStatusId = -1;
	private CountUpInteger counterSourceUser = new CountUpInteger(5);
	private CountUpInteger counterTargetStatus = new CountUpInteger(5);

	public ToastManager(Activity activity)
	{
		this.activity = activity;
	}

	public void noticeEvent(final EventModel model)
	{
		if (lastUserId == model.source.getId())
		{
			if (counterSourceUser.isOver())
			{
				return;
			}
			else if (counterSourceUser.countUp())
			{
				new UiHandler()
				{
					@Override
					public void run()
					{
						toast.cancel();
						Toast.makeText(activity, enemyUser.getScreenName() + "に爆撃を受けています", Toast.LENGTH_LONG).show();
					}
				}.post();
				return;
			}
		}
		else
		{
			lastUserId = model.source.getId();
			counterSourceUser.reset();
			enemyUser = model.source;
		}
		
		new UiHandler()
		{
			@Override
			public void run()
			{
				if (toast == null)
				{
					toast = new Toast(activity);
				}
				viewToastBase = EventViewFactory.getToastView(activity, model, viewToastBase);
				toast.setView(viewToastBase);
				toast.setGravity(Gravity.BOTTOM, 0, 80);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			}
		}.post();
	}
}
