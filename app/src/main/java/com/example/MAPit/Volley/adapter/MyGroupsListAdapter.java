package com.example.MAPit.Volley.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.MAPit.MAPit.R;
import com.example.MAPit.Volley.app.AppController;
import com.example.MAPit.Volley.data.Group_Item;

import java.util.List;

/**
 * Created by SETU on 1/24/2015.
 */
public class MyGroupsListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Group_Item> groupItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public MyGroupsListAdapter(Activity activity, List<Group_Item> groupItems) {
        this.activity = activity;
        this.groupItems = groupItems;
    }

    @Override
    public int getCount() {
        return groupItems.size();
    }

    @Override
    public Object getItem(int location) {
        return groupItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.mygroups_list_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.mygroup_name);
        TextView location = (TextView) convertView.findViewById(R.id.mygroup_location);
        NetworkImageView profilePic = (NetworkImageView) convertView.findViewById(R.id.mygroupPic);
        final Button removeGroup = (Button) convertView.findViewById(R.id.bt_remove_group);

        Group_Item item = groupItems.get(position);

        name.setText(item.getGroup_Name());
        profilePic.setImageUrl(item.getGroup_Image(), imageLoader);
        location.setText(item.getGroup_location());

        //button joinGroup listener is needed to fill
        removeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupItems.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}


