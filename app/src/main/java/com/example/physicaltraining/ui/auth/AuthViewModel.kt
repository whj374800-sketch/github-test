package com.example.physicaltraining.ui.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthState(
    val isLoggedIn: Boolean = false,
    val uid: String? = null,
    val email: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(
        AuthState(
            isLoggedIn = auth.currentUser != null,
            uid = auth.currentUser?.uid,
            email = auth.currentUser?.email
        )
    )
    val authState = _authState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser

        _authState.value = AuthState(
            isLoggedIn = user != null,
            uid = user?.uid,
            email = user?.email,
            isLoading = false,
            errorMessage = null
        )
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.update {
                it.copy(errorMessage = "이메일과 비밀번호를 입력하세요.")
            }
            return
        }

        _authState.update {
            it.copy(isLoading = true, errorMessage = null)
        }

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    _authState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = task.exception?.message ?: "로그인에 실패했습니다."
                        )
                    }
                }
            }
    }

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.update {
                it.copy(errorMessage = "이메일과 비밀번호를 입력하세요.")
            }
            return
        }

        if (password.length < 6) {
            _authState.update {
                it.copy(errorMessage = "비밀번호는 최소 6자리 이상이어야 합니다.")
            }
            return
        }

        _authState.update {
            it.copy(isLoading = true, errorMessage = null)
        }

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    _authState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = task.exception?.message ?: "회원가입에 실패했습니다."
                        )
                    }
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }
}