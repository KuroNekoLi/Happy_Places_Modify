package com.happyplaces

import androidx.core.net.toUri
import com.happyplaces.database.HappyPlace

val mockHappyPlaceList = listOf<HappyPlace>(
    HappyPlace(
        id = 1,
        title = "大安區",
        image = "https://firebasestorage.googleapis.com/v0/b/kolfanci.appspot.com/o/rectange.png?alt=media&token=19343790-47e2-4382-890d-5b0c4fe50966".toUri(),
        description = "大安區是台北市大安區的一個村莊，位於台北市北區的大安街，屬於台北市第一府城區。",
        date = "2022/05/01",
        location = "台北市北區大安街",
        latitude = 25.048333,
        longitude = 121.783333
    ),
    HappyPlace(
        id = 2,
        title = "大安區",
        image = "https://firebasestorage.googleapis.com/v0/b/kolfanci.appspot.com/o/rectange.png?alt=media&token=19343790-47e2-4382-890d-5b0c4fe50966".toUri(),
        description = "大安區是台北市大安區的一個村莊，位於台北市北區的大安街，屬於台北市第一府城區。",
        date = "2022/05/01",
        location = "台北市北區大安街",
        latitude = 25.048333,
        longitude = 121.783333
    ),
)