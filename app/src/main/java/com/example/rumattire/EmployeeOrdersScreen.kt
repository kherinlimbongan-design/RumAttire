package com.example.rumattire

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================
// RUM ATTIRE - PESANAN SCREEN
// Design reference: SVG 393 x 852 px
// Jetpack Compose + Material 3
// ============================================================

// ------------------------------------------------------------
// COLOR PALETTE
// ------------------------------------------------------------

private object OrdersColors {

    val background = Color(0xFFF7F2E9)
    val surface = Color(0xFFFBF8F2)
    val white = Color(0xFFFFFFFF)

    val textPrimary = Color(0xFF3B2A22)
    val textSecondary = Color(0xFF8B7B70)
    val textMuted = Color(0xFF6B5A4E)

    val border = Color(0xFFE6DACA)
    val divider = Color(0xFFEADFCF)

    val brown = Color(0xFF4A3328)

    val blue = Color(0xFF2F6FDE)
    val blueDark = Color(0xFF1E9E8F)
    val bluePale = Color(0xFFE4ECFB)

    val teal = Color(0xFF1E9E8F)
    val tealPale = Color(0xFFDFF3EF)

    val orange = Color(0xFFE8704A)
    val orangePale = Color(0xFFFDE8DE)

    val purple = Color(0xFF7B61D9)
}

// ------------------------------------------------------------
// ORDER FILTER
// ------------------------------------------------------------

enum class OrderFilter(
    val label: String
) {
    ALL("Semua"),
    NEW("Baru"),
    CHECKED("Diperiksa"),
    RECHECK("Cek ulang")
}

// ------------------------------------------------------------
// ORDER DATA
// ------------------------------------------------------------

data class EmployeeOrderItem(
    val id: String,
    val customer: String,
    val orderCode: String,
    val category: String,
    val pickupDate: String,
    val status: OrderFilter,
    val itemCount: Int,
    val checkedCount: Int,
    val progress: Float,
    val warning: String? = null
)

// ------------------------------------------------------------
// SAMPLE ORDERS - SESUAI SVG
// ------------------------------------------------------------

private val employeeSampleOrders = listOf(

    EmployeeOrderItem(
        id = "1",
        customer = "Nadia & Rizky",
        orderCode = "#WA-26041",
        category = "Sewa",
        pickupDate = "Ambil 4 Nov",
        status = OrderFilter.CHECKED,
        itemCount = 4,
        checkedCount = 2,
        progress = 0.50f
    ),

    EmployeeOrderItem(
        id = "2",
        customer = "Dinda Pratama",
        orderCode = "#WA-26044",
        category = "Sewa",
        pickupDate = "Ambil 6 Nov",
        status = OrderFilter.RECHECK,
        itemCount = 3,
        checkedCount = 2,
        progress = 0.667f,
        warning = "Owner mengubah 1 barang, cek ulang"
    ),

    EmployeeOrderItem(
        id = "3",
        customer = "Salsa Amelia",
        orderCode = "#WA-26047",
        category = "Aksesori",
        pickupDate = "Ambil 8 Nov",
        status = OrderFilter.NEW,
        itemCount = 3,
        checkedCount = 0,
        progress = 0f
    )
)

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun EmployeeOrdersScreen(
    modifier: Modifier = Modifier,
    orders: List<EmployeeOrderItem> = employeeSampleOrders,
    onNotificationClick: () -> Unit = {},
    onOrderClick: (EmployeeOrderItem) -> Unit = {},
    onTabClick: (String) -> Unit = {}
) {

    var selectedFilter by remember {
        mutableStateOf(OrderFilter.ALL)
    }

    val filteredOrders = remember(
        selectedFilter,
        orders
    ) {
        when (selectedFilter) {
            OrderFilter.ALL -> orders
            else -> orders.filter {
                it.status == selectedFilter
            }
        }
    }

    val newCount = orders.count {
        it.status == OrderFilter.NEW
    }

    val checkedCount = orders.count {
        it.status == OrderFilter.CHECKED
    }

    val recheckCount = orders.count {
        it.status == OrderFilter.RECHECK
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrdersColors.background)
    ) {

        // CONTENT

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
        ) {

            // HEADER

            OrdersHeader(
                orderCount = orders.size,
                onNotificationClick =
                    onNotificationClick
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // FILTERS

            OrderFilterBar(
                selectedFilter = selectedFilter,
                newCount = newCount,
                checkedCount = checkedCount,
                recheckCount = recheckCount,
                onFilterSelected = {
                    selectedFilter = it
                }
            )

            Spacer(
                modifier = Modifier.height(17.dp)
            )

            // ORDER LIST

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(11.dp)
            ) {

                filteredOrders.forEach { order ->

                    OrderCard(
                        order = order,
                        onClick = {
                            onOrderClick(order)
                        }
                    )
                }
            }

            // EMPTY STATE

            if (filteredOrders.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 45.dp
                        ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.Assignment,
                        contentDescription = null,
                        tint = OrdersColors.textSecondary,
                        modifier = Modifier.size(34.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Tidak ada pesanan",
                        color = OrdersColors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // FOOTER INFORMATION

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Ketuk pesanan untuk membuka checklist persiapan",
                color = OrdersColors.textSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }

        // BOTTOM NAVIGATION

        OrdersBottomNavigation(
            selectedItem = "Pesanan",
            onTabClick = onTabClick
        )
    }
}

