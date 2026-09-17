package br.ufrn.tanalista

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
