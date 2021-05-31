package br.com.joao.felipe.comprarei.activitys

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Adapter
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import br.com.joao.felipe.comprarei.R
import br.com.joao.felipe.comprarei.adapters.ListaComprasAdapter
import br.com.joao.felipe.comprarei.dao.Compra
import br.com.joao.felipe.comprarei.dao.database
import br.com.joao.felipe.comprarei.dialogs.NovaCompraDialog
import br.com.joao.felipe.comprarei.utils.constantes.BANCO_COMPRAS
import br.com.joao.felipe.comprarei.utils.constantes.BANCO_PRODUTOS
import br.com.joao.felipe.comprarei.utils.constantes.CHAVE_COMPRA
import kotlinx.android.synthetic.main.layout_compra.*
import kotlinx.android.synthetic.main.layout_compra.view.*
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

        setSupportActionBar(findViewById(R.id.custom_appbar))

        listView = findViewById(R.id.list_view_compras)

        adapter = ListaComprasAdapter(listaCompras, this)
        listView.adapter = adapter as ListaComprasAdapter

        fab_adiciona_compra.setOnClickListener {
            cadastroNovaCompra()
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            deletaProduto(position)
            return@setOnItemLongClickListener true
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, ListaProdutosActivity::class.java)
            intent.putExtra(CHAVE_COMPRA, listaCompras[position].id)
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

                val listaCompra = parseList(parser)

                mensagemListaVazia(listaCompra)

                listaCompras.clear()
                listaCompras.addAll(listaCompra)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_appbar, menu)

        val searchView = menu.findItem(R.id.botao_pesquisa).actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setBackgroundColor(getColor(R.color.green))
        searchView.focusable = View.FOCUSABLE_AUTO
        searchView.isIconified = false
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank() || newText.isNullOrEmpty()) {
                    listView.adapter = ListaComprasAdapter(listaCompras, this@ListaComprasActivity)
                } else {
                    val itens =
                        listaCompras.filter { it.nome.contains(newText) || it.data.contains(newText) }
                    listView.adapter =
                        ListaComprasAdapter(itens as MutableList<Compra>, this@ListaComprasActivity)
                    Log.d("Compra", "$itens")
                }
                return false
            }
        })

        val searchMenuItem = menu.findItem(R.id.botao_pesquisa)
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                listView.adapter = ListaComprasAdapter(listaCompras, this@ListaComprasActivity)
                return true
            }

        })
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.botao_pesquisa -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
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
            idCompra = insert(BANCO_COMPRAS, "nome" to nome, "data" to data)
            if (idCompra != -1L) {
                toast("Compra criada!")
                listaCompras.add(Compra(idCompra, nome, data))
                mensagemListaVazia(listaCompras)
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
                    val comprasDeletadas = delete(BANCO_COMPRAS, "id = {id}", "id" to idItemDelecao)
                    val produtosDeletados = delete(
                        BANCO_PRODUTOS,
                        whereClause = "compra = {compra}",
                        "compra" to idItemDelecao
                    )
                    if (comprasDeletadas != 0) {
                        listaCompras.removeAt(position)
                    } else {
                        toast("Não foi possível realizar a deleção")
                    }
                    Log.d("Debug", "Linhas deletadas: $comprasDeletadas $produtosDeletados")
                    mensagemListaVazia(listaCompras)
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

    private fun mensagemListaVazia(listaProduto: List<Compra>) {
        if (listaProduto.isNotEmpty()) {
            mensagem_lista_vazia_compras.visibility = View.INVISIBLE
        } else {
            mensagem_lista_vazia_compras.visibility = View.VISIBLE
        }
    }
}