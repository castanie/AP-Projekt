package at.aau.appdev.colorpicker.gallery

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import at.aau.appdev.colorpicker.ui.theme.ColorPickerTheme

@Composable
fun GalleryScreen(navController: NavController) {
    val viewModel: GalleryViewModel = viewModel()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ColorPickerTheme {
        val fakeNavController = rememberNavController()
        GalleryScreen(fakeNavController)
    }
}