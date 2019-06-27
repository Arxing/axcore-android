package org.arxing.pagemanager;

import com.annimon.stream.Stream;

import org.arxing.axutils_android.Logger;
import org.arxing.axutils_android.ThreadUtil;

import java.util.LinkedList;
import java.util.Objects;

class PageHistory {
    private LinkedList<HistoryInfo> history = new LinkedList<>();
    private boolean allowSingleTaskPage = true;
    private Logger logger = new Logger("PageHistory");

    public void setAllowSingleTaskPage(boolean allowSingleTaskPage) {
        this.allowSingleTaskPage = allowSingleTaskPage;
    }

    public void addToHistory(String fragmentTag, String layoutTag) {
        HistoryInfo info = new HistoryInfo(fragmentTag, layoutTag);
        if (history.contains(info) && allowSingleTaskPage) {
            //如果這個頁面已經存在歷史紀錄 則把它拉到最上層 不重複紀錄
            history.remove(info);
        }
        history.add(info);
    }

    public void removeToHistory(String fragmentTag) {
        HistoryInfo info = Stream.of(history).filter(i -> i.fragmentTag.equals(fragmentTag)).findSingle().orElse(null);
        if (info != null)
            history.remove(info);
    }

    public void clearHistory() {
        history.clear();
    }

    public HistoryInfo removeLast() {
        return history.removeLast();
    }

    public HistoryInfo getLastPage() {
        return history.size() > 0 ? history.getLast() : null;
    }

    public boolean canBack() {
        return history.size() > 1;
    }

    public HistoryInfo getBackPage() {
        return getBackPage(1);
    }

    public HistoryInfo getBackPage(int backCount) {
        if (backCount < history.size() - 1) {
            return history.get(history.size() - backCount - 1);
        }
        return null;
    }

    public void resetRoot(String fragmentTag, String layoutTag) {
        clearHistory();
        addToHistory(fragmentTag, layoutTag);
    }

    public void trace() {
        logger.i("---------------------");
        ThreadUtil.runOnNewThread(() -> {
            for (HistoryInfo info : history) {
                ThreadUtil.sleep(1);
                logger.i("[%s]: %s", info.fragmentTag, info.layoutTag);
            }
        });
    }

    public static class HistoryInfo {
        private String fragmentTag;
        private String layoutTag;

        HistoryInfo(String fragmentTag, String layoutTag) {
            this.fragmentTag = fragmentTag;
            this.layoutTag = layoutTag;
        }

        public String getFragmentTag() {
            return fragmentTag;
        }

        public void setFragmentTag(String fragmentTag) {
            this.fragmentTag = fragmentTag;
        }

        public String getLayoutTag() {
            return layoutTag;
        }

        public void setLayoutTag(String layoutTag) {
            this.layoutTag = layoutTag;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            HistoryInfo that = (HistoryInfo) o;
            return Objects.equals(fragmentTag, that.fragmentTag) && Objects.equals(layoutTag, that.layoutTag);
        }

        @Override public int hashCode() {
            return Objects.hash(fragmentTag, layoutTag);
        }
    }
}
