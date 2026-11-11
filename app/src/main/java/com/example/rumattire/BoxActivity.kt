package com.example.rumattire

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.layout.heightIn

// ---------- Palet warna (sama dengan layar lain) ----------
private val Canvas = Color(0xFFF7F3EB)
private val NavBackground = Color(0xFFFAF7F1)
private val Brown = Color(0xFF3E2B23)
private val BrownIndicator = Color(0xFF4B3429)
private val BrownNav = Color(0xFF49352A)
private val BrownSoft = Color(0xFF786657)
private val BrownMuted = Color(0xFF988478)
private val BrownHint = Color(0xFFAC998C)
private val IconBackground = Color(0xFFF5EDE2)
private val CardDivider = Color(0xFFF0E5D8)
private val CardBorder = Color(0xFFE7D8C6)
private val NavIndicatorColor = Color(0xFFEFE4D6)

private val AtelierColors = lightColorScheme(
    primary = Brown,
    onPrimary = Color.White,
    secondary = Color(0xFFC4A46A),
    background = Canvas,
    onBackground = Brown,
    surface = Color.White,
    onSurface = Brown,
    outline = BrownMuted,
)

private val navItems = listOf(
    NavItem("Beranda", R.drawable.ic_inv_beranda),
    NavItem("Inventori", R.drawable.ic_inv_baju),
    NavItem("Pesanan", R.drawable.ic_inv_pesanan),
    NavItem("Box", R.drawable.ic_inv_box),
    NavItem("Lainnya", R.drawable.ic_inv_menu),
)

data class BoxItem(
    val code: String,
    val name: String,
    val location: String,
    val itemCount: String,
    val contents: String
)

internal val boxItems = listOf(
    BoxItem(
        code = "BX-012",
        name = "Box BX-012",
        location = "Lemari B · Rak 1",
        itemCount = "5 item",
        contents = "Isi: Veil, tiara, sarung tangan"
    ),
    BoxItem(
        code = "BX-019",
        name = "Box BX-019",
        location = "Lemari A · Rak 3",
        itemCount = "8 item",
        contents = "Isi: Cover garment, hanger, jarik"
    ),
    BoxItem(
        code = "BX-028",
        name = "Box BX-028",
        location = "Lemari A · Rak 2",
        itemCount = "6 item",
        contents = "Isi: Kebaya, kain batik, kain songket"
    ),
    BoxItem(
        code = "BX-031",
        name = "Box BX-031",
        location = "Rak C · Lantai 2",
        itemCount = "4 item",
        contents = "Isi: Beskap, blangkon, keris"
    )
)

@Composable
fun BoxManagementScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onBoxClick: (String) -> Unit = {},
    onAddBoxClick: () -> Unit = {},
    onScanQrClick: () -> Unit = {},
    onNavSelected: (Int) -> Unit = {},
    boxes: List<BoxItem> = boxItems,
) {
    MaterialTheme(colorScheme = AtelierColors) {
        var query by rememberSaveable { mutableStateOf("") }
        var selectedNav by remember { mutableIntStateOf(3) }

        val filtered = boxes.filter { box ->
            query.isBlank() ||
                    box.code.contains(query, ignoreCase = true) ||
                    box.location.contains(query, ignoreCase = true)
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
                    text = "Box Management",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brown
                )
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_inv_notifikasi),
                        contentDescription = "Notifikasi",
                        tint = Brown,
                        modifier = Modifier.size(24.dp)
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
                    .padding(top = 4.dp, bottom = 24.dp)
            ) {
                // Judul daftar box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Box penyimpanan",
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )
                    Text(
                        text = "${boxItems.size} box",
                        fontSize = 12.sp,
                        color = BrownMuted
                    )
                }

                // Pencarian
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(start = 14.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_box_search),
                        contentDescription = null,
                        tint = BrownMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 13.sp, color = Brown),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                        decorationBox = { innerTextField ->
                            if (query.isEmpty()) {
                                Text(
                                    text = "Cari ID box atau lokasi",
                                    fontSize = 13.sp,
                                    color = BrownHint
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                // Kartu box
                filtered.forEach { box ->
                    BoxCard(
                        box = box,
                        onClick = { onBoxClick(box.code) },
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                if (filtered.isEmpty()) {
                    Text(
                        text = "Box tidak ditemukan.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        fontSize = 13.sp,
                        color = BrownMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Tombol aksi
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tambah box",
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .border(1.dp, BrownIndicator, RoundedCornerShape(16.dp))
                            .clickable(onClick = onAddBoxClick)
                            .padding(vertical = 14.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrownIndicator,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "Scan QR box",
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .background(Brown, RoundedCornerShape(16.dp))
                            .clickable(onClick = onScanQrClick)
                            .padding(vertical = 14.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                            indicatorColor = NavIndicatorColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxCard(
    box: BoxItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
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
                    painter = painterResource(R.drawable.ic_inv_box),
                    contentDescription = null,
                    tint = BrownIndicator,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = box.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brown
                )
                Text(
                    text = box.location,
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 11.sp,
                    color = BrownMuted
                )
            }
            Text(
                text = box.itemCount,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(IconBackground, RoundedCornerShape(50))
                    .padding(horizontal = 14.dp, vertical = 5.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BrownSoft
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 10.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(CardDivider)
        )

        Text(
            text = box.contents,
            fontSize = 12.sp,
            color = BrownMuted
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun BoxManagementScreenPreview() {
    BoxManagementScreen()
}
