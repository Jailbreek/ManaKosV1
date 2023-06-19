package com.example.manakos.activites.users

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manakos.activites.other.MainActivity
import com.example.manakos.activites.other.MenuActivity
import com.example.manakos.activites.other.EncryptPass
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var settings: SharedPreferences
    lateinit var binding: ActivityLoginBinding
    lateinit var databaseRequests: DatabaseRequests
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseRequests =  DatabaseRequests(this@LoginActivity)
        settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)

        binding.buttonLogin.setOnClickListener(View.OnClickListener { onClickBtnLogin() })
        binding.buttonBack.setOnClickListener(View.OnClickListener { onClickBtnBack() })
    }

    private fun onClickBtnBack (){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun onClickBtnLogin (){
        val login = binding.editTextPersonName.text.toString()
        var password = binding.editTextPassword.text.toString()

        password = EncryptPass.SHA1(password)

        val tenant = databaseRequests.selectUserFromTenant(login, password)
        val admin = databaseRequests.selectUserFromAdmin(login, password)
        if(tenant.id_tenant != null && tenant.id_tenant != 0){
            val i = Intent(this, MenuActivity::class.java)
            startActivity(i)
            val editor: SharedPreferences.Editor = settings.edit()
            editor.putString("role", "tenant")
            editor.putInt("id", tenant.id_tenant)
            editor.putBoolean("is_logged", true).apply()
        } else if(admin.id != null && admin.id != 0) {
            val i = Intent(this, MenuActivity::class.java)
            startActivity(i)
            val editor: SharedPreferences.Editor = settings.edit()
            editor.putString("role", "admin").apply()
            editor.putInt("id", 0).apply()
            editor.putBoolean("is_logged", true).apply()
        }
        else{

            Toast.makeText(this, "Username atau password Salah!", Toast.LENGTH_SHORT).
            show()
        }
    }

    override fun onBackPressed() {
        return
    }
}