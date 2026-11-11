package com.example.rumattire

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

private val LogoutInk = Color(0xFF3B2A22)
private val LogoutBrown = Color(0xFF4A3328)
private val LogoutMuted = Color(0xFF8B7B70)
private val LogoutGold = Color(0xFFC9A96E)
private val LogoutRed = Color(0xFFD64B5E)
private val LogoutScrim = Color(0xFF2A1B15).copy(alpha = 0.62f)

@Composable
fun LogoutDialog(
    accountName: String,
    accountEmail: String,
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        val dialogWindow =
            (LocalView.current.parent as? DialogWindowProvider)?.window

        SideEffect {
            // Gunakan warna scrim dari SVG tanpa tambahan dim dari sistem.
            dialogWindow?.setDimAmount(0f)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LogoutScrim)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentAlignment = Alignment.Center
        ) {
            LogoutDialogCard(
                accountName = accountName,
                accountEmail = accountEmail,
                onCancel = onDismiss,
                onConfirmLogout = onConfirmLogout,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun LogoutDialogCard(
    accountName: String,
    accountEmail: String,
    onCancel: () -> Unit,
    onConfirmLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .widthIn(max = 329.dp)
            .fillMaxWidth()
            .testTag("logoutDialog")
            .semantics { paneTitle = "Konfirmasi keluar dari akun" },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, LogoutGold.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFFDF1F3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFFBE4E7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    LogoutLineIcon()
                }
            }

            Spacer(Modifier.height(4.dp))

            LogoutText(
                text = "Keluar dari akun?",
                size = 23f,
                lineHeight = 28f,
                color = LogoutInk,
                align = TextAlign.Center,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(Modifier.height(8.dp))

            LogoutText(
                text = "Sesi Anda di perangkat ini akan diakhiri,\n" +
                        "lalu Anda kembali ke halaman Login.",
                size = 12.5f,
                lineHeight = 18f,
                color = LogoutMuted,
                align = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            LogoutAccountChip(
                name = accountName,
                email = accountEmail
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("logoutCancel"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, LogoutBrown),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = LogoutBrown
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    LogoutText(
                        text = "Batal",
                        size = 13.5f,
                        lineHeight = 18f,
                        weight = FontWeight.Bold,
                        color = LogoutBrown
                    )
                }

                Button(
                    onClick = onConfirmLogout,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("logoutConfirm"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogoutRed,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    LogoutText(
                        text = "Keluar",
                        size = 13.5f,
                        lineHeight = 18f,
                        weight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoutAccountChip(name: String, email: String) {
    val initial = name.trim().firstOrNull()?.uppercase() ?: "?"

    Row(
        modifier = Modifier
            .widthIn(max = 232.dp)
            .fillMaxWidth()
            .heightIn(min = 34.dp)
            .background(Color(0xFFF3EBDD), CircleShape)
            .padding(start = 7.dp, end = 12.dp, top = 5.dp, bottom = 5.dp)
            .testTag("logoutAccountChip"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(LogoutGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            LogoutText(
                text = initial,
                size = 12.5f,
                lineHeight = 16f,
                color = LogoutInk
            )
        }

        Spacer(Modifier.width(9.dp))

        LogoutText(
            text = email,
            size = 12f,
            lineHeight = 16f,
            color = LogoutBrown,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LogoutLineIcon() {
    val path = remember {
        PathParser().parsePathString(
            "M12.8 3.2H6.4 " +
                    "C4.633 3.2 3.2 4.633 3.2 6.4V25.6 " +
                    "C3.2 27.367 4.633 28.8 6.4 28.8H12.8 " +
                    "M19.2 22.4L25.6 16L19.2 9.6 " +
                    "M25.6 16H11.2"
        ).toPath()
    }

    Canvas(modifier = Modifier.size(32.dp)) {
        scale(
            scaleX = size.width / 32f,
            scaleY = size.height / 32f,
            pivot = Offset.Zero
        ) {
            drawPath(
                path = path,
                color = LogoutRed,
                style = Stroke(
                    width = 2.08f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

@Composable
private fun LogoutText(
    text: String,
    size: Float,
    lineHeight: Float,
    color: Color,
    modifier: Modifier = Modifier,
    weight: FontWeight = FontWeight.Normal,
    align: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = weight,
        textAlign = align,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            letterSpacing = 0.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        )
    )
}

@Preview(
    name = "Dialog Keluar · Employee",
    widthDp = 393,
    heightDp = 852,
    showBackground = true
)
@Composable
private fun PreviewLogoutDialog() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F2E9)),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.fillMaxSize().background(LogoutScrim))

        LogoutDialogCard(
            accountName = "Sari",
            accountEmail = "sari@rumattire.com",
            onCancel = {},
            onConfirmLogout = {},
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}