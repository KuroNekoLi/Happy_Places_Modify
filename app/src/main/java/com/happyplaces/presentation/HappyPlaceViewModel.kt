package com.happyplaces.presentation


import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyplaces.data.model.AddPlaceEvent
import com.happyplaces.data.model.AddPlaceUiState
import com.happyplaces.data.model.toAddPlaceUiState
import com.happyplaces.data.model.toHappyPlace
import com.happyplaces.domain.HappyPlaceRepository
import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.util.ApiResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class HappyPlaceViewModel(
    private val application: Context,
    private val repository: HappyPlaceRepository
) : ViewModel() {
    val dataListApiResourceFlow = repository.getAllHappyPlaces()
        .flowOn(IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiResource.Loading())
    private val _uiState = MutableStateFlow(AddPlaceUiState())
    val uiState: StateFlow<AddPlaceUiState> = _uiState

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun onTitleChange(v: String) = _uiState.update { it.copy(title = v) }
    fun onDescriptionChange(v: String) = _uiState.update { it.copy(description = v) }
    fun onDateSelected(dateStr: String) = _uiState.update { it.copy(date = dateStr) }
    fun onLocationClick() =
        _uiState.update { it.copy(event = AddPlaceEvent.ShowPlacesAutocomplete) }

    fun onLocationSelected(addr: String, lat: Double, lng: Double) =
        _uiState.update { it.copy(location = addr, latitude = lat, longitude = lng) }

    fun onSelectCurrentLocation() =
        _uiState.update { it.copy(event = AddPlaceEvent.RequestCurrentLocation) }

    fun onAddImageClick() = _uiState.update { it.copy(event = AddPlaceEvent.ShowImagePicker) }
    fun onImagePicked(uri: Uri) =
        _uiState.update { it.copy(imageUrl = uri.toString()) }

    fun onDateClick() {
        _uiState.update { it.copy(event = AddPlaceEvent.ShowDatePicker) }
    }

    fun onEventConsumed() {
        _uiState.update { it.copy(event = null) }
    }

    fun onSaveClick() {
        val s = _uiState.value
        val description = s.description
        val photoUri = s.imageUrl
        val location = s.location
        val isEditMode = s.isEditMode
        when {
            s.title.isBlank() -> {
                _uiState.update { it.copy(event = AddPlaceEvent.ShowToast("Please enter title")) }
            }

            description.isBlank() -> {
                _uiState.update { it.copy(event = AddPlaceEvent.ShowToast("Please enter description")) }
            }

            location.isBlank() -> {
                _uiState.update { it.copy(event = AddPlaceEvent.ShowToast("Please select location")) }
            }

            photoUri == null -> {
                _uiState.update { it.copy(event = AddPlaceEvent.ShowToast("Please add image")) }
            }

            isEditMode -> {
                update()
            }

            else -> uiState.value.toHappyPlace()?.let { happyPlace ->
                insert(happyPlace)
                _uiState.update { state -> state.copy(event = AddPlaceEvent.NavigateBack) }
            }
        }
    }

    fun insert(happyPlace: HappyPlace): Job =
        CoroutineScope(IO).launch { //使用CoroutineScope而不使用viewModelScope是因為不想在viewModel重新建立時取消工作
        val resultFlow = repository.insert(happyPlace)
        resultFlow.collect {
            withContext(Main) {
                it.data?.let {
                    _message.value = "第 $it 個資料已新增"
                }
                it.message?.let {
                    _message.value = it
                }
            }
        }
    }

    fun update() = viewModelScope.launch {
        uiState.value.toHappyPlace()?.let {
            val resultFlow = repository.update(it)
            resultFlow.collect {
                it.data?.let {
                    if (it > 0) {
                        _message.value = "第 $it 個資料已更新"
                    } else {
                        _message.value = "發生錯誤"
                    }
                    _uiState.update { it.copy(event = AddPlaceEvent.NavigateBack) }
//                _uiState.update { it.copy(event = if (numberOfRows > 0) AddPlaceEvent.ShowToast("第 $numberOfRows 個資料已更新") else AddPlaceEvent.ShowToast("發生錯誤")) }
                }
                it.message?.let {
                    _message.value = it
                }
            }
        }
    }

    fun delete(happyPlace: HappyPlace) = viewModelScope.launch {
        val resultFlow = repository.delete(happyPlace)
        resultFlow.collect {
            it.data?.let { rowsDeleted ->
                withContext(Main) {
                    _message.value =
                        if (rowsDeleted > 0) "第 $rowsDeleted 個資料已刪除" else "發生錯誤"
                }
            }
            it.message?.let { errorMessage ->
                withContext(Main) {
                    _message.value = errorMessage
                }
            }
        }
    }

    /** 依 API 版本選擇 Geocoder 呼叫 */
    private fun getAddressFromLatLng(lat: Double, lng: Double) = viewModelScope.launch(IO) {
        val geocoder = Geocoder(application, Locale.getDefault())

        // Android 13 (API 33) 以上 ─ 使用非阻塞版
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addr: MutableList<Address>) {
                    addr.firstOrNull()?.toFormatted()?.also(::postAddress)
                }

                override fun onError(errorMessage: String?) {
                    Log.e("Geocoder", errorMessage ?: "unknown error")
                }
            })
        } else {
            // 舊 API：同步呼叫；因為在 IO 區域，不會阻塞 UI
            @Suppress("DEPRECATION")
            val list = geocoder.getFromLocation(lat, lng, 1)
            list?.firstOrNull()?.toFormatted()?.also(::postAddress)
        }
    }

    fun updateCurrentLatLng(lat: Double, lng: Double) {
        // 先更新經緯度 (地圖可立即用)；地址稍後再補
        _uiState.update { it.copy(latitude = lat, longitude = lng) }
        getAddressFromLatLng(lat, lng)
    }

    fun updateAllHappyPlaces() {
        viewModelScope.launch {
            repository.updateAllHappyPlaces().collect {
                it.data?.let {

                }
            }
        }

    }

    /** 將 Address 轉成完整字串 */
    private fun Address.toFormatted(): String = buildString {
        for (i in 0..maxAddressLineIndex) append(getAddressLine(i)).append(", ")
    }.removeSuffix(", ")

    private fun postAddress(addr: String) {
        _uiState.update { it.copy(location = addr) }
    }

    fun getHappyPlaceById(id: String) {
        viewModelScope.launch {
            Log.i("LinLi", "id : $id")
            repository.getHappyPlaceById(id).collect {
                Log.i("LinLi", "it : ${it.data}")
                Log.i("LinLi", "it : ${it.message}")
                it.data?.let {
                    _uiState.value = it.toAddPlaceUiState()
                }
            }
        }
    }
}