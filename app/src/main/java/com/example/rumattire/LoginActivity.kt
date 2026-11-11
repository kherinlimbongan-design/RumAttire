package com.example.rumattire

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// ---------- Palet warna (dari XML) ----------
private val Cream = Color(0xFFFCF9F3)
private val Gold = Color(0xFFC4A46A)
private val Brown = Color(0xFF3E2B23)
private val BrownMuted = Color(0xFF7E6B60)
private val HintColor = Color(0xFF8D7C72)
private val Overlay = Color(0xCC2E1F17)
private val FieldBorder = Color(0xFFD9CFC4)

private val AtelierColors = lightColorScheme(
    primary = Brown,
    onPrimary = Cream,
    secondary = Gold,
    background = Cream,
    onBackground = Brown,
    surface = Color.White,
    onSurface = Brown,
    outline = HintColor,
)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: (email: String, password: String, rememberMe: Boolean) -> Unit,
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    initialEmail: String = "",
    initialName: String = "",
    submitting: Boolean = false,
) {
    MaterialTheme(colorScheme = AtelierColors) {
        var email by rememberSaveable(initialEmail) { mutableStateOf(initialEmail) }
        var displayName by rememberSaveable { mutableStateOf(initialName) }
        var password by androidx.compose.runtime.remember { mutableStateOf("") }
        var showPassword by rememberSaveable { mutableStateOf(false) }
        var rememberMe by rememberSaveable { mutableStateOf(true) }
        val focusManager = LocalFocusManager.current

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Cream)
        ) {
            // Latar atas + lapisan cokelat
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.bg_login_2),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Overlay)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 32.dp, bottom = 24.dp)
            ) {
                // Nama merek
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "R / A",
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Serif,
                        color = Gold
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .height(28.dp)
                            .width(1.dp)
                            .background(Gold.copy(alpha = 0.33f))
                    )
                    Text(
                        text = "Rum Attire",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Cream
                    )
                }

                Spacer(Modifier.height(28.dp))

                Text(
                    text = "RUANG PEMILIK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.10.em,
                    color = Gold
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Selamat datang\nkembali",
                    fontSize = 36.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Cream
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Kelola atelier, dengan setiap detail terjaga.",
                    fontSize = 14.sp,
                    color = Cream
                )

                Spacer(Modifier.height(38.dp))

                // Panel putih
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Masuk ke akun Anda",
                            fontSize = 25.sp,
                            fontFamily = FontFamily.Serif,
                            color = Brown
                        )

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = displayName,
                            onValueChange = {
                                displayName = it
                                onNameChange(it)
                            },
                            label = { Text("Nama pengguna") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors()
                        )
                        Spacer(Modifier.height(16.dp))

                        // Email
                        Text(
                            text = "Email",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brown
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Masukkan Email", color = HintColor) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors()
                        )

                        Spacer(Modifier.height(16.dp))

                        // Kata sandi
                        Text(
                            text = "Kata sandi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brown
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Masukkan kata sandi", color = HintColor) },
                            singleLine = true,
                            visualTransformation = if (showPassword) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors()
                        )

                        TextButton(
                            onClick = { showPassword = !showPassword },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = if (showPassword) {
                                    "Sembunyikan kata sandi"
                                } else {
                                    "Tampilkan kata sandi"
                                },
                                fontSize = 12.sp,
                                color = BrownMuted
                            )
                        }

                        // Ingat saya + Lupa kata sandi
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Brown)
                                )
                                Text(
                                    text = "Ingat saya",
                                    fontSize = 12.sp,
                                    color = BrownMuted
                                )
                            }
                            TextButton(onClick = onForgotPasswordClick) {
                                Text(
                                    text = "Lupa kata sandi?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Brown
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = { onLoginClick(email, password, rememberMe) },
                            enabled = !submitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Brown,
                                contentColor = Cream
                            )
                        ) {
                            Text(
                                text = "Masuk sebagai Owner",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Butuh bantuan masuk? Hubungi admin atelier.",
                    fontSize = 12.sp,
                    color = BrownMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                TextButton(
                    onClick = onRegisterClick,
                    enabled = !submitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Belum punya akun? Daftar", color = Brown)
                }

                // Divider "AKSES OWNER"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(
                        modifier = Modifier.width(20.dp),
                        color = Gold
                    )
                    Text(
                        text = "AKSES OWNER",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.12.em,
                        color = BrownMuted
                    )
                    HorizontalDivider(
                        modifier = Modifier.width(20.dp),
                        color = Gold
                    )
                }
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Brown,
    unfocusedBorderColor = FieldBorder,
    focusedTextColor = Brown,
    unfocusedTextColor = Brown,
    cursorColor = Brown,
    focusedContainerColor = Cream,
    unfocusedContainerColor = Cream
)

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(onLoginClick = { _, _, _ -> })
    }
}