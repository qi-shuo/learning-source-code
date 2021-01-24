package com.qis.executor.impl;

import com.qis.config.BoundSql;
import com.qis.executor.Executor;
import com.qis.pojo.Configuration;
import com.qis.pojo.MappedStatement;
import com.qis.utils.GenericTokenParser;
import com.qis.utils.ParameterMapping;
import com.qis.utils.impl.ParameterMappingTokenHandler;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qishuo
 * @date 2021/1/24 6:13 下午
 */
public class SimpleExecutor implements Executor {
    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {

        DataSource dataSource = configuration.getDataSource();
        //1:获取数据库连接
        Connection connection = dataSource.getConnection();
        //2:获取sql语句
        String sql = mappedStatement.getSql();

        BoundSql boundSql = getBoundSql(sql);
        //3:获取预处理的sql
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
        //4:设置参数
        if (params != null && params.length != 0) {
            List<ParameterMapping> paramNameList = boundSql.getParamNameList();
            //参数类型
            String paramType = mappedStatement.getParamType();
            Class<?> paramTypeClass = getClassType(paramType);
            paramTypeClass.getDeclaredConstructor().newInstance();
            for (int i = 0; i < paramNameList.size(); i++) {
                ParameterMapping parameterMapping = paramNameList.get(i);
                String content = parameterMapping.getContent();
                //反射获取属性对象
                Field declaredField = paramTypeClass.getDeclaredField(content);
                //设置暴力访问
                declaredField.setAccessible(true);
                //获取指定属性的值
                Object paramValue = declaredField.get(params[0]);
                //索引是从i+1开始的
                preparedStatement.setObject(i + 1, paramValue);
            }
        }
        //5:执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClassType = getClassType(resultType);
        List<Object> result = new ArrayList<>();
        //6:封装返回体
        while (resultSet.next()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            //从第一1列开始到最后一列
            Object resultObj = resultTypeClassType.getDeclaredConstructor().newInstance();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(columnName);
                //通过内省或者反射进行赋值
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClassType);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(resultObj, columnValue);

            }
            result.add(resultObj);

        }
        return (List<E>) result;
    }

    /**
     * 根据类的全路径获取class
     *
     * @param path
     * @return
     */
    private Class<?> getClassType(String path) throws ClassNotFoundException {
        if (path != null) {
            return Class.forName(path);
        }
        return null;
    }

    /**
     * 完成对#{}的解析工作:将#{}使用?替换,2:解析出#{}里面的值进行存储
     *
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类,配置标记解析器来完成对占位符的解析工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析的sql
        String parseSql = genericTokenParser.parse(sql);
        return BoundSql.builder()
                .sql(parseSql)
                .paramNameList(parameterMappingTokenHandler.getParameterMappings())
                .build();
    }
}
