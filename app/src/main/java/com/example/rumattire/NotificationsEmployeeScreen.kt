package com.example.rumattire

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.navigationBarsPadding

// ============================================================
// COLOR PALETTE
// ============================================================

private val NotificationBackground = Color(0xFFF4EFE7)
private val NotificationSurface = Color(0xFFFFFFFF)
private val NotificationBottomSurface = Color(0xFFFDFBF7)

private val NotificationBrown = Color(0xFF2E211B)
private val NotificationDarkBrown = Color(0xFF3E2B23)
private val NotificationText = Color(0xFF6B5B4F)
private val NotificationSecondary = Color(0xFF8C7B6F)

private val NotificationBorder = Color(0xFFE4DACB)
private val NotificationCardBorder = Color(0xFFEBDDCB)
private val NotificationUnreadBorder = Color(0xFFCDB8A5)

private val NotificationOrange = Color(0xFFE8714A)
private val NotificationOrangeDark = Color(0xFFC4502A)

private val NotificationTealBg = Color(0xFFD8F0EA)
private val NotificationTeal = Color(0xFF17756A)
private val NotificationTealAction = Color(0xFF1E8F7E)

private val NotificationRedBg = Color(0xFFFCE5DB)
private val NotificationRed = Color(0xFFC4502A)

private val NotificationGoldBg = Color(0xFFFBF1D3)
private val NotificationGold = Color(0xFF8A6410)

private val NotificationBlueBg = Color(0xFFE3ECFC)
private val NotificationBlue = Color(0xFF2557B5)

private val NotificationDivider = Color(0xFFE9E2D8)
private val NotificationSelected = Color(0xFFEBDDCB)


// ============================================================
// DATA MODELS
// ============================================================

enum class EmployeeNotificationCategory {
    ORDER,
    RETURN,
    STATUS
}

enum class EmployeeNotificationType {
    NEW_ORDER,
    ORDER_EDITED,
    RETURN_REMINDER,
    ORDER_CONFIRMED
}

enum class EmployeeNotificationGroup {
    TODAY,
    YESTERDAY
}

enum class EmployeeNotificationAction {
    OPEN_CHECKLIST,
    RECHECK_ITEMS,
    START_RETURN_INSPECTION,
    VIEW_ORDER
}

enum class EmployeeBottomDestination {
    TUGAS,
    PESANAN,
    SCAN,
    NOTIFIKASI,
    LAINNYA
}

data class EmployeeNotification(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val category: EmployeeNotificationCategory,
    val type: EmployeeNotificationType,
    val group: EmployeeNotificationGroup,
    val actionLabel: String,
    val action: EmployeeNotificationAction,
    val isUnread: Boolean
)

private enum class NotificationFilter {
    ALL,
    UNREAD,
    ORDER,
    RETURN,
    STATUS
}


// ============================================================
// SAMPLE NOTIFICATION DATA
// ============================================================