// ------------------------------------------------------------
// HEADER
// ------------------------------------------------------------

@Composable
private fun OrdersHeader(
    orderCount: Int,
    onNotificationClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "Pesanan",
                    color = OrdersColors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.4).sp
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Dari owner · $orderCount pesanan perlu ditangani",
                    color = OrdersColors.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clickable(
                        interactionSource =
                            remember {
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
                    tint = OrdersColors.textPrimary,
                    modifier = Modifier.size(25.dp)
                )

                // UNREAD INDICATOR

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(
                            x = (-1).dp,
                            y = 2.dp
                        )
                        .size(8.dp)
                        .background(
                            OrdersColors.orange,
                            CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = OrdersColors.background,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

// ------------------------------------------------------------
// FILTER BAR
// ------------------------------------------------------------

@Composable
private fun OrderFilterBar(
    selectedFilter: OrderFilter,
    newCount: Int,
    checkedCount: Int,
    recheckCount: Int,
    onFilterSelected: (OrderFilter) -> Unit
) {
    val totalCount = newCount + checkedCount + recheckCount

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        FilterChipItem(
            label = "Semua",
            count = totalCount,
            selected = selectedFilter == OrderFilter.ALL,
            badgeColor = OrdersColors.brown,
            onClick = {
                onFilterSelected(OrderFilter.ALL)
            }
        )

        FilterChipItem(
            label = "Baru",
            count = newCount,
            selected = selectedFilter == OrderFilter.NEW,
            badgeColor = OrdersColors.teal,
            onClick = {
                onFilterSelected(OrderFilter.NEW)
            }
        )

        FilterChipItem(
            label = "Diperiksa",
            count = checkedCount,
            selected = selectedFilter == OrderFilter.CHECKED,
            badgeColor = OrdersColors.blue,
            onClick = {
                onFilterSelected(OrderFilter.CHECKED)
            }
        )

        FilterChipItem(
            label = "Cek ulang",
            count = recheckCount,
            selected = selectedFilter == OrderFilter.RECHECK,
            badgeColor = OrdersColors.orange,
            onClick = {
                onFilterSelected(OrderFilter.RECHECK)
            }
        )
    }
}

// ------------------------------------------------------------
// FILTER CHIP
// ------------------------------------------------------------

@Composable
private fun FilterChipItem(
    label: String,
    count: Int,
    selected: Boolean,
    badgeColor: Color,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) {
        OrdersColors.brown
    } else {
        OrdersColors.white
    }

    val textColor = if (selected) {
        OrdersColors.white
    } else {
        OrdersColors.textPrimary
    }

    val borderColor = if (selected) {
        OrdersColors.brown
    } else {
        OrdersColors.border
    }

    val badgeBackground = if (selected) {
        OrdersColors.white
    } else {
        badgeColor
    }

    val badgeTextColor = if (selected) {
        OrdersColors.brown
    } else {
        OrdersColors.white
    }

    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 11.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false
        )

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    color = badgeBackground,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = count.toString(),
                color = badgeTextColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 10.sp
            )
        }
    }
}

