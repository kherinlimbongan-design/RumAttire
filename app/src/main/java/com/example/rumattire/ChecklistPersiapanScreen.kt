package com.example.rumattire

import android.app.Activity
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.tooling.preview.Preview

// ============================================================
// COLOR PALETTE
// ============================================================

private object ChecklistColors {

    val background = Color(0xFFF7F2E9)
    val surface = Color(0xFFFFFFFF)
    val footer = Color(0xFFFBF8F2)

    val brown = Color(0xFF3B2A22)
    val brownLight = Color(0xFF5C4336)
    val brownDark = Color(0xFF3A2820)

    val secondaryText = Color(0xFF998779)
    val border = Color(0xFFE6DACA)
    val divider = Color(0xFFE6DACA)

    val teal = Color(0xFF1E9E8F)
    val tealLight = Color(0xFFDFF3EF)
    val tealProgress = Color(0xFF3CC4B3)

    val red = Color(0xFFD64B5E)
    val redLight = Color(0xFFFBE4E7)
    val noteBackground = Color(0xFFFDEFF1)

    val purple = Color(0xFF8064E8)
    val purpleLight = Color(0xFFECE7FB)

    val numberBackground = Color(0xFFF3EBDD)
    val gold = Color(0xFFD5B66C)
}

// ============================================================
// DATA MODEL
// ============================================================

enum class ProductAnswer {
    AVAILABLE,
    UNAVAILABLE,
    UNANSWERED
}

data class ChecklistProduct(
    val id: String,
    val name: String,
    val sku: String,
    val size: String,
    val boxId: String,
    val answer: ProductAnswer,
    val note: String? = null
)

// ============================================================
// MAIN SCREEN
// ============================================================

@Composable
fun ChecklistPersiapanScreen(
    onBackClick: () -> Unit = {},
    onSendToOwner: (List<ChecklistProduct>) -> Unit = {}
) {

    var productList by remember {
        mutableStateOf(
            listOf(
                ChecklistProduct(
                    id = "1",
                    name = "Gaun Pengantin Lace Ivory",
                    sku = "GN-014",
                    size = "Ukuran M",
                    boxId = "BX-004",
                    answer = ProductAnswer.AVAILABLE
                ),

                ChecklistProduct(
                    id = "2",
                    name = "Sepatu Pump Champagne",
                    sku = "SH-021",
                    size = "Ukuran 38",
                    boxId = "BX-009",
                    answer = ProductAnswer.UNAVAILABLE,
                    note = "Tidak ada di BX-009, mungkin masih di laundry."
                ),

                ChecklistProduct(
                    id = "3",
                    name = "Veil Tulle Panjang",
                    sku = "VL-003",
                    size = "Ukuran All size",
                    boxId = "BX-012",
                    answer = ProductAnswer.UNANSWERED
                ),

                ChecklistProduct(
                    id = "4",
                    name = "Mahkota Kristal",
                    sku = "AC-008",
                    size = "Ukuran All size",
                    boxId = "BX-012",
                    answer = ProductAnswer.UNANSWERED
                )
            )
        )
    }

    val totalProducts = productList.size

    val answeredProducts = productList.count {
        it.answer != ProductAnswer.UNANSWERED
    }

    val remainingProducts = totalProducts - answeredProducts

    val progress = if (totalProducts > 0) {
        answeredProducts.toFloat() / totalProducts.toFloat()
    } else {
        0f
    }

    val allAnswered = remainingProducts == 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChecklistColors.background)
    ) {

        // HEADER, SUMMARY AND SECTION TITLE

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp)
        ) {

            ChecklistHeader(
                orderId = "#WA-26041",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            OrderSummaryCard(
                customerName = "Nadia & Rizky",
                orderType = "Sewa",
                pickupDate = "Ambil 4 Nov, 16.00",
                answered = answeredProducts,
                total = totalProducts,
                progress = progress
            )

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Daftar barang",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChecklistColors.brown
                )

                Text(
                    text = "$totalProducts barang",
                    fontSize = 12.sp,
                    color = ChecklistColors.secondaryText
                )
            }
        }

        // PRODUCT CHECKLIST

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 12.dp
            ),

            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                items = productList,
                key = { it.id }
            ) { product ->

                ChecklistItemCard(
                    product = product,
                    highlighted = product.id == "3",
                    onAnswer = { selectedAnswer ->

                        productList = productList.map { item ->

                            if (item.id == product.id) {
                                item.copy(
                                    answer = selectedAnswer
                                )
                            } else {
                                item
                            }
                        }
                    }
                )
            }
        }

        // FIXED BOTTOM ACTION

        ChecklistFooter(
            remaining = remainingProducts,
            enabled = allAnswered,
            onSendClick = {
                onSendToOwner(productList)
            }
        )
    }
}

// ============================================================
// HEADER
// ============================================================

