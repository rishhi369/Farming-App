# FORMAL PROJECT REPORT

## Title of the Project

**Agri India: A Smart Android Application for Farmers**

---

## Certificate / Submission Statement

This is to certify that the project titled **"Agri India: A Smart Android Application for Farmers"** is a genuine software project developed using **Android Studio**, **Kotlin**, **XML**, **Firebase**, and external APIs for academic submission and demonstration purposes.

This project has been designed and implemented to address real-world agricultural needs such as weather forecasting, market price tracking, government scheme awareness, agriculture product access, and farmer community interaction through a mobile platform.

---

## Abstract

Agriculture is one of the most important sectors in India, and farmers often require timely access to information related to weather conditions, government schemes, market prices, modern farming techniques, and essential agricultural products. However, this information is usually scattered across different websites, applications, and offices, making it difficult for farmers to access everything from a single platform.

The proposed project, **Agri India**, is an Android application developed to solve this problem by integrating multiple farming-related services into one mobile app. The application provides weather forecasting, APMC market prices, agriculture articles, government schemes, ecommerce for farm-related products, and a social media-like farmer community. The application uses **Firebase Authentication** for user login and registration, **Cloud Firestore** for cloud database storage, **OpenWeather API** for weather information, and **Government APMC API** for mandi price data.

The project is designed using a mostly **MVVM-based Android architecture** with Activities, Fragments, ViewModels, adapters, and Firebase integration. The application focuses on usability, modularity, and demo reliability by including fallback data handling and safer null/error management.

This project demonstrates how mobile technology can be used to support farmers by improving access to agricultural information, digital services, and communication tools.

---

## Acknowledgement

I would like to express my sincere gratitude to my teachers, guides, and department for their valuable guidance and encouragement during the development of this project. Their support helped me understand the practical aspects of Android development, Firebase integration, API usage, UI design, and project presentation.

I would also like to acknowledge the use of Android Studio, Firebase services, and publicly available development resources that supported the implementation and testing of this application.

---

## Table of Contents

1. Introduction  
2. Problem Statement  
3. Objectives of the Project  
4. Scope of the Project  
5. Existing System and Proposed System  
6. Technologies Used  
7. System Architecture  
8. Module Description  
9. Database and API Integration  
10. User Interface Design  
11. Implementation Details  
12. Testing and Validation  
13. Advantages of the System  
14. Limitations  
15. Future Scope  
16. Conclusion  
17. References  

---

## 1. Introduction

India is an agriculture-based country where a large section of the population depends on farming and related activities for livelihood. Farmers need daily support in the form of weather updates, mandi rates, crop information, product availability, and government assistance schemes. In many cases, these resources are not easily available in a centralized digital form.

With the growth of smartphones and internet connectivity, Android applications have become a practical medium for delivering agriculture-related services directly to farmers. Keeping this in mind, the project **Agri India** was developed as a smart mobile application to provide useful agricultural services in one place.

This project is not limited to a single feature. Instead, it combines:

- weather forecasting
- APMC market price viewing
- agriculture learning articles
- government yojana information
- ecommerce for farm products
- farmer community posting and interaction

This makes the application practical, informative, and suitable for real-world use as well as academic demonstration.

---

## 2. Problem Statement

Farmers often face the following problems:

- Lack of a single platform for agricultural information
- Difficulty accessing timely weather updates
- Limited knowledge of market prices before selling crops
- Incomplete awareness of government schemes
- Need to use multiple platforms for articles, products, and communication
- Limited digital community support among farmers

Because of this, decision-making becomes difficult and inefficient.

Therefore, there is a need for a unified Android application that can provide:

- weather information
- mandi prices
- scheme details
- learning content
- agriculture product discovery
- farmer interaction

all within one user-friendly platform.

---

## 3. Objectives of the Project

The main objectives of the project are:

- To develop an Android application for farmers
- To provide weather forecast information in a simple format
- To display APMC/mandi market prices
- To provide agricultural learning articles
- To show government schemes useful for farmers
- To provide an ecommerce module for agriculture products
- To create a community platform for farmer interaction
- To use modern Android development practices such as ViewModel, LiveData, and Firebase
- To create a stable and presentable application suitable for demonstration and future expansion

---

## 4. Scope of the Project

The scope of this project includes the development of a mobile application that supports multiple farmer-focused digital services in one place.

### Current Scope

