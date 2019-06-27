package org.arxing.pagemanager;


import android.support.annotation.IdRes;

class LayoutInfo {
    private @IdRes int layoutId;
    private String layoutTag;
    private String topFragmentTag;

    LayoutInfo(String layoutTag, int layoutId) {
        this.layoutId = layoutId;
        this.layoutTag = layoutTag;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public String getLayoutTag() {
        return layoutTag;
    }

    public void setLayoutTag(String layoutTag) {
        this.layoutTag = layoutTag;
    }

    public String getTopFragmentTag() {
        return topFragmentTag;
    }

    public void setTopFragmentTag(String topFragmentTag) {
        this.topFragmentTag = topFragmentTag;
    }
}
