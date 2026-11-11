package com.example.rumattire

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
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
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val OrderBackground = Color(0xFFF7F2E9)
private val OrderFieldBackground = Color(0xFFFBF8F2)
private val OrderBeige = Color(0xFFF3EBDD)
private val OrderInk = Color(0xFF3B2A22)
private val OrderBrown = Color(0xFF4A3328)
private val OrderMuted = Color(0xFF8B7B70)
private val OrderHintInk = Color(0xFF6B5A4E)
private val OrderBorder = Color(0xFFE6DACA)
private val OrderDivider = Color(0xFFEFE6D8)
private val OrderAvailableBackground = Color(0xFFE1F3EB)
private val OrderAvailableInk = Color(0xFF008663)
private val OrderNewBackground = Color(0xFFFDEBDD)
private val OrderNewInk = Color(0xFFC3450B)
private val IndonesianLocale = Locale("id", "ID")

enum class EditOrderType(val label: String) {
    Rental("Sewa"),
    Sale("Jual"),
    Accessory("Aksesori")
}

enum class EditOrderItemIcon {
    Shirt, Fabric, Veil, Cover, Shoe
}

data class EditOrderItem(
    val sku: String,
    val name: String,
    val size: String,
    val boxId: String,
    val icon: EditOrderItemIcon,
    val alreadyValidated: Boolean = false
)

data class EditOrderData(
    val orderId: String,
    val customerName: String,
    val phone: String,
    val type: EditOrderType,
    val packageName: String,
    val eventDate: Long,
    val pickupDateTime: Long,
    val returnDateTime: Long,
    val items: List<EditOrderItem>,
    val totalAmount: Long = 0L
)

data class EditOrderResult(
    val order: EditOrderData,
    val itemSkusToValidate: List<String>,
    val removedItemSkus: List<String>
)

internal fun sampleOrderDate(day: Int, hour: Int = 0): Long =
    Calendar.getInstance().apply {
        clear()
        set(2026, Calendar.NOVEMBER, day, hour, 0, 0)
    }.timeInMillis

internal val SampleEditOrder = EditOrderData(
    orderId = "WA-26041",
    customerName = "Nadia Putri",
    phone = "0812-3456-7890",
    type = EditOrderType.Rental,
    packageName = "Paket Akad Ivory",
    eventDate = sampleOrderDate(5),
    pickupDateTime = sampleOrderDate(4, 16),
    returnDateTime = sampleOrderDate(7, 17),
    totalAmount = 6_000_000L,
    items = listOf(
        EditOrderItem(
            "KB-014", "Kebaya Ivory Bordir", "M", "BX-028",
            EditOrderItemIcon.Shirt, alreadyValidated = true
        ),
        EditOrderItem(
            "BT-008", "Kain Batik Sogan", "2 m", "BX-028",
            EditOrderItemIcon.Fabric, alreadyValidated = true
        ),
        EditOrderItem(
            "VL-003", "Veil Tulle Panjang", "All size", "BX-012",
            EditOrderItemIcon.Veil, alreadyValidated = true
        ),
        EditOrderItem(
            "CV-028", "Cover Garment Ivory", "L", "BX-019",
            EditOrderItemIcon.Cover, alreadyValidated = true
        ),
        EditOrderItem(
            "SH-006", "Sepatu Pump Nude", "38", "BX-007",
            EditOrderItemIcon.Shoe
        )
    )
)

class EditPesananActivity : ComponentActivity() {

