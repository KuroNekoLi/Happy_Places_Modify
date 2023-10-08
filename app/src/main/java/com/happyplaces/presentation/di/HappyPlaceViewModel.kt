package com.happyplaces.presentation.di


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.happyplaces.database.HappyPlace
import com.happyplaces.database.HappyPlaceRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HappyPlaceViewModel(private val repository: HappyPlaceRepository) : ViewModel(){

    private val _message = MutableLiveData<String>()
    val message : LiveData<String> = _message

    fun getDataList() = liveData {
        repository.dataList.collect{
            emit(it)
        }
    }

    fun insert(happyPlace: HappyPlace) = viewModelScope.launch(IO) {
        val newRowId = repository.insert(happyPlace)
        withContext(Main){
            if (newRowId > -1){
                _message.value = "第 $newRowId 個資料已新增"
            }else{
                _message.value = "發生錯誤"
            }
        }
    }
    fun update(happyPlace: HappyPlace) = viewModelScope.launch(IO) {
        val numberOfRows = repository.update(happyPlace)
        withContext(Main){
            if (numberOfRows > 0){
                _message.value = "第 $numberOfRows 個資料已更新"
            }else{
                _message.value = "發生錯誤"
            }
        }
    }

    fun delete(happyPlace: HappyPlace) = viewModelScope.launch(IO) {
        val numberOfRowsDeleted = repository.delete(happyPlace)
        withContext(Main){
            if (numberOfRowsDeleted > 0){
                _message.value = "第 $numberOfRowsDeleted 個資料已刪除"
            }else{
                _message.value = "發生錯誤"
            }
        }
    }
}