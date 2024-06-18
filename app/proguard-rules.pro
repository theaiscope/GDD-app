# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Do not obfuscate field names, ends up with a/b/c/d properties in firestore
-keep public class net.aiscope.gdd_app.**{*;}

# Build issues with R8
-dontwarn com.amplitude.api.*
-dontwarn com.bugsnag.android.*
-dontwarn com.heapanalytics.android.*
-dontwarn com.mixpanel.android.mpmetrics.*
-dontwarn com.segment.analytics.*