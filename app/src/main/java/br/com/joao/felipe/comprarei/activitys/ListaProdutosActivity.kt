package br.com.joao.felipe.comprarei.activitys

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.joao.felipe.comprarei.*
import br.com.joao.felipe.comprarei.adapters.ListaProdutosAdapter
import br.com.joao.felipe.comprarei.dao.Produto
import br.com.joao.felipe.comprarei.dao.database
import br.com.joao.felipe.comprarei.utils.constantes.BANCO_PRODUTOS
import br.com.joao.felipe.comprarei.utils.constantes.CHAVE_COMPRA
import br.com.joao.felipe.comprarei.utils.constantes.CHAVE_PRODUTO
import br.com.joao.felipe.comprarei.utils.formatadores.Formata
import kotlinx.android.synthetic.main.lista_compras.*
import kotlinx.android.synthetic.main.lista_produtos.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.toast
import java.math.BigDecimal
import kotlin.properties.Delegates

class ListaProdutosActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: Adapter
    private var listaProdutos = mutableListOf<Produto>()
    private var idCompra by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lista_produtos)

        listView = findViewById(R.id.list_view_produtos)
        adapter = ListaProdutosAdapter(this, listaProdutos)
        listView.adapter = adapter as ListaProdutosAdapter

        if (intent.hasExtra(CHAVE_COMPRA)) {
            idCompra = intent.getSerializableExtra(CHAVE_COMPRA) as Long
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            return@setOnItemLongClickListener deletaProduto(position)
        }

        fab_adiciona_produto.setOnClickListener {
            val intent = Intent(this, FormularioProdutoActivity::class.java)
            startActivityForResult(intent, 1)
        }

        listView.setOnItemClickListener { _, _, posicao, _ ->
            getItemEdicao(posicao)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val adapter = listView.adapter as ListaProdutosAdapter

        when (requestCode) {
            1 -> adicionaProduto(resultCode, data, adapter)
            2 -> editaProduto(resultCode, data, adapter)
        }
    }

    override fun onResume() {
        super.onResume()
        val adapter = listView.adapter as ListaProdutosAdapter
        getListaBanco(adapter)
    }

    private fun getListaBanco(adapter: ListaProdutosAdapter) {
        database.use {
            select(BANCO_PRODUTOS).whereArgs("compra = {compra}", "compra" to idCompra).exec {
                val parser =
                    rowParser { id: Long, nome: String, valor: String, quantidade: String, marca: String, compra: Long ->
                        Produto(id, nome, valor, quantidade, marca, compra)
                    }

                val listaBanco = parseList(parser)

                listaProdutos.clear()
                listaProdutos.addAll(listaBanco)
                mensagemListaVazia(listaProdutos)
                adapter.notifyDataSetChanged()
                preencheSumario()
            }
        }
    }

    private fun preencheSumario() {
        val quantideItens = findViewById<TextView>(R.id.lista_produto_quantidade)
        val valorTotalProdutos = findViewById<TextView>(R.id.lista_produto_valor_total)

        quantideItens.text = listaProdutos.size.toString()
        valorTotalProdutos.text = pegaValorTotal()
    }

    private fun pegaValorTotal(): String {
        var total = BigDecimal("0.0")
        var totalItemBigDecimal: BigDecimal
        var quantidadeItemBigDecimal: BigDecimal
        var totalMultiplica: BigDecimal
        for (item in listaProdutos) {
            totalItemBigDecimal = BigDecimal(item.valor.filter { it.isDigit() || it == '.' })
            quantidadeItemBigDecimal = BigDecimal(item.quantidade)
            totalMultiplica =
                totalItemBigDecimal.multiply(BigDecimal(quantidadeItemBigDecimal.toString()))
            total = total.add(BigDecimal(totalMultiplica.toString()))
        }
        return Formata.formatoDinheiroBigDecimal(total)
    }

    private fun editaProduto(resultCode: Int, data: Intent?, adapter: ListaProdutosAdapter) {
        if (resultCode == RESULT_OK && data != null) {
            val itemRecebido = data.getSerializableExtra(CHAVE_PRODUTO) as Produto
            database.use {
                update(
                    BANCO_PRODUTOS,
                    "nome" to itemRecebido.nome,
                    "valor" to itemRecebido.valor,
                    "quantidade" to itemRecebido.quantidade,
                    "marca" to itemRecebido.marca
                ).whereArgs("id = {id}", "id" to itemRecebido.id).exec()
            }
            val indexEdita = listaProdutos.indexOfFirst { it.id == itemRecebido.id }

            listaProdutos[indexEdita] = itemRecebido
            mensagemListaVazia(listaProdutos)
            adapter.notifyDataSetChanged()
            preencheSumario()
        } else {
            Log.d("Debug", "Result sem data: $data")
        }
    }

    private fun adicionaProduto(resultCode: Int, data: Intent?, adapter: ListaProdutosAdapter) {
        if (resultCode == RESULT_OK && data != null) {
            val rawItem = data.getSerializableExtra(CHAVE_PRODUTO) as Produto
            database.use {
                val idProduto = insert(
                    BANCO_PRODUTOS,
                    "nome" to rawItem.nome,
                    "marca" to rawItem.marca,
                    "valor" to rawItem.valor,
                    "quantidade" to rawItem.quantidade,
                    "compra" to idCompra
                )
                if (idProduto != -1L) {
                    listaProdutos.add(
                        Produto(
                            idProduto,
                            rawItem.nome,
                            rawItem.valor,
                            rawItem.quantidade,
                            rawItem.marca,
                            idCompra
                        )
                    )
                    toast("Produto adicionado à lista")
                    mensagemListaVazia(listaProdutos)
                    adapter.notifyDataSetChanged()
                } else {
                    toast("Produto não criado!")
                }
            }
            preencheSumario()
        } else {
            Log.d("Debug", "Result sem data: $data")
        }
    }

    private fun getItemEdicao(posicao: Int) {
        val intent = Intent(this, FormularioProdutoActivity::class.java)

        val idItemEdicao = listaProdutos[posicao].id

        database.use {
            select(BANCO_PRODUTOS).whereArgs("id = {id}", "id" to idItemEdicao).exec {
                val parser =
                    rowParser { id: Long, nome: String, valor: String, quantidade: String, marca: String, compra: Long ->
                        Produto(id, nome, valor, quantidade, marca, compra)
                    }
                try {
                    val item = parseOpt(parser) as Produto
                    intent.putExtra(CHAVE_PRODUTO, item)
                    startActivityForResult(intent, 2)
                } catch (e: Exception) {
                    Log.d("Debug", "Produto não encontrado no banco")
                    toast("Não é possível editar no momento")
                }
            }
        }
    }

    private fun deletaProduto(position: Int): Boolean {
        val adapter = listView.adapter as ListaProdutosAdapter
        val idItemDelecao = listaProdutos[position].id
        val builder = AlertDialog.Builder(this@ListaProdutosActivity)

        builder.setMessage("Tem certeza que deseja deletar este produto?")
            .setCancelable(false)
            .setPositiveButton("Confirmar") { _, _ ->
                database.use {
                    val linhasDeletadas = delete(BANCO_PRODUTOS, "id = {id}", "id" to idItemDelecao)
                    listaProdutos.removeAt(position)
                    Log.d("Debug", "Linhas deletadas: $linhasDeletadas")
                    mensagemListaVazia(listaProdutos)
                    adapter.notifyDataSetChanged()
                    preencheSumario()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
        return true
    }

    private fun mensagemListaVazia(listaProduto: List<Produto>) {
        if (listaProduto.isNotEmpty()) {
            mensagem_lista_vazia_produto.visibility = View.INVISIBLE
        } else {
            mensagem_lista_vazia_produto.visibility = View.VISIBLE
        }
    }
}