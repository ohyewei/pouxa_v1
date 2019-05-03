package pouxateam.pouxa;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Vivian on 2016/8/31.
 */
public class KeywordFragmant extends Fragment {

    private ImageButton btnsearchicon, icsearch, icimage, ichome, iclike, icsetting;
    private Button btnclear;
    private ImageView fire;
    private TextView tvtitle, element1, element2, element3;
    private EditText inputcube;
    private TextView[] elementArray = new TextView[3];
    Intent intent = new Intent();

    Trend trend;

    //以圖搜圖
    CreatePHash p;
    String imagecode;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    final ArrayList<String> choose = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //導入Tab分頁的Fragment Layout
        return inflater.inflate(R.layout.keyword_search_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


        tvtitle = (TextView) getView().findViewById(R.id.tvtitle);
        setTvtitle();

        inputcube = (EditText) getView().findViewById(R.id.inputcube);
        inputcube.setTextSize(30);
        //inputcube.setCursorVisible(false);
        inputcube.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //按下完成鍵要執行的動作
                intent.setClass(getActivity(), SearchResult.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("searchtype", "productname");
                bundle.putString("keyword", inputcube.getText().toString());
                //將Bundle物件傳給intent
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }
        });

        btnsearchicon = (ImageButton) getView().findViewById(R.id.btnsearchicon);

        btnclear = (Button) getView().findViewById(R.id.btnclear);
        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputcube.setText("");
            }
        });
        fire = (ImageView) getView().findViewById(R.id.firegif);
        fire.setBackgroundResource(R.drawable.hot_gif);
        AnimationDrawable frameAnimation =
                (AnimationDrawable) fire.getBackground();
        frameAnimation.start();

        trend = new Trend(getActivity());
        element1 = (TextView) getView().findViewById(R.id.element1);
        element2 = (TextView) getView().findViewById(R.id.element2);
        element3 = (TextView) getView().findViewById(R.id.element3);
        elementArray[0] = element1;
        elementArray[1] = element2;
        elementArray[2] = element3;
        setTvElement();
        for( TextView tv : elementArray){
            tv.setOnClickListener(tvOnClickListener);
        }

        icsearch = (ImageButton) getView().findViewById(R.id.icsearch);
        icimage = (ImageButton) getView().findViewById(R.id.icimage);
        choose.add("拍攝照片");
        choose.add("從相簿選取");
        choose.add("取消");        ichome = (ImageButton) getView().findViewById(R.id.ichome);
        iclike = (ImageButton) getView().findViewById(R.id.iclike);
        icsetting = (ImageButton) getView().findViewById(R.id.icsetting);
        ImageButton[] ibtnres = {icsearch, icimage, ichome, iclike, icsetting, btnsearchicon};
        for (ImageButton ib : ibtnres) {
            ib.setOnClickListener(ibtnOnClickListener);
        }

        p = new CreatePHash();
        
    }


    protected void setTvtitle() {
        Thread thread = new Thread(new Runnable() {
            Drawable drawable = getResources().getDrawable(R.drawable.btn_search);

            @Override
            public void run() {
                //建立一個ImageSpan元件並帶入要插入的圖片
                drawable.setBounds(0, 0, 150, 150);             //設置圖片大小
                ImageSpan mImageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

                //建立一個SpannableString元件並帶入要顯示的文字字串
                SpannableString mSpannableString = new SpannableString(" 商品搜尋");

                //插入mImageSpan圖片，並指定在字串裡的第22個位置到23個位置進行插入 (總字串長度為23，圖片插入位置為22-23的位置)
                mSpannableString.setSpan(mImageSpan, 0, 1, 0);

                //將組合後的文字圖片放入TextView裡
                tvtitle.setText(mSpannableString);
            }
        });
        thread.start();
    }

    //取得流行元素
    protected void setTvElement() {
        trend.getTrendData(new Trend.VolleyCallback() {
            @Override
            public void onSuccess(String[] trendArray) {
                for (int i = 0; i < 3; i++) {
                    elementArray[i].setText(trendArray[i]);
                }
            }
        });

    }

    private View.OnClickListener ibtnOnClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icsearch:
                    break;
                case R.id.icimage:
                    new AlertDialog.Builder(getActivity())
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
                    intent.setClass(getActivity(), MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.iclike:
                    intent.setClass(getActivity(), Like.class);
                    startActivity(intent);
                    break;
                case R.id.icsetting:
                    intent.setClass(getActivity(), Setting.class);
                    startActivity(intent);
                    break;
                case R.id.btnsearchicon:
                    intent.setClass(getActivity(), SearchResult.class);
                    //new一個Bundle物件，並將要傳遞的資料傳入
                    Bundle bundle = new Bundle();
                    bundle.putString("searchtype", "productname");
                    bundle.putString("keyword", inputcube.getText().toString());
                    //將Bundle物件傳給intent
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        }
    };

    private View.OnClickListener tvOnClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            intent.setClass(getActivity(), SearchResult.class);
            //new一個Bundle物件，並將要傳遞的資料傳入
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.element1:
                    bundle.putString("searchtype", "productname");
                    bundle.putString("keyword", element1.getText().toString());
                    break;
                case R.id.element2:
                    bundle.putString("searchtype", "productname");
                    bundle.putString("keyword", element2.getText().toString());
                    break;
                case R.id.element3:
                    bundle.putString("searchtype", "productname");
                    bundle.putString("keyword", element3.getText().toString());
                    break;
            }

            //將Bundle物件傳給intent
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == CAMERA || requestCode == PHOTO) && data != null) {
            //取得照片路徑uri
            Uri uri = data.getData();
            ContentResolver cr = getActivity().getContentResolver();

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

                passintent.setClass(getActivity(), SearchResult.class);
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
