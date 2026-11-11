package com.example.rumattire

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
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
import androidx.core.content.ContextCompat

private val PaymentBackground = Color(0xFFF7F2E9)
private val PaymentNavBackground = Color(0xFFFCF9F3)
private val PaymentText = Color(0xFF3E2B23)
private val PaymentMuted = Color(0xFF716055)
private val PaymentAccent = Color(0xFF493226)
private val PaymentDivider = Color(0xFFEEE4D8)

class PaymentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PaymentTheme {
                PaymentScreen(
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onNotification = {
                        showIntegrationMessage("Notifikasi")
                    },
                    onNavigate = { menu ->
                        showIntegrationMessage(menu)
                    },
                    onRecordSettlement = { orderId ->
                        showIntegrationMessage("Catat pelunasan $orderId")
                    },
                    onOpenOrder = { orderId ->
                        showIntegrationMessage("Buka pesanan $orderId")
                    }
                )
            }
        }
    }

    private fun showIntegrationMessage(action: String) {
        // Ganti dengan navigasi atau handler dari aplikasi kamu.
        Toast.makeText(
            this,
            "$action belum dihubungkan",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
private fun PaymentTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = PaymentAccent,
            onPrimary = Color.White,
            background = PaymentBackground,
            onBackground = PaymentText,
            surface = PaymentNavBackground,
            onSurface = PaymentText
        ),
        content = content
    )
}

private enum class PaymentFilter(
    val label: String,
    val tag: String,
    val labelTag: String
) {
    All("Semua", "filterSemua", "tvFilterSemua"),
    ToCollect("Perlu ditagih", "filterPerluDitagih", "tvFilterPerluDitagih"),
    Deposit("DP", "filterDp", "tvFilterDp"),
    Paid("Lunas", "filterLunas", "tvFilterLunas")
}

data class PaymentWorkspaceRecord(
    val orderId: String,
    val customer: String,
    val total: Long,
    val payment: OrderConfirmationResult?
)

@Composable
internal fun WorkspacePaymentScreen(
    workspace: OwnerWorkspace,
    onBack: () -> Unit,
    onNotification: () -> Unit,
    onNavigate: (String) -> Unit,
    onOpenOrder: (String) -> Unit,
    onRecordSettlement: (String) -> Unit
) {
    val records = workspace.orders.values.sortedByDescending { it.orderId }.map {
        PaymentWorkspaceRecord(
            it.orderId, it.customerName, workspace.total(it.orderId), workspace.payments[it.orderId]
        )
    }
    PaymentScreen(onBack, onNotification, onNavigate, onRecordSettlement, onOpenOrder, records)
}