private fun sampleNotifications() = listOf(

    EmployeeNotification(
        id = "NOTIF-001",
        title = "Pesanan baru dari owner",
        description =
            "Salsa Amelia · #WA-26047 · Aksesori. " +
                    "3 barang perlu disiapkan, ambil 8 Nov.",
        time = "09.30",
        category = EmployeeNotificationCategory.ORDER,
        type = EmployeeNotificationType.NEW_ORDER,
        group = EmployeeNotificationGroup.TODAY,
        actionLabel = "Buka checklist",
        action = EmployeeNotificationAction.OPEN_CHECKLIST,
        isUnread = true
    ),

    EmployeeNotification(
        id = "NOTIF-002",
        title = "Pesanan diedit, cek ulang",
        description =
            "Dinda Pratama · #WA-26044. Owner " +
                    "mengubah 1 barang, validasi ulang.",
        time = "09.12",
        category = EmployeeNotificationCategory.ORDER,
        type = EmployeeNotificationType.ORDER_EDITED,
        group = EmployeeNotificationGroup.TODAY,
        actionLabel = "Cek ulang barang",
        action = EmployeeNotificationAction.RECHECK_ITEMS,
        isUnread = true
    ),

    EmployeeNotification(
        id = "NOTIF-003",
        title = "Pengingat pengembalian",
        description =
            "Dinda & Arif · #WA-26038 kembali hari ini, " +
                    "paling lambat 17.00 WIB. Periksa 4 barang.",
        time = "08.45",
        category = EmployeeNotificationCategory.RETURN,
        type = EmployeeNotificationType.RETURN_REMINDER,
        group = EmployeeNotificationGroup.TODAY,
        actionLabel = "Mulai pemeriksaan",
        action = EmployeeNotificationAction.START_RETURN_INSPECTION,
        isUnread = true
    ),

    EmployeeNotification(
        id = "NOTIF-004",
        title = "Pesanan dikonfirmasi",
        description =
            "Ayu & Fajar · #WA-26042. Owner sudah " +
                    "mengonfirmasi, barang siap diambil.",
        time = "16.40",
        category = EmployeeNotificationCategory.STATUS,
        type = EmployeeNotificationType.ORDER_CONFIRMED,
        group = EmployeeNotificationGroup.YESTERDAY,
        actionLabel = "Lihat pesanan",
        action = EmployeeNotificationAction.VIEW_ORDER,
        isUnread = false
    ),

    EmployeeNotification(
        id = "NOTIF-005",
        title = "Pesanan baru dari owner",
        description =
            "Nadia & Rizky · #WA-26041 · Sewa. " +
                    "4 barang perlu disiapkan, ambil 4 Nov.",
        time = "14.20",
        category = EmployeeNotificationCategory.ORDER,
        type = EmployeeNotificationType.NEW_ORDER,
        group = EmployeeNotificationGroup.YESTERDAY,
        actionLabel = "Buka checklist",
        action = EmployeeNotificationAction.OPEN_CHECKLIST,
        isUnread = false
    )
)


// ============================================================
// MAIN SCREEN
// ============================================================

