package com.example.lotus.utils


import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.example.lotus.R
import com.example.lotus.models.Comment
import com.example.lotus.models.Photo
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by User on 9/22/2017.
 */
class MainFeedListAdapter(
    @NonNull context: Context,
    @LayoutRes resource: Int,
    @NonNull objects: List<ContactsContract.CommonDataKinds.Photo?>?
) :
    ArrayAdapter<Photo?>(context, resource,
        objects as MutableList<Photo?>
    ) {
    interface OnLoadMoreItemsListener {
        fun onLoadMoreItems()
    }

    var mOnLoadMoreItemsListener: OnLoadMoreItemsListener? = null
    private val mInflater: LayoutInflater
    private val mLayoutResource: Int
    private val mContext: Context
//    private val mReference: DatabaseReference
    private var currentUsername = ""

    class ViewHolder {
        var mprofileImage: CircleImageView? = null
        var likesString: String? = null
        var username: TextView? = null
        var timeDetla: TextView? = null
        var caption: TextView? = null
        var likes: TextView? = null
        var comments: TextView? = null
        var image: SquareImageView? = null
        var heartRed: ImageView? = null
        var heartWhite: ImageView? = null
        var comment: ImageView? = null
//        var settings: UserAccountSettings = UserAccountSettings()
//        var user: User = User()
        var users: StringBuilder? = null
        var mLikesString: String? = null
        var likeByCurrentUser = false
        var heart: Heart? = null
        var detector: GestureDetector? = null
        var photo: Photo? = null
    }

    @NonNull
    override fun getView(
        position: Int,
        @Nullable convertView: View?,
        @NonNull parent: ViewGroup
    ): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutResource, parent, false)
            holder = ViewHolder()
            holder.username =
                convertView.findViewById<View>(R.id.username) as TextView
            holder.image =
                convertView.findViewById<View>(R.id.post_image) as SquareImageView
            holder.heartRed =
                convertView.findViewById<View>(R.id.love_red) as ImageView
            holder.heartWhite =
                convertView.findViewById<View>(R.id.love_white) as ImageView
            holder.comment =
                convertView.findViewById<View>(R.id.comment_post) as ImageView
            holder.likes =
                convertView.findViewById<View>(R.id.image_likes) as TextView
            holder.comments =
                convertView.findViewById<View>(R.id.image_comments_link) as TextView
            holder.caption =
                convertView.findViewById<View>(R.id.image_caption) as TextView
            holder.timeDetla =
                convertView.findViewById<View>(R.id.image_time_posted) as TextView
            holder.mprofileImage =
                convertView.findViewById<View>(R.id.profile_photo) as CircleImageView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.photo = getItem(position)
        holder.detector = GestureDetector(mContext, GestureListener(holder))
        holder.users = StringBuilder()
        holder.heart = holder.heartWhite?.let { holder.heartRed?.let { it1 -> Heart(it, it1) } }

        //get the current users username (need for checking likes string)
        getCurrentUsername()

        //get likes string
        getLikesString(holder)

        //set the caption
        holder.caption?.setText(getItem(position)?.getCaption())

        //set the comment
        val comments: List<Comment> = getItem(position)!!.getComments()
        holder.comments!!.text = "View all " + comments.size + " comments"
//        holder.comments!!.setOnClickListener {
//            Log.d(
//                TAG,
//                "onClick: loading comment thread for " + getItem(position).getPhoto_id()
//            )
//            (mContext as HomeActivity).onCommentThreadSelected(
//                getItem(position),
//                mContext.getString(R.string.home_activity)
//            )
//
//            //going to need to do something else?
//            (mContext as HomeActivity).hideLayout()
//        }

        //set the time it was posted
        val timestampDifference = getTimestampDifference(getItem(position))
        if (timestampDifference != "0") {
            holder.timeDetla!!.text = "$timestampDifference DAYS AGO"
        } else {
            holder.timeDetla!!.text = "TODAY"
        }

        //set the profile image
        val imageLoader: ImageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(getItem(position)?.getImage_path(), holder.image)


        //get the profile image and username
