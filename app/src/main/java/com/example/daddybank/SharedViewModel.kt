package com.example.daddybank

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val selectedUser = MutableLiveData<User?>()
    val accountValuesSeries = MutableLiveData<List<Pair<String, Double>>>()
}
