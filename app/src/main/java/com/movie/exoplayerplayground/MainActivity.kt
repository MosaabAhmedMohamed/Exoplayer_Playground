package com.movie.exoplayerplayground

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.cast.*
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.movie.exoplayerplayground.cast.ControllerActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.view.*


class MainActivity : AppCompatActivity() {

    private val TAG = "TEST12345"

    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory

    lateinit var castContext: CastContext
    lateinit var sessionManagerListener: SessionManagerListener<CastSession>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        castContext = CastContext.getSharedInstance(this)
        sessionManagerListener = object : SessionManagerListener<CastSession> {
            override fun onSessionStarted(p0: CastSession?, p1: String?) {
                // インターネットにある動画ファイルのリンク。各自用意して。
                val uri = STREAM_URL
                val mediaMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
                    putString(MediaMetadata.KEY_TITLE, "Title")
                    putString(MediaMetadata.KEY_SUBTITLE, "Description")
                    //その他にも addImage でアルバムカバー？画像？の設定が可能
                }

                val englishSubtitle = MediaTrack.Builder(1, MediaTrack.TYPE_TEXT)
                    .setName("English Subtitle")
                    .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                    .setContentId("https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/tracks/DesigningForGoogleCast-en.vtt") /* language is required for subtitle type but optional otherwise */
                    .setLanguage("en-US")
                    .build()

                val arSubtitle = MediaTrack.Builder(2, MediaTrack.TYPE_TEXT)
                    .setName("m3u8 Subtitle")
                    .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                    .setContentId("https://github.com/grafov/m3u8/blob/master/sample-playlists/master-with-closed-captions-eq-none.m3u8") /* language is required for subtitle type but optional otherwise */
                    .setLanguage("ar-EG")

                    .build()

                val mediaInfo = MediaInfo.Builder(uri).apply {
                    setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    setContentType("videos/mp4")
                    setMetadata(mediaMetadata)
                    setMediaTracks(listOf(englishSubtitle, arSubtitle))
                }

                val mediaLoadRequestData = MediaLoadRequestData.Builder().apply {
                    setMediaInfo(mediaInfo.build())
                        .setAutoplay(true)
                }
                val remoteMediaClient = p0?.remoteMediaClient
                remoteMediaClient?.load(mediaLoadRequestData.build())
                remoteMediaClient?.setActiveMediaTracks(longArrayOf(1, 2))?.setResultCallback {
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
                showCastView(this@MainActivity)

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

    fun showCastView(context: Context) {
        val castSession =
            CastContext.getSharedInstance(context).sessionManager.currentCastSession
        if (castSession == null || !castSession.isConnected) {
            Log.w(
               "showCastView",
                "showQueuePopup(): not connected to a cast device"
            )
            return
        }
        val remoteMediaClient = castSession.remoteMediaClient
        if (remoteMediaClient == null) {
            Log.w("showCastView",
                "showQueuePopup(): null RemoteMediaClient"
            )
            return
        }
        val intent = Intent(context, ControllerActivity::class.java)
        context.startActivity(intent)
    }

    private fun initializePlayer() {

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)

        mediaDataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"))

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(Uri.parse(STREAM_URL))

        simpleExoPlayer.prepare(mediaSource, false, false)
        simpleExoPlayer.playWhenReady = true

        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.player = simpleExoPlayer
        playerView.requestFocus()

        /** Default repeat mode is REPEAT_MODE_OFF */
        btnChangeRepeatMode.setOnClickListener {
            if(simpleExoPlayer.repeatMode == Player.REPEAT_MODE_OFF)
                simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ONE
            else if(simpleExoPlayer.repeatMode == Player.REPEAT_MODE_ONE){
                simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ALL
            }
            else{
                simpleExoPlayer.repeatMode = Player.REPEAT_MODE_OFF
            }
        }

        simpleExoPlayer.addListener( object : Player.EventListener{
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                Log.d(TAG, "onPlaybackParametersChanged: ")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                Log.d(TAG, "onTracksChanged: ")
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.d(TAG, "onPlayerError: ")
            }

            /** 4 playbackState exists */
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when(playbackState){
                    Player.STATE_BUFFERING -> {
                        progressBar.visibility = View.VISIBLE
                        Log.d(TAG, "onPlayerStateChanged - STATE_BUFFERING" )
                        toast("onPlayerStateChanged - STATE_BUFFERING")
                    }
                    Player.STATE_READY -> {
                        progressBar.visibility = View.INVISIBLE
                        Log.d(TAG, "onPlayerStateChanged - STATE_READY" )
                        toast("onPlayerStateChanged - STATE_READY")
                    }
                    Player.STATE_IDLE -> {
                        Log.d(TAG, "onPlayerStateChanged - STATE_IDLE" )
                        toast("onPlayerStateChanged - STATE_IDLE")
                    }
                    Player.STATE_ENDED -> {
                        Log.d(TAG, "onPlayerStateChanged - STATE_ENDED" )
                        toast("onPlayerStateChanged - STATE_ENDED")
                    }
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.d(TAG, "onLoadingChanged: ")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                Log.d(TAG, "onPositionDiscontinuity: ")
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.d(TAG, "onRepeatModeChanged: ")
                Toast.makeText(baseContext, "repeat mode changed", Toast.LENGTH_SHORT).show()
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                Log.d(TAG, "onTimelineChanged: ")
            }
        })

        CastButtonFactory.setUpMediaRouteButton(this,
            playerView.media_route_btn
        )

    }

    private fun releasePlayer() {
        simpleExoPlayer.release()
    }

    public override fun onStart() {
        super.onStart()

        if (Util.SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23) initializePlayer()

        castContext.sessionManager.addSessionManagerListener(
            sessionManagerListener,
            CastSession::class.java
        )
    }

    public override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()

        castContext.sessionManager.removeSessionManagerListener(
            sessionManagerListener,
            CastSession::class.java
        )
    }

    public override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23) releasePlayer()
    }

    companion object {
        const val STREAM_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    }

}