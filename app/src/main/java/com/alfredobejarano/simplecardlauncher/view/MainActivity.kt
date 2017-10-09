package com.alfredobejarano.simplecardlauncher.view

import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.alfredobejarano.simplecardlauncher.R
import com.alfredobejarano.simplecardlauncher.model.App
import com.alfredobejarano.simplecardlauncher.presenter.AppsPresenter
import com.alfredobejarano.simplecardlauncher.presenter.Utils
import com.alfredobejarano.simplecardlauncher.presenter.adapter.AppsListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_loading.*

class MainActivity : AppCompatActivity() {
    var mApp: Int = 0
    lateinit var mView: View
    val UNINSTALL_REQUEST_CODE: Int = 60

    /**
     * Creates the activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppsPresenter(this)
    }

    /**
     * Refreshes the wallpaper if it has been changed.
     */
    override fun onResume() {
        super.onResume()
        try {
            launcher_wallpaper.setImageDrawable(WallpaperManager.getInstance(this).drawable)
        } catch (e: Exception) {
            launcher_wallpaper.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_wallpaper))
        }
    }

    /**
     * Do nothing when back has been pressed.
     */
    override fun onBackPressed() {
        // Do nothing.
    }

    /**
     * Shows or hides the loading view.
     */
    fun displayLoadingDialog(visibility: Int) {
        loadingView.visibility = visibility
    }

    /**
     * Displays the apps in the list.
     */
    fun setup(data: MutableList<App>) {
        val orderedData: MutableList<App> = Utils().sortAppList(data)
        apps_list.layoutManager = LinearLayoutManager(this)
        apps_list.adapter = AppsListAdapter(orderedData, this)
    }

    /**
     * Removes an app.
     */
    fun uninstallApp(pkg: String) {
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
        intent.data = Uri.parse("package:" + pkg)
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
        startActivityForResult(intent, UNINSTALL_REQUEST_CODE)
    }

    /**
     * Detects when an app has been uninstalled and refreshes the app list.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                refreshApps()
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                Snackbar.make(mView, R.string.app_cant_be_removed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Reloads all the apps in the device and puts them in the list.
     */
    private fun refreshApps() {
        (apps_list.adapter as AppsListAdapter).apps.removeAt(mApp)
        apps_list.invalidate()
        displayLoadingDialog(View.VISIBLE)
        AppsPresenter(this)
    }
}
