package net.miz_hi.smileessence.task.impl;

import net.miz_hi.smileessence.auth.Account;
import net.miz_hi.smileessence.model.status.tweet.TweetModel;
import net.miz_hi.smileessence.notification.Notificator;
import net.miz_hi.smileessence.task.Task;
import net.miz_hi.smileessence.twitter.API;
import net.miz_hi.smileessence.twitter.ResponseConverter;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.LinkedList;
import java.util.List;

public class GetUserTimelineTask extends Task<List<TweetModel>>
{

    private Account account;
    private Paging page;
    private long userId;

    public GetUserTimelineTask(Account account, long userId, Paging page)
    {
        this.account = account;
        this.page = page;
        this.userId = userId;
    }

    @Override
    public List<TweetModel> call()
    {
        LinkedList<Status> resp = new LinkedList<Status>();

        try
        {
            resp.addAll(API.getUserTimeline(account, userId, page));
        }
        catch (TwitterException e)
        {
            e.printStackTrace();
        }

        LinkedList<TweetModel> list = new LinkedList<TweetModel>();

        while (!resp.isEmpty())
        {
            TweetModel model = ResponseConverter.convert(resp.pollLast());
            list.offerFirst(model);
        }

        return list;
    }

    @Override
    public void onPreExecute()
    {

    }

    @Override
    public void onPostExecute(List<TweetModel> result)
    {
        if (result.size() < 1)
        {
            Notificator.alert("タイムラインの取得に失敗しました");
        }
    }

}
