package org.gl.jmd.view.list.item;

import org.gl.jmd.R;
import org.gl.jmd.model.UE;
import org.gl.jmd.view.list.TwoTextArrayAdapter.RowType;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ListItemUE implements Item {
	
	private UE ue;
	
	private int posUE;
	
	public ListItemUE() {
		this.ue = null;
		this.posUE = 0;
	}

	public ListItemUE(UE ue, int posUE) {
		this.ue = ue;
		this.posUE = posUE;
	}
	
	public UE getUE() {
		return this.ue;
	}

	public int getPosUE() {
		return this.posUE;
	}

	@Override
	public int getViewType() {
		return RowType.LIST_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.simple_list, null);
		} else {
			view = convertView;
		}

		TextView text1 = (TextView) view.findViewById(R.id.titre);
		
		if (ue != null) {
			text1.setText(ue.getNom()); 
		} else {
			view = (View) inflater.inflate(R.layout.simple_list, null);

			TextView text = (TextView) view.findViewById(R.id.titre);
			text.setText("Aucune UE.");
		}

		return view;
	}

}