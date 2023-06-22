package com.example.manakos.activites.Payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityPaymentWorkBinding
import com.example.manakos.models.Payment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PaymentWorkActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentWorkBinding
    lateinit var payment: Payment
    private lateinit var databaseRequests: DatabaseRequests

    lateinit var path: String
    var fileName = "check.jpg"
    lateinit var selectedImage: Uri

    var id: Int = 0
    var role = ""
    lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        selectedImage = Uri.EMPTY
        path = this.applicationInfo.dataDir
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseRequests = DatabaseRequests(this@PaymentWorkActivity )
        settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)
        fillData(intent.getIntExtra("id", 0))

        binding.buttonBack.setOnClickListener(View.OnClickListener {
                val i = Intent(this, PaymentItemActivity::class.java)
                i.putExtra("id", intent.getIntExtra("id", 0))
                startActivity(i)})
        binding.buttonWork.setOnClickListener(View.OnClickListener { workPayment() })
        binding.buttonLoadCheque.setOnClickListener(View.OnClickListener { loadCheck() })
        checkUser()
    }

    private fun checkUser() {
        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()
        if(role == "tenant"){
            binding.editTextAmount.isEnabled = false
            binding.checkStatus.isEnabled = false
        }
    }

    private fun loadCheck() {
        val PICK_IMAGE = 1

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    private fun workPayment() {
        payment.status = binding.checkStatus.isChecked
        if(!selectedImage.equals(Uri.EMPTY)){
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
            writeImage(bitmap)
        }
        databaseRequests.updatePayment(payment)
        val i = Intent(this, PaymentItemActivity::class.java)
        i.putExtra("id", intent.getIntExtra("id", 0))
        startActivity(i)
    }

    private fun fillData(id: Int) {
        payment = databaseRequests.selectPaymentFromId(id)

        binding.editTextNumberFlat.setText(databaseRequests.selectFlatNumberFromId(payment.id_flat))
        binding.editTextService.setText(databaseRequests.selectNameRateFromId(payment.id_rate))
        binding.editTextPeriod.setText(payment.period)
        binding.editTextAmount.setText(payment.amount.toString())
        binding.checkStatus.isChecked = payment.status
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

    fun writeImage(bitmap: Bitmap): Boolean{
        return try {
            val dir = File("$path/IngChecks/")
            if(!dir.exists()){
                dir.mkdirs()
            }

            fileName = payment.id.toString() + fileName;
            val writer = FileOutputStream(File(dir,  fileName))

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageArrByte = baos.toByteArray()

            writer.write(imageArrByte)
            writer.close()

            Toast.makeText(this, "успешно", Toast.LENGTH_SHORT).show()
            payment.cheque = fileName
            true
        } catch (exp: Exception) {
            Toast.makeText(this, exp.message, Toast.LENGTH_SHORT).show()
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            selectedImage = data.data!!

            binding.imgCheque.setImageURI(selectedImage)
        }
    }
}
