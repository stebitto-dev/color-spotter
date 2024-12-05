package com.stebitto.feature_color_history.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stebitto.common.theme.MyApplicationTheme
import com.stebitto.common.theme.Typography
import com.stebitto.feature_color_history.R
import com.stebitto.feature_color_history.models.ColorPresentationModel
import java.util.Date

@Composable
internal fun ColorHistory(
    modifier: Modifier = Modifier,
    viewModel: ColorHistoryViewModel
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.dispatch(ColorHistoryIntent.LoadColors)
    }

    ColorListScreen(colorItems = state.value.colors)
}

@Composable
internal fun ColorListScreen(
    modifier: Modifier = Modifier,
    colorItems: List<ColorPresentationModel>,
    errorMessage: String? = null
) {
    when {
        errorMessage != null -> {
            Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
                ErrorMessage(errorMessage = errorMessage)
            }
        }
        colorItems.isEmpty() -> {
            Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
                EmptyListMessage()
            }
        }
        else -> LazyColumn {
            items(colorItems, key = { it.id }) { colorItem ->
                ColorItemCard(colorItem)
            }
        }
    }
}

@Composable
internal fun ColorItemCard(colorItem: ColorPresentationModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = Color(colorItem.red, colorItem.green, colorItem.blue))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = colorItem.name, fontWeight = FontWeight.Black)
                Text(text = Date(colorItem.lastSeen).toString(), style = Typography.bodyMedium)
                Text(text = "Hex: ${colorItem.hexCode}", style = Typography.bodyMedium)
                Text(text = "RGB: (${colorItem.red}, ${colorItem.green}, ${colorItem.blue})", style = Typography.bodyMedium)
            }
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
internal fun ColorItemCardPreview() {
    MyApplicationTheme {
        ColorItemCard(
            colorItem = ColorPresentationModel(
                id = 1,
                name = "Color Name",
                hexCode = "#FFFFFF",
                red = 255,
                green = 255,
                blue = 255,
                lastSeen = System.currentTimeMillis()
            )
        )
    }
}

@Composable
internal fun EmptyListMessage(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.empty_history),
        color = MaterialTheme.colorScheme.onBackground,
        style = Typography.bodyLarge,
        modifier = modifier.padding(16.dp)
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF000000,
    name = "Dark Mode"
)
@Composable
internal fun ColorListScreenPreview() {
    MyApplicationTheme {
        EmptyListMessage()
    }
}


@Composable
internal fun ErrorMessage(modifier: Modifier = Modifier, errorMessage: String) {
    Text(
        text = errorMessage,
        color = MaterialTheme.colorScheme.error,
        style = Typography.bodyLarge,
        modifier = modifier.padding(16.dp)
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF000000,
    name = "Dark Mode"
)
@Composable
internal fun ErrorMessagePreview() {
    MyApplicationTheme {
        ErrorMessage(errorMessage = "Error message")
    }
}