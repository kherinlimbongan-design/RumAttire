package com.example.rumattire

import android.graphics.Color as AndroidColor
import android.view.Window
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.navigationBarsPadding

// ============================================================
// RUM ATTIRE - COLOR PALETTE
// ============================================================

private object RumPalette {
    val canvas = Color(0xFFF7F2E9)
    val paper = Color(0xFFFBF8F2)
    val white = Color(0xFFFFFFFF)
    val ink = Color(0xFF3B2A22)
    val darkBrown = Color(0xFF4A3328)
    val muted = Color(0xFF8B7B70)
    val mutedDark = Color(0xFF6B5A4E)
    val border = Color(0xFFE6DACA)
    val gold = Color(0xFFC9A96E)
    val teal = Color(0xFF3CC4B3)
    val tealDark = Color(0xFF1E9E8F)
    val orange = Color(0xFFE8704A)
    val purple = Color(0xFF7B61D9)
    val blue = Color(0xFF2F6FDE)
    val bluePale = Color(0xFFE4ECFB)
    val tealPale = Color(0xFFDFF3EF)
    val purplePale = Color(0xFFECE7FB)
    val orangePale = Color(0xFFFDE8DE)
    val returnPale = Color(0xFFFDF4DE)
    val returnBorder = Color(0xFFEBCB82)
    val returnInk = Color(0xFF5A3E00)
    val preview = Color(0xFFF7F2E9)
    val progressTrack = Color(0xFFEADFCF)
}

// ============================================================
// MATERIAL 3 THEME
// ============================================================

private val RumLightColorScheme = lightColorScheme(
    primary = RumPalette.darkBrown,
    onPrimary = RumPalette.white,
    secondary = RumPalette.tealDark,
    background = RumPalette.canvas,
    onBackground = RumPalette.ink,
    surface = RumPalette.white,
    onSurface = RumPalette.ink
)

@Composable
fun RumAttireEmployeeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RumLightColorScheme,
        content = content
    )
}

// ============================================================
// EMPLOYEE DATA STATE
// ============================================================

data class EmployeeDashboardUiState(
    val employeeName: String = "Sari",
    val dateLabel: String = "EMPLOYEE · RABU, 4 NOVEMBER",
    val preparationCount: Int = 2,
    val returnCount: Int = 1,
    val currentOrderCode: String = "#WA-26041",
    val customerName: String = "Nadia & Rizky",
    val pickupInfo: String = "Ambil 4 Nov, 16.00",
    val orderStatus: String = "Sedang diperiksa",
    val progress: Float = 0.5f,
    val checkedItemsLabel: String =
        "2 dari 4 barang diperiksa · Sari",
    val nextItemName: String = "Veil Tulle Panjang",
    val nextItemCode: String = "VL-003",
    val nextItemSize: String = "All size",
    val nextItemBox: String = "BX-012",
    val returnTitle: String =
        "Pengembalian Dinda hari ini",
    val returnSubtitle: String =
        "#WA-26038 · paling lambat 17.00 WIB",
    val hasUnreadNotification: Boolean = true
)

enum class EmployeeTab {
    TUGAS,
    PESANAN,
    SCAN,
    NOTIFIKASI,
    LAINNYA
}

// ============================================================
// SYSTEM BAR CONFIGURATION
// ============================================================

fun setRumAttireSystemBars(window: Window) {

    window.statusBarColor =
        AndroidColor.rgb(247, 242, 233)

    window.navigationBarColor =
        AndroidColor.rgb(251, 248, 242)

    WindowCompat.setDecorFitsSystemWindows(
        window,
        true
    )

    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).apply {
        isAppearanceLightStatusBars = true
        isAppearanceLightNavigationBars = true
    }
}

// ============================================================
// EMPLOYEE DASHBOARD SCREEN
// ============================================================

