with open('app/src/main/java/com/modxlab/admin/ui/screens/AddUserScreen.kt', 'r') as f:
    content = f.read()

if "import androidx.compose.foundation.text.KeyboardOptions" not in content:
    content = content.replace("import androidx.compose.material3.Text", "import androidx.compose.foundation.text.KeyboardOptions\nimport androidx.compose.ui.text.input.KeyboardType\nimport androidx.compose.material3.Text")
    with open('app/src/main/java/com/modxlab/admin/ui/screens/AddUserScreen.kt', 'w') as f:
        f.write(content)
