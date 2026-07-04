# Agri India / Farming App - Complete Project Documentation for Exam and Viva

## 1. PROJECT OVERVIEW

### App Name
- **Agri India**
- The Gradle root project name is **Farming App**
- The Android application package name is **`com.project.farmingapp`**

### Objective of the App
- To provide a **single Android application** that combines multiple farmer-support services.
- To reduce the need for farmers to install different apps for:
  - weather forecast
  - mandi/APMC price checking
  - agriculture articles
  - government schemes (Yojanas)
  - e-commerce for farm products
  - farmer community/social media

### Problem It Solves
- Farmers often need to use **multiple apps and websites** for daily farming decisions.
- Important agricultural information is scattered across many platforms.
- Small and medium farmers may not have a simple, unified digital tool.
- This app solves that by giving **one-stop access** to:
  - information
  - awareness
  - community interaction
  - products
  - market updates

### Target Users
- Farmers in India
- Agriculture students
- Rural users interested in farming information
- Teachers/examiners as a demo audience for a smart agriculture application

---

## 2. TECHNOLOGIES USED

### Languages
- **Kotlin**: main application logic
- **XML**: UI layouts
- **Gradle (Groovy DSL)**: build configuration

### Frameworks and Libraries
- **Firebase Authentication**
  - email/password login and registration
- **Firebase Firestore**
  - users
  - posts
  - products
  - cart
  - orders
  - articles
  - yojanas
- **Firebase Storage**
  - dependency is present, but active exam/demo flow avoids storage upload
- **Firebase Realtime Database**
  - dependency is present, but the current active logic mainly uses Firestore
- **Retrofit**
  - OpenWeather API integration
  - APMC data.gov.in integration
- **Gson Converter**
  - JSON parsing for Retrofit APIs
- **Android Lifecycle / ViewModel / LiveData**
  - state and data communication
- **Kotlin Coroutines**
  - used in Agri News feed loading
- **Glide**
  - image loading from URLs
- **Picasso**
  - dependency present, Glide is used more actively
- **Google Play Services Location**
  - current location detection
- **Navigation Safe Args**
  - dependency included, though most navigation is handled manually with fragment transactions
- **ViewPager2**
  - intro slider and e-commerce image slider
- **Razorpay SDK**
  - dependency present, but current flow uses **demo order placement** instead of live payment

### Tools
- **Android Studio**
- **Gradle**
- **Firebase Console**
- **Git / GitHub** (project source origin)

---

## 3. ARCHITECTURE

### Architecture Pattern Used
- The project mostly follows a **hybrid MVVM architecture**
- It is not a strict clean architecture project
- Real pattern in this codebase:
  - **View**: Activities, Fragments, XML layouts
  - **ViewModel**: business/data preparation logic
  - **Model**: repositories, API services, Firestore data models

### Why It Is Called Hybrid MVVM
- `LoginActivity`, `SignupActivity`, `DashboardFragment`, `WeatherFragment`, etc. act as **Views**
- `AuthViewModel`, `WeatherViewModel`, `ArticleViewModel`, `EcommViewModel`, etc. hold application logic
- API classes like `WeatherApi.kt`, `APMCApi.kt` and Firebase repositories/model classes represent the data layer
- However, some Firebase and UI logic is still directly written inside fragments/activities, so it is **MVVM-inspired**, not fully strict MVVM

### Folder Structure Explanation

#### `app/src/main/java/com/project/farmingapp/view`
- Contains all Activities and Fragments
- Subfolders:
  - `auth`
  - `dashboard`
  - `weather`
  - `apmc`
  - `articles`
  - `yojna`
  - `ecommerce`
  - `socialmedia`
  - `user`
  - `introscreen`

#### `app/src/main/java/com/project/farmingapp/viewmodel`
- Contains ViewModels and listener interfaces
- Examples:
  - `AuthViewModel`
  - `WeatherViewModel`
  - `ArticleViewModel`
  - `EcommViewModel`
  - `YojnaViewModel`
  - `UserDataViewModel`
  - `AgriNewsViewModel`

#### `app/src/main/java/com/project/farmingapp/model`
- Contains repository and API classes
- Examples:
  - `AuthRepository`
  - `WeatherApi`
  - `WeatherRepository`
  - `APMCApi`
  - `ArticleRepository`

#### `app/src/main/java/com/project/farmingapp/model/data`
- Data model classes for API and app data
- Examples:
  - weather response classes
  - APMC response classes
  - intro slider data
  - social post/order/cart data
  - agri news model

#### `app/src/main/java/com/project/farmingapp/adapter`
- RecyclerView adapters for all list/grid UIs

#### `app/src/main/java/com/project/farmingapp/utilities`
- Common interfaces and helper extensions

#### `app/src/main/res/layout`
- XML layout files for all screens and list items

#### `app/src/main/res/drawable`
- shapes, icons, images, backgrounds

### Data Flow (Step-by-Step)

#### Example 1: Login Flow
1. User enters email and password in `activity_login.xml`
2. `LoginActivity` binds the UI with `AuthViewModel`
3. Login button triggers `AuthViewModel.loginButtonClicked()`
4. `AuthViewModel` validates inputs
5. It calls `AuthRepository.logInWithEmail()`
6. `FirebaseAuth.signInWithEmailAndPassword()` runs
7. Result returns through `LiveData<String>`
8. `LoginActivity` observes the result
9. On success, it opens `DashboardActivity`

