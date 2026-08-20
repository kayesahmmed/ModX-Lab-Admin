with open('app/src/main/java/com/modxlab/admin/MainActivity.kt', 'r') as f:
    content = f.read()

# Add viewmodel to onCreate
if "AuthViewModel" not in content:
    content = content.replace("import androidx.activity.ComponentActivity", "import androidx.activity.ComponentActivity\nimport androidx.activity.viewModels\nimport com.modxlab.admin.ui.viewmodel.AuthViewModel")
    
    content = content.replace("class MainActivity : ComponentActivity() {", "class MainActivity : ComponentActivity() {\n    private val authViewModel: AuthViewModel by viewModels()")
    
    # call silentLogin
    content = content.replace("super.onCreate(savedInstanceState)", "super.onCreate(savedInstanceState)\n        authViewModel.silentLogin()")

    with open('app/src/main/java/com/modxlab/admin/MainActivity.kt', 'w') as f:
        f.write(content)
