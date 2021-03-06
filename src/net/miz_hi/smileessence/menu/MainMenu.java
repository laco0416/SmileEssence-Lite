package net.miz_hi.smileessence.menu;

import android.app.Activity;
import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.command.CommandEditExtraWord;
import net.miz_hi.smileessence.command.CommandEditMenu;
import net.miz_hi.smileessence.command.CommandEditTemplate;
import net.miz_hi.smileessence.command.CommandReConnect;
import net.miz_hi.smileessence.command.main.CommandCommercial;
import net.miz_hi.smileessence.command.main.CommandFinish;
import net.miz_hi.smileessence.command.main.CommandOpenSetting;
import net.miz_hi.smileessence.command.main.CommandReport;
import net.miz_hi.smileessence.command.user.UserCommandOpenAclog;
import net.miz_hi.smileessence.command.user.UserCommandOpenFavstar;
import net.miz_hi.smileessence.command.user.UserCommandOpenTwilog;
import net.miz_hi.smileessence.dialog.ExpandMenuDialog;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends ExpandMenuDialog
{

    public MainMenu(Activity activity)
    {
        super(activity);
        setTitle("メインメニュー");
    }

    @Override
    public List<MenuElement> getElements()
    {
        List<MenuElement> list = new ArrayList<MenuElement>();

        if (Client.getMainAccount() == null)
        {
            list.add(new MenuElement(new CommandOpenSetting(activity)));
            list.add(new MenuElement(new CommandFinish()));
        }
        else
        {
            MenuElement settingMenu = new MenuElement("設定");
            settingMenu.addChild(new MenuElement(new CommandOpenSetting(activity)));
            settingMenu.addChild(new MenuElement(new CommandEditTemplate(activity)));
            settingMenu.addChild(new MenuElement(new CommandEditExtraWord(activity)));
            settingMenu.addChild(new MenuElement(new CommandEditMenu(activity)));
            list.add(settingMenu);

            MenuElement serviceMenu = new MenuElement("外部サービス");
            serviceMenu.addChild(new MenuElement(new UserCommandOpenFavstar(Client.getMainAccount().getScreenName(), activity)));
            serviceMenu.addChild(new MenuElement(new UserCommandOpenAclog(Client.getMainAccount().getScreenName(), activity)));
            serviceMenu.addChild(new MenuElement(new UserCommandOpenTwilog(Client.getMainAccount().getScreenName(), activity)));
            list.add(serviceMenu);

            MenuElement otherMenu = new MenuElement("その他");
            otherMenu.addChild(new MenuElement(new CommandReConnect()));
            otherMenu.addChild(new MenuElement(new CommandCommercial()));
            otherMenu.addChild(new MenuElement(new CommandReport()));
            otherMenu.addChild(new MenuElement(new CommandFinish()));
            list.add(otherMenu);
        }
        return list;
    }
}
