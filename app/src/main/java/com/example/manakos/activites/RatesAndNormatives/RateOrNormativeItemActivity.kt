package com.example.manakos.activites.RatesAndNormatives

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityRateOrNormativeItemBinding
import com.example.manakos.models.Normative
import com.example.manakos.models.Rate

class RateOrNormativeItemActivity : AppCompatActivity() {
    lateinit var binding: ActivityRateOrNormativeItemBinding

    private var rate = Rate()
    private var normative = Normative()
    private lateinit var databaseRequests: DatabaseRequests

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateOrNormativeItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseRequests = DatabaseRequests(this@RateOrNormativeItemActivity)

        fillData(intent.getIntExtra("id_rate", 0), intent.getIntExtra("id_normative", 0))
        binding.update.setOnClickListener(View.OnClickListener {
            updateIndication()
        })

        binding.buttonBack.setOnClickListener(View.OnClickListener {
            val i = Intent(this, RatesAndNormativesActivity::class.java)
            startActivity(i)
        })
    }

    private fun updateIndication() {
        val i = Intent(this, WorkRateOrNormativeActivity::class.java)
        i.putExtra("id_rate", intent.getIntExtra("id_rate", 0))
        i.putExtra("id_normative", intent.getIntExtra("id_normative", 0))
        startActivity(i)
    }

    private fun fillData(id_rate: Int, id_normative: Int) {
        if (id_rate != 0) {
            rate = databaseRequests.selectRateFromId(id_rate)

            binding.textView.text = "Tarif"
            binding.txtVwName.text = rate.name
            binding.txtVwValue.text = rate.value.toString()
        } else {
            normative = databaseRequests.selectNormativeFromId(id_normative)

            binding.textView.text = "Normatif"
            binding.txtVwName.text = normative.name
            binding.txtVwValue.text = normative.value.toString()
        }
    }
}
