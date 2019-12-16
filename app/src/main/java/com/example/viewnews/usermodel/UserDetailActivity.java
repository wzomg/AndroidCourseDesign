package com.example.viewnews.usermodel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
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
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.viewnews.R;
import com.example.viewnews.tools.BaseActivity;

import org.litepal.LitePal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserDetailActivity extends BaseActivity {

    private ImageView userAvatar;

    private Toolbar detailToolbar;

    private String userId;

    public static final int CHOOSE_USER_AVATAR = 11;

    private UserInfo userInfo;

    // 定义线性布局
    private LinearLayout layout_avatar, layout_nickname, layout_sex, layout_birth, layout_signature;

    private TextView showNickName, showSex, showBirthday, showSignature;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        detailToolbar = findViewById(R.id.userData_toolbar);
        detailToolbar.setTitle("个人信息");
        setSupportActionBar(detailToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_left);
        }
        layout_avatar = findViewById(R.id.lay_avatar);
        layout_nickname = findViewById(R.id.lay_nickname);

        layout_sex = findViewById(R.id.lay_sex);
        layout_birth = findViewById(R.id.lay_birthday);
        layout_signature = findViewById(R.id.lay_signature);

        userAvatar = findViewById(R.id.user_avatar);

        showNickName = findViewById(R.id.show_name);
        showSex = findViewById(R.id.show_sex);

        showBirthday = findViewById(R.id.show_birthday);
        showSignature = findViewById(R.id.show_sign);

        userId = getIntent().getStringExtra("user_edit_id");
        initData();

        calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        Date date = null;
        if (!TextUtils.isEmpty(userInfo.getUserBirthDay()) && !userInfo.getUserBirthDay().equals("待完善")) {
            try {
                date = format.parse(userInfo.getUserBirthDay());
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    // 初始化数据
    private void initData() {
        List<UserInfo> infos = LitePal.where("userAccount = ?", userId).find(UserInfo.class);
        userInfo = infos.get(0);
        System.out.println("用户详情界面的信息为" + userInfo);
        showNickName.setText(userInfo.getNickName());
        showSex.setText(userInfo.getUserSex());
        showBirthday.setText(userInfo.getUserBirthDay());
        showSignature.setText(userInfo.getUserSignature());
        String curImagePath = userInfo.getImagePath();
        diplayImage(curImagePath);
    }

    // 在活动由不可见变为可见的时候调用
    @Override
    protected void onStart() {
        super.onStart();
        layout_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(UserDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });
        layout_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserDetailActivity.this)
                        .title("修改昵称")
                        .inputRangeRes(2, 8, R.color.colorBlack)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("请输入要修改的昵称", userInfo.getNickName(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // CharSequence的值是可读可写序列，而String的值是只读序列。
                                //Toast.makeText(UserDetailActivity.this, input, Toast.LENGTH_SHORT).show();

                                System.out.println(input.toString());
                                // 重新设置值，当前活动被销毁时才保存到数据库
                                userInfo.setNickName(input.toString());
                                showNickName.setText(userInfo.getNickName());
                            }
                        })
                        .positiveText("确定")
                        .show();

            }
        });
        layout_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] contentSex = new String[]{"男", "女"};
                new MaterialDialog.Builder(UserDetailActivity.this)
                        .title("修改性别")
                        .items(contentSex)
                        .itemsCallbackSingleChoice(userInfo.getUserSex().equals("女") ? 1 : 0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                System.out.println("选择哪一个" + which);
                                System.out.println("选择的内容是" + text);
                                userInfo.setUserSex(text.toString());
                                showSex.setText(userInfo.getUserSex());
                                return true;
                            }
                        })
                        .show();
            }
        });

        layout_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(UserDetailActivity.this, R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        String birth = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        System.out.println(birth);
                        userInfo.setUserBirthDay(birth);
                        showBirthday.setText(birth);
                    }
                },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        layout_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserDetailActivity.this)
                        .title("修改个性签名")
                        .inputRangeRes(1, 38, R.color.colorBlack)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("请输入要修改的个性签名", userInfo.getUserSignature(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {

                                System.out.println(input.toString());
                                userInfo.setUserSignature(input.toString());
                                showSignature.setText(userInfo.getUserSignature());
                            }
                        })
                        .positiveText("确定")
                        .show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 保存更改的数据
                userInfo.save();
                Intent intent = new Intent();
                intent.putExtra("nickName", showNickName.getText().toString());
                intent.putExtra("signature", showSignature.getText().toString());
                intent.putExtra("imagePath", userInfo.getImagePath());
                setResult(RESULT_OK, intent);
                System.out.println("当前个人信息活动页被销毁！！！");
                UserDetailActivity.this.finish();
                break;
        }
        return true;
    }

    private void openAlbum() {
        Intent mIntent = new Intent("android.intent.action.GET_CONTENT");
        mIntent.setType("image/*");
        startActivityForResult(mIntent, CHOOSE_USER_AVATAR);
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
            case CHOOSE_USER_AVATAR:
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
        // 根据图片路径显示图片
        userInfo.setImagePath(imagePath);
        System.out.println("更新图片后，用户信息为" + userInfo);
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
            userAvatar.setImageBitmap(bitmap);
        } else {
            userAvatar.setImageResource(R.drawable.no_login_avatar);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("个人信息页面被销毁。。。。。。。。。。");
    }
}
