package com.marvambi.degrande.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.marvambi.degrande.datas.Author


class BWOpenDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertMessage(msg: MessageModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.MessageEntry.COLUMN_ID, msg.id)
        values.put(DBContract.MessageEntry.COLUMN_AUTHOR, msg.author.toString())
        values.put(DBContract.MessageEntry.COLUMN_TOPIC, msg.topic)
        values.put(DBContract.MessageEntry.COLUMN_TEXT, msg.text)
        values.put(DBContract.MessageEntry.COLUMN_CREATEAT, msg.createAt)
        // Insert the new row, returning the primary key value of the new row
        val newRowId: Long
        /*try {
            newRowId = db.insert(DBContract.MessageEntry.TABLE_NAME, null, values)
            Log.w("Result", " $newRowId")
        } catch (ex: SQLiteConstraintException) {
            Log.w("Error:", "Error Inserting ${ex.message}")
        }*/

        return (db.insert(DBContract.MessageEntry.TABLE_NAME, null, values) >= 1)
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteMessage(msgid: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.MessageEntry.COLUMN_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(msgid)
        // Issue SQL statement.
        db.delete(DBContract.MessageEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    fun readMessage(tpic: String): ArrayList<MessageModel> {
        val messages = ArrayList<MessageModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.MessageEntry.TABLE_NAME + " WHERE " + DBContract.MessageEntry.COLUMN_TOPIC + "='" + tpic + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            //db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var id: String
        var author: Author
        var text: String
        var topic: String
        var createAt: Long
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                id = cursor.getString(cursor.getColumnIndex(DBContract.MessageEntry.COLUMN_ID))
                author = Author(cursor.getString(cursor.getColumnIndex(DBContract.MessageEntry.COLUMN_AUTHOR)))
                text = cursor.getString(cursor.getColumnIndex(DBContract.MessageEntry.COLUMN_TEXT))
                topic = cursor.getString(cursor.getColumnIndex(DBContract.MessageEntry.COLUMN_TOPIC))
                createAt = cursor.getLong(cursor.getColumnIndex(DBContract.MessageEntry.COLUMN_CREATEAT))

                messages.add(MessageModel(id, author, topic, text, createAt))
                cursor.moveToNext()
            }
        }
        return messages
    }

    /*fun readAllUsers(): ArrayList<UserModel> {
        val users = ArrayList<UserModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var userid: String
        var name: String
        var age: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                userid = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
                name = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_NAME))
                age = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_AGE))

                users.add(UserModel(userid, name, age))
                cursor.moveToNext()
            }
        }
        return users
    }*/

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FeedReader.db"

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.MessageEntry.TABLE_NAME + " (" +
                        DBContract.MessageEntry.COLUMN_ID + " TEXT PRIMARY KEY," +
                        DBContract.MessageEntry.COLUMN_AUTHOR + " OBJECT," +
                        DBContract.MessageEntry.COLUMN_TOPIC + " TEXT," +
                        DBContract.MessageEntry.COLUMN_TEXT + " TEXT," +
                        DBContract.MessageEntry.COLUMN_CREATEAT + " LONG)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.MessageEntry.TABLE_NAME
    }

}