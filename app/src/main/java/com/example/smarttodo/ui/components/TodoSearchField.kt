package com.example.smarttodo.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun TodoSearchField(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text("할 일 검색")
        },
        leadingIcon = {
            Text(
                text = "🔍",
                fontSize = 18.sp
            )
        },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                TextButton(
                    onClick = onClearClick
                ) {
                    Text("✕")
                }
            }
        },
        singleLine = true
    )
}