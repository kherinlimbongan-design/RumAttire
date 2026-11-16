
package com.example.rumattire

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================
// COLOR PALETTE
// ============================================================

private val ReturnBackground = Color(0xFFF4EFE7)
private val ReturnSurface = Color(0xFFFCF9F3)
private val ReturnBrown = Color(0xFF2E211B)
private val ReturnSecondary = Color(0xFF8C7B6F)
private val ReturnMuted = Color(0xFF5A4A3F)
private val ReturnBorder = Color(0xFFE4DACB)

private val ReturnHeroStart = Color(0xFF5C4538)
private val ReturnHeroEnd = Color(0xFF2F211B)

private val ReturnSuccessBg = Color(0xFFD8F0EA)
private val ReturnSuccess = Color(0xFF1E8F7E)
private val ReturnSuccessDark = Color(0xFF17756A)
private val ReturnSuccessBorder = Color(0xFFBFE3DB)

private val ReturnDangerBg = Color(0xFFFCE5DB)
private val ReturnDanger = Color(0xFFE8714A)
private val ReturnDangerDark = Color(0xFFC4502A)
private val ReturnDangerBorder = Color(0xFFF4B9A2)

private val ReturnWarningBg = Color(0xFFFBF1D3)
private val ReturnWarning = Color(0xFFE3A93B)
private val ReturnWarningDark = Color(0xFF8A6410)

private val ReturnPurple = Color(0xFF6C5CE7)
private val ReturnPurpleBg = Color(0xFFEEEAFE)

private val ReturnItemIconBg = Color(0xFFF6EEE2)
private val ReturnNoteBg = Color(0xFFFDF0EA)
private val ReturnDisabled = Color(0xFFC9BBAD)


// ============================================================
// DATA MODEL
// ============================================================

private enum class ReturnCondition {
    GOOD,
    DAMAGED,
    MISSING,
    PENDING
}

private enum class ReturnItemIcon {
    SHIRT,
    CROWN
}

private data class ReturnItemUi(
    val id: String,
    val name: String,
    val sku: String,
    val variant: String,
    val boxId: String,
    val icon: ReturnItemIcon,
    val condition: ReturnCondition,
    val note: String? = null,
    val highlighted: Boolean = false
)

data class ReturnItemResult(
    val itemId: String,
    val condition: String,
    val note: String?
)

private fun initialReturnItems() = listOf(

    ReturnItemUi(
        id = "GN-021",
        name = "Gaun Satin Champagne",
        sku = "GN-021",
        variant = "Ukuran M",
        boxId = "BX-004",
        icon = ReturnItemIcon.SHIRT,
        condition = ReturnCondition.GOOD,
        highlighted = true
    ),

    ReturnItemUi(
        id = "SL-005",
        name = "Selendang Songket Emas",
        sku = "SL-005",
        variant = "All size",
        boxId = "BX-009",
        icon = ReturnItemIcon.SHIRT,
        condition = ReturnCondition.GOOD
    ),

    ReturnItemUi(
        id = "AC-011",
        name = "Tiara Mutiara",
        sku = "AC-011",
        variant = "All size",
        boxId = "BX-012",
        icon = ReturnItemIcon.CROWN,
        condition = ReturnCondition.DAMAGED,
        note = "Mutiara lepas 2 butir."
    ),

    ReturnItemUi(
        id = "CV-016",
        name = "Cover Garment",
        sku = "CV-016",
        variant = "All size",
        boxId = "BX-004",
        icon = ReturnItemIcon.SHIRT,
        condition = ReturnCondition.PENDING
    )
)


// ============================================================
// MAIN SCREEN
// ============================================================

