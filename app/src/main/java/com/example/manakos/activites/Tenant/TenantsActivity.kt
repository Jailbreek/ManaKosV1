package com.example.manakos.activites.Tenant

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manakos.R
import com.example.manakos.activites.Counter.CountersActivity
import com.example.manakos.activites.Flat.FlatsActivity
import com.example.manakos.activites.Indication.IndicationsActivity
import com.example.manakos.activites.Payment.PaymentsActivity
import com.example.manakos.activites.RatesAndNormatives.RatesAndNormativesActivity
import com.example.manakos.activites.other.MenuActivity
import com.example.manakos.activites.users.AccountActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityTenantsBinding
import com.example.manakos.models.Tenant
import com.example.manakos.recyclerview.TenantViewAdapter

class TenantsActivity : AppCompatActivity() {
    lateinit var binding: ActivityTenantsBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var adapter: TenantViewAdapter

    var id: Int = 0
    var role = ""
    lateinit var settings: SharedPreferences

    private var tenants = ArrayList<Tenant>()
    private lateinit var databaseRequests:DatabaseRequests

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTenantsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@TenantsActivity, drawer,
                R.string.open,
                R.string.close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()
            settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.flatsItem ->{
                        val i = Intent(this@TenantsActivity, FlatsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.homeItem ->{
                        val i = Intent(this@TenantsActivity, MenuActivity::class.java)
                        startActivity(i)
                    }
                    R.id.tenantsItem ->{
                        val i = Intent(this@TenantsActivity, TenantsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.accountItem ->{
                        val i = Intent(this@TenantsActivity, AccountActivity::class.java)
                        startActivity(i)
                    }
                    R.id.сountersItem ->{
                        val i = Intent(this@TenantsActivity, CountersActivity::class.java)
                        startActivity(i)
                    }
                    R.id.indicationsItem ->{
                        val i = Intent(this@TenantsActivity, IndicationsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.ratesItem ->{
                        val i = Intent(this@TenantsActivity, RatesAndNormativesActivity::class.java)
                        startActivity(i)
                    }
                    R.id.paymentsItem ->{
                        val i = Intent(this@TenantsActivity, PaymentsActivity::class.java)
                        startActivity(i)
                    }
                }
                true
            }
            binding.recycleViewTenants.layoutManager = LinearLayoutManager(this@TenantsActivity)
            databaseRequests =  DatabaseRequests(this@TenantsActivity)
            binding.serchViewTenant.setOnClickListener(View.OnClickListener {
                searchTenant()
            })
            checkUser()
        }
    }

    private fun searchTenant() {
        val newTenants = tenants.filter { it.full_name.contains(binding.serchViewTenant.query)  } as ArrayList<Tenant>
        adapter = TenantViewAdapter(newTenants, this, this)
        binding.recycleViewTenants.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            true
        return super.onOptionsItemSelected(item)
    }

    fun OnClickMenu(view: View?) {
        openDrawer(binding.drawer)
    }

    fun openDrawer(drawerLayout: DrawerLayout?) {
        drawerLayout!!.openDrawer(GravityCompat.START)
    }

    private fun fillData(id: Int) {
        if(id == 0) tenants = databaseRequests.selectTenants()
        else tenants.add(databaseRequests.selectTenantsFromId(id))
        adapter = TenantViewAdapter(tenants, this, this)
        binding.recycleViewTenants.adapter = adapter
    }

    fun OnClickAddTenant(view: View?){
        val i = Intent(this@TenantsActivity, WorkTenantActivity::class.java)
        startActivity(i)
    }

    fun checkUser(){
        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()
        if(role == "tenant"){
            binding.navView.menu.removeItem(R.id.ratesItem)
            binding.forAdmin.visibility = View.GONE
            binding.serchViewTenant.visibility = View.GONE
        }
        fillData(id)
    }
}