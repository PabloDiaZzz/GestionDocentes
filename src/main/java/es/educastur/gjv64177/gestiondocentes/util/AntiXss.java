package es.educastur.gjv64177.gestiondocentes.util;


import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;


public class AntiXss extends StdDeserializer<String> {
	public AntiXss() {
		super(String.class);
	}

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
		String value = p.getValueAsString();
		if (value == null)
			return null;
		String limpio = Jsoup.clean(value, Safelist.none());
		return org.jsoup.parser.Parser.unescapeEntities(limpio, true);
	}

	@Override
	public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
		if (property == null) {
			return this;
		}
		boolean claseProtegida = property.getMember().getDeclaringClass().isAnnotationPresent(SeguroXss.class);
		boolean campoExcluido = property.getAnnotation(NoLimpiar.class) != null;

		if (claseProtegida && !campoExcluido) {
			return this;
		}

		return null;
	}
}
