package com.example.rumattire

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rumattire.R

// ---------- Palet warna (sama dengan layar lain) ----------
private val Canvas = Color(0xFFF7F3EB)
private val NavBackground = Color(0xFFFAF7F1)
private val NavBorder = Color(0xFFE7D8C6)
private val Brown = Color(0xFF3E2B23)
private val BrownIndicator = Color(0xFF4B3429)
private val BrownNav = Color(0xFF49352A)
private val BrownMuted = Color(0xFF988478)
private val BrownSoft = Color(0xFF786657)
private val BrownHint = Color(0xFF988478)
private val Gold = Color(0xFFC4A46A)
private val Orange = Color(0xFFD64E09)
private val OrangeSoft = Color(0xFFFCE9DC)
private val OrangeDot = Color(0xFFE96728)
private val Green = Color(0xFF008C62)
private val Blue = Color(0xFF265DF2)
private val CardDivider = Color(0xFFF0E5D8)
private val ProgressTrack = Color(0xFFEADFCF)
private val IconBackground = Color(0xFFF5EDE2)
private val ChipInactiveBorder = Color(0xFFE4D6C4)
private val PendingColor = Color(0xFFB7791F)

private val AtelierColors = lightColorScheme(
    primary = Brown,
    onPrimary = Color.White,
    secondary = Gold,
    background = Canvas,
    onBackground = Brown,
    surface = Color.White,
    onSurface = Brown,
    outline = BrownMuted
)

private data class PesananNavItem(val label: String, val icon: Int)

private val navItems = listOf(
    PesananNavItem("Beranda", R.drawable.ic_inv_beranda),
    PesananNavItem("Inventori", R.drawable.ic_inv_baju),
    PesananNavItem("Pesanan", R.drawable.ic_inv_pesanan),
    PesananNavItem("Box", R.drawable.ic_inv_box),
    PesananNavItem("Lainnya", R.drawable.ic_inv_menu)
)

data class OrderItem(
    val id: String,
    val customer: String,
    val detail: String,
    val eventDate: String,
    val pickupDate: String,
    val validationText: String,
    val validationCount: Int,
    val validationColor: Color,
    val status: String,
    val statusColor: Color,
    val statusIcon: Int,
    val progress: Float,
    val progressColor: Color,
    val filterKey: String
)

internal val sampleOrders = listOf(
    OrderItem(
        id = "WA-26041",
        customer = "Nadia & Rizky",
        detail = "#WA-26041 · Sewa · Paket Akad Ivory",
        eventDate = "5 Nov 2026",
        pickupDate = "4 Nov · 16.00 WIB",
        validationText = "4 dari 4 ada",
        validationCount = 4,
        validationColor = Green,
        status = "Menunggu konfirmasi",
        statusColor = PendingColor,
        statusIcon = R.drawable.ic_order_clock,
        progress = 4f / 4f,
        progressColor = Green,
        filterKey = "Menunggu persiapan"
    ),
    OrderItem(
        id = "WA-26042",
        customer = "Ayu & Fajar",
        detail = "#WA-26042 · Sewa · Paket Resepsi",
        eventDate = "6 Nov 2026",
        pickupDate = "5 Nov · 10.00 WIB",
        validationText = "2 dari 4 dicek",
        validationCount = 2,
        validationColor = Blue,
        status = "Sedang diperiksa",
        statusColor = Blue,
        statusIcon = R.drawable.ic_order_clock,
        progress = 2f / 4f,
        progressColor = Blue,
        filterKey = "Sedang diperiksa"
    )
)

private val filters = listOf("Semua", "Menunggu persiapan", "Sedang diperiksa")

