package damlaehican.com.talkytoddlechatapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CustomChatAdapter> mAdaterList2;

    public CustomListAdapter(Context mContext, List<CustomChatAdapter> mAdaterList2) {
        this.mContext = mContext;
        this.mAdaterList2 = mAdaterList2;
    }



    @Override
    public int getCount() {
        return 0;
    }


    @Override
    public Object getItem(int i) {
        return mAdaterList2.size();
    }


    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = View.inflate(mContext, R.layout.custom_chat_list_view, null);

        TextView tvMsgText = v.findViewById(R.id.txtMessage);
        tvMsgText.setText(mAdaterList2.get(i).getS_msgText());


        if(mAdaterList2.get(i).getS_userName() == "sen"){

            tvMsgText.setTextColor(Color.GREEN);
            tvMsgText.setTextAlignment(view.TEXT_ALIGNMENT_TEXT_END);
        }
        v.setTag(mAdaterList2.get(i).getS_msgText());

        return v;
    }
}
