package com.example.rumattire

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import java.util.Locale
import kotlinx.coroutines.launch

// ============================================================
// COLOR PALETTE - ROOM ATTIRE WEDDING
// Mengikuti warna dari SVG asli.
// ============================================================

private val CariBoxBackground = Color(0xFFF7F2E9)
private val CariBoxSurface = Color(0xFFFFFFFF)
private val CariBoxBottomSurface = Color(0xFFFBF8F2)

private val CariBoxBrown = Color(0xFF3B2A22)
private val CariBoxDarkBrown = Color(0xFF4A3328)
private val CariBoxSecondary = Color(0xFF8B7B70)
private val CariBoxNavText = Color(0xFF6B5A4E)

private val CariBoxSand = Color(0xFFEADFCF)
private val CariBoxBorder = Color(0xFFE6DACA)

// Purple
private val CariBoxPurpleBg = Color(0xFFECE7FB)
private val CariBoxPurple = Color(0xFF7B61D9)

// Orange
private val CariBoxOrangeBg = Color(0xFFFDE8DE)
private val CariBoxOrange = Color(0xFFE8704A)

// Teal
private val CariBoxTealBg = Color(0xFFDFF3EF)
private val CariBoxTeal = Color(0xFF1E9E8F)

// Warning
private val CariBoxWarningBg = Color(0xFFFBF0D2)
private val CariBoxWarningBorder = Color(0xFFEBCB82)
private val CariBoxWarningText = Color(0xFF5A3E00)
private val CariBoxWarningSecondary = Color(0xFF8A6B2A)


// ============================================================
// DATA MODEL
// ============================================================

private data class RecentBox(
    val id: String,
    val description: String,
    val totalItems: String,
    val background: Color,
    val accent: Color
)

private val recentBoxData = listOf(
    RecentBox(
        id = "BX-012",
        description = "Aksesori kepala",
        totalItems = "8 barang",
        background = CariBoxPurpleBg,
        accent = CariBoxPurple
    ),
    RecentBox(
        id = "BX-004",
        description = "Gaun pengantin",
        totalItems = "5 barang",
        background = CariBoxOrangeBg,
        accent = CariBoxOrange
    ),
    RecentBox(
        id = "BX-009",
        description = "Sepatu",
        totalItems = "12 barang",
        background = CariBoxTealBg,
        accent = CariBoxTeal
    )
)


// ============================================================
// CUSTOM ICON SYSTEM
// Semua ikon digambar langsung menggunakan Compose Canvas.
// Tidak membutuhkan Material Icons Extended.
// ============================================================

private enum class CariBoxIcon {
    Bell,
    Scan,
    Pencil,
    Box,
    Search,
    ChevronRight,
    Info,
    Task,
    Clipboard,
    Menu
}

