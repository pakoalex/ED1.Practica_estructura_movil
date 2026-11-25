package mx.edu.upemor.ed1persistenciadatos.ui.prendas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.data.entity.Prenda

class PrendaAdapter(
    private val alEditar: (Prenda) -> Unit,
    private val alEliminar: (Prenda) -> Unit
) : ListAdapter<Prenda, PrendaAdapter.PrendaViewHolder>(Comparador) {

    class PrendaViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvNombre: TextView = vista.findViewById(R.id.tvItemNombre)
        val tvCodigo: TextView = vista.findViewById(R.id.tvItemCodigo)
        val tvTallaColor: TextView = vista.findViewById(R.id.tvItemTallaColor)
        val tvPrecioCantidad: TextView = vista.findViewById(R.id.tvItemPrecioCantidad)
        val tvMarca: TextView = vista.findViewById(R.id.tvItemMarca)
        val btnEditar: Button = vista.findViewById(R.id.btnItemEditar)
        val btnEliminar: Button = vista.findViewById(R.id.btnItemEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrendaViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_prenda, parent, false)
        return PrendaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PrendaViewHolder, position: Int) {
        val prenda = getItem(position)
        val ctx = holder.itemView.context
        holder.tvNombre.text = prenda.nombre
        holder.tvCodigo.text = ctx.getString(R.string.item_codigo, prenda.codigo)
        holder.tvTallaColor.text = ctx.getString(R.string.item_talla_color, prenda.talla, prenda.color)
        holder.tvPrecioCantidad.text =
            ctx.getString(R.string.item_precio_cantidad, prenda.precio, prenda.cantidad)
        holder.tvMarca.text = ctx.getString(R.string.item_marca, prenda.marca)
        holder.btnEditar.setOnClickListener { alEditar(prenda) }
        holder.btnEliminar.setOnClickListener { alEliminar(prenda) }
    }

    private object Comparador : DiffUtil.ItemCallback<Prenda>() {
        override fun areItemsTheSame(a: Prenda, b: Prenda) = a.codigo == b.codigo
        override fun areContentsTheSame(a: Prenda, b: Prenda) = a == b
    }
}
