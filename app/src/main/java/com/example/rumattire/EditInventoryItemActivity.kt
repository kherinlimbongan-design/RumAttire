package com.example.rumattire

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val EditBackground = Color(0xFFF7F2E9)
private val EditField = Color(0xFFFBF8F2)
private val EditInk = Color(0xFF3B2A22)
private val EditBrown = Color(0xFF4A3328)
private val EditMuted = Color(0xFF8B7B70)
private val EditBorder = Color(0xFFE6DACA)
private val EditIconBackground = Color(0xFFF3EBDD)
private const val MaxPhotoBytes = 2 * 1024 * 1024

data class InventoryEditBox(
    val id: String,
    val location: String
)

data class InventoryItemDraft(
    val name: String,
    val sku: String,
    val category: String,
    val size: String,
    val color: String,
    val stock: Int,
    val box: InventoryEditBox,
    val photoName: String,
    val photoUri: Uri? = null
)

internal val SampleItem = InventoryItemDraft(
    name = "Kebaya Ivory Bordir",
    sku = "KB-014",
    category = "Kebaya",
    size = "M",
    color = "Ivory",
    stock = 1,
    box = InventoryEditBox("BX-028", "Lemari A · Rak 2"),
    photoName = "kebaya-ivory-bordir.jpg"
)

class EditInventoryItemActivity : ComponentActivity() {

