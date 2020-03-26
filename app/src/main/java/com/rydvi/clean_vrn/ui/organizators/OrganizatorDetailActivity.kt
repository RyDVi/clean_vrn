package com.rydvi.clean_vrn.ui.organizators

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import android.view.MenuItem
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import kotlinx.android.synthetic.main.activity_organizator_detail.*

/**
 * An activity representing a single Organizator detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [OrganizatorListActivity].
 */
class OrganizatorDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizator_detail)
        setSupportActionBar(detail_toolbar)

        fab.setOnClickListener {
            val intent = Intent(this, OrganizatorCreateEdit::class.java).apply {
                putExtra(
                    OrganizatorCreateEdit.ORG_ID,
                    intent.getLongExtra(OrganizatorDetailFragment.ARG_ITEM_ID, 0)
                )
                putExtra(OrganizatorCreateEdit.ORG_MODE,CreateEditMode.EDIT)
            }
            //Отключение сохранения навигации в истории
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            val fragment = OrganizatorDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(
                        OrganizatorDetailFragment.ARG_ITEM_ID,
                        intent.getLongExtra(OrganizatorDetailFragment.ARG_ITEM_ID, 0)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.organizator_detail_container, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                //Передача ИД фрагмента для навигации на него в MainActivity
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra(MainActivity.ARG_FRAGMENT_ID, R.id.nav_organizators)
                }
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
