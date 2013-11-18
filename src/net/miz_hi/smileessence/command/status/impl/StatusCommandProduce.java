package net.miz_hi.smileessence.command.status.impl;

import android.text.TextUtils;
import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.command.IConfirmable;
import net.miz_hi.smileessence.command.IHideable;
import net.miz_hi.smileessence.command.status.StatusCommand;
import net.miz_hi.smileessence.model.status.tweet.TweetModel;
import net.miz_hi.smileessence.notification.Notificator;
import net.miz_hi.smileessence.preference.EnumPreferenceKey;
import net.miz_hi.smileessence.task.impl.RetweetTask;
import net.miz_hi.smileessence.task.impl.TweetTask;
import twitter4j.StatusUpdate;

import java.util.Calendar;
import java.util.Locale;


public class StatusCommandProduce extends StatusCommand implements IHideable, IConfirmable
{

    public StatusCommandProduce(TweetModel status)
    {
        super(status);
    }

    @Override
    public String getName()
    {
        return "今日のおもしろツイートです";
    }

    @Override
    public void workOnUiThread()
    {
        String lastProduce = Client.getPreferenceValue(EnumPreferenceKey.LAST_PRODUCE_DATE);
        Calendar today = Calendar.getInstance(Locale.getDefault());
        String todayStr = String.format("%02d%02d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DATE));
        System.out.println(todayStr);
        System.out.println(lastProduce);
        if (TextUtils.isEmpty(lastProduce) || !lastProduce.equals(todayStr))
        {
            Client.putPreferenceValue(EnumPreferenceKey.LAST_PRODUCE_DATE, todayStr);

            new TweetTask(new StatusUpdate("今日のおもしろツイートです"))
            {
                @Override
                public void onPostExecute(Boolean result)
                {
                    if (result)
                    {
                        try
                        {
                            Thread.sleep(100);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        status.favorite();
                        new RetweetTask(status.statusId)
                        {
                            @Override
                            public void onPostExecute(Boolean result)
                            {
                                if (result)
                                {
                                    try
                                    {
                                        Thread.sleep(100);
                                    }
                                    catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    new TweetTask(new StatusUpdate("以上です")).callAsync();

                                }
                                else
                                {
                                    new TweetTask(new StatusUpdate("紹介失敗しました")).callAsync();
                                }
                            }
                        }.callAsync();
                    }
                    else
                    {
                        Notificator.alert("失敗しました");
                    }
                }
            }.callAsync();
        }
        else
        {
            Notificator.alert("今日はすでに認定済みです");
        }
    }

    @Override
    public boolean getDefaultVisibility()
    {
        return !status.getOriginal().user.isProtected && !status.getOriginal().getUser().isMe();
    }
}
