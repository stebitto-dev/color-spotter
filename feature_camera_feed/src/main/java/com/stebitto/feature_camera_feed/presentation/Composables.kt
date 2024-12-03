package com.stebitto.feature_camera_feed.presentation

import android.content.res.Configuration
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stebitto.common.theme.MyApplicationTheme
import com.stebitto.feature_camera_feed.R
import androidx.camera.core.Preview as CameraPreview

@Composable
internal fun CameraPreview(
    modifier: Modifier = Modifier,
    viewModel: CameraFeedViewModel
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    val radius = 100f
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = remember { CameraPreview.Builder().build() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    LaunchedEffect(cameraProviderFuture) {
        val provider = cameraProviderFuture.get()
        cameraProvider = provider

        provider?.let {
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            previewView?.let { previewView ->
                preview.surfaceProvider = previewView.surfaceProvider
                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context)
                ) { imageProxy ->
                    viewModel.dispatch(CameraFeedIntent.OnFrameAnalyze(imageProxy.toBitmap(), radius))
                    imageProxy.close()
                }
                it.unbindAll()
                it.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
            }
        }
    }

    Box(modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { currentContext ->
                PreviewView(currentContext).also { previewView = it }
            }
        )

        CircleHoleOverlay(radius = radius, modifier = modifier)

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(100.dp)
                .background(color = state.value.colorRgb)
                .padding(16.dp)
        ) {
            Text(text = "Color(${state.value.colorRgb.red}, ${state.value.colorRgb.green}, ${state.value.colorRgb.blue})")
        }
    }
}

@Composable
internal fun CircleHoleOverlay(
    radius: Float = 100f,
    borderWidth: Dp = 2.dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = modifier.fillMaxSize()) {
            // Semi transparent overlay
            drawRect(color = Color.Black.copy(alpha = 0.8f))
            // Stroke
            drawCircle(
                color = Color.White,
                radius = radius + borderWidth.toPx() / 2,
                style = Stroke(width = borderWidth.toPx())
            )
            // Target area
            drawCircle(
                color = Color.Transparent,
                radius = radius,
                blendMode = BlendMode.Clear
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFF0000)
@Composable
internal fun CircleHoleOverlayPreview() {
    MyApplicationTheme {
        CircleHoleOverlay()
    }
}

@Composable
internal fun CameraPermission(
    modifier: Modifier = Modifier,
    onPermissionRequest: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.camera_permission_required),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onPermissionRequest() }
        ) {
            Text(text = stringResource(R.string.camera_open))
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF000000,
    name = "Dark Mode"
)
@Composable
internal fun CameraPermissionPreview() {
    MyApplicationTheme {
        CameraPermission()
    }
}