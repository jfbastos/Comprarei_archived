package br.com.joao.felipe.comprarei.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import br.com.joao.felipe.comprarei.R
import br.com.joao.felipe.comprarei.dao.Compra

class ListaComprasAdapter(private val compras: MutableList<Compra>, context: Context) :
    BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return compras.size
    }

    override fun getItem(position: Int): Compra {
        return compras[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.layout_compra, parent, false)
        val nomeCompra = rowView.findViewById<TextView>(R.id.compra_nome)
        val dataCompra = rowView.findViewById<TextView>(R.id.compra_data)
        val item = getItem(position)

        nomeCompra?.text = item.nome
        dataCompra?.text = item.data

        return rowView
    }


}