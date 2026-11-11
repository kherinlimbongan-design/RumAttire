package com.example.rumattire

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Palet warna (dari XML detail box) ----------
private val Canvas = Color(0xFFF4EFE7)
private val HeaderBackground = Color(0xFFFAF6EF)
private val FooterBackground = Color(0xFFF4EFE7)
private val FooterBorder = Color(0xFFE5DACA)
private val NavBackground = Color(0xFFFAF7F1)
private val NavBorder = Color(0xFFE7D8C6)
private val Ink = Color(0xFF30251F)
private val InkSecondary = Color(0xFF857568)
private val BrownIndicator = Color(0xFF4B3429)
private val BrownSoft = Color(0xFF786657)
private val BrownMuted = Color(0xFF988478)
private val BrownQr = Color(0xFF918174)
private val Orange = Color(0xFFC85A09)
private val OrangeText = Color(0xFFC95A08)
private val Green = Color(0xFF12833D)
private val GreenSoft = Color(0xFFE3F4EE)
private val IconBackground = Color(0xFFF5EDE2)
private val CardBorder = Color(0xFFE7D8C6)
private val RowDivider = Color(0xFFEEE3D5)
private val BookedBackground = Color(0xFFFCE9DC)

private val AtelierColors = lightColorScheme(
    primary = Ink,
    onPrimary = Color.White,
    secondary = Color(0xFFC4A46A),
    background = Canvas,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    outline = BrownMuted,
)

private val navItems = listOf(
    NavItem("Beranda", R.drawable.ic_inv_beranda),
    NavItem("Inventori", R.drawable.ic_inv_baju),
    NavItem("Pesanan", R.drawable.ic_inv_pesanan),
    NavItem("Box", R.drawable.ic_inv_box),
    NavItem("Lainnya", R.drawable.ic_inv_menu),
)

private data class BoxContentItem(
    val name: String,
    val info: String,
    val status: String,
    val booked: Boolean,
)

private val boxContents = listOf(
    BoxContentItem("Kebaya Ivory Bordir", "KB-014 · M · Ivory", "Dipesan", booked = true),
    BoxContentItem("Kebaya Brokat Rose", "KB-016 · S · Rose", "Tersedia", booked = false),
    BoxContentItem("Kebaya Modern Satin", "KB-019 · L · Putih tulang", "Tersedia", booked = false),
    BoxContentItem("Kebaya Songket Emas", "KB-022 · M · Emas", "Tersedia", booked = false),
    BoxContentItem("Kebaya Payet Klasik", "KB-025 · M · Ivory", "Tersedia", booked = false),
)