    companion object {
        const val EXTRA_ORDER_ID = "edit_order_id"
        const val EXTRA_CUSTOMER_NAME = "edit_order_customer_name"
        const val EXTRA_PHONE = "edit_order_phone"
        const val EXTRA_ORDER_TYPE = "edit_order_type"
        const val EXTRA_PACKAGE_NAME = "edit_order_package_name"
        const val EXTRA_EVENT_DATE = "edit_order_event_date"
        const val EXTRA_PICKUP = "edit_order_pickup"
        const val EXTRA_RETURN = "edit_order_return"
        const val EXTRA_ITEM_SKUS = "edit_order_item_skus"
        const val EXTRA_VALIDATE_SKUS = "edit_order_validate_skus"
        const val EXTRA_REMOVED_SKUS = "edit_order_removed_skus"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                0xFFF7F2E9.toInt()
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                0xFFFBF8F2.toInt()
            )
        )

        setContent {
            EditPesananScreen(
                initialOrder = SampleEditOrder,
                catalog = SampleEditOrder.items,
                onCancel = { finish() },
                onSave = { result ->
                    val order = result.order
                    val data = Intent().apply {
                        putExtra(EXTRA_ORDER_ID, order.orderId)
                        putExtra(EXTRA_CUSTOMER_NAME, order.customerName)
                        putExtra(EXTRA_PHONE, order.phone)
                        putExtra(EXTRA_ORDER_TYPE, order.type.name)
                        putExtra(EXTRA_PACKAGE_NAME, order.packageName)
                        putExtra(EXTRA_EVENT_DATE, order.eventDate)
                        putExtra(EXTRA_PICKUP, order.pickupDateTime)
                        putExtra(EXTRA_RETURN, order.returnDateTime)
                        putStringArrayListExtra(
                            EXTRA_ITEM_SKUS,
                            ArrayList(order.items.map { it.sku })
                        )
                        putStringArrayListExtra(
                            EXTRA_VALIDATE_SKUS,
                            ArrayList(result.itemSkusToValidate)
                        )
                        putStringArrayListExtra(
                            EXTRA_REMOVED_SKUS,
                            ArrayList(result.removedItemSkus)
                        )
                    }

                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
            )
        }
    }
}

@Composable
fun EditPesananScreen(
    initialOrder: EditOrderData,
    catalog: List<EditOrderItem>,
    onCancel: () -> Unit,
    onSave: (EditOrderResult) -> Unit,
    showDesignSystemBars: Boolean = false,
    creating: Boolean = false,
    showTotalField: Boolean = false,
    totalEditable: Boolean = true
) {
    val context = LocalContext.current

    var customerName by rememberSaveable(initialOrder.orderId) {
        mutableStateOf(initialOrder.customerName)
    }
    var phone by rememberSaveable(initialOrder.orderId) {
        mutableStateOf(initialOrder.phone)
    }
    var orderType by rememberSaveable(initialOrder.orderId) {
        mutableStateOf(initialOrder.type)
    }
    var packageName by rememberSaveable(initialOrder.orderId) {
        mutableStateOf(initialOrder.packageName)
    }
    var eventDate by rememberSaveable(initialOrder.orderId) {
        mutableLongStateOf(initialOrder.eventDate)
    }
    var pickup by rememberSaveable(initialOrder.orderId) {
        mutableLongStateOf(initialOrder.pickupDateTime)
    }
    var returnDate by rememberSaveable(initialOrder.orderId) {
        mutableLongStateOf(initialOrder.returnDateTime)
    }
    var selectedSkus by rememberSaveable(initialOrder.orderId) {
        mutableStateOf(ArrayList(initialOrder.items.map { it.sku }.distinct()))
    }
    var amountDigits by rememberSaveable(initialOrder.orderId) {
        mutableStateOf(initialOrder.totalAmount.takeIf { it > 0L }?.toString().orEmpty())
    }
    var showItemPicker by rememberSaveable(initialOrder.orderId) {
        mutableStateOf(false)
    }

    val itemsBySku = (initialOrder.items + catalog)
        .distinctBy { it.sku }
        .associateBy { it.sku }

    val originallyValidated = initialOrder.items
        .filter { it.alreadyValidated }
        .map { it.sku }
        .toSet()

    val selectedItems = selectedSkus.mapNotNull(itemsBySku::get)
    val remainingItems = itemsBySku.values.filter { it.sku !in selectedSkus }
    val itemsToValidate = selectedItems.filter { it.sku !in originallyValidated }
    val validatedCount = selectedItems.size - itemsToValidate.size

    val availabilityDatesChanged =
        pickup != initialOrder.pickupDateTime ||
                returnDate != initialOrder.returnDateTime

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = OrderBrown,
            onPrimary = Color.White,
            background = OrderBackground,
            onBackground = OrderInk,
            surface = Color.White,
            onSurface = OrderInk
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
            modifier = Modifier.fillMaxSize().testTag("editOrderRoot"),
            color = OrderBackground
        ) {
            Column(Modifier.fillMaxSize().then(insets)) {
                if (showDesignSystemBars) {
                    EditOrderStatusBar()
                }

                EditOrderHeader(initialOrder.orderId, onCancel, creating)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EditOrderNotice()

                    EditOrderCard("Pelanggan", "editOrderCustomerCard") {
                        Spacer(Modifier.height(10.dp))

                        EditOrderInput(
                            label = "Nama pelanggan",
                            value = customerName,
                            onChange = { customerName = it }
                        )

                        Spacer(Modifier.height(12.dp))

                        EditOrderInput(
                            label = "No. telepon",
                            value = phone,
                            onChange = { phone = it },
                            keyboardType = KeyboardType.Phone
                        )
                    }

                    EditOrderCard("Jenis pesanan", "editOrderTypeCard") {
                        Spacer(Modifier.height(10.dp))

                        EditOrderTypeSelector(
                            selected = orderType,
                            onSelect = { orderType = it }
                        )

                        Spacer(Modifier.height(12.dp))

                        EditOrderInput(
                            label = "Nama paket",
                            value = packageName,
                            onChange = { packageName = it }
                        )

                        if (showTotalField) {
                            Spacer(Modifier.height(12.dp))
                            if (totalEditable) {
                                EditOrderInput(
                                    label = "Total pesanan (Rp)",
                                    value = amountDigits,
                                    onChange = { input ->
                                        amountDigits = input.filter { it in '0'..'9' }.take(15)
                                    },
                                    keyboardType = KeyboardType.Number
                                )
                            } else {
                                OrderText("Total pesanan · ${workspaceMoney(initialOrder.totalAmount)}", 13)
                            }
                        }
                    }

                    EditOrderCard("Tanggal", "editOrderDateCard") {
                        Spacer(Modifier.height(10.dp))

                        EditOrderDateField(
                            label = "Tanggal acara",
                            value = formatOrderDate(eventDate, "d MMM yyyy"),
                            onClick = {
                                pickEditOrderDate(context, eventDate, includeTime = false) {
                                    eventDate = it
                                }
                            }
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            EditOrderDateField(
                                label = "Ambil",
                                value = formatOrderDate(pickup, "d MMM · HH.mm"),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    pickEditOrderDate(context, pickup, includeTime = true) {
                                        pickup = it
                                    }
                                }
                            )

                            EditOrderDateField(
                                label = "Kembali",
                                value = formatOrderDate(returnDate, "d MMM · HH.mm"),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    pickEditOrderDate(context, returnDate, includeTime = true) {
                                        returnDate = it
                                    }
                                }
                            )
                        }
                    }

                    EditOrderItemsCard(
                        items = selectedItems,
                        originallyValidated = originallyValidated,
                        pickup = pickup,
                        returnDate = returnDate,
                        availabilityDatesChanged = availabilityDatesChanged,
                        onRemove = { sku ->
                            selectedSkus = ArrayList(selectedSkus.filterNot { it == sku })
                        },
                        onAdd = { showItemPicker = true }
                    )
                }

                EditOrderFooter(
                    newCount = itemsToValidate.size,
                    validatedCount = validatedCount,
                    showDesignSystemBars = showDesignSystemBars,
                    onCancel = onCancel,
                    onSave = {
                        val total = if (showTotalField && totalEditable) {
                            amountDigits.toLongOrNull() ?: 0L
                        } else initialOrder.totalAmount
                        val message = when {
                            showTotalField && total <= 0L -> "Isi total pesanan yang valid."
                            customerName.isBlank() -> "Isi nama pelanggan."
                            phone.isBlank() -> "Isi nomor telepon pelanggan."
                            packageName.isBlank() -> "Isi nama paket."
                            returnDate <= pickup -> "Waktu kembali harus setelah waktu ambil."
                            selectedItems.isEmpty() -> "Pilih minimal satu barang."
                            else -> null
                        }

                        if (message != null) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            onSave(
                                EditOrderResult(
                                    order = initialOrder.copy(
                                        customerName = customerName.trim(),
                                        phone = phone.trim(),
                                        type = orderType,
                                        packageName = packageName.trim(),
                                        eventDate = eventDate,
                                        pickupDateTime = pickup,
                                        returnDateTime = returnDate,
                                        items = selectedItems,
                                        totalAmount = total
                                    ),
                                    itemSkusToValidate = itemsToValidate.map { it.sku },
                                    removedItemSkus = initialOrder.items
                                        .map { it.sku }
                                        .filterNot { it in selectedSkus }
                                )
                            )
                        }
                    }
                )
            }
        }

        if (showItemPicker) {
            AlertDialog(
                onDismissRequest = { showItemPicker = false },
                title = { Text("Tambah barang") },
                text = {
                    if (remainingItems.isEmpty()) {
                        Text("Semua barang dalam katalog sudah dipilih.")
                    } else {
                        Column(
                            Modifier.heightIn(max = 300.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            remainingItems.forEach { item ->
                                TextButton(
                                    onClick = {
                                        selectedSkus = ArrayList(selectedSkus + item.sku)
                                        showItemPicker = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("${item.name} · ${item.sku}")
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showItemPicker = false }) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}

@Composable
private fun EditOrderHeader(orderId: String, onBack: () -> Unit, creating: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(start = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp)
                .semantics { contentDescription = "Kembali" }
                .testTag("editOrderBack")
        ) {
            OrderLineIcon("M13 20L5 12L13 4 M5 12H19", 24, OrderInk, 1.8f)
        }

        OrderText(
            text = if (creating) "Tambah Pesanan · $orderId" else "Edit Pesanan · $orderId",
            size = 15,
            bold = true,
            maxLines = 1,
            modifier = Modifier.offset(x = (-5).dp).semantics { heading() }
        )
    }
}

@Composable
private fun EditOrderNotice() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .background(OrderBeige, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("editOrderNotice"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OrderLineIcon(
            pathData = "M8 1A7 7 0 1 1 8 15A7 7 0 1 1 8 1 " +
                    "M8 6V11 M8 3.5V3.51",
            size = 16,
            color = OrderHintInk,
            stroke = 1.3f
        )

        Spacer(Modifier.width(8.dp))

        OrderText(
            text = "Hanya barang yang berubah dikirim ulang ke\n" +
                    "karyawan untuk divalidasi.",
            size = 12,
            lineHeight = 16,
            color = OrderHintInk
        )
    }
}

@Composable
private fun EditOrderCard(
    title: String,
    tag: String,
    trailingTitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().testTag(tag),
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, OrderBorder)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OrderText(
                    text = title,
                    size = 14,
                    lineHeight = 18,
                    bold = true,
                    modifier = Modifier.weight(1f).semantics { heading() }
                )

                if (trailingTitle != null) {
                    OrderText(trailingTitle, 12, color = OrderMuted)
                }
            }

            content()
        }
    }
}

