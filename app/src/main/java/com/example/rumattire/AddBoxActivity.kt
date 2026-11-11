package com.example.rumattire

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

private val AddBoxBackground = Color(0xFFF4EFE7)
private val AddBoxHeaderColor = Color(0xFFFAF6EF)
private val AddBoxNavigationColor = Color(0xFFFAF7F1)
private val AddBoxTitleColor = Color(0xFF30251F)
private val AddBoxBrown = Color(0xFF4B3429)
private val AddBoxMuted = Color(0xFF857568)
private val AddBoxInactive = Color(0xFF786657)
private val AddBoxHintColor = Color(0xFF988478)

data class AddBoxFormData(
    val idBox: String,
    val namaBox: String,
    val lokasiPenyimpanan: String,
    val kategoriUtama: String
)

class AddBoxActivity : ComponentActivity() {

    companion object {
        const val EXTRA_ID_BOX = "extra_id_box"
        const val EXTRA_NAMA_BOX = "extra_nama_box"
        const val EXTRA_LOKASI_BOX = "extra_lokasi_box"
        const val EXTRA_KATEGORI_BOX = "extra_kategori_box"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // BX-004 mengikuti nilai awal pada XML.
        // Kirim ID dari proses aplikasi melalui EXTRA_ID_BOX.
        val idBox = intent.getStringExtra(EXTRA_ID_BOX) ?: "BX-004"

        setContent {
            AddBoxTheme {
                AddBoxScreen(
                    idBox = idBox,
                    onBack = {
                        onBackPressedDispatcher.onBackPressed()
                    },
                    onNotification = {
                        // Sambungkan ke halaman notifikasi.
                        Toast.makeText(
                            this,
                            "Navigasi notifikasi belum disambungkan",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onNavigate = { menu ->
                        // Sambungkan ke Activity atau route aplikasi.
                        Toast.makeText(
                            this,
                            "Navigasi $menu belum disambungkan",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onCancel = {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    },
                    onSave = { form ->
                        if (form.namaBox.isBlank()) {
                            Toast.makeText(
                                this,
                                "Nama box harus diisi",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Mengembalikan data formulir ke Activity pemanggil.
                            // Hubungkan ke repository/database aplikasi
                            // jika penyimpanan dilakukan di Activity ini.
                            val result = Intent().apply {
                                putExtra(EXTRA_ID_BOX, form.idBox)
                                putExtra(EXTRA_NAMA_BOX, form.namaBox)
                                putExtra(
                                    EXTRA_LOKASI_BOX,
                                    form.lokasiPenyimpanan
                                )
                                putExtra(
                                    EXTRA_KATEGORI_BOX,
                                    form.kategoriUtama
                                )
                            }

                            setResult(Activity.RESULT_OK, result)
                            finish()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AddBoxTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = AddBoxBrown,
            onPrimary = Color.White,
            background = AddBoxBackground,
            onBackground = AddBoxTitleColor,
            surface = AddBoxHeaderColor,
            onSurface = AddBoxTitleColor
        ),
        content = content
    )
}

@Composable
fun AddBoxScreen(
    idBox: String = "BX-004",
    initialNamaBox: String = "Box Kebaya Akad",
    onBack: () -> Unit = {},
    onNotification: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onCancel: () -> Unit = {},
    onSave: (AddBoxFormData) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    val lokasiOptions = stringArrayResource(
        R.array.opsi_lokasi_box
    ).toList()

    val kategoriOptions = stringArrayResource(
        R.array.opsi_kategori_box
    ).toList()

    var namaBox by rememberSaveable(idBox, initialNamaBox) {
        mutableStateOf(initialNamaBox)
    }

    var lokasiPenyimpanan by rememberSaveable(idBox) {
        mutableStateOf(lokasiOptions.firstOrNull().orEmpty())
    }

    var kategoriUtama by rememberSaveable(idBox) {
        mutableStateOf(kategoriOptions.firstOrNull().orEmpty())
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tambahBoxRoot"),
        color = AddBoxBackground
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding(),
            containerColor = AddBoxBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                AddBoxHeader(
                    onBack = onBack,
                    onNotification = onNotification
                )
            },
            bottomBar = {
                Column {
                    AddBoxActionFooter(
                        onCancel = {
                            focusManager.clearFocus()
                            onCancel()
                        },
                        onSave = {
                            focusManager.clearFocus()

                            onSave(
                                AddBoxFormData(
                                    idBox = idBox,
                                    namaBox = namaBox,
                                    lokasiPenyimpanan = lokasiPenyimpanan,
                                    kategoriUtama = kategoriUtama
                                )
                            )
                        }
                    )

                    AddBoxBottomNavigation(
                        onNavigate = onNavigate
                    )
                }
            }
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                val viewportHeight = maxHeight

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scrollTambahBox")
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = viewportHeight)
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 12.dp,
                            bottom = 20.dp
                        )
                ) {
                    // KARTU INFORMASI BOX
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cardInformasiBox")
                            .addBoxDrawableBackground(
                                R.drawable.bg_detail_outline
                            )
                            .padding(20.dp)
                    ) {
                        AddBoxText(
                            text = "Informasi box",
                            size = 17,
                            bold = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        AddBoxFieldLabel("ID box")

                        // ID BOX OTOMATIS
                        Row(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("layoutIdBoxOtomatis")
                                .addBoxDrawableBackground(
                                    R.drawable.bg_addbox_id_field
                                )
                                .padding(
                                    start = 16.dp,
                                    end = 12.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AddBoxText(
                                text = idBox,
                                size = 15,
                                bold = true,
                                color = Color(0xFF817165),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("tvIdBoxOtomatis")
                            )

                            Box(
                                modifier = Modifier
                                    .addBoxDrawableBackground(
                                        R.drawable.bg_addbox_id_badge
                                    )
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                AddBoxText(
                                    text = "Otomatis",
                                    size = 12,
                                    bold = true,
                                    color = Color(0xFF735034)
                                )
                            }
                        }

                        AddBoxFieldLabel("Nama box")

                        // INPUT NAMA BOX
                        BasicTextField(
                            value = namaBox,
                            onValueChange = {
                                namaBox = it
                            },
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("etNamaBox")
                                .semantics {
                                    contentDescription = "Nama box"
                                }
                                .addBoxDrawableBackground(
                                    R.drawable.bg_addbox_input_active
                                )
                                .padding(horizontal = 16.dp),
                            singleLine = true,
                            textStyle = addBoxTextStyle(
                                size = 15,
                                color = AddBoxTitleColor
                            ),
                            cursorBrush = SolidColor(AddBoxBrown),
                            keyboardOptions = KeyboardOptions(
                                capitalization =
                                    KeyboardCapitalization.Sentences,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (namaBox.isEmpty()) {
                                        AddBoxText(
                                            text = "Masukkan nama box",
                                            size = 15,
                                            color = AddBoxHintColor
                                        )
                                    }

                                    innerTextField()
                                }
                            }
                        )

                        AddBoxFieldLabel("Lokasi penyimpanan")

                        AddBoxDropdown(
                            tag = "spLokasiPenyimpanan",
                            label = "Lokasi penyimpanan",
                            options = lokasiOptions,
                            selectedValue = lokasiPenyimpanan,
                            onSelected = {
                                lokasiPenyimpanan = it
                            }
                        )

                        AddBoxFieldLabel("Kategori utama")

                        AddBoxDropdown(
                            tag = "spKategoriUtama",
                            label = "Kategori utama",
                            options = kategoriOptions,
                            selectedValue = kategoriUtama,
                            onSelected = {
                                kategoriUtama = it
                            }
                        )
                    }

                    // KARTU LABEL QR OTOMATIS
                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .testTag("cardLabelQrOtomatis")
                            .addBoxDrawableBackground(
                                R.drawable.bg_detail_outline
                            )
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .addBoxDrawableBackground(
                                    R.drawable.bg_addbox_qr_placeholder
                                )
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    R.drawable.ic_addbox_qr_placeholder
                                ),
                                contentDescription = "Ikon QR otomatis",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("ivIconQrOtomatis")
                            )
                        }

                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        ) {
                            AddBoxText(
                                text = "Label QR otomatis",
                                size = 15,
                                bold = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            AddBoxText(
                                text = "Kode QR dibuat setelah box " +
                                        "disimpan, lalu dicetak dan " +
                                        "ditempel pada box.",
                                size = 13,
                                color = AddBoxMuted,
                                lineHeight = 17.sp,
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddBoxHeader(
    onBack: () -> Unit,
    onNotification: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(AddBoxHeaderColor)
            .padding(start = 8.dp, end = 12.dp)
            .testTag("headerTambahBox"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .testTag("btnKembali")
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_inv_back),
                contentDescription = "Kembali",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }

        AddBoxText(
            text = "Tambah Box",
            size = 18,
            bold = true,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .testTag("tvJudulTambahBox")
        )

        Box(
            modifier = Modifier.size(48.dp)
        ) {
            IconButton(
                onClick = onNotification,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("btnNotifikasi")
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.ic_inv_notifikasi
                    ),
                    contentDescription = "Notifikasi baru",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp, y = 10.dp)
                    .size(8.dp)
                    .testTag("indicatorNotifikasi")
                    .addBoxDrawableBackground(
                        resource = R.drawable.bg_detail_circle,
                        tint = 0xFFC85A09.toInt()
                    )
            )
        }
    }
}

@Composable
private fun AddBoxFieldLabel(
    text: String
) {
    AddBoxText(
        text = text,
        size = 13,
        color = AddBoxMuted,
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun AddBoxDropdown(
    tag: String,
    label: String,
    options: List<String>,
    selectedValue: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var anchorWidthPx by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current

    val menuWidth = with(density) {
        anchorWidthPx.toDp()
    }

    Box(
        modifier = Modifier
            .padding(top = 6.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .onSizeChanged {
                    anchorWidthPx = it.width
                }
                .testTag(tag)
                .semantics {
                    contentDescription = label
                }
                .addBoxDrawableBackground(
                    R.drawable.bg_addbox_input
                )
                .clickable(
                    role = Role.DropdownList,
                    onClick = {
                        focusManager.clearFocus()
                        expanded = true
                    }
                )
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AddBoxText(
                text = selectedValue,
                size = 15,
                modifier = Modifier.weight(1f)
            )

            // Slot 26dp menjaga ruang akhir teks sebesar 42dp:
            // padding akhir 16dp + slot ikon 26dp.
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.ic_addbox_chevron_down
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.width(menuWidth),
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        AddBoxText(
                            text = option,
                            size = 15
                        )
                    },
                    modifier = Modifier.semantics {
                        selected = option == selectedValue
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AddBoxActionFooter(
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AddBoxBackground)
            .testTag("footerAksiTambahBox")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5DACA))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 14.dp,
                    bottom = 20.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .heightIn(min = 48.dp)
                    .testTag("btnBatalTambahBox")
                    .addBoxDrawableBackground(
                        R.drawable.bg_box_button_outline
                    )
                    .clickable(
                        role = Role.Button,
                        onClick = onCancel
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                AddBoxText(
                    text = "Batal",
                    size = 14,
                    bold = true,
                    color = AddBoxBrown,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.width(12.dp))

            Row(
                modifier = Modifier
                    .weight(1.4f)
                    .heightIn(min = 48.dp)
                    .testTag("btnSimpanBox")
                    .addBoxDrawableBackground(
                        R.drawable.bg_inv_button
                    )
                    .clickable(
                        role = Role.Button,
                        onClick = onSave
                    )
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.ic_addbox_check_circle
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(Modifier.width(8.dp))

                AddBoxText(
                    text = "Simpan box",
                    size = 14,
                    bold = true,
                    color = Color.White
                )
            }
        }
    }
}

private data class AddBoxNavigationItem(
    val tag: String,
    val indicatorTag: String,
    val label: String,
    @DrawableRes val icon: Int,
    val active: Boolean = false
)

@Composable
private fun AddBoxBottomNavigation(
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        AddBoxNavigationItem(
            tag = "navBeranda",
            indicatorTag = "indicatorBeranda",
            label = "Beranda",
            icon = R.drawable.ic_inv_beranda
        ),
        AddBoxNavigationItem(
            tag = "navInventori",
            indicatorTag = "indicatorInventori",
            label = "Inventori",
            icon = R.drawable.ic_inv_baju
        ),
        AddBoxNavigationItem(
            tag = "navPesanan",
            indicatorTag = "indicatorPesanan",
            label = "Pesanan",
            icon = R.drawable.ic_inv_pesanan
        ),
        AddBoxNavigationItem(
            tag = "navBox",
            indicatorTag = "indicatorBox",
            label = "Box",
            icon = R.drawable.ic_inv_box,
            active = true
        ),
        AddBoxNavigationItem(
            tag = "navLainnya",
            indicatorTag = "indicatorLainnya",
            label = "Lainnya",
            icon = R.drawable.ic_inv_menu
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AddBoxNavigationColor)
            .testTag("bottomNavigation")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE7D8C6))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .height(64.dp)
                        .testTag(item.tag)
                        .then(
                            if (item.active) {
                                Modifier.addBoxDrawableBackground(
                                    R.drawable.bg_inv_nav_active
                                )
                            } else {
                                Modifier
                            }
                        )
                        .semantics {
                            selected = item.active
                        }
                        .clickable(
                            role = Role.Tab,
                            onClick = {
                                onNavigate(item.label)
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(
                            width = 36.dp,
                            height = 28.dp
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(Modifier.height(2.dp))

                    AddBoxText(
                        text = item.label,
                        size = 10,
                        bold = item.active,
                        color = if (item.active) {
                            AddBoxBrown
                        } else {
                            AddBoxInactive
                        }
                    )

                    Spacer(Modifier.height(4.dp))

                    // Transparan tetap mengambil ruang,
                    // seperti visibility="invisible" pada XML.
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(3.dp)
                            .testTag(item.indicatorTag)
                            .background(
                                if (item.active) {
                                    AddBoxBrown
                                } else {
                                    Color.Transparent
                                }
                            )
                    )
                }
            }
        }
    }
}

