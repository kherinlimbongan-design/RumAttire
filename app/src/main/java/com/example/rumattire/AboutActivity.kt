package com.example.rumattire

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AboutBackground = Color(0xFFF4EFE7)
private val AboutHeaderBackground = Color(0xFFFAF6EF)
private val AboutNavBackground = Color(0xFFFDFBF7)
private val AboutInk = Color(0xFF2E211B)
private val AboutBrown = Color(0xFF3A2A22)
private val AboutBody = Color(0xFF5A4A3F)
private val AboutMuted = Color(0xFF8C7B6F)
private val AboutGold = Color(0xFFC4A46A)
private val AboutCream = Color(0xFFEADCC8)
private val AboutBorder = Color(0xFFE4DACB)
private val AboutDivider = Color(0xFFF0E8DB)

class AboutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = 0xFFFAF6EF.toInt()
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = 0xFFFDFBF7.toInt()
            )
        )

        setContent {
            AboutScreen(
                onBack = { onBackPressedDispatcher.onBackPressed() },
                onNotifications = {
                    showPendingDestination("Notifikasi")
                },
                onNavigate = { destination ->
                    if (destination == AboutDestination.More) {
                        finish()
                    } else {
                        showPendingDestination(destination.label)
                    }
                }
            )
        }
    }

    private fun showPendingDestination(label: String) {
        // Ganti dengan navigasi ke layar yang sesuai di proyekmu.
        Toast.makeText(
            this,
            "$label belum dihubungkan",
            Toast.LENGTH_SHORT
        ).show()
    }
}

enum class AboutDestination(val label: String) {
    Home("Beranda"),
    Inventory("Inventori"),
    Orders("Pesanan"),
    Boxes("Box"),
    More("Lainnya")
}

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    onNotifications: () -> Unit,
    onNavigate: (AboutDestination) -> Unit,
    version: String = "1.0.0",
    hasUnreadNotifications: Boolean = true,
    showDesignSystemBars: Boolean = false
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = AboutBrown,
            background = AboutBackground,
            surface = Color.White,
            onSurface = AboutInk
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().testTag("aboutRoot"),
            color = AboutBackground
        ) {
            val horizontalInsets = if (showDesignSystemBars) {
                Modifier
            } else {
                Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().then(horizontalInsets)
            ) {
                AboutHeader(
                    onBack = onBack,
                    onNotifications = onNotifications,
                    hasUnreadNotifications = hasUnreadNotifications,
                    showDesignSystemBars = showDesignSystemBars
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("aboutContent")
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 12.dp,
                            bottom = 26.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AboutBrandCard(version)

                    AboutCard(tag = "aboutDescriptionCard") {
                        AboutSectionTitle("Tentang aplikasi")
                        Spacer(Modifier.height(8.dp))

                        AboutText(
                            text = "Rum Attire adalah aplikasi manajemen " +
                                    "internal untuk atelier busana pengantin. " +
                                    "Kelola inventaris, box penyimpanan dengan " +
                                    "QR code, pesanan, persiapan barang, dan " +
                                    "pembayaran dalam satu tempat.",
                            size = 14,
                            lineHeight = 21,
                            color = AboutBody
                        )
                    }

                    AboutFeaturesCard()
                    AboutTeamCard()
                    AboutDetailsCard(version)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .testTag("aboutCopyright"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AboutText(
                            text = "© 2026 Tim Benang Emas",
                            size = 12,
                            lineHeight = 18,
                            color = AboutMuted,
                            align = TextAlign.Center
                        )
                        AboutText(
                            text = "Dibuat dengan teliti untuk Rum Attire",
                            size = 12,
                            lineHeight = 18,
                            color = AboutMuted,
                            align = TextAlign.Center
                        )
                    }
                }

                AboutBottomNavigation(
                    onNavigate = onNavigate,
                    showDesignSystemBars = showDesignSystemBars
                )
            }
        }
    }
}

