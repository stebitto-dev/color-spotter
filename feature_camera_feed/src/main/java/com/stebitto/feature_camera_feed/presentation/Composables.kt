package com.stebitto.feature_camera_feed.presentation

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color as GraphicsColor
import android.util.Size
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.Analyzer
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
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
import com.stebitto.common.theme.Typography
import com.stebitto.feature_camera_feed.R
import com.stebitto.feature_camera_feed.TARGET_RADIUS
import androidx.camera.core.Preview as CameraPreview
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
internal fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraFeedViewModel,
    onGoToColorHistoryClick: () -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val isCameraSetup = remember { mutableStateOf(false) }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    val analyzer = Analyzer { imageProxy ->
        viewModel.dispatch(CameraFeedIntent.OnFrameAnalyze(imageProxy.toBitmap(), TARGET_RADIUS))
        imageProxy.close()
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                CameraFeedEffect.ShowToastCameraNotReady -> {
                    Toast.makeText(context, R.string.camera_not_ready, Toast.LENGTH_SHORT).show()
                }
                CameraFeedEffect.GoToColorHistory -> {
                    imageAnalysis.clearAnalyzer()
                    onGoToColorHistoryClick()
                }
                CameraFeedEffect.StartFrameAnalysis -> {
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        analyzer
                    )
                }
                CameraFeedEffect.StopFrameAnalysis -> {
                    imageAnalysis.clearAnalyzer()
                }
            }
        }
    }

    val activityContext = LocalContext.current as ComponentActivity
    DisposableEffect(Unit) {
        activityContext.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(GraphicsColor.TRANSPARENT)
        )

        onDispose {
            activityContext.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(GraphicsColor.TRANSPARENT, GraphicsColor.TRANSPARENT)
            )
        }
    }

    Box(modifier.fillMaxSize()) {
        CameraPreview(
            context = context,
            imageAnalysis = imageAnalysis,
            onCameraReady = { isCameraSetup.value = true }
        )

        CircleHoleOverlay()

        val color = state.value.colorInt?.let { ComposeColor(it) } ?: ComposeColor.Gray
        ColorNameBox(
            color = color,
            colorName = state.value.colorName,
            colorHex = state.value.colorHex,
            colorRed = state.value.colorRed,
            colorGreen = state.value.colorGreen,
            colorBlue = state.value.colorBlue,
            colorLuminance = state.value.colorLuminance
        )

        ToggleFrameAnalyzeButton(
            isAnalyzing = state.value.isAnalyzing,
            color = color,
            colorLuminance = state.value.colorLuminance,
            onToggleFrameAnalyze = {
                if (state.value.isAnalyzing) {
                    viewModel.dispatch(CameraFeedIntent.OnStopAnalysis)
                } else {
                    if (isCameraSetup.value) {
                        viewModel.dispatch(CameraFeedIntent.OnStartAnalysis)
                    } else {
                        viewModel.dispatch(CameraFeedIntent.OnCameraNotReady)
                    }
                }
            },
            onGoToColorHistoryClick = {
                viewModel.dispatch(CameraFeedIntent.OnGoToColorHistory)
            }
        )
    }
}

@Composable
internal fun CameraPreview(
    context: Context,
    imageAnalysis: ImageAnalysis,
    onCameraReady: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = remember { CameraPreview.Builder().build() }
    val imageCapture = remember { ImageCapture.Builder().build() }

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
                it.unbindAll()
                it.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
                // Ready for image analysis
                onCameraReady()
            }
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { currentContext ->
            PreviewView(currentContext).also { previewView = it }
        }
    )
}

@Composable
internal fun CircleHoleOverlay(
    modifier: Modifier = Modifier,
    radius: Float = TARGET_RADIUS,
    borderWidth: Dp = 2.dp
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = modifier.fillMaxSize()) {
            // Semi transparent overlay
            drawRect(color = ComposeColor.Black.copy(alpha = 0.8f))
            // Stroke
            drawCircle(
                color = ComposeColor.White,
                radius = radius + borderWidth.toPx() / 2,
                style = Stroke(width = borderWidth.toPx())
            )
            // Target area
            drawCircle(
                color = ComposeColor.Transparent,
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
internal fun BoxScope.ColorNameBox(
    modifier: Modifier = Modifier,
    color: ComposeColor,
    colorName: String,
    colorHex: String,
    colorRed: Int,
    colorGreen: Int,
    colorBlue: Int,
    colorLuminance: Float
) {
    Column(
        modifier = modifier
            .safeContentPadding()
            .align(Alignment.TopCenter)
            .height(170.dp)
            .fillMaxWidth(0.8f)
            .background(color = color),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = colorName,
            color = if (colorLuminance < 0.5) ComposeColor.White else ComposeColor.Black,
            style = Typography.headlineMedium
        )
        if (colorHex.isNotBlank()) {
            Text(
                text = "#${colorHex.uppercase()}",
                color = if (colorLuminance < 0.5) ComposeColor.White else ComposeColor.Black,
                style = Typography.headlineSmall
            )
        }
        if (colorRed >= 0 && colorGreen >= 0 && colorBlue >= 0) {
            Text(
                text = "$colorRed, $colorGreen, $colorBlue",
                color = if (colorLuminance < 0.5) ComposeColor.White else ComposeColor.Black,
                style = Typography.headlineSmall
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
internal fun ColorNameBoxPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            ColorNameBox(
                color = ComposeColor.Gray,
                colorName = "Color name",
                colorHex = "000000",
                colorRed = 0,
                colorGreen = 0,
                colorBlue = 0,
                colorLuminance = 0.4f
            )
        }
    }
}

@Composable
internal fun GoToColorHistoryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        modifier = modifier.padding(16.dp),
        onClick = { onClick() }
    ) {
        Icon(
            imageVector = Icons.Filled.History,
            contentDescription = "Go to color history"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
internal fun GoToColorHistoryButtonPreview() {
    MyApplicationTheme {
        GoToColorHistoryButton()
    }
}

@Composable
internal fun BoxScope.ToggleFrameAnalyzeButton(
    modifier: Modifier = Modifier,
    isAnalyzing: Boolean,
    color: ComposeColor,
    colorLuminance: Float,
    onToggleFrameAnalyze: () -> Unit = {},
    onGoToColorHistoryClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(0.8f)
            .safeGesturesPadding()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier.weight(0.8f),
            onClick = { onToggleFrameAnalyze() },
            colors = ButtonDefaults.buttonColors(containerColor = color),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = (if (isAnalyzing) stringResource(R.string.stop_text) else stringResource(R.string.start_text)).uppercase(),
                color = if (colorLuminance < 0.5) ComposeColor.White else ComposeColor.Black,
                style = Typography.headlineSmall
            )
        }

        GoToColorHistoryButton(onClick = { onGoToColorHistoryClick() })
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
internal fun ToggleFrameAnalyzeButtonPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            ToggleFrameAnalyzeButton(
                isAnalyzing = true,
                color = ComposeColor.Gray,
                colorLuminance = 0.4f
            )
        }
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