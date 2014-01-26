package net.miz_hi.smileessence.data.extra;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import net.miz_hi.smileessence.R;
import net.miz_hi.smileessence.listener.ExtractOnClickListener;
import net.miz_hi.smileessence.util.CustomListAdapter;

public class ExtraWordListAdapter extends CustomListAdapter<ExtraWord>
{

    public ExtraWordListAdapter(Activity activity)
    {
        super(activity, Integer.MAX_VALUE);
    }

    @Override
    public View getView(int position, View convertedView, ViewGroup parent)
    {
        if(convertedView == null)
        {
            convertedView = getInflater().inflate(R.layout.menuitem_white, null);
        }

        ExtraWord ExtraWord = (ExtraWord) getItem(position);

        TextView viewText = (TextView) convertedView.findViewById(R.id.menuitem_text);
        viewText.setText(ExtraWord.getText());

        ExtractOnClickListener listener = new ExtractOnClickListener(this, getActivity(), ExtraWord);

        convertedView.setOnClickListener(listener);
        convertedView.setOnLongClickListener(listener);

        return convertedView;
    }
}
