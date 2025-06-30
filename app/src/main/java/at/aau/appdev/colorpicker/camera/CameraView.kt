package at.aau.appdev.colorpicker.camera

import android.opengl.GLSurfaceView
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import at.aau.appdev.colorpicker.MainActivity
import at.aau.appdev.colorpicker.R
import at.aau.appdev.colorpicker.generateColor
import at.aau.appdev.colorpicker.ui.theme.ColorPickerTheme
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import kotlinx.coroutines.delay
import kotlin.random.Random

var anchor: Anchor? = null

@Composable
fun CameraScreen(navController: NavController) {
    val viewModel: CameraViewModel = viewModel()

    val session = (LocalActivity.current as MainActivity).session!!
    val display = LocalContext.current.display
    val renderer = CameraRenderer(session, display)
    val lifecycleOwner = LocalLifecycleOwner.current

    // https://developer.android.com/develop/ui/compose/migrate/interoperability-apis/views-in-compose
    AndroidView(factory = { context ->
        // https://developer.android.com/reference/android/opengl/GLSurfaceView
        // https://github.com/google-ar/arcore-android-sdk/tree/main/samples/hello_ar_kotlin
        GLSurfaceView(context).apply {
            setEGLContextClientVersion(3)
            setRenderer(renderer)
        }
    }, modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = { offset ->
                // TODO: Here, the 'viewModel' should be tasked with adding a new point based on
                // TODO: the tap offset. The coordinates do not have to be normalized as both
                // TODO: 'hitTest' and 'hitTestInstantPlacement' support screen-space coordinates.
                // INFO: Whether ARCore is used or not, a custom interface should be added to allow
                // INFO: for different data providers.
                Log.d(
                    "CameraView.CameraScreen", "Tapped at coordinates ${offset.x}, ${offset.y}."
                )
                var hitResults = emptyList<HitResult>()
                do {
                    hitResults = renderer.frame.hitTestInstantPlacement(offset.x, offset.x, 3.0f)
                    Log.d("CameraView.CameraScreen", "Hit test failed.")
                } while (hitResults.isEmpty())
                Log.d("CameraView.CameraScreen", "Hit test succeeded.")
                // TODO: Every anchor should be stored in the 'viewModel' so it can be properly
                // TODO: disposed of when it is not needed anymore:
                anchor = hitResults.get(0).createAnchor()
            })
        }, update = { view ->
        val glView = view as GLSurfaceView

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    session.resume()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    glView.onPause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    glView.onResume()
                }

                Lifecycle.Event.ON_STOP -> {
                    session.pause()
                }

                else -> Log.d("GLSurfaceView", "Lifecycle: $event")
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
    })

    ColorProbeOverlay(listOf(0, 1, 2, 3, 4))
    Box(modifier = Modifier.fillMaxSize()) {
        ControlRow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            navController = navController
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    ColorPickerTheme {
        val fakeNavController = rememberNavController()
        CameraScreen(fakeNavController)
    }
}

@Composable
fun ColorProbeOverlay(
    points: List<Int>,
    modifier: Modifier = Modifier,
    ringRadiusDp: Dp = 12.dp,
    ringThicknessDp: Dp = 4.dp
) {
    val refreshTrigger = remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            refreshTrigger.intValue = (refreshTrigger.intValue + 1) % Int.MAX_VALUE
        }
    }

    val density = LocalDensity.current
    val ringRadiusPx = with(density) { ringRadiusDp.toPx() }
    val innerRadiusPx = ringRadiusPx - with(density) { ringThicknessDp.toPx() }

    val random = Random(refreshTrigger.intValue)
    val display = LocalContext.current.display
    Canvas(modifier = modifier) {

        ///
        if (anchor != null) {
            // FIXME:
            val offset = Offset(0.0f, 0.0f)
            val color = generateColor()

            drawCircle(
                color = Color.White, radius = ringRadiusPx, center = offset
            )
            drawCircle(
                color = color, radius = innerRadiusPx, center = offset
            )
        }
        ///

        points.forEach { point ->
            val offset =
                Offset(random.nextFloat() * display.width, random.nextFloat() * display.height)
            val color = generateColor()

            drawCircle(
                color = Color.White, radius = ringRadiusPx, center = offset
            )
            drawCircle(
                color = color, radius = innerRadiusPx, center = offset
            )
        }
    }
}

@Composable
fun ControlRow(modifier: Modifier, navController: NavController) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PhotoLibraryNavButton(navController = navController)
        CaptureButton { }
        ColorGalleryNavButton(navController = navController)
    }
}

@Composable
fun CaptureButton(
    modifier: Modifier = Modifier, innerColor: Color = Color.DarkGray, onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(160.dp, 60.dp)
            .shadow(8.dp, RoundedCornerShape(50), clip = false)
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .padding(4.dp)
            .clip(RoundedCornerShape(50))
            .background(innerColor)
            .combinedClickable(onClick = {
                // TODO: Save single probe to gallery (using 'viewModel').
                Log.d("CameraView.CaptureButton", "Short press of capture button.")
            }, onLongClick = {
                // TODO: Save multiple probes to gallery (using 'viewModel').
                Log.d("CameraView.CaptureButton", "Long press of capture button.")
            })
    )
}

@Composable
fun DropShadowIconButton(resource: Int, contentDescription: String?, onClick: () -> Unit) {
    Box(modifier = Modifier.size(64.dp)) {
        Icon(
            painter = painterResource(resource),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .blur(12.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .alpha(0.2f)
        )
        IconButton(
            onClick = onClick, modifier = Modifier.size(64.dp)
        ) {
            Icon(
                painter = painterResource(resource),
                contentDescription = contentDescription,
                tint = Color.White
            )
        }
    }
}

@Composable
fun PhotoLibraryNavButton(navController: NavController) {
    DropShadowIconButton(R.drawable.ic_photo_library, "Photo Library", {})
}

@Composable
fun ColorGalleryNavButton(navController: NavController) {
    DropShadowIconButton(
        R.drawable.ic_color_palette, "Color Palette", { navController.navigate("gallery") })
}