@Composable
private fun AboutHeader(
    onBack: () -> Unit,
    onNotifications: () -> Unit,
    hasUnreadNotifications: Boolean,
    showDesignSystemBars: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AboutHeaderBackground)
            .then(
                if (showDesignSystemBars) Modifier else Modifier.statusBarsPadding()
            )
            .testTag("aboutHeader")
    ) {
        if (showDesignSystemBars) {
            AboutDesignStatusBar()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 7.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("aboutBack")
                    .semantics { contentDescription = "Kembali" }
            ) {
                AboutLineIcon(AboutIcon.Back, AboutInk)
            }

            AboutText(
                text = "Tentang Aplikasi",
                size = 17,
                lineHeight = 22,
                weight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .weight(1f)
                    .semantics { heading() }
            )

            IconButton(
                onClick = onNotifications,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("aboutNotifications")
                    .semantics {
                        contentDescription = if (hasUnreadNotifications) {
                            "Notifikasi baru"
                        } else {
                            "Notifikasi"
                        }
                    }
            ) {
                Box(Modifier.size(24.dp)) {
                    AboutLineIcon(AboutIcon.Bell, AboutInk)

                    if (hasUnreadNotifications) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-0.5).dp, y = (-1.5).dp)
                                .size(9.dp),
                            shape = CircleShape,
                            color = Color(0xFFC2570C),
                            border = BorderStroke(1.5.dp, AboutHeaderBackground)
                        ) {}
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutBrandCard(version: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 212.dp)
            .testTag("aboutBrandCard"),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        Box(Modifier.fillMaxWidth()) {
            Canvas(Modifier.matchParentSize()) {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF5C4538), Color(0xFF2F211B)),
                        start = Offset.Zero,
                        end = Offset(189.129.dp.toPx(), 307.781.dp.toPx())
                    )
                )

                val goldCenter = Offset(size.width * 0.85f, size.height * 0.10f)
                val goldRadius = size.width * 0.70f

                scale(
                    scaleX = 1f,
                    scaleY = size.height * 0.70f / goldRadius,
                    pivot = goldCenter
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AboutGold.copy(alpha = 0.28f),
                                AboutGold.copy(alpha = 0f)
                            ),
                            center = goldCenter,
                            radius = goldRadius
                        ),
                        radius = goldRadius,
                        center = goldCenter
                    )
                }

                val warmColor = Color(0xFF9C7A62)
                val warmCenter = Offset(size.width * 0.10f, size.height * 0.90f)
                val warmRadius = size.width * 0.60f

                scale(
                    scaleX = 1f,
                    scaleY = size.height * 0.60f / warmRadius,
                    pivot = warmCenter
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                warmColor.copy(alpha = 0.35f),
                                warmColor.copy(alpha = 0f)
                            ),
                            center = warmCenter,
                            radius = warmRadius
                        ),
                        radius = warmRadius,
                        center = warmCenter
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 34.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AboutText(
                    text = "R / A",
                    size = 44,
                    lineHeight = 50,
                    color = AboutGold,
                    serif = true,
                    align = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .size(width = 32.dp, height = 1.5.dp)
                        .background(AboutGold.copy(alpha = 0.70f))
                )
                Spacer(Modifier.height(10.5.dp))

                AboutText(
                    text = "Rum Attire",
                    size = 28,
                    lineHeight = 32,
                    color = Color(0xFFFCF9F3),
                    serif = true,
                    align = TextAlign.Center
                )
                AboutText(
                    text = "Setiap detail, untuk momen istimewa.",
                    size = 13,
                    lineHeight = 18,
                    color = AboutCream,
                    align = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .widthIn(min = 98.dp)
                        .heightIn(min = 28.dp)
                        .background(Color.White.copy(alpha = 0.12f), CircleShape)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AboutText(
                        text = "Versi $version",
                        size = 12,
                        lineHeight = 16,
                        color = AboutCream
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutCard(
    tag: String,
    verticalPadding: Int = 20,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().testTag(tag),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AboutBorder)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = verticalPadding.dp
            ),
            content = content
        )
    }
}

@Composable
private fun AboutSectionTitle(text: String) {
    AboutText(
        text = text,
        size = 16,
        lineHeight = 20,
        weight = FontWeight.Bold,
        modifier = Modifier.semantics { heading() }
    )
}