@Composable
private fun CariBoxVectorIcon(
    type: CariBoxIcon,
    modifier: Modifier = Modifier,
    tint: Color = CariBoxBrown,
    strokeWidth: Float = 1.8f
) {
    Canvas(modifier = modifier) {

        val sx = size.width / 24f
        val sy = size.height / 24f

        withTransform({
            scale(
                scaleX = sx,
                scaleY = sy,
                pivot = Offset.Zero
            )
        }) {

            val outline = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )

            when (type) {

                // ---------------- BELL ----------------

                CariBoxIcon.Bell -> {

                    val path = Path().apply {
                        moveTo(5f, 17.5f)
                        cubicTo(6.2f, 16.4f, 6.8f, 15.2f, 6.8f, 13.5f)
                        lineTo(6.8f, 10.8f)

                        cubicTo(6.8f, 7.8f, 9f, 5.5f, 12f, 5.5f)
                        cubicTo(15f, 5.5f, 17.2f, 7.8f, 17.2f, 10.8f)

                        lineTo(17.2f, 13.5f)
                        cubicTo(17.2f, 15.2f, 17.8f, 16.4f, 19f, 17.5f)

                        close()
                    }

                    drawPath(path, tint, style = outline)

                    drawLine(
                        tint,
                        Offset(5f, 17.5f),
                        Offset(19f, 17.5f),
                        strokeWidth
                    )

                    val clapper = Path().apply {
                        moveTo(9.5f, 20f)
                        cubicTo(10.1f, 22f, 13.9f, 22f, 14.5f, 20f)
                    }

                    drawPath(clapper, tint, style = outline)
                }

                // ---------------- SCAN QR ----------------

                CariBoxIcon.Scan -> {

                    val path = Path().apply {

                        moveTo(8f, 4f)
                        lineTo(4f, 4f)
                        lineTo(4f, 8f)

                        moveTo(16f, 4f)
                        lineTo(20f, 4f)
                        lineTo(20f, 8f)

                        moveTo(20f, 16f)
                        lineTo(20f, 20f)
                        lineTo(16f, 20f)

                        moveTo(8f, 20f)
                        lineTo(4f, 20f)
                        lineTo(4f, 16f)

                        moveTo(8f, 12f)
                        lineTo(16f, 12f)
                    }

                    drawPath(path, tint, style = outline)
                }

                // ---------------- PENCIL ----------------

                CariBoxIcon.Pencil -> {

                    val path = Path().apply {
                        moveTo(5f, 19f)
                        lineTo(8f, 18.3f)
                        lineTo(19f, 7.3f)
                        lineTo(16.7f, 5f)
                        lineTo(5.7f, 16f)
                        close()
                    }

                    drawPath(path, tint, style = outline)

                    drawLine(
                        tint,
                        Offset(15.5f, 6.2f),
                        Offset(17.8f, 8.5f),
                        strokeWidth
                    )
                }

                // ---------------- BOX ----------------

                CariBoxIcon.Box -> {

                    val path = Path().apply {

                        moveTo(12f, 3f)
                        lineTo(21f, 7.5f)
                        lineTo(21f, 16.5f)
                        lineTo(12f, 21f)
                        lineTo(3f, 16.5f)
                        lineTo(3f, 7.5f)
                        close()

                        moveTo(3f, 7.5f)
                        lineTo(12f, 12f)
                        lineTo(21f, 7.5f)

                        moveTo(12f, 12f)
                        lineTo(12f, 21f)
                    }

                    drawPath(path, tint, style = outline)
                }

                // ---------------- SEARCH ----------------

                CariBoxIcon.Search -> {

                    drawCircle(
                        color = tint,
                        radius = 6f,
                        center = Offset(10f, 10f),
                        style = outline
                    )

                    drawLine(
                        color = tint,
                        start = Offset(14.5f, 14.5f),
                        end = Offset(20f, 20f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }

                // ---------------- CHEVRON ----------------

                CariBoxIcon.ChevronRight -> {

                    val path = Path().apply {
                        moveTo(9f, 5f)
                        lineTo(16f, 12f)
                        lineTo(9f, 19f)
                    }

                    drawPath(path, tint, style = outline)
                }

                // ---------------- INFORMATION ----------------

                CariBoxIcon.Info -> {

                    drawCircle(
                        color = tint,
                        radius = 9f,
                        center = Offset(12f, 12f),
                        style = outline
                    )

                    drawLine(
                        tint,
                        Offset(12f, 10.5f),
                        Offset(12f, 16f),
                        strokeWidth
                    )

                    drawCircle(
                        color = tint,
                        radius = 0.9f,
                        center = Offset(12f, 7f)
                    )
                }

                // ---------------- TASK ----------------

                CariBoxIcon.Task -> {

                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(4f, 4f),
                        size = androidx.compose.ui.geometry.Size(16f, 16f),
                        cornerRadius = CornerRadius(3f, 3f),
                        style = outline
                    )

                    val tick = Path().apply {
                        moveTo(7f, 12f)
                        lineTo(10.5f, 15.5f)
                        lineTo(17f, 8.5f)
                    }

                    drawPath(tick, tint, style = outline)
                }

                // ---------------- CLIPBOARD ----------------

                CariBoxIcon.Clipboard -> {

                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(6f, 5f),
                        size = androidx.compose.ui.geometry.Size(12f, 16f),
                        cornerRadius = CornerRadius(2f, 2f),
                        style = outline
                    )

                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(9f, 3f),
                        size = androidx.compose.ui.geometry.Size(6f, 4f),
                        cornerRadius = CornerRadius(1.5f, 1.5f),
                        style = outline
                    )

                    drawLine(
                        tint,
                        Offset(9f, 11f),
                        Offset(15f, 11f),
                        strokeWidth
                    )

                    drawLine(
                        tint,
                        Offset(9f, 15f),
                        Offset(15f, 15f),
                        strokeWidth
                    )
                }

                // ---------------- MENU ----------------

                CariBoxIcon.Menu -> {

                    drawLine(
                        tint,
                        Offset(4f, 6f),
                        Offset(20f, 6f),
                        strokeWidth
                    )

                    drawLine(
                        tint,
                        Offset(4f, 12f),
                        Offset(20f, 12f),
                        strokeWidth
                    )

                    drawLine(
                        tint,
                        Offset(4f, 18f),
                        Offset(20f, 18f),
                        strokeWidth
                    )
                }
            }
        }
    }
}