@Composable
private fun ChecklistHeader(
    orderId: String,
    onBackClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 0.dp)
            .height(30.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = onBackClick
                ),
            contentAlignment = Alignment.Center
        ) {

            Canvas(
                modifier = Modifier.size(22.dp)
            ) {

                val color = ChecklistColors.brown

                drawLine(
                    color = color,
                    start = Offset(size.width * 0.85f, size.height * 0.5f),
                    end = Offset(size.width * 0.15f, size.height * 0.5f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = color,
                    start = Offset(size.width * 0.15f, size.height * 0.5f),
                    end = Offset(size.width * 0.48f, size.height * 0.17f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = color,
                    start = Offset(size.width * 0.15f, size.height * 0.5f),
                    end = Offset(size.width * 0.48f, size.height * 0.83f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Checklist persiapan",
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ChecklistColors.brown,
            maxLines = 1
        )

        Text(
            text = orderId,
            fontSize = 11.sp,
            color = ChecklistColors.secondaryText
        )
    }
}

// ============================================================
// ORDER SUMMARY CARD
// ============================================================

@Composable
private fun OrderSummaryCard(
    customerName: String,
    orderType: String,
    pickupDate: String,
    answered: Int,
    total: Int,
    progress: Float
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        ChecklistColors.brownLight,
                        ChecklistColors.brownDark
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFF80664E),
                shape = RoundedCornerShape(16.dp)
            )
    ) {

        // Decorative circular lines

        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            val center = Offset(
                x = size.width - 18.dp.toPx(),
                y = 18.dp.toPx()
            )

            drawCircle(
                color = ChecklistColors.gold.copy(alpha = 0.13f),
                radius = 76.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            drawCircle(
                color = ChecklistColors.gold.copy(alpha = 0.13f),
                radius = 101.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = customerName,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )

                Text(
                    text = "$answered/$total dijawab",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChecklistColors.gold
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$orderType · $pickupDate",
                fontSize = 11.sp,
                color = Color(0xFFD8C9BC)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress background

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF6B5A50))
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(ChecklistColors.tealProgress)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Jawab semua barang untuk mengirim ke owner",
                fontSize = 10.sp,
                color = Color(0xFFD8C9BC)
            )
        }
    }
}

// ============================================================
// CHECKLIST PRODUCT CARD
// ============================================================

@Composable
private fun ChecklistItemCard(
    product: ChecklistProduct,
    highlighted: Boolean,
    onAnswer: (ProductAnswer) -> Unit
) {

    val cardShape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(ChecklistColors.surface)
            .border(
                width = if (highlighted) 1.5.dp else 1.dp,
                color = if (highlighted) {
                    ChecklistColors.brown
                } else {
                    ChecklistColors.border
                },
                shape = cardShape
            )
            .padding(
                horizontal = 14.dp,
                vertical = 14.dp
            )
    ) {

        // PRODUCT INFORMATION

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 34.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            ProductStatusIcon(
                product = product
            )

            Spacer(modifier = Modifier.width(11.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChecklistColors.brown
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${product.sku} · ${product.size}",
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = ChecklistColors.secondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // DIVIDER

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ChecklistColors.divider)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // ORIGINAL BOX INFORMATION

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(21.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Box asal",
                fontSize = 10.sp,
                color = ChecklistColors.secondaryText
            )

            Spacer(modifier = Modifier.width(14.dp))

            BoxLocationBadge(
                boxId = product.boxId
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AVAILABLE / UNAVAILABLE BUTTONS

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AnswerButton(
                modifier = Modifier.weight(1f),
                label = "Ada",
                symbol = "✓",
                selected = product.answer == ProductAnswer.AVAILABLE,
                selectedColor = ChecklistColors.teal,
                textColor = ChecklistColors.teal,
                onClick = {
                    onAnswer(ProductAnswer.AVAILABLE)
                }
            )

            AnswerButton(
                modifier = Modifier.weight(1f),
                label = "Tidak ada",
                symbol = "×",
                selected = product.answer == ProductAnswer.UNAVAILABLE,
                selectedColor = ChecklistColors.red,
                textColor = ChecklistColors.red,
                onClick = {
                    onAnswer(ProductAnswer.UNAVAILABLE)
                }
            )
        }

        // OPTIONAL NOTE

        if (
            product.answer == ProductAnswer.UNAVAILABLE &&
            product.note != null
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            ProductNote(
                note = product.note
            )
        }
    }
}

// ============================================================
// PRODUCT STATUS ICON
// ============================================================

@Composable
private fun ProductStatusIcon(
    product: ChecklistProduct
) {

    val backgroundColor = when (product.answer) {

        ProductAnswer.AVAILABLE ->
            ChecklistColors.tealLight

        ProductAnswer.UNAVAILABLE ->
            ChecklistColors.redLight

        ProductAnswer.UNANSWERED ->
            ChecklistColors.numberBackground
    }

    val symbolColor = when (product.answer) {

        ProductAnswer.AVAILABLE ->
            ChecklistColors.teal

        ProductAnswer.UNAVAILABLE ->
            ChecklistColors.red

        ProductAnswer.UNANSWERED ->
            ChecklistColors.brown
    }

    val symbol = when (product.answer) {

        ProductAnswer.AVAILABLE -> "✓"

        ProductAnswer.UNAVAILABLE -> "×"

        ProductAnswer.UNANSWERED -> product.id
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(backgroundColor),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = symbol,
            fontSize = if (
                product.answer == ProductAnswer.UNANSWERED
            ) 16.sp else 25.sp,

            fontWeight = FontWeight.Bold,
            color = symbolColor
        )
    }
}

