package co.camaleon.moneymanager.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import co.camaleon.moneymanager.R;
import co.camaleon.moneymanager.db.CategoriaDao;
import co.camaleon.moneymanager.db.CuentaDao;
import co.camaleon.moneymanager.db.DBOperations;
import co.camaleon.moneymanager.db.MovimientoDao;
import co.camaleon.moneymanager.models.Categoria;
import co.camaleon.moneymanager.models.Cuenta;
import co.camaleon.moneymanager.models.Movimiento;
import co.camaleon.moneymanager.utils.ConstantsUtils;
import co.camaleon.moneymanager.utils.DateUtils;
import co.camaleon.moneymanager.utils.FormValidation;
import co.camaleon.moneymanager.utils.Utils;

/**
 * Created by Usuario on 30/03/2015.
 */
public class NewMovimientoFragment extends Fragment{
    private RadioGroup radioTipo;
    private Button btnCancelar, btnGuardar;
    private TextView txtDescripcion, txtFechaFormat, txtFecha, txtValor, txtNCategoria, txtNCuenta, lblNCategoria, lblNCuenta;
    private ImageButton btnDP;
    private Spinner spCategoria, spCuenta;
    private Movimiento movimiento = null;

    private DBOperations dbOperations;

    //datetimepicker
    private int year, month, day, hour, minute;

