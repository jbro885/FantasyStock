package com.fantasystock.fantasystock;

import android.support.v7.widget.RecyclerView;

/**
 * Created by chengfu_lin on 3/11/16.
 */
public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}