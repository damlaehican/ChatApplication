package damlaehican.com.talkytoddlechatapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.util.List;

public class ListAdapter extends BaseAdapter {


    private Context mContext;
    private List<Adapter> mAdapterList;

    public ListAdapter(Context mContext, List<Adapter> mAdapterList) {
        this.mContext = mContext;
        this.mAdapterList = mAdapterList;
    }


    @Override
    public int getCount() {

        return mAdapterList.size();
    }


    @Override
    public Object getItem(int i) {

        return mAdapterList.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = View.inflate(mContext, R.layout.customlistview_friend, null);

        TextView tvName = v.findViewById(R.id.textViewfriendMail);
        ImageView imageUser = v.findViewById(R.id.imageViewFriend);

        v.setTag(mAdapterList.get(i).getS_name());

        tvName.setText(mAdapterList.get(i).getS_name());

        Glide.with(mContext)
                .load(mAdapterList.get(i).getS_image())
                .into(imageUser);

        return v;
    }
}
