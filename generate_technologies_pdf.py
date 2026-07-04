from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, ListFlowable, ListItem


OUTPUT = r"C:\Users\dipak\Farming-App\TECHNOLOGIES_USED_DETAILED.pdf"


styles = getSampleStyleSheet()
styles.add(
    ParagraphStyle(
        name="TitleCenter",
        parent=styles["Title"],
        alignment=TA_CENTER,
        fontName="Helvetica-Bold",
        fontSize=20,
        leading=24,
        spaceAfter=18,
        textColor=colors.HexColor("#1f3a2e"),
    )
)
styles.add(
    ParagraphStyle(
        name="SectionHeader",
        parent=styles["Heading2"],
        fontName="Helvetica-Bold",
        fontSize=14,
        leading=18,
        textColor=colors.HexColor("#234b3b"),
        spaceBefore=10,
        spaceAfter=8,
    )
)
styles.add(
    ParagraphStyle(
        name="SubHeader",
        parent=styles["Heading3"],
        fontName="Helvetica-Bold",
        fontSize=11,
        leading=14,
        textColor=colors.HexColor("#234b3b"),
        spaceBefore=8,
        spaceAfter=4,
    )
)
styles.add(
    ParagraphStyle(
        name="BodyJustify",
        parent=styles["BodyText"],
        fontName="Helvetica",
        fontSize=10.5,
        leading=15,
        alignment=TA_JUSTIFY,
        spaceAfter=6,
    )
)


doc = SimpleDocTemplate(
    OUTPUT,
    pagesize=A4,
    rightMargin=0.7 * inch,
    leftMargin=0.7 * inch,
    topMargin=0.7 * inch,
    bottomMargin=0.7 * inch,
)

story = []


def add_title(text):
    story.append(Paragraph(text, styles["TitleCenter"]))


def add_section(title):
    story.append(Paragraph(title, styles["SectionHeader"]))


def add_subheader(title):
    story.append(Paragraph(title, styles["SubHeader"]))


def add_para(text):
    story.append(Paragraph(text, styles["BodyJustify"]))


def add_bullets(items):
    flow = ListFlowable(
        [
            ListItem(Paragraph(item, styles["BodyJustify"]), leftIndent=8)
            for item in items
        ],
        bulletType="bullet",
        start="circle",
        leftIndent=18,
        bulletFontName="Helvetica",
        bulletFontSize=8,
    )
    story.append(flow)
    story.append(Spacer(1, 6))


add_title("Detailed Explanation of Technologies Used")
add_para(
    "This PDF explains the technologies used in the <b>Agri India / Farming App</b> project. "
    "The explanation is written in simple but technical language so it can be used during viva, teacher review, "
    "or project submission."
)

add_section("1. Programming Languages")

add_subheader("Kotlin")
add_para(
    "<b>Kotlin</b> is the main programming language used in this Android application. "
    "It is officially supported by Google for Android development and is more modern than Java in many areas."
)
add_bullets(
    [
        "Kotlin is used for the core application logic such as Activities, Fragments, ViewModels, adapters, models, and Firebase/API integration.",
        "It provides null-safety, which helps reduce common crashes such as null pointer exceptions.",
        "It supports concise syntax, so code becomes shorter and easier to maintain.",
        "It works very well with Android Jetpack libraries like ViewModel, LiveData, Navigation, and Data Binding.",
    ]
)
add_para(
    "In this project, Kotlin is used in files such as <b>DashboardActivity.kt</b>, <b>AuthViewModel.kt</b>, "
    "<b>WeatherViewModel.kt</b>, <b>EcommViewModel.kt</b>, and many other module classes."
)
add_para(
    "<b>Why we used Kotlin:</b> It makes Android code cleaner, safer, and easier to explain and maintain."
)