// ============================================================
// BOX LOCATION BADGE
// ============================================================

@Composable
private fun BoxLocationBadge(
    boxId: String
) {

    Row(
        modifier = Modifier
            .height(21.dp)
            .clip(RoundedCornerShape(50))
            .background(ChecklistColors.purpleLight)
            .padding(horizontal = 8.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Canvas(
            modifier = Modifier.size(13.dp)
        ) {

            val path = Path().apply {

                moveTo(size.width * 0.5f, size.height * 0.05f)
                lineTo(size.width * 0.95f, size.height * 0.30f)
                lineTo(size.width * 0.95f, size.height * 0.72f)
                lineTo(size.width * 0.5f, size.height * 0.97f)
                lineTo(size.width * 0.05f, size.height * 0.72f)
                lineTo(size.width * 0.05f, size.height * 0.30f)
                close()
            }

            drawPath(
                path = path,
                color = ChecklistColors.purple,
                style = Stroke(
                    width = 1.7.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            drawLine(
                color = ChecklistColors.purple,
                start = Offset(size.width * 0.5f, size.height * 0.05f),
                end = Offset(size.width * 0.5f, size.height * 0.97f),
                strokeWidth = 1.3.dp.toPx()
            )

            drawLine(
                color = ChecklistColors.purple,
                start = Offset(size.width * 0.05f, size.height * 0.30f),
                end = Offset(size.width * 0.5f, size.height * 0.53f),
                strokeWidth = 1.3.dp.toPx()
            )

            drawLine(
                color = ChecklistColors.purple,
                start = Offset(size.width * 0.95f, size.height * 0.30f),
                end = Offset(size.width * 0.5f, size.height * 0.53f),
                strokeWidth = 1.3.dp.toPx()
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = boxId,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ChecklistColors.purple
        )
    }
}

// ============================================================
// ANSWER BUTTON
// ============================================================

@Composable
private fun AnswerButton(
    modifier: Modifier = Modifier,
    label: String,
    symbol: String,
    selected: Boolean,
    selectedColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {

    val buttonShape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .height(40.dp)
            .clip(buttonShape)
            .background(
                if (selected) {
                    selectedColor
                } else {
                    Color.White
                }
            )
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) {
                    selectedColor
                } else {
                    ChecklistColors.border
                },
                shape = buttonShape
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            ),

        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = symbol,
            fontSize = 21.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) {
                Color.White
            } else {
                textColor
            }
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) {
                Color.White
            } else {
                textColor
            }
        )
    }
}

// ============================================================
// PRODUCT NOTE
// ============================================================

@Composable
private fun ProductNote(
    note: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(ChecklistColors.noteBackground)
            .padding(
                horizontal = 11.dp,
                vertical = 9.dp
            )
    ) {

        Text(
            text = "CATATAN",
            fontSize = 10.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ChecklistColors.red
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = note,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = Color(0xFF95505A)
        )
    }
}

// ============================================================
// FIXED FOOTER
// ============================================================

@Composable
private fun ChecklistFooter(
    remaining: Int,
    enabled: Boolean,
    onSendClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ChecklistColors.footer)
            .drawBehind {

                drawLine(
                    color = ChecklistColors.border,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
    ) {

        Text(
            text = if (enabled) {
                "Semua barang sudah dijawab"
            } else {
                "Jawab $remaining barang lagi untuk mengirim"
            },

            modifier = Modifier.fillMaxWidth(),

            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            color = ChecklistColors.secondaryText
        )

        Spacer(modifier = Modifier.height(9.dp))

        val buttonColor = if (enabled) {
            ChecklistColors.brown
        } else {
            Color(0xFFD9CDBD)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(buttonColor)
                .clickable(
                    enabled = enabled,
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = onSendClick
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Kirim ke owner",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ============================================================
// SYSTEM BAR CONFIGURATION
// CALL THIS FROM YOUR ACTIVITY ON CREATE
// ============================================================

fun Activity.configureChecklistSystemBars() {

    WindowCompat.setDecorFitsSystemWindows(
        window,
        false
    )

    window.statusBarColor = AndroidColor.TRANSPARENT
    window.navigationBarColor = AndroidColor.TRANSPARENT

    WindowCompat.getInsetsController(
        window,
        window.decorView
    ).apply {

        isAppearanceLightStatusBars = true
        isAppearanceLightNavigationBars = true
    }
}

@Preview(
    showBackground = true,
    heightDp = 800,
    widthDp = 393
)
@Composable
private fun ChecklistPersiapanScreenPreview() {
    MaterialTheme {
        ChecklistPersiapanScreen()
    }
}
