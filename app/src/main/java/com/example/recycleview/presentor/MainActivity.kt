package com.example.recycleview.presentor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recycleview.UserActionListener
import com.example.recycleview.UsersAdapter
import com.example.recycleview.activity.Messanger
import com.example.recycleview.adapter.usersAdapter.App
import com.example.recycleview.databinding.ActivityMainBinding
import com.example.recycleview.model.user.User
import com.example.recycleview.model.user.UsersListener
import com.example.recycleview.model.user.UsersService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UsersAdapter

    private val usersService: UsersService
        get() = (applicationContext as App).usersService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutManager = LinearLayoutManager(this)
        adapter = UsersAdapter(object : UserActionListener {
            override fun onUserMove(user: User, moveBy: Int, userPosition: Int) {
                usersService.moveUser(user, moveBy)
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (userPosition == firstVisibleItemPosition || (userPosition == firstVisibleItemPosition + 1 && moveBy < 0)) {
                    val v = binding.recyclerView.getChildAt(0)
                    val offset = if (v == null) 0 else v.top - binding.recyclerView.paddingTop
                    layoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, offset)
                }


            }

            override fun onUserDelete(user: User) {
                usersService.deleteUser(user)
            }

            override fun onUSerDetails(user: User) {
                val intent = Intent(this@MainActivity, Messanger::class.java).apply {
                    putExtra("photo", user.photo)
                    putExtra("name", user.name)

                }
                startActivity(intent)
            }

        })


        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        val itemAnimator = binding.recyclerView.itemAnimator

        if (itemAnimator is DefaultItemAnimator) {
            itemAnimator.supportsChangeAnimations
        }

        usersService.addListener(usersListener)


        binding.swipeRefrechLayout.setOnRefreshListener {
            usersService.addListener(usersListener)
            adapter.notifyDataSetChanged()
            binding.swipeRefrechLayout.isRefreshing = false

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        usersService.removeListener(usersListener)
    }

    private val usersListener: UsersListener = {
        adapter.users = it
    }


}