package com.example.rumattire

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.util.Locale

internal data class WorkspaceNotice(val title: String, val detail: String)

internal class OwnerWorkspace : ViewModel() {
    var account by mutableStateOf<LocalAccount?>(null)
    private var workspaceEmail: String? = null
    val inventory = mutableStateMapOf<String, InventoryItemDraft>()
    val boxes = mutableStateMapOf<String, BoxItem>()
    val orders = mutableStateMapOf<String, EditOrderData>()
    val payments = mutableStateMapOf<String, OrderConfirmationResult>()
    val notices = mutableStateListOf<WorkspaceNotice>()
    val itemHistory = mutableStateMapOf<String, List<String>>()

    fun open(account: LocalAccount) {
        if (workspaceEmail != account.email) {
            reset()
            workspaceEmail = account.email
        }
        this.account = account
    }

    fun reset() {
        account = null
        workspaceEmail = null
        inventory.clear()
        boxes.clear()
        orders.clear()
        payments.clear()
        notices.clear()
        itemHistory.clear()

        boxItems.forEach { boxes[it.code] = it }
        boxes["BX-007"] = BoxItem("BX-007", "Box BX-007", "Lokasi belum dicatat", "", "")
        sampleItems.forEach { item ->
            inventory[item.sku] = InventoryItemDraft(
                item.name, item.sku, item.category,
                item.size.substringBefore(" ·"), item.size.substringAfter(" ·", ""),
                Regex("\\d+").find(item.stock)?.value?.toIntOrNull() ?: 0,
                editBox(item.boxCode),
                if (item.sku == SampleItem.sku) SampleItem.photoName else "Belum ada foto"
            )
        }
        SampleEditOrder.items.filterNot { it.sku in inventory }.forEach { item ->
            inventory[item.sku] = InventoryItemDraft(
                item.name, item.sku, when (item.icon) {
                    EditOrderItemIcon.Fabric -> "Kain"
                    EditOrderItemIcon.Cover -> "Cover"
                    EditOrderItemIcon.Shoe -> "Sepatu"
                    EditOrderItemIcon.Veil -> "Veil"
                    EditOrderItemIcon.Shirt -> "Kebaya"
                }, item.size, "", 1, editBox(item.boxId), "Belum ada foto"
            )
        }

        orders["WA-26041"] = SampleEditOrder.copy(
            items = SampleEditOrder.items.filter { it.alreadyValidated }
        )
        orders["WA-26042"] = SampleEditOrder.copy(
            orderId = "WA-26042",
            customerName = "Ayu & Fajar",
            phone = "",
            totalAmount = 5_000_000L,
            packageName = "Paket Resepsi",
            eventDate = sampleOrderDate(6),
            pickupDateTime = sampleOrderDate(5, 10),
            returnDateTime = sampleOrderDate(7, 17),
            items = SampleEditOrder.items.take(4).map { it.copy(alreadyValidated = true) }
        )
        orders["WA-26038"] = SampleEditOrder.copy(
            orderId = "WA-26038",
            customerName = "Dinda",
            phone = "",
            totalAmount = 4_500_000L,
            packageName = "Paket Sewa",
            eventDate = sampleOrderDate(-2),
            pickupDateTime = sampleOrderDate(-3),
            returnDateTime = sampleOrderDate(-1),
            items = SampleEditOrder.items.take(2)
        )
        payments["WA-26042"] = OrderConfirmationResult(
            "WA-26042", ConfirmationPaymentMethod.Deposit,
            2_000_000L, 3_000_000L, sampleOrderDate(1), sampleOrderDate(7)
        )
        payments["WA-26038"] = OrderConfirmationResult(
            "WA-26038", ConfirmationPaymentMethod.PaidInFull,
            4_500_000L, 0L, sampleOrderDate(-2), null
        )
    }

    fun editBox(id: String): InventoryEditBox =
        InventoryEditBox(id, boxes[id]?.location.orEmpty())

    fun contents(id: String) = inventory.values.filter { it.box.id == id }.sortedBy { it.sku }

    fun saveItem(originalSku: String?, item: InventoryItemDraft): Boolean {
        val match = inventory.keys.firstOrNull { it.equals(item.sku, ignoreCase = true) }
        if (match != null && match != originalSku) return false
        val old = originalSku?.let { inventory[it] }
        if (originalSku != null && item.sku != originalSku &&
            orders.values.any { order -> order.items.any { it.sku == originalSku } }) return false
        val history = old?.let { itemHistory[it.sku] }.orEmpty()
        if (old != null && old.box.id != item.box.id) {
            itemHistory[item.sku] = listOf("Dari ${old.box.id} ke ${item.box.id}") + history
            notify("Barang dipindah", "${item.name} · ${old.box.id} → ${item.box.id}")
        } else if (history.isNotEmpty()) {
            itemHistory[item.sku] = history
        }
        if (originalSku != null && originalSku != item.sku) {
            inventory.remove(originalSku)
            itemHistory.remove(originalSku)
        }
        inventory[item.sku] = item
        return true
    }

    fun notify(title: String, detail: String) {
        notices.add(0, WorkspaceNotice(title, detail))
    }

    fun status(order: EditOrderData): String {
        val payment = payments[order.orderId]
        return when {
            order.items.isNotEmpty() && order.items.none { it.alreadyValidated } -> "Menunggu persiapan"
            order.items.any { !it.alreadyValidated } -> "Sedang diperiksa"
            payment != null && payment.remainingAmount == 0L -> "Terkonfirmasi (Lunas)"
            payment != null -> "Terkonfirmasi (DP)"
            else -> "Menunggu konfirmasi"
        }
    }

    fun total(orderId: String): Long = orders[orderId]?.totalAmount ?: 0L
}

internal fun workspaceMoney(amount: Long): String =
    "Rp" + NumberFormat.getIntegerInstance(Locale("id", "ID")).format(amount)