@Composable
fun ReturnManagementEmployeeScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onItemStatusChanged: (String, String) -> Unit = { _, _ -> },
    onAddDamagePhoto: (String) -> Unit = {},
    onFinishReturn: (List<ReturnItemResult>) -> Unit = {}
) {

    var items by remember {
        mutableStateOf(initialReturnItems())
    }

    val totalItems = items.size

    val goodCount = items.count {
        it.condition == ReturnCondition.GOOD
    }

    // Barang rusak dan hilang masuk kategori
    // barang yang membutuhkan tindak lanjut.
    val damagedCount = items.count {
        it.condition == ReturnCondition.DAMAGED ||
                it.condition == ReturnCondition.MISSING
    }

    val pendingCount = items.count {
        it.condition == ReturnCondition.PENDING
    }

    val inspectedCount = totalItems - pendingCount
    val remainingCount = pendingCount

    val progress = if (totalItems > 0) {
        inspectedCount.toFloat() / totalItems.toFloat()
    } else {
        0f
    }

    val updateCondition: (String, ReturnCondition) -> Unit =
        { itemId, newCondition ->

            items = items.map { item ->
                if (item.id == itemId) {
                    item.copy(condition = newCondition)
                } else {
                    item
                }
            }

            onItemStatusChanged(itemId, newCondition.name)
        }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = ReturnBackground
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // HEADER
            ReturnHeader(
                onBack = onBack
            )

            // SCROLLABLE CONTENT
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {

                    Spacer(Modifier.height(4.dp))

                    // ORDER INFORMATION
                    ReturnHeroCard(
                        inspectedCount = inspectedCount,
                        totalItems = totalItems,
                        progress = progress
                    )

                    Spacer(Modifier.height(12.dp))

                    // SUMMARY CARDS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        ReturnSummaryCard(
                            modifier = Modifier.weight(1f),
                            count = goodCount,
                            label = "Baik",
                            icon = ReturnGlyphType.CHECK,
                            iconBackground = ReturnSuccessBg,
                            iconColor = ReturnSuccess,
                            countColor = ReturnSuccess
                        )

                        ReturnSummaryCard(
                            modifier = Modifier.weight(1f),
                            count = damagedCount,
                            label = "Rusak",
                            icon = ReturnGlyphType.WARNING,
                            iconBackground = ReturnDangerBg,
                            iconColor = ReturnDanger,
                            countColor = ReturnDanger
                        )

                        ReturnSummaryCard(
                            modifier = Modifier.weight(1f),
                            count = pendingCount,
                            label = "Belum",
                            icon = ReturnGlyphType.CLOCK,
                            iconBackground = ReturnWarningBg,
                            iconColor = ReturnWarning,
                            countColor = ReturnWarning
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    // SECTION TITLE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Pemeriksaan barang",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ReturnBrown
                        )

                        Spacer(Modifier.weight(1f))

                        Text(
                            text = "$totalItems barang",
                            fontSize = 12.sp,
                            color = ReturnSecondary
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // RETURN ITEM LIST
                    items.forEachIndexed { index, item ->

                        ReturnItemCard(
                            item = item,
                            onConditionSelected = { condition ->
                                updateCondition(item.id, condition)
                            },
                            onAddPhoto = {
                                onAddDamagePhoto(item.id)
                            }
                        )

                        if (index != items.lastIndex) {
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }

            // FIXED BOTTOM ACTION
            ReturnFooter(
                remainingCount = remainingCount,
                onFinish = {
                    if (remainingCount == 0) {
                        onFinishReturn(
                            items.map {
                                ReturnItemResult(
                                    itemId = it.id,
                                    condition = it.condition.name,
                                    note = it.note
                                )
                            }
                        )
                    }
                }
            )
        }
    }
}


// ============================================================
// HEADER
// ============================================================

@Composable
private fun ReturnHeader(
    onBack: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(
                    role = Role.Button,
                    onClick = onBack
                ),
            contentAlignment = Alignment.Center
        ) {

            ReturnGlyph(
                type = ReturnGlyphType.BACK,
                tint = ReturnBrown,
                modifier = Modifier.size(21.dp),
                strokeWidth = 2.dp
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = "Pengembalian barang",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ReturnBrown,
            maxLines = 1
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "#WA-26038",
            fontSize = 12.sp,
            color = ReturnSecondary,
            maxLines = 1
        )
    }
}


// ============================================================
// HERO ORDER CARD
// ============================================================

