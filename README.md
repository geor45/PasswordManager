
## 📋 Features 

Secure Encryption: Uses [π.χ. AES-256] to encrypt all sensitive data.

Biometric Authentication: Support for Fingerprint and Face Unlock.

Password Generator: Create strong, customizable passwords on the fly.

Search & Categorize: Easily organize your accounts by categories (Social, Work, Finance).

Offline Storage: Your data stays on your device—no cloud, no leaks.



## ✨ Key Features


🔐 Secure Vault: A centralized, encrypted local database to store all your sensitive credentials safely.

🤖 Smart Password Generator: * Generates high-entropy passwords using a mix of uppercase, lowercase, numbers, and symbols.

Ensures that every new service you sign up for has a unique and uncrackable password.

☝️ Biometric Integration: * Uses Android's BiometricPrompt API for secure and fast access.

Supports Fingerprint and Face Unlock to eliminate the need for a master password every time.

🗄️ Organized Storage (Room DB): * Efficient data persistence using the Room Library.

Fast search and categorization to find your accounts in seconds.

🕶️ User Privacy: * 100% Offline: No internet permission required. Your passwords never leave your device.

Zero Knowledge: As the developer, I have no access to your data.



## 🛠 Future Enhancements (Roadmap)


[ ] Dark Mode support for better user experience at night.

[ ] Auto-fill Service to automatically enter passwords in other apps.

[ ] Export/Import encrypted backups.


## 🛠 Tech Stack


Language: [Kotlin / Java]

Architecture: MVVM (Model-View-ViewModel)

Database: Room Database (with SQLCipher for encryption)

UI: Material 3 / Jetpack Compose

Dependency Injection


## 📸 Screenshots





## 🚀 How to Run


Clone the repository:

Bash
git clone https://github.com/geor45/PasswordManager.git
Open the project in Android Studio.

Build and run on an emulator or a physical device (API 24+ recommended).


## 🛡️ Security Note

This app is a portfolio project. While it uses industry-standard encryption practices, always exercise caution when storing real-world sensitive passwords in a self-made application.
