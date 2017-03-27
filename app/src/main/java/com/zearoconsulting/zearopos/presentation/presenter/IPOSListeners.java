package com.zearoconsulting.zearopos.presentation.presenter;

import com.zearoconsulting.zearopos.presentation.model.Category;
import com.zearoconsulting.zearopos.presentation.model.POSLineItem;
import com.zearoconsulting.zearopos.presentation.model.POSOrders;
import com.zearoconsulting.zearopos.presentation.model.Product;

/**
 * Created by saravanan on 27-05-2016.
 */
public interface IPOSListeners {

    void onUpdateProductToCart(long categoryId,Product mProduct, boolean addOrRemove);
    void addOrRemoveItemFromCart(long categoryId, POSLineItem mProduct, boolean addOrRemove);
    void onSelectedOrder(POSOrders orders);
    void onSelectedCategory(Category category);
    void orderCancelSuccess();
    void orderCancelFailure();
    void onReprintOrder(POSOrders order);
}
