package com.example.viewnews.usermodel;

import org.litepal.crud.LitePalSupport;

public class Article extends LitePalSupport {

    // 用户账号（唯一标识）
    private String userId;
    // 标题
    private String articleTitle;
    // 作者昵称
    private String articleAuthor;
    // 编写时间
    private String articleTime;
    // 文章图片地址
    private String articleImagePath;
    // 文章内容
    private String articleContent;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleAuthor() {
        return articleAuthor;
    }

    public void setArticleAuthor(String articleAuthor) {
        this.articleAuthor = articleAuthor;
    }

    public String getArticleTime() {
        return articleTime;
    }

    public void setArticleTime(String articleTime) {
        this.articleTime = articleTime;
    }

    public String getArticleImagePath() {
        return articleImagePath;
    }

    public void setArticleImagePath(String articleImagePath) {
        this.articleImagePath = articleImagePath;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    @Override
    public String toString() {
        return "Article{" +
                "userId='" + userId + '\'' +
                ", articleTitle='" + articleTitle + '\'' +
                ", articleAuthor='" + articleAuthor + '\'' +
                ", articleTime='" + articleTime + '\'' +
                ", articleImagePath='" + articleImagePath + '\'' +
                ", articleContent='" + articleContent + '\'' +
                '}';
    }
}
