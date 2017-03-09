package at.metalab.meks.metapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.Fade
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.RelativeLayout
import at.metalab.meks.metapp.control.ControlActivtiy
import at.metalab.meks.metapp.issues.IssuesActivity
import at.metalab.meks.metapp.screeninvader.ScreenInvaderActivtiy
import at.metalab.meks.metapp.settings.SettingsActivity
import org.jetbrains.anko.intentFor

open class BaseDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var mRootLayout : FrameLayout
    lateinit var drawerToggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val toolbar = findViewById(R.id.activity_base_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawerToggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val navigationView = findViewById(R.id.activity_base_drawer_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        mRootLayout = findViewById(R.id.activity_base_root) as FrameLayout
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
        // Handle navigation view item clicks here.
        var intent : Intent? = null
        when (item.itemId) {
            R.id.nav_screeninvader -> { intent = intentFor<ScreenInvaderActivtiy>()}
            R.id.nav_control -> { intent = intentFor<ControlActivtiy>() }
            R.id.nav_issues -> { intent = intentFor<IssuesActivity>() }
            R.id.nav_settings -> { intent = intentFor<SettingsActivity>()}
            R.id.nav_share -> { }
            R.id.nav_github -> { }
        }
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)

        startActivity(intent)
        return true
    }
}
