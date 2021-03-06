package net.miz_hi.smileessence.view.fragment.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.ArrowKeyMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.toolbox.NetworkImageView;
import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.R;
import net.miz_hi.smileessence.cache.ImageCache;
import net.miz_hi.smileessence.cache.UserCache;
import net.miz_hi.smileessence.dialog.ConfirmDialog;
import net.miz_hi.smileessence.dialog.SelectPictureDialog;
import net.miz_hi.smileessence.listener.PostEditTextListener;
import net.miz_hi.smileessence.menu.MainMenu;
import net.miz_hi.smileessence.menu.PostingMenu;
import net.miz_hi.smileessence.model.status.tweet.TweetModel;
import net.miz_hi.smileessence.model.status.user.UserModel;
import net.miz_hi.smileessence.notification.Notificator;
import net.miz_hi.smileessence.preference.EnumPreferenceKey;
import net.miz_hi.smileessence.status.StatusViewFactory;
import net.miz_hi.smileessence.status.TweetUtils;
import net.miz_hi.smileessence.system.PostSystem;
import net.miz_hi.smileessence.system.PostSystem.PostPageState;
import net.miz_hi.smileessence.task.Task;
import net.miz_hi.smileessence.task.impl.GetUserTask;
import net.miz_hi.smileessence.theme.IColorTheme;
import net.miz_hi.smileessence.twitter.ResponseConverter;
import net.miz_hi.smileessence.util.UiHandler;
import net.miz_hi.smileessence.view.activity.MainActivity;
import net.miz_hi.smileessence.view.fragment.NamedFragment;
import twitter4j.User;

@SuppressLint("ValidFragment")
public class PostFragment extends NamedFragment implements OnClickListener
{

    TextView textCount;
    EditText editText;
    FrameLayout frameInReplyTo;
    ImageView imagePict;
    NetworkImageView iconView;
    TextView screenNameView;
    boolean inited = false;

    @Override
    public String getTitle()
    {
        return "Post";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        IColorTheme theme = Client.getSettings().getTheme();
        View page = inflater.inflate(R.layout.post_layout, container, false);
        page.setBackgroundColor(getResources().getColor(theme.getBackground1()));
        editText = (EditText) page.findViewById(R.id.editText_tweet);
        frameInReplyTo = (FrameLayout) page.findViewById(R.id.frame_inreplyto);
        imagePict = (ImageView) page.findViewById(R.id.image_pict);
        textCount = (TextView) page.findViewById(R.id.textView_count);
        textCount.setTextColor(getResources().getColor(theme.getNormalTextColor()));
        Button imageButtonSubmit = (Button) page.findViewById(R.id.imBtn_tweet);
        ImageButton imageButtonDelete = (ImageButton) page.findViewById(R.id.post_delete);
        ImageButton imageButtonMenu = (ImageButton) page.findViewById(R.id.post_menu);
        ImageButton imageButtonPict = (ImageButton) page.findViewById(R.id.post_pickpict);
        ImageButton config = (ImageButton) page.findViewById(R.id.post_config);
        config.setImageResource(theme.getConfigIcon());
        config.setOnClickListener(this);
        iconView = (NetworkImageView) page.findViewById(R.id.post_myIcon);
        screenNameView = (TextView) page.findViewById(R.id.post_myName);
        screenNameView.setTextColor(getResources().getColor(theme.getNormalTextColor()));

        PostEditTextListener listener = new PostEditTextListener(textCount);
        editText.setTextSize(Client.getSettings().getTextSize() + 3);
        editText.addTextChangedListener(listener);
        editText.setOnFocusChangeListener(listener);
        editText.setMovementMethod(new ArrowKeyMovementMethod()
        {
            @Override
            protected boolean right(TextView widget, Spannable buffer)
            {
                //Homeタブに戻るのを阻止
                return widget.getSelectionEnd() == widget.length() || super.right(widget, buffer);
            }
        });
        if(Client.<Boolean>getPreferenceValue(EnumPreferenceKey.OPEN_IME))
        {
            editText.requestFocus();
        }

        imageButtonSubmit.setOnClickListener(this);
        imageButtonDelete.setImageResource(theme.getDeleteIcon());
        imageButtonDelete.setOnClickListener(this);
        imageButtonMenu.setImageResource(theme.getMenuIcon());
        imageButtonMenu.setOnClickListener(this);
        imageButtonPict.setImageResource(theme.getPictureIcon());
        imageButtonPict.setOnClickListener(this);
        imagePict.setOnClickListener(this);

        loadState();
        if(Client.getMainAccount() != null)
        {
            initMyInformation();
        }

        return page;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        saveState();
    }

    @Override
    public void onSelected()
    {
        loadState();
        openIme();
    }

    @Override
    public void onDeselect()
    {
        saveState();
        hideIme();
    }

    public PostPageState getState()
    {
        return PostSystem.getState();
    }

    public void loadState()
    {
        PostPageState state = getState();
        if(editText != null)
        {
            String text = state.getText();
            int selectionStart = state.getSelectionStart();
            int selectionEnd = state.getSelectionEnd();
            setText(text);
            setSelection(selectionStart, selectionEnd);
        }
        if(frameInReplyTo != null)
        {
            long inReplyTo = state.getInReplyToStatusId();
            setInReplyTo(inReplyTo);
        }
        if(imagePict != null)
        {
            String picturePath = state.getPicturePath();
            setPicture(picturePath);
        }
        if(!inited && iconView != null && Client.getMainAccount() != null)
        {
            initMyInformation();
        }
    }