- Android mobile application
- Firebase-based login and cloud storage
- Weather API integration
- APMC market data integration
- Firestore-backed articles, schemes, products, posts, cart, and orders
- Social media posting and interaction
- Dashboard-based navigation

### Intended Users

- Farmers
- Agriculture students
- Rural users with Android smartphones
- Users interested in farm support information

### Geographic Relevance

The ecommerce module has been adapted to better reflect the needs of **Nashik-region farming**, especially for:

- grapes
- onion
- vegetables
- irrigation-related products
- crop protection items

---

## 5. Existing System and Proposed System

### Existing System

In the existing situation, farmers often rely on:

- separate weather apps
- separate news or article sources
- separate market-price portals
- manual information from local shops or offices
- independent platforms for product purchase

This causes fragmentation and inconvenience.

### Drawbacks of Existing System

- Information is not centralized
- User experience is inconsistent across platforms
- Scheme awareness is limited
- Market information is not always easy to compare
- No integrated farmer communication feature

### Proposed System

The proposed system is **Agri India**, which brings together:

- weather updates
- mandi price information
- educational articles
- government yojanas
- product browsing and ordering
- community posting and discussion

### Benefits of Proposed System

- One-stop platform
- Improved convenience
- Better digital accessibility
- Easier project demonstration and expansion

---

## 6. Technologies Used

### Programming Languages

- **Kotlin** - main application logic
- **XML** - Android UI layout design

### Development Tools

- **Android Studio**
- **Gradle**
- **Firebase Console**

### Android Libraries and Components

- ViewModel
- LiveData
- Data Binding
- RecyclerView
- ViewPager2
- Navigation Component / Safe Args
- Material Components

### Backend / Cloud Services

- Firebase Authentication
- Cloud Firestore

### APIs and External Services

- OpenWeather API
- Government APMC API
- Google News RSS feed for agri headlines
- Google Play Services Location

### Image and UI Utilities

- Glide
- Picasso

### Additional Dependencies Present

- Firebase Storage
- Firebase Realtime Database
- Razorpay SDK
- Room

Some of these are included in the project dependencies, while the primary active modules for the final application rely mainly on **Firebase Authentication**, **Firestore**, **Retrofit**, and **ViewModel-based logic**.

---

## 7. System Architecture

### Architecture Pattern

The application mainly follows a **hybrid MVVM architecture**.

### Explanation of MVVM in This Project

#### Model Layer

The model layer contains:

- data classes
- API interfaces
- repository classes

Examples:

- `AuthRepository.kt`
- `WeatherApi.kt`
- `APMCApi.kt`
- model data classes under `model/data`

#### View Layer

The view layer contains:

- Activities
- Fragments
- XML layout files

Examples:

- `LoginActivity.kt`
- `DashboardFragment.kt`
- `WeatherFragment.kt`

#### ViewModel Layer

The ViewModel layer handles:

- business logic
- data fetching
- LiveData updates

Examples:

- `AuthViewModel.kt`
- `WeatherViewModel.kt`
- `EcommViewModel.kt`
- `YojnaViewModel.kt`

### Folder Structure

#### `adapter`

- RecyclerView adapters for lists and cards

#### `model`

- repositories and API interfaces

#### `model/data`

- data classes and model objects

#### `view`

- screens divided by modules

#### `viewmodel`

- business logic and LiveData management

#### `utilities`

- helper functions and interfaces

#### `res/layout`

- XML screen and item layouts

#### `res/drawable`

- shapes, icons, and local visual assets

### Data Flow

The general data flow is:

1. User interacts with UI
2. Activity/Fragment sends action to ViewModel
3. ViewModel accesses Firebase/API/repository
4. Data is returned as LiveData or direct callback
5. UI observes and updates screen

This structure improves modularity and reduces excessive UI-side logic in many parts of the app.

---

## 8. Module Description

### 8.1 Intro / Onboarding Module

This module is used when the app is opened for the first time.

#### Functions

- Displays app introduction screens
- Uses `ViewPager2`
- Stores first-time status using `SharedPreferences`

#### Benefit

- Helps new users understand the app before login

---

### 8.2 Authentication Module

This module handles user registration and login.

#### Screens

- LoginActivity
- SignupActivity

#### Features

- Signup using email and password
- Login using email and password
- Forgot password support
- Redirect logged-in users to dashboard

#### Backend

- Firebase Authentication
- Firestore user profile storage

