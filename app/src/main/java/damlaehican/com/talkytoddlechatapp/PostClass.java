package damlaehican.com.talkytoddlechatapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostClass extends ArrayAdapter<String> {


    private final ArrayList<String> userEmail;
    private final ArrayList<String> userImage;
    private final Activity context;


    public PostClass(ArrayList<String> userEmail, ArrayList<String> userImage, Activity context) {
        super(context,R.layout.custom_view, userEmail);
        this.userEmail = userEmail;
        this.userImage = userImage;
        this.context = context;


    }


    @Override
    public View getView(int position,  View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.custom_view, null, true);

        TextView userEmailText = customView.findViewById(R.id.userEmailTextView);
        ImageView imageView = customView.findViewById(R.id.imageView3);

        userEmailText.setText(userEmail .get(position));
        Picasso.get().load(userImage.get(position)).into(imageView);

        return customView;
    }
}