private fun addBoxTextStyle(
    size: Int,
    color: Color = AddBoxTitleColor,
    bold: Boolean = false,
    lineHeight: TextUnit = TextUnit.Unspecified
): TextStyle {
    return TextStyle(
        color = color,
        fontSize = size.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = if (bold) {
            FontWeight.Bold
        } else {
            FontWeight.Normal
        },
        letterSpacing = 0.sp,
        lineHeight = lineHeight,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    )
}

@Composable
private fun AddBoxText(
    text: String,
    size: Int,
    modifier: Modifier = Modifier,
    color: Color = AddBoxTitleColor,
    bold: Boolean = false,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = textAlign,
        style = addBoxTextStyle(
            size = size,
            color = color,
            bold = bold,
            lineHeight = lineHeight
        )
    )
}

/**
 * Menggunakan drawable latar asli dari XML.
 * Mendukung shape, stroke, sudut, dan gradient
 * tanpa perlu membuat ulang drawable.
 */
@Composable
private fun Modifier.addBoxDrawableBackground(
    @DrawableRes resource: Int,
    tint: Int? = null
): Modifier {
    val context = LocalContext.current

    val drawable = remember(context, resource, tint) {
        requireNotNull(
            ContextCompat.getDrawable(context, resource)
        ) {
            "Drawable tidak ditemukan: $resource"
        }.mutate().apply {
            if (tint != null) {
                setTint(tint)
            }
        }
    }

    return drawWithContent {
        drawable.setBounds(
            0,
            0,
            size.width.toInt(),
            size.height.toInt()
        )

        drawable.draw(drawContext.canvas.nativeCanvas)
        drawContent()
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Tambah Box",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun AddBoxScreenPreview() {
    AddBoxTheme {
        AddBoxScreen()
    }
}