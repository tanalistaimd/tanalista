package com.example.tanalista

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform