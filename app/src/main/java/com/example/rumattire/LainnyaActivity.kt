package com.example.rumattire

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

private val MoreBackground = Color(0xFFF7F2E9)
private val MoreInk = Color(0xFF3B2A22)
private val MoreMuted = Color(0xFF8B7B70)
private val MoreGold = Color(0xFFC9A96E)
private val MoreBorder = Color(0xFFE6DACA)
private val MoreOrange = Color(0xFFE8704A)
private val MoreRed = Color(0xFFD64B5E)

class LainnyaOwnerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bars = SystemBarStyle.light(
            scrim = android.graphics.Color.TRANSPARENT,
            darkScrim = 0xFFF7F2E9.toInt()
        )

        enableEdgeToEdge(
            statusBarStyle = bars,
            navigationBarStyle = bars
        )

        setContent {
            var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

            LainnyaOwnerScreen(
                onOpenProfile = {
                    showPendingAction("Profil")
                },
                onOpenPayment = {
                    startActivity(
                        Intent(
                            this@LainnyaOwnerActivity,
                            PaymentActivity::class.java
                        )
                    )
                },
                onOpenNotifications = {
                    showPendingAction("Notifikasi")
                },
                onOpenAbout = {
                    showPendingAction("Tentang Aplikasi")
                },
                onLogout = { showLogoutDialog = true
                },
                onNavigate = { menu ->
                    showPendingAction(menu)
                }
            )

            if (showLogoutDialog) {
                LogoutDialog(
                    accountName = "Sari",
                    accountEmail = "sari@rumattire.com",
                    onDismiss = {
                        showLogoutDialog = false
                    },
                    onConfirmLogout = {
                        showLogoutDialog = false

                        // Panggil fungsi logout proyekmu untuk menghapus sesi,
                        // lalu arahkan pengguna ke halaman Login.
                    }
                )
            }
        }
    }

    private fun showPendingAction(action: String) {
        Toast.makeText(
            this,
            "$action belum dihubungkan",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun LainnyaOwnerScreen(
    name: String = "Ratna",
    email: String = "ratna@rumattire.com",
    avatarInitial: String = "R",
    paymentCount: Int = 2,
    notificationCount: Int = 3,
    version: String = "1.0.0",
    showDesignSystemBars: Boolean = false,
    onOpenProfile: () -> Unit = {},
    onOpenPayment: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = MoreInk,
            background = MoreBackground,
            onBackground = MoreInk,
            surface = Color.White,
            onSurface = MoreInk
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MoreBackground
        ) {
            val systemPadding = if (showDesignSystemBars) {
                Modifier
            } else {
                Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(systemPadding)
            ) {
                if (showDesignSystemBars) {
                    MoreDesignStatusBar()
                }

                // Semua koordinat isi mengikuti SVG,
                // dengan area status bar 44dp dikeluarkan.
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(724.dp)
                    ) {
                        MoreBaselineText(
                            text = "Lainnya",
                            size = 26,
                            serif = true,
                            modifier = Modifier
                                .offset(x = 24.dp, y = 46.dp)
                        )

                        MoreBaselineText(
                            text = "$name · Owner",
                            size = 12,
                            color = MoreMuted,
                            modifier = Modifier
                                .offset(x = 24.dp, y = 66.dp)
                        )

                        OwnerProfileCard(
                            name = name,
                            email = email,
                            initial = avatarInitial,
                            onClick = onOpenProfile,
                            modifier = Modifier
                                .offset(y = 88.dp)
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                        )

                        MoreBaselineText(
                            text = "Kelola",
                            size = 14,
                            weight = FontWeight.SemiBold,
                            modifier = Modifier
                                .offset(x = 16.dp, y = 238.dp)
                        )

                        MoreMenuCard(
                            modifier = Modifier
                                .offset(y = 254.dp)
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                        ) {
                            MoreMenuRow(
                                title = "Pembayaran",
                                subtitle = "Tagihan, DP, dan pelunasan",
                                icon = MoreIcon.Payment,
                                iconColor = Color(0xFFB7791F),
                                iconBackground = Color(0xFFFBF0D2),
                                badgeCount = paymentCount,
                                badgeColor = Color(0xFFB7791F),
                                onClick = onOpenPayment
                            )
                        }

                        MoreBaselineText(
                            text = "Pengaturan",
                            size = 14,
                            weight = FontWeight.SemiBold,
                            modifier = Modifier
                                .offset(x = 16.dp, y = 348.dp)
                        )

                        MoreMenuCard(
                            modifier = Modifier
                                .offset(y = 364.dp)
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                        ) {
                            MoreMenuRow(
                                title = "Profil",
                                subtitle = "Nama, email, kata sandi",
                                icon = MoreIcon.Profile,
                                iconColor = Color(0xFF1E9E8F),
                                iconBackground = Color(0xFFDFF3EF),
                                onClick = onOpenProfile
                            )

                            MoreSettingsDivider()

                            MoreMenuRow(
                                title = "Notifikasi",
                                subtitle = "Riwayat dan pengingat",
                                icon = MoreIcon.Notification,
                                iconColor = MoreOrange,
                                iconBackground = Color(0xFFFDE8DE),
                                badgeCount = notificationCount,
                                badgeColor = MoreOrange,
                                onClick = onOpenNotifications
                            )

                            MoreSettingsDivider()

                            MoreMenuRow(
                                title = "Tentang Aplikasi",
                                subtitle = "Versi $version",
                                icon = MoreIcon.Info,
                                iconColor = Color(0xFF7B61D9),
                                iconBackground = Color(0xFFECE7FB),
                                onClick = onOpenAbout
                            )
                        }

                        MoreMenuCard(
                            borderColor = Color(0xFFF2C9CF),
                            modifier = Modifier
                                .offset(y = 572.dp)
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                        ) {
                            MoreMenuRow(
                                title = "Keluar",
                                subtitle = "Akhiri sesi di perangkat ini",
                                icon = MoreIcon.Logout,
                                iconColor = MoreRed,
                                iconBackground = Color(0xFFFBE4E7),
                                titleColor = MoreRed,
                                showChevron = false,
                                onClick = onLogout
                            )
                        }

                        MoreBaselineText(
                            text = "R / A",
                            size = 18,
                            color = MoreGold,
                            serif = true,
                            letterSpacing = 1f,
                            align = TextAlign.Center,
                            modifier = Modifier
                                .offset(y = 676.dp)
                                .fillMaxWidth()
                        )

                        MoreBaselineText(
                            text = "Rum Attire · Owner Workspace · v$version",
                            size = 11,
                            color = MoreMuted,
                            align = TextAlign.Center,
                            modifier = Modifier
                                .offset(y = 698.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                MoreBottomNavigation(onNavigate)

                if (showDesignSystemBars) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(Color(0xFFFBF8F2)),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(width = 100.dp, height = 4.dp)
                                .background(MoreInk, CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnerProfileCard(
    name: String,
    email: String,
    initial: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(112.dp)
            .semantics {
                role = Role.Button
                contentDescription = "Buka profil $name"
            },
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF5C4336),
                            Color(0xFF3A2820)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = MoreGold.copy(alpha = 0.35f),
                    shape = shape
                )
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val center = Offset(
                    x = size.width - 20.dp.toPx(),
                    y = 14.dp.toPx()
                )

                listOf(64.dp, 92.dp).forEach { radius ->
                    drawCircle(
                        color = MoreGold.copy(alpha = 0.16f),
                        radius = radius.toPx(),
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(x = 19.dp, y = 27.dp)
                    .size(58.dp)
                    .border(
                        1.dp,
                        MoreGold.copy(alpha = 0.5f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MoreGold, CircleShape)
                )

                MoreBaselineText(
                    text = initial,
                    size = 24,
                    serif = true,
                    align = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 38.dp)
                        .fillMaxWidth()
                )
            }

            MoreBaselineText(
                text = name,
                size = 19,
                color = Color(0xFFFFF8EE),
                serif = true,
                modifier = Modifier.offset(x = 94.dp, y = 44.dp)
            )

            MoreBaselineText(
                text = email,
                size = 12,
                color = Color(0xFFE4D6C4),
                modifier = Modifier.offset(x = 94.dp, y = 64.dp)
            )

            Box(
                modifier = Modifier
                    .offset(x = 94.dp, y = 76.dp)
                    .size(width = 57.dp, height = 22.dp)
                    .background(MoreOrange, CircleShape)
            ) {
                MoreBaselineText(
                    text = "Owner",
                    size = 10.5f,
                    color = Color.White,
                    weight = FontWeight.SemiBold,
                    align = TextAlign.Center,
                    modifier = Modifier
                        .offset(y = 14.5.dp)
                        .fillMaxWidth()
                )
            }

            MoreVectorIcon(
                icon = MoreIcon.Chevron,
                color = MoreGold,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 13.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun MoreMenuCard(
    modifier: Modifier,
    borderColor: Color = MoreBorder,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            ),
        content = content
    )
}

@Composable
private fun MoreMenuRow(
    title: String,
    subtitle: String,
    icon: MoreIcon,
    iconColor: Color,
    iconBackground: Color,
    onClick: () -> Unit,
    titleColor: Color = MoreInk,
    badgeCount: Int = 0,
    badgeColor: Color = MoreOrange,
    showChevron: Boolean = true
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .semantics { role = Role.Button },
        color = Color.Transparent
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .offset(x = 16.dp, y = 12.dp)
                    .size(40.dp)
                    .background(
                        iconBackground,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                MoreVectorIcon(
                    icon = icon,
                    color = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Cadangkan ruang untuk badge dan panah.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 68.dp,
                        end = if (badgeCount > 0) 88.dp else 40.dp
                    )
            ) {
                MoreBaselineText(
                    text = title,
                    size = 14,
                    color = titleColor,
                    weight = FontWeight.SemiBold,
                    modifier = Modifier.offset(y = 30.dp)
                )

                MoreBaselineText(
                    text = subtitle,
                    size = 11,
                    color = MoreMuted,
                    modifier = Modifier.offset(y = 48.dp)
                )
            }

            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-52).dp, y = 22.dp)
                        .size(width = 28.dp, height = 20.dp)
                        .background(badgeColor, CircleShape)
                ) {
                    MoreBaselineText(
                        text = badgeCount.toString(),
                        size = 11,
                        color = Color.White,
                        weight = FontWeight.Bold,
                        align = TextAlign.Center,
                        modifier = Modifier
                            .offset(y = 14.dp)
                            .fillMaxWidth()
                    )
                }
            }

            if (showChevron) {
                MoreVectorIcon(
                    icon = MoreIcon.Chevron,
                    color = MoreMuted,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun MoreSettingsDivider() {
    // Overlay garis pada batas baris agar tinggi kartu tetap 192dp.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(start = 68.dp, end = 16.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFEFE6D8))
        )
    }
}

private data class MoreNavItem(
    val label: String,
    val icon: MoreIcon
)

private val moreNavigationItems = listOf(
    MoreNavItem("Beranda", MoreIcon.Home),
    MoreNavItem("Inventori", MoreIcon.Inventory),
    MoreNavItem("Pesanan", MoreIcon.Order),
    MoreNavItem("Box", MoreIcon.Box),
    MoreNavItem("Lainnya", MoreIcon.Menu)
)

@Composable
private fun MoreBottomNavigation(
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFFFBF8F2))
    ) {
        Row(Modifier.fillMaxSize()) {
            moreNavigationItems.forEach { item ->
                val active = item.label == "Lainnya"
                val color = if (active) {
                    Color(0xFF4A3328)
                } else {
                    Color(0xFF6B5A4E)
                }

                Surface(
                    onClick = { onNavigate(item.label) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .semantics {
                            role = Role.Tab
                            selected = active
                        },
                    color = Color.Transparent
                ) {
                    Box(Modifier.fillMaxSize()) {
                        if (active) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = 10.dp)
                                    .size(width = 64.dp, height = 48.dp)
                                    .background(
                                        Color(0xFFEADFCF),
                                        RoundedCornerShape(14.dp)
                                    )
                            )
                        }

                        MoreVectorIcon(
                            icon = item.icon,
                            color = color,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = 14.dp)
                                .size(24.dp)
                        )

                        MoreBaselineText(
                            text = item.label,
                            size = 10,
                            color = color,
                            weight = if (active) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                            align = TextAlign.Center,
                            modifier = Modifier
                                .offset(y = 49.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MoreBorder)
        )
    }
}

