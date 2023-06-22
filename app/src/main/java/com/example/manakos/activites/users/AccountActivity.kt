package com.example.manakos.activites.users

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
import com.example.manakos.R
import com.example.manakos.activites.Counter.CountersActivity
import com.example.manakos.activites.Flat.FlatsActivity
import com.example.manakos.activites.Indication.IndicationsActivity
import com.example.manakos.activites.other.MenuActivity
import com.example.manakos.activites.Payment.PaymentsActivity
import com.example.manakos.activites.RatesAndNormatives.RatesAndNormativesActivity
import com.example.manakos.activites.Tenant.TenantsActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityAccountBinding
import com.example.manakos.models.Admin
import com.example.manakos.models.Tenant

class AccountActivity : AppCompatActivity() {
    lateinit var binding:ActivityAccountBinding
    lateinit var toggle: ActionBarDrawerToggle

    var id: Int = 0
    var role = ""
    lateinit var settings: SharedPreferences
    private lateinit var databaseRequests: DatabaseRequests

    var tenant = Tenant()
    var admin = Admin()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@AccountActivity, drawer,
                R.string.open,
                R.string.close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            databaseRequests =  DatabaseRequests(this@AccountActivity)
            settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)

            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.flatsItem ->{
                        val i = Intent(this@AccountActivity, FlatsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.homeItem ->{
                        val i = Intent(this@AccountActivity, MenuActivity::class.java)
                        startActivity(i)
                    }
                    R.id.tenantsItem ->{
                        val i = Intent(this@AccountActivity, TenantsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.accountItem ->{
                        val i = Intent(this@AccountActivity, AccountActivity::class.java)
                        startActivity(i)
                    }
                    R.id.сountersItem ->{
                        val i = Intent(this@AccountActivity, CountersActivity::class.java)
                        startActivity(i)
                    }
                    R.id.indicationsItem ->{
                        val i = Intent(this@AccountActivity, IndicationsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.ratesItem ->{
                        val i = Intent(this@AccountActivity, RatesAndNormativesActivity::class.java)
                        startActivity(i)
                    }
                    R.id.paymentsItem ->{
                        val i = Intent(this@AccountActivity, PaymentsActivity::class.java)
                        startActivity(i)
                    }
                }
                true
            }
        }
        checkUser()
        binding.exit.setOnClickListener(View.OnClickListener {
            exitAccount()
        })
        binding.updateUser.setOnClickListener(View.OnClickListener {
            val i = Intent(this@AccountActivity, WorkAccountActivity::class.java)
            i.putExtra("from", "account")
            i.putExtra("id", id)
            startActivity(i)
        })
    }

    private fun fillData(id: Int) {
        if (id == 0){
            admin = databaseRequests.selectAdmin()
            binding.txtVwEmail.text = admin.email
            binding.profile.text = "Hiii\nAdmin"
        }
        else {
            tenant = databaseRequests.selectTenantsFromId(id)
            binding.txtVwEmail.text = tenant.email
            binding.profile.text = "Hiii\n${tenant.full_name}\n"
        }
    }

    private fun exitAccount() {
        val editor: SharedPreferences.Editor = settings.edit()
        editor.putString("role", "").apply()
        editor.putInt("id", 0).apply()
        editor.putBoolean("is_logged", false).apply()

        val i = Intent(this@AccountActivity, LoginActivity::class.java)
        startActivity(i)
    }

    private fun checkUser() {
        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()
        if(role == "tenant"){
            binding.navView.menu.removeItem(R.id.ratesItem)
        }
        fillData(id)
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

}
