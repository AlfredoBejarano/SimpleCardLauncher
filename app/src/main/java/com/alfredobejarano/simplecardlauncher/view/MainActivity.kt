package com.alfredobejarano.simplecardlauncher.view

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfredobejarano.simplecardlauncher.R
import com.alfredobejarano.simplecardlauncher.adapter.AppsListAdapter
import com.alfredobejarano.simplecardlauncher.databinding.ActivityMainBinding
import com.alfredobejarano.simplecardlauncher.model.App
import com.alfredobejarano.simplecardlauncher.presenter.AppsPresenter
import com.alfredobejarano.simplecardlauncher.presenter.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT

const val PERMISSIONS_REQUEST_CODE = 5

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
		val grantedStorage = ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
		if (!grantedStorage) {
			AlertDialog.Builder(this)
				.setTitle(R.string.permission_required)
				.setMessage(R.string.permission_advice)
				.setPositiveButton(R.string.yes) { dialog, _ ->
					ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
					dialog.dismiss()
				}.setNegativeButton(R.string.no) { dialog, _ ->
					dialog.dismiss()
				}.show()
		}
	}

	/**
	 * Refreshes the wallpaper if it has been changed.
	 */
	override fun onResume() {
		super.onResume()
		setWallpaper()
	}

	/**
	 * Do nothing when back has been pressed.
	 */
	override fun onBackPressed() {
		// Do nothing.
	}

	private fun setWallpaper() {
		try {
			binding.launcherWallpaper.setImageDrawable(WallpaperManager.getInstance(this).drawable)
		} catch (e: Exception) {
			binding.launcherWallpaper.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_wallpaper))
		}
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
		val intent = Intent(Intent.ACTION_DELETE)
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

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.isNotEmpty()) {
			if (grantResults.first() == PERMISSION_GRANTED) {
				setWallpaper()
			} else {
				AlertDialog.Builder(this)
					.setTitle(R.string.permission_required)
					.setMessage(R.string.permission_denied_message)
					.setPositiveButton(R.string.close) { dialog, _ ->
						dialog.dismiss()
					}.setNegativeButton(R.string.grant) { dialog, _ ->
						ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
						dialog.dismiss()
					}.show()
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
