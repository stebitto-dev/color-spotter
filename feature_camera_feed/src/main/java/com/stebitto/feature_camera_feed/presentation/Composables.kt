package com.stebitto.feature_camera_feed.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stebitto.common.theme.MyApplicationTheme
import com.stebitto.feature_camera_feed.R

@Composable
internal fun CameraPreview() {

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

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun CameraPermissionPreview() {
    MyApplicationTheme {  }
    CameraPermission()
}