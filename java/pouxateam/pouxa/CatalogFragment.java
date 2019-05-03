package pouxateam.pouxa;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Vivian on 2016/8/31.
 */
public class CatalogFragment extends Fragment {

    RadioButton top, bottom, jacket;
    RadioGroup catalog;
    TextView tvtitle, sub1, sub2, sub3, sub4, sub5;
    private ImageButton icsearch, icimage, ichome, iclike, icsetting;

    //以圖搜圖
    CreatePHash p;
    String imagecode;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    final ArrayList<String> choose = new ArrayList<>();

    Intent intent;
    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //導入Tab分頁的Fragment Layout
        return inflater.inflate(R.layout.catalog_search_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvtitle = (TextView) getView().findViewById(R.id.tvtitle);
        setTvtitle();

        intent = new Intent();
        bundle = new Bundle();
        //取得元件
        catalog = (RadioGroup) getView().findViewById(R.id.rgroup_catalog);
        top = (RadioButton) getView().findViewById(R.id.rbtn_top);
        bottom = (RadioButton) getView().findViewById(R.id.rbtn_bottom);
        jacket = (RadioButton) getView().findViewById(R.id.rbtn_jacket);
        sub1 = (TextView) getView().findViewById(R.id.subtxt1);
        sub2 = (TextView) getView().findViewById(R.id.subtxt2);
        sub3 = (TextView) getView().findViewById(R.id.subtxt3);
        sub4 = (TextView) getView().findViewById(R.id.subtxt4);
        sub5 = (TextView) getView().findViewById(R.id.subtxt5);
        catalog.setOnCheckedChangeListener(listener);

        TextView[] textViews = {sub1, sub2, sub3, sub4, sub5};
        for (TextView tv : textViews) {
            tv.setOnClickListener(tvOnClickListener);
        }

        icsearch = (ImageButton) getView().findViewById(R.id.icsearch);
        icimage = (ImageButton) getView().findViewById(R.id.icimage);
        choose.add("拍攝照片");
        choose.add("從相簿選取");
        choose.add("取消");
        ichome = (ImageButton) getView().findViewById(R.id.ichome);
        iclike = (ImageButton) getView().findViewById(R.id.iclike);
        icsetting = (ImageButton) getView().findViewById(R.id.icsetting);
        ImageButton[] ibtnres = {icsearch, icimage, ichome, iclike, icsetting};
        for (ImageButton ib : ibtnres) {
            ib.setOnClickListener(ibtnOnClickListener);
        }
    }

    protected void setTvtitle() {
        final Drawable drawable = getResources().getDrawable(R.drawable.btn_search);


        //建立一個ImageSpan元件並帶入要插入的圖片
        drawable.setBounds(0, 0, 150, 150);             //設置圖片大小
        ImageSpan mImageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

        //建立一個SpannableString元件並帶入要顯示的文字字串
        SpannableString mSpannableString = new SpannableString(" 商品搜尋");

        //插入mImageSpan圖片，並指定在字串裡的第0個位置到1個位置進行插入
        mSpannableString.setSpan(mImageSpan, 0, 1, 0);
        //將組合後的文字圖片放入TextView裡
        tvtitle.setText(mSpannableString);


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
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub

            switch (checkedId) {
                case R.id.rbtn_top:
                    sub1.setText("T恤");
                    sub1.setVisibility(View.VISIBLE);
                    sub2.setText("襯衫");
                    sub2.setVisibility(View.VISIBLE);
                    sub3.setText("洋裝");
                    sub3.setVisibility(View.VISIBLE);
                    sub4.setText("毛衣");
                    sub4.setVisibility(View.VISIBLE);
                    sub5.setText("背心");
                    sub5.setVisibility(View.VISIBLE);
                    break;

                case R.id.rbtn_bottom:
                    sub1.setText("短褲");
                    sub1.setVisibility(View.VISIBLE);
                    sub2.setText("長褲");
                    sub2.setVisibility(View.VISIBLE);
                    sub3.setText("裙子");
                    sub3.setVisibility(View.VISIBLE);
                    sub4.setVisibility(View.GONE);
                    sub5.setVisibility(View.GONE);
                    break;

                case R.id.rbtn_jacket:
                    sub1.setText("棉質");
                    sub1.setVisibility(View.VISIBLE);
                    sub2.setText("針織");
                    sub2.setVisibility(View.VISIBLE);
                    sub3.setText("薄外套");
                    sub3.setVisibility(View.VISIBLE);
                    sub4.setVisibility(View.GONE);
                    sub5.setVisibility(View.GONE);
                    break;

            }
        }
    };

    private View.OnClickListener tvOnClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            intent.setClass(getActivity(), SearchResult.class);
            bundle.putString("searchtype", "productname");
            switch (v.getId()) {
                case R.id.subtxt1:
                    if (sub1.getText().equals("T恤")) {
                        bundle.putString("keyword", "上衣");
                    } else if (sub1.getText().equals("短褲")) {
                        bundle.putString("keyword", "短褲");
                    } else if (sub1.getText().equals("棉質")) {
                        bundle.putString("keyword", "棉質");
                    }
                    break;
                case R.id.subtxt2:
                    if (sub2.getText().equals("襯衫")) {
                        bundle.putString("keyword", "襯衫");
                    } else if (sub2.getText().equals("長褲")) {
                        bundle.putString("keyword", "長褲");
                    } else if (sub2.getText().equals("針織")) {
                        bundle.putString("keyword", "針織");
                    }
                    break;
                case R.id.subtxt3:
                    if (sub3.getText().equals("洋裝")) {
                        bundle.putString("keyword", "洋裝");
                    } else if (sub3.getText().equals("裙子")) {
                        bundle.putString("keyword", "裙");
                    } else if (sub3.getText().equals("薄外套")) {
                        bundle.putString("keyword", "薄外套");
                    }
                    break;

                case R.id.subtxt4:
                    bundle.putString("keyword", "毛衣");
                    break;

                case R.id.subtxt5:
                    bundle.putString("keyword", "背心");
                    break;
            }

            try {
                //將Bundle物件傳給intent
                intent.putExtras(bundle);
                startActivity(intent);
            } catch (NullPointerException e) {
                if (bundle.getString("keyword").equals(null)) {
                    Toast.makeText(getActivity(), "請選擇項目", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

}
