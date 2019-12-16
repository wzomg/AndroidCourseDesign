package com.example.viewnews.usermodel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.viewnews.NewsCollectBean;
import com.example.viewnews.R;
import com.example.viewnews.WebActivity;
import com.example.viewnews.tools.BaseActivity;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;

public class UserFavoriteActivity extends BaseActivity {

    private ListView favoriteNewsList;
    private List<NewsCollectBean> sonNewList = new ArrayList<>();
    private Toolbar favoriteToolbar;
    private String userIdNumber;

    private FavoriteNewsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorite);
        userIdNumber = getIntent().getStringExtra("user_love_id");
        System.out.println("收藏：当前用户的id为" + userIdNumber);
        favoriteNewsList = findViewById(R.id.favorite_newsList);
        favoriteToolbar = findViewById(R.id.userFavorite_toolbar);
        favoriteToolbar.setTitle("我的收藏");
        setSupportActionBar(favoriteToolbar);
        initNews();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }
    }

    // 初始化数据
    private void initNews() {
        sonNewList = LitePal.where("userIdNumer = ? ", userIdNumber).find(NewsCollectBean.class);
        System.out.println("当前喜欢的列表有：" + sonNewList);
        adapter = new FavoriteNewsListAdapter(UserFavoriteActivity.this, R.layout.item_favorite_news,sonNewList);
        favoriteNewsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        favoriteNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsCollectBean dataBean = sonNewList.get(position);
                Intent intent = new Intent(UserFavoriteActivity.this, WebActivity.class);
                intent.putExtra("pageUrl", dataBean.getNewsUrl());
                intent.putExtra("uniquekey", dataBean.getNewsId());
                intent.putExtra("news_title", dataBean.getNewSTitle());
                startActivityForResult(intent, 4);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                UserFavoriteActivity.this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 4:
                if(resultCode == RESULT_OK) {
                    initNews();
                    System.out.println("奥里给！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
                }
                break;
        }
    }
}