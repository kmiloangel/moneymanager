package co.camaleon.moneymanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.camaleon.moneymanager.R;
import co.camaleon.moneymanager.fragments.CategoriaFragment;
import co.camaleon.moneymanager.models.Categoria;
import co.camaleon.moneymanager.utils.Utils;

/**
 * Created by Usuario on 10/04/2015.
 */
public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {
    private ArrayList<Categoria> mDataset;
    private CategoriaFragment.OnItemTouchListener onItemTouchListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView nombres;
        public TextView presupuesto;

        public ViewHolder(View itemView) {
            super(itemView);
            nombres     = (TextView) itemView.findViewById(R.id.nombre);
            presupuesto = (TextView) itemView.findViewById(R.id.presupuesto);

            onItemClickListener(itemView);
        }

        private void onItemClickListener(View itemView){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getPosition());
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoriaAdapter(ArrayList<Categoria> categorias, CategoriaFragment.OnItemTouchListener itemTouchListener) {
        mDataset = categorias;
        onItemTouchListener = itemTouchListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoriaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_categoria, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Categoria cat = mDataset.get(position);

        holder.nombres.setText(Utils.getStringWhenEmpty(cat.getNombre()));
        holder.presupuesto.setText( Utils.setPriceFormat(cat.getPresupuesto()) );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