#### Example 2: Weather Flow
1. Coordinates are stored in `WeatherViewModel`
2. `DashboardActivity` or `WeatherFragment` updates coordinates
3. `WeatherViewModel.updateNewData()` calls `WeatherApi`
4. OpenWeather returns forecast JSON
5. Retrofit parses it into `WeatherRootList`
6. `LiveData` updates UI observers
7. `DashboardFragment` and `WeatherFragment` display current and future weather

#### Example 3: E-Commerce Flow
1. `EcommViewModel.loadAllEcommItems()` loads product documents from Firestore
2. `EcommerceFragment` observes product list
3. User taps a product card
4. `EcommerceItemFragment` loads product details
5. User adds the item to cart
6. Cart data is saved in Firestore: `users/{uid}/cart`
7. User opens Cart / Buy flow
8. `RazorPayActivity` saves a demo order to `users/{uid}/orders`
9. `MyOrdersFragment` listens to Firestore and displays the order

---

## 4. MODULE-WISE EXPLANATION

### 4.1 Intro Module

#### Purpose
- Shows introductory slides when the app is opened for the first time

#### UI Design
- `activity_intro.xml`
- Uses `ViewPager2` with indicators and a `Next / Get Started` button

#### Backend Logic
- `IntroActivity.kt`
- Stores first-time flag in `SharedPreferences` (`MyPrefs`)

#### Key Classes
- `IntroActivity.kt`
- `IntroAdapter.kt`
- `IntroData.kt`

---

### 4.2 Authentication Module

#### Purpose
- User registration
- User login
- Password reset
- Optional Google sign-in support logic exists, but active demo mainly uses email/password

#### UI Design
- `activity_login.xml`
  - background image
  - Material text input fields
  - login button
  - forgot password
  - create account link
- `activity_signup.xml`
  - name, mobile, email, city, password, confirm password

#### Backend Logic
- `AuthViewModel`
  - validates fields
  - sends signup/login requests
- `AuthRepository`
  - communicates with FirebaseAuth
  - creates Firestore user document after registration

#### Key Classes and Methods
- `AuthViewModel.signupButtonClicked()`
- `AuthViewModel.loginButtonClicked()`
- `AuthRepository.signInWithEmail()`
- `AuthRepository.logInWithEmail()`
- `FirebaseAuth.sendPasswordResetEmail()`

---

### 4.3 Dashboard / Home Module

#### Purpose
- Main landing screen after login
- Shows weather summary, article categories, agri news, and featured products

#### UI Design
- `activity_dashboard.xml`
  - drawer layout
  - included `app_bar_main`
  - navigation drawer
- `app_bar_main.xml`
  - main `FrameLayout` for fragments
  - bottom navigation
- `fragment_dashboard.xml`
  - custom modernized home design
  - weather hero card
  - article category cards
  - agri news horizontal recycler
  - products horizontal recycler

#### Backend Logic
- `DashboardActivity`
  - controls navigation
  - checks intro/login state
  - manages drawer + bottom navigation
  - loads user data
  - handles location and permission
- `DashboardFragment`
  - reads weather from `WeatherViewModel`
  - reads products from `EcommViewModel`
  - reads agri news from `AgriNewsViewModel`

#### Key Classes and Methods
- `DashboardActivity.automatedClick()`
- `DashboardActivity.getLocation()`
- `DashboardActivity.updateWeatherFromLocation()`
- `DashboardFragment.observeDashboardData()`
- `DashboardFragment.setupClicks()`

---

### 4.4 Weather Module

#### Purpose
- Shows forecast based on latitude/longitude
- Can use default city or current device location

#### UI Design
- `fragment_weather.xml`
  - city title
  - use my location button
  - horizontal current weather list
  - vertical forecast list

#### Backend Logic
- `WeatherViewModel`
  - stores coordinates
  - fetches forecast using Retrofit
- `WeatherFragment`
  - observes forecast LiveData
  - splits data into current-day and daily forecast

#### Key Classes and Methods
- `WeatherViewModel.updateCoordinates()`
- `WeatherViewModel.updateNewData()`
- `WeatherFragment.renderForecasts()`
- `WeatherApi.weatherInstances.getWeather()`

---

### 4.5 APMC Module

#### Purpose
- Displays mandi/APMC crop prices

#### UI Design
- `fragment_apmc.xml`
  - state spinner
  - district spinner
  - list of grouped market price records

#### Backend Logic
- `ApmcFragment`
  - builds state-district mapping locally
  - calls `APMCApi.getSomeData(district)`
  - groups records by market
  - shows fallback sample records if API fails

#### Key Classes and Methods
- `ApmcFragment.setupSpinners()`
- `ApmcFragment.getApmc()`
- `ApmcFragment.groupRecords()`
- `ApmcFragment.sampleRecords()`
- `APMCApi.apmcInstances.getSomeData()`

---

### 4.6 Articles Module

#### Purpose
- Provides agricultural learning content by category

#### UI Design
- `fragment_article_list.xml`
  - recycler view grid of articles
- `article_list_single.xml`
  - article card with image and title
- `fragment_fruits.xml`
  - full details of selected article

#### Backend Logic
- `ArticleViewModel`
  - loads Firestore collection for selected category
  - seeds sample article if collection is empty
- `ArticleListFragment`
  - lists category-specific articles
