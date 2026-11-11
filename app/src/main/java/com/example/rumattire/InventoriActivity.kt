package com.example.rumattire

import androidx.compose.foundation.background
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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rumattire.R

// ---------- Palet warna (sama dengan layar lain) ----------
private val Canvas = Color(0xFFF7F3EB)
private val NavBackground = Color(0xFFFAF7F1)
private val Brown = Color(0xFF3E2B23)
private val BrownNav = Color(0xFF49352A)
private val BrownMuted = Color(0xFF988478)
private val BrownSoft = Color(0xFF786657)
private val BrownHint = Color(0xFFAC998C)
private val Gold = Color(0xFFC4A46A)
private val Green = Color(0xFF008C62)
private val Orange = Color(0xFFD64E09)
private val OrangeSoft = Color(0xFFFCE9DC)
private val GreenSoft = Color(0xFFE3F4EE)
private val CardDivider = Color(0xFFF0E5D8)

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

private val navItems = listOf(
    NavItem("Beranda", R.drawable.ic_inv_beranda),
    NavItem("Inventori", R.drawable.ic_inv_baju),
    NavItem("Pesanan", R.drawable.ic_inv_pesanan),
    NavItem("Box", R.drawable.ic_inv_box),
    NavItem("Lainnya", R.drawable.ic_inv_menu)
)

data class InventoriItem(
    val name: String,
    val stock: String,
    val sku: String,
    val size: String,
    val category: String,
    val boxCode: String,
    val status: String,
    val statusBooked: Boolean,
    val icon: Int
)

internal val sampleItems = listOf(
    InventoriItem(
        name = "Kebaya Ivory Bordir",
        stock = "Stok: 1 unit",
        sku = "KB-014",
        size = "M · Ivory",
        category = "Kebaya",
        boxCode = "BX-028",
        status = "Dipesan 4–7 Nov",
        statusBooked = true,
        icon = R.drawable.ic_inv_baju
    ),
    InventoriItem(
        name = "Beskap Cokelat Klasik",
        stock = "Stok: 1 unit",
        sku = "BS-021",
        size = "L · Cokelat",
        category = "Beskap",
        boxCode = "BX-031",
        status = "Tersedia",
        statusBooked = false,
        icon = R.drawable.ic_inv_beskap
    ),
    InventoriItem(
        name = "Veil Tulle Panjang",
        stock = "Stok: 2 unit",
        sku = "VL-003",
        size = "All size · Putih",
        category = "Veil",
        boxCode = "BX-012",
        status = "Tersedia",
        statusBooked = false,
        icon = R.drawable.ic_inv_veil
    )
)

private val categories = listOf("Semua", "Kebaya", "Beskap", "Kain", "Veil")

@Composable
fun InventoriScreen(
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAddItemClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
    onNavSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    items: List<InventoriItem> = sampleItems
) {
    MaterialTheme(colorScheme = AtelierColors) {
        var query by rememberSaveable { mutableStateOf("") }
        var selectedCategory by rememberSaveable { mutableStateOf("Semua") }
        var selectedNav by remember { mutableIntStateOf(1) }

        val filtered = items.filter { item ->
            (selectedCategory == "Semua" || item.category == selectedCategory) &&
                    (query.isBlank() ||
                            item.name.contains(query, ignoreCase = true) ||
                            item.sku.contains(query, ignoreCase = true) ||
                            item.size.contains(query, ignoreCase = true))
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
                    text = "Inventory Management",
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
                    .padding(bottom = 32.dp)
            ) {
                // Judul & jumlah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Koleksi atelier",
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )
                    Text(
                        text = "${filtered.size} item · ${selectedCategory}",
                        fontSize = 12.sp,
                        color = BrownMuted
                    )
                }

                // Pencarian
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp),
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
                        textStyle = TextStyle(fontSize = 13.sp, color = Brown),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                        decorationBox = { innerTextField ->
                            if (query.isEmpty()) {
                                Text(
                                    text = "Cari nama, SKU, atau ukuran",
                                    fontSize = 13.sp,
                                    color = BrownHint
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                // Filter kategori (bisa digeser ke samping)
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (categories + items.map { it.category }).distinct().forEach { category ->
                        val selected = category == selectedCategory
                        Text(
                            text = category,
                            modifier = Modifier
                                .height(32.dp)
                                .background(
                                    if (selected) Brown else Color.Transparent,
                                    RoundedCornerShape(50)
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Color.White else BrownSoft
                        )
                    }
                }

                // Filter box dan tanggal (tampilan saja)
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        icon = R.drawable.ic_inv_storage,
                        label = "Box: Semua",
                        modifier = Modifier.weight(0.9f)
                    )
                    FilterChip(
                        icon = R.drawable.ic_inv_calendar,
                        label = "Tanggal: Semua",
                        modifier = Modifier.weight(1.1f)
                    )
                }

                // Daftar item
                filtered.forEachIndexed { index, item ->
                    InventoriCard(
                        item = item,
                        onClick = { onItemClick(item.sku) },
                        modifier = Modifier.padding(top = if (index == 0) 18.dp else 16.dp)
                    )
                }

                if (filtered.isEmpty()) {
                    Text(
                        text = "Tidak ada item yang cocok.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        fontSize = 13.sp,
                        color = BrownMuted,
                        textAlign = TextAlign.Center
                    )
                }

                // Tombol tambah item
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Brown, RoundedCornerShape(16.dp))
                        .clickable(onClick = onAddItemClick),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_inv_plus),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Tambah item inventori",
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
private fun FilterChip(
    icon: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(36.dp)
            .background(Color.White, RoundedCornerShape(50))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = BrownSoft,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp),
            fontSize = 12.sp,
            color = BrownSoft
        )
        Icon(
            painter = painterResource(R.drawable.ic_inv_down),
            contentDescription = null,
            tint = BrownSoft,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun InventoriCard(
    item: InventoriItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF5EDE2), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = null,
                    tint = BrownSoft,
                    modifier = Modifier.size(22.dp)
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
                    color = Brown
                )
                Text(
                    text = item.stock,
                    modifier = Modifier.padding(top = 3.dp),
                    fontSize = 11.sp,
                    color = BrownMuted
                )
            }
            Text(
                text = item.status,
                modifier = Modifier
                    .background(
                        if (item.statusBooked) OrangeSoft else GreenSoft,
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.statusBooked) Orange else Green
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 10.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(CardDivider)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                DetailLabel("SKU")
                DetailValue(item.sku)
                DetailLabel("Ukuran", modifier = Modifier.padding(top = 10.dp))
                DetailValue(item.size)
            }
            Column(modifier = Modifier.weight(1f)) {
                DetailLabel("Kategori")
                DetailValue(item.category)
                DetailLabel("Box penyimpanan", modifier = Modifier.padding(top = 10.dp))
                Row(
                    modifier = Modifier.padding(top = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_inv_storage),
                        contentDescription = null,
                        tint = Brown,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = item.boxCode,
                        modifier = Modifier.padding(start = 4.dp),
                        fontSize = 12.sp,
                        color = Brown
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 10.sp,
        color = BrownMuted
    )
}

@Composable
private fun DetailValue(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 3.dp),
        fontSize = 12.sp,
        color = Brown
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun InventoriScreenPreview() {
    InventoriScreen()
}