package com.example.lotus.ui.editprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
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
import com.example.lotus.ui.explore.general.GeneralActivity
import com.example.lotus.ui.profile.ProfileActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.edit_profile_activity.*
import java.io.File

// TODO: 12/08/20 add DOB and GENDER
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EditProfileActivity : AppCompatActivity() {
    private val myUserID = SharedPrefManager.getInstance(this).user._id
    private val token = SharedPrefManager.getInstance(this).token.token
    private val myUsername = SharedPrefManager.getInstance(this).user.username
    private val myPass = SharedPrefManager.getInstance(this).user.password
    private val myPostCount = SharedPrefManager.getInstance(this).user.postsCount

    private var profileData: User? = null
    var changeProfilePicture: Uri? = null
    var profPictUpdate : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_activity)

        SelectPicture()
        Update()
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
        changeProfilePicture = data?.data!!
        profPictUpdate = changeProfilePicture
        imageEditProfile.load(changeProfilePicture) {
            transformations(CircleCropTransformation())
        }
        Log.d("COBA CEK ", changeProfilePicture.toString())
        Log.d("COBA profPictUpdate ", profPictUpdate.toString())
        val update = findViewById<ImageView>(R.id.updateProfile)

        if (profPictUpdate != null) {
            update.setOnClickListener {
                UpdateProfile()
                Log.d("UpdateProfile", "terpencet")
            }
        }
    }


    fun backToHome(view: View) {
        this.onBackPressed()
    }

    fun Update() {
        val update = findViewById<ImageView>(R.id.updateProfile)
            update.setOnClickListener {
                UpdateProfile2()
                Log.d("UpdateProfile2", "terpencet")
            }

    }

    fun UpdateProfile() {
        val load = findViewById<ProgressBar>(R.id.loadingUpdate)
        load.visibility = View.VISIBLE
        val updateUser = AndroidNetworking.upload(EnvService.ENV_API + "/users/{username}")
        val avatar = File(profPictUpdate?.path)
        Log.d("COBA CEK profPictUpdate UPDATE ", avatar.toString())

        updateUser
            .addHeaders("Authorization", "Bearer $token")
            .addPathParameter("username", myUsername.toString())
            .addMultipartFile("profilePicture", avatar)
            .addMultipartParameter("email", EditProfileEmail.text.toString())
            .addMultipartParameter("username", EditProfileUsername.text.toString())
            .addMultipartParameter("phone", EditProfilePhone.text.toString())
            .addMultipartParameter("bio", EditProfileBio.text.toString())
            .addMultipartParameter("name", EditProfilename.text.toString())
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(response: Respon) {
                        if (response.code.toString() == "200") {
                            load.visibility = View.GONE
                            val user = User(
                                0,
                                myUserID,
                                "",
                                EditProfileBio.text.toString(),
                                createdAt = false,
                                deleted = false,
                                email = EditProfileEmail.text.toString(),
                                emailVerified = false,
                                name = EditProfilename.text.toString(),
                                password = myPass,
                                phone = EditProfilePhone.text.toString(),
                                postsCount = myPostCount,
                                updatedAt = "",
                                username = EditProfileUsername.text.toString()
                            )
                            SharedPrefManager.getInstance(applicationContext).saveUser(user)
                            Log.d("cekSaveEmail", user.email.toString())
                            Log.d("status", response.data.toString())
                            Log.d("cekUser", user.toString())
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Edit Profile Succes",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(applicationContext, ProfileActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onError(anError: ANError?) {
                        load.visibility = View.GONE
                        Log.d("ERROORORRRRR", anError.toString())
                    }

                })
    }

    fun UpdateProfile2() {
        val load = findViewById<ProgressBar>(R.id.loadingUpdate)
        val updateUser = AndroidNetworking.upload(EnvService.ENV_API + "/users/{username}")
        load.visibility = View.VISIBLE
        updateUser
            .addHeaders("Authorization", "Bearer $token")
            .addPathParameter("username", myUsername.toString())
            .addMultipartParameter("email", EditProfileEmail.text.toString())
            .addMultipartParameter("username", EditProfileUsername.text.toString())
            .addMultipartParameter("phone", EditProfilePhone.text.toString())
            .addMultipartParameter("bio", EditProfileBio.text.toString())
            .addMultipartParameter("name", EditProfilename.text.toString())
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                Respon::class.java,
                object : ParsedRequestListener<Respon> {
                    override fun onResponse(response: Respon) {
                        load.visibility = View.GONE
                        if (response.code.toString() == "200") {
                            val user = User(
                                0,
                                myUserID,
                                "",
                                EditProfileBio.text.toString(),
                                createdAt = false,
                                deleted = false,
                                email = EditProfileEmail.text.toString(),
                                emailVerified = false,
                                name = EditProfilename.text.toString(),
                                password = myPass,
                                phone = EditProfilePhone.text.toString(),
                                postsCount = myPostCount,
                                updatedAt = "",
                                username = EditProfileUsername.text.toString()
                            )
                            SharedPrefManager.getInstance(applicationContext).saveUser(user)
                            Log.d("cekSaveEmail", user.email.toString())
                            Log.d("status", response.data.toString())
                            Log.d("cekUser", user.toString())
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Edit Profile Succes",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(applicationContext, ProfileActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onError(anError: ANError?) {
                        load.visibility = View.GONE
                        Log.d("ERROORORRRRR", anError.toString())
                    }

                })
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