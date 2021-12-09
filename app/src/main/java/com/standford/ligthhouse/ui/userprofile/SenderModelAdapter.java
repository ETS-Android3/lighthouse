package com.standford.ligthhouse.ui.userprofile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.standford.ligthhouse.R;
import com.standford.ligthhouse.model.MessageModel;

import java.util.ArrayList;

public class SenderModelAdapter extends ArrayAdapter<MessageModel> {
    /**
     * adapter class to populate LIstView with data from list of LinkModel objects.
     *
     * @param context
     * @param links
     */
    public SenderModelAdapter(Context context, ArrayList<MessageModel> links) {
        super(context, 0, links);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageModel messageModel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_domain_list_layout, parent, false);
        }

        TextView tvSender = convertView.findViewById(R.id.tvSender);


        tvSender.setText(messageModel.getSender() + " : " + messageModel.getOriginalMessage());


        return convertView;
    }
}
