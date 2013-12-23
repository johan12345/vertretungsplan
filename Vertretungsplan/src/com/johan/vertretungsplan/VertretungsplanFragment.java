package com.johan.vertretungsplan;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

public class VertretungsplanFragment extends Fragment {
	protected OnFragmentInteractionListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(String type, Fragment sender);
	}
}
