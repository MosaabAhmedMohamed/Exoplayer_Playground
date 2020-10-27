package com.movie.exoplayerplayground

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaStatus
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient

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
                    //その他にも addImage でアルバムカバー？画像？の設定が可能
                }
                val mediaInfo = MediaInfo.Builder(uri).apply {
                    setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    setContentType("videos/mp4")
                    setMetadata(mediaMetadata)
                }
                val mediaLoadRequestData = MediaLoadRequestData.Builder().apply {
                    setMediaInfo(mediaInfo.build())
                }
                val remoteMediaClient = p0?.remoteMediaClient
                remoteMediaClient?.load(mediaLoadRequestData.build())

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