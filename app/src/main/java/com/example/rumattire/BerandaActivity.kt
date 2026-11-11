package com.example.rumattire

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rumattire.R
import androidx.compose.foundation.layout.heightIn

// ---------- Palet warna (sama dengan layar login & registrasi) ----------
private val Cream = Color(0xFFFCF9F3)
private val Canvas = Color(0xFFF7F3EB)
private val NavBackground = Color(0xFFFAF7F1)
private val NavBorder = Color(0xFFE7D8C6)
private val Gold = Color(0xFFC4A46A)
private val GoldLight = Color(0xFFD8BD86)
private val Brown = Color(0xFF3E2B23)
private val BrownNav = Color(0xFF49352A)
private val BrownMuted = Color(0xFF988478)
private val BrownSoft = Color(0xFF786657)
private val ChipText = Color(0xFFE8DCCD)
private val Green = Color(0xFF008C62)
private val Orange = Color(0xFFD64E09)
private val OrangeSoft = Color(0xFFFCE9DC)
private val CardBorder = Color(0xFFEADFCF)

private val AtelierColors = lightColorScheme(
    primary = Brown,
    onPrimary = Cream,
    secondary = Gold,
    background = Canvas,
    onBackground = Brown,
    surface = Color.White,
    onSurface = Brown,
    outline = BrownMuted
)

private val navItems = listOf(
    NavItem("Beranda", R.drawable.ic_beranda),
    NavItem("Inventori", R.drawable.ic_inventori),
    NavItem("Pesanan", R.drawable.ic_pesanan),
    NavItem("Box", R.drawable.ic_box),
    NavItem("Lainnya", R.drawable.ic_menu)
)

@Composable
fun BerandaScreen(
    greeting: String = "Selamat pagi, Ratna",
    dateLabel: String = "OWNER · SENIN, 9 NOVEMBER",
    pendingOrderText: String = "1 pesanan menunggu konfirmasi Anda",
    receivedPercent: Int = 42,
    receivedAmount: String = "Rp6.500.000",
    totalOrderText: String = "3 pesanan · total Rp15.500.000",
    actionCount: Int = 2,
    approvalDetail: String = "Nadia & Rizky · WA-26041",
    approvalBadge: String = "1 pesanan",
    paymentTitle: String = "Pelunasan Ayu & Fajar · Rp3.000.000",
    paymentDetail: String = "Jatuh tempo 7 Nov · Buka Pembayaran",
    preparingCount: Int = 2,
    returnCount: Int = 1,
    activeCount: Int = 3,
    onNotificationClick: () -> Unit = {},
    onPendingOrderClick: () -> Unit = {},
    onApprovalClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onNavSelected: (Int) -> Unit = {},
    onSummaryClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    MaterialTheme(colorScheme = AtelierColors) {
        var selectedNav by remember { mutableIntStateOf(0) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Canvas)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // ---------- KONTEN (bisa digulir) ----------
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 32.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rum Attire · Owner",
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_notifikasi),
                            contentDescription = "Notifikasi",
                            tint = Brown,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Kartu sapaan
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brown, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateLabel,
                            modifier = Modifier.weight(1f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                        Text(
                            text = "R / A",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 19.sp,
                            color = Gold
                        )
                    }

                    Text(
                        text = greeting,
                        modifier = Modifier.padding(top = 4.dp),
                        fontSize = 27.sp,
                        fontFamily = FontFamily.Serif,
                        color = Cream
                    )

                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Gold.copy(alpha = 0.33f))
                    )

                    // Chip pesanan menunggu
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(50))
                            .clickable(onClick = onPendingOrderClick)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(Orange, CircleShape)
                        )
                        Text(
                            text = pendingOrderText,
                            modifier = Modifier
                                .padding(start = 7.dp, end = 12.dp),
                            fontSize = 11.sp,
                            color = ChipText
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_chevron_right),
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Kartu penerimaan pesanan
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Penerimaan pesanan aktif",
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            color = BrownMuted
                        )
                        Text(
                            text = "$receivedPercent% diterima",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Green
                        )
                    }

                    Text(
                        text = receivedAmount,
                        modifier = Modifier.padding(top = 6.dp),
                        fontSize = 30.sp,
                        color = Brown
                    )

                    Text(
                        text = totalOrderText,
                        modifier = Modifier.padding(top = 2.dp),
                        fontSize = 12.sp,
                        color = BrownMuted
                    )

                    LinearProgressIndicator(
                        progress = { receivedPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .height(6.dp),
                        color = Green,
                        trackColor = CardBorder,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }

                // Judul tindakan
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Perlu tindakan Anda",
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )
                    Text(
                        text = "$actionCount item",
                        fontSize = 12.sp,
                        color = BrownMuted
                    )
                }

                // Persetujuan pesanan
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 72.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable(onClick = onApprovalClick)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(OrangeSoft, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_persetujuan),
                            contentDescription = null,
                            tint = Orange,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp, end = 8.dp)
                    ) {
                        Text(
                            text = "Persetujuan pesanan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brown
                        )
                        Text(
                            text = approvalDetail,
                            modifier = Modifier.padding(top = 4.dp),
                            fontSize = 11.sp,
                            color = BrownMuted
                        )
                    }
                    Text(
                        text = approvalBadge,
                        modifier = Modifier
                            .background(OrangeSoft, RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Orange
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = BrownMuted,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(18.dp)
                    )
                }

                // Pengingat pelunasan
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .heightIn(min = 64.dp)
                        .background(Color(0xFFFFF4DC), RoundedCornerShape(16.dp))
                        .clickable(onClick = onPaymentClick)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFFFE9B0), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_pembayaran),
                            contentDescription = null,
                            tint = Color(0xFF684500),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp, end = 8.dp)
                    ) {
                        Text(
                            text = paymentTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF684500)
                        )
                        Text(
                            text = paymentDetail,
                            modifier = Modifier.padding(top = 4.dp),
                            fontSize = 11.sp,
                            color = Color(0xFFA47724)
                        )
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = Color(0xFFA66D00),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Hari ini di atelier",
                    modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brown
                )

                // Ringkasan hari ini
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        count = preparingCount,
                        label = "Persiapan",
                        modifier = Modifier.weight(1f).clickable { onSummaryClick(0) }
                    )
                    SummaryCard(
                        count = returnCount,
                        label = "Pengembalian",
                        modifier = Modifier.weight(1f).clickable { onSummaryClick(1) }
                    )
                    SummaryCard(
                        count = activeCount,
                        label = "Pesanan aktif",
                        modifier = Modifier.weight(1f).clickable { onSummaryClick(2) }
                    )
                }
            }

            // ---------- NAVIGASI BAWAH ----------
            NavigationBar(
                containerColor = NavBackground,
                tonalElevation = 0.dp,
                modifier = Modifier.background(NavBackground)
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedNav == index,
                        onClick = {
                            selectedNav = index
                            onNavSelected(index)
                        },
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (selectedNav == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrownNav,
                            selectedTextColor = BrownNav,
                            unselectedIconColor = BrownSoft,
                            unselectedTextColor = BrownSoft,
                            indicatorColor = Color(0xFFEFE4D6)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    count: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .height(84.dp)
    ) {
        Text(
            text = count.toString(),
            fontSize = 26.sp,
            color = Brown
        )
        Text(
            text = label,
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 11.sp,
            color = BrownMuted
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun BerandaScreenPreview() {
    BerandaScreen()
}