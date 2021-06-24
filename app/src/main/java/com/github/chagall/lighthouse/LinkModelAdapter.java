package com.github.chagall.lighthouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LinkModelAdapter extends ArrayAdapter<LinkModel> {
    public LinkModelAdapter(Context context, ArrayList<LinkModel> links) {
        super(context, 0, links);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinkModel link = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
        }

        TextView Sender = (TextView) convertView.findViewById(R.id.Sender);
        TextView Link = (TextView) convertView.findViewById(R.id.Link);
        TextView OriginalMessage = (TextView) convertView.findViewById(R.id.OriginalMessage);

        Sender.setText(link.sender);
        Link.setText(link.link);
        OriginalMessage.setText(link.originalMessage);

        return convertView;
    }
}
