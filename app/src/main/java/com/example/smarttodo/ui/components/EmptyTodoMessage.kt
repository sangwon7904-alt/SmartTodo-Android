package com.example.smarttodo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyTodoMessage(
    selectedFilter: String,
    searchText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = when {
                searchText.isNotBlank() -> "검색 결과가 없습니다."
                selectedFilter == "오늘" -> "오늘 마감할 일이 없습니다."
                selectedFilter == "미완료" -> "남아 있는 할 일이 없습니다."
                selectedFilter == "완료" -> "완료한 할 일이 없습니다."
                else -> "아직 할 일이 없습니다."
            },
            fontSize = 18.sp
        )

        if (selectedFilter == "전체" && searchText.isBlank()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "새 할 일 버튼을 눌러 할 일을 추가하세요.",
                fontSize = 14.sp
            )
        }
    }
}