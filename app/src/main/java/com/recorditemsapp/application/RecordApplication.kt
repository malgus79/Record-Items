package com.recorditemsapp.application

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.recorditemsapp.model.database.RecordDatabase

class RecordApplication : Application() {

    companion object{
        lateinit var database: RecordDatabase
    }

    override fun onCreate() {
        super.onCreate()

        val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE RecordEntity ADD COLUMN photoUrl TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE UNIQUE INDEX index_RecordEntity_name ON RecordEntity (name)")
            }
        }

        database = Room.databaseBuilder(this,
            RecordDatabase::class.java,
            "RecordDatabase")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }
}