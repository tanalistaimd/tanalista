package br.ufrn.tanalista

actual fun getPlatform(): Platform =
    object : Platform {
        override val name: String = "Desktop (Java ${System.getProperty("java.version")})"
    }
