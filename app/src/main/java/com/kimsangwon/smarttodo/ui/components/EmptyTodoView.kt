package com.kimsangwon.smarttodo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyTodoView(
    hasAnyTodo: Boolean,
    selectedFilter: String,
    searchText: String
) {
    val isSearchResultEmpty = searchText.isNotBlank()

    val iconText = when {
        isSearchResultEmpty -> "🔍"
        else -> "📝"
    }

    val title = when {
        isSearchResultEmpty ->
            "검색 결과가 없습니다."

        hasAnyTodo ->
            "$selectedFilter 항목이 없습니다."

        else ->
            "아직 할 일이 없습니다."
    }

    val description = when {
        isSearchResultEmpty ->
            "다른 검색어를 입력해보세요."

        hasAnyTodo ->
            "다른 필터를 선택해보세요."

        else ->
            "오른쪽 아래 새 할 일 버튼을 눌러\n첫 번째 할 일을 추가해보세요."
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 56.dp,
                bottom = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = iconText,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}