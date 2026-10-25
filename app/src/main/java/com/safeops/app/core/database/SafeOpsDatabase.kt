package com.safeops.app.core.database

/**
 * Offline Sync and Local Storage Foundation contracts.
 * Prepares the architecture for future Room entities and offline synchronization without premature complexity.
 */
enum class SyncStatus {
    DRAFT,
    PENDING_SYNC,
    SYNCING,
    SYNCED,
    SYNC_FAILED
}

interface OfflineSyncable {
    val id: String
    val syncStatus: SyncStatus
    val lastModifiedTimestamp: Long
}

interface LocalSyncQueue {
    suspend fun enqueueItem(type: String, itemId: String)
    suspend fun getPendingItems(): List<String>
    suspend fun markSynced(itemId: String)
}
