
package com.example.rumattire

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
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

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean


// ============================================================
// ROOM ATTIRE WEDDING
// EMPLOYEE - SCAN QR / BARCODE
// JETPACK COMPOSE + MATERIAL 3 + CAMERAX + ML KIT
// ============================================================


// ============================================================
// 1. COLOR PALETTE
// ============================================================

private val AtelierBackground = Color(0xFFF7F2E9)
private val AtelierSurface = Color(0xFFFCF9F3)

private val AtelierBrown = Color(0xFF3B2A22)
private val AtelierBrownDark = Color(0xFF241812)
private val AtelierBrownLight = Color(0xFF6B5A4E)
private val AtelierMuted = Color(0xFF8B7A6E)

private val AtelierGold = Color(0xFFC9A86A)
private val AtelierGoldLight = Color(0xFFE9DECC)

private val AtelierTerracotta = Color(0xFFE8704A)

private val AtelierTeal = Color(0xFF3CC4B3)
private val AtelierTealDark = Color(0xFF13786E)
private val AtelierTealBackground = Color(0xFFDDF2EE)

private val AtelierPurple = Color(0xFF8061E8)
private val AtelierPurpleBackground = Color(0xFFEDE7FC)

private val AtelierBorder = Color(0xFFE8DCCB)


// ============================================================
// 2. ACTIVITY
// ============================================================

class ScanEmployeeActivity : ComponentActivity() {

    companion object {

        const val EXTRA_SCAN_VALUE = "extra_scan_value"
        const val EXTRA_SCAN_FORMAT = "extra_scan_format"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = AtelierBrown,
                    onPrimary = AtelierSurface,
                    background = AtelierBackground,
                    onBackground = AtelierBrown,
                    surface = AtelierSurface,
                    onSurface = AtelierBrown,
                    secondary = AtelierGold
                )
            ) {

                ScanEmployeeScreen(

                    onNotificationClick = {

                        Toast.makeText(
                            this,
                            "Halaman notifikasi belum disambungkan",
                            Toast.LENGTH_SHORT
                        ).show()
                    },

                    onNavigate = { menu ->

                        Toast.makeText(
                            this,
                            "Navigasi $menu belum disambungkan",
                            Toast.LENGTH_SHORT
                        ).show()
                    },

                    onScanResult = { value, format ->

                        // Hasil scan dikirim kembali kepada Activity pemanggil.
                        // Tidak ada permintaan API atau database.

                        val result = Intent().apply {

                            putExtra(EXTRA_SCAN_VALUE, value)
                            putExtra(EXTRA_SCAN_FORMAT, format)
                        }

                        setResult(Activity.RESULT_OK, result)
                    }
                )
            }
        }
    }
}


// ============================================================
// 3. CONTEXT HELPER
// ============================================================

private tailrec fun Context.findActivity(): Activity? {

    return when (this) {

        is Activity -> this

        is ContextWrapper -> baseContext.findActivity()

        else -> null
    }
}


// ============================================================
// 4. MAIN SCREEN
// ============================================================

