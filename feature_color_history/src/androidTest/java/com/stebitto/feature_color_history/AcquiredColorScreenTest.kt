package com.stebitto.feature_color_history

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.stebitto.feature_color_history.models.ColorPresentationModel
import com.stebitto.feature_color_history.presentation.ColorList
import org.junit.Rule
import org.junit.Test

class AcquiredColorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_ColorList_empty() {
        composeTestRule.setContent {
            ColorList(
                sortAlphabetically = false,
                colorItems = listOf(),
                errorMessage = null
            )
        }
        composeTestRule.onNodeWithTag("test_empty_list").assertExists()
    }

    @Test
    fun test_ColorList_withItems() {
        composeTestRule.setContent {
            ColorList(
                sortAlphabetically = false,
                colorItems = listOf(ColorPresentationModel(1, "Color 1", 1234567890, 123456)),
                errorMessage = null
            )
        }
        composeTestRule.onNodeWithTag("test_color_list").assertExists()
    }

    @Test
    fun test_ColorList_withErrorMessage() {
        composeTestRule.setContent {
            ColorList(
                sortAlphabetically = false,
                colorItems = listOf(),
                errorMessage = "Error message"
            )
        }
        composeTestRule.onNodeWithTag("test_error_message").assertExists()
    }
}