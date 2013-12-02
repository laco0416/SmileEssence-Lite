package net.miz_hi.smileessence.command.main;

import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.R;
import net.miz_hi.smileessence.command.MenuCommand;
import net.miz_hi.smileessence.system.PostSystem;

public class CommandReport extends MenuCommand
{

    public CommandReport()
    {
    }

    @Override
    public String getName()
    {
        return "作者にレポートを送る";
    }

    @Override
    public void workOnUiThread()
    {
        PostSystem.setText(" #SmileEssence @laco0416 [" + Client.getString(R.string.app_version) + "]");
        PostSystem.setCursor(0);
        PostSystem.openPostPage();
    }

}
