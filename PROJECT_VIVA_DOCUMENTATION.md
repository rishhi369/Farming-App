# Agri India / Farming App - Complete Project Documentation for Exam/Viva

This document explains the full Android project in simple but technical language. It is written for:

- viva preparation
- project report explanation
- demo presentation
- answering examiner questions confidently

---

## 1. PROJECT OVERVIEW

### App Name

- **Agri India**
- In `settings.gradle`, the Gradle project name is **Farming App**
- In presentation, you can say:
  - "My project is Agri India, an Android application for farmers."

### Objective of the App

- To provide multiple agriculture-related services in one mobile application
- To help farmers access:
  - weather information
  - market prices
  - agriculture articles
  - government schemes
  - farm-related product shopping
  - farmer community posts

### Problem It Solves

Farmers usually need different sources for different tasks:

- one app/site for weather
- another for mandi prices
- another for government scheme information
- another for learning articles
- another for agricultural products

This project solves that by combining all of them into one Android app.

### Target Users

- Farmers
- Agriculture students
- Rural users interested in farming
- Users who want weather, mandi prices, and scheme details in one app
- Users from agriculture-heavy regions such as Nashik

---

## 2. TECHNOLOGIES USED

### Languages

- **Kotlin** for app logic
- **XML** for UI layouts
- Small amount of Android resource configuration in XML

### Frameworks and Libraries

#### Firebase

- **Firebase Authentication**
  - for signup and login
- **Cloud Firestore**
  - for storing users, posts, products, orders, schemes, and articles
- **Firebase Storage**
  - dependency is present, but active demo flow avoids storage upload
- **Firebase Realtime Database**
  - dependency is present, but Firestore is the main active database

#### Networking

- **Retrofit**
  - for API calls
- **Gson Converter**
  - for JSON parsing
- **Coroutines**
  - for background/network work

#### Android Architecture Components

- **ViewModel**
- **LiveData**
- **Navigation Component / Safe Args**
- **Data Binding**

#### UI Libraries

- **Material Components**
- **Glide**
  - image loading
- **Picasso**
  - image loading in some screens
- **ViewPager2**
  - onboarding and image slider

#### Google Services

- **Google Play Services Location**
  - device location for weather
- **Google Sign-In dependency**
  - dependency is added, but Google login is guarded because the project is now using user-configured Firebase

#### Other Libraries

- **Razorpay SDK**
  - dependency exists, but current flow uses demo order placement instead of live payment
- **Room**
  - dependency exists but is not the main active storage layer

### Tools

- **Android Studio**
- **Gradle**
- **Firebase Console**
- **Android Emulator / Android phone**
- **Git/GitHub** (project was cloned from GitHub)

---

## 3. ARCHITECTURE

### Architecture Pattern Used

The app mainly follows a **hybrid MVVM architecture**.

#### Why hybrid?

Because:

- `Activity` and `Fragment` classes handle UI and user interaction
- `ViewModel` classes store and process screen data
- `Repository` classes exist for some modules, but not every module strictly uses repository separation
- some screens directly access Firestore in Fragment/Adapter code

So the app is best described as:

- **Mostly MVVM**
- with some **direct Firebase access in UI classes**

### Main Layers

#### 1. View Layer

Files in:

- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\view`
- `C:\Users\dipak\Farming-App\app\src\main\res\layout`

Purpose:

- show UI
- receive clicks
- navigate between screens
- observe LiveData

Examples:

- `LoginActivity.kt`
- `DashboardFragment.kt`
- `EcommerceFragment.kt`
- `WeatherFragment.kt`

#### 2. ViewModel Layer

Files in:

- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\viewmodel`

Purpose:

- keep UI data
- talk to Firebase/API/repository
- expose LiveData to Fragments/Activities

Examples:

- `AuthViewModel.kt`
- `WeatherViewModel.kt`
- `EcommViewModel.kt`
- `YojnaViewModel.kt`

#### 3. Model Layer

Files in:

- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\model`
- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\model\data`

Purpose:

- API definitions
- repository classes
- data classes / POJOs / model objects

Examples:

- `WeatherApi.kt`
- `APMCApi.kt`
- `AuthRepository.kt`
- `WeatherRootList.kt`
- `CartItem.kt`

### Folder Structure Explanation

#### `adapter/`

Contains RecyclerView adapters and UI binding helpers.

Examples:

- `EcommerceAdapter.kt`
- `SMPostListAdapter.kt`
- `WeatherAdapter.kt`

#### `model/`

Contains repositories and API interfaces.

Examples:

- `AuthRepository.kt`
- `WeatherApi.kt`
- `APMCApi.kt`

#### `model/data/`

Contains model/data classes used by APIs and Firestore.

Examples:

- `Post.kt`
- `orders.kt`
- `APMCRecords.kt`

#### `view/`

Contains Activities and Fragments, grouped by module.

Examples:

- `view/auth`
- `view/dashboard`
- `view/ecommerce`
- `view/socialmedia`

#### `viewmodel/`

Contains business/data logic for screens.

#### `utilities/`

Contains interfaces and helper extension methods.

Examples:

- `ViewUtils.kt`
- `CellClickListener.kt`
- `CartItemBuy.kt`

#### `res/layout/`

Contains XML layout files for screens and list items.

#### `res/drawable/`

Contains icons, images, and shape backgrounds.

### Data Flow (Step-by-Step)

#### Example: Login Flow

1. User enters email and password in `LoginActivity`
2. Activity uses data binding to connect fields to `AuthViewModel`
3. User clicks login button
4. `AuthViewModel.onLoginButtonClick()` is called
5. `AuthRepository.logInWithEmail()` calls Firebase Authentication
6. Success/failure message is posted to LiveData
7. `LoginActivity` observes result
8. On success, app opens `DashboardActivity`

#### Example: Weather Flow

