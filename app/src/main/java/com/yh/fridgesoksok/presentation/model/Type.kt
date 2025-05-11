package com.yh.fridgesoksok.presentation.model

import androidx.annotation.DrawableRes
import com.yh.fridgesoksok.R

enum class Type(val id: Int, val label: String, @DrawableRes val icon: Int, @DrawableRes val iconLarge: Int) {
    All(0, "전체", R.drawable.ingredients, R.drawable.ingredients_large),
    Others(1, "기타", R.drawable.ingredients, R.drawable.ingredients_large),
    Ingredients(13, "식재료", R.drawable.ingredients, R.drawable.ingredients_large),
    SideDish(14, "반찬", R.drawable.side, R.drawable.side_large),
    Dairy(6, "유제품", R.drawable.daily, R.drawable.daily_large),
    Meat(11, "고기", R.drawable.meat, R.drawable.meat_large),
    Seafood(14, "해산물", R.drawable.seafood, R.drawable.seafood_large),
    Bakery(3, "빵", R.drawable.bakery, R.drawable.bakery_large),
    Sauces(5, "소스", R.drawable.sauces, R.drawable.sauces_large),
    Health(8, "건강식품", R.drawable.health, R.drawable.health_large),
    Beverages(2, "음료", R.drawable.beverages, R.drawable.beverages_large),
    Canned(4, "캔 & 병", R.drawable.canned, R.drawable.canned_large),
    Frozen(7, "냉동", R.drawable.frozen, R.drawable.frozen_large),
    Instant(9, "인스턴트", R.drawable.instant, R.drawable.instant_large),
    Kimchi(10, "김치", R.drawable.kimchi, R.drawable.kimchi_large),
    Noodle(12, "면 & 파스타", R.drawable.noodle, R.drawable.noodle_large),
    Snack(15, "과자", R.drawable.snack, R.drawable.snack_large),
    Tofu(16, "견과류", R.drawable.tofu, R.drawable.tofu_large);

    companion object {
        fun fromId(id: Int): Type {
            return entries.find { it.id == id } ?: All
        }
    }
}
