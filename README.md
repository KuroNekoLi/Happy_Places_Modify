# Android 12 & Kotlin 專案修改版

此專案基於 Udemy 上的課程 "The Complete Android 12 & Kotlin Development Masterclass" 進行了一系列的修改與優化。原課程連結如下：

- [The Complete Android 12 & Kotlin Development Masterclass](https://www.udemy.com/course/android-kotlin-developer/)

## 主要的修改

以下列出了對原始專案進行的主要變更：

1. **改用MVVM架構**：將原本的架構轉變為符合現代Android開發的MVVM (Model-View-ViewModel) 架構。
2. **改用Room**：對資料存儲部分，改用了Room持久性庫來管理SQLite數據庫。
3. **使用ViewBinding**：通過使用ViewBinding，更有效地與UI元件互動，提高性能和類型安全。
4. **重寫Google的Geocoder**：改進了Geocoder的實現，使其更準確和高效。
5. **將AsyncTask改為Coroutine**：利用Kotlin的協程來處理非同步任務，以簡化代碼並減少資源使用。
6. **權限處理的改寫**：通過`ActivityResultLauncher`來處理應用程序權限，使權限管理更直觀且易於維護。

## 未來規劃

我也正在計劃進一步的改進，包括但不限於：

- **使用依賴注入**：計劃引入依賴注入框架，如Hilt或Dagger，以提高組件之間的測試性和解耦。
- **使用Jetpack Compose**：將傳統的UI構建方法遷移到更現代化的Jetpack Compose。
- **混用MVI，改用UDF**：考慮導入更具韌性的架構模式，如MVI (Model-View-Intent) 或 UDF (Unidirectional Data Flow)，以提高用戶體驗和代碼可維護性。

## 貢獻

如果你對於本專案感興趣，或有任何想法、建議，歡迎提交 issue 或 pull request。

## 授權

本專案修改自Udemy上的課程，其所有權利均屬於原作者所有。本修改版僅供學習和研究使用，不得用於商業目的。