@Composable
fun PesananScreen(
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCreateOrderClick: () -> Unit = {},
    onOrderClick: (String) -> Unit = {},
    onNavSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    orders: List<OrderItem> = sampleOrders
) {
    MaterialTheme(colorScheme = AtelierColors) {
        var query by rememberSaveable { mutableStateOf("") }
        var selectedFilter by rememberSaveable { mutableStateOf("Semua") }
        var selectedNav by remember { mutableIntStateOf(2) }

        val filtered = orders.filter { order ->
            (selectedFilter == "Semua" || order.filterKey == selectedFilter) &&
                    (query.isBlank() ||
                            order.customer.contains(query, ignoreCase = true) ||
                            order.id.contains(query, ignoreCase = true))
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Canvas)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // ---------- HEADER ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 8.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_inv_back),
                        contentDescription = "Kembali",
                        tint = Brown,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Order Management",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brown
                )
                Box(modifier = Modifier.size(48.dp)) {
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_inv_notifikasi),
                            contentDescription = "Notifikasi baru",
                            tint = Brown,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 10.dp)
                            .size(8.dp)
                            .background(OrangeDot, CircleShape)
                    )
                }
            }

            // ---------- KONTEN (bisa digulir) ----------
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                // Judul & jumlah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pesanan aktif",
                        modifier = Modifier.weight(1f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )
                    Text(
                        text = "${filtered.size} pesanan",
                        fontSize = 12.sp,
                        color = BrownMuted
                    )
                }

                // Pencarian
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_inv_search),
                        contentDescription = null,
                        tint = BrownHint,
                        modifier = Modifier.size(18.dp)
                    )
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp, color = Brown),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .weight(1f),
                        decorationBox = { innerTextField ->
                            if (query.isEmpty()) {
                                Text(
                                    text = "Cari pelanggan atau ID pesanan",
                                    fontSize = 14.sp,
                                    color = BrownHint
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                // Filter (bisa digeser ke samping)
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (filters + orders.map { it.filterKey }).distinct().forEach { filter ->
                        val selected = filter == selectedFilter
                        Text(
                            text = filter,
                            modifier = Modifier
                                .height(36.dp)
                                .then(
                                    if (selected) {
                                        Modifier.background(Brown, RoundedCornerShape(50))
                                    } else {
                                        Modifier.border(
                                            1.dp,
                                            ChipInactiveBorder,
                                            RoundedCornerShape(50)
                                        )
                                    }
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Color.White else BrownSoft
                        )
                    }
                }

                // Daftar pesanan
                filtered.forEachIndexed { index, order ->
                    OrderCard(
                        order = order,
                        onClick = { onOrderClick(order.id) },
                        modifier = Modifier.padding(top = if (index == 0) 12.dp else 16.dp)
                    )
                }

                if (filtered.isEmpty()) {
                    Text(
                        text = "Tidak ada pesanan yang cocok.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        fontSize = 13.sp,
                        color = BrownMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // ---------- TOMBOL BUAT PESANAN (tetap di atas navbar) ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Canvas)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Brown, RoundedCornerShape(16.dp))
                        .clickable(onClick = onCreateOrderClick),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_inv_plus),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Buat pesanan baru",
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
private fun OrderCard(
    order: OrderItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, ChipInactiveBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Ikon dan status
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(IconBackground, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_inv_pesanan),
                    contentDescription = null,
                    tint = BrownIndicator,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .background(order.statusColor, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(order.statusIcon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = order.status,
                    modifier = Modifier.padding(start = 6.dp),
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }

        Text(
            text = order.customer,
            modifier = Modifier.padding(top = 14.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Brown
        )
        Text(
            text = order.detail,
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 12.sp,
            color = BrownMuted
        )

        Box(
            modifier = Modifier
                .padding(top = 14.dp, bottom = 12.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(CardDivider)
        )

        OrderInfoRow("Acara", order.eventDate, bold = true)
        OrderInfoRow("Ambil", order.pickupDate, bold = true, modifier = Modifier.padding(top = 8.dp))

        Row(
            modifier = Modifier
                .padding(top = 18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Validasi barang",
                modifier = Modifier.weight(1f),
                fontSize = 12.sp,
                color = BrownMuted
            )
            Text(
                text = order.validationText,
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = order.validationColor
            )
        }

        LinearProgressIndicator(
            progress = { order.progress },
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(6.dp),
            color = order.progressColor,
            trackColor = ProgressTrack,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun OrderInfoRow(
    label: String,
    value: String,
    bold: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.width(68.dp),
            fontSize = 12.sp,
            color = BrownMuted
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = Brown
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun PesananScreenPreview() {
    PesananScreen()
}