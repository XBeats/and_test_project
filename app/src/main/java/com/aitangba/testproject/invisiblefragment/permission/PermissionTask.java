package com.aitangba.testproject.invisiblefragment.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.view.View;

import com.aitangba.testproject.invisiblefragment.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class PermissionTask {
    private static final String TAG_INNER_FRAGMENT = "PermissionFragment";
    private final Context mContext;
    private final FragmentManager mFragmentManager;

    public PermissionTask(FragmentActivity fragmentActivity) {
        this.mContext = fragmentActivity;
        this.mFragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    public PermissionTask(Fragment fragment) {
        this.mContext = fragment.getContext();
        this.mFragmentManager = fragment.getChildFragmentManager();
    }

    public PermissionTask(View view) {
        this.mContext = view.getContext();
        this.mFragmentManager = ViewUtils.findFragmentManager(view);
    }

    public void start(@NonNull String... permissions) {
        List<String> grantedList = new ArrayList<>();
        List<String> deniedList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                boolean granted = ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
                if (granted) {
                    grantedList.add(permission);
                } else {
                    deniedList.add(permission);
                }
            }

            if (grantedList.size() == permissions.length) {
                onSuccess();
            } else if (deniedList.size() > 0) {
                List<Fragment> fragmentList = mFragmentManager.getFragments();
                PermissionFragment permissionFragment = null;
                for (Fragment fragment : fragmentList) {
                    if (fragment != null && fragment instanceof PermissionFragment) {
                        permissionFragment = (PermissionFragment) fragment;
                        break;
                    }
                }

                if (permissionFragment == null) {
                    permissionFragment = (PermissionFragment) Fragment.instantiate(mContext, PermissionFragment.class.getName());
                    mFragmentManager.beginTransaction().add(permissionFragment, TAG_INNER_FRAGMENT).commitAllowingStateLoss();
                }
                permissionFragment.add(this, deniedList);
            }
        } else {
            onSuccess();
        }
    }

    protected void onSuccess() {}

    protected void onFailed(){}

    public static class PermissionFragment extends Fragment {
        private static final int INIT_CODE = -1;

        private int mReqCode = INIT_CODE;
        private PermissionTask mPermissionTask;
        private List<String> mDeniedList = new ArrayList<>();

        public void add(PermissionTask permissionTask, List<String> deniedList) {
            mPermissionTask = permissionTask;
            mDeniedList.clear();
            mDeniedList.addAll(deniedList);
            mReqCode = (mReqCode + 1) % 100;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestPermissions(mDeniedList.toArray(new String[mDeniedList.size()]), mReqCode);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (mReqCode == INIT_CODE || mReqCode != requestCode) {
                return;
            }

            List<String> grantedList = new ArrayList<>();
            List<String> deniedList = new ArrayList<>();

            for (int i = 0; i < grantResults.length; i++) {
                boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (granted) {
                    grantedList.add(permissions[i]);
                } else {
                    deniedList.add(permissions[i]);
                }
            }

            if (deniedList.size() > 0) {
                mPermissionTask.onFailed();
            } else if (grantedList.size() > 0) {
                mPermissionTask.onSuccess();
            }

            mPermissionTask.mFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }
}