---

### 8.3 Dashboard / Home Module

This is the main landing page after login.

#### Features

- Weather hero card
- Agriculture category cards
- Agri News section
- Products preview section

#### Purpose

- To provide a quick overview of important app features from one screen

---

### 8.4 Weather Module

This module provides weather forecasting.

#### Features

- Temperature
- Humidity
- Wind speed
- city display
- forecast cards
- "Use My Location" support

#### Backend

- OpenWeather API
- location services

---

### 8.5 APMC Module

This module displays mandi prices.

#### Features

- State selection
- District selection
- Market-wise grouped commodity prices
- fallback sample data if API fails

#### Backend

- Government market price API

---

### 8.6 Articles Module

This module provides crop and agriculture learning content.

#### Categories

- Plants
- Methods
- Diseases
- Fruits

#### Backend

- Firestore collections
- sample data seeding

---

### 8.7 Yojana Module

This module displays farmer-related government schemes.

#### Features

- Scheme list
- detail page
- eligibility, objectives, budget, website information

#### Backend

- Firestore collection `yojnas`

---

### 8.8 Ecommerce Module

This module provides agriculture product browsing and order placement.

#### Features

- Product list
- category filter chips
- product detail page
- add to cart
- cart management
- demo order placement
- order history

#### Backend

- Firestore products
- Firestore cart and order subcollections

---

### 8.9 Social Media Module

This module supports farmer community engagement.

#### Features

- create post
- category-based posts
- like post
- comment on post
- live feed update

#### Backend

- Firestore `posts`
- Firestore comment subcollection

---

### 8.10 User Profile Module

This module shows and updates user information.

#### Features

- profile display
- city/about edit
- user posts display
- own-post deletion

#### Backend

- Firestore user document
- Firestore posts query by user id

---

## 9. Database and API Integration

### Firebase Authentication

Used for:

- user signup
- user login
- session handling
- password reset

### Cloud Firestore

Used for:

- users
- products
- cart
- orders
- posts
- comments
- yojanas
- article collections

### Firestore Structure

#### Main Collections

- `users`
- `products`
- `posts`
- `yojnas`
- `article_plants`
- `article_methods`
- `article_diseases`
- `article_fruits`

#### Subcollections

- `users/{uid}/cart`
- `users/{uid}/orders`
- `posts/{postId}/comments`

### API Integration

#### OpenWeather API

Used to fetch:

- forecast
- temperature
- humidity
- wind speed

#### Government APMC API

Used to fetch:

- commodity market price data
- district-wise mandi information

#### Agri News Feed

Used to fetch:

- current agriculture headlines through RSS parsing

### Data Reliability Strategy

To improve reliability during usage and demonstration:

- weather has fallback city logic
- APMC has fallback sample data
- news has fallback sample headlines
- Firestore collections seed demo data when empty

This makes the project more stable in practical situations.

---

## 10. User Interface Design

### Design Approach

The user interface has been designed using Android XML layouts with a section-based approach. Different screens use RecyclerViews, Material inputs, card-based sections, and icons to create a structured mobile experience.

### Important UI Areas

#### Authentication UI

- simple and clear form layout
- email/password inputs
- sign-in and sign-up action buttons

#### Dashboard UI

- modern weather hero card
- category shortcuts
- agri news cards
- product preview shelf

#### Ecommerce UI

- filter chips
- product cards
- detailed item page with image slider

#### Social Media UI

- post feed
- create-post screen
- like/comment interaction on cards

#### Profile UI

- user detail section
- edit controls
- post listing area

### Design Principles Used

- modular screen layout
- reusable item cards
- section separation
- responsive list presentation
- user-friendly navigation

---

## 11. Implementation Details

### Authentication Implementation

- Data binding connects login/signup UI to `AuthViewModel`
- `AuthRepository` performs Firebase Auth operations
- Firestore stores additional user profile information

### Dashboard Implementation

- `DashboardActivity` acts as main host activity
- `DashboardFragment` combines weather, articles, agri news, and products

### Weather Implementation

- `WeatherViewModel` stores coordinates and weather response
- `WeatherApi` fetches forecast through Retrofit
- `WeatherFragment` and `DashboardFragment` observe weather data

### APMC Implementation

- `ApmcFragment` collects selected state and district
- API is called using Retrofit
- records are grouped by market

### Article and Yojana Implementation

