package com.ram.farmersmarket.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ram.farmersmarket.models.Product
import com.ram.farmersmarket.models.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FarmersMarket.db"
        private const val DATABASE_VERSION = 4  // Incremented version to force recreation

        // User table
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_LOCATION = "location"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_USER_LATITUDE = "user_latitude"
        private const val COLUMN_USER_LONGITUDE = "user_longitude"

        // Products table
        private const val TABLE_PRODUCTS = "products"
        private const val COLUMN_PRODUCT_ID = "product_id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_IMAGE_PATH = "image_path"
        private const val COLUMN_SELLER_PHONE = "seller_phone"
        private const val COLUMN_SELLER_NAME = "seller_name"
        private const val COLUMN_LOCATION_PRODUCT = "location"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create users table with location coordinates
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PHONE TEXT UNIQUE NOT NULL,
                $COLUMN_LOCATION TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_USER_LATITUDE REAL DEFAULT 0.0,
                $COLUMN_USER_LONGITUDE REAL DEFAULT 0.0
            )
        """.trimIndent()

        // Create products table with location coordinates
        val createProductTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_PRICE REAL NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_IMAGE_PATH TEXT,
                $COLUMN_SELLER_PHONE TEXT NOT NULL,
                $COLUMN_SELLER_NAME TEXT NOT NULL,
                $COLUMN_LOCATION_PRODUCT TEXT NOT NULL,
                $COLUMN_LATITUDE REAL DEFAULT 0.0,
                $COLUMN_LONGITUDE REAL DEFAULT 0.0
            )
        """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createProductTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop existing tables and recreate them to ensure clean schema
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    // Updated addUser method with location
    fun addUser(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, user.name)
            put(COLUMN_PHONE, user.phone)
            put(COLUMN_LOCATION, user.location)
            put(COLUMN_PASSWORD, user.password)
            put(COLUMN_USER_LATITUDE, user.latitude)
            put(COLUMN_USER_LONGITUDE, user.longitude)
        }
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    // Updated getUser method
    fun getUser(phone: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_PHONE = ?"
        val cursor = db.rawQuery(query, arrayOf(phone))

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_LONGITUDE))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    // Updated updateUserLocation method
    fun updateUserLocation(phone: String, latitude: Double, longitude: Double, locationName: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_LATITUDE, latitude)
            put(COLUMN_USER_LONGITUDE, longitude)
            put(COLUMN_LOCATION, locationName)
        }
        val result = db.update(TABLE_USERS, values, "$COLUMN_PHONE = ?", arrayOf(phone))
        return result > 0
    }

    // Updated addProduct method with location
    fun addProduct(product: Product): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, product.title)
            put(COLUMN_DESCRIPTION, product.description)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_CATEGORY, product.category)
            put(COLUMN_IMAGE_PATH, product.imagePath)
            put(COLUMN_SELLER_PHONE, product.sellerPhone)
            put(COLUMN_SELLER_NAME, product.sellerName)
            put(COLUMN_LOCATION_PRODUCT, product.location)
            put(COLUMN_LATITUDE, product.latitude)
            put(COLUMN_LONGITUDE, product.longitude)
        }
        return db.insert(TABLE_PRODUCTS, null, values)
    }

    // Updated getAllProducts method
    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_PRODUCTS ORDER BY $COLUMN_PRODUCT_ID DESC"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val product = Product(
                productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)),
                sellerPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELLER_PHONE)),
                sellerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELLER_NAME)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_PRODUCT)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
            )
            products.add(product)
        }
        cursor.close()
        return products
    }

    // NEW: Get products near a location
    fun getProductsNearLocation(latitude: Double, longitude: Double, maxDistanceKm: Double = 50.0): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase

        // Simple distance calculation using Haversine formula approximation
        val query = """
            SELECT *,
            (6371 * acos(cos(radians(?)) * cos(radians($COLUMN_LATITUDE)) * 
            cos(radians($COLUMN_LONGITUDE) - radians(?)) + 
            sin(radians(?)) * sin(radians($COLUMN_LATITUDE)))) AS distance
            FROM $TABLE_PRODUCTS 
            WHERE $COLUMN_LATITUDE != 0.0 AND $COLUMN_LONGITUDE != 0.0
            HAVING distance < ?
            ORDER BY distance
            LIMIT 100
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(
            latitude.toString(),
            longitude.toString(),
            latitude.toString(),
            maxDistanceKm.toString()
        ))

        while (cursor.moveToNext()) {
            val product = Product(
                productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)),
                sellerPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELLER_PHONE)),
                sellerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELLER_NAME)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_PRODUCT)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
            )
            products.add(product)
        }
        cursor.close()
        return products
    }

    // Existing methods (keep as is)
    fun validateUser(phone: String, password: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_PHONE = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(phone, password))

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_LONGITUDE))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun getProductsBySeller(sellerPhone: String): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_PRODUCTS WHERE $COLUMN_SELLER_PHONE = ? ORDER BY $COLUMN_PRODUCT_ID DESC"
        val cursor = db.rawQuery(query, arrayOf(sellerPhone))

        while (cursor.moveToNext()) {
            val product = Product(
                productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)),
                sellerPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELLER_PHONE)),
                sellerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELLER_NAME)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION_PRODUCT)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
            )
            products.add(product)
        }
        cursor.close()
        return products
    }

    fun deleteProduct(productId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_PRODUCTS, "$COLUMN_PRODUCT_ID = ?", arrayOf(productId.toString()))
        return result > 0
    }
}