- `FruitsFragment`
  - renders article details such as description, process, soil, diseases, attributes

#### Article Categories Used
- `article_plants`
- `article_methods`
- `article_diseases`
- `article_fruits`

#### Key Methods
- `ArticleViewModel.getAllArticles()`
- `ArticleViewModel.seedSampleArticles()`
- `FruitsFragment.renderSelectedArticle()`

---

### 4.7 Yojana Module

#### Purpose
- Shows agriculture schemes and government support information

#### UI Design
- `fragment_yojna_list.xml`
  - list of schemes
- `single_yojna_list.xml`
  - scheme list item
- `fragment_yojna.xml`
  - scheme details page

#### Backend Logic
- `YojnaViewModel`
  - loads schemes from Firestore
  - seeds sample yojanas if empty
- `YojnaListFragment`
  - shows scheme list
- `YojnaFragment`
  - shows description, launch year, ministry, budget, eligibility, documents, objective, website

#### Key Methods
- `YojnaViewModel.getAllYojna()`
- `YojnaViewModel.seedSampleYojnas()`
- `YojnaViewModel.getYojna()`

---

### 4.8 E-Commerce Module

#### Purpose
- Allows browsing of agricultural products
- Supports cart and demo orders

#### UI Design
- `fragment_ecommerce.xml`
  - category chips
  - product list
- `single_ecomm_item.xml`
  - product list card
- `fragment_ecommerce_item.xml`
  - product detail page
- `fragment_cart.xml`
  - cart list
- `activity_razor_pay.xml`
  - address form and place order button
- `fragment_my_orders.xml`
  - list of orders

#### Backend Logic
- `EcommViewModel`
  - loads products from Firestore
  - seeds sample products if collection is empty
  - now contains Nashik-focused catalog
- `EcommerceFragment`
  - filter products by chip type
- `EcommerceItemFragment`
  - loads selected product details
  - add to cart
  - buy now
- `CartFragment`
  - listens to `users/{uid}/cart`
  - computes total
- `RazorPayActivity`
  - used as demo checkout page
  - saves order into Firestore
- `MyOrdersFragment`
  - shows all placed demo orders

#### Product Categories in Current Build
- All
- Fertilizer
- Plant Care
- Irrigation
- Seeds
- Grapes

#### Key Methods
- `EcommViewModel.loadAllEcommItems()`
- `EcommViewModel.seedSampleProducts()`
- `EcommViewModel.getSpecificCategoryItems()`
- `EcommerceItemFragment.setupCartActions()`
- `CartFragment.addToOrders()`
- `RazorPayActivity.placeDemoOrder()`

---

### 4.9 Social Media Module

#### Purpose
- Community module for farmer-to-farmer knowledge sharing

#### UI Design
- `fragment_social_media_posts.xml`
  - list of posts
  - floating action button for new post
- `fragment_s_m_create_post.xml`
  - title, description, category, create button
- `post_with_image_sm.xml`
  - user info
  - title and description
  - category
  - like button
  - like count
  - comment input and comment count

#### Backend Logic
- `SMCreatePostFragment`
  - creates text posts
  - stores metadata in Firestore `posts`
  - updates user’s `posts` array in Firestore
- `SocialMediaPostsFragment`
  - live snapshot listener on posts collection
- `SMPostListAdapter`
  - binds post content
  - handles likes/unlikes
  - saves comments in subcollection
  - updates comment count

#### Post Features
- category selection
- like/unlike
- comment create
- live feed updates

#### Key Methods
- `SMCreatePostFragment.createTextPost()`
- `SMCreatePostFragment.savePost()`
- `SMPostListAdapter.bindLikes()`
- `SMPostListAdapter.bindComments()`

---

### 4.10 User Profile Module

#### Purpose
- Shows user profile and user’s own posts
- Allows editing basic profile details

#### UI Design
- `fragment_user.xml`
  - background image
  - profile image
  - user name, city, email
  - about section
  - edit/save icons
  - user posts recycler

#### Backend Logic
- `UserDataViewModel`
  - get user profile
  - update city/about
  - delete post
- `UserProfilePostsViewModel`
  - get current user posts from Firestore
- `UserFragment`
  - loads profile and posts
  - toggles edit mode
  - saves profile edits
  - allows deleting own post

#### Key Methods
- `UserDataViewModel.getUserData()`
- `UserDataViewModel.updateUserField()`
- `UserDataViewModel.deleteUserPost()`
- `UserProfilePostsViewModel.getAllPosts()`

---

### 4.11 Agri News Module

#### Purpose
- Shows fresh agriculture-related news headlines on the home screen

#### UI Design
- `single_agri_news_item.xml`
  - headline
  - source
  - date
  - tap to open

#### Backend Logic
- `AgriNewsViewModel`
  - fetches Google News RSS agriculture feed
  - parses RSS XML manually
  - uses fallback local headlines if fetching fails
- `AgriNewsAdapter`
  - opens article link in browser

#### Key Methods
- `AgriNewsViewModel.loadAgriNews()`
- `AgriNewsViewModel.fetchGoogleNewsRss()`
- `AgriNewsViewModel.parseRssFeed()`
- `AgriNewsViewModel.fallbackNews()`

---

## 5. CODE EXPLANATION (VERY IMPORTANT)

This section explains the **important files line-by-line in grouped form** so it is easy to speak in viva.

### 5.1 `app/build.gradle`

