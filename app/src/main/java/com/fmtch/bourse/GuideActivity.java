package com.fmtch.bourse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparentForImageView(this, null);
        setContentView(R.layout.activity_guide);
        ViewPager viewPager = findViewById(R.id.view_pager);
        List<View> mViewList = new ArrayList<>();
        LayoutInflater lf = LayoutInflater.from(this);
        View view1 = lf.inflate(R.layout.view_guide_first, null);
        View view2 = lf.inflate(R.layout.view_guide_second, null);
        View view3 = lf.inflate(R.layout.view_guide_third, null);
        view3.findViewById(R.id.tv_finish).setOnClickListener(v -> {
            SpUtils.put(KeyConstant.KEY_FIRST_OPEN,false);
            ARouter.getInstance().build(RouterMap.MAIN_PAGE).navigation();
            finish();
        });
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);
        viewPager.setAdapter(new ViewPagerAdatper(mViewList));
    }

    public class ViewPagerAdatper extends PagerAdapter {
        private List<View> mViewList ;

        public ViewPagerAdatper(List<View> mViewList ) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }

}
