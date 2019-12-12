package co.camaleon.moneymanager.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import co.camaleon.moneymanager.R;
import co.camaleon.moneymanager.models.DetalleSaldo;
import co.camaleon.moneymanager.utils.Utils;

/**
 * Created by Usuario on 20/04/2015.
 */
public class DetalleSaldoAdapter extends BaseAdapter {
    ArrayList<DetalleSaldo> detalleSaldos;
    Context context;


    public DetalleSaldoAdapter(Context con, ArrayList<DetalleSaldo> detalleSaldos){
        context = con;
        this.detalleSaldos = detalleSaldos;
    }

    @Override
    public int getCount() {
        return detalleSaldos.size();
    }

    @Override
    public DetalleSaldo getItem(int position) {
        return detalleSaldos.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_detalle_saldo, parent, false);
        }

        TextView txtNombre = (TextView) convertView.findViewById(R.id.txt_nombre);
        TextView txtSaldo  = (TextView) convertView.findViewById(R.id.txt_saldo);

        DetalleSaldo ds = detalleSaldos.get(position);

        String color;
        if( (ds.getPresupuesto() * 0.8) > ds.getSaldo()){
            color = context.getResources().getString(R.string.saldo_ok);
        }else{
            color = context.getResources().getString(R.string.saldo_slow);
        }

        Log.e("TAG", (ds.getPresupuesto() * 0.8) + " " + ds.getSaldo() );

        txtNombre.setText(ds.getNombre()+ " (" + Utils.setPriceFormat(ds.getPresupuesto()) + ")");
        txtSaldo.setText(Html.fromHtml("<font color='" + color + "'>" + Utils.setPriceFormat(ds.getSaldo()) + "</font>"));

        return convertView;
    }
}