#### What this file does
- Applies plugins
- Sets Android SDK versions
- Enables Data Binding
- Adds all required libraries

#### Important logic by block
- `apply plugin: 'com.android.application'`
  - tells Gradle this is an Android app module
- `apply plugin: 'kotlin-android'`
  - enables Kotlin for Android
- `apply plugin: 'com.google.gms.google-services'`
  - enables Firebase config processing
- `android { ... }`
  - compile SDK = 33
  - min SDK = 21
  - target SDK = 33
  - release build disables minify
  - data binding is enabled
- `dependencies { ... }`
  - Firebase for authentication/database/storage
  - Retrofit + Gson for APIs
  - Lifecycle libraries for MVVM
  - Glide/Picasso for images
  - Location services
  - Razorpay dependency

### 5.2 `AndroidManifest.xml`

#### Line-group explanation
- Declares package name
- Adds permissions:
  - internet
  - fine location
- Declares GPS hardware feature
- Registers activities:
  - `IntroActivity`
  - `RazorPayActivity`
  - `DashboardActivity`
  - `SignupActivity`
  - `LoginActivity`
- `DashboardActivity` has the launcher intent-filter, so it opens first
- Adds meta-data for Razorpay API key and fonts

### 5.3 `IntroActivity.kt`

#### Logic explanation
- Creates list of intro slides using `IntroData`
- Sets the adapter for `ViewPager2`
- Shows slide indicators
- Updates the button text:
  - `Next`
  - `Get Started`
- On completion:
  - saves `firstTime = false`
  - opens `LoginActivity`

#### Viva explanation
- “This activity is used for onboarding. It introduces features to first-time users and stores a preference so the intro is not shown again.”

### 5.4 `LoginActivity.kt`

#### Logic explanation
- Data-binding connects XML fields to `AuthViewModel`
- Checks `FirebaseAuth.currentUser`
  - if user already exists, directly opens dashboard
- `createaccountText` opens signup
- forgot password sends reset email through Firebase
- `onSuccess()` observes login result and opens dashboard

#### Viva explanation
- “The login activity uses Firebase Authentication and MVVM. Inputs are bound to the viewmodel, and the activity only reacts to success or failure states.”

### 5.5 `SignupActivity.kt`

#### Logic explanation
- Similar binding approach as login
- Redirects existing users to login
- On signup success:
  - creates Firebase Auth account
  - creates Firestore user profile
  - opens dashboard

#### Viva explanation
- “The signup screen collects personal details, validates them in the viewmodel, and stores both authentication credentials and profile information.”

### 5.6 `AuthViewModel.kt`

#### Logic explanation by section
- Variables:
  - signup fields
  - login fields
  - userType and posts list
- `signupButtonClicked()`
  - trims inputs
  - validates empty fields
  - checks mobile length
  - validates email format
  - checks password match and length
  - builds data map for Firestore
  - calls `AuthRepository.signInWithEmail()`
- `loginButtonClicked()`
  - validates login fields
  - calls repository login
- `returnActivityResult()`
  - processes Google sign-in result if enabled

#### Viva explanation
- “The ViewModel handles all validation and delegates Firebase operations to the repository.”

### 5.7 `AuthRepository.kt`

#### Logic explanation
- `signInWithEmail()`
  - creates Firebase account
  - fetches generated UID
  - stores complete profile in Firestore under `users/{uid}`
- `signInToGoogle()`
  - authenticates with Google credential
  - creates Firestore profile if user document does not already exist
- `logInWithEmail()`
  - signs in existing user with Firebase Auth

#### Viva explanation
- “This repository separates Firebase authentication/database calls from the UI layer.”

### 5.8 `DashboardActivity.kt`

#### Logic explanation by responsibility

##### User/session setup
- Loads data binding
- gets `UserDataViewModel`, `UserProfilePostsViewModel`, `WeatherViewModel`
- checks `SharedPreferences` first-time flag
- redirects to intro or login if required

##### Navigation setup
- Initializes drawer toggle
- sets navigation drawer listener
- loads default `DashboardFragment`
- handles bottom nav:
  - home
  - APMC
  - e-commerce
  - social posts

##### Drawer menu
- opens:
  - e-commerce
  - APMC
  - create post
  - social feed
  - weather
  - articles
  - my orders
  - logout

##### User profile header
- observes `userliveData`
- fills navigation header:
  - name
  - email
  - city
  - profile image
  - post count

##### Location flow
- `automatedClick()` checks GPS and permissions
- `getLocation()` requests current location using fused location provider
- `loadLastKnownLocation()` tries fallback if fresh location fails
- `updateWeatherFromLocation()` converts coordinates to city using `Geocoder`, then updates `WeatherViewModel`

#### Viva explanation
- “DashboardActivity acts as the central controller for navigation, user session handling, and location-based weather updates.”

### 5.9 `DashboardFragment.kt`

#### Logic explanation
- Initializes:
  - `WeatherViewModel`
  - `EcommViewModel`
  - `AgriNewsViewModel`
- sets default Mumbai coordinates if none exist
- loads product data and agri news
- `observeDashboardData()`
  - observes coordinates and triggers weather update
  - updates hero weather card
  - shows featured products
  - shows agri news strip
- `setupClicks()`
  - weather card -> `WeatherFragment`
  - category cards -> `ArticleListFragment` / `YojnaListFragment`

