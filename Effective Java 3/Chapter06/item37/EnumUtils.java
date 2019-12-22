package util;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumUtils {
	
	/**
	 * findEnum
	 * 
	 * @param <T> 
	 * @param clz Enum type
	 * @param filter 찾을 조건
	 * @param defaultEnumValue 값이 없을 경우 반환할 값
	 * @return 조건에 맞는 값 반환
	 */
	public static <T extends Enum<T>> T findEnum(Class<T> clz, Predicate<T> filter, T defaultEnumValue) {
		return EnumSet.allOf(clz).parallelStream()
			.filter(filter)
			.findFirst()
			.orElse(defaultEnumValue);
	}
	
	public static <T extends Enum<T>> T findEnum(Class<T> clz, Predicate<T> filter) {
		return findEnum(clz,filter,null);
	}
	
	/**
	 * fromString
	 * 
	 * @param enumMap stringToEnum을 반환받은 enumMap
	 * @param type 코드 값
	 * @return enum 열거 값을 String 타입으로 반환
	 */
	public static Optional<Object> fromString(Map<String,Object> enumMap, String type) {
	    return Optional.ofNullable(enumMap.get(type));
	}
	
	/**
	 * stringToEnum
	 * 
	 * @param val enum 열거값에 대한 values()
	 * @return Map<String,Object> 반환
	 */
	@SuppressWarnings("rawtypes")
	public static <T> Map<String, Object> stringToEnum(T[] val) {
		return Stream.of(val).collect(
					Collectors.toMap(Object::toString, e -> ((Enum)e).name())
				);
	}

}
