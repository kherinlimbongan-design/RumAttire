package com.example.rumattire

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.print.PrintHelper
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.util.Locale

internal fun inventoryListItem(item: InventoryItemDraft): InventoriItem {
    val appearance = sampleItems.find { it.sku == item.sku }
    return InventoriItem(
        name = item.name,
        stock = "Stok: ${item.stock} unit",
        sku = item.sku,
        size = listOf(item.size, item.color).filter { it.isNotBlank() }.joinToString(" · "),
        category = item.category,
        boxCode = item.box.id,
        status = if (item.stock == 0) "Stok habis" else appearance?.status ?: "Tersedia",
        statusBooked = item.stock == 0 || appearance?.statusBooked == true,
        icon = appearance?.icon ?: R.drawable.ic_inv_baju
    )
}

internal fun inventoryOrderItem(item: InventoryItemDraft): EditOrderItem {
    val icon = when (item.category.lowercase(Locale.ROOT)) {
        "kain" -> EditOrderItemIcon.Fabric
        "veil" -> EditOrderItemIcon.Veil
        "cover" -> EditOrderItemIcon.Cover
        "sepatu" -> EditOrderItemIcon.Shoe
        else -> EditOrderItemIcon.Shirt
    }
    return EditOrderItem(item.sku, item.name, item.size, item.box.id, icon)
}

internal fun orderListItem(order: EditOrderData, status: String): OrderItem {
    val template = sampleOrders.first()
    val checked = order.items.count { it.alreadyValidated }
    return template.copy(
        id = order.orderId,
        customer = order.customerName,
        detail = "#${order.orderId} · ${order.type.label} · ${order.packageName}",
        eventDate = formatOrderDate(order.eventDate, "d MMM yyyy"),
        pickupDate = formatOrderDate(order.pickupDateTime, "d MMM · HH.mm"),
        validationText = "$checked dari ${order.items.size} ada",
        validationCount = checked,
        status = status,
        progress = if (order.items.isEmpty()) 0f else checked.toFloat() / order.items.size,
        filterKey = status
    )
}

internal fun orderConflicts(
    draft: EditOrderData,
    previous: EditOrderData?,
    workspace: OwnerWorkspace
): List<String> {
    val datesChanged = previous == null || draft.pickupDateTime != previous.pickupDateTime ||
        draft.returnDateTime != previous.returnDateTime
    val previousSkus = previous?.items?.map { it.sku }.orEmpty().toSet()
    val overlapping = workspace.orders.values.filter {
        it.orderId != draft.orderId && it.type == EditOrderType.Rental &&
            it.pickupDateTime < draft.returnDateTime && draft.pickupDateTime < it.returnDateTime
    }
    return draft.items.filter { item ->
        if (!datesChanged && item.sku in previousSkus) false
        else {
            val stock = workspace.inventory[item.sku]?.stock ?: 0
            overlapping.count { order -> order.items.any { it.sku == item.sku } } >= stock
        }
    }.map { it.sku }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WorkspacePage(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { TextButton(onClick = onBack) { Text("Kembali") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)
                .imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
internal fun MissingRecord(onBack: () -> Unit) {
    WorkspacePage("Data tidak tersedia", onBack) {
        Text("Buka kembali daftar untuk memilih data yang tersedia pada sesi ini.")
    }
}

@Composable
internal fun ManualBoxScreen(onBack: () -> Unit, onOpen: (String) -> Unit) {
    var id by rememberSaveable { mutableStateOf("") }
    WorkspacePage("Cari box dengan ID", onBack) {
        OutlinedTextField(
            value = id, onValueChange = { id = it }, singleLine = true,
            label = { Text("ID box") }, placeholder = { Text("BX-028") }
        )
        Button(onClick = { onOpen(id) }, enabled = id.isNotBlank()) { Text("Lihat isi box") }
    }
}

internal fun boxQrBitmap(id: String): Bitmap {
    val size = 512
    val matrix = QRCodeWriter().encode(id, BarcodeFormat.QR_CODE, size, size)
    val pixels = IntArray(size * size) { index ->
        if (matrix[index % size, index / size]) android.graphics.Color.BLACK
        else android.graphics.Color.WHITE
    }
    return Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888)
}

@Composable
internal fun PrintBoxScreen(id: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val bitmap = remember(id) { boxQrBitmap(id) }
    WorkspacePage("Cetak label QR", onBack) {
        Text(id, style = MaterialTheme.typography.titleLarge)
        Image(
            bitmap = bitmap.asImageBitmap(), contentDescription = "QR box $id",
            modifier = Modifier.size(220.dp)
        )
        Button(onClick = { PrintHelper(context).printBitmap("Rum Attire · $id", bitmap) }) {
            Text("Cetak label QR")
        }
    }
}
