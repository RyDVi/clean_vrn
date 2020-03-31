package com.rydvi.clean_vrn

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.ui.login.LoginActivity
import com.rydvi.clean_vrn.ui.utils.UserType

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        loading = findViewById(R.id.loading_main)
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

        //Установка данных о пользователе
        val headerNavView = navView.getHeaderView(0)
        val textUserType = headerNavView.findViewById<TextView>(R.id.text_user_type)
        val textUserFullname = headerNavView.findViewById<TextView>(R.id.text_user_fullname)
        val textUserEmail = headerNavView.findViewById<TextView>(R.id.text_user_email)
        val textUserPhone = headerNavView.findViewById<TextView>(R.id.text_user_phone)
        DataRepository.getSession()?.let { curSession ->
            var translateUserType: Int? = null
            when (curSession.idUserType) {
                UserType.administrator.getUserTypeId() -> translateUserType =
                    UserType.administrator.getUserTypeString()
                UserType.organizator.getUserTypeId() -> translateUserType =
                    UserType.organizator.getUserTypeString()
                UserType.player.getUserTypeId() -> translateUserType =
                    UserType.player.getUserTypeString()
            }
            translateUserType?.let {
                textUserType.text = resources.getString(it)
                if (UserType.player.getUserTypeId() !== curSession.idUserType) {
                    textUserFullname.text =
                        "${curSession.lastname} ${curSession.firstname} ${curSession.middlename}"
                    textUserEmail.text = curSession.email
                    textUserPhone.text = curSession.phone
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

    fun showLoading(isShow: Boolean) {
        loading.visibility = if (isShow) ProgressBar.VISIBLE else ProgressBar.GONE
    }

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