private enum class MoreIcon(val pathData: String) {
    Payment(
        "M-8 -2 H8 M3 2 H5"
    ),
    Profile(
        "M-7 8 a7 5.5 0 0 1 14 0"
    ),
    Notification(
        "M-6 4 V-1 a6 6 0 0 1 12 0 V4 l1.8 2.5 H-7.8 Z " +
                "M-2.5 9 a2.5 2.5 0 0 0 5 0"
    ),
    Info(
        "M0 -1 V4.5 M0 -4.5 v.01"
    ),
    Logout(
        "M-2 -8 H-6 a2 2 0 0 0 -2 2 V6 " +
                "a2 2 0 0 0 2 2 H-2 " +
                "M2 -4 L6 0 L2 4 M6 0 H-3"
    ),
    Chevron(
        "M-2 -5 L3 0 L-2 5"
    ),
    Home(""),
    Inventory(
        "M-8 -5 L-3.5 -8.5 Q0 -5.5 3.5 -8.5 " +
                "L8 -5 L6 -1 L4 -2 V8 H-4 V-2 L-6 -1 Z"
    ),
    Order(
        "M-3 -8 v-1.5 h6 v1.5 M-3.5 -2 h7 M-3.5 2.5 h7"
    ),
    Box(
        "M-8 -4 L0 -8 L8 -4 V5 L0 9 L-8 5 Z " +
                "M-8 -4 L0 0 L8 -4 M0 0 V9"
    ),
    Menu(
        "M-8 -5 H8 M-8 0 H8 M-8 5 H8"
    )
}

