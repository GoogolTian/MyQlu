package com.hugo.myqlu.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.ChengjiBean;
import com.hugo.myqlu.utils.HtmlUtils;
import com.hugo.myqlu.utils.SpUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class SearchCjActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //    @Bind(R.id.spinner_year)
//    Spinner spinnerYear;
//    @Bind(R.id.spinner_xueqi)
//    Spinner spinnerXueqi;
//    @Bind(R.id.spinner_mode)
//    Spinner spinnerMode;
    @Bind(R.id.list_cj)
    ListView listCj;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rootView)
    LinearLayout rootView;
    //   private String cjcxUrl;
    private String stuCenterUrl;
    private Context mContext = this;
    private List<String> xueqi = new ArrayList<>();
    private List<String> mode = new ArrayList<>();
    ArrayAdapter<String> xueqiAdapter;
    ArrayAdapter<String> modeAdapter;
    ArrayAdapter<String> yearsAdapter;
    private List<String> yearList;
    private String ddlXN = "";
    private String ddlXQ = "";
    private String selectMode = null;
    private String VIEWSTATE = null;
    private String XQVIEWSTATE;
    private String JUSTZXVIEWSTATE;
    private String JUSTXNVIEWSTATE;
    private static String BUTTON_XQ = "%B0%B4%D1%A7%C6%DA%B2%E9%D1%AF";
    private static String BUTTON_XN = "%B0%B4%D1%A7%C4%EA%B2%E9%D1%AF";
    private static String BUTTON_ZX = "%D4%DA%D0%A3%D1%A7%CF%B0%B3%C9%BC%A8%B2%E9%D1%AF";
    private List<ChengjiBean> cjList;
    private ListView listview;
    private String title = "";
    private TextView tvTitle;
    private String tempXQ;
    private String tempXN;
    private String username;
    private String password;
    private String stuXH;
    private String stuName;
    private String noCodeVIEWSTATE = "dDwtMTQxNDAwNjgwODt0PDtsPGk8MD47PjtsPHQ8O2w8aTwyMT47aTwyMz47aTwyNT47aTwyNz47PjtsPHQ8cDxsPGlubmVyaHRtbDs+O2w8Oz4+Ozs+O3Q8cDxsPGlubmVyaHRtbDs+O2w8Oz4+Ozs+O3Q8cDxsPGlubmVyaHRtbDs+O2w8Oz4+Ozs+O3Q8cDxsPGlubmVyaHRtbDs+O2w8Oz4+Ozs+Oz4+Oz4+Oz6Mdzik0aXSmAdZCumNJ3uYvSG9aA==";
    private String noCodeLoginUrl = "http://210.44.159.4/default6.aspx";
    //成绩查询url，需要替换数据
    private static String cjcxUrl = "http://210.44.159.4/xscj.aspx?xh=stuxh&xm=stuname&gnmkdm=N121605";
    private static String StuCenterUrl = "http://210.44.159.4/xs_main.aspx?xh=stuxh";
    private String mainUrl = "http://210.44.159.4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_cj);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
        initYearList();
        // initLisitener();
    }


    private void initView() {
        listview = (ListView) findViewById(R.id.list_cj);
        View view = View.inflate(mContext, R.layout.item_header, null);
        tvTitle = ButterKnife.findById(view, R.id.tv_title);
        listview.addHeaderView(view, null, false);
    }

    private void initData() {
        yearList = null;
        cjcxUrl = getIntent().getStringExtra("cjcxUrl");
        stuCenterUrl = getIntent().getStringExtra("StuCenterUrl");
        XQVIEWSTATE = getString(R.string.XQVIEWSTATE);
        JUSTZXVIEWSTATE = getString(R.string.JUSTZXVIEWSTATE);
        JUSTXNVIEWSTATE = getString(R.string.JUSTXNVIEWSTATE);

        SharedPreferences sp = SpUtil.getSp(mContext, "privacy");
        //已保存的用户名和密码
        username = sp.getString("username", null);
        password = sp.getString("password", null);
        System.out.println("用户名和密码 :" + username + "--" + password);
        xueqi.add("1");
        xueqi.add("2");
        xueqi.add("3");
        mode.add("学期");
        mode.add("学年");
        mode.add("在校");
    }

    private void initYearList() {
        OkHttpUtils.get()
                .url(cjcxUrl)
                .addHeader("Host", "210.44.159.4")
                .addHeader("Referer", stuCenterUrl)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                //加载学年失败，原因：没有登录，或者已经掉线,静默登陆
                if (username != null && password != null) {
                    System.out.println("获取年份失败，装备静默登陆");
                    requestLoginByNoCode();
                }
            }

            @Override
            public void onResponse(String response) {
                // tvContent.setText(response);
                System.out.println("initYearList----onResponse");
                HtmlUtils utils = new HtmlUtils(response);
                yearList = utils.parseSelectList();
                initSpinner();
            }
        });
    }

    private void requestLoginByNoCode() {

        OkHttpUtils.post().url(noCodeLoginUrl)
                .addParams("__VIEWSTATE", noCodeVIEWSTATE)
                .addParams("__VIEWSTATEGENERATOR", "89ADBA87")
                .addParams("tname", "")
                .addParams("tbtns", "")
                .addParams("tnameXw", "yhdl")
                .addParams("tbtnsXw", "yhdlyhdl|xwxsdl")
                .addParams("txtYhm", username) //学号
                .addParams("txtXm", password) //不知道是什么，和密码一样
                .addParams("txtMm", password)
                .addParams("rblJs", "%D1%A7%C9%FA")
                .addParams("btnDl", "%B5%C7+%C2%BC")
                .addHeader("Referer", "http://210.44.159.4/default6.aspx")
                .addHeader("Host", "210.44.159.4")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                System.out.println("onError");
                //tvContent.setText(e.getMessage().toString());
            }

            @Override
            public void onResponse(String response) {
                //登陆成功，此时已经进入个人首页
                //抓取查询url
                HtmlUtils utils = new HtmlUtils(response);
                cjcxUrl = mainUrl + "/" + utils.encoder(response);
                String xhandName = utils.getXhandName();
                initUrlData(xhandName);
                initYearList();
            }
        });
    }

    private void initUrlData(String xhandName) {
        //201311011034 田宇同学
        String[] split = xhandName.split(" ");
        stuXH = split[0]; //用户的学号
        stuName = split[1].replace("同学", "");  //用户的姓名
        System.out.println(stuXH + "-------" + stuName);
        //设置需要的url
        cjcxUrl = cjcxUrl.replace("stuxh", stuXH).replace("stuname", stuName);
        StuCenterUrl = StuCenterUrl.replace("stuxh", stuXH);
    }

    private void initSpinner() {
//        @Bind(R.id.spinner_year)
//        Spinner spinnerYear;
//        @Bind(R.id.spinner_xueqi)
//        Spinner spinnerXueqi;
//        @Bind(R.id.spinner_mode)
//        Spinner spinnerMode;
        Spinner spinnerYear = (Spinner) findViewById(R.id.spinner_year);
        Spinner spinnerXueqi = (Spinner) findViewById(R.id.spinner_xueqi);
        Spinner spinnerMode = (Spinner) findViewById(R.id.spinner_mode);

        xueqiAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, xueqi);
        xueqiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerXueqi.setAdapter(xueqiAdapter);
        spinnerXueqi.setSelection(1, true);

        modeAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mode);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMode.setAdapter(modeAdapter);
        spinnerMode.setSelection(0, true);

        yearsAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, yearList);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearsAdapter);
        spinnerYear.setSelection(1, true);

        //选择监听器
        spinnerYear.setOnItemSelectedListener(this);
        spinnerXueqi.setOnItemSelectedListener(this);
        spinnerMode.setOnItemSelectedListener(this);

        initLisitener();
    }

    private void initLisitener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_year:
                ddlXN = yearList.get(position).toString();
                break;
            case R.id.spinner_xueqi:
                ddlXQ = xueqi.get(position).toString();
                break;
            case R.id.spinner_mode:
                String modeString = mode.get(position).toString();
                if (modeString.equals("学期")) {
                    selectMode = BUTTON_XQ;
                    VIEWSTATE = XQVIEWSTATE;
                } else if (modeString.equals("学年")) {
                    selectMode = BUTTON_XN;
                    VIEWSTATE = JUSTXNVIEWSTATE;
                } else if (modeString.equals("在校")) {
                    selectMode = BUTTON_ZX;
                    VIEWSTATE = JUSTZXVIEWSTATE;
                }
                break;
        }
        checkoutFromWeb();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void checkoutFromWeb() {
        //临时变量
        tempXQ = "";
        tempXN = "";

        tempXQ = ddlXQ;
        tempXN = ddlXN;
        if (selectMode == null) {
            return;
        }
        if (selectMode == BUTTON_XQ) {
            //按学期查询
            if (ddlXQ.equals("") || ddlXN.equals("")) {
                return;
            }
            title = ddlXN + "学年第" + ddlXQ + "学期成绩";
        } else if (selectMode.equals(BUTTON_ZX)) {
            //在校成绩查询
            title = "在校学习成绩";
            ddlXQ = "";
            ddlXN = "";
        } else if (selectMode.equals(BUTTON_XN)) {
            //按学年查询
            if (ddlXN.equals("")) {
                return;
            }
            ddlXQ = "";
            title = ddlXN + "学年学习成绩";
        }

        System.out.println("开始查成绩了");
        System.out.println(ddlXN + "----" + ddlXQ + "----" + selectMode);
        final PostFormBuilder post = OkHttpUtils.post();

        post.url(cjcxUrl)
                .addHeader("Host", "210.44.159.4")
                .addHeader("Referer", cjcxUrl)
                .addParams("__VIEWSTATE", VIEWSTATE)
                .addParams("__VIEWSTATEGENERATOR", "8963BEEC")
                .addParams("ddlXN", ddlXN)
                .addParams("ddlXQ", ddlXQ)
                .addParams("txtQSCJ", "0")
                .addParams("txtZZCJ", "100")
                .addParams("Button1", "%B0%B4%D1%A7%C6%DA%B2%E9%D1%AF")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {
                System.out.println("onError");
                System.out.println(e.getMessage().toString());
            }

            @Override
            public void onResponse(String response) {
                System.out.println("onResponse--------checkoutFromWeb");
                HtmlUtils cjUtils = new HtmlUtils(response);
                cjList = cjUtils.parseCJTable();
                initUI();
                //如果CJList的size == 1，表示没有成绩；
                if (cjList.size() == 1) {
                    System.out.println("没有成绩哦");
                    title = "当前条件没有成绩哦";
                }
                //还原数据
                ddlXQ = tempXQ;
                ddlXN = tempXN;
            }
        });
    }

    private void initUI() {
        MyAdapter adapter = new MyAdapter();
        tvTitle.setText(title);
        tvTitle.setTextSize(18);
        tvTitle.setGravity(Gravity.CENTER);
        listview.setAdapter(adapter);
        startAnim();
    }

    private void startAnim() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim);
        listview.startAnimation(animation);
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cjList.size();
        }

        @Override
        public Object getItem(int position) {
            return cjList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(mContext, R.layout.item_cj, null);
                holder = new ViewHolder();
                holder.tvKeCheng = ButterKnife.findById(view, R.id.tv_kecheng);
                holder.tvCj = ButterKnife.findById(view, R.id.tv_chengji);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.tvKeCheng.setText(cjList.get(position).getCourseName());
            holder.tvCj.setText(cjList.get(position).getCourseCj());
            return view;
        }
    }

    public class ViewHolder {
        TextView tvKeCheng;
        TextView tvCj;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
