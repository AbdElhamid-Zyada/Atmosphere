package com.example.atmoshpere.ui.utils

object Translations {
    private val ar = mapOf(
        "Home" to "الرئيسية",
        "Favorites" to "المفضلة",
        "Alerts" to "التنبيهات",
        "Settings" to "الإعدادات",
        "UNITS & LOCALIZATION" to "الوحدات واللغة",
        "Temperature Unit" to "وحدة الحرارة",
        "Wind Speed" to "سرعة الرياح",
        "Location" to "الموقع",
        "Use current location" to "استخدام الموقع الحالي",
        "Choose custom location" to "اختيار موقع مخصص",
        "Language" to "اللغة",
        "APPEARANCE" to "المظهر",
        "DARK GLASS" to "الزجاجي المظلم",
        "FROST WHITE" to "الأبيض الصقيعي",
        "DEVICE" to "النظام",
        "Hourly Forecast" to "التوقعات بالساعة",
        "5-Day Forecast" to "توقعات ٥ أيام",
        "Remove Location" to "إزالة الموقع",
        "Are you sure you want to remove" to "هل أنت متأكد من إزالة",
        "Confirm" to "تأكيد",
        "Cancel" to "إلغاء",
        "+ ADD LOCATION" to "+ إضافة موقع",
        "Select This Location" to "اختر هذا الموقع",
        "Search city or country..." to "ابحث عن مدينة أو دولة...",
        "No weather data available" to "لا توجد بيانات للطقس",
        "Feels like" to "يشبه",
        "Humidity" to "الرطوبة",
        "Pressure" to "الضغط",
        "Clouds" to "السحب",
        "Wind" to "الرياح",
        "Alerts Screen" to "شاشة التنبيهات",
        "No alerts scheduled" to "لا توجد تنبيهات مجدولة",
        "+ ADD ALERT" to "+ إضافة تنبيه",
        "Select Time" to "اختر الوقت",
        "Select Days" to "اختر الأيام",
        "Alert Type" to "نوع التنبيه",
        "Save Alert" to "حفظ التنبيه",
        "🚨 LOOK OUTSIDE! 🚨" to "🚨 انظر بالخارج! 🚨",
        "Today's weather brief is here" to "ملخص الطقس اليوم هنا",
        "DISMISS" to "إغلاق"
    )

    fun get(key: String, lang: String): String {
        return if (lang == "ar") ar[key] ?: key else key
    }
}