1. `DashboardActivity` or Weather screen gets device location
2. Latitude/longitude/city are sent to `WeatherViewModel.updateCoordinates()`
3. `WeatherViewModel.updateNewData()` calls `WeatherApi` through Retrofit
4. API response is parsed to `WeatherRootList`
5. LiveData updates
6. `DashboardFragment` or `WeatherFragment` observes LiveData
7. UI updates temperature, humidity, wind, forecast

#### Example: Ecommerce Flow

1. `EcommerceFragment` calls `EcommViewModel.loadAllEcommItems()`
2. ViewModel reads Firestore `products` collection
3. If collection is empty, demo products are seeded
4. Result is posted to LiveData
5. RecyclerView adapter shows products
6. On product click, `EcommerceItemFragment` opens
7. Add to cart writes data to `users/{uid}/cart/{productId}`

---

## 4. MODULE-WISE EXPLANATION

This section explains each major module.

### A. Intro / Onboarding Module

#### Purpose

- To introduce the application when user opens it first time
- To explain main app features visually

#### UI Design Explanation

- Uses `ViewPager2`
- Shows multiple intro pages with text and images
- Has next/skip style onboarding flow

#### Backend Logic

- Uses `SharedPreferences` (`MyPrefs`) with `firstTime`
- If first time is false, intro is skipped in future launches

#### Key Files

- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\view\introscreen\IntroActivity.kt`
- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\adapter\IntroAdapter.kt`
- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\model\data\IntroData.kt`
- `C:\Users\dipak\Farming-App\app\src\main\res\layout\activity_intro.xml`

---

### B. Authentication Module

#### Purpose

- User signup
- User login
- Password reset
- Session management

#### UI Design Explanation

- Separate login and signup screens
- Material-style text input fields
- Background image
- Form validation messages using Toast

#### Backend Logic

- `FirebaseAuth.createUserWithEmailAndPassword()`
- `FirebaseAuth.signInWithEmailAndPassword()`
- User profile stored in Firestore `users/{uid}`

#### Key Files

- `LoginActivity.kt`
- `SignupActivity.kt`
- `AuthViewModel.kt`
- `AuthRepository.kt`
- `activity_login.xml`
- `activity_signup.xml`

#### Important Logic

- Signup validates all fields before creating account
- Firestore user document stores:
  - name
  - mobile number
  - email
  - city
  - profile image
  - post count
  - uid

---

### C. Dashboard / Home Module

#### Purpose

- Main landing screen after login
- Gives access to weather, articles, news, and product preview

#### UI Design Explanation

- Modern home layout
- Hero weather card
- category cards for agriculture content
- agri news list
- product preview shelf

#### Backend Logic

- Reads weather from `WeatherViewModel`
- Reads products from `EcommViewModel`
- Reads agri news from `AgriNewsViewModel`

#### Key Files

- `DashboardActivity.kt`
- `DashboardFragment.kt`
- `fragment_dashboard.xml`
- `app_bar_main.xml`
- `activity_dashboard.xml`

---

### D. Weather Module

#### Purpose

- Show live weather forecast
- Show temperature, humidity, and wind speed
- Show weather for current or fallback location

#### UI Design Explanation

- city name on top
- use-my-location button
- horizontal current forecast list
- vertical daily forecast list

#### Backend Logic

- Uses device location through `FusedLocationProviderClient`
- Calls OpenWeather API using Retrofit
- Parses 5-day / 3-hour forecast data

#### Key Files

- `WeatherFragment.kt`
- `WeatherViewModel.kt`
- `WeatherApi.kt`
- `WeatherAdapter.kt`
- `CurrentWeatherAdapter.kt`
- `fragment_weather.xml`

---

### E. APMC / Market Price Module

#### Purpose

- Show crop market prices by district
- Help farmers compare mandi prices

#### UI Design Explanation

- state spinner
- district spinner
- market-wise grouped list
- price details for commodities

#### Backend Logic

- Calls Government APMC API from `data.gov.in`
- Filters by district
- Groups result by market
- Uses fallback demo data if API fails or returns empty

#### Key Files

- `ApmcFragment.kt`
- `APMCApi.kt`
- `ApmcAdapter.kt`
- `fragment_apmc.xml`
- `apmc_single_list.xml`

---

### F. Articles Module

#### Purpose

- Provide agriculture learning content
- Cover plants, methods, diseases, fruits

#### UI Design Explanation

- article list grid
- image + title based cards
- article detail screen with long content sections

#### Backend Logic

- Reads from Firestore collections
- If collection is empty, seeds sample article
- Detail screen maps article fields to UI

#### Key Files

- `ArticleListFragment.kt`
- `FruitsFragment.kt`
- `ArticleViewModel.kt`
- `ArticleListAdapter.kt`
- `fragment_article_list.xml`
- `fragment_fruits.xml`

---

### G. Yojana Module

#### Purpose

- Show government schemes for farmers
- Educate users about benefits, budget, eligibility, documents

#### UI Design Explanation

- list of schemes
- detail screen with text sections and image

#### Backend Logic

- Data stored in Firestore collection `yojnas`
- seeds sample data if empty

#### Key Files

- `YojnaListFragment.kt`
- `YojnaFragment.kt`
- `YojnaViewModel.kt`
- `YojnaAdapter.kt`
- `fragment_yojna_list.xml`
- `fragment_yojna.xml`

---

### H. Ecommerce Module

#### Purpose

- Show agriculture products
- Allow viewing details
- add to cart
- buy demo order
- view orders

#### UI Design Explanation

- category chips
- product cards
- product detail page with image slider, attributes, price, quantity
- cart list
- order history

#### Backend Logic

- Products stored in Firestore `products`
- cart stored under `users/{uid}/cart`
- orders stored under `users/{uid}/orders`
- product list is seeded with Nashik-relevant products if empty

#### Key Files

- `EcommerceFragment.kt`
- `EcommerceItemFragment.kt`
- `CartFragment.kt`
- `RazorPayActivity.kt`
- `MyOrdersFragment.kt`
- `EcommViewModel.kt`
- `CartItemsAdapter.kt`
- `MyOrdersAdapter.kt`

---

### I. Social Media Module

#### Purpose

- Create a farmer community space
- allow posts, likes, and comments

#### UI Design Explanation

- post feed screen
- floating action button for new post
- create-post form
- category selection
- like button and comment input

#### Backend Logic

- posts stored in Firestore `posts`
- comments stored in subcollection `posts/{postId}/comments`
- likes handled using `likedBy` array and `likes` count

#### Key Files

- `SocialMediaPostsFragment.kt`
- `SMCreatePostFragment.kt`
- `SMPostListAdapter.kt`
- `fragment_social_media_posts.xml`
- `fragment_s_m_create_post.xml`
- `post_with_image_sm.xml`

---

### J. User Profile Module

#### Purpose

- Show user information
- allow edit of city and about section
- show user posts

#### UI Design Explanation

- cover image + profile image
- name, email, city, about
- edit mode for profile fields
- user posts list

#### Backend Logic

- Reads user profile from Firestore `users/{uid}`
- updates city/about in Firestore
- loads user posts from `posts` collection
- delete own post

#### Key Files

- `UserFragment.kt`
- `UserDataViewModel.kt`
- `UserProfilePostsViewModel.kt`
- `PostListUserProfileAdapter.kt`
- `fragment_user.xml`

---

### K. Agri News Module

#### Purpose

- Show current agriculture-related headlines
- make Home screen feel live and updated

#### UI Design Explanation

- compact cards on dashboard
- title, source, date
- tap to open article in browser

#### Backend Logic

- Parses Google News RSS feed using `HttpURLConnection` + `XmlPullParser`
- if feed fails, fallback demo headlines are used

#### Key Files

- `AgriNewsViewModel.kt`
- `AgriNewsAdapter.kt`
- `AgriNewsItem.kt`
- `single_agri_news_item.xml`

---

## 5. CODE EXPLANATION (VERY IMPORTANT)

This section explains the most important files in viva-friendly language. Since the project has many files, the explanation is grouped file-by-file and method-by-method, which is easier to speak than reading every code line literally.

### 5.1 `AuthRepository.kt`

File:

- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\model\AuthRepository.kt`