@Composable
fun NotificationsEmployeeScreen(
    modifier: Modifier = Modifier,

    // Aktifkan untuk mencocokkan status bar SVG di Preview.
    // Pada aplikasi Android, status bar sebenarnya ditangani OS.
    showMockStatusBar: Boolean = false,
    showMockHomeIndicator: Boolean = false,

    onMarkAllRead: () -> Unit = {},

    onNotificationClick: (EmployeeNotification) -> Unit = {},

    onNotificationAction: (EmployeeNotification) -> Unit = {},

    onBottomNavigationClick: (EmployeeBottomDestination) -> Unit = {}
) {

    var notifications by remember {
        mutableStateOf(sampleNotifications())
    }

    var selectedFilter by remember {
        mutableStateOf(NotificationFilter.ALL)
    }

    val unreadCount = notifications.count {
        it.isUnread
    }

    val filteredNotifications = notifications.filter { notification ->

        when (selectedFilter) {

            NotificationFilter.ALL -> true

            NotificationFilter.UNREAD ->
                notification.isUnread

            NotificationFilter.ORDER ->
                notification.category == EmployeeNotificationCategory.ORDER

            NotificationFilter.RETURN ->
                notification.category == EmployeeNotificationCategory.RETURN

            NotificationFilter.STATUS ->
                notification.category == EmployeeNotificationCategory.STATUS
        }
    }

    val todayNotifications = filteredNotifications.filter {
        it.group == EmployeeNotificationGroup.TODAY
    }

    val yesterdayNotifications = filteredNotifications.filter {
        it.group == EmployeeNotificationGroup.YESTERDAY
    }

    fun markAsRead(id: String) {

        notifications = notifications.map { notification ->

            if (notification.id == id) {
                notification.copy(isUnread = false)
            } else {
                notification
            }
        }
    }

    fun markAllAsRead() {

        notifications = notifications.map {
            it.copy(isUnread = false)
        }

        onMarkAllRead()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = NotificationBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NotificationBackground)
        ) {

            // STATUS BAR MOCK
            if (showMockStatusBar) {
                NotificationStatusBar()
            }

            // HEADER
            NotificationHeader(
                unreadCount = unreadCount,
                onMarkAllRead = {
                    markAllAsRead()
                }
            )

            // FILTER CHIPS
            NotificationFilterBar(
                selectedFilter = selectedFilter,
                notifications = notifications,
                onFilterSelected = {
                    selectedFilter = it
                }
            )

            // NOTIFICATION CONTENT
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(Modifier.height(15.dp))

                if (filteredNotifications.isEmpty()) {

                    EmptyNotificationState()

                } else {

                    // TODAY
                    if (todayNotifications.isNotEmpty()) {

                        NotificationSectionHeader(
                            title = "Hari ini",
                            newCount = todayNotifications.count {
                                it.isUnread
                            }
                        )

                        Spacer(Modifier.height(11.dp))

                        todayNotifications.forEach { notification ->

                            NotificationCard(
                                notification = notification,

                                onCardClick = {

                                    markAsRead(notification.id)

                                    onNotificationClick(
                                        notification
                                    )
                                },

                                onActionClick = {

                                    markAsRead(notification.id)

                                    onNotificationAction(
                                        notification
                                    )
                                }
                            )

                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    // YESTERDAY
                    if (yesterdayNotifications.isNotEmpty()) {

                        Spacer(Modifier.height(9.dp))

                        NotificationSectionHeader(
                            title = "Kemarin",
                            newCount = yesterdayNotifications.count {
                                it.isUnread
                            }
                        )

                        Spacer(Modifier.height(11.dp))

                        yesterdayNotifications.forEach { notification ->

                            NotificationCard(
                                notification = notification,

                                onCardClick = {

                                    markAsRead(notification.id)

                                    onNotificationClick(
                                        notification
                                    )
                                },

                                onActionClick = {

                                    markAsRead(notification.id)

                                    onNotificationAction(
                                        notification
                                    )
                                }
                            )

                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            // BOTTOM NAVIGATION
            EmployeeBottomNavigation(
                selectedDestination = EmployeeBottomDestination.NOTIFIKASI,
                onDestinationClick = onBottomNavigationClick
            )

            // MOCK HOME INDICATOR FOR PREVIEW
            if (showMockHomeIndicator) {
                NotificationHomeIndicator()
            }
        }
    }
}


// ============================================================
// STATUS BAR
// ============================================================

@Composable
private fun NotificationStatusBar() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .padding(
                start = 24.dp,
                end = 20.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "09.41",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NotificationBrown
        )

        Spacer(Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Canvas(
                modifier = Modifier.size(
                    width = 19.dp,
                    height = 14.dp
                )
            ) {

                val heights = listOf(
                    0.35f,
                    0.55f,
                    0.75f,
                    1f
                )

                heights.forEachIndexed { index, height ->

                    val barWidth = size.width * 0.16f

                    drawRoundRect(
                        color = NotificationBrown,
                        topLeft = Offset(
                            x = index * size.width * 0.24f,
                            y = size.height * (1f - height)
                        ),
                        size = Size(
                            barWidth,
                            size.height * height
                        ),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                }
            }

            Text(
                text = "86%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NotificationBrown
            )

            Canvas(
                modifier = Modifier.size(
                    width = 23.dp,
                    height = 13.dp
                )
            ) {

                drawRoundRect(
                    color = NotificationBrown,
                    topLeft = Offset(0f, 1f),
                    size = Size(
                        size.width * 0.88f,
                        size.height - 2f
                    ),
                    cornerRadius = CornerRadius(3f, 3f),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                drawRoundRect(
                    color = NotificationBrown,
                    topLeft = Offset(
                        2.dp.toPx(),
                        3.dp.toPx()
                    ),
                    size = Size(
                        size.width * 0.69f,
                        size.height - 6.dp.toPx()
                    ),
                    cornerRadius = CornerRadius(1f, 1f)
                )

                drawRoundRect(
                    color = NotificationBrown,
                    topLeft = Offset(
                        size.width * 0.91f,
                        size.height * 0.32f
                    ),
                    size = Size(
                        size.width * 0.08f,
                        size.height * 0.36f
                    ),
                    cornerRadius = CornerRadius(1f, 1f)
                )
            }
        }
    }
}


// ============================================================
// HEADER
// ============================================================

@Composable
private fun NotificationHeader(
    unreadCount: Int,
    onMarkAllRead: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(87.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Notifikasi",
                fontFamily = FontFamily.Serif,
                fontSize = 29.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Normal,
                color = NotificationBrown
            )

            Spacer(Modifier.height(1.dp))

            Text(
                text = "$unreadCount belum dibaca · Rabu, 4 November",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = NotificationSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // MARK ALL AS READ BUTTON
        Box(
            modifier = Modifier
                .size(37.dp)
                .clip(CircleShape)
                .background(NotificationSurface)
                .border(
                    width = 1.dp,
                    color = NotificationBorder,
                    shape = CircleShape
                )
                .clickable(
                    role = Role.Button,
                    onClick = onMarkAllRead
                ),
            contentAlignment = Alignment.Center
        ) {

            NotificationIcon(
                type = NotificationIconType.DOUBLE_CHECK,
                tint = NotificationDarkBrown,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        }
    }
}


// ============================================================
// FILTER BAR
// ============================================================

@Composable
private fun NotificationFilterBar(
    selectedFilter: NotificationFilter,
    notifications: List<EmployeeNotification>,
    onFilterSelected: (NotificationFilter) -> Unit
) {

    val allCount = notifications.size

    val unreadCount = notifications.count {
        it.isUnread
    }

    val orderCount = notifications.count {
        it.category == EmployeeNotificationCategory.ORDER
    }

    val returnCount = notifications.count {
        it.category == EmployeeNotificationCategory.RETURN
    }

    val statusCount = notifications.count {
        it.category == EmployeeNotificationCategory.STATUS
    }

    val filters = listOf(

        FilterOption(
            filter = NotificationFilter.ALL,
            label = "Semua",
            count = allCount,
            badgeColor = Color(0xFF6B5B4F)
        ),

        FilterOption(
            filter = NotificationFilter.UNREAD,
            label = "Belum dibaca",
            count = unreadCount,
            badgeColor = NotificationOrange
        ),

        FilterOption(
            filter = NotificationFilter.ORDER,
            label = "Pesanan",
            count = orderCount,
            badgeColor = NotificationTealAction
        ),

        FilterOption(
            filter = NotificationFilter.RETURN,
            label = "Pengembalian",
            count = returnCount,
            badgeColor = NotificationGold
        ),

        FilterOption(
            filter = NotificationFilter.STATUS,
            label = "Status",
            count = statusCount,
            badgeColor = NotificationBlue
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            )
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        filters.forEach { option ->

            NotificationFilterChip(
                option = option,
                selected = selectedFilter == option.filter,
                onClick = {
                    onFilterSelected(option.filter)
                }
            )
        }
    }

    Spacer(Modifier.height(7.dp))
}

private data class FilterOption(
    val filter: NotificationFilter,
    val label: String,
    val count: Int,
    val badgeColor: Color
)



@Composable
private fun NotificationFilterChip(
    option: FilterOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) {
        NotificationDarkBrown
    } else {
        NotificationSurface
    }

    val textColor = if (selected) {
        Color.White
    } else {
        NotificationText
    }

    Row(
        modifier = Modifier
            .height(36.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = NotificationBorder,
                shape = CircleShape
            )
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 12.dp),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {

        // LABEL FILTER
        Text(
            text = option.label,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = textColor,
            maxLines = 1,
            softWrap = false
        )

        // NUMBER BADGE
        Box(
            modifier = Modifier
                .widthIn(min = 19.dp)
                .height(19.dp)
                .clip(CircleShape)
                .background(
                    if (selected) {
                        Color(0xFF6B5B4F)
                    } else {
                        option.badgeColor
                    }
                )
                .padding(
                    horizontal = if (option.count > 9) 3.dp else 0.dp
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = option.count.toString(),

                modifier = Modifier.offset(
                    x = 0.dp,
                    y = (-0.5).dp
                ),

                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 12.sp,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),

                maxLines = 1,
                softWrap = false
            )
        }
    }
}




// ============================================================
// SECTION HEADER
// ============================================================

@Composable
private fun NotificationSectionHeader(
    title: String,
    newCount: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 23.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = NotificationBrown
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = if (newCount > 0) {
                "$newCount baru"
            } else {
                "sudah dibaca"
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (newCount > 0) {
                NotificationOrangeDark
            } else {
                NotificationSecondary
            }
        )
    }
}


// ============================================================
// NOTIFICATION CARD
// ============================================================

@Composable
private fun NotificationCard(
    notification: EmployeeNotification,
    onCardClick: () -> Unit,
    onActionClick: () -> Unit
) {

    val shape = RoundedCornerShape(21.dp)

    val iconBackground: Color
    val iconTint: Color
    val actionTint: Color
    val iconType: NotificationIconType

    when (notification.type) {

        EmployeeNotificationType.NEW_ORDER -> {

            iconBackground = NotificationTealBg
            iconTint = NotificationTeal
            actionTint = NotificationTeal
            iconType = NotificationIconType.CLIPBOARD
        }

        EmployeeNotificationType.ORDER_EDITED -> {

            iconBackground = NotificationRedBg
            iconTint = NotificationRed
            actionTint = NotificationOrangeDark
            iconType = NotificationIconType.REFRESH
        }

        EmployeeNotificationType.RETURN_REMINDER -> {

            iconBackground = NotificationGoldBg
            iconTint = NotificationGold
            actionTint = NotificationGold
            iconType = NotificationIconType.CLOCK
        }

        EmployeeNotificationType.ORDER_CONFIRMED -> {

            iconBackground = NotificationBlueBg
            iconTint = NotificationBlue
            actionTint = NotificationBlue
            iconType = NotificationIconType.CHECK_CIRCLE
        }
    }

    val cardBorder = if (notification.isUnread) {
        NotificationUnreadBorder
    } else {
        NotificationCardBorder
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(NotificationSurface)
            .border(
                width = 1.dp,
                color = cardBorder,
                shape = shape
            )
            .clickable(
                role = Role.Button,
                onClick = onCardClick
            )
            .padding(
                horizontal = 15.dp,
                vertical = 14.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            // NOTIFICATION ICON
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {

                NotificationIcon(
                    type = iconType,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 1.7.dp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // TITLE + TIME + UNREAD DOT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = notification.title,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = NotificationBrown,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (notification.isUnread) {

                        Spacer(Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    NotificationOrange,
                                    CircleShape
                                )
                        )

                        Spacer(Modifier.width(5.dp))
                    } else {
                        Spacer(Modifier.width(6.dp))
                    }

                    Text(
                        text = notification.time,
                        fontSize = 12.sp,
                        color = NotificationSecondary,
                        maxLines = 1
                    )
                }

                Spacer(Modifier.height(4.dp))

                // DESCRIPTION
                Text(
                    text = notification.description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = NotificationText,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                // DIVIDER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(NotificationDivider)
                )

                Spacer(Modifier.height(7.dp))

                // ACTION LINK
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(
                            role = Role.Button,
                            onClick = onActionClick
                        )
                        .padding(vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = notification.actionLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = actionTint,
                        maxLines = 1
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = "›",
                        fontSize = 19.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = actionTint
                    )
                }
            }
        }
    }
}


