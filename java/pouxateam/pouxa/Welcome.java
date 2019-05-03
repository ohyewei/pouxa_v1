package pouxateam.pouxa;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Welcome extends AppCompatActivity {

    ImageView img1, img2, img3, img4;
    RelativeLayout rl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/typeface.otf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        rl = (RelativeLayout) findViewById(R.id.activity_welcome);
        img1 = (ImageView) findViewById(R.id.imageView);
        img2 = (ImageView) findViewById(R.id.imageView2);
        img3 = (ImageView) findViewById(R.id.imageView3);
        img4 = (ImageView) findViewById(R.id.imageView4);


        Animation out1 = AnimationUtils.loadAnimation(Welcome.this, R.anim.push_up_out1);
        Animation out2 = AnimationUtils.loadAnimation(Welcome.this, R.anim.push_up_out2);
        Animation out3 = AnimationUtils.loadAnimation(Welcome.this, R.anim.push_up_out3);
        Animation out4 = AnimationUtils.loadAnimation(Welcome.this, R.anim.push_up_out4);

        //點螢幕跳過動畫
        rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){ //按下螢幕
                    Welcome.this.mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 0);//直接跳轉
                }
                return false;
            }
        });

        img4.startAnimation(out4);
        img1.startAnimation(out1);
        img2.startAnimation(out2);
        img3.startAnimation(out3);



        Welcome.this.mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 9000);//9秒跳轉

    }

    private static final int GOTO_MAIN_ACTIVITY = 0;

    //畫面自動跳轉
    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GOTO_MAIN_ACTIVITY:

                    Intent intent = new Intent(Welcome.this, MainActivity.class);
                    //startActivity(intent);
                    //頁面轉換效果
                    startActivityForResult(intent,11);
                    //overridePendingTransition(進入下一頁的特效id,退出當前頁面的特效id)
                    overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
                    //finish();
                    break;

                default:
                    break;
            }
        } };
}
