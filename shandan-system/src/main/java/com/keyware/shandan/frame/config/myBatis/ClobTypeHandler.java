package com.keyware.shandan.frame.config.myBatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * 全局的Clob字段类型处理器
 * </p>
 *
 * @author Administrator
 * @since 2021/5/31
 */
@Component
@MappedTypes({String.class})
@MappedJdbcTypes({JdbcType.CLOB})
public class ClobTypeHandler implements TypeHandler<String> {
    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        java.sql.Clob clob = null;
        try {
            if (parameter == null) {
                parameter = "";
            }
            clob = new javax.sql.rowset.serial.SerialClob(parameter.toString().toCharArray());
        } catch (Exception ignored) {

        }
        ps.setClob(i, clob);
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        java.sql.Clob s = rs.getClob(columnName);
        if (rs.wasNull()) {
            return "";
        } else {
            String clobStr = "";
            Reader inStream = s.getCharacterStream();
            char[] c = new char[(int) s.length()];
            try {
                inStream.read(c);
                clobStr = new String(c);
                inStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return clobStr;
        }
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }
}
