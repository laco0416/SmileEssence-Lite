package net.miz_hi.smileessence.preference;

import net.miz_hi.smileessence.Client;
import net.miz_hi.smileessence.R;

public enum EnumPreferenceKey
{
    LAST_USED_USER_ID(EnumValueType.LONG, "user_id", -1L),
    TEXT_SIZE(EnumValueType.INTEGER, Client.getMainActivity().getString(R.string.key_setting_textSize), 10),
    AFTER_SUBMIT(EnumValueType.BOOLEAN, Client.getMainActivity().getString(R.string.key_setting_aftersubmit), true),
    NOTICE_UNFAV(EnumValueType.BOOLEAN, Client.getMainActivity().getString(R.string.key_setting_notice_unfav), false),
    OPEN_IME(EnumValueType.BOOLEAN, Client.getMainActivity().getString(R.string.key_setting_open_ime), true),
    CONFIRM_DIALOG(EnumValueType.BOOLEAN, Client.getMainActivity().getString(R.string.key_setting_confirm_dialog), true),
    READ_MORSE(EnumValueType.BOOLEAN, Client.getMainActivity().getString(R.string.key_setting_morse), true),
    NAME_STYLE(EnumValueType.STRING, Client.getMainActivity().getString(R.string.key_setting_namestyle), Client.getMainActivity().getString(R.string.namestyle_s_n)),
    SHOW_READ_RETWEET(EnumValueType.BOOLEAN, Client.getMainActivity().getString(R.string.key_setting_read_retweet), true),
    LAST_PRODUCE_DATE(EnumValueType.STRING, "last_produce_date", ""),
    SEARCH_INCLUDE_RT(EnumValueType.BOOLEAN, Client.getMainActivity().getString(R.string.key_setting_search_include_rt), false),
    REQUEST_COUNT(EnumValueType.INTEGER, Client.getMainActivity().getString(R.string.key_setting_requestCount), 20),
    THEME_IS_DARK(EnumValueType.BOOLEAN, Client.getMainActivity().getString(R.string.key_setting_theme), false);

    private final EnumValueType type;
    private final String key;
    private final Object defaultValue;

    private EnumPreferenceKey(EnumValueType type, String key, Object defaultValue)
    {
        this.type = type;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey()
    {
        return this.key;
    }

    public EnumValueType getType()
    {
        return this.type;
    }

    public Object getDefaultValue()
    {
        return this.defaultValue;
    }

    public enum EnumValueType
    {
        BOOLEAN,
        INTEGER,
        LONG,
        FLOAT,
        STRING
    }
}
