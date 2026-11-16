
package com.example.rumattire

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================
// COLOR PALETTE - ROOM ATTIRE WEDDING
// ============================================================

private object DetailBoxColors {

    val background = Color(0xFFF7F2E9)
    val surface = Color(0xFFFBF8F2)
    val white = Color(0xFFFFFFFF)

    val brown = Color(0xFF3B2A22)
    val brownButton = Color(0xFF4A3328)

    val secondary = Color(0xFF8B7B70)
    val border = Color(0xFFE6DACA)

    val gold = Color(0xFFC9A96E)
    val cream = Color(0xFFF0E4D2)
    val heroStart = Color(0xFF5C4336)
    val heroEnd = Color(0xFF3A2820)

    val purple = Color(0xFF7B61D9)
    val purpleBg = Color(0xFFECE7FB)

    val teal = Color(0xFF1E9E8F)
    val tealBg = Color(0xFFDFF3EF)

    val yellow = Color(0xFFB7791F)
    val yellowBg = Color(0xFFFBF0D2)

    val orange = Color(0xFFE8704A)
    val orangeBg = Color(0xFFFDE8DE)

    val pink = Color(0xFFD64B5E)
    val pinkBg = Color(0xFFFBE4E7)
}

// ============================================================
// DATA MODEL
// ============================================================

data class DetailBoxItemUi(
    val name: String,
    val sku: String,
    val size: String,
    val iconBackground: Color,
    val iconColor: Color,
    val orderInfo: String? = null
)

data class DetailBoxEmployeeUi(
    val id: String,
    val category: String,
    val location: String,
    val items: List<DetailBoxItemUi>
)

// ============================================================
// SAMPLE DATA - SESUAI SVG
// ============================================================

internal val sampleDetailBoxEmployee = DetailBoxEmployeeUi(

    id = "BX-012",

    category = "Aksesori kepala",

    location = "Lemari B · Rak 2",

    items = listOf(

        DetailBoxItemUi(
            name = "Veil Tulle Panjang",
            sku = "VL-003",
            size = "All size",
            iconBackground = DetailBoxColors.tealBg,
            iconColor = DetailBoxColors.teal,
            orderInfo = "Ada di pesanan #WA-26041"
        ),

        DetailBoxItemUi(
            name = "Mahkota Kristal",
            sku = "AC-008",
            size = "All size",
            iconBackground = DetailBoxColors.yellowBg,
            iconColor = DetailBoxColors.yellow
        ),

        DetailBoxItemUi(
            name = "Tiara Mutiara",
            sku = "AC-011",
            size = "All size",
            iconBackground = DetailBoxColors.purpleBg,
            iconColor = DetailBoxColors.purple
        ),

        DetailBoxItemUi(
            name = "Jepit Rambut Bunga",
            sku = "AC-015",
            size = "All size",
            iconBackground = DetailBoxColors.orangeBg,
            iconColor = DetailBoxColors.orange
        ),

        DetailBoxItemUi(
            name = "Bando Satin Ivory",
            sku = "AC-019",
            size = "All size",
            iconBackground = DetailBoxColors.pinkBg,
            iconColor = DetailBoxColors.pink
        ),

        DetailBoxItemUi(
            name = "Veil Katedral Lace",
            sku = "VL-007",
            size = "All size",
            iconBackground = DetailBoxColors.tealBg,
            iconColor = DetailBoxColors.teal
        ),

        DetailBoxItemUi(
            name = "Hiasan Rambut Emas",
            sku = "AC-023",
            size = "All size",
            iconBackground = DetailBoxColors.yellowBg,
            iconColor = DetailBoxColors.yellow
        ),

        DetailBoxItemUi(
            name = "Pita Satin Champagne",
            sku = "AC-027",
            size = "All size",
            iconBackground = DetailBoxColors.purpleBg,
            iconColor = DetailBoxColors.purple
        )
    )
)

// ============================================================
// CUSTOM ICON TYPE
// ============================================================

private enum class DetailIconType {
    BACK,
    EYE,
    QR,
    LOCATION,
    SHIRT,
    SCAN
}

// ============================================================
// CUSTOM VECTOR ICON
// Semua ikon dibuat menggunakan Canvas.
// Tidak membutuhkan drawable tambahan.
// ============================================================

