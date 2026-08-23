# Add project specific ProGuard rules here.

# Keep app core classes
-keep class com.realtor.geeksales.** { *; }
-keep class com.realtor.geeksales.data.db.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class **_HiltModules* { *; }
-keep class **_HiltComponents* { *; }
-keep class **_HiltApps* { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# Room
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Apache POI - reflective access
-keep class org.apache.poi.** { *; }
-dontwarn org.apache.poi.**
-dontwarn org.openxmlformats.schemas.**
-keep class org.openxmlformats.schemas.** { *; }
-keep class com.microsoft.schemas.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-dontwarn org.apache.xmlbeans.**

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class com.realtor.geeksales.data.**$$serializer { *; }
-keepclassmembers class com.realtor.geeksales.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.realtor.geeksales.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# OpenCSV
-keep class com.opencsv.** { *; }
-dontwarn com.opencsv.**

# KotlinX DateTime
-keep class kotlinx.datetime.** { *; }

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Missing classes (from R8 missing_rules.txt)
-dontwarn aQute.bnd.annotation.spi.**
-dontwarn com.microsoft.schemas.office.**
-dontwarn com.microsoft.schemas.vml.**
-dontwarn java.awt.**
-dontwarn org.osgi.framework.**