@Composable
fun EmployeeDashboardScreen(
    modifier: Modifier = Modifier,
    state: EmployeeDashboardUiState =
        EmployeeDashboardUiState(),
    onNotificationClick: () -> Unit = {},
    onPreparationClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onReturnsClick: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    onContinueChecklist: () -> Unit = {},
    onReturnClick: () -> Unit = {},
    onTabSelected: (EmployeeTab) -> Unit = {}
) {

    var selectedTab by remember {
        mutableStateOf(EmployeeTab.TUGAS)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RumPalette.canvas)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(40.dp))

            EmployeeHeader(
                hasUnreadNotification =
                    state.hasUnreadNotification,
                onNotificationClick =
                    onNotificationClick
            )

            Spacer(Modifier.height(16.dp))

            GreetingCard(
                employeeName = state.employeeName,
                dateLabel = state.dateLabel,
                preparationCount =
                    state.preparationCount,
                onClick = onPreparationClick
            )

            Spacer(Modifier.height(16.dp))

            QuickActions(
                preparationCount =
                    state.preparationCount,
                returnCount = state.returnCount,
                onPreparationClick =
                    onPreparationClick,
                onScanClick = onScanClick,
                onReturnsClick = onReturnsClick
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Sedang dikerjakan",
                    color = RumPalette.ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = state.currentOrderCode,
                    color = RumPalette.muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            CurrentOrderCard(
                state = state,
                onClick = onOrderClick
            )

            Spacer(Modifier.height(16.dp))

            PrimaryButton(
                text = "Lanjutkan checklist",
                onClick = onContinueChecklist
            )

            Spacer(Modifier.height(16.dp))

            ReturnReminderCard(
                title = state.returnTitle,
                subtitle = state.returnSubtitle,
                onClick = onReturnClick
            )

            Spacer(Modifier.height(16.dp))
        }

        EmployeeBottomNavigation(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
                onTabSelected(it)
            }
        )
    }
}

// ============================================================
// EMPLOYEE HEADER
// ============================================================

@Composable
private fun EmployeeHeader(
    hasUnreadNotification: Boolean,
    onNotificationClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = "Rum Attire · Employee",
            color = RumPalette.ink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = onNotificationClick
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Outlined.NotificationsNone,
                contentDescription = "Notifikasi",
                tint = RumPalette.ink,
                modifier = Modifier.size(27.dp)
            )

            if (hasUnreadNotification) {

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(
                            x = (-1).dp,
                            y = 1.dp
                        )
                        .size(9.dp)
                        .background(
                            RumPalette.orange,
                            CircleShape
                        )
                        .border(
                            1.5.dp,
                            RumPalette.canvas,
                            CircleShape
                        )
                )
            }
        }
    }
}

// ============================================================
// GREETING CARD
// ============================================================