#### Purpose

- Central authentication helper
- Connects UI layer to Firebase Authentication and Firestore

#### Logic Explanation

- Creates an instance of `FirebaseAuth`
- Uses `Firebase.firestore` for Firestore

#### Important Functions

##### `signInWithEmail(email, password, userDetails, listener)`

- Creates Firebase account using email and password
- On success:
  - gets `uid`
  - creates a Firestore user document
  - saves all extra user profile fields
- On failure:
  - returns error message through `AuthListener`

##### `logInWithEmail(email, password, listener)`

- Signs in existing user with Firebase
- Returns success or error through listener

##### `firebaseAuthWithGoogle(idToken, listener)`

- Handles Google credential login
- If user does not exist in Firestore, creates profile
- In current demo flow, Google Sign-In is not the main path because the cloned project is now connected to the user’s own Firebase setup

#### Viva Explanation

You can say:

- "This repository separates Firebase authentication work from the Activity."
- "It creates the account in Firebase Auth and stores extra user data in Firestore."

---

### 5.2 `AuthViewModel.kt`

File:

- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\viewmodel\AuthViewModel.kt`

#### Purpose

- Holds authentication form data
- validates user input
- calls repository methods

#### Main Variables

- `name`
- `mobNo`
- `email`
- `city`
- `password`
- `confirmPassword`
- `loginEmail`
- `loginPassword`

These are bound to XML using data binding.

#### Important Functions

##### `onSignUpButtonClick()`

- Checks if fields are empty
- checks password match
- builds user data map
- calls `AuthRepository.signInWithEmail`

##### `onLoginButtonClick()`

- Checks login field validity
- calls `AuthRepository.logInWithEmail`

##### `returnActivityResult(context, data)`

- Handles Google sign-in result if configured

#### Viva Explanation

- "ViewModel stores form values and validation logic, so Activity stays cleaner."

---

### 5.3 `LoginActivity.kt`

File:

- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\view\auth\LoginActivity.kt`

#### Purpose

- Login screen controller

#### Logic

- Inflates layout using data binding
- creates `AuthViewModel`
- if user is already logged in, directly opens Dashboard
- supports forgot password using `sendPasswordResetEmail`
- observes login success message

#### Important Blocks

##### Data Binding Setup

- Connects XML fields and buttons to `AuthViewModel`

##### Forgot Password

- Reads email field
- sends reset email through FirebaseAuth

##### Success Observer

- If ViewModel posts `"Success"`, DashboardActivity opens

#### Viva Explanation

- "This Activity acts as the controller for login. It binds the UI to the ViewModel and reacts to success/failure."

---

### 5.4 `SignupActivity.kt`

#### Purpose

- New user registration screen

#### Logic

- Similar to LoginActivity
- binds signup fields to ViewModel
- after successful registration, user is redirected to dashboard

#### Viva Explanation

- "SignupActivity sends registration data through the ViewModel and stores the user in Firebase."

---

### 5.5 `DashboardActivity.kt`

File:

- `C:\Users\dipak\Farming-App\app\src\main\java\com\project\farmingapp\view\dashboard\DashboardActivity.kt`

#### Purpose

- Main container activity after login
- controls bottom navigation, drawer navigation, user header, and location updates

#### Major Responsibilities

- checks onboarding state
- checks authentication state
- opens default home fragment
- loads user data in navigation drawer header
- handles menu clicks
- handles weather location fetch

