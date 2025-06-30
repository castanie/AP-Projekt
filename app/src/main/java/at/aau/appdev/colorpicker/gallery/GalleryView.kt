package at.aau.appdev.colorpicker.gallery

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import at.aau.appdev.colorpicker.R
import at.aau.appdev.colorpicker.generateColor
import at.aau.appdev.colorpicker.generateColors
import at.aau.appdev.colorpicker.ui.theme.ColorPickerTheme
import kotlin.random.Random

@Composable
fun GalleryScreen(navController: NavController) {
    val viewModel: GalleryViewModel = viewModel()

    Scaffold { padding ->
        Log.i("sydaf", "GalleryScreen: $padding")
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gallery", fontSize = 40.sp, fontWeight = FontWeight.Medium)
                    IconButton(onClick = {}, modifier = Modifier.size(64.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_menu_open),
                            contentDescription = "Sort By",
                            tint = Color.Black
                        )
                    }
                }
                CardStaggeredGrid()
            }
            CameraNavButton(navController)
            // ActionButton(navController)
        }
    }
}

@Composable
fun BoxScope.CameraNavButton(navController: NavController) {
    FloatingActionButton(
        onClick = { navController.navigate("camera") },
        modifier = Modifier
            .padding(24.dp)
            .size(64.dp)
            .align(Alignment.BottomStart)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_view_in_ar), contentDescription = "Camera View"
        )
    }
}

/*
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BoxScope.ActionButton(navController: NavController) {
    var isExpanded = false
    FloatingActionButtonMenu(
        expanded = isExpanded,
        button = {
            ToggleFloatingActionButton(
                checked = isExpanded, onCheckedChange = { isExpanded = !isExpanded }) {}
        },
    ) {}
}
*/

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ColorPickerTheme {
        val fakeNavController = rememberNavController()
        GalleryScreen(fakeNavController)
    }
}

@Composable
fun CardStaggeredGrid() {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalItemSpacing = 2.dp,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(20) { item ->
            if (Random.nextInt(
                    0, 5
                ) > 1
            ) ColorSwatch(color = generateColor()) else ColorFan(colors = generateColors())
        }
    }
}

val offsets = arrayOf(
    floatArrayOf(0.6875f, 1.0f),
    floatArrayOf(0.625f, 0.8375f, 1.0f),
    floatArrayOf(0.5625f, 0.75f, 0.8875f, 1.0f),
    floatArrayOf(0.5f, 0.675f, 0.8125f, 0.9125f, 1.0f),
    floatArrayOf(0.4375f, 0.625f, 0.7625f, 0.8625f, 0.9375f, 1.0f)
)

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ColorSwatch(modifier: Modifier = Modifier, color: Color) {
    Box(
        modifier = modifier
            .aspectRatio(1.6f)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(color)
        ) {
            Text(
                color.toArgb().toHexString(HexFormat.UpperCase).slice(IntRange(2, 7)),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopStart)
            )
        }
    }
}

@Composable
fun ColorFan(modifier: Modifier = Modifier, colors: List<Color>) {
    val count = colors.size
    Box(
        modifier = modifier
            .aspectRatio(1.25f)
            .padding(8.dp)
    ) {
        for (i in (count - 1) downTo 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(offsets.get(count - 2).get(i))
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.get(i))
            )
        }

        Box(
            modifier = Modifier
                .padding(12.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
        )

        var textSize: IntSize = IntSize(80, 80);
        Text(
            "Collection",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomEnd)
                .zIndex(1.0f)
                .onGloballyPositioned { textSize = it.size })
        Log.i("LOOOOL", "ColorFan: $textSize")
        Box(modifier = Modifier
            .matchParentSize()
            .clip(RoundedCornerShape(8.dp))
            .graphicsLayer {
                renderEffect = android.graphics.RenderEffect.createBlurEffect(
                    20f, 20f, android.graphics.Shader.TileMode.CLAMP
                ).asComposeRenderEffect()
            }
            .background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.8f))
            .zIndex(0.9f)) {
            // TODO: Here, a duplicate of the color fan that has been created above
            // TODO: in the 'for' loop must be redrawn, clipped, an then blurred.

        }
    }
}


class DiscreteDistribution(private val outcomes: List<Int>, weights: List<Double>) {
    private val cumulative: List<Double>

    init {
        val total = weights.sum()
        cumulative = weights.runningFold(0.0) { acc, weight -> acc + weight / total }.drop(1)
    }

    fun sample(): Int {
        val r = Random.nextDouble()
        val index = cumulative.indexOfFirst { r <= it }
        return outcomes[index]
    }
}