@Composable
private fun EditOrderFieldLabel(label: String) {
    OrderText(label, 11, lineHeight = 14, color = OrderMuted)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun EditOrderInput(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(Modifier.fillMaxWidth()) {
        EditOrderFieldLabel(label)

        Surface(
            modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
            color = OrderFieldBackground,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, OrderBorder)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                textStyle = orderTextStyle(13, 18, OrderInk, false),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                cursorBrush = SolidColor(OrderBrown),
                modifier = Modifier.fillMaxWidth()
                    .semantics { contentDescription = label }
                    .testTag("editOrderField-$label"),
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
    }
}

@Composable
private fun EditOrderTypeSelector(
    selected: EditOrderType,
    onSelect: (EditOrderType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(OrderBeige, RoundedCornerShape(12.dp))
            .padding(4.dp)
            .selectableGroup()
    ) {
        EditOrderType.entries.forEach { type ->
            val active = selected == type

            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 36.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (active) OrderBrown else Color.Transparent)
                    .selectable(
                        selected = active,
                        role = Role.Tab,
                        onClick = { onSelect(type) }
                    )
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                OrderText(
                    text = type.label,
                    size = 13,
                    lineHeight = 18,
                    bold = active,
                    color = if (active) Color.White else OrderHintInk
                )
            }
        }
    }
}

