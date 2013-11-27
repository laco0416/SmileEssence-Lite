package net.miz_hi.smileessence.talkchase;

import net.miz_hi.smileessence.cache.TweetCache;
import net.miz_hi.smileessence.model.status.tweet.TweetModel;
import net.miz_hi.smileessence.model.statuslist.StatusList;
import net.miz_hi.smileessence.model.statuslist.impl.TalkList;
import net.miz_hi.smileessence.notification.Notificator;
import net.miz_hi.smileessence.status.TweetUtils;
import net.miz_hi.smileessence.statuslist.StatusListManager;
import net.miz_hi.smileessence.view.fragment.impl.TalkFragment;

public class TalkChaser
{

    public TalkFragment fragment;
    public long chasingId;
    public StatusList talkList;

    public TalkChaser(TalkFragment fragment)
    {
        this.talkList = new TalkList();
        this.fragment = fragment;
    }

    public void hitNewTweet(TweetModel tweet)
    {
        this.talkList.addToTop(tweet.getOriginal());
        updateChasingId(tweet.getOriginal().statusId);
    }

    private void updateChasingId(long id)
    {
        this.chasingId = id;
        fragment.setChasingId(chasingId);
    }

    /**
     * Call on Network Thread
     */
    public void startRelation(long statusId)
    {
        TweetModel start = TweetUtils.getOrCreateStatusModel(statusId);
        if (start == null)
        {
            Notificator.alert("ツイートの取得に失敗しました");
            return;
        }
        talkList.addToTop(start);

        //Load older
        long inReplyTo = start.getInReplyToStatusId();
        while (inReplyTo > 0)
        {
            TweetModel older = TweetUtils.getOrCreateStatusModel(inReplyTo);
            if (older == null)
            {
                Notificator.alert("ツイートの取得に失敗しました");
                break;
            }
            talkList.addToBottom(older);
            inReplyTo = older.getInReplyToStatusId();
        }

        //Load newer
        inReplyTo = start.statusId;
        while (true)
        {
            long before = inReplyTo;
            for (TweetModel newer : TweetCache.getList())
            {
                if (newer.getInReplyToStatusId() == inReplyTo)
                {
                    talkList.addToTop(newer);
                    inReplyTo = newer.statusId;
                    break;
                }
            }
            if (before == inReplyTo)
            {
                break;
            }
        }

        updateChasingId(((TweetModel) talkList.getStatus(0)).statusId);
        StatusListManager.getAdapter(talkList).forceNotifyAdapter();
    }

    public void stopRelation()
    {
        TalkManager.removeTalkChaser(this);
    }
}
