package com.example.rumattire

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.rumattire.ui.theme.Typography
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bars = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT,
            0xFFF7F2E9.toInt()
        )
        enableEdgeToEdge(statusBarStyle = bars, navigationBarStyle = bars)
        val accounts = LocalAccounts(applicationContext)
        val workspace = ViewModelProvider(this)[OwnerWorkspace::class.java]
        if (savedInstanceState != null && workspace.account == null) {
            accounts.remembered()?.let(workspace::open)
        }

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF4A3328),
                    background = Color(0xFFF7F2E9),
                    surface = Color(0xFFFCF9F3)
                ),
                typography = Typography
            ) {
                RumAttireApp(accounts, workspace)
            }
        }
    }
}

private val ownerTabs = listOf("home", "inventory", "orders", "boxes", "more")
private val ownerTabLabels = listOf("Beranda", "Inventori", "Pesanan", "Box", "Lainnya")

@Composable
private fun RumAttireApp(accounts: LocalAccounts, workspace: OwnerWorkspace) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var stack by rememberSaveable { mutableStateOf(arrayListOf("splash")) }
    var loginEmail by rememberSaveable { mutableStateOf("") }
    var loginName by rememberSaveable { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var showLogout by rememberSaveable { mutableStateOf(false) }
    val account = workspace.account
    val requested = stack.last()
    val entryRoutes = setOf("splash", "onboarding", "login", "register", "help")
    val route = if (account == null && requested !in entryRoutes) "login" else requested
    val section = route.substringBefore('/')
    val id = route.substringAfter('/', "")
    val notifyUser: (String) -> Unit = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }

    fun go(next: String) {
        if (route != next) stack = ArrayList(stack + next)
    }

    fun back() {
        if (stack.size > 1) stack = ArrayList(stack.dropLast(1))
        else if (workspace.account != null && route != "home") stack = arrayListOf("home")
    }

    fun tab(index: Int) {
        val target = ownerTabs.getOrNull(index) ?: return
        stack = if (target == "home") arrayListOf("home") else arrayListOf("home", target)
    }

    fun tabLabel(label: String) = tab(ownerTabLabels.indexOf(label))
    fun loginRoot() { stack = arrayListOf("login") }

    fun restoreOrLogin() {
        val remembered = workspace.account ?: accounts.remembered()
        if (remembered != null) {
            workspace.open(remembered)
            stack = arrayListOf("home")
        } else loginRoot()
    }

    fun authenticate(action: suspend () -> Unit) {
        if (busy) return
        busy = true
        scope.launch {
            try {
                action()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                notifyUser("Data akun lokal belum dapat diproses.")
            } finally {
                busy = false
            }
        }
    }

    fun openBox(code: String) {
        val normalized = code.trim().uppercase(Locale.ROOT)
        if (workspace.boxes.containsKey(normalized)) go("box/$normalized")
        else notifyUser("Box $normalized tidak ditemukan.")
    }

    val notifications = { go("notifications") }

    BackHandler(enabled = busy || stack.size > 1 || requested != route) {
        if (!busy) {
            if (requested != route) loginRoot() else back()
        }
    }

    key(route) {
        when (section) {
            "splash" -> {
                MaterialTheme(colorScheme = splashColorScheme) { SplashScreen() }
                LaunchedEffect(Unit) {
                    delay(2_000)
                    if (accounts.onboardingComplete) restoreOrLogin()
                    else stack = arrayListOf("onboarding")
                }
            }

            "onboarding" -> RumAttireOnboarding(onFinished = {
                accounts.onboardingComplete = true
                restoreOrLogin()
            })

            "login" -> LoginScreen(
                modifier = Modifier.safeDrawingPadding().imePadding(),
                initialEmail = loginEmail,
                initialName = loginName,
                onNameChange = { loginName = it },
                submitting = busy,
                onRegisterClick = {
                    loginRoot()
                    go("register")
                },
                onForgotPasswordClick = {
                    if (!busy) {
                        loginRoot()
                        go("help")
                    }
                },
                onLoginClick = { email, password, remember ->
                    val submittedName = loginName
                    if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() || password.isBlank()) {
                        notifyUser("Isi email dan kata sandi yang valid.")
                    } else authenticate {
                        val loggedIn = withContext(Dispatchers.IO) {
                            accounts.login(email, password, submittedName, remember)
                        }
                        if (loggedIn == null) notifyUser("Email atau kata sandi tidak sesuai. Daftar jika belum punya akun.")
                        else {
                            workspace.open(loggedIn)
                            loginName = ""
                            loginEmail = ""
                            stack = arrayListOf("home")
                        }
                    }
                }
            )

            "register" -> RegisterScreen(
                modifier = Modifier.safeDrawingPadding().imePadding(),
                onBackClick = { if (!busy) loginRoot() },
                onLoginClick = { if (!busy) loginRoot() },
                onRegisterClick = { name, email, phone, atelier, password ->
                    if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        notifyUser("Masukkan alamat email yang valid.")
                    } else authenticate {
                        val registered = withContext(Dispatchers.IO) {
                            accounts.register(name, email, phone, atelier, password)
                        }
                        if (registered) {
                            loginEmail = email.trim()
                            loginName = name.trim()
                            notifyUser("Pendaftaran berhasil. Silakan masuk.")
                            loginRoot()
                        } else notifyUser("Pendaftaran gagal. Email ini mungkin sudah terdaftar.")
                    }
                }
            )

            "help" -> WorkspacePage("Bantuan masuk", ::back) {
                Text("Hubungi admin atelier jika lupa kata sandi.")
                Text("Pemulihan lewat email memerlukan layanan akun Rum Attire.")
            }

            "home" -> if (account != null) {
                val waiting = workspace.orders.values
                    .filter { workspace.status(it) == "Menunggu konfirmasi" }
                    .sortedBy { it.orderId }
                val unpaid = workspace.payments.values
                    .filter { it.remainingAmount > 0 }.sortedBy { it.orderId }
                val received = workspace.payments.values.sumOf { it.amountReceived }
                val total = received + unpaid.sumOf { it.remainingAmount } +
                    workspace.orders.values.filter { it.orderId !in workspace.payments }
                        .sumOf { workspace.total(it.orderId) }
                val firstUnpaid = unpaid.firstOrNull()
                BerandaScreen(
                    greeting = "Selamat datang, ${account.name}",
                    dateLabel = "OWNER · " + formatOrderDate(System.currentTimeMillis(), "EEEE, d MMMM")
                        .uppercase(Locale("id", "ID")),
                    pendingOrderText = "${waiting.size} pesanan menunggu konfirmasi Anda",
                    receivedPercent = if (total == 0L) 0 else (100.0 * received / total).toInt(),
                    receivedAmount = workspaceMoney(received),
                    totalOrderText = "${workspace.orders.size} pesanan · total ${workspaceMoney(total)}",
                    actionCount = waiting.size + unpaid.size,
                    approvalDetail = waiting.firstOrNull()?.let { "${it.customerName} · ${it.orderId}" }
                        ?: "Belum ada pesanan siap dikonfirmasi",
                    approvalBadge = "${waiting.size} pesanan",
                    paymentTitle = firstUnpaid?.let {
                        "Pelunasan ${workspace.orders[it.orderId]?.customerName.orEmpty()} · ${workspaceMoney(it.remainingAmount)}"
                    } ?: "Tidak ada sisa tagihan",
                    paymentDetail = "Buka Pembayaran",
                    preparingCount = workspace.orders.values.count {
                        workspace.status(it) in setOf("Menunggu persiapan", "Sedang diperiksa")
                    },
                    returnCount = workspace.orders.values.count {
                        it.orderId in workspace.payments && it.type == EditOrderType.Rental
                    },
                    activeCount = workspace.orders.size,
                    onNotificationClick = notifications,
                    onPendingOrderClick = { tab(2) },
                    onApprovalClick = { waiting.firstOrNull()?.let { go("order/${it.orderId}") } ?: tab(2) },
                    onPaymentClick = { go("payments") },
                    onNavSelected = ::tab,
                    onSummaryClick = { index ->
                        when (index) {
                            0 -> go("preparation")
                            1 -> go("returns")
                            else -> tab(2)
                        }
                    }
                )
            }

            "inventory" -> InventoriScreen(
                onBackClick = ::back,
                onNotificationClick = notifications,
                onAddItemClick = { go("editItem") },
                onItemClick = { go("item/$it") },
                onNavSelected = ::tab,
                items = workspace.inventory.values.sortedBy { it.sku }.map(::inventoryListItem)
            )

            "item" -> workspace.inventory[id]?.let { item ->
                DetailInventoriScreen(
                    itemCode = id,
                    item = item,
                    history = workspace.itemHistory[id].orEmpty(),
                    onBackClick = ::back,
                    onMoveBoxClick = { go("moveItem/$id") },
                    onEditItemClick = { go("editItem/$id") },
                    onViewBoxClick = { openBox(item.box.id) }
                )
            } ?: MissingRecord(::back)

            "editItem" -> {
                val item = workspace.inventory[id]
                if (item == null && id.isNotEmpty()) {
                    MissingRecord(::back)
                } else {
                    val draft = item ?: SampleItem.copy(
                        name = "", sku = "", size = "", color = "", stock = 0,
                        box = InventoryEditBox("", ""), photoName = "Belum ada foto"
                    )
                    EditInventoryItemScreen(
                        initialItem = draft,
                        categories = workspace.inventory.values.map { it.category }.distinct(),
                        boxes = workspace.boxes.keys.sorted().map(workspace::editBox),
                        scanIntent = Intent(context, ScanQrActivity::class.java),
                        creating = item == null,
                        onCancel = ::back,
                        onSave = { updated ->
                            if (workspace.saveItem(item?.sku, updated)) {
                                notifyUser("Perubahan item disimpan untuk sesi ini.")
                                if (item != null && item.sku != updated.sku) {
                                    stack = arrayListOf("home", "inventory", "item/${updated.sku}")
                                } else back()
                            } else notifyUser("SKU sudah dipakai atau masih terhubung dengan pesanan.")
                        }
                    )
                }
            }

            "moveItem" -> workspace.inventory[id]?.let { item ->
                WorkspacePage("Pindah box · $id", ::back) {
                    workspace.boxes.values.sortedBy { it.code }.forEach { box ->
                        OutlinedButton(
                            onClick = {
                                if (workspace.saveItem(id, item.copy(box = workspace.editBox(box.code)))) back()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = box.code != item.box.id
                        ) { Text("${box.code} · ${box.location}") }
                    }
                }
            } ?: MissingRecord(::back)

            "boxes" -> BoxManagementScreen(
                onBackClick = ::back,
                onNotificationClick = notifications,
                onBoxClick = ::openBox,
                onAddBoxClick = { go("addBox") },
                onScanQrClick = { go("scan") },
                onNavSelected = ::tab,
                boxes = workspace.boxes.values.sortedBy { it.code }.map { box ->
                    val contents = workspace.contents(box.code)
                    box.copy(
                        itemCount = "${contents.size} item",
                        contents = if (contents.isEmpty()) "Box kosong"
                        else "Isi: " + contents.joinToString(", ") { it.name }
                    )
                }
            )

            "box" -> workspace.boxes[id]?.let { box ->
                DetailBoxScreen(
                    boxCode = box.code,
                    box = box,
                    items = workspace.contents(id),
                    onBackClick = ::back,
                    onNotificationClick = notifications,
                    onPrintQrClick = { go("printBox/$id") },
                    onManageContentClick = { go("manageBox/$id") },
                    onItemClick = { go("item/$it") },
                    onNavSelected = ::tab
                )
            } ?: MissingRecord(::back)

            "addBox" -> {
                val nextId = generateSequence(1) { it + 1 }
                    .map { "BX-" + it.toString().padStart(3, '0') }
                    .first { it !in workspace.boxes }
                AddBoxScreen(
                    idBox = nextId,
                    initialNamaBox = "",
                    onBack = ::back,
                    onCancel = ::back,
                    onNotification = notifications,
                    onNavigate = ::tabLabel,
                    onSave = { form ->
                        if (form.namaBox.isBlank()) notifyUser("Nama box harus diisi.")
                        else {
                            workspace.boxes[form.idBox] = BoxItem(
                                form.idBox, form.namaBox.trim(), form.lokasiPenyimpanan,
                                "0 item", "Box kosong"
                            )
                            workspace.notify("Box ditambahkan", form.idBox)
                            back()
                        }
                    }
                )
            }

            "scan" -> ScanQrScreen(
                onBack = ::back,
                onNotification = notifications,
                onNavigate = ::tabLabel,
                onOpenBox = ::openBox,
                onManualEntry = { go("manualBox") }
            )

            "manualBox" -> ManualBoxScreen(::back, ::openBox)
            "printBox" -> if (id in workspace.boxes) PrintBoxScreen(id, ::back) else MissingRecord(::back)

            "manageBox" -> workspace.boxes[id]?.let { box ->
                WorkspacePage("Kelola isi ${box.code}", ::back) {
                    Text("Pilih barang untuk mengubah lokasi penyimpanannya.")
                    workspace.contents(id).forEach { item ->
                        OutlinedButton(onClick = { go("moveItem/${item.sku}") }, modifier = Modifier.fillMaxWidth()) {
                            Text("${item.name} · ${item.sku}")
                        }
                    }
                    Button(onClick = { tab(1) }) { Text("Buka inventori") }
                }
            } ?: MissingRecord(::back)

            "preparation", "returns" -> {
                val preparation = section == "preparation"
                val relevant = workspace.orders.values.filter {
                    if (preparation) workspace.status(it) in setOf("Menunggu persiapan", "Sedang diperiksa")
                    else it.orderId in workspace.payments && it.type == EditOrderType.Rental
                }.sortedBy { it.orderId }

                WorkspacePage(if (preparation) "Persiapan" else "Jadwal pengembalian", ::back) {
                    if (relevant.isEmpty()) Text("Belum ada pesanan pada daftar ini.")
                    relevant.forEach { order ->
                        OutlinedButton(
                            onClick = { go("order/${order.orderId}") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "${order.customerName} · " + if (preparation) workspace.status(order)
                                else formatOrderDate(order.returnDateTime, "d MMM yyyy · HH.mm")
                            )
                        }
                    }
                }
            }

            "orders" -> PesananScreen(
                onBackClick = ::back,
                onNotificationClick = notifications,
                onCreateOrderClick = { go("editOrder") },
                onOrderClick = { go("order/$it") },
                onNavSelected = ::tab,
                orders = workspace.orders.values.sortedByDescending { it.orderId }.map {
                    orderListItem(it, workspace.status(it))
                }
            )

            "order" -> workspace.orders[id]?.let { order ->
                DetailPesananScreen(
                    orderCode = id,
                    order = order,
                    orderStatus = workspace.status(order),
                    payment = workspace.payments[id],
                    orderTotal = workspace.total(id),
                    onBackClick = ::back,
                    onNotificationClick = notifications,
                    onEditOrderClick = { go("editOrder/$id") },
                    onConfirmOrderClick = {
                        when {
                            id in workspace.payments -> go("payments")
                            order.items.isEmpty() || order.items.any { !it.alreadyValidated } ->
                                notifyUser("Konfirmasi tersedia setelah semua barang divalidasi.")
                            else -> go("confirm/$id")
                        }
                    },
                    onItemClick = ::openBox
                )
            } ?: MissingRecord(::back)

            "editOrder" -> {
                val existing = workspace.orders[id]
                if (existing == null && id.isNotEmpty()) MissingRecord(::back)
                else {
                    val newId = generateSequence(26043) { it + 1 }
                        .map { "WA-$it" }.first { it !in workspace.orders }
                    val draft = existing ?: SampleEditOrder.copy(
                        orderId = newId, customerName = "", phone = "", packageName = "",
                        eventDate = System.currentTimeMillis(), pickupDateTime = System.currentTimeMillis(),
                        returnDateTime = System.currentTimeMillis() + 86_400_000L,
                        items = emptyList(), totalAmount = 0L
                    )
                    EditPesananScreen(
                        initialOrder = draft,
                        catalog = workspace.inventory.values.filter { it.stock > 0 }.map(::inventoryOrderItem),
                        creating = existing == null,
                        showTotalField = true,
                        totalEditable = id !in workspace.payments,
                        onCancel = ::back,
                        onSave = { result ->
                            val conflicts = orderConflicts(result.order, existing, workspace)
                            if (conflicts.isNotEmpty()) {
                                notifyUser("Stok tidak mencukupi pada tanggal tersebut: ${conflicts.joinToString()}.")
                            } else {
                                workspace.orders[result.order.orderId] = result.order
                                workspace.notify(
                                    if (existing == null) "Pesanan dibuat" else "Pesanan diperbarui",
                                    "${result.order.orderId} · ${result.itemSkusToValidate.size} barang perlu divalidasi"
                                )
                                back()
                            }
                        }
                    )
                }
            }

            "confirm" -> workspace.orders[id]?.let { order ->
                val total = workspace.total(id)
                if (id in workspace.payments || order.items.isEmpty() || order.items.any { !it.alreadyValidated }) {
                    MissingRecord(::back)
                } else {
                    KonfirmasiPesananScreen(
                        order = OrderConfirmationInfo(
                            id, order.customerName, order.type.label, order.packageName,
                            formatOrderDate(order.eventDate, "d MMM yyyy"),
                            formatOrderDate(order.pickupDateTime, "d MMM · HH.mm"),
                            total, order.items.count { it.alreadyValidated }, order.items.size
                        ),
                        initialDeposit = total / 2,
                        initialReceivedDate = confirmationSampleDate(3),
                        initialDueDate = order.pickupDateTime,
                        onCancel = ::back,
                        onConfirm = {
                            workspace.payments[id] = it
                            workspace.notify("Pesanan terkonfirmasi", "$id · ${it.paymentMethod.label}")
                            back()
                        }
                    )
                }
            } ?: MissingRecord(::back)

            "payments" -> WorkspacePaymentScreen(
                workspace = workspace,
                onBack = ::back,
                onNotification = notifications,
                onNavigate = ::tabLabel,
                onOpenOrder = { go("order/$it") },
                onRecordSettlement = { go("settle/$it") }
            )

            "settle" -> workspace.payments[id]?.let { payment ->
                WorkspacePage("Catat pelunasan", ::back) {
                    Text(id)
                    Text("Sisa tagihan ${workspaceMoney(payment.remainingAmount)}")
                    Button(
                        enabled = payment.remainingAmount > 0,
                        onClick = {
                            workspace.payments[id] = payment.copy(
                                paymentMethod = ConfirmationPaymentMethod.PaidInFull,
                                amountReceived = payment.amountReceived + payment.remainingAmount,
                                remainingAmount = 0,
                                receivedDateMillis = System.currentTimeMillis(),
                                settlementDueDateMillis = null
                            )
                            workspace.notify("Pelunasan dicatat", id)
                            back()
                        }
                    ) { Text("Simpan pelunasan") }
                }
            } ?: MissingRecord(::back)

            "more" -> if (account != null) {
                LainnyaOwnerScreen(
                    name = account.name,
                    email = account.email,
                    avatarInitial = account.name.take(1).uppercase(),
                    paymentCount = workspace.payments.values.count { it.remainingAmount > 0 },
                    notificationCount = workspace.notices.size,
                    onOpenProfile = { go("profile") },
                    onOpenPayment = { go("payments") },
                    onOpenNotifications = notifications,
                    onOpenAbout = { go("about") },
                    onLogout = { showLogout = true },
                    onNavigate = ::tabLabel
                )

                if (showLogout) {
                    LogoutDialog(
                        accountName = account.name,
                        accountEmail = account.email,
                        onDismiss = { if (!busy) showLogout = false },
                        onConfirmLogout = {
                            authenticate {
                                if (withContext(Dispatchers.IO) { accounts.logout() }) {
                                    showLogout = false
                                    loginEmail = account.email
                                    loginName = ""
                                    workspace.reset()
                                    loginRoot()
                                } else notifyUser("Sesi belum dapat diakhiri.")
                            }
                        }
                    )
                }
            }

            "profile" -> if (account != null) {
                ProfileScreen(
                    profile = ProfileUiData(
                        account.name, account.email, "Owner", "Akses Owner",
                        "Diubah " + formatOrderDate(account.passwordChangedAt, "d MMM yyyy")
                    ),
                    onBack = ::back,
                    onChangePassword = { go("password") }
                )
            }

            "password" -> if (account != null) {
                WorkspacePasswordScreen(busy, ::back) { current, replacement ->
                    authenticate {
                        val updated = withContext(Dispatchers.IO) {
                            accounts.changePassword(account.email, current, replacement)
                        }
                        if (updated == null) notifyUser("Kata sandi lama tidak sesuai atau perubahan gagal disimpan.")
                        else {
                            workspace.account = updated
                            notifyUser("Kata sandi diperbarui.")
                            back()
                        }
                    }
                }
            }

            "about" -> AboutScreen(
                onBack = ::back,
                onNotifications = notifications,
                onNavigate = { tab(it.ordinal) },
                hasUnreadNotifications = workspace.notices.isNotEmpty()
            )

            "notifications" -> WorkspacePage("Notifikasi", ::back) {
                if (workspace.notices.isEmpty()) Text("Belum ada notifikasi pada sesi ini.")
                workspace.notices.forEach { notice ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(notice.title, style = MaterialTheme.typography.titleSmall)
                            Text(notice.detail, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            else -> MissingRecord(::back)
        }
    }
}

@Composable
private fun WorkspacePasswordScreen(
    busy: Boolean,
    onBack: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var replacement by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    val valid = current.isNotEmpty() && replacement.length >= 8 && replacement == confirmation

    WorkspacePage("Ubah kata sandi", onBack = { if (!busy) onBack() }) {
        OutlinedTextField(current, { current = it }, label = { Text("Kata sandi lama") },
            singleLine = true, visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(replacement, { replacement = it }, label = { Text("Kata sandi baru") },
            singleLine = true, visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(confirmation, { confirmation = it }, label = { Text("Ulangi kata sandi baru") },
            singleLine = true, visualTransformation = PasswordVisualTransformation())
        Text("Gunakan minimal 8 karakter dan isi ulang kata sandi baru.")
        Button(onClick = { onSave(current, replacement) }, enabled = valid && !busy) {
            Text("Simpan kata sandi")
        }
    }
}