@Composable
private fun EditOrderDateField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        EditOrderFieldLabel(label)

        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
                .height(44.dp)
                .semantics {
                    role = Role.Button
                    contentDescription = "$label, $value"
                },
            shape = RoundedCornerShape(12.dp),
            color = OrderFieldBackground,
            border = BorderStroke(1.dp, OrderBorder)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OrderText(value, 13, modifier = Modifier.weight(1f), maxLines = 1)
                Spacer(Modifier.width(4.dp))
                OrderLineIcon(
                    "M4.5 3H13.5Q16 3 16 5.5V13Q16 15.5 13.5 15.5H4.5 " +
                            "Q2 15.5 2 13V5.5Q2 3 4.5 3Z M2 7.5H16 M6 1.5V4.5 M12 1.5V4.5",
                    18, OrderHintInk, 1.3f
                )
            }
        }
    }
}

private fun pickEditOrderDate(
    context: android.content.Context,
    value: Long,
    includeTime: Boolean,
    onSelected: (Long) -> Unit
) {
    val current = Calendar.getInstance().apply { timeInMillis = value }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            val chosen = Calendar.getInstance().apply {
                clear()
                set(year, month, day)
            }

            if (includeTime) {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        chosen.set(Calendar.HOUR_OF_DAY, hour)
                        chosen.set(Calendar.MINUTE, minute)
                        onSelected(chosen.timeInMillis)
                    },
                    current.get(Calendar.HOUR_OF_DAY),
                    current.get(Calendar.MINUTE),
                    true
                ).show()
            } else {
                onSelected(chosen.timeInMillis)
            }
        },
        current.get(Calendar.YEAR),
        current.get(Calendar.MONTH),
        current.get(Calendar.DAY_OF_MONTH)
    ).show()
}

internal fun formatOrderDate(value: Long, pattern: String): String =
    SimpleDateFormat(pattern, IndonesianLocale).format(Date(value))

private fun formatOrderRange(start: Long, end: Long): String {
    val sameMonth = formatOrderDate(start, "yyyy-MM") == formatOrderDate(end, "yyyy-MM")
    return if (sameMonth) {
        "${formatOrderDate(start, "d")}–${formatOrderDate(end, "d MMM yyyy")}"
    } else {
        "${formatOrderDate(start, "d MMM yyyy")}–${formatOrderDate(end, "d MMM yyyy")}"
    }
}

