package onlymash.flexbooru.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import onlymash.flexbooru.R
import onlymash.flexbooru.common.Values.BOORU_TYPE_DAN
import onlymash.flexbooru.common.Values.BOORU_TYPE_DAN1
import onlymash.flexbooru.common.Values.BOORU_TYPE_GEL
import onlymash.flexbooru.common.Values.BOORU_TYPE_MOE
import onlymash.flexbooru.common.Values.BOORU_TYPE_SANKAKU
import onlymash.flexbooru.common.Values.HASH_SALT_CONTAINED
import onlymash.flexbooru.common.Values.SCHEME_HTTPS
import onlymash.flexbooru.data.database.dao.BooruDao
import onlymash.flexbooru.data.model.common.Booru
import onlymash.flexbooru.extension.isHost
import onlymash.flexbooru.ui.activity.BooruConfigActivity
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.erased.instance

private const val CONFIG_NAME_KEY = "booru_config_name"
private const val CONFIG_TYPE_KEY = "booru_config_type"
private const val CONFIG_SCHEME_KEY = "booru_config_scheme"
private const val CONFIG_HOST_KEY = "booru_config_host"
private const val CONFIG_HASH_SALT_KEY = "booru_config_hash_salt"

private const val CONFIG_TYPE_DAN = "danbooru"
private const val CONFIG_TYPE_DAN1 = "danbooru1"
private const val CONFIG_TYPE_MOE = "moebooru"
private const val CONFIG_TYPE_GEL = "gelbooru"
private const val CONFIG_TYPE_SANKAKU = "sankaku"

class BooruConfigFragment : PreferenceFragmentCompat(), KodeinAware,
    Toolbar.OnMenuItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    override val kodein: Kodein by kodein()
    private val booruDao: BooruDao by instance()
    private val sp: SharedPreferences by instance()

    private var booru: Booru? = null

    private val hashBoorus = arrayOf(
        BOORU_TYPE_DAN1,
        BOORU_TYPE_MOE,
        BOORU_TYPE_SANKAKU
    )

    private var hashSaltPreferences: Preference? = null

    private var name: String
        get() = sp.getString(CONFIG_NAME_KEY, "") ?: ""
        set(value) = sp.edit().putString(CONFIG_NAME_KEY, value).apply()

    private var scheme: String
        get() = sp.getString(CONFIG_SCHEME_KEY, SCHEME_HTTPS) ?: SCHEME_HTTPS
        set(value) = sp.edit().putString(CONFIG_SCHEME_KEY, value).apply()

    private var host: String
        get() = sp.getString(CONFIG_HOST_KEY, "") ?: ""
        set(value) = sp.edit().putString(CONFIG_HOST_KEY, value).apply()

    private var type: Int
        get() = getBooruTypeInt(sp.getString(CONFIG_TYPE_KEY, CONFIG_TYPE_MOE) ?: CONFIG_TYPE_MOE)
        set(value) = sp.edit().putString(CONFIG_TYPE_KEY, getBooruTypeString(value)).apply()

    private var hashSalt: String
        get() = sp.getString(CONFIG_HASH_SALT_KEY, "") ?: ""
        set(value) = sp.edit().putString(CONFIG_HASH_SALT_KEY, value).apply()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val activity = activity as BooruConfigActivity
        val booruUid = activity.intent?.getLongExtra(BooruConfigActivity.EXTRA_BOORU_UID, -1L) ?: -1L
        if (booruUid >= 0) {
            booru = booruDao.getBooruByUid(booruUid)
        }
        if (booru == null) {
            booru = Booru(
                name = "",
                scheme = SCHEME_HTTPS,
                host = "",
                type = BOORU_TYPE_MOE,
                hashSalt = "-$HASH_SALT_CONTAINED-"
            )
        }
        booru?.let {
            name = it.name
            scheme = it.scheme
            host = it.host
            type = it.type
            hashSalt = it.hashSalt
        }
        addPreferencesFromResource(R.xml.pref_booru_config)
        hashSaltPreferences = findPreference(CONFIG_HASH_SALT_KEY)
        hashSaltPreferences?.isVisible = type in hashBoorus
        sp.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        sp.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val booru = booru ?: return true
        when (item?.itemId) {
            R.id.action_booru_config_delete -> {
                context?.let { context ->
                    AlertDialog.Builder(context)
                        .setTitle(R.string.booru_config_dialog_title_delete)
                        .setPositiveButton(R.string.dialog_yes) { _, _ ->
                            booruDao.delete(booru.uid)
                            activity?.finish()
                        }
                        .setNegativeButton(R.string.dialog_no, null)
                        .create()
                        .show()
                }
            }
            R.id.action_booru_config_apply -> {
                when {
                    booru.name.isEmpty() -> {
                        Snackbar.make(listView, R.string.booru_config_name_cant_empty, Snackbar.LENGTH_LONG).show()
                    }
                    booru.host.isEmpty() -> {
                        Snackbar.make(listView, R.string.booru_config_host_cant_empty, Snackbar.LENGTH_LONG).show()
                    }
                    !booru.host.isHost() -> {
                        Snackbar.make(listView, getString(R.string.booru_config_host_invalid), Snackbar.LENGTH_LONG).show()
                    }
                    booru.type in hashBoorus && booru.hashSalt.isEmpty() -> {
                        Snackbar.make(listView, R.string.booru_config_hash_salt_cant_empty, Snackbar.LENGTH_LONG).show()
                    }
                    booru.type in hashBoorus && !booru.hashSalt.contains(HASH_SALT_CONTAINED) -> {
                        Snackbar.make(listView, getString(R.string.booru_config_hash_salt_must_contain_yp), Snackbar.LENGTH_LONG).show()
                    }
                    booru.uid == 0L -> {
                        if (booru.type !in hashBoorus) {
                            booru.hashSalt = ""
                        }
                        booruDao.insert(booru)
                        activity?.finish()
                    }
                    else -> {
                        booruDao.update(booru)
                        activity?.finish()
                    }
                }
            }
        }
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when(key) {
            CONFIG_NAME_KEY -> booru?.name = name
            CONFIG_SCHEME_KEY -> booru?.scheme = scheme
            CONFIG_HOST_KEY -> booru?.host = host
            CONFIG_TYPE_KEY -> {
                val type = type
                booru?.type = type
                hashSaltPreferences?.isVisible = type in hashBoorus
            }
            CONFIG_HASH_SALT_KEY -> booru?.hashSalt = hashSalt
        }
    }

    private fun getBooruTypeString(booruType: Int): String {
        return when (booruType) {
            BOORU_TYPE_MOE -> CONFIG_TYPE_MOE
            BOORU_TYPE_DAN -> CONFIG_TYPE_DAN
            BOORU_TYPE_DAN1 -> CONFIG_TYPE_DAN1
            BOORU_TYPE_GEL -> CONFIG_TYPE_GEL
            else -> CONFIG_TYPE_SANKAKU
        }
    }

    private fun getBooruTypeInt(booruType: String): Int {
        return when (booruType) {
            CONFIG_TYPE_MOE -> BOORU_TYPE_MOE
            CONFIG_TYPE_DAN -> BOORU_TYPE_DAN
            CONFIG_TYPE_DAN1 -> BOORU_TYPE_DAN1
            CONFIG_TYPE_GEL -> BOORU_TYPE_GEL
            else -> BOORU_TYPE_SANKAKU
        }
    }
}