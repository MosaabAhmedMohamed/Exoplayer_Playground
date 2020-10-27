package com.movie.exoplayerplayground.cast

import android.content.Context
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.framework.media.NotificationOptions

 class CastOptionsProvider : OptionsProvider {
    override fun getCastOptions(p0: Context?): CastOptions {

        //ExpandedControllerActivityに必要
        val notificationOptions = NotificationOptions.Builder()
            .setTargetActivityClassName(ControllerActivity::class.java.getName())
            .build()
        val mediaOptions = CastMediaOptions.Builder()
            .setNotificationOptions(notificationOptions)
            .setExpandedControllerActivityClassName(ControllerActivity::class.java.getName())
            .build()

        // 5ドル払わないので受信側をデフォルトにする。
        // Default Media Receiver って名前になる？
        val id = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID
        return CastOptions.Builder()
            .setReceiverApplicationId(id)
            .setCastMediaOptions(mediaOptions)
            .build()
    }

    override fun getAdditionalSessionProviders(p0: Context?): MutableList<SessionProvider>? {
        return null
    }
}