//        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
//        val query: Query = reference
//            .child(mContext.getString(R.string.dbname_user_account_settings))
//            .orderByChild(mContext.getString(R.string.field_user_id))
//            .equalTo(getItem(position).getUser_id())
//        query.addListenerForSingleValueEvent(object : ValueEventListener() {
//            fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (singleSnapshot in dataSnapshot.getChildren()) {
//
//                    // currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();
//                    Log.d(
//                        TAG, "onDataChange: found user: "
//                                + singleSnapshot.getValue(UserAccountSettings::class.java)
//                            .getUsername()
//                    )
//                    holder.username.setText(
//                        singleSnapshot.getValue(UserAccountSettings::class.java).getUsername()
//                    )
//                    holder.username!!.setOnClickListener {
//                        Log.d(
//                            TAG,
//                            "onClick: navigating to profile of: " +
//                                    holder.user.getUsername()
//                        )
//                        val intent = Intent(mContext, ProfileActivity::class.java)
//                        intent.putExtra(
//                            mContext.getString(R.string.calling_activity),
//                            mContext.getString(R.string.home_activity)
//                        )
//                        intent.putExtra(mContext.getString(R.string.intent_user), holder.user)
//                        mContext.startActivity(intent)
//                    }
//                    imageLoader.displayImage(
//                        singleSnapshot.getValue(UserAccountSettings::class.java).getProfile_photo(),
//                        holder.mprofileImage
//                    )
//                    holder.mprofileImage.setOnClickListener(View.OnClickListener {
//                        Log.d(
//                            TAG,
//                            "onClick: navigating to profile of: " +
//                                    holder.user.getUsername()
//                        )
//                        val intent = Intent(mContext, ProfileActivity::class.java)
//                        intent.putExtra(
//                            mContext.getString(R.string.calling_activity),
//                            mContext.getString(R.string.home_activity)
//                        )
//                        intent.putExtra(mContext.getString(R.string.intent_user), holder.user)
//                        mContext.startActivity(intent)
//                    })
//                    holder.settings = singleSnapshot.getValue(UserAccountSettings::class.java)
//                    holder.comment!!.setOnClickListener {
//                        (mContext as HomeActivity).onCommentThreadSelected(
//                            getItem(position),
//                            mContext.getString(R.string.home_activity)
//                        )
//
//                        //another thing?
//                        (mContext as HomeActivity).hideLayout()
//                    }
//                }
//            }
//
//            fun onCancelled(databaseError: DatabaseError?) {}
//        })
//
//        //get the user object
//        val userQuery: Query = mReference
//            .child(mContext.getString(R.string.dbname_users))
//            .orderByChild(mContext.getString(R.string.field_user_id))
//            .equalTo(getItem(position).getUser_id())
//        userQuery.addListenerForSingleValueEvent(object : ValueEventListener() {
//            fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (singleSnapshot in dataSnapshot.getChildren()) {
//                    Log.d(
//                        TAG, "onDataChange: found user: " +
//                                singleSnapshot.getValue(User::class.java).getUsername()
//                    )
//                    holder.user = singleSnapshot.getValue(User::class.java)
//                }
//            }
//
//            fun onCancelled(databaseError: DatabaseError?) {}
//        })
//        if (reachedEndOfList(position)) {
//            loadMoreData()
//        }
        return convertView!!
    }

    private fun reachedEndOfList(position: Int): Boolean {
        return position == count - 1
    }

    private fun loadMoreData() {
        try {
            mOnLoadMoreItemsListener = context as OnLoadMoreItemsListener
        } catch (e: ClassCastException) {
            Log.e(
                TAG,
                "loadMoreData: ClassCastException: " + e.message
            )
        }
        try {
            mOnLoadMoreItemsListener!!.onLoadMoreItems()
        } catch (e: NullPointerException) {
            Log.e(
                TAG,
                "loadMoreData: ClassCastException: " + e.message
            )
        }
    }

    inner class GestureListener(var mHolder: ViewHolder) :
        SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.d(
                TAG,
                "onDoubleTap: double tap detected."
            )
            Log.d(
                TAG,
                "onDoubleTap: clicked on photo: " + (mHolder.photo?.getPhoto_id())
            )