@Composable
private fun EditOrderItemsCard(
    items: List<EditOrderItem>,
    originallyValidated: Set<String>,
    pickup: Long,
    returnDate: Long,
    availabilityDatesChanged: Boolean,
    onRemove: (String) -> Unit,
    onAdd: () -> Unit
) {
    EditOrderCard(
        title = "Pilih barang",
        trailingTitle = "${items.size} item",
        tag = "editOrderItemsCard"
    ) {
        Spacer(Modifier.height(2.dp))

        val range = formatOrderRange(pickup, returnDate)
        OrderText(
            text = if (availabilityDatesChanged) {
                "Ketersediaan perlu dicek untuk $range"
            } else {
                "Ketersediaan dicek untuk $range"
            },
            size = 11,
            lineHeight = 16,
            color = OrderMuted
        )

        Spacer(Modifier.height(12.dp))

        items.forEachIndexed { index, item ->
            val validated = item.sku in originallyValidated

            Box(Modifier.fillMaxWidth()) {
                EditOrderItemRow(
                    item = item,
                    validated = validated,
                    onRemove = { onRemove(item.sku) }
                )

                if (index < items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        thickness = 1.dp,
                        color = OrderDivider
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Surface(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth().height(44.dp)
                .testTag("editOrderAddItem")
                .semantics { role = Role.Button },
            color = Color.White,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val inset = strokeWidth / 2

                    drawRoundRect(
                        color = Color(0xFFB8A794),
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        cornerRadius = CornerRadius(12.dp.toPx()),
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(4.dp.toPx(), 4.dp.toPx())
                            )
                        )
                    )
                },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OrderLineIcon("M3 8H13 M8 3V13", 16, OrderBrown, 1.6f)
                Spacer(Modifier.width(6.dp))
                OrderText("Tambah barang", 14, bold = true, color = OrderBrown)
            }
        }
    }
}

