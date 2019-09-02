package com.fmtch.mine.mvp.view.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.GlideLoadUtils;
import com.fmtch.base.utils.ImageUtil;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;
import com.fmtch.mine.mvp.view.customview.AuthSubmitDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

@Route(path = RouterMap.AUTH)
public class AuthActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.et_name)
    EditText etName;
    @BindView(R2.id.et_id_card_num)
    EditText etIdCardNum;
    @BindView(R2.id.iv_card_front)
    ImageView ivCardFront;
    @BindView(R2.id.iv_card_opposite)
    ImageView ivCardOpposite;
    @BindView(R2.id.iv_hand_card_front)
    ImageView ivHandCardFront;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_auth;
    }

    private int mSelectPos = 0;//0:身份证正面  1:身份证反面 2:手持身份证正面
    private SuperRequest mRequest;
    private static final int IMAGE_PICKER_REQUEST_CODE = 0x1000;

    @Override
    protected void initView() {
        super.initView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRequest = new SuperRequest();
    }

    /**
     * 提交实名认证
     */
    @OnClick(R2.id.btn_submit)
    public void onBtnSubmitClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        String name = etName.getEditableText().toString();
        String card_id = etIdCardNum.getEditableText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showLongToast(R.string.mine_please_input_real_name);
            return;
        }
        if (TextUtils.isEmpty(card_id)) {
            ToastUtils.showLongToast(R.string.mine_please_input_real_id_card);
            return;
        }
        if (TextUtils.isEmpty(mRequest.getPassport_front())) {
            ToastUtils.showLongToast(R.string.mine_please_upload_card_front);
            return;
        }
        if (TextUtils.isEmpty(mRequest.getPassport_back())) {
            ToastUtils.showLongToast(R.string.mine_please_upload_card_back);
            return;
        }
        if (TextUtils.isEmpty(mRequest.getPassport_image())) {
            ToastUtils.showLongToast(R.string.mine_please_upload_hand_card_front);
            return;
        }
        mRequest.setName(name);
        mRequest.setPassport_id(card_id);
        mRequest.setCountry("CN");
        mRequest.setPassport_type("0");
        RequestUtil.requestPost(API.SUBMIT_KYC, mRequest, new OnResponseListenerImpl() {
            @Override
            public void onNext(SuperResponse response) {
                super.onNext(response);
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        UserLoginInfo userLoginInfo = realm.where(UserLoginInfo.class).findFirst();
                        if (userLoginInfo != null) {
                            userLoginInfo.setKyc_status(2);//待审核状态
                            realm.copyToRealmOrUpdate(userLoginInfo);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        new AuthSubmitDialog(AuthActivity.this)
                                .setCancelable(false)
                                .setOnOkClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                })
                                .builder()
                                .show();
                    }
                });
            }
        }, AuthActivity.this, true, getString(R.string.mine_loading_submit));
    }

    @OnClick({R2.id.fl_card_front, R2.id.fl_card_opposite, R2.id.fl_hand_card_front})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.fl_card_front) {
            //点击身份证正面
            mSelectPos = 0;
        } else if (id == R.id.fl_card_opposite) {
            //点击身份证反面
            mSelectPos = 1;
        } else if (id == R.id.fl_hand_card_front) {
            //点击手持身份证正面
            mSelectPos = 2;
        }
        openPhotos();
    }

    private void openPhotos() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setCrop(false);
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE);
    }

    private String filePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照功能或者裁剪后返回
        if (data != null && requestCode == IMAGE_PICKER_REQUEST_CODE) {
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);

            filePath = images.get(images.size() - 1).path;
            RequestUtil.uploadFile(this, filePath, new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    switch (mSelectPos) {
                        case 0:
                            //身份证正面
                            mRequest.setPassport_front(response.getDownload_url());
                            GlideLoadUtils.getInstance().glideLoad(AuthActivity.this, filePath, ivCardFront);
                            break;
                        case 1:
                            //身份证反面
                            mRequest.setPassport_back(response.getDownload_url());
                            GlideLoadUtils.getInstance().glideLoad(AuthActivity.this, filePath, ivCardOpposite);
                            break;
                        case 2:
                            //手持身份证正面
                            mRequest.setPassport_image(response.getDownload_url());
                            GlideLoadUtils.getInstance().glideLoad(AuthActivity.this, filePath, ivHandCardFront);
                            break;
                    }
                }
            });
        }
    }

}
