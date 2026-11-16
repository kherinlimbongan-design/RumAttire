
package com.example.rumattire

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalContentColor
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.text.style.TextOverflow

private val EmployeeMoreBackground = Color(0xFFF7F2E9)
private val EmployeeMoreBrown = Color(0xFF3B2A22)
private val EmployeeMoreMuted = Color(0xFF8B7B70)
private val EmployeeMoreGold = Color(0xFFC9A96E)
private val EmployeeMoreBorder = Color(0xFFE6DACA)
private val EmployeeMoreSurface = Color(0xFFFBF8F2)
private val EmployeeMoreRed = Color(0xFFD64B5E)

@Composable
fun EmployeeMoreScreen(
    name: String,
    email: String,
    onOpenProfile: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAbout: () -> Unit,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmployeeMoreBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {

        // CONTENT
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 4.dp)
        ) {

            Spacer(Modifier.height(25.dp))

            Text(
                text = "Lainnya",
                fontSize = 26.sp,
                fontFamily = FontFamily.Serif,
                color = EmployeeMoreBrown
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = "$name · Employee",
                fontSize = 12.sp,
                color = EmployeeMoreMuted
            )

            Spacer(Modifier.height(20.dp))

            // PROFILE CARD
            Surface(
                onClick = onOpenProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 112.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color.Transparent
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF5C4336),
                                    Color(0xFF3A2820)
                                )
                            ),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(18.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(
                                    EmployeeMoreGold,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = name
                                    .trim()
                                    .firstOrNull()
                                    ?.uppercase() ?: "E",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmployeeMoreBrown
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = name,
                                fontSize = 17.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = email,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = Color(0xFFE5D9CC),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(Modifier.height(5.dp))

                            Text(
                                text = "EMPLOYEE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmployeeMoreGold
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = "Buka profil",
                            tint = EmployeeMoreGold
                        )
                    }
                }
            }

            Spacer(Modifier.height(25.dp))

            Text(
                text = "Pengaturan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = EmployeeMoreBrown
            )

            Spacer(Modifier.height(10.dp))

            // SETTINGS CARD
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    EmployeeMoreBorder
                )
            ) {

                Column {

                    EmployeeMoreRow(
                        title = "Profil",
                        subtitle = "Nama, email, dan kata sandi",
                        icon = {
                            Icon(
                                Icons.Outlined.PersonOutline,
                                contentDescription = null
                            )
                        },
                        tint = Color(0xFF1E9E8F),
                        background = Color(0xFFDFF3EF),
                        onClick = onOpenProfile
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(start = 68.dp),
                        color = EmployeeMoreBorder
                    )

                    EmployeeMoreRow(
                        title = "Notifikasi",
                        subtitle = "Riwayat dan pengingat",
                        icon = {
                            Icon(
                                Icons.Outlined.NotificationsNone,
                                contentDescription = null
                            )
                        },
                        tint = Color(0xFFE8704A),
                        background = Color(0xFFFDE8DE),
                        onClick = onOpenNotifications
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(start = 68.dp),
                        color = EmployeeMoreBorder
                    )

                    EmployeeMoreRow(
                        title = "Tentang Aplikasi",
                        subtitle = "Rum Attire Wedding · Versi 1.0.0",
                        icon = {
                            Icon(
                                Icons.Outlined.Info,
                                contentDescription = null
                            )
                        },
                        tint = Color(0xFF7B61D9),
                        background = Color(0xFFECE7FB),
                        onClick = onOpenAbout
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // LOGOUT CARD
            Surface(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFF2C9CF)
                )
            ) {

                Row(
                    modifier = Modifier.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFBE4E7)),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            Icons.Outlined.ExitToApp,
                            contentDescription = null,
                            tint = EmployeeMoreRed
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "Keluar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmployeeMoreRed
                        )

                        Text(
                            text = "Akhiri sesi di perangkat ini",
                            fontSize = 11.sp,
                            color = EmployeeMoreMuted
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = EmployeeMoreMuted
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "R / A",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 19.sp,
                fontFamily = FontFamily.Serif,
                color = EmployeeMoreGold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = "Rum Attire · Employee Workspace · v1.0.0",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                fontSize = 10.sp,
                color = EmployeeMoreMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // BOTTOM NAVIGATION
        NavigationBar(
            containerColor = EmployeeMoreSurface,
            tonalElevation = 0.dp
        ) {

            val labels = listOf(
                "Tugas",
                "Pesanan",
                "Scan",
                "Notifikasi",
                "Lainnya"
            )

            val icons = listOf(
                Icons.Outlined.CheckBox,
                Icons.Outlined.Assignment,
                Icons.Outlined.QrCodeScanner,
                Icons.Outlined.NotificationsNone,
                Icons.Outlined.Menu
            )

            labels.forEachIndexed { index, label ->

                NavigationBarItem(
                    selected = label == "Lainnya",

                    onClick = {
                        onNavigate(label)
                    },

                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = label
                        )
                    },

                    label = {
                        Text(
                            text = label,
                            fontSize = 10.sp
                        )
                    },

                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmployeeMoreBrown,
                        selectedTextColor = EmployeeMoreBrown,
                        indicatorColor = Color(0xFFEADFCF),
                        unselectedIconColor = EmployeeMoreMuted,
                        unselectedTextColor = EmployeeMoreMuted
                    )
                )
            }
        }
    }
}


// ============================================================
// EMPLOYEE MORE MENU ROW
// ============================================================

@Composable
private fun EmployeeMoreRow(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    tint: Color,
    background: Color,
    onClick: () -> Unit
) {

    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 76.dp)
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(background),
                contentAlignment = Alignment.Center
            ) {

                CompositionLocalProvider(
                    LocalContentColor provides tint
                ) {
                    icon()
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmployeeMoreBrown
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = EmployeeMoreMuted
                )
            }

            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = EmployeeMoreMuted
            )
        }
    }
}
