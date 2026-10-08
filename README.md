# 🌱 PlantX

**PlantX** is an AI-powered Android mobile application that identifies plants from images using computer vision. Users can take a photo of a plant or select an existing image from their gallery, and PlantX analyzes the image to provide the plant's name and identification confidence.

The application uses **Kotlin for Android**, **Django REST Framework for the backend**, and **Ollama with Qwen2.5-VL** for AI-powered plant vision analysis.

---

## 📱 Overview

PlantX was developed to make plant identification simple and accessible through a mobile device.

Instead of manually searching through plant databases, users can simply provide an image of a plant. The image is uploaded to the PlantX backend, where an AI vision model analyzes the image and returns the identification result.

### How PlantX Works

```text
Android App
     │
     │ Capture / Select Image
     ▼
PlantX Android Application
     │
     │ HTTP Multipart Request
     ▼
Django REST API
     │
     │ Image Processing
     ▼
Ollama + Qwen2.5-VL
     │
     │ AI Analysis
     ▼
Plant Identification Result
     │
     │ Plant Name + Confidence
     ▼
Android Application
```

---

## ✨ Features

### 🌱 Plant Identification

Users can identify a plant by:

* Taking a photo using the device camera
* Selecting an image from the gallery
* Uploading the image to the backend
* Receiving an AI-generated plant identification result

### 🤖 AI-Powered Recognition

PlantX uses **Qwen2.5-VL**, a vision-language model running through **Ollama**, to analyze plant images.

The AI provides:

* Plant name
* Identification result
* Confidence score

### 👤 User Authentication

PlantX includes user authentication features:

* User registration
* User login
* Email-based account identification
* Password authentication

### 📊 Confidence Score

The application displays the AI's confidence level for the identified plant, helping users understand how reliable the identification result is.

### 📷 Camera and Gallery Support

Users have two ways to provide an image:

```text
Camera
   ↓
Take Plant Photo
   ↓
Identify Plant
```

or

```text
Gallery
   ↓
Select Plant Image
   ↓
Identify Plant
```

### 📜 Plant Identification History

Plant identification results can be stored and accessed as part of the user's identification history.

---

## 🛠️ Technologies Used

### Android

* **Kotlin**
* Android Studio
* XML Layouts
* Android SDK
* Retrofit
* Material Components

### Backend

* **Python**
* **Django**
* **Django REST Framework**
* Django CORS Headers

### AI

* **Ollama**
* **Qwen2.5-VL 7B**

### Communication

* REST API
* HTTP
* Multipart image upload
* JSON responses

### Database

* SQLite / Django database

---

## 📂 Project Structure

### Android Application

```text
PlantX/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── example/
│           │           └── plantx/
│           │               │
│           │               ├── LoginActivity.kt
│           │               ├── MainActivity.kt
│           │               ├── RegisterActivity.kt
│           │               │
│           │               ├── Registration/
│           │               │   └── RegisterActivity.kt
│           │               │
│           │               ├── IdentifyActivity.kt
│           │               │
│           │               ├── data/
│           │               │
│           │               ├── network/
│           │               │   └── RetrofitClient.kt
│           │               │
│           │               └── ...
│           │
│           └── res/
│               ├── drawable/
│               ├── layout/
│               ├── mipmap/
│               └── values/
│
└── build.gradle
```

### Django Backend

```text
plantbackend/
│
├── accounts/
│   ├── models.py
│   ├── serializers.py
│   ├── views.py
│   └── urls.py
│
├── plants/
│   ├── models.py
│   ├── serializers.py
│   ├── views.py
│   └── urls.py
│
├── plantx/
│   ├── settings.py
│   ├── urls.py
│   ├── asgi.py
│   └── wsgi.py
│
├── manage.py
└── requirements.txt
```

---

## 🔌 API

PlantX communicates with the Django backend through REST API endpoints.

### Authentication

#### Register

```http
POST /api/auth/register/
```

Example request:

```json
{
    "full_name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "confirm_password": "password123"
}
```

#### Login

```http
POST /api/auth/login/
```

---

### Plant Identification

```http
POST /api/plants/identify/
```

The endpoint accepts a plant image using multipart form data.

```text
Content-Type: multipart/form-data
```

Example:

```text
image: plant.jpg
```

The backend processes the image and sends it to the AI vision model.

Example response:

```json
{
    "plant_name": "Monstera Deliciosa",
    "confidence": 92
}
```

---

## 🤖 AI Model

PlantX uses **Qwen2.5-VL 7B** through Ollama for image analysis.

The model receives the uploaded plant image and analyzes visual characteristics to determine the most likely plant identification.

Example workflow:

```text
Plant Image
     ↓
Django API
     ↓
Ollama
     ↓
Qwen2.5-VL
     ↓
Visual Analysis
     ↓
Plant Name
     ↓
Confidence Score
```

