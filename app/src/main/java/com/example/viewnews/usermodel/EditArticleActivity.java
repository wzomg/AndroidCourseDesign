package com.example.viewnews.usermodel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.viewnews.R;
import com.example.viewnews.tools.BaseActivity;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class EditArticleActivity extends BaseActivity {


    private ImageView editImageView;
    private EditText editTitle, editContent;
    private Button publishBtn;

    public static final int CHOOSE_ARTICLE_IMAGE = 22;

    private String editImagePath = "";

    private Article article = new Article();

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        userID = getIntent().getStringExtra("userId");
        Toolbar toolbar = (Toolbar) findViewById(R.id.article_edit_toolbar);
        toolbar.setTitle("编辑文章");
        List<UserInfo> userInfos = LitePal.where("userAccount = ?", userID).find(UserInfo.class);
        article.setUserId(userID);
        article.setArticleAuthor(userInfos.get(0).getNickName());
        System.out.println("当前文章详情为：" + article);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //启用HomeAsUp按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }

        editImageView = findViewById(R.id.edit_picture);
        editTitle = findViewById(R.id.edit_title);
        editContent = findViewById(R.id.edit_content);
        publishBtn = findViewById(R.id.edit_publish);

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(EditArticleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditArticleActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });

        // 发布时间
        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(editTitle.getText()) || TextUtils.isEmpty(editContent.getText())) {
                    Toast.makeText(EditArticleActivity.this, "输入不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    article.setArticleTitle(editTitle.getText().toString());
                    article.setArticleContent(editContent.getText().toString());
                    article.setArticleImagePath(editImagePath);
                    Calendar calendar= Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    System.out.println(sdf.format(calendar.getTime()));
                    article.setArticleTime(sdf.format(calendar.getTime()));
                    boolean save = article.save();
                    if(save) Toast.makeText(EditArticleActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(EditArticleActivity.this, "发布失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openAlbum() {
        Intent mIntent = new Intent("android.intent.action.GET_CONTENT");
        mIntent.setType("image/*");
        startActivityForResult(mIntent, CHOOSE_ARTICLE_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_ARTICLE_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKiKat(data);
                    } else {
                        handleImageBeforeKiKat(data);
                    }
                }
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImageOnKiKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如或是content类型的URI就使用普通方法处理
            imagePath = getImagePath(uri, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的直接获取图片路径就行
            imagePath = uri.getPath();
        }
        editImagePath = imagePath;
        // 根据图片路径显示图片
        diplayImage(imagePath);
    }

    private void handleImageBeforeKiKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        diplayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void diplayImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            editImageView.setImageBitmap(bitmap);
        } else {
            editImageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                // 提醒刷新列表
                setResult(RESULT_OK, intent);
                EditArticleActivity.this.finish();
                return true;
        }
        return true;
    }
}
