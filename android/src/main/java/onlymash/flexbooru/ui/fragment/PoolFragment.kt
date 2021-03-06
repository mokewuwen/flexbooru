package onlymash.flexbooru.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.refreshable_list.*
import onlymash.flexbooru.R
import onlymash.flexbooru.common.Settings.pageLimit
import onlymash.flexbooru.common.Values.BOORU_TYPE_DAN1
import onlymash.flexbooru.common.Values.BOORU_TYPE_MOE
import onlymash.flexbooru.data.action.ActionPool
import onlymash.flexbooru.data.model.common.Booru
import onlymash.flexbooru.data.repository.NetworkState
import onlymash.flexbooru.data.repository.pool.PoolRepositoryImpl
import onlymash.flexbooru.glide.GlideApp
import onlymash.flexbooru.ui.adapter.PoolAdapter
import onlymash.flexbooru.ui.viewmodel.PoolViewModel
import onlymash.flexbooru.ui.viewmodel.getPoolViewModel

class PoolFragment : SearchBarFragment() {

    private var action: ActionPool? = null

    private lateinit var poolViewModel: PoolViewModel
    private lateinit var poolAdapter: PoolAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        poolViewModel = getPoolViewModel(PoolRepositoryImpl(booruApis))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getSearchBarHint(): CharSequence =
        getString(R.string.search_bar_hint_search_pools)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSearchBarTitle(getString(R.string.title_pools))
        poolAdapter = PoolAdapter(GlideApp.with(this),
            downloadPoolCallback = {

        }) {
            poolViewModel.retry()
        }
        list.apply {
            layoutManager = LinearLayoutManager(this@PoolFragment.context, RecyclerView.VERTICAL, false)
            adapter = poolAdapter
        }
        poolViewModel.pools.observe(viewLifecycleOwner, Observer {
            poolAdapter.submitList(it)
        })
        poolViewModel.networkState.observe(viewLifecycleOwner, Observer {
            poolAdapter.setNetworkState(it)
        })
        poolViewModel.refreshState.observe(viewLifecycleOwner, Observer {
            if (it != NetworkState.LOADING) {
                swipe_refresh.isRefreshing = false
            }
        })
        swipe_refresh.setOnRefreshListener {
            poolViewModel.refresh()
        }
    }

    override fun onBooruLoaded(booru: Booru?) {
        if (booru == null) {
            action == null
            poolViewModel.show(null)
            return
        }
        if (action == null) {
            action = ActionPool(
                booru = booru,
                limit = poolLimit(booru.type)
            )
        } else {
            action?.let {
                it.booru = booru
                it.limit = poolLimit(booru.type)
            }
        }
        poolViewModel.show(action)
    }

    private fun poolLimit(booruType: Int): Int {
        return when (booruType) {
            BOORU_TYPE_MOE, BOORU_TYPE_DAN1 -> 20
            else -> pageLimit
        }
    }

    override fun onApplySearch(query: String) {
        super.onApplySearch(query)
        action?.let {
            it.query = query
            poolViewModel.show(action)
            poolViewModel.refresh()
        }
    }
}