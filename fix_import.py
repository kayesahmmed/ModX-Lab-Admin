with open('app/src/main/java/com/modxlab/admin/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace("androidx.compose.material.icons.filled.Add", "Icons.Filled.Add")

if "import androidx.compose.material.icons.filled.Add" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Home", "import androidx.compose.material.icons.filled.Home\nimport androidx.compose.material.icons.filled.Add")

with open('app/src/main/java/com/modxlab/admin/MainActivity.kt', 'w') as f:
    f.write(content)
