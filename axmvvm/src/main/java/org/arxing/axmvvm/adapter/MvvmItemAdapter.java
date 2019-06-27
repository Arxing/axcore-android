package org.arxing.axmvvm.adapter;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;

import org.arxing.axmvvm.ViewModel;
import org.arxing.axmvvm.ViewModelHelper;


/**
 * 一維Adapter
 */
public abstract class MvvmItemAdapter<TData> extends MvvmAdapter {

    protected ObservableList<TData> dataList = new ObservableArrayList<>();

    public MvvmItemAdapter(ViewModelHelper helper) {
        super(helper);
    }

    public void setData(ObservableList<TData> dataList) {
        this.dataList = dataList;
    }

    public TData getData(int position) {
        return dataList.get(position);
    }

    @Override public int getSizeOfSections() {
        return 1;
    }

    @Override public int getSizeOfItems(int sectionIndex) {
        return dataList.size();
    }

    public abstract void refreshBindingItemViewModel(ViewModel viewModel, int sectionIndex, int itemIndex);

    public abstract @LayoutRes int getItemLayoutRes();

    public abstract ViewModel createItemViewModel(ViewDataBinding binding);
}