@Composable
fun PaymentScreen(
    onBack: () -> Unit,
    onNotification: () -> Unit,
    onNavigate: (String) -> Unit,
    onRecordSettlement: (String) -> Unit,
    onOpenOrder: (String) -> Unit,
    records: List<PaymentWorkspaceRecord>? = null
) {
    var filter by rememberSaveable { mutableStateOf(PaymentFilter.All) }

    val showDeposit = filter != PaymentFilter.Paid
    val showWaiting = filter == PaymentFilter.All
    val showPaid = filter == PaymentFilter.All || filter == PaymentFilter.Paid

    Surface(
        modifier = Modifier.fillMaxSize().testTag("paymentManagementRoot"),
        color = PaymentBackground
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            containerColor = PaymentBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                PaymentHeader(onBack, onNotification)
            },
            bottomBar = {
                PaymentNavigation(onNavigate)
            }
        ) { padding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
            ) {
                val viewportHeight = maxHeight

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scrollPayment")
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = viewportHeight)
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                ) {
                    PaymentSummary(records)

                    PaymentFilters(
                        activeFilter = filter,
                        onSelect = { filter = it }
                    )

                    if (records != null) {
                        val filtered = records.filter { record ->
                            val payment = record.payment
                            when (filter) {
                                PaymentFilter.All -> true
                                PaymentFilter.ToCollect, PaymentFilter.Deposit ->
                                    payment != null && payment.remainingAmount > 0L
                                PaymentFilter.Paid -> payment != null && payment.remainingAmount == 0L
                            }
                        }
                        if (filtered.isEmpty()) {
                            PaymentLabel("Belum ada pembayaran untuk filter ini.", 13,
                                modifier = Modifier.padding(top = 16.dp))
                        }
                        val outstanding = filtered.filter { it.payment?.remainingAmount != 0L }
                        val paid = filtered.filter { it.payment?.remainingAmount == 0L }
                        if (outstanding.isNotEmpty()) {
                            PaymentSectionHeader(
                                "Perlu ditindaklanjuti", "${outstanding.size} pesanan",
                                "headerTindakLanjut", "tvJumlahTindakLanjut", Modifier.padding(top = 12.dp)
                            )
                            outstanding.forEach { record ->
                                WorkspacePaymentCard(record, onOpenOrder, onRecordSettlement)
                            }
                        }
                        if (paid.isNotEmpty()) {
                            PaymentSectionHeader(
                                "Sudah lunas", "${paid.size} pesanan",
                                "headerSudahLunas", "tvJumlahPesananLunas", Modifier.padding(top = 24.dp)
                            )
                            paid.forEach { record ->
                                WorkspacePaymentCard(record, onOpenOrder, onRecordSettlement)
                            }
                        }
                    } else if (showDeposit) {
                        PaymentSectionHeader(
                            title = "Perlu ditindaklanjuti",
                            count = if (showWaiting) "2 pesanan" else "1 pesanan",
                            tag = "headerTindakLanjut",
                            countTag = "tvJumlahTindakLanjut",
                            modifier = Modifier.padding(top = 12.dp)
                        )

                        PaymentDepositCard(
                            onRecordSettlement = {
                                onRecordSettlement("WA-26042")
                            }
                        )
                    }

                    if (records == null && showWaiting) {
                        PaymentWaitingCard(
                            onOpenOrder = {
                                onOpenOrder("WA-26041")
                            }
                        )
                    }

                    if (records == null && showPaid) {
                        PaymentSectionHeader(
                            title = "Sudah lunas",
                            count = "1 pesanan",
                            tag = "headerSudahLunas",
                            countTag = "tvJumlahPesananLunas",
                            modifier = Modifier.padding(top = 24.dp)
                        )

                        PaymentPaidCard()
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentHeader(
    onBack: () -> Unit,
    onNotification: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 8.dp)
            .testTag("headerPayment"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp).testTag("btnKembaliPayment")
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_inv_back),
                contentDescription = "Kembali",
                tint = PaymentAccent,
                modifier = Modifier.size(24.dp)
            )
        }

        PaymentLabel(
            text = "Payment Management",
            size = 16,
            bold = true,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onNotification,
            modifier = Modifier.size(48.dp).testTag("btnNotifikasiPayment")
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_inv_notifikasi),
                contentDescription = "Notifikasi",
                tint = PaymentAccent,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private data class PaymentNavItem(
    val label: String,
    val tag: String,
    @DrawableRes val icon: Int
)

private val paymentNavItems = listOf(
    PaymentNavItem("Beranda", "navBeranda", R.drawable.ic_inv_beranda),
    PaymentNavItem("Inventori", "navInventori", R.drawable.ic_inv_baju),
    PaymentNavItem("Pesanan", "navPesanan", R.drawable.ic_inv_pesanan),
    PaymentNavItem("Box", "navBox", R.drawable.ic_inv_box),
    PaymentNavItem("Lainnya", "navLainnya", R.drawable.ic_inv_menu)
)

