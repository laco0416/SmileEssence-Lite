package net.miz_hi.smileessence.view.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.R;
import net.miz_hi.smileessence.auth.AuthenticationDB;
import net.miz_hi.smileessence.command.main.CommandInformation;
import net.miz_hi.smileessence.command.main.CommandOpenLicense;
import net.miz_hi.smileessence.data.list.ListModel;
import net.miz_hi.smileessence.data.search.SearchModel;
import net.miz_hi.smileessence.dialog.ConfirmDialog;
import net.miz_hi.smileessence.dialog.SeekBarDialog;
import net.miz_hi.smileessence.notification.Notificator;
import net.miz_hi.smileessence.preference.EnumPreferenceKey;

public class SettingActivity extends PreferenceActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        Preference textSize = findPreference(getResources().getString(R.string.key_setting_textSize));
        textSize.setOnPreferenceClickListener(new OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                final SeekBarDialog helper = new SeekBarDialog(SettingActivity.this, "テキストサイズ");
                helper.setSeekBarMax(16);
                helper.setSeekBarStart(Client.getTextSize() - 8);
                helper.setLevelCorrect(8);
                helper.setText("デフォルト = 10");
                helper.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Client.putPreferenceValue(EnumPreferenceKey.TEXT_SIZE, helper.getProgress() + helper.getLevelCorrect());
                        Client.loadPreferences();
                    }
                });
                helper.createSeekBarDialog().show();
                return true;
            }
        });

        Preference requestCount = findPreference(getResources().getString(R.string.key_setting_requestCount));
        requestCount.setOnPreferenceClickListener(new OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                final SeekBarDialog helper = new SeekBarDialog(SettingActivity.this, "読み込み件数");
                helper.setSeekBarMax(80);
                helper.setSeekBarStart(Client.<Integer>getPreferenceValue(EnumPreferenceKey.REQUEST_COUNT) - 20);
                helper.setLevelCorrect(20);
                helper.setText("大きすぎると動作が重くなります");
                helper.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Client.putPreferenceValue(EnumPreferenceKey.REQUEST_COUNT, helper.getProgress() + helper.getLevelCorrect());
                        Client.loadPreferences();
                    }
                });
                helper.createSeekBarDialog().show();
                return true;
            }
        });

        Preference deleteAccounts = findPreference(getResources().getString(R.string.key_setting_delete_accounts));
        deleteAccounts.setOnPreferenceClickListener(new OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                ConfirmDialog helper = new ConfirmDialog(SettingActivity.this, "本当にリセットしますか？");
                helper.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case DialogInterface.BUTTON_POSITIVE:
                            {
                                Notificator.toast("全ての認証情報をリセットします。再起動してください");
                                AuthenticationDB.instance().deleteAll();
                                ListModel.instance().deleteAll();
                                SearchModel.instance().deleteAll();
                                finish();
                                MainActivity.getInstance().finish(true);
                                break;
                            }
                            default:
                            {
                                dialog.dismiss();
                            }
                        }

                    }
                });
                helper.createYesNoAlert().show();
                return true;
            }
        });

        Preference appInfo = findPreference(getResources().getString(R.string.app_info));
        appInfo.setOnPreferenceClickListener(new OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                new CommandInformation((Activity) preference.getContext()).run();
                return true;
            }
        });

        Preference licenseNotice = findPreference(getResources().getString(R.string.license_notices));
        licenseNotice.setOnPreferenceClickListener(new OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                new CommandOpenLicense((Activity) preference.getContext()).run();
                return true;
            }
        });
    }

}