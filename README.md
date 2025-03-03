# Humana - მაღაზიის მართვის აპლიკაცია

Android აპლიკაცია Humana-ს მაღაზიების მართვისთვის.

## ფუნქციები

- 🗺️ Google Maps ინტეგრაცია მაღაზიების ლოკაციების სანახავად
- 📱 მაღაზიების მართვა და მონიტორინგი
- 👤 მომხმარებლის ავტორიზაცია
- 📊 მაღაზიის მონაცემების მართვა

## ტექნოლოგიები

- Kotlin
- Android SDK
- Google Maps API
- Firebase Authentication
- Firebase Realtime Database

## დაყენების ინსტრუქცია

1. დააკლონირეთ რეპოზიტორია:
```bash
git clone https://github.com/shootX/Humana.git
```

2. Android Studio-ში გახსენით პროექტი

3. Google Maps API Key-ის კონფიგურაცია:
   - გახსენით [Google Cloud Console](https://console.cloud.google.com/)
   - შექმენით ახალი პროექტი ან აირჩიეთ არსებული
   - ჩართეთ Maps SDK for Android
   - გადადით Credentials გვერდზე
   - შექმენით ახალი API Key (Create Credentials -> API Key)
   - შეზღუდეთ API Key მხოლოდ Android აპლიკაციისთვის:
     - Application restrictions -> Android apps
     - დაამატეთ თქვენი აპლიკაციის SHA-1 და package name

4. API Key-ის დამატება:
   - გახსენით `app/src/main/AndroidManifest.xml`
   - მოძებნეთ `com.google.android.geo.API_KEY` meta-data თეგი
   - ჩასვით თქვენი API Key value ატრიბუტში:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="თქვენი_API_KEY" />
   ```

5. SHA-1 ხელმოწერის მიღება:
   ```bash
   cd android
   ./gradlew signingReport
   ```

6. გაუშვით აპლიკაცია Android Studio-დან

## ხშირი პრობლემები

1. Google Maps არ იტვირთება:
   - შეამოწმეთ API Key-ის სისწორე
   - დარწმუნდით, რომ Maps SDK for Android ჩართულია
   - შეამოწმეთ ინტერნეტ კავშირი
   - გადაამოწმეთ SHA-1 ხელმოწერა Google Cloud Console-ში

## სისტემური მოთხოვნები

- Android 6.0 (API level 23) ან უფრო ახალი
- Google Play Services
- ინტერნეტ კავშირი
- GPS მოწყობილობა

## ლიცენზია

ყველა უფლება დაცულია © 2024 Humana 
