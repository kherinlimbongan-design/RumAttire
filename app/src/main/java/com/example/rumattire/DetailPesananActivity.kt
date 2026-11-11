package com.example.rumattire

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rumattire.R

// ---------- Palet warna (sama dengan layar lain) ----------
private val Canvas = Color(0xFFF7F3EB)
private val FooterBackground = Color(0xFFF7F3EB)
private val Brown = Color(0xFF3E2B23)
private val BrownIndicator = Color(0xFF4B3429)
private val BrownMuted = Color(0xFF988478)
private val BrownSoft = Color(0xFF786657)
private val BrownLight = Color(0xFFAC998C)
private val Gold = Color(0xFFC4A46A)
private val Orange = Color(0xFFD64E09)
private val OrangeSoft = Color(0xFFFCE9DC)
private val OrangeDot = Color(0xFFE96728)
private val Green = Color(0xFF008C62)
private val Blue = Color(0xFF265DF2)
private val CardDivider = Color(0xFFF0E5D8)
private val TimelineLine = Color(0xFFE7D8C6)
private val TimelinePending = Color(0xFFDDCDB9)
private val IconBackground = Color(0xFFF5EDE2)
private val ProgressTrack = Color(0xFFEADFCF)
private val CardBorder = Color(0xFFE7D8C6)
private val BadgeBackground = Color(0xFFF5EDE2)
private val AvailableBackground = Color(0xFFE3F4EE)

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

private data class PesananItem(
    val name: String,
    val detail: String,
    val statusLabel: String,
    val boxCode: String,
    val icon: Int
)

private val pesananItems = listOf(
    PesananItem(
        name = "Kebaya Ivory Bordir",
        detail = "KB-014 · M · Ivory",
        statusLabel = "✓ Ada",
        boxCode = "BX-028",
        icon = R.drawable.ic_inv_baju
    ),
    PesananItem(
        name = "Kain Batik Sogan",
        detail = "BT-008 · 2 m · Sogan",
        statusLabel = "✓ Ada",
        boxCode = "BX-028",
        icon = R.drawable.ic_detail_kain
    ),
    PesananItem(
        name = "Veil Tulle Panjang",
        detail = "VL-003 · All size · Putih",
        statusLabel = "✓ Ada",
        boxCode = "BX-012",
        icon = R.drawable.ic_inv_veil
    ),
    PesananItem(
        name = "Cover Garment Ivory",
        detail = "CV-028 · L · Ivory",
        statusLabel = "✓ Ada",
        boxCode = "BX-019",
        icon = R.drawable.ic_detail_garment
    )
)

private data class TimelineStep(
    val title: String,
    val detail: String,
    val state: StepState,
    val titleColor: Color,
    val detailColor: Color
)

private enum class StepState { DONE, CURRENT, PENDING }

private val timelineSteps = listOf(
    TimelineStep(
        title = "Menunggu persiapan",
        detail = "3 Nov · 14.20 · Owner mengirim ke karyawan",
        state = StepState.DONE,
        titleColor = Brown,
        detailColor = BrownMuted
    ),
    TimelineStep(
        title = "Sedang diperiksa",
        detail = "4 Nov · 08.40 · Karyawan mulai validasi",
        state = StepState.DONE,
        titleColor = Brown,
        detailColor = BrownMuted
    ),
    TimelineStep(
        title = "Menunggu konfirmasi",
        detail = "4 Nov · 09.12 · Hasil validasi dikirim ke owner",
        state = StepState.CURRENT,
        titleColor = OrangeDot,
        detailColor = BrownMuted
    ),
    TimelineStep(
        title = "Terkonfirmasi (DP / Lunas)",
        detail = "Dipilih saat konfirmasi pesanan",
        state = StepState.PENDING,
        titleColor = BrownMuted,
        detailColor = BrownLight
    ),
    TimelineStep(
        title = "Selesai",
        detail = "Setelah barang kembali diperiksa",
        state = StepState.PENDING,
        titleColor = BrownMuted,
        detailColor = BrownLight
    )
)

