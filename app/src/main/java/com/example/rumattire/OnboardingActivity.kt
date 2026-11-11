package com.example.rumattire

import android.app.Activity
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.caverock.androidsvg.SVG

private val OnboardingBackground = Color(0xFFF5EFE6)
private val OnboardingInk = Color(0xFF3F2A22)

private const val DesignWidth = 393
private const val DesignHeight = 852
private const val DesignContentTop = 44
private const val DesignContentBottom = 812
private const val RasterScale = 3

class OnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val systemBars = SystemBarStyle.light(
            scrim = android.graphics.Color.TRANSPARENT,
            darkScrim = 0xFFF5EFE6.toInt()
        )

        enableEdgeToEdge(
            statusBarStyle = systemBars,
            navigationBarStyle = systemBars
        )

        setContent {
            RumAttireOnboarding(
                onFinished = {
                    // Sambungkan ke halaman tujuan aplikasi di sini,
                    // atau tangani RESULT_OK dari Activity pemanggil.
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            )
        }
    }
}

@Composable
fun RumAttireOnboarding(onFinished: () -> Unit) {
    var page by rememberSaveable { mutableIntStateOf(1) }

    BackHandler(enabled = page > 1) {
        page--
    }

    when (page) {
        1 -> OnboardingFrameOne(
            onNext = { page = 2 },
            onSkip = onFinished
        )

        2 -> OnboardingFrameTwo(
            onNext = { page = 3 },
            onSkip = onFinished
        )

        else -> OnboardingFrameThree(
            onStart = onFinished
        )
    }
}

private enum class OnboardingPage(
    val number: Int,
    val assetName: String,
    val section: String,
    val title: String,
    val description: String,
    val illustrationDescription: String,
    val primaryLabel: String
) {
    Inventory(
        number = 1,
        assetName = "onboarding_1.svg",
        section = "01 · Inventaris & Box",
        title = "Inventaris dan box terkelola rapi",
        description = "Catat setiap busana dan aksesori, simpan dalam box, " +
                "lalu pindai QR untuk melihat isinya seketika.",
        illustrationDescription = "Kebaya Ivory, KB-014, ukuran M. " +
                "Lima item dalam box. Label QR BX-028, Lemari A, Rak 2.",
        primaryLabel = "Lanjut"
    ),

    Orders(
        number = 2,
        assetName = "onboarding_2.svg",
        section = "02 · Pesanan & Persiapan",
        title = "Pesanan siap sebelum hari acara",
        description = "Kirim daftar barang ke karyawan, pantau validasi " +
                "Ada atau Tidak ada hingga pesanan siap dikonfirmasi.",
        illustrationDescription = "Daftar periksa pesanan dengan tiga tanda " +
                "centang, satu tanda silang, label Ada dan Tidak ada, serta " +
                "Kirim ke owner.",
        primaryLabel = "Lanjut"
    ),

    Payments(
        number = 3,
        assetName = "onboarding_3.svg",
        section = "03 · Pembayaran & Notifikasi",
        title = "Pembayaran dan pengingat otomatis",
        description = "Pilih DP atau Lunas, lalu biarkan notifikasi " +
                "mengingatkan tagihan dan jadwal pengembalian.",
        illustrationDescription = "Total sewa Rp6.000.000. Pilihan DP dan Lunas. " +
                "Sisa tagihan Rp3.000.000. Pengingat tagihan jatuh tempo " +
                "7 November pukul 09.00.",
        primaryLabel = "Mulai"
    )
}

@Composable
fun OnboardingFrameOne(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    showDesignSystemBars: Boolean = false
) {
    OnboardingFrame(
        page = OnboardingPage.Inventory,
        onPrimary = onNext,
        onSkip = onSkip,
        showDesignSystemBars = showDesignSystemBars
    )
}

@Composable
fun OnboardingFrameTwo(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    showDesignSystemBars: Boolean = false
) {
    OnboardingFrame(
        page = OnboardingPage.Orders,
        onPrimary = onNext,
        onSkip = onSkip,
        showDesignSystemBars = showDesignSystemBars
    )
}

@Composable
fun OnboardingFrameThree(
    onStart: () -> Unit,
    showDesignSystemBars: Boolean = false
) {
    OnboardingFrame(
        page = OnboardingPage.Payments,
        onPrimary = onStart,
        showDesignSystemBars = showDesignSystemBars
    )
}

@Composable
private fun OnboardingFrame(
    page: OnboardingPage,
    onPrimary: () -> Unit,
    showDesignSystemBars: Boolean,
    onSkip: (() -> Unit)? = null
) {
    val assets = LocalContext.current.assets

    val renderResult = remember(assets, page.assetName) {
        runCatching {
            renderOnboardingSvg(assets, page.assetName)
        }
    }

    val bitmap = renderResult.getOrNull()

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = OnboardingInk,
            onPrimary = OnboardingBackground,
            surface = OnboardingBackground,
            onSurface = OnboardingInk,
            background = OnboardingBackground
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = OnboardingBackground
        ) {
            if (bitmap == null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val error = renderResult.exceptionOrNull()
                    val cause = error?.cause

                    Text(
                        text = buildString {
                            appendLine("Gagal menampilkan ${page.assetName}")
                            appendLine("${error?.javaClass?.simpleName}: ${error?.message}")

                            if (cause != null) {
                                appendLine("Penyebab: ${cause.javaClass.simpleName}")
                                append(cause.message.orEmpty())
                            }
                        },
                        textAlign = TextAlign.Center,
                        color = OnboardingInk
                    )
                }
            } else {
                OnboardingFrameContent(
                    page = page,
                    bitmap = bitmap,
                    onPrimary = onPrimary,
                    onSkip = onSkip,
                    showDesignSystemBars = showDesignSystemBars
                )
            }
        }
    }
}