@Composable
private fun GreetingCard(
    employeeName: String,
    dateLabel: String,
    preparationCount: Int,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 174.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF5C4336),
                        Color(0xFF3A2820)
                    )
                )
            )
            .border(
                1.dp,
                Color(0xFF8A6B4B),
                shape
            )
    ) {

        Canvas(Modifier.fillMaxSize()) {

            val center = Offset(
                size.width - 18.dp.toPx(),
                4.dp.toPx()
            )

            drawCircle(
                color = RumPalette.gold.copy(alpha = 0.13f),
                radius = 92.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            drawCircle(
                color = RumPalette.gold.copy(alpha = 0.10f),
                radius = 120.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = dateLabel,
                    color = RumPalette.gold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "R / A",
                    color = RumPalette.gold,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(13.dp))

            // NAMA KARYAWAN: BOLEH MENJADI DUA BARIS
            Text(
                text = "Selamat bekerja, $employeeName",
                color = Color(0xFFFFF8EE),
                fontSize = 25.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(13.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        RumPalette.gold.copy(alpha = 0.38f)
                    )
            )

            Spacer(Modifier.height(11.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.10f))
                    .clickable(
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null,
                        onClick = onClick
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 7.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(
                            RumPalette.teal,
                            CircleShape
                        )
                )

                Spacer(Modifier.width(7.dp))

                Text(
                    text = "$preparationCount pesanan perlu disiapkan hari ini",
                    color = Color(0xFFEADFCF),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(5.dp))

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = RumPalette.gold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}


// ============================================================
// QUICK ACTIONS
// ============================================================

@Composable
private fun QuickActions(
    preparationCount: Int,
    returnCount: Int,
    onPreparationClick: () -> Unit,
    onScanClick: () -> Unit,
    onReturnsClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        QuickActionCard(
            modifier = Modifier.weight(1f),
            label = "Persiapan",
            icon = Icons.Outlined.CheckBox,
            iconTint = RumPalette.tealDark,
            iconBackground = RumPalette.tealPale,
            badge = preparationCount.takeIf {
                it > 0
            },
            onClick = onPreparationClick
        )

        QuickActionCard(
            modifier = Modifier.weight(1f),
            label = "Scan QR",
            icon = Icons.Outlined.QrCodeScanner,
            iconTint = RumPalette.purple,
            iconBackground = RumPalette.purplePale,
            badge = null,
            onClick = onScanClick
        )

        QuickActionCard(
            modifier = Modifier.weight(1f),
            label = "Kembali",
            icon = Icons.Outlined.Inventory2,
            iconTint = RumPalette.orange,
            iconBackground = RumPalette.orangePale,
            badge = returnCount.takeIf {
                it > 0
            },
            onClick = onReturnsClick
        )
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    badge: Int?,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(90.dp) // Tinggi diperbesar dari 78.dp
            .clip(RoundedCornerShape(15.dp))
            .background(RumPalette.white)
            .border(
                1.dp,
                RumPalette.border,
                RoundedCornerShape(15.dp)
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            if (badge != null) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            if (iconTint == RumPalette.orange)
                                RumPalette.orange
                            else
                                RumPalette.tealDark,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge.toString(),
                        color = RumPalette.white,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(9.dp))

        Text(
            text = label,
            color = RumPalette.ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            maxLines = 1,
            softWrap = false
        )
    }
}

// ============================================================
// CURRENT ORDER CARD
// ============================================================

@Composable
private fun CurrentOrderCard(
    state: EmployeeDashboardUiState,
    onClick: () -> Unit
) {

    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(RumPalette.white)
            .border(
                1.5.dp,
                RumPalette.ink,
                shape
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            )
            .padding(
                horizontal = 16.dp,
                vertical = 15.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3EBDD)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined.Assignment,
                    contentDescription = null,
                    tint = RumPalette.darkBrown,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = state.customerName,
                    color = RumPalette.ink,
                    fontSize = 15.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${state.currentOrderCode} · ${state.pickupInfo}",
                    color = RumPalette.muted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(6.dp))

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(RumPalette.bluePale)
                    .padding(
                        horizontal = 8.dp,
                        vertical = 5.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = RumPalette.blue,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = state.orderStatus,
                    color = RumPalette.blue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 1
                )
            }
        }

        Spacer(Modifier.height(17.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(RumPalette.border)
        )

        Spacer(Modifier.height(11.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = "Kemajuan pesanan",
                color = RumPalette.muted,
                fontSize = 13.sp,
                fontFamily = FontFamily.SansSerif
            )

            Text(
                text = "${(state.progress.coerceIn(0f, 1f) * 100).toInt()}%",
                color = RumPalette.ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(RumPalette.progressTrack)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        state.progress.coerceIn(0f, 1f)
                    )
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                RumPalette.blue,
                                RumPalette.tealDark
                            )
                        )
                    )
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = RumPalette.muted,
                modifier = Modifier.size(19.dp)
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = state.checkedItemsLabel,
                color = RumPalette.muted,
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(11.dp))

        NextItemPreview(
            itemName = state.nextItemName,
            itemCode = state.nextItemCode,
            itemSize = state.nextItemSize,
            boxCode = state.nextItemBox
        )
    }
}

// ============================================================
// NEXT ITEM PREVIEW
// ============================================================

