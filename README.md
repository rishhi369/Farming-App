# 👨‍🌾 Agri India - One stop application for Indian Farmers

✒️ This is an Android Application designed for our Indian farmers. 

✒️ The technology has become a part and parcel of everyone's life and it is also helping us to make our daily tasks easy.
We identified that in the Agriculture sector, the farmers are still lacking the best technology use cases. 

✒️ There are thousands of applications in the markets for farmers but they only provide the limited features.

✒️ The main reason for creating this application was to combine every possible feature into the single app. This will remove the burden of managing multiple account and apps for farmers.
The application is also designed in such a way that it will be easy to use.
<br /><br />


## ✨ Features:
- Government Yojna Awareness
- E-commerce Platform
- Daily APMC Price Updates
- Community Network (Social Media)
- Reading articles based on categories
- Weather Forecasting
<br /><br />

## 📱 Technologies / Tools used for building this app includes:
| Android Studio | Firebase | Kotlin | External APIs | Android OS |
| --- | --- | --- | --- | --- |


## 🤩 Designs:


## 🚀 Getting Started

To get a local copy up and running, follow these simple steps:

### Prerequisites
* Android Studio (Latest Version)
* JDK 17
* Firebase Account

### Installation & Setup

1. **Clone the repository**
   ```sh
   git clone https://github.com/rishhi369/Farming-App.git
   ```

2. **Firebase Setup**
   * Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/).
   * Add an Android App with package name `com.project.farmingapp`.
   * Download `google-services.json` and place it in the `app/` directory.
   * Enable **Email/Google Authentication**, **Firestore**, and **Storage**.

3. **API Keys & Secrets**
   * Create a file named `secrets.properties` in the root directory.
   * Add your Razorpay Key:
     ```properties
     RAZORPAY_KEY=your_razorpay_key_here
     ```

4. **Build the Project**
   * Open the project in Android Studio.
   * Let Gradle sync and build.
   * Run the app on an emulator or physical device.

---

## 📦 Deployment & Installation

### How to Install (For Users)
Since this app is distributed as an APK, follow these steps to install it on your Android device:
1.  **Download**: Navigate to the [Releases](https://github.com/rishhi369/Farming-App/releases) section and download the latest `.apk` file.
2.  **Enable Unknown Sources**:
    -   Go to **Settings** > **Security** (or **Apps**).
    -   Enable **Install Unknown Apps** for your browser or file manager.
3.  **Install**: Open the downloaded file and tap **Install**.

### Automated Build (For Developers)
This project uses **GitHub Actions** to automate builds. Every time you push a tag (e.g., `v1.0.0`), a production-ready APK is automatically generated and attached to a new Release.

> [!IMPORTANT]
> To use the automated build, you must add the following **GitHub Secrets** to your repository:
> - `GOOGLE_SERVICES_JSON_BASE64`: Your `google-services.json` content encoded in Base64.
> - `SECRETS_PROPERTIES`: The contents of your `secrets.properties` file.

---

## 🛡️ Production Security Checklist
Before going live, ensure your **Firebase Security Rules** are configured properly:

- [ ] **Firestore**: Replace "allow-all" rules with:
  ```javascript
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
  ```
- [ ] **Storage**: Ensure files are only writable by authenticated users.
- [ ] **Razorpay**: Switch from **Test Mode** to **Live Mode** in your Razorpay dashboard and update your `secrets.properties`.

## 👨‍💻 Maintainer:
**Dipak Bachhav**

## 😀 If you liked the app, Please give it a ⭐ and fork the repository. 🤚🏻
