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
import co.camaleon.moneymanager.models.Categoria;
import co.camaleon.moneymanager.utils.FormValidation;
import co.camaleon.moneymanager.utils.Utils;

/**
 * Created by Usuario on 10/04/2015.
 */
public class NewCategoriaFragment extends Fragment {
    private TextView txtNombre, txtPresupuesto;
    private Button btnCancelar, btnGuardar;
    private DBOperations dbOperations;

    private Categoria categoria = null;

    public void NewCategoriaFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_new_categoria, container, false);

        btnCancelar     = (Button) fragment.findViewById(R.id.btn_cancelar);
        btnGuardar      = (Button) fragment.findViewById(R.id.btn_guardar);
        txtNombre       = (TextView) fragment.findViewById(R.id.txt_nombre);
        txtPresupuesto  = (TextView) fragment.findViewById(R.id.txt_presupuesto);

        dbOperations    = new DBOperations(getActivity());

        onCancelListener();

        onSaveListener(fragment);

        //VERIFICO SI ES EDICION DE CATEGORIA
        if(getArguments() != null) {
            int categoriaId = getArguments().getInt("categoria_id");
            categoria = dbOperations.getCategoriaById(categoriaId);

            txtNombre.setText(categoria.getNombre());
            txtPresupuesto.setText(String.valueOf(categoria.getPresupuesto()));
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
                //VALIDATE FIELDS
                if (!FormValidation.validate(FormValidation.VALIDATE_EMPTY, txtNombre, getResources().getString(R.string.empty_nombre_error))
                        ) {
                    return;
                }

                DBOperations.saveNewCategoria(dbOperations, txtNombre.getText().toString(), txtPresupuesto.getText().toString(), categoria);

                Utils.goToPreviousFragment(getActivity(), getFragmentManager());
            }
        });
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