@Composable
private fun NextItemPreview(
    itemName: String,
    itemCode: String,
    itemSize: String,
    boxCode: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(RumPalette.preview)
            .padding(horizontal = 8.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(RumPalette.white),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Outlined.Checkroom,
                contentDescription = null,
                tint = RumPalette.darkBrown,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = itemName,
                color = RumPalette.ink,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Berikutnya · $itemCode · $itemSize",
                color = RumPalette.muted,
                fontSize = 11.sp,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(5.dp))

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(RumPalette.white)
                .border(
                    1.dp,
                    RumPalette.border,
                    CircleShape
                )
                .padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Outlined.Inventory2,
                contentDescription = null,
                tint = RumPalette.darkBrown,
                modifier = Modifier.size(15.dp)
            )

            Spacer(Modifier.width(5.dp))

            Text(
                text = boxCode,
                color = RumPalette.darkBrown,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============================================================
// PRIMARY BUTTON
// ============================================================

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(RumPalette.darkBrown)
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = RumPalette.white,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
    }
}

// ============================================================
// RETURN REMINDER CARD
// ============================================================

@Composable
private fun ReturnReminderCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(RumPalette.returnPale)
            .border(
                1.dp,
                RumPalette.returnBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 15.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    Color(0xFFFBE5AE),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = Color(0xFF9A6A00),
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = RumPalette.returnInk,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                color = Color(0xFF8A641D),
                fontSize = 11.sp,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector =
                Icons.Outlined.ChevronRight,
            contentDescription = "Lihat pengembalian",
            tint = Color(0xFF9A6A00),
            modifier = Modifier.size(22.dp)
        )
    }
}

// ============================================================
// EMPLOYEE BOTTOM NAVIGATION
// ============================================================

@Composable
private fun EmployeeBottomNavigation(
    selectedTab: EmployeeTab,
    onTabSelected: (EmployeeTab) -> Unit
) {

    val items = listOf(
        Triple(
            EmployeeTab.TUGAS,
            "Tugas",
            Icons.Outlined.CheckBox
        ),
        Triple(
            EmployeeTab.PESANAN,
            "Pesanan",
            Icons.Outlined.Assignment
        ),
        Triple(
            EmployeeTab.SCAN,
            "Scan",
            Icons.Outlined.QrCodeScanner
        ),
        Triple(
            EmployeeTab.NOTIFIKASI,
            "Notifikasi",
            Icons.Outlined.NotificationsNone
        ),
        Triple(
            EmployeeTab.LAINNYA,
            "Lainnya",
            Icons.Outlined.Menu
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RumPalette.paper)
            .navigationBarsPadding()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(RumPalette.border)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            items.forEach { (tab, label, icon) ->

                val selected = selectedTab == tab

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null,
                            onClick = {
                                onTabSelected(tab)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier = Modifier
                            .width(64.dp)
                            .height(52.dp)
                            .clip(
                                RoundedCornerShape(17.dp)
                            )
                            .background(
                                if (selected) {
                                    Color(0xFFEADFCF)
                                } else {
                                    Color.Transparent
                                }
                            ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        Box {

                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (selected) {
                                    RumPalette.darkBrown
                                } else {
                                    RumPalette.mutedDark
                                },
                                modifier = Modifier.size(25.dp)
                            )

                            if (
                                tab == EmployeeTab.NOTIFIKASI
                            ) {

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(
                                            x = 2.dp,
                                            y = (-1).dp
                                        )
                                        .size(8.dp)
                                        .background(
                                            RumPalette.orange,
                                            CircleShape
                                        )
                                )
                            }
                        }

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = label,
                            color = if (selected) {
                                RumPalette.darkBrown
                            } else {
                                RumPalette.mutedDark
                            },
                            fontSize = 10.sp,
                            fontWeight = if (selected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            fontFamily = FontFamily.SansSerif,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// ANDROID STUDIO PREVIEW
// ============================================================

@Preview(
    name = "Rum Attire Employee Dashboard",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun EmployeeDashboardPreview() {

    RumAttireEmployeeTheme {
        EmployeeDashboardScreen()
    }
}