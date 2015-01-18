package org.gl.jmd.view.list;

import org.gl.jmd.R;
import org.gl.jmd.view.list.TwoTextArrayAdapter.RowType;
import org.gl.jmd.view.list.item.Item;

import android.view.*;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Footer implements Item {

	private boolean isAjourne;

	private double moyenne;

	public Footer(boolean isAjourne, double moyenne) {
		this.isAjourne = isAjourne;
		this.moyenne = moyenne;
	}

	@Override
	public int getViewType() {
		return RowType.FOOTER.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;

		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.footer, null);
		} else {
			view = convertView;
		}

		TextView text = (TextView) view.findViewById(R.id.titre);
		text.setText("Moyenne : " + this.moyenne);
		
		TextView isValid = (TextView) view.findViewById(R.id.isValide);
		RelativeLayout.LayoutParams r = (RelativeLayout.LayoutParams) isValid.getLayoutParams();

		if (this.isAjourne) {
			isValid.setText("Défaillant.");
			
			r.width = 150;
		} else if (this.moyenne < 10.0) {
			isValid.setText("Non validée.");
			
			r.width = 190;
		} else {
			isValid.setText("Validée.");
			
			r.width = 140;
		}
		
		isValid.setLayoutParams(r);

		return view;
	}
}