---

## 💻 Backend Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/PlantX.git
cd PlantX
```

---

### 2. Create a Virtual Environment

Windows:

```powershell
python -m venv venv
```

Activate the environment:

```powershell
venv\Scripts\activate
```

If PowerShell execution policy prevents activation, you can run Python directly:

```powershell
venv\Scripts\python.exe manage.py runserver
```

---

### 3. Install Dependencies

```powershell
pip install -r requirements.txt
```

---

### 4. Run Database Migrations

```powershell
python manage.py migrate
```

---

### 5. Start the Django Server

For local development:

```powershell
python manage.py runserver
```

For testing with an Android phone on the same Wi-Fi network:

```powershell
python manage.py runserver 0.0.0.0:8000
```

The backend can then be accessed using the computer's local IP address:

```text
http://YOUR_PC_IP:8000
```

---

## 🧠 Ollama Setup

Install Ollama and download the required vision model.

Pull Qwen2.5-VL:

```bash
ollama pull qwen2.5vl:7b
```

Verify that the model is installed:

```bash
ollama list
```

You should see:

```text
qwen2.5vl:7b
```

Make sure Ollama is running before using the plant identification feature.

---

## 📱 Android Configuration

Update the API base URL in the Android application.

For example:

```kotlin
private const val BASE_URL = "http://192.168.1.201:8000/"
```

> Replace `192.168.1.201` with the local IP address of the computer running the Django server.

The Android phone and development computer should be connected to the **same Wi-Fi network**.

---

## 🔐 Android Internet Permission

The application requires Internet access to communicate with the Django backend.

Add the following to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

For local HTTP development, Android may also require cleartext traffic to be enabled:

```xml
<application
    android:usesCleartextTraffic="true"
    ... >
```

Use HTTPS instead of cleartext HTTP when deploying the application to production.

---

## 📸 Identification Screen

The PlantX identification screen provides controls for:

* Image preview
* Camera
* Gallery
* Identify Plant
* Loading indicator
* Plant name
* Confidence score
* Identify another plant

Example UI components:

```text
┌─────────────────────────────┐
│          PlantX              │
│                             │
│      ┌───────────────┐      │
│      │               │      │
│      │  Plant Image  │      │
│      │               │      │
│      └───────────────┘      │
│                             │
│   [ 📷 Camera ]             │
│   [ 🖼 Select Image ]       │
│                             │
│      [ Identify ]           │
│                             │
│   Plant: Monstera           │
│   Confidence: 92%           │
│                             │
│   [ Identify Another ]      │
└─────────────────────────────┘
```

---

## 🔄 Application Flow

```text
Launch PlantX
      │
      ▼
    Login
      │
      ├───────────────┐
      │               │
      ▼               ▼
   Register        Login
                      │
                      ▼
              Plant Identification
                      │
             ┌────────┴────────┐
             │                 │
             ▼                 ▼
          Camera            Gallery
             │                 │
             └────────┬────────┘
                      ▼
                Select Image
                      │
                      ▼
                Upload Image
                      │
                      ▼
                 Django API
                      │
                      ▼
                 Ollama AI
                      │
                      ▼
               Qwen2.5-VL
                      │
                      ▼
             Identification
                      │
                      ▼
             Display Result
                      │
                      ▼
              Save to History
```

---

## 🎯 Project Objectives

PlantX aims to:

1. Develop an accessible mobile plant identification application.
2. Use AI-based computer vision for plant recognition.
3. Provide users with quick plant identification results.
4. Display a confidence score for AI predictions.
5. Demonstrate integration between an Android mobile application and a REST API.
6. Explore the practical use of vision-language AI models in mobile applications.

---

## 🚀 Future Improvements

Possible future features include:

* 🌿 Detailed plant information
* 💧 Plant care recommendations
* ☀️ Sunlight requirements
* 💦 Watering schedules
* 🌱 Plant disease detection
* 📍 Location-based plant information
* ⭐ Favorite plants
* 🔔 Plant care reminders
* 📚 Expanded plant database
* 👤 User profile management
* ☁️ Cloud deployment
* 🔒 HTTPS and production authentication
* 📊 Improved AI confidence evaluation

---

## ⚠️ Disclaimer

PlantX is intended as an educational and experimental AI application. Plant identification results are generated by an AI model and may not always be accurate.

Users should verify important plant identification results using reliable botanical references, especially when determining whether a plant is edible, medicinal, or poisonous.

---

## 👨‍💻 Development

**Project:** PlantX
**Platform:** Android
**Language:** Kotlin
**Backend:** Django REST Framework
**AI:** Ollama + Qwen2.5-VL
**Database:** Django-supported database
**Architecture:** Android Client + REST API + AI Vision Model

---

## 📄 License

This project is developed for educational and academic purposes.

You may modify and extend the project according to your own requirements.
