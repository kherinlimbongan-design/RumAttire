package com.example.rumattire

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ProfileBackground = Color(0xFFF7F2E9)
private val ProfileInk = Color(0xFF3B2A22)
private val ProfileMuted = Color(0xFF8B7B70)
private val ProfileGold = Color(0xFFC9A96E)
private val ProfileBorder = Color(0xFFE6DACA)
private val ProfileDivider = Color(0xFFEFE6D8)
private val ProfileFooter = Color(0xFFFBF8F2)
private val ProfileButton = Color(0xFF4A3328)
private val ProfileTeal = Color(0xFF1E9E8F)
private val ProfilePurple = Color(0xFF7B61D9)
private val ProfilePurpleLight = Color(0xFFECE7FB)

data class ProfileUiData(
    val name: String,
    val email: String,
    val role: String,
    val accessLabel: String,
    val passwordUpdatedLabel: String
)

private val sampleEmployeeProfile = ProfileUiData(
    name = "Sari",
    email = "sari@rumattire.com",
    role = "Employee",
    accessLabel = "Akses terbatas",
    passwordUpdatedLabel = "Diubah 12 Okt 2026"
)

class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val statusBar = SystemBarStyle.light(
            scrim = android.graphics.Color.TRANSPARENT,
            darkScrim = 0xFFF7F2E9.toInt()
        )

        val navigationBar = SystemBarStyle.light(
            scrim = android.graphics.Color.TRANSPARENT,
            darkScrim = 0xFFFBF8F2.toInt()
        )

        enableEdgeToEdge(
            statusBarStyle = statusBar,
            navigationBarStyle = navigationBar
        )

        setContent {
            ProfileScreen(
                profile = sampleEmployeeProfile,
                onBack = {
                    onBackPressedDispatcher.onBackPressed()
                },
                onChangePassword = {
                    // Hubungkan ke layar ubah kata sandi aplikasi.
                    Toast.makeText(
                        this@ProfileActivity,
                        "Halaman ubah kata sandi belum dihubungkan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}

@Composable
fun ProfileScreen(
    profile: ProfileUiData,
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    showDesignSystemBars: Boolean = false
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = ProfileButton,
            onPrimary = Color.White,
            background = ProfileBackground,
            onBackground = ProfileInk,
            surface = Color.White,
            onSurface = ProfileInk
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().testTag("profileRoot"),
            color = ProfileBackground
        ) {
            val screenInsets = if (showDesignSystemBars) {
                Modifier
            } else {
                Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                    )
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().then(screenInsets)
            ) {
                if (showDesignSystemBars) {
                    ProfileDesignStatusBar()
                }

                ProfileHeader(onBack)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("profileContent")
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileHero(profile)

                    ProfileCard(
                        title = "Informasi akun",
                        bottomSpace = 8,
                        modifier = Modifier.testTag("accountInfoCard")
                    ) {
                        ProfileInfoRow(
                            label = "Nama",
                            value = profile.name,
                            icon = ProfileIcon.Person,
                            iconTint = ProfileTeal,
                            iconBackground = Color(0xFFDFF3EF),
                            showDivider = true
                        )

                        ProfileInfoRow(
                            label = "Email",
                            value = profile.email,
                            icon = ProfileIcon.Email,
                            iconTint = Color(0xFFE8704A),
                            iconBackground = Color(0xFFFDE8DE),
                            showDivider = true
                        )

                        ProfileInfoRow(
                            label = "Role",
                            value = profile.role,
                            icon = ProfileIcon.Shield,
                            iconTint = ProfilePurple,
                            iconBackground = ProfilePurpleLight,
                            trailingContent = {
                                if (profile.accessLabel.isNotBlank()) {
                                    ProfileBadge(
                                        label = profile.accessLabel,
                                        background = ProfilePurpleLight,
                                        color = ProfilePurple,
                                        minimumWidth = 111,
                                        height = 22
                                    )
                                }
                            }
                        )
                    }

                    ProfileCard(
                        title = "Keamanan",
                        bottomSpace = 6,
                        modifier = Modifier.testTag("securityCard")
                    ) {
                        ProfileInfoRow(
                            label = "Kata sandi",
                            value = null,
                            icon = ProfileIcon.Lock,
                            iconTint = Color(0xFFB7791F),
                            iconBackground = Color(0xFFFBF0D2),
                            trailingContent = {
                                ProfileText(
                                    text = profile.passwordUpdatedLabel,
                                    size = 11,
                                    color = ProfileMuted
                                )
                            }
                        )
                    }
                }

                ProfileActionFooter(
                    showDesignSystemBars = showDesignSystemBars,
                    onChangePassword = onChangePassword
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).testTag("profileHeader"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 7.dp)
                .size(48.dp)
                .testTag("profileBack")
                .semantics { contentDescription = "Kembali" }
        ) {
            ProfileLineIcon(ProfileIcon.Back, ProfileInk)
        }

        ProfileText(
            text = "Profil",
            size = 15,
            weight = FontWeight.Bold,
            modifier = Modifier
                .offset(x = (-5).dp)
                .semantics { heading() }
        )
    }
}

@Composable
private fun ProfileHero(profile: ProfileUiData) {
    val shape = RoundedCornerShape(18.dp)
    val initial = profile.name.trim().take(1).uppercase().ifEmpty { "?" }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 184.dp)
            .testTag("profileHero")
            .border(1.dp, ProfileGold.copy(alpha = 0.35f), shape),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(Modifier.fillMaxWidth()) {
            Canvas(Modifier.matchParentSize()) {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF5C4336), Color(0xFF3A2820)),
                        start = Offset.Zero,
                        end = Offset(148.888.dp.toPx(), 292.112.dp.toPx())
                    )
                )

                val circleCenter = Offset(
                    x = size.width - 20.dp.toPx(),
                    y = 14.dp.toPx()
                )

                for (radius in listOf(64.dp, 92.dp)) {
                    drawCircle(
                        color = ProfileGold.copy(alpha = 0.16f),
                        radius = radius.toPx(),
                        center = circleCenter,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 184.dp)
                    .padding(top = 16.dp, bottom = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .border(1.dp, ProfileGold.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(74.dp)
                            .background(ProfileGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        ProfileText(initial, size = 34, lineHeight = 40)
                    }
                }

                Spacer(Modifier.height(8.dp))

                ProfileText(
                    text = profile.name,
                    size = 22,
                    lineHeight = 26,
                    color = Color(0xFFFFF8EE)
                )

                Spacer(Modifier.height(8.dp))

                ProfileBadge(
                    label = profile.role,
                    background = ProfileTeal,
                    color = Color.White,
                    minimumWidth = 84,
                    height = 24
                )
            }
        }
    }
}

