package net.miz_hi.smileessence.menu;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import net.miz_hi.smileessence.R;
import net.miz_hi.smileessence.command.CommandAddTemplate;
import net.miz_hi.smileessence.command.CommandOpenUrl;
import net.miz_hi.smileessence.command.ICommand;
import net.miz_hi.smileessence.command.post.CommandAppendHashtag;
import net.miz_hi.smileessence.command.status.impl.*;
import net.miz_hi.smileessence.command.user.UserCommandAddReply;
import net.miz_hi.smileessence.command.user.UserCommandIntroduce;
import net.miz_hi.smileessence.command.user.UserCommandReply;
import net.miz_hi.smileessence.command.user.UserCommandUserMenu;
import net.miz_hi.smileessence.dialog.ExpandMenuDialog;
import net.miz_hi.smileessence.model.status.tweet.TweetModel;
import net.miz_hi.smileessence.status.StatusViewFactory;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TweetMenu extends ExpandMenuDialog
{

    private TweetModel status;

    public TweetMenu(Activity activity, TweetModel model)
    {
        super(activity);
        this.status = model;
        setTitle(getHeaderView());
    }

    private View getHeaderView()
    {
        View header = StatusViewFactory.newInstance(inflater, null).getStatusView(status);

        View commands = header.findViewById(R.id.status_commands);
        commands.setVisibility(View.VISIBLE);
        ImageButton reply = (ImageButton) header.findViewById(R.id.status_reply);
        ImageButton retweet = (ImageButton) header.findViewById(R.id.status_retweet);
        ImageButton favorite = (ImageButton) header.findViewById(R.id.status_favorite);
        ImageButton delete = (ImageButton) header.findViewById(R.id.status_delete);

        final StatusCommandRetweet commandRetweet = new StatusCommandRetweet(status);

        //init
        if (!commandRetweet.getDefaultVisibility())
        {
            retweet.setVisibility(View.GONE);
        }
        if (!status.user.isMe())
        {
            delete.setVisibility(View.GONE);
        }
        //on/off
        if (status.isFavorited())
        {
            favorite.setImageResource(R.drawable.icon_favorite_on);
            favorite.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    new StatusCommandUnfavorite(status).run();
                    dispose();
                }
            });
        }
        else
        {
            favorite.setImageResource(R.drawable.icon_favorite_off);
            favorite.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    new StatusCommandFavorite(status).run();
                    dispose();
                }
            });
        }

        retweet.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                commandRetweet.run();
                dispose();
            }
        });

        reply.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                new StatusCommandReply(status).run();
                dispose();
            }
        });

        delete.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new StatusCommandDelete(status).run();
                dispose();
            }
        });

        return header;
    }

    public List<ICommand> getStatusMenu()
    {
        List<ICommand> list = new ArrayList<ICommand>();
        list.add(new StatusCommandReplyToAll(status));
        list.add(new StatusCommandFavAndRetweet(status));
        list.add(new StatusCommandChaseTalk(activity, status));
        list.add(new StatusCommandCopy(status));
        list.add(new StatusCommandTofuBuster(activity, status));
        list.add(new StatusCommandUnOffRetweet(status));
        list.add(new StatusCommandWarotaRT(status));
        list.add(new StatusCommandMakeAnonymous(status));
        list.add(new StatusCommandNanigaja(status));
        list.add(new StatusCommandCongrats(status));
        list.add(new UserCommandIntroduce(status.getOriginal().user.screenName));
        list.add(new StatusCommandReview(activity, status));
        list.add(new StatusCommandProduce(status));
        list.add(new StatusCommandTranslate(activity, status));
        list.add(new CommandAddTemplate(status.getText()));
        list.add(new StatusCommandClipboard(status));
        list.add(new StatusCommandShare(status, activity));
        list.add(new StatusCommandOpenUrl(status, activity));
        return list;
    }

    private List<ICommand> getURLMenu()
    {
        List<ICommand> list = new ArrayList<ICommand>();
        if (status.getUrls() != null)
        {
            for (URLEntity urlEntity : status.getUrls())
            {
                String url = urlEntity.getExpandedURL();
                if (url != null)
                {
                    list.add(new CommandOpenUrl(activity, url));
                }
            }
        }
        if (status.getMedias() != null)
        {
            for (MediaEntity mediaEntity : status.getMedias())
            {
                String url = mediaEntity.getMediaURL();
                if (url != null)
                {
                    list.add(new CommandOpenUrl(activity, url));
                }
            }
        }
        return list;
    }

    private List<ICommand> getHashtagMenu()
    {
        List<ICommand> list = new ArrayList<ICommand>();
        if (status.getHashtags() != null)
        {
            for (HashtagEntity hashtag : status.getHashtags())
            {
                list.add(new CommandAppendHashtag(hashtag.getText()));
            }
        }
        return list;
    }

    private List<String> getUsersList()
    {
        List<String> list = new ArrayList<String>();
        list.add(status.user.screenName);
        if (status.getUserMentions() != null)
        {
            for (UserMentionEntity e : status.getUserMentions())
            {
                if (!list.contains(e.getScreenName()))
                {
                    list.add(e.getScreenName());
                }
            }
        }
        if (!list.contains(status.getOriginal().user.screenName))
        {
            list.add(status.getOriginal().user.screenName);
        }
        return list;
    }

    private Map<String, List<ICommand>> getUserMenu(List<String> userList)
    {
        Map<String, List<ICommand>> map = new HashMap<String, List<ICommand>>();
        for (String userName : userList)
        {
            ArrayList<ICommand> list = new ArrayList<ICommand>();
            list.add(new UserCommandReply(userName));
            list.add(new UserCommandAddReply(userName));
            list.add(new UserCommandUserMenu(userName, activity));
            map.put(userName, list);
        }
        return map;
    }

    @Override
    public List<MenuElement> getElements()
    {
        List<MenuElement> list = new ArrayList<MenuElement>();
        List<ICommand> url = getURLMenu();
        if (!url.isEmpty())
        {
            for (ICommand iCommand : url)
            {
                list.add(new MenuElement(iCommand));
            }
        }

        MenuElement command = new MenuElement("コマンド");
        List<ICommand> commands = getStatusMenu();
        for (ICommand iCommand : commands)
        {
            command.addChild(new MenuElement(iCommand));
        }
        list.add(command);

        for (String name : getUsersList())
        {
            MenuElement user = new MenuElement("@" + name);
            List<ICommand> userMenu = getUserMenu(getUsersList()).get(name);
            for (ICommand iCommand : userMenu)
            {
                user.addChild(new MenuElement(iCommand));
            }
            list.add(user);
        }

        MenuElement hashtag = new MenuElement("ハッシュタグ");
        List<ICommand> hashtags = getHashtagMenu();
        if (!hashtags.isEmpty())
        {
            for (ICommand iCommand : hashtags)
            {
                hashtag.addChild(new MenuElement(iCommand));
            }
            list.add(hashtag);
        }

        return list;
    }
}
