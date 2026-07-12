package com.example.smarttodo.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TodoFilterSection(
    selectedFilter: String,
    filterOptions: List<String>,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
    ) {

        filterOptions.forEachIndexed { index, option ->

            SegmentedButton(
                selected = selectedFilter == option,
                onClick = {
                    onFilterSelected(option)
                },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = filterOptions.size
                )
            ) {
                Text(option)
            }

        }

    }

}