add_subheader("XML")
add_para(
    "<b>XML</b> is used to design the user interface layouts of the application. "
    "In Android, XML is commonly used to define visual structure separately from business logic."
)
add_bullets(
    [
        "XML layouts define TextViews, ImageViews, RecyclerViews, Buttons, Card-like sections, and form inputs.",
        "It keeps UI design separate from Kotlin code, which improves project organization.",
        "It allows easier styling, alignment, spacing, and responsive mobile design.",
    ]
)
add_para(
    "Examples from this project include <b>activity_login.xml</b>, <b>fragment_dashboard.xml</b>, "
    "<b>fragment_weather.xml</b>, and many RecyclerView item layouts."
)
add_para(
    "<b>Why we used XML:</b> It is the standard Android layout language and makes UI design modular and maintainable."
)

add_section("2. Development Tools")

add_subheader("Android Studio")
add_para(
    "<b>Android Studio</b> is the main IDE used to develop the application."
)
add_bullets(
    [
        "It provides code editor, layout editor, emulator support, logcat, and Gradle integration.",
        "It helps in building, running, debugging, and packaging the Android application.",
        "It supports Firebase integration, dependency management, APK generation, and resource handling.",
    ]
)
add_para(
    "<b>Why we used Android Studio:</b> It is the official IDE for Android development and gives full support for Kotlin, XML, Gradle, and device testing."
)

add_subheader("Gradle")
add_para(
    "<b>Gradle</b> is the build automation system used by Android Studio."
)
add_bullets(
    [
        "It manages project dependencies such as Firebase, Retrofit, Glide, Material Components, and Room.",
        "It controls SDK version, build variants, app id, plugin configuration, and APK generation.",
        "It helps convert source code and resources into an installable APK.",
    ]
)
add_para(
    "The project uses Gradle files like <b>build.gradle</b> and <b>app/build.gradle</b>."
)
add_para(
    "<b>Why we used Gradle:</b> It is required for Android builds and makes dependency and version management easier."
)

add_subheader("Firebase Console")
add_para(
    "<b>Firebase Console</b> is the cloud management dashboard used to configure backend services."
)
add_bullets(
    [
        "It is used to create the Firebase project for the app.",
        "It is used to enable Authentication providers such as Email/Password.",
        "It is used to create Firestore database and manage cloud data.",
        "It provides the <b>google-services.json</b> file needed by the Android app.",
    ]
)
add_para(
    "<b>Why we used Firebase Console:</b> It allows fast backend setup without building a custom server."
)

add_section("3. Android Libraries and Components")

add_subheader("ViewModel")
add_para(
    "<b>ViewModel</b> is an Android Architecture Component used to store and manage UI-related data in a lifecycle-aware way."
)
add_bullets(
    [
        "It keeps business logic separate from Activity and Fragment code.",
        "It survives configuration changes like screen rotation better than plain UI variables.",
        "It is used in this project for auth, weather, ecommerce, yojana, article, user, and news modules.",
    ]
)
add_para(
    "Examples: <b>AuthViewModel.kt</b>, <b>WeatherViewModel.kt</b>, <b>EcommViewModel.kt</b>, <b>YojnaViewModel.kt</b>."
)
add_para(
    "<b>Why we used ViewModel:</b> It improves structure and reduces unnecessary logic inside UI classes."
)

add_subheader("LiveData")
add_para(
    "<b>LiveData</b> is an observable data holder class used with ViewModel."
)
add_bullets(
    [
        "When data changes in the ViewModel, the UI automatically updates if it is observing that LiveData.",
        "It helps create reactive screen updates for weather, login state, products, posts, and schemes.",
        "It also respects lifecycle states, so updates are safer."
    ]
)
add_para(
    "<b>Why we used LiveData:</b> It makes UI updates cleaner and better organized than manual refresh code."
)

add_subheader("Data Binding")
add_para(
    "<b>Data Binding</b> is used to connect XML UI directly with Kotlin data and methods."
)
add_bullets(
    [
        "Form inputs in login and signup are connected to ViewModel fields using data binding.",
        "Button click methods can be triggered directly from XML.",
        "It reduces findViewById boilerplate and improves readability."
    ]
)
add_para(
    "<b>Why we used Data Binding:</b> It makes form-based screens like authentication simpler and more maintainable."
)