// ------------------------------------------------------------
// ORDER CARD
// ------------------------------------------------------------

@Composable
private fun OrderCard(
    order: EmployeeOrderItem,
    onClick: () -> Unit
) {
    val isChecked = order.status == OrderFilter.CHECKED
    val isRecheck = order.status == OrderFilter.RECHECK
    val isNew = order.status == OrderFilter.NEW

    val borderColor = if (isChecked) {
        OrdersColors.brown
    } else {
        OrdersColors.border
    }

    val borderWidth = if (isChecked) {
        1.5.dp
    } else {
        1.dp
    }

    val cardShape = RoundedCornerShape(16.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(OrdersColors.white)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = cardShape
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            )
            .padding(
                horizontal = 15.dp,
                vertical = 17.dp
            )
    ) {

        // TOP SECTION

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ORDER ICON

            OrderIcon(
                isNew = isNew,
                isRecheck = isRecheck
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            // CUSTOMER INFORMATION

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = order.customer,
                    color = OrdersColors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "${order.orderCode} · ${order.category} · ${order.pickupDate}",
                    color = OrdersColors.textSecondary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            // STATUS

            OrderStatusBadge(
                status = order.status
            )
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        // DIVIDER

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(OrdersColors.divider)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ITEM COUNT

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = if (isRecheck) {
                    "${order.itemCount} barang · 1 berubah"
                } else {
                    "${order.itemCount} barang"
                },
                color = OrdersColors.textSecondary,
                fontSize = 12.sp
            )

            Text(
                text = if (isNew) {
                    "Belum dimulai"
                } else {
                    "${order.checkedCount}/${order.itemCount}"
                },
                color = OrdersColors.textPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(9.dp)
        )

        // PROGRESS BAR

        OrderProgressBar(
            progress = order.progress
        )

        // WARNING BOX

        if (isRecheck && order.warning != null) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            RecheckWarning(
                message = order.warning
            )
        }
    }
}

// ------------------------------------------------------------
// ORDER ICON
// ------------------------------------------------------------