@Composable
fun ScanEmployeeScreen(
    onNotificationClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onScanResult: (String, String) -> Unit = { _, _ -> }
) {

    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current

    val isPreview = LocalInspectionMode.current

    val mainExecutor = remember(context) {

        ContextCompat.getMainExecutor(context)
    }


    // --------------------------------------------------------
    // CAMERA PERMISSION
    // --------------------------------------------------------

    var permissionGranted by remember(isPreview, context) {

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


    // --------------------------------------------------------
    // CAMERA STATE
    // --------------------------------------------------------

    var camera by remember {

        mutableStateOf<Camera?>(null)
    }

    var cameraReady by remember {

        mutableStateOf(false)
    }

    var cameraError by remember {

        mutableStateOf<String?>(null)
    }

    var statusText by remember {

        mutableStateOf("Menyiapkan kamera...")
    }

    var flashEnabled by remember {

        mutableStateOf(false)
    }

    var cameraSession by remember {

        mutableIntStateOf(0)
    }


    // --------------------------------------------------------
    // SCAN RESULT
    // --------------------------------------------------------

    var scannedValue by rememberSaveable {

        mutableStateOf<String?>(null)
    }

    var scannedFormat by rememberSaveable {

        mutableStateOf<String?>(null)
    }


    // --------------------------------------------------------
    // SCREEN STATE
    // --------------------------------------------------------

    var selectedMode by rememberSaveable {

        mutableStateOf("scan")
    }

    var showManualDialog by rememberSaveable {

        mutableStateOf(false)
    }

    var manualId by rememberSaveable {

        mutableStateOf("")
    }


    // ========================================================
    // 5. PERMISSION LAUNCHER
    // ========================================================

    val permissionLauncher = rememberLauncherForActivityResult(

        contract = ActivityResultContracts.RequestPermission()

    ) { granted ->

        permissionGranted = granted

        if (granted) {

            permissionPermanentlyDenied = false

            cameraError = null

            camera = null

            cameraReady = false

            statusText = "Menyiapkan kamera..."

            cameraSession++

        } else {

            permissionPermanentlyDenied =

                activity != null &&

                        !ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            Manifest.permission.CAMERA
                        )

            statusText = "Izin kamera diperlukan"
        }
    }


    // Minta izin kamera saat layar pertama dibuka.

    LaunchedEffect(isPreview) {

        if (
            !isPreview &&
            !permissionGranted &&
            !permissionRequested
        ) {

            permissionRequested = true

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }


    // ========================================================
    // 6. CHECK PERMISSION WHEN RETURNING TO APP
    // ========================================================

    DisposableEffect(lifecycleOwner, context, isPreview) {

        val observer = LifecycleEventObserver { _, event ->

            if (
                !isPreview &&
                event == Lifecycle.Event.ON_RESUME
            ) {

                val granted =

                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                permissionGranted = granted

                if (granted) {

                    permissionPermanentlyDenied = false

                    cameraError = null

                    statusText = "Menyiapkan kamera..."

                    cameraSession++

                } else {

                    camera = null

                    cameraReady = false

                    flashEnabled = false

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


    // ========================================================
    // 7. OBSERVE CAMERA FLASH STATE
    // ========================================================

    DisposableEffect(camera, lifecycleOwner) {

        val currentCamera = camera

        val torchObserver = Observer<Int> { state ->

            flashEnabled = state == TorchState.ON
        }

        currentCamera?.cameraInfo?.torchState?.observe(
            lifecycleOwner,
            torchObserver
        )

        onDispose {

            currentCamera?.cameraInfo?.torchState?.removeObserver(
                torchObserver
            )
        }
    }


    // ========================================================
    // 8. HANDLE SCAN RESULT
    // ========================================================

    val handleScanResult: (String, String) -> Unit = { value, format ->

        if (value.isNotBlank()) {

            scannedValue = value

            scannedFormat = format

            statusText = "Kode berhasil dibaca"

            // Hanya menyimpan hasil sementara di aplikasi.
            // Tidak mengakses backend.

            onScanResult(value, format)
        }
    }


    // ========================================================
    // 9. FLASHLIGHT CONTROL
    // ========================================================

    val toggleFlash: () -> Unit = {

        val currentCamera = camera

        if (currentCamera != null && cameraReady) {

            if (currentCamera.cameraInfo.hasFlashUnit()) {

                val operation =

                    currentCamera.cameraControl.enableTorch(
                        !flashEnabled
                    )

                operation.addListener(

                    {

                        runCatching {

                            operation.get()

                        }.onFailure {

                            Toast.makeText(
                                context,
                                "Senter gagal diaktifkan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },

                    mainExecutor
                )

            } else {

                Toast.makeText(
                    context,
                    "Perangkat tidak memiliki lampu flash",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            Toast.makeText(
                context,
                "Kamera belum siap",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // ========================================================
    // 10. CAMERA RETRY
    // ========================================================

    val retryCamera: () -> Unit = {

        if (permissionGranted) {

            camera = null

            cameraReady = false

            cameraError = null

            statusText = "Menghubungkan kamera..."

            cameraSession++

        } else if (permissionPermanentlyDenied) {

            try {

                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                ).apply {

                    data = Uri.parse(
                        "package:${context.packageName}"
                    )
                }

                context.startActivity(intent)

            } catch (exception: Exception) {

                Toast.makeText(
                    context,
                    "Pengaturan aplikasi tidak dapat dibuka",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            permissionRequested = true

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }


    // ========================================================
    // 11. MAIN SCAFFOLD
    // ========================================================

    Scaffold(

        modifier = Modifier.fillMaxSize(),

        containerColor = AtelierBackground,

        bottomBar = {

            EmployeeBottomNavigation(

                selectedItem = "Scan",

                onNavigate = onNavigate
            )
        }

    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())

        ) {

            // ------------------------------------------------
            // HEADER
            // ------------------------------------------------

            ScanHeader(

                onNotificationClick = onNotificationClick
            )


            Spacer(modifier = Modifier.height(24.dp))


            // ------------------------------------------------
            // MODE SELECTOR
            // ------------------------------------------------

            ScanModeSelector(

                selectedMode = selectedMode,

                modifier = Modifier.padding(horizontal = 24.dp),

                onScanClick = {

                    selectedMode = "scan"

                    showManualDialog = false
                },

                onManualClick = {

                    selectedMode = "manual"

                    showManualDialog = true
                }
            )


            Spacer(modifier = Modifier.height(24.dp))


            // ------------------------------------------------
            // LIVE CAMERA SCANNER
            // ------------------------------------------------

            ScannerCameraCard(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(320.dp),

                permissionGranted = permissionGranted,

                cameraSession = cameraSession,

                scanningEnabled = scannedValue == null,

                cameraReady = cameraReady,

                statusText = cameraError ?: statusText,

                onCameraBound = { newCamera ->

                    camera = newCamera
                },

                onStreaming = {

                    cameraReady = true

                    cameraError = null

                    statusText = "Kamera aktif"
                },

                onError = { message ->

                    cameraReady = false

                    cameraError = message

                    statusText = message
                },

                onBarcodeDetected = { value, format ->

                    handleScanResult(
                        value,
                        barcodeFormatName(format)
                    )
                }
            )


            // ------------------------------------------------
            // PERMISSION / CAMERA ERROR ACTION
            // ------------------------------------------------

            if (!permissionGranted || cameraError != null) {

                Button(

                    onClick = retryCamera,

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 14.dp
                        )
                        .height(48.dp),

                    shape = RoundedCornerShape(15.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = AtelierBrown
                    )

                ) {

                    Text(

                        text = when {

                            permissionGranted -> "Coba kamera lagi"

                            permissionPermanentlyDenied ->
                                "Buka pengaturan kamera"

                            else -> "Izinkan akses kamera"
                        },

                        fontWeight = FontWeight.Bold
                    )
                }
            }


            Spacer(modifier = Modifier.height(20.dp))


            // ------------------------------------------------
            // ACTION BUTTONS
            // ------------------------------------------------

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),

                horizontalArrangement = Arrangement.spacedBy(14.dp)

            ) {

                ScanActionButton(

                    modifier = Modifier.weight(1f),

                    title = "Senter",

                    enabled = cameraReady,

                    icon = {

                        Icon(
                            imageVector = Icons.Outlined.FlashOn,
                            contentDescription = "Senter",
                            tint = Color(0xFFC18427),
                            modifier = Modifier.size(25.dp)
                        )
                    },

                    iconBackground = Color(0xFFFCF0D1),

                    onClick = toggleFlash
                )


                ScanActionButton(

                    modifier = Modifier.weight(1f),

                    title = "QR rusak?",

                    enabled = true,

                    icon = {

                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Input ID manual",
                            tint = AtelierPurple,
                            modifier = Modifier.size(25.dp)
                        )
                    },

                    iconBackground = AtelierPurpleBackground,

                    onClick = {

                        selectedMode = "manual"

                        showManualDialog = true
                    }
                )
            }


            // ------------------------------------------------
            // SCAN RESULT
            // ------------------------------------------------

            if (scannedValue != null) {

                ScanResultCard(

                    value = scannedValue!!,

                    format = scannedFormat ?: "Barcode",

                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 22.dp
                    ),

                    onScanAgain = {

                        scannedValue = null

                        scannedFormat = null

                        cameraError = null

                        statusText = "Menyiapkan kamera..."

                        cameraReady = false

                        camera = null

                        cameraSession++
                    }
                )
            }


            Spacer(modifier = Modifier.height(24.dp))


            // ------------------------------------------------
            // INFORMATION CARD
            // ------------------------------------------------

            ScanInformationCard(

                modifier = Modifier.padding(horizontal = 24.dp)
            )


            Spacer(modifier = Modifier.height(24.dp))
        }
    }


    // ========================================================
    // 12. MANUAL ID DIALOG
    // ========================================================

    if (showManualDialog) {

        AlertDialog(

            onDismissRequest = {

                showManualDialog = false

                selectedMode = "scan"
            },

            containerColor = AtelierBackground,

            shape = RoundedCornerShape(25.dp),

            title = {

                Text(
                    text = "Masukkan ID Box",
                    color = AtelierBrown,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },

            text = {

                Column {

                    Text(
                        text = "Masukkan ID secara manual jika QR Code atau barcode tidak dapat dipindai.",
                        color = AtelierMuted,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(

                        value = manualId,

                        onValueChange = {
                            manualId = it
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true,

                        label = {
                            Text("ID Box")
                        },

                        placeholder = {
                            Text("Contoh: BOX-001")
                        },

                        shape = RoundedCornerShape(14.dp),

                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = AtelierBrown,

                            unfocusedBorderColor = AtelierBorder,

                            focusedLabelColor = AtelierBrown,

                            cursorColor = AtelierBrown
                        )
                    )
                }
            },

            confirmButton = {

                TextButton(

                    enabled = manualId.isNotBlank(),

                    onClick = {

                        handleScanResult(
                            manualId.trim(),
                            "Input Manual"
                        )

                        manualId = ""

                        selectedMode = "scan"

                        showManualDialog = false
                    }

                ) {

                    Text(
                        text = "Gunakan ID",
                        color = AtelierBrown,
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showManualDialog = false

                        selectedMode = "scan"
                    }

                ) {

                    Text(
                        text = "Batal",
                        color = AtelierMuted
                    )
                }
            }
        )
    }
}


// ============================================================
// 13. SCAN HEADER
// ============================================================

@Composable
private fun ScanHeader(

    onNotificationClick: () -> Unit

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            ),

        horizontalArrangement = Arrangement.SpaceBetween,

        verticalAlignment = Alignment.CenterVertically

    ) {

        Column {

            Text(

                text = "Scan Box",

                fontSize = 29.sp,

                fontWeight = FontWeight.Normal,

                fontFamily = FontFamily.Serif,

                color = AtelierBrown,

                letterSpacing = (-0.6).sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(

                text = "Lihat isi box penyimpanan",

                fontSize = 14.sp,

                color = AtelierMuted
            )
        }


        Box(

            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(onClick = onNotificationClick),

            contentAlignment = Alignment.Center

        ) {

            Icon(

                imageVector = Icons.Outlined.Notifications,

                contentDescription = "Notifikasi",

                tint = AtelierBrown,

                modifier = Modifier.size(27.dp)
            )


            Box(

                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp, end = 5.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(AtelierTerracotta)
                    .border(
                        1.dp,
                        AtelierBackground,
                        CircleShape
                    )
            )
        }
    }
}


// ============================================================
// 14. SCAN MODE SELECTOR
// ============================================================

@Composable
private fun ScanModeSelector(

    selectedMode: String,

    modifier: Modifier = Modifier,

    onScanClick: () -> Unit,

    onManualClick: () -> Unit

) {

    Row(

        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(AtelierGoldLight)
            .padding(5.dp),

        verticalAlignment = Alignment.CenterVertically

    ) {

        // SCAN QR

        ModeTab(

            modifier = Modifier.weight(1f),

            title = "Scan QR",

            selected = selectedMode == "scan",

            icon = {

                Icon(
                    imageVector = Icons.Outlined.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },

            onClick = onScanClick
        )


        // KETIK ID

        ModeTab(

            modifier = Modifier.weight(1f),

            title = "Ketik ID",

            selected = selectedMode == "manual",

            icon = {

                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },

            onClick = onManualClick
        )
    }
}


@Composable
private fun ModeTab(

    modifier: Modifier = Modifier,

    title: String,

    selected: Boolean,

    icon: @Composable () -> Unit,

    onClick: () -> Unit

) {

    Row(

        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(50.dp))
            .background(
                if (selected) AtelierBrown
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),

        horizontalArrangement = Arrangement.Center,

        verticalAlignment = Alignment.CenterVertically

    ) {

        CompositionLocalProvider(

            LocalContentColor provides
                    if (selected) Color.White
                    else AtelierBrown

        ) {

            icon()
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(

            text = title,

            color = if (selected) Color.White
            else AtelierBrown,

            fontSize = 15.sp,

            fontWeight = FontWeight.Bold,

            maxLines = 1
        )
    }
}


// ============================================================
// 15. CAMERA SCANNER CARD
// ============================================================

@Composable
private fun ScannerCameraCard(

    modifier: Modifier = Modifier,

    permissionGranted: Boolean,

    cameraSession: Int,

    scanningEnabled: Boolean,

    cameraReady: Boolean,

    statusText: String,

    onCameraBound: (Camera) -> Unit,

    onStreaming: () -> Unit,

    onError: (String) -> Unit,

    onBarcodeDetected: (String, Int) -> Unit

) {

    Box(

        modifier = modifier
            .clip(RoundedCornerShape(38.dp))
            .background(

                Brush.verticalGradient(

                    colors = listOf(
                        Color(0xFF3E2D25),
                        AtelierBrownDark
                    )
                )
            )
            .border(
                1.dp,
                Color(0xFF66503A),
                RoundedCornerShape(38.dp)
            )

    ) {

        // ----------------------------------------------------
        // LIVE CAMERA PREVIEW
        // ----------------------------------------------------

        if (permissionGranted) {

            key(cameraSession) {

                ScanCameraPreview(

                    modifier = Modifier.fillMaxSize(),

                    scanningEnabled = scanningEnabled,

                    onCameraBound = onCameraBound,

                    onStreaming = onStreaming,

                    onError = onError,

                    onBarcodeDetected = onBarcodeDetected
                )
            }

            // Sedikit lapisan gelap agar overlay tetap jelas.

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.16f)
                    )
            )

        } else {

            // CAMERA PERMISSION PLACEHOLDER

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),

                horizontalAlignment = Alignment.CenterHorizontally,

                verticalArrangement = Arrangement.Center

            ) {

                Icon(

                    imageVector = Icons.Outlined.CameraAlt,

                    contentDescription = null,

                    tint = AtelierGold,

                    modifier = Modifier.size(43.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(

                    text = "Akses kamera diperlukan",

                    color = Color.White,

                    fontSize = 16.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(

                    text = "Izinkan kamera untuk mulai memindai QR Code dan barcode.",

                    color = Color(0xFFD9CEC1),

                    fontSize = 12.sp,

                    textAlign = TextAlign.Center,

                    lineHeight = 18.sp
                )
            }
        }


        // ----------------------------------------------------
        // SCANNER FRAME OVERLAY
        // ----------------------------------------------------

        ScannerFrameOverlay(
            modifier = Modifier.fillMaxSize()
        )


        // ----------------------------------------------------
        // CAMERA STATUS PILL
        // ----------------------------------------------------

        Surface(

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp),

            color = Color(0xDD241812),

            shape = CircleShape

        ) {

            Row(

                modifier = Modifier.padding(
                    horizontal = 15.dp,
                    vertical = 10.dp
                ),

                verticalAlignment = Alignment.CenterVertically

            ) {

                Box(

                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(

                            if (cameraReady) AtelierTeal
                            else AtelierGold
                        )
                )

                Spacer(modifier = Modifier.width(9.dp))

                Text(

                    text = when {

                        !permissionGranted ->
                            "Menunggu izin kamera"

                        !cameraReady ->
                            statusText

                        !scanningEnabled ->
                            "Kode berhasil dibaca"

                        else ->
                            "Arahkan ke QR pada box"
                    },

                    color = Color(0xFFF5EFE6),

                    fontSize = 12.sp,

                    fontWeight = FontWeight.Medium,

                    maxLines = 1,

                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


// ============================================================
// 16. SCANNER FRAME + ANIMATED SCAN LINE
// ============================================================

@Composable
private fun ScannerFrameOverlay(

    modifier: Modifier = Modifier

) {

    val infiniteTransition = rememberInfiniteTransition(
        label = "scanner_transition"
    )

    val scanProgress by infiniteTransition.animateFloat(

        initialValue = 0f,

        targetValue = 1f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                durationMillis = 2200,
                easing = LinearEasing
            ),

            repeatMode = RepeatMode.Restart
        ),

        label = "scan_line_progress"
    )


    Canvas(modifier = modifier) {

        val width = size.width
        val height = size.height

        val left = width * 0.18f
        val right = width * 0.82f

        val top = height * 0.15f
        val bottom = height * 0.83f

        val cornerLength = width * 0.10f

        val stroke = 3.5.dp.toPx()


        // TOP LEFT

        drawLine(
            AtelierGold,
            Offset(left, top + cornerLength),
            Offset(left, top),
            stroke,
            StrokeCap.Round
        )

        drawLine(
            AtelierGold,
            Offset(left, top),
            Offset(left + cornerLength, top),
            stroke,
            StrokeCap.Round
        )


        // TOP RIGHT

        drawLine(
            AtelierGold,
            Offset(right - cornerLength, top),
            Offset(right, top),
            stroke,
            StrokeCap.Round
        )

        drawLine(
            AtelierGold,
            Offset(right, top),
            Offset(right, top + cornerLength),
            stroke,
            StrokeCap.Round
        )


        // BOTTOM LEFT

        drawLine(
            AtelierGold,
            Offset(left, bottom - cornerLength),
            Offset(left, bottom),
            stroke,
            StrokeCap.Round
        )

        drawLine(
            AtelierGold,
            Offset(left, bottom),
            Offset(left + cornerLength, bottom),
            stroke,
            StrokeCap.Round
        )


        // BOTTOM RIGHT

        drawLine(
            AtelierGold,
            Offset(right - cornerLength, bottom),
            Offset(right, bottom),
            stroke,
            StrokeCap.Round
        )

        drawLine(
            AtelierGold,
            Offset(right, bottom - cornerLength),
            Offset(right, bottom),
            stroke,
            StrokeCap.Round
        )


        // ANIMATED TURQUOISE SCAN LINE

        val lineY = top + (bottom - top) * scanProgress

        drawLine(

            brush = Brush.horizontalGradient(

                colors = listOf(
                    Color.Transparent,
                    AtelierTeal,
                    Color.Transparent
                ),

                startX = left,
                endX = right
            ),

            start = Offset(left, lineY),

            end = Offset(right, lineY),

            strokeWidth = 2.5.dp.toPx(),

            cap = StrokeCap.Round
        )
    }
}


// ============================================================
// 17. REAL CAMERAX + ML KIT
// ============================================================

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun ScanCameraPreview(

    modifier: Modifier = Modifier,

    scanningEnabled: Boolean,

    onCameraBound: (Camera) -> Unit,

    onStreaming: () -> Unit,

    onError: (String) -> Unit,

    onBarcodeDetected: (String, Int) -> Unit

) {

    if (LocalInspectionMode.current) {

        Box(

            modifier = modifier.background(Color(0xFF292521)),

            contentAlignment = Alignment.Center

        ) {

            Text(
                text = "Preview kamera",
                color = Color.White,
                fontSize = 14.sp
            )
        }

        return
    }


    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    val mainExecutor = remember(context) {

        ContextCompat.getMainExecutor(context)
    }


    // Update callback tanpa membuat ulang camera session.

    val currentScanningEnabled by rememberUpdatedState(
        scanningEnabled
    )

    val currentOnCameraBound by rememberUpdatedState(
        onCameraBound
    )

    val currentOnStreaming by rememberUpdatedState(
        onStreaming
    )

    val currentOnError by rememberUpdatedState(
        onError
    )

    val currentOnBarcodeDetected by rememberUpdatedState(
        onBarcodeDetected
    )


    // --------------------------------------------------------
    // PREVIEW VIEW
    // --------------------------------------------------------

    val previewView = remember(context) {

        PreviewView(context).apply {

            implementationMode =
                PreviewView.ImplementationMode.COMPATIBLE

            scaleType =
                PreviewView.ScaleType.FILL_CENTER

            setBackgroundColor(
                android.graphics.Color.BLACK
            )
        }
    }


    AndroidView(

        factory = { previewView },

        modifier = modifier
    )


    // --------------------------------------------------------
    // CAMERA LIFECYCLE
    // --------------------------------------------------------

    DisposableEffect(previewView, lifecycleOwner) {

        val cameraExecutor = Executors.newSingleThreadExecutor()


        // ML KIT: SUPPORT ALL BARCODE FORMATS

        val scannerOptions = BarcodeScannerOptions.Builder()

            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            )

            .build()


        val scanner = BarcodeScanning.getClient(
            scannerOptions
        )


        // CAMERA PREVIEW

        val preview = Preview.Builder()
            .build()
            .also {

                it.setSurfaceProvider(
                    previewView.surfaceProvider
                )
            }


        // IMAGE ANALYSIS

        val analysis = ImageAnalysis.Builder()

            .setBackpressureStrategy(
                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
            )

            .build()


        // THREAD SAFETY

        val disposed = AtomicBoolean(false)

        val processing = AtomicBoolean(false)

        val resultSent = AtomicBoolean(false)

        val scannerClosed = AtomicBoolean(false)


        var provider: ProcessCameraProvider? = null

        var boundCamera: Camera? = null


        // ----------------------------------------------------
        // CLOSE SCANNER SAFELY
        // ----------------------------------------------------

        fun closeScannerIfDisposed() {

            if (
                disposed.get() &&
                !processing.get() &&
                scannerClosed.compareAndSet(false, true)
            ) {

                scanner.close()

                cameraExecutor.shutdown()
            }
        }


        // ----------------------------------------------------
        // PREVIEW STREAM OBSERVER
        // ----------------------------------------------------

        val streamObserver = Observer<PreviewView.StreamState> {

            if (
                !disposed.get() &&
                it == PreviewView.StreamState.STREAMING
            ) {

                currentOnStreaming()
            }
        }


        // ----------------------------------------------------
        // CAMERA STATE OBSERVER
        // ----------------------------------------------------

        val cameraStateObserver = Observer<CameraState> {

            if (
                !disposed.get() &&
                it.error != null
            ) {

                currentOnError(
                    "Kamera mengalami gangguan. Coba buka kembali."
                )
            }
        }


        previewView.previewStreamState.observe(
            lifecycleOwner,
            streamObserver
        )


        // ----------------------------------------------------
        // IMAGE ANALYSIS / BARCODE DETECTION
        // ----------------------------------------------------

        analysis.setAnalyzer(cameraExecutor) { imageProxy ->

            if (
                disposed.get() ||
                !currentScanningEnabled ||
                !processing.compareAndSet(false, true)
            ) {

                imageProxy.close()

                return@setAnalyzer
            }


            // Pastikan tidak memulai pemrosesan setelah dispose.

            if (
                disposed.get() ||
                !currentScanningEnabled
            ) {

                imageProxy.close()

                processing.set(false)

                closeScannerIfDisposed()

                return@setAnalyzer
            }


            val mediaImage = imageProxy.image

            if (mediaImage == null) {

                imageProxy.close()

                processing.set(false)

                closeScannerIfDisposed()

                return@setAnalyzer
            }


            try {

                // Convert CameraX ImageProxy to ML Kit InputImage.

                val inputImage = InputImage.fromMediaImage(

                    mediaImage,

                    imageProxy.imageInfo.rotationDegrees
                )


                // REAL-TIME BARCODE SCANNING

                scanner.process(inputImage)

                    .addOnSuccessListener(cameraExecutor) { barcodes ->

                        if (
                            !disposed.get() &&
                            currentScanningEnabled
                        ) {

                            val detectedBarcode = barcodes.firstOrNull {

                                !it.rawValue.isNullOrBlank()
                            }


                            if (
                                detectedBarcode != null &&
                                resultSent.compareAndSet(false, true)
                            ) {

                                val value =
                                    detectedBarcode.rawValue.orEmpty()

                                val format =
                                    detectedBarcode.format


                                // Return result to main UI thread.

                                mainExecutor.execute {

                                    if (
                                        !disposed.get() &&
                                        currentScanningEnabled
                                    ) {

                                        currentOnBarcodeDetected(
                                            value,
                                            format
                                        )
                                    }
                                }
                            }
                        }
                    }

                    .addOnFailureListener(cameraExecutor) {

                        // Frame yang gagal dianalisis dilewati.
                        // Kamera tetap berjalan dan mencoba frame berikutnya.
                    }

                    .addOnCompleteListener(cameraExecutor) {

                        // WAJIB: release ImageProxy.

                        imageProxy.close()

                        processing.set(false)

                        closeScannerIfDisposed()
                    }

            } catch (exception: Exception) {

                imageProxy.close()

                processing.set(false)

                if (!disposed.get()) {

                    mainExecutor.execute {

                        currentOnError(
                            "Gambar kamera gagal diproses."
                        )
                    }
                }

                closeScannerIfDisposed()
            }
        }


        // ----------------------------------------------------
        // INITIALIZE CAMERA PROVIDER
        // ----------------------------------------------------

        val providerFuture =
            ProcessCameraProvider.getInstance(context)


        providerFuture.addListener(

            {

                if (!disposed.get()) {

                    try {

                        val cameraProvider = providerFuture.get()

                        provider = cameraProvider


                        // Bind camera to current lifecycle.

                        val activeCamera = cameraProvider.bindToLifecycle(

                            lifecycleOwner,

                            CameraSelector.DEFAULT_BACK_CAMERA,

                            preview,

                            analysis
                        )


                        boundCamera = activeCamera


                        activeCamera.cameraInfo.cameraState.observe(

                            lifecycleOwner,

                            cameraStateObserver
                        )


                        currentOnCameraBound(activeCamera)

                    } catch (exception: Exception) {

                        currentOnError(

                            "Kamera gagal dibuka. Pastikan izin kamera aktif."
                        )
                    }
                }
            },

            mainExecutor
        )


        // ----------------------------------------------------
        // CLEAN UP
        // ----------------------------------------------------

        onDispose {

            disposed.set(true)


            previewView.previewStreamState.removeObserver(
                streamObserver
            )


            boundCamera?.cameraInfo?.cameraState?.removeObserver(
                cameraStateObserver
            )


            analysis.clearAnalyzer()


            provider?.unbind(
                preview,
                analysis
            )


            closeScannerIfDisposed()
        }
    }
}


// ============================================================
// 18. BARCODE FORMAT NAME
// ============================================================

private fun barcodeFormatName(format: Int): String {

    return when (format) {

        Barcode.FORMAT_QR_CODE ->
            "QR Code"

        Barcode.FORMAT_CODE_128 ->
            "Code 128"

        Barcode.FORMAT_CODE_39 ->
            "Code 39"

        Barcode.FORMAT_CODE_93 ->
            "Code 93"

        Barcode.FORMAT_CODABAR ->
            "Codabar"

        Barcode.FORMAT_EAN_13 ->
            "EAN-13"

        Barcode.FORMAT_EAN_8 ->
            "EAN-8"

        Barcode.FORMAT_ITF ->
            "ITF"

        Barcode.FORMAT_UPC_A ->
            "UPC-A"

        Barcode.FORMAT_UPC_E ->
            "UPC-E"

        Barcode.FORMAT_PDF417 ->
            "PDF417"

        Barcode.FORMAT_AZTEC ->
            "Aztec"

        Barcode.FORMAT_DATA_MATRIX ->
            "Data Matrix"

        else ->
            "Barcode"
    }
}


// ============================================================
// 19. ACTION BUTTON
// ============================================================

@Composable
private fun ScanActionButton(

    modifier: Modifier = Modifier,

    title: String,

    enabled: Boolean,

    iconBackground: Color,

    icon: @Composable () -> Unit,

    onClick: () -> Unit

) {

    Surface(

        modifier = modifier
            .height(76.dp)
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),

        color = AtelierSurface,

        shape = RoundedCornerShape(21.dp),

        border = BorderStroke(
            1.dp,
            AtelierBorder
        )

    ) {

        Row(

            modifier = Modifier.padding(horizontal = 9.dp),

            horizontalArrangement = Arrangement.Center,

            verticalAlignment = Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBackground),

                contentAlignment = Alignment.Center

            ) {

                icon()
            }

            Spacer(modifier = Modifier.width(9.dp))

            Text(

                text = title,

                color = AtelierBrown,

                fontSize = 14.sp,

                fontWeight = FontWeight.Bold,

                maxLines = 1
            )
        }
    }
}


// ============================================================
// 20. SCAN RESULT CARD
// ============================================================

@Composable
private fun ScanResultCard(

    value: String,

    format: String,

    modifier: Modifier = Modifier,

    onScanAgain: () -> Unit

) {

    Surface(

        modifier = modifier.fillMaxWidth(),

        color = AtelierTealBackground,

        shape = RoundedCornerShape(23.dp),

        border = BorderStroke(
            1.dp,
            Color(0xFFB9E6DE)
        )

    ) {

        Column(

            modifier = Modifier.padding(18.dp)

        ) {

            Row(

                verticalAlignment = Alignment.CenterVertically

            ) {

                Icon(

                    imageVector = Icons.Outlined.CheckCircle,

                    contentDescription = null,

                    tint = AtelierTealDark,

                    modifier = Modifier.size(25.dp)
                )

                Spacer(modifier = Modifier.width(9.dp))

                Text(

                    text = "Kode berhasil dipindai",

                    fontWeight = FontWeight.Bold,

                    fontSize = 17.sp,

                    color = AtelierTealDark
                )
            }


            Spacer(modifier = Modifier.height(15.dp))


            Text(

                text = "HASIL PEMINDAIAN",

                color = AtelierTealDark,

                fontSize = 10.sp,

                fontWeight = FontWeight.Bold,

                letterSpacing = 1.2.sp
            )


            Spacer(modifier = Modifier.height(6.dp))


            Text(

                text = value,

                color = AtelierBrown,

                fontSize = 19.sp,

                fontWeight = FontWeight.Bold,

                lineHeight = 26.sp
            )


            Spacer(modifier = Modifier.height(5.dp))


            Text(

                text = "Format: $format",

                color = AtelierTealDark,

                fontSize = 12.sp
            )


            Spacer(modifier = Modifier.height(16.dp))


            OutlinedButton(

                onClick = onScanAgain,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),

                shape = RoundedCornerShape(13.dp),

                border = BorderStroke(
                    1.dp,
                    AtelierTealDark
                )

            ) {

                Icon(
                    imageVector = Icons.Outlined.QrCodeScanner,
                    contentDescription = null,
                    tint = AtelierTealDark,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(

                    text = "Scan box lainnya",

                    color = AtelierTealDark,

                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// ============================================================
// 21. INFORMATION CARD
// ============================================================

@Composable
private fun ScanInformationCard(

    modifier: Modifier = Modifier

) {

    Surface(

        modifier = modifier.fillMaxWidth(),

        color = AtelierTealBackground,

        shape = RoundedCornerShape(23.dp),

        border = BorderStroke(
            1.dp,
            Color(0xFFB9E6DE)
        )

    ) {

        Row(

            modifier = Modifier.padding(
                horizontal = 15.dp,
                vertical = 19.dp
            ),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF4FBF9)),

                contentAlignment = Alignment.Center

            ) {

                Icon(

                    imageVector = Icons.Outlined.Inventory2,

                    contentDescription = null,

                    tint = AtelierTealDark,

                    modifier = Modifier.size(27.dp)
                )
            }


            Spacer(modifier = Modifier.width(13.dp))


            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = "Hasil scan menampilkan isi box",

                    fontSize = 15.sp,

                    fontWeight = FontWeight.Bold,

                    color = AtelierTealDark,

                    lineHeight = 20.sp
                )


                Spacer(modifier = Modifier.height(5.dp))


                Text(

                    text = "Saat ini ID QR dan barcode dibaca langsung di perangkat. Data isi box dapat dihubungkan setelah backend tersedia.",

                    fontSize = 12.sp,

                    color = Color(0xFF299486),

                    lineHeight = 18.sp
                )
            }
        }
    }
}


// ============================================================
// 22. EMPLOYEE BOTTOM NAVIGATION
// ============================================================

private data class EmployeeNavItem(

    val label: String,

    val icon: ImageVector

)


@Composable
private fun EmployeeBottomNavigation(

    selectedItem: String,

    onNavigate: (String) -> Unit

) {

    val items = listOf(

        EmployeeNavItem(
            "Tugas",
            Icons.Outlined.TaskAlt
        ),

        EmployeeNavItem(
            "Pesanan",
            Icons.Outlined.Assignment
        ),

        EmployeeNavItem(
            "Scan",
            Icons.Outlined.QrCodeScanner
        ),

        EmployeeNavItem(
            "Notifikasi",
            Icons.Outlined.Notifications
        ),

        EmployeeNavItem(
            "Lainnya",
            Icons.Outlined.Menu
        )
    )


    Surface(

        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),

        color = Color(0xFFFCF9F4),

        shadowElevation = 0.dp

    ) {

        Column {

            HorizontalDivider(

                color = AtelierBorder,

                thickness = 1.dp
            )


            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(73.dp)
                    .padding(
                        horizontal = 7.dp,
                        vertical = 5.dp
                    ),

                verticalAlignment = Alignment.CenterVertically

            ) {

                items.forEach { item ->

                    val selected =
                        item.label == selectedItem


                    Column(

                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(18.dp))
                            .background(

                                if (selected)
                                    AtelierGoldLight
                                else
                                    Color.Transparent
                            )
                            .clickable {

                                onNavigate(item.label)
                            }
                            .padding(vertical = 5.dp),

                        horizontalAlignment = Alignment.CenterHorizontally,

                        verticalArrangement = Arrangement.Center

                    ) {

                        Icon(

                            imageVector = item.icon,

                            contentDescription = item.label,

                            tint = if (selected)
                                AtelierBrown
                            else
                                AtelierBrownLight,

                            modifier = Modifier.size(23.dp)
                        )


                        Spacer(modifier = Modifier.height(4.dp))


                        Text(

                            text = item.label,

                            color = if (selected)
                                AtelierBrown
                            else
                                AtelierBrownLight,

                            fontSize = 10.sp,

                            fontWeight = if (selected)
                                FontWeight.Bold
                            else
                                FontWeight.Normal,

                            maxLines = 1,

                            textAlign = TextAlign.Center,

                            overflow = TextOverflow.Clip
                        )
                    }
                }
            }
        }
    }
}
