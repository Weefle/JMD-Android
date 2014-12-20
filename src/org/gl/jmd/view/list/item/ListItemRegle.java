package org.gl.jmd.view.list.item;

import org.gl.jmd.R;
import org.gl.jmd.model.Regle;
import org.gl.jmd.view.list.TwoTextArrayAdapter.RowType;

import android.view.*;
import android.widget.TextView;

public class ListItemRegle implements Item {
	
	private Regle r;

	public ListItemRegle(Regle r, int posRegle) {
		this.r = r;
	}
	
	public Regle getRegle() {
		return this.r;
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

		TextView text = (TextView) view.findViewById(R.id.titre);
		text.setText(r.getNomUE());			

		return view;
	}

}