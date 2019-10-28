package com.example.demo.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SqlExecuteHandler implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // get mapper info
        MapperInfo info = getMapperInfo(method);

        // execute sql
        return executeSql(info, args);
    }

    private MapperInfo getMapperInfo(Method method) throws Exception {
        MapperInfo info = SqlMappersHolder.INSTANCE.getMapperInfo(
                method.getDeclaringClass().getName(),
                method.getName());
        if (info == null) {
            throw new Exception("Mapper not found for method: " +
                    method.getDeclaringClass().getName() + "." + method.getName());
        }
        return info;
    }

    private Object executeSql(MapperInfo info, Object[] params)
            throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object result = null;
        PreparedStatement pstat = ConnectionManager.get().prepareStatement(info.getSql());
        for (int i = 0; i < params.length; i++) {
            pstat.setObject(i + 1, params[i]);
        }


        if (info.getQueryType() == QueryType.SELECT) {
            ResultSet rs = pstat.executeQuery();
            rs.first();
            // 将查询结果映射为Java类或基本数据类型）
            // 目前简化版仅支持String和int两种类型
            if (rs.getMetaData().getColumnCount() == 1) {
                switch (info.getResultType()) {
                    case "int":
                        result = rs.getInt(1);
                        break;
                    default:
                        result = rs.getString(1);
                }
            } else {
                Class<?> resTypeClass = Class.forName(info.getResultType());
                Object inst = resTypeClass.newInstance();
                for (Field field : resTypeClass.getDeclaredFields()) {
                    String setterName = "set" +
                            field.getName().substring(0, 1).toUpperCase() +
                            field.getName().substring(1);
                    Method md;

                    switch (field.getType().getSimpleName()) {
                        case "int":
                            md = resTypeClass.getMethod(setterName, new Class[]{int.class});
                            md.invoke(inst, rs.getInt(field.getName()));
                            break;

                        default:
                            md = resTypeClass.getMethod(setterName, new Class[]{String.class});
                            md.invoke(inst, rs.getString(field.getName()));
                    }
                }
                result = inst;
            }
        } else {
            result = pstat.executeUpdate();
        }
        return result;
    }

}
