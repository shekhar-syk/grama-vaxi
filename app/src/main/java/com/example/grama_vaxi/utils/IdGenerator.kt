package com.example.grama_vaxi.utils

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdGenerator @Inject constructor() {
    fun newId(): String = UUID.randomUUID().toString()
}
