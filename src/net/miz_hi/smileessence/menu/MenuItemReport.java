package net.miz_hi.smileessence.menu;

import net.miz_hi.smileessence.dialog.DialogAdapter;
import net.miz_hi.smileessence.view.MainActivity;
import android.app.Activity;

public class MenuItemReport extends MenuItemBase
{

	public MenuItemReport(Activity activity, DialogAdapter adapter)
	{
		super(activity, adapter);
	}

	@Override
	public String getText()
	{
		return "作者へレポートを送る";
	}

	@Override
	public void work()
	{
		MainActivity.getInstance().openTweetViewToTweet("#SmileEssence @laco0416 ");
	}

}
