package com.example.rumattire

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Outline
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.Locale
import androidx.compose.ui.platform.LocalInspectionMode

private val ScanBackground = Color(0xFFF5F0E8)
private val ScanSurface = Color(0xFFFCF9F3)
private val ScanBrown = Color(0xFF3E2B23)
private val ScanSecondary = Color(0xFF716055)
private val ScanDivider = Color(0xFFE4DACB)
private val ScanGold = Color(0xFFC4A46A)

class ScanQrActivity : ComponentActivity() {

    companion object {
        const val EXTRA_QR_VALUE = "extra_qr_value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = ScanBrown,
                    onPrimary = ScanSurface,
                    background = ScanBackground,
                    onBackground = ScanBrown,
                    surface = ScanSurface,
                    onSurface = ScanBrown
                )
            ) {
                ScanQrScreen(
                    onBack = {
                        onBackPressedDispatcher.onBackPressed()
                    },
                    onNotification = {
                        // Sambungkan ke halaman notifikasi aplikasi kamu.
                        Toast.makeText(
                            this,
                            "Navigasi notifikasi belum disambungkan",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onNavigate = { menu ->
                        // Sambungkan ke Activity atau route aplikasi kamu.
                        Toast.makeText(
                            this,
                            "Navigasi $menu belum disambungkan",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onOpenBox = { qrValue ->
                        // Activity pemanggil menerima isi QR melalui extra ini.
                        val result = Intent().putExtra(
                            EXTRA_QR_VALUE,
                            qrValue
                        )

                        setResult(Activity.RESULT_OK, result)
                        finish()
                    }
                )
            }
        }
    }
}

private fun android.content.Context.findActivity(): ComponentActivity? {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is ComponentActivity) return ctx
        ctx = ctx.baseContext ?: break
    }
    return ctx as? ComponentActivity
}