@Composable
private fun EditOrderItemRow(
    item: EditOrderItem,
    validated: Boolean,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(vertical = 8.dp)
            .testTag("editOrderItem-${item.sku}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp)
                .background(OrderBeige, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            val path = when (item.icon) {
                EditOrderItemIcon.Shirt ->
                    "M4 6L8.5 3C10.83 5 13.17 5 15.5 3L20 6L18 10L16 9V20H8V9L6 10Z"

                EditOrderItemIcon.Fabric ->
                    "M6 4H18Q20 4 20 6V18Q20 20 18 20H6Q4 20 4 18V6Q4 4 6 4Z " +
                            "M4 10L14 4 M4 16L20 6 M10 20L20 14"

                EditOrderItemIcon.Veil ->
                    "M3 5C9 2.33 15 2.33 21 5C19 9.67 19 14.67 21 20 " +
                            "C15 17.33 9 17.33 3 20C5 14.67 5 9.67 3 5Z"

                EditOrderItemIcon.Cover ->
                    "M9 3H15Q18 3 18 6V18Q18 21 15 21H9Q6 21 6 18V6Q6 3 9 3Z " +
                            "M12 3V21 M6 9H18"

                EditOrderItemIcon.Shoe ->
                    "M3 17V12C6.33 12 8.67 10.33 10 7L13 9 " +
                            "C14.33 11.67 17 13.33 21 14V17Z"
            }

            OrderLineIcon(path, 24, OrderBrown, 1.5f)
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            OrderText(item.name, 13, lineHeight = 18, bold = true)
            Spacer(Modifier.height(2.dp))
            OrderText(
                text = "${item.sku} · ${item.size} · ${item.boxId}",
                size = 11,
                lineHeight = 14,
                color = OrderMuted
            )
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .widthIn(min = 62.dp)
                .heightIn(min = 22.dp)
                .background(
                    if (validated) OrderAvailableBackground else OrderNewBackground,
                    CircleShape
                )
                .padding(horizontal = 6.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            OrderText(
                text = if (validated) "Tersedia" else "Baru",
                size = 10,
                lineHeight = 14,
                bold = true,
                color = if (validated) OrderAvailableInk else OrderNewInk
            )
        }

        Spacer(Modifier.width(3.dp))

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .size(31.dp, 48.dp)
                .semantics { contentDescription = "Hapus ${item.name}" }
        ) {
            Box(
                modifier = Modifier.size(22.dp).background(OrderBeige, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                OrderLineIcon("M3 3L11 11 M11 3L3 11", 14, OrderHintInk, 1.4f)
            }
        }
    }
}

@Composable
private fun EditOrderFooter(
    newCount: Int,
    validatedCount: Int,
    showDesignSystemBars: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(OrderFieldBackground)
            .then(
                if (showDesignSystemBars) Modifier else Modifier.navigationBarsPadding()
            )
            .testTag("editOrderFooter")
    ) {
        HorizontalDivider(thickness = 1.dp, color = OrderBorder)

        OrderText(
            text = "$newCount barang baru dikirim ulang · $validatedCount barang tetap tervalidasi",
            size = 12,
            lineHeight = 18,
            color = OrderMuted,
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
                    .testTag("editOrderCancel"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, OrderBrown),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = OrderBrown
                ),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                OrderText("Batal", 13, bold = true, color = OrderBrown)
            }

            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f).height(44.dp)
                    .testTag("editOrderSave"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrderBrown,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                OrderText(
                    text = "Simpan & kirim ke karyawan",
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
                        .background(OrderInk, CircleShape)
                )
            }
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun OrderLineIcon(
    pathData: String,
    size: Int,
    color: Color,
    stroke: Float
) {
    val path = remember(pathData) {
        PathParser().parsePathString(pathData).toPath()
    }

    Canvas(Modifier.size(size.dp)) {
        val viewport = size.toFloat()
        scale(
            scaleX = this.size.width / viewport,
            scaleY = this.size.height / viewport,
            pivot = Offset.Zero
        ) {
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = stroke,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

private fun orderTextStyle(
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
private fun OrderText(
    text: String,
    size: Int,
    modifier: Modifier = Modifier,
    lineHeight: Int = size + 4,
    color: Color = OrderInk,
    bold: Boolean = false,
    align: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        style = orderTextStyle(size, lineHeight, color, bold),
        textAlign = align,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun EditOrderStatusBar() {
    Row(
        modifier = Modifier.fillMaxWidth().height(44.dp)
            .padding(start = 24.dp, end = 33.dp)
            .offset(y = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OrderText("09.41", 12, bold = true)
        Spacer(Modifier.weight(1f))

        Canvas(Modifier.size(width = 18.dp, height = 12.dp)) {
            for (index in 0..3) {
                val barHeight = (4 + index * 2).dp.toPx()
                drawRoundRect(
                    color = OrderInk,
                    topLeft = Offset((index * 5).dp.toPx(), 10.dp.toPx() - barHeight),
                    size = Size(3.dp.toPx(), barHeight),
                    cornerRadius = CornerRadius(1.dp.toPx())
                )
            }
        }

        Spacer(Modifier.width(8.dp))
        OrderText("86%", 11)
        Spacer(Modifier.width(4.dp))

        Canvas(Modifier.size(width = 22.dp, height = 11.dp)) {
            drawRoundRect(
                color = OrderInk,
                topLeft = Offset(0.5.dp.toPx(), 0.5.dp.toPx()),
                size = Size(21.dp.toPx(), 10.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
            drawRoundRect(
                color = OrderInk,
                topLeft = Offset(2.5.dp.toPx(), 2.5.dp.toPx()),
                size = Size(15.dp.toPx(), 6.dp.toPx()),
                cornerRadius = CornerRadius(1.5.dp.toPx())
            )
        }
    }
}

@Preview(
    name = "Edit Pesanan · WA-26041",
    widthDp = 393,
    heightDp = 1372,
    showBackground = true
)
@Composable
private fun PreviewEditPesanan() {
    EditPesananScreen(
        initialOrder = SampleEditOrder,
        catalog = SampleEditOrder.items,
        onCancel = {},
        onSave = {},
        showDesignSystemBars = true
    )
}