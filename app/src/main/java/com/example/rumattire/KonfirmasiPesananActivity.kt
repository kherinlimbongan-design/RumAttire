package com.example.rumattire

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.semantics.role

private val ConfirmationBackground = Color(0xFFF7F2E9)
private val ConfirmationField = Color(0xFFFBF8F2)
private val ConfirmationBeige = Color(0xFFF3EBDD)
private val ConfirmationInk = Color(0xFF3B2A22)
private val ConfirmationBrown = Color(0xFF4A3328)
private val ConfirmationMuted = Color(0xFF8B7B70)
private val ConfirmationSecondary = Color(0xFF6B5A4E)
private val ConfirmationBorder = Color(0xFFE6DACA)
private val ConfirmationDivider = Color(0xFFEFE6D8)
private val ConfirmationGreen = Color(0xFF0E8A5F)
private val ConfirmationGreenBackground = Color(0xFFE3F4EC)
private val ConfirmationOrange = Color(0xFFC4511A)
private val ConfirmationOrangeBorder = Color(0xFFE0642A)
private val ConfirmationOrangeBackground = Color(0xFFFDEBDD)
private val ConfirmationLocale = Locale("id", "ID")

enum class ConfirmationPaymentMethod(val label: String) {
    Deposit("DP"),
    PaidInFull("Lunas")
}

data class OrderConfirmationInfo(
    val id: String,
    val customerName: String,
    val orderType: String,
    val packageName: String,
    val eventLabel: String,
    val pickupLabel: String,
    val totalAmount: Long,
    val availableItems: Int,
    val totalItems: Int
)

data class OrderConfirmationResult(
    val orderId: String,
    val paymentMethod: ConfirmationPaymentMethod,
    val amountReceived: Long,
    val remainingAmount: Long,
    val receivedDateMillis: Long,
    val settlementDueDateMillis: Long?
)

internal val sampleConfirmationOrder = OrderConfirmationInfo(
    id = "WA-26041",
    customerName = "Nadia & Rizky",
    orderType = "Sewa",
    packageName = "Paket Akad Ivory",
    eventLabel = "5 Nov 2026",
    pickupLabel = "4 Nov · 16.00 WIB",
    totalAmount = 6_000_000L,
    availableItems = 4,
    totalItems = 4
)

internal fun confirmationSampleDate(day: Int): Long =
    Calendar.getInstance().apply {
        clear()
        set(2026, Calendar.NOVEMBER, day)
    }.timeInMillis

class KonfirmasiPesananActivity : ComponentActivity() {

    companion object {
        const val EXTRA_ORDER_ID = "confirmation_order_id"
        const val EXTRA_PAYMENT_METHOD = "confirmation_payment_method"
        const val EXTRA_AMOUNT_RECEIVED = "confirmation_amount_received"
        const val EXTRA_REMAINING_AMOUNT = "confirmation_remaining_amount"
        const val EXTRA_RECEIVED_DATE = "confirmation_received_date"
        const val EXTRA_SETTLEMENT_DUE_DATE = "confirmation_settlement_due_date"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = 0xFFF7F2E9.toInt()
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = 0xFFFBF8F2.toInt()
            )
        )

        setContent {
            KonfirmasiPesananScreen(
                order = sampleConfirmationOrder,
                initialDeposit = 3_000_000L,
                initialReceivedDate = confirmationSampleDate(3),
                initialDueDate = confirmationSampleDate(4),
                onCancel = { finish() },
                onConfirm = { result ->
                    val data = Intent().apply {
                        putExtra(EXTRA_ORDER_ID, result.orderId)
                        putExtra(EXTRA_PAYMENT_METHOD, result.paymentMethod.name)
                        putExtra(EXTRA_AMOUNT_RECEIVED, result.amountReceived)
                        putExtra(EXTRA_REMAINING_AMOUNT, result.remainingAmount)
                        putExtra(EXTRA_RECEIVED_DATE, result.receivedDateMillis)

                        result.settlementDueDateMillis?.let { dueDate ->
                            putExtra(EXTRA_SETTLEMENT_DUE_DATE, dueDate)
                        }
                    }

                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
            )
        }
    }
}

