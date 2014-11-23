package org.gl.jmd.view.list;

import org.gl.jmd.R;
import org.gl.jmd.model.Annee;
import org.gl.jmd.view.list.TwoTextArrayAdapter.RowType;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ListItem implements Item {
	
	private Annee a;
	
	private int posDip;
	
	private int posAnn;
	
	public ListItem() {
		this.a = null;
		
		this.posDip = 0;
		this.posAnn = 0;
	}

	public ListItem(Annee a, int posDip, int posAnn) {
		this.a = a;
		
		this.posDip = posDip;
		this.posAnn = posAnn;
	}
	
	public Annee getAnnee() {
		return this.a;
	}
	
	public int getPosDip() {
		return this.posDip;
	}
	
	public int getPosAnnee() {
		return this.posAnn;
	}

	@Override
	public int getViewType() {
		return RowType.LIST_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.etudiant_liste_annees_list, null);
		} else {
			view = convertView;
		}

		TextView text1 = (TextView) view.findViewById(R.id.titre);
		TextView text2 = (TextView) view.findViewById(R.id.description);
		TextView text3 = (TextView) view.findViewById(R.id.note);
		
		if (a != null) {
			text1.setText(a.getNom());
			text2.setText(a.getEtablissement().getNom());
			text3.setText("" + a.getMoyenne());
	
			if (a.getMoyenne() >= 10) {
				text3.setBackgroundResource(R.drawable.badge_moyenne_ok);
			} else if ((a.getMoyenne() < 10) && (a.getMoyenne() >= 0)) {
				text3.setBackgroundResource(R.drawable.badge_moyenne_nok);
			} else if (a.getMoyenne() == -1.0) {
				text3.setText("0 note");
				text3.setBackgroundResource(R.drawable.badge_note);
			} 
		} else {
			view = (View) inflater.inflate(R.layout.simple_list, null);

			TextView text = (TextView) view.findViewById(R.id.titre);
			text.setText("Aucune année.");
		}

		return view;
	}
}