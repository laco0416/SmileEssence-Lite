package net.miz_hi.smileessence.system;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.auth.Account;
import net.miz_hi.smileessence.auth.AuthenticationDB;
import net.miz_hi.smileessence.auth.AuthorizeHelper;
import net.miz_hi.smileessence.auth.Consumers;
import net.miz_hi.smileessence.cache.ImageCache;
import net.miz_hi.smileessence.cache.TweetCache;
import net.miz_hi.smileessence.cache.UserCache;
import net.miz_hi.smileessence.core.EnumRequestCode;
import net.miz_hi.smileessence.core.MyExecutor;
import net.miz_hi.smileessence.data.list.ListManager;
import net.miz_hi.smileessence.data.search.Search;
import net.miz_hi.smileessence.data.search.SearchManager;
import net.miz_hi.smileessence.dialog.SingleButtonDialog;
import net.miz_hi.smileessence.model.statuslist.timeline.Timeline;
import net.miz_hi.smileessence.model.statuslist.timeline.impl.ListTimeline;
import net.miz_hi.smileessence.model.statuslist.timeline.impl.SearchTimeline;
import net.miz_hi.smileessence.notification.Notificator;
import net.miz_hi.smileessence.preference.EnumPreferenceKey;
import net.miz_hi.smileessence.statuslist.StatusListAdapter;
import net.miz_hi.smileessence.statuslist.StatusListManager;
import net.miz_hi.smileessence.task.impl.GetUserTask;
import net.miz_hi.smileessence.twitter.ResponseConverter;
import net.miz_hi.smileessence.twitter.TwitterManager;
import net.miz_hi.smileessence.view.fragment.impl.ListFragment;
import net.miz_hi.smileessence.view.fragment.impl.SearchFragment;
import twitter4j.User;

public class MainActivitySystem
{

    public AuthorizeHelper authHelper;
    public Uri tempFilePath;

    public void onDestroyed()
    {
        TwitterManager.closeTwitterStream();
        PostSystem.clear(false);
        ImageCache.clearCache();
        TweetCache.clearCache();
        UserCache.clearCache();
        MyExecutor.shutdown();
    }

    public boolean checkAccount(Activity activity)
    {
        if(Client.hasAuthorizedAccount())
        {
            accountSetup();
            return true;
        }
        else
        {
            authHelper = new AuthorizeHelper(activity, Consumers.getDefault());
            //NOT AUTHORIZED
            SingleButtonDialog.show(activity, "認証してください", "認証ページヘ", new Runnable()
            {

                @Override
                public void run()
                {
                    authHelper.oauthSend();
                }
            });
            return false;
        }
    }

    public void authorize(Activity activity, Uri data)
    {
        Account account = authHelper.oauthReceive(data);
        Client.setMainAccount(account);
        if(checkAccount(activity))
        {
            startTwitter(activity);
            loadListPage(activity);
        }
    }

    public void accountSetup()
    {
        long lastUsedId = Client.getPreferenceValue(EnumPreferenceKey.LAST_USED_USER_ID);

        for(Account account : AuthenticationDB.instance().findAll())
        {
            if(account.getUserId() == lastUsedId)
            {
                Client.setMainAccount(account);
                break;
            }
        }
        if(Client.getMainAccount() == null)
        {
            Client.setMainAccount(AuthenticationDB.instance().findAll().get(0));
        }
    }

    public void loadListPage(Activity activity)
    {
        for(net.miz_hi.smileessence.data.list.List list : ListManager.getLists())
        {
            Timeline timeline = new ListTimeline(list.getListId());
            StatusListManager.registerListTimeline(list.getListId(), timeline, new StatusListAdapter(activity, timeline));
            timeline.loadNewer();
            ListFragment fragment = ListFragment.newInstance(list.getListId(), list.getName());
            PageController.getInstance().addPage(fragment);
        }
    }

    public void loadSearchPage(Activity activity)
    {
        for(Search search : SearchManager.getSearches())
        {
            SearchTimeline timeline = new SearchTimeline(search.getQuery());
            StatusListManager.registerSearchTimeline(search.getId(), timeline, new StatusListAdapter(activity, timeline));
            timeline.loadNewer();
            SearchFragment fragment = SearchFragment.getInstance(search.getId(), search.getQuery());
            PageController.getInstance().addPage(fragment);
        }
    }

    public void startTwitter(Activity activity)
    {

        boolean connected = TwitterManager.openTwitterStream(activity);

        if(connected)
        {
            try
            {
                new GetUserTask(Client.getMainAccount().getUserId())
                {
                    @Override
                    public void onPostExecute(User result)
                    {
                        if(result != null)
                        {
                            ResponseConverter.convert(result);
                        }
                    }
                }.callAsync().get();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Notificator.alert("接続出来ません");
                return;
            }

            StatusListManager.getHomeTimeline().loadOlder();

            StatusListManager.getMentionsTimeline().loadOlder();
        }
        else
        {
            Notificator.alert("接続出来ません");
        }
    }

    public void receivePicture(Activity activity, Intent data, int reqCode)
    {
        try
        {
            Uri uri;
            if(reqCode == EnumRequestCode.PICTURE.ordinal())
            {
                uri = data.getData();
            }
            else
            {
                uri = tempFilePath;
            }
            Cursor c = activity.getContentResolver().query(uri, null, null, null, null);
            c.moveToFirst();
            String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
            PostSystem.setPicturePath(path);
            PostSystem.openPostPage();
            Notificator.info("画像をセットしました");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Notificator.alert("失敗しました");
        }
    }
}