@Composable
private fun DetailLineIcon(
    type: DetailIconType,
    modifier: Modifier = Modifier,
    tint: Color = DetailBoxColors.brown,
    strokeWidth: Float = 1.8f
) {

    Canvas(modifier = modifier) {

        val unit = minOf(size.width, size.height) / 24f

        val left = (size.width - unit * 24f) / 2f
        val top = (size.height - unit * 24f) / 2f

        val sw = unit * strokeWidth

        fun point(x: Float, y: Float): Offset {
            return Offset(
                left + x * unit,
                top + y * unit
            )
        }

        fun line(
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float
        ) {
            drawLine(
                color = tint,
                start = point(x1, y1),
                end = point(x2, y2),
                strokeWidth = sw,
                cap = StrokeCap.Round
            )
        }

        when (type) {

            // ---------------- BACK ARROW ----------------

            DetailIconType.BACK -> {

                line(19f, 12f, 5f, 12f)
                line(5f, 12f, 12f, 5f)
                line(5f, 12f, 12f, 19f)
            }

            // ---------------- EYE ICON ----------------

            DetailIconType.EYE -> {

                val path = Path().apply {

                    moveTo(point(2.5f, 12f).x, point(2.5f, 12f).y)

                    cubicTo(
                        point(5f, 8f).x,
                        point(5f, 8f).y,
                        point(8.2f, 6.5f).x,
                        point(8.2f, 6.5f).y,
                        point(12f, 6.5f).x,
                        point(12f, 6.5f).y
                    )

                    cubicTo(
                        point(15.8f, 6.5f).x,
                        point(15.8f, 6.5f).y,
                        point(19f, 8f).x,
                        point(19f, 8f).y,
                        point(21.5f, 12f).x,
                        point(21.5f, 12f).y
                    )

                    cubicTo(
                        point(19f, 16f).x,
                        point(19f, 16f).y,
                        point(15.8f, 17.5f).x,
                        point(15.8f, 17.5f).y,
                        point(12f, 17.5f).x,
                        point(12f, 17.5f).y
                    )

                    cubicTo(
                        point(8.2f, 17.5f).x,
                        point(8.2f, 17.5f).y,
                        point(5f, 16f).x,
                        point(5f, 16f).y,
                        point(2.5f, 12f).x,
                        point(2.5f, 12f).y
                    )

                    close()
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = Stroke(
                        width = sw,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                drawCircle(
                    color = tint,
                    radius = unit * 2.2f,
                    center = point(12f, 12f),
                    style = Stroke(width = sw)
                )
            }

            // ---------------- QR CODE ICON ----------------

            DetailIconType.QR -> {

                fun finder(x: Float, y: Float) {

                    drawRoundRect(
                        color = tint,
                        topLeft = point(x, y),
                        size = Size(
                            unit * 7.5f,
                            unit * 7.5f
                        ),
                        cornerRadius = CornerRadius(unit * 1.2f),
                        style = Stroke(width = sw)
                    )

                    drawRoundRect(
                        color = tint,
                        topLeft = point(x + 2.3f, y + 2.3f),
                        size = Size(
                            unit * 2.9f,
                            unit * 2.9f
                        ),
                        cornerRadius = CornerRadius(unit * 0.5f)
                    )
                }

                finder(1.5f, 1.5f)
                finder(15f, 1.5f)
                finder(1.5f, 15f)

                drawRect(
                    color = tint,
                    topLeft = point(15f, 15f),
                    size = Size(unit * 2.2f, unit * 2.2f)
                )

                drawRect(
                    color = tint,
                    topLeft = point(19f, 15f),
                    size = Size(unit * 2.2f, unit * 2.2f)
                )

                drawRect(
                    color = tint,
                    topLeft = point(15f, 19f),
                    size = Size(unit * 2.2f, unit * 2.2f)
                )

                drawRect(
                    color = tint,
                    topLeft = point(19f, 20f),
                    size = Size(unit * 2.2f, unit * 2.2f)
                )
            }

            // ---------------- LOCATION ICON ----------------

            DetailIconType.LOCATION -> {

                val path = Path().apply {

                    moveTo(
                        point(12f, 22f).x,
                        point(12f, 22f).y
                    )

                    cubicTo(
                        point(9f, 18f).x,
                        point(9f, 18f).y,
                        point(5f, 14f).x,
                        point(5f, 14f).y,
                        point(5f, 10f).x,
                        point(5f, 10f).y
                    )

                    cubicTo(
                        point(5f, 6f).x,
                        point(5f, 6f).y,
                        point(8f, 3f).x,
                        point(8f, 3f).y,
                        point(12f, 3f).x,
                        point(12f, 3f).y
                    )

                    cubicTo(
                        point(16f, 3f).x,
                        point(16f, 3f).y,
                        point(19f, 6f).x,
                        point(19f, 6f).y,
                        point(19f, 10f).x,
                        point(19f, 10f).y
                    )

                    cubicTo(
                        point(19f, 14f).x,
                        point(19f, 14f).y,
                        point(15f, 18f).x,
                        point(15f, 18f).y,
                        point(12f, 22f).x,
                        point(12f, 22f).y
                    )
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = Stroke(
                        width = sw,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                drawCircle(
                    color = tint,
                    radius = unit * 2f,
                    center = point(12f, 10f),
                    style = Stroke(width = sw)
                )
            }

            // ---------------- SHIRT ICON ----------------

            DetailIconType.SHIRT -> {

                val path = Path().apply {

                    moveTo(point(9f, 4f).x, point(9f, 4f).y)

                    lineTo(point(11f, 3f).x, point(11f, 3f).y)
                    lineTo(point(13f, 3f).x, point(13f, 3f).y)
                    lineTo(point(15f, 4f).x, point(15f, 4f).y)

                    lineTo(point(20.5f, 7f).x, point(20.5f, 7f).y)
                    lineTo(point(18.2f, 11f).x, point(18.2f, 11f).y)

                    lineTo(point(15.8f, 9.5f).x, point(15.8f, 9.5f).y)
                    lineTo(point(15.8f, 21f).x, point(15.8f, 21f).y)

                    lineTo(point(8.2f, 21f).x, point(8.2f, 21f).y)
                    lineTo(point(8.2f, 9.5f).x, point(8.2f, 9.5f).y)

                    lineTo(point(5.8f, 11f).x, point(5.8f, 11f).y)
                    lineTo(point(3.5f, 7f).x, point(3.5f, 7f).y)

                    close()
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = Stroke(
                        width = sw,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                line(9.5f, 4.2f, 12f, 6.3f)
                line(12f, 6.3f, 14.5f, 4.2f)
            }

            // ---------------- SCAN ICON ----------------

            DetailIconType.SCAN -> {

                line(3f, 9f, 3f, 4f)
                line(3f, 4f, 9f, 4f)

                line(15f, 4f, 21f, 4f)
                line(21f, 4f, 21f, 9f)

                line(3f, 15f, 3f, 20f)
                line(3f, 20f, 9f, 20f)

                line(15f, 20f, 21f, 20f)
                line(21f, 20f, 21f, 15f)
            }
        }
    }
}

// ============================================================
// MAIN SCREEN
// ============================================================

@Composable
fun DetailBoxEmployeeScreen(
    box: DetailBoxEmployeeUi = sampleDetailBoxEmployee,
    onBack: () -> Unit = {},
    onScanAnotherBox: () -> Unit = {}
) {

    Scaffold(

        modifier = Modifier.fillMaxSize(),

        containerColor = DetailBoxColors.background,

        bottomBar = {

            DetailBoxBottomBar(
                onScanAnotherBox = onScanAnotherBox
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DetailBoxColors.background)
                .padding(innerPadding)
        ) {

            // HEADER

            DetailBoxHeader(
                onBack = onBack
            )

            // SCROLLABLE CONTENT

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // BOX SUMMARY

                DetailBoxSummaryCard(
                    box = box
                )

                // SECTION HEADER

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 10.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Isi box",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DetailBoxColors.brown,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${box.items.size} barang",
                        fontSize = 12.sp,
                        color = DetailBoxColors.secondary,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                // ITEM LIST

                if (box.items.isEmpty()) {

                    Text(
                        text = "Belum ada barang di dalam box.",
                        fontSize = 13.sp,
                        color = DetailBoxColors.secondary,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )

                } else {

                    box.items.forEachIndexed { index, item ->

                        DetailBoxItemCard(
                            item = item
                        )

                        if (index != box.items.lastIndex) {

                            Spacer(
                                modifier = Modifier.height(7.dp)
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }
}

// ============================================================
// HEADER
// ============================================================

@Composable
private fun DetailBoxHeader(
    onBack: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // BACK BUTTON

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack),

            contentAlignment = Alignment.Center
        ) {

            DetailLineIcon(
                type = DetailIconType.BACK,
                modifier = Modifier.size(25.dp),
                tint = DetailBoxColors.brown,
                strokeWidth = 2f
            )
        }

        // TITLE

        Text(
            text = "Detail box",

            modifier = Modifier.padding(start = 7.dp),

            fontSize = 17.sp,

            fontWeight = FontWeight.Bold,

            color = DetailBoxColors.brown,

            fontFamily = FontFamily.SansSerif,

            maxLines = 1
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // READ ONLY BADGE

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(DetailBoxColors.purpleBg)
                .padding(
                    horizontal = 10.dp,
                    vertical = 7.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            DetailLineIcon(
                type = DetailIconType.EYE,
                modifier = Modifier.size(15.dp),
                tint = DetailBoxColors.purple
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = "Hanya lihat",

                fontSize = 11.sp,

                fontWeight = FontWeight.SemiBold,

                color = DetailBoxColors.purple,

                fontFamily = FontFamily.SansSerif,

                maxLines = 1
            )
        }
    }
}

// ============================================================
// BOX SUMMARY CARD
// ============================================================

@Composable
private fun DetailBoxSummaryCard(
    box: DetailBoxEmployeeUi
) {

    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 132.dp)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        DetailBoxColors.heroStart,
                        DetailBoxColors.heroEnd
                    )
                )
            )
            .border(
                width = 1.dp,
                color = DetailBoxColors.gold.copy(alpha = 0.35f),
                shape = shape
            )
    ) {

        // DECORATIVE CIRCLES

        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            val center = Offset(
                x = size.width * 0.94f,
                y = size.height * 0.05f
            )

            drawCircle(
                color = DetailBoxColors.gold.copy(alpha = 0.12f),
                radius = size.width * 0.24f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            drawCircle(
                color = DetailBoxColors.gold.copy(alpha = 0.12f),
                radius = size.width * 0.36f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),

            verticalAlignment = Alignment.Top
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // LABEL

                Text(
                    text = "HASIL SCAN",

                    fontSize = 11.sp,

                    fontWeight = FontWeight.Bold,

                    color = DetailBoxColors.gold,

                    fontFamily = FontFamily.SansSerif,

                    lineHeight = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                // BOX ID

                Text(
                    text = box.id,

                    fontSize = 30.sp,

                    fontWeight = FontWeight.Normal,

                    color = DetailBoxColors.white,

                    fontFamily = FontFamily.SansSerif,

                    lineHeight = 36.sp,

                    maxLines = 1
                )

                // CATEGORY

                Text(
                    text = box.category,

                    fontSize = 13.sp,

                    color = DetailBoxColors.cream,

                    fontFamily = FontFamily.SansSerif,

                    lineHeight = 18.sp,

                    maxLines = 1,

                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // SUMMARY CHIPS

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    DetailBoxCountPill(
                        count = box.items.size
                    )

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    DetailBoxLocationPill(
                        location = box.location,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            // QR CODE CONTAINER

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF8EE)),

                contentAlignment = Alignment.Center
            ) {

                DetailLineIcon(
                    type = DetailIconType.QR,
                    modifier = Modifier.size(42.dp),
                    tint = DetailBoxColors.brownButton,
                    strokeWidth = 1.6f
                )
            }
        }
    }
}

// ============================================================
// BOX COUNT PILL
// ============================================================

@Composable
private fun DetailBoxCountPill(
    count: Int
) {

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.13f))
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(7.dp)
                .background(
                    Color(0xFF3CC4B3),
                    CircleShape
                )
        )

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        Text(
            text = "$count barang",

            fontSize = 11.sp,

            color = DetailBoxColors.cream,

            fontFamily = FontFamily.SansSerif,

            maxLines = 1
        )
    }
}