- Firestore data is loaded through ViewModels
- if collections are empty, sample data is seeded
- list screen opens detail screen

### Ecommerce Implementation

- Firestore product catalog is loaded through `EcommViewModel`
- product detail screen supports cart and order flow
- cart and order history are user-specific

### Social Media Implementation

- posts are stored in Firestore
- likes use count and array fields
- comments use nested Firestore collections

### Profile Implementation

- user data is loaded from Firestore
- editable city/about fields are updated in Firestore

---

## 12. Testing and Validation

### Testing Method

The application was primarily tested through:

- manual functional testing
- screen-by-screen verification
- device-based demo testing
- Gradle build verification

### Functional Areas Tested

- Signup and login flow
- dashboard navigation
- weather loading
- location update
- APMC district selection
- article loading
- yojana loading
- ecommerce browsing
- add to cart
- order creation
- social media post creation
- profile editing

### Validation Strategy

- ensured screen navigation does not break
- reduced null-related crashes
- added fallback data where needed
- used build success as technical validation

---

## 13. Advantages of the System

- One app for multiple agriculture-related needs
- Simple Android mobile access
- Cloud-based data with Firebase
- Real-time style content such as weather, news, and social posts
- Better presentation and usability for academic demonstration
- Extendable architecture for future features
- Practical support for farmers

---

## 14. Limitations

- Some old placeholder files still exist from the original codebase
- Live payment gateway is not active in final demo flow
- Google Sign-In is present in dependencies but not used as the main path
- Firebase Storage upload flow is not enabled for the demo
- Some architecture parts are mixed and can be cleaned further
- API usage depends on internet connectivity

---

## 15. Future Scope

The project can be improved in the following ways:

- Add real payment integration
- Add image upload for posts and profile
- Add admin panel
- Add multilingual support
- Add push notifications
- Add crop recommendation features
- Add disease detection using machine learning
- Add chatbot for farmer guidance
- Add stronger offline support and caching
- Refactor into a stricter clean MVVM architecture

---

## 16. Conclusion

The project **Agri India** successfully demonstrates how an Android application can be used to support farmers by bringing together multiple useful digital services on a single platform. The application covers weather forecasting, mandi prices, agricultural articles, government schemes, ecommerce, and community interaction. It has been developed using Kotlin and Android Studio with Firebase as the main backend and external APIs for live information.

The project is academically valuable because it combines mobile app development, backend integration, API consumption, UI design, and modular architecture in a single practical application. It is also socially relevant because it addresses real agricultural information needs.

Overall, this project shows how technology can be used to build a meaningful, user-oriented solution for the farming domain.

---

## 17. References

- Android Developers Documentation  
- Kotlin Documentation  
- Firebase Documentation  
- OpenWeather API Documentation  
- Data.gov.in API Resources  
- Material Design Guidelines  
- Android Studio Official Documentation  

---

## Annexure A - Key Project Files

Some of the major project files are:

- `DashboardActivity.kt`
- `DashboardFragment.kt`
- `LoginActivity.kt`
- `SignupActivity.kt`
- `AuthViewModel.kt`
- `AuthRepository.kt`
- `WeatherViewModel.kt`
- `WeatherApi.kt`
- `ApmcFragment.kt`
- `APMCApi.kt`
- `ArticleViewModel.kt`
- `YojnaViewModel.kt`
- `EcommViewModel.kt`
- `EcommerceFragment.kt`
- `EcommerceItemFragment.kt`
- `CartFragment.kt`
- `RazorPayActivity.kt`
- `MyOrdersFragment.kt`
- `SocialMediaPostsFragment.kt`
- `SMCreatePostFragment.kt`
- `SMPostListAdapter.kt`
- `UserFragment.kt`

---

## Annexure B - Suggested Viva Demo Flow

For formal demonstration, the application can be presented in this order:

1. Login / Signup  
2. Dashboard  
3. Weather  
4. APMC  
5. Articles  
6. Yojanas  
7. Ecommerce  
8. Cart and Demo Order  
9. Social Media  
10. Profile  

---

## Annexure C - Short Oral Summary

**Agri India is an Android application developed to support farmers by combining weather updates, market prices, government schemes, articles, ecommerce, and community interaction in one platform. The application is developed using Kotlin, XML, Firebase, Retrofit, and Android architecture components. It is designed to be practical, modular, and useful for both academic demonstration and future real-world enhancement.**
