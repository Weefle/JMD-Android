package org.gl.jmd.view.list;

import org.gl.jmd.R;
import org.gl.jmd.view.list.TwoTextArrayAdapter.RowType;

import android.view.*;
import android.widget.TextView;

public class Header implements Item {
	
	private final String name;

	public Header(String name) {
		this.name = name;
	}

	@Override
	public int getViewType() {
		return RowType.HEADER_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		View view;
		
		if (convertView == null) {
			view = (View) inflater.inflate(R.layout.header, null);
		} else {
			view = convertView;
		}

		TextView text = (TextView) view.findViewById(R.id.separator);
		text.setText(name);

		return view;
	}
}