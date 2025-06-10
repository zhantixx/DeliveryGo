package com.example.deliverygo.Activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deliverygo.Adapter.AdminProductAdapter
import com.example.deliverygo.Data.AppDatabase
import com.example.deliverygo.Domain.ProductModel
import com.example.deliverygo.R
import com.example.deliverygo.databinding.ActivityAdminBinding
import com.example.deliverygo.databinding.DialogAddEditProductBinding
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var adapter: AdminProductAdapter
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AdminProductAdapter(
            onEditClick = { product -> showAddEditDialog(product) },
            onDeleteClick = { product ->
                lifecycleScope.launch {
                    db.productDao().delete(product)
                    loadProducts()
                    Toast.makeText(this@AdminActivity, "Oshirildi", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.productRecycler.layoutManager = LinearLayoutManager(this)
        binding.productRecycler.adapter = adapter

        binding.addProductBtn.setOnClickListener {
            showAddEditDialog(null)
        }

        lifecycleScope.launch {
            val existing = db.productDao().getAll()
            if (existing.isEmpty()) {
                val fakeData =listOf(
                    ProductModel(
                        1, "Alma", "Bal tamshysy sekildi dәmdi, qyzyl alma – kúni boyy sergекtiq beredi hәm vitaminge toly.", 500.0, R.drawable.apple, 1, 4.5
                    ),
                    ProductModel(
                        2, "Banan", "Jumyq hәm baldam banan – energiyany tez qalpyńa keltiredi, tańǵy asqa nemese snack retinde táueldi.", 450.0, R.drawable.banana, 1, 4.0
                    ),
                    ProductModel(
                        3, "Cola", "Gazdy, salqyn susin – dostarmen bas qosqanǵa hәm tamáqtan keıin jarasady. Qapqaǵy bar plast bottle.", 800.0, R.drawable.cola, 2, 4.8
                    ),
                    ProductModel(
                        4, "Pepsi", "Kөńіl-kúı kóteretin klassikalyk Pepsi – ısqyn kezińizde ǵajap susin.", 750.0, R.drawable.pepsi, 2, 4.2
                    ),
                    ProductModel(
                        5, "Chipsy", "Tattı hәm tұzdy kartop chipsilery – film kórgende nemese dostarmen oqyǵa taǵam retinde keremet.", 1000.0, R.drawable.chips, 3, 4.9
                    ),
                    ProductModel(
                        6, "Sý", "Taza filtrlenǵen sý – organismdi ǵasyrlarǵa jyljytatyn negizgi súiyqtyq. 0.5L bottle format.", 200.0, R.drawable.water, 2, 4.6
                    ),
                    ProductModel(
                        7, "Shokolad", "Qaramel men süt qatysyndagy sýleýli shokolad – tańǵy kú́il-kúıdi jaqsartady.", 600.0, R.drawable.chocolate, 3, 4.7
                    ),
                    ProductModel(
                        8, "Nan", "Täpsі hәm jyly nan – tandyrda pісken, ush tүsti mýkіem nan: tүs, dәm hәm sapa.", 350.0, R.drawable.bread, 1, 4.3
                    ),
                    ProductModel(
                        9, "Tort", "Qalyn kremmen jәne jeńіl bіskvitpen jasalǵan tort – túǵan kúnder men arnayı oqyǵalar ushіn.", 1500.0, R.drawable.cake, 3, 4.9
                    ),
                    ProductModel(
                        10, "Qymyz", "Últtyq kúndelikti susin – Qymyz immunitetti kүsheitip, jasartady. 1L bottle.", 900.0, R.drawable.kymyz, 2, 4.4
                    )
                )
                fakeData.forEach { db.productDao().insert(it) }
            }
            loadProducts()
        }
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            val products = db.productDao().getAll()
            adapter.submitList(products)
        }
    }

    private fun showAddEditDialog(product: ProductModel?) {
        val dialogBinding = DialogAddEditProductBinding.inflate(LayoutInflater.from(this))
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setTitle(if (product == null) "Tauar qosu" else "Ozgertu")
            .setPositiveButton("Saqtau", null)
            .setNegativeButton("Otmena", null)

        val dialog = dialogBuilder.create()

        product?.let {
            dialogBinding.titleEdit.setText(it.title)
            dialogBinding.priceEdit.setText(it.price.toString())
            dialogBinding.imageEdit.setText(it.imageRes)
        }

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val title = dialogBinding.titleEdit.text.toString()
                val price = dialogBinding.priceEdit.text.toString().toDoubleOrNull()
                val imageRes = dialogBinding.imageEdit.text.toString().toIntOrNull()

                if (title.isBlank() || price == null || imageRes == null) {
                    Toast.makeText(this, "Barlyk oristerdi toltyrynyz", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newProduct = ProductModel(
                    id = product?.id ?: 0,
                    title = title,
                    description = product?.description ?: "",
                    price = price,
                    imageRes = imageRes,
                    categoryId = product?.categoryId ?: 1,
                    rating = product?.rating ?: 4.0
                )


                lifecycleScope.launch {
                    if (product == null) {
                        db.productDao().insert(newProduct)
                        Toast.makeText(this@AdminActivity, "Qosyldy", Toast.LENGTH_SHORT).show()
                    } else {
                        db.productDao().update(newProduct)
                        Toast.makeText(this@AdminActivity, "Janartyldy", Toast.LENGTH_SHORT).show()
                    }
                    loadProducts()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}
