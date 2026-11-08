package com.yh.fridgesoksok.presentation.model

import androidx.annotation.DrawableRes
import com.yh.fridgesoksok.BuildConfig
import com.yh.fridgesoksok.R

/**
 * 식품 카테고리 (id 는 서버 FoodCategory 와 동일)
 * @param isExtended 서버에 추가된 카테고리(18~22). EXTENDED_CATEGORIES 가 켜져야 선택지에 노출
 */
enum class Type(
    val id: Int,
    val label: String,
    @DrawableRes val icon: Int,
    @DrawableRes val iconLarge: Int,
    val isExtended: Boolean = false
) {
    All(0, "전체", R.drawable.ingredients, R.drawable.ingredients_large),
    Others(1, "기타", R.drawable.ingredients, R.drawable.ingredients_large),
    SideDish(14, "반찬", R.drawable.side, R.drawable.side_large),
    Dairy(6, "유제품", R.drawable.daily, R.drawable.daily_large),
    Eggs(20, "달걀", R.drawable.daily, R.drawable.daily_large, isExtended = true), // TODO: 전용 아이콘
    Meat(11, "고기", R.drawable.meat, R.drawable.meat_large),
    Seafood(15, "해산물", R.drawable.seafood, R.drawable.seafood_large),
    Vegetables(18, "채소", R.drawable.ingredients, R.drawable.ingredients_large, isExtended = true), // TODO: 전용 아이콘
    Fruits(19, "과일", R.drawable.ingredients, R.drawable.ingredients_large, isExtended = true), // TODO: 전용 아이콘
    Bakery(3, "빵", R.drawable.bakery, R.drawable.bakery_large),
    Sauces(5, "소스", R.drawable.sauces, R.drawable.sauces_large),
    Health(8, "건강식품", R.drawable.health, R.drawable.health_large),
    BabyFood(22, "유아식", R.drawable.health, R.drawable.health_large, isExtended = true), // TODO: 전용 아이콘
    Beverages(2, "음료", R.drawable.beverages, R.drawable.beverages_large),
    Alcohol(21, "주류", R.drawable.beverages, R.drawable.beverages_large, isExtended = true), // TODO: 전용 아이콘
    Canned(4, "캔 & 병", R.drawable.canned, R.drawable.canned_large),
    Frozen(7, "냉동", R.drawable.frozen, R.drawable.frozen_large),
    Instant(9, "인스턴트", R.drawable.instant, R.drawable.instant_large),
    Kimchi(10, "김치", R.drawable.kimchi, R.drawable.kimchi_large),
    Noodle(12, "면 & 파스타", R.drawable.noodle, R.drawable.noodle_large),
    Ingredients(13, "제빵 및 기타 식재료", R.drawable.ingredients, R.drawable.ingredients_large),
    Snack(16, "과자", R.drawable.snack, R.drawable.snack_large),
    Tofu(17, "견과류", R.drawable.tofu, R.drawable.tofu_large);

    companion object {
        private val idMap = entries.associateBy { it.id }
        fun fromId(id: Int): Type = idMap[id] ?: All

        // 필터·편집 화면에 노출할 카테고리 (서버 미지원 카테고리 선택 방지)
        val selectableEntries: List<Type>
            get() = selectable(BuildConfig.EXTENDED_CATEGORIES)

        internal fun selectable(extendedEnabled: Boolean): List<Type> =
            entries.filter { extendedEnabled || !it.isExtended }
    }
}

val FoodModel.type: Type
    get() = Type.fromId(categoryId)
