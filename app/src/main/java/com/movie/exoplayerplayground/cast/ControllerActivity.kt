package com.movie.exoplayerplayground.cast


import android.view.Menu
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity
import com.movie.exoplayerplayground.R

class ControllerActivity : ExpandedControllerActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item)
        return true
    }

}