package com.yh.fridgesoksok.presentation.common.constants

/**
 * 앱에서 사용하는 문자열 리소스
 * TODO: 추후 Android Resource Strings (strings.xml)로 마이그레이션 권장
 */
object StringResources {
    // 에러 메시지
    object ErrorMessages {
        const val RECIPE_GENERATION_FAILED = "레시피 생성에 실패했습니다."
        const val RECIPE_GENERATION_QUOTA_EXCEEDED = "AI 레시피 생성 횟수를 모두 소진하였습니다."
        const val SELECT_INGREDIENTS = "재료를 선택해주세요"
        const val RECIPE_DELETE_FAILED = "레시피 삭제에 실패했어요."

        const val RECEIPT_RECOGNITION_FAILED = "영수증을 인식하지 못했어요 :("
        const val RECEIPT_QUOTA_EXCEEDED = "영수증 인식 횟수를 모두 소진하였습니다."
        const val FOOD_ADD_FAILED = "식품 추가에 실패했어요. 다시 시도해주세요."
        const val EXPIRY_INPUT_REQUIRED = "소비기한을 찾지 못한 식품이 있어요. 눌러서 직접 입력해주세요."

        const val FOOD_NAME_REQUIRED = "식품 이름을 입력해주세요"
        const val FOOD_UPDATE_FAILED = "변경에 실패했어요. 다시 시도해주세요."
        const val FOOD_DELETE_FAILED = "삭제에 실패했어요. 다시 시도해주세요."
        const val FOOD_LOAD_FAILED = "식품을 불러오는데 실패했어요."

        const val RECIPE_LOAD_FAILED = "레시피를 읽어올 수 없습니다 :("
    }

    // 확인 다이얼로그 메시지
    object DialogMessages {
        const val USE_THIS_PHOTO = "이 사진을 사용 할까요?"
        const val DELETE_RECIPE_CONFIRM = "정말 이 레시피를 삭제하시겠습니까?"
        const val DELETE_FOOD_CONFIRM = "정말 이 식품을 삭제하시겠습니까?"
    }

    // 버튼 텍스트
    object ButtonText {
        const val DELETE = "삭제"
        const val CONFIRM = "확인"
        const val CANCEL = "취소"
        const val SAVE = "저장"
        const val ADD = "추가하기"
        const val EDIT = "변경하기"
    }
}