#### Important Logic

##### Onboarding Check

- Reads `SharedPreferences`
- If first time, opens intro

##### User Session Check

- If no logged-in user, sends user to `LoginActivity`

##### Navigation

- Bottom navigation routes:
  - Home
  - APMC
  - Ecommerce
  - Social

- Drawer routes:
  - Weather
  - Fruits Articles
  - My Orders
  - Create Post
  - Community
  - Logout

##### Location Logic

- Uses `FusedLocationProviderClient`
- asks for current location
- geocodes latitude/longitude into city
- updates `WeatherViewModel`
- if no location is available, weather module uses fallback city

#### Viva Explanation

- "DashboardActivity is the central activity of the project. It acts like the shell of the app and loads different fragments according to bottom-nav or drawer selection."

---

### 5.6 `DashboardFragment.kt`

#### Purpose

- Implements the redesigned Home screen

#### Logic

- gets weather from `WeatherViewModel`
- gets products from `EcommViewModel`
- gets agri news from `AgriNewsViewModel`
- opens article and scheme modules on category clicks

#### Important Methods / Blocks

##### Weather Observation

- If no coordinates are set, defaults to Mumbai
- updates temperature, humidity, wind, city name, weather icon

##### Product Observation

- Loads product list
- shuffles products
- shows only a few products on dashboard

##### News Observation

- Loads headlines
- updates recycler and status text

##### Category Click Handlers

- plants -> article list
- methods -> article list
- diseases -> article list
- fruits -> article list
- yojana -> yojana list

#### Viva Explanation

- "The dashboard is a combination screen. It aggregates data from weather, articles, news, and products using multiple ViewModels."

---

### 5.7 `WeatherViewModel.kt`

#### Purpose

- Holds weather location and API result

#### Main LiveData

- `coordinates`
- `newDataTrial`
- `message1`
- `message2`

#### Important Functions

##### `updateCoordinates(data)`

- stores new latitude/longitude/city
- clears previous weather so UI can refresh

##### `updateNewData()`

- calls OpenWeather API using Retrofit
- stores response in LiveData
- if error happens, stores error text

#### Viva Explanation

- "WeatherViewModel separates API work and location data from the Fragment."

---

### 5.8 `WeatherApi.kt`

#### Purpose

- Retrofit API interface for OpenWeather

#### Important Details

- Base URL: `https://api.openweathermap.org/`
- Endpoint used: `/data/2.5/forecast`
- Parameters:
  - latitude
  - longitude
  - API key
  - units = metric

#### Viva Explanation

- "This file defines the weather API endpoint using Retrofit annotations."

---

### 5.9 `WeatherFragment.kt`

#### Purpose

- Dedicated weather forecast screen

#### Logic

- observes location and weather data
- can request location update by pressing `Use My Location`
- shows horizontal and vertical forecast lists

#### Viva Explanation

- "WeatherFragment is the detailed presentation layer of the weather module."

---

### 5.10 `APMCApi.kt`

#### Purpose

- Retrofit interface for mandi price API

#### Important Details

- Base URL: `https://api.data.gov.in/`
- Uses resource endpoint for crop price data
- API key is passed in query
- district filter is passed in query

#### Viva Explanation

- "This file connects the app to the government market-price API."

---

### 5.11 `ApmcFragment.kt`

#### Purpose

- Displays market prices by state and district

#### Logic

- user selects state
- district list updates
- API call is made using district
- result records are grouped market-wise
- if API fails, fallback sample data is shown

#### Key Strength

- This module is demo-safe because it does not break when the API is unavailable

#### Viva Explanation

- "I used fallback data so the app still works in demo conditions even if the API is down."

---

### 5.12 `ArticleViewModel.kt`

#### Purpose

- Manages article data from Firestore

#### Important Functions

##### `getAllArticles(collectionName)`

- reads all docs from selected article collection
- seeds sample article if collection is empty

##### `getMyArticle(name)`

- loads single fruit/article item

##### `seedSampleArticles(collectionName)`

- inserts one default article document

#### Viva Explanation

- "This ViewModel ensures that article screens always have data, even in a fresh Firebase project."

---

### 5.13 `ArticleListFragment.kt`

#### Purpose

- Shows list of articles for selected category

#### Logic

- receives category and title in fragment arguments
- requests data from ViewModel
- binds data to RecyclerView adapter
- opens detail screen on click

---

### 5.14 `FruitsFragment.kt`

#### Purpose

- Generic article detail screen

#### Logic

- reads selected article title from arguments
- finds matching article
- binds long fields like:
  - description
  - process
  - soil
  - state
  - disease
- expands/collapses description

#### Viva Explanation

- "Although the class is named FruitsFragment, it is being used as the detailed article screen for learning content."

---

### 5.15 `YojnaViewModel.kt`

#### Purpose

- manages scheme data from Firestore

#### Important Functions

##### `getAllYojna()`

- fetches all scheme documents
- if empty, seeds sample schemes

##### `getYojna(id)`

- fetches a single scheme by document id

#### Viva Explanation

- "This module uses Firestore instead of a public API to keep scheme data stable and controlled."

---

### 5.16 `EcommViewModel.kt`

#### Purpose

- Controls product catalog and product retrieval

#### Main Logic

- Loads products from Firestore
- Seeds sample products if collection is empty
- Filters by type/category
- Loads a single product by ID

#### Important Point

- The demo catalog is curated for **Nashik-oriented farming needs**

#### Seeded Product Categories

- fertilizer
- plant care
- irrigation
- seeds
- grapes

#### Viva Explanation

- "I adapted the ecommerce catalog according to Nashik farming needs such as grapes, onion, vegetables, irrigation, and crop protection."

---

### 5.17 `EcommerceFragment.kt`

#### Purpose

- Product list screen

#### Logic

