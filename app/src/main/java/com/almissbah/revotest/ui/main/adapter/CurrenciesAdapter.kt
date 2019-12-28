package com.almissbah.revotest.ui.main.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.almissbah.revotest.R
import com.almissbah.revotest.data.remote.model.Currency
import com.almissbah.revotest.databinding.CurrenciesListItemBinding
import com.almissbah.revotest.utils.DiffUtilsCallback


class CurrenciesAdapter :
    RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>() {

    private var mCurrencies: MutableList<Currency> = mutableListOf()
    var mBaseCurrency: Currency? = null
    lateinit var mItemClickListener: ItemClickListener
    lateinit var mTextChangeListener: TextChangeListener

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CurrencyViewHolder {
        val context: Context = viewGroup.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: CurrenciesListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.currencies_list_item, viewGroup, false)

        return CurrencyViewHolder(
            binding
        )
    }


    override fun getItemCount(): Int {
        return mCurrencies.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(
        holder: CurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val currency = payloads[0] as Currency
            updateItemView(holder, position, currency)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency: Currency = mCurrencies[holder.adapterPosition]
        holder.binding.currency = currency

        holder.binding.value.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    mItemClickListener.onClicked(
                        holder.binding.value,
                        mCurrencies[holder.adapterPosition],
                        holder.adapterPosition
                    )
                }
            }
            false
        }

        holder.itemView.setOnClickListener {
            mItemClickListener.onClicked(
                holder.binding.value,
                mCurrencies[holder.adapterPosition],
                holder.adapterPosition
            )
        }
    }


    fun setData(newCurrencies: MutableList<Currency>) {
        val diffCallback = DiffUtilsCallback(mCurrencies, newCurrencies)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mCurrencies = newCurrencies
        diffResult.dispatchUpdatesTo(this)
    }

    private fun updateItemView(holder: CurrencyViewHolder, position: Int, currency: Currency) {
        mCurrencies[position] = currency
        holder.binding.currencyName.text = currency.name
        if ((mBaseCurrency != null && mBaseCurrency != currency) || mBaseCurrency == null)
            holder.binding.value.setText(currency.value.toString())
    }

    class CurrencyViewHolder(itemBinding: CurrenciesListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: CurrenciesListItemBinding = itemBinding
    }

    interface TextChangeListener {
        fun onChange(view: EditText, currency: Currency, text: String)
    }

    interface ItemClickListener {
        fun onClicked(view: EditText, currency: Currency, position: Int)
    }
}