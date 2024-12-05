package com.stebitto.colorspotter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.stebitto.common.theme.MyApplicationTheme
import com.stebitto.navigation.CameraFeedRoutes
import com.stebitto.navigation.ColorHistoryRoutes
import com.stebitto.navigation.cameraFeedNavGraph
import com.stebitto.navigation.colorHistoryNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = CameraFeedRoutes.CAMERA_FEED.name
                ) {
                    cameraFeedNavGraph(
                        onNavigateToColorHistory = {
                            navController.navigate(ColorHistoryRoutes.COLOR_HISTORY.name)
                        }
                    )
                    colorHistoryNavGraph()
                }
            }
        }
    }
}