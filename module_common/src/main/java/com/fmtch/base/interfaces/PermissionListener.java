package com.fmtch.base.interfaces;

import java.util.List;

/**
 * 权限回调接口
 */
public interface PermissionListener {
    void onGranted();

    void onDenied(List<String> deniedPermissions);
}
