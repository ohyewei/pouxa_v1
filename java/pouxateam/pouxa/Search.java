package pouxateam.pouxa;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Search extends FragmentActivity {

    private FrameLayout rlBackgroundPanel = null;
    private BitmapDrawable bmpDrawImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/typeface.otf");

        fnSetBackground(); // 呼叫設置背景函數

        //獲取TabHost控制元件
        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        //設定Tab頁面的顯示區域，帶入Context、FragmentManager、Container ID
        mTabHost.setup(this, getSupportFragmentManager(), R.id.container);


        //新增兩個Tab
        mTabHost.addTab(mTabHost.newTabSpec("keyword")
                        .setIndicator("關鍵字搜尋")
                ,KeywordFragmant.class,null);
        TextView x = (TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        x.setTextSize(23);

        mTabHost.addTab(mTabHost.newTabSpec("catalog")
                        .setIndicator("分類搜尋")
                ,CatalogFragment.class,null);

        TextView y = (TextView) mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        y.setTextSize(23);

    }

    /*
    @Override
    public void onBackPressed() {
        getSupportFragmentManager().addOnBackStackChangedListener(getListener());
    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    if (backStackEntryCount == 0) {
                        finish();
                    }
                    Fragment fragment = manager.getFragments()
                            .get(backStackEntryCount - 1);
                    fragment.onResume();
                }
            }
        };
        return result;
    }*/

    //設置背景函數
    public void fnSetBackground() {
        // 先取得RelativeLayout
        rlBackgroundPanel = (FrameLayout) findViewById(R.id.container);
        // 取得該張圖片，並放置在變數bmpDrawImg
        bmpDrawImg = new BitmapDrawable(getResources().openRawResource(+R.drawable.bg_all));
        // 最後就是設定圖片
        rlBackgroundPanel.setBackgroundDrawable(bmpDrawImg);
    }
}
