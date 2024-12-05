package com.stebitto.feature_color_history.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.stebitto.common.formatTimestamp
import com.stebitto.common.theme.MyApplicationTheme
import com.stebitto.common.theme.Typography
import com.stebitto.feature_color_history.R
import com.stebitto.feature_color_history.models.ColorPresentationModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ColorHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: ColorHistoryViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.dispatch(ColorHistoryIntent.LoadColors)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.acquired_screen_title),
                        style = Typography.headlineMedium,
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back_content_description)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.dispatch(ColorHistoryIntent.OnSortColors) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SortByAlpha,
                            contentDescription = stringResource(R.string.sort_alphabetically_content_description)
                        )
                    }
                }
            )
        },
    ) { padding ->
        ColorList(
            modifier = modifier.padding(padding),
            sortAlphabetically = state.value.sortAlphabetically,
            colorItems = state.value.colors,
            errorMessage = state.value.errorMessage,
            onDelete = { viewModel.dispatch(ColorHistoryIntent.OnDeleteColor(it)) }
        )
    }
}

@Composable
internal fun ColorList(
    modifier: Modifier = Modifier,
    sortAlphabetically: Boolean,
    colorItems: List<ColorPresentationModel>,
    errorMessage: String? = null,
    onDelete: (ColorPresentationModel) -> Unit
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
        else -> LazyColumn(modifier = modifier) {
            items(
                if (sortAlphabetically) colorItems.sortedBy { it.name } else colorItems,
                key = { it.id }
            ) { colorItem ->
                ColorItemCard(
                    colorItem = colorItem,
                    onDelete = { onDelete(colorItem) }
                )
            }
        }
    }
}

@Composable
internal fun ColorItemCard(
    colorItem: ColorPresentationModel,
    onDelete: () -> Unit = {}
) {
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
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = Color(colorItem.red, colorItem.green, colorItem.blue))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = colorItem.name, fontWeight = FontWeight.Black)
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = colorItem.lastSeen.formatTimestamp(), style = Typography.bodyMedium)
                        Text(text = "Hex: ${colorItem.hexCode}", style = Typography.bodyMedium)
                        Text(text = "RGB: (${colorItem.red}, ${colorItem.green}, ${colorItem.blue})", style = Typography.bodyMedium)
                    }
                    IconButton(
                        modifier = Modifier.align(Alignment.Bottom),
                        onClick = { onDelete() }
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_content_description))
                    }
                }
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