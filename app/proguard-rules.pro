# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# --- GENERAL ANDROID / OPTIMIZATIONS ---
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- MODEL CLASSES / DTOS ---
# Crucial for Moshi/Retrofit so fields are not renamed.
# Adjust package name if your models are elsewhere.
-keep class com.example.data.model.** { *; }
-keep class com.example.data.local.entity.** { *; }

# --- RETROFIT ---
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# --- MOSHI ---
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier @interface *
-keep class * extends com.squareup.moshi.JsonAdapter {
    public <init>(...);
}
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}
-keep class kotlin.Metadata { *; }

# --- ROOM ---
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-keep @androidx.room.Database class *
-keepclassmembers class * {
    @androidx.room.PrimaryKey *;
    @androidx.room.ColumnInfo *;
    @androidx.room.Relation *;
    @androidx.room.Ignore *;
}

# --- OKHTTP ---
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# --- COROUTINES ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# --- FIREBASE / PLAY SERVICES ---
-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }

