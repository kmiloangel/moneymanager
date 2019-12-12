package co.camaleon.moneymanager.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import co.camaleon.moneymanager.R;
import co.camaleon.moneymanager.adapters.MovimientoAdapter;
import co.camaleon.moneymanager.db.DBOperations;
import co.camaleon.moneymanager.db.MovimientoDao;
import co.camaleon.moneymanager.models.Categoria;
import co.camaleon.moneymanager.models.Cuenta;
import co.camaleon.moneymanager.models.Movimiento;
import co.camaleon.moneymanager.utils.ConstantsUtils;
import co.camaleon.moneymanager.utils.DateUtils;
import co.camaleon.moneymanager.utils.EndlessRecyclerViewScrollListener;
import co.camaleon.moneymanager.utils.SwipeableRecyclerViewTouchListener;
import co.camaleon.moneymanager.utils.Utils;

public class MovimientoFragment extends Fragment {
    private static final String TAG = MovimientoFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ImageButton newItemIcon;
    private ProgressBar progressBar;
    private Button btnShowFilter;
    LinearLayoutManager linearLayoutManager;

    //FILTER LAYOUT
    private LinearLayout filterLayout;
    private Spinner spCategoria, spCuenta;
    private CheckBox ckOutcomes, ckIncomes;
    private TextView txtDateFromFormat, txtDateFrom, txtDateToFormat, txtDateTo, txtAmountFrom, txtAmountTo, txtDescription;
    private ImageButton btnDPDateFrom, btnDPDateTo;
    private Button btnDoFilter, btnCancelFilter;
    private String whereFilterQuery = null;
    //HANDLES DATEPICKER
    private int year = 0, month = 0, day = 0;

    private DBOperations dbOperations;

    private ArrayList<Movimiento> movimientos = new ArrayList<Movimiento>();
    //private ArrayList<Movimiento> movimientosAll;
    private View fragmentMovimiento;
    private int pageNumber = 1;
    private boolean firstExecution = true;

    private EndlessRecyclerViewScrollListener scrollListener;

    public MovimientoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentMovimiento = inflater.inflate(R.layout.fragment_movimiento, container, false);

        linearLayoutManager = new LinearLayoutManager(getActivity());

        newItemIcon         = (ImageButton) fragmentMovimiento.findViewById(R.id.new_icon);
        progressBar         = (ProgressBar) fragmentMovimiento.findViewById(R.id.item_progress_bar);

        //Filters
        btnShowFilter           = (Button) fragmentMovimiento.findViewById(R.id.btn_filter);
        filterLayout            = (LinearLayout) fragmentMovimiento.findViewById(R.id.ll_filter);
        spCategoria             = (Spinner) fragmentMovimiento.findViewById(R.id.sp_categoria);
        spCuenta                = (Spinner) fragmentMovimiento.findViewById(R.id.sp_cuenta);
        ckOutcomes              = (CheckBox) fragmentMovimiento.findViewById(R.id.ck_tipo_egreso);
        ckIncomes               = (CheckBox) fragmentMovimiento.findViewById(R.id.ck_tipo_ingreso);
        txtDateFrom             = (TextView) fragmentMovimiento.findViewById(R.id.txt_date_from);
        txtDateFromFormat       = (TextView) fragmentMovimiento.findViewById(R.id.txt_date_from_format);
        btnDPDateFrom           = (ImageButton) fragmentMovimiento.findViewById(R.id.btn_dp_date_from);
        btnDPDateTo             = (ImageButton) fragmentMovimiento.findViewById(R.id.btn_dp_date_to);
        txtDateTo               = (TextView) fragmentMovimiento.findViewById(R.id.txt_date_to);
        txtDateToFormat         = (TextView) fragmentMovimiento.findViewById(R.id.txt_date_to_format);
        txtAmountFrom           = (TextView) fragmentMovimiento.findViewById(R.id.txt_amount_from);
        txtAmountTo             = (TextView) fragmentMovimiento.findViewById(R.id.txt_amount_to);
        txtDescription          = (TextView) fragmentMovimiento.findViewById(R.id.txt_description);
        btnDoFilter             = (Button) fragmentMovimiento.findViewById(R.id.btn_do_filter);
        btnCancelFilter         = (Button) fragmentMovimiento.findViewById(R.id.btn_cancel_filter);

        dbOperations = new DBOperations(getActivity());

        //ACTUALIZO ESQUEMA
        //dbOperations.updateDB();

