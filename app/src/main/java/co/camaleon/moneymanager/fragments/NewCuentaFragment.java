package co.camaleon.moneymanager.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import co.camaleon.moneymanager.R;
import co.camaleon.moneymanager.db.DBOperations;
import co.camaleon.moneymanager.models.Cuenta;
import co.camaleon.moneymanager.utils.Utils;

/**
 * Created by Usuario on 10/04/2015.
 */
public class NewCuentaFragment extends Fragment {
    private TextView txtNombre, txtDescripcion;
    private Button btnCancelar, btnGuardar;
    private DBOperations dbOperations;

    private Cuenta cuenta = null;

    public void NewCuentaFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_new_cuenta, container, false);

        btnCancelar     = (Button) fragment.findViewById(R.id.btn_cancelar);
        btnGuardar      = (Button) fragment.findViewById(R.id.btn_guardar);
        txtNombre       = (TextView) fragment.findViewById(R.id.txt_nombre);
        txtDescripcion  = (TextView) fragment.findViewById(R.id.txt_descripcion);

        dbOperations = new DBOperations(getActivity());

        onCancelListener();

        onSaveListener(fragment);

        //VERIFICO SI ES EDICION DE CATEGORIA
        if(getArguments() != null) {
            int cuentaId = getArguments().getInt("cuenta_id");
            cuenta = dbOperations.getCuentaById(cuentaId);

            txtNombre.setText(cuenta.getNombre());
            txtDescripcion.setText(cuenta.getDescripcion());
        }

        return fragment;
    }

    private void onCancelListener(){
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.goToPreviousFragment(getActivity(), getFragmentManager());
            }
        });
    }

    private void onSaveListener(final View fragment){
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBOperations.saveNewCuenta(dbOperations, txtNombre.getText().toString(), txtDescripcion.getText().toString(), cuenta);

                Utils.goToPreviousFragment(getActivity(), getFragmentManager());
            }
        });
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
