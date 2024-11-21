# Bird Watching App  

Welcome to the Bird Watching App! This project aims to provide bird enthusiasts with a user-friendly mobile application to discover local birdwatching hotspots, track bird sightings, and navigate to destinations efficiently.  

## Features  

1. **User Registration & Authentication**  
   - Register a new account.  
   - Login using a username and password.  

2. **User Settings**  
   - Toggle between metric (kilometers) and imperial (miles) systems.  
   - Set a maximum distance for birdwatching hotspots.  

3. **Hotspot Map**  
   - Display nearby birdwatching hotspots using the embedded map.  
   - Filter hotspots based on user preferences.  
   - Display user location on the map.  
   - Get directions to selected hotspots with route visualization.  

4. **Bird Observation Tracker**  
   - Save bird sightings at the current location.  
   - View all recorded observations.  
   - Display bird observations on the map.  

5. **Backend Integration**  
   - Firebase Authentication for secure user management.  
   - Firebase Firestore for storing user preferences and bird observations.  

6. **Deployment**  
   - Hosted on Azure App Services with automated CI/CD pipeline setup in Azure DevOps.  

---

## Prerequisites  

### Tools Required  
- **Android Studio**: For developing and running the app.  
- **Firebase Console**: To manage backend services.  
- **Azure DevOps**: For CI/CD pipelines.  

### Dependencies  
- Android SDK  
- Google Maps API  
- Firebase Authentication  
- Firebase Firestore  
- eBird API 2.0  

---

## Installation  

### Clone the Repository  
```bash
git clone https://github.com/yourusername/birdwatching-app.git
cd birdwatching-app