@Composable
private fun OnboardingFrameContent(
    page: OnboardingPage,
    bitmap: Bitmap,
    onPrimary: () -> Unit,
    onSkip: (() -> Unit)?,
    showDesignSystemBars: Boolean
) {
    val visibleTop = if (showDesignSystemBars) 0 else DesignContentTop
    val visibleBottom = if (showDesignSystemBars) {
        DesignHeight
    } else {
        DesignContentBottom
    }
    val visibleHeight = visibleBottom - visibleTop

    val sourceRect = remember(visibleTop, visibleBottom) {
        Rect(
            0,
            visibleTop * RasterScale,
            DesignWidth * RasterScale,
            visibleBottom * RasterScale
        )
    }

    val bitmapPaint = remember {
        Paint(Paint.FILTER_BITMAP_FLAG)
    }

    val insets = if (showDesignSystemBars) {
        Modifier
    } else {
        Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().then(insets),
        contentAlignment = Alignment.Center
    ) {
        val scale = minOf(
            maxWidth.value / DesignWidth,
            maxHeight.value / visibleHeight
        )

        // Posisi tiap komponen memakai koordinat viewBox SVG.
        fun region(
            x: Int,
            y: Int,
            width: Int,
            height: Int
        ): Modifier = Modifier
            .absoluteOffset(
                x = (x * scale).dp,
                y = ((y - visibleTop) * scale).dp
            )
            .size((width * scale).dp, (height * scale).dp)

        Box(
            modifier = Modifier
                .size(
                    (DesignWidth * scale).dp,
                    (visibleHeight * scale).dp
                )
                .testTag("onboardingFrame${page.number}")
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawContext.canvas.nativeCanvas.drawBitmap(
                    bitmap,
                    sourceRect,
                    RectF(0f, 0f, size.width, size.height),
                    bitmapPaint
                )
            }

            // Label TalkBack untuk komponen yang tergambar pada SVG.
            OnboardingDescription(
                label = "Rum Attire",
                modifier = region(24, 48, 180, 40)
            )

            if (onSkip != null) {
                OnboardingAction(
                    label = "Lewati",
                    tag = "onboardingSkip",
                    modifier = region(309, 46, 68, 48),
                    cornerRadius = (12 * scale).dp,
                    onClick = onSkip
                )
            }

            OnboardingDescription(
                label = page.illustrationDescription,
                modifier = region(24, 104, 345, 360)
            )

            OnboardingDescription(
                label = page.section,
                modifier = region(24, 483, 345, 21)
            )

            OnboardingDescription(
                label = page.title,
                modifier = region(24, 505, 345, 82),
                isHeading = true
            )

            OnboardingDescription(
                label = page.description,
                modifier = region(24, 603, 345, 74)
            )

            OnboardingDescription(
                label = "Halaman ${page.number} dari 3",
                modifier = region(164, 691, 65, 20)
                    .testTag("onboardingProgress")
            )

            OnboardingAction(
                label = page.primaryLabel,
                tag = "onboardingPrimary",
                modifier = region(24, 730, 345, 54),
                cornerRadius = (18 * scale).dp,
                onClick = onPrimary
            )
        }
    }
}

@Composable
private fun OnboardingDescription(
    label: String,
    modifier: Modifier,
    isHeading: Boolean = false
) {
    Box(
        modifier = modifier.semantics {
            contentDescription = label
            if (isHeading) heading()
        }
    )
}

@Composable
private fun OnboardingAction(
    label: String,
    tag: String,
    modifier: Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    // Bentuk dan tulisan tombol berasal dari SVG.
    // Surface menyediakan interaksi tekan dan ripple Material 3.
    Surface(
        onClick = onClick,
        modifier = modifier
            .testTag(tag)
            .semantics {
                role = Role.Button
                contentDescription = label
            },
        color = Color.Transparent,
        contentColor = OnboardingInk,
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Box(Modifier.fillMaxSize())
    }
}

private fun renderOnboardingSvg(
    assets: AssetManager,
    assetName: String
): Bitmap {
    val svg = SVG.getFromAsset(assets, assetName)

    val width = DesignWidth * RasterScale
    val height = DesignHeight * RasterScale

    // Sesuaikan ukuran dokumen SVG dengan ukuran bitmap.
    svg.setDocumentWidth(width.toFloat())
    svg.setDocumentHeight(height.toFloat())

    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = AndroidCanvas(bitmap)

    svg.renderToCanvas(canvas)

    return bitmap
}

@Preview(name = "01 · Inventaris & Box", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewOnboardingFrameOne() {
    OnboardingFrameOne(
        onNext = {},
        onSkip = {},
        showDesignSystemBars = true
    )
}

@Preview(name = "02 · Pesanan & Persiapan", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewOnboardingFrameTwo() {
    OnboardingFrameTwo(
        onNext = {},
        onSkip = {},
        showDesignSystemBars = true
    )
}

@Preview(name = "03 · Pembayaran & Notifikasi", widthDp = 393, heightDp = 852)
@Composable
private fun PreviewOnboardingFrameThree() {
    OnboardingFrameThree(
        onStart = {},
        showDesignSystemBars = true
    )
}

@Preview(
    name = "Alur onboarding lengkap",
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun PreviewOnboardingFlow() {
    RumAttireOnboarding(
        onFinished = {}
    )
}