- loads all items
- filters by category chips
- opens product details
- opens cart from toolbar/menu

#### Viva Explanation

- "This Fragment is the entry point of the ecommerce module."

---

### 5.18 `EcommerceItemFragment.kt`

#### Purpose

- Product detail screen

#### Logic

- loads selected item details
- shows product images in slider
- shows attributes/specifications
- quantity increase/decrease
- add to cart
- buy now

#### Important Backend Actions

##### Add to Cart

- writes to `users/{uid}/cart/{productId}`
- stores quantity and timestamp

##### Buy Now

- opens `RazorPayActivity` with product details

#### Viva Explanation

- "This screen combines product presentation with transactional actions like add-to-cart and buy-now."

---

### 5.19 `CartFragment.kt`

#### Purpose

- Shows cart items of current user

#### Logic

- reads user's cart collection
- joins cart product ids with Firestore product data
- calculates total cost
- supports buy flow

#### Viva Explanation

- "CartFragment acts like a temporary order preparation screen."

---

### 5.20 `RazorPayActivity.kt`

#### Purpose

- Checkout / order placement screen

#### Current Demo Logic

- It is not using live payment gateway in demo flow
- User enters address and contact details
- On confirm, Firestore order document is created

#### Why this is useful

- Stable for exams
- avoids payment API key issues
- still demonstrates order workflow

#### Viva Explanation

- "I converted the payment screen into a demo order placement flow so the full ecommerce process can be shown without payment dependency."

---

### 5.21 `MyOrdersFragment.kt`

#### Purpose

- Shows current user’s order history

#### Logic

- reads `users/{uid}/orders`
- listens in real time using snapshot listener
- uses adapter to display orders

---

### 5.22 `SMCreatePostFragment.kt`

#### Purpose

- Create social/community posts

#### Logic

- user selects category
- enters title and description
- Firestore post document is saved
- current user document gets post ID in array

#### Post Fields

- userID
- name
- title
- description
- timeStamp
- category
- likes
- likedBy
- commentsCount

#### Viva Explanation

- "This fragment enables farmer-to-farmer knowledge sharing."

---

### 5.23 `SocialMediaPostsFragment.kt`

#### Purpose

- Displays post feed

#### Logic

- snapshot listener on `posts`
- newest posts first
- empty state message if no post exists
- floating button opens create-post screen

---

### 5.24 `SMPostListAdapter.kt`

#### Purpose

- Binds each social post card
- handles interactions directly

#### Important Features

##### Safe Binding

- avoids crash when optional fields are missing

##### Like System

- checks whether current user already liked the post
- updates:
  - `likes`
  - `likedBy`

##### Comments

- creates comment under `posts/{postId}/comments`
- increments `commentsCount`

#### Viva Explanation

- "This adapter is more than a display adapter; it also handles Firestore interactions like like and comment updates."

---

### 5.25 `UserDataViewModel.kt`

#### Purpose

- loads user profile
- updates city/about
- deletes post ownership reference

#### Important Functions

##### `setUser(uid)`

- loads user Firestore document

##### `updateMap(userId, data)`

- updates profile fields

##### `deletePost(postId, userId)`

- deletes post document
- removes post id from user document array

---

### 5.26 `UserFragment.kt`

#### Purpose

- user profile screen

#### Logic

- loads current user data
- toggles edit mode
- saves city/about
- shows own posts
- delete own post

#### Viva Explanation

- "This fragment demonstrates user-specific Firestore data handling."

---

### 5.27 `AgriNewsViewModel.kt`

#### Purpose

- fetches agriculture headlines

#### Logic

- downloads RSS feed from Google News
- parses title/source/date
- posts list into LiveData
- fallback headlines are provided if network parsing fails

#### Viva Explanation

- "I used an RSS-based live news feed so the app gets current agriculture headlines without requiring a separate paid API key."

---

### 5.28 Important Adapters

#### `EcommerceAdapter.kt`

- Binds product image, title, retailer, price, and rating

#### `DashboardEcomItemAdapter.kt`

- Binds compact product cards on home screen

#### `CartItemsAdapter.kt`

- Binds cart row, updates quantity, removes items, supports buying

#### `MyOrdersAdapter.kt`

- Binds order data and reloads product info

#### `WeatherAdapter.kt`

- Binds daily weather rows

#### `CurrentWeatherAdapter.kt`

- Binds short interval forecast cards

#### `YojnaAdapter.kt`

- Binds scheme list item with title/image/status

#### `ArticleListAdapter.kt`

- Binds article title and image in list/grid form

---

### 5.29 Utility Files

#### `ViewUtils.kt`

- helper extension functions:
  - `toast()`
  - `show()`
  - `hide()`

#### `CellClickListener.kt`

- generic click callback interface

#### `CartItemBuy.kt`

- interface for buy action from list items

---

### 5.30 Legacy / Placeholder Files

These files exist but are not the strongest part of the final active demo flow:

- `PrePaymentFragment.kt`
- `PaymentFragment.kt`
- `MachineFragment.kt`
- `APMCRepository.kt`
- `ArticleRepository.kt`
- `WeatherRepository.kt`
- `SocialMediaViewModel.kt` (older/less central in current flow)

In viva, if asked, say:

- "Some files are older experimental or placeholder parts from the original project structure. The active and tested flow uses the fragment/viewmodel classes I have explained above."

---

## 6. DATABASE / API INTEGRATION

### Main Database Used

- **Firebase Firestore**

### Authentication Used

- **Firebase Authentication**

### Firestore Collections and Paths

#### `users`

Stores user profile data:

- name
- email
- mobile number
- city
- profile image
- about
- posts array

#### `users/{uid}/cart`

Stores cart items for each user

#### `users/{uid}/orders`

Stores orders for each user

#### `posts`

Stores social media/community posts

#### `posts/{postId}/comments`

Stores comments for each post

