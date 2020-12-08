package com.movie.exoplayerplayground

import android.os.Bundle
import android.service.carrier.CarrierMessagingService
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.cast.*
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.api.ResultCallback


class CastActivity : AppCompatActivity() {

    lateinit var castContext: CastContext
    lateinit var sessionManagerListener: SessionManagerListener<CastSession>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cast)

        castContext = CastContext.getSharedInstance(this)

        sessionManagerListener = object : SessionManagerListener<CastSession> {
            override fun onSessionStarted(p0: CastSession?, p1: String?) {
                // インターネットにある動画ファイルのリンク。各自用意して。
                val uri = ""
                val mediaMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
                    putString(MediaMetadata.KEY_TITLE, "たいとるだよ")
                    putString(MediaMetadata.KEY_SUBTITLE, "サブタイトルだよ")
                }

                val englishSubtitle = MediaTrack.Builder(1, MediaTrack.TYPE_TEXT)
                    .setName("English Subtitle")
                    .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                    .setContentId("https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/tracks/DesigningForGoogleCast-en.vtt") /* language is required for subtitle type but optional otherwise */
                    .setLanguage("en-US")
                    .build()

                val arSubtitle = MediaTrack.Builder(2, MediaTrack.TYPE_TEXT)
                    .setName("ar Subtitle")
                    .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                    .setContentId("https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/tracks/DesigningForGoogleCast-en.vtt") /* language is required for subtitle type but optional otherwise */
                    .setLanguage("ar-EG")
                    .build()

                val mediaInfo = MediaInfo.Builder(uri).apply {
                    setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    setContentType("video/mp4")
                    setMetadata(mediaMetadata)
                    setMediaTracks(listOf(englishSubtitle, arSubtitle))


                }
                val mediaLoadRequestData = MediaLoadRequestData.Builder().apply {
                    setMediaInfo(mediaInfo.build())
                        .setAutoplay(true)
                }
                val remoteMediaClient = p0?.remoteMediaClient
                remoteMediaClient?.load(mediaLoadRequestData.build())
                // the ID for the French subtitle is '2' and for the French audio '3'
                Log.e("remoteMediaClient", "Failed with status code:" );

                // the ID for the French subtitle is '2' and for the French audio '3'
                remoteMediaClient?.setActiveMediaTracks(longArrayOf(2, 3))?.setResultCallback {
                    Log.e("remoteMediaClient", "Failed with status code:" + it.status.statusCode);

                    if (!it.status.isSuccess) {
                        Log.e("remoteMediaClient", "Failed with status code:" + it.status.statusCode);
                    }
                }
                remoteMediaClient?.registerCallback(object : RemoteMediaClient.Callback() {
                    override fun onStatusUpdated() {
                        super.onStatusUpdated()
                        if (remoteMediaClient.playerState == MediaStatus.PLAYER_STATE_IDLE) {
                            println("再生終了")
                        }
                    }
                })
            }

            override fun onSessionResumeFailed(p0: CastSession?, p1: Int) {

            }

            override fun onSessionSuspended(p0: CastSession?, p1: Int) {

            }

            override fun onSessionEnded(p0: CastSession?, p1: Int) {

            }

            override fun onSessionResumed(p0: CastSession?, p1: Boolean) {

            }

            override fun onSessionStarting(p0: CastSession?) {

            }

            override fun onSessionResuming(p0: CastSession?, p1: String?) {

            }

            override fun onSessionEnding(p0: CastSession?) {

            }

            override fun onSessionStartFailed(p0: CastSession?, p1: Int) {

            }

        }
    }

    override fun onResume() {
        super.onResume()
        castContext.sessionManager.addSessionManagerListener(
            sessionManagerListener,
            CastSession::class.java
        )
    }

    override fun onPause() {
        super.onPause()
        castContext.sessionManager.removeSessionManagerListener(
            sessionManagerListener,
            CastSession::class.java
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item)
        return true
    }

}