package at.aau.appdev.colorpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import at.aau.appdev.colorpicker.camera.CameraScreen
import at.aau.appdev.colorpicker.detail.DetailScreen
import at.aau.appdev.colorpicker.gallery.GalleryScreen
import at.aau.appdev.colorpicker.ui.theme.ColorPickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColorPickerTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationHost(
                        modifier = Modifier.padding(innerPadding), navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(modifier = modifier, navController = navController, startDestination = "camera") {
        composable(
            route = "camera", arguments = listOf(navArgument("label") {
                type = NavType.StringType; defaultValue = "Camera"
            })
        ) { CameraScreen(navController) }

        composable(
            route = "gallery", arguments = listOf(navArgument("label") {
                type = NavType.StringType; defaultValue = "Gallery"
            })
        ) { DetailScreen(navController) }

        composable(
            route = "detail/{id}", arguments = listOf(navArgument("label") {
                type = NavType.StringType; defaultValue = "Detail"
            }, navArgument("id") {
                type = NavType.IntType; defaultValue = null
            })
        ) { GalleryScreen(navController) }
    }
}
