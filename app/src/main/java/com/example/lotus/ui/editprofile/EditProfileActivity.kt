package com.example.lotus.ui.editprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import coil.transform.CircleCropTransformation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.lotus.R
import com.example.lotus.models.Respon
import com.example.lotus.models.User
import com.example.lotus.service.EnvService
import com.example.lotus.storage.SharedPrefManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.edit_profile_activity.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EditProfileActivity : AppCompatActivity() {
    private val myUserID = SharedPrefManager.getInstance(this).user._id
    private val token = SharedPrefManager.getInstance(this).token.token
    private val myUsername = SharedPrefManager.getInstance(this).user.username
    private var profileData: User? = null
    lateinit var profilePicture: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_activity)

        SelectPicture()
        getProfileData(myUsername.toString())
        Log.d("myUserID", myUserID.toString())
    }

    private fun SelectPicture() {
        val photoProfile = findViewById<TextView>(R.id.changeProfilePicture)
        photoProfile.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        profilePicture = data?.data!!
        imageEditProfile.load(profilePicture) {
            transformations(CircleCropTransformation())
        }
        Log.d("COBA CEK ", profilePicture.toString())
    }


    fun backToHome(view: View) {
        this.onBackPressed()
    }
//    fun UpdateProfilePict (view: View){
//        val updateUser = AndroidNetworking.upload(EnvService.ENV_API + "/users/{username}")
//        val avatar = File(profilePicture.path)
//        updateUser.addMultipartFile("profilePicture", avatar)
//        Log.d("COBA CEK ", avatar.toString())
//        Log.d("username",myUsername.toString() )
//        updateUser
//            .addPathParameter("username", myUsername)
//            .addMultipartParameter("email", EditProfileEmail.text.toString())
//            .addMultipartParameter("username", EditProfileUsername.text.toString())
//            .addMultipartParameter("phone", EditProfilePhone.text.toString())
//            .addMultipartParameter("bio", EditProfileBio.text.toString())
//            .addMultipartParameter("name", EditProfilename.text.toString())
//            .setTag(this)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsObject(
//                Respon::class.java,
//                object : ParsedRequestListener<Respon>{
//                    override fun onResponse(response: Respon) {
//                        if (response.code.toString() == "200"){
//                            Log.d("status", response.data.toString())
//                            Toast.makeText(this@EditProfileActivity,"Edit Profile Succes", Toast.LENGTH_LONG).show()
//                        }
//                    }
//                    override fun onError(anError: ANError?) {
//                        Log.d("ERROORORRRRR", anError.toString())
//                    }
//
//                }
//            )
//    }

    fun EditProfile(view: View) {
        val a = EditProfileUsername.text.toString()
        Log.d("newUname",EditProfileUsername.text.toString())
        val updateUser = AndroidNetworking.patch(EnvService.ENV_API + "/users/{username}")
        updateUser
            .addHeaders("Authorization", "Bearer $token")
            .addPathParameter("username", myUsername.toString())
            .addBodyParameter("email", EditProfileEmail.text.toString())
            .addBodyParameter("username", EditProfileUsername.text.toString())
            .addBodyParameter("phone", EditProfilePhone.text.toString())
            .addBodyParameter("bio", EditProfileBio.text.toString())
            .addBodyParameter("name", EditProfilename.text.toString())
            .setTag(this)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(response: Respon) {
                        if (response.code.toString() == "200") {
                            val user : User? = null
//                            user!!.username = EditProfileUsername.text.toString()
                            Log.d("New Username", a)
//                            if (user != null) {
//                                SharedPrefManager.getInstance(applicationContext)
//                                    .saveUser(user)
//                            }
                            Log.d("status", response.data.toString())
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Edit Profile Succes",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onError(anError: ANError?) {
                        Log.d("ERROORORRRRR", anError.toString())
                    }

                }
            )
    }

    private fun getProfileData(uname: String) {
        Log.d("UnameEditProfile", uname)
        AndroidNetworking.get(EnvService.ENV_API + "/users/{username}")
            .addHeaders("Authorization", "Bearer $token")
            .addPathParameter("username", uname)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(respon: Respon) {
                        val gson = Gson()
                        if (respon.code.toString() == "200") {
                            Log.d("Get Profile Data", "Success")
                            val jsonRes = gson.toJson(respon.data)
                            val data = gson.fromJson(jsonRes, User::class.java)
                            profileData = data
                            setProfile(data)
                            Log.d("DATANYA", respon.data.toString())
                        } else {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Error while getting data profile, code: ${respon.code} \n${respon.data}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("ERROR!!!", "Like Post ${respon.code}, ${respon.data}")
                        }
                    }

                    override fun onError(anError: ANError) {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Error while getting data profile, code: ${anError.errorDetail}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("ERROR!!!", "Like Post ${anError.errorCode}")

                    }
                })
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun setProfile(data: User) {
        if (data.avatar != null) {
            imageEditProfile.load(data.avatar) {
                crossfade(true)
                crossfade(300)
                transformations(CircleCropTransformation())
            }
        }

        EditProfilename.text = data.name!!.toEditable()
        EditProfileUsername.text = data.username!!.toEditable()
        EditProfileBio.text = data.bio!!.toEditable()
        EditProfileEmail.text = data.email!!.toEditable()
        EditProfilePhone.text = data.phone!!.toEditable()
    }

}