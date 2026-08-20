with open('app/src/main/java/com/modxlab/admin/MainActivity.kt', 'r') as f:
    content = f.read()

security_check = """        super.onCreate(savedInstanceState)
        
        // Runtime Security Check to prevent repackaging
        if (packageName != "com.kayesahmmed.admin") {
            finishAffinity()
            return
        }
"""
content = content.replace("super.onCreate(savedInstanceState)", security_check)

with open('app/src/main/java/com/modxlab/admin/MainActivity.kt', 'w') as f:
    f.write(content)
