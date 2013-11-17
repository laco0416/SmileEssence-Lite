package net.miz_hi.smileessence.view.fragment.impl;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import net.miz_hi.smileessence.R;
import net.miz_hi.smileessence.cache.MyImageCache;
import net.miz_hi.smileessence.core.MyExecutor;
import net.miz_hi.smileessence.menu.UserMenu;
import net.miz_hi.smileessence.model.status.user.UserModel;
import net.miz_hi.smileessence.task.impl.GetUserTask;
import net.miz_hi.smileessence.util.UiHandler;
import net.miz_hi.smileessence.view.IRemovable;
import net.miz_hi.smileessence.view.fragment.NamedFragment;

@SuppressLint("ValidFragment")
public class UserInfoFragment extends NamedFragment implements OnClickListener, IRemovable
{

    UserModel user;
    TextView screennameView;
    TextView nameView;
    TextView homepageView;
    TextView locateView;
    TextView isFollowingView;
    TextView isFollowedView;
    TextView isProtectedView;
    TextView descriptionView;
    TextView tweetcountView;
    TextView followingView;
    TextView followedView;
    TextView favoriteView;
    NetworkImageView iconView;

    private UserInfoFragment()
    {
    }

    public static UserInfoFragment newInstance(UserModel user)
    {
        UserInfoFragment fragment = new UserInfoFragment();
        fragment.user = user;
        return fragment;
    }

    @Override
    public String getTitle()
    {
        return user.screenName + "'s Profile";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View page = inflater.inflate(R.layout.userinfo_layout, container, false);
        Button reload = (Button) page.findViewById(R.id.user_reload);
        reload.setOnClickListener(this);
        Button menu = (Button) page.findViewById(R.id.user_menu);
        menu.setOnClickListener(this);

        screennameView = (TextView) page.findViewById(R.id.user_screenname);
        nameView = (TextView) page.findViewById(R.id.user_name);
        homepageView = (TextView) page.findViewById(R.id.user_homepage);
        locateView = (TextView) page.findViewById(R.id.user_locate);
        isFollowingView = (TextView) page.findViewById(R.id.user_isfollowing);
        isFollowedView = (TextView) page.findViewById(R.id.user_isfollowed);
        isProtectedView = (TextView) page.findViewById(R.id.user_isprotected);
        descriptionView = (TextView) page.findViewById(R.id.user_bio);
        tweetcountView = (TextView) page.findViewById(R.id.user_count_tweet);
        followingView = (TextView) page.findViewById(R.id.user_count_following);
        followedView = (TextView) page.findViewById(R.id.user_count_followed);
        favoriteView = (TextView) page.findViewById(R.id.user_count_favorite);
        iconView = (NetworkImageView) page.findViewById(R.id.user_icon);
        reload(false);
        return page;
    }

    private void reload(final boolean force)
    {
        new UiHandler()
        {

            @Override
            public void run()
            {
                screennameView.setText(user.screenName);
                nameView.setText(user.name);
                if (TextUtils.isEmpty(user.homePageUrl))
                {
                    homepageView.setVisibility(View.GONE);
                }
                else
                {
                    homepageView.setText(user.homePageUrl);
                }
                if (TextUtils.isEmpty(user.location))
                {
                    locateView.setVisibility(View.GONE);
                }
                else
                {
                    locateView.setText(user.location);
                }
                isFollowingView.setText(user.isFriend(force) ? "フォローしています" : user.isMe() ? "あなたです" : "フォローしていません");
                isFollowedView.setText(user.isFollower(force) ? "フォローされています" : user.isMe() ? "あなたです" : "フォローされていません");
                isProtectedView.setText(user.isProtected ? "非公開" : "公開");
                descriptionView.setText(user.description);
                tweetcountView.setText(Integer.toString(user.statusCount));
                followingView.setText(Integer.toString(user.friendCount));
                followedView.setText(Integer.toString(user.followerCount));
                favoriteView.setText(Integer.toString(user.favoriteCount));
                MyImageCache.setImageToView(user.iconUrl, iconView);
            }
        }.post();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.user_reload:
            {
                final ProgressDialog pd = ProgressDialog.show(getActivity(), null, "情報を更新中...", true);
                MyExecutor.execute(new Runnable()
                {
                    public void run()
                    {
                        user.updateData(new GetUserTask(user.userId).call());
                        reload(true);
                        pd.dismiss();
                    }
                });
                break;
            }
            case R.id.user_menu:
            {
                new UserMenu(getActivity(), user).create().show();
                break;
            }
        }
    }

    @Override
    public void onRemoved()
    {
    }

}
