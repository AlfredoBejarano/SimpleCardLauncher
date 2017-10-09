package com.alfredobejarano.simplecardlauncher.presenter.broadcastrecevier

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alfredobejarano.simplecardlauncher.view.MainActivity

/**
 * @author @AlfredoBejarano
 * @version 1.0
 * @since 08/10/2017
 */
class AppInstallerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startActivity(Intent(context, MainActivity().javaClass))
    }
}