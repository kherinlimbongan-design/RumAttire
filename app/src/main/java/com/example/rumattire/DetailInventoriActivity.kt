package com.example.rumattire

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rumattire.R

// ---------- Palet warna (sama dengan layar lain) ----------
private val Canvas = Color(0xFFF7F3EB)
private val FooterBackground = Color(0xFFFAF7F1)
private val FooterBorder = Color(0xFFE7D8C6)
private val Brown = Color(0xFF3E2B23)
private val BrownIndicator = Color(0xFF4B3429)
private val BrownMuted = Color(0xFF988478)
private val BrownSoft = Color(0xFF786657)
private val Gold = Color(0xFFC4A46A)
private val Orange = Color(0xFFD64E09)
private val OrangeSoft = Color(0xFFFCE9DC)
private val OrangeDot = Color(0xFFE96728)
private val Green = Color(0xFF008C62)
private val TimelineLine = Color(0xFFE7D8C6)
private val IconBackground = Color(0xFFF5EDE2)

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

@Composable
fun DetailInventoriScreen(
    itemCode: String = "KB-014",
    onBackClick: () -> Unit = {},
    onMoveBoxClick: () -> Unit = {},
    onEditItemClick: () -> Unit = {},
    onViewBoxClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    item: InventoryItemDraft = SampleItem,
    history: List<String>? = null
) {
    MaterialTheme(colorScheme = AtelierColors) {
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
                    .padding(start = 8.dp, end = 16.dp),
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
                    text = "Detail Item · $itemCode",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brown
                )
            }

            // ---------- KONTEN (bisa digulir) ----------
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp, bottom = 16.dp)
            ) {
                // Kartu utama item
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, FooterBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    // Ilustrasi
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(IconBackground, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(inventoryListItem(item).icon),
                            contentDescription = "Ilustrasi item",
                            tint = BrownSoft,
                            modifier = Modifier.size(72.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brown
                            )
                            Text(
                                text = "$itemCode · ${item.category}",
                                modifier = Modifier.padding(top = 4.dp),
                                fontSize = 12.sp,
                                color = BrownMuted
                            )
                        }
                        Text(
                            text = inventoryListItem(item).status,
                            modifier = Modifier
                                .background(OrangeSoft, RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Orange
                        )
                    }
                }

                // Info lengkap
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Info lengkap",
                        modifier = Modifier.padding(bottom = 12.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )
                    InfoRow("SKU", itemCode)
                    InfoRow("Kategori", item.category, modifier = Modifier.padding(top = 12.dp))
                    InfoRow("Ukuran", item.size, modifier = Modifier.padding(top = 12.dp))
                    InfoRow("Warna", item.color, modifier = Modifier.padding(top = 12.dp))
                    InfoRow("Stok", "${item.stock} unit", modifier = Modifier.padding(top = 12.dp))
                }

                // Box penyimpanan
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Box penyimpanan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
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
                                .padding(start = 12.dp, end = 8.dp)
                        ) {
                            Text(
                                text = item.box.id,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brown
                            )
                            Text(
                                text = item.box.location,
                                modifier = Modifier.padding(top = 4.dp),
                                fontSize = 11.sp,
                                color = BrownMuted
                            )
                        }
                        Text(
                            text = "Lihat box",
                            modifier = Modifier
                                .border(1.dp, BrownIndicator, RoundedCornerShape(50))
                                .clickable(onClick = onViewBoxClick)
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrownIndicator
                        )
                    }
                }

                // Riwayat item
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
                            text = "Riwayat item",
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brown
                        )
                        Text(
                            text = if (history == null) "4 aktivitas" else "${history.size} aktivitas",
                            fontSize = 12.sp,
                            color = BrownMuted
                        )
                    }

                    Column(modifier = Modifier.padding(top = 20.dp)) {
                        if (history != null) {
                            if (history.isEmpty()) Text("Belum ada perpindahan box pada sesi ini.")
                            history.forEachIndexed { index, detail ->
                                HistoryItem("Perpindahan box", detail, OrangeDot, null, index < history.lastIndex)
                            }
                        } else {
                        HistoryItem(
                            title = "Dipesan · WA-26041",
                            detail = "3 Nov · 14.20 · Nadia & Rizky, 4–7 Nov",
                            dotColor = OrangeDot,
                            icon = null,
                            showLine = true
                        )
                        HistoryItem(
                            title = "Dikembalikan · WA-26031",
                            detail = "28 Okt · 16.40 · Diperiksa Sari, kondisi baik",
                            dotColor = Green,
                            icon = R.drawable.ic_detail_check,
                            showLine = true
                        )
                        HistoryItem(
                            title = "Dipindah ke BX-028",
                            detail = "20 Okt · 10.05 · Dari BX-019 oleh Ratna",
                            dotColor = Color.White,
                            icon = R.drawable.ic_detail_arrow_right,
                            showLine = true,
                            iconTint = BrownIndicator,
                            borderColor = TimelineLine
                        )
                        HistoryItem(
                            title = "Item ditambahkan",
                            detail = "12 Agu · 09.30 · Ratna, box BX-019",
                            dotColor = Color.White,
                            icon = R.drawable.ic_inv_plus,
                            showLine = false,
                            iconTint = BrownIndicator,
                            borderColor = TimelineLine
                        )
                        }
                    }
                }
            }

            // ---------- FOOTER (tetap terlihat) ----------
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
                Text(
                    text = "Perpindahan box tercatat di riwayat item",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                    fontSize = 11.sp,
                    color = BrownMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Pindah box",
                        modifier = Modifier
                            .weight(0.9f)
                            .height(48.dp)
                            .border(1.dp, BrownIndicator, RoundedCornerShape(12.dp))
                            .clickable(onClick = onMoveBoxClick)
                            .padding(vertical = 14.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrownIndicator,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "Edit item",
                        modifier = Modifier
                            .weight(1.1f)
                            .height(48.dp)
                            .background(Brown, RoundedCornerShape(12.dp))
                            .clickable(onClick = onEditItemClick)
                            .padding(vertical = 14.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            color = BrownMuted
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            color = Brown,
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
private fun HistoryItem(
    title: String,
    detail: String,
    dotColor: Color,
    icon: Int?,
    showLine: Boolean,
    iconTint: Color = Color.White,
    borderColor: Color = Color.Transparent
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Kolom garis waktu
        Column(
            modifier = Modifier.width(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(dotColor, CircleShape)
                    .border(1.dp, borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = if (dotColor == Color.White) iconTint else Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
            if (showLine) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(1.dp)
                        .weight(1f)
                        .background(TimelineLine)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp, bottom = if (showLine) 20.dp else 0.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Brown
            )
            Text(
                text = detail,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 11.sp,
                color = BrownMuted
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun DetailInventoriScreenPreview() {
    DetailInventoriScreen()
}