        //LISTENERS
        newItemIconOnClickListener();
        onBtnShowFilterListener();
        chargeSpinners();
        onShowDatePickerListener(btnDPDateFrom, txtDateFrom, txtDateFromFormat);
        onShowDatePickerListener(btnDPDateTo, txtDateTo, txtDateToFormat);
        onBtnDoFilterListener();
        onCancelFilterListener();

        return fragmentMovimiento;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.movimiento_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //ON ITEM TOUCH LISTENER
        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
                //Toast.makeText(getActivity(), "Tapped " + movimientos.get(position), Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putInt("movimiento_id", movimientos.get(position).getId());

                Fragment newFragment = new NewMovimientoFragment();
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.frame_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        };

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                progressBar.setVisibility(View.VISIBLE);

                pageNumber++;

                //new Handler().postDelayed(createRunnable(page), 1500);

                ArrayList<Movimiento> tempMovimientos = dbOperations.getMovimientosList(whereFilterQuery, ConstantsUtils.MOVIMIENTOS_PAGE_ITEMS, pageNumber);

                movimientos.addAll(tempMovimientos);

                mAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

                //Log.e(TAG, "LLEGA!!!!" + pageNumber +" " + totalItemsCount);
            }
        };

        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

        // specify an adapter
        mAdapter = new MovimientoAdapter(movimientos, dbOperations, getActivity(), itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);

        //CLEAR WHEN COMES FROM NEW/UPDATE MOV
        if(!firstExecution) movimientos.clear();

        //GET ELEMENTS TO THE CURRENT PAGE NUMBER
        for(int i = 1; i<= pageNumber; i++){
            ArrayList<Movimiento> tempMovimientos = dbOperations.getMovimientosList(whereFilterQuery, ConstantsUtils.MOVIMIENTOS_PAGE_ITEMS, i);
            movimientos.addAll(tempMovimientos);
        }

        if(!firstExecution) {
            //update when comes from new/update mov
            mAdapter.notifyDataSetChanged();
            scrollListener.resetState();
        }else{
            //WHEN THERES NO ELEMENTS
            if(movimientos.size() == 0){
                FrameLayout fm = (FrameLayout) fragmentMovimiento.findViewById(R.id.fl_no_regs);
                fm.setVisibility(View.VISIBLE);
                btnShowFilter.setVisibility(View.GONE);
            }
        }
        firstExecution = false;

        //ADD SWIPE TO DELETE FUNCTION
        mRecyclerView.addOnItemTouchListener(swipeToDelete());
    }

    /*private Runnable createRunnable(final int page){
        Runnable aRunnable = new Runnable(){
            public void run(){
                ArrayList<Movimiento> tempMovimientos = dbOperations.getMovimientosList(null, ConstantsUtils.MOVIMIENTOS_PAGE_ITEMS, page + 1);

                for(int i = 0; i < tempMovimientos.size(); i++){
                    movimientos.add(tempMovimientos.get(i));
                }

                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        };
        return aRunnable;
    }*/

    private String getFiltersWhere(){
        int categoria_id    = ((Categoria) spCategoria.getSelectedItem()).getId();
        int cuenta_id       = ((Cuenta) spCuenta.getSelectedItem()).getId();
        boolean outComes    = ckOutcomes.isChecked();
        boolean inComes     = ckIncomes.isChecked();
        String dateFrom     = txtDateFrom.getText().toString();
        String dateTo       = txtDateTo.getText().toString();
        String amountFrom   = txtAmountFrom.getText().toString();
        String amountTo     = txtAmountTo.getText().toString();
        String desc         = txtDescription.getText().toString();

        return dbOperations.getMovimientoFilterWhere(categoria_id, cuenta_id, outComes, inComes, dateFrom, dateTo, amountFrom, amountTo, desc);
    }

    //SWIPE TO DELETE
    private RecyclerView.OnItemTouchListener swipeToDelete(){
        //OBJETO PARA REALIZAR DELETE
        final MovimientoDao movimientoDao = new MovimientoDao();

        SwipeableRecyclerViewTouchListener swipeTouchListener = new SwipeableRecyclerViewTouchListener(mRecyclerView,
                new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipe(int position) {
                        return true;
                    }

                    @Override
                    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            //ELIMINO DE BD
                            Movimiento movimiento = movimientos.get(position);
                            dbOperations.delete(movimientoDao, movimiento.getId());

                            //ELIMINO DE ARRAY LIST
                            movimientos.remove(position);
                            //movimientosAll.remove(position);

                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            //ELIMINO DE BD
                            Movimiento movimiento = movimientos.get(position);
                            dbOperations.delete(movimientoDao, movimiento.getId());

                            //ELIMINO DE ARRAY LIST
                            movimientos.remove(position);
                            //movimientosAll.remove(position);

                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
        );

        return swipeTouchListener;
    }

    private void onShowDatePickerListener(ImageButton btnLauncher, final TextView txtFecha, final TextView txtFechaFormat){
        btnLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(txtFecha, txtFechaFormat);
            }
        });

        txtFechaFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(txtFecha, txtFechaFormat);
            }
        });

        txtFechaFormat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateTimePicker(txtFecha, txtFechaFormat);
                }
            }
        });
    }

    private void showDateTimePicker(final TextView txtFecha, final TextView txtFechaFormat){
        String currentDate = txtFecha.getText().toString();

        //GET CURRENT SELECTED DATE
        if(currentDate.length() == 0){
            final Calendar c = Calendar.getInstance();

            year    = c.get(Calendar.YEAR);
            month   = c.get(Calendar.MONTH);
            day     = c.get(Calendar.DAY_OF_MONTH);
        }else{
            String[] dateArray      = currentDate.split(" ");
            String[] fecha          = dateArray[0].split("-");

            year    = Integer.parseInt(fecha[0]);
            month   = Integer.parseInt(fecha[1]) - 1;
            day     = Integer.parseInt(fecha[2]);
        }

        final View dialogView = View.inflate(getActivity(), R.layout.date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int yearChanged, int monthOfYearChanged, int dayOfMonthChanged) {
                year    = yearChanged;
                month   = monthOfYearChanged;
                day     = dayOfMonthChanged;

                setDateInFormat(txtFecha, txtFechaFormat, yearChanged, monthOfYearChanged, dayOfMonthChanged);
            }
        });

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateInFormat(txtFecha, txtFechaFormat, year, month, day);

                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    //SHOWS THE DATE IN FIELDS
    private void setDateInFormat(TextView txtFecha, TextView txtFechaFormat, int year, int month, int day){
        String dateStr = DateUtils.getDateFormated(year, month, day);
        txtFechaFormat.setText(DateUtils.setDateFormat(dateStr, false, null));
        txtFecha.setText( dateStr );
    }

    private void chargeSpinners(){
        ArrayList<Categoria> categorias = dbOperations.getCategoriasList();
        ArrayList<Cuenta> cuentas = dbOperations.getCuentasList(false);

        Categoria nCat = new Categoria();
        nCat.setId(-1);
        nCat.setNombre(getResources().getString(R.string.txt_choose_category));
        categorias.add(0, nCat);

        Cuenta nCue = new Cuenta();
        nCue.setId(-1);
        nCue.setNombre(getResources().getString(R.string.txt_choose_account));
        cuentas.add(0, nCue);

        ArrayAdapter<Categoria> dataAdapter = new ArrayAdapter<Categoria>(getActivity(), android.R.layout.simple_spinner_item, categorias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(dataAdapter);

        ArrayAdapter<Cuenta> adapter_state = new ArrayAdapter<Cuenta>(getActivity(), android.R.layout.simple_spinner_item, cuentas);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCuenta.setAdapter(adapter_state);
    }

    private void onBtnShowFilterListener(){
        btnShowFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onCancelFilterListener(){
        btnCancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(getActivity());
                filterLayout.setVisibility(View.GONE);
            }
        });
    }

    private void onBtnDoFilterListener(){
        btnDoFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            pageNumber = 1;

            whereFilterQuery = getFiltersWhere();

            movimientos.clear();

            ArrayList<Movimiento> tempMovimientos = dbOperations.getMovimientosList(whereFilterQuery, ConstantsUtils.MOVIMIENTOS_PAGE_ITEMS, pageNumber);

            movimientos.addAll(tempMovimientos);

            //Log.e(TAG, "FILTERED ITEMS " + movimientos.size());

            mAdapter.notifyDataSetChanged();

            scrollListener.resetState();

            if(movimientos.size() == 0){
                Toast.makeText(getActivity(), "No hay resulados!", Toast.LENGTH_SHORT).show();
            }else{
                filterLayout.setVisibility(View.GONE);
            }
            }
        });
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

                //Toast.makeText(getActivity(), "ESCUCHO", Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface OnItemTouchListener {
        public void onCardViewTap(View view, int position);
    }
}
