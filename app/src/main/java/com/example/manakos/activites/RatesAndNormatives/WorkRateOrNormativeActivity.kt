package com.example.manakos.activites.RatesAndNormatives

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityWorkRateOrNormativeBinding
import com.example.manakos.models.Normative
import com.example.manakos.models.Rate

class WorkRateOrNormativeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorkRateOrNormativeBinding
    lateinit var rate: Rate
    lateinit var normative: Normative
    private lateinit var databaseRequests: DatabaseRequests

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkRateOrNormativeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseRequests = DatabaseRequests(this@WorkRateOrNormativeActivity)
        fillData(intent.getIntExtra("id_rate", 0), intent.getIntExtra("id_normative", 0))

        binding.buttonBack.setOnClickListener(View.OnClickListener {
            transition()
        })
        binding.buttonWork.setOnClickListener(View.OnClickListener { work() })
    }

    private fun work() {
        if (rate.id != 0 && rate.id != null) {
            try {
                rate.value = binding.editTextValue.text.toString().toFloat()
            } catch (_: Exception) {
            }

            var error: String? = null

            if (binding.editTextValue.text.toString().isEmpty()) error = "value"

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setTitle("Error")
            builder.setNegativeButton(
                "Ok",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })

            if (error == null) {
                databaseRequests.updateRate(rate)
                transition()
            } else {
                when (error) {
                    "value" ->
                        builder.setMessage("Error input pada nilai: jumlah karakter dalam string harus setidaknya 1.")
                }
                builder.show()
            }
        } else {
            try {
                normative.value = binding.editTextValue.text.toString().toFloat()
            } catch (_: Exception) {
            }

            var error: String? = null

            if (binding.editTextValue.text.toString().isEmpty()) error = "value"

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setTitle("Error")
            builder.setNegativeButton(
                "Ok",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })

            if (error == null) {
                databaseRequests.updateNormative(normative)
                transition()
            } else {
                when (error) {
                    "value" ->
                        builder.setMessage("Error input pada nilai: jumlah karakter dalam string harus setidaknya 1.")
                }
                builder.show()
            }
        }
    }

    private fun fillData(id_rate: Int, id_normative: Int) {
        if (id_rate != 0) {
            rate = databaseRequests.selectRateFromId(id_rate)

            binding.textView.text = "Tarif"
            binding.editTextName.setText(rate.name)
            binding.editTextValue.setText(rate.value.toString())
        } else {
            rate = Rate()
            normative = databaseRequests.selectNormativeFromId(id_normative)

            binding.textView.text = "Normatif"
            binding.editTextName.setText(normative.name)
            binding.editTextValue.setText(normative.value.toString())
        }
    }

    private fun transition() {
        val i = Intent(this, RateOrNormativeItemActivity::class.java)
        i.putExtra("id_rate", intent.getIntExtra("id_rate", 0))
        i.putExtra("id_normative", intent.getIntExtra("id_normative", 0))
        startActivity(i)
    }
}
