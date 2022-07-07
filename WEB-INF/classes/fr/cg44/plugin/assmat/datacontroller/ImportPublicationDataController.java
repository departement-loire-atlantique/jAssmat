package fr.cg44.plugin.assmat.datacontroller;

import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Data;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

public class ImportPublicationDataController extends BasicDataController {

	@Override
	public void beforeWrite(Data data, int op, Member mbr, Map map) {
		if (op == OP_CREATE || op == OP_UPDATE) {
			if (Util.notEmpty(map)) {
				for (Object it : map.keySet()) {
					if (Util.notEmpty(it)) {
						if (Util.notEmpty(map.get(it))) {
							Object o = map.get(it);
							if (o instanceof Element) {
								Element elementRoot = (Element) o;
								List<Element> listElements = elementRoot.getChildren();
								for (Element element : listElements) {
									if ("extradatas".equals(element.getAttributeValue("name"))) {
										List<Element> listeExtradatas = element.getChildren();
										for (Element eltExtradata : listeExtradatas) {
											data.setExtraData(eltExtradata.getAttributeValue("name"), eltExtradata.getValue());
										}
									}
									if ("extradbdatas".equals(element.getAttributeValue("name"))) {
										List<Element> listeExtraDBdatas = element.getChildren();
										for (Element eltExtraDBdata : listeExtraDBdatas) {
											data.setExtraDBData(eltExtraDBdata.getAttributeValue("name"), eltExtraDBdata.getValue());
										}
									}
								}

							}
						}
					}
				}
			}
		}
	}
}
