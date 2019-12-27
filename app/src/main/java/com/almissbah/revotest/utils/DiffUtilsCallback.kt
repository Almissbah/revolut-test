package com.almissbah.revotest.utils

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.almissbah.revotest.data.remote.model.Currency


class DiffUtilsCallback(
    private val oldList: MutableList<Currency>,
    private val newList: MutableList<Currency>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old == new
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val old = oldList[oldPosition]
        val new = newList[newPosition]
        return old.contentEquals(new)
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return newList[newItemPosition]
    }

}