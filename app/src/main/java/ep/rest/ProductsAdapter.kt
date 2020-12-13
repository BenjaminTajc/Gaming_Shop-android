package ep.rest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*

class ProductsAdapter(context: Context) : ArrayAdapter<Product>(context, 0, ArrayList()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Check if an existing view is being reused, otherwise inflate the view
        val view = if (convertView == null)
            LayoutInflater.from(context).inflate(R.layout.productlist_element, parent, false)
        else
            convertView

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvAuthor = view.findViewById<TextView>(R.id.tvAuthor)
        val tvPrice = view.findViewById<TextView>(R.id.tvPrice)


        val product = getItem(position)

        tvTitle.text = product?.ime
        tvAuthor.text = product?.opis
        tvPrice.text = String.format(Locale.ENGLISH, "%.2f EUR", product?.cena)

        return view
    }
}
