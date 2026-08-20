with open('app/src/main/java/com/modxlab/admin/ui/screens/AddUserScreen.kt', 'r') as f:
    content = f.read()

# I will replace the Row for device access to include a text field for custom device access
old_row = """                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {"""
new_row_content = """                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Single Device Option
                    val isSingle = deviceAccess == "1"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSingle) BrandEmerald else Color.White.copy(alpha = 0.05f))
                            .clickable { deviceAccess = "1" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1 Device",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (isSingle) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Unlimited Option
                    val isUnlimited = deviceAccess == "∞"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isUnlimited) BrandEmerald else Color.White.copy(alpha = 0.05f))
                            .clickable { deviceAccess = "∞" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Unlimited",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (isUnlimited) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Custom Input Option
                    val isCustom = !isSingle && !isUnlimited
                    OutlinedTextField(
                        value = if (isCustom) deviceAccess else "",
                        onValueChange = { newValue -> 
                            // Only allow numbers
                            if (newValue.all { it.isDigit() }) {
                                deviceAccess = if (newValue.isEmpty()) "2" else newValue
                            }
                        },
                        placeholder = { 
                            Text("Custom", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp) 
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandEmerald,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }"""

# Find where the old row starts
start_idx = content.find("                Row(\n                    modifier = Modifier.fillMaxWidth(),\n                    horizontalArrangement = Arrangement.spacedBy(10.dp)\n                ) {")

if start_idx != -1:
    end_idx = content.find("                Spacer(modifier = Modifier.height(24.dp))", start_idx)
    content = content[:start_idx] + new_row_content + "\n\n" + content[end_idx:]
    with open('app/src/main/java/com/modxlab/admin/ui/screens/AddUserScreen.kt', 'w') as f:
        f.write(content)
    print("Success")
else:
    print("Failed to find start_idx")
