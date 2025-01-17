package test1a.c14220172.cobafirebase

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore

    var DataProvinsi = ArrayList<daftarProvinsi>()
    lateinit var lvAdapter : SimpleAdapter

    lateinit var _etProvinsi : EditText
    lateinit var _etIbuKota : EditText

    var data: MutableList<Map<String,String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _etProvinsi = findViewById<EditText>(R.id.et_provinsi)
        _etIbuKota = findViewById<EditText>(R.id.et_ibukota)
        val _btSimpan = findViewById<Button>(R.id.bt_simpan)
        val _lvData = findViewById<ListView>(R.id.lv_data)

        lvAdapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf<String>("Pro","Ibu"),
            intArrayOf(
                android.R.id.text1,
                android.R.id.text2
            )
        )
        _lvData.adapter = lvAdapter

        fun TambahData(db:FirebaseFirestore, Provinsi:String, Ibukota:String){
            val dataBaru = daftarProvinsi(Provinsi,Ibukota)
            db.collection("tbProvinsi")
                .add(dataBaru)
                .addOnSuccessListener{
                    _etProvinsi.setText("")
                    _etIbuKota.setText("")
                    Log.d("Firebase", "Data Berhasil Disimpan")
                }
                .addOnFailureListener{
                    Log.d("Firebase", it.message.toString())
                }

        }

        fun readData(db:FirebaseFirestore){
            db.collection("tbProvinsi").get()
                .addOnSuccessListener {
                    result ->
                    DataProvinsi.clear()
                    for (document in result){
                        val readData = daftarProvinsi(
                            document.data.get("provinsi").toString(),
                            document.data.get("ibukota").toString()
                        )
                        DataProvinsi.add(readData)
                        data.clear()
                        DataProvinsi.forEach{
                            val dt: MutableMap<String,String> = HashMap(2)
                            dt["Pro"] = it.provinsi
                            dt["Ibu"] = it.ibukota
                            data.add(dt)
                        }
                    }
                    lvAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Log.d("Firebase", it.message.toString())
                }
        }

        _btSimpan.setOnClickListener {
            TambahData(db,_etProvinsi.text.toString(),_etIbuKota.text.toString())
            readData(db)
        }

        readData(db)

    }
}