@Composable
fun KonfirmasiPesananScreen(
    order: OrderConfirmationInfo,
    initialDeposit: Long,
    initialReceivedDate: Long,
    initialDueDate: Long,
    onCancel: () -> Unit,
    onConfirm: (OrderConfirmationResult) -> Unit,
    showDesignSystemBars: Boolean = false
) {
    val context = LocalContext.current

    var method by rememberSaveable(order.id) {
        mutableStateOf(ConfirmationPaymentMethod.Deposit)
    }
    var depositDigits by rememberSaveable(order.id, initialDeposit) {
        mutableStateOf(initialDeposit.coerceAtLeast(0L).toString())
    }
    var receivedDate by rememberSaveable(order.id, initialReceivedDate) {
        mutableLongStateOf(initialReceivedDate)
    }
    var dueDate by rememberSaveable(order.id, initialDueDate) {
        mutableLongStateOf(initialDueDate)
    }

    val isDeposit = method == ConfirmationPaymentMethod.Deposit
    val deposit = depositDigits.toLongOrNull() ?: 0L
    val received = if (isDeposit) deposit else order.totalAmount
    val remaining = (order.totalAmount - received).coerceAtLeast(0L)

    val footerSummary = if (isDeposit) {
        "DP ${confirmationRupiah(deposit)} · sisa ${confirmationRupiah(remaining)}" +
                " · jatuh tempo ${confirmationDate(dueDate, "d MMM")}"
    } else {
        "Lunas ${confirmationRupiah(order.totalAmount)} · " +
                "diterima ${confirmationDate(receivedDate, "d MMM")}"
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = ConfirmationBrown,
            onPrimary = Color.White,
            background = ConfirmationBackground,
            surface = Color.White,
            onSurface = ConfirmationInk
        )
    ) {
        val insets = if (showDesignSystemBars) {
            Modifier
        } else {
            Modifier
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                    )
                )
                .imePadding()
        }

        Surface(
            modifier = Modifier.fillMaxSize().testTag("orderConfirmationRoot"),
            color = ConfirmationBackground
        ) {
            Column(Modifier.fillMaxSize().then(insets)) {
                if (showDesignSystemBars) ConfirmationStatusBar()

                ConfirmationHeader(order.id, onCancel)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ConfirmationSummary(order)

                    ConfirmationPaymentCard(
                        method = method,
                        onMethodChange = { method = it },
                        depositDigits = depositDigits,
                        onDepositChange = { input ->
                            depositDigits = input
                                .filter { it in '0'..'9' }
                                .take(18)
                        },
                        totalAmount = order.totalAmount,
                        receivedDate = receivedDate,
                        dueDate = dueDate,
                        remainingAmount = remaining,
                        onPickReceivedDate = {
                            pickConfirmationDate(context, receivedDate) {
                                receivedDate = it
                            }
                        },
                        onPickDueDate = {
                            pickConfirmationDate(context, dueDate) {
                                dueDate = it
                            }
                        }
                    )

                    ConfirmationReminder(isDeposit)
                    ConfirmationStatusCard(method)
                }

                ConfirmationFooter(
                    summary = footerSummary,
                    showDesignSystemBars = showDesignSystemBars,
                    onCancel = onCancel,
                    onConfirm = {
                        val message = when {
                            order.totalAmount <= 0L ->
                                "Total pesanan harus lebih dari nol."

                            isDeposit && (deposit <= 0L || deposit >= order.totalAmount) ->
                                "Nominal DP harus lebih dari nol dan kurang dari total sewa."

                            isDeposit && dayStart(dueDate) < dayStart(receivedDate) ->
                                "Jatuh tempo tidak boleh sebelum tanggal DP diterima."

                            else -> null
                        }

                        if (message != null) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            onConfirm(
                                OrderConfirmationResult(
                                    orderId = order.id,
                                    paymentMethod = method,
                                    amountReceived = received,
                                    remainingAmount = remaining,
                                    receivedDateMillis = receivedDate,
                                    settlementDueDateMillis = if (isDeposit) dueDate else null
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ConfirmationHeader(orderId: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(start = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(38.dp)
                .testTag("confirmationBack")
                .semantics { contentDescription = "Kembali" }
        ) {
            ConfirmationIcon(ConfirmationGlyph.Back, ConfirmationInk)
        }

        ConfirmationText(
            text = "Konfirmasi Pesanan · $orderId",
            size = 15,
            bold = true,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 12.dp)
                .semantics { heading() }
        )
    }
}

@Composable
private fun ConfirmationSummary(order: OrderConfirmationInfo) {
    Surface(
        modifier = Modifier.fillMaxWidth().testTag("confirmationSummary"),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, ConfirmationBrown)
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp, end = 16.dp, top = 10.dp, bottom = 7.dp
            )
        ) {
            Box(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    ConfirmationText(
                        text = order.customerName,
                        size = 15,
                        lineHeight = 20,
                        bold = true,
                        maxLines = 1,
                        modifier = Modifier.padding(end = 118.dp)
                    )

                    ConfirmationText(
                        text = "#${order.id} · ${order.orderType} · ${order.packageName}",
                        size = 12,
                        lineHeight = 18,
                        color = ConfirmationMuted
                    )
                }

                ConfirmationAvailabilityBadge(
                    available = order.availableItems,
                    total = order.totalItems,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(thickness = 1.dp, color = ConfirmationDivider)
            Spacer(Modifier.height(6.dp))

            ConfirmationSummaryRow("Acara", order.eventLabel)
            ConfirmationSummaryRow("Ambil", order.pickupLabel)
            ConfirmationSummaryRow(
                label = "Total sewa",
                value = confirmationRupiah(order.totalAmount),
                bold = true
            )
        }
    }
}

@Composable
private fun ConfirmationAvailabilityBadge(
    available: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val allAvailable = total > 0 && available == total
    val tint = if (allAvailable) ConfirmationGreen else ConfirmationOrange
    val background = if (allAvailable) {
        ConfirmationGreenBackground
    } else {
        ConfirmationOrangeBackground
    }

    Row(
        modifier = modifier
            .widthIn(min = 110.dp)
            .heightIn(min = 26.dp)
            .background(background, CircleShape)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (allAvailable) {
            ConfirmationIcon(
                glyph = ConfirmationGlyph.Check,
                tint = tint,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
        }

        ConfirmationText(
            text = "$available dari $total ada",
            size = 11,
            lineHeight = 14,
            color = tint,
            bold = true
        )
    }
}

@Composable
private fun ConfirmationSummaryRow(
    label: String,
    value: String,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConfirmationText(
            text = label,
            size = 13,
            lineHeight = 18,
            color = ConfirmationMuted,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(8.dp))

        ConfirmationText(
            text = value,
            size = 13,
            lineHeight = 18,
            bold = bold,
            align = TextAlign.End
        )
    }
}

@Composable
private fun ConfirmationPaymentCard(
    method: ConfirmationPaymentMethod,
    onMethodChange: (ConfirmationPaymentMethod) -> Unit,
    depositDigits: String,
    onDepositChange: (String) -> Unit,
    totalAmount: Long,
    receivedDate: Long,
    dueDate: Long,
    remainingAmount: Long,
    onPickReceivedDate: () -> Unit,
    onPickDueDate: () -> Unit
) {
    val isDeposit = method == ConfirmationPaymentMethod.Deposit

    ConfirmationCard(
        title = "Pembayaran",
        tag = "confirmationPayment"
    ) {
        Spacer(Modifier.height(10.dp))
        ConfirmationPaymentSelector(method, onMethodChange)

        Spacer(Modifier.height(12.dp))

        if (isDeposit) {
            ConfirmationFieldLabel("Nominal DP")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = ConfirmationField,
                border = BorderStroke(1.dp, ConfirmationBorder)
            ) {
                BasicTextField(
                    value = depositDigits,
                    onValueChange = onDepositChange,
                    modifier = Modifier.fillMaxWidth()
                        .testTag("confirmationDepositAmount")
                        .semantics { contentDescription = "Nominal DP" },
                    singleLine = true,
                    textStyle = confirmationTextStyle(
                        size = 13,
                        lineHeight = 18,
                        color = ConfirmationInk,
                        bold = false
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    visualTransformation = ConfirmationRupiahTransformation,
                    cursorBrush = SolidColor(ConfirmationBrown),
                    decorationBox = { field ->
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .heightIn(min = 44.dp)
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            field()
                        }
                    }
                )
            }
        } else {
            ConfirmationFieldLabel("Nominal pelunasan")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = ConfirmationField,
                border = BorderStroke(1.dp, ConfirmationBorder)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .heightIn(min = 44.dp)
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    ConfirmationText(confirmationRupiah(totalAmount), 13)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (isDeposit) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                ConfirmationDateField(
                    label = "Tanggal DP diterima",
                    value = receivedDate,
                    onClick = onPickReceivedDate,
                    modifier = Modifier.weight(1f)
                )

                ConfirmationDateField(
                    label = "Jatuh tempo pelunasan",
                    value = dueDate,
                    onClick = onPickDueDate,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            ConfirmationDateField(
                label = "Tanggal pelunasan diterima",
                value = receivedDate,
                onClick = onPickReceivedDate,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(ConfirmationBeige, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("confirmationRemainingAmount"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConfirmationText(
                text = "Sisa tagihan",
                size = 13,
                color = ConfirmationSecondary,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            ConfirmationText(
                text = confirmationRupiah(remainingAmount),
                size = 14,
                color = if (remainingAmount > 0L) {
                    ConfirmationOrange
                } else {
                    ConfirmationGreen
                },
                bold = true
            )
        }
    }
}

@Composable
private fun ConfirmationPaymentSelector(
    selected: ConfirmationPaymentMethod,
    onSelect: (ConfirmationPaymentMethod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(ConfirmationBeige, RoundedCornerShape(12.dp))
            .padding(4.dp)
            .selectableGroup()
            .testTag("confirmationPaymentSelector")
    ) {
        ConfirmationPaymentMethod.entries.forEach { method ->
            val active = method == selected

            Box(
                modifier = Modifier.weight(1f)
                    .heightIn(min = 36.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (active) ConfirmationBrown else Color.Transparent)
                    .selectable(
                        selected = active,
                        role = Role.Tab,
                        onClick = { onSelect(method) }
                    )
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                ConfirmationText(
                    text = method.label,
                    size = 13,
                    lineHeight = 18,
                    color = if (active) Color.White else ConfirmationSecondary,
                    bold = active
                )
            }
        }
    }
}

@Composable
private fun ConfirmationFieldLabel(label: String) {
    ConfirmationText(
        text = label,
        size = 11,
        lineHeight = 14,
        color = ConfirmationMuted
    )
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun ConfirmationDateField(
    label: String,
    value: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatted = confirmationDate(value, "d MMM yyyy")

    Column(modifier) {
        ConfirmationFieldLabel(label)

        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
                .height(44.dp)
                .semantics {
                    role = Role.Button
                    contentDescription = "$label, $formatted"
                },
            shape = RoundedCornerShape(12.dp),
            color = ConfirmationField,
            border = BorderStroke(1.dp, ConfirmationBorder)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ConfirmationText(
                    text = formatted,
                    size = 13,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                ConfirmationIcon(
                    glyph = ConfirmationGlyph.Calendar,
                    tint = ConfirmationSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ConfirmationReminder(isDeposit: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = 48.dp)
            .background(ConfirmationBeige, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("confirmationReminder"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConfirmationIcon(
            glyph = ConfirmationGlyph.Bell,
            tint = ConfirmationSecondary,
            modifier = Modifier.size(18.dp)
        )

        Spacer(Modifier.width(8.dp))

        ConfirmationText(
            text = if (isDeposit) {
                "Setelah dikonfirmasi, pengingat tagihan dan\n" +
                        "jadwal pengembalian dikirim otomatis."
            } else {
                "Setelah dikonfirmasi, pengingat jadwal pengembalian " +
                        "dikirim otomatis."
            },
            size = 12,
            lineHeight = 16,
            color = ConfirmationSecondary
        )
    }
}

@Composable
private fun ConfirmationStatusCard(method: ConfirmationPaymentMethod) {
    ConfirmationCard(
        title = "Status setelah konfirmasi",
        tag = "confirmationStatus"
    ) {
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConfirmationStatusChip(
                text = "Menunggu konfirmasi",
                background = ConfirmationOrangeBackground,
                tint = ConfirmationOrange,
                outline = ConfirmationOrangeBorder,
                modifier = Modifier.weight(156f)
            )

            ConfirmationIcon(
                glyph = ConfirmationGlyph.Forward,
                tint = ConfirmationMuted,
                modifier = Modifier.size(16.dp)
            )

            ConfirmationStatusChip(
                text = "Terkonfirmasi (${method.label})",
                background = ConfirmationGreenBackground,
                tint = ConfirmationGreen,
                outline = ConfirmationGreen,
                modifier = Modifier.weight(141f)
            )
        }
    }
}

@Composable
private fun ConfirmationStatusChip(
    text: String,
    background: Color,
    tint: Color,
    outline: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.heightIn(min = 26.dp),
        shape = CircleShape,
        color = background,
        border = BorderStroke(1.dp, outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(7.dp).background(tint, CircleShape)
            )
            Spacer(Modifier.width(6.dp))
            ConfirmationText(
                text = text,
                size = 11,
                lineHeight = 14,
                color = tint,
                bold = true
            )
        }
    }
}

@Composable
private fun ConfirmationCard(
    title: String,
    tag: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().testTag(tag),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ConfirmationBorder)
    ) {
        Column(Modifier.padding(16.dp)) {
            ConfirmationText(
                text = title,
                size = 14,
                lineHeight = 18,
                bold = true,
                modifier = Modifier.semantics { heading() }
            )
            content()
        }
    }
}

@Composable
private fun ConfirmationFooter(
    summary: String,
    showDesignSystemBars: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(ConfirmationField)
            .then(
                if (showDesignSystemBars) Modifier else Modifier.navigationBarsPadding()
            )
            .testTag("confirmationFooter")
    ) {
        HorizontalDivider(thickness = 1.dp, color = ConfirmationBorder)

        ConfirmationText(
            text = summary,
            size = 12,
            lineHeight = 18,
            color = ConfirmationMuted,
            align = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.width(88.dp).height(44.dp)
                    .testTag("confirmationCancel"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, ConfirmationBrown),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = ConfirmationBrown
                ),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                ConfirmationText("Batal", 13, color = ConfirmationBrown, bold = true)
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f).height(44.dp)
                    .testTag("confirmationSubmit"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ConfirmationBrown,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                ConfirmationText(
                    text = "Konfirmasi pesanan",
                    size = 13,
                    lineHeight = 16,
                    color = Color.White,
                    bold = true,
                    align = TextAlign.Center
                )
            }
        }

        if (showDesignSystemBars) {
            Box(
                modifier = Modifier.fillMaxWidth().height(24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    Modifier.padding(top = 10.dp)
                        .size(width = 100.dp, height = 4.dp)
                        .background(ConfirmationInk, CircleShape)
                )
            }
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun pickConfirmationDate(
    context: Context,
    currentMillis: Long,
    onSelected: (Long) -> Unit
) {
    val current = Calendar.getInstance().apply {
        timeInMillis = currentMillis
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            val selected = Calendar.getInstance().apply {
                clear()
                set(year, month, day)
            }
            onSelected(selected.timeInMillis)
        },
        current.get(Calendar.YEAR),
        current.get(Calendar.MONTH),
        current.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun dayStart(value: Long): Long =
    Calendar.getInstance().apply {
        timeInMillis = value
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private fun confirmationDate(value: Long, pattern: String): String =
    SimpleDateFormat(pattern, ConfirmationLocale).format(Date(value))

private fun groupRupiahDigits(digits: String): String =
    digits.reversed().chunked(3).joinToString(".").reversed()

private fun confirmationRupiah(value: Long): String =
    "Rp${groupRupiahDigits(value.toString())}"

// Menampilkan "Rp3.000.000", sementara nilai yang diedit tetap "3000000".
private object ConfirmationRupiahTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val formatted = "Rp${groupRupiahDigits(original)}"
        val originalOffsets = IntArray(original.length + 1)
        val transformedOffsets = IntArray(formatted.length + 1)

        originalOffsets[0] = 2
        var digitCount = 0

        formatted.forEachIndexed { index, character ->
            if (character in '0'..'9') {
                digitCount++
                originalOffsets[digitCount] = index + 1
            }
            transformedOffsets[index + 1] = digitCount
        }

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int =
                    originalOffsets[offset]

                override fun transformedToOriginal(offset: Int): Int =
                    transformedOffsets[offset]
            }
        )
    }
}

private enum class ConfirmationGlyph(val path: String, val stroke: Float) {
    Back("M19 12H5 M13 20L5 12L13 4", 1.8f),
    Check("M4 12L10 18L20 6", 3.2f),
    Forward("M2 12H22 M16 6L22 12L16 18", 2.1f),
    Calendar(
        "M6 4H18Q21.3 4 21.3 7.3V17.3Q21.3 20.7 18 20.7H6 " +
                "Q2.7 20.7 2.7 17.3V7.3Q2.7 4 6 4Z " +
                "M2.7 10H21.3 M8 2V6 M16 2V6",
        1.73f
    ),
    Bell(
        "M5.3 17.3V9.3C5.3 5.7 8.3 2.7 12 2.7S18.7 5.7 18.7 9.3V17.3 " +
                "L21.3 20H2.7L5.3 17.3Z M9.3 23C9.3 24 10.5 24 12 24 " +
                "S14.7 24 14.7 23",
        1.73f
    )
}

@Composable
private fun ConfirmationIcon(
    glyph: ConfirmationGlyph,
    tint: Color,
    modifier: Modifier = Modifier.size(24.dp)
) {
    val path = remember(glyph) {
        PathParser().parsePathString(glyph.path).toPath()
    }

    Canvas(modifier) {
        scale(size.width / 24f, size.height / 24f, pivot = Offset.Zero) {
            drawPath(
                path = path,
                color = tint,
                style = Stroke(
                    width = glyph.stroke,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

private fun confirmationTextStyle(
    size: Int,
    lineHeight: Int,
    color: Color,
    bold: Boolean
) = TextStyle(
    color = color,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
    letterSpacing = 0.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

@Composable
private fun ConfirmationText(
    text: String,
    size: Int,
    modifier: Modifier = Modifier,
    lineHeight: Int = size + 4,
    color: Color = ConfirmationInk,
    bold: Boolean = false,
    align: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        style = confirmationTextStyle(size, lineHeight, color, bold),
        textAlign = align,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun ConfirmationStatusBar() {
    Row(
        modifier = Modifier.fillMaxWidth().height(44.dp)
            .padding(start = 24.dp, end = 33.dp)
            .offset(y = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConfirmationText("09.41", 12, bold = true)
        Spacer(Modifier.weight(1f))

        Canvas(Modifier.size(width = 18.dp, height = 12.dp)) {
            for (index in 0..3) {
                val height = (4 + index * 2).dp.toPx()
                drawRoundRect(
                    color = ConfirmationInk,
                    topLeft = Offset(
                        (index * 5).dp.toPx(),
                        10.dp.toPx() - height
                    ),
                    size = Size(3.dp.toPx(), height),
                    cornerRadius = CornerRadius(1.dp.toPx())
                )
            }
        }

        Spacer(Modifier.width(8.dp))
        ConfirmationText("86%", 11)
        Spacer(Modifier.width(4.dp))

        Canvas(Modifier.size(width = 22.dp, height = 11.dp)) {
            drawRoundRect(
                color = ConfirmationInk,
                topLeft = Offset(0.5.dp.toPx(), 0.5.dp.toPx()),
                size = Size(21.dp.toPx(), 10.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
            drawRoundRect(
                color = ConfirmationInk,
                topLeft = Offset(2.5.dp.toPx(), 2.5.dp.toPx()),
                size = Size(15.dp.toPx(), 6.dp.toPx()),
                cornerRadius = CornerRadius(1.5.dp.toPx())
            )
        }
    }
}

@Preview(
    name = "Konfirmasi Pesanan · WA-26041",
    widthDp = 393,
    heightDp = 878,
    showBackground = true
)
@Composable
private fun PreviewKonfirmasiPesanan() {
    KonfirmasiPesananScreen(
        order = sampleConfirmationOrder,
        initialDeposit = 3_000_000L,
        initialReceivedDate = confirmationSampleDate(3),
        initialDueDate = confirmationSampleDate(4),
        onCancel = {},
        onConfirm = {},
        showDesignSystemBars = true
    )
}