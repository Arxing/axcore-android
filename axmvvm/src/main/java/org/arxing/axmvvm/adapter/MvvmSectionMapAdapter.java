package org.arxing.axmvvm.adapter;

import android.databinding.ObservableArrayMap;
import android.databinding.ObservableMap;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;

import com.annimon.stream.Stream;

import org.arxing.axmvvm.ViewModel;
import org.arxing.axmvvm.ViewModelHelper;

import java.util.List;

/**
 * 二維Adapter並吃Map資料
 */
public abstract class MvvmSectionMapAdapter<TKey, TData> extends MvvmAdapter {

    protected ObservableMap<TKey, ? extends List<TData>> dataMap = new ObservableArrayMap<>();

    public MvvmSectionMapAdapter(ViewModelHelper helper) {
        super(helper);
    }

    public void setData(ObservableMap<TKey, ? extends List<TData>> dataMap) {
        this.dataMap = dataMap;
    }

    public TKey getKeyBySectionIndex(int sectionIndex) {
        return Stream.of(dataMap.keySet()).toList().get(sectionIndex);
    }

    @Override public final int getSizeOfSections() {
        return dataMap.keySet().size();
    }

    @Override public final int getSizeOfItems(int sectionIndex) {
        return dataMap.get(getKeyBySectionIndex(sectionIndex)).size();
    }

    /*
     * item
     * */

    public abstract void refreshBindingItemViewModel(ViewModel viewModel, int sectionIndex, int itemIndex);

    public abstract @LayoutRes int getItemLayoutRes();

    public abstract ViewModel createItemViewModel(ViewDataBinding binding);

    /*
     * header
     * */

    public abstract void refreshBindingHeaderViewModel(ViewModel viewModel, int sectionIndex);

    public abstract @LayoutRes int getHeaderLayoutRes();

    public abstract ViewModel createHeaderViewModel(ViewDataBinding binding);

}
