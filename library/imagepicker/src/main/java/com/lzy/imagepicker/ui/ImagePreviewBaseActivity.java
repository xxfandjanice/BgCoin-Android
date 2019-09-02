package com.lzy.imagepicker.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.imagepicker.DataHolder;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.adapter.ImagePageAdapter;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.util.Utils;
import com.lzy.imagepicker.view.ViewPagerFixed;

import java.util.ArrayList;

/**
 * ================================================
 * ��    �ߣ�jeasonlzy������Ң Github��ַ��https://github.com/jeasonlzy0216
 * ��    ����1.0
 * �������ڣ�2016/5/19
 * ��    ����
 * �޶���ʷ��ͼƬԤ���Ļ���
 * ================================================
 */
public abstract class ImagePreviewBaseActivity extends ImageBaseActivity {

    protected ImagePicker imagePicker;
    protected ArrayList<ImageItem> mImageItems;      //��ת��ImagePreviewFragment��ͼƬ�ļ���
    protected int mCurrentPosition = 0;              //��ת��ImagePreviewFragmentʱ����ţ��ڼ���ͼƬ
    protected TextView mTitleCount;                  //��ʾ��ǰͼƬ��λ��  ����  5/31
    protected ArrayList<ImageItem> selectedImages;   //�����Ѿ�ѡ�е�ͼƬ
    protected View content;
    protected View topBar;
    protected ViewPagerFixed mViewPager;
    protected ImagePageAdapter mAdapter;
    protected boolean isFromItems = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        mCurrentPosition = getIntent().getIntExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        isFromItems = getIntent().getBooleanExtra(ImagePicker.EXTRA_FROM_ITEMS, false);

        if (isFromItems) {
            // ��˵�����ᵼ�´���ͼƬ����
            mImageItems = (ArrayList<ImageItem>) getIntent().getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
        } else {
            // ������������ûᵼ��Ԥ������
            mImageItems = (ArrayList<ImageItem>) DataHolder.getInstance().retrieve(DataHolder.DH_CURRENT_IMAGE_FOLDER_ITEMS);
        }

        imagePicker = ImagePicker.getInstance();
        selectedImages = imagePicker.getSelectedImages();

        //��ʼ���ؼ�
        content = findViewById(R.id.content);

        //��Ϊ״̬��͸���󣬲�����������ƣ����Ը�ͷ������״̬����marginֵ����֤ͷ�����ᱻ����
        topBar = findViewById(R.id.top_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }
        topBar.findViewById(R.id.btn_ok).setVisibility(View.GONE);
        topBar.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitleCount = (TextView) findViewById(R.id.tv_des);

        mViewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        mAdapter = new ImagePageAdapter(this, mImageItems);
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);

        //��ʼ����ǰҳ���״̬
        mTitleCount.setText(getString(R.string.ip_preview_image_count, mCurrentPosition + 1, mImageItems.size()));
    }

    /** ����ʱ������ͷ��β */
    public abstract void onImageSingleTap();

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ImagePicker.getInstance().restoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ImagePicker.getInstance().saveInstanceState(outState);
    }
}