// ============================================================
// BOX LOCATION PILL
// ============================================================

@Composable
private fun DetailBoxLocationPill(
    location: String,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.13f))
            .padding(
                horizontal = 9.dp,
                vertical = 6.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        DetailLineIcon(
            type = DetailIconType.LOCATION,
            modifier = Modifier.size(14.dp),
            tint = DetailBoxColors.gold
        )

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        Text(
            text = location,

            fontSize = 11.sp,

            color = DetailBoxColors.cream,

            fontFamily = FontFamily.SansSerif,

            maxLines = 1,

            overflow = TextOverflow.Ellipsis
        )
    }
}

// ============================================================
// BOX ITEM CARD
// ============================================================

@Composable
private fun DetailBoxItemCard(
    item: DetailBoxItemUi
) {

    val hasOrder = item.orderInfo != null

    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = if (hasOrder) 72.dp else 60.dp
            )
            .clip(shape)
            .background(DetailBoxColors.white)
            .border(
                width = if (hasOrder) 1.5.dp else 1.dp,
                color = if (hasOrder) {
                    DetailBoxColors.brown
                } else {
                    DetailBoxColors.border
                },
                shape = shape
            )
            .padding(
                horizontal = 12.dp,
                vertical = 9.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // ITEM ICON BACKGROUND

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.iconBackground),

            contentAlignment = Alignment.Center
        ) {

            DetailLineIcon(
                type = DetailIconType.SHIRT,
                modifier = Modifier.size(23.dp),
                tint = item.iconColor,
                strokeWidth = 1.8f
            )
        }

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        // ITEM INFORMATION

        Column(
            modifier = Modifier.weight(1f),

            verticalArrangement = Arrangement.Center
        ) {

            // ITEM NAME

            Text(
                text = item.name,

                fontSize = 14.sp,

                fontWeight = FontWeight.Bold,

                color = DetailBoxColors.brown,

                fontFamily = FontFamily.SansSerif,

                lineHeight = 19.sp,

                maxLines = 1,

                overflow = TextOverflow.Ellipsis
            )

            // SKU AND SIZE

            Text(
                text = "${item.sku} · ${item.size}",

                modifier = Modifier.padding(top = 2.dp),

                fontSize = 12.sp,

                fontWeight = FontWeight.Normal,

                color = DetailBoxColors.secondary,

                fontFamily = FontFamily.SansSerif,

                lineHeight = 16.sp,

                maxLines = 1,

                overflow = TextOverflow.Ellipsis
            )

            // ORDER INFORMATION

            item.orderInfo?.let { info ->

                Text(
                    text = info,

                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(CircleShape)
                        .background(DetailBoxColors.tealBg)
                        .padding(
                            horizontal = 9.dp,
                            vertical = 3.dp
                        ),

                    fontSize = 10.sp,

                    fontWeight = FontWeight.Bold,

                    color = DetailBoxColors.teal,

                    fontFamily = FontFamily.SansSerif,

                    lineHeight = 13.sp,

                    maxLines = 1,

                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ============================================================
// BOTTOM ACTION BAR
// ============================================================

@Composable
private fun DetailBoxBottomBar(
    onScanAnotherBox: () -> Unit
) {

    Surface(
        color = DetailBoxColors.surface,
        tonalElevation = 0.dp
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {

            // TOP DIVIDER

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DetailBoxColors.border)
            )

            // SCAN ANOTHER BOX BUTTON

            Button(
                onClick = onScanAnotherBox,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
                    .height(48.dp),

                shape = RoundedCornerShape(15.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = DetailBoxColors.brownButton,
                    contentColor = Color.White
                ),

                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),

                contentPadding = PaddingValues(
                    horizontal = 16.dp
                )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    DetailLineIcon(
                        type = DetailIconType.SCAN,
                        modifier = Modifier.size(21.dp),
                        tint = Color.White,
                        strokeWidth = 2f
                    )

                    Spacer(
                        modifier = Modifier.width(11.dp)
                    )

                    Text(
                        text = "Scan box lain",

                        fontSize = 14.sp,

                        fontWeight = FontWeight.Bold,

                        color = Color.White,

                        fontFamily = FontFamily.SansSerif,

                        maxLines = 1
                    )
                }
            }
        }
    }
}
