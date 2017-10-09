package com.alfredobejarano.simplecardlauncher.presenter.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.alfredobejarano.simplecardlauncher.R
import com.alfredobejarano.simplecardlauncher.model.App
import com.alfredobejarano.simplecardlauncher.view.MainActivity
import com.alfredobejarano.simplecardlauncher.view.viewholder.AppsViewHolder

/**
 * @author @AlfredoBejarano
 * @version 1.0
 * @since 07/10/2017
 */
class AppsListAdapter(apps: MutableList<App>, activity: MainActivity) : RecyclerView.Adapter<AppsViewHolder>() {
    val apps: MutableList<App> = apps
    val activity: MainActivity = activity
    override fun onBindViewHolder(holder: AppsViewHolder?, position: Int) {
        holder?.render(apps[position], position)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AppsViewHolder {
        return AppsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.layout_item, parent, false), activity)
    }
}