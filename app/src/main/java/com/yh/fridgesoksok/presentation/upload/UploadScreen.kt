package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.theme.CustomBackGroundColor
import com.yh.fridgesoksok.presentation.theme.CustomBlackTextColor
import com.yh.fridgesoksok.presentation.theme.CustomPrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    navController: NavController,
    viewModel: UploadViewModel = hiltViewModel()
) {
    // 사진/갤러리에서 화면이 네비게이트 된 경우
    val capturedImage =
        navController.previousBackStackEntry?.savedStateHandle?.get<Bitmap>("capturedImage")

    LaunchedEffect(Unit) {
        if (capturedImage != null) {
            viewModel.uploadReceiptImage(capturedImage)
        }
    }

    val newFoods by viewModel.newFoods.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                title = {
                    Text(
                        text = "음식 추가하기",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.insertEmptyFood() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "추가하기"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Button(
                    onClick = { /* 클릭 동작 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = CustomPrimaryColor
                    )
                ) {
                    Text(
                        text = "저장하기",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current,
                        fontWeight = FontWeight(700)
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .background(CustomBackGroundColor)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(newFoods) { index, food ->
                FoodInputBlock(
                    index = index,
                    name = food.name,
                    type = food.type,
                    count = food.count,
                    onNameChange = { viewModel.updateFoodName(index, it) },
                    onTypeChange = { viewModel.updateFoodCategory(index, it) },
                    onCountChange = { viewModel.updateFoodCount(index, it) }
                )
            }
        }

    }
}

@Composable
fun FoodInputBlock(
    index: Int,
    name: String,
    type: Int,
    count: String,
    onNameChange: (String) -> Unit,
    onTypeChange: (Int) -> Unit,
    onCountChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "음식 ${index + 1}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = CustomBlackTextColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        FoodNameInput(name, onNameChange)

        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            FoodTypeSelector(type, onTypeChange)

            Spacer(modifier = Modifier.width(4.dp))

            FoodCountInput(count, onCountChange)
        }
    }
}

@Composable
fun FoodNameInput(
    name: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value
    val borderColor = if (isFocused) Color.Black else Color.LightGray
    val borderWidth = if (isFocused) 1.5.dp else 1.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        if (name.isEmpty()) {
            Text(
                text = "식품 이름을 입력하세요",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        BasicTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            interactionSource = interactionSource
        )
    }
}


@Composable
fun FoodTypeSelector(
    selectedType: Int,
    onTypeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryOptions = listOf("반찬", "음료", "과일", "육류", "냉동", "소스", "유제품", "채소", "과자", "기타")
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = categoryOptions.getOrNull(selectedType) ?: ""
    val borderColor = if (expanded) Color.Black else Color.LightGray
    val borderWidth = if (expanded) 1.5.dp else 1.dp
    val focusManager = LocalFocusManager.current

    Column {
        Box(
            modifier = modifier
                .fillMaxWidth(0.5f)
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .clickable { expanded = true }
            ) {
                Text(
                    text = selectedLabel.ifEmpty { "유형" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedLabel.isEmpty()) Color.Gray else Color.Black
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.White)
                    .heightIn(max = 166.dp)
            ) {
                categoryOptions.forEachIndexed { index, category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onTypeSelected(index)
                            expanded = false
                            focusManager.clearFocus(force = true) // 🔥 포커스 제거
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FoodCountInput(
    count: String,
    onCountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value

    val borderColor = if (isFocused) Color.Black else Color.LightGray
    val borderWidth = if (isFocused) 1.5.dp else 1.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        if (count.isEmpty()) {
            Text(
                text = "수량을 입력하세요",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        BasicTextField(
            value = count,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    val number = newValue.toIntOrNull()
                    if (number == null || number <= 99) {
                        onCountChange(newValue)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            interactionSource = interactionSource,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),

            )
    }
}