#### Viva explanation
- “The home fragment is a dashboard aggregator. It combines weather, article navigation, news, and featured products using multiple ViewModels.”

### 5.10 `WeatherViewModel.kt` and `WeatherApi.kt`

#### WeatherViewModel
- keeps `coordinates`
- stores last response in `newDataTrial`
- `updateCoordinates()` changes current city/lat/lon
- `updateNewData()` calls Retrofit API using current coordinates

#### WeatherApi
- defines Retrofit base URL for OpenWeather
- embeds API key
- `getWeather(lat, lon)` returns forecast response

#### Viva explanation
- “The weather module uses Retrofit with OpenWeather. The ViewModel stores coordinates and publishes forecast data to observers.”

### 5.11 `WeatherFragment.kt`

#### Logic explanation
- Sets screen title
- initializes recycler layouts
- button `Use My Location` calls activity location flow
- observes coordinates to refresh weather
- `renderForecasts()`
  - creates two datasets:
    - current day/hourly cards
    - daily forecast list

### 5.12 `ApmcFragment.kt`

#### Logic explanation
- initializes state and district map
- spinner 1 = state
- spinner 2 = district
- on district selection:
  - calls APMC API
  - if API fails -> fallback demo prices
- `groupRecords()`
  - groups multiple commodity records under each market

#### Viva explanation
- “The APMC module uses a government API and also has fallback sample records to keep the screen usable if the API is unavailable.”

### 5.13 `ArticleViewModel.kt`, `ArticleListFragment.kt`, `FruitsFragment.kt`

#### ArticleViewModel
- loads article collection from Firestore
- seeds sample data if collection is empty
- stores list in `message3`

#### ArticleListFragment
- gets `collectionName` and title from bundle
- observes article list
- creates recycler grid
- on click opens `FruitsFragment`

#### FruitsFragment
- reads selected article title
- finds article data in loaded list
- binds:
  - title
  - description
  - process
  - soil
  - state
  - diseases
  - attributes
- supports expand/collapse description

### 5.14 `YojnaViewModel.kt`, `YojnaListFragment.kt`, `YojnaFragment.kt`

#### YojnaViewModel
- loads schemes from Firestore
- seeds PM-KISAN and Soil Health Card examples if empty

#### YojnaListFragment
- observes yojana list
- shows linear recycler
- opens details screen on click

#### YojnaFragment
- loads scheme by document id
- fills all details
- formats list fields with numbering

### 5.15 `EcommViewModel.kt`

#### Logic explanation
- Firestore collection used: `products`
- `loadAllEcommItems()`
  - if products are not seeded, seeds sample data first
  - then loads all products
- `getSpecificCategoryItems(type)`
  - returns products filtered by category
- `getSpecificItem(id)`
  - loads one product document
- `seedSampleProducts()`
  - creates Nashik-focused demo product catalog

#### Viva explanation
- “The e-commerce data is Firestore-based and the ViewModel also creates sample products when the collection is empty, which is useful for demo reliability.”

### 5.16 `EcommerceFragment.kt`

#### Logic explanation
- observes `ecommLiveData`
- shows products in recycler
- chip selection filters categories
- clicking a product opens `EcommerceItemFragment`

### 5.17 `EcommerceItemFragment.kt`

#### Logic explanation
- gets selected product ID
- loads document from Firestore
- binds details:
  - title
  - short and long description
  - price
  - how to use
  - delivery charge
  - rating
  - images
  - attributes
- `addToCart`
  - stores quantity and timestamp in `users/{uid}/cart/{productId}`
- `buynow`
  - opens `RazorPayActivity` with product info

### 5.18 `CartFragment.kt` and `CartItemsAdapter.kt`

#### CartFragment
- listens to `users/{uid}/cart`
- converts cart docs into local `items` map
- computes total price by loading each product document
- adapter handles remove, quantity update, and buy now

#### CartItemsAdapter
- binds each cart item with product details
- updates Firestore quantity directly
- removes item from cart
- buy now calls `CartItemBuy.addToOrders()`

### 5.19 `RazorPayActivity.kt` and `MyOrdersFragment.kt`

#### RazorPayActivity
- now acts as **demo order placement screen**
- collects name, locality, city, state, pincode, mobile
- computes total price
- creates Firestore order document in `users/{uid}/orders`

#### MyOrdersFragment
- listens to `orders` subcollection
- shows empty-state if no orders
- uses adapter to display order history

### 5.20 `SMCreatePostFragment.kt`, `SocialMediaPostsFragment.kt`, `SMPostListAdapter.kt`

#### SMCreatePostFragment
- category spinner
- title and description input
- builds post document
- stores post in `posts`
- updates user’s `posts` array

#### SocialMediaPostsFragment
- live listener on `posts`
- newest posts first
- floating button opens create post screen

#### SMPostListAdapter
- displays post details
- handles image/video hiding or showing
- loads user profile image
- `bindLikes()`
  - toggles like state using `FieldValue.increment` and `arrayUnion/arrayRemove`
- `bindComments()`
  - writes comment into `posts/{postId}/comments`
  - increments `commentsCount`

### 5.21 `UserDataViewModel.kt`, `UserProfilePostsViewModel.kt`, `UserFragment.kt`

#### UserDataViewModel
- fetches user document
- updates profile fields
- deletes post and removes its ID from user document

#### UserProfilePostsViewModel
- contains several older post-loading methods
- actively useful method:
  - `getAllPosts(userId)` -> loads posts by `userID`

