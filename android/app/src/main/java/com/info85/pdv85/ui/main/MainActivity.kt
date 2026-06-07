package com.info85.pdv85.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.info85.pdv85.R
import com.info85.pdv85.data.remote.NetworkClient
import com.info85.pdv85.databinding.ActivityMainBinding
import com.info85.pdv85.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        NavigationUI.setupWithNavController(b.navView, navController)

        b.navView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.action_logout) {
                NetworkClient.clearCookies()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            } else {
                val handled = NavigationUI.onNavDestinationSelected(item, navController)
                b.drawerLayout.closeDrawers()
                handled
            }
        }
    }
}