@Composable
private fun MoreVectorIcon(
    icon: MoreIcon,
    color: Color,
    modifier: Modifier = Modifier
) {
    val path = remember(icon) {
        PathParser().parsePathString(icon.pathData).toPath()
    }

    Canvas(modifier) {
        val stroke = Stroke(
            width = if (icon == MoreIcon.Menu) 1.8f else 1.6f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        scale(
            scaleX = size.width / 24f,
            scaleY = size.height / 24f,
            pivot = Offset.Zero
        ) {
            translate(left = 12f, top = 12f) {
                when (icon) {
                    MoreIcon.Payment -> {
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(-8f, -6f),
                            size = Size(16f, 12f),
                            cornerRadius = CornerRadius(2.5f),
                            style = stroke
                        )
                    }

                    MoreIcon.Profile -> {
                        drawCircle(
                            color = color,
                            radius = 3.6f,
                            center = Offset(0f, -3.5f),
                            style = stroke
                        )
                    }

                    MoreIcon.Info -> {
                        drawCircle(
                            color = color,
                            radius = 8f,
                            center = Offset.Zero,
                            style = stroke
                        )
                    }

                    MoreIcon.Home -> {
                        listOf(
                            Offset(-8f, -8f),
                            Offset(1.5f, -8f),
                            Offset(-8f, 1.5f),
                            Offset(1.5f, 1.5f)
                        ).forEach { position ->
                            drawRoundRect(
                                color = color,
                                topLeft = position,
                                size = Size(6.5f, 6.5f),
                                cornerRadius = CornerRadius(1.5f),
                                style = stroke
                            )
                        }
                    }

                    MoreIcon.Order -> {
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(-7f, -8f),
                            size = Size(14f, 17f),
                            cornerRadius = CornerRadius(2.5f),
                            style = stroke
                        )
                    }

                    else -> Unit
                }

                drawPath(path, color, style = stroke)
            }
        }
    }
}

