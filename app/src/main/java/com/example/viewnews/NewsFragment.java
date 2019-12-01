package com.example.viewnews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

//每个tab下的碎片Fragment
public class NewsFragment extends Fragment {

    private ListView newsListView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private List<NewsBean.ResultBean.DataBean> list;

    private static final int UPNEWS_INSERT = 0;

    //String data;

    //private TextView tv;

    //每一个Fragment页面都有一个浮动按钮，用于快速回到顶部
    private FloatingActionButton fab;

    //添加此注解的原理：https://blog.csdn.net/androidsj/article/details/79865091
    @SuppressLint("HandlerLeak")
    private Handler newsHandler = new Handler() {
        //主线程
        @Override
        public void handleMessage(Message msg) {
            String uniquekey, title, date, category, author_name, url, thumbnail_pic_s, thumbnail_pic_s02, thumbnail_pic_s03;
            switch (msg.what) {
                case UPNEWS_INSERT:
                    //从本地数据库或者服务器来获取NewsBean数据
                    list = ((NewsBean) msg.obj).getResult().getData();
                    //构造一个适配器来填充新闻列表数据
                    TabAdapter adapter = new TabAdapter(getActivity(), list);
                    newsListView.setAdapter(adapter);
                    //当adapter中的数据被更改后必须马上调用notifyDataSetChanged予以更新。
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

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
        View view = inflater.inflate(R.layout.news_item, container, false);
        //获取每个实例之后，返回当前视图
        newsListView = (ListView) view.findViewById(R.id.newsListView);
        //tv = (TextView) view.findViewById(R.id.text_response);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        return view;
    }

    //当NewsFragment所在的Activity启动完成后调用，声明周期紧接在onCreateView之后
    //使用此注解的讲解：https://blog.csdn.net/androidsj/article/details/79865091
    //@SuppressLint("HandlerLeak")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getActivity():获得Fragment依附的Activity对象。
        //getActivity()和onAttach()结合使用的生命周期相关讲解：https://blog.csdn.net/u013446591/article/details/72730767
        //getActivity()为空的情况，讲解1：https://blog.csdn.net/u012811342/article/details/80493352
        //讲解2：https://blog.csdn.net/qq_31010739/article/details/83348085
        onAttach(getActivity());
        Bundle bundle = getArguments();
        //获取键对应的值，参数2表示默认填充的值，其用法和Intent差不多，但又有区别
        //data = bundle.getString("name", "top");
        //tv.setText(data);
        final String data = bundle.getString("name", "top");
        //实现置顶功能
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //滚动到第一个可见的item位置，下标为0，具体讲解：https://www.jianshu.com/p/a5cd3cff2f1b
                newsListView.smoothScrollToPosition(0);
            }
        });
        //实现下拉刷新的功能
        //设置下拉刷新进度条的颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorRed);
        //实现下拉刷新的监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //postDelayed中需要传两个参数，一个是Runnable对象，一个是以毫秒为单位的时间：1s
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 下一步实现从数据库中读取数据刷新到newsListView的适配器中

                        // 用于表示刷新事件已结束，并隐藏刷新进度条
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 1000);
            }
        });

        //异步加载数据
        getDataFromNet(data);
        //监听新闻列表子项
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击条目的路径，传值显示WebView页面
                String url = list.get(position).getUrl();
                String uniquekey = list.get(position).getUniquekey();
                final NewsBean.ResultBean.DataBean dataBean = (NewsBean.ResultBean.DataBean) list.get(position);
                /*Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);*/
            }
        });
    }
    //异步消息处理机制
    private void getDataFromNet(final String data){
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            //子线程
            @Override
            protected String doInBackground(Void... params) { // 自己的key：af2d37d2ed31f7a074f1d49b5460a0b5，可以替换下面请求中的key
                String path = "http://v.juhe.cn/toutiao/index?type="+data+"&key=547ee75ef186fc55a8f015e38dcfdb9a";
                URL url = null;
                try {
                    url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    connection.setRequestMethod("GET");
                    //设置读取超时的毫秒数
                    connection.setReadTimeout(5000);
                    //设置连接超时时间
                    connection.setConnectTimeout(5000);
                    //获取状态码
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK){ // 200
                        //获取服务器返回的输入流
                        InputStream inputStream = connection.getInputStream();
                        String json = streamToString(inputStream,"utf-8");
                        //返回任务的执行结果
                        return json;
                    } else {
                        System.out.println(responseCode);
                        return "已达到今日访问次数上限";
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }
            //当给后台任务执行完毕并通过return语句返回时，此方法将被调用，返回来的数据可以进行一些UI操作，并以参数传入
            protected void onPostExecute(final String result){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //查看状态码是否为200，（开子线程）若不是，则从本地或者服务器加载相应的数据
                        NewsBean newsBean = new Gson().fromJson(result, NewsBean.class);
                        System.out.println(newsBean.getError_code());
                        //若超过请求次数，则直接从数据库中获取
                        if ("10012".equals(""+newsBean.getError_code())){
                            //下一步实现从数据库加载数据

                        }
                        //obtainmessage()方法是从消息池中拿来一个msg，不需要另开辟空间new，new需要重新申请，效率低，obtianmessage可以循环利用；
                        Message msg = newsHandler.obtainMessage();
                        msg.what = UPNEWS_INSERT;
                        msg.obj = newsBean;
                        //发送一个通知来填充数据，因为安卓不允许在子线程中进行UI操作
                        newsHandler.sendMessage(msg);
                    }
                }).start();
            }
            //当后台任务中调用了publishProgress(Progress...)方法后，onProgressUpdate方法很快被执行
            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
        };
        //启动异步加载任务
        task.execute();
    }
    //输入流转化成字符串
    private String streamToString(InputStream inputStream, String charset){
        try {
            //创建一个使用命名字符集的InputStreamReader。 InputStreamReader(InputStream in, String charsetName)
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
            //构造一个字符输入流对象
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s = null;
            StringBuilder builder = new StringBuilder();
            while ((s = bufferedReader.readLine()) != null){
                builder.append(s);
            }
            //关闭流
            bufferedReader.close();
            inputStreamReader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
