package org.gl.jmd.view.list;

import org.gl.jmd.Constantes;
import org.gl.jmd.R;
import org.gl.jmd.model.Matiere;
import org.gl.jmd.view.list.TwoTextArrayAdapter.RowType;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ListItemMatiere implements Item {
	
	private Matiere m;
	
	private int posUE;
	
	private int posMatiere;
	
	public ListItemMatiere() {
		this.m = null;
		this.posUE = 0;
		this.posMatiere = 0;
	}

	public ListItemMatiere(Matiere m, int posUE, int posMatiere) {
		this.m = m;
		this.posUE = posUE;
		this.posMatiere = posMatiere;
	}
	
	public Matiere getMatiere() {
		return this.m;
	}

	public int getPosUE() {
		return this.posUE;
	}
	
	public int getPosMatiere() {
		return this.posMatiere;
	}

	@Override
	public int getViewType() {
		return RowType.LIST_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.etudiant_liste_matiere_list, null);
		} else {
			view = convertView;
		}

		TextView text = (TextView) view.findViewById(R.id.titre);
		
		if (m != null) {			
			if (m.getNom().length() > Constantes.LIMIT_TEXT) {
				text.setText(m.getNom().substring(0, Constantes.LIMIT_TEXT) + "...");
			} else {
				text.setText(m.getNom());
			}
			
			TextView textDesc = (TextView) view.findViewById(R.id.description);
			
			textDesc.setText("Coefficient : " + m.getCoefficient());
			
			TextView textIOption = (TextView) view.findViewById(R.id.isOption);
			
			if (m.isOption()) {
				textIOption.setText("Option");
			} else {
				textIOption.setVisibility(View.GONE);
			}
			
			TextView textNote = (TextView) view.findViewById(R.id.note);
			
			if (m.getNoteFinale() != -1.0) {
				textNote.setText("" + m.getNoteFinale());
				
				if (m.getNoteFinale() >= 10) {							
					textNote.setBackgroundResource(R.drawable.badge_moyenne_ok);
				} else if ((m.getNoteFinale() < 10) && 
						(m.getNoteFinale() >= 0)) {
					
					textNote.setBackgroundResource(R.drawable.badge_moyenne_nok);
				}
			} else {
				textNote.setText("0 note");
				textNote.setBackgroundResource(R.drawable.badge_note);
			}
		} else {
			view = (View) inflater.inflate(R.layout.simple_list, null);
			text = (TextView) view.findViewById(R.id.titre);
			
			text.setText("Aucune matière.");
		}

		return view;
	}

}