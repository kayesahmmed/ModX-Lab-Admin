with open('app/src/main/java/com/modxlab/admin/ui/screens/AddUserScreen.kt', 'r') as f:
    content = f.read()

# Replace the broken button code
old_code = """                    Button(
                        onClick = {
                            viewModel.addUser(
                                user = user,
                                pass = pass,
                                deviceAccess = deviceAccess,
                                validity = validity
                            )
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandEmerald,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = user.isNotBlank() && pass.isNotBlank() && validity.isNotBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Create Key", fontWeight = FontWeight.Bold)
                    }"""

new_code = """                    Button(
                        onClick = {
                            viewModel.addUser(
                                username = username,
                                pass = password,
                                access = deviceAccess,
                                validityHours = validityHours,
                                onSuccess = onNavigateBack
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandEmerald,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = username.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Create Key", fontWeight = FontWeight.Bold)
                    }"""

content = content.replace(old_code, new_code)

with open('app/src/main/java/com/modxlab/admin/ui/screens/AddUserScreen.kt', 'w') as f:
    f.write(content)
