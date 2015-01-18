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
		
		if ((ue != null) && (ue.getNbOptionsMini() == 0)) {
			view = (View) inflater.inflate(R.layout.simple_list, null);

			TextView text = (TextView) view.findViewById(R.id.titre);
			text.setText(ue.getNom());
		} else if ((ue != null) && (ue.getNbOptionsMini() > 0)) {
			view = (View) inflater.inflate(R.layout.admin_ue_list, null);

			TextView text1 = (TextView) view.findViewById(R.id.titre);
			text1.setText(ue.getNom()); 
			
			TextView text2 = (TextView) view.findViewById(R.id.description);
			text2.setText("Nombre d'option(s) minimum : " + ue.getNbOptionsMini());
		} else {
			view = (View) inflater.inflate(R.layout.simple_list, null);

			TextView text = (TextView) view.findViewById(R.id.titre);
			text.setText("Aucune UE.");
		}

		return view;
	}

}