@Composable
fun DetailBoxScreen(
    modifier: Modifier = Modifier,
    boxCode: String = "BX-003",
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onPrintQrClick: () -> Unit = {},
    onManageContentClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
    onNavSelected: (Int) -> Unit = {},
    box: BoxItem? = null,
    items: List<InventoryItemDraft>? = null,
) {
    MaterialTheme(colorScheme = AtelierColors) {
        var selectedNav by remember { mutableIntStateOf(3) }
        val contents = items?.map {
            val appearance = inventoryListItem(it)
            BoxContentItem(it.name, "${it.sku} · ${it.size} · ${it.color}", appearance.status, appearance.statusBooked)
        } ?: boxContents

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Canvas)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            // ---------- HEADER ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(HeaderBackground)
                    .padding(start = 8.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_inv_back),
                        contentDescription = "Kembali",
                        tint = Ink,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Box Detail · $boxCode",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Box(modifier = Modifier.size(48.dp)) {
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_inv_notifikasi),
                            contentDescription = "Notifikasi baru",
                            tint = Ink,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 10.dp)
                            .size(8.dp)
                            .background(Orange, CircleShape)
                    )
                }
            }

            // ---------- KONTEN (bisa digulir) ----------
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                // Ringkasan box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(IconBackground, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_inv_box),
                                    contentDescription = null,
                                    tint = Ink,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Text(
                                text = box?.name ?: "Box $boxCode",
                                modifier = Modifier.padding(top = 12.dp),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Ink
                            )
                            Text(
                                text = box?.location ?: "Rak A-03 · Gudang atelier",
                                modifier = Modifier.padding(top = 4.dp),
                                fontSize = 13.sp,
                                color = InkSecondary
                            )
                        }

                        // QR box
                        Column(
                            modifier = Modifier.width(80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val qr = remember(boxCode) { boxQrBitmap(boxCode).asImageBitmap() }
                            Image(
                                bitmap = qr,
                                contentDescription = "Kode QR box $boxCode",
                                modifier = Modifier.size(80.dp)
                                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                    .padding(3.dp)
                            )
                            Text(
                                text = boxCode,
                                modifier = Modifier.padding(top = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrownQr
                            )
                        }
                    }

                    // Jumlah, kategori, tanggal
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SummaryColumn(
                            label = "Jumlah barang",
                            value = "${contents.size} item",
                            modifier = Modifier.weight(1f)
                        )
                        SummaryColumn(
                            label = "Kategori",
                            value = items?.map { it.category }?.distinct()?.joinToString(", ")?.ifBlank { "—" } ?: "Kebaya",
                            modifier = Modifier.weight(0.9f)
                        )
                        SummaryColumn(
                            label = "Diperbarui",
                            value = if (box == null) "2 Nov 2026" else "Sesi ini",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Daftar isi box
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Isi box",
                            modifier = Modifier.weight(1f),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                        Text(
                            text = "${contents.size} item · ${contents.count { it.booked }} dipesan",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 13.sp,
                            color = InkSecondary
                        )
                    }

                    if (contents.isEmpty()) Text("Belum ada barang dalam box ini.")
                    contents.forEachIndexed { index, item ->
                        if (index > 0) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 48.dp, top = 8.dp, bottom = 8.dp)
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(RowDivider)
                            )
                        }
                        BoxContentRow(
                            item = item,
                            modifier = Modifier.padding(top = if (index == 0) 18.dp else 0.dp),
                            onClick = { onItemClick(item.info.substringBefore(" ")) }
                        )
                    }
                }
            }

            // ---------- FOOTER AKSI (tetap di atas navbar) ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FooterBackground)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(FooterBorder)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 14.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cetak label QR",
                        modifier = Modifier
                            .weight(0.9f)
                            .heightIn(min = 48.dp)
                            .border(1.dp, BrownIndicator, RoundedCornerShape(16.dp))
                            .clickable(onClick = onPrintQrClick)
                            .padding(horizontal = 10.dp, vertical = 14.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrownIndicator,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier
                            .weight(1.1f)
                            .heightIn(min = 48.dp)
                            .background(Ink, RoundedCornerShape(16.dp))
                            .clickable(onClick = onManageContentClick)
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_detail_box_plus),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "Kelola isi box",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // ---------- NAVIGASI BAWAH ----------
            NavBar(selectedIndex = selectedNav) { index ->
                selectedNav = index
                onNavSelected(index)
            }
        }
    }
}

@Composable
private fun SummaryColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = BrownMuted
        )
        Text(
            text = value,
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Ink
        )
    }
}

@Composable
private fun BoxContentRow(
    item: BoxContentItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(IconBackground, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_inv_baju),
                contentDescription = null,
                tint = Ink,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 8.dp)
        ) {
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = item.info,
                modifier = Modifier.padding(top = 3.dp),
                fontSize = 12.sp,
                color = BrownMuted
            )
        }
        Text(
            text = item.status,
            modifier = Modifier
                .width(72.dp)
                .background(
                    if (item.booked) BookedBackground else GreenSoft,
                    RoundedCornerShape(50)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (item.booked) OrangeText else Green,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NavBar(selectedIndex: Int, onSelected: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(NavBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(NavBorder)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .height(64.dp)
                        .clickable { onSelected(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.label,
                        tint = if (selected) BrownIndicator else BrownSoft,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = item.label,
                        modifier = Modifier.padding(top = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) BrownIndicator else BrownSoft
                    )
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(width = 20.dp, height = 3.dp)
                                .background(BrownIndicator)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun DetailBoxScreenPreview() {
    DetailBoxScreen()
}
