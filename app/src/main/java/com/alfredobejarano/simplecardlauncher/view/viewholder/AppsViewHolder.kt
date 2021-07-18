package com.alfredobejarano.simplecardlauncher.view.viewholder

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.alfredobejarano.simplecardlauncher.R
import com.alfredobejarano.simplecardlauncher.model.App
import com.alfredobejarano.simplecardlauncher.presenter.Utils
import com.alfredobejarano.simplecardlauncher.view.MainActivity
import com.google.android.material.snackbar.Snackbar


/**
 * @author @AlfredoBejarano
 * @version 1.0
 * @since 07/10/2017
 */
class AppsViewHolder(itemView: View, activity: MainActivity) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
	private var appPos: Int = 0
	private lateinit var mApp: App
	private lateinit var pkg: String
	private var activity: MainActivity = activity

	/**
	 * Displays data app on an Item
	 */
	fun render(app: App, pos: Int) {
		appPos = pos
		mApp = app
		itemView.startAnimation(AnimationUtils.loadAnimation(itemView.context, android.R.anim.slide_in_left))
		itemView.findViewById<CardView>(R.id.itemCardView).setCardBackgroundColor(app.color)
		itemView.findViewById<ImageView>(R.id.item_image).setImageDrawable(app.icon)
		itemView.findViewById<TextView>(R.id.item_label).text = app.name
		itemView.findViewById<TextView>(R.id.item_label).setTextColor(Utils().getTextColor(app.color))
		pkg = app.pkg.toString()
		itemView.setOnClickListener(this)
		itemView.setOnLongClickListener(this)
	}

	/**
	 * Detects when a card has been long-pressed and prompts to uninstall it.
	 */
	override fun onLongClick(view: View?): Boolean {
		view?.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.shake))
		activity.uninstallApp(mApp.pkg.toString())
		return true
	}

	/**
	 * Opens an app when the card has been clicked.
	 */
	override fun onClick(view: View?) {
		try {
			activity.displayLoadingDialog(View.VISIBLE)
			view?.startAnimation(AnimationUtils.loadAnimation(view.context, android.R.anim.slide_out_right))
			view?.context?.startActivity(view.context?.packageManager?.getLaunchIntentForPackage(pkg))
			activity.displayLoadingDialog(View.GONE)
		} catch (e: Exception) {
			Snackbar.make(itemView.rootView, R.string.app_not_installed, Snackbar.LENGTH_SHORT).show()
		}
	}
}