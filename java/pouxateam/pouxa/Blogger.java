package pouxateam.pouxa;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Blogger extends AppCompatActivity {

    private TextView tvtitle, zoeylike,ananchenlike, bensheelike, eyeslike, millyqlike, stellahyclike,
            tramylike, youwinlike, loislinlinlike, rockyrocketlike, hannnlike,
            cuterosalindlike, lsberrywlike, mavismaylike, michellebuylike ;

    private Button ananchen, benshee, eyes, millyq, stellahyc, tramy, youwin, loislinlin, rockyrocket,
            hannn, cuterosalind, lsberryw, mavismay, michellebuy, zoey;
    private ImageButton icsearch, icimage, ichome, iclike, icsetting;
    private RelativeLayout rlBackgroundPanel = null;
    private BitmapDrawable bmpDrawImg = null;
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    private BtnOnClickListener btnOnClickListener = new BtnOnClickListener();
    private IbtnOnClickListener ibtnOnClickListener = new IbtnOnClickListener();

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
        setContentView(R.layout.activity_blogger);

        findView();
        fnSetBackground();
        setTvtitle();
        setLikeCount();


        //監聽事件
        Button[] btnres = {ananchen, benshee, eyes, millyq, stellahyc, tramy, youwin, loislinlin, rockyrocket,
                hannn, cuterosalind, lsberryw, mavismay, michellebuy, zoey,};
        for (Button b : btnres) {
            b.setOnClickListener(btnOnClickListener);
        }
        ImageButton[] ibtnres = {icsearch, icimage, ichome, iclike, icsetting};
        choose.add("拍攝照片");
        choose.add("從相簿選取");
        choose.add("取消");
        for (ImageButton ib : ibtnres) {
            ib.setOnClickListener(ibtnOnClickListener);
        }

        p = new CreatePHash();

    }

    public void fnSetBackground() {
        // 先取得RelativeLayout
        rlBackgroundPanel = (RelativeLayout) findViewById(R.id.activity_blogger);
        // 取得該張圖片，並放置在變數bmpDrawImg
        bmpDrawImg = new BitmapDrawable(getResources().openRawResource(+R.drawable.bg_all));
        // 最後就是設定圖片
        rlBackgroundPanel.setBackgroundDrawable(bmpDrawImg);
    }
    protected void findView(){

        tvtitle = (TextView) findViewById(R.id.tvtitle);
        ananchen = (Button) findViewById(R.id.ananchen);
        ananchenlike = (TextView) findViewById(R.id.ananchenlike);
        benshee = (Button) findViewById(R.id.benshee);
        bensheelike = (TextView) findViewById(R.id.bensheelike);
        eyes = (Button) findViewById(R.id.eyes);
        eyeslike = (TextView) findViewById(R.id.eyeslike);
        millyq = (Button) findViewById(R.id.millyq);
        millyqlike = (TextView) findViewById(R.id.millyqlike);
        stellahyc = (Button) findViewById(R.id.stellahyc);
        stellahyclike = (TextView) findViewById(R.id.stellahyclike);
        tramy = (Button) findViewById(R.id.tramy);
        tramylike = (TextView) findViewById(R.id.tramylike);
        youwin = (Button) findViewById(R.id.youwin);
        youwinlike = (TextView) findViewById(R.id.youwinlike);
        loislinlin = (Button) findViewById(R.id.loislinlin);
        loislinlinlike = (TextView) findViewById(R.id.loislinlinlike);
        rockyrocket = (Button) findViewById(R.id.rockyrocket);
        rockyrocketlike = (TextView) findViewById(R.id.rockyrocketlike);
        hannn = (Button) findViewById(R.id.hannn);
        hannnlike = (TextView) findViewById(R.id.hannnlike);
        cuterosalind = (Button) findViewById(R.id.cuterosalind);
        cuterosalindlike = (TextView) findViewById(R.id.cuterosalindlike);
        lsberryw = (Button) findViewById(R.id.lsberryw);
        lsberrywlike = (TextView) findViewById(R.id.lsberrywlike);
        mavismay = (Button) findViewById(R.id.mavismay);
        mavismaylike = (TextView) findViewById(R.id.mavismaylike);
        michellebuy = (Button) findViewById(R.id.michellebuy);
        michellebuylike = (TextView) findViewById(R.id.michellebuylike);
        zoey = (Button) findViewById(R.id.zoey);
        zoeylike = (TextView) findViewById(R.id.zoeylike);

        icsearch = (ImageButton) findViewById(R.id.icsearch);
        icimage = (ImageButton) findViewById(R.id.icimage);
        ichome = (ImageButton) findViewById(R.id.ichome);
        iclike = (ImageButton) findViewById(R.id.iclike);
        icsetting = (ImageButton) findViewById(R.id.icsetting);
    }
    protected void setTvtitle() {
        //建立一個ImageSpan元件並帶入要插入的圖片
        Drawable drawable = getResources().getDrawable(R.drawable.btn_blogger);
        drawable.setBounds(0, 0, 144, 144);             //設置圖片大小
        ImageSpan mImageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

        //建立一個SpannableString元件並帶入要顯示的文字字串
        SpannableString mSpannableString = new SpannableString(" 部落客穿搭");

        //插入mImageSpan圖片，並指定在字串裡的第22個位置到23個位置進行插入 (總字串長度為23，圖片插入位置為22-23的位置)
        mSpannableString.setSpan(mImageSpan, 0, 1, 0);

        //將組合後的文字圖片放入TextView裡
        tvtitle.setText(mSpannableString);
    }

    protected void setLikeCount(){

        TextView[] tvres = {zoeylike,ananchenlike, bensheelike, eyeslike, millyqlike, stellahyclike,
                tramylike, youwinlike, loislinlinlike, rockyrocketlike, hannnlike,
                cuterosalindlike, lsberrywlike, mavismaylike, michellebuylike};

        //建立一個ImageSpan元件並帶入要插入的圖片
        Drawable drawable = getResources().getDrawable(R.drawable.img_like);
        drawable.setBounds(0, 0, 60, 60);             //設置圖片大小
        ImageSpan mImageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

        for( int  i = 0 ; i < tvres.length ; i++ ){
            //建立一個SpannableString元件並帶入要顯示的文字字串
            SpannableString mSpannableString = new SpannableString(tvres[i].getText().toString());
            //插入mImageSpan圖片，並指定在字串裡的第22個位置到23個位置進行插入 (總字串長度為23，圖片插入位置為22-23的位置)
            mSpannableString.setSpan(mImageSpan, 0, 1, 0);
            //將組合後的文字圖片放入TextView裡
            tvres[i].setText(mSpannableString);
        }

    }

    final class BtnOnClickListener implements View.OnClickListener {

        final public void onClick(final View v) {
            Thread thread = new Thread(new Runnable() {
                @Override
               public void run() {
                    switch (v.getId()) {
                        case R.id.ananchen:
                            bundle.putString("blogger", "ananchen");
                            break;
                        case R.id.benshee:
                            bundle.putString("blogger", "benshee");
                            break;
                        case R.id.eyes:
                            bundle.putString("blogger", "eyes");
                            break;
                        case R.id.millyq:
                            bundle.putString("blogger", "millyq");
                            break;
                        case R.id.stellahyc:
                            bundle.putString("blogger", "stellahyc");
                            break;
                        case R.id.tramy:
                            bundle.putString("blogger", "tramy");
                            break;
                        case R.id.youwin:
                            bundle.putString("blogger", "youwin");
                            break;
                        case R.id.loislinlin:
                            bundle.putString("blogger", "loislinlin");
                            break;
                        case R.id.rockyrocket:
                            bundle.putString("blogger", "rockyrocket");
                            break;
                        case R.id.hannn:
                            bundle.putString("blogger", "hannn");
                            break;
                        case R.id.cuterosalind:
                            bundle.putString("blogger", "cuterosalind");
                            break;
                        case R.id.lsberryw:
                            bundle.putString("blogger", "lsberryw");
                            break;
                        case R.id.mavismay:
                            bundle.putString("blogger", "mavismay");
                            break;
                        case R.id.michellebuy:
                            bundle.putString("blogger", "michellebuy");
                            break;
                        case R.id.zoey:
                            bundle.putString("blogger", "zoey");
                            break;
                    }

                    intent.setClass(Blogger.this, Article.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            thread.start();
        }
    }

    class IbtnOnClickListener implements View.OnClickListener {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icsearch:
                    intent.setClass(Blogger.this, Search.class);
                    startActivity(intent);
                    break;
                case R.id.icimage:
                    new AlertDialog.Builder(Blogger.this)
                            .setItems(choose.toArray(new String[choose.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    if(choose.get(position).equals("拍攝照片"))
                                        //開啟相機
                                        ;
                                    else if(choose.get(position).equals("從相簿選取")){
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
                case R.id.ichome:
                    intent.setClass(Blogger.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.iclike:
                    intent.setClass(Blogger.this, Like.class);
                    startActivity(intent);
                    break;
                case R.id.icsetting:
                    intent.setClass(Blogger.this, Setting.class);
                    startActivity(intent);
                    break;
            }
        }
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

                passintent.setClass(Blogger.this, SearchResult.class);
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
    //釋放空間
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 每個Drawable被加到VIEW上面都會產生一個callback，所以在recycle圖片之前，必須先把callback設成null
        // 設成null以後，背景圖片自然就會不見，就會變成黑的背景。bmpDrawImg的狀態就會是沒有被使用中。
        rlBackgroundPanel.getBackground().setCallback(null);
        // 先判斷bmpDrawImg 是否為null，如果不是null，且bmpDrawImg 還沒有被recycle的話就進行recycle
        if (null != bmpDrawImg && !bmpDrawImg.getBitmap().isRecycled()) {
            bmpDrawImg.getBitmap().recycle();
        }
        System.gc();
    }

}