@Composable
private fun ProfileCard(
    title: String,
    bottomSpace: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, ProfileBorder)
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = bottomSpace.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().heightIn(min = 46.dp),
                contentAlignment = Alignment.TopStart
            ) {
                ProfileText(
                    text = title,
                    size = 14,
                    lineHeight = 18,
                    weight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp).semantics { heading() }
                )
            }

            content()
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String?,
    icon: ProfileIcon,
    iconTint: Color,
    iconBackground: Color,
    showDivider: Boolean = false,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackground, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                ProfileLineIcon(icon, iconTint)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                ProfileText(
                    text = label,
                    size = 11,
                    lineHeight = 14,
                    color = ProfileMuted
                )

                Spacer(Modifier.height(2.dp))

                if (value == null) {
                    ProfilePasswordDots()
                } else {
                    ProfileText(
                        text = value,
                        size = 14,
                        lineHeight = 18,
                        weight = FontWeight.Bold
                    )
                }
            }

            if (trailingContent != null) {
                Spacer(Modifier.width(8.dp))
                trailingContent()
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 52.dp),
                thickness = 1.dp,
                color = ProfileDivider
            )
        }
    }
}

@Composable
private fun ProfilePasswordDots() {
    Canvas(
        modifier = Modifier
            .size(width = 56.dp, height = 18.dp)
            .semantics { contentDescription = "Kata sandi disembunyikan" }
    ) {
        repeat(8) { index ->
            drawCircle(
                color = ProfileInk,
                radius = 2.344.dp.toPx(),
                center = Offset(
                    x = (3.478f + index * 6.958f).dp.toPx(),
                    y = 11.5.dp.toPx()
                )
            )
        }
    }
}

