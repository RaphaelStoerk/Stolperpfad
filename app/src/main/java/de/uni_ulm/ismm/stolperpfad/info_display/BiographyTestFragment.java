package de.uni_ulm.ismm.stolperpfad.info_display;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BiographyTestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BiographyTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BiographyTestFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private int position;

    AQuery aq;

    public BiographyTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BiographyTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BiographyTestFragment newInstance(int position) {
        BiographyTestFragment fragment = new BiographyTestFragment();
        fragment.position = position;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_biography_test, container, false);

        aq = new AQuery(rootView);

        aq.id(R.id.fragment_test_text).text("Page " + position);
        aq.id(R.id.image_biography_fragment).clicked(view -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


                builder.setTitle("Dies ist eine Information");
                builder.setMessage("Sie haben auf einen Informationspunkt einer Biographie gedrückt," +
                        "daher sehen sie hier gerade eine entsprechende Information zu diesem Punkt.");

                builder.setPositiveButton("Danke", (dialogInterface, i) -> {

                });
                builder.setNegativeButton("Bitte", (dialogInterface, i) -> {});

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
        }).getImageView().setColorFilter(
                Color.rgb((int)(Math.random()*150 + 100),
                        (int)(Math.random()*150 + 100),
                        (int)(Math.random()*150 + 100)));
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
