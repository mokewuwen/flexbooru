package onlymash.flexbooru.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.refreshable_list.*
import onlymash.flexbooru.R
import onlymash.flexbooru.common.Settings.pageLimit
import onlymash.flexbooru.common.Values.BOORU_TYPE_DAN
import onlymash.flexbooru.common.Values.BOORU_TYPE_DAN1
import onlymash.flexbooru.common.Values.BOORU_TYPE_GEL
import onlymash.flexbooru.common.Values.BOORU_TYPE_MOE
import onlymash.flexbooru.data.action.ActionTag
import onlymash.flexbooru.data.model.common.Booru
import onlymash.flexbooru.data.repository.NetworkState
import onlymash.flexbooru.data.repository.tag.TagRepositoryImpl
import onlymash.flexbooru.ui.adapter.TagAdapter
import onlymash.flexbooru.ui.viewmodel.TagViewModel
import onlymash.flexbooru.ui.viewmodel.getTagViewModel

private const val TYPE_ALL = -1
private const val TYPE_GENERAL = 0
private const val TYPE_ARTIST = 1
private const val TYPE_COPYRIGHT = 3
private const val TYPE_CHARACTER = 4
private const val TYPE_CIRCLE = 5
private const val TYPE_FAULTS = 6
private const val TYPE_META = 5
private const val TYPE_MODEL = 5
private const val TYPE_PHOTO_SET = 6
private const val TYPE_META_SANKAKU = 9
private const val TYPE_STUDIO = 2
private const val TYPE_GENRE = 5
private const val TYPE_MEDIUM = 8

private const val ORDER_DATE = "date"
private const val ORDER_NAME = "name"
private const val ORDER_COUNT = "count"

class TagFragment : SearchBarFragment() {

    private var action: ActionTag? = null

    private lateinit var tagViewModel: TagViewModel
    private lateinit var tagAdapter: TagAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tagViewModel = getTagViewModel(TagRepositoryImpl(booruApis))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getSearchBarHint(): CharSequence =
        getString(R.string.search_bar_hint_search_tags)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSearchBarTitle(getString(R.string.title_tags))
        tagAdapter = TagAdapter {
            tagViewModel.retry()
        }
        list.apply {
            layoutManager = LinearLayoutManager(this@TagFragment.context, RecyclerView.VERTICAL, false)
            adapter = tagAdapter
        }
        tagViewModel.tags.observe(viewLifecycleOwner, Observer {
            tagAdapter.submitList(it)
        })
        tagViewModel.networkState.observe(viewLifecycleOwner, Observer {
            tagAdapter.setNetworkState(it)
        })
        tagViewModel.refreshState.observe(viewLifecycleOwner, Observer {
            if (it != NetworkState.LOADING) {
                swipe_refresh.isRefreshing = false
            }
        })
        swipe_refresh.setOnRefreshListener {
            tagViewModel.refresh()
        }
    }

    override fun onBooruLoaded(booru: Booru?) {
        if (booru == null) {
            action == null
            tagViewModel.show(null)
            return
        }
        if (action == null) {
            action = ActionTag(
                booru = booru,
                limit = tagLimit(booru.type),
                order = ORDER_COUNT
            )
            setSearchBarMenu(when (booru.type) {
                BOORU_TYPE_DAN1 -> R.menu.tag_dan_one
                BOORU_TYPE_DAN -> R.menu.tag_dan
                BOORU_TYPE_MOE -> R.menu.tag_moe
                BOORU_TYPE_GEL -> R.menu.tag_gel
                else -> R.menu.tag_sankaku
            })
        } else {
            action?.let {
                it.booru = booru
                it.limit = tagLimit(booru.type)
            }
        }
        tagViewModel.show(action)
    }

    private fun tagLimit(booruType: Int): Int {
        return when (booruType) {
            BOORU_TYPE_MOE, BOORU_TYPE_DAN1 -> 20
            else -> pageLimit
        }
    }

    override fun onApplySearch(query: String) {
        super.onApplySearch(query)
        action?.let {
            it.query = query
            tagViewModel.show(action)
            tagViewModel.refresh()
        }
    }

    private fun updateActionAndRefresh(action: ActionTag) {
        tagViewModel.show(action)
        tagViewModel.refresh()
    }

    override fun onMenuItemClick(menuItem: MenuItem) {
        super.onMenuItemClick(menuItem)
        when (menuItem.itemId) {
            R.id.action_tag_order_date -> {
                action?.let {
                    it.order = ORDER_DATE
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_order_name -> {
                action?.let {
                    it.order = ORDER_NAME
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_order_count -> {
                action?.let {
                    it.order = ORDER_COUNT
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_all -> {
                action?.let {
                    it.type = TYPE_ALL
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_general -> {
                action?.let {
                    it.type = TYPE_GENERAL
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_artist -> {
                action?.let {
                    it.type = TYPE_ARTIST
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_copyright -> {
                action?.let {
                    it.type = TYPE_COPYRIGHT
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_character -> {
                action?.let {
                    it.type = TYPE_CHARACTER
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_circle -> {
                action?.let {
                    it.type = TYPE_CIRCLE
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_faults -> {
                action?.let {
                    it.type = TYPE_FAULTS
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_meta -> {
                action?.let {
                    it.type = TYPE_META
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_model -> {
                action?.let {
                    it.type = TYPE_MODEL
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_photo_set -> {
                action?.let {
                    it.type = TYPE_PHOTO_SET
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_meta_sankaku -> {
                action?.let {
                    it.type = TYPE_META_SANKAKU
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_genre -> {
                action?.let {
                    it.type = TYPE_GENRE
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_studio -> {
                action?.let {
                    it.type = TYPE_STUDIO
                    updateActionAndRefresh(it)
                }
            }
            R.id.action_tag_type_medium -> {
                action?.let {
                    it.type = TYPE_MEDIUM
                    updateActionAndRefresh(it)
                }
            }
        }
    }
}