@Composable
private fun ProfileBadge(
    label: String,
    background: Color,
    color: Color,
    minimumWidth: Int,
    height: Int
) {
    Box(
        modifier = Modifier
            .widthIn(min = minimumWidth.dp)
            .heightIn(min = height.dp)
            .background(background, CircleShape)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        ProfileText(
            text = label,
            size = 11,
            lineHeight = 14,
            weight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun ProfileActionFooter(
    showDesignSystemBars: Boolean,
    onChangePassword: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProfileFooter)
            .testTag("profileActionFooter")
    ) {
        val bottomInsets = if (showDesignSystemBars) {
            Modifier
        } else {
            Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(bottomInsets)
                .padding(bottom = if (showDesignSystemBars) 32.dp else 12.dp)
        ) {
            HorizontalDivider(thickness = 1.dp, color = ProfileBorder)

            Button(
                onClick = onChangePassword,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 15.dp)
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("changePasswordButton"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProfileButton,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                ProfileLineIcon(ProfileIcon.Lock, Color.White)

                Spacer(Modifier.width(8.dp))

                ProfileText(
                    text = "Ubah kata sandi",
                    size = 14,
                    weight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (showDesignSystemBars) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp)
                    .size(width = 100.dp, height = 4.dp)
                    .background(ProfileInk, CircleShape)
            )
        }
    }
}

private enum class ProfileIcon(val pathData: String) {
    Back("M13 20L5 12L13 4 M5 12H19"),

    Person(
        "M15.6 8.5 A3.6 3.6 0 1 1 8.4 8.5 " +
                "A3.6 3.6 0 1 1 15.6 8.5 Z " +
                "M5 20 C5 16.963 8.134 14.5 12 14.5 " +
                "C15.866 14.5 19 16.963 19 20"
    ),

    Email(
        "M6.5 6.5H17.5 Q20 6.5 20 9V15 Q20 17.5 17.5 17.5 " +
                "H6.5 Q4 17.5 4 15V9 Q4 6.5 6.5 6.5 Z " +
                "M4 8.5L12 14L20 8.5"
    ),

    Shield(
        "M12 3L20 6V12C20 16 17.333 19 12 21 " +
                "C6.667 19 4 16 4 12V6L12 3 Z " +
                "M9 12L11.5 14.5L15.5 10"
    ),

    Lock(
        "M8 11H16 Q18 11 18 13V18 Q18 20 16 20H8 " +
                "Q6 20 6 18V13 Q6 11 8 11 Z " +
                "M8.5 11V8 A3.5 3.5 0 0 1 15.5 8V11"
    )
}

@Composable
private fun ProfileLineIcon(
    icon: ProfileIcon,
    tint: Color
) {
    val path = remember(icon) {
        PathParser().parsePathString(icon.pathData).toPath()
    }

    Canvas(Modifier.size(24.dp)) {
        scale(
            scaleX = size.width / 24f,
            scaleY = size.height / 24f,
            pivot = Offset.Zero
        ) {
            drawPath(
                path = path,
                color = tint,
                style = Stroke(
                    width = if (icon == ProfileIcon.Back) 1.8f else 1.6f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

@Composable
private fun ProfileText(
    text: String,
    size: Int,
    modifier: Modifier = Modifier,
    color: Color = ProfileInk,
    weight: FontWeight = FontWeight.Normal,
    lineHeight: Int = size + 4
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        fontWeight = weight,
        fontFamily = FontFamily.SansSerif,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            letterSpacing = 0.sp,
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        )
    )
}

@Composable
private fun ProfileDesignStatusBar() {
    Box(
        modifier = Modifier.fillMaxWidth().height(44.dp)
    ) {
        ProfileText(
            text = "09.41",
            size = 12,
            weight = FontWeight.SemiBold,
            modifier = Modifier.offset(x = 24.dp, y = 16.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 33.dp)
                .offset(y = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(Modifier.size(width = 18.dp, height = 12.dp)) {
                val heights = listOf(4, 6, 8, 10)

                heights.forEachIndexed { index, height ->
                    drawRoundRect(
                        color = ProfileInk,
                        topLeft = Offset(
                            (index * 5).dp.toPx(),
                            (10 - height).dp.toPx()
                        ),
                        size = Size(3.dp.toPx(), height.dp.toPx()),
                        cornerRadius = CornerRadius(1.dp.toPx())
                    )
                }
            }

            Spacer(Modifier.width(8.dp))
            ProfileText("86%", size = 11)
            Spacer(Modifier.width(4.dp))

            Canvas(Modifier.size(width = 22.dp, height = 11.dp)) {
                drawRoundRect(
                    color = ProfileInk,
                    topLeft = Offset(0.5.dp.toPx(), 0.5.dp.toPx()),
                    size = Size(21.dp.toPx(), 10.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx()),
                    style = Stroke(1.dp.toPx())
                )
                drawRoundRect(
                    color = ProfileInk,
                    topLeft = Offset(2.5.dp.toPx(), 2.5.dp.toPx()),
                    size = Size(15.dp.toPx(), 6.dp.toPx()),
                    cornerRadius = CornerRadius(1.5.dp.toPx())
                )
            }
        }
    }
}

@Preview(
    name = "Profil Employee",
    widthDp = 393,
    heightDp = 852,
    showBackground = true
)
@Composable
private fun PreviewProfileEmployee() {
    ProfileScreen(
        profile = sampleEmployeeProfile,
        onBack = {},
        onChangePassword = {},
        showDesignSystemBars = true
    )
}