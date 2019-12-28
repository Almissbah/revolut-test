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
import com.almissbah.revotest.data.remote.model.Resource
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

    private lateinit var mBinding: CurrenciesFragmentBinding
    private lateinit var mViewModel: CurrenciesViewModel
    private lateinit var mCurrenciesAdapter: CurrenciesAdapter
    private var mBaseEditText: EditText? = null
    @Inject
    lateinit var mRevoRepository: RevoRepository

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            mViewModel.calculateValues(s.toString())
        }
    }

    private val onClickListener = object : ItemClickListener {
        override fun onClicked(view: EditText, currency: Currency, position: Int) {
            if (currency != mCurrenciesAdapter.mBaseCurrency || mBaseEditText == null) {
                mViewModel.moveItemToTop(position, currency)
                setBaseCurrency(currency)
                setFocusAndShowKeyboard(view)
            }
        }
    }
    private val textChangeListener = object :
        CurrenciesAdapter.TextChangeListener {
        override fun onChange(view: EditText, currency: Currency, text: String) {
            mViewModel.calculateValues(text)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.currencies_fragment, container, false
        )
        initRecyclerView()
        mBinding.Retry.setOnClickListener {
            mViewModel.subscribe()
        }
        return mBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(CurrenciesViewModel::class.java)
        mViewModel.mRevoRepository = this.mRevoRepository

        mViewModel.getCurrencies().observe(
            this,
            Observer { resource ->
                when (resource.stats) {
                    Resource.Status.SUCCESS -> {
                        showRecyclerView()
                        updateAdapter(resource.currencies!!)
                    }
                    Resource.Status.FAIL ->
                        showError()
                    Resource.Status.INIT ->
                        showLoading()
                    Resource.Status.LOADING ->
                        showLoading()
                }
            })
    }

    override fun onResume() {
        mViewModel.subscribe()
        super.onResume()
    }

    override fun onPause() {
        mViewModel.unSubscribe()
        super.onPause()
    }

    override fun onDestroy() {
        mViewModel.unSubscribe()
        super.onDestroy()
    }

    private fun initRecyclerView() {
        mBinding.rvCurrencies.layoutManager = LinearLayoutManager(this.context)
        mCurrenciesAdapter = CurrenciesAdapter()
        mCurrenciesAdapter.mItemClickListener = onClickListener
        mCurrenciesAdapter.mTextChangeListener = textChangeListener
        mBinding.rvCurrencies.adapter = mCurrenciesAdapter
    }

    private fun setBaseCurrency(currency: Currency) {
        mCurrenciesAdapter.mBaseCurrency = currency
        mViewModel.setBaseCurrency(currency)
    }

    private fun setFocusAndShowKeyboard(view: EditText) {
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
        mBaseEditText = view
    }

    private fun clearFocusedView() {
        mBaseEditText?.removeTextChangedListener(textWatcher)
        mBaseEditText?.clearFocus()
    }


    private fun showLoading() {
        if (mBinding.rvCurrencies.visibility == View.INVISIBLE) {
            mBinding.progressBar.visibility = View.VISIBLE
            mBinding.ErrorMsg.visibility = View.INVISIBLE
            mBinding.Retry.visibility = View.INVISIBLE
        }
    }

    private fun showError() {
        mBinding.rvCurrencies.visibility = View.INVISIBLE
        mBinding.progressBar.visibility = View.INVISIBLE
        mBinding.ErrorMsg.visibility = View.VISIBLE
        mBinding.Retry.visibility = View.VISIBLE
        mViewModel.unSubscribe()

    }

    private fun showRecyclerView() {
        mBinding.progressBar.visibility = View.INVISIBLE
        mBinding.rvCurrencies.visibility = View.VISIBLE
        mBinding.ErrorMsg.visibility = View.INVISIBLE
        mBinding.Retry.visibility = View.INVISIBLE
    }


    private fun updateAdapter(newCurrencies: MutableList<Currency>) {
        if (mCurrenciesAdapter.mBaseCurrency == null) mCurrenciesAdapter.mBaseCurrency =
            newCurrencies[0]
        mCurrenciesAdapter.setData(newCurrencies)
        mBinding.rvCurrencies.setItemViewCacheSize(newCurrencies.size)
    }

}
