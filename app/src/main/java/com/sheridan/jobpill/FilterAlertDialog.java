package com.sheridan.jobpill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FilterAlertDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.filter_alert_dialog,null);

        EditText location = v.findViewById(R.id.txt_location_filter);
        Spinner catSpinner = v.findViewById(R.id.spinner_category_filter);
        Spinner paySpinner = v.findViewById(R.id.spinner_pay_filter);


        //prepopulate data on the filter options if they exist
        Bundle bundle = getArguments();
       if(!bundle.isEmpty()){
           String loc = bundle.getString("location","");
           int cat = bundle.getInt("category",0);
           int pay = bundle.getInt("estimatedPay",0);
           location.setText(loc);
           catSpinner.setSelection(cat);
           paySpinner.setSelection(pay);


       }




        builder.setView(v);


        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogPositiveClick(FilterAlertDialog.this);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogNegativeClick(FilterAlertDialog.this);
            }
        });

        builder.setNeutralButton("Clear Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogNeutralClick(FilterAlertDialog.this);
            }
        });

        return builder.create();
    }

    public interface FilterDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogNeutralClick(DialogFragment dialog);
    }

    FilterDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity = (Activity) context;

        try{
            listener = (FilterDialogListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" must implement FilterDialogListener");
        }
    }
}
