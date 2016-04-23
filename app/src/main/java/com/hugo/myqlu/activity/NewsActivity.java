package com.hugo.myqlu.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.hugo.myqlu.R;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.hugo.myqlu.utils.JsoupUtils.parseNewsHtmlFromUrl;

public class NewsActivity extends AppCompatActivity {
    private String PAGE_ID_GDYW = "s/22/t/2/p/15/i/";
    private String newsUrl = "http://news.qlu.edu.cn/";
    private String[] newsTitle;
    private MyHandler myHandler = null;

    private RecyclerView mRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initLisitener();
        myHandler = new MyHandler();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Document doc = parseNewsHtmlFromUrl(NewsActivity.this, newsUrl, PAGE_ID_GDYW);
                String s[] = new String[17];
                Elements es = doc.select("a[href]");
                int i = 0;
                for (Element e : es) {
                    switch (e.text()) {
                        case "工大要闻":
                        case "首页":
                        case "上一页":
                        case "下一页":
                        case "尾页":
                        case "跳转":
                            break;
                        default:
                            s[i] = e.text();
                            System.out.println(s[i++]);
                    }
                }
                Message message = Message.obtain();
                message.obj = s;
                myHandler.sendMessage(message);
            }
        }.start();
    }

    private void initLisitener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initRecycler() {
        mRecyclerView = (RecyclerView) findViewById(R.id.news_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyAdapter(newsTitle));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            newsTitle = (String[]) msg.obj;
            initRecycler();
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private String[] mData;

        public MyAdapter(String[] mData) {
            this.mData = mData;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_title_list, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mData[position]);
        }


        @Override
        public int getItemCount() {
            return mData.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.title_text);
            }
        }
    }

}
