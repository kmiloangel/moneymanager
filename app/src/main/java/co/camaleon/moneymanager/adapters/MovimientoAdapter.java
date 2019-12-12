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
import co.camaleon.moneymanager.db.DBOperations;
import co.camaleon.moneymanager.fragments.MovimientoFragment;
import co.camaleon.moneymanager.models.Categoria;
import co.camaleon.moneymanager.models.Cuenta;
import co.camaleon.moneymanager.models.Movimiento;
import co.camaleon.moneymanager.utils.DateUtils;
import co.camaleon.moneymanager.utils.Utils;
import android.util.Log;
/**
 * Created by Usuario on 23/03/2015.
 */
public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.ViewHolder> {
    private ArrayList<Movimiento> mDataset;
    private MovimientoFragment.OnItemTouchListener onItemTouchListener;
    private DBOperations dbOperations;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tipo_movimiento, valor, descripcion, fecha, categoria, cuenta;
        public View cardItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.cardItemView   = itemView;
            tipo_movimiento     = (TextView) itemView.findViewById(R.id.tipo_movimiento);
            valor               = (TextView) itemView.findViewById(R.id.valor);
            descripcion         = (TextView) itemView.findViewById(R.id.descripcion);
            fecha               = (TextView) itemView.findViewById(R.id.fecha);
            categoria           = (TextView) itemView.findViewById(R.id.categoria);
            cuenta              = (TextView) itemView.findViewById(R.id.cuenta);

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
    public MovimientoAdapter(ArrayList<Movimiento> movimientos, DBOperations db, Context con, MovimientoFragment.OnItemTouchListener itemTouchListener) {
        mDataset = movimientos;
        dbOperations = db;
        context = con;
        onItemTouchListener = itemTouchListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MovimientoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_movimiento, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Categoria categoria;
        Cuenta cuenta;

        Movimiento movimiento = mDataset.get(position);


        categoria   = dbOperations.getCategoriaById(movimiento.getCategoria_id());
        cuenta      = dbOperations.getCuentaById(movimiento.getCuenta_id());


        holder.tipo_movimiento.setText(movimiento.isEgreso() ?  R.string.tipo_movimiento_egreso : R.string.tipo_movimiento_ingreso);
        holder.valor.setText( Utils.setPriceFormat(movimiento.getValor()) );

        if(movimiento.getDescripcion() == null || movimiento.getDescripcion().isEmpty()) holder.descripcion.setVisibility(View.GONE);
        holder.descripcion.setText(movimiento.getDescripcion());

        holder.fecha.setText(DateUtils.setDateFormat(movimiento.getFecha(), true, null));
        holder.categoria.setText(context.getString(R.string.categoria) + ": " + Utils.getStringWhenEmpty(categoria.getNombre()));
        holder.cuenta.setText(context.getString(R.string.cuenta) + ": " +  Utils.getStringWhenEmpty(cuenta.getNombre()));

        String color = cuenta.getColor();
        if(color == null){
            color = cuenta.getCuentaColor();
        }

        holder.cardItemView.setBackgroundColor(Color.parseColor(color));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
