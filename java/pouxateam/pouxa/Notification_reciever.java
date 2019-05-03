package pouxateam.pouxa;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Vivian on 2016/8/16.
 */
public class Notification_reciever extends BroadcastReceiver {


    SharedPreferences prefs;
    Gson gson = new Gson();

    NotificationManager notificationManager;

    Intent repeating_intent;
    PendingIntent pendingIntent;

    Context mcontext;

    private String strCity, strDate, morning, night;

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("開啟Notification_reciever");


        mcontext = context;

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        strCity = prefs.getString("city", "臺北市");
        System.out.println("地區：" + strCity);

        SimpleDateFormat sDateFormat = new SimpleDateFormat("MM/dd");
        strDate = sDateFormat.format(new java.util.Date());
        System.out.println("系統日期:" + strDate);


        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //當使用者按下通知欄中的通知時要開啟的 Activity
        repeating_intent = new Intent(context, Weather.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //建立待處理意圖
        pendingIntent = PendingIntent.getActivity(context, 888, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            strCity = URLEncoder.encode(strCity, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try{
            CityWeather cityWeather = new CityWeather(mcontext);
            cityWeather.getCityWeatherData(new CityWeather.VolleyCallback() {
                @Override
                public void onSuccess(ArrayList<CityWeatherResultIdioms> allData) {
                    morning = allData.get(0).getMorning();
                    night = allData.get(0).getNight();
                    String avgtemp = getAvgtemp(morning,night);
                    String title = "今日" + URLDecoder.decode(strCity) + "天氣：" + avgtemp;
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true);
                    getRecommand(morning,night,builder);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    //建立建議穿搭的字串
    private void getRecommand(String morning, String night, final NotificationCompat.Builder builder) {
        int morninglow = Integer.parseInt(morning.substring(3,5));
        int morninghigh = Integer.parseInt(morning.substring(8,10));
        int nightlow = Integer.parseInt(night.substring(3,5));
        int nighthigh = Integer.parseInt(night.substring(8,10));
        int avglow = (morninglow+nightlow)/2 ;
        int avghigh = (morninghigh+nighthigh)/2;
        final int temp = (avghigh+avglow)/2;
        String content = "";
        final String[] hotItem = {"短袖", "短褲", "背心", "裙子"};
        final String[] coldItem = {"長袖", "長褲", "外套"};
        final Random random = new Random();
        Trend trend = new Trend(mcontext);

        trend.getTrendData(new Trend.VolleyCallback() {
            public void onSuccess(String[] trendArray) {

                String s;
                if (temp > 27) {
                    s = "今天穿上"+trendArray[random.nextInt(10)] + hotItem[random.nextInt(4)]+"吧！";
                } else {
                    s = "今天穿上"+trendArray[random.nextInt(10)] + coldItem[random.nextInt(3)]+"吧！";
                }

                System.out.println("Content："+s);
                builder.setContentText(s);
                notificationManager.notify(888, builder.build());
            }
        });

    }

    //建立早晚平均溫度的字串
    private String getAvgtemp(String morning, String night) {
        int morninglow = Integer.parseInt(morning.substring(3,5));
        int morninghigh = Integer.parseInt(morning.substring(8,10));
        int nightlow = Integer.parseInt(night.substring(3,5));
        int nighthigh = Integer.parseInt(night.substring(8,10));
        String avglow = String.valueOf((morninglow+nightlow)/2) ;
        String avghigh = String.valueOf((morninghigh+nighthigh)/2) ;
        String avgtemp = avglow +"度 ~ " + avghigh +"度";

        return avgtemp;
    }
}

