package com.yh.fridgesoksok.presentation.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yh.fridgesoksok.presentation.common.comp.ConfirmDialog
import com.yh.fridgesoksok.presentation.common.comp.ErrorScreen
import com.yh.fridgesoksok.presentation.common.comp.SearchBar
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun confirmDialog_타이틀과_메시지가_표시되어야_한다() {
        // Given
        val title = "테스트 타이틀"
        val message = "테스트 메시지"

        // When
        composeTestRule.setContent {
            ConfirmDialog(
title = title,
                message = message,
                onConfirm = {},
                onDismiss = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(message).assertIsDisplayed()
    }

    @Test
    fun confirmDialog_확인_버튼_클릭_시_콜백이_호출되어야_한다() {
        // Given
        var confirmCalled = false

        // When
        composeTestRule.setContent {
            ConfirmDialog(
title = "테스트",
                message = "메시지",
                onConfirm = { confirmCalled = true },
                onDismiss = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("확인").performClick()
        assert(confirmCalled)
    }

    @Test
    fun errorScreen_에러_메시지가_표시되어야_한다() {
        // Given
        val errorMessage = "오류가 발생했습니다"

        // When
        composeTestRule.setContent {
            ErrorScreen(
                message = errorMessage,
                onRefreshClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun errorScreen_재시도_버튼이_표시되어야_한다() {
        // When
        composeTestRule.setContent {
            ErrorScreen(
                message = "에러",
                onRefreshClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("다시 시도").assertIsDisplayed()
    }

    @Test
    fun errorScreen_재시도_버튼_클릭_시_콜백이_호출되어야_한다() {
        // Given
        var retryCalled = false

        // When
        composeTestRule.setContent {
            ErrorScreen(
                message = "에러",
                onRefreshClick = { retryCalled = true }
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("다시 시도").performClick()
        assert(retryCalled)
    }

    @Test
    fun searchBar_placeholder가_표시되어야_한다() {
        // When
        composeTestRule.setContent {
            SearchBar(
                value = "",
                onValueChange = {},
                onDone = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("검색할 내용을 입력해주세요!").assertIsDisplayed()
    }

    @Test
    fun searchBar_입력된_텍스트가_표시되어야_한다() {
        // Given
        val query = "사과"

        // When
        composeTestRule.setContent {
            SearchBar(
                value = query,
                onValueChange = {},
                onDone = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(query).assertIsDisplayed()
    }
}
