package com.example.user.simpleui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDrinkOrderListener} interface
 * to handle interaction events.
 * Use the {@link DrinkOrderDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrinkOrderDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // UI Component
    NumberPicker mNumberPicker;
    NumberPicker lNumberPicker;
    RadioGroup iceRadioGroup;
    RadioGroup sugarRadioGroup;
    EditText noteEditText;


    // TODO: Rename and change types of parameters
    private DrinkOrder drinkOrder;

    private OnDrinkOrderListener mListener;

    public DrinkOrderDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DrinkOrderDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static DrinkOrderDialog newInstance(DrinkOrder drinkOrder) {
        DrinkOrderDialog fragment = new DrinkOrderDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, drinkOrder);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            String data = getArguments().getString(ARG_PARAM1);
//            drinkOrder = DrinkOrder.newInstanceWithJsonObject(data);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_drink_order_dialog, container, false);
//    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            drinkOrder = getArguments().getParcelable(ARG_PARAM1);

        }

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View root = layoutInflater.inflate(R.layout.fragment_drink_order_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(root)
                .setTitle(drinkOrder.drink.getName())
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drinkOrder.lNumber = lNumberPicker.getValue();
                        drinkOrder.mNumber = mNumberPicker.getValue();
                        drinkOrder.ice = getSelectedItemFromRadioGroup(iceRadioGroup);
                        drinkOrder.sugar = getSelectedItemFromRadioGroup(sugarRadioGroup);
                        drinkOrder.note = noteEditText.getText().toString();
                        if(mListener != null)
                        {
                            mListener.OnDrinkOrderFinished(drinkOrder);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        mNumberPicker = (NumberPicker)root.findViewById(R.id.MNumberPicker);
        mNumberPicker.setMaxValue(100);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setValue(drinkOrder.mNumber);
        lNumberPicker = (NumberPicker)root.findViewById(R.id.LNumberPicker);
        lNumberPicker.setMaxValue(100);
        lNumberPicker.setMinValue(0);
        lNumberPicker.setValue(drinkOrder.lNumber);
        iceRadioGroup = (RadioGroup)root.findViewById(R.id.iceRadioGroup);
        sugarRadioGroup = (RadioGroup)root.findViewById(R.id.sugarRadioGroup);
        noteEditText = (EditText)root.findViewById(R.id.noteEditText);



        return builder.create();
    }

    private String getSelectedItemFromRadioGroup(RadioGroup radioGroup)
    {
        int id = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)radioGroup.findViewById(id);
        return radioButton.getText().toString();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDrinkOrderListener) {
            mListener = (OnDrinkOrderListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDrinkOrderListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnDrinkOrderListener) {
            mListener = (OnDrinkOrderListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnDrinkOrderListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDrinkOrderListener {
        void OnDrinkOrderFinished(DrinkOrder drinkOrder);
    }
}
