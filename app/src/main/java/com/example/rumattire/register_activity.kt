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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
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
import com.example.rumattire.R

// ---------- Palet warna (sama dengan layar login) ----------
private val Cream = Color(0xFFFCF9F3)
private val Gold = Color(0xFFC4A46A)
private val GoldLight = Color(0xFFD8BD86)
private val Brown = Color(0xFF3E2B23)
private val BrownMuted = Color(0xFF716055)
private val HintColor = Color(0xFF8B7B70)
private val Overlay = Color(0xCC2E1F17)
private val FieldBorder = Color(0xFFD9CFC4)
private val SubtitleColor = Color(0xFFF0E5D7)

private val AtelierColors = lightColorScheme(
    primary = Brown,
    onPrimary = Cream,
    secondary = Gold,
    background = Cream,
    onBackground = Brown,
    surface = Color.White,
    onSurface = Brown,
    outline = HintColor
)

@Composable
fun RegisterScreen(
    onRegisterClick: (
        namaLengkap: String,
        email: String,
        noHp: String,
        namaAtelier: String,
        password: String
    ) -> Unit,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    MaterialTheme(colorScheme = AtelierColors) {
        var namaLengkap by rememberSaveable { mutableStateOf("") }
        var email by rememberSaveable { mutableStateOf("") }
        var noHp by rememberSaveable { mutableStateOf("") }
        var namaAtelier by rememberSaveable { mutableStateOf("") }
        var password by androidx.compose.runtime.remember { mutableStateOf("") }
        var konfirmasi by androidx.compose.runtime.remember { mutableStateOf("") }
        var showPassword by rememberSaveable { mutableStateOf(false) }
        var showKonfirmasi by rememberSaveable { mutableStateOf(false) }
        var setujuKetentuan by rememberSaveable { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val focusManager = LocalFocusManager.current

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF7F2E9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ---------- HEADER: FOTO + LAPISAN COKELAT ----------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(292.dp)
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 28.dp, top = 16.dp, end = 28.dp)
                    ) {
                        // Tombol kembali
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_inv_back),
                                contentDescription = "Kembali",
                                tint = Cream,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Identitas brand
                        Row(
                            modifier = Modifier.padding(top = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "R / A",
                                fontSize = 30.sp,
                                fontFamily = FontFamily.Serif,
                                color = Gold
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .height(30.dp)
                                    .width(1.dp)
                                    .background(Cream.copy(alpha = 0.6f))
                            )
                            Text(
                                text = "Rum Attire",
                                fontSize = 26.sp,
                                fontFamily = FontFamily.Serif,
                                color = Cream
                            )
                        }

                        Text(
                            text = "RUANG PEMILIK",
                            modifier = Modifier.padding(top = 22.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.10.em,
                            color = GoldLight
                        )

                        Text(
                            text = "Buat akun baru",
                            modifier = Modifier.padding(top = 8.dp),
                            fontSize = 34.sp,
                            fontFamily = FontFamily.Serif,
                            color = Cream
                        )

                        Text(
                            text = "Daftarkan atelier Anda dan mulai mengelola.",
                            modifier = Modifier.padding(top = 4.dp),
                            fontSize = 14.sp,
                            color = SubtitleColor
                        )
                    }
                }

                // ---------- KARTU FORM (menumpuk di atas foto) ----------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-40).dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 3.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Daftar akun Owner",
                                fontSize = 27.sp,
                                fontFamily = FontFamily.Serif,
                                color = Brown
                            )

                            // Nama lengkap
                            Spacer(Modifier.height(20.dp))
                            FormLabel("Nama lengkap")
                            OutlinedTextField(
                                value = namaLengkap,
                                onValueChange = { namaLengkap = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Masukkan nama lengkap", color = HintColor) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors()
                            )

                            // Email
                            Spacer(Modifier.height(18.dp))
                            FormLabel("Email")
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("nama@email.com", color = HintColor) },
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

                            // Nomor HP
                            Spacer(Modifier.height(18.dp))
                            FormLabel("Nomor HP")
                            OutlinedTextField(
                                value = noHp,
                                onValueChange = { noHp = it },
                                modifier = Modifier.fillMaxWidth(),
                                prefix = {
                                    Text(
                                        text = "+62",
                                        fontWeight = FontWeight.Bold,
                                        color = Brown
                                    )
                                },
                                placeholder = { Text("812 3456 7890", color = HintColor) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors()
                            )

                            // Nama atelier
                            Spacer(Modifier.height(18.dp))
                            FormLabel("Nama atelier")
                            OutlinedTextField(
                                value = namaAtelier,
                                onValueChange = { namaAtelier = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Masukkan nama atelier", color = HintColor) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors()
                            )

                            // Kata sandi
                            Spacer(Modifier.height(18.dp))
                            FormLabel("Kata sandi")
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Buat kata sandi", color = HintColor) },
                                singleLine = true,
                                visualTransformation = if (showPassword) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_eye),
                                            contentDescription = if (showPassword) {
                                                "Sembunyikan kata sandi"
                                            } else {
                                                "Tampilkan kata sandi"
                                            },
                                            tint = BrownMuted,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors()
                            )
                            Text(
                                text = "Minimal 8 karakter, kombinasi huruf dan angka.",
                                modifier = Modifier.padding(top = 6.dp),
                                fontSize = 12.sp,
                                color = BrownMuted
                            )

                            // Konfirmasi kata sandi
                            Spacer(Modifier.height(18.dp))
                            FormLabel("Konfirmasi kata sandi")
                            OutlinedTextField(
                                value = konfirmasi,
                                onValueChange = { konfirmasi = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Ulangi kata sandi", color = HintColor) },
                                singleLine = true,
                                visualTransformation = if (showKonfirmasi) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showKonfirmasi = !showKonfirmasi }) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_eye),
                                            contentDescription = if (showKonfirmasi) {
                                                "Sembunyikan konfirmasi kata sandi"
                                            } else {
                                                "Tampilkan konfirmasi kata sandi"
                                            },
                                            tint = BrownMuted,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
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

                            // Persetujuan
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Checkbox(
                                    checked = setujuKetentuan,
                                    onCheckedChange = { setujuKetentuan = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Brown)
                                )
                                Text(
                                    text = "Saya menyetujui Ketentuan Penggunaan dan Kebijakan Privasi Rum Attire.",
                                    modifier = Modifier
                                        .padding(top = 12.dp)
                                        .weight(1f),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = BrownMuted
                                )
                            }

                            // Pesan error
                            errorMessage?.let { message ->
                                Text(
                                    text = message,
                                    modifier = Modifier.padding(top = 8.dp),
                                    fontSize = 12.sp,
                                    color = Color(0xFFB3261E)
                                )
                            }

                            // Tombol daftar
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    errorMessage = when {
                                        namaLengkap.isBlank() || email.isBlank() ||
                                                noHp.isBlank() || namaAtelier.isBlank() ||
                                                password.isBlank() || konfirmasi.isBlank() ->
                                            "Semua kolom wajib diisi."
                                        password.length < 8 ->
                                            "Kata sandi minimal 8 karakter."
                                        password != konfirmasi ->
                                            "Konfirmasi kata sandi tidak cocok."
                                        !setujuKetentuan ->
                                            "Setujui ketentuan penggunaan terlebih dahulu."
                                        else -> {
                                            onRegisterClick(
                                                namaLengkap.trim(),
                                                email.trim(),
                                                noHp.trim(),
                                                namaAtelier.trim(),
                                                password
                                            )
                                            null
                                        }
                                    }
                                },
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
                                    text = "Daftar sebagai Owner",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Link ke login
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sudah punya akun?",
                            fontSize = 13.sp,
                            color = BrownMuted
                        )
                        TextButton(onClick = onLoginClick) {
                            Text(
                                text = "Masuk",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brown
                            )
                        }
                    }

                    // Footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.width(28.dp),
                            color = Gold
                        )
                        Text(
                            text = "AKSES OWNER",
                            modifier = Modifier.padding(horizontal = 20.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.12.em,
                            color = BrownMuted
                        )
                        HorizontalDivider(
                            modifier = Modifier.width(28.dp),
                            color = Gold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 6.dp),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Brown
    )
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

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun RegisterScreenPreview() {
    RegisterScreen(onRegisterClick = { _, _, _, _, _ -> })
}