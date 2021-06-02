package br.com.joao.felipe.comprarei.activitys

import Exceptions.BancoException
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import br.com.joao.felipe.comprarei.R
import br.com.joao.felipe.comprarei.adapters.ListaComprasAdapter
import br.com.joao.felipe.comprarei.dao.BancoOperacoes
import br.com.joao.felipe.comprarei.dao.Compra
import br.com.joao.felipe.comprarei.dialogs.NovaCompraDialog
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
    private var action: ActionMode? = null
    private var posicao : Int = -1

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

//            when (action) {
//                null -> {
//                    action = startActionMode(actionModeCallback)
//                    posicao = position
//                    listView.isSelected = true
//                }
//            }

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
        val lista = BancoOperacoes.pegaComprasBanco(this)

        mensagemListaVazia(lista)
        listaCompras.clear()
        listaCompras.addAll(lista)
        adapter.notifyDataSetChanged()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_appbar, menu)

        val menuItemEdicao = menu.findItem(R.id.acao_editar)
        val menuItemDelecao = menu.findItem(R.id.acao_deletar)

        menuItemDelecao?.setEnabled(false)?.isVisible = false
        menuItemEdicao?.setEnabled(false)?.isVisible = false

        val searchView = menu.findItem(R.id.botao_pesquisa).actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        configuraSearchView(searchView, searchManager)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                efetuaPesquisa(newText)
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

    override fun onCadastro(nome: String, data: String) {
        adicionaBanco(nome, data)
    }

    private fun efetuaPesquisa(newText: String?) {
        if (newText.isNullOrBlank() || newText.isNullOrEmpty()) {
            listView.adapter = ListaComprasAdapter(listaCompras, this@ListaComprasActivity)
        } else {
            val itens = listaCompras.filter {
                it.nome.contains(newText) || it.data.contains(newText)
            }
            listView.adapter =
                ListaComprasAdapter(itens as MutableList<Compra>, this@ListaComprasActivity)
            Log.d("Compra", "$itens")
        }
    }

    private fun configuraSearchView(searchView: SearchView, searchManager: SearchManager) {
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setBackgroundColor(getColor(R.color.green))
        searchView.focusable = View.FOCUSABLE_AUTO
        searchView.isIconified = false
    }

    private fun cadastroNovaCompra() {
        val dialogoNovaCompra = NovaCompraDialog()
        dialogoNovaCompra.show(supportFragmentManager, "dialogoNovaCompra")
    }

    private fun adicionaBanco(nome: String, data: String) {

        val adapter = listView.adapter as ListaComprasAdapter

        try {
            val id = BancoOperacoes.adicionaCompraBanco(this, nome, data)
            toast("Compra criada!")
            listaCompras.add(Compra(id, nome, data))
            mensagemListaVazia(listaCompras)
            adapter.notifyDataSetChanged()
        } catch (e: BancoException) {
            toast("Produto não cadastrado.")
        }
    }

    private fun deletaProduto(position: Int) {

        val adapter = listView.adapter as ListaComprasAdapter

        try {
            if(BancoOperacoes.deletaCompraBanco(this, listaCompras[position].id)){
                listaCompras.removeAt(position)
                mensagemListaVazia(listaCompras)
                adapter.notifyDataSetChanged()
            }
        } catch (e: BancoException) {
            toast("Não foi possível realizar a deleção")
        }
    }

    private fun mensagemListaVazia(listaProduto: List<Compra>) {
        if (listaProduto.isNotEmpty()) {
            mensagem_lista_vazia_compras.visibility = View.INVISIBLE
        } else {
            mensagem_lista_vazia_compras.visibility = View.VISIBLE
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.acoes_compra, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.acao_editar -> {
                    toast("Edita")
                    mode.finish()
                    true
                }
                R.id.acao_deletar -> {
                    deletaProduto(position = posicao)
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            action = null
        }
    }

}
