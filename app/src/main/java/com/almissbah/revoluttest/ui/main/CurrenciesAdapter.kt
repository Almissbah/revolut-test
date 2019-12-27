package com.almissbah.revoluttest.ui.main

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
import com.almissbah.revoluttest.R
import com.almissbah.revoluttest.data.remote.model.Currency
import com.almissbah.revoluttest.databinding.CurrenciesListItemBinding
import com.almissbah.revoluttest.utils.DiffUtilsCallback


class CurrenciesAdapter :
    RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>() {

    var currenciesList: MutableList<Currency> = mutableListOf()
    var baseCurrency: Currency? = null
    lateinit var itemClickListener: ItemClickListener
    lateinit var textChangeListener: TextChangeListener

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CurrencyViewHolder {
        val context: Context = viewGroup.context
        val layoutInflater = LayoutInflater.from(context)

        val binding: CurrenciesListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.currencies_list_item, viewGroup, false)

        return CurrencyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currenciesList.size
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
            currenciesList[position] = currency
            holder.binding.currencyName.text = currency.name
            if (baseCurrency != currency)
                holder.binding.value.setText(currency.value.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency: Currency = currenciesList[position]
        holder.binding.currency = currency
        holder.binding.value.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    itemClickListener.onClicked(
                        holder.binding.value,
                        currency,
                        holder.adapterPosition
                    )
                }
            }
            false
        }

        holder.itemView.setOnClickListener { v ->
            itemClickListener.onClicked(holder.binding.value, currency, holder.adapterPosition)

        }
    }


    fun setData(newCurrencies: MutableList<Currency>) {
        val diffCallback = DiffUtilsCallback(currenciesList, newCurrencies)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        currenciesList = newCurrencies
        diffResult.dispatchUpdatesTo(this)
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