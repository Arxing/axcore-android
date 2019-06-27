package org.arxing.pagemanager;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import org.arxing.pagemanager.protocol.IBackHandle;
import org.arxing.pagemanager.protocol.IPageFragment;


public abstract class PageFragment extends Fragment implements IPageFragment, IBackHandle {
    private PageManager pageManager;
    String targetLayoutTag;
    Animation animEnter;
    Animation animExit;
    boolean isBack;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutRes(), container, false);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pageManager = new PageManager(getContext(), getChildFragmentManager());
        onInit();
        onFragmentShow(false);
    }

    @Override public void onStart() {
        super.onStart();
    }

    @Override public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onFragmentDismiss();
        } else {
            onFragmentShow(isBack);
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }

    @Override public boolean onActivityBackPressed() {
        return false;
    }

    @Override public String getTargetLayoutTag() {
        return targetLayoutTag;
    }

    @Override public void setTargetLayoutTag(String layoutTag) {
        this.targetLayoutTag = layoutTag;
    }

    @Override public void setEnterAnim(Animation anim) {
        animEnter = anim;
    }

    @Override public void setExitAnim(Animation anim) {
        animExit = anim;
    }

    @Override public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return enter ? animEnter : animExit;
    }

    @Override public final void setIsBack(boolean isBack) {
        this.isBack = isBack;
    }

    public void onFragmentShow(boolean isBack) {
    }

    public void onFragmentDismiss() {
    }

    protected <T extends View> T findView(@IdRes int id) {
        return getView().findViewById(id);
    }

    protected abstract int getLayoutRes();

    protected void onInit() {
    }

    protected PageManager getPageManager() {
        return pageManager;
    }
}