#### UserFragment
- observes profile document
- observes user posts
- toggles edit mode
- saves city/about
- delete confirmation for own posts

### 5.22 Supporting Adapters and Utilities

#### Important adapters
- `ArticleListAdapter` -> article cards
- `EcommerceAdapter` -> product cards
- `DashboardEcomItemAdapter` -> featured home products
- `WeatherAdapter` -> daily forecast
- `CurrentWeatherAdapter` -> current/day forecast cards
- `YojnaAdapter` -> yojana list
- `MyOrdersAdapter` -> order list
- `PostListUserProfileAdapter` -> user’s posts list
- `AgriNewsAdapter` -> home news cards

#### Utilities
- `CellClickListener.kt`
  - generic click callback interface
- `CartItemBuy.kt`
  - callback interface for buying item from adapter
- `ViewUtils.kt`
  - toast/show/hide helper extensions

---

## 6. DATABASE / API INTEGRATION

### Database Used
- **Firebase Firestore**
- **Firebase Authentication**

### Firestore Collections and Data Usage

#### `users`
- document id = Firebase UID
- stores:
  - name
  - email
  - mobNo
  - city
  - about
  - profileImage
  - backImage
  - posts array
  - userType

#### `products`
- stores e-commerce items
- title, price, retailer, availability, rating, type, description, images, attributes

#### `posts`
- social media posts
- title, description, category, userID, timeStamp, likes, likedBy, commentsCount

#### `posts/{postId}/comments`
- comment subcollection

#### `yojnas`
- government scheme data

#### `article_*`
- article categories:
  - plants
  - methods
  - diseases
  - fruits

#### `users/{uid}/cart`
- productId-based cart documents

#### `users/{uid}/orders`
- order history

### API Usage

#### OpenWeather API
- file: `WeatherApi.kt`
- used for weather forecast
- method: `getWeather(lat, lon)`

#### Government APMC API
- file: `APMCApi.kt`
- source: `https://api.data.gov.in/`
- used for mandi/market crop price data

#### Google News RSS
- file: `AgriNewsViewModel.kt`
- uses HTTP connection + XML parsing
- no API key required

### Data Handling Process
- API returns JSON or XML
- parsing is done into model classes or custom parser
- data stored in `LiveData`
- fragments observe the data
- Firestore listeners keep UI updated in real time where needed

---

## 7. UI/UX DESIGN

### Layout Style
- Mostly uses:
  - `ConstraintLayout`
  - `FrameLayout`
  - `LinearLayout`
  - `ScrollView`
  - `NestedScrollView`
  - `RecyclerView`
  - `CardView`

### Design Principles Used
- Material inputs and buttons in auth screens
- RecyclerView-based list design
- Card-based information grouping
- bottom navigation + drawer navigation
- Firebase-driven content
- home dashboard designed for quick scanning

### Screen-wise XML Notes

#### Authentication
- full-screen background image
- form-based design
- Data Binding with `AuthViewModel`

#### Dashboard
- drawer + bottom navigation
- modular fragment container

#### Home
- weather hero card
- category cards
- agri news shelf
- product shelf

#### Weather
- split current and future forecast

#### E-commerce
- filter chips + list + details + cart + orders

#### Social Media
- feed layout with FAB and interactive post cards

#### Profile
- banner image + profile card + editable fields + user posts

---

## 8. FEATURES

### Complete Feature List
- intro/onboarding screen
- email/password registration
- login and session handling
- forgot password email
- bottom navigation
- navigation drawer
- live weather forecast
- location-based weather
- APMC mandi price lookup
- agriculture article categories
- article detail screen
- government yojana list and details
- agriculture e-commerce catalog
- product filtering
- product detail page
- add to cart
- cart quantity change
- demo order placement
- order history
- social media post creation
- social feed
- post categories
- likes
- comments
- user profile
- edit city/about
- delete own posts
- agri news on home screen

### Internal Working Summary
- Firebase Authentication manages user identity
- Firestore manages dynamic app content
- Retrofit handles external JSON APIs
- custom XML parsing handles RSS feed
- LiveData links ViewModel and UI
- RecyclerViews render lists and cards

---

## 9. ERROR HANDLING

### Possible Errors and Handling

#### Login errors
- invalid email/password
- handled by Firebase failure callbacks
- message shown using toast

#### Signup errors
- empty fields
- password mismatch
- short password
- invalid email
- invalid mobile length
- handled in `AuthViewModel`

#### Weather API failure
- logs error
- dashboard keeps fallback/default city behavior

#### Location failure
- if GPS off -> alert dialog opens settings
- if no permission -> permission request
- if location not detected -> keeps default weather

#### APMC API failure
- shows sample fallback market records

#### Empty Firestore collections
- articles, yojanas, products are seeded with sample data

#### Social post issues
- if not logged in or title/description empty -> toast shown

#### Orders/cart issues
- if user not logged in -> safe return / message shown

### Strength of Error Handling
- The project is designed for **demo safety**
- Several modules use:
  - fallback data
  - empty state messages
  - safe null checks

---

## 10. TESTING

### How the App Was Tested
- manual testing in Android Studio and on physical Android device
- build verification using:
  - `.\gradlew.bat assembleDebug`
- navigation testing:
  - auth -> dashboard
  - home -> weather
  - home -> articles
  - e-commerce -> cart -> demo order
  - social media -> create -> like -> comment
  - profile -> edit -> post delete

