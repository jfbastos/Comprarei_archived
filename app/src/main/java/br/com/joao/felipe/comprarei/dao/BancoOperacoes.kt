package br.com.joao.felipe.comprarei.dao

import Exceptions.BancoException
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import br.com.joao.felipe.comprarei.utils.constantes.BANCO_COMPRAS
import br.com.joao.felipe.comprarei.utils.constantes.BANCO_PRODUTOS
import org.jetbrains.anko.db.*

class BancoOperacoes {

    companion object {
        private var id: Long = -1L

        fun adicionaCompraBanco(contexto: Context, nome: String, data: String): Long {
            ListaComprasDatabase.getInstance(contexto).use {
                id = insert(BANCO_COMPRAS, "nome" to nome, "data" to data)
                if (id == -1L) {
                    throw BancoException("Compra não cadastrada")
                }
            }
            return id
        }

        fun deletaCompraBanco(contexto: Context, id: Long){

            val builder = AlertDialog.Builder(contexto)
            var deletado : Boolean

            builder.setMessage("Tem certeza que deseja deletar?")
                .setCancelable(true)
                .setPositiveButton("Confirmar") { _, _ ->
                    ListaComprasDatabase.getInstance(contexto).use {
                        val comprasDeletadas = delete(BANCO_COMPRAS, "id = {id}", "id" to id)
                        val produtosDeletados = delete(
                            BANCO_PRODUTOS,
                            whereClause = "compra = {compra}",
                            "compra" to id
                        )
                        if (comprasDeletadas == 0 && produtosDeletados == 0) {
                            throw BancoException("Compra não deletada")
                        }
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                    deletado = false
                }
            val alert = builder.create()
            alert.show()
        }

        fun pegaComprasBanco(contexto: Context): List<Compra> {
            var listaCompra: List<Compra>

            listaCompra = ListaComprasDatabase.getInstance(contexto).use {
                select(BANCO_COMPRAS).exec {
                    val parser = rowParser { id: Long, nome: String, data: String ->
                        Compra(id, nome, data)
                    }
                    listaCompra = parseList(parser)
                    return@exec listaCompra
                }
            }
            return listaCompra
        }

        fun adicionaProdutoBanco(registro: Produto, contexto: Context, idCompra: Int): Long {
            ListaComprasDatabase.getInstance(contexto).use {
                val id = insert(
                    BANCO_PRODUTOS,
                    "nome" to registro.nome,
                    "marca" to registro.marca,
                    "valor" to registro.valor,
                    "quantidade" to registro.quantidade,
                    "compra" to idCompra
                )
                if (id != -1L) {
                    Toast.makeText(contexto, "Produto cadastrada.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(contexto, "Produto cadastrada.", Toast.LENGTH_SHORT).show()
                }
            }
            return id
        }
    }
}