    /**
     * save to state: text, cursor
     */
    public void saveState()
    {
        if(editText != null)
        {
            String text = editText.getText().toString();
            int start = editText.getSelectionStart();
            int end = editText.getSelectionEnd();
            PostPageState state = getState();
            state.setText(text);
            state.setSelectionStart(start);
            state.setSelectionEnd(end);
        }
    }

    private void initMyInformation()
    {
        UserModel me = UserCache.get(Client.getMainAccount().getUserId());
        if(me != null)
        {
            ImageCache.setImageToView(me.iconUrl, iconView);
            screenNameView.setText(me.screenName);
        }
        else
        {
            new GetUserTask(Client.getMainAccount().getUserId())
            {
                @Override
                public void onPostExecute(User result)
                {
                    if(result != null)
                    {
                        UserModel model = ResponseConverter.convert(result);
                        ImageCache.setImageToView(model.iconUrl, iconView);
                        screenNameView.setText(model.screenName);
                    }
                }
            }.callAsync();
        }
        inited = true;
    }

    public void setText(String s)
    {
        editText.setText(s);
    }

    public void setSelection(int start, int end)
    {
        if(start < 0 || end < 0)
        {
            editText.setSelection(0);
        }
        else if(start > editText.length())
        {
            editText.setSelection(editText.length());
        }
        else if(end > editText.length())
        {
            editText.setSelection(start, editText.length());
        }
        else
        {
            editText.setSelection(start, end);
        }
    }

    public void setInReplyTo(final long l)
    {
        if(l == PostSystem.NONE_ID)
        {
            frameInReplyTo.setVisibility(View.GONE);
        }
        else
        {
            new Task<View>()
            {

                @Override
                public void onPreExecute()
                {
                }

                @Override
                public void onPostExecute(View result)
                {
                    frameInReplyTo.removeAllViewsInLayout();
                    frameInReplyTo.addView(result);
                    frameInReplyTo.setVisibility(View.VISIBLE);
                }

                @Override
                public View call() throws Exception
                {
                    TweetModel status = TweetUtils.getOrCreateStatusModel(l);
                    return StatusViewFactory.newInstance(MainActivity.getInstance().getLayoutInflater(), null).getStatusView(status);
                }
            }.callAsync();
        }
    }

    public void setPicture(final String path)
    {
        if(path == null)
        {
            imagePict.setVisibility(View.GONE);
            return;
        }
        new Task<Bitmap>()
        {

            @Override
            public void onPreExecute()
            {
            }

            @Override
            public void onPostExecute(Bitmap result)
            {
                imagePict.setImageBitmap(result);
                imagePict.setVisibility(View.VISIBLE);
            }

            @Override
            public Bitmap call() throws Exception
            {
                Options opt = new Options();
                opt.inPurgeable = true; // GC可能にする
                opt.inSampleSize = 2;
                return BitmapFactory.decodeFile(path, opt);
            }
        }.callAsync();
    }

    public void clear()
    {
        editText.setText("");
        setInReplyTo(PostSystem.NONE_ID);
        PostSystem.clear(true);
        removePicture();
    }

    public void clearBySubmit()
    {
        editText.setText("");
        setInReplyTo(PostSystem.NONE_ID);
        PostSystem.clear(false);
        imagePict.setVisibility(View.GONE);
    }

    public void removePicture()
    {
        if(imagePict.isShown())
        {
            ConfirmDialog.show(MainActivity.getInstance(), "画像の投稿を取り消しますか？", new Runnable()
            {

                @Override
                public void run()
                {
                    getState().setPicturePath(null);
                    imagePict.setVisibility(View.GONE);
                    Notificator.info("取り消しました");
                }
            });
        }
    }

    public void openIme()
    {
        if(editText != null)
        {
            new UiHandler()
            {

                @Override
                public void run()
                {
                    if(Client.<Boolean>getPreferenceValue(EnumPreferenceKey.OPEN_IME))
                    {
                        InputMethodManager imm = (InputMethodManager) Client.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, 0);
                    }
                }
            }.postDelayed(100);
        }
    }

    public void hideIme()
    {
        if(editText != null)
        {
            InputMethodManager imm = (InputMethodManager) Client.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.imBtn_tweet:
            {
                if(PostSystem.submit(editText.getText().toString()))
                {
                    if(Client.<Boolean>getPreferenceValue(EnumPreferenceKey.AFTER_SUBMIT))
                    {
                        MainActivity.getInstance().getViewPager().setCurrentItem(1);
                    }
                    clearBySubmit();
                }
                break;
            }
            case R.id.post_menu:
            {
                saveState();
                hideIme();
                new PostingMenu(getActivity()).create().show();
                break;
            }
            case R.id.post_pickpict:
            {
                saveState();
                hideIme();
                new SelectPictureDialog(getActivity()).create().show();
                break;
            }
            case R.id.post_delete:
            {
                ConfirmDialog.show(getActivity(), "全消去しますか？", new Runnable()
                {
                    @Override
                    public void run()
                    {
                        clear();
                    }
                });
                break;
            }
            case R.id.image_pict:
            {
                removePicture();
                break;
            }
            case R.id.post_config:
            {
                saveState();
                hideIme();
                new MainMenu(getActivity()).create().show();
                break;
            }
            default:
                break;
        }
    }
}