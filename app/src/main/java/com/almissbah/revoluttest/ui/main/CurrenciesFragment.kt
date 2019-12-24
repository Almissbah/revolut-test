package com.almissbah.revoluttest.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.almissbah.revoluttest.R
import com.almissbah.revoluttest.data.Repository
import com.almissbah.revoluttest.ui.BaseFragment
import javax.inject.Inject

class CurrenciesFragment : BaseFragment() {

    companion object {
        fun newInstance() = CurrenciesFragment()
    }

    private lateinit var viewModel: CurrenciesViewModel
    @Inject
    lateinit var repository: Repository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CurrenciesViewModel::class.java)
        viewModel.repository = this.repository
        viewModel.subscribe()
    }

}