#### `products`

Stores ecommerce products

#### `yojnas`

Stores government schemes

#### Article Collections

- `article_plants`
- `article_methods`
- `article_diseases`
- `article_fruits`

### API Usage

#### 1. OpenWeather API

Used in:

- `WeatherApi.kt`
- `WeatherViewModel.kt`

Purpose:

- forecast
- temperature
- humidity
- wind
- icon

#### 2. Government APMC API

Used in:

- `APMCApi.kt`
- `ApmcFragment.kt`

Purpose:

- mandi/market prices
- commodity details
- min and max price

#### 3. Google News RSS Feed

Used in:

- `AgriNewsViewModel.kt`

Purpose:

- current agriculture-related headlines

### Data Handling Process

#### Firestore Data Handling

1. View/Fragment calls ViewModel or Firebase directly
2. Query is executed
3. Data is converted to map/model/list
4. LiveData or adapter is updated
5. UI refreshes

#### API Data Handling

1. Retrofit or manual network call sends request
2. JSON/XML response is received
3. It is parsed into models
4. LiveData stores the result
5. Fragment observes and updates screen

---

## 7. UI/UX DESIGN

### Layout Explanation

The app uses XML layouts for:

- Activities
- Fragments
- RecyclerView items
- custom card shapes
- button backgrounds

### Design Principles Used

- Material-style input fields and buttons
- navigation drawer + bottom navigation
- RecyclerView for scalable lists
- ViewPager2 for intro and image slider
- section-based dashboard
- card-based content organization

### Home Screen Design

- Hero weather card
- content category shortcuts
- agri news section
- products preview
- modern card backgrounds and soft spacing

### Authentication Design

- simple form layout
- clear call-to-action buttons
- background visual

### Ecommerce Design

- product cards
- category chips
- detail page with image slider and action buttons

### Social Media Design

- feed layout
- create button
- inline likes/comments

### UX Considerations

- fallback data where live API may fail
- empty state messages
- guarded navigation
- profile edit/save flow
- location button instead of forcing auto location every time

---

## 8. FEATURES

### Complete Feature List

- Intro/onboarding
- User signup
- User login
- Password reset
- Session management
- Dashboard home screen
- Live weather forecast
- Use my location for weather
- Mandi / APMC prices
- Agriculture article categories
- Article detail reading
- Government yojana listing
- Yojana details
- Ecommerce product listing
- Product filtering
- Product details
- Add to cart
- Cart quantity update
- Remove from cart
- Demo order placement
- Order history
- Social media feed
- Create categorized posts
- Like posts
- Comment on posts
- Profile view
- Profile edit
- View own posts
- Delete own post
- Agri news feed

### How Features Work Internally

#### Signup/Login

- Firebase Authentication validates credentials
- Firestore stores extra profile info

#### Weather

- location -> coordinates -> OpenWeather API -> LiveData -> UI

#### APMC

- district selection -> government API -> grouped data -> list

#### Articles

- category selection -> Firestore collection load -> detail screen

#### Yojana

- Firestore scheme data -> list -> detail page

#### Ecommerce

- Firestore products -> product detail -> cart -> order in Firestore

#### Social

- Firestore posts -> like/comment sub-updates -> feed refresh

#### Profile

- user document load -> edit fields -> save update

---

## 9. ERROR HANDLING

### Possible Errors

- user enters empty login/signup fields
- passwords do not match
- Firebase auth failure
- Firestore collection is empty
- weather API fails
- APMC API fails
- internet unavailable
- location unavailable
- product fields are missing
- post fields are missing

### How They Are Handled

#### Authentication Errors

- validation checks in `AuthViewModel`
- Toast messages for missing/invalid fields
- Firebase failure messages returned through listener

#### Empty Database Handling

- sample data is seeded in:
  - articles
  - yojanas
  - products

#### API Failure Handling

- APMC uses fallback sample prices
- Agri News uses fallback headlines
- weather uses fallback Mumbai location if no location is available

#### Null Safety Improvements

Many later fixes improved:

- profile image loading
- post field binding
- weather fallback
- product detail safety
- adapter binding safety

### Viva Point

- "I handled both validation errors and runtime data-availability issues to make the app demo-safe."

---

## 10. TESTING

### How the App Was Tested

- Manual testing in Android Studio
- Manual testing on physical Android phone
- Module-by-module testing
- Build verification through Gradle assemble

### Main Testing Areas

- signup and login
- weather loading
- location update
- APMC dropdown and result
- article list and detail open
- yojana list and detail open
- ecommerce product flow
- add to cart
- place order
- order history
- create post
- like/comment
- edit profile

### Sample Test Cases

#### Test Case 1: Signup

- Input valid user details
- Expected:
  - account is created
  - Firestore user document is created
  - dashboard opens

#### Test Case 2: Invalid Signup

- Leave fields empty
- Expected:
  - validation message shown
  - signup not performed

#### Test Case 3: Login

- Enter valid email/password
- Expected:
  - user logged in
  - dashboard opens

#### Test Case 4: Weather API

- Open weather screen with internet
- Expected:
  - forecast loads

#### Test Case 5: Weather Without Location

- Location unavailable
- Expected:
  - fallback city weather shown

#### Test Case 6: APMC API Failure

- API not available / bad internet
- Expected:
  - fallback market records shown

#### Test Case 7: Add To Cart

- Open product
- click add to cart
- Expected:
  - item stored in user cart

#### Test Case 8: Demo Order

- Open cart/product
- place demo order
- Expected:
  - order saved in Firestore
  - visible in My Orders

#### Test Case 9: Create Social Post

- Enter title/description/category
- Expected:
  - post appears in feed

#### Test Case 10: Like/Comment

- Like a post and add comment
- Expected:
  - count updates
  - comment stored

---

## 11. DEPLOYMENT

### Steps to Run the Project