// ============================================================
// MAIN SCREEN
// ============================================================

@Composable
fun CariBoxEmployeeScreen(
    onScanQr: () -> Unit = {},
    onNotification: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onOpenBox: (String) -> Unit = {},
    initialBoxId: String = "BX-012"
) {

    val context = LocalContext.current
    val view = LocalView.current

    // Atur warna system bar Android.
    SideEffect {

        if (!view.isInEditMode) {

            val activity = context.findCariBoxActivity()

            activity?.window?.let { window ->

                window.statusBarColor =
                    CariBoxBackground.toArgb()

                window.navigationBarColor =
                    CariBoxBottomSurface.toArgb()

                WindowCompat.getInsetsController(
                    window,
                    view
                ).apply {

                    isAppearanceLightStatusBars = true
                    isAppearanceLightNavigationBars = true
                }
            }
        }
    }

    var boxId by rememberSaveable(initialBoxId) {
        mutableStateOf(initialBoxId)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    // Fungsi pencarian box.
    val searchBox: () -> Unit = {

        val normalizedId = boxId
            .trim()
            .uppercase(Locale.ROOT)

        if (!Regex("^BX-\\d{3}$").matches(normalizedId)) {

            scope.launch {
                snackbarHostState.showSnackbar(
                    "Format ID box harus seperti BX-000"
                )
            }

        } else {

            boxId = normalizedId
            onOpenBox(normalizedId)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CariBoxBackground,

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },

        bottomBar = {

            CariBoxBottomNavigation(
                onNavigate = onNavigate
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CariBoxBackground)
                .verticalScroll(rememberScrollState())
        ) {

            // =================================================
            // HEADER
            // =================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),

                    verticalAlignment = Alignment.Top
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Scan box",
                            fontSize = 24.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Normal,
                            color = CariBoxBrown,
                            maxLines = 1
                        )

                        Text(
                            text = "Lihat isi box penyimpanan",
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Normal,
                            color = CariBoxSecondary,
                            maxLines = 1
                        )
                    }

                    // Notification icon.
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(28.dp)
                            .clickable(
                                onClick = onNotification
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        CariBoxVectorIcon(
                            type = CariBoxIcon.Bell,
                            modifier = Modifier.size(24.dp),
                            tint = CariBoxBrown
                        )

                        // Notification indicator.
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(8.dp)
                                .background(
                                    CariBoxOrange,
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    CariBoxBackground,
                                    CircleShape
                                )
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))
            }

            // =================================================
            // MAIN CONTENT
            // =================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                // ---------------------------------------------
                // MODE SELECTOR
                // ---------------------------------------------

                CariBoxModeSelector(
                    onScanQr = onScanQr
                )

                Spacer(Modifier.height(22.dp))

                // ---------------------------------------------
                // ID BOX LABEL
                // ---------------------------------------------

                Text(
                    text = "ID box",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CariBoxBrown
                )

                Spacer(Modifier.height(8.dp))

                // ---------------------------------------------
                // ID BOX INPUT
                // ---------------------------------------------

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CariBoxSurface)
                        .border(
                            width = 1.5.dp,
                            color = CariBoxDarkBrown,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 14.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Box icon background.
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(CariBoxPurpleBg),

                        contentAlignment = Alignment.Center
                    ) {

                        CariBoxVectorIcon(
                            type = CariBoxIcon.Box,
                            modifier = Modifier.size(19.dp),
                            tint = CariBoxPurple,
                            strokeWidth = 2.0f
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    BasicTextField(
                        value = boxId,

                        onValueChange = {
                            boxId = it
                                .uppercase(Locale.ROOT)
                                .take(6)
                        },

                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),

                        singleLine = true,

                        textStyle = TextStyle(
                            color = CariBoxBrown,
                            fontSize = 17.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        ),

                        cursorBrush = SolidColor(CariBoxBrown),

                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            keyboardType = KeyboardType.Ascii,
                            imeAction = ImeAction.Search
                        ),

                        keyboardActions = KeyboardActions(
                            onSearch = {
                                searchBox()
                            }
                        ),

                        decorationBox = { innerTextField ->

                            Box(
                                contentAlignment = Alignment.CenterStart
                            ) {

                                if (boxId.isEmpty()) {

                                    Text(
                                        text = "BX-000",
                                        fontSize = 17.sp,
                                        color = CariBoxSecondary
                                    )
                                }

                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // ---------------------------------------------
                // INPUT HELPER TEXT
                // ---------------------------------------------

                Text(
                    text = "Format BX-000, tertera pada label di box",
                    fontSize = 12.5.sp,
                    lineHeight = 20.sp,
                    color = CariBoxSecondary,
                    maxLines = 1
                )

                Spacer(Modifier.height(14.dp))

                // ---------------------------------------------
                // SEARCH BUTTON
                // ---------------------------------------------

                Button(
                    onClick = searchBox,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),

                    shape = RoundedCornerShape(12.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = CariBoxDarkBrown,
                        contentColor = Color.White
                    ),

                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    ),

                    contentPadding = PaddingValues(0.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        CariBoxVectorIcon(
                            type = CariBoxIcon.Search,
                            modifier = Modifier.size(22.dp),
                            tint = Color.White,
                            strokeWidth = 2.0f
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = "Cari box",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(Modifier.height(26.dp))

                // ---------------------------------------------
                // RECENT BOX TITLE
                // ---------------------------------------------

                Text(
                    text = "Box terakhir dilihat",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CariBoxBrown
                )

                Spacer(Modifier.height(10.dp))

                // ---------------------------------------------
                // RECENT BOX LIST
                // ---------------------------------------------

                recentBoxData.forEachIndexed { index, box ->

                    RecentBoxCard(
                        box = box,
                        onClick = {
                            boxId = box.id
                            onOpenBox(box.id)
                        }
                    )

                    if (index != recentBoxData.lastIndex) {
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Spacer(Modifier.height(22.dp))

                // ---------------------------------------------
                // QR INFORMATION CARD
                // ---------------------------------------------

                CariBoxWarningCard()

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}


// ============================================================
// MODE SELECTOR
// ============================================================

@Composable
private fun CariBoxModeSelector(
    onScanQr: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(CircleShape)
            .background(CariBoxSand)
            .padding(4.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // Scan QR - inactive.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(CircleShape)
                .clickable(onClick = onScanQr),

            contentAlignment = Alignment.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                CariBoxVectorIcon(
                    type = CariBoxIcon.Scan,
                    modifier = Modifier.size(20.dp),
                    tint = CariBoxDarkBrown
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Scan QR",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CariBoxDarkBrown,
                    maxLines = 1
                )
            }
        }

        // Ketik ID - active.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(CariBoxDarkBrown),

            contentAlignment = Alignment.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                CariBoxVectorIcon(
                    type = CariBoxIcon.Pencil,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Ketik ID",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}


// ============================================================
// RECENT BOX CARD
// ============================================================

@Composable
private fun RecentBoxCard(
    box: RecentBox,
    onClick: () -> Unit
) {

    Surface(
        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),

        shape = RoundedCornerShape(14.dp),

        color = CariBoxSurface,

        border = BorderStroke(
            1.dp,
            CariBoxBorder
        ),

        shadowElevation = 0.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // ---------------------------------------------
            // BOX ICON
            // ---------------------------------------------

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(box.background),

                contentAlignment = Alignment.Center
            ) {

                CariBoxVectorIcon(
                    type = CariBoxIcon.Box,
                    modifier = Modifier.size(25.dp),
                    tint = box.accent,
                    strokeWidth = 2.0f
                )
            }

            Spacer(Modifier.width(14.dp))

            // ---------------------------------------------
            // BOX INFORMATION
            // ---------------------------------------------

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = box.id,
                    fontSize = 15.5.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CariBoxBrown,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = box.description,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = CariBoxSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ---------------------------------------------
            // ITEM COUNT BADGE
            // ---------------------------------------------

            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 70.dp)
                    .height(24.dp)
                    .clip(CircleShape)
                    .background(box.background)
                    .padding(horizontal = 10.dp),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = box.totalItems,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = box.accent,
                    maxLines = 1
                )
            }

            Spacer(Modifier.width(12.dp))

            // ---------------------------------------------
            // CHEVRON
            // ---------------------------------------------

            CariBoxVectorIcon(
                type = CariBoxIcon.ChevronRight,
                modifier = Modifier.size(18.dp),
                tint = CariBoxSecondary
            )
        }
    }
}