//            val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
//            val query: DownloadManager.Query = reference
//                .child(mContext.getString(R.string.dbname_photos))
//                .child(mHolder.photo.getPhoto_id())
//                .child(mContext.getString(R.string.field_likes))
//            query.addListenerForSingleValueEvent(object : ValueEventListener() {
//                fun onDataChange(dataSnapshot: DataSnapshot) {
//                    for (singleSnapshot in dataSnapshot.getChildren()) {
//                        val keyID: String = singleSnapshot.getKey()
//
//                        //case1: Then user already liked the photo
//                        if (mHolder.likeByCurrentUser //                                && singleSnapshot.getValue(Like.class).getUser_id()
//                        //                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                        ) {
//                            mReference.child(mContext.getString(R.string.dbname_photos))
//                                .child(mHolder.photo.getPhoto_id())
//                                .child(mContext.getString(R.string.field_likes))
//                                .child(keyID)
//                                .removeValue()
//                            ///
//                            mReference.child(mContext.getString(R.string.dbname_user_photos)) //                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                .child(mHolder.photo.getUser_id())
//                                .child(mHolder.photo.getPhoto_id())
//                                .child(mContext.getString(R.string.field_likes))
//                                .child(keyID)
//                                .removeValue()
//                            mHolder.heart.toggleLike()
//                            getLikesString(mHolder)
//                        } else if (!mHolder.likeByCurrentUser) {
//                            //add new like
//                            addNewLike(mHolder)
//                            break
//                        }
//                    }
//                    if (!dataSnapshot.exists()) {
//                        //add new like
//                        addNewLike(mHolder)
//                    }
//                }
//
//                fun onCancelled(databaseError: DatabaseError?) {}
//            })
            return true
        }

    }

    private fun addNewLike(holder: ViewHolder) {
        Log.d(TAG, "addNewLike: adding new like")
//        val newLikeID: String = mReference.push().getKey()
//        val like = Like()
//        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid())
//        mReference.child(mContext.getString(R.string.dbname_photos))
//            .child(holder.photo.getPhoto_id())
//            .child(mContext.getString(R.string.field_likes))
//            .child(newLikeID)
//            .setValue(like)
//        mReference.child(mContext.getString(R.string.dbname_user_photos))
//            .child(holder.photo.getUser_id())
//            .child(holder.photo.getPhoto_id())
//            .child(mContext.getString(R.string.field_likes))
//            .child(newLikeID)
//            .setValue(like)
        holder.heart?.toggleLike()
        getLikesString(holder)
    }

    private fun getCurrentUsername() {
        Log.d(
            TAG,
            "getCurrentUsername: retrieving user account settings"
        )
//        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
//        val query: Query = reference
//            .child(mContext.getString(R.string.dbname_users))
//            .orderByChild(mContext.getString(R.string.field_user_id))
//            .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
//        query.addListenerForSingleValueEvent(object : ValueEventListener() {
//            fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (singleSnapshot in dataSnapshot.getChildren()) {
//                    currentUsername =
//                        singleSnapshot.getValue(UserAccountSettings::class.java).getUsername()
//                }
//            }
//
//            fun onCancelled(databaseError: DatabaseError?) {}
//        })
    }

    private fun getLikesString(holder: ViewHolder) {
        Log.d(
            TAG,
            "getLikesString: getting likes string"
        )
        Log.d(
            TAG,
            "getLikesString: photo id: " + (holder.photo?.getPhoto_id())
        )
//        try {
//            val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
//            val query: Query = reference
//                .child(mContext.getString(R.string.dbname_photos))
//                .child(holder.photo.getPhoto_id())
//                .child(mContext.getString(R.string.field_likes))
//            query.addListenerForSingleValueEvent(object : ValueEventListener() {
//                fun onDataChange(dataSnapshot: DataSnapshot) {
//                    holder.users = StringBuilder()
//                    for (singleSnapshot in dataSnapshot.getChildren()) {
//                        val reference: DatabaseReference =
//                            FirebaseDatabase.getInstance().getReference()
//                        val query: Query = reference
//                            .child(mContext.getString(R.string.dbname_users))
//                            .orderByChild(mContext.getString(R.string.field_user_id))
//                            .equalTo(singleSnapshot.getValue(Like::class.java).getUser_id())
//                        query.addListenerForSingleValueEvent(object : ValueEventListener() {
//                            fun onDataChange(dataSnapshot: DataSnapshot) {
//                                for (singleSnapshot in dataSnapshot.getChildren()) {
//                                    Log.d(
//                                        TAG,
//                                        "onDataChange: found like: " +
//                                                singleSnapshot.getValue(User::class.java)
//                                                    .getUsername()
//                                    )
//                                    holder.users.append(
//                                        singleSnapshot.getValue(User::class.java).getUsername()
//                                    )
//                                    holder.users!!.append(",")
//                                }
//                                val splitUsers =
//                                    holder.users.toString().split(",".toRegex()).toTypedArray()
//                                if (holder.users.toString()
//                                        .contains("$currentUsername,")
//                                ) { //mitch, mitchell.tabian
//                                    holder.likeByCurrentUser = true
//                                } else {
//                                    holder.likeByCurrentUser = false
//                                }
//                                val length = splitUsers.size
//                                if (length == 1) {
//                                    holder.likesString = "Liked by " + splitUsers[0]
//                                } else if (length == 2) {
//                                    holder.likesString = ("Liked by " + splitUsers[0]
//                                            + " and " + splitUsers[1])
//                                } else if (length == 3) {
//                                    holder.likesString = ("Liked by " + splitUsers[0]
//                                            + ", " + splitUsers[1]
//                                            + " and " + splitUsers[2])
//                                } else if (length == 4) {
//                                    holder.likesString = ("Liked by " + splitUsers[0]
//                                            + ", " + splitUsers[1]
//                                            + ", " + splitUsers[2]
//                                            + " and " + splitUsers[3])
//                                } else if (length > 4) {
//                                    holder.likesString = ("Liked by " + splitUsers[0]
//                                            + ", " + splitUsers[1]
//                                            + ", " + splitUsers[2]
//                                            + " and " + (splitUsers.size - 3) + " others")
//                                }
//                                Log.d(
//                                    TAG,
//                                    "onDataChange: likes string: " + holder.likesString
//                                )
//                                //setup likes string
//                                setupLikesString(holder, holder.likesString)
//                            }
//
//                            fun onCancelled(databaseError: DatabaseError?) {}
//                        })
//                    }
//                    if (!dataSnapshot.exists()) {
//                        holder.likesString = ""
//                        holder.likeByCurrentUser = false
//                        //setup likes string
//                        setupLikesString(holder, holder.likesString)
//                    }
//                }
//
//                fun onCancelled(databaseError: DatabaseError?) {}
//            })
//        } catch (e: NullPointerException) {
//            Log.e(
//                TAG,
//                "getLikesString: NullPointerException: " + e.message
//            )
//            holder.likesString = ""
//            holder.likeByCurrentUser = false
//            //setup likes string
//            setupLikesString(holder, holder.likesString)
//        }
    }

    private fun setupLikesString(
        holder: ViewHolder,
        likesString: String?
    ) {
        Log.d(
            TAG,
            "setupLikesString: likes string:" + holder.likesString
        )
        Log.d(
            TAG,
            "setupLikesString: photo id: " + (holder.photo!!.getPhoto_id())
        )
        if (holder.likeByCurrentUser) {
            Log.d(
                TAG,
                "setupLikesString: photo is liked by current user"
            )
            holder.heartWhite!!.visibility = View.GONE
            holder.heartRed!!.visibility = View.VISIBLE
            holder.heartRed!!.setOnTouchListener { v, event -> holder.detector!!.onTouchEvent(event) }
        } else {
            Log.d(
                TAG,
                "setupLikesString: photo is not liked by current user"
            )
            holder.heartWhite!!.visibility = View.VISIBLE
            holder.heartRed!!.visibility = View.GONE
            holder.heartWhite!!.setOnTouchListener { v, event ->
                holder.detector!!.onTouchEvent(
                    event
                )
            }
        }
        holder.likes!!.text = likesString
    }

    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private fun getTimestampDifference(photo: Photo?): String {
        Log.d(
            TAG,
            "getTimestampDifference: getting timestamp difference."
        )
        var difference = ""
        val c = Calendar.getInstance()
        val sdf =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA)
        sdf.timeZone = TimeZone.getTimeZone("Canada/Pacific") //google 'android list of timezones'
        val today = c.time
        sdf.format(today)
        val timestamp: Date
        val photoTimestamp: String = photo!!.getDate_created()
        try {
            timestamp = sdf.parse(photoTimestamp)
            difference =
                Math.round(((today.time - timestamp.time) / 1000 / 60 / 60 / 24).toFloat())
                    .toString()
        } catch (e: ParseException) {
            Log.e(
                TAG,
                "getTimestampDifference: ParseException: " + e.message
            )
            difference = "0"
        }
        return difference
    }

    companion object {
        private const val TAG = "MainFeedListAdapter"
    }

    init {
        mInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mLayoutResource = resource
        mContext = context
//        mReference = FirebaseDatabase.getInstance().getReference()

//        for(Photo photo: objects){
//            Log.d(TAG, "MainFeedListAdapter: photo id: " + photo.getPhoto_id());
//        }
    }
}