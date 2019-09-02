package com.fmtch.base.widget.dialog;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.R;
import com.fmtch.base.event.BottomDialogItem;
import com.fmtch.base.utils.DialogUtils;

import java.util.List;

/**
 * Created by wtc on 2019/6/27
 */
public class BottomListDialog {

    public static BottomSheetDialog showBottomListDialog(final Context context, final List<BottomDialogItem> list, final BottomDialogItemOnClickListener listener) {
        if (list == null || list.size() == 0) {
            return null;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_list, null);
        final BottomSheetDialog dialog = DialogUtils.showDetailBottomDialog(context, view);

        RecyclerView rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(context));
        BaseQuickAdapter adapter = new BaseQuickAdapter<BottomDialogItem, BaseViewHolder>(R.layout.item_dialog_bottom, list) {
            @Override
            protected void convert(BaseViewHolder helper, BottomDialogItem item) {
                helper.setText(R.id.tv_item, item.getText());
                if (item.isSelected()) {
                    helper.setTextColor(R.id.tv_item, context.getResources().getColor(R.color.theme));
                } else {
                    helper.setTextColor(R.id.tv_item, context.getResources().getColor(R.color.color_common_text99));
                }
            }
        };
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (listener != null) {
                    listener.itemOnClick(view, position);
                }
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    public interface BottomDialogItemOnClickListener {
        void itemOnClick(View view, int position);
    }
}
