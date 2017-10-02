package com.yamlConversor.definitions;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.yamlConversor.util.TabHelper;

public class YamlObjectGenerator implements TabHelper {

	private static final String fileDefinitionYamlName = "yaml(x).yaml";

	public static String generateDefinitionsYaml(Object obj, String superClazz) throws Exception {
		StringBuilder sb = new StringBuilder();
		String simpleName = obj.getClass().getSimpleName();
		sb.append(yamlFullTab + simpleName + ":\n" + repeat(4) + "type: ");
		sb.append(getObject(obj));
		if (superClazz != null && !superClazz.equals("Object"))
			sb.append(repeat(6) + toCamelCase(superClazz) + ":\n" + repeat(8) + "$ref: \"#/definitions/" + superClazz
					+ "\"\n");

		new File("yaml/").mkdir();
		File yamlDefinition = new File("yaml/" + fileDefinitionYamlName.replace("x", simpleName));
		PrintWriter writer = new PrintWriter(yamlDefinition, "UTF-8");
		writer.println(sb.toString());
		writer.close();

		return sb.toString();
	}

	private static String getObject(Object obj) throws Exception {
		Class<?> clazz = obj.getClass();

		StringBuilder sb = new StringBuilder();
		ArrayList<Field> fields = new ArrayList<Field>(Arrays.asList(clazz.getDeclaredFields()));

		sb.append("object\n");
		sb.append(repeat(4) + "properties:\n");
		for (Field field : fields) {
			field.setAccessible(true);
			if (!field.isAccessible() || !Modifier.isStatic(field.getModifiers()))
				sb.append(repeat(6) + field.getName() + ":\n" + repeat(8)
						+ getTypesFormatted(field, field.getType().getSimpleName()) + "\n");
		}

		return sb.toString().replaceAll("int", "integer");
	}

	private static String getTypesFormatted(Field field, String type) throws Exception {
		type = type.toLowerCase();

		if (Collection.class.isAssignableFrom(field.getType()))
			type = "type: " + getArrays(field);
		else if (type.contains("[]"))
			type = "type: " + "array\n" + repeat(6) + "items:\ntype: " + type.substring(0, type.length() - 2);

		else if (!isPrimitive(field)) {
			char firstChar = Character.toUpperCase(type.charAt(0));
			type = firstChar + type.substring(1);
			type = "$ref: \"#/definitions/" + type + "\"";
		} else
			type = "type: " + type;

		return type.replaceAll("integer", "int");
	}

	private static boolean isPrimitive(Field field) {
		Class<?> clazz = field.getType();
		return getPrimitiveTypes().contains(clazz);
	}

	private static boolean isPrimitive(String type) throws Exception {
		Class<?> clazz = Class.forName(type);
		return getPrimitiveTypes().contains(clazz);
	}

	private static String getArrays(Field field) throws Exception {
		ParameterizedType pt = (ParameterizedType) field.getGenericType();
		String preType = "array\n" + repeat(8) + "items:\n" + repeat(10) + "type: ";
		String rawType = pt.getActualTypeArguments()[0].getTypeName();
		String[] split = rawType.toLowerCase().split("\\.");
		String type = split[split.length - 1];
		type = (type.equals("integer") ? "int" : type);
		if (!isPrimitive(rawType))
			type = getObject(Class.forName(rawType).newInstance());
		return preType + type;
	}

	private static Set<Class<?>> getPrimitiveTypes() {
		Set<Class<?>> primitiveTypes = new HashSet<Class<?>>();
		primitiveTypes.add(boolean.class);
		primitiveTypes.add(char.class);
		primitiveTypes.add(String.class);
		primitiveTypes.add(byte.class);
		primitiveTypes.add(short.class);
		primitiveTypes.add(int.class);
		primitiveTypes.add(Integer.class);
		primitiveTypes.add(long.class);
		primitiveTypes.add(float.class);
		primitiveTypes.add(Double.class);
		primitiveTypes.add(double.class);
		primitiveTypes.add(void.class);
		return primitiveTypes;
	}

	private static String toCamelCase(String str) {
		char firstChar = Character.toLowerCase(str.charAt(0));
		return firstChar + str.substring(1);
	}

	private static String repeat(int count) {
		return TabHelper.repeat(count);
	}

}
