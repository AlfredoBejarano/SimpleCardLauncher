package com.alfredobejarano.simplecardlauncher.presenter

import android.graphics.Color
import com.alfredobejarano.simplecardlauncher.model.App
import java.util.*

/**
 * @author @AlfredoBejarano
 * @version 1.0
 * @since 07/10/2017
 */
class Utils {

    /**
     * Returns the correct text color depending on the card background.
     */
    fun getTextColor(color: Int): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        return if ((red * 0.299) + (green * 0.587) + (blue * 0.114) > 186) Color.BLACK else Color.WHITE
    }

    /**
     * Sorts apps in alphabetical order by name.
     */
    fun sortAppList(list: MutableList<App>): MutableList<App> {
        if (list.size > 0) {
            Collections.sort(list) { pApp, nApp -> pApp.name.toString().compareTo(nApp.name.toString()) }
        }
        return list
    }
}