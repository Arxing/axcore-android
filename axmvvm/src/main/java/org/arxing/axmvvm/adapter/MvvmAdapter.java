package org.arxing.axmvvm.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ObservableMap;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.arxing.axmvvm.ViewModel;
import org.arxing.axmvvm.ViewModelHelper;
import org.arxing.axmvvm.adapter.holder.MvvmFooterViewHolder;
import org.arxing.axmvvm.adapter.holder.MvvmGlobalFooterViewHolder;
import org.arxing.axmvvm.adapter.holder.MvvmGlobalHeaderViewHolder;
import org.arxing.axmvvm.adapter.holder.MvvmHeaderViewHolder;
import org.arxing.axmvvm.adapter.holder.MvvmItemViewHolder;
import org.arxing.lazyrecyclerview.LazyAdapter;
import org.arxing.lazyrecyclerview.LazyFooterViewHolder;
import org.arxing.lazyrecyclerview.LazyHeaderViewHolder;
import org.arxing.lazyrecyclerview.LazyItemViewHolder;
import org.arxing.axutils_android.Logger;
import org.arxing.axutils_android.ThreadUtil;


/**
 * MVVM adapter的原型
 */
class MvvmAdapter extends LazyAdapter implements ViewModelHelper {
    protected Logger logger = Logger.defLogger;
    public ViewModelHelper helper;

    public MvvmAdapter(ViewModelHelper helper) {
        this.helper = helper;
    }

    public <T> void observeList(ObservableList<T> list) {
        list.addOnListChangedCallback(new OnListSimpleCallback<T>() {
            @Override public void onAnyChanged(ObservableList<T> sender, int type) {
                ThreadUtil.post(() -> notifyAllSectionsDataSetChanged());
            }
        });
    }

    public <TK, TV> void observeMap(ObservableMap<TK, TV> map) {
        map.addOnMapChangedCallback(new ObservableMap.OnMapChangedCallback<ObservableMap<TK, TV>, TK, TV>() {
            @Override public void onMapChanged(ObservableMap<TK, TV> sender, TK key) {
                ThreadUtil.post(() -> notifyAllSectionsDataSetChanged());
            }
        });
    }

    public void refresh() {
        notifyAllSectionsDataSetChanged();
    }

    @Override public int getSizeOfSections() {
        return 0;
    }

    @Override public int getSizeOfItems(int sectionIndex) {
        return 0;
    }

    /*
     * other methods
     * */

    public void refreshBindingHeaderViewModel(ViewModel viewModel, int sectionIndex) {
    }

    public void refreshBindingFooterViewModel(ViewModel viewModel, int sectionIndex) {
    }

    public void refreshBindingGlobalHeaderViewModel(ViewModel viewModel) {
    }

    public void refreshBindingGlobalFooterViewModel(ViewModel viewModel) {
    }

    public void refreshBindingItemViewModel(ViewModel viewModel, int sectionIndex, int itemIndex) {
    }

    /*
     * create viewModel
     * */

    public ViewModel createHeaderViewModel(ViewDataBinding binding) {
        return null;
    }

    public ViewModel createFooterViewModel(ViewDataBinding binding) {
        return null;
    }

    public ViewModel createItemViewModel(ViewDataBinding binding) {
        return null;
    }

    public ViewModel createGlobalHeaderViewModel(ViewDataBinding binding) {
        return null;
    }

    public ViewModel createGlobalFooterViewModel(ViewDataBinding binding) {
        return null;
    }

    /*
     * layout res
     * */

    public @LayoutRes int getHeaderLayoutRes() {
        return 0;
    }

    public @LayoutRes int getFooterLayoutRes() {
        return 0;
    }

    public @LayoutRes int getItemLayoutRes() {
        return 0;
    }

    public @LayoutRes int getGlobalHeaderLayoutRes() {
        return 0;
    }

    public @LayoutRes int getGlobalFooterLayoutRes() {
        return 0;
    }

    /*
     * create holder
     */

    private ViewDataBinding createBinding(ViewGroup parent, @LayoutRes int layout) {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layout, parent, false);
    }

    @Override public final LazyItemViewHolder createItemViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = createBinding(parent, getItemLayoutRes());
        ViewModel viewModel = createItemViewModel(binding);
        return new MvvmItemViewHolder(viewModel);
    }

    @Override public final LazyHeaderViewHolder createHeaderViewHolder(ViewGroup parent, int headerUserType) {
        ViewDataBinding binding = createBinding(parent, getHeaderLayoutRes());
        ViewModel viewModel = createHeaderViewModel(binding);
        return new MvvmHeaderViewHolder(viewModel);
    }

    @Override public final LazyFooterViewHolder createFooterViewHolder(ViewGroup parent, int footerUserType) {
        ViewDataBinding binding = createBinding(parent, getFooterLayoutRes());
        ViewModel viewModel = createFooterViewModel(binding);
        return new MvvmFooterViewHolder(viewModel);
    }

    @Override public MvvmGlobalHeaderViewHolder createGlobalHeaderViewHolder(ViewGroup parent) {
        ViewDataBinding binding = createBinding(parent, getGlobalHeaderLayoutRes());
        ViewModel viewModel = createGlobalHeaderViewModel(binding);
        return new MvvmGlobalHeaderViewHolder(viewModel);
    }

    @Override public MvvmGlobalFooterViewHolder createGlobalFooterViewHolder(ViewGroup parent) {
        ViewDataBinding binding = createBinding(parent, getGlobalFooterLayoutRes());
        ViewModel viewModel = createGlobalFooterViewModel(binding);
        return new MvvmGlobalFooterViewHolder(viewModel);
    }

    /*
     * bind holder
     * */

    @Override public final void bindItemViewHolder(LazyItemViewHolder viewHolder, int sectionIndex, int itemIndex, int viewType) {
        MvvmItemViewHolder holder = (MvvmItemViewHolder) viewHolder;
        refreshBindingItemViewModel(holder.viewModel, sectionIndex, itemIndex);
    }

    @Override public final void bindHeaderViewHolder(LazyHeaderViewHolder viewHolder, int sectionIndex, int headerUserType) {
        MvvmHeaderViewHolder holder = (MvvmHeaderViewHolder) viewHolder;
        refreshBindingHeaderViewModel(holder.viewModel, sectionIndex);
    }

    @Override public final void bindFooterViewHolder(LazyFooterViewHolder viewHolder, int sectionIndex, int footerUserType) {
        MvvmFooterViewHolder holder = (MvvmFooterViewHolder) viewHolder;
        refreshBindingFooterViewModel(holder.viewModel, sectionIndex);
    }

    @Override public final void bindGlobalHeaderViewHolder(LazyItemViewHolder viewHolder) {
        MvvmGlobalHeaderViewHolder holder = (MvvmGlobalHeaderViewHolder) viewHolder;
        refreshBindingGlobalHeaderViewModel(holder.viewModel);
    }

    @Override public final void bindGlobalFooterViewHolder(LazyItemViewHolder viewHolder) {
        MvvmGlobalFooterViewHolder holder = (MvvmGlobalFooterViewHolder) viewHolder;
        refreshBindingGlobalFooterViewModel(holder.viewModel);
    }

    @Override public Context getContext() {
        return helper.getContext();
    }
}
