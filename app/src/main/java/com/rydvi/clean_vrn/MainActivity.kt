package com.rydvi.clean_vrn

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.ui.login.LoginActivity
import com.rydvi.clean_vrn.ui.organizators.OrganizatorDetailFragment

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_teams, R.id.nav_organizators, R.id.nav_map,
                R.id.nav_games, R.id.nav_exit
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Если при навигации на данный активити передан ИД фрагмента из списка appBarConfiguration,
        // то выполняется навигация на него, иначе открывается фрагмент nav_games
        if (savedInstanceState == null) {
            val idNavFragment = intent.getIntExtra(ARG_FRAGMENT_ID, R.id.nav_games)
            navController.navigate(idNavFragment)
        }

        //Переопределение действий кнопок меню в выдвигаемом меню
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                //Переопределяем кнопку меню exit
                R.id.nav_exit -> {
                    DataRepository.logout {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }
                //остальные оставляем как есть
                else -> {
                    menuItem.isChecked = true
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

//    Отвечает за кнопку настроек в крайнем правом углу. Если расскоментить, то меню появится
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_FRAGMENT_ID = "fragment_id"
    }
}
