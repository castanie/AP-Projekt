package at.aau.appdev.colorpicker.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import at.aau.appdev.colorpicker.ui.theme.ColorPickerTheme

@Composable
fun DetailScreen(navController: NavController) {
    val viewModel: DetailViewModel = viewModel()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ColorPickerTheme {
        val fakeNavController = rememberNavController()
        DetailScreen(fakeNavController)
    }
}