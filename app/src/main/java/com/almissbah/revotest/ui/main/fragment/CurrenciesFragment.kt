package com.almissbah.revotest.ui.main.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.almissbah.revotest.R
import com.almissbah.revotest.data.RevoRepository
import com.almissbah.revotest.data.remote.model.Currency
import com.almissbah.revotest.databinding.CurrenciesFragmentBinding
import com.almissbah.revotest.ui.base.BaseFragment
import com.almissbah.revotest.ui.main.adapter.CurrenciesAdapter
import com.almissbah.revotest.ui.main.adapter.CurrenciesAdapter.ItemClickListener
import com.almissbah.revotest.ui.main.viewmodel.CurrenciesViewModel
import javax.inject.Inject


class CurrenciesFragment : BaseFragment() {

    companion object {
        fun newInstance() =
            CurrenciesFragment()
    }

    private lateinit var binding: CurrenciesFragmentBinding
    private lateinit var viewModel: CurrenciesViewModel
    private lateinit var currenciesAdapter: CurrenciesAdapter
    private var mCurrenciesList: MutableList<Currency> = mutableListOf()
    private var focusedView: EditText? = null
    @Inject
    lateinit var revoRepository: RevoRepository

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.calculateValues(s.toString())
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.currencies_fragment, container, false
        )
        initCurrenciesView()
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CurrenciesViewModel::class.java)
        viewModel.revoRepository = this.revoRepository
        subscribeToLiveData()

    }

    override fun onDestroy() {
        viewModel.unSubscribe()
        super.onDestroy()
    }

    private fun initCurrenciesView() {
        binding.rvCurrencies.layoutManager = LinearLayoutManager(this.context)
        currenciesAdapter =
            CurrenciesAdapter()
        currenciesAdapter.itemClickListener = object : ItemClickListener {
            override fun onClicked(view: EditText, currency: Currency, position: Int) {
                if (currency != currenciesAdapter.baseCurrency || focusedView == null) {
                    currenciesAdapter.baseCurrency = currency
                    moveItemToTop(position, currency)
                    viewModel.updateBaseCurrency(currency)
                    focusAndShowKeyboard(view)
                }

            }
        }

        currenciesAdapter.textChangeListener = object :
            CurrenciesAdapter.TextChangeListener {
            override fun onChange(view: EditText, currency: Currency, text: String) {
                viewModel.calculateValues(text)
            }
        }
        binding.rvCurrencies.adapter = currenciesAdapter
    }

    private fun focusAndShowKeyboard(view: EditText) {
        clearFocusedView()
        setFocusToView(view)
        showKeyboard(view)
    }

    private fun showKeyboard(view: EditText) {
        val imm =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setFocusToView(view: EditText) {
        view.requestFocus()
        view.addTextChangedListener(textWatcher)
        focusedView = view
    }

    private fun clearFocusedView() {
        if (focusedView != null) {
            focusedView!!.removeTextChangedListener(textWatcher)
            focusedView!!.clearFocus()
        }
    }


    fun moveItemToTop(
        from: Int,
        currency: Currency
    ) {
        mCurrenciesList.removeAt(from)
        mCurrenciesList.add(0, currency)
        currenciesAdapter.setData(mCurrenciesList)
    }


    private fun subscribeToLiveData() {
        viewModel.subscribe().observe(
            this,
            Observer { currencies ->
                val tempList = arrangeList(currencies)
                updateAdapter(tempList)

            })
    }

    private fun arrangeList(currencies: MutableList<Currency>): MutableList<Currency> {
        val tempList = mutableListOf<Currency>()
        if (mCurrenciesList.size > 0) mCurrenciesList.forEach { currency ->
            val item: Currency? =
                currencies.find { t -> t.name == currency.name }
            if (item != null) {
                tempList.add(item)
                currencies.remove(item)
            }
        }
        tempList.addAll(currencies)
        return tempList
    }

    private fun updateAdapter(list: MutableList<Currency>) {
        mCurrenciesList.clear()
        mCurrenciesList.addAll(list)

        currenciesAdapter.setData(list)
        binding.rvCurrencies.setItemViewCacheSize(list.size)

        if (currenciesAdapter.baseCurrency == null) currenciesAdapter.baseCurrency =
            list[0]
    }


}
