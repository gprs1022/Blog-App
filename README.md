# BlogApp 📝

BlogApp is an Android application that allows users to create, view, and manage blog articles. It leverages Firebase Realtime Database for storing blog data and Firebase Authentication for user authentication. Users can register, log in, create new blog articles, edit existing ones, and view articles posted by other users.

## Features 🚀

- **User Authentication:**
  - 📧 User registration with email and password.
  - 🔐 User login and logout functionality.
  - 🔑 Password reset option.

- **Blog Management:**
  - 📝 Create new blog articles with a title and description.
  - ✏️ Edit existing blog articles.
  - 🗑️ Delete blog articles.

- **User Profile:**
  - 👤 Display user information such as name and profile picture.
  - 🖼️ Edit user profile (name, profile picture).

- **Realtime Updates:**
  - 🔄 Realtime synchronization of blog data using Firebase Realtime Database.
  - 📡 Immediate reflection of changes made by other users.

- **Edge-to-Edge Layout:**
  - 📱 Utilizes AndroidX libraries for edge-to-edge layout support.

## Technologies Used 🛠️

- **Firebase:**
  - 🔥 Firebase Realtime Database - For storing and syncing blog data.
  - 🔒 Firebase Authentication - For user authentication and management.
  - 📦 Firebase Storage - For storing user profile pictures.

- **AndroidX Libraries:**
  - 📱 AndroidX AppCompat - For providing a consistent UI experience across different Android versions.
  - 🧰 AndroidX Core-KTX - Kotlin extensions for core Android framework APIs.
  - 🔄 AndroidX Lifecycle - Components to build lifecycle-aware components.

- **Glide:**
  - 🖼️ For image loading and caching.

## Screenshots 📷

![Group 31](https://github.com/gprs1022/Blog-App/assets/88311782/bf98ec91-5f49-46ef-8baa-639c895f30ac)
![Group 32](https://github.com/gprs1022/Blog-App/assets/88311782/0d90078d-5fab-47ed-8f0d-11bb0a118ee7)
![Group 34](https://github.com/gprs1022/Blog-App/assets/88311782/a5308e6c-a647-4745-9631-8ad5e29faf7a)

![Group 33](https://github.com/gprs1022/Blog-App/assets/88311782/da1820ec-a1e3-4588-9566-23dfaae472db)


## Setup Instructions 🛠️

1. **Clone the repository:**
  -git clone https://github.com/gprs1022/BlogApp.git


2. **Setup Firebase:**
- Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/).
- Enable Firebase Authentication with email/password.
- Enable Firebase Realtime Database.
- Enable Firebase Storage.

3. **Add Firebase Configuration:**
- Download `google-services.json` from Firebase Console.
- Place `google-services.json` in the `app` directory of your project.

4. **Build and Run:**
- Open the project using Android Studio.
- Build and run the application on an Android device or emulator.

## Contributing 🤝

Contributions are welcome! Please fork this repository and create a pull request with your changes. For major changes, please open an issue first to discuss what you would like to change.

## License 📄

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments 🙏

- Thanks to [Open Source Community](https://opensource.org/) for inspiration and support.
- This project was developed as part of [Your Organization/University] course/project/workshop.

