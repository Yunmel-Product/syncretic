package com.yunmel.extreme.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

public class QuerySession extends Session implements QueryFactory {
	private final static QuerySession querySession = new QuerySession();
	private PreparedStatement psmt = null;
	private ResultSet rs = null;
	private int pageSize = 5;//
	private long c = 0;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	protected QuerySession() {
	}

	public static QuerySession getInstanceof() {
		return querySession;
	}

	@Override
	public <T> Set<Object> queryList(String sql, Class<T> clazz, Object[] params) {
		Set<Object> list = null;
		con = getConn();
		if (con != null) {
			try {
				psmt = con.prepareStatement(sql);
				int count = params.length, i;
				for (i = 0; i < count; ++i)
					psmt.setObject(i + 1, params[i]);
				rs = psmt.executeQuery();

				if (isNull(rs)) {
					return null;
				}

				showLog(sql);
				list = invoke(clazz, rs, rs.getMetaData(), rs.getMetaData().getColumnCount());

				close();

			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
		return list;
	}

	@Override
	public <T> T queryOne(String sql, Class<T> clazz, Object[] params) {
		con = getConn();
		if (con != null) {
			try {
				int count = params.length, i;
				psmt = con.prepareStatement(sql);
				for (i = 0; i < count; ++i)
					psmt.setObject(i + 1, params[i]);
				rs = psmt.executeQuery();

				if (isNull(rs)) {
					return null;
				}
				T t = (T) invoke(clazz, rs, rs.getMetaData(), rs.getMetaData().getColumnCount()).iterator().next();
				close();
				return t;
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
		return null;
	}

	@Override
	public void delete(String sql, Object[] params) {
		con = getConn();
		if (con != null) {
			try {
				int count = params.length, i;
				psmt = con.prepareStatement(sql);
				for (i = 0; i < count; ++i)
					psmt.setObject(i + 1, params[i]);
				psmt.execute();
				showLog(sql);
				close();
			} catch (SQLException e) {
				close();
			}
		}
	}

	@Override
	public void update(String sql, Object[] params) {
		con = getConn();
		if (con != null) {
			try {
				int count = params.length, i;
				psmt = con.prepareStatement(sql);
				for (i = 0; i < count; ++i)
					psmt.setObject(i + 1, params[i]);
				psmt.executeUpdate();
				showLog(sql);
				close();
			} catch (SQLException e) {
				e.printStackTrace();
				close();
			}
		}
	}

	@Override
	public <T> void insertBatch(List<Object> list) {
		Object obj = list.get(0);
		String sql = getSaveSQL(obj).keySet().iterator().next();
		con = getConn();
		if (con != null) {
			try {
				con.setAutoCommit(false);
				psmt = con.prepareStatement(sql);
				list.forEach((t) -> {
					for (Map.Entry<String, List<Object>> map : getSaveSQL(t).entrySet()) {
						int size = map.getValue().size();
						List<Object> args = map.getValue();
						for (int i = 0; i < size; ++i) {
							try {
								psmt.setObject(i + 1, args.get(i));
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						c++;
						try {

							psmt.addBatch();
							if (c % 5000 == 0) {
								psmt.executeBatch();
								psmt.clearBatch();
								con.commit();
								showLog(sql);

							}
						} catch (SQLException e) {
							e.printStackTrace();
						}

					}
					try {
						con.commit();
						showLog(sql);

					} catch (SQLException e) {
						e.printStackTrace();
					}
				});

			} catch (Exception e) {
				e.printStackTrace();

			}

			close();
		}
	}

	@Override
	public void deleteBatch(String sql, List<Object[]> list) {
		int count = 0, size = 0;
		con = getConn();
		if (con != null) {
			try {
				con.setAutoCommit(false);
				psmt = con.prepareStatement(sql);
				for (int i = 0; i < list.size(); ++i) {
					count = list.get(i).length;
					for (int j = 0; j < count; ++j) {
						psmt.setObject(j + 1, list.get(i)[j]);
					}
					psmt.addBatch();
					size++;
					if (size % 5000 == 0) {
						psmt.executeBatch();
						psmt.clearBatch();
						con.commit();
						showLog(sql);

					}
				}
				con.commit();
				showLog(sql);

			} catch (SQLException e) {
				e.printStackTrace();
			}

			close();
		}
	}

	@Override
	public void updateBatch(String sql, List<Object[]> list) {
		int count = 0, size = 0;
		con = getConn();
		if (con != null) {
			try {
				con.setAutoCommit(false);
				psmt = con.prepareStatement(sql);
				for (int i = 0; i < list.size(); ++i) {
					count = list.get(i).length;
					for (int j = 0; j < count; ++j) {
						psmt.setObject(j + 1, list.get(i)[j]);

					}
					psmt.addBatch();
					size++;
					if (size % 5000 == 0) {
						psmt.executeBatch();
						psmt.clearBatch();
						con.commit();
						showLog(sql);

					}
				}
				con.commit();
				showLog(sql);

			} catch (SQLException e) {
				e.printStackTrace();
			}

			close();
		}
	}

	@Override
	public long size(String sql, Object[] params) {
		con = getConn();
		if (con != null) {
			try {
				int count = params.length, i;
				psmt = con.prepareStatement(sql);
				for (i = 0; i < count; ++i)
					psmt.setObject(i + 1, params[i]);
				rs = psmt.executeQuery();

				if (isNull(rs)) {
					return 0;
				}

				showLog(sql);
				rs.next();
				long l = rs.getLong(1);
				close();
				return l;
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
		return 0;
	}

	@Override
	public <T> Set<Object> pageNo(String sql, Class<T> clazz, Object[] params, int next) {
		con = getConn();
		if (con != null) {
			try {
				int count = params.length, i;
				psmt = con.prepareStatement(sql);
				for (i = 0; i < count; ++i)
					psmt.setObject(i + 1, params[i]);// �O�Å���
				rs = psmt.executeQuery();

				if (isNull(rs)) {
					return null;
				}

				RowSetFactory rowSetFactory = RowSetProvider.newFactory();
				CachedRowSet cachedRowSet = rowSetFactory.createCachedRowSet();
				cachedRowSet.setPageSize(pageSize);
				cachedRowSet.populate(rs, (next - 1) * pageSize + 1);
				close();
				return invoke(clazz, cachedRowSet, cachedRowSet.getMetaData(),
						cachedRowSet.getMetaData().getColumnCount());
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
		return null;
	}

	@Override
	public <T> Set<Object> pageNo(Class<T> clazz, int next) {
		con = getConn();
		if (con != null) {
			try {
				psmt = con.prepareStatement(getSelectSQL(clazz.newInstance()));
				rs = psmt.executeQuery();

				if (isNull(rs)) {
					return null;
				}

				RowSetFactory rowSetFactory = RowSetProvider.newFactory();
				CachedRowSet cachedRowSet = rowSetFactory.createCachedRowSet();
				cachedRowSet.setPageSize(pageSize);
				cachedRowSet.populate(rs, (next - 1) * pageSize + 1);
				close();
				return invoke(clazz, cachedRowSet, cachedRowSet.getMetaData(),
						cachedRowSet.getMetaData().getColumnCount());
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
		return null;
	}
}