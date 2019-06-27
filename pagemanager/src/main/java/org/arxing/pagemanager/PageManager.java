package org.arxing.pagemanager;

import android.content.Context;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.arxing.apiconnector.ResponseBodyInfo;
import org.arxing.apiconnector.ResponseMap;
import org.arxing.pagemanager.protocol.IPageFragment;
import org.arxing.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PageManager {
    private Context context;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Transaction transaction;
    private Map<String, LayoutInfo> pageLayoutMap;
    private Map<String, AnimationInfo> layoutAnimationMap;
    private Map<String, AnimationInfo> fragmentAnimationMap;
    private PageHistory pageHistory;
    private Logger logger = new Logger("PageManager");
    private boolean traceEnabled;

    public PageManager(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.pageLayoutMap = new HashMap<>();
        this.layoutAnimationMap = new HashMap<>();
        this.fragmentAnimationMap = new HashMap<>();
        this.transaction = new Transaction();
        this.pageHistory = new PageHistory();
    }

    private void throwError(String format, Object... objs) {
        throw new Error(String.format(format, objs));
    }

    private LayoutInfo getPageLayoutInfo(String layoutTag) {
        if (pageLayoutMap.containsKey(layoutTag)) {
            return pageLayoutMap.get(layoutTag);
        } else {
            throwError("This layout(%s) is unregistered.", layoutTag);
        }
        return null;
    }

    public void registerLayout(String tag, @IdRes int container) {
        pageLayoutMap.put(tag, new LayoutInfo(tag, container));
    }

    public void registerLayoutAnimation(String tag, Animation anim, boolean enter) {
        if (!layoutAnimationMap.containsKey(tag))
            layoutAnimationMap.put(tag, new AnimationInfo());
        AnimationInfo info = layoutAnimationMap.get(tag);
        if (enter)
            info.enter = anim;
        else
            info.exit = anim;
    }

    public void registerLayoutAnimation(String tag, @AnimRes int anim, boolean enter) {
        Animation animation = AnimationUtils.loadAnimation(context, anim);
        registerLayoutAnimation(tag, animation, enter);
    }

    public void registerFragmentAnimation(String tag, Animation anim, boolean enter) {
        if (!fragmentAnimationMap.containsKey(tag))
            fragmentAnimationMap.put(tag, new AnimationInfo());
        AnimationInfo info = fragmentAnimationMap.get(tag);
        if (enter)
            info.enter = anim;
        else
            info.exit = anim;
    }

    public void registerFragmentAnimation(String tag, @AnimRes int anim, boolean enter) {
        Animation animation = AnimationUtils.loadAnimation(context, anim);
        registerFragmentAnimation(tag, animation, enter);
    }


    public void unregisterLayout(String tag) {
        if (pageLayoutMap.containsKey(tag))
            pageLayoutMap.remove(tag);
    }

    public void unregisterLayoutAnimation(String tag) {
        if (layoutAnimationMap.containsKey(tag))
            layoutAnimationMap.remove(tag);
    }

    public void unregisterFragmentAnimation(String tag) {
        if (fragmentAnimationMap.containsKey(tag))
            fragmentAnimationMap.remove(tag);
    }

    public Transaction beginTransaction() {
        fragmentTransaction = fragmentManager.beginTransaction();
        transaction.reset();
        return transaction;
    }

    public void showDialog(DialogPageFragment fragment, boolean reset) {
        if (!fragment.isAdded() && !fragment.isVisible() && !fragment.isRemoving()) {
            if (reset)
                fragment.onReset();
            fragment.show(fragmentManager, fragment.getFragmentTag());
        }
    }

    public void dismissDialog(DialogPageFragment fragment) {
        if (fragment.isAdded())
            fragment.dismiss();
    }

    public <T extends Fragment & IPageFragment> T obtainFragment(String tag, Class<T> fragmentClass) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            try {
                fragment = fragmentClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (T) fragment;
    }

    public static void showDialog(FragmentManager fragmentManager, String fragmentTag, DialogPageFragment fragment) {
        if (!fragment.isAdded() && !fragment.isVisible() && !fragment.isRemoving())
            fragment.show(fragmentManager, fragmentTag);
    }

    public boolean handleBackPressed() {
        if (!pageHistory.canBack())
            return false;
        String topTag = pageHistory.getLastPage().getFragmentTag();
        if (topTag == null)
            return false;
        PageFragment topFragment = (PageFragment) fragmentManager.findFragmentByTag(topTag);
        if (topFragment == null)
            return false;
        return topFragment.onActivityBackPressed();
    }

    public boolean canBack() {
        return pageHistory.canBack();
    }

    public String backPage() {
        Transaction transaction = beginTransaction();
        if (transaction.topFragmentTag != null) {
            PageFragment topFragment = (PageFragment) fragmentManager.findFragmentByTag(transaction.topFragmentTag);
            transaction.hide(topFragment, true);
        }
        PageHistory.HistoryInfo newTop = pageHistory.getLastPage();
        PageFragment newFragment = (PageFragment) fragmentManager.findFragmentByTag(newTop.getFragmentTag());
        transaction.show(newTop.getLayoutTag(), newFragment, true).commit(true);
        return newTop.getFragmentTag();
    }

    public String getTopPage() {
        return pageHistory.getLastPage().getFragmentTag();
    }

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public class Transaction {
        private ResponseMap responseMap;
        private Map<String, Object> transferData = new ConcurrentHashMap<>();
        private IPageFragment targetEnterPage;
        private IPageFragment targetExitPage;
        private String topFragmentTag;

        private void setEnterAnim(IPageFragment page, String fragmentTag, String layoutTag) {
            AnimationInfo info;
            info = fragmentAnimationMap.get(fragmentTag);
            if (info == null)
                info = layoutAnimationMap.get(layoutTag);
            if (info != null) {
                page.setEnterAnim(info.enter);
            }
        }

        private void setEnterAnim(IPageFragment page, @AnimRes int animRes) {
            Animation animExit = AnimationUtils.loadAnimation(context, animRes);
            page.setExitAnim(animExit);
        }

        private void setExitAnim(IPageFragment page, String fragmentTag, String layoutTag) {
            AnimationInfo info;
            info = fragmentAnimationMap.get(fragmentTag);
            if (info == null)
                info = layoutAnimationMap.get(layoutTag);
            if (info != null) {
                page.setExitAnim(info.exit);
            }
        }

        private void setExitAnim(IPageFragment page, @AnimRes int animRes) {
            Animation animExit = AnimationUtils.loadAnimation(context, animRes);
            page.setExitAnim(animExit);
        }

        private void reset() {
            if (responseMap != null)
                responseMap.clear();
            targetEnterPage = null;
            targetExitPage = null;
        }

        public Transaction putBundle(ResponseMap responseMap) {
            this.responseMap = responseMap;
            return this;
        }

        public Transaction putBundle(String key, ResponseBodyInfo value) {
            responseMap.putBody(key, value);
            return this;
        }

        public Transaction putTransferData(Map<String, Object> transferData) {
            this.transferData = transferData;
            return this;
        }

        public Transaction putTransferData(String key, Object value) {
            transferData.put(key, value);
            return this;
        }

        public <T extends Fragment & IPageFragment> Transaction wantToSee(String layoutTag, T fragment, PageInfo pageInfo) {
            boolean addToHistory = pageInfo.isAddToHistory();
            boolean setToHistoryRoot = pageInfo.isSetToHistoryRoot();
            boolean canReshow = pageInfo.canReshow();
            boolean animEnter = pageInfo.isAnimEnter();
            boolean animExit = pageInfo.isAnimExit();

            // check whether there is top page in current layout.
            show(layoutTag, fragment, animEnter, false, addToHistory, setToHistoryRoot);
            if (topFragmentTag != null) {
                if (topFragmentTag.equals(fragment.getFragmentTag())) {
                    if (canReshow) {
                        detach(fragment);
                        attach(fragment);
                    }
                } else {
                    PageFragment topFragment = (PageFragment) fragmentManager.findFragmentByTag(topFragmentTag);
                    hide(topFragment, animExit, false);
                }
            }
            return this;
        }

        public <T extends Fragment & IPageFragment> Transaction show(String layoutTag,
                                                                     String fragmentTag,
                                                                     Class<T> fragmentClass,
                                                                     boolean anim) {
            show(layoutTag, obtainFragment(fragmentTag, fragmentClass), anim, false);
            return this;
        }

        public <T extends Fragment & IPageFragment> Transaction show(String layoutTag, T fragment, boolean isBack) {
            return show(layoutTag, fragment, true, isBack);
        }

        public <T extends Fragment & IPageFragment> Transaction show(String layoutTag, T fragment, boolean anim, boolean isBack) {
            return show(layoutTag, fragment, anim, isBack, true, false);
        }

        /**
         * <pre>
         * If page wasn't added, then add it.
         * If page was added but hidden, then show it.
         */
        public <T extends Fragment & IPageFragment> Transaction show(String layoutTag,
                                                                     T fragment,
                                                                     boolean anim,
                                                                     boolean isBack,
                                                                     boolean addToHistory,
                                                                     boolean setToHistoryRoot) {
            if (anim)
                setEnterAnim(fragment, fragment.getFragmentTag(), layoutTag);
            targetEnterPage = fragment;

            LayoutInfo layoutInfo = getPageLayoutInfo(layoutTag);
            int layoutId = layoutInfo.getLayoutId();

            if (fragment.isAdded()) {
                if (fragment.isHidden()) {
                    fragmentTransaction.show(fragment);
                    fragment.setTargetLayoutTag(layoutTag);
                    fragment.setIsBack(isBack);
                }
            } else {
                fragmentTransaction.add(layoutId, fragment, fragment.getFragmentTag());
                fragment.setTargetLayoutTag(layoutTag);
                fragment.setIsBack(isBack);
            }
            if (setToHistoryRoot) {
                pageHistory.resetRoot(fragment.getFragmentTag(), layoutTag);
            } else if (addToHistory) {
                pageHistory.addToHistory(fragment.getFragmentTag(), layoutTag);
            }
            layoutInfo.setTopFragmentTag(fragment.getFragmentTag());
            return this;
        }

        public <T extends Fragment & IPageFragment> Transaction hide(T fragment) {
            return hide(fragment, false);
        }

        public <T extends Fragment & IPageFragment> Transaction hide(T fragment, boolean removeFromHistory) {
            return hide(fragment, true, removeFromHistory);
        }

        /**
         * <pre>
         * If page wasn't hidden, then hide it.
         */
        public <T extends Fragment & IPageFragment> Transaction hide(T fragment, boolean anim, boolean removeFromHistory) {
            if (anim) {
                setExitAnim(fragment, fragment.getFragmentTag(), fragment.getTargetLayoutTag());
            }
            targetExitPage = fragment;

            if (!fragment.isHidden()) {
                fragmentTransaction.hide(fragment);
            }
            if (removeFromHistory)
                pageHistory.removeToHistory(fragment.getFragmentTag());
            return this;
        }

        @UnSafe public <T extends Fragment & IPageFragment> Transaction remove(T fragment) {
            if (fragment.isAdded())
                fragmentTransaction.remove(fragment);
            pageHistory.removeToHistory(fragment.getFragmentTag());
            return this;
        }

        @UnSafe public <T extends Fragment & IPageFragment> Transaction replace(String layoutTag, T fragment) {
            return replace(layoutTag, fragment, true);
        }

        @UnSafe public <T extends Fragment & IPageFragment> Transaction replace(String layoutTag, T fragment, boolean anim) {
            LayoutInfo layoutInfo = getPageLayoutInfo(layoutTag);
            int layoutId = layoutInfo.getLayoutId();
            String fragmentTag = fragment.getFragmentTag();
            fragmentTransaction.replace(layoutId, fragment, fragmentTag);
            return this;
        }

        @UnSafe public <T extends Fragment & IPageFragment> Transaction attach(T fragment) {
            fragmentTransaction.attach(fragment);
            return this;
        }

        @UnSafe public <T extends Fragment & IPageFragment> Transaction detach(T fragment) {
            fragmentTransaction.detach(fragment);
            return this;
        }

        public void commit() {
            commit(false);
        }

        public void commit(boolean isBack) {
            fragmentTransaction.commit();
            fragmentTransaction = null;
            if (targetEnterPage != null) {
                if (!isBack)
                    targetEnterPage.receiveData(responseMap, transferData);
                topFragmentTag = targetEnterPage.getFragmentTag();
            }
            if (traceEnabled)
                pageHistory.trace();
        }
    }
}
