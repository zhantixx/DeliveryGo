package com.example.deliverygo.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.deliverygo.Domain.CategoryModel
import com.example.deliverygo.Domain.ProductModel
import com.example.deliverygo.R

class MainViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryModel>>()
    val categories: LiveData<List<CategoryModel>> = _categories

    private val _products = MutableLiveData<List<ProductModel>>()
    val products: LiveData<List<ProductModel>> = _products

    private val _topProducts = MutableLiveData<List<ProductModel>>()
    val topProducts: LiveData<List<ProductModel>> = _topProducts

    private val fullProductList = mutableListOf<ProductModel>()

    init {
        loadData()
    }

    private fun loadData() {
        val categoryList = listOf(
            CategoryModel().apply { id = 0; title = "Barlygy" },
            CategoryModel().apply { id = 1; title = "Jemister" },
            CategoryModel().apply { id = 2; title = "Súsyndar" },
            CategoryModel().apply { id = 3; title = "Tañgaltyrlar" }
        )
        _categories.value = categoryList

        fullProductList.addAll(
            listOf(
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
        )


        selectCategory(0)

        _topProducts.value = fullProductList.sortedByDescending { it.rating }
    }

    fun selectCategory(categoryId: Int) {
        _products.value = if (categoryId == 0) {
            fullProductList
        } else {
            fullProductList.filter { it.categoryId == categoryId }
        }
    }
}
