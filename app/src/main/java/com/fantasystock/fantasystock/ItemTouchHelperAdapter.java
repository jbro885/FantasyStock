package com.fantasystock.fantasystock;

/**
 * Created by chengfu_lin on 3/11/16.
 */
public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}