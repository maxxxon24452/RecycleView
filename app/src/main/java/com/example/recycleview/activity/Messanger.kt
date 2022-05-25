package com.example.recycleview.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recycleview.MessangerAdapter
import com.example.recycleview.UserActionListenerr
import com.example.recycleview.adapter.usersAdapter.App
import com.example.recycleview.databinding.ActivityMessangerBinding
import com.example.recycleview.model.user.User
import com.example.recycleview.model.user.UsersListener
import com.example.recycleview.model.user.UsersService

import com.squareup.picasso.Picasso


class Messanger : AppCompatActivity() {
    private lateinit var binding: ActivityMessangerBinding
    private lateinit var adapter: MessangerAdapter

    private val messangService: UsersService
        get() = (applicationContext as App).usersService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessangerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutManager = LinearLayoutManager(this)
        adapter = MessangerAdapter(object : UserActionListenerr {
            override fun onUserMove(user: User, moveBy: Int, userPosition: Int) {
                messangService.moveUser(user, moveBy)
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (userPosition == firstVisibleItemPosition || (userPosition == firstVisibleItemPosition + 1 && moveBy < 0)) {
                    val v = binding.recyclerViewMessanger.getChildAt(0)
                    val offset = if (v == null) 0 else v.top - binding.recyclerViewMessanger.paddingTop
                    layoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, offset)
                }


            }

            override fun onUserDelete(user: User) {
                messangService.deleteUser(user)
            }

            override fun onUSerDetails(user: User) {

            }

        })


        binding.recyclerViewMessanger.layoutManager = layoutManager
        binding.recyclerViewMessanger.adapter = adapter
        val itemAnimator = binding.recyclerViewMessanger.itemAnimator

        if (itemAnimator is DefaultItemAnimator) {
            itemAnimator.supportsChangeAnimations
        }


        var photo = intent.getStringExtra("photo")
        var name = intent.extras?.getString("name")


        binding.userNameTextViewMessanger.text = name
        Picasso.get().load(photo).into( binding.photoImageViewMessanger)

        binding.swipeRefrechLayoutMessanger.setOnRefreshListener {



            adapter.notifyDataSetChanged()
            binding.swipeRefrechLayoutMessanger.isRefreshing = false

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        messangService.removeListener(usersListener)
    }

    private val usersListener: UsersListener = {
        adapter.users = it
    }

}


