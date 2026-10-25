package com.safeops.app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "safeops_user_preferences")

interface UserPreferencesDataSource {
    val rememberMeFlow: Flow<Boolean>
    val lastUserEmailFlow: Flow<String?>
    val lastSelectedRoleFlow: Flow<String?>
    val isDarkModeFlow: Flow<Boolean?>

    suspend fun setRememberMe(rememberMe: Boolean)
    suspend fun setLastUserEmail(email: String?)
    suspend fun setLastSelectedRole(roleName: String)
    suspend fun setDarkMode(isDark: Boolean?)
    suspend fun clear()
}

class UserPreferencesDataSourceImpl(private val context: Context) : UserPreferencesDataSource {

    private object PreferencesKeys {
        val REMEMBER_ME = booleanPreferencesKey("pref_remember_me")
        val LAST_USER_EMAIL = stringPreferencesKey("pref_last_user_email")
        val LAST_SELECTED_ROLE = stringPreferencesKey("pref_last_selected_role")
        val DARK_MODE = booleanPreferencesKey("pref_dark_mode")
    }

    private val safePreferencesFlow: Flow<Preferences> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                emit(emptyPreferences())
            }
        }

    override val rememberMeFlow: Flow<Boolean> = safePreferencesFlow
        .map { preferences -> preferences[PreferencesKeys.REMEMBER_ME] ?: false }

    override val lastUserEmailFlow: Flow<String?> = safePreferencesFlow
        .map { preferences -> preferences[PreferencesKeys.LAST_USER_EMAIL] }

    override val lastSelectedRoleFlow: Flow<String?> = safePreferencesFlow
        .map { preferences -> preferences[PreferencesKeys.LAST_SELECTED_ROLE] }

    override val isDarkModeFlow: Flow<Boolean?> = safePreferencesFlow
        .map { preferences -> preferences[PreferencesKeys.DARK_MODE] }

    override suspend fun setRememberMe(rememberMe: Boolean) {
        try {
            context.userDataStore.edit { preferences ->
                preferences[PreferencesKeys.REMEMBER_ME] = rememberMe
            }
        } catch (_: Exception) {}
    }

    override suspend fun setLastUserEmail(email: String?) {
        try {
            context.userDataStore.edit { preferences ->
                if (email != null) {
                    preferences[PreferencesKeys.LAST_USER_EMAIL] = email
                } else {
                    preferences.remove(PreferencesKeys.LAST_USER_EMAIL)
                }
            }
        } catch (_: Exception) {}
    }

    override suspend fun setLastSelectedRole(roleName: String) {
        try {
            context.userDataStore.edit { preferences ->
                preferences[PreferencesKeys.LAST_SELECTED_ROLE] = roleName
            }
        } catch (_: Exception) {}
    }

    override suspend fun setDarkMode(isDark: Boolean?) {
        try {
            context.userDataStore.edit { preferences ->
                if (isDark != null) {
                    preferences[PreferencesKeys.DARK_MODE] = isDark
                } else {
                    preferences.remove(PreferencesKeys.DARK_MODE)
                }
            }
        } catch (_: Exception) {}
    }

    override suspend fun clear() {
        try {
            context.userDataStore.edit { preferences ->
                preferences.clear()
            }
        } catch (_: Exception) {}
    }
}
