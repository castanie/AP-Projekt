package at.aau.appdev.colorpicker.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import at.aau.appdev.colorpicker.ui.theme.ColorPickerTheme

@Composable
fun CameraScreen(navController: NavController) {
    val viewModel: CameraViewModel = viewModel()
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    ColorPickerTheme {
        val fakeNavController = rememberNavController()
        CameraScreen(fakeNavController)
    }
}