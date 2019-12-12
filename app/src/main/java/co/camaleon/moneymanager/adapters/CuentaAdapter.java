package co.camaleon.moneymanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.camaleon.moneymanager.R;
import co.camaleon.moneymanager.fragments.CuentaFragment;
import co.camaleon.moneymanager.models.Cuenta;
import co.camaleon.moneymanager.utils.Utils;

/**
 * Created by Usuario on 10/04/2015.
 */
public class CuentaAdapter extends RecyclerView.Adapter<CuentaAdapter.ViewHolder> {
    private ArrayList<Cuenta> mDataset;
    private Context context;
    private CuentaFragment.OnItemTouchListener onItemTouchListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView nombre, descripcion, saldo;
        private View cardItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardItemView = itemView;

            nombre      = (TextView) itemView.findViewById(R.id.nombre);
            descripcion = (TextView) itemView.findViewById(R.id.descripcion);
            saldo       = (TextView) itemView.findViewById(R.id.saldo);

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
    public CuentaAdapter(ArrayList<Cuenta> cuentas, Context con, CuentaFragment.OnItemTouchListener itemTouchListener) {
        mDataset = cuentas;
        context = con;
        onItemTouchListener = itemTouchListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CuentaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cuenta, parent, false);
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

        Cuenta cue = mDataset.get(position);

        holder.nombre.setText(Utils.getStringWhenEmpty(cue.getNombre()));
        holder.descripcion.setText(context.getString(R.string.descripcion) + ": " + Utils.getStringWhenEmpty(cue.getDescripcion()));
        holder.saldo.setText(context.getString(R.string.saldo) + ": " + Utils.setPriceFormat(cue.getSaldo()));

        holder.cardItemView.setBackgroundColor(Color.parseColor(cue.getColor()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
