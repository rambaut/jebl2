package jebl.util;

import java.util.*;

/**
 * @author rambaut
 *         Date: Nov 27, 2005
 *         Time: 1:31:50 PM
 */
public class AttributableHelper implements Attributable {

	public void setAttribute(String name, Object value) {
		attributeMap.put(name, value);
	}

	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

    public void removeAttribute(String name) {
		attributeMap.remove(name);
	}

    public Set<String> getAttributeNames() {
		return attributeMap.keySet();
	}

	public Map<String, Object> getAttributeMap() {
		return Collections.unmodifiableMap(attributeMap);
	}

	Map<String, Object> attributeMap = new LinkedHashMap<String, Object>();
}

