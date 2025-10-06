package com.ram.farmersmarket.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ram.farmersmarket.models.Product
import com.ram.farmersmarket.models.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FarmersMarket.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val TABLE_PRODUCTS = "products"

        const val COLUMN_ID = "id"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_NAME = "name"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_PRICE = "price"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_IMAGE_PATH = "image_path"
        const val COLUMN_SELLER_PHONE = "seller_phone"
        const val COLUMN_SELLER_NAME = "seller_name"
        const val COLUMN_PRODUCT_LOCATION = "location"
        const val COLUMN_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PHONE TEXT UNIQUE NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_LOCATION TEXT NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()

        val createProductsTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_PRICE REAL NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_IMAGE_PATH TEXT,
                $COLUMN_SELLER_PHONE TEXT NOT NULL,
                $COLUMN_SELLER_NAME TEXT NOT NULL,
                $COLUMN_PRODUCT_LOCATION TEXT NOT NULL,
                $COLUMN_STATUS TEXT DEFAULT 'available',
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createProductsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    fun addUser(phone: String, name: String, location: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PHONE, phone)
            put(COLUMN_NAME, name)
            put(COLUMN_LOCATION, location)
            put(COLUMN_CREATED_AT, System.currentTimeMillis())
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun getUserByPhone(phone: String): User? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USERS, null, "$COLUMN_PHONE = ?", arrayOf(phone), null, null, null)

        return try {
            if (cursor.moveToFirst()) {
                User(
                    id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    phoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                    name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION))
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            cursor.close()
        }
    }

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
            put(COLUMN_PRODUCT_LOCATION, product.location)
            put(COLUMN_STATUS, product.status)
            put(COLUMN_CREATED_AT, product.createdAt)
        }
        return db.insert(TABLE_PRODUCTS, null, values)
    }

    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, "$COLUMN_CREATED_AT DESC")

        try {
            while (cursor.moveToNext()) {
                products.add(
                    Product(
                        id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                        title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                        price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)),
                        category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)),
                        sellerPhone = cursor.getString(cursor.getColumnIndex(COLUMN_SELLER_PHONE)),
                        sellerName = cursor.getString(cursor.getColumnIndex(COLUMN_SELLER_NAME)),
                        location = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_LOCATION))
                    )
                )
            }
        } catch (e: Exception) {
            // Log error but return empty list
            e.printStackTrace()
        } finally {
            cursor.close()
        }
        return products
    }
}