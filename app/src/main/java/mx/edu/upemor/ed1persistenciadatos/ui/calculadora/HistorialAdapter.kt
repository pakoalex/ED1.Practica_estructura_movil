package mx.edu.upemor.ed1persistenciadatos.ui.calculadora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mx.edu.upemor.ed1persistenciadatos.R
import mx.edu.upemor.ed1persistenciadatos.data.entity.HistorialCalculo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistorialAdapter :
    ListAdapter<HistorialCalculo, HistorialAdapter.HistorialViewHolder>(Comparador) {

    class HistorialViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvExpresion: TextView = vista.findViewById(R.id.tvHistExpresion)
        val tvResultado: TextView = vista.findViewById(R.id.tvHistResultado)
        val tvDetalle: TextView = vista.findViewById(R.id.tvHistDetalle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(vista)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val registro = getItem(position)
        val ctx = holder.itemView.context
        val tipo = if (registro.tipo == HistorialCalculo.TIPO_CIENTIFICA) {
            ctx.getString(R.string.historial_tipo_cientifica)
        } else {
            ctx.getString(R.string.historial_tipo_normal)
        }
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(registro.fecha))
        holder.tvExpresion.text = registro.expresion
        holder.tvResultado.text = ctx.getString(R.string.historial_resultado, registro.resultado)
        holder.tvDetalle.text = ctx.getString(R.string.historial_detalle, tipo, fecha)
    }

    private object Comparador : DiffUtil.ItemCallback<HistorialCalculo>() {
        override fun areItemsTheSame(a: HistorialCalculo, b: HistorialCalculo) = a.id == b.id
        override fun areContentsTheSame(a: HistorialCalculo, b: HistorialCalculo) = a == b
    }
}