@Composable
private fun OrderIcon(
    isNew: Boolean,
    isRecheck: Boolean
) {

    val iconBackground = when {
        isNew -> OrdersColors.tealPale
        isRecheck -> OrdersColors.orangePale
        else -> OrdersColors.bluePale
    }

    val iconColor = when {
        isNew -> OrdersColors.teal
        isRecheck -> OrdersColors.orange
        else -> OrdersColors.blue
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(iconBackground),
        contentAlignment = Alignment.Center
    ) {

        // Clipboard outline

        Icon(
            imageVector =
                Icons.Outlined.Assignment,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ------------------------------------------------------------
// STATUS BADGE
// ------------------------------------------------------------

@Composable
private fun OrderStatusBadge(
    status: OrderFilter
) {

    val backgroundColor: Color
    val foregroundColor: Color
    val label: String

    val statusIcon = when (status) {

        OrderFilter.CHECKED -> {
            backgroundColor = OrdersColors.bluePale
            foregroundColor = OrdersColors.blue
            label = "Sedang diperiksa"
            Icons.Outlined.AccessTime
        }

        OrderFilter.RECHECK -> {
            backgroundColor = OrdersColors.orangePale
            foregroundColor = OrdersColors.orange
            label = "Perlu dicek ulang"
            Icons.Outlined.Refresh
        }

        OrderFilter.NEW -> {
            backgroundColor = OrdersColors.tealPale
            foregroundColor = OrdersColors.teal
            label = "Baru"
            Icons.Outlined.Add
        }

        OrderFilter.ALL -> {
            backgroundColor = OrdersColors.bluePale
            foregroundColor = OrdersColors.blue
            label = "Semua"
            Icons.Outlined.AccessTime
        }
    }

    Row(
        modifier = Modifier
            .height(25.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(
                horizontal = 8.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.Center
    ) {

        Icon(
            imageVector = statusIcon,
            contentDescription = null,
            tint = foregroundColor,
            modifier = Modifier.size(13.dp)
        )

        Spacer(
            modifier = Modifier.width(4.dp)
        )

        Text(
            text = label,
            color = foregroundColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

// ------------------------------------------------------------
// PROGRESS BAR
// ------------------------------------------------------------

@Composable
private fun OrderProgressBar(
    progress: Float
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(CircleShape)
            .background(OrdersColors.divider)
    ) {

        if (progress > 0f) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        progress.coerceIn(0f, 1f)
                    )
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                OrdersColors.blue,
                                OrdersColors.blueDark
                            )
                        )
                    )
            )
        }
    }
}

// ------------------------------------------------------------
// RECHECK WARNING
// ------------------------------------------------------------

@Composable
private fun RecheckWarning(
    message: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(OrdersColors.orangePale)
            .padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = OrdersColors.orange,
            modifier = Modifier.size(19.dp)
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = message,
            color = Color(0xFFB84E2E),
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 15.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

// ------------------------------------------------------------
// BOTTOM NAVIGATION
// ------------------------------------------------------------

@Composable
private fun OrdersBottomNavigation(
    selectedItem: String,
    onTabClick: (String) -> Unit
) {

    val navigationItems = listOf(
        Triple(
            "Tugas",
            Icons.Outlined.CheckBox,
            false
        ),
        Triple(
            "Pesanan",
            Icons.Outlined.Assignment,
            false
        ),
        Triple(
            "Scan",
            Icons.Outlined.QrCodeScanner,
            false
        ),
        Triple(
            "Notifikasi",
            Icons.Outlined.NotificationsNone,
            true
        ),
        Triple(
            "Lainnya",
            Icons.Outlined.Menu,
            false
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OrdersColors.surface)
            .navigationBarsPadding()
    ) {

        // TOP DIVIDER

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(OrdersColors.divider)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .padding(
                    horizontal = 5.dp,
                    vertical = 5.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            navigationItems.forEach {
                    (label, icon, hasNotification) ->

                val selected = label == selectedItem

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource =
                                remember {
                                    MutableInteractionSource()
                                },
                            indication = null,
                            onClick = {
                                onTabClick(label)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier = Modifier
                            .width(64.dp)
                            .height(48.dp)
                            .clip(
                                RoundedCornerShape(15.dp)
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
                                    OrdersColors.brown
                                } else {
                                    OrdersColors.textMuted
                                },
                                modifier = Modifier.size(22.dp)
                            )

                            if (hasNotification) {

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(
                                            x = 3.dp,
                                            y = (-2).dp
                                        )
                                        .size(7.dp)
                                        .background(
                                            OrdersColors.orange,
                                            CircleShape
                                        )
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = label,
                            color = if (selected) {
                                OrdersColors.brown
                            } else {
                                OrdersColors.textMuted
                            },
                            fontSize = 10.sp,
                            fontWeight = if (selected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // BOTTOM SYSTEM GESTURE INDICATOR

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(OrdersColors.textPrimary)
            )
        }
    }
}

// ------------------------------------------------------------
// PREVIEW
// ------------------------------------------------------------

@Preview(
    name = "Rum Attire - Pesanan",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun EmployeeOrdersPreview() {

    MaterialTheme {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = OrdersColors.background
        ) {

            EmployeeOrdersScreen()
        }
    }
}