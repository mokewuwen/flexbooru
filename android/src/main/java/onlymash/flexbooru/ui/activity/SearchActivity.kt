package onlymash.flexbooru.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import onlymash.flexbooru.R
import onlymash.flexbooru.common.Keys.POST_QUERY
import onlymash.flexbooru.ui.fragment.PostFragment

class SearchActivity : AppCompatActivity() {

    companion object {
        fun startSearch(context: Context, query: String) {
            context.startActivity(Intent(context, SearchActivity::class.java)
                .putExtra(POST_QUERY, query))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_post_container)
        if (fragment is PostFragment && !fragment.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }
}