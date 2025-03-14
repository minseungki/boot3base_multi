package com.example.demo.util;

import com.example.demo.dto.common.enumeration.CodeEnum;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

public abstract class CodeEnumTypeHandler<E extends Enum<E> & CodeEnum> implements TypeHandler<CodeEnum> {

	private final Class<E> type;

	public CodeEnumTypeHandler(Class<E> type) {
		this.type = type;
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, CodeEnum parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getDesc());
	}

	@Override
	public CodeEnum getResult(ResultSet rs, String columnName) throws SQLException {
		return getDescEnum(rs.getString(columnName));
	}

	@Override
	public CodeEnum getResult(ResultSet rs, int columnIndex) throws SQLException {
		return getDescEnum(rs.getString(columnIndex));
	}

	@Override
	public CodeEnum getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getDescEnum(cs.getString(columnIndex));
	}

	private CodeEnum getDescEnum(String code) {
		return EnumSet.allOf(type)
				.stream()
				.filter(value -> value.getDesc().equals(code))
				.findFirst()
				.orElseGet(null);
	}

}