// ============================================================
// EMPTY NOTIFICATION STATE
// ============================================================

@Composable
private fun EmptyNotificationState() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 75.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    NotificationTealBg,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            NotificationIcon(
                type = NotificationIconType.BELL,
                tint = NotificationTeal,
                modifier = Modifier.size(29.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Tidak ada notifikasi",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = NotificationBrown
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = "Belum ada pemberitahuan untuk kategori ini.",
            fontSize = 12.sp,
            color = NotificationSecondary
        )
    }
}


// ============================================================
// BOTTOM NAVIGATION
// ============================================================

private enum class EmployeeNavIcon {
    TASK,
    CLIPBOARD,
    SCAN,
    BELL,
    MORE
}

@Composable
private fun EmployeeBottomNavigation(
    selectedDestination: EmployeeBottomDestination,
    onDestinationClick: (EmployeeBottomDestination) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NotificationBottomSurface)
            .navigationBarsPadding()
            .border(
                width = 1.dp,
                color = NotificationDivider,
                shape = RoundedCornerShape(0.dp)
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomNavigationItem(
                modifier = Modifier.weight(1f),
                label = "Tugas",
                destination = EmployeeBottomDestination.TUGAS,
                icon = EmployeeNavIcon.TASK,
                selected = selectedDestination ==
                        EmployeeBottomDestination.TUGAS,
                onClick = onDestinationClick
            )

            BottomNavigationItem(
                modifier = Modifier.weight(1f),
                label = "Pesanan",
                destination = EmployeeBottomDestination.PESANAN,
                icon = EmployeeNavIcon.CLIPBOARD,
                selected = selectedDestination ==
                        EmployeeBottomDestination.PESANAN,
                onClick = onDestinationClick
            )

            BottomNavigationItem(
                modifier = Modifier.weight(1f),
                label = "Scan",
                destination = EmployeeBottomDestination.SCAN,
                icon = EmployeeNavIcon.SCAN,
                selected = selectedDestination ==
                        EmployeeBottomDestination.SCAN,
                onClick = onDestinationClick
            )

            BottomNavigationItem(
                modifier = Modifier.weight(1f),
                label = "Notifikasi",
                destination = EmployeeBottomDestination.NOTIFIKASI,
                icon = EmployeeNavIcon.BELL,
                selected = selectedDestination ==
                        EmployeeBottomDestination.NOTIFIKASI,
                showBadge = true,
                onClick = onDestinationClick
            )

            BottomNavigationItem(
                modifier = Modifier.weight(1f),
                label = "Lainnya",
                destination = EmployeeBottomDestination.LAINNYA,
                icon = EmployeeNavIcon.MORE,
                selected = selectedDestination ==
                        EmployeeBottomDestination.LAINNYA,
                onClick = onDestinationClick
            )
        }
    }
}

