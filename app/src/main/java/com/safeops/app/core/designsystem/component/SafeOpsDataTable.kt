package com.safeops.app.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Definition of a Table Column.
 */
data class SafeOpsTableColumn<T>(
    val title: String,
    val key: String,
    val weight: Float = 1f,
    val isSortable: Boolean = true,
    val cellContent: @Composable (item: T) -> Unit
)

/**
 * Enterprise SafeOps Data Table Component.
 * Features:
 * - Search bar with instant text query filter
 * - Quick filter pills
 * - Sortable columns with ascending/descending arrow indicators
 * - Clickable table rows with hover/ripple feedback
 * - Empty state feedback when query yields no results
 * - Dark & Light mode theme-adaptive styling
 */
@Composable
fun <T> SafeOpsDataTable(
    columns: List<SafeOpsTableColumn<T>>,
    items: List<T>,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    onSearchQueryChange: ((String) -> Unit)? = null,
    searchPlaceholder: String = "Search records...",
    filters: List<String> = emptyList(),
    selectedFilter: String? = null,
    onFilterSelected: ((String?) -> Unit)? = null,
    sortColumnKey: String? = null,
    isAscending: Boolean = true,
    onSortClick: ((String) -> Unit)? = null,
    emptyMessage: String = "No data found matching criteria"
) {
    val isDark = SafeOpsTheme.colors.isDark
    val cardBg = if (isDark) Color(0xFF18181B) else Color(0xFFFFFDFF)
    val headerRowBg = if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9)
    val altRowBg = if (isDark) Color(0xFF1F1F23) else Color(0xFFFAFAFD)
    val borderColor = if (isDark) Color(0xFF333338) else Color(0xFFE4E2E9)
    val textColor = if (isDark) Color(0xFFE8E6EC) else Color(0xFF18181D)
    val subTextColor = if (isDark) Color(0xFFA6A3AA) else Color(0xFF77767E)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ── Top Controls: Search Bar & Filter Chips ──
            if (onSearchQueryChange != null || filters.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Search Bar
                    if (onSearchQueryChange != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9))
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = SafeOpsIcons.Search,
                                    contentDescription = "Search",
                                    tint = subTextColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = onSearchQueryChange,
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = textColor,
                                        fontSize = 13.5.sp
                                    ),
                                    cursorBrush = SolidColor(Color(0xFF0878FF)),
                                    modifier = Modifier.weight(1f),
                                    decorationBox = { innerTextField ->
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = searchPlaceholder,
                                                fontSize = 13.5.sp,
                                                color = subTextColor.copy(alpha = 0.7f)
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                                if (searchQuery.isNotEmpty()) {
                                    Icon(
                                        imageVector = SafeOpsIcons.Close,
                                        contentDescription = "Clear",
                                        tint = subTextColor,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { onSearchQueryChange("") }
                                    )
                                }
                            }
                        }
                    }

                    // Filter Chips Row
                    if (filters.isNotEmpty() && onFilterSelected != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                val isAllSelected = selectedFilter == null
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (isAllSelected) Color(0xFF0878FF)
                                            else (if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9))
                                        )
                                        .border(
                                            1.dp,
                                            if (isAllSelected) Color(0xFF0878FF) else borderColor,
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable { onFilterSelected(null) }
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "All",
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isAllSelected) Color.White else subTextColor
                                    )
                                }
                            }

                            items(filters) { filter ->
                                val isSelected = selectedFilter == filter
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (isSelected) Color(0xFF0878FF)
                                            else (if (isDark) Color(0xFF27272A) else Color(0xFFF1F5F9))
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF0878FF) else borderColor,
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable { onFilterSelected(filter) }
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = filter,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else subTextColor
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(color = borderColor, thickness = 1.dp)
            }

            // ── Table Column Headers ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerRowBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                columns.forEach { col ->
                    val isSorted = sortColumnKey == col.key
                    Row(
                        modifier = Modifier
                            .weight(col.weight)
                            .clickable(enabled = col.isSortable && onSortClick != null) {
                                onSortClick?.invoke(col.key)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = col.title,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSorted) Color(0xFF0878FF) else subTextColor,
                            letterSpacing = 0.5.sp
                        )
                        if (col.isSortable) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSorted && isAscending) "↑" else if (isSorted) "↓" else "↕",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSorted) Color(0xFF0878FF) else subTextColor.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = borderColor, thickness = 1.dp)

            // ── Table Rows / Empty State ──
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = SafeOpsIcons.Document,
                            contentDescription = null,
                            tint = subTextColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = emptyMessage,
                            fontSize = 13.sp,
                            color = subTextColor
                        )
                    }
                }
            } else {
                items.forEachIndexed { index, item ->
                    val rowBg = if (index % 2 == 1) altRowBg else Color.Transparent
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rowBg)
                            .clickable { onItemClick(item) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        columns.forEach { col ->
                            Box(modifier = Modifier.weight(col.weight)) {
                                col.cellContent(item)
                            }
                        }
                    }
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            color = borderColor.copy(alpha = 0.5f),
                            thickness = 0.5.dp
                        )
                    }
                }

                // ── Footer Count Strip ──
                HorizontalDivider(color = borderColor, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerRowBg)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: ${items.size} records",
                        fontSize = 11.5.sp,
                        color = subTextColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
