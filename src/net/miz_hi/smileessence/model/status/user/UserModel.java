package net.miz_hi.smileessence.model.status.user;

import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.model.status.IStatusModel;
import net.miz_hi.smileessence.preference.EnumPreferenceKey;
import net.miz_hi.smileessence.status.EnumNameStyle;
import net.miz_hi.smileessence.task.impl.GetUserTask;
import net.miz_hi.smileessence.util.StringUtils;
import twitter4j.User;

import java.util.Date;
import java.util.concurrent.Future;

public class UserModel implements IStatusModel
{

    public long userId;
    public String screenName;
    public String name;
    public String homePageUrl;
    public String location;
    public String description;
    public String iconUrl;
    public String biggerIconUrl;
    public String headerImageUrl;
    public int statusCount;
    public int friendCount;
    public int followerCount;
    public int favoriteCount;
    public Date createdAt;
    public boolean isProtected;


    private UserModel()
    {
    }

    public UserModel(User user)
    {
        userId = user.getId();
        updateData(user);
    }

    public UserModel updateData(User user)
    {
        screenName = user.getScreenName();
        name = user.getName();
        homePageUrl = user.getURLEntity().getExpandedURL();
        location = user.getLocation();
        description = StringUtils.replaceUrlEntity(user.getDescription(), user.getDescriptionURLEntities());
        iconUrl = user.getProfileImageURL();
        biggerIconUrl = user.getBiggerProfileImageURL();
        headerImageUrl = user.getProfileBannerURL();
        statusCount = user.getStatusesCount();
        friendCount = user.getFriendsCount();
        followerCount = user.getFollowersCount();
        favoriteCount = user.getFavouritesCount();
        createdAt = user.getCreatedAt();
        isProtected = user.isProtected();
        //        ImageCache.preCache(iconUrl);
        return this;
    }

    public User getRawUser()
    {
        Future<User> resp = new GetUserTask(userId).callAsync();
        try
        {
            return resp.get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isMe()
    {
        return userId == Client.getMainAccount().getUserId();
    }

    public static UserModel getNullUserModel()
    {
        UserModel user = new UserModel();
        user.screenName = "";
        user.name = "";
        user.homePageUrl = "";
        user.location = "";
        user.description = "";
        user.iconUrl = "";
        user.headerImageUrl = "";
        user.statusCount = 0;
        user.friendCount = 0;
        user.followerCount = 0;
        user.favoriteCount = 0;
        user.createdAt = new Date();
        user.isProtected = false;
        return user;
    }

    @Override
    public UserModel getUser()
    {
        return this;
    }

    @Override
    public String getTextTop()
    {
        StringBuilder builder = new StringBuilder();
        String style = Client.getPreferenceValue(EnumPreferenceKey.NAME_STYLE);
        if (style.equals(EnumNameStyle.S_N.get()) || style.equals(EnumNameStyle.S.get()))
        {
            builder.append(screenName);
        }
        else if (style.equals(EnumNameStyle.N_S.get()) || style.equals(EnumNameStyle.N.get()))
        {
            builder.append(name);
        }
        if (style.equals(EnumNameStyle.S_N.get()))
        {
            builder.append(" / ");
            builder.append(name);
        }
        else if (style.equals(EnumNameStyle.N_S.get()))
        {
            builder.append(" / ");
            builder.append(screenName);
        }
        return builder.toString();
    }

    @Override
    public String getTextContent()
    {
        if (description.length() > 100)
        {
            return description.substring(0, 100) + "...";
        }
        else
        {
            return description;
        }
    }

    @Override
    public String getTextBottom()
    {
        return location;
    }
}
