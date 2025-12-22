package lto.manager.web.handlers.http.templates.models;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryModel {
	private final Map<String, Object> queryMap;

	public QueryModel(final String queryStr) {
		queryMap = parseQuery(queryStr);
	}

	public Object get(final String key) {
		return queryMap.get(key);
	}

	public Set<String> keySet() {
		return queryMap.keySet();
	}

	@SuppressWarnings("unchecked")
	public String getString(final String key) {
		var result = queryMap.get(key);
		if (result instanceof String) {
			return (String) result;
		} else if (result != null) {
			return ((List<String>) result).get(0);
		}
		return null;
	}

	public String getString(final String key, final String defaultValue) {
		final String query = getString(key);
		return query == null ? defaultValue : query;
	}

	public String getStringNotNull(final String key) {
		final String query = getString(key);
		return query == null ? "" : query;
	}

	public int getInt(final String key, final int defaultValue) {
		final String value = getString(key);
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public boolean getChecked(final String key) {
		final String value = getString(key);
		return "on".equals(value);
	}

	public boolean getChecked(final String key, final boolean defaultValue) {
		final String value = getString(key);
		return value == null ? defaultValue : "on".equals(value);
	}

	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T getEnum(final String key, T defaultValue) {
		try {
			final String value = getString(key); // Enum value as String
			var enumResult = Enum.valueOf(defaultValue.getClass(), value);
			return (T) enumResult;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends Enum<T>> T getEnumOrdinal(final String key, T defaultValue) {
		try {
			final int ordinal = getInt(key, defaultValue.ordinal()); // Ordinal value of enum
			final EnumSet<? extends Enum> enumSet = EnumSet.allOf(defaultValue.getClass());
			for (final var e : enumSet) { // TODO could be better
				if (e.ordinal() == ordinal) {
					return (T) e;
				}
			}
		} catch (Exception e) {
			return defaultValue;
		}
		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	public List<String> getQueryArray(String key) {
		Object o = queryMap.get(key);
		if (o instanceof String) {
			var list = new ArrayList<String>();
			list.add((String) o);
			return list;
		} else {
			return (List<String>) o;
		}
	}

	public List<String> getArrayNotNull(String key) {
		final List<String> result = getQueryArray(key);
		return result == null ? new ArrayList<String>() : result;
	}

	private Map<String, Object> parseQuery(final String query) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 1) {
					key = URLDecoder.decode(param[0], StandardCharsets.UTF_8);
					value = URLDecoder.decode(param[1], StandardCharsets.UTF_8);
				}

				if (!parameters.containsKey(key)) {
					parameters.put(key, value);
				} else if (key != null) {
					Object o = parameters.get(key);
					if (o instanceof String) {
						List<String> list = new ArrayList<String>();
						list.add((String) o);
						list.add(value);
						parameters.put(key, list);
					} else {
						@SuppressWarnings("unchecked")
						List<String> list = (List<String>) o;
						list.add(value);
					}
				}
			}
		}
		return parameters;
	}
}