@Composable
private fun ReturnHeroCard(
    inspectedCount: Int,
    totalItems: Int,
    progress: Float
) {

    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ReturnHeroStart,
                        ReturnHeroEnd
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(140f, 260f)
                )
            )
    ) {

        // Decorative circular pattern
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            val center = Offset(
                x = size.width * 1.02f,
                y = size.height * 0.17f
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.035f),
                radius = size.width * 0.39f,
                center = center
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.035f),
                radius = size.width * 0.29f,
                center = center
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.035f),
                radius = size.width * 0.19f,
                center = center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                )
        ) {

            // TOP INFORMATION
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "PENGEMBALIAN · HARI INI",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC4A46A),
                    letterSpacing = 0.2.sp
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = "$inspectedCount/$totalItems diperiksa",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReturnSurface
                )
            }

            Spacer(Modifier.height(6.dp))

            // CUSTOMER NAME
            Text(
                text = "Dinda & Arif",
                fontSize = 25.sp,
                fontFamily = FontFamily.Serif,
                color = ReturnSurface,
                lineHeight = 29.sp,
                maxLines = 1
            )

            Spacer(Modifier.height(1.dp))

            // ORDER TYPE
            Text(
                text = "Sewa · paling lambat kembali 17.00 WIB",
                fontSize = 12.sp,
                color = Color(0xFFE5D9CC),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(13.dp))

            // PROGRESS BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(alpha = 0.20f)
                    )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF22A98B)
                                )
                            )
                        )
                )
            }

            Spacer(Modifier.height(10.dp))

            // EMPLOYEE INFORMATION
            Text(
                text = "Petugas Sari · periksa kondisi setiap barang",
                fontSize = 11.sp,
                color = Color(0xFFE5D9CC),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


// ============================================================
// SUMMARY CARD
// ============================================================

@Composable
private fun ReturnSummaryCard(
    modifier: Modifier = Modifier,
    count: Int,
    label: String,
    icon: ReturnGlyphType,
    iconBackground: Color,
    iconColor: Color,
    countColor: Color
) {

    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .height(80.dp)
            .clip(shape)
            .background(ReturnSurface)
            .border(
                width = 1.dp,
                color = ReturnBorder,
                shape = shape
            )
    ) {

        // ICON
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 11.dp)
                .size(34.dp)
                .background(
                    iconBackground,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            ReturnGlyph(
                type = icon,
                tint = iconColor,
                modifier = Modifier.size(17.dp)
            )
        }

        // COUNT BADGE
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 10.dp)
                .size(22.dp)
                .background(
                    countColor,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = count.toString(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // LABEL
        Text(
            text = label,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 12.dp,
                    bottom = 9.dp
                ),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ReturnBrown
        )
    }
}


// ============================================================
// RETURN ITEM CARD
// ============================================================

@Composable
private fun ReturnItemCard(
    item: ReturnItemUi,
    onConditionSelected: (ReturnCondition) -> Unit,
    onAddPhoto: () -> Unit
) {

    val shape = RoundedCornerShape(21.dp)

    val borderColor = when {
        item.condition == ReturnCondition.DAMAGED ->
            ReturnDangerBorder

        item.highlighted &&
                item.condition == ReturnCondition.GOOD ->
            ReturnSuccessBorder

        else -> ReturnBorder
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ReturnSurface)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .padding(15.dp)
    ) {

        // ITEM HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ITEM ICON
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ReturnItemIconBg),
                contentAlignment = Alignment.Center
            ) {

                ReturnGlyph(
                    type = if (
                        item.icon == ReturnItemIcon.CROWN
                    ) {
                        ReturnGlyphType.CROWN
                    } else {
                        ReturnGlyphType.SHIRT
                    },
                    tint = ReturnMuted,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 1.7.dp
                )
            }

            Spacer(Modifier.width(11.dp))

            // ITEM NAME AND SKU
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = item.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReturnBrown,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "${item.sku} · ${item.variant}",
                    fontSize = 11.sp,
                    color = ReturnSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(5.dp))

            // CONDITION BADGE
            ReturnStatusPill(
                condition = item.condition
            )
        }

        Spacer(Modifier.height(12.dp))

        // DIVIDER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ReturnBorder)
        )

        Spacer(Modifier.height(10.dp))

        // BOX INFORMATION
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Kembali ke box",
                fontSize = 11.sp,
                color = ReturnSecondary
            )

            Spacer(Modifier.width(8.dp))

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFFDFBF7))
                    .border(
                        1.dp,
                        ReturnBorder,
                        CircleShape
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                ReturnGlyph(
                    type = ReturnGlyphType.BOX,
                    tint = ReturnMuted,
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 1.5.dp
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    text = item.boxId,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReturnMuted
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // CONDITION ACTION BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {

            ConditionActionButton(
                modifier = Modifier.weight(1f),
                label = "Baik",
                icon = ReturnGlyphType.CHECK,
                selected = item.condition == ReturnCondition.GOOD,
                activeColor = ReturnSuccess,
                iconColor = ReturnSuccess,
                onClick = {
                    onConditionSelected(ReturnCondition.GOOD)
                }
            )

            ConditionActionButton(
                modifier = Modifier.weight(1f),
                label = "Rusak",
                icon = ReturnGlyphType.WARNING,
                selected = item.condition == ReturnCondition.DAMAGED,
                activeColor = ReturnDanger,
                iconColor = ReturnDanger,
                onClick = {
                    onConditionSelected(ReturnCondition.DAMAGED)
                }
            )

            ConditionActionButton(
                modifier = Modifier.weight(1f),
                label = "Hilang",
                icon = ReturnGlyphType.MISSING,
                selected = item.condition == ReturnCondition.MISSING,
                activeColor = ReturnPurple,
                iconColor = ReturnPurple,
                onClick = {
                    onConditionSelected(ReturnCondition.MISSING)
                }
            )
        }

        // DAMAGE NOTE
        if (item.condition == ReturnCondition.DAMAGED) {

            Spacer(Modifier.height(10.dp))

            ReturnDamageNote(
                note = item.note
                    ?: "Tambahkan catatan kerusakan.",
                onAddPhoto = onAddPhoto
            )
        }
    }
}


