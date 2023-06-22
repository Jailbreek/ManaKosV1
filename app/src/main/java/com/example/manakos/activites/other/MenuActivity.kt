package com.example.manakos.activites.other

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.manakos.R
import com.example.manakos.activites.Counter.CountersActivity
import com.example.manakos.activites.Flat.FlatsActivity
import com.example.manakos.activites.Indication.IndicationsActivity
import com.example.manakos.activites.Payment.PaymentsActivity
import com.example.manakos.activites.RatesAndNormatives.RatesAndNormativesActivity
import com.example.manakos.activites.Tenant.TenantsActivity
import com.example.manakos.activites.users.AccountActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    lateinit var settings: SharedPreferences
    lateinit var binding:ActivityMenuBinding
    lateinit var toggle: ActionBarDrawerToggle
    var id: Int = 0
    var role = ""
    private lateinit var databaseRequests: DatabaseRequests
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(
                this@MenuActivity, drawer,
                R.string.open,
                R.string.close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()
            databaseRequests =  DatabaseRequests(this@MenuActivity)
            settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)

            supportActionBar?.setDisplayHomeAsUpEnabled(true);
            getWindow().setFlags(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE, WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.flatsItem -> {
                        val i = Intent(this@MenuActivity, FlatsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.homeItem -> {
                        val i = Intent(this@MenuActivity, MenuActivity::class.java)
                        startActivity(i)
                    }
                    R.id.tenantsItem -> {
                        val i = Intent(this@MenuActivity, TenantsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.accountItem -> {
                        val i = Intent(this@MenuActivity, AccountActivity::class.java)
                        startActivity(i)
                    }
                    R.id.сountersItem -> {
                        val i = Intent(this@MenuActivity, CountersActivity::class.java)
                        startActivity(i)
                    }
                    R.id.indicationsItem -> {
                        val i = Intent(this@MenuActivity, IndicationsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.ratesItem -> {
                        val i = Intent(this@MenuActivity, RatesAndNormativesActivity::class.java)
                        startActivity(i)
                    }
                    R.id.paymentsItem -> {
                        val i = Intent(this@MenuActivity, PaymentsActivity::class.java)
                        startActivity(i)
                    }
                }
                true
            }
        }
        checkUser()
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

    fun closeDrawer(drawerLayout: DrawerLayout?) {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }


    fun checkUser(){
        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()
        if(role == "tenant"){
            binding.navView.menu.removeItem(R.id.ratesItem)
            sendNotification()
        }
    }

    private fun sendNotification() {
        val flatsI = databaseRequests.selectFlatsFromIdTenant(id)
        var count = 0
        for (item in flatsI){
            count += databaseRequests.selectCountPaymentsWhereFlatNotStatus(item.id)
        }
        if (count != 0){
            val intent_item = Intent(this, MenuActivity::class.java)
            intent_item.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK )
            val pendingIntent = PendingIntent.getActivity(this, 0, intent_item, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            val compat =  NotificationCompat.Builder(this, "CHANNEL_ID")
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_baseline_home_24)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setContentTitle("Administrasi")
                .setContentText("Anda memiliki tagihan yang belum dibayar!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            createChannelIfNeeded(notificationManager)
            notificationManager.notify(1, compat.build())
        }
    }

    fun createChannelIfNeeded(manager : NotificationManager){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("CHANNEL_ID", "CHANNEL_ID", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(notificationChannel)
        }
    }

}
