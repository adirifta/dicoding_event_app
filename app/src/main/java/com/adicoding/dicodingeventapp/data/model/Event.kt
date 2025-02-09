package com.adicoding.dicodingeventapp.data.model

data class Event(
    val id: Int,
    val name: String,
    val description: String,
    val imageLogo: String,
    val mediaCover: String,
    val ownerName: String,
    val beginTime: String,
    val quota: Int,
    val registrants: Int,
    val link: String
)