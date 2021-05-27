package br.com.joao.felipe.comprarei.dao

import android.content.Context
import android.widget.Toast
import br.com.joao.felipe.comprarei.utils.constantes.BANCO_COMPRAS
import br.com.joao.felipe.comprarei.utils.constantes.BANCO_PRODUTOS
import org.jetbrains.anko.db.insert

class BancoOperacoes {

    companion object {
        private var id: Long = -1L

        fun adicionaCompraBanco(nome: String, data: String, contexto: Context): Long {
            ListaComprasDatabase.getInstance(contexto).use {
                id = insert(BANCO_COMPRAS, "nome" to nome, "data" to data)
                if (id != -1L) {
                    Toast.makeText(contexto, "Compra cadastrada.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(contexto, "Compra não cadastrada.", Toast.LENGTH_SHORT).show()
                }
            }
            return id
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