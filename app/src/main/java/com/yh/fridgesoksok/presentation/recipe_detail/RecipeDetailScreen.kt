package com.yh.fridgesoksok.presentation.recipe_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor2
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipe: RecipeModel?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (recipe != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    CoilImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        imageModel = { recipe.recipeImageUrl },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        ),
                        loading = {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .border(1.dp, CustomGreyColor3)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        },
                        failure = {
                            Image(
                                painter = painterResource(R.drawable.basic_food_image),
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                        },
                    )
                }

                item {
                    Column{
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = recipe.recipeName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = CustomGreyColor7
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = "생성날짜 ${recipe.createdAt}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Normal,
                            color = CustomGreyColor5
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = "재료",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CustomGreyColor7
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(recipe.ingredientTypes.size) { index ->
                                Column(
                                    modifier = Modifier.size(width = 80.dp, height = 106.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // 재료 아이콘
                                    Box(
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(CircleShape)
                                            .background(CustomGreyColor2)
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(Type.entries.firstOrNull { it.id == recipe.ingredientTypes[index] }?.icon ?: R.drawable.ingredients),
                                            contentDescription = null,
                                            contentScale = ContentScale.None
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = recipe.ingredients[index],
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Normal,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = recipe.recipeContent,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                            fontWeight = FontWeight.Normal,
                            color = CustomGreyColor7
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        } else {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "레시피를 읽어올 수 없습니다 :(",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp)
                .padding(top = 16.dp)
                .size(28.dp)
                .align(Alignment.TopStart)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .zIndex(1f),
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(4.dp)
                    .clickable {
                        navController.popBackStack()
                    },
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

//@Composable
//fun RecipeDetailScreen(
//    navController: NavController,
//    recipe: RecipeModel?
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        // ✅ 스크롤 가능한 영역
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            item {
//                CoilImage(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(300.dp),
//                    imageModel = { recipe?.recipeImageUrl },
//                    imageOptions = ImageOptions(
//                        contentScale = ContentScale.Crop,
//                        alignment = Alignment.Center
//                    ),
//                    loading = {
//                        Box(
//                            modifier = Modifier
//                                .matchParentSize()
//                                .border(1.dp, CustomGreyColor3)
//                        ) {
//                            CircularProgressIndicator(
//                                modifier = Modifier.align(Alignment.Center)
//                            )
//                        }
//                    },
//                    failure = {
//                        Image(
//                            painter = painterResource(R.drawable.basic_food_image),
//                            contentScale = ContentScale.Crop,
//                            contentDescription = null
//                        )
//                    },
//                )
//            }
//
//            recipe?.let {
//                item {
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Text(
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        text = it.recipeName,
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = CustomGreyColor7
//                    )
//                }
//
//                item {
//                    Spacer(modifier = Modifier.height(10.dp))
//                    Text(
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        text = "생성날짜 ${it.createdAt}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = CustomGreyColor5
//                    )
//                }
//
//                item {
//                    Spacer(modifier = Modifier.height(20.dp))
//                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Text(
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        text = "재료",
//                        style = MaterialTheme.typography.bodyMedium,
//                        fontWeight = FontWeight.SemiBold,
//                        color = CustomGreyColor7
//                    )
//                }
//
//                item {
//                    LazyRow(
//                        contentPadding = PaddingValues(horizontal = 16.dp),
//                        horizontalArrangement = Arrangement.spacedBy(10.dp)
//                    ) {
//                        val count = minOf(it.ingredients.size, it.ingredientTypes.size)
//                        items(count) { index ->
//                            Column(
//                                modifier = Modifier.size(width = 80.dp, height = 106.dp),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .size(70.dp)
//                                        .clip(CircleShape)
//                                        .background(CustomGreyColor2)
//                                        .padding(10.dp),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Image(
//                                        painter = painterResource(Type.entries.firstOrNull { t -> t.id == it.ingredientTypes[index] }?.icon ?: R.drawable.ingredients),
//                                        contentDescription = null,
//                                        contentScale = ContentScale.None
//                                    )
//                                }
//                                Spacer(modifier = Modifier.height(4.dp))
//                                Text(
//                                    text = it.ingredients[index],
//                                    style = MaterialTheme.typography.bodySmall,
//                                    textAlign = TextAlign.Center,
//                                    maxLines = 2,
//                                    overflow = TextOverflow.Ellipsis
//                                )
//                            }
//                        }
//                    }
//                }
//
//                item {
//                    Spacer(modifier = Modifier.height(20.dp))
//                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Text(
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        text = it.recipeContent,
//                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
//                        color = CustomGreyColor7
//                    )
//                    Spacer(modifier = Modifier.height(20.dp))
//                }
//            } ?: item {
//                Text(
//                    modifier = Modifier.align(Alignment.CenterHorizontally),
//                    text = "레시피를 읽어올 수 없습니다 :(",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.error
//                )
//            }
//        }
//
//        // ✅ 고정된 Back 버튼 (Box 위에 오버레이)
//        Box(
//            modifier = Modifier
//                .statusBarsPadding()
//                .padding(start = 16.dp, top = 16.dp)
//                .size(28.dp)
//                .align(Alignment.TopStart)
//                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
//                .clickable { navController.popBackStack() }
//                .zIndex(1f),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                imageVector = Icons.Default.ArrowBackIosNew,
//                contentDescription = null,
//                tint = Color.White
//            )
//        }
//    }
//}
