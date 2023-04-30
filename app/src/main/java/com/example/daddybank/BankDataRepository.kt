package com.example.daddybank

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
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
import java.util.*
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList


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

    fun getBalance(user: User, currentDate: String): Double {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val current = sdf.parse(currentDate) ?: return 0.0

        var balance = 0.0
        for (transaction in user.transactions) {
            val date = sdf.parse(transaction.date) ?: continue
            if (date <= current) {
                balance += if (transaction.type == "deposit") transaction.amount else -transaction.amount
            }
        }

        val sortedInterestRates = user.interestRates.sortedBy { it.startDate }
        var lastInterestRateDate = sdf.parse(sortedInterestRates.first().startDate) ?: return balance

        for (interestRate in sortedInterestRates) {
            val start = sdf.parse(interestRate.startDate) ?: continue
            val end = sdf.parse(interestRate.endDate) ?: continue

            if (start <= current) {
                val interestDays = if (current < end) {
                    TimeUnit.MILLISECONDS.toDays(current.time - lastInterestRateDate.time)
                } else {
                    TimeUnit.MILLISECONDS.toDays(end.time - lastInterestRateDate.time)
                }

                balance += balance * interestRate.rate * interestDays / 365.0
                lastInterestRateDate = end
            }
        }

        return balance
    }

    fun getAccountValuesSeries(user: User, startDate: String, endDate: String): List<Pair<String, Double>> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val start = sdf.parse(startDate) ?: return emptyList()
        val end = sdf.parse(endDate) ?: return emptyList()

        val dateRange = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.time = start

        while (calendar.time <= end) {
            dateRange.add(sdf.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }

        val accountValuesSeries = ArrayList<Pair<String, Double>>(dateRange.size)
        for (date in dateRange) {
            val balance = getBalance(user, date)
            accountValuesSeries.add(Pair(date, balance))
        }

        return accountValuesSeries
    }

}
