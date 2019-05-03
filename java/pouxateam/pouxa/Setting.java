package pouxateam.pouxa;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Setting extends PreferenceActivity {

    SharedPreferences prefs;
    AlarmManager alarm;
    PendingIntent pending;
    Calendar calendar;
    String SETTING = "SETTING";

    Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/typeface.otf");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mypreference);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isOn = prefs.getBoolean("notify_switch", false);
        System.out.println("開關："+isOn);
        String strTime = prefs.getString("notify_time", "6");
        System.out.println("推播時間："+strTime);


        //取得今天日期
        SimpleDateFormat dDateFormat = new SimpleDateFormat("dd");
        String date = dDateFormat.format(new java.util.Date());

        //設定行事曆
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strTime));
        //calendar.set(Calendar.DATE,Integer.parseInt(date));
        calendar.set(Calendar.DATE,Integer.parseInt(date)+1);
        System.out.println("行事曆時間："+calendar.get(Calendar.DATE)+"日"+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND));

        //建立意圖
        Intent intent = new Intent();
        //這裡的 this 是指當前的 Activity
        //Notification_reciever.class 則是負責接收的 BroadcastReceiver
        intent.setClass(this, Notification_reciever.class);
        //建立待處理意圖
        pending = PendingIntent.getBroadcast(this, 888, intent, 0);
        //取得AlarmManager
        alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending);

        SwitchPreference switchState = (SwitchPreference) findPreference("notify_switch");
        switchState.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isOn = ((SwitchPreference) preference).isChecked();
                if (isOn == true) {
                    alarm.cancel(pending);
                } else {

                    //設定一個警報
                    //參數1,我們選擇一個會在指定時間喚醒裝置的警報類型
                    //參數2,將指定的時間以millisecond傳入
                    //參數3,傳入待處理意圖
                    alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
                    Toast.makeText(getApplicationContext(),"請設定推播時間及地區", Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

        Preference myLike = findPreference("like");
        myLike.setOnPreferenceClickListener(preferenceClickListener);
        Preference myTutorial = findPreference("tutorial");
        myTutorial.setOnPreferenceClickListener(preferenceClickListener);
        Preference myProblem = findPreference("problem");
        myProblem.setOnPreferenceClickListener(preferenceClickListener);
        Preference myAbout = findPreference("about");
        myAbout.setOnPreferenceClickListener(preferenceClickListener);


    }
    private Preference.OnPreferenceClickListener preferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent browser = new Intent();
            switch(preference.getKey()){
                case "like":
                    //我的收藏
                    System.out.println("點了我的收藏");
                    browser.setClass(Setting.this,Like.class);
                    break;

                case "tutorial":
                    //教學影片
                    Uri video = Uri.parse("https://www.youtube.com/");
                    browser = new Intent(Intent.ACTION_VIEW, video);
                    break;

                case "problem":
                    //E-mail
                    Uri email = Uri.parse("mailto:ohyewei@gmail.com");
                    browser = new Intent(Intent.ACTION_SENDTO, email);
                    break;

                case "about":
                    System.out.println("點了關於我們");
                    browser.setClass(Setting.this,About.class);
                    break;
            }
            startActivity(browser);
            return true;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(SETTING, MODE_PRIVATE);
        SharedPreferences.Editor e = settings.edit();
        e.putBoolean("notify_switch", false)
         .putString("notify_time", "6")
                .putString("city","臺北市").apply();
    }


}
