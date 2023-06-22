package com.example.manakos.activites.Payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.manakos.activites.Indication.IndicationsActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityIndicationItemBinding
import com.example.manakos.databinding.ActivityPaymentItemBinding
import com.example.manakos.models.Indication
import com.example.manakos.models.Payment
import java.io.File
import java.io.FileInputStream

class PaymentItemActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentItemBinding

    private var payment = Payment()

    lateinit var path: String

    private lateinit var databaseRequests: DatabaseRequests
    override fun onCreate(savedInstanceState: Bundle?) {
        path = this.applicationInfo.dataDir
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseRequests =  DatabaseRequests(this@PaymentItemActivity)

        fillData(intent.getIntExtra("id", 0))
        binding.update.setOnClickListener(View.OnClickListener {
            updatePayment()
        })

        binding.buttonBack.setOnClickListener(View.OnClickListener {
            val i = Intent(this, PaymentsActivity::class.java)
            startActivity(i)
        })
    }

    private fun updatePayment() {
        val i = Intent(this, PaymentWorkActivity::class.java)
        i.putExtra("id", payment.id)
        startActivity(i)
    }

    private fun fillData(id: Int) {
        payment = databaseRequests.selectPaymentFromId(id)

        binding.txtVwNumberFlat.text = databaseRequests.selectFlatNumberFromId(payment.id_flat)
        binding.txtVwService.text = databaseRequests.selectNameRateFromId(payment.id_rate)
        binding.txtVwPeriod.text = payment.period
        binding.txtVwAmount.text = payment.amount.toString()
        if(payment.status == true) binding.txtVwStatus.text = "Ya"
        else binding.txtVwStatus.text = "Tidak"

        if(payment.cheque != null){
            binding.imgCheque.setImageBitmap(readImage(payment.cheque))
        }
    }

    fun readImage(fileName: String): Bitmap?{
        try {
            val dir = File("$path/IngChecks/")

            val readFrom = File(dir, fileName)
            val content = ByteArray(readFrom.length().toInt())

            val stream = FileInputStream(readFrom)
            stream.read(content)

            val bitmap = BitmapFactory.decodeByteArray(content, 0, content.size)
            return bitmap
        }
        catch (exp: Exception){
            return null
        }
    }
}
