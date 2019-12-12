package co.camaleon.moneymanager.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

import co.camaleon.moneymanager.R;
import co.camaleon.moneymanager.adapters.DetalleSaldoAdapter;
import co.camaleon.moneymanager.db.DBOperations;
import co.camaleon.moneymanager.utils.DateUtils;
import co.camaleon.moneymanager.utils.Utils;

/**
 * Created by Usuario on 20/04/2015.
 */
public class HomeFragment extends Fragment{
    private DBOperations dbOperations;
    private TextView txtSaldo, txtUltimoMovimiento, txtCurrentMonth;
    private Button btnDetalle, btnPrevMonth, btnNextMonth;
    private ListView lvDetalle;
    private ImageButton newItemIcon;
    private int currentMonth, currentYear;
    private Calendar cal;
    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment       = inflater.inflate(R.layout.fragment_home, container, false);
        cal                 = Calendar.getInstance();
        newItemIcon         = (ImageButton) fragment.findViewById(R.id.new_icon);
        txtSaldo            = (TextView) fragment.findViewById(R.id.txt_saldo);
        txtUltimoMovimiento = (TextView) fragment.findViewById(R.id.txt_ultimo_movimiento);
        txtCurrentMonth     = (TextView) fragment.findViewById(R.id.txt_current_month);
        btnDetalle          = (Button) fragment.findViewById(R.id.btn_detalle);
        btnPrevMonth        = (Button) fragment.findViewById(R.id.btn_prev_month);
        btnNextMonth        = (Button) fragment.findViewById(R.id.btn_next_month);
        lvDetalle           = (ListView) fragment.findViewById(R.id.lv_saldos);
        currentMonth        = cal.get(Calendar.MONTH);
        currentYear         = cal.get(Calendar.YEAR);

        //Log.e("Home", currentMonth + "");

        newItemIconOnClickListener();

        dbOperations = new DBOperations(getActivity());

        //ACTUALIZO ESQUEMA
        dbOperations.updateDB();
        //dbOperations.fixMovimientoDates();

        btnDetalleListener();
        btnPrevMonthListener();
        btnNextMonthListener();

        return fragment;
    }

    private void newItemIconOnClickListener(){
        newItemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new fragment and transaction
                Fragment newFragment = new NewMovimientoFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.frame_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });
    }

    public void btnDetalleListener(){
        btnDetalle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                btnPrevMonth.setVisibility(View.VISIBLE);
                btnNextMonth.setVisibility(View.VISIBLE);
                txtCurrentMonth.setVisibility(View.VISIBLE);
                btnDetalle.setVisibility(View.GONE);

                getDetalleSaldo();
            }
        });
    }

    public void btnPrevMonthListener(){
        btnPrevMonth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, -1);

                getDetalleSaldo();
            }
        });
    }

    public void btnNextMonthListener(){
        btnNextMonth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, 1);

                getDetalleSaldo();
            }
        });
    }

    private void getDetalleSaldo(){
        int month = (cal.get(Calendar.MONTH) + 1);
        String cMonth = month < 10 ? "0" + month : "" + month;

        String dateFrom = cal.get(Calendar.YEAR) + "-" + cMonth + "-01 00:00:00";
        String dateEnd  = cal.get(Calendar.YEAR) + "-" + cMonth + "-31 23:59:59";

        txtCurrentMonth.setText(DateUtils.setDateFormat(dateFrom, false, "yyyy/MMM"));

        DetalleSaldoAdapter detalleSaldoAdapter = new DetalleSaldoAdapter(getActivity(), dbOperations.getDetalleSaldo(dateFrom, dateEnd));
        lvDetalle.setAdapter(detalleSaldoAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtSaldo.setText(Html.fromHtml(getResources().getString(R.string.h_lbl_saldo_actual) + " <b>" + Utils.setPriceFormat(dbOperations.getSaldoActual()) + "</b>"));
        txtUltimoMovimiento.setText(getResources().getString(R.string.h_lbl_ultimo_movimiento) + " " + Utils.getStringWhenEmpty(DateUtils.setDateFormat(dbOperations.getFechaUltimoMovimiento(), true, null)));
    }
}
