package com.example.mohammedehsanullahshareef_hw1

object Constants {
    // Emulator: 10.0.2.2 reaches host machine's localhost
    // Real device on same WiFi: use your machine's local IP e.g. http://192.168.1.x:3001/
    const val BASE_URL = "http://10.0.2.2:3001/"
    const val EXTRA_CATEGORY_STATS = "extra_category_stats"
    const val EXTRA_OUTFIT_NAME = "extra_outfit_name"
    const val EXTRA_OUTFIT_REASONING = "extra_outfit_reasoning"
    const val EXTRA_OUTFIT_ITEMS = "extra_outfit_items"
    const val REQUEST_BROWSE_CLOSET = 1001
    const val REQUEST_OUTFIT_GENERATOR = 1002
}
