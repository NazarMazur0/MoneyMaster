package com.example.moneymaster.components

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.example.moneymaster.models.CategoryModel
import com.example.moneymaster.models.OperationModel

object DbDriver {

    val acc=myDbDHelperContract.AccountEntry
    val ops=myDbDHelperContract.OperationEntry
    val cat=myDbDHelperContract.CategoryEntry
    val cur=myDbDHelperContract.CurrencyEntry
    val clauses=SqlClauses

    class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            Log.d("DB","${clauses.SQL_CREATE_Currency}" +
                    "\n${clauses.SQL_CREATE_Category}\n" +
                    "${clauses.SQL_CREATE_Account}\n" +
                    "${clauses.SQL_CREATE_Operation}"+
                    "${clauses.SQL_CREATE_Currency}"+
                    "${clauses.SQL_INSERT_Category}"
            )
            db.execSQL( clauses.SQL_CREATE_Currency)
            db.execSQL( clauses.SQL_CREATE_Category)
            db.execSQL( clauses.SQL_CREATE_Account)
            db.execSQL( clauses.SQL_CREATE_Operation)
            db.execSQL(clauses.SQL_INSERT_Currency)
            db.execSQL(clauses.SQL_INSERT_Category)



        }
        fun insertCategory(values:ContentValues): Long? {
            val db = writableDatabase
            return   db.insert(cat.TABLE_NAME, null, values)
        }
        fun getAccountSaldoById(accountID: Long):Float{
            val db = readableDatabase
            Log.d("DB","SELECT ${acc.COLUMN_NAME_Saldo} FROM ${acc.TABLE_NAME} WHERE ID=${accountID}")
            val cursor = db.rawQuery("SELECT ${acc.COLUMN_NAME_Saldo} FROM ${acc.TABLE_NAME} WHERE ID=${accountID}",null)
            cursor.moveToNext()
            return   cursor.getFloat(0)
        }
        fun getAccountSymbolById(accountID: Int):String{
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT ${cur.COLUMN_NAME_Symbol} FROM ${cur.TABLE_NAME} \n" +
                    "LEFT JOIN ${acc.TABLE_NAME} ON ${cur.TABLE_NAME}.${cur.COLUMN_NAME_ID} = ${acc.TABLE_NAME}.${acc.COLUMN_NAME_CurrencyID} \n" +
                    "WHERE ${acc.TABLE_NAME}.${acc.COLUMN_NAME_ID} = ${accountID}\n",null)
            cursor.moveToNext()
            return   cursor.getString(0)
        }
        fun insertDefaultAccount():Long?{
            val db = writableDatabase
            val values = ContentValues().apply {
                put("ID", 0)
                put("CurrencyID", 0)
                put("Saldo", 0)

            }
            return db.insert(acc.TABLE_NAME,null,values)
        }
        fun getCategory(ID: Long): String? {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT CategoryName FROM ${cat.TABLE_NAME} WHERE ID=${ID}",null)
            cursor.moveToNext()
            return   cursor.getString(0)
        }
        fun getAllIncomeCategorysNames() : List<String>{
            val db = readableDatabase
           // Log.d("DB",)
            val cursor = db.rawQuery("SELECT ${cat.COLUMN_NAME_CategoryName} FROM ${cat.TABLE_NAME} WHERE ${cat.COLUMN_NAME_CategoryType}=1 OR ${cat.COLUMN_NAME_CategoryType}=2 ",null)
            val res = mutableListOf<String>()
            while (cursor.moveToNext()) res.add(cursor.getString(0))
            return res
        }
        fun getAllExpensesCategorysNames() : List<String>{
            val db = readableDatabase
            // Log.d("DB",)
            val cursor = db.rawQuery("SELECT ${cat.COLUMN_NAME_CategoryName} FROM ${cat.TABLE_NAME} WHERE ${cat.COLUMN_NAME_CategoryType}=0 OR ${cat.COLUMN_NAME_CategoryType}=2 ",null)
            val res = mutableListOf<String>()
            while (cursor.moveToNext()) res.add(cursor.getString(0))
            return res
        }
        fun getLastOperationID():Int{
            try {
                val db= readableDatabase
                val cursor =  db.rawQuery("SELECT ${ops.COLUMN_NAME_ID} FROM ${ops.TABLE_NAME} ORDER BY ${ops.COLUMN_NAME_ID} DESC LIMIT 1",null)
                cursor.moveToNext()
                return cursor.getInt(0)

            }
            catch (e:RuntimeException){
                return -1
            }
        }
        fun getCategorysSumByAccountID(accountID: Int):List<CategoryModel>{
            val db = readableDatabase
            val sql =("SELECT ${cat.COLUMN_NAME_CategoryName},${cat.COLUMN_NAME_CategoryIcon} , ${cat.COLUMN_NAME_CategoryDescription},\n" +
                    "  sum(CASE WHEN ${ops.COLUMN_NAME_OperationType} = 0 THEN ${ops.COLUMN_NAME_OperationSum} ELSE 0 END) AS \"CategorySUM_Type0\",\n" +
                    "  sum(CASE WHEN ${ops.COLUMN_NAME_OperationType} = 1 THEN ${ops.COLUMN_NAME_OperationSum} ELSE 0 END) AS \"CategorySUM_Type1\"\n" +
                    "FROM ${cat.TABLE_NAME}\n" +
                    "JOIN ${ops.TABLE_NAME} ON ${cat.TABLE_NAME}.${cat.COLUMN_NAME_ID} = ${ops.COLUMN_NAME_CategoryID}\n" +
                    "GROUP BY ${cat.COLUMN_NAME_CategoryName}, ${cat.COLUMN_NAME_CategoryIcon},${cat.COLUMN_NAME_CategoryDescription};")
            val res = mutableListOf<CategoryModel>()
            val cursor = db.rawQuery(sql,null)
            while (cursor.moveToNext()){
                res.add(CategoryModel(cursor.getString(0),cursor.getString(2),cursor.getString(1),cursor.getFloat(3),cursor.getFloat(4)))
            }
            return res
        }
        fun getOperationByID(id: Int):OperationModel{
           val db=readableDatabase
            val cursor = db.rawQuery("SELECT ${ops.TABLE_NAME}.${ops.COLUMN_NAME_ID},${cat.COLUMN_NAME_CategoryIcon},${cat.COLUMN_NAME_CategoryName},${ops.COLUMN_NAME_OperationTime},${ops.COLUMN_NAME_OperationSum},${ops.COLUMN_NAME_OperationType},${ops.COLUMN_NAME_OperationDescription} FROM ${ops.TABLE_NAME} JOIN ${cat.TABLE_NAME} ON ${ops.TABLE_NAME}.${ops.COLUMN_NAME_CategoryID} = ${cat.TABLE_NAME}.${cat.COLUMN_NAME_ID} WHERE ${ops.TABLE_NAME}.${ops.COLUMN_NAME_ID}=${id}",null)
            cursor.moveToNext()
            return OperationModel(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getLong(3),cursor.getFloat(4),cursor.getInt(5)==1,cursor.getString(6))
        }
        fun getOperationsByAccountID(accountID: Int):List<OperationModel>{
            val db= readableDatabase
            val res = mutableListOf<OperationModel>()
            val sql =("SELECT ${ops.TABLE_NAME}.${ops.COLUMN_NAME_ID},${ops.COLUMN_NAME_OperationSum},${ops.COLUMN_NAME_OperationType},${ops.COLUMN_NAME_OperationTime}, ${cat.COLUMN_NAME_CategoryName},${cat.COLUMN_NAME_CategoryIcon} FROM ${ops.TABLE_NAME} JOIN ${cat.TABLE_NAME}  ON ${ops.COLUMN_NAME_CategoryID}=${cat.TABLE_NAME}.${cat.COLUMN_NAME_ID} WHERE ${ops.COLUMN_NAME_AccountID} = $accountID ORDER BY ${ops.COLUMN_NAME_OperationTime} DESC,${ops.TABLE_NAME}.${ops.COLUMN_NAME_ID} DESC")
            val cursor =  db.rawQuery(sql,null)
            while (cursor.moveToNext())
                res.add(OperationModel(cursor.getInt(0), cursor.getString(5),cursor.getString(4),cursor.getLong(3),cursor.getFloat(1), cursor.getInt(2)==1,""
                ))
            return res
        }
        fun insertOperation(accountID:Int,categoryName:String,sum:Float,type:Boolean,description:String?,time:Long) {
            val db = writableDatabase
            var id =getLastOperationID()
             id = if(id==-1) 0 else id+1
            Log.d("DB","INSERT INTO Operation  VALUES ( ${id}, ${accountID},(SELECT ID FROM ${cat.TABLE_NAME} WHERE ${cat.COLUMN_NAME_CategoryName} like '${categoryName}') ,${sum}, ${if(type) 1 else 0},'${description}',${time} )\n")
            db.execSQL("INSERT INTO Operation  VALUES ( ${id}, ${accountID},(SELECT ${cat.TABLE_NAME}.${cat.COLUMN_NAME_ID} FROM ${cat.TABLE_NAME} WHERE ${cat.COLUMN_NAME_CategoryName} like '${categoryName}') ,${sum}, ${if(type) 1 else 0},'${description}',${time} )\n")
        }
        fun updateAccount(accountID: Int){
            val db = writableDatabase
            val   sql = "UPDATE ${acc.TABLE_NAME} \n" +
                    "\tSET ${acc.COLUMN_NAME_Saldo} = \n" +
                    "(SELECT IFNULL(SUM(CASE WHEN ${ops.COLUMN_NAME_OperationType} = 1 AND ${ops.COLUMN_NAME_AccountID} = ${accountID} THEN ${ops.COLUMN_NAME_OperationSum} ELSE 0 END), 0.00) FROM ${ops.TABLE_NAME}) - (SELECT IFNULL(SUM(CASE WHEN ${ops.COLUMN_NAME_OperationType} = 0 AND ${ops.COLUMN_NAME_AccountID} = ${accountID} THEN ${ops.COLUMN_NAME_OperationSum} ELSE 0 END), 0.00) FROM ${ops.TABLE_NAME})"

            Log.d("DB",sql)
            db.execSQL(sql)
        }








        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        }
        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        }

        companion object {

            const val DATABASE_VERSION = 17
            const val DATABASE_NAME = "MoneyMaster.db"
        }
    }



    object myDbDHelperContract {
        object OperationEntry : BaseColumns {
            const val TABLE_NAME = "Operation"
            const val COLUMN_NAME_ID = "ID"
            const val COLUMN_NAME_AccountID = "AccountID"
            const val COLUMN_NAME_CategoryID = "CategoryID"
            const val COLUMN_NAME_OperationSum = "OperationSum"
            const val COLUMN_NAME_OperationType = "OperationType"
            const val COLUMN_NAME_OperationDescription = "${TABLE_NAME}Description"
            const val COLUMN_NAME_OperationTime = "${TABLE_NAME}Time"
        }
        object AccountEntry : BaseColumns {
            const val TABLE_NAME = "Account"
            const val COLUMN_NAME_ID = "ID"
            const val COLUMN_NAME_CurrencyID = "CurrencyID"
            const val COLUMN_NAME_Saldo = "Saldo"
        }
        object CategoryEntry : BaseColumns {
            const val TABLE_NAME = "Category"
            const val COLUMN_NAME_ID = "ID"
            const val COLUMN_NAME_CategoryName= "CategoryName"
            const val COLUMN_NAME_CategoryType= "CategoryType"
            const val COLUMN_NAME_CategoryDescription = "CategoryDescription"
            const val COLUMN_NAME_CategoryIcon = "Icon"

        }
        object CurrencyEntry : BaseColumns {
            const val TABLE_NAME = "Currency"
            const val COLUMN_NAME_ID = "ID"
            const val COLUMN_NAME_CurrencyName= "CurrencyName"
            const val COLUMN_NAME_ISO = "ISO"
            const val COLUMN_NAME_Symbol = "Symbol"

        }

    }

    object SqlClauses {

        internal const val SQL_CREATE_Account= "CREATE TABLE IF NOT EXISTS \"${acc.TABLE_NAME}\" (\n" +
                "\t\"${acc.COLUMN_NAME_ID}\" int NOT NULL UNIQUE DEFAULT '',\n" +
                "\t\"${acc.COLUMN_NAME_CurrencyID}\" int NOT NULL DEFAULT '',\n" +
                "\t\"${acc.COLUMN_NAME_Saldo}\" float NOT NULL DEFAULT '',\n" +
                "\tPRIMARY KEY (\"${acc.COLUMN_NAME_ID}\"),\n" +
                "\tFOREIGN KEY (\"${acc.COLUMN_NAME_CurrencyID}\") REFERENCES \"${cur.TABLE_NAME}\"(\"${cur.COLUMN_NAME_ID}\")\n" +
                ");\n"
        internal const val SQL_CREATE_Operation = "CREATE TABLE IF NOT EXISTS \"${ops.TABLE_NAME}\" (\n" +
                "\t\"${ops.COLUMN_NAME_ID}\" int NOT NULL UNIQUE DEFAULT '',\n" +
                "\t\"${ops.COLUMN_NAME_AccountID}\" int NOT NULL DEFAULT '',\n" +
                "\t\"${ops.COLUMN_NAME_CategoryID}\" int NOT NULL DEFAULT '',\n" +
                "\t\"${ops.COLUMN_NAME_OperationSum}\" float NOT NULL DEFAULT '',\n" +
                "\t\"${ops.COLUMN_NAME_OperationType}\" int NOT NULL CHECK (\"${ops.COLUMN_NAME_OperationType}\" IN (0, 1)),\n" +
                "\t\"${ops.COLUMN_NAME_OperationDescription}\" text DEFAULT '',\n" +
                "\t\"${ops.COLUMN_NAME_OperationTime}\" time NOT NULL DEFAULT '',\n" +
                "\tPRIMARY KEY (\"${ops.COLUMN_NAME_ID}\"),\n" +
                "\tFOREIGN KEY (\"${ops.COLUMN_NAME_AccountID}\") REFERENCES \"${acc.TABLE_NAME}\"(\"${acc.COLUMN_NAME_ID}\"),\n" +
                "\tFOREIGN KEY (\"${ops.COLUMN_NAME_CategoryID}\") REFERENCES \"${cat.TABLE_NAME}\"(\"${cat.COLUMN_NAME_ID}\")\n" +
                ");\n"

        internal const val SQL_CREATE_Currency =  "CREATE TABLE IF NOT EXISTS \"${cur.TABLE_NAME}\" (\n" +
                "\t\"${cur.COLUMN_NAME_ID}\" int NOT NULL UNIQUE DEFAULT '',\n" +
                "\t\"${cur.COLUMN_NAME_CurrencyName}\" int NOT NULL UNIQUE DEFAULT '',\n" +
                "\t\"${cur.COLUMN_NAME_ISO}\" text NOT NULL UNIQUE DEFAULT '',\n" +
                "\t\"${cur.COLUMN_NAME_Symbol}\" text NOT NULL UNIQUE DEFAULT '',\n" +
                "\tPRIMARY KEY (\"${cur.COLUMN_NAME_ID}\")\n" +
                ");\n"
        internal const val SQL_CREATE_Category =  "CREATE TABLE IF NOT EXISTS \"${cat.TABLE_NAME}\" (\n" +
                "\t\"${cat.COLUMN_NAME_ID}\" int NOT NULL UNIQUE DEFAULT '',\n" +
                "\t\"${cat.COLUMN_NAME_CategoryName}\" text NOT NULL DEFAULT '',\n" +
                "\t\"${cat.COLUMN_NAME_CategoryType}\" int NOT NULL CHECK (\"${cat.COLUMN_NAME_CategoryType}\" IN (0,1,2)),\n"+
                "\t\"${cat.COLUMN_NAME_CategoryDescription}\" text NOT NULL ,\n" +
                "\t\"${cat.COLUMN_NAME_CategoryIcon}\" int DEFAULT '',\n" +
                "\tPRIMARY KEY (\"${cat.COLUMN_NAME_ID}\")\n" +
                ");\n"
        internal const val SQL_INSERT_Currency= "INSERT INTO ${cur.TABLE_NAME} VALUES (\"0\",\"UAH\",\"980\",\"₴\"), (\"1\",\"USD\",\"840\",\"\$\"),(\"2\",\"EUR\",\"978\",\"€\")"
        internal const val SQL_INSERT_Category = "INSERT into ${cat.TABLE_NAME} VALUES (0,\"Food\",0,\"Food\",\"food\"),(1,\"Transport\",0,\"Transport\",\"transport\"),(2,\"Rent\",2,\"Rent\",\"rent\"),(3,\"Entertainment\",0,\"Entertainment\",\"entertainment\"),(4,\"Health\",0,\"Health\",\"health\"),(6,\"Salary\",1,\"Salary\",\"salary\")"

    }
}