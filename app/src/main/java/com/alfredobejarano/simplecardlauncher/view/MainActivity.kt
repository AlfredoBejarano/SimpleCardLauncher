package com.alfredobejarano.simplecardlauncher.view

import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfredobejarano.simplecardlauncher.R
import com.alfredobejarano.simplecardlauncher.model.App
import com.alfredobejarano.simplecardlauncher.presenter.AppsPresenter
import com.alfredobejarano.simplecardlauncher.presenter.Utils
import com.alfredobejarano.simplecardlauncher.adapter.AppsListAdapter
import com.alfredobejarano.simplecardlauncher.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT

class MainActivity : AppCompatActivity() {
	var mApp: Int = 0
	private val UNINSTALL_REQUEST_CODE: Int = 60
	private lateinit var binding: ActivityMainBinding

	/**
	 * Creates the activity.
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		AppsPresenter(this)
	}

	/**
	 * Refreshes the wallpaper if it has been changed.
	 */
	override fun onResume() {
		super.onResume()
		try {
			binding.launcherWallpaper.setImageDrawable(WallpaperManager.getInstance(this).drawable)
		} catch (e: Exception) {
			binding.launcherWallpaper.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_wallpaper))
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
		binding.loadingBarContainer.loadingView.visibility = visibility
	}

	/**
	 * Displays the apps in the list.
	 */
	fun setup(data: MutableList<App>) {
		val orderedData: MutableList<App> = Utils().sortAppList(data)
		binding.appsList.layoutManager = LinearLayoutManager(this)
		binding.appsList.adapter = AppsListAdapter(orderedData, this)
	}

	/**
	 * Removes an app.
	 */
	fun uninstallApp(pkg: String) {
		val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
		intent.data = Uri.parse("package:$pkg")
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
				Snackbar.make(binding.root, R.string.app_cant_be_removed, LENGTH_SHORT).show()
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data)
		}
	}

	/**
	 * Reloads all the apps in the device and puts them in the list.
	 */
	private fun refreshApps() {
		(binding.appsList.adapter as AppsListAdapter).apps.removeAt(mApp)
		binding.appsList.invalidate()
		displayLoadingDialog(View.VISIBLE)
		AppsPresenter(this)
	}
}