private data class AboutFeature(
    val title: String,
    val description: String,
    val icon: AboutIcon
)

private val aboutFeatures = listOf(
    AboutFeature(
        "Inventaris & box QR",
        "Pantau barang, lokasi, dan isi box",
        AboutIcon.Box
    ),
    AboutFeature(
        "Pesanan & persiapan",
        "Validasi barang oleh karyawan",
        AboutIcon.Clipboard
    ),
    AboutFeature(
        "Pembayaran DP & lunas",
        "Catat dan pantau tagihan",
        AboutIcon.Wallet
    ),
    AboutFeature(
        "Notifikasi otomatis",
        "Pengingat tagihan dan pengembalian",
        AboutIcon.Bell
    )
)

@Composable
private fun AboutFeaturesCard() {
    AboutCard(tag = "aboutFeaturesCard") {
        AboutSectionTitle("Fitur utama")
        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            aboutFeatures.forEach { feature ->
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFAF4EA), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AboutLineIcon(feature.icon, AboutBrown)
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(Modifier.weight(1f)) {
                        AboutText(
                            text = feature.title,
                            size = 14,
                            lineHeight = 18,
                            weight = FontWeight.Bold
                        )
                        AboutText(
                            text = feature.description,
                            size = 12,
                            lineHeight = 16,
                            color = AboutMuted
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}

private data class AboutTeamMember(
    val name: String,
    val initial: String,
    val background: Color,
    val foreground: Color
)

private val aboutTeamMembers = listOf(
    AboutTeamMember("Kerin", "K", Color(0xFF3E2B23), AboutGold),
    AboutTeamMember("Hardianti", "H", Color(0xFF8A7260), Color(0xFFFCF9F3)),
    AboutTeamMember("Zhelon", "Z", AboutGold, Color(0xFF3E2B23))
)

@Composable
private fun AboutTeamCard() {
    AboutCard(tag = "aboutTeamCard") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f)) {
                AboutSectionTitle("Tim pengembang")
            }
            Spacer(Modifier.width(8.dp))
            AboutText(
                text = "${aboutTeamMembers.size} anggota",
                size = 13,
                lineHeight = 18,
                color = AboutMuted
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF3E2B23), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                AboutText(
                    text = "BE",
                    size = 18,
                    lineHeight = 22,
                    color = AboutGold,
                    serif = true
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                AboutText(
                    text = "Tim Benang Emas",
                    size = 20,
                    lineHeight = 24,
                    serif = true
                )
                AboutText(
                    text = "Pengembang aplikasi Rum Attire",
                    size = 12,
                    lineHeight = 16,
                    color = AboutMuted
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        HorizontalDivider(thickness = 1.dp, color = AboutDivider)
        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth()) {
            aboutTeamMembers.forEach { member ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(member.background, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AboutText(
                            text = member.initial,
                            size = 26,
                            lineHeight = 32,
                            serif = true,
                            color = member.foreground
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    AboutText(
                        text = member.name,
                        size = 14,
                        lineHeight = 18,
                        weight = FontWeight.Bold,
                        align = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutDetailsCard(version: String) {
    AboutCard(tag = "aboutDetailsCard", verticalPadding = 14) {
        AboutDetailRow("Mitra", "Rum Attire", divider = true)
        AboutDetailRow("Versi", version, divider = true)
        AboutDetailRow("Institusi", "Politeknik Negeri Ujung Pandang")
    }
}

@Composable
private fun AboutDetailRow(
    label: String,
    value: String,
    divider: Boolean = false
) {
    Box(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AboutText(label, size = 13, lineHeight = 18, color = AboutMuted)
            Spacer(Modifier.width(12.dp))
            AboutText(
                text = value,
                size = 13,
                lineHeight = 18,
                weight = FontWeight.Bold,
                align = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }

        if (divider) {
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter),
                thickness = 1.dp,
                color = AboutDivider
            )
        }
    }
}

@Composable
private fun AboutBottomNavigation(
    onNavigate: (AboutDestination) -> Unit,
    showDesignSystemBars: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AboutNavBackground)
            .then(
                if (showDesignSystemBars) Modifier else Modifier.navigationBarsPadding()
            )
            .testTag("aboutNavbar")
    ) {
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE9E2D8))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 6.dp, top = 7.dp, bottom = 8.dp)
        ) {
            AboutDestination.entries.forEach { destination ->
                val active = destination == AboutDestination.More
                val icon = when (destination) {
                    AboutDestination.Home -> AboutIcon.Home
                    AboutDestination.Inventory -> AboutIcon.Shirt
                    AboutDestination.Orders -> AboutIcon.Clipboard
                    AboutDestination.Boxes -> AboutIcon.Box
                    AboutDestination.More -> AboutIcon.Menu
                }
                val color = if (active) AboutInk else AboutBody

                Surface(
                    onClick = { onNavigate(destination) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 1.dp)
                        .heightIn(min = 64.dp)
                        .semantics {
                            role = Role.Tab
                            selected = active
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = if (active) Color(0xFFEFE3D3) else Color.Transparent
                ) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AboutLineIcon(icon, color)
                        Spacer(Modifier.height(3.dp))
                        AboutText(
                            text = destination.label,
                            size = 11,
                            lineHeight = 15,
                            weight = if (active) FontWeight.Bold else FontWeight.Normal,
                            color = color,
                            align = TextAlign.Center
                        )
                        Spacer(Modifier.height(5.dp))
                        Box(
                            Modifier
                                .size(width = 20.dp, height = 2.4.dp)
                                .background(
                                    if (active) AboutBrown else Color.Transparent,
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }

        if (showDesignSystemBars) {
            Box(
                modifier = Modifier.fillMaxWidth().height(24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    Modifier
                        .padding(top = 8.dp)
                        .size(width = 104.dp, height = 4.dp)
                        .background(AboutBrown, CircleShape)
                )
            }
        }
    }
}

private enum class AboutIcon(val pathData: String) {
    Back("M20 12H4 M10.5 18.5L4 12L10.5 5.5"),
    Bell(
        "M6 8C6 4.69 8.69 2 12 2S18 4.69 18 8 " +
                "C18 15 21 17 21 17H3C3 17 6 15 6 8Z " +
                "M10.3 21C11 22.34 13 22.34 13.7 21"
    ),
    Box(
        "M11 21.73Q12 22.31 13 21.73L20 17.73 " +
                "Q21 17.15 21 16V8Q21 6.85 20 6.27L13 2.27 " +
                "Q12 1.69 11 2.27L4 6.27Q3 6.85 3 8V16 " +
                "Q3 17.15 4 17.73Z M12 22V12L3.3 7 " +
                "M12 12L20.7 7 M16.5 9.42L7.5 4.27"
    ),
    Clipboard(
        "M9 2H15Q16 2 16 3V5Q16 6 15 6H9Q8 6 8 5V3Q8 2 9 2Z " +
                "M16 4H18Q20 4 20 6V20Q20 22 18 22H6Q4 22 4 20V6 " +
                "Q4 4 6 4H8 M12 11H16 M12 16H16 M8 11H8.01 M8 16H8.01"
    ),
    Wallet(
        "M19 7V4Q19 3 18 3H5Q3 3 3 5Q3 7 5 7H20Q21 7 21 8V12 " +
                "M21 12H18Q16 12 16 14Q16 16 18 16H21Q22 16 22 15V13Q22 12 21 12Z " +
                "M3 5V19Q3 21 5 21H20Q21 21 21 20V16"
    ),
    Shirt(
        "M20.38 3.46L16 2C16 4.21 14.21 6 12 6S8 4.21 8 2L3.62 3.46 " +
                "Q2.03 4 2.28 5.69L2.86 9.16Q3 10 3.85 10H6V20Q6 22 8 22H16 " +
                "Q18 22 18 20V10H20.15Q21 10 21.14 9.16L21.72 5.69Q21.97 4 20.38 3.46Z"
    ),
    Menu("M4 6H20 M4 12H20 M4 18H20"),
    Home(""),
    Wifi("M8 10.5C10.3 8.5 13.7 8.5 16 10.5 M10 12.8Q12 11.2 14 12.8")
}

@Composable
private fun AboutLineIcon(
    icon: AboutIcon,
    tint: Color,
    modifier: Modifier = Modifier.size(24.dp)
) {
    val path = remember(icon) {
        if (icon.pathData.isEmpty()) null
        else PathParser().parsePathString(icon.pathData).toPath()
    }

    Canvas(modifier) {
        val stroke = Stroke(
            width = when (icon) {
                AboutIcon.Menu -> 1.9f
                AboutIcon.Wifi -> 1.5f
                else -> 1.8f
            },
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        scale(
            scaleX = size.width / 24f,
            scaleY = size.height / 24f,
            pivot = Offset.Zero
        ) {
            if (path != null) {
                drawPath(path, tint, style = stroke)
            }

            when (icon) {
                AboutIcon.Home -> {
                    for (x in listOf(3f, 14f)) {
                        for (y in listOf(3f, 14f)) {
                            drawRoundRect(
                                color = tint,
                                topLeft = Offset(x, y),
                                size = Size(7f, 7f),
                                cornerRadius = CornerRadius(1f),
                                style = stroke
                            )
                        }
                    }
                }
                AboutIcon.Wifi -> drawCircle(tint, 1f, Offset(12f, 15f))
                else -> Unit
            }
        }
    }
}

@Composable
private fun AboutText(
    text: String,
    size: Int,
    modifier: Modifier = Modifier,
    lineHeight: Int = size + 4,
    color: Color = AboutInk,
    weight: FontWeight = FontWeight.Normal,
    serif: Boolean = false,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        fontWeight = weight,
        fontFamily = if (serif) FontFamily.Serif else FontFamily.SansSerif,
        textAlign = align,
        style = TextStyle(
            letterSpacing = 0.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        )
    )
}

@Composable
private fun AboutDesignStatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AboutText("09.41", size = 13, weight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))

        Canvas(Modifier.size(width = 14.dp, height = 10.dp)) {
            val heights = listOf(4, 6, 8, 10)
            heights.forEachIndexed { index, height ->
                drawRoundRect(
                    color = AboutInk,
                    topLeft = Offset(
                        (index * 4).dp.toPx(),
                        (10 - height).dp.toPx()
                    ),
                    size = Size(2.dp.toPx(), height.dp.toPx()),
                    cornerRadius = CornerRadius(1.dp.toPx())
                )
            }
        }

        Spacer(Modifier.width(2.dp))
        AboutLineIcon(AboutIcon.Wifi, AboutInk, Modifier.size(18.dp))
        Spacer(Modifier.width(2.dp))
        AboutText("86%", size = 12, weight = FontWeight.Bold)
        Spacer(Modifier.width(3.dp))

        Canvas(Modifier.size(width = 21.dp, height = 11.dp)) {
            drawRoundRect(
                color = AboutInk,
                topLeft = Offset(0.6.dp.toPx(), 0.6.dp.toPx()),
                size = Size(18.dp.toPx(), 9.dp.toPx()),
                cornerRadius = CornerRadius(2.5.dp.toPx()),
                style = Stroke(1.2.dp.toPx())
            )
            drawRoundRect(
                color = AboutInk,
                topLeft = Offset(2.6.dp.toPx(), 2.6.dp.toPx()),
                size = Size(13.dp.toPx(), 5.dp.toPx()),
                cornerRadius = CornerRadius(1.dp.toPx())
            )
            drawRoundRect(
                color = AboutInk,
                topLeft = Offset(19.3.dp.toPx(), 4.dp.toPx()),
                size = Size(1.5.dp.toPx(), 3.dp.toPx()),
                cornerRadius = CornerRadius(0.75.dp.toPx())
            )
        }
    }
}

@Preview(
    name = "Tentang Aplikasi Rum Attire",
    widthDp = 393,
    heightDp = 1410,
    showBackground = true
)
@Composable
private fun PreviewAboutScreen() {
    AboutScreen(
        onBack = {},
        onNotifications = {},
        onNavigate = {},
        showDesignSystemBars = true
    )
}