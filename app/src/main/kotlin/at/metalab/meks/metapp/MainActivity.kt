package at.metalab.meks.metapp

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.FrameLayout
import at.metalab.meks.metapp.screeninvader.ScreenInvaderFragment

open class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var mRootLayout : FrameLayout
    lateinit var drawerToggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.activity_main_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawerToggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val navigationView = findViewById(R.id.activity_main_drawer_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        mRootLayout = findViewById(R.id.activity_main_frame) as FrameLayout
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment : BaseFragment? = null
        when (item.itemId) {
            R.id.nav_screeninvader -> { fragment = ScreenInvaderFragment() }
            R.id.nav_control -> { }
            R.id.nav_issues -> { }
            R.id.nav_settings -> { }
            R.id.nav_share -> { }
            R.id.nav_github -> { }
        }
        if (fragment != null) {
            var ft : FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(R.id.activity_main_frame, fragment)
            ft.commit()

            if (supportActionBar != null) {
                supportActionBar!!.title = resources.getString(fragment.actionBarTitle)
            }
        }
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)

        return true
    }
}