### Sample Test Cases

#### Authentication
- valid signup
- signup with empty field
- login with valid credentials
- login with wrong password

#### Weather
- app loads default city weather
- location permission granted -> weather updates
- location denied -> app still shows default weather

#### APMC
- select valid district -> live data
- API failure -> sample data visible

#### Articles
- open category -> list visible
- click article -> details visible

#### E-commerce
- load product list
- filter by chip
- open product details
- add to cart
- update quantity
- place demo order
- order visible in My Orders

#### Social Media
- create text post
- like post
- comment on post

#### Profile
- edit city/about
- changes saved in Firestore
- post count updates

---

## 11. DEPLOYMENT

### Steps to Run the Project
1. Open the project in Android Studio
2. Sync Gradle
3. Connect Firebase project and ensure `google-services.json` exists in `app/`
4. Run on emulator or physical device
5. Ensure:
   - internet is available
   - location permission is granted if weather location is needed

### APK Generation Process
1. Android Studio -> Build -> Build APK(s)
2. Or command line:
   - `.\gradlew.bat assembleDebug`
3. Generated APK path:
   - `app/build/outputs/apk/debug/app-debug.apk`

### Phone Demo Requirements
- install latest APK
- internet ON
- location ON
- Firebase project configured
- login credentials ready

---

## 12. LIMITATIONS & FUTURE SCOPE

### Current Limitations
- Google sign-in logic exists but is not fully configured in the current demo build
- Firebase Storage image upload is not active in the demo flow
- Some classes are legacy or placeholder:
  - `MachineFragment`
  - `PaymentFragment`
  - `PrePaymentFragment`
  - `APMCRepository`
- Project uses deprecated APIs in some places:
  - `ViewModelProviders`
  - older location request patterns still remain in activity
- Architecture is hybrid MVVM, not fully clean
- Some adapters and screens still use older synthetic view access

### Future Improvements
- migrate to ViewBinding fully
- replace deprecated APIs
- use Navigation Component more consistently
- enable real payment integration
- enable real image/video post upload with Firebase Storage
- improve offline caching
- add search in articles/products/posts
- add multilingual support
- add notifications for weather and scheme updates

---

## 13. VIVA QUESTIONS & ANSWERS

### 1. What is the main objective of your project?
- To provide one Android app for farmers that combines weather, market prices, articles, schemes, e-commerce, and community interaction.

### 2. Which architecture pattern is used?
- A hybrid MVVM architecture.

### 3. Why do you call it hybrid MVVM?
- Because ViewModels are used for data/business logic, but some logic is still directly present in Activities and Fragments.

### 4. Which database is used?
- Firebase Firestore.

### 5. Which authentication method is used?
- Firebase Authentication with email/password login.

### 6. Which APIs are used in your app?
- OpenWeather API, Government APMC API, and Google News RSS feed.

### 7. How is weather data fetched?
- Using Retrofit from OpenWeather API based on latitude and longitude.

### 8. How is location handled?
- Through `FusedLocationProviderClient` in `DashboardActivity`.

### 9. How are articles stored?
- In Firestore collections like `article_plants`, `article_methods`, `article_diseases`, and `article_fruits`.

### 10. How do you handle empty Firestore collections?
- The app seeds sample data for articles, yojanas, and products.

### 11. How does the cart work?
- Cart items are stored under `users/{uid}/cart` in Firestore.

### 12. Is payment real or demo?
- In the current exam build, payment is demo-based. Orders are placed directly into Firestore without a real payment transaction.

### 13. Why did you keep demo payment?
- To make the app stable and exam-safe without requiring live payment configuration.

### 14. How does the social media module work?
- Posts are stored in Firestore, the feed listens in real time, likes update counts, and comments are stored in a subcollection.

### 15. How is the profile section implemented?
- User profile data is fetched from Firestore and displayed in `UserFragment`. City and about fields can be updated.

### 16. What is the purpose of LiveData in your project?
- It helps UI components observe data changes from ViewModels and update automatically.

### 17. What is Retrofit used for?
- For making HTTP API requests and parsing JSON into Kotlin objects.

### 18. What is the advantage of Firebase in this project?
- It provides authentication, cloud database, and easy integration with Android.

### 19. How do you handle API failures?
- By showing fallback data or safe empty messages, especially in APMC and Agri News modules.

### 20. What are the main modules of your application?
- Authentication, Dashboard, Weather, APMC, Articles, Yojana, E-commerce, Social Media, Profile, Agri News.

### 21. How is the home screen designed?
- It acts as a dashboard showing a weather hero card, article shortcuts, news headlines, and featured products.

### 22. What is stored in the `users` collection?
- Name, email, mobile number, city, about, profile image, user type, and user post IDs.

### 23. Why did you use Firestore instead of SQLite?
- Because Firestore provides cloud synchronization, easier real-time updates, and supports multi-device access.

### 24. What is the purpose of `EcommViewModel.seedSampleProducts()`?
- It creates a demo product catalog if Firestore is empty.

### 25. What makes your app suitable for farmers?
- It combines practical tools: weather, crop information, market prices, schemes, products, and farmer community support.

---

## 14. SUMMARY