@Composable
private fun PaymentNavigation(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PaymentNavBackground)
            .testTag("navbarPayment")
    ) {
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE4DACB))

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            paymentNavItems.forEach { item ->
                val active = item.tag == "navLainnya"
                val color = if (active) PaymentAccent else PaymentMuted
                val background = if (active) {
                    Modifier.paymentDrawable(R.drawable.bg_inv_nav_active)
                } else {
                    Modifier
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .height(64.dp)
                        .testTag(item.tag)
                        .then(background)
                        .semantics { selected = active }
                        .clickable(role = Role.Tab) { onNavigate(item.label) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )

                    PaymentLabel(
                        text = item.label,
                        size = 10,
                        color = color,
                        bold = active,
                        align = TextAlign.Center,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentSummary(records: List<PaymentWorkspaceRecord>? = null) {
    val received = records?.sumOf { it.payment?.amountReceived ?: 0L }
    val remaining = records?.sumOf { it.payment?.remainingAmount ?: 0L }
    val depositCount = records?.count { (it.payment?.remainingAmount ?: 0L) > 0L }
    val followUp = records?.count { it.payment?.remainingAmount != 0L }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cardRingkasanPayment")
            .paymentDrawable(R.drawable.bg_payment_summary)
            .padding(16.dp)
    ) {
        PaymentLabel("Total belum dilunasi", 12, color = PaymentMuted)

        PaymentLabel(
            text = remaining?.let(::workspaceMoney) ?: "Rp3.000.000",
            size = 30,
            modifier = Modifier.padding(top = 4.dp).testTag("tvTotalBelumLunas")
        )

        PaymentLabel(
            text = depositCount?.let { "Dari $it pesanan dengan DP" } ?: "Dari 1 pesanan dengan DP",
            size = 12,
            color = PaymentMuted,
            modifier = Modifier
                .padding(top = 4.dp)
                .testTag("tvKeteranganBelumLunas")
        )

        HorizontalDivider(
            modifier = Modifier.padding(top = 14.dp, bottom = 12.dp),
            thickness = 1.dp,
            color = PaymentDivider
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                PaymentLabel("Diterima", 12, color = PaymentMuted)
                PaymentLabel(
                    text = received?.let(::workspaceMoney) ?: "Rp6.500.000",
                    size = 15,
                    bold = true,
                    modifier = Modifier.padding(top = 4.dp).testTag("tvTotalDiterima")
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                PaymentLabel("Perlu ditindaklanjuti", 12, color = PaymentMuted)
                PaymentLabel(
                    text = followUp?.let { "$it pesanan" } ?: "2 pesanan",
                    size = 15,
                    bold = true,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .testTag("tvTotalTindakLanjut")
                )
            }
        }
    }
}

@Composable
private fun PaymentFilters(
    activeFilter: PaymentFilter,
    onSelect: (PaymentFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PaymentFilter.entries.forEach { option ->
            val active = activeFilter == option
            val background = if (active) {
                R.drawable.bg_inv_button
            } else {
                R.drawable.bg_inv_card
            }

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .testTag(option.tag)
                    .semantics { selected = active }
                    .clickable(role = Role.Tab) { onSelect(option) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .paymentDrawable(background)
                        .padding(
                            horizontal = if (option == PaymentFilter.Deposit) {
                                16.dp
                            } else {
                                14.dp
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PaymentLabel(
                        text = option.label,
                        size = 12,
                        color = if (active) Color.White else PaymentMuted,
                        bold = active,
                        modifier = Modifier.testTag(option.labelTag)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentSectionHeader(
    title: String,
    count: String,
    tag: String,
    countTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PaymentLabel(title, 14, modifier = Modifier.weight(1f), bold = true)

        PaymentLabel(
            text = count,
            size = 12,
            color = PaymentMuted,
            modifier = Modifier.padding(start = 8.dp).testTag(countTag)
        )
    }
}

@Composable
private fun PaymentDepositCard(onRecordSettlement: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .testTag("cardPaymentDp")
            .paymentDrawable(R.drawable.bg_inv_card)
            .padding(16.dp)
    ) {
        PaymentOrderHeader(
            name = "Ayu & Fajar",
            order = "#WA-26042 · Sewa",
            status = "●  DP · Tempo 7 Nov",
            nameTag = "tvNamaPelangganDp",
            orderTag = "tvNomorPesananDp",
            statusTag = "tvStatusDp",
            statusBackground = R.drawable.bg_payment_dp,
            statusColor = Color(0xFF946000)
        )

        PaymentCardDivider()

        PaymentDetailRow("Total sewa", "Rp5.000.000", "tvTotalSewaDp")

        PaymentDetailRow(
            label = "DP diterima · 1 Nov",
            value = "Rp2.000.000",
            valueTag = "tvNominalDp",
            labelTag = "tvTanggalDp",
            modifier = Modifier.padding(top = 8.dp)
        )

        PaymentDetailRow(
            label = "Sisa tagihan",
            value = "Rp3.000.000",
            valueTag = "tvSisaTagihan",
            valueColor = Color(0xFFC3450B),
            bold = true,
            modifier = Modifier.padding(top = 8.dp)
        )

        PaymentDetailRow(
            label = "Jatuh tempo",
            value = "7 Nov 2026",
            valueTag = "tvJatuhTempo",
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_inv_notifikasi),
                contentDescription = null,
                tint = PaymentMuted,
                modifier = Modifier.size(14.dp)
            )

            PaymentLabel(
                text = "Pengingat tagihan terkirim 5 Nov · 09.00",
                size = 11,
                color = PaymentMuted,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .weight(1f)
                    .testTag("tvPengingatTagihan")
            )
        }

        PaymentAction(
            label = "Catat pelunasan",
            tag = "btnCatatPelunasan",
            background = R.drawable.bg_inv_button,
            textColor = Color.White,
            modifier = Modifier.padding(top = 12.dp),
            onClick = onRecordSettlement
        )
    }
}

@Composable
private fun PaymentWaitingCard(onOpenOrder: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .testTag("cardPaymentMenunggu")
            .paymentDrawable(R.drawable.bg_inv_card)
            .padding(16.dp)
    ) {
        PaymentOrderHeader(
            name = "Nadia & Rizky",
            order = "#WA-26041 · Sewa",
            status = "●  Menunggu konfirmasi",
            nameTag = "tvNamaPelangganMenunggu",
            orderTag = "tvNomorPesananMenunggu",
            statusTag = "tvStatusMenunggu",
            statusBackground = R.drawable.bg_payment_waiting,
            statusColor = Color(0xFFBB430C)
        )

        PaymentCardDivider()

        PaymentDetailRow(
            label = "Total sewa",
            value = "Rp6.000.000",
            valueTag = "tvTotalSewaMenunggu"
        )

        PaymentDetailRow(
            label = "Pembayaran",
            value = "Belum ditentukan",
            valueTag = "tvMetodePembayaranMenunggu",
            valueColor = PaymentMuted,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .paymentDrawable(R.drawable.bg_inv_icon)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_payment_info),
                contentDescription = null,
                tint = PaymentMuted,
                modifier = Modifier.size(18.dp)
            )

            PaymentLabel(
                text = "DP atau lunas dipilih saat pemilik mengonfirmasi pesanan.",
                size = 11,
                color = PaymentMuted,
                lineHeight = 15,
                modifier = Modifier.padding(start = 8.dp).weight(1f)
            )
        }

        PaymentAction(
            label = "Buka pesanan",
            tag = "btnBukaPesananPayment",
            background = R.drawable.bg_box_button_outline,
            textColor = PaymentAccent,
            modifier = Modifier.padding(top = 16.dp),
            onClick = onOpenOrder
        )
    }
}

@Composable
private fun PaymentPaidCard() {
    Row(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .testTag("cardPaymentLunas")
            .paymentDrawable(R.drawable.bg_inv_card)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .paymentDrawable(R.drawable.bg_payment_paid),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_detail_check),
                contentDescription = null,
                tint = Color(0xFF008663),
                modifier = Modifier.size(22.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 8.dp)
        ) {
            PaymentLabel(
                text = "Dinda · WA-26038",
                size = 13,
                bold = true,
                modifier = Modifier.testTag("tvNamaPelangganLunas")
            )

            PaymentLabel(
                text = "Sewa · Lunas 29 Okt",
                size = 11,
                color = PaymentMuted,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .testTag("tvTanggalPelunasan")
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            PaymentLabel(
                text = "Rp4.500.000",
                size = 13,
                bold = true,
                modifier = Modifier.testTag("tvNominalLunas")
            )

            PaymentLabel(
                text = "Lunas",
                size = 10,
                color = Color(0xFF007A58),
                bold = true,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .paymentDrawable(R.drawable.bg_payment_paid)
                    .padding(horizontal = 12.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun PaymentOrderHeader(
    name: String,
    order: String,
    status: String,
    nameTag: String,
    orderTag: String,
    statusTag: String,
    @DrawableRes statusBackground: Int,
    statusColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            PaymentLabel(
                text = name,
                size = 15,
                bold = true,
                modifier = Modifier.testTag(nameTag)
            )

            PaymentLabel(
                text = order,
                size = 12,
                color = PaymentMuted,
                modifier = Modifier.padding(top = 4.dp).testTag(orderTag)
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 156.dp)
                .heightIn(min = 28.dp)
                .testTag(statusTag)
                .paymentDrawable(statusBackground)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            PaymentLabel(
                text = status,
                size = 11,
                color = statusColor,
                bold = true,
                align = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PaymentCardDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        thickness = 1.dp,
        color = PaymentDivider
    )
}

@Composable
private fun PaymentDetailRow(
    label: String,
    value: String,
    valueTag: String,
    modifier: Modifier = Modifier,
    labelTag: String? = null,
    valueColor: Color = PaymentText,
    bold: Boolean = false
) {
    val labelModifier = if (labelTag == null) {
        Modifier
    } else {
        Modifier.testTag(labelTag)
    }

    Row(modifier = modifier.fillMaxWidth()) {
        PaymentLabel(
            text = label,
            size = 13,
            color = PaymentMuted,
            modifier = Modifier.weight(1f).then(labelModifier)
        )

        PaymentLabel(
            text = value,
            size = 13,
            color = valueColor,
            bold = bold,
            modifier = Modifier.padding(start = 8.dp).testTag(valueTag)
        )
    }
}

@Composable
private fun PaymentAction(
    label: String,
    tag: String,
    @DrawableRes background: Int,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .testTag(tag)
            .paymentDrawable(background)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        PaymentLabel(label, 14, color = textColor, bold = true)
    }
}

@Composable
private fun PaymentLabel(
    text: String,
    size: Int,
    modifier: Modifier = Modifier,
    color: Color = PaymentText,
    bold: Boolean = false,
    align: TextAlign = TextAlign.Start,
    lineHeight: Int? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        textAlign = align,
        style = TextStyle(
            letterSpacing = 0.sp,
            lineHeight = lineHeight?.sp ?: androidx.compose.ui.unit.TextUnit.Unspecified,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
}

@Composable
private fun Modifier.paymentDrawable(@DrawableRes resource: Int): Modifier {
    val context = LocalContext.current
    val drawable = remember(context, resource) {
        requireNotNull(ContextCompat.getDrawable(context, resource)).mutate()
    }

    return drawBehind {
        drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
        drawable.draw(drawContext.canvas.nativeCanvas)
    }
}

@Preview(
    name = "Payment Management",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun PaymentScreenPreview() {
    PaymentTheme {
        PaymentScreen(
            onBack = {},
            onNotification = {},
            onNavigate = {},
            onRecordSettlement = {},
            onOpenOrder = {}
        )
    }
}

@Composable
private fun WorkspacePaymentCard(
    record: PaymentWorkspaceRecord,
    onOpenOrder: (String) -> Unit,
    onRecordSettlement: (String) -> Unit
) {
    val payment = record.payment
    val hasBalance = payment != null && payment.remainingAmount > 0L
    val status = when {
        payment == null -> "●  Menunggu konfirmasi"
        hasBalance -> "●  DP"
        else -> "Lunas"
    }

    Column(
        Modifier.padding(top = 12.dp).fillMaxWidth()
            .paymentDrawable(R.drawable.bg_inv_card).padding(16.dp)
    ) {
        PaymentOrderHeader(
            name = record.customer,
            order = "#${record.orderId}",
            status = status,
            nameTag = "paymentName-${record.orderId}",
            orderTag = "paymentOrder-${record.orderId}",
            statusTag = "paymentStatus-${record.orderId}",
            statusBackground = when {
                payment == null -> R.drawable.bg_payment_waiting
                hasBalance -> R.drawable.bg_payment_dp
                else -> R.drawable.bg_payment_paid
            },
            statusColor = when {
                payment == null -> Color(0xFFBB430C)
                hasBalance -> Color(0xFF946000)
                else -> Color(0xFF007A58)
            }
        )

        PaymentCardDivider()
        PaymentDetailRow(
            "Total pesanan", if (record.total > 0L) workspaceMoney(record.total) else "Belum dicatat",
            "paymentTotal-${record.orderId}"
        )
        if (payment != null) {
            PaymentDetailRow(
                "Diterima", workspaceMoney(payment.amountReceived),
                "paymentReceived-${record.orderId}", modifier = Modifier.padding(top = 8.dp)
            )
            PaymentDetailRow(
                "Sisa tagihan", workspaceMoney(payment.remainingAmount),
                "paymentBalance-${record.orderId}", modifier = Modifier.padding(top = 8.dp)
            )
            payment.settlementDueDateMillis?.let { due ->
                PaymentDetailRow(
                    "Jatuh tempo", formatOrderDate(due, "d MMM yyyy"),
                    "paymentDue-${record.orderId}", modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        PaymentAction(
            label = if (hasBalance) "Catat pelunasan" else "Buka pesanan",
            tag = "paymentAction-${record.orderId}",
            background = if (hasBalance) R.drawable.bg_inv_button else R.drawable.bg_box_button_outline,
            textColor = if (hasBalance) Color.White else PaymentAccent,
            modifier = Modifier.padding(top = 12.dp),
            onClick = {
                if (hasBalance) onRecordSettlement(record.orderId) else onOpenOrder(record.orderId)
            }
        )
    }
}
