package org.arxing.pagemanager;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.arxing.pagemanager.protocol.IPageFragment;


/**
 * <pre>
 * 第一次顯示
 * onAttach
 * onCreate
 * onCreateDialog
 * onCreateView
 * onViewCreated
 * onActivityCreated
 * onViewStateRestored
 * onStart
 * onResume
 *
 *
 * 點外面取消
 * onCancel
 * onDismiss
 * onPause
 * onStop
 * onDestroyView
 * onDestroy
 * onDetach
 *
 *
 * 關閉
 * onDismiss
 * onPause
 * onStop
 * onDestroyView
 * onDestroy
 * onDetach
 */
public abstract class DialogPageFragment extends DialogFragment implements IPageFragment {
    protected final static int REQUEST_CODE_DEFAULT = 0x0010;
    protected final static int REQUEST_CODE_UPDATE = 0x0011;
    protected final static int REQUEST_CODE_LOAD_MORE = 0x0012;
    private View rootView;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doConfigStyle();
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = doCreateDialog(rootView = doCreateView(savedInstanceState));
        doConfigDialog(dialog);
        return dialog;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        doInitView(rootView);
    }

    @Override public void onStop() {
        super.onStop();
        onSavedUIState();
    }

    @Override public void onResume() {
        super.onResume();
        onResumeUIState();
    }

    /**
     * 設置頁面
     */
    protected abstract int getLayoutRes();

    /**
     * 設置樣式 如全屏顯示 是否顯示titleBar
     */
    protected abstract void doConfigStyle();

    /**
     * 創建View
     */
    protected abstract View doCreateView(Bundle savedInstanceState);

    /**
     * 初始化View 綁定View
     */
    protected abstract void doInitView(View view);

    protected abstract Dialog doCreateDialog(View view);

    protected abstract void doConfigDialog(Dialog dialog);

    protected abstract void onResumeUIState();

    protected abstract void onSavedUIState();

    protected abstract void onReset();
}
