package com.example.lotus.storage

import android.content.Context
import android.util.Log
import com.example.lotus.models.Post
import com.example.lotus.models.Token
import com.example.lotus.models.User
import com.google.gson.Gson

class SharedPrefManager private constructor(private val mCtx: Context) {

    //for cek login
    val isLoggedIn: Boolean
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("_id", "wkwk") != "wkwk"
        }

    // for get token
    val token: Token
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return Token(
                sharedPreferences.getString("token", null)
            )
        }


    val user: User
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return User(
                sharedPreferences.getInt("__v", 0),
                sharedPreferences.getString("_id", "wkwk"),
                sharedPreferences.getString("avatar", null),
                sharedPreferences.getString("bio", null),
                sharedPreferences.getBoolean("createdAt", false),
                sharedPreferences.getBoolean("deleted", false),
                sharedPreferences.getString("email", null),
                sharedPreferences.getBoolean("emailVerified", false),
                sharedPreferences.getString("name", null),
                sharedPreferences.getString("password", null),
                sharedPreferences.getString("phone", null),
                sharedPreferences.getInt("postsCount", 0),
                sharedPreferences.getString("updatedAt", null),
                sharedPreferences.getString("username", null)
            )
        }

    fun saveUser(user: User) {

        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("_id", user._id)
        editor.putString("avatar", user.avatar)
        editor.putString("bio", user.bio)
        editor.putString("email", user.email)
        editor.putString("name", user.name)
        editor.putString("phone", user.phone)
        editor.putInt("postsCount", user.postsCount!!)
        editor.putString("username", user.username)

        editor.apply()

    }

    fun saveToken(token: Token) {

        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("token", token.token)
        editor.apply()

    }


    //for logout
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
        }    }

    fun setCachePost(post: ArrayList<Post>){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val jsonString = Gson().toJson(post)
        Log.d("CATAT 2 ", jsonString)
        val set: MutableSet<String> = HashSet()
        set.addAll(listOf(jsonString))
        editor.putStringSet("cache_post", set)
        editor.apply()
    }

    val cachePost: MutableSet<String>?
        get() {
            val sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val value = sharedPreferences.getStringSet("cache_post", null)
            Log.d("VALUENYA", value.toString())
//            val result = Gson().fromJson(value, ArrayList<Post>()::class.java)

//            Log.d("COKCOCKOCK", result[0].toString())
//            for (res in result){
//                Log.d("Test result", res)
//            }

            return value
        }

}
