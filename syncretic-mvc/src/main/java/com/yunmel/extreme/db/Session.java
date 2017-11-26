package com.yunmel.extreme.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.yunmel.extreme.annotation.db.Column;
import com.yunmel.extreme.annotation.db.Enitry;
import com.yunmel.extreme.annotation.db.GeneratedValue;
import com.yunmel.extreme.annotation.db.Id;
import com.yunmel.extreme.annotation.db.Table;
import com.yunmel.extreme.util.JdbcUtils;

public class Session implements Factory {

	private final static Session session = new Session();

	protected Connection con = null;

	protected String dirverClass = null, jdbcUrl = null, username = null, password = null, dialect = null,
			secondCache = null, showSql = null, c3p0 = null;

	protected String tableName;

	public Connection getCon() {
		return con;
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	public static Session getInstanceof() {
		return session;
	}

	@Override
	public <T> T get(Serializable id, Class<T> clazz) {
		String sql = "";
		con = getConn();
		if (con != null) {
			String tableName = getTableName(clazz);
			sql += "SELECT * FROM " + tableName + " WHERE id=?";
			PreparedStatement psmt = null;

			try {
				psmt = con.prepareStatement(sql);
				psmt.setObject(1, id);
				ResultSet rs = psmt.executeQuery();
				if (rs != null && rs.next()) {
					rs.previous();
					T t = (T) pottResult(rs, clazz).iterator().next();
					close();
					return t;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				close();
			}

		}
		return null;
	}

	@Override
	public <T> Object save(Object t) {
		List<Object> valueList = new ArrayList<Object>();// ��Ų���
		tableName = getTableName(t.getClass());

		con = getConn();

		if (con != null) {
			String sql = "INSERT INTO " + tableName, left = "(", right = "(";
			Field[] fs = t.getClass().getDeclaredFields();

			for (Field f : fs) {
				String field = f.getName();
				String methodName = "get";
				methodName += String.valueOf(field.charAt(0)).toUpperCase() + field.substring(1);
				Column cloum = f.getAnnotation(Column.class);
				String type = f.getType().toString();
				int index = type.lastIndexOf(".");
				type = type.substring(index + 1);
				if (!field.equals("id") && !type.equals("int") || !type.equals("Integer")) {
					if (cloum != null) {
						if (!cloum.name().equals(null))
							left += cloum.name() + ",";
						else
							left += f.getName() + ",";
					} else /* if (f.getAnnotation(OnetToMany.class) == null) */ {
						left += f.getName() + ",";
					}
				}

				try {
					if (!"id".equals(field) /* && f.getAnnotation(OnetToMany.class) == null */) {
						// right += "'" + t.getClass().getMethod(methodName).invoke(t) + "'" + ",";
						right += "?,";
						valueList.add(t.getClass().getMethod(methodName).invoke(t));// ��ֵ
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			left = left.substring(0, left.length() - 1) + ")";
			right = right.substring(0, right.length() - 1) + ")";

			// ƴ��sql
			sql += left + " VALUES" + right;

			try {
				PreparedStatement psmt = con.prepareStatement(sql);
				for (int i = 1; i <= valueList.size(); ++i) {
					psmt.setObject(i, valueList.get(i - 1));
				}
				psmt.execute();
				showLog(sql);
				close();
			} catch (SQLException e) {
				e.printStackTrace();
				close();
			}
		}

		return t;
	}

	@Override
	public <T> void delete(Serializable id, Class<T> clazz) {
		/*
		 * �ж� dialect ����sqlƴ��
		 * 
		 */
		String sql = "DELETE FROM ", tableName;

		con = getConn();

		if (con != null) {
			tableName = getTableName(clazz);
			sql += tableName + " WHERE id=?";
			PreparedStatement psmt = null;
			try {
				psmt = con.prepareStatement(sql);
				psmt.setObject(1, id);
				psmt.execute();
				showLog(sql);
				close();
			} catch (SQLException e) {
				close();
			}
		}
	}

	@Override
	public void update(Serializable id, Object obj) {
		String sql = "UPDATE " + getTableName(obj.getClass()) + " SET ";
		Map<String, Object> map = null;
		PreparedStatement psmt = null;
		con = getConn();

		if (con != null) {
			try {
				map = getMap(obj);
				for (Map.Entry<String, Object> m : map.entrySet()) {
					sql += m.getKey() + "=" + "'" + m.getValue() + "'" + ",";
				}
				int posit = sql.lastIndexOf(",");
				sql = sql.substring(0, posit);
				sql += " WHERE id=?";

				psmt = con.prepareStatement(sql);
				psmt.setObject(1, id);
				psmt.execute();
				close();
				showLog(sql);
			} catch (Exception e) {
				e.printStackTrace();
				close();

			}
		}
	}

	/**
	 * ��ʼ��
	 * 
	 * @throws IOException
	 * @throws JDOMException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	protected Session() {
		List<String> list = new ArrayList<>();

		SAXBuilder builder = new SAXBuilder();

		InputStream ins = Session.class.getClassLoader().getResourceAsStream("cjxy-orm.xml");
		if (ins == null) {
			new RuntimeException("cjxy-orm.xml not find");
		}

		Document doc = null;
		try {
			doc = builder.build(ins);
		} catch (JDOMException | IOException e2) {
			e2.printStackTrace();
		}

		Element element = doc.getRootElement();
		List<Element> elms = ((Element) element.getChildren().get(0)).getChildren();
		for (Element e : elms) {
			String value = e.getText();
			List<Attribute> abs = e.getAttributes();
			String key = abs.get(0).getName();
			if (!key.equals("class")) {
				switch (abs.get(0).getValue()) {
				case "orm.c3p0":
					c3p0 = e.getText();
					break;
				case "connection.dirver_class":
					dirverClass = e.getText();
					break;
				case "connection.url":
					jdbcUrl = e.getText();
					break;
				case "connection.username":
					username = e.getText();
					break;
				case "connection.password":
					password = e.getText();
					break;
				case "orm.dialect":
					dialect = e.getText();
					break;
				case "orm.second_cache":
					secondCache = e.getText();
					break;
				case "show_sql":
					showSql = e.getText();
					break;
				}

			} else {
				list.add(abs.get(0).getValue());
			}
		}

		if (c3p0 != null) {
			if (c3p0.equalsIgnoreCase("true")) {
				con = JdbcUtils.getConnection();
			} else {
				try {
					Class.forName(dirverClass);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					con = DriverManager.getConnection(jdbcUrl, username, password);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		// �������ݱ�
		init(list);
	}

	protected void init(List<String> list) {

		con = getConn();

		list.forEach((str) -> {
			Class<?> clazz = null;
			boolean flag = false;
			try {
				clazz = Class.forName(str);

				Enitry enitry = clazz.getAnnotation(Enitry.class);

				if (enitry != null) {
					String sql = "CREATE TABLE ", field = "", otherSql = "";
					tableName = getTableName(clazz);
					if (con != null) {
						String s = "SELECT COUNT(*) FROM " + tableName;
						Statement stm = con.createStatement();
						try {
							stm.executeQuery(s);
							flag = true;
						} catch (Exception e) {

							System.err.println(tableName + "������,���ڴ�����.....");

							// flag=false;
						}
					}
					if (!flag) {
						sql += tableName + "(";
						Field[] fs = clazz.getDeclaredFields();// ��ȡ�ֶ��ϵ�ע��
						for (Field f : fs) {
							if (f.getName().equals("id")) {// ǿ��Ҫ��
								Id id = f.getAnnotation(Id.class);
								GeneratedValue gValue = f.getAnnotation(GeneratedValue.class);
								if (id != null && gValue != null) {// ����
									String s = gValue.startegy();
									switch (s) {
									case "IDENTITY":
										sql += f.getName() + " Integer  primary key not  null  auto_increment,";
										break;
									case "Auto":
										break;// ��ʱ��ʵ��
									case "UUID":
										break;
									}
								}
							} else {// ��ͨ�ֶ�
								Column cloum = f.getAnnotation(Column.class);
								if (cloum != null) {
									sql += cloum.name();
									sql += " " + cloum.type();
									String type = f.getType().toString();
									int index = type.lastIndexOf(".");
									type = type.substring(index + 1);// ����Д�
									if (!type.equalsIgnoreCase("time") && !type.equals("Date")
											&& !type.equalsIgnoreCase("int") && !type.equalsIgnoreCase("float")
											&& !type.equalsIgnoreCase("double") && !type.equalsIgnoreCase("Integer")
											&& !type.equalsIgnoreCase("Long") && !type.equalsIgnoreCase("TIMESTAMP")
											&& !type.equalsIgnoreCase("DATETIME")) {

										if (!cloum.length().equals(null))
											sql += "(" + cloum.length() + ")";
									}

									if (cloum.unique())
										sql += " UNIQUE";
									if (cloum.nullable()) {
										sql += " NULL";
									} else {
										sql += " NOT NULL";
									}
									sql += ",";

								} else /* if (f.getAnnotation(OnetToMany.class) == null) */ {// �ų�����ӳ��
									sql += f.getName() + " VARCHAR(255),";
								}
							}

						}
						sql = sql.substring(0, sql.length() - 1);
						sql += ")";
						System.err.println("ִ�гɹ�:" + sql + "  ʱ��" + new Date());
						Statement stm = con.createStatement();
						stm.execute(sql);
						stm.close();
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				close();

			}
			/*---------------------------------------------------------------------*/
		});
		close();// ��������ͷ���Դ
	}

	/**
	 * ��ȡ����
	 * 
	 * @param clazz
	 * @return
	 */
	public String getTableName(Class<?> clazz) {
		String tableName = clazz.getSimpleName();
		Table table = clazz.getAnnotation(Table.class);

		if (table != null) {
			return table.name();// ����ע���б���
		}

		return tableName;// ��������
	}

	/**
	 * ����sql
	 * 
	 * @param clazz
	 * @return
	 */
	public String generSql(String targeClassName) {
		String sql = "CREATE TABLE ", tableName = null;
		Object obj = null;
		try {
			obj = Class.forName(targeClassName).newInstance();
			tableName = getTableName(Class.forName(targeClassName));
			sql += tableName + "(";
			Field[] fs = obj.getClass().getDeclaredFields();
			for (Field f : fs) {
				if (f.getName().equals("id")) {
					Id id = f.getAnnotation(Id.class);
					GeneratedValue gValue = f.getAnnotation(GeneratedValue.class);
					if (id != null && gValue != null) {
						String s = gValue.startegy();
						switch (s) {
						case "IDENTITY":
							sql += f.getName() + " Integer  primary key not  null  auto_increment,";
							break;
						case "Auto":
							break;
						case "UUID":
							break;
						}
					}
				} else {
					Column cloum = f.getAnnotation(Column.class);
					if (cloum != null) {
						sql += cloum.name();
						sql += " " + cloum.type();
						String type = f.getType().toString();
						int index = type.lastIndexOf(".");
						type = type.substring(index + 1);
						if (!type.equals("Date")) {
							if (!cloum.length().equals(null))
								sql += "(" + cloum.length() + ")";
						}

						if (cloum.unique())
							sql += " UNIQUE";
						if (cloum.nullable()) {
							sql += " NULL";
						} else {
							sql += " NOT NULL";
						}
						sql += ",";

					} else /* if (f.getAnnotation(OnetToMany.class) == null) */ {// �ų�����ӳ��
						sql += f.getName() + " VARCHAR(255),";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	/**
	 * ��������
	 */

	public boolean beginTransaction() {

		if (con != null) {
			try {
				con.setAutoCommit(false);
				return true;
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean commit() {
		if (con != null) {
			try {
				con.commit();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void close() {

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void rollback() throws SQLException {

		if (con != null) {
			con.rollback();
		}
	}

	protected Set<Object> pottResult(ResultSet rs, Class<?> clazz) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int count = metaData.getColumnCount();
		return invoke(clazz, rs, metaData, count);
	}

	protected <T> Set<Object> invoke(Class<T> clazz, ResultSet rs, ResultSetMetaData metaData, int count) {

		List<String> list = new ArrayList<>();
		Set<Object> result = new LinkedHashSet<>();
		try {
			String type = null;
			Field[] fs = clazz.getDeclaredFields();
			for (Field f : fs) {
				String field = f.getName();
				field = String.valueOf(field.charAt(0)).toUpperCase() + field.substring(1);
				list.add("set" + field);
			}

			while (rs.next()) {

				Object o = Class.forName(clazz.getName()).newInstance();
				for (int i = 0; i < count; ++i) {
					String columnType = metaData.getColumnTypeName(i + 1);
					if (columnType.equalsIgnoreCase("INT") || columnType.equalsIgnoreCase("Integer")) {
						try {
							clazz.getMethod(list.get(i), Integer.class).invoke(o, rs.getInt(i + 1));
						} catch (Exception e) {
							clazz.getMethod(list.get(i), int.class).invoke(o, rs.getInt(i + 1));
						}
					} else if (columnType.equalsIgnoreCase("short") || columnType.equalsIgnoreCase("Short")) {
						try {
							clazz.getMethod(list.get(i), short.class).invoke(o, rs.getString(i + 1));
						} catch (Exception e) {
							clazz.getMethod(list.get(i), Short.class).invoke(o, rs.getString(i + 1));
						}
					} else if (columnType.equalsIgnoreCase("long") || columnType.equalsIgnoreCase("Long")) {
						try {
							clazz.getMethod(list.get(i), long.class).invoke(o, rs.getString(i + 1));
						} catch (Exception e) {
							clazz.getMethod(list.get(i), Long.class).invoke(o, rs.getString(i + 1));
						}
					} else if (columnType.equalsIgnoreCase("VARCHAR") || columnType.equalsIgnoreCase("CHAR")
							|| columnType.equalsIgnoreCase("TEXT")) {
						clazz.getMethod(list.get(i), String.class).invoke(o, rs.getString(i + 1));
					} else if (columnType.equalsIgnoreCase("DATE") || columnType.equalsIgnoreCase("TIME")
							|| columnType.equalsIgnoreCase("TIMESTAMP") || columnType.equalsIgnoreCase("DATETIME")) {
						clazz.getMethod(list.get(i), Date.class).invoke(o, rs.getDate(i + 1));
					} else if (columnType.equalsIgnoreCase("FLOAT")) {
						try {
							clazz.getMethod(list.get(i), Float.class).invoke(o, rs.getFloat(i + 1));
						} catch (Exception e) {
							// ��������쳣
							clazz.getMethod(list.get(i), float.class).invoke(o, rs.getFloat(i + 1));
						}
					} else if (columnType.equalsIgnoreCase("DOUBLE")) {
						try {
							clazz.getMethod(list.get(i), Double.class).invoke(o, rs.getDouble(i + 1));
						} catch (Exception e) {

							clazz.getMethod(list.get(i), double.class).invoke(o, rs.getDouble(i + 1));
						}
					}

					result.add(o);
				}
			}

			return result;

		} catch (Exception e) {
			e.printStackTrace();
			close();
		}

		return null;
	}

	protected Map<String, Object> getMap(Object t) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<String, Object> map = new HashMap<>();
		Field[] fs = t.getClass().getDeclaredFields();

		for (Field f : fs) {
			String field = f.getName();
			String methodName = "get";
			methodName += String.valueOf(field.charAt(0)).toUpperCase() + field.substring(1);
			Column cloum = f.getAnnotation(Column.class);
			String type = f.getType().toString();
			int index = type.lastIndexOf(".");
			type = type.substring(index + 1);
			if (!field.equals("id") && !type.equals("int") || !type.equals("Integer")) {
				if (cloum != null) {
					if (!cloum.name().equals(null)) {
						map.put(cloum.name(), t.getClass().getMethod(methodName).invoke(t));
					}
				} else {
					map.put(f.getName(), t.getClass().getMethod(methodName).invoke(t));
				}
			}
		}

		return map;
	}

	protected void showLog(String sql) {
		if (showSql.equals("true")) {

		}
	}

	protected Map<String, List<Object>> getSaveSQL(Object t) {
		Map<String, List<Object>> map = new LinkedHashMap<>();
		List<Object> valueList = new ArrayList<>();
		tableName = getTableName(t.getClass());

		if (con != null) {
			String sql = "INSERT INTO " + tableName, left = "(", right = "(";
			Field[] fs = t.getClass().getDeclaredFields();

			for (Field f : fs) {
				String field = f.getName();
				String methodName = "get";
				methodName += String.valueOf(field.charAt(0)).toUpperCase() + field.substring(1);
				Column cloum = f.getAnnotation(Column.class);
				String type = f.getType().toString();
				int index = type.lastIndexOf(".");
				type = type.substring(index + 1);
				if (!field.equals("id") && !type.equals("int") || !type.equals("Integer")) {
					if (cloum != null) {
						if (!cloum.name().equals(null))
							left += cloum.name() + ",";
						else
							left += f.getName() + ",";
					} else /* if (f.getAnnotation(OnetToMany.class) == null) */ {
						left += f.getName() + ",";
					}
				}

				try {
					if (!"id".equals(field) /* && f.getAnnotation(OnetToMany.class) == null */) {
						// right += "'" + t.getClass().getMethod(methodName).invoke(t) + "'" + ",";
						right += "?,";
						valueList.add(t.getClass().getMethod(methodName).invoke(t));// ��ֵ
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			left = left.substring(0, left.length() - 1) + ")";
			right = right.substring(0, right.length() - 1) + ")";

			sql += left + " VALUES" + right;
			// sql+="|"+UUID.randomUUID();
			map.put(sql, valueList);
			return map;
		}
		return null;

	}

	@Override
	public <T> long size(Class<T> clazz) {
		long count = 0;
		String sql = "";
		ResultSet rs = null;
		con = getConn();
		if (con != null) {
			String tableName = getTableName(clazz);
			sql += "SELECT COUNT(*) FROM " + tableName;
			PreparedStatement psmt = null;
			try {
				psmt = con.prepareStatement(sql);
				rs = psmt.executeQuery();

				if (isNull(rs)) {
					return 0;
				}

				rs.next();
				count = rs.getLong(1);
				close();
			} catch (SQLException e) {
				e.printStackTrace();
				close();
			}
		}
		return count;
	}

	protected String getSelectSQL(Object obj) {
		String sql = "SELECT * FROM " + getTableName(obj.getClass());
		return sql;
	}

	protected boolean isNull(ResultSet rs) {
		try {
			if (rs != null && rs.next()) {
				rs.previous();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	protected Connection getConn() {
		return JdbcUtils.getConnection();
	}

	protected void close(ResultSet rs, PreparedStatement psmt, Statement stm, Connection con) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (psmt != null && stm != null) {
			try {
				psmt.close();
				stm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}