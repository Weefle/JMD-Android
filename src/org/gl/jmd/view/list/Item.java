package org.gl.jmd.view.list;

import android.view.*;

public interface Item {
	
    public int getViewType();
    
    public View getView(LayoutInflater inflater, View convertView);
    
}