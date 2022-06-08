package fr.cg44.plugin.assmat.policyfilter;

import java.util.HashMap;
import java.util.Map;

import com.jalios.jcms.Data;
import com.jalios.jcms.Publication;
import com.jalios.jcms.plugin.Plugin;
import com.jalios.jcms.policy.BasicExportPolicyFilter;
import com.jalios.util.Util;

public class ExportPolicyFilterCd44 extends BasicExportPolicyFilter {
	@Override
	public boolean init(Plugin plugin) {
		return true;
	}

	@Override
	public void processDataExport(Data data, StringBuffer sbf, Map map) {
		Publication pub = (Publication) data;
		if(Util.notEmpty(pub.getExtraDataMap())){
			HashMap<String, String> extraMap = pub.getExtraDataMap();

			StringBuffer sbfData = new StringBuffer();

			sbfData.append("<field name='extradatas'>\n");
			for (String key : extraMap.keySet()) {
				sbfData.append("<field name='"+key+"'>" + extraMap.get(key) + "</field>\n");	
			}
			sbfData.append("</field>\n");

			sbf.insert(sbf.indexOf("</data>"), sbfData.toString());
		}

		if(Util.notEmpty(pub.getExtraDBDataMap())){
			StringBuffer sbfData = new StringBuffer();

			Map<String, String> extraDBMap = pub.getExtraDBDataMap();

			sbfData.append("<field name='extradbdatas'>\n");
			for (String key : extraDBMap.keySet()) {
				sbfData.append("<field name='"+key+"'>" + extraDBMap.get(key) + "</field>\n");	
			}
			sbfData.append("</field>\n");

			sbf.insert(sbf.indexOf("</data>"), sbfData.toString());
		}

		super.processDataExport(data, sbf, map);
	}

}