// ============================================================
// STATUS PILL
// ============================================================

@Composable
private fun ReturnStatusPill(
    condition: ReturnCondition
) {

    val backgroundColor: Color
    val textColor: Color
    val label: String
    val icon: ReturnGlyphType

    when (condition) {

        ReturnCondition.GOOD -> {
            backgroundColor = ReturnSuccessBg
            textColor = ReturnSuccessDark
            label = "Baik"
            icon = ReturnGlyphType.CHECK
        }

        ReturnCondition.DAMAGED -> {
            backgroundColor = ReturnDangerBg
            textColor = ReturnDangerDark
            label = "Ada kerusakan"
            icon = ReturnGlyphType.WARNING
        }

        ReturnCondition.MISSING -> {
            backgroundColor = ReturnPurpleBg
            textColor = ReturnPurple
            label = "Hilang"
            icon = ReturnGlyphType.MISSING
        }

        ReturnCondition.PENDING -> {
            backgroundColor = ReturnWarningBg
            textColor = ReturnWarningDark
            label = "Belum diperiksa"
            icon = ReturnGlyphType.CLOCK
        }
    }

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(
                horizontal = 9.dp,
                vertical = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ReturnGlyph(
            type = icon,
            tint = textColor,
            modifier = Modifier.size(12.dp),
            strokeWidth = 1.7.dp
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1
        )
    }
}


// ============================================================
// CONDITION BUTTON
// ============================================================

@Composable
private fun ConditionActionButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: ReturnGlyphType,
    selected: Boolean,
    activeColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {

    val shape = RoundedCornerShape(14.dp)

    val backgroundColor = if (selected) {
        activeColor
    } else {
        ReturnSurface
    }

    val contentColor = if (selected) {
        Color.White
    } else {
        iconColor
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (selected) {
                    activeColor
                } else {
                    ReturnBorder
                },
                shape = shape
            )
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            ReturnGlyph(
                type = icon,
                tint = contentColor,
                modifier = Modifier.size(14.dp),
                strokeWidth = 1.8.dp
            )

            Spacer(Modifier.width(5.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) {
                    Color.White
                } else {
                    ReturnMuted
                },
                maxLines = 1
            )
        }
    }
}


// ============================================================
// DAMAGE NOTE
// ============================================================

@Composable
private fun ReturnDamageNote(
    note: String,
    onAddPhoto: () -> Unit
) {

    val shape = RoundedCornerShape(15.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ReturnNoteBg)
            .border(
                width = 1.dp,
                color = ReturnDangerBorder,
                shape = shape
            )
            .padding(
                horizontal = 11.dp,
                vertical = 7.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "CATATAN",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = ReturnDangerDark
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = note,
                fontSize = 11.sp,
                color = ReturnMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // CAMERA BUTTON
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ReturnSurface)
                .border(
                    1.dp,
                    ReturnDangerBorder,
                    CircleShape
                )
                .clickable(
                    role = Role.Button,
                    onClick = onAddPhoto
                ),
            contentAlignment = Alignment.Center
        ) {

            ReturnGlyph(
                type = ReturnGlyphType.CAMERA,
                tint = ReturnDanger,
                modifier = Modifier.size(19.dp),
                strokeWidth = 1.7.dp
            )
        }
    }
}


