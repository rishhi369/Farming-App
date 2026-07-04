Mobile Application Development Report
on
Agri India: Smart Farming Support Application

Submitted By: [Your Name]
Class / Roll No.: [Your Class and Roll Number]
Under the Guidance of:
[Guide Name 1]
[Guide Name 2]

Department of Computer Science / Information Technology
[College Name]
[Academic Year]

---

## Abstract

In India, farmers regularly depend on timely information related to weather changes, crop prices, government schemes, agricultural products, and farming knowledge in order to make better decisions. However, this information is usually available across multiple applications, websites, and offline sources, making it difficult for farmers to access everything from one convenient platform. This creates delays in decision-making and increases the digital gap for users in the agriculture sector.

This project presents **Agri India**, an Android mobile application developed using **Kotlin**, **XML**, **Firebase**, and external APIs to support farmers through a single integrated platform. The application provides daily weather forecasting, APMC market price updates, government yojana information, farming articles, ecommerce support for agricultural products, and a farmer community space where users can create and view posts.

Agri India attempts to reduce the need for multiple separate apps by bringing together key farming services in one user-friendly interface. Farmers can log in securely, check forecast information based on their location, browse mandi rates by district, read useful agricultural content, explore products such as fertilizers and irrigation items, and interact with other users through social posts. The app is designed with multiple fragments and Firebase-backed modules to provide modular, scalable, and practical mobile functionality.

Future improvements can include multilingual support, crop recommendation using machine learning, expert consultation, order tracking notifications, and wider integration with live agricultural datasets and government services.

---

## Table of Contents

| Sr. No. | Topics | Page No. |
| --- | --- | --- |
| 1 | Introduction | 4 |
| 2 | Problem Statement | 5 |
| 3 | Background | 6-7 |
| 4 | Figma / Architecture | 8 |
| 5 | Screenshots | 9-11 |
| 6 | User Manual | 12 |
| 7 | Conclusion | 13 |
| 8 | Link to the Project | 14 |
| 9 | Appendices | 15 |

---

## 1. Introduction

### Overview of the Project

Agri India is a farmer-focused Android application developed to provide important agricultural services in one place. It is designed for users who need daily support for weather planning, market rate checking, awareness about farming schemes, agriculture-related reading material, product access, and community interaction. Instead of using separate platforms for each task, the user can access all major features through a single mobile application.

### Brief Summary

The application provides:

- **User Authentication:** Farmers can create an account, log in securely, and manage access to personalized features.
- **Weather Forecasting:** Users can view weather updates and use device location to fetch forecast data for their current city.
- **APMC Price Updates:** Users can select a state and district to view mandi-related crop price information.
- **Government Yojana Information:** Farmers can browse and read details of useful agricultural schemes and benefits.
- **Ecommerce Platform:** The app displays agricultural products such as seeds, fertilizers, pesticides, irrigation items, and region-specific farming products.
- **Farmer Community:** Users can create posts, share farming tips or questions, and read posts from other farmers inside the app.

---

## 2. Problem Statement

### Definition of the Problem

Farmers often face difficulties because useful agricultural information is not available in a single place. Weather information may be in one app, market prices in another portal, government scheme details on websites, and farming products on separate ecommerce platforms. This separation increases effort, consumes time, and limits timely decisions, especially for users who need simple and mobile-friendly access.

Farmers do not always have a standardized platform to:

- Check local weather updates in a simple form
- Track crop market prices from APMC sources
- Read agricultural articles and farming tips
- Explore government schemes relevant to farming
- Buy or view agricultural products digitally
- Connect with other farmers for communication and support

### Objectives

- To build a single Android application for multiple farming-related services
- To provide weather forecast details using live location and API data
- To display APMC market prices based on selected district
- To provide access to government yojanas and agriculture information
- To create an ecommerce section for farming-related products
- To support farmer interaction through a social posting module

### Outcomes

- **For Farmers:** Faster access to important agricultural information through one mobile app
- **For Rural Users:** Easier digital adoption using a familiar Android-based interface
- **For the System:** Modular and expandable architecture using Firebase, fragments, and API integration

---

## 3. Background

### Tools and Technologies

### Frontend

- **Kotlin:** Main programming language used for Android application logic
- **XML:** User interface layout design for activities, fragments, cards, and navigation views
- **Android Studio:** Primary IDE used for development, debugging, and APK generation
- **Data Binding:** Used for cleaner UI interaction with ViewModel-based code

### Backend and Cloud Services

- **Firebase Authentication:** Handles user registration, login, password reset, and authentication state
- **Cloud Firestore:** Stores app content such as users, posts, products, yojanas, and order-related data
- **Firebase Storage / Realtime Database:** Present in the project for cloud-related support and extensibility

### API and Live Data Integration

- **OpenWeather API / Weather API integration:** Used for forecast-related weather data
- **APMC API:** Used to fetch market price information for selected districts
- **Google Play Services Location:** Used to detect user location for weather updates

### UI and Support Libraries

