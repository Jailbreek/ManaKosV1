package com.example.manakos.activites.Indication

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.manakos.activites.Counter.ListCountersActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityWorkIndicationBinding
import com.example.manakos.models.Indication
import java.time.LocalDate

class WorkIndicationActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorkIndicationBinding
    lateinit var indication: Indication
    private lateinit var databaseRequests: DatabaseRequests
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkIndicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseRequests = DatabaseRequests(this@WorkIndicationActivity )
        fillData(intent.getIntExtra("id", 0), intent.getIntExtra("id_counter", 0))

        binding.buttonBack.setOnClickListener(View.OnClickListener {
            if(indication.id == 0 || indication.id == null){
                val i = Intent(this, ListCountersActivity::class.java)
                i.putExtra("id_counter", intent.getIntExtra("id_counter", 0))
                startActivity(i)
            } else{
                val i = Intent(this, IndicationItemActivity::class.java)
                i.putExtra("id", indication.id)
                startActivity(i)
            } })
        binding.buttonWork.setOnClickListener(View.OnClickListener { workIndication() })
    }

    private fun workIndication() {
        try {
            indication.value = binding.editTextValue.text.toString().toInt()
        } catch (_: Exception) {
        }

        var error: String? = null

        if(binding.editTextValue.text.toString().isEmpty()) error = "value"

        var count = 0
        var count2 = 0
        if(indication.id == 0 || indication.id == null) count += databaseRequests.selectCountIndicationsFromIndicationWherePeriod(indication.period, indication.id_counter)
        if(indication.id == 0 || indication.id == null) count2 += databaseRequests.selectCountFromPayment(indication.period)
        if(count!=0) error = "count"
        if(count2!=0) error = "count2"

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
        builder.setNegativeButton(
            "Ok",
            DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            })

        if(error == null)
        {
            if(indication.id != null && indication.id != 0) databaseRequests.updateIndication(indication)
            else databaseRequests.createIndication(indication)
            transition()
        }
        else{
            when(error){
                "count2" ->
                    builder.setMessage("Pencatatan tidak dapat dilakukan karena perhitungan sudah dilakukan untuk periode ini.")
                "count" ->
                    builder.setMessage("Tindakan ini tidak dapat dilakukan karena pembacaan untuk periode ini telah diproses ke meteran yang dituju.")
                "value" ->
                    builder.setMessage("Kesalahan pengisian nilai: Jumlah karakter dalam string harus 3.")
            }
            builder.show()
        }
    }

    private fun fillData(id: Int, id_counter: Int) {
        if(id == 0){
            indication = Indication()
            indication.id_counter = id_counter
            binding.editTextNumberFlat.setText(databaseRequests.selectFlatNumberFromId(databaseRequests.selectIdFlatFromCounter(id_counter)))
            binding.editTextCounter.setText(databaseRequests.selectTypeFromCounter(id_counter))

            val date = LocalDate.now()
            val monthNames = arrayOf("Januari, Februari, Maret, April, Mei, Juni, Juli, Agustus, September, Oktober, November, Desember")
            var period = ""
            if(date.monthValue - 1 == 0) period = (monthNames[11] + " " + (date.year - 1))
            else period = (monthNames[date.monthValue - 2] + " " + (date.year))
            indication.period = period
            binding.editTextPeriod.setText(period)


            binding.textView.text = "Pengiriman"
            binding.buttonWork.text = "Kirim"
        } else {
            binding.textView.text = "Edit"
            binding.buttonWork.text = "Ubah"

            indication = databaseRequests.selectIndicationFromId(id)

            binding.editTextNumberFlat.setText(databaseRequests.selectFlatNumberFromId(databaseRequests.selectIdFlatFromCounter(indication.id_counter)))
            binding.editTextCounter.setText(databaseRequests.selectTypeFromCounter(indication.id_counter))
            binding.editTextPeriod.setText(indication.period)
            binding.editTextValue.setText(indication.value.toString())
        }
    }

    private fun transition() {
        if(indication.id == 0 || indication.id == null){
            val i = Intent(this, IndicationsActivity::class.java)
            startActivity(i)
        } else{
            val i = Intent(this, IndicationItemActivity::class.java)
            i.putExtra("id", indication.id)
            startActivity(i)
        }
    }
}