@Composable
fun DetailPesananScreen(
    orderCode: String = "WA-26041",
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onEditOrderClick: () -> Unit = {},
    onConfirmOrderClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    order: EditOrderData? = null,
    orderStatus: String = "Menunggu konfirmasi",
    payment: OrderConfirmationResult? = null,
    orderTotal: Long = 6_000_000L
) {
    val displayedItems = order?.items?.map {
        PesananItem(it.name, "${it.sku} · ${it.size}",
            if (it.alreadyValidated) "✓ Ada" else "Belum dicek",
            it.boxId, R.drawable.ic_inv_baju)
    } ?: pesananItems
    val validated = order?.items?.count { it.alreadyValidated } ?: 4
    val statusSteps = if (order == null) timelineSteps else listOf(
        TimelineStep(orderStatus, "Status pesanan pada sesi ini", StepState.CURRENT, Brown, BrownMuted)
    )
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
                    text = "Order Detail · $orderCode",
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
                    .padding(top = 4.dp, bottom = 16.dp)
            ) {
                // Ringkasan pesanan
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
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
                                .background(OrangeSoft, RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Orange, CircleShape)
                            )
                            Text(
                                text = orderStatus,
                                modifier = Modifier.padding(start = 6.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Orange
                            )
                        }
                    }

                    Text(
                        text = order?.customerName ?: "Nadia & Rizky",
                        modifier = Modifier.padding(top = 12.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )
                    Text(
                        text = order?.let { "#${it.orderId} · ${it.type.label} · ${it.packageName}" } ?: "#WA-26041 · Sewa · Paket Akad Ivory",
                        modifier = Modifier.padding(top = 4.dp),
                        fontSize = 12.sp,
                        color = BrownMuted
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 14.dp, bottom = 14.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CardDivider)
                    )

                    SummaryRow("Pelanggan", order?.let { listOf(it.customerName, it.phone).filter(String::isNotBlank).joinToString(" · ") } ?: "Nadia Putri · 0812-3456-7890")
                    SummaryRow("Acara", order?.let { formatOrderDate(it.eventDate, "d MMM yyyy") } ?: "5 Nov 2026", modifier = Modifier.padding(top = 10.dp))
                    SummaryRow("Ambil", order?.let { formatOrderDate(it.pickupDateTime, "d MMM · HH.mm") } ?: "4 Nov · 16.00 WIB", modifier = Modifier.padding(top = 10.dp))
                    SummaryRow("Kembali", order?.let { formatOrderDate(it.returnDateTime, "d MMM · HH.mm") } ?: "7 Nov · 17.00 WIB", modifier = Modifier.padding(top = 10.dp))
                }

                // Validasi barang
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
                            text = "Validasi barang",
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brown
                        )
                        Text(
                            text = "$validated dari ${displayedItems.size} ada",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Green
                        )
                    }

                    LinearProgressIndicator(
                        progress = { if (displayedItems.isEmpty()) 0f else validated.toFloat() / displayedItems.size },
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .height(6.dp),
                        color = Green,
                        trackColor = ProgressTrack,
                        strokeCap = StrokeCap.Round
                    )

                    Text(
                        text = if (order == null) "Divalidasi Sari · 4 Nov 2026, 09.12 WIB" else "${displayedItems.size - validated} barang menunggu validasi",
                        modifier = Modifier.padding(top = 12.dp),
                        fontSize = 11.sp,
                        color = BrownMuted
                    )
                }

                // Daftar barang
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
                            text = "Daftar barang",
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brown
                        )
                        Text(
                            text = "${displayedItems.size} item",
                            fontSize = 12.sp,
                            color = BrownMuted
                        )
                    }

                    displayedItems.forEachIndexed { index, item ->
                        if (index > 0) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 14.dp, bottom = 10.dp)
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(CardDivider)
                            )
                        }
                        ItemRow(
                            item = item,
                            modifier = Modifier.padding(top = if (index == 0) 16.dp else 0.dp),
                            onClick = { onItemClick(item.boxCode) }
                        )
                    }
                }

                // Linimasa status
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Linimasa status",
                        modifier = Modifier.padding(bottom = 16.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )

                    statusSteps.forEachIndexed { index, step ->
                        TimelineRow(
                            step = step,
                            showLine = index < statusSteps.lastIndex,
                            lineColor = when {
                                index == 0 || index == 1 -> Green
                                else -> TimelinePending
                            }
                        )
                    }
                }

                // Pembayaran
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
                            text = "Pembayaran",
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brown
                        )
                        Text(
                            text = payment?.let { "Terkonfirmasi (${it.paymentMethod.label})" } ?: "Belum dikonfirmasi",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .background(BadgeBackground, RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrownSoft
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 12.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CardDivider)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total sewa",
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            color = BrownMuted
                        )
                        Text(
                            text = if (orderTotal > 0) workspaceMoney(orderTotal) else "Belum dicatat",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brown
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Metode",
                            modifier = Modifier.width(72.dp),
                            fontSize = 12.sp,
                            color = BrownMuted
                        )
                        Text(
                            text = payment?.paymentMethod?.label ?: "DP atau Lunas · dipilih saat konfirmasi",
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            color = Brown,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            // ---------- FOOTER AKSI (tetap terlihat) ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FooterBackground)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Edit hanya mengirim ulang barang yang berubah.",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 11.sp,
                    color = BrownMuted,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Edit pesanan",
                        modifier = Modifier
                            .weight(0.8f)
                            .height(48.dp)
                            .border(1.dp, BrownIndicator, RoundedCornerShape(12.dp))
                            .clickable(onClick = onEditOrderClick)
                            .padding(vertical = 14.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrownIndicator,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (payment == null) "Konfirmasi pesanan" else "Buka pembayaran",
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .background(Brown, RoundedCornerShape(12.dp))
                            .clickable(onClick = onConfirmOrderClick)
                            .padding(vertical = 14.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(86.dp),
            fontSize = 12.sp,
            color = BrownMuted
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            color = Brown,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ItemRow(
    item: PesananItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(IconBackground, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = BrownSoft,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 8.dp)
        ) {
            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Brown
            )
            Text(
                text = item.detail,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 11.sp,
                color = BrownMuted
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = item.statusLabel,
                modifier = Modifier
                    .background(AvailableBackground, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Green
            )
            Row(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .background(BadgeBackground, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_inv_storage),
                    contentDescription = null,
                    tint = BrownIndicator,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = item.boxCode,
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = 10.sp,
                    color = BrownIndicator
                )
            }
        }
    }
}

@Composable
private fun TimelineRow(
    step: TimelineStep,
    showLine: Boolean,
    lineColor: Color
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.width(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        when (step.state) {
                            StepState.DONE -> Green
                            StepState.CURRENT -> OrangeDot
                            StepState.PENDING -> TimelinePending
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (step.state == StepState.DONE) {
                    Icon(
                        painter = painterResource(R.drawable.ic_detail_check),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
            if (showLine) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(1.dp)
                        .height(40.dp)
                        .background(lineColor)
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp, bottom = if (showLine) 20.dp else 0.dp)
        ) {
            Text(
                text = step.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = step.titleColor
            )
            Text(
                text = step.detail,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 11.sp,
                color = step.detailColor
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun DetailPesananScreenPreview() {
    DetailPesananScreen()
}