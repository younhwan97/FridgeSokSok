package com.yh.fridgesoksok.presentation.model

import androidx.annotation.DrawableRes
import com.yh.fridgesoksok.R

enum class Type(val id: Int, val label: String, @DrawableRes val icon: Int) {
    All(0, "전체", R.drawable.ingredients),
    Ingredients(1, "식재료", R.drawable.ingredients),
    Beverages(2, "음료", R.drawable.beverages),
    Bakery(3, "빵", R.drawable.bakery),
    Canned(4, "캔 & 병", R.drawable.canned),
    Sauces(5, "소스", R.drawable.sauces),
    Dairy(6, "유제품", R.drawable.daily),
    Frozen(7, "냉동", R.drawable.frozen),
    Health(8, "건강식품", R.drawable.health),
    Instant(9, "인스턴트", R.drawable.instant),
    Kimchi(10, "김치", R.drawable.kimchi),
    Meat(11, "고기", R.drawable.meat),
    Noodle(12, "면 & 파스타", R.drawable.noodle),
    SideDish(13, "반찬", R.drawable.side),
    Seafood(14, "해산물", R.drawable.seafood),
    Snack(15, "과자", R.drawable.snack),
    Tofu(16, "견과류", R.drawable.tofu);

    companion object {
        fun fromId(id: Int): Type {
            return entries.find { it.id == id } ?: All
        }
    }
}
