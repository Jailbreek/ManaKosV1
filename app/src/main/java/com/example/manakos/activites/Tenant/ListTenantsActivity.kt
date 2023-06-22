package com.example.manakos.activites.Tenant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.manakos.activites.Flat.FlatItemActivity
import com.example.manakos.activites.Flat.FlatsActivity
import com.example.manakos.activites.Flat.WorkFlatActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityListTenantsBinding
import com.example.manakos.models.Flat
import com.example.manakos.models.Tenant
import com.google.gson.Gson


class ListTenantsActivity : AppCompatActivity() {
    lateinit var binding: ActivityListTenantsBinding

    private var tenants = ArrayList<Tenant>();
    private lateinit var flat: Flat
    private lateinit var databaseRequests:DatabaseRequests;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListTenantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseRequests =  DatabaseRequests(this@ListTenantsActivity)

        val str = intent.getStringExtra("flat")
        flat = Gson().fromJson(str, Flat::class.javaObjectType)

        fillData()

        binding.checkBox.setOnClickListener(View.OnClickListener {
            binding.listTenants.isEnabled = !binding.checkBox.isChecked
            if(binding.checkBox.isChecked) {
                val i = binding.listTenants.checkedItemPosition
                binding.listTenants.setItemChecked(i, false)
            }
        })

        binding.buttonBack.setOnClickListener(View.OnClickListener { transition() })
        binding.buttonWork.setOnClickListener(View.OnClickListener { workFlat() })

        binding.serchViewTenant.setOnClickListener(View.OnClickListener {
            searchTenant()
        })
    }

    private fun searchTenant() {
        val newTenants = tenants.filter { it.full_name.contains(binding.serchViewTenant.query)  } as ArrayList<Tenant>
        var values = emptyArray<String>()
        for (i in 0..newTenants.size - 1){
            values += ("Nama: " + newTenants[i].full_name + ",\nNomor Telepon: " + newTenants[i].phone_number)
        }

        val adapter = ArrayAdapter(this@ListTenantsActivity, android.R.layout.simple_list_item_single_choice, values)
        binding.listTenants.choiceMode = ListView.CHOICE_MODE_SINGLE
        binding.listTenants.adapter = adapter
    }

    private fun workFlat() {
        if(binding.checkBox.isChecked) flat.id_tenant = null
        else flat.id_tenant = tenants[binding.listTenants.checkedItemPosition].id_tenant
        if(flat.id == 0 && flat.id == null) {
            databaseRequests.createFlat(flat)
            val i = Intent(this, FlatsActivity::class.java)
            startActivity(i)
        }
        else{
            databaseRequests.updateFlat(flat)
            val i = Intent(this, FlatItemActivity::class.java)
            i.putExtra("id", flat.id)
            startActivity(i)
        }

    }

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fillData() {
        tenants = databaseRequests.selectTenants()
        var values = emptyArray<String>()
        for (i in 0..tenants.size - 1){
            values += ("Nama: " + tenants[i].full_name + ",\nNomor Telepon: " + tenants[i].phone_number)
        }

        val adapter = ArrayAdapter(this@ListTenantsActivity, android.R.layout.simple_list_item_single_choice, values)
        binding.listTenants.choiceMode = ListView.CHOICE_MODE_SINGLE
        binding.listTenants.adapter = adapter

        if(flat.id_tenant != null && flat.id_tenant != 0) {
            binding.listTenants.setItemChecked(tenants.indexOfFirst { it.id_tenant == flat.id_tenant }, true)
        } else{
            binding.checkBox.isChecked = true
            binding.listTenants.isEnabled = !binding.checkBox.isChecked
        }

        if(flat.id != null) binding.buttonWork.text = "Edit"
        else binding.buttonWork.text = "Tambah"

        if(tenants.size == 0) binding.buttonWork.isEnabled = false
    }

    private fun transition() {
        val i = Intent(this, WorkFlatActivity::class.java)
        i.putExtra("flat", Gson().toJson(flat))
        startActivity(i)
    }
}
