package com.standford.ligthhouse.ui.userprofile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.standford.ligthhouse.R;
import com.standford.ligthhouse.model.BodyList;

import java.util.ArrayList;

public class WriteUpAdapter extends ArrayAdapter<BodyList> {
    /**
     * adapter class to populate LIstView with data from list of LinkModel objects.
     *
     * @param context
     * @param links
     */
    ArrayList<BodyList> linkstest = new ArrayList<>();

    public WriteUpAdapter(Context context, ArrayList<BodyList> links) {
        super(context, 0, links);
        linkstest = links;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BodyList messageModel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_writeup_list_layout, parent, false);
        }

        TextView tvWriteUp = convertView.findViewById(R.id.tvWriteUp);
        TextView tvWriteUpContent = convertView.findViewById(R.id.tvWriteUpContent);

        Gson gson = new Gson();

        tvWriteUp.setText(messageModel.getTitle());
        tvWriteUpContent.setText(messageModel.getBody().toString());


        return convertView;
    }

    @Override
    public int getCount() {
        Log.e("SIZE", "getCount: " + linkstest.size());
        return linkstest.size();
    }
}