// ============================================================
// FIXED BOTTOM FOOTER
// ============================================================

@Composable
private fun ReturnFooter(
    remainingCount: Int,
    onFinish: () -> Unit
) {

    val enabled = remainingCount == 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ReturnSurface)
            .drawBehind {

                drawLine(
                    color = ReturnBorder,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
    ) {

        // REMAINING INFORMATION
        Text(
            text = if (remainingCount > 0) {
                "Periksa $remainingCount barang lagi untuk menyelesaikan"
            } else {
                "Semua barang sudah diperiksa"
            },
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            color = ReturnSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(10.dp))

        // FINISH BUTTON
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(
                    if (enabled) {
                        Color(0xFF4A3328)
                    } else {
                        ReturnDisabled
                    }
                )
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onFinish
                ),
            contentAlignment = Alignment.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                ReturnGlyph(
                    type = ReturnGlyphType.CHECK,
                    tint = Color.White,
                    modifier = Modifier.size(17.dp),
                    strokeWidth = 2.dp
                )

                Spacer(Modifier.width(9.dp))

                Text(
                    text = "Selesaikan pengembalian",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}


// ============================================================
// CUSTOM VECTOR ICONS
// Tidak membutuhkan drawable XML atau asset tambahan.
// ============================================================

private enum class ReturnGlyphType {
    BACK,
    CHECK,
    WARNING,
    CLOCK,
    BOX,
    SHIRT,
    CROWN,
    MISSING,
    CAMERA
}

@Composable
private fun ReturnGlyph(
    type: ReturnGlyphType,
    tint: Color,
    modifier: Modifier = Modifier.size(18.dp),
    strokeWidth: androidx.compose.ui.unit.Dp = 1.8.dp
) {

    Canvas(modifier = modifier) {

        val w = size.width
        val h = size.height
        val sw = strokeWidth.toPx()

        fun point(x: Float, y: Float) =
            Offset(w * x, h * y)

        val outline = Stroke(
            width = sw,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        when (type) {

            // BACK ARROW
            ReturnGlyphType.BACK -> {

                drawLine(
                    tint,
                    point(0.78f, 0.5f),
                    point(0.22f, 0.5f),
                    sw,
                    StrokeCap.Round
                )

                drawLine(
                    tint,
                    point(0.22f, 0.5f),
                    point(0.52f, 0.2f),
                    sw,
                    StrokeCap.Round
                )

                drawLine(
                    tint,
                    point(0.22f, 0.5f),
                    point(0.52f, 0.8f),
                    sw,
                    StrokeCap.Round
                )
            }

            // CHECK
            ReturnGlyphType.CHECK -> {

                val path = Path().apply {
                    moveTo(w * 0.18f, h * 0.52f)
                    lineTo(w * 0.42f, h * 0.75f)
                    lineTo(w * 0.84f, h * 0.25f)
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = outline
                )
            }

            // WARNING TRIANGLE
            ReturnGlyphType.WARNING -> {

                val path = Path().apply {
                    moveTo(w * 0.5f, h * 0.10f)
                    lineTo(w * 0.93f, h * 0.86f)
                    lineTo(w * 0.07f, h * 0.86f)
                    close()
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = outline
                )

                drawLine(
                    tint,
                    point(0.5f, 0.34f),
                    point(0.5f, 0.58f),
                    sw,
                    StrokeCap.Round
                )

                drawCircle(
                    color = tint,
                    radius = sw * 0.75f,
                    center = point(0.5f, 0.72f)
                )
            }

            // CLOCK
            ReturnGlyphType.CLOCK -> {

                drawCircle(
                    color = tint,
                    radius = w * 0.39f,
                    center = point(0.5f, 0.5f),
                    style = outline
                )

                drawLine(
                    tint,
                    point(0.5f, 0.5f),
                    point(0.5f, 0.27f),
                    sw,
                    StrokeCap.Round
                )

                drawLine(
                    tint,
                    point(0.5f, 0.5f),
                    point(0.69f, 0.59f),
                    sw,
                    StrokeCap.Round
                )
            }

            // BOX
            ReturnGlyphType.BOX -> {

                val path = Path().apply {
                    moveTo(w * 0.5f, h * 0.10f)
                    lineTo(w * 0.88f, h * 0.30f)
                    lineTo(w * 0.88f, h * 0.72f)
                    lineTo(w * 0.5f, h * 0.92f)
                    lineTo(w * 0.12f, h * 0.72f)
                    lineTo(w * 0.12f, h * 0.30f)
                    close()
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = outline
                )

                drawLine(
                    tint,
                    point(0.12f, 0.30f),
                    point(0.5f, 0.51f),
                    sw
                )

                drawLine(
                    tint,
                    point(0.88f, 0.30f),
                    point(0.5f, 0.51f),
                    sw
                )

                drawLine(
                    tint,
                    point(0.5f, 0.51f),
                    point(0.5f, 0.92f),
                    sw
                )
            }

            // SHIRT
            ReturnGlyphType.SHIRT -> {

                val path = Path().apply {

                    moveTo(w * 0.36f, h * 0.15f)
                    lineTo(w * 0.23f, h * 0.21f)

                    lineTo(w * 0.08f, h * 0.43f)
                    lineTo(w * 0.24f, h * 0.58f)

                    lineTo(w * 0.34f, h * 0.47f)
                    lineTo(w * 0.34f, h * 0.90f)

                    lineTo(w * 0.66f, h * 0.90f)
                    lineTo(w * 0.66f, h * 0.47f)

                    lineTo(w * 0.76f, h * 0.58f)
                    lineTo(w * 0.92f, h * 0.43f)

                    lineTo(w * 0.77f, h * 0.21f)
                    lineTo(w * 0.64f, h * 0.15f)

                    quadraticTo(
                        w * 0.5f,
                        h * 0.35f,
                        w * 0.36f,
                        h * 0.15f
                    )

                    close()
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = outline
                )
            }

            // CROWN
            ReturnGlyphType.CROWN -> {

                val path = Path().apply {

                    moveTo(w * 0.13f, h * 0.30f)
                    lineTo(w * 0.31f, h * 0.49f)
                    lineTo(w * 0.50f, h * 0.14f)
                    lineTo(w * 0.69f, h * 0.49f)
                    lineTo(w * 0.87f, h * 0.30f)

                    lineTo(w * 0.81f, h * 0.76f)
                    lineTo(w * 0.19f, h * 0.76f)
                    close()
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = outline
                )

                drawLine(
                    tint,
                    point(0.19f, 0.88f),
                    point(0.81f, 0.88f),
                    sw,
                    StrokeCap.Round
                )
            }

            // MISSING / CLOSE
            ReturnGlyphType.MISSING -> {

                drawCircle(
                    color = tint,
                    radius = w * 0.38f,
                    center = point(0.5f, 0.5f),
                    style = outline
                )

                drawLine(
                    tint,
                    point(0.36f, 0.36f),
                    point(0.64f, 0.64f),
                    sw,
                    StrokeCap.Round
                )

                drawLine(
                    tint,
                    point(0.64f, 0.36f),
                    point(0.36f, 0.64f),
                    sw,
                    StrokeCap.Round
                )
            }

            // CAMERA
            ReturnGlyphType.CAMERA -> {

                drawRoundRect(
                    color = tint,
                    topLeft = point(0.10f, 0.29f),
                    size = Size(w * 0.80f, h * 0.57f),
                    cornerRadius = CornerRadius(
                        w * 0.10f,
                        h * 0.10f
                    ),
                    style = outline
                )

                val top = Path().apply {
                    moveTo(w * 0.31f, h * 0.29f)
                    lineTo(w * 0.39f, h * 0.16f)
                    lineTo(w * 0.62f, h * 0.16f)
                    lineTo(w * 0.70f, h * 0.29f)
                }

                drawPath(
                    path = top,
                    color = tint,
                    style = outline
                )

                drawCircle(
                    color = tint,
                    radius = w * 0.16f,
                    center = point(0.5f, 0.57f),
                    style = outline
                )
            }
        }
    }
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    name = "Return Management Employee",
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun ReturnManagementEmployeeScreenPreview() {

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF4A3328),
            onPrimary = Color.White,
            background = ReturnBackground,
            onBackground = ReturnBrown,
            surface = ReturnSurface,
            onSurface = ReturnBrown
        )
    ) {

        ReturnManagementEmployeeScreen(
            onBack = {},
            onItemStatusChanged = { _, _ -> },
            onAddDamagePhoto = {},
            onFinishReturn = {}
        )
    }
}
