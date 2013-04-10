package net.miz_hi.smileessence.menu;

import java.util.ArrayList;
import java.util.List;

import net.miz_hi.smileessence.command.CommandAppendHashtag;
import net.miz_hi.smileessence.command.CommandInsertText;
import net.miz_hi.smileessence.command.CommandMenuParent;
import net.miz_hi.smileessence.command.CommandParseMorse;
import net.miz_hi.smileessence.command.MenuCommand;
import net.miz_hi.smileessence.data.StatusStore;
import net.miz_hi.smileessence.data.template.Template;
import net.miz_hi.smileessence.data.template.Templates;
import net.miz_hi.smileessence.dialog.DialogAdapter;
import android.app.Activity;
import android.app.Dialog;

public class TweetMenu extends DialogAdapter
{

	private static TweetMenu instance;

	private TweetMenu(Activity activity)
	{
		super(activity);
	}
	
	public static void show()
	{
		instance.createMenuDialog(true).show();
	}

	@Override
	public Dialog createMenuDialog(boolean init)
	{
		if (init)
		{
			list.clear();
			list.add(new CommandInsertText("ワロタｗ"));
			list.add(new CommandParseMorse());
			if(!getTemplateMenu().isEmpty())
			{
				list.add(new CommandMenuParent(this, "定型文", getTemplateMenu()));
			}
			if(!getHashtagMenu().isEmpty())
			{
				list.add(new CommandMenuParent(this, "最近見たハッシュタグ", getHashtagMenu()));
			}
			setTitle("ツイートメニュー");
		}

		return super.createMenuDialog();
	}
	
	private List<MenuCommand> getHashtagMenu()
	{
		List<MenuCommand> list = new ArrayList<MenuCommand>();
		for(String hashtag : StatusStore.getHashtagList())
		{
			list.add(new CommandAppendHashtag(hashtag));
		}
		return list;
	}
	
	private List<MenuCommand> getTemplateMenu()
	{
		List<MenuCommand> list = new ArrayList<MenuCommand>();
		for(Template template : Templates.getTemplates())
		{
			list.add(new CommandInsertText(template.getText()));
		}
		return list;
	}
	
	public static void init(Activity activity)
	{
		instance = new TweetMenu(activity);
	}
	
	public static TweetMenu getInstance()
	{
		return instance;
	}

}
