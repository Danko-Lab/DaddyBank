package com.example.daddybank

import retrofit2.Call
import retrofit2.http.GET
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.io.Serializable
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class BankData(val users: List<User>)

data class User(val name: String, val transactions: List<Transaction>, val interestRates: List<InterestRate>)

data class Transaction(val date: String, val type: String, val amount: Double)

data class InterestRate(val startDate: String, val endDate: String, val rate: Double)

interface BankApi {
    @GET("files/dbstatefile.json")
    fun getBankData(): Call<BankData>
}

class BankDataRepository(context: Context) : Serializable {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("bank_data", Context.MODE_PRIVATE)

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.dankolab.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val bankApi: BankApi = retrofit.create(BankApi::class.java)

    var users: List<User> = listOf()
        private set

    suspend fun loadData() {
        val cachedUsers = getCachedUsers()
        if (cachedUsers.isNotEmpty()) {
            users = cachedUsers
        } else {
            val result = fetchUsers()
            if (result.isSuccess) {
                users = result.getOrNull().orEmpty()
            }
        }
    }

    suspend fun fetchUsers(): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bankApi.getBankData().execute()
                if (response.isSuccessful) {
                    response.body()?.let { bankData ->
                        saveUsers(bankData.users)
                        Result.success(bankData.users)
                    } ?: Result.failure(Exception("Invalid response"))
                } else {
                    Result.failure(Exception("Request failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }.also {
                Log.d("BankDataRepository", "fetchUsers result: $it")
            }
        }
    }

    fun getCachedUsers(): List<User> {
        val json = sharedPreferences.getString("users", "")
        if (json.isNullOrEmpty()) {
            return emptyList()
        }

        val type = object : TypeToken<List<User>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun saveUsers(users: List<User>) {
        val json = Gson().toJson(users)
        sharedPreferences.edit().putString("users", json).apply()
    }


}