@Composable
fun ScanQrScreen(
    onBack: () -> Unit,
    onNotification: () -> Unit,
    onNavigate: (String) -> Unit,
    onOpenBox: (String) -> Unit,
    onManualEntry: (() -> Unit)? = null
) {
    val isPreview = LocalInspectionMode.current

    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current

    val mainExecutor = remember(context) {
        ContextCompat.getMainExecutor(context)
    }

    var permissionGranted by remember(isPreview) {
        mutableStateOf(
            isPreview ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var permissionRequested by rememberSaveable {
        mutableStateOf(false)
    }

    var permissionPermanentlyDenied by rememberSaveable {
        mutableStateOf(false)
    }

    var cameraRetry by remember { mutableIntStateOf(0) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var cameraReady by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }

    var zoomRatio by remember { mutableFloatStateOf(1f) }
    var flashEnabled by remember { mutableStateOf(false) }

    var qrValue by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var statusText by remember {
        mutableStateOf("Menyiapkan kamera…")
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted

        permissionPermanentlyDenied =
            !granted && activity != null &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.CAMERA
                    )

        statusText = if (granted) {
            "Menyiapkan kamera…"
        } else {
            "Izin kamera diperlukan"
        }
    }

    // Pemeriksaan izin ketika kembali dari pengaturan aplikasi.
    DisposableEffect(lifecycleOwner, context, isPreview) {
        val observer = LifecycleEventObserver { _, event ->
            if (!isPreview && event == Lifecycle.Event.ON_RESUME) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                permissionGranted = granted

                if (granted) {
                    permissionPermanentlyDenied = false
                } else {
                    camera = null
                    cameraReady = false
                    statusText = "Izin kamera diperlukan"
                }
            }
        }

        if (!isPreview) {
            lifecycleOwner.lifecycle.addObserver(observer)
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Minta izin saat layar pertama kali dibuka.
    LaunchedEffect(isPreview) {
        if (
            !isPreview &&
            !permissionGranted &&
            !permissionRequested
        ) {
            permissionRequested = true
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Ikuti nilai zoom dan senter yang benar-benar dilaporkan kamera.
    DisposableEffect(camera, lifecycleOwner) {
        val currentCamera = camera

        val zoomObserver = Observer<androidx.camera.core.ZoomState> {
            zoomRatio = it.zoomRatio
        }

        val torchObserver = Observer<Int> {
            flashEnabled = it == TorchState.ON
        }

        currentCamera?.cameraInfo?.zoomState?.observe(
            lifecycleOwner,
            zoomObserver
        )

        currentCamera?.cameraInfo?.torchState?.observe(
            lifecycleOwner,
            torchObserver
        )

        onDispose {
            currentCamera?.cameraInfo?.zoomState?.removeObserver(
                zoomObserver
            )

            currentCamera?.cameraInfo?.torchState?.removeObserver(
                torchObserver
            )
        }
    }

    val changeZoom: () -> Unit = {
        val currentCamera = camera
        val zoomState = currentCamera?.cameraInfo?.zoomState?.value

        if (currentCamera != null && zoomState != null && cameraReady) {
            val minimum = zoomState.minZoomRatio
            val maximum = zoomState.maxZoomRatio
            val base = 1f.coerceIn(minimum, maximum)

            val next = when {
                zoomState.zoomRatio < 1.9f && maximum > 1.05f ->
                    2f.coerceIn(minimum, maximum)

                zoomState.zoomRatio < 2.9f && maximum > 2.05f ->
                    3f.coerceIn(minimum, maximum)

                else -> base
            }

            val operation = currentCamera.cameraControl.setZoomRatio(next)

            operation.addListener(
                {
                    runCatching { operation.get() }
                        .onFailure {
                            Toast.makeText(
                                context,
                                "Pembesaran kamera gagal diubah",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                },
                mainExecutor
            )
        }
    }

    val toggleFlash: () -> Unit = {
        val currentCamera = camera

        if (currentCamera != null && cameraReady) {
            if (currentCamera.cameraInfo.hasFlashUnit()) {
                val operation = currentCamera.cameraControl.enableTorch(
                    !flashEnabled
                )

                operation.addListener(
                    {
                        runCatching { operation.get() }
                            .onFailure {
                                Toast.makeText(
                                    context,
                                    "Senter gagal diubah",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    },
                    mainExecutor
                )
            } else {
                Toast.makeText(
                    context,
                    "Perangkat ini tidak memiliki senter",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("scanQrRoot"),
        color = ScanBackground
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ScanBackground,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                ScanHeader(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Top +
                                    WindowInsetsSides.Horizontal
                        )
                    ),
                    onBack = onBack,
                    onNotification = onNotification
                )
            },
            bottomBar = {
                ScanBottomBar(
                    onNavigate = onNavigate
                )
            }
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                val minimumHeight = maxHeight

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = minimumHeight)
                        .padding(24.dp)
                ) {
                    ScanText(
                        text = "Arahkan ke QR box",
                        size = 24,
                        bold = true,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ScanText(
                        text = "Posisikan kode di dalam bingkai. " +
                                "Pastikan label box terlihat jelas.",
                        size = 14,
                        color = ScanSecondary,
                        lineHeight = 20,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                            .height(320.dp)
                            .testTag("cameraContainer")
                            .drawableBackground(
                                resource = R.drawable.bg_scan_camera,
                                clipToOutline = true
                            )
                    ) {
                        if (permissionGranted) {
                            key(cameraRetry) {
                                ScanCameraPreview(
                                    scanningEnabled = qrValue == null,
                                    onCameraBound = {
                                        camera = it
                                    },
                                    onStreaming = {
                                        cameraReady = true
                                        cameraError = null
                                        statusText = if (qrValue == null) {
                                            "Kamera aktif"
                                        } else {
                                            "QR terbaca"
                                        }
                                    },
                                    onError = { message ->
                                        cameraReady = false
                                        cameraError = message
                                        statusText = "Kamera gagal dibuka"
                                    },
                                    onQrDetected = { value ->
                                        if (qrValue == null) {
                                            qrValue = value
                                            statusText = "QR terbaca"
                                        }
                                    }
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 44.dp)
                                .size(220.dp)
                                .testTag("scanFrame")
                                .drawableBackground(
                                    R.drawable.bg_scan_frame
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 12.dp)
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(ScanGold)
                                    .testTag("scanLine")
                            )
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(Color(0x88000000))
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("btnZoom")
                                    .semantics {
                                        contentDescription =
                                            "Ubah pembesaran kamera"
                                    }
                                    .clickable(
                                        enabled = cameraReady,
                                        role = Role.Button,
                                        onClick = changeZoom
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                ScanText(
                                    text = formatZoom(zoomRatio),
                                    size = 13,
                                    color = ScanSurface,
                                    bold = true
                                )
                            }

                            ScanText(
                                text = statusText,
                                size = 12,
                                color = ScanSurface,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("tvStatusKamera")
                            )

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("btnFlash")
                                    .semantics {
                                        contentDescription =
                                            if (flashEnabled) {
                                                "Matikan senter"
                                            } else {
                                                "Nyalakan senter"
                                            }
                                    }
                                    .clickable(
                                        enabled = cameraReady,
                                        role = Role.Button,
                                        onClick = toggleFlash
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.ic_scan_flash
                                    ),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .testTag("iconFlash")
                                )
                            }
                        }
                    }

                    ScanText(
                        text = cameraError
                            ?: "QR akan dibaca secara otomatis.",
                        size = 12,
                        color = ScanSecondary,
                        lineHeight = 18,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .testTag("tvPetunjukScan")
                            .semantics {
                                liveRegion = LiveRegionMode.Polite
                            }
                    )

                    if (onManualEntry != null) {
                        androidx.compose.material3.TextButton(onClick = onManualEntry) {
                            Text("Masukkan ID box secara manual")
                        }
                    }

                    if (!permissionGranted || cameraError != null) {
                        ScanPrimaryAction(
                            text = when {
                                permissionGranted -> "Coba kamera lagi"
                                permissionPermanentlyDenied ->
                                    "Buka pengaturan kamera"
                                else -> "Izinkan kamera"
                            },
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .testTag("btnAksesKamera"),
                            onClick = {
                                when {
                                    permissionGranted -> {
                                        camera = null
                                        cameraReady = false
                                        cameraError = null
                                        statusText = "Menyiapkan kamera…"
                                        cameraRetry++
                                    }

                                    permissionPermanentlyDenied -> {
                                        val intent = Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.parse(
                                                "package:${context.packageName}"
                                            )
                                        )
                                        context.startActivity(intent)
                                    }

                                    else -> {
                                        permissionRequested = true
                                        permissionLauncher.launch(
                                            Manifest.permission.CAMERA
                                        )
                                    }
                                }
                            }
                        )
                    }

                    qrValue?.let { value ->
                        ScanResultCard(
                            qrValue = value,
                            onOpenBox = {
                                onOpenBox(value)
                            }
                        )

                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btnScanUlang")
                                .clickable(
                                    role = Role.Button,
                                    onClick = {
                                        qrValue = null
                                        statusText = if (cameraReady) {
                                            "Kamera aktif"
                                        } else {
                                            "Menyiapkan kamera…"
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ScanText(
                                text = "Scan box lainnya",
                                size = 14,
                                bold = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanHeader(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onNotification: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScanSurface)
            .then(modifier)
            .height(64.dp)
            .padding(horizontal = 8.dp)
            .testTag("headerScan"),
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
                tint = ScanBrown,
                modifier = Modifier.size(24.dp)
            )
        }

        ScanText(
            text = "QR Code Scanner",
            size = 16,
            bold = true,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        )

        IconButton(
            onClick = onNotification,
            modifier = Modifier
                .size(48.dp)
                .testTag("btnNotifikasi")
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_inv_notifikasi
                ),
                contentDescription = "Notifikasi",
                tint = ScanBrown,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private data class ScanNavItem(
    val tag: String,
    val label: String,
    @DrawableRes val icon: Int,
    val selected: Boolean = false
)

@Composable
private fun ScanBottomBar(
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        ScanNavItem(
            tag = "navBeranda",
            label = "Beranda",
            icon = R.drawable.ic_inv_beranda
        ),
        ScanNavItem(
            tag = "navInventori",
            label = "Inventori",
            icon = R.drawable.ic_inv_baju
        ),
        ScanNavItem(
            tag = "navPesanan",
            label = "Pesanan",
            icon = R.drawable.ic_inv_pesanan
        ),
        ScanNavItem(
            tag = "navBox",
            label = "Box",
            icon = R.drawable.ic_inv_box,
            selected = true
        ),
        ScanNavItem(
            tag = "navLainnya",
            label = "Lainnya",
            icon = R.drawable.ic_inv_menu
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScanSurface)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Bottom +
                            WindowInsetsSides.Horizontal
                )
            )
            .testTag("navbarScan")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ScanDivider)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items.forEach { item ->
                val itemColor = if (item.selected) {
                    ScanBrown
                } else {
                    ScanSecondary
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .height(64.dp)
                        .testTag(item.tag)
                        .then(
                            if (item.selected) {
                                Modifier.drawableBackground(
                                    R.drawable.bg_inv_nav_active
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable(
                            role = Role.Tab,
                            onClick = {
                                onNavigate(item.label)
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        tint = itemColor,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(Modifier.height(5.dp))

                    ScanText(
                        text = item.label,
                        size = 10,
                        color = itemColor,
                        bold = item.selected,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(4.dp))

                    // Slot tetap ada saat indikator tidak terlihat,
                    // sama seperti visibility="invisible" pada XML.
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(3.dp)
                            .background(
                                if (item.selected) {
                                    ScanBrown
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

@Composable
private fun ScanResultCard(
    qrValue: String,
    onOpenBox: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .testTag("cardHasilScan")
            .drawableBackground(R.drawable.bg_inv_card)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .drawableBackground(R.drawable.bg_inv_icon),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_inv_box),
                    contentDescription = null,
                    tint = ScanBrown,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                ScanText(
                    text = "Box $qrValue",
                    size = 16,
                    bold = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tvNamaBoxScan")
                )

                ScanText(
                    text = "QR terbaca. Buka untuk memeriksa data box.",
                    size = 12,
                    color = ScanSecondary,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth()
                        .testTag("tvInfoBoxScan")
                )
            }
        }

        ScanPrimaryAction(
            text = "Lihat isi box",
            showArrow = true,
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("btnLihatIsiBox"),
            onClick = onOpenBox
        )
    }
}

@Composable
private fun ScanPrimaryAction(
    text: String,
    modifier: Modifier = Modifier,
    showArrow: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .drawableBackground(R.drawable.bg_inv_button)
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScanText(
            text = text,
            size = 14,
            color = ScanSurface,
            bold = true
        )

        if (showArrow) {
            Spacer(Modifier.width(10.dp))

            Icon(
                painter = painterResource(R.drawable.ic_inv_back),
                contentDescription = null,
                tint = ScanSurface,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(180f)
            )
        }
    }
}

@Composable
private fun ScanText(
    text: String,
    size: Int,
    modifier: Modifier = Modifier,
    color: Color = ScanBrown,
    bold: Boolean = false,
    fontFamily: FontFamily = FontFamily.SansSerif,
    lineHeight: Int? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size.sp,
        fontWeight = if (bold) {
            FontWeight.Bold
        } else {
            FontWeight.Normal
        },
        fontFamily = fontFamily,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Clip,
        style = TextStyle(
            letterSpacing = 0.sp,
            lineHeight = lineHeight?.sp
                ?: androidx.compose.ui.unit.TextUnit.Unspecified,
            platformStyle = PlatformTextStyle(
                includeFontPadding = true
            )
        )
    )
}

/**
 * Menggambar drawable XML asli, termasuk shape, stroke,
 * gradient, dan sudutnya, di belakang konten Compose.
 *
 * clipToOutline digunakan pada container kamera sesuai XML.
 */
@Composable
private fun Modifier.drawableBackground(
    @DrawableRes resource: Int,
    clipToOutline: Boolean = false
): Modifier {
    val context = LocalContext.current

    val drawable = remember(context, resource) {
        requireNotNull(
            ContextCompat.getDrawable(context, resource)
        ) {
            "Drawable tidak ditemukan: $resource"
        }.mutate()
    }

    return drawWithContent {
        drawable.setBounds(
            0,
            0,
            size.width.toInt(),
            size.height.toInt()
        )

        val canvas = drawContext.canvas
        canvas.save()

        try {
            val nativeCanvas = canvas.nativeCanvas

            if (clipToOutline) {
                val outline = Outline()
                val outlineRect = Rect()

                drawable.getOutline(outline)

                if (outline.getRect(outlineRect)) {
                    val clipPath = Path().apply {
                        addRoundRect(
                            RectF(outlineRect),
                            outline.radius,
                            outline.radius,
                            Path.Direction.CW
                        )
                    }

                    nativeCanvas.clipPath(clipPath)
                }
            }

            drawable.draw(nativeCanvas)
            drawContent()
        } finally {
            canvas.restore()
        }
    }
}

private fun formatZoom(value: Float): String {
    val number = if (value == value.toInt().toFloat()) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }

    return "$number×"
}

/**
 * Preview kamera nyata dan pembacaan QR.
 * Bingkai pada UI berfungsi sebagai panduan posisi,
 * sesuai komentar pada XML.
 */
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun ScanCameraPreview(
    scanningEnabled: Boolean,
    onCameraBound: (Camera) -> Unit,
    onStreaming: () -> Unit,
    onError: (String) -> Unit,
    onQrDetected: (String) -> Unit
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF292521))
                .testTag("previewCamera"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pratinjau kamera",
                color = ScanSurface,
                fontSize = 14.sp
            )
        }

        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Lanjutkan seluruh kode kamera yang sudah ada,
    // mulai dari rememberUpdatedState hingga DisposableEffect.

    val currentScanningEnabled by rememberUpdatedState(scanningEnabled)
    val currentOnCameraBound by rememberUpdatedState(onCameraBound)
    val currentOnStreaming by rememberUpdatedState(onStreaming)
    val currentOnError by rememberUpdatedState(onError)
    val currentOnQrDetected by rememberUpdatedState(onQrDetected)

    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode =
                PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER

            importantForAccessibility =
                android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier
            .fillMaxSize()
            .testTag("previewCamera")
    )

    DisposableEffect(previewView, lifecycleOwner) {
        val executor = ContextCompat.getMainExecutor(context)

        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(
                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
            )
            .build()

        val providerFuture = ProcessCameraProvider.getInstance(context)

        var provider: ProcessCameraProvider? = null
        var boundCamera: Camera? = null
        var disposed = false
        var processing = false

        val streamObserver = Observer<PreviewView.StreamState> { state ->
            if (!disposed && state == PreviewView.StreamState.STREAMING) {
                currentOnStreaming()
            }
        }

        val cameraStateObserver =
            Observer<androidx.camera.core.CameraState> { state ->
                if (!disposed && state.error != null) {
                    currentOnError(
                        "Kamera tidak tersedia. Coba buka kamera lagi."
                    )
                }
            }

        previewView.previewStreamState.observe(
            lifecycleOwner,
            streamObserver
        )

        analysis.setAnalyzer(executor) { imageProxy ->
            if (disposed || processing || !currentScanningEnabled) {
                imageProxy.close()
            } else {
                val mediaImage = imageProxy.image

                if (mediaImage == null) {
                    imageProxy.close()
                } else {
                    processing = true

                    try {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        scanner.process(image)
                            .addOnSuccessListener(executor) { barcodes ->
                                if (!disposed && currentScanningEnabled) {
                                    val value = barcodes
                                        .firstOrNull {
                                            !it.rawValue.isNullOrBlank()
                                        }
                                        ?.rawValue

                                    if (value != null) {
                                        currentOnQrDetected(value)
                                    }
                                }
                            }
                            .addOnFailureListener(executor) {
                                if (!disposed) {
                                    currentOnError(
                                        "QR gagal diproses. " +
                                                "Coba buka kamera lagi."
                                    )
                                }
                            }
                            .addOnCompleteListener(executor) {
                                imageProxy.close()
                                processing = false

                                if (disposed) {
                                    scanner.close()
                                }
                            }
                    } catch (exception: Exception) {
                        imageProxy.close()
                        processing = false

                        if (!disposed) {
                            currentOnError(
                                "Gambar kamera gagal diproses."
                            )
                        }
                    }
                }
            }
        }

        providerFuture.addListener(
            {
                if (!disposed) {
                    try {
                        val cameraProvider = providerFuture.get()
                        provider = cameraProvider

                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            analysis
                        )

                        boundCamera = camera

                        camera.cameraInfo.cameraState.observe(
                            lifecycleOwner,
                            cameraStateObserver
                        )

                        currentOnCameraBound(camera)
                    } catch (exception: Exception) {
                        currentOnError(
                            "Kamera gagal dibuka. " +
                                    "Pastikan kamera tersedia dan izin diberikan."
                        )
                    }
                }
            },
            executor
        )

        onDispose {
            disposed = true

            previewView.previewStreamState.removeObserver(
                streamObserver
            )

            boundCamera?.cameraInfo?.cameraState?.removeObserver(
                cameraStateObserver
            )

            analysis.clearAnalyzer()
            provider?.unbind(preview, analysis)
            preview.setSurfaceProvider(null)

            // Tunggu pemrosesan yang sedang berjalan sebelum menutup scanner.
            if (!processing) {
                scanner.close()
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 800)
@Composable
private fun ScanQrScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = ScanBrown,
            onPrimary = ScanSurface,
            background = ScanBackground,
            onBackground = ScanBrown,
            surface = ScanSurface,
            onSurface = ScanBrown,
        )
    ) {
        ScanQrScreen(
            onBack = {},
            onNotification = {},
            onNavigate = {},
            onOpenBox = {},
        )
    }
}