add_subheader("RecyclerView")
add_para(
    "<b>RecyclerView</b> is used to efficiently display scrollable lists of items."
)
add_bullets(
    [
        "Used for products, weather forecast, article lists, schemes, posts, comments area behavior, and orders.",
        "It is memory-efficient because it reuses list item views.",
        "It works with adapters to separate item design from binding logic."
    ]
)
add_para(
    "<b>Why we used RecyclerView:</b> Many modules in this app are list-based, so RecyclerView is the best standard Android choice."
)

add_subheader("ViewPager2")
add_para(
    "<b>ViewPager2</b> is used for swipeable screens."
)
add_bullets(
    [
        "Used in the intro/onboarding module.",
        "Used in the product image slider in ecommerce item detail.",
    ]
)
add_para(
    "<b>Why we used ViewPager2:</b> It provides a modern swipeable interface for multiple pages or images."
)

add_subheader("Navigation Component / Safe Args")
add_para(
    "<b>Navigation Component</b> helps with structured screen transitions in Android. "
    "<b>Safe Args</b> provides safer argument passing between destinations."
)
add_bullets(
    [
        "It improves screen navigation consistency.",
        "It reduces errors when passing data between fragments.",
        "The Gradle setup includes the Safe Args plugin."
    ]
)
add_para(
    "<b>Why we used it:</b> It supports safer fragment communication and cleaner navigation patterns."
)

add_subheader("Material Components")
add_para(
    "<b>Material Components</b> provide modern Android UI widgets such as text fields, buttons, chips, and cards."
)
add_bullets(
    [
        "Used in authentication forms, filter chips, buttons, and general UI styling.",
        "Helps the app follow Android design standards more closely.",
    ]
)
add_para(
    "<b>Why we used Material Components:</b> They make the UI more modern, consistent, and user-friendly."
)

add_section("4. Backend and Cloud Services")

add_subheader("Firebase Authentication")
add_para(
    "<b>Firebase Authentication</b> is used for user identity management."
)
add_bullets(
    [
        "It handles account creation and login for email/password users.",
        "It also supports password reset functionality.",
        "It provides a secure and easy backend auth solution without writing a custom authentication server."
    ]
)
add_para(
    "<b>Why we used it:</b> It is simple to integrate with Android and reliable for user authentication."
)

add_subheader("Cloud Firestore")
add_para(
    "<b>Cloud Firestore</b> is the main cloud database used in the project."
)
add_bullets(
    [
        "It stores user profiles, products, orders, cart items, posts, comments, schemes, and articles.",
        "It is flexible because it is document-based, which suits dynamic app features well.",
        "It supports real-time updates and easy integration with Firebase Authentication."
    ]
)
add_para(
    "Examples of collections used in the project include <b>users</b>, <b>products</b>, <b>posts</b>, <b>yojnas</b>, and article collections."
)
add_para(
    "<b>Why we used Firestore:</b> It is fast to set up, cloud-hosted, scalable for student projects, and fits mobile app development very well."
)

add_section("5. APIs and External Services")

add_subheader("OpenWeather API")
add_para(
    "<b>OpenWeather API</b> is used in the weather module."
)
add_bullets(
    [
        "It fetches forecast data using latitude and longitude.",
        "The app shows temperature, humidity, wind speed, and weather condition details.",
        "It is integrated using Retrofit in <b>WeatherApi.kt</b>."
    ]
)
add_para(
    "<b>Why we used it:</b> Farmers need weather updates for irrigation, spraying, and crop planning, so this is one of the most useful live features."
)

add_subheader("Government APMC API")
add_para(
    "<b>Government APMC API</b> is used for mandi or market price data."
)
add_bullets(
    [
        "It retrieves commodity pricing records such as minimum price, maximum price, and market information.",
        "The app filters records by district and groups them by market.",
        "It helps farmers compare rates before selling produce."
    ]
)
add_para(
    "<b>Why we used it:</b> It directly matches the app's agriculture domain and gives practical value to farmers."
)

