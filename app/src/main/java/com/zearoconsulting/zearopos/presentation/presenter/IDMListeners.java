package com.zearoconsulting.zearopos.presentation.presenter;

import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.model.Product;

/**
 * Created by saravanan on 23-10-2016.
 */

public interface IDMListeners {
    void onSelectedCategory(Category category);
    void onSelectedItem(long categoryId,Product mProduct,int position);
}
