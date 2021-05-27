package br.com.joao.felipe.comprarei.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.com.joao.felipe.comprarei.utils.constantes.BANCO_COMPRAS
import br.com.joao.felipe.comprarei.utils.constantes.BANCO_PRODUTOS
import org.jetbrains.anko.db.*


class ListaComprasDatabase(context: Context) :
    ManagedSQLiteOpenHelper(ctx = context, name = "listaCompras.db", version = 1) {


    companion object {
        private var instance: ListaComprasDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): ListaComprasDatabase {
            if (instance == null) {
                instance = ListaComprasDatabase(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(
            BANCO_COMPRAS,
            true,
            "id" to INTEGER + PRIMARY_KEY + UNIQUE,
            "nome" to TEXT,
            "data" to TEXT
        )

        db?.createTable(
            BANCO_PRODUTOS,
            true,
            "id" to INTEGER + PRIMARY_KEY + UNIQUE,
            "nome" to TEXT,
            "valor" to TEXT,
            "quantidade" to TEXT,
            "marca" to TEXT,
            "compra" to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

}

val Context.database: ListaComprasDatabase get() = ListaComprasDatabase.getInstance(this)