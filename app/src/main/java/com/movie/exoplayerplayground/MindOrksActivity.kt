package com.movie.exoplayerplayground

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_mind_orks.*

class MindOrksActivity : AppCompatActivity(), Player.EventListener {

    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0
    private val mp4Url = "https://html5demos.com/assets/dizzy.mp4"
    private val dashUrl = "https://storage.googleapis.com/wvmedia/clear/vp9/tears/tears_uhd.mpd"
    private val urlList = listOf(mp4Url to "default", dashUrl to "dash")

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "exoplayer-sample")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_orks)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        simpleExoplayer = ExoPlayerFactory.newSimpleInstance(this)
        val randomUrl = urlList.random()
        preparePlayer(randomUrl.first, randomUrl.second)
        exoplayerView.player = simpleExoplayer
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.playWhenReady = true
        simpleExoplayer.addListener(this)
    }

    private fun buildMediaSource(uri: Uri, type: String): MediaSource {
        return if (type == "dash") {
            DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
        }
    }

    private fun preparePlayer(videoUrl: String, type: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri, type)
        simpleExoplayer.prepare(mediaSource)
    }

    private fun releasePlayer() {
        playbackPosition = simpleExoplayer.currentPosition
        simpleExoplayer.release()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        // handle error
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            progressBar.visibility = View.INVISIBLE
    }

}