### Short Conclusion for Exam
- “My project is an Android application named Agri India developed in Android Studio using Kotlin. It is designed as a one-stop solution for farmers. The app includes weather forecasting, APMC market price updates, agriculture articles, government yojana information, e-commerce features, and a social media community. The project uses Firebase Authentication and Firestore as the backend, along with external APIs like OpenWeather and Government APMC API. I followed a hybrid MVVM architecture using Activities, Fragments, ViewModels, LiveData, Retrofit, and RecyclerViews. The main goal of the project is to make agricultural information and services easily accessible through a single mobile application.”

---

## APPENDIX A - SOURCE CODE FILE INVENTORY

### Core source files under `src/main/java`

#### Root
- `PrePaymentFragment.kt` - placeholder fragment for older payment flow

#### Adapters
- `AgriNewsAdapter.kt` - binds home news cards
- `ApmcAdapter.kt` - binds grouped APMC market records
- `ArticleListAdapter.kt` - binds article cards
- `AttributesNormalAdapter.kt` - shows normal product attributes
- `AttributesSelectionAdapter.kt` - shows selectable product attributes
- `CartItemsAdapter.kt` - binds cart items and quantity changes
- `CurrentWeatherAdapter.kt` - binds current forecast cards
- `DashboardEcomItemAdapter.kt` - binds featured products on dashboard
- `EcommerceAdapter.kt` - binds e-commerce list items
- `EcommImageSliderAdapter.kt` - product image slider
- `IntroAdapter.kt` - intro slide pager adapter
- `MyOrdersAdapter.kt` - binds orders list
- `PaginationListener.kt` - pagination helper (legacy/utility)
- `PostListUserProfileAdapter.kt` - binds user’s own posts
- `SMPostListAdapter.kt` - binds social media posts
- `WeatherAdapter.kt` - binds daily weather items
- `YojnaAdapter.kt` - binds yojana list items

#### Model / API / Repository
- `APMCApi.kt` - APMC Retrofit API
- `APMCRepository.kt` - placeholder repository
- `ArticleRepository.kt` - older article repository logic
- `AuthRepository.kt` - Firebase auth repository
- `WeatherApi.kt` - OpenWeather Retrofit API
- `WeatherRepository.kt` - weather repository helper

#### Model data
- `AgriNewsItem.kt`
- `APMCCustomRecords.kt`
- `APMCMain.kt`
- `APMCRecords.kt`
- `CartItem.kt`
- `IntroData.kt`
- `orders.kt`
- `Post.kt`
- `Weather.kt`
- `WeatherList.kt`
- `WeatherMain.kt`
- `WeatherRootList.kt`
- `WeatherWind.kt`

#### Utilities
- `CartItemBuy.kt` - buy callback interface
- `CellClickListener.kt` - click callback interface
- `ViewUtils.kt` - UI helper extensions

#### View / Screens
- `view/apmc/ApmcFragment.kt`
- `view/articles/ArticleListFragment.kt`
- `view/articles/FruitsFragment.kt`
- `view/articles/MachineFragment.kt` - placeholder
- `view/auth/LoginActivity.kt`
- `view/auth/SignupActivity.kt`
- `view/dashboard/DashboardActivity.kt`
- `view/dashboard/DashboardFragment.kt`
- `view/ecommerce/CartFragment.kt`
- `view/ecommerce/EcommerceFragment.kt`
- `view/ecommerce/EcommerceItemFragment.kt`
- `view/ecommerce/MyOrdersFragment.kt`
- `view/ecommerce/PaymentFragment.kt` - placeholder
- `view/ecommerce/RazorPayActivity.kt`
- `view/introscreen/IntroActivity.kt`
- `view/socialmedia/SMCreatePostFragment.kt`
- `view/socialmedia/SocialMediaPostsFragment.kt`
- `view/user/UserFragment.kt`
- `view/weather/WeatherFragment.kt`
- `view/yojna/YojnaFragment.kt`
- `view/yojna/YojnaListFragment.kt`

#### ViewModels
- `AgriNewsViewModel.kt`
- `ArticleListener.kt`
- `ArticleViewModel.kt`
- `AuthListener.kt`
- `AuthViewModel.kt`
- `EcommViewModel.kt`
- `SocialMediaViewModel.kt` - present, but feed logic now mainly lives directly in fragment/adapter
- `UserDataViewModel.kt`
- `UserProfilePostsViewModel.kt`
- `WeatherListener.kt`
- `WeatherViewModel.kt`
- `YojnaViewModel.kt`

### Important layout files under `src/main/res/layout`
- `activity_dashboard.xml`
- `activity_intro.xml`
- `activity_login.xml`
- `activity_razor_pay.xml`
- `activity_signup.xml`
- `fragment_apmc.xml`
- `fragment_article_list.xml`
- `fragment_cart.xml`
- `fragment_dashboard.xml`
- `fragment_ecommerce.xml`
- `fragment_ecommerce_item.xml`
- `fragment_fruits.xml`
- `fragment_my_orders.xml`
- `fragment_payment.xml`
- `fragment_pre_payment.xml`
- `fragment_social_media_posts.xml`
- `fragment_s_m_create_post.xml`
- `fragment_user.xml`
- `fragment_weather.xml`
- `fragment_yojna.xml`
- `fragment_yojna_list.xml`
- item/layout files for recycler rows and cards

### Important note for viva
- Build-generated files inside `app/build/` are **not hand-written source code**
- They are automatically produced by Gradle, Data Binding, and Kotlin compilation