add_subheader("Google News RSS Feed")
add_para(
    "<b>Google News RSS feed</b> is used to fetch agriculture-related headlines."
)
add_bullets(
    [
        "It provides current agri-news without requiring a dedicated paid news API key.",
        "The app parses XML feed data to extract title, source, and date.",
        "It is shown in the Home screen as the Agri News section."
    ]
)
add_para(
    "<b>Why we used it:</b> It adds freshness to the dashboard and helps the app feel more dynamic."
)

add_subheader("Google Play Services Location")
add_para(
    "<b>Google Play Services Location</b> is used to detect the device's current coordinates."
)
add_bullets(
    [
        "It is used for the weather module so the app can show local weather instead of only a fixed city.",
        "The app requests current location and then converts it into weather input coordinates.",
    ]
)
add_para(
    "<b>Why we used it:</b> Weather becomes more relevant when tied to the farmer's current location."
)

add_section("6. Image and UI Utilities")

add_subheader("Glide")
add_para(
    "<b>Glide</b> is an image loading library."
)
add_bullets(
    [
        "It loads images from URLs or resources into ImageViews.",
        "It is efficient and handles caching automatically.",
        "It is used in products, social posts, profile images, and weather icons."
    ]
)
add_para(
    "<b>Why we used Glide:</b> It simplifies image loading and improves app performance compared to manual image handling."
)

add_subheader("Picasso")
add_para(
    "<b>Picasso</b> is another image loading library used in some parts of the project."
)
add_bullets(
    [
        "It provides simple syntax for loading remote images.",
        "It appears in some earlier or existing project areas where image loading was already implemented."
    ]
)
add_para(
    "<b>Why we used Picasso:</b> It was already part of the codebase and works well for straightforward image display."
)

add_section("7. Additional Dependencies Present")

add_subheader("Firebase Storage")
add_para(
    "<b>Firebase Storage</b> is a cloud file storage service for media such as images or videos."
)
add_bullets(
    [
        "It would normally be used for uploading post images, profile images, or other user media.",
        "In the final demo-safe flow of this project, upload functionality is reduced to avoid Firebase storage configuration issues."
    ]
)
add_para(
    "<b>Why it is present:</b> The original project structure included media upload intentions, and future versions can use it fully."
)

add_subheader("Firebase Realtime Database")
add_para(
    "<b>Firebase Realtime Database</b> is another Firebase database product."
)
add_bullets(
    [
        "It stores data as a large JSON tree and supports real-time synchronization.",
        "In this project, Firestore is the actively used main database, not Realtime Database."
    ]
)
add_para(
    "<b>Why it is present:</b> It may have been included in the original dependency setup for experimentation or planned features."
)

add_subheader("Razorpay SDK")
add_para(
    "<b>Razorpay SDK</b> is a payment gateway library."
)
add_bullets(
    [
        "It is normally used for real online payment processing in ecommerce applications.",
        "In this project, the payment screen was converted into a demo order flow for stability and exam safety."
    ]
)
add_para(
    "<b>Why it is present:</b> It reflects the ecommerce payment intention of the project, even though final demonstration uses demo ordering instead of live payment."
)

add_subheader("Room")
add_para(
    "<b>Room</b> is a local SQLite abstraction library provided by Android Jetpack."
)
add_bullets(
    [
        "Room is usually used for offline local database storage in Android applications.",
        "In this project, Firestore is the primary active data source, so Room is not central in the final implemented flow."
    ]
)
add_para(
    "<b>Why it is present:</b> It may have been added for offline-storage planning or from the original project setup."
)

add_section("8. Final Viva Summary")
add_para(
    "In simple words, these technologies were chosen because each one solves a specific need in the project. "
    "Kotlin and XML build the Android app, Android Studio and Gradle manage development, ViewModel and LiveData improve architecture, "
    "Firebase handles authentication and cloud data, Retrofit and external APIs provide live information, Glide/Picasso load images, and the extra dependencies support future expansion."
)
add_para(
    "A good viva answer is: <b>\"I selected these technologies to make the application modular, cloud-connected, visually functional, and suitable for real-time agriculture use cases such as weather, market prices, products, and user interaction.\"</b>"
)

doc.build(story)
print(f"Created: {OUTPUT}")
