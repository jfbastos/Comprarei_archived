package br.com.joao.felipe.comprarei.activitys

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import br.com.joao.felipe.comprarei.R
import br.com.joao.felipe.comprarei.adapters.ListaComprasAdapter
import br.com.joao.felipe.comprarei.dao.Compra
import br.com.joao.felipe.comprarei.dao.database
import br.com.joao.felipe.comprarei.dialogs.NovaCompraDialog
import br.com.joao.felipe.comprarei.utils.constantes.BANCO_COMPRAS
import kotlinx.android.synthetic.main.lista_compras.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.toast

class ListaComprasActivity : AppCompatActivity(), NovaCompraDialog.Cadastra {
    private lateinit var listView: ListView
    private var listaCompras = mutableListOf<Compra>()
    private lateinit var adapter: Adapter
    private var idCompra: Long = -1L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lista_compras)

        listView = findViewById(R.id.list_view_compras)

        adapter = ListaComprasAdapter(listaCompras, this)
        listView.adapter = adapter as ListaComprasAdapter

        fab_adiciona_compra.setOnClickListener {
            cadastroNovaCompra()
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            return@setOnItemLongClickListener deletaProduto(position)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, ListaProdutosActivity::class.java)
            intent.putExtra("idCompra", listaCompras[position].id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val adapter = listView.adapter as ListaComprasAdapter

        database.use {
            select(BANCO_COMPRAS).exec {
                val parser = rowParser { id: Long, nome: String, data: String ->
                    Compra(id, nome, data)
                }

                val listaProduto = parseList(parser)

                listaCompras.clear()
                listaCompras.addAll(listaProduto)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun cadastroNovaCompra() {
        val dialogoNovaCompra = NovaCompraDialog()
        dialogoNovaCompra.show(supportFragmentManager, "dialogoNovaCompra")
    }

    override fun onCadastro(nome: String, data: String) {
        adicionaBanco(nome, data)
    }

    private fun adicionaBanco(nome: String, data: String) {

        val adapter = listView.adapter as ListaComprasAdapter

        database.use {
            idCompra = insert(
                BANCO_COMPRAS,
                "nome" to nome,
                "data" to data
            )
            if (idCompra != -1L) {
                toast("Compra criada! ID = $idCompra")
                listaCompras.add(Compra(idCompra, nome, data))
                adapter.notifyDataSetChanged()
            } else {
                toast("Compra não criada!")
            }
        }
    }

    private fun deletaProduto(position: Int): Boolean {

        val adapter = listView.adapter as ListaComprasAdapter
        val idItemDelecao = listaCompras[position].id
        val builder = AlertDialog.Builder(this@ListaComprasActivity)

        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(false)
            .setPositiveButton("Confirmar") { _, _ ->
                database.use {
                    val linhasDeletadas = delete(BANCO_COMPRAS, "id = {id}", "id" to idItemDelecao)
                    listaCompras.removeAt(position)
                    Log.d("Debug", "Linhas deletadas: $linhasDeletadas")
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

        return true
    }
}