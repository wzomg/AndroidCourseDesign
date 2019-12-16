package com.example.viewnews.usermodel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.viewnews.R;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>{

    private static final String TAG = "ArticleAdapter";

    private Context mContext;

    private List<Article> mArticleList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView  articleImage;
        TextView articleTitle;
        TextView articleTime;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            articleImage = (ImageView) view.findViewById(R.id.article_image);
            articleTitle = (TextView) view.findViewById(R.id.article_name);
            articleTime = (TextView) view.findViewById(R.id.article_time);
        }
    }

    public ArticleAdapter(List<Article> fruitList) {
        mArticleList = fruitList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_article, parent, false);

        final ViewHolder holder = new ViewHolder(view);
        //给cardView注册了一个监听器,当点击时,构造一个Intent并带到ArticleActivity活动中
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Article article = mArticleList.get(position);
                Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                intent.putExtra(ArticleDetailActivity.ARTICLE_IMAGE_ID, article.getArticleImagePath());
                intent.putExtra(ArticleDetailActivity.ARTICLE_NAME, article.getArticleTitle());
                intent.putExtra(ArticleDetailActivity.ARTICLE_TIME, article.getArticleTime());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = mArticleList.get(position);
        holder.articleTitle.setText(article.getArticleTitle());
        holder.articleTime.setText(article.getArticleTime());
        //使用Glide来加载图片，with方法传入一个Context、Activity或Fragment参数，最后调用load()方法去加载图片在
        Glide.with(mContext)
                .load(article.getArticleImagePath())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.articleImage);
    }
    @Override
    public int getItemCount() {
        return mArticleList.size();
    }
}
