package com.stebitto.feature_camera_feed.presentation

import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.stebitto.feature_camera_feed.models.BitmapWrapper
import com.stebitto.feature_camera_feed.models.ColorPresentationModel
import android.graphics.Color as GraphicsColor
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
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    val analyzer = Analyzer { imageProxy ->
        viewModel.dispatch(
            CameraFeedIntent.OnFrameAnalyze(
                BitmapWrapper(imageProxy.toBitmap()),
                TARGET_RADIUS
            )
        )
        imageProxy.close()
    }

    LaunchedEffect(state.value.isAnalyzing) {
        if (state.value.isAnalyzing) {
            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                analyzer
            )
        } else {
            imageAnalysis.clearAnalyzer()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                CameraFeedEffect.GoToColorHistory -> {
                    imageAnalysis.clearAnalyzer()
                    onGoToColorHistoryClick()
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
            imageAnalysis = imageAnalysis
        )

        CircleHoleOverlay()

        ColorNameBox(colorItem = state.value.colorPresentationModel)

        ToggleFrameAnalyzeButton(
            isAnalyzing = state.value.isAnalyzing,
            onToggleFrameAnalyze = {
                if (state.value.isAnalyzing) {
                    viewModel.dispatch(CameraFeedIntent.OnStopAnalysis)
                } else {
                    viewModel.dispatch(CameraFeedIntent.OnStartAnalysis)
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
    imageAnalysis: ImageAnalysis
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
    colorItem: ColorPresentationModel
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        modifier = modifier
            .safeContentPadding()
            .align(Alignment.TopCenter)
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = Color(
                            colorItem.getRed(),
                            colorItem.getGreen(),
                            colorItem.getBlue()
                        )
                    )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = colorItem.colorName, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(8.dp))
                if (colorItem.getColorHex().isNotBlank()) {
                    Text(
                        text = "Hex: ${colorItem.getColorHex().uppercase()}",
                        style = Typography.bodyLarge
                    )
                }
                if (colorItem.colorInt != null) {
                    Text(text = "RGB: (${colorItem.getRed()}, ${colorItem.getGreen()}, ${colorItem.getBlue()})", style = Typography.bodyLarge)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
internal fun ColorNameBoxPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            ColorNameBox(
                colorItem = ColorPresentationModel(
                    colorInt = android.graphics.Color.CYAN,
                    colorName = "Color name"
                )
            )
        }
    }
}

@Composable
internal fun GoToColorHistoryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    IconButton(
        modifier = modifier.size(48.dp),
        onClick = { onClick() },
        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
    ) {
        Icon(
            imageVector = Icons.Filled.History,
            contentDescription = "Go to color history"
        )
    }
}

@Composable
internal fun BoxScope.ToggleFrameAnalyzeButton(
    modifier: Modifier = Modifier,
    isAnalyzing: Boolean,
    onToggleFrameAnalyze: () -> Unit = {},
    onGoToColorHistoryClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .safeGesturesPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onToggleFrameAnalyze() },
            modifier = Modifier.weight(0.8f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = (if (isAnalyzing) stringResource(R.string.stop_text) else stringResource(R.string.start_text)).uppercase(),
                fontWeight = FontWeight.W500,
                style = Typography.headlineSmall,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        GoToColorHistoryButton(onClick = { onGoToColorHistoryClick() })
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
internal fun ToggleFrameAnalyzeButtonPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            ToggleFrameAnalyzeButton(isAnalyzing = true)
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