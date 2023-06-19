package com.example.manakos.activites.Flat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manakos.R
import com.example.manakos.activites.Counter.CountersActivity
import com.example.manakos.activites.Indication.IndicationsActivity
import com.example.manakos.activites.Payment.PaymentsActivity
import com.example.manakos.activites.RatesAndNormatives.RatesAndNormativesActivity
import com.example.manakos.activites.Tenant.TenantsActivity
import com.example.manakos.activites.other.MenuActivity
import com.example.manakos.activites.users.AccountActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityFlatsBinding
import com.example.manakos.models.Flat
import com.example.manakos.recyclerview.FlatViewAdapter
import kotlin.collections.ArrayList


class FlatsActivity : AppCompatActivity() {
    lateinit var binding: ActivityFlatsBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var adapter: FlatViewAdapter

    var id: Int = 0
    var role = ""
    lateinit var settings: SharedPreferences

    private lateinit var databaseRequests: DatabaseRequests
    private var flats = ArrayList<Flat>()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlatsBinding.inflate(layoutInflater)
        settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)

        //menu-start
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(
                this@FlatsActivity, drawer,
                R.string.open,
                R.string.close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            window.setFlags(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE, WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.flatsItem -> {
                        val i = Intent(this@FlatsActivity, FlatsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.homeItem -> {
                        val i = Intent(this@FlatsActivity, MenuActivity::class.java)
                        startActivity(i)
                    }
                    R.id.tenantsItem -> {
                        val i = Intent(this@FlatsActivity, TenantsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.accountItem -> {
                        val i = Intent(this@FlatsActivity, AccountActivity::class.java)
                        startActivity(i)
                    }
                    R.id.сountersItem -> {
                        val i = Intent(this@FlatsActivity, CountersActivity::class.java)
                        startActivity(i)
                    }
                    R.id.indicationsItem -> {
                        val i = Intent(this@FlatsActivity, IndicationsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.ratesItem -> {
                        val i = Intent(this@FlatsActivity, RatesAndNormativesActivity::class.java)
                        startActivity(i)
                    }
                    R.id.paymentsItem -> {
                        val i = Intent(this@FlatsActivity, PaymentsActivity::class.java)
                        startActivity(i)
                    }
                }
                true
            }
        }
        //menu-end

        binding.recycleViewFlats.layoutManager = LinearLayoutManager(this)
        databaseRequests =  DatabaseRequests(this@FlatsActivity)
        binding.addFlat.setOnClickListener(View.OnClickListener {addFlat()})
        binding.serchViewFlat.setOnClickListener(View.OnClickListener {
            searchFlat()
        })
        checkUser()
    }

    private fun checkUser() {
        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()
        if(role == "tenant"){
            binding.navView.menu.removeItem(R.id.ratesItem)
            binding.forAdmin.visibility = View.GONE
            binding.serchViewFlat.visibility = View.GONE
        }
        fillData(id)
    }

    private fun searchFlat() {
        val newFlats = flats.filter { it.flat_number.contains(binding.serchViewFlat.query)  } as ArrayList<Flat>
        adapter = FlatViewAdapter(newFlats, this, this)
        binding.recycleViewFlats.adapter = adapter
    }

    private fun addFlat() {
        val i = Intent(this@FlatsActivity, WorkFlatActivity::class.java)
        startActivity(i)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
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
        if (id == 0) flats = databaseRequests.selectFlats()
        else flats = databaseRequests.selectFlatsFromIdTenant(id)
        adapter = FlatViewAdapter(flats, this, this)
        binding.recycleViewFlats.adapter = adapter
    }
}