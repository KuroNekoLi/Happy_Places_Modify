# 多語言功能實現說明

## 功能概述

本 App 支援繁體中文和英文兩種語言，並提供以下功能：

1. **自動語言偵測**：根據系統語言自動選擇介面語言
2. **手動語言切換**：用戶可在設定中手動切換語言
3. **語言持久化**：選擇的語言會被保存，下次啟動時自動套用

## 支援語言

- **繁體中文 (zh-TW)**：適用於台灣、香港等地區
- **英文 (en)**：預設語言，適用於其他地區

## 檔案結構

### 字串資源檔

```
app/src/main/res/
├── values/strings.xml              # 預設字串（英文）
└── values-zh-rTW/strings.xml       # 繁體中文字串
```

### 核心程式碼

```
app/src/main/java/com/happyplaces/
├── util/LanguageManager.kt         # 語言管理工具類
├── App.kt                          # Application 類，初始化語言設定
└── presentation/ui/compose/profile/SettingScreen.kt  # 設定頁面，包含語言選擇
```

## 實現細節

### 1. LanguageManager 工具類

`LanguageManager` 是核心的語言管理類，提供以下功能：

- **語言枚舉**：定義支援的語言選項
- **語言儲存**：使用 SharedPreferences 保存用戶選擇
- **語言套用**：使用 `AppCompatDelegate.setApplicationLocales()` 設定語言
- **系統語言偵測**：自動偵測系統是否為繁體中文

### 2. 語言選項

```kotlin
enum class Language(val code: String, val displayName: String) {
    SYSTEM("system", "Follow System / 跟隨系統"),
    TRADITIONAL_CHINESE("zh-TW", "繁體中文"),
    ENGLISH("en", "English")
}
```

### 3. 應用程式初始化

在 `App.onCreate()` 中呼叫 `LanguageManager.initializeLanguage(this)` 來初始化語言設定。

### 4. 設定頁面

設定頁面包含：

- 語言選擇項目（顯示當前選擇的語言）
- 點擊後彈出語言選擇對話框
- Radio Button 顯示所有可用語言選項

## 語言切換邏輯

### 自動選擇邏輯

1. **首次啟動**：檢查系統語言
    - 如果系統是繁體中文（zh-TW 或 zh-HK）→ 使用繁體中文
    - 其他情況 → 使用英文

2. **後續啟動**：讀取用戶之前的選擇

### 手動切換邏輯

1. 用戶在設定中選擇語言
2. 立即套用新語言（使用 `AppCompatDelegate.setApplicationLocales()`）
3. 保存選擇到 SharedPreferences
4. App 重新套用新的語言環境

## 字串資源命名規範

### 分類命名

```xml
<!-- 新增/編輯頁面 -->
<string name="edit_text_hint_title">Title</string>
<string name="add_happy_place">Add Happy Place</string>

<!-- 導航和底部選單 -->
<string name="bottom_nav_recommend">Recommend</string>
<string name="bottom_nav_search">Search</string>

<!-- 搜尋頁面 -->
<string name="search_hint">Search places...</string>
<string name="search_no_results">No results found</string>

<!-- 我的地圖頁面 -->
<string name="my_map_loading">Loading places...</string>
<string name="my_map_empty_message">No places yet</string>

<!-- 設定頁面 -->
<string name="language">Language</string>
<string name="language_system">Follow System</string>

<!-- 通用文字 -->
<string name="loading">Loading</string>
<string name="retry">Retry</string>
<string name="cancel">Cancel</string>
```

### 不可翻譯資源

API 金鑰和 Facebook 設定使用 `translatable="false"` 標記：

```xml
<string name="google_maps_api_key" translatable="false">AIzaSy...</string>
<string name="facebook_application_id" translatable="false">1003688...</string>
```

## 使用方式

### 在 Compose 中使用

```kotlin
Text(text = stringResource(R.string.app_name))
```

### 在底部導航中使用

```kotlin
data class BottomNavigationRoute<T : Any>(
    @StringRes val nameRes: Int, 
    val route: T, 
    val icon: ImageVector
)

val bottomNavigationRoutes = listOf(
    BottomNavigationRoute(R.string.bottom_nav_recommend, Recommend, Icons.Default.Recommend)
)
```

## 測試方式

### 測試語言切換

1. 開啟 App → 進入設定 → 點擊語言
2. 選擇不同語言選項
3. 確認介面立即切換語言
4. 重新啟動 App 確認語言設定被保存

### 測試系統語言偵測

1. 將裝置系統語言設為繁體中文
2. 清除 App 資料（或首次安裝）
3. 開啟 App，確認自動使用繁體中文
4. 將系統語言改為英文，重複步驟確認使用英文

## 注意事項

1. **API 版本兼容**：`LanguageManager` 包含對 Android N (API 24) 以下版本的兼容處理
2. **字串完整性**：所有用戶可見的字串都必須加入字串資源檔
3. **翻譯一致性**：兩種語言的字串資源必須保持同步
4. **測試覆蓋**：新增功能時要確保兩種語言都有對應的字串資源

## 未來擴展

如需新增其他語言：

1. 創建對應的 `values-{language}` 目錄
2. 在 `LanguageManager.Language` 枚舉中新增語言選項
3. 更新語言偵測邏輯
4. 翻譯所有字串資源