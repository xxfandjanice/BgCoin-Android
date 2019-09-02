package com.fmtch.mine.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.utils.AndroidShare;
import com.fmtch.base.utils.FileUtils;
import com.fmtch.base.utils.ShotViewUtil;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.utils.ZXingUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.pojo.response.InviteInfo;

import java.util.ArrayList;
import java.util.List;


public class InviteSharePicDialog implements View.OnClickListener {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗

    private String mInviteUrl;

    private Bitmap mDownUrlBitmap;
    private Bitmap mShareBigmap;
    private AndroidShare androidShare;

    public InviteSharePicDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_invite_pic, null);
        androidShare = new AndroidShare(context);
    }

    public InviteSharePicDialog setInviteUrl(String url) {
        this.mInviteUrl = url;
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(true);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        Window dialogWindow = dialog.getWindow();
        WindowManager windowManager = dialogWindow.getWindowManager();
        dialogWindow.setWindowAnimations(R.style.BottomDialogInAndOutAnim);
        dialogWindow.setGravity(Gravity.BOTTOM);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialogWindow.setAttributes(lp);

        RecyclerView rv = mDialogLayout.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        final BaseQuickPageStateAdapter<InviteInfo, BaseViewHolder> adapter = new BaseQuickPageStateAdapter<InviteInfo, BaseViewHolder>(mContext, R.layout.item_invite) {
            @Override
            protected void convert(final BaseViewHolder helper, InviteInfo item) {
                switch (item.getPos()) {
                    case 0:
                        helper.setBackgroundRes(R.id.rl_share_bg, R.drawable.bg_share_first);
                        break;
                    case 1:
                        helper.setBackgroundRes(R.id.rl_share_bg, R.drawable.bg_share_second);
                        break;
                    case 2:
                        helper.setBackgroundRes(R.id.rl_share_bg, R.drawable.bg_share_third);
                        break;
                    case 3:
                        helper.setBackgroundRes(R.id.rl_share_bg, R.drawable.bg_share_four);
                        break;
                    default:
                        helper.setBackgroundRes(R.id.rl_share_bg, R.drawable.bg_share_first);
                        break;
                }
                if (item.getStatus() == 1) {
                    helper.setGone(R.id.view_select_bg, true);
                } else {
                    helper.setGone(R.id.view_select_bg, false);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mDownUrlBitmap == null)
                            mDownUrlBitmap = ZXingUtils.Create2DCode(mInviteUrl, helper.getView(R.id.iv_qr_code).getWidth(), helper.getView(R.id.iv_qr_code).getHeight());
                        helper.setImageBitmap(R.id.iv_qr_code, mDownUrlBitmap);
                    }
                }, 100);
            }
        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<InviteInfo> data = adapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    if (i == position) {
                        data.get(i).setStatus(1);
                    } else {
                        data.get(i).setStatus(0);
                    }
                }
                mShareBigmap = ShotViewUtil.getViewBp(view.findViewById(R.id.rl_share_bg));
                adapter.notifyDataSetChanged();
            }
        });
        rv.setAdapter(adapter);
        List<InviteInfo> data = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            data.add(new InviteInfo(i));
        }
        adapter.setNewData(data);

        mDialogLayout.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mDialogLayout.findViewById(R.id.tv_wechat).setOnClickListener(this);
        mDialogLayout.findViewById(R.id.tv_moments).setOnClickListener(this);
        mDialogLayout.findViewById(R.id.tv_telegram).setOnClickListener(this);
        mDialogLayout.findViewById(R.id.tv_download).setOnClickListener(this);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        if (mShareBigmap == null) {
            ToastUtils.showLongToast(R.string.mine_please_select_one);
            return;
        }
        dialog.dismiss();
        int id = v.getId();
        if (id == R.id.tv_wechat) {
            androidShare.shareWeChatFriend("", "", AndroidShare.DRAWABLE, mShareBigmap);
        } else if (id == R.id.tv_moments) {
            androidShare.shareWeChatFriendCircle("", "", mShareBigmap);
        } else if (id == R.id.tv_telegram) {
            androidShare.shareTelegram("", "", mShareBigmap);
        } else if (id == R.id.tv_download) {
            FileUtils.savePic(mContext, mShareBigmap);
        }
    }
}