@Composable
private fun BottomNavigationItem(
    modifier: Modifier = Modifier,
    label: String,
    destination: EmployeeBottomDestination,
    icon: EmployeeNavIcon,
    selected: Boolean,
    showBadge: Boolean = false,
    onClick: (EmployeeBottomDestination) -> Unit
) {

    val tint = if (selected) {
        NotificationDarkBrown
    } else {
        NotificationSecondary
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(
                horizontal = 2.dp,
                vertical = 5.dp
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) {
                    NotificationSelected
                } else {
                    Color.Transparent
                }
            )
            .clickable(
                role = Role.Tab,
                onClick = {
                    onClick(destination)
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // NAVIGATION ICON
        Box(
            modifier = Modifier.size(25.dp),
            contentAlignment = Alignment.Center
        ) {

            NotificationIcon(
                type = when (icon) {

                    EmployeeNavIcon.TASK ->
                        NotificationIconType.TASK

                    EmployeeNavIcon.CLIPBOARD ->
                        NotificationIconType.CLIPBOARD

                    EmployeeNavIcon.SCAN ->
                        NotificationIconType.SCAN

                    EmployeeNavIcon.BELL ->
                        NotificationIconType.BELL

                    EmployeeNavIcon.MORE ->
                        NotificationIconType.MORE
                },
                tint = tint,
                modifier = Modifier.size(23.dp),
                strokeWidth = 1.8.dp
            )

            if (showBadge) {

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-1).dp)
                        .size(9.dp)
                        .background(
                            NotificationOrange,
                            CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = NotificationBottomSurface,
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


// ============================================================
// HOME INDICATOR - PREVIEW ONLY
// ============================================================

@Composable
private fun NotificationHomeIndicator() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
            .background(NotificationBottomSurface),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .width(50.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(Color(0xFF3A2A22))
        )
    }
}


// ============================================================
// ICON SYSTEM
// Custom Canvas Icons - No Additional Drawable Required
// ============================================================

private enum class NotificationIconType {
    DOUBLE_CHECK,
    CLIPBOARD,
    REFRESH,
    CLOCK,
    CHECK_CIRCLE,
    TASK,
    SCAN,
    BELL,
    MORE
}

@Composable
private fun NotificationIcon(
    type: NotificationIconType,
    tint: Color,
    modifier: Modifier = Modifier.size(20.dp),
    strokeWidth: androidx.compose.ui.unit.Dp = 1.8.dp
) {

    Canvas(modifier = modifier) {

        val w = size.width
        val h = size.height
        val sw = strokeWidth.toPx()

        fun p(x: Float, y: Float) =
            Offset(w * x, h * y)

        val outline = Stroke(
            width = sw,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        when (type) {

            // DOUBLE CHECK
            NotificationIconType.DOUBLE_CHECK -> {

                val first = Path().apply {
                    moveTo(w * 0.08f, h * 0.54f)
                    lineTo(w * 0.29f, h * 0.76f)
                    lineTo(w * 0.64f, h * 0.35f)
                }

                val second = Path().apply {
                    moveTo(w * 0.39f, h * 0.59f)
                    lineTo(w * 0.53f, h * 0.73f)
                    lineTo(w * 0.91f, h * 0.28f)
                }

                drawPath(
                    path = first,
                    color = tint,
                    style = outline
                )

                drawPath(
                    path = second,
                    color = tint,
                    style = outline
                )
            }

            // CLIPBOARD
            NotificationIconType.CLIPBOARD -> {

                drawRoundRect(
                    color = tint,
                    topLeft = p(0.20f, 0.20f),
                    size = Size(w * 0.60f, h * 0.72f),
                    cornerRadius = CornerRadius(
                        w * 0.08f,
                        h * 0.08f
                    ),
                    style = outline
                )

                drawRoundRect(
                    color = tint,
                    topLeft = p(0.37f, 0.10f),
                    size = Size(w * 0.26f, h * 0.19f),
                    cornerRadius = CornerRadius(
                        w * 0.05f,
                        h * 0.05f
                    ),
                    style = outline
                )

                for (i in 0..2) {

                    val y = 0.45f + i * 0.16f

                    drawLine(
                        color = tint,
                        start = p(0.36f, y),
                        end = p(0.65f, y),
                        strokeWidth = sw,
                        cap = StrokeCap.Round
                    )
                }
            }

            // REFRESH / EDIT
            NotificationIconType.REFRESH -> {

                drawArc(
                    color = tint,
                    startAngle = 42f,
                    sweepAngle = 155f,
                    useCenter = false,
                    topLeft = p(0.13f, 0.13f),
                    size = Size(w * 0.74f, h * 0.74f),
                    style = outline
                )

                drawArc(
                    color = tint,
                    startAngle = 222f,
                    sweepAngle = 155f,
                    useCenter = false,
                    topLeft = p(0.13f, 0.13f),
                    size = Size(w * 0.74f, h * 0.74f),
                    style = outline
                )

                val arrow1 = Path().apply {
                    moveTo(w * 0.70f, h * 0.10f)
                    lineTo(w * 0.89f, h * 0.12f)
                    lineTo(w * 0.87f, h * 0.31f)
                }

                val arrow2 = Path().apply {
                    moveTo(w * 0.30f, h * 0.90f)
                    lineTo(w * 0.11f, h * 0.88f)
                    lineTo(w * 0.13f, h * 0.69f)
                }

                drawPath(arrow1, tint, style = outline)
                drawPath(arrow2, tint, style = outline)
            }

            // CLOCK
            NotificationIconType.CLOCK -> {

                drawCircle(
                    color = tint,
                    radius = w * 0.39f,
                    center = p(0.5f, 0.5f),
                    style = outline
                )

                drawLine(
                    tint,
                    p(0.5f, 0.5f),
                    p(0.5f, 0.27f),
                    sw,
                    StrokeCap.Round
                )

                drawLine(
                    tint,
                    p(0.5f, 0.5f),
                    p(0.69f, 0.59f),
                    sw,
                    StrokeCap.Round
                )
            }

            // CHECK CIRCLE
            NotificationIconType.CHECK_CIRCLE -> {

                drawCircle(
                    color = tint,
                    radius = w * 0.39f,
                    center = p(0.5f, 0.5f),
                    style = outline
                )

                val path = Path().apply {
                    moveTo(w * 0.29f, h * 0.51f)
                    lineTo(w * 0.44f, h * 0.66f)
                    lineTo(w * 0.72f, h * 0.35f)
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = outline
                )
            }

            // TASK CHECKBOX
            NotificationIconType.TASK -> {

                drawRoundRect(
                    color = tint,
                    topLeft = p(0.15f, 0.12f),
                    size = Size(w * 0.70f, h * 0.76f),
                    cornerRadius = CornerRadius(
                        w * 0.10f,
                        h * 0.10f
                    ),
                    style = outline
                )

                val path = Path().apply {
                    moveTo(w * 0.29f, h * 0.51f)
                    lineTo(w * 0.44f, h * 0.65f)
                    lineTo(w * 0.70f, h * 0.36f)
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = outline
                )
            }

            // QR SCAN
            NotificationIconType.SCAN -> {

                // Top left
                drawLine(tint, p(0.12f, 0.36f), p(0.12f, 0.12f), sw)
                drawLine(tint, p(0.12f, 0.12f), p(0.36f, 0.12f), sw)

                // Top right
                drawLine(tint, p(0.64f, 0.12f), p(0.88f, 0.12f), sw)
                drawLine(tint, p(0.88f, 0.12f), p(0.88f, 0.36f), sw)

                // Bottom left
                drawLine(tint, p(0.12f, 0.64f), p(0.12f, 0.88f), sw)
                drawLine(tint, p(0.12f, 0.88f), p(0.36f, 0.88f), sw)

                // Bottom right
                drawLine(tint, p(0.64f, 0.88f), p(0.88f, 0.88f), sw)
                drawLine(tint, p(0.88f, 0.88f), p(0.88f, 0.64f), sw)

                drawLine(
                    tint,
                    p(0.37f, 0.50f),
                    p(0.63f, 0.50f),
                    sw,
                    StrokeCap.Round
                )
            }

            // BELL
            NotificationIconType.BELL -> {

                val path = Path().apply {

                    moveTo(w * 0.24f, h * 0.70f)

                    lineTo(w * 0.30f, h * 0.60f)
                    lineTo(w * 0.30f, h * 0.42f)

                    quadraticTo(
                        w * 0.30f,
                        h * 0.18f,
                        w * 0.50f,
                        h * 0.16f
                    )

                    quadraticTo(
                        w * 0.70f,
                        h * 0.18f,
                        w * 0.70f,
                        h * 0.42f
                    )

                    lineTo(w * 0.70f, h * 0.60f)
                    lineTo(w * 0.77f, h * 0.70f)

                    close()
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = outline
                )

                drawArc(
                    color = tint,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = p(0.40f, 0.67f),
                    size = Size(w * 0.20f, h * 0.20f),
                    style = outline
                )

                drawLine(
                    tint,
                    p(0.50f, 0.08f),
                    p(0.50f, 0.15f),
                    sw,
                    StrokeCap.Round
                )
            }

            // MORE / MENU
            NotificationIconType.MORE -> {

                for (i in 0..2) {

                    val y = 0.25f + i * 0.25f

                    drawLine(
                        color = tint,
                        start = p(0.15f, y),
                        end = p(0.85f, y),
                        strokeWidth = sw,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}


// ============================================================
// HOME PREVIEW
// ============================================================

@Preview(
    name = "Notifications Employee",
    showBackground = true,
    widthDp = 393,
    heightDp = 1076
)
@Composable
private fun NotificationsEmployeeScreenPreview() {

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = NotificationDarkBrown,
            onPrimary = Color.White,
            background = NotificationBackground,
            onBackground = NotificationBrown,
            surface = NotificationSurface,
            onSurface = NotificationBrown
        )
    ) {

        NotificationsEmployeeScreen(
            showMockStatusBar = true,
            showMockHomeIndicator = true,
            onMarkAllRead = {},
            onNotificationClick = {},
            onNotificationAction = {},
            onBottomNavigationClick = {}
        )
    }
}
