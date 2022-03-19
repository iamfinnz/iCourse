package com.example.icourse.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.icourse.R
import com.example.icourse.adapter.MaterialsAdapter
import com.example.icourse.databinding.ActivityMainBinding
import com.example.icourse.model.Material
import com.example.icourse.model.User
import com.example.icourse.presentation.content.ContentActivity
import com.example.icourse.presentation.user.UserActivity
import com.example.icourse.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_POSITION = "extra_position"
    }

    var sampleImages = intArrayOf(
        R.drawable.cs1,
        R.drawable.cs2,
        R.drawable.cs3
    )

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var materialsAdapter: MaterialsAdapter
    private lateinit var userDatabase: DatabaseReference
    private lateinit var materialDatabase: DatabaseReference
    private var currentUser: FirebaseUser? = null
    
    private var listenerUser = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            hideLoading()
            val user = snapshot.getValue(User::class.java)
            user?.let {
                mainBinding.apply {
                    tvNameUserMain.text = it.nameUser

                    Glide
                        .with(this@MainActivity)
                        .load(it.avatarUser)
                        .placeholder(android.R.color.darker_gray)
                        .into(ivAvatarMain)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            hideLoading()
            Log.e("MainActivity", "[onCancelled] - ${error.message}")
            showDialogError(this@MainActivity, error.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)


        //Init
        materialsAdapter = MaterialsAdapter()
        userDatabase = FirebaseDatabase.getInstance().getReference("users")
        materialDatabase = FirebaseDatabase.getInstance().getReference("materials")
        currentUser = FirebaseAuth.getInstance().currentUser

        val carouselView = findViewById(R.id.carouselView) as CarouselView;
        carouselView.setPageCount(sampleImages.size);
        carouselView.setImageListener(imageListener);

        getDataFirebase()
        onAction()
    }

    var imageListener: ImageListener = object : ImageListener {
        override fun setImageForPosition(position: Int, imageView: ImageView) {
            // You can use Glide or Picasso here
            imageView.setImageResource(sampleImages[position])
        }
    }

    private var listenerMaterials = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            hideLoading()
            if (snapshot.value != null){
                showData()
                val json = Gson().toJson(snapshot.value)
                val type = object : TypeToken<MutableList<Material>>() {}.type
                val materials = Gson().fromJson<MutableList<Material>>(json, type)

                materials?.let { materialsAdapter.materials = it }
            } else {
                showEmptyData()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            hideLoading()
            Log.e("MainActivity", "[onCancelled] - ${error.message}")
            showDialogError(this@MainActivity, error.message)
        }

    }

    private fun showEmptyData() {
        mainBinding.apply {
            ivEmptyData.visible()
            etSearchMain.disable()
            rvMaterialsMain.gone()
        }
    }

    private fun showData() {
        mainBinding.apply {
            ivEmptyData.gone()
            etSearchMain.enabled()
            rvMaterialsMain.visible()
        }
    }

    private fun getDataFirebase() {
        showLoading()
        userDatabase
            .child(currentUser?.uid.toString())
            .addValueEventListener(listenerUser)

        materialDatabase
            .addValueEventListener(listenerMaterials)

        mainBinding.rvMaterialsMain.adapter = materialsAdapter
    }

    override fun onResume() {
        super.onResume()
        if (intent != null){
            val position = intent.getIntExtra(EXTRA_POSITION, 0)
            mainBinding.rvMaterialsMain.smoothScrollToPosition(position)
        }
    }


    private fun showLoading() {
        mainBinding.swipeMain.isRefreshing = true
    }

    private fun hideLoading() {
        mainBinding.swipeMain.isRefreshing = false
    }

    private fun onAction() {
        mainBinding.apply {
            ivAvatarMain.setOnClickListener {
                startActivity<UserActivity>()
            }

            etSearchMain.addTextChangedListener {
                materialsAdapter.filter.filter(it.toString())
            }

            etSearchMain.setOnEditorActionListener{_, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    val dataSearch = etSearchMain.text.toString().trim()
                    materialsAdapter.filter.filter(dataSearch)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            swipeMain.setOnRefreshListener {
                getDataFirebase()
            }
        }

        materialsAdapter.onClick { material, position ->
            startActivity<ContentActivity>(
                ContentActivity.EXTRA_MATERIAL to material,
                ContentActivity.EXTRA_POSITION to position
            )
        }
    }
}