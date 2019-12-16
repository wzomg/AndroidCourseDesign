package com.example.viewnews.usermodel;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.viewnews.MainActivity;
import com.example.viewnews.R;
import com.example.viewnews.tools.BaseActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.LitePal;

import java.util.List;

public class ArticleDetailActivity extends BaseActivity {

    public static final String  ARTICLE_NAME = "artile_name";

    public static final String ARTICLE_IMAGE_ID = "artile_image_id";

    public static final String ARTICLE_TIME = "artile_time";

   private String articleName, articleImageId, articleTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        Intent intent = getIntent();

        articleName = intent.getStringExtra(ARTICLE_NAME);
        articleImageId = intent.getStringExtra(ARTICLE_IMAGE_ID);
        articleTime = intent.getStringExtra(ARTICLE_TIME);
        System.out.println("当前图片的地址为：" + articleImageId);
        System.out.println("当前文章的标题为：" + articleName);
        System.out.println("当前文章的发布时间为：" + articleTime);

        Toolbar toolbar = (Toolbar) findViewById(R.id.article_detail_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //启用HomeAsUp按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ImageView articleImageView = (ImageView) findViewById(R.id.article_image_view);
        TextView articleContentText = (TextView) findViewById(R.id.article_content_text);

        TextView articleAuthor = findViewById(R.id.article_author11);

        TextView articleTime12 = findViewById(R.id.article_time11);

        // 此处去查询数据库
        List<Article> articles = LitePal.where("userId = ? AND articleTitle = ? AND articleTime = ?", MainActivity.currentUserId, articleName, articleTime).find(Article.class);

        //设置当前界面的标题，作者，内容
        collapsingToolbar.setTitle(articleName);
        // 设置时间
        articleTime12.setText(articleTime);
        articleAuthor.setText(articles.get(0).getArticleAuthor());
        articleContentText.setText(articles.get(0).getArticleContent());

        Glide.with(this).load(articleImageId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(articleImageView);


        //监听删除点击事件
        FloatingActionButton delFab = (FloatingActionButton) findViewById(R.id.delete_article);
        delFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击删除文章
                new MaterialDialog.Builder(ArticleDetailActivity.this)
                        .title("提示")
                        .content("确认是否删除此篇文章")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                //Toast.makeText(ArticleDetailActivity.this, "点击了确认按钮", Toast.LENGTH_SHORT).show();
                                int isOk = LitePal.deleteAll(Article.class, "userId = ? AND articleTitle = ? AND articleTime = ?", MainActivity.currentUserId, articleName, articleTime);
                                if(isOk > 0) {
                                    Toast.makeText(ArticleDetailActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                    ArticleDetailActivity.this.finish();
                                } else {
                                    Toast.makeText(ArticleDetailActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .positiveText("确认")
                        .negativeText("取消")
                        .show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //关闭当前活动,从而返回上一个活动
                ArticleDetailActivity.this.finish();
                return true;
        }
        return true;
    }
}
