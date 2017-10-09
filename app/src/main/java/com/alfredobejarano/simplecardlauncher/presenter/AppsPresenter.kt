package com.alfredobejarano.simplecardlauncher.presenter

import android.R
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.view.View
import com.alfredobejarano.simplecardlauncher.model.App
import com.alfredobejarano.simplecardlauncher.view.MainActivity

/**
 * @author @AlfredoBejarano
 * @version 1.0
 * @since 07/10/2017
 */
class AppsPresenter(view: MainActivity) {
    val view: MainActivity = view

    init {
        view.displayLoadingDialog(View.VISIBLE)
        view.setup(getApps())
        view.displayLoadingDialog(View.GONE)
    }

    /**
     * Retrieves the apps from the device.
     */
    fun getApps(): MutableList<App> {
        var apps: MutableList<App> = arrayListOf()
        val packageManager: PackageManager = view.packageManager

        // Start the launching intent for retrieving apps
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        // Retrieves apps
        for (ri in packageManager.queryIntentActivities(i, 0)) {
            var color: Int
            try {
                color = extractColor(packageManager, ri)
            } catch (e: Exception) {
                color = Color.WHITE
                e.printStackTrace()
            }
            // Store the app.
            apps.add(App(ri.loadLabel(packageManager), ri.activityInfo.loadIcon(packageManager), color, ri.activityInfo.packageName))
        }

        return apps
    }

    /**
     * Extracts the primary color from an app.
     */
    private fun extractColor(packageManager: PackageManager, ri: ResolveInfo): Int {
        // Retrieve app theme for using the primary color later.
        var res: Resources = packageManager.getResourcesForApplication(ri.activityInfo.packageName)
        var theme: Resources.Theme = res.newTheme()
        var cn: ComponentName = packageManager.getLaunchIntentForPackage(ri.activityInfo.packageName).component
        theme.applyStyle(packageManager.getActivityInfo(cn, 0).theme, false)

        // Specifies the location for the primary color.
        var attrs = IntArray(2)
        attrs[0] = res.getIdentifier("colorPrimary", "attr", ri.activityInfo.packageName)
        attrs[1] = R.attr.colorPrimary

        // Gets the color
        var a: TypedArray = theme.obtainStyledAttributes(attrs)
        @SuppressLint("ResourceType")
        var color: Int = a.getColor(0, a.getColor(1, Color.WHITE))

        // Recycle the TypedArray
        a.recycle()

        return color
    }
}