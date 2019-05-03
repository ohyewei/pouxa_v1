package pouxateam.pouxa;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnsearch, btnimage, btnweather, btnevent, btnblog, btnsetting;
    private TextView txtweather, txtootd, txtevent, txttrend;
    private ViewFlipper mFlipper;
    SharedPreferences prefs;
    private String strCity;
    Intent intent = new Intent();
    //以圖搜圖
    CreatePHash p;
    String imagecode;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    final ArrayList<String> choose = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/typeface.otf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlipper = (ViewFlipper) this.findViewById(R.id.flipper);
        mFlipper.startFlipping();
        mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_up_in));     //進入動畫
        mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_up_out));   //出去動畫

        txtevent = (TextView) findViewById(R.id.info_event);
        txtootd = (TextView) findViewById(R.id.info_ootd);
        txttrend = (TextView) findViewById(R.id.info_trend);
        txtweather = (TextView) findViewById(R.id.info_weather);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        strCity = prefs.getString("city", "臺北市");

        setInformation();

        btnsearch = (ImageButton) findViewById(R.id.btnsearch);
        btnsearch.setOnClickListener(myOnClickListener);

        btnimage = (ImageButton) findViewById(R.id.btnimage);
        btnimage.setOnClickListener(myOnClickListener);
        choose.add("拍攝照片");
        choose.add("從相簿選取");
        choose.add("取消");

        btnweather = (ImageButton) findViewById(R.id.btnweather);
        btnweather.setOnClickListener(myOnClickListener);

        btnevent = (ImageButton) findViewById(R.id.btnevent);
        btnevent.setOnClickListener(myOnClickListener);

        btnblog = (ImageButton) findViewById(R.id.btnblog);
        btnblog.setOnClickListener(myOnClickListener);

        btnsetting = (ImageButton) findViewById(R.id.btnsetting);
        btnsetting.setOnClickListener(myOnClickListener);

        p = new CreatePHash();

    }

    private void setInformation() {
        final Random random = new Random();

        //取得天氣
        final CityWeather cityWeather = new CityWeather(this);
        cityWeather.getCityWeatherData(new CityWeather.VolleyCallback() {
            public void onSuccess(ArrayList<CityWeatherResultIdioms> allData) {
                String morning = allData.get(0).getMorning();
                String night = allData.get(0).getNight();
                String avgtemp = getAvgtemp(morning, night);
                String strCityWeather = "<font color=\"#FF0000\">今日" + URLDecoder.decode(strCity) + "：</font>"
                        + "<font color=\"#000000\">" + avgtemp + "</font>";
                txtweather.setText(Html.fromHtml(strCityWeather));
            }
        });

        //取得優惠活動
        final MiuStarEvent miuStarEvent = new MiuStarEvent(this);
        miuStarEvent.getMiuStarEvent(new MiuStarEvent.VolleyCallback() {
            public void onSuccess(final ArrayList<EventResultIdioms> miuData) {

                String strMiuEvent = "<font color=\"#FF0000\">Miu-Star最新優惠：</font><br>"
                        + "<font color=\"#000000\">" + miuData.get(0).getEventname().substring(0,miuData.get(0).getEventname().indexOf("\n")) + "</font>";
                txtevent.setText(Html.fromHtml(strMiuEvent));
                txtevent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browser = new Intent();
                        Bundle bundle = new Bundle();

                        bundle.putString("storename", miuData.get(0).getStorename().trim());
                        browser.setClass(MainActivity.this, Event.class);
                        browser.putExtras(bundle);
                        startActivity(browser);
                    }
                });
            }
        });

        //取得流行元素
        Trend trend = new Trend(this);
        trend.getTrendData(new Trend.VolleyCallback() {
            public void onSuccess(String[] trendArray) {
                String strTrend = "<font color=\"#FF0000\">現正流行：</font>" +
                        "<font color=\"#000000\">" + trendArray[random.nextInt(10)] + "、" + trendArray[random.nextInt(10)] + "、" + trendArray[random.nextInt(10)] + "</font>";
                txttrend.setText(Html.fromHtml(strTrend));
            }
        });

        //取得穿搭推薦
        final String[] hotItem = {"短袖", "短褲", "背心", "裙子"};
        final String[] coldItem = {"長袖", "長褲", "外套"};
        trend.getTrendData(new Trend.VolleyCallback() {
            public void onSuccess(final String[] trendArray) {
                cityWeather.getCityWeatherData(new CityWeather.VolleyCallback() {
                    public void onSuccess(ArrayList<CityWeatherResultIdioms> allData) {
                        String strOotd;
                        int morninglow = Integer.parseInt(allData.get(0).getMorning().substring(3, 5));
                        int morninghigh = Integer.parseInt(allData.get(0).getMorning().substring(8, 10));
                        int nightlow = Integer.parseInt(allData.get(0).getNight().substring(3, 5));
                        int nighthigh = Integer.parseInt(allData.get(0).getNight().substring(8, 10));
                        int temp = (morninglow + morninghigh + nightlow +nighthigh)/4 ;
                        if (temp > 27) {
                            strOotd = "<font color=\"#FF0000\">建議搭配：</font>"
                                    + "<font color=\"#000000\">" + trendArray[random.nextInt(10)] + hotItem[random.nextInt(4)] + "</font>";
                            txtootd.setText(Html.fromHtml(strOotd));
                        } else {
                            strOotd = "<font color=\"#FF0000\">建議搭配：</font>"
                                    + "<font color=\"#000000\">" + trendArray[random.nextInt(10)] + coldItem[random.nextInt(4)] + "</font>";
                            txtootd.setText(Html.fromHtml(strOotd));
                        }
                    }
                });
            }
        });
    }

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {

        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.btnsearch:
                    intent.setClass(MainActivity.this, Search.class);
                    startActivity(intent);
                    break;
                case R.id.btnimage:
                    new AlertDialog.Builder(MainActivity.this)
                            .setItems(choose.toArray(new String[choose.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    if (choose.get(position).equals("拍攝照片"))
                                        //開啟相機
                                        ;
                                    else if (choose.get(position).equals("從相簿選取")) {
                                        //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因
                                        //為點選相片後返回程式呼叫onActivityResult
                                        Intent photointent = new Intent();
                                        photointent.setType("image/*");
                                        photointent.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(photointent, PHOTO);
                                    }
                                }
                            })
                            .show();
                    break;
                case R.id.btnweather:
                    intent.setClass(MainActivity.this, Weather.class);
                    startActivity(intent);
                    break;
                case R.id.btnevent:
                    intent.setClass(MainActivity.this, Store.class);
                    startActivity(intent);
                    break;
                case R.id.btnblog:
                    intent.setClass(MainActivity.this, Blogger.class);
                    startActivity(intent);
                    break;
                case R.id.btnsetting:
                    intent.setClass(MainActivity.this, Setting.class);
                    startActivity(intent);
                    break;
            }

        }
    };

    //建立早晚平均溫度的字串
    private String getAvgtemp(String morning, String night) {
        int morninglow = Integer.parseInt(morning.substring(3, 5));
        int morninghigh = Integer.parseInt(morning.substring(8, 10));
        int nightlow = Integer.parseInt(night.substring(3, 5));
        int nighthigh = Integer.parseInt(night.substring(8, 10));
        String avglow = String.valueOf((morninglow + nightlow) / 2);
        String avghigh = String.valueOf((morninghigh + nighthigh) / 2);
        String avgtemp = avglow + "度 ~ " + avghigh + "度";

        return avgtemp;
    }

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == CAMERA || requestCode == PHOTO) && data != null) {
            //取得照片路徑uri
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();

            try {
                //讀取照片，型態為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                // 產生圖片的PHashCode
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();
                InputStream is = new ByteArrayInputStream(bitmapdata);
                try {
                    p.initCoefficients();
                    imagecode = p.getHash(is);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent passintent = new Intent();
                Bundle passbundle = new Bundle();

                passintent.setClass(MainActivity.this, SearchResult.class);
                //把圖片分析碼傳給SearchResult頁面
                passbundle.putString("searchtype", "picturecode");
                passbundle.putString("keyword", imagecode);
                //將Bundle物件傳給passintent
                passintent.putExtras(passbundle);
                startActivity(passintent);
                System.out.println("這張圖片的PHash：" + imagecode);

            } catch (FileNotFoundException e) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
