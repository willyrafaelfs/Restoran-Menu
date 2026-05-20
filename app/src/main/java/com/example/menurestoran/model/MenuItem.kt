// File: model/MenuItem.kt
package com.example.menurestoran.model

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class MenuItem(
    val id: Long,
    val name: String,
    val price: String,
    val description: String,
    val imageUrl: String,
    val category: String
)

object MenuRepository {
    private const val MENU_KEY = "restaurant_menu_data"
    private val gson = Gson()

    private val defaultMenu = listOf(
        MenuItem(
            1,
            "Nasi Goreng Spesial",
            "Rp 25.000",
            "Nasi goreng legendaris yang dimasak dengan bumbu rempah rahasia warisan keluarga, memberikan cita rasa gurih dan aroma yang menggugah selera di setiap suapannya. Teknik memasak menggunakan api besar memberikan aroma 'wok hei' yang khas pada bulir-bulir nasi yang pulen.\n\nDisajikan lengkap dengan telur mata sapi yang lumer, suwiran ayam goreng yang empuk, irisan bakso sapi, dan kerupuk udang yang renyah. Tidak lupa tambahan acar segar dari mentimun dan wortel serta sambal terasi pedas sebagai pelengkap sempurna yang menambah dimensi rasa.\n\nMenu ini adalah pilihan favorit pelanggan sejak tahun 1990 karena konsistensi rasanya yang tetap terjaga dan porsinya yang mengenyangkan untuk santap siang maupun malam. Sangat cocok bagi Anda yang merindukan masakan rumah dengan sentuhan profesional.",
            "https://images.unsplash.com/photo-1512058560366-18510be2db19?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            2,
            "Mie Ayam Jamur",
            "Rp 18.000",
            "Mie telur kenyal buatan sendiri tanpa bahan pengawet, dipadukan dengan potongan ayam bumbu kecap yang meresap sempurna hingga ke dalam serat dagingnya. Tekstur mie yang 'al dente' berpadu harmonis dengan gurihnya tumisan ayam.\n\nDilengkapi dengan jamur kuping segar yang memberikan tekstur unik dan kuah kaldu ayam bening yang gurih, dimasak perlahan selama 6 jam untuk mengekstraksi rasa alami. Setiap porsi juga disajikan dengan sawi hijau segar yang renyah dan taburan bawang goreng serta daun seledri yang menambah aroma harum.\n\nSangat nikmat disantap selagi hangat, terutama saat cuaca mendung atau hujan. Anda juga bisa menambahkan pangsit goreng atau bakso sebagai topping tambahan sesuai selera untuk menambah kekayaan rasa dalam satu mangkuk.",
            "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            3,
            "Sate Ayam Madura",
            "Rp 30.000",
            "Sepuluh tusuk sate ayam pilihan yang dipotong dari bagian dada tanpa lemak, dibakar dengan arang kayu untuk memberikan aroma smoky yang khas dan otentik. Proses pembakaran dilakukan dengan teknik khusus agar daging tetap juicy dan tidak kering.\n\nDisiram dengan bumbu kacang kental yang legit, gurih, dan memiliki tekstur kacang yang masih terasa kasar sebagai bukti keasliannya. Ditambah dengan kecap manis berkualitas tinggi, irisan bawang merah mentah yang pedas-segar, dan perasan jeruk limau yang memberikan kejutan rasa asam di akhir.\n\nSajian ini biasanya dihidangkan bersama lontong lembut yang dibungkus daun pisang atau nasi putih hangat. Sate ini adalah representasi terbaik dari kuliner tradisional Nusantara yang kaya akan rempah dan teknik memasak tradisional yang diwariskan turun-temurun.",
            "https://images.unsplash.com/photo-1529692236671-f1f6e9460272?w=500&q=80",
            "Makanan"
        ),
        MenuItem(
            4,
            "Es Teh Manis",
            "Rp 5.000",
            "Teh seduh tradisional yang dibuat dari perpaduan daun teh melati pilihan dari perkebunan terbaik di Jawa Tengah, menghasilkan aroma harum bunga melati dan rasa sepet yang pas. Disajikan dingin dengan es batu kristal dan gula pasir murni untuk kesegaran maksimal.\n\nMinuman klasik ini merupakan pendamping sempurna untuk segala jenis makanan berat, berfungsi untuk menetralkan rasa di lidah dan memberikan sensasi segar yang melegakan tenggorokan terutama di siang hari yang terik. Kesederhanaan yang membawa kesegaran sejati.",
            "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500&q=80",
            "Minuman"
        ),
        MenuItem(
            5,
            "Kopi Susu Gula Aren",
            "Rp 15.000",
            "Kopi susu kekinian yang diracik dari biji kopi Arabika pilihan yang dipanggang dengan tingkat kematangan sedang (medium roast). Dipadukan dengan susu cair full cream yang gurih dan pemanis alami dari gula aren cair murni yang didatangkan langsung dari pengrajin lokal.\n\nMemiliki tekstur yang creamy dan rasa manis yang tidak berlebihan (not too sweet), dengan sentuhan aroma karamel dan smoky dari gula aren. Sangat cocok dinikmati bagi Anda yang menginginkan asupan kafein namun tetap ingin merasakan sensasi kelembutan susu dalam satu gelas yang menyegarkan.",
            "https://images.unsplash.com/photo-1541167760496-162955ed8a4f?w=500&q=80",
            "Minuman"
        )
    )

    fun getMenu(prefs: SharedPreferences): List<MenuItem> {
        val json = prefs.getString(MENU_KEY, null)
        return if (json == null) {
            saveMenu(prefs, defaultMenu)
            defaultMenu
        } else {
            val type = object : TypeToken<List<MenuItem>>() {}.type
            gson.fromJson(json, type)
        }
    }

    fun saveMenu(prefs: SharedPreferences, menu: List<MenuItem>) {
        val json = gson.toJson(menu)
        prefs.edit().putString(MENU_KEY, json).apply()
    }

    fun addMenuItem(prefs: SharedPreferences, item: MenuItem) {
        val currentMenu = getMenu(prefs).toMutableList()
        currentMenu.add(item)
        saveMenu(prefs, currentMenu)
    }

    fun deleteMenuItem(prefs: SharedPreferences, id: Long) {
        val currentMenu = getMenu(prefs).toMutableList()
        currentMenu.removeAll { it.id == id }
        saveMenu(prefs, currentMenu)
    }

    fun updateMenuItem(prefs: SharedPreferences, updatedItem: MenuItem) {
        val currentMenu = getMenu(prefs).toMutableList()
        val index = currentMenu.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            currentMenu[index] = updatedItem
            saveMenu(prefs, currentMenu)
        }
    }
}
