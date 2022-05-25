package com.example.recycleview.adapter.usersAdapter

import android.app.Application

import com.example.recycleview.model.user.UsersService

class App: Application() {

    val usersService = UsersService()
}