// ============================================================
// WARNING / INFORMATION CARD
// ============================================================

@Composable
private fun CariBoxWarningCard() {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),

        shape = RoundedCornerShape(14.dp),

        color = CariBoxWarningBg,

        border = BorderStroke(
            1.dp,
            CariBoxWarningBorder
        ),

        shadowElevation = 0.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            CariBoxVectorIcon(
                type = CariBoxIcon.Info,
                modifier = Modifier.size(20.dp),
                tint = CariBoxWarningText,
                strokeWidth = 1.8f
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "QR rusak atau tidak terbaca?",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CariBoxWarningText,
                    maxLines = 1
                )

                Text(
                    text = "Ketik ID box secara manual untuk melihat isinya",
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = CariBoxWarningSecondary,
                    maxLines = 1
                )
            }
        }
    }
}


// ============================================================
// BOTTOM NAVIGATION
// ============================================================

private data class CariBoxNavItem(
    val title: String,
    val route: String,
    val icon: CariBoxIcon
)

@Composable
private fun CariBoxBottomNavigation(
    onNavigate: (String) -> Unit
) {

    val items = listOf(

        CariBoxNavItem(
            title = "Tugas",
            route = "Tugas",
            icon = CariBoxIcon.Task
        ),

        CariBoxNavItem(
            title = "Pesanan",
            route = "Pesanan",
            icon = CariBoxIcon.Clipboard
        ),

        CariBoxNavItem(
            title = "Scan",
            route = "Scan",
            icon = CariBoxIcon.Scan
        ),

        CariBoxNavItem(
            title = "Notifikasi",
            route = "Notifikasi",
            icon = CariBoxIcon.Bell
        ),

        CariBoxNavItem(
            title = "Lainnya",
            route = "Lainnya",
            icon = CariBoxIcon.Menu
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CariBoxBottomSurface)
            .drawBehind {

                drawLine(
                    color = CariBoxBorder,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEach { item ->

                val selected = item.route == "Scan"

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            onNavigate(item.route)
                        },

                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .width(64.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (selected) {
                                    CariBoxSand
                                } else {
                                    Color.Transparent
                                }
                            ),

                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        // Navigation icon.
                        Box(
                            modifier = Modifier.size(22.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            CariBoxVectorIcon(
                                type = item.icon,
                                modifier = Modifier.size(22.dp),
                                tint = if (selected) {
                                    CariBoxBrown
                                } else {
                                    CariBoxNavText
                                }
                            )

                            // Notification dot.
                            if (item.route == "Notifikasi") {

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 1.dp, y = (-1).dp)
                                        .size(8.dp)
                                        .background(
                                            CariBoxOrange,
                                            CircleShape
                                        )
                                        .border(
                                            1.dp,
                                            CariBoxBottomSurface,
                                            CircleShape
                                        )
                                )
                            }
                        }

                        // Navigation label.
                        Text(
                            text = item.title,
                            fontSize = 11.5.sp,
                            lineHeight = 14.sp,
                            fontWeight = if (selected) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                            color = if (selected) {
                                CariBoxBrown
                            } else {
                                CariBoxNavText
                            },
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // Android system navigation inset.
        // Gesture navigation handle tetap ditangani Android.
        Spacer(
            modifier = Modifier.windowInsetsBottomHeight(
                WindowInsets.navigationBars
            )
        )
    }
}


// ============================================================
// ACTIVITY CONTEXT HELPER
// ============================================================

private tailrec fun Context.findCariBoxActivity(): Activity? {

    return when (this) {

        is Activity -> this

        is ContextWrapper -> baseContext.findCariBoxActivity()

        else -> null
    }
}


// ============================================================
// PREVIEW
// ============================================================

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun CariBoxEmployeeScreenPreview() {

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = CariBoxDarkBrown,
            onPrimary = Color.White,
            background = CariBoxBackground,
            onBackground = CariBoxBrown,
            surface = CariBoxSurface,
            onSurface = CariBoxBrown
        )
    ) {

        CariBoxEmployeeScreen(
            onScanQr = {},
            onNotification = {},
            onNavigate = {},
            onOpenBox = {}
        )
    }
}
