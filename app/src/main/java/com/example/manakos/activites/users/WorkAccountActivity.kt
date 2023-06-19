package com.example.manakos.activites.users

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.manakos.activites.Tenant.TenantItemActivity
import com.example.manakos.activites.other.EncryptPass
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityWorkAccountBinding
import com.example.manakos.models.Admin
import com.example.manakos.models.Tenant

class WorkAccountActivity : AppCompatActivity() {
    lateinit var settings: SharedPreferences
    lateinit var binding: ActivityWorkAccountBinding
    lateinit var databaseRequests: DatabaseRequests
    var id: Int = 0
    var role = ""
    var tenant = Tenant()
    private var admin = Admin()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)
        databaseRequests =  DatabaseRequests(this@WorkAccountActivity)
        fillData()

        binding.buttonBack.setOnClickListener(View.OnClickListener {
            back()
        })

        binding.buttonWork.setOnClickListener(View.OnClickListener {
            work()
        })

        binding.check.setOnClickListener(View.OnClickListener{
            binding.editTextPassword.isEnabled = binding.check.isChecked
            binding.editTextRePassword.isEnabled = binding.check.isChecked
            if(!binding.check.isChecked){
                binding.forPassword.visibility = View.GONE
            } else {
                binding.forPassword.visibility = View.VISIBLE
            }
        })

    }

    private fun fillData() {
        binding.forPassword.visibility = View.GONE
        binding.check.isChecked = false

        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()

        if(intent.getIntExtra("id", 0) != 0) {
            tenant = databaseRequests.selectTenantsFromId(intent.getIntExtra("id", 0))
            binding.editTextEmail.setText(tenant.email)
            if(tenant.password != null && tenant.password.isNotEmpty()) binding.txtPassword.setText("Password sudah ada")
            else binding.txtPassword.setText("Password belum ada")
        }
        else if(role == "tenant"){
            tenant = databaseRequests.selectTenantsFromId(id)
            binding.editTextEmail.setText(tenant.email)
            if(tenant.password != null && tenant.password.isNotEmpty()) binding.txtPassword.setText("Password sudah ada")
            else binding.txtPassword.setText("Password belum ada")
        } else {
            binding.textView2.text = "administrator"
            admin = databaseRequests.selectAdmin()
            binding.editTextEmail.setText(admin.email)
            binding.txtPassword.visibility = View.GONE
        }
    }

    private fun work() {
        val login = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val repassword = binding.editTextRePassword.text.toString()
        var error = ""

        if(login.isNotEmpty() && !isEmailValid(login)) error = "email"
        if(binding.check.isChecked) {
            if(password.length < 10) error = "password"
            if(password != repassword) error = "repassword"
        }
        var count = 0
        if(error != "email") {
            if(admin.id == 0 || admin.id == null) {
                count += databaseRequests.selectCountTenantsWhereEmailNotId(tenant.id_tenant, login)
                count += databaseRequests.selectCountAdminWhereEmail(login)
            }
            else {
                count += databaseRequests.selectCountTenantsWhereEmail(login)
            }
        }
        if(count != 0) error = "count"
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
        builder.setNegativeButton(
            "Оk",
            DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })

        if(error == "")
        {
            if(role == "tenant" || intent.getIntExtra("id", 0) != 0){
                tenant.email = login
                if(binding.check.isChecked) tenant.password = EncryptPass.SHA1(password)
                databaseRequests.updateTenantUser(tenant)
            } else {
                admin.email = login
                if(binding.check.isChecked) admin.password = EncryptPass.SHA1(password)
                databaseRequests.updateAdmin(admin)
            }
            back()
        }
        else{
            when(error){
                "count" ->
                    builder.setMessage("Email sudah digunakan.")
                "email" ->
                    builder.setMessage("Masukan email tidak valid: tidak sesuai dengan format email.")
                "password" ->
                    builder.setMessage("Masukan password tidak valid: minimal panjang string adalah 10 karakter.")
                "repassword" ->
                    builder.setMessage("Password tidak cocok.")
            }
            builder.show()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun back() {
        if(intent.getStringExtra("from") == "account" ){
            val i = Intent(this, AccountActivity::class.java)
            startActivity(i)
        } else{
            val i = Intent(this, TenantItemActivity::class.java)
            i.putExtra("id", tenant.id_tenant)
            startActivity(i)
        }
    }
}