    companion object {
        const val EXTRA_NAME = "inventory_name"
        const val EXTRA_SKU = "inventory_sku"
        const val EXTRA_ORIGINAL_SKU = "inventory_original_sku"
        const val EXTRA_CATEGORY = "inventory_category"
        const val EXTRA_SIZE = "inventory_size"
        const val EXTRA_COLOR = "inventory_color"
        const val EXTRA_STOCK = "inventory_stock"
        const val EXTRA_BOX_ID = "inventory_box_id"
        const val EXTRA_BOX_LOCATION = "inventory_box_location"
        const val EXTRA_PHOTO_NAME = "inventory_photo_name"
        const val EXTRA_PHOTO_URI = "inventory_photo_uri"
        const val EXTRA_CATEGORY_OPTIONS = "inventory_category_options"
        const val EXTRA_BOX_IDS = "inventory_box_ids"
        const val EXTRA_BOX_LOCATIONS = "inventory_box_locations"

        private const val SCANNER_ACTIVITY =
            "com.example.rumattire.ScanQrActivity"
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

        val initial = InventoryItemDraft(
            name = intent.getStringExtra(EXTRA_NAME) ?: SampleItem.name,
            sku = intent.getStringExtra(EXTRA_SKU) ?: SampleItem.sku,
            category = intent.getStringExtra(EXTRA_CATEGORY) ?: SampleItem.category,
            size = intent.getStringExtra(EXTRA_SIZE) ?: SampleItem.size,
            color = intent.getStringExtra(EXTRA_COLOR) ?: SampleItem.color,
            stock = intent.getIntExtra(EXTRA_STOCK, SampleItem.stock),
            box = InventoryEditBox(
                id = intent.getStringExtra(EXTRA_BOX_ID) ?: SampleItem.box.id,
                location = intent.getStringExtra(EXTRA_BOX_LOCATION)
                    ?: SampleItem.box.location
            ),
            photoName = intent.getStringExtra(EXTRA_PHOTO_NAME) ?: SampleItem.photoName,
            photoUri = intent.getStringExtra(EXTRA_PHOTO_URI)?.let(Uri::parse)
        )

        val categories = intent.getStringArrayListExtra(EXTRA_CATEGORY_OPTIONS).orEmpty()
        val boxIds = intent.getStringArrayListExtra(EXTRA_BOX_IDS).orEmpty()
        val boxLocations = intent.getStringArrayListExtra(EXTRA_BOX_LOCATIONS).orEmpty()
        val boxes = boxIds.mapIndexed { index, id ->
            InventoryEditBox(id, boxLocations.getOrNull(index).orEmpty())
        }
        val scanIntent = Intent().setClassName(this, SCANNER_ACTIVITY)

        setContent {
            EditInventoryItemScreen(
                initialItem = initial,
                categories = categories,
                boxes = boxes,
                scanIntent = scanIntent,
                onCancel = { finish() },
                onSave = { updated ->
                    val result = Intent().apply {
                        putExtra(EXTRA_ORIGINAL_SKU, initial.sku)
                        putExtra(EXTRA_NAME, updated.name)
                        putExtra(EXTRA_SKU, updated.sku)
                        putExtra(EXTRA_CATEGORY, updated.category)
                        putExtra(EXTRA_SIZE, updated.size)
                        putExtra(EXTRA_COLOR, updated.color)
                        putExtra(EXTRA_STOCK, updated.stock)
                        putExtra(EXTRA_BOX_ID, updated.box.id)
                        putExtra(EXTRA_BOX_LOCATION, updated.box.location)
                        putExtra(EXTRA_PHOTO_NAME, updated.photoName)

                        updated.photoUri?.let { uri ->
                            putExtra(EXTRA_PHOTO_URI, uri.toString())
                            clipData = ClipData.newRawUri("Foto item", uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    }

                    setResult(Activity.RESULT_OK, result)
                    finish()
                }
            )
        }
    }
}

@Composable
fun EditInventoryItemScreen(
    initialItem: InventoryItemDraft,
    categories: List<String>,
    boxes: List<InventoryEditBox>,
    onCancel: () -> Unit,
    onSave: (InventoryItemDraft) -> Unit,
    scanIntent: Intent? = null,
    showDesignSystemBars: Boolean = false,
    creating: Boolean = false
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by rememberSaveable(initialItem.sku) { mutableStateOf(initialItem.name) }
    var sku by rememberSaveable(initialItem.sku) { mutableStateOf(initialItem.sku) }
    var category by rememberSaveable(initialItem.sku) { mutableStateOf(initialItem.category) }
    var size by rememberSaveable(initialItem.sku) { mutableStateOf(initialItem.size) }
    var color by rememberSaveable(initialItem.sku) { mutableStateOf(initialItem.color) }
    var stock by rememberSaveable(initialItem.sku) {
        mutableStateOf(initialItem.stock.toString())
    }
    var boxId by rememberSaveable(initialItem.sku) { mutableStateOf(initialItem.box.id) }
    var photoName by rememberSaveable(initialItem.sku) {
        mutableStateOf(initialItem.photoName)
    }
    var photoUriString by rememberSaveable(initialItem.sku) {
        mutableStateOf(initialItem.photoUri?.toString())
    }
    var loadingPhoto by remember { mutableStateOf(false) }

    val availableCategories = (listOf(initialItem.category) + categories)
        .filter { it.isNotBlank() }
        .distinct()

    val availableBoxes = (listOf(initialItem.box) + boxes)
        .filter { it.id.isNotBlank() }
        .distinctBy { it.id }

    val selectedBox = availableBoxes.find { it.id == boxId } ?: initialItem.box
    val selectedPhotoUri = remember(photoUriString) {
        photoUriString?.let(Uri::parse)
    }

    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            loadingPhoto = true

            scope.launch {
                try {
                    val pickedName = withContext(Dispatchers.IO) {
                        validateEditPhoto(context, uri)
                    }

                    photoName = pickedName
                    photoUriString = uri.toString()
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    Toast.makeText(
                        context,
                        error.message ?: "Foto tidak dapat dibaca.",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    loadingPhoto = false
                }
            }
        }
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val qr = result.data?.getStringExtra("extra_qr_value")?.trim()
            val match = availableBoxes.firstOrNull {
                it.id.equals(qr, ignoreCase = true)
            }

            if (match != null) {
                boxId = match.id
            } else {
                Toast.makeText(
                    context,
                    "QR tidak cocok dengan box yang tersedia.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = EditBrown,
            onPrimary = Color.White,
            background = EditBackground,
            surface = Color.White,
            onSurface = EditInk
        )
    ) {
        val systemInsets = if (showDesignSystemBars) {
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
            modifier = Modifier.fillMaxSize().testTag("editInventoryRoot"),
            color = EditBackground
        ) {
            Column(Modifier.fillMaxSize().then(systemInsets)) {
                if (showDesignSystemBars) EditDesignStatusBar()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .size(38.dp)
                            .semantics { contentDescription = "Kembali" }
                    ) {
                        EditLineIcon(EditIcon.Back, EditInk, Modifier.size(24.dp))
                    }

                    EditText(
                        text = if (creating) "Tambah item" else "Edit Item · ${initialItem.sku}",
                        size = 15,
                        bold = true,
                        modifier = Modifier.padding(start = 8.dp),
                        maxLines = 1
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EditCard("Foto item", "editPhotoCard") {
                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EditPhotoThumbnail(selectedPhotoUri)
                            Spacer(Modifier.width(16.dp))

                            Column(Modifier.weight(1f)) {
                                EditText(photoName, 13, maxLines = 1)
                                Spacer(Modifier.height(3.dp))
                                EditText("JPG atau PNG · maks 2 MB", 11, color = EditMuted)
                                Spacer(Modifier.height(10.dp))

                                EditSmallButton(
                                    label = "Ganti foto",
                                    minWidth = 102,
                                    enabled = !loadingPhoto,
                                    onClick = { photoLauncher.launch("image/*") }
                                )
                            }
                        }
                    }

                    EditCard("Informasi item", "editItemInfoCard") {
                        Spacer(Modifier.height(10.dp))

                        EditInput(
                            label = "Nama item",
                            value = name,
                            onChange = { name = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                            EditInput(
                                label = "SKU",
                                value = sku,
                                onChange = { sku = it },
                                modifier = Modifier.weight(1f)
                            )

                            Column(Modifier.weight(1f)) {
                                EditFieldLabel("Kategori")

                                EditDropdown(
                                    choices = availableCategories,
                                    label = "Kategori",
                                    height = 44,
                                    itemLabel = { it },
                                    onSelect = { category = it }
                                ) {
                                    EditText(
                                        text = category,
                                        size = 13,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                            EditInput("Ukuran", size, { size = it }, Modifier.weight(1f))
                            EditInput("Warna", color, { color = it }, Modifier.weight(1f))

                            EditInput(
                                label = "Stok",
                                value = stock,
                                onChange = { input ->
                                    stock = input.filter { digit -> digit in '0'..'9' }
                                },
                                modifier = Modifier.weight(1f),
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }

                    EditCard("Box penyimpanan", "editStorageBoxCard") {
                        Spacer(Modifier.height(2.dp))
                        EditText("Pilih box fisik tempat item disimpan", 11, color = EditMuted)
                        Spacer(Modifier.height(8.dp))

                        EditDropdown(
                            choices = availableBoxes,
                            label = "Box penyimpanan",
                            height = 64,
                            itemLabel = { "${it.id} · ${it.location}" },
                            onSelect = { boxId = it.id },
                            startPadding = 12
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(EditIconBackground, RoundedCornerShape(11.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                EditLineIcon(EditIcon.Box, EditBrown, Modifier.size(24.dp))
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                EditText(selectedBox.id, 13, bold = true, maxLines = 1)
                                Spacer(Modifier.height(3.dp))
                                EditText(selectedBox.location, 11, color = EditMuted, maxLines = 1)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        EditSmallButton(
                            label = "Scan QR box",
                            minWidth = 132,
                            icon = EditIcon.Scan
                        ) {
                            if (scanIntent == null) {
                                Toast.makeText(
                                    context,
                                    "Pemindai QR belum dihubungkan.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                try {
                                    scannerLauncher.launch(scanIntent)
                                } catch (_: ActivityNotFoundException) {
                                    Toast.makeText(
                                        context,
                                        "Activity pemindai QR belum tersedia.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }

                EditFooter(
                    onCancel = onCancel,
                    canSave = !loadingPhoto,
                    showDesignSystemBars = showDesignSystemBars,
                    onSave = {
                        val stockNumber = stock.toIntOrNull()

                        when {
                            name.isBlank() || sku.isBlank() || category.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    "Isi nama item, SKU, dan kategori.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            stockNumber == null || stockNumber < 0 -> {
                                Toast.makeText(
                                    context,
                                    "Masukkan jumlah stok yang valid.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            selectedBox.id.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    "Pilih box penyimpanan.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> onSave(
                                InventoryItemDraft(
                                    name = name.trim(),
                                    sku = sku.trim(),
                                    category = category,
                                    size = size.trim(),
                                    color = color.trim(),
                                    stock = stockNumber,
                                    box = selectedBox,
                                    photoName = photoName,
                                    photoUri = selectedPhotoUri
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

private fun validateEditPhoto(context: Context, uri: Uri): String {
    val resolver = context.contentResolver

    // Baca maksimal 2 MB + 1 byte, termasuk saat ukuran tidak dilaporkan.
    resolver.openInputStream(uri)?.use { input ->
        val buffer = ByteArray(8192)
        var total = 0

        while (total <= MaxPhotoBytes) {
            val count = input.read(
                buffer,
                0,
                minOf(buffer.size, MaxPhotoBytes + 1 - total)
            )
            if (count == -1) break
            total += count
        }

        require(total in 1..MaxPhotoBytes) {
            "Foto harus berukuran maksimal 2 MB."
        }
    } ?: error("Foto tidak dapat dibaca.")

    val bounds = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }

    resolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, bounds)
    }

    require(
        bounds.outWidth > 0 && bounds.outHeight > 0 &&
                bounds.outMimeType in listOf("image/jpeg", "image/png")
    ) {
        "Pilih foto dengan format JPG atau PNG."
    }

    val displayName = resolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null
    )?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
    }

    return displayName ?: if (bounds.outMimeType == "image/png") {
        "foto-item.png"
    } else {
        "foto-item.jpg"
    }
}

@Composable
private fun EditPhotoThumbnail(uri: Uri?) {
    val context = LocalContext.current

    val bitmap by produceState<Bitmap?>(null, context, uri) {
        value = null

        if (uri != null) {
            value = withContext(Dispatchers.IO) {
                runCatching {
                    val resolver = context.contentResolver
                    val bounds = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }

                    resolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, bounds)
                    }

                    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
                        return@runCatching null
                    }

                    var sample = 1
                    while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 512) {
                        sample *= 2
                    }

                    val options = BitmapFactory.Options().apply {
                        inSampleSize = sample
                    }

                    resolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, options)
                    }
                }.getOrNull()
            }
        }
    }

    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(EditIconBackground),
        contentAlignment = Alignment.Center
    ) {
        val photo = bitmap

        if (photo != null) {
            Image(
                bitmap = remember(photo) { photo.asImageBitmap() },
                contentDescription = "Foto item",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            EditLineIcon(EditIcon.Shirt, EditBrown, Modifier.size(44.dp))
        }
    }
}

@Composable
private fun EditCard(
    title: String,
    tag: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().testTag(tag),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, EditBorder)
    ) {
        Column(Modifier.padding(16.dp)) {
            EditText(title, 14, bold = true, lineHeight = 18)
            content()
        }
    }
}

@Composable
private fun EditFieldLabel(label: String) {
    EditText(label, 11, color = EditMuted, lineHeight = 14)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun EditInput(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier) {
        EditFieldLabel(label)

        Surface(
            modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
            shape = RoundedCornerShape(12.dp),
            color = EditField,
            border = BorderStroke(1.dp, EditBorder)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = label }
                    .testTag("editField-$label"),
                textStyle = editTextStyle(13, EditInk, false, 18),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                cursorBrush = SolidColor(EditBrown),
                decorationBox = { input ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 44.dp)
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        input()
                    }
                }
            )
        }
    }
}

@Composable
private fun <T> EditDropdown(
    choices: List<T>,
    label: String,
    height: Int,
    itemLabel: (T) -> String,
    onSelect: (T) -> Unit,
    startPadding: Int = 14,
    content: @Composable RowScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var anchorWidth by remember { mutableIntStateOf(0) }
    val menuWidth = with(LocalDensity.current) { anchorWidth.toDp() }

    Box(Modifier.fillMaxWidth()) {
        Surface(
            onClick = { expanded = true },
            enabled = choices.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = height.dp)
                .onSizeChanged { anchorWidth = it.width }
                .semantics {
                    contentDescription = label
                    role = Role.Button
                },
            shape = RoundedCornerShape(12.dp),
            color = EditField,
            border = BorderStroke(1.dp, EditBorder)
        ) {
            Row(
                modifier = Modifier.padding(start = startPadding.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
                EditLineIcon(EditIcon.Chevron, EditMuted, Modifier.size(18.dp))
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(menuWidth)
        ) {
            choices.forEach { choice ->
                DropdownMenuItem(
                    text = { EditText(itemLabel(choice), 13) },
                    onClick = {
                        onSelect(choice)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EditSmallButton(
    label: String,
    minWidth: Int,
    icon: EditIcon? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.widthIn(min = minWidth.dp).height(32.dp),
        shape = CircleShape,
        border = BorderStroke(1.5.dp, EditBrown),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = EditBrown),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        if (icon != null) {
            EditLineIcon(icon, EditBrown, Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
        }

        EditText(label, 12, color = EditBrown, bold = true, maxLines = 1)
    }
}

@Composable
private fun EditFooter(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    canSave: Boolean,
    showDesignSystemBars: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EditField)
            .then(
                if (showDesignSystemBars) Modifier else Modifier.navigationBarsPadding()
            )
            .testTag("editInventoryFooter")
    ) {
        HorizontalDivider(thickness = 1.dp, color = EditBorder)

        EditText(
            text = "Perubahan box tercatat di riwayat item",
            size = 12,
            color = EditMuted,
            lineHeight = 18,
            align = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.width(88.dp).height(44.dp).testTag("editCancel"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, EditBrown),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = EditBrown
                ),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                EditText("Batal", 13, color = EditBrown, bold = true)
            }

            Button(
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier.weight(1f).height(44.dp).testTag("editSave"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditBrown,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                EditText(
                    text = "Simpan perubahan",
                    size = 13,
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
                    Modifier
                        .padding(top = 10.dp)
                        .size(width = 100.dp, height = 4.dp)
                        .background(EditInk, CircleShape)
                )
            }
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
}

private enum class EditIcon(val data: String, val stroke: Float) {
    Back("M19 12H5 M13 20L5 12L13 4", 1.8f),
    Shirt(
        "M4.4 8.8L14.3 2.2C19.4334 6.6 24.5667 6.6 29.7 2.2" +
                "L39.6 8.8L35.2 17.6L30.8 15.4V39.6H13.2V15.4L8.8 17.6Z",
        2.42f
    ),
    Box(
        "M12 21L20 17V8L12 4L4 8V17L12 21Z " +
                "M4 8L12 12M20 8L12 12M12 12V21",
        1.5f
    ),
    Chevron("M5 9L9 13L13 9", 1.4f),
    Scan("M2 6V2H6 M10 2H14V6 M14 10V14H10 M6 14H2V10 M6 8H10", 1.4f)
}

@Composable
private fun EditLineIcon(
    icon: EditIcon,
    color: Color,
    modifier: Modifier
) {
    val path = remember(icon) {
        PathParser().parsePathString(icon.data).toPath()
    }

    val viewport = when (icon) {
        EditIcon.Shirt -> 44f
        EditIcon.Chevron -> 18f
        EditIcon.Scan -> 16f
        else -> 24f
    }

    Canvas(modifier) {
        scale(size.width / viewport, size.height / viewport, pivot = Offset.Zero) {
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = icon.stroke,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

private fun editTextStyle(
    size: Int,
    color: Color,
    bold: Boolean,
    lineHeight: Int
) = TextStyle(
    fontSize = size.sp,
    color = color,
    lineHeight = lineHeight.sp,
    fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
    letterSpacing = 0.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

@Composable
private fun EditText(
    text: String,
    size: Int,
    modifier: Modifier = Modifier,
    color: Color = EditInk,
    bold: Boolean = false,
    lineHeight: Int = size + 4,
    maxLines: Int = Int.MAX_VALUE,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        modifier = modifier,
        style = editTextStyle(size, color, bold, lineHeight),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = align
    )
}

@Composable
private fun EditDesignStatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(start = 24.dp, end = 33.dp)
            .offset(y = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditText("09.41", 12, bold = true)
        Spacer(Modifier.weight(1f))

        Canvas(Modifier.size(width = 18.dp, height = 12.dp)) {
            for (index in 0..3) {
                val barHeight = (4 + index * 2).dp.toPx()

                drawRoundRect(
                    color = EditInk,
                    topLeft = Offset(
                        (index * 5).dp.toPx(),
                        10.dp.toPx() - barHeight
                    ),
                    size = Size(3.dp.toPx(), barHeight),
                    cornerRadius = CornerRadius(1.dp.toPx())
                )
            }
        }

        Spacer(Modifier.width(8.dp))
        EditText("86%", 11)
        Spacer(Modifier.width(4.dp))

        Canvas(Modifier.size(width = 22.dp, height = 11.dp)) {
            drawRoundRect(
                color = EditInk,
                topLeft = Offset(0.5.dp.toPx(), 0.5.dp.toPx()),
                size = Size(21.dp.toPx(), 10.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
            drawRoundRect(
                color = EditInk,
                topLeft = Offset(2.5.dp.toPx(), 2.5.dp.toPx()),
                size = Size(15.dp.toPx(), 6.dp.toPx()),
                cornerRadius = CornerRadius(1.5.dp.toPx())
            )
        }
    }
}

@Preview(
    name = "Edit Item · KB-014",
    widthDp = 393,
    heightDp = 868,
    showBackground = true
)
@Composable
private fun PreviewEditInventoryItem() {
    EditInventoryItemScreen(
        initialItem = SampleItem,
        categories = emptyList(),
        boxes = emptyList(),
        onCancel = {},
        onSave = {},
        showDesignSystemBars = true
    )
}