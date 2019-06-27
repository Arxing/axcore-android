package org.arxing.axmvvm.adapter;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;

import org.arxing.axmvvm.ViewModel;
import org.arxing.axmvvm.ViewModelHelper;


/**
 * 二維Adapter並吃List資料
 */
public abstract class MvvmSectionListAdapter<TData> extends MvvmAdapter {

    protected ObservableList<TData> dataList = new ObservableArrayList<>();

    public MvvmSectionListAdapter(ViewModelHelper helper) {
        super(helper);
    }

    public void setDataList(ObservableList<TData> dataList) {
        this.dataList = dataList;
    }

    @Override public int getSizeOfSections() {
        return dataList.size();
    }

    @Override public abstract int getSizeOfItems(int sectionIndex);

    /*
     * item
     * */

    public abstract void refreshBindingItemViewModel(ViewModel viewModel, int sectionIndex, int itemIndex);

    public abstract @LayoutRes int getItemLayoutRes();

    public abstract ViewModel createItemViewModel(ViewDataBinding binding);

    /*
     * header
     * */

    public abstract void refreshBindingHeaderViewModel(ViewModel viewModel, int sectionIndex, int itemIndex);

    public abstract @LayoutRes int getHeaderLayoutRes();

    public abstract ViewModel createHeaderViewModel(ViewDataBinding binding);

}
