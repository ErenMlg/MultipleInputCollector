package com.softcross.multipleinputcollector

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable

@Composable
fun SelectionLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val placeables = measurables.map {
                it.measure(constraints)
            }

            val groupedPlaceable = mutableListOf<List<Placeable>>()
            var currentGroup = mutableListOf<Placeable>()
            var currentGroupWidth = 0

            placeables.forEach { placeable ->
                if (currentGroupWidth + placeable.width <= constraints.maxWidth) {
                    currentGroup.add(placeable)
                    currentGroupWidth += placeable.width
                } else {
                    groupedPlaceable.add(currentGroup)
                    currentGroup = mutableListOf(placeable)
                    currentGroupWidth = placeable.width
                }
            }

            if (currentGroup.isNotEmpty()) {
                groupedPlaceable.add(currentGroup)
            }

            var totalHeight = 0
            groupedPlaceable.forEach { row ->
                totalHeight += row.maxOfOrNull { it.height } ?: 0
            }

            layout(
                width = constraints.maxWidth,
                height = totalHeight
            ) {
                var yPosition = 0
                groupedPlaceable.forEach { row ->
                    var xPosition = 0
                    row.forEach { placeable ->
                        placeable.place(
                            x = xPosition,
                            y = yPosition + ((row.maxOf { it.height }) - placeable.height) / 2
                        )
                        xPosition += placeable.width
                    }
                    yPosition += row.maxOfOrNull { it.height } ?: 0
                }
            }
        },
        content = content
    )
}