    public void NewMovimientoFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_new_movimiento, container, false);

        btnCancelar     = (Button) fragment.findViewById(R.id.btn_cancelar);
        btnGuardar      = (Button) fragment.findViewById(R.id.btn_guardar);
        txtFecha        = (TextView) fragment.findViewById(R.id.txt_fecha);
        txtFechaFormat  = (TextView) fragment.findViewById(R.id.txt_fecha_format);
        btnDP           = (ImageButton) fragment.findViewById(R.id.btn_dp);
        spCategoria     = (Spinner) fragment.findViewById(R.id.sp_categoria);
        txtNCategoria   = (TextView) fragment.findViewById(R.id.txt_n_categoria);
        spCuenta        = (Spinner) fragment.findViewById(R.id.sp_cuenta);
        txtNCuenta      = (TextView) fragment.findViewById(R.id.txt_n_cuenta);
        radioTipo       = (RadioGroup) fragment.findViewById(R.id.radio_tipo);
        txtDescripcion  = (TextView) fragment.findViewById(R.id.txt_descripcion);
        txtValor        = (TextView) fragment.findViewById(R.id.txt_valor);
        lblNCategoria   = (TextView) fragment.findViewById(R.id.lbl_n_categoria);
        lblNCuenta      = (TextView) fragment.findViewById(R.id.lbl_n_cuenta);

        dbOperations = new DBOperations(getActivity());

        chargeSpinners();

        //VERIFICO SI ES EDICION DE MOVIMIENTO
        if(getArguments() != null) {
            int movimientoId = getArguments().getInt("movimiento_id");
            //int pageNumber   = getArguments().getInt("page");
            movimiento = dbOperations.getMovimientoById(movimientoId);

            txtDescripcion.setText(movimiento.getDescripcion());
            txtValor.setText(String.valueOf(movimiento.getValor()));

            String[] completeFecha  = movimiento.getFecha().split(" ");
            String[] fecha          = completeFecha[0].split("-");
            int hour                = 0;
            int minute              = 0;
            if(completeFecha.length >1) {
                String[] hora = completeFecha[1].split(":");
                hour = Integer.parseInt(hora[0]);
                minute = Integer.parseInt(hora[1]);
            }

            setDateInFormat(Integer.parseInt(fecha[0]), Integer.parseInt(fecha[1]) - 1, Integer.parseInt(fecha[2]), hour, minute);

            int tipoId = movimiento.isEgreso() ? R.id.radio_tipo_egreso : R.id.radio_tipo_ingreso;

            radioTipo.check(tipoId);

            ArrayAdapter<Cuenta> spCuentaAdapter = (ArrayAdapter<Cuenta>) spCuenta.getAdapter();
            for(int i=0; i < spCuentaAdapter.getCount(); i++) {
                if(movimiento.getCuenta_id() == spCuentaAdapter.getItem(i).getId()) {
                    spCuenta.setSelection(i);
                    break;
                }
            }

            ArrayAdapter<Categoria> spCategoriaAdapter = (ArrayAdapter<Categoria>) spCategoria.getAdapter();
            for(int i=0; i < spCategoriaAdapter.getCount(); i++) {
                if(movimiento.getCategoria_id() == spCategoriaAdapter.getItem(i).getId()) {
                    spCategoria.setSelection(i);
                    break;
                }
            }
        }else{
            //MARCO SELECCIONADO EGRESO POR DEFECTO
            radioTipo.check(R.id.radio_tipo_egreso);

            setCurrentDate();
        }

        onCancelListener();

        onSaveListener(fragment);

        onShowDatePickerListener();

        return fragment;
    }

    private void chargeSpinners(){
        ArrayList<Categoria> categorias = dbOperations.getCategoriasList();
        ArrayList<Cuenta> cuentas = dbOperations.getCuentasList(false);

        Categoria nCat = new Categoria();
        nCat.setId(-1);
        nCat.setNombre(getResources().getString(R.string.txt_new_categoria));
        categorias.add(nCat);

        Cuenta nCue = new Cuenta();
        nCue.setId(-1);
        nCue.setNombre(getResources().getString(R.string.txt_new_cuenta));
        cuentas.add(nCue);

        ArrayAdapter<Categoria> dataAdapter = new ArrayAdapter<Categoria>(getActivity(), android.R.layout.simple_spinner_item, categorias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(dataAdapter);

        ArrayAdapter<Cuenta> adapter_state = new ArrayAdapter<Cuenta>(getActivity(), android.R.layout.simple_spinner_item, cuentas);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCuenta.setAdapter(adapter_state);

        onChangeSpinners();
    }

    //PARA MOSTRAR LOS CAMPOS DE NUEVA CATEGORIA Y NUEVA CUENTA
    private void onChangeSpinners(){
        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int categoria_id = ((Categoria) spCategoria.getSelectedItem()).getId();

                if(categoria_id == -1){
                    lblNCategoria.setVisibility(View.VISIBLE);
                    txtNCategoria.setVisibility(View.VISIBLE);
                }else{
                    lblNCategoria.setVisibility(View.GONE);
                    txtNCategoria.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spCuenta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int cuenta_id = ((Cuenta) spCuenta.getSelectedItem()).getId();

                if (cuenta_id == -1) {
                    lblNCuenta.setVisibility(View.VISIBLE);
                    txtNCuenta.setVisibility(View.VISIBLE);
                } else {
                    lblNCuenta.setVisibility(View.GONE);
                    txtNCuenta.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                int categoria_id = ((Categoria) spCategoria.getSelectedItem()).getId();
                int cuenta_id = ((Cuenta) spCuenta.getSelectedItem()).getId();

                //VALIDATE FIELDS
                if (!FormValidation.validate(FormValidation.VALIDATE_EMPTY, txtValor, getResources().getString(R.string.m_empty_valor_error)) ||
                        !FormValidation.validate(FormValidation.VALIDATE_NUMERIC, txtValor, getResources().getString(R.string.m_numeric_valor_error)) ||
                        (categoria_id == -1 && !FormValidation.validate(FormValidation.VALIDATE_EMPTY, txtNCategoria, getResources().getString(R.string.m_empty_new_cat_error))) ||
                        (cuenta_id == -1 && !FormValidation.validate(FormValidation.VALIDATE_EMPTY, txtNCuenta, getResources().getString(R.string.m_empty_new_cue_error)))
                        ) {
                    return;
                }

                int radioSelected = radioTipo.getCheckedRadioButtonId();
                int tipoId = 0;
                if (radioSelected == R.id.radio_tipo_egreso) {
                    tipoId = ConstantsUtils.TIPO_EGRESO;
                } else if (radioSelected == R.id.radio_tipo_ingreso) {
                    tipoId = ConstantsUtils.TIPO_INGRESO;
                } else {
                    tipoId = ConstantsUtils.TIPO_TRANSFERENCIA;
                }

                if (categoria_id == -1) {
                    categoria_id = DBOperations.saveNewCategoria(dbOperations, txtNCategoria.getText().toString(), "", null);
                }

                if (cuenta_id == -1) {
                    cuenta_id = DBOperations.saveNewCuenta(dbOperations, txtNCuenta.getText().toString(), "", null);
                }

                ContentValues values = new ContentValues();

                values.clear();
                values.put("cuenta_id", cuenta_id);
                values.put("categoria_id", categoria_id);
                values.put("egreso", tipoId == ConstantsUtils.TIPO_EGRESO ? 1 : 0);
                values.put("fecha", txtFecha.getText().toString());
                values.put("valor", txtValor.getText().toString());
                values.put("descripcion", txtDescripcion.getText().toString());

                if (movimiento != null) {
                    dbOperations.update(values, new MovimientoDao(), movimiento.getId());
                } else {
                    Cuenta cuenta = dbOperations.getCuentaById(cuenta_id);
                    dbOperations.updateField(new CuentaDao(), "usos", String.valueOf(cuenta.getUsos() + 1), cuenta_id);

                    Categoria categoria = dbOperations.getCategoriaById(categoria_id);
                    dbOperations.updateField(new CategoriaDao(), "usos", String.valueOf(categoria.getUsos() + 1), categoria_id);

                    dbOperations.insertOrIgnore(values, new MovimientoDao());
                }

                Utils.goToPreviousFragment(getActivity(), getFragmentManager());
            }
        });
    }

    private void onShowDatePickerListener(){
        btnDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
                /*
                DatePickerFragment.newInstance(year, month, day, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        setDateInFormat(year, monthOfYear, dayOfMonth);
                    }
                }).show(getFragmentManager(), null);*/
            }
        });

        txtFechaFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
                /*
                DatePickerFragment.newInstance(year, month, day, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        setDateInFormat(year, monthOfYear, dayOfMonth);
                    }
                }).show(getFragmentManager(), null);
                */
            }
        });

        txtFechaFormat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateTimePicker();
                    /*
                    DatePickerFragment.newInstance(year, month, day, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            setDateInFormat(year, monthOfYear, dayOfMonth);
                        }
                    }).show(getFragmentManager(), null);
                    */
                }
            }
        });
    }

    private void showDateTimePicker(){
        final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setDateInFormat(year, monthOfYear, dayOfMonth, hour, minute);
            }
        });

        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setDateInFormat(year, month, day, hourOfDay, minute);
            }
        });

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    //SHOWS THE DATE IN FIELDS
    private void setDateInFormat(int year, int month, int day, int hour, int minute){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;

        String dateStr = DateUtils.getDateTimeFormated(year, month, day, hour, minute);

        txtFechaFormat.setText(DateUtils.setDateFormat(dateStr, true, null));
        txtFecha.setText( dateStr );
    }

    //SETS THE ACTUAL DATE
    private void setCurrentDate(){
        final Calendar c = Calendar.getInstance();

        setDateInFormat(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}


