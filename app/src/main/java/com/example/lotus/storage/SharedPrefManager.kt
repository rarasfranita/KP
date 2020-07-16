package com.example.lotus.storage

import android.content.Context
import com.example.lotus.models.DataUser
import com.example.lotus.models.User

class SharedPrefManager private constructor(private val mCtx: Context) {

    val isLoggedIn: Boolean
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getInt("id", -1) != -1
        }

//    val data: DataUser
//        get() {
//            val sharedPreferences =
//                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//            return DataUser(
//                sharedPreferences.getString("string", null)
////                sharedPreferences.all(data.user)
//            )
//        }

    val user: User
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return User(
                sharedPreferences.getInt("_v", user.__v!!),
                sharedPreferences.getString("_id", null),
                sharedPreferences.getString("avatar", null),
                sharedPreferences.getString("bio", null),
                sharedPreferences.getBoolean("createdAt", user.createdAt!!),
                sharedPreferences.getBoolean("deleted", user.deleted!!),
                sharedPreferences.getString("email", null),
                sharedPreferences.getBoolean("emailVerified", user.emailVerified!!),
                sharedPreferences.getString("name", null),
                sharedPreferences.getString("password", null),
                sharedPreferences.getString("phone", null),
                sharedPreferences.getInt("postsCount", user.postsCount!!),
                sharedPreferences.getString("updatedAt", null),
                sharedPreferences.getString("username", null)
            )
        }

    fun saveUser(user: User) {

        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("_id", null)
        editor.putString("avatar", null)
        editor.putString("bio", null)
        editor.putString("email", null)
        editor.putString("name", null)
        editor.putString("phone", null)
        editor.putInt("postsCount", user.postsCount!!)
        editor.putString("updatedAt", null)
        editor.putString("username", null)

        editor.apply()

    }

    fun clear() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private val SHARED_PREF_NAME = "lotus"
        private var mInstance: SharedPrefManager? = null

        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }

}