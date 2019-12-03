package com.wxy.scaleheaderlayout.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    //缓存View,因为Navigation切换Fragment是通过replace的，每次都会重新onCreateView，所以缓存住用户操作的东西
    protected View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(getLayoutId(), container,false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent则从parent删除，防止发生这个rootview已经有parent的错误。
        ViewGroup mViewGroup = (ViewGroup) view.getParent();
        if (mViewGroup != null) {
            mViewGroup.removeView(view);
        }
        ButterKnife.bind(this,view);
        initView();
        return view;
    }
    public abstract int getLayoutId();
    public abstract void initView();
}
