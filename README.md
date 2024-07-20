# Lynk

Lynk is a social networking Android application built with Java and Firebase. The app allows users to sign up, log in, create posts with images and captions, like and comment on posts. The app leverages Firebase for user authentication, Firestore for data storage, and Firebase Storage for image storage.

## Table of Contents

1. [Project Description](#project-description)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Dependencies](#dependencies)
5. [Installation](#installation)
6. [Usage](#usage)
7. [Firestore Structure](#firestore-structure)
8. [Screenshots](#screenshots)
9. [Known Issues](#known-issues)
10. [Future Improvements](#future-improvements)
11. [Contributing](#contributing)
12. [License](#license)

## Project Description

Lynk is designed to provide a seamless social networking experience on mobile devices. Users can interact with each other by creating posts, liking and commenting on posts. The app ensures real-time updates and data synchronization using Firebase services.

## Features

- **User Authentication**: Users can sign up, log in, and log out securely.
- **Profile Management**: Users can update their profile with an avatar, name, and username.
- **Post Creation**: Users can create posts with images and captions.
- **Post Updation or deletion**: Users can update posts with images and captions and also delte the post only own post not other users.
- **Like and Comment**: Users can like and comment on posts, with real-time updates.
- **Real-Time Updates**: All interactions are updated in real-time using Firebase Firestore.

## Tech Stack

- **Java**: The primary programming language used for Android development.
- **Firebase Authentication**: For user authentication and management.
- **Firebase Firestore**: For real-time database management and storage.
- **Firebase Storage**: For storing user-uploaded images.
- **Glide**: For image loading and caching.

## Dependencies

The following dependencies are used in the project:

```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'com.google.firebase:firebase-auth:21.0.1'
    implementation 'com.google.firebase:firebase-firestore:23.0.3'
    implementation 'com.google.firebase:firebase-storage:20.0.1'
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
    implementation 'de.hdodenhof:circleimageview:3.1.0'
    implementation 'com.google.android.material:material:1.3.0'
    implementation 'androidx.recyclerview:recyclerview:1.1.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
}
```

## Installation

1. **Clone the Repository**: 
    ```bash
    git clone https://github.com/yourusername/Lynk.git
    ```

2. **Open the Project**: Open the project in Android Studio.

3. **Add Firebase Configuration**: 
    - Go to the Firebase Console and create a new project.
    - Add an Android app to your Firebase project and download the `google-services.json` file.
    - Place the `google-services.json` file in the `app` directory of your project.

4. **Sync Project with Gradle Files**: Ensure all dependencies are correctly installed by syncing the project.

## Usage

1. **Sign Up**: New users can sign up by providing their name, username, email, password, and an avatar.
2. **Log In**: Existing users can log in using their email and password.
3. **Create Post**: After logging in, users can create new posts with images and captions.
4. **Like and Comment**: Users can like and comment on posts, which are updated in real-time.

## Firestore Structure

The Firestore database is structured as follows:

### Users Collection

Each user document contains:
- `avatarUrl`: URL of the user's avatar image.
- `email`: User's email address.
- `name`: User's full name.
- `username`: User's unique username.

### Posts Collection

Each post document contains:
- `caption`: Caption text of the post.
- `imageUrl`: URL of the post's image.
- `timestamp`: Timestamp of when the post was created.
- `userId`: ID of the user who created the post.

  ## code screen ss
  ![image](https://github.com/user-attachments/assets/029fab5a-54ac-4a9c-8a09-cf830130a277)


## Screenshots📷
<table>
  <tr>
    <td>splash Screen normal</td>
     <td>Shimer Loading  </td>
     <td>home</td>
    
  </tr>
  <tr>
    <td><img src="https://github.com/Vishallab/lynk/blob/master/splash.jpeg" width=240 height=410/></td>
    <td><img src="https://github.com/Vishallab/lynk/blob/master/shimmer%20loading.jpeg" width=240 height=410/></td>
    <td><img src="https://github.com/Vishallab/lynk/blob/master/main%20screen.jpeg" width=240 height=410/></td>
    
  </tr>
  
  <tr>
    <td>create post</td>
    <td>Profile</td>
     <td>commets</td>
  </tr>
  <tr>
    <td><img src="https://github.com/Vishallab/lynk/blob/master/create%20post%20.jpeg" width=240 height=410/></td>
    <td><img src="https://github.com/Vishallab/lynk/blob/master/profile%20.jpeg" width=240 height=410/></td>
    <td><img src="https://github.com/Vishallab/lynk/blob/master/comment.jpeg" width=240 height=410/></td>
  </tr>

  <tr>
    <td>All user card to chat</td>
    <td>chat room</td>
  </tr>
  <tr>
    <td><img src="https://github.com/Vishallab/lynk/blob/master/all%20user%20card%20to%20chat.jpeg" width=240 height=410/></td>
    <td><img src="https://github.com/Vishallab/lynk/blob/master/chat%20room%20.jpeg" width=240 height=410/></td>
  </tr>
 </table>

<br>

## Known Issues

- **App Reload on Like**: When a user clicks the like button, the entire app reloads due to the use of Firestore instead of Firebase Realtime Database. This can cause a less than optimal user experience.
- **Messaging Feature**: The messaging feature is not yet implemented. Future updates will include the ability for users to send private messages to each other.
- **Activity vs. Fragment**: The app currently uses Activities for navigation, but it can be improved by using Fragments for better performance and navigation management.

## Future Improvements

- **Messaging**: Implement a private messaging feature allowing users to send and receive messages.
- **Fragment Integration**: Refactor the app to use Fragments instead of Activities to improve performance and navigation.
- **Real-Time Updates**: Optimize the real-time updates to avoid reloading the entire app on specific interactions.
- **UI/UX Enhancements**: Improve the overall user interface and user experience.


### •  Installation
To build and run this project, you will need:

- Android Studio IDE <br>

- Android SDK with compile SDK version 34 and minimum SDK version 26

### Steps:
1. Clone this repository:
```
git clone https://github.com/Vishallab/Lynk.git
```

2. Open the project in Android Studio.

3. Build and run the project on an Android emulator or a physical device.

### How To Run
Follow these steps to run the Cine-Mate app on your Android device or emulator:

1. Open your terminal (preferably Git Bash or Terminal in Android Studio).
2. Clone the repository:

```
git clone https://github.com/Vishallab/Lynk.git
```
3. Navigate to the project directory.
4. Sync the project with Gradle files.
5. Open an emulator or connect a real device.
6. Run the app using Android Studio's run/debug configurations.

 ## Video Demo

https://github.com/Vishallab/lynk/blob/master/demo.mp4

## Contributing
Contributions are welcome! Please fork this repository and submit pull requests for any enhancements or bug fixes.

- Fork the repository
- Create your feature branch (git checkout -b feature/AmazingFeature)
- Commit your changes (git commit -m 'Add some AmazingFeature')
- Push to the branch (git push origin feature/AmazingFeature)
- Open a pull request
- Developed And Maintained by 
 ### 😎 Vishal Mishra

## Social Media
Connect with me:
- [LinkedIn](https://www.linkedin.com/in/vishalmishra01)
- [Instagram](https://www.instagram.com/ig_viishal)
- [GitHub](https://www.github.com/Vishallab)
