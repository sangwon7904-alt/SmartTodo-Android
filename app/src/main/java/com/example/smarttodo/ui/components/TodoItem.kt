package com.example.smarttodo.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttodo.data.model.Todo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItem(
    todo: Todo,
    onCheckedChange: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            )
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = { onCheckedChange() }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = todo.title,
            fontSize = 18.sp,
            textDecoration = if (todo.isCompleted) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            }
        )
    }
}