package com.example.viewnews.usermodel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.viewnews.R;
import com.example.viewnews.tools.BaseActivity;

import org.litepal.LitePal;

import java.util.List;

public class RegisterActivity extends BaseActivity {

    private EditText reg_userAccount;
    private EditText reg_userPwd;
    private EditText reg_confirm_userPwd;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_userAccount = findViewById(R.id.register_userAccount);
        reg_userPwd = findViewById(R.id.register_pwd);
        reg_confirm_userPwd = findViewById(R.id.confirm_pwd);
        registerBtn = findViewById(R.id.register_click);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 首先验证输入是否为空
                String userId = reg_userAccount.getText().toString();
                String userPwd = reg_userPwd.getText().toString();
                String secondPwd = reg_confirm_userPwd.getText().toString();
                List<UserInfo> all = LitePal.findAll(UserInfo.class);
                if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(userPwd) || TextUtils.isEmpty(secondPwd)) {
                    // 判断字符串是否为null或者""
                    Toast.makeText(RegisterActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    // 判断两次输入的密码是否匹配，匹配则写入数据库，并且结束当前活动，自动返回登录界面
                    if(userPwd.equals(secondPwd)) {
                        List<UserInfo> userInfoList = LitePal.where("userAccount = ?", userId).find(UserInfo.class);
                        if(userInfoList.size() > 0) {
                            Toast.makeText(RegisterActivity.this, "当前账号已被注册，请重新输入账号", Toast.LENGTH_SHORT).show();
                        } else {
                            UserInfo userInfo = new UserInfo();
                            userInfo.setUserAccount(userId);
                            userInfo.setUserPwd(secondPwd);
                            userInfo.setUserBirthDay("待完善");
                            userInfo.setUserSex("待完善");
                            userInfo.setUserSignature("这个人很懒，TA什么也没留下。");
                            // 给其设置一个用户名
                            userInfo.setNickName("用户" + (all.size() + 1));
                            userInfo.save();
                            System.out.println(userInfo);
                            Intent intent = new Intent();
                            intent.putExtra("register_status", "注册成功");
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "请确认输入密码与确认密码是否一致?", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