1. Open project in Android Studio
2. Connect Firebase project
3. Add correct `google-services.json` in:
   - `app/google-services.json`
4. Sync Gradle
5. Make sure internet permission is available
6. Run on emulator or physical device

### Important Firebase Setup Steps

- Create Firebase project
- Add Android app with correct package name
- enable Email/Password sign-in
- download `google-services.json`
- place file inside app module
- create Firestore database

### APK Generation Process

#### Debug APK

- In Android Studio:
  - Build -> Build APK(s)
- Or via Gradle:

```powershell
.\gradlew.bat assembleDebug
```

Generated path:

- `C:\Users\dipak\Farming-App\app\build\outputs\apk\debug\app-debug.apk`

#### Release APK (general process)

- Build -> Generate Signed Bundle/APK
- choose APK
- create keystore
- select release build variant

### Exam Demo Advice

- Keep internet ON
- keep location ON
- allow app location permission
- log in once before demo
- test home screen before showing

---

## 12. LIMITATIONS & FUTURE SCOPE

### Current Limitations

- Some legacy files remain from original GitHub project
- Google Sign-In is not the main configured auth path
- Payment is demo order, not live payment
- Firebase Storage upload is not active in final demo flow
- Some dependencies are present but not fully used:
  - Room
  - Realtime Database
  - Kodein-related setup
- Some architecture parts are mixed rather than fully clean MVVM
- Some data is demo-seeded for stability

### Future Improvements

- Add fully live payment integration
- Add real product inventory management
- Add admin dashboard/web panel
- Add image upload for posts/profile
- Add push notifications
- Add multilingual support
- Add crop recommendation using ML
- Add disease detection from plant images
- Add chatbot for agriculture help
- Add live scheme/news APIs with caching
- Refactor whole project into cleaner strict MVVM + repository pattern

---

## 13. VIVA QUESTIONS & ANSWERS

Below are sample viva questions with short and strong answers.

### 1. What is the name of your project?

- My project is **Agri India**, an Android application for farmers.

### 2. What is the main objective of your app?

- The objective is to provide weather, market prices, articles, schemes, ecommerce, and community interaction in one agriculture-focused application.

### 3. Which programming language did you use?

- I used **Kotlin** for Android logic and **XML** for UI design.

### 4. Which database is used in your project?

- The main database is **Firebase Firestore**.

### 5. Which authentication method is used?

- I used **Firebase Authentication** with email and password.

### 6. Which architecture pattern is followed?

- The project mostly follows **MVVM architecture**, though some parts still have direct Firebase access in fragments/adapters.

### 7. Why did you choose Firebase?

- Firebase provides quick backend setup, authentication, cloud database, and real-time updates, which is useful for mobile projects.

### 8. How does signup work in your app?

- Signup uses Firebase Authentication to create the user account, then stores extra user profile fields in Firestore.

### 9. How does login work in your app?

- Login uses Firebase Authentication with email and password. On success, the user is redirected to the dashboard.

### 10. Which APIs have you used?

- OpenWeather API, Government APMC API, and a Google News RSS feed for agri headlines.

### 11. Why does your app show Mumbai weather sometimes?

- Mumbai is used as a fallback location when the device location is not available, so the weather module never appears blank.

### 12. How does the weather module work?

- The app gets location coordinates, calls OpenWeather API through Retrofit, then shows temperature, humidity, wind, and forecast.

### 13. How does the APMC module work?

- The user selects a state and district, the app calls the government market-price API, and data is grouped market-wise. If API fails, fallback demo data is shown.

### 14. Why did you use fallback data?

- Fallback data improves reliability, especially during demos and unstable network conditions.

### 15. How is ecommerce implemented?

- Products are stored in Firestore, users can view details, add products to cart, and place demo orders that are also saved in Firestore.

### 16. Why is Razorpay not used as live payment?

- For exam/demo stability, I changed the screen to demo order placement so the full purchase flow can still be shown without payment gateway risk.

### 17. How does the social media module work?

- Users create posts stored in Firestore. Posts support categories, likes, and comments. Comments are stored in subcollections.

### 18. How are likes handled?

- Each post stores a `likedBy` array and a `likes` count. Firestore array updates and counters are used for like/unlike.

### 19. How does the profile screen work?

- It loads current user data from Firestore, allows editing city/about, and displays user-specific posts.

### 20. What are the main collections in Firestore?

- `users`, `products`, `posts`, `yojnas`, article collections, and per-user subcollections like `cart` and `orders`.

### 21. What is the role of ViewModel in your app?

- ViewModel stores UI-related data and survives configuration changes. It separates business logic from the UI layer.

### 22. What is Retrofit?

- Retrofit is a type-safe HTTP client for Android used to connect to REST APIs and convert responses into Kotlin model classes.

### 23. What is LiveData?

- LiveData is an observable data holder. When data changes in ViewModel, the UI observes and updates automatically.

### 24. What are RecyclerViews used for in your project?

- RecyclerViews are used for product lists, weather forecast lists, post feeds, orders, schemes, and article lists.

### 25. What is data binding?

- Data binding connects UI components in XML directly with ViewModel variables and methods, reducing boilerplate code.

### 26. What are the limitations of your project?

- Some features are demo-safe rather than fully production-ready, such as demo payment and disabled media upload.

### 27. What future improvements do you want to make?

- Real payment integration, image upload, notifications, chatbot, crop recommendation, and a cleaner architecture refactor.

### 28. Why is your app useful for farmers?

- Because it combines multiple agricultural services in one place and reduces the need to use many different apps.

### 29. Why did you customize ecommerce for Nashik farmers?

- Nashik is a major farming region with grapes, onions, irrigation needs, and crop protection demands, so customizing products makes the app more region-relevant.

### 30. How did you make the project exam-safe?

