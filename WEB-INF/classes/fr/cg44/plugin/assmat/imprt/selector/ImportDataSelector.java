package fr.cg44.plugin.assmat.imprt.selector;

import com.jalios.jcms.Data;
import com.jalios.jcms.DataSelector;
import com.jalios.util.Util;

public class ImportDataSelector implements DataSelector {

	String[] id = null;
	
	public ImportDataSelector(String[] id) {
		this.id = id;
	}
	
	@Override
	public boolean isSelected(Data data) {
		if(data.isImported() && Util.notEmpty(id) && Util.arrayContains(id, data.getImportId())){
			return true;
		}
		return false;
	}

}
