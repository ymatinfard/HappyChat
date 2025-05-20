# 🍔 Conversational AI Chat – Android

This project is an Android-based MVP (Minimum Viable Product) of a **Conversational AI Chat feature** built for the **Support section of a food delivery application**. Users can interact with an AI assistant to **place**, **track**, or **cancel** food orders. At the current stage, the application supports only the **happy path for placing an order**.

## 🎥 Demo

<img src="screenshots/conversational_ai_screen.gif" alt="Conversational AI Chat Demo" width="300"/>

---

## 💡 Overview

This MVP showcases the core capabilities of a conversational system in an Android application, where:

* Users can **chat naturally** with an AI assistant.
* User input is analyzed via a **Natural Language Processor** to extract **intentions**.
* The application then **executes appropriate actions** based on these intentions.

All the **dialog processing and intent recognition** are handled on the **server-side**, powered by the [Rasa Framework](https://rasa.com/), an open-source machine learning framework for automated text and voice-based conversations.

> 💡 The server-side implementation (built with Python and Rasa) is available here:
> 👉 [https://github.com/ymatinfard/conversational-ai-server](https://github.com/ymatinfard/conversational-ai-server)

---

## 🔧 Tech Stack

### Client (Android)

* **Kotlin**
* **Room Database** – used as the **single source of truth** for managing message state (`pending`, `sent`, `failed`) and conversation data.
* **Retrofit (REST)** – for client-server communication.
* **MVVM Architecture** – clean separation of concerns.

### Server

* **Python**
* **Rasa** – for intent recognition and dialog management.
* **REST API**

---

## 📡 Communication

Currently, the app communicates with the server using the **REST protocol**. While functional for MVP purposes, **gRPC** is recommended for production due to:

* Lower serialization/deserialization overhead
* Improved performance
* Strong typing and better contract enforcement

---

## 🧠 Dialog Flow

1. User sends a message through the chat UI.
2. Message is **persisted in Room database** (with status `pending`).
3. The message is sent to the **Rasa server**.
4. Rasa processes the dialog and determines user **intent**.
5. The appropriate action is performed (e.g., order placed).
6. A response is returned and displayed to the user.
7. Message status is updated (`sent` or `failed`) accordingly.

> ⚠️ Currently, only the **"placing an order"** flow is implemented end-to-end as a happy path.

---

## 🧪 Running the App

### 🔁 Server Setup

You must first clone and run the server:

```bash
git clone https://github.com/ymatinfard/conversational-ai-server
cd conversational-ai-server
rasa run --enable-api
```

---

### 📱 Connecting Android App to Server

#### ✅ Using Emulator:

Set API address in the Android app to:

```
http://10.0.2.2:5005/
```

> `10.0.2.2` routes Android emulator network requests to your host machine.

#### ✅ Using Real Device (USB Debugging):

1. Run the following command:

   ```bash
   adb reverse tcp:8000 tcp:8000
   ```
2. Set API address in the Android app to:

   ```
   http://192.168.52.96:8000/
   ```

   > Replace `192.168.52.96` with your actual local IP address.

Ensure server is running on **port 8000**.
