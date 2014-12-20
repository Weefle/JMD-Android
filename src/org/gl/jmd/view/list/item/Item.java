package org.gl.jmd.view.list.item;

import android.view.*;

public interface Item {
	
    public int getViewType();
    
    public View getView(LayoutInflater inflater, View convertView);
    
}