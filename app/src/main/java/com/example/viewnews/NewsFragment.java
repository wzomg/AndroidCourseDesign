package com.example.viewnews;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

//每个tab下的碎片Fragment
public class NewsFragment extends Fragment {

    String data;

    private TextView tv;

    //每一个Fragment页面都有一个浮动按钮，用于快速回到顶部
    private FloatingActionButton fab;

    //创建Fragment被添加到活动中时回调，且只会被调用一次
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //每次创建，绘制该Fragment的View组件时回调，将显示的View返回
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载新闻列表
        View view = inflater.inflate(R.layout.news_item, container,false);
        //获取实例之后，返回当前视图
        tv = (TextView) view.findViewById(R.id.text_response);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        return view;
    }

    //当NewsFragment所在的Activity启动完成后调用，声明周期紧接在onCreateView之后
    //使用此注解的讲解：https://blog.csdn.net/androidsj/article/details/79865091
    //@SuppressLint("HandlerLeak")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onAttach(getActivity());
        Bundle bundle = getArguments();
        //获取键对应的值，参数2表示默认填充的值，其用法和Intent差不多，但又有区别
        data = bundle.getString("name","top");
        tv.setText(data);
    }
}