/**
 * Menempatkan baseline teks pada koordinat SVG.
 * Dengan demikian posisi teks mengikuti desain sumber.
 */
@Composable
private fun MoreBaselineText(
    text: String,
    size: Number,
    modifier: Modifier = Modifier,
    color: Color = MoreInk,
    weight: FontWeight = FontWeight.Normal,
    serif: Boolean = false,
    align: TextAlign = TextAlign.Start,
    letterSpacing: Float = 0f
) {
    Text(
        text = text,
        color = color,
        fontSize = size.toFloat().sp,
        fontWeight = weight,
        fontFamily = if (serif) {
            FontFamily.Serif
        } else {
            FontFamily.SansSerif
        },
        textAlign = align,
        style = TextStyle(
            letterSpacing = letterSpacing.sp,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        ),
        modifier = modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            val baseline = placeable[FirstBaseline]

            layout(placeable.width, 0) {
                placeable.placeRelative(
                    x = 0,
                    y = -baseline
                )
            }
        }
    )
}

@Composable
private fun MoreDesignStatusBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        MoreBaselineText(
            text = "09.41",
            size = 12,
            weight = FontWeight.SemiBold,
            modifier = Modifier.offset(x = 24.dp, y = 29.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 33.dp)
                .offset(y = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(
                Modifier.size(width = 18.dp, height = 12.dp)
            ) {
                val unit = 1.dp.toPx()

                listOf(4f, 6f, 8f, 10f).forEachIndexed { index, height ->
                    drawRoundRect(
                        color = MoreInk,
                        topLeft = Offset(
                            index * 5 * unit,
                            (11 - height) * unit
                        ),
                        size = Size(3 * unit, height * unit),
                        cornerRadius = CornerRadius(unit)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = "86%",
                fontSize = 11.sp,
                color = MoreInk
            )

            Spacer(Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .size(width = 21.dp, height = 10.dp)
                    .border(
                        width = 1.dp,
                        color = MoreInk,
                        shape = RoundedCornerShape(3.dp)
                    )
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(15.dp)
                        .fillMaxHeight()
                        .background(
                            MoreInk,
                            RoundedCornerShape(1.5.dp)
                        )
                )
            }
        }
    }
}

@Preview(
    name = "Lainnya Owner",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun PreviewLainnyaOwner() {
    LainnyaOwnerScreen(
        showDesignSystemBars = true
    )
}