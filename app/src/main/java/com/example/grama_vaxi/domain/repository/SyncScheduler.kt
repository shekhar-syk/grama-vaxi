package com.example.grama_vaxi.domain.repository

interface SyncScheduler {
    fun enqueueImmediateSync()
    fun enqueuePeriodicSync()
}
