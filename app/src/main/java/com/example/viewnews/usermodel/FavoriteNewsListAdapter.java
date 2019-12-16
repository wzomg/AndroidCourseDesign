package com.example.viewnews.usermodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.viewnews.NewsCollectBean;
import com.example.viewnews.R;

import java.util.List;

public class FavoriteNewsListAdapter extends ArrayAdapter<NewsCollectBean> {

    private int resourceId;

    public FavoriteNewsListAdapter(Context context, int textViewResourceId, List<NewsCollectBean> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsCollectBean dataBean = getItem(position);
        View view =null;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
        } else {
            view = convertView;
        }
        TextView newsName = (TextView) view.findViewById(R.id.title_news);
        newsName.setText(dataBean.getNewSTitle());
        return view;
    }
    public class ViewHoder{
        private TextView newsName;
    }
}