package br.com.joao.felipe.comprarei.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import br.com.joao.felipe.comprarei.R
import br.com.joao.felipe.comprarei.dao.Produto
import br.com.joao.felipe.comprarei.utils.formatadores.Formata

class ListaProdutosAdapter(
    private val contexto: Context, private val produtos: MutableList<Produto>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return produtos.size
    }

    override fun getItem(position: Int): Produto {
        return produtos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.layout_produto, parent, false)
        val nomeProduto = rowView.findViewById<TextView>(R.id.produto_nome)
        val valorProduto = rowView.findViewById<TextView>(R.id.produto_valor)
        val item = getItem(position)

        nomeProduto?.text = item.nome
        valorProduto?.text =
            Formata.formatoDinheiroString(item.valor.filter { it.isDigit() || it == '.' })

        return rowView
    }
}