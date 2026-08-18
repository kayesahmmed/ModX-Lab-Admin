package com.modxlab.admin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.modxlab.admin.data.model.DashboardStats
import com.modxlab.admin.data.model.MaintenanceEntity
import com.modxlab.admin.data.model.SellerEntity
import com.modxlab.admin.data.model.UserEntity
import com.modxlab.admin.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // Dashboard
    val dashboardStats: StateFlow<DashboardStats> = repository.dashboardStats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardStats()
    )

    // Maintenance
    val maintenance: StateFlow<MaintenanceEntity?> = repository.maintenanceFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // User Filter & Search
    private val _userSearchQuery = MutableStateFlow("")
    val userSearchQuery: StateFlow<String> = _userSearchQuery.asStateFlow()

    private val _userStatusFilter = MutableStateFlow("ALL") // "ALL", "ACTIVE", "INACTIVE"
    val userStatusFilter: StateFlow<String> = _userStatusFilter.asStateFlow()

    val filteredUsers: StateFlow<List<UserEntity>> = combine(
        repository.allUsers,
        _userSearchQuery,
        _userStatusFilter
    ) { users, query, filter ->
        users.filter { user ->
            val matchesQuery = query.isBlank() ||
                    user.user.contains(query, ignoreCase = true) ||
                    user.key.contains(query, ignoreCase = true) ||
                    user.device.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                "ACTIVE" -> user.isActive
                "INACTIVE" -> !user.isActive
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Seller Filter & Search
    private val _sellerSearchQuery = MutableStateFlow("")
    val sellerSearchQuery: StateFlow<String> = _sellerSearchQuery.asStateFlow()

    private val _sellerStatusFilter = MutableStateFlow("ALL") // "ALL", "ACTIVE", "INACTIVE"
    val sellerStatusFilter: StateFlow<String> = _sellerStatusFilter.asStateFlow()

    val filteredSellers: StateFlow<List<SellerEntity>> = combine(
        repository.allSellers,
        _sellerSearchQuery,
        _sellerStatusFilter
    ) { sellers, query, filter ->
        sellers.filter { seller ->
            val matchesQuery = query.isBlank() ||
                    seller.user.contains(query, ignoreCase = true) ||
                    seller.key.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                "ACTIVE" -> seller.isActive
                "INACTIVE" -> !seller.isActive
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setUserSearchQuery(query: String) {
        _userSearchQuery.value = query
    }

    fun setUserStatusFilter(filter: String) {
        _userStatusFilter.value = filter
    }

    fun setSellerSearchQuery(query: String) {
        _sellerSearchQuery.value = query
    }

    fun setSellerStatusFilter(filter: String) {
        _sellerStatusFilter.value = filter
    }

    // Operations
    fun addUser(
        username: String,
        pass: String,
        access: String,
        validityDays: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val created = repository.addUser(username, pass, access, validityDays)
                _snackbarMessage.emit("License Key Generated: ${created.key}")
                onSuccess()
            } catch (e: Exception) {
                _snackbarMessage.emit("Failed to add user: ${e.message}")
            }
        }
    }

    fun updateUserCredentials(key: String, user: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateUserCredentials(key, user, pass)
                _snackbarMessage.emit("Credentials updated successfully")
                onSuccess()
            } catch (e: Exception) {
                _snackbarMessage.emit("Update failed: ${e.message}")
            }
        }
    }

    fun toggleUserStatus(key: String, activate: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleUserStatus(key, activate)
                _snackbarMessage.emit(if (activate) "User activated" else "User deactivated")
            } catch (e: Exception) {
                _snackbarMessage.emit("Status change failed: ${e.message}")
            }
        }
    }

    fun deleteUser(key: String) {
        viewModelScope.launch {
            try {
                repository.deleteUser(key)
                _snackbarMessage.emit("User license deleted")
            } catch (e: Exception) {
                _snackbarMessage.emit("Deletion failed: ${e.message}")
            }
        }
    }

    fun addSeller(
        username: String,
        pass: String,
        access: String,
        coin: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val created = repository.addSeller(username, pass, access, coin)
                _snackbarMessage.emit("Seller Created: ${created.user}")
                onSuccess()
            } catch (e: Exception) {
                _snackbarMessage.emit("Failed to add seller: ${e.message}")
            }
        }
    }

    fun updateSellerCredentials(key: String, user: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateSellerCredentials(key, user, pass)
                _snackbarMessage.emit("Seller credentials updated")
                onSuccess()
            } catch (e: Exception) {
                _snackbarMessage.emit("Update failed: ${e.message}")
            }
        }
    }

    fun toggleSellerStatus(key: String, activate: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleSellerStatus(key, activate)
                _snackbarMessage.emit(if (activate) "Seller activated" else "Seller deactivated")
            } catch (e: Exception) {
                _snackbarMessage.emit("Status change failed: ${e.message}")
            }
        }
    }

    fun deleteSeller(key: String) {
        viewModelScope.launch {
            try {
                repository.deleteSeller(key)
                _snackbarMessage.emit("Seller deleted")
            } catch (e: Exception) {
                _snackbarMessage.emit("Deletion failed: ${e.message}")
            }
        }
    }

    fun setMaintenanceUpdate(
        version: String,
        message: String,
        link: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.setMaintenanceUpdate(version, message, link)
                _snackbarMessage.emit("Maintenance broadcast deployed successfully")
                onSuccess()
            } catch (e: Exception) {
                _snackbarMessage.emit("Broadcast failed: ${e.message}")
            }
        }
    }

    suspend fun getUser(key: String): UserEntity? = repository.getUserByKey(key)
    suspend fun getSeller(key: String): SellerEntity? = repository.getSellerByKey(key)
}

class AdminViewModelFactory(
    private val repository: AdminRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
