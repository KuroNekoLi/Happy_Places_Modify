package com.happyplaces.presentation


import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.happyplaces.database.HappyPlace
import com.happyplaces.database.HappyPlaceRepository
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class HappyPlaceViewModel(application: Application, private val repository: HappyPlaceRepository) :
    AndroidViewModel(application) {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    // LiveData to hold the address. Views can observe this to get updates
    val address: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getDataList() = liveData {
        repository.dataList.collect {
            emit(it)
        }
    }

    fun insert(happyPlace: HappyPlace) = viewModelScope.launch(IO) {
        val newRowId = repository.insert(happyPlace)
        withContext(Main) {
            if (newRowId > -1) {
                _message.value = "第 $newRowId 個資料已新增"
            } else {
                _message.value = "發生錯誤"
            }
        }
    }

    fun update(happyPlace: HappyPlace) = viewModelScope.launch(IO) {
        val numberOfRows = repository.update(happyPlace)
        withContext(Main) {
            if (numberOfRows > 0) {
                _message.value = "第 $numberOfRows 個資料已更新"
            } else {
                _message.value = "發生錯誤"
            }
        }
    }

    fun delete(happyPlace: HappyPlace) = viewModelScope.launch(IO) {
        val numberOfRowsDeleted = repository.delete(happyPlace)
        withContext(Main) {
            if (numberOfRowsDeleted > 0) {
                _message.value = "第 $numberOfRowsDeleted 個資料已刪除"
            } else {
                _message.value = "發生錯誤"
            }
        }
    }

    // Function to get address from latitude and longitude
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getAddressFromLatLng(latitude: Double, longitude: Double) {
        viewModelScope.launch(IO) {
            val geocoder = Geocoder(getApplication(), Locale.getDefault())
            geocoder.getFromLocation(latitude, longitude, 1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isNotEmpty()) {
                            val returnedAddress = addresses[0]
                            val sb = StringBuilder()
                            for (i in 0..returnedAddress.maxAddressLineIndex) {
                                sb.append(returnedAddress.getAddressLine(i)).append(",")
                            }
                            sb.deleteCharAt(sb.length - 1) // Removing the last comma from the address.
                            address.postValue(sb.toString())
                        } else {
                            Log.e("Get Address", "No Address returned!")
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                        if (errorMessage != null) {
                            Log.e("Get Address", errorMessage)
                        }
                    }
                })
        }
    }
}