- **RecyclerView:** Used for listing products, posts, weather items, and APMC records
- **ViewModel and LiveData:** Used to manage UI-related data across configuration changes
- **Navigation Components / Safe Args:** Used for screen movement and fragment handling
- **Material Components:** Used for modern Android UI elements
- **Glide and Picasso:** Used for loading and displaying images
- **Razorpay SDK:** Used for demo payment support in the ecommerce flow

### Software and Hardware Requirements

| Category | Details |
| --- | --- |
| IDE | Android Studio |
| Language | Kotlin, XML |
| Version Control | Git / GitHub |
| Cloud Platform | Firebase Console |
| APIs | Weather API, APMC API |
| Mobile Device | Android phone or emulator |
| Connectivity | Internet connection required |

---

## 4. Figma (Architecture if any)

The application follows a multi-screen Android architecture based mainly on **Activities, Fragments, ViewModels, Firebase services, and API integrations**.

### High-Level Architecture

- **Authentication Layer:** Login and signup activities handle user access through Firebase Authentication.
- **Dashboard Layer:** A central dashboard activity manages the overall navigation of the app.
- **Feature Fragments:** Individual modules such as Weather, APMC, Ecommerce, Social Media, Articles, and Yojna are separated into fragments.
- **Data Layer:** ViewModels and Firebase/API calls handle business logic and data retrieval.
- **Cloud Layer:** Firestore stores user-generated and app-managed content, while external APIs provide weather and market information.

### Basic Flow

1. User opens the app
2. User signs up or logs in
3. Dashboard opens as the main home screen
4. User navigates to modules like Weather, APMC, Ecommerce, Community, and Yojna
5. Data is fetched from Firebase or APIs and displayed in RecyclerViews and detail screens

You can use the following simplified architecture diagram in your submission:

`User -> Login/Signup -> Dashboard -> Feature Modules -> Firebase / APIs -> Mobile UI`

---

## 5. Screenshots of the Project

Add screenshots from your app in a format similar to the sample report. Recommended screenshots:

**Fig 1:** Login page

**Fig 2:** Dashboard / Home page

**Fig 3:** Weather forecast page

**Fig 4:** APMC price page

**Fig 5:** Ecommerce product page

**Fig 6:** Social media / farmer community page

If your teacher wants exactly the same count style as the sample, use six screenshots across three pages with two images per page.

---

## 6. User Manual

### Getting Started

1. Download and install the application APK on an Android device.
2. Open the app from the phone menu.
3. Create an account using the Sign Up option, or log in with an existing account.

### Using the Main Features

### Login and Account Access

1. Open the application.
2. Enter registered email and password on the login screen.
3. Tap `Login` to continue to the dashboard.
4. If required, use the `Forgot Password` option to reset account access.

### Checking Weather Information

1. Open the Weather module from the dashboard or navigation menu.
2. View forecast information displayed for the default or selected city.
3. Tap `Use My Location` to detect the device location and update weather details for the current area.

### Viewing APMC Market Prices

1. Open the APMC module.
2. Select the state from the dropdown list.
3. Select the district from the second dropdown.
4. View crop price records shown in the list for the selected market area.

### Reading Government Schemes

1. Open the Yojna or scheme section.
2. Select a scheme from the available list.
3. Read the title, description, launch details, eligibility, required documents, objectives, and website link.

### Using the Ecommerce Module

1. Open the Ecommerce section.
2. Browse all products or filter by category such as fertilizer, pesticide, irrigation, seed, or grapes.
3. Open any item to view details.
4. Add items to cart and proceed toward the payment or order flow.

### Creating and Viewing Farmer Posts

1. Open the Social Media section.
2. Tap the floating action button to create a post.
3. Enter the post title, description, and category.
4. Submit the post and return to the community feed to view updates from users.

---

## 7. Conclusion

### Summary of Findings

The Farming App successfully combines several useful agricultural services into one Android platform. Instead of depending on multiple apps, the user can log in once and access weather forecasts, APMC price information, government yojanas, ecommerce products, agriculture articles, and farmer community posts from a unified interface.

### Key Results

- **Integrated Access:** The app provides multiple farming services in one place
- **Live Utility:** Weather and market modules make the app useful for day-to-day agricultural planning
- **Digital Awareness:** Yojana and article sections improve knowledge access for farmers
- **User Engagement:** Community posting enables interaction and information sharing among users
- **Technical Implementation:** Firebase integration, fragment-based navigation, and API support make the app modular and extendable

### Final Remark

This project demonstrates that mobile application development can be used effectively in the agriculture sector to improve access to information, digital participation, and basic ecommerce support for farmers. With further refinement and larger-scale deployment, Agri India can become a more practical real-world digital assistant for farming communities.

---

## 8. Link of the Project (GitHub)

[Paste your GitHub repository link here]

---

## 9. Appendices

- APK file of the project
- Source code repository link
- Screenshots used in the report
- Course completion certificate, if required by the department

---

## Final Editing Notes

Before submission, replace these placeholders:

- `[Your Name]`
- `[Your Class and Roll Number]`
- `[Guide Name 1]`
- `[Guide Name 2]`
- `[College Name]`
- `[Academic Year]`

You should also insert actual screenshots from the app in Section 5 and, if needed, export this report to PDF or Word format for submission.