- I added fallback handling, seeded data, null-safety fixes, demo order flow, and safer UI binding so the app remains functional during presentation.

---

## 14. SUMMARY

### Short Conclusion You Can Speak in Exam

- "My project is Agri India, an Android application developed in Kotlin using Android Studio."
- "The main aim of the app is to provide important services for farmers in one place, such as weather forecasting, mandi prices, government schemes, agriculture articles, ecommerce, and farmer community interaction."
- "I used Firebase Authentication for login and signup, Firestore as the cloud database, OpenWeather API for weather, Government APMC API for market prices, and a live agri news feed for headlines."
- "The application mainly follows MVVM architecture and uses RecyclerView, ViewModel, LiveData, Retrofit, and data binding."
- "I also added fallback data and safer error handling so the app remains stable during demo conditions."
- "Overall, this project is useful because it combines information, shopping, and communication features for farmers in a single mobile platform."

---

## APPENDIX A - FULL CODEBASE MAP

This section helps if the examiner asks, "What are all the important files in your project?"

### Activities

- `IntroActivity.kt` - onboarding flow
- `LoginActivity.kt` - login
- `SignupActivity.kt` - registration
- `DashboardActivity.kt` - main container activity
- `RazorPayActivity.kt` - demo checkout/order placement

### Dashboard/Home

- `DashboardFragment.kt` - home screen
- `AgriNewsViewModel.kt` - live agri headlines
- `AgriNewsAdapter.kt` - headline list binding

### Authentication

- `AuthViewModel.kt`
- `AuthRepository.kt`
- `AuthListener.kt`

### Weather

- `WeatherFragment.kt`
- `WeatherViewModel.kt`
- `WeatherApi.kt`
- `WeatherRepository.kt` - legacy helper
- `WeatherListener.kt`
- `CurrentWeatherAdapter.kt`
- `WeatherAdapter.kt`
- model files:
  - `Weather.kt`
  - `WeatherMain.kt`
  - `WeatherList.kt`
  - `WeatherRootList.kt`
  - `WeatherWind.kt`

### APMC

- `ApmcFragment.kt`
- `APMCApi.kt`
- `APMCRepository.kt` - placeholder
- `ApmcAdapter.kt`
- model files:
  - `APMCMain.kt`
  - `APMCRecords.kt`
  - `APMCCustomRecords.kt`

### Articles

- `ArticleListFragment.kt`
- `FruitsFragment.kt`
- `MachineFragment.kt` - placeholder
- `ArticleViewModel.kt`
- `ArticleRepository.kt` - older repository
- `ArticleListener.kt`
- `ArticleListAdapter.kt`

### Yojanas

- `YojnaListFragment.kt`
- `YojnaFragment.kt`
- `YojnaViewModel.kt`
- `YojnaAdapter.kt`

### Ecommerce

- `EcommerceFragment.kt`
- `EcommerceItemFragment.kt`
- `CartFragment.kt`
- `MyOrdersFragment.kt`
- `PaymentFragment.kt` - legacy
- `PrePaymentFragment.kt` - legacy
- `EcommViewModel.kt`
- `EcommerceAdapter.kt`
- `DashboardEcomItemAdapter.kt`
- `CartItemsAdapter.kt`
- `MyOrdersAdapter.kt`
- `EcommImageSliderAdapter.kt`
- `AttributesNormalAdapter.kt`
- `AttributesSelectionAdapter.kt`
- data files:
  - `CartItem.kt`
  - `orders.kt`

### Social Media

- `SocialMediaPostsFragment.kt`
- `SMCreatePostFragment.kt`
- `SMPostListAdapter.kt`
- `SocialMediaViewModel.kt` - older/helper structure
- `Post.kt`

### User/Profile

- `UserFragment.kt`
- `UserDataViewModel.kt`
- `UserProfilePostsViewModel.kt`
- `PostListUserProfileAdapter.kt`

### Utilities

- `ViewUtils.kt`
- `CellClickListener.kt`
- `CartItemBuy.kt`
- `PaginationListener.kt`

### Layout Groups

#### Activity Layouts

- `activity_intro.xml`
- `activity_login.xml`
- `activity_signup.xml`
- `activity_dashboard.xml`
- `activity_razor_pay.xml`

#### Main Fragment Layouts

- `fragment_dashboard.xml`
- `fragment_weather.xml`
- `fragment_apmc.xml`
- `fragment_article_list.xml`
- `fragment_fruits.xml`
- `fragment_yojna_list.xml`
- `fragment_yojna.xml`
- `fragment_ecommerce.xml`
- `fragment_ecommerce_item.xml`
- `fragment_cart.xml`
- `fragment_my_orders.xml`
- `fragment_social_media_posts.xml`
- `fragment_s_m_create_post.xml`
- `fragment_user.xml`

#### Item Layouts

- `single_dashboard_ecomm_item.xml`
- `single_agri_news_item.xml`
- `single_ecomm_item.xml`
- `single_cart_item.xml`
- `single_myorder_item.xml`
- `single_currentweather.xml`
- `single_weather.xml`
- `single_yojna_list.xml`
- `article_list_single.xml`
- `post_with_image_sm.xml`
- `user_profile_posts_single.xml`
- `apmc_single_list.xml`

---

## APPENDIX B - STRONG DEMO ORDER FOR EXAM

If you want to show the app smoothly, use this order:

1. Login
2. Home screen
3. Weather
4. APMC
5. Articles
6. Yojanas
7. Ecommerce
8. Add to cart
9. Demo order
10. My Orders
11. Social Media
12. Create post
13. Like/comment
14. Profile edit

---

## APPENDIX C - ONE-LINE PROJECT INTRODUCTION

You can say this in one line:

- "Agri India is a farmer-support Android app that integrates weather, mandi prices, schemes, learning resources, ecommerce, and community interaction using Kotlin, Firebase, and external APIs."
