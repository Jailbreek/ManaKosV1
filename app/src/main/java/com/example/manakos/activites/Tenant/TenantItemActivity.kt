package com.example.manakos.activites.Tenant

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manakos.activites.users.WorkAccountActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityTenantItemBinding
import com.example.manakos.models.Flat
import com.example.manakos.models.Tenant
import com.example.manakos.recyclerview.FlatViewAdapter
import java.time.format.DateTimeFormatter


class TenantItemActivity : AppCompatActivity() {
    lateinit var binding: ActivityTenantItemBinding

    private var flats = ArrayList<Flat>()
    private var tenant: Tenant = Tenant()
    private lateinit var databaseRequests: DatabaseRequests

    var role = ""
    lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTenantItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)
        databaseRequests =  DatabaseRequests(this@TenantItemActivity)

        binding.recycleViewFlats.layoutManager = LinearLayoutManager(this@TenantItemActivity)
        fillData(intent.getIntExtra("id", 0))
        binding.update.setOnClickListener(View.OnClickListener {
            updateTenant()
        })
        binding.delete.setOnClickListener(View.OnClickListener {
            deleteTenant()
        })

        binding.user.setOnClickListener(View.OnClickListener {
            val i = Intent(this@TenantItemActivity, WorkAccountActivity::class.java)
            i.putExtra("id",tenant.id_tenant)
            startActivity(i)
        })
        checkUser()
    }

    fun fillData(id: Int){
        val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        tenant = databaseRequests.selectTenantsFromId(id)

        binding.txtVwFullName.text = tenant.full_name
        binding.txtVwDateOfRegistration.text = tenant.date_of_registration.format(dtf)
        binding.txtVwNumberFamileMembers.text = tenant.number_of_family_members.toString()
        binding.txtVwPhoneNumber.text = tenant.phone_number
        binding.txtVwEmail.text = tenant.email

        flats = databaseRequests.selectFlatsFromIdTenant(tenant.id_tenant)
        val adapter = FlatViewAdapter(flats, this, this)
        binding.recycleViewFlats.adapter = adapter
    }

    private fun updateTenant(){
        val i = Intent(this@TenantItemActivity, WorkTenantActivity::class.java)
        i.putExtra("id",tenant.id_tenant)
        startActivity(i)
    }

    fun OnClickBtnBack (view: View?){
        val i = Intent(this, TenantsActivity::class.java)
        startActivity(i)
    }

    private fun deleteTenant(){
        val rows = databaseRequests.selectCountTenantsFromFlats(tenant.id_tenant)
        if(rows == 0)
        {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setTitle("Hapus Tenant")
            builder.setMessage("Apakah Anda yakin ingin menghapus?")

            builder.setPositiveButton(
                "Ya",
                DialogInterface.OnClickListener { dialog, which ->
                    val cursor = databaseRequests.deleteTenant(tenant.id_tenant)
                    if (cursor == -1)  Toast.makeText(this@TenantItemActivity, "Gagal menghapus data dari database!", Toast.LENGTH_SHORT).show()
                    else {
                        Toast.makeText(this@TenantItemActivity, "Penyewa telah dihapus", Toast.LENGTH_SHORT).show()
                        val i = Intent(this, TenantsActivity::class.java)
                        startActivity(i)
                    }
                    dialog.dismiss()
                })

            builder.setNegativeButton(
                "Tidak",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })

            builder.show()
        }
        else
        {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setNegativeButton(
                "Ok",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            builder.setMessage("Tidak dapat menghapus penyewa karena terkait dengan beberapa unit apartemen!")
            builder.show()
        }
    }

    fun checkUser(){
        role = settings.getString("role", "").toString()
        if(role == "tenant"){
            binding.forAdmin.visibility = View.GONE
        }
    }
}