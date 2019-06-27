package org.arxing.lazyrecyclerview;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class GlobalHeaderFooterSectioningAdapter extends SectioningAdapter {
    private final static int ITEM_TYPE_GLOBAL_HEADER = 200;
    private final static int ITEM_TYPE_GLOBAL_FOOTER = 201;

    public final int getSizeOfItems() {
        return getSizeOfItems(0);
    }

    public int getSizeOfItems(int sectionIndex) {
        return 0;
    }

    public int getSizeOfSections() {
        return 0;
    }

    public LazyItemViewHolder createGlobalHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    public LazyItemViewHolder createGlobalFooterViewHolder(ViewGroup parent) {
        return null;
    }

    /**
     * 返回item holder
     */
    public LazyItemViewHolder createItemViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * 返回header holder
     */
    public LazyHeaderViewHolder createHeaderViewHolder(ViewGroup parent, int headerUserType) {
        return null;
    }

    /**
     * 返回footer holder
     */
    public LazyFooterViewHolder createFooterViewHolder(ViewGroup parent, int footerUserType) {
        return null;
    }

    /**
     * 綁定item holder
     */
    public void bindItemViewHolder(LazyItemViewHolder viewHolder, int sectionIndex, int itemIndex, int viewType) {
    }

    /**
     * 綁定header holder
     */
    public void bindHeaderViewHolder(LazyHeaderViewHolder viewHolder, int sectionIndex, int headerUserType) {
    }

    /**
     * 綁定footer holder
     */
    public void bindFooterViewHolder(LazyFooterViewHolder viewHolder, int sectionIndex, int footerUserType) {
    }

    public void bindGlobalHeaderViewHolder(LazyItemViewHolder viewHolder) {
    }

    public void bindGlobalFooterViewHolder(LazyItemViewHolder viewHolder) {
    }

    /**
     * 是否有header
     */
    public boolean isSectionHaveHeader(int sectionIndex) {
        return false;
    }

    /**
     * 是否有footer
     */
    public boolean isSectionHaveFooter(int sectionIndex) {
        return false;
    }

    public boolean hasGlobalHeader() {
        return false;
    }

    public boolean hasGlobalFooter() {
        return false;
    }

    protected final View $(ViewGroup parent, @LayoutRes int id) {
        return LayoutInflater.from(parent.getContext()).inflate(id, parent, false);
    }

    public final boolean isGlobalHeader(int sectionIndex) {
        return hasGlobalHeader() && sectionIndex == 0;
    }

    public final boolean isGlobalFooter(int sectionIndex) {
        return hasGlobalFooter() && sectionIndex >= getNumberOfSections() - 1;
    }

    public int sectionHeaderUserType(int sectionIndex) {
        return super.getSectionHeaderUserType(sectionIndex);
    }

    public int sectionFooterUserType(int sectionIndex) {
        return super.getSectionFooterUserType(sectionIndex);
    }

    public int sectionItemUserType(int sectionIndex, int itemIndex) {
        return super.getSectionItemUserType(sectionIndex, itemIndex);
    }

    public int getGlobalHeaderSection() {
        return hasGlobalFooter() ? 0 : -1;
    }

    public int getGlobalFooterSection() {
        return hasGlobalFooter() ? getNumberOfSections() - 1 : -1;
    }

    // size

    /**
     * 返回真實的Section數量 開放接口為{@code getSizeOfSections()}
     */
    @Override public final int getNumberOfSections() {
        try {
            int size = getSizeOfSections();
            if (hasGlobalHeader())
                size++;
            if (hasGlobalFooter())
                size++;
            return size;
        } catch (Exception e) {
            return 0;
        }
    }

    //強制Global header/footer只擁有1個item
    @Override public final int getNumberOfItemsInSection(int sectionIndex) {
        try {
            if (isGlobalHeader(sectionIndex) || isGlobalFooter(sectionIndex))
                return 1;
            return getSizeOfItems(sectionIndex);
        } catch (Exception e) {
            return 0;
        }
    }

    // has header/footer

    //global header/footer 不觸發 isSectionHaveHeader 接口
    @Override public final boolean doesSectionHaveHeader(int sectionIndex) {
        if (isGlobalHeader(sectionIndex) || isGlobalFooter(sectionIndex))
            return false;
        else
            return isSectionHaveHeader(sectionIndex);
    }

    //global header/footer 不觸發 isSectionHaveFooter 接口
    @Override public final boolean doesSectionHaveFooter(int sectionIndex) {
        if (isGlobalHeader(sectionIndex) || isGlobalFooter(sectionIndex))
            return false;
        else
            return isSectionHaveFooter(sectionIndex);
    }

    // create holder

    @Override public final ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        switch (itemUserType) {
            case ITEM_TYPE_GLOBAL_HEADER:
                return createGlobalHeaderViewHolder(parent);
            case ITEM_TYPE_GLOBAL_FOOTER:
                return createGlobalFooterViewHolder(parent);
            default:
                return createItemViewHolder(parent, itemUserType);
        }
    }

    @Override public final HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerUserType) {
        return createHeaderViewHolder(parent, headerUserType);
    }

    @Override public final FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int footerUserType) {
        return createFooterViewHolder(parent, footerUserType);
    }

    /**
     * stickyHeaders這個lib在27.1.1發生了一個issue 覆寫此方法可解決
     *
     * @see <a href="https://github.com/ShamylZakariya/StickyHeaders/issues/87">ISSUE</a>
     */
    @Override public final GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new GhostHeaderViewHolder(ghostView);
    }

    // bind holder

    @Override public final void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemUserType) {
        switch (itemUserType) {
            case ITEM_TYPE_GLOBAL_HEADER:
                bindGlobalHeaderViewHolder((LazyItemViewHolder) viewHolder);
                break;
            case ITEM_TYPE_GLOBAL_FOOTER:
                bindGlobalFooterViewHolder((LazyItemViewHolder) viewHolder);
                break;
            default:
                bindItemViewHolder((LazyItemViewHolder) viewHolder, sectionIndex, itemIndex, itemUserType);
                break;
        }
    }

    @Override public final void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int sectionIndex, int headerUserType) {
        bindHeaderViewHolder((LazyHeaderViewHolder) viewHolder, sectionIndex, headerUserType);
    }

    @Override public final void onBindFooterViewHolder(FooterViewHolder viewHolder, int sectionIndex, int footerUserType) {
        bindFooterViewHolder((LazyFooterViewHolder) viewHolder, sectionIndex, footerUserType);
    }

    // item type


    @Override public final int getSectionHeaderUserType(int sectionIndex) {
        return sectionHeaderUserType(sectionIndex);
    }

    @Override public final int getSectionFooterUserType(int sectionIndex) {
        return sectionFooterUserType(sectionIndex);
    }

    @Override public final int getSectionItemUserType(int sectionIndex, int itemIndex) {
        if (isGlobalHeader(sectionIndex))
            return ITEM_TYPE_GLOBAL_HEADER;
        if (isGlobalFooter(sectionIndex))
            return ITEM_TYPE_GLOBAL_FOOTER;
        return sectionItemUserType(sectionIndex, itemIndex);
    }
}
