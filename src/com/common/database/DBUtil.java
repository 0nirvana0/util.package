package com.common.database;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.common.config.PropertiesUtil;
import com.common.file.FileUtil;

/**
 * 数据库连接，获取表，(字段名、类型、长度)列表，备份还原数据库
 * @author liuqiang
 *
 */
public class DBUtil {
	private static Log logger = LogFactory.getLog(DBUtil.class);
	private static Properties pros = PropertiesUtil.getPropertiesUtil("config/db/dbback.properties").loadProperties();
	public static Map<String, String> backUpTableList = new ConcurrentHashMap<String, String>();
	public static Map<String, String> recoverTableList = new ConcurrentHashMap<String, String>();

	public static DBUtil getDBUtil() {
		return new DBUtil();
	}

	/**
	 * 获取 Connection
	 * 只用于mysql
	 * @param username	用户名
	 * @param password	密码
	 * @param dburl		数据库连接地址:端口
	 * @param databaseName 数据库名
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public Connection getConnect(String username, String password, String dburl, String databaseName) throws SQLException, ClassNotFoundException {
		return DriverManager.getConnection("jdbc:mysql://" + dburl + "/" + databaseName + "?user=" + username + "&password=" + password);
	}

	/**
	 * @return 获取conn对象(通过PageData)
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
//	public Connection getConnect(PageData pd) throws ClassNotFoundException, SQLException {
//		String username = pd.getString("username"); // 用户名
//		String password = pd.getString("password"); // 密码
//		String address = pd.getString("dbAddress"); // 数据库连接地址
//		String dbport = pd.getString("dbport"); // 端口
//		String databaseName = pd.getString("databaseName"); // 数据库名
//		return getConnect(username, password, address + ":" + dbport, databaseName);
//	}

	/**
	 * @return 获取conn对象(通过配置文件)
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConnect() throws ClassNotFoundException, SQLException {
		String username = pros.getProperty("username"); // 用户名
		String password = pros.getProperty("password"); // 密码
		String address = pros.getProperty("dbAddress"); // 数据库连接地址
		String dbport = pros.getProperty("dbport"); // 端口
		String databaseName = pros.getProperty("databaseName"); // 数据库名
		return getConnect(username, password, address + ":" + dbport, databaseName);
	}

	/**获取本数据库的所有表名(通过PageData)
	 * @return
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
//	public List<String> getTables(PageData pd) throws ClassNotFoundException, SQLException {
//		Connection conn = getConnect(pd);
//		List<String> tables = getTables(conn);
//		return tables;
//
//	}

	/**获取本数据库的所有表名(通过配置文件)
	 * @return
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public List<String> getTables() throws ClassNotFoundException, SQLException {
		Connection conn = getConnect();
		List<String> tables = getTables(conn);
		return tables;
	}

	/**
	* 获取某个conn下的所有表
	* @param conn 数据库连接对象
	* @return
	*/
	public List<String> getTables(Connection conn) {
		try {
			List<String> listTb = new ArrayList<String>();
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				listTb.add(rs.getString(3));
			}
			return listTb;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**(字段名、类型、长度)列表
	 * @param conn
	 * @param table
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
//	public static List<Map<String, String>> getFieldParameterLsit(PageData pd, String table) throws SQLException, ClassNotFoundException {
//		Connection conn = DBUtil.getDBCon(pd);
//		PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement("select * from " + table);
//		pstmt.execute(); // 这点特别要注意:如果是Oracle而对于mysql可以不用加.
//		List<Map<String, String>> columnList = new ArrayList<Map<String, String>>(); // 存放字段
//		ResultSetMetaData rsmd = (ResultSetMetaData) pstmt.getMetaData();
//		for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
//			Map<String, String> fmap = new HashMap<String, String>();
//			fmap.put("fieldNanme", rsmd.getColumnName(i)); // 字段名称
//			fmap.put("fieldType", rsmd.getColumnTypeName(i)); // 字段类型名称
//			fmap.put("fieldLength", String.valueOf(rsmd.getColumnDisplaySize(i))); // 长度
//			fmap.put("fieldSccle", String.valueOf(rsmd.getScale(i))); // 小数点右边的位数
//			columnList.add(fmap); // 把字段名放list里
//		}
//		return columnList;
//	}

	//==========================================================
	//备份数据库

	/**
	 * 执行数据库备份入口
	 * @param tableName 表名
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Object backup(String tableName) throws InterruptedException, ExecutionException {

		if (null != backUpTableList.get(tableName)) {
			return null;
		}

		backUpTableList.put(tableName, tableName); // 标记已经用于备份(防止同时重复备份,比如备份一个表的线程正在运行，又发来一个备份此表的命令)
		ExecutorService pool = Executors.newFixedThreadPool(2);
		Callable<Object> fhc = new DBBackUpCallable(tableName); // 创建一个有返回值的线程
		Future<Object> f1 = pool.submit(fhc); // 启动线程
		String backstr = f1.get().toString(); // 获取线程执行完毕的返回值
		pool.shutdown(); // 关闭线程
		return backstr;
	}

	/**执行数据库还原入口
	 * @param tableName 表名
	 * @param sqlFilePath 备份文件存放完整路径
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Object recover(String tableName, String sqlFilePath) throws InterruptedException, ExecutionException {
		if (null != recoverTableList.get(tableName))
			return null;
		recoverTableList.put(tableName, tableName); // 标记已经用于还原(防止同时重复还原,比如还原一个表的线程正在运行，又发来一个还原此表的命令)
		ExecutorService pool = Executors.newFixedThreadPool(2);
		Callable<Object> fhc = new DBRecoverCallable(tableName, sqlFilePath); // 创建一个有返回值的线程
		Future<Object> f1 = pool.submit(fhc); // 启动线程
		String backstr = f1.get().toString(); // 获取线程执行完毕的返回值
		pool.shutdown(); // 关闭线程
		return backstr;
	}

	/**
	 * 用于执行某表的备份(内部类)线程
	 * @author liuqiang
	 *
	 */
	class DBBackUpCallable implements Callable<Object> {
		String tableName = null;

		public DBBackUpCallable(String tableName) {
			this.tableName = tableName;
		}

		@Override
		public Object call() throws Exception {
			try {

				String username = pros.getProperty("username"); // 用户名
				String password = pros.getProperty("password"); // 密码
				String address = pros.getProperty("dbAddress"); // 数据库连接地址
				String databaseName = pros.getProperty("databaseName"); // 数据库名
				String sqlpath = pros.getProperty("sqlFilePath"); // 存储路径
				//String ffilename = DateUtil.getSdfTimes();//yyyyMMddHHmmss
				String ffilename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				String commandStr = "";
				//=============== 
				// 数据库在本地(和tomcat在同一台服务器上)
				FileUtil.createDir(sqlpath);
				commandStr = getExecStr(address, username, password, sqlpath, tableName, databaseName, ffilename); // 命令语句
				Runtime cmd = Runtime.getRuntime();
				Process p = cmd.exec(commandStr);
				p.waitFor(); // 该语句用于标记，如果备份没有完成，则该线程持续等待
				//================
				String fileType = ".sql";
				if ("".equals(tableName)) {
					return sqlpath + databaseName + "_" + ffilename + fileType;
				} else {
					return sqlpath + tableName + "_" + ffilename + fileType;
				}
			} catch (Exception e) {
				logger.error("备份操作出现问题", e);
				return "errer";
			} finally {
				backUpTableList.remove(tableName); // 最终都将解除
			}
		}

	}

	/**数据库备份命令字符串
	 * @param address 数据库连接地址
	 * @param username 用户名
	 * @param password 密码
	 * @param sqlpath 存储路径
	 * @param tableName 表名
	 * @param databaseName 数据库名
	 * @param ffilename 日期当路径和保存文件名的后半部分
	 * @return 完整的命令字符串
	 */
	public String getExecStr(String address, String username, String password, String sqlpath, String tableName, String databaseName, String ffilename) {
		StringBuffer sb = new StringBuffer();
		sb.append("mysqldump ");
		sb.append("--opt ");
		sb.append("-h ");
		sb.append(address);
		sb.append(" ");
		sb.append("--user=");
		sb.append(username);
		sb.append(" ");
		sb.append("--password=");
		sb.append(password);
		sb.append(" ");
		sb.append("--lock-all-tables=true ");
		sb.append("--result-file=");
		sb.append(sqlpath);
		sb.append(("".equals(tableName) ? databaseName + "_" + ffilename : tableName + "_" + ffilename) + ".sql");
		sb.append(" ");
		sb.append("--default-character-set=utf8 ");
		sb.append(databaseName);
		sb.append(" ");
		sb.append(tableName);// 当tableName为“”时，备份整库
		return sb.toString();
	}

	/**
	 * 用于执行某表或整库的还原(内部类)线程
	 * @author liuqiang
	 *
	 */
	class DBRecoverCallable implements Callable<Object> {
		String tableName = null;
		String sqlFilePath = null;

		public DBRecoverCallable(String tableName, String sqlFilePath) {
			this.tableName = tableName;
			this.sqlFilePath = sqlFilePath;
		}

		@Override
		public Object call() {
			try {

				String username = pros.getProperty("username"); // 用户名
				String password = pros.getProperty("password"); // 密码
				String databaseName = pros.getProperty("databaseName"); // 数据库名

				this.recoverMysql(sqlFilePath, username, password, databaseName);
				return "ok";

			} catch (Exception e) {
				logger.error("还原操作出现问题", e);
				return "errer";
			} finally {
				recoverTableList.remove(tableName); // 最终都将解除
			}
		}
		//mysql -umysql -pmysql -e source d:/backup.sql
		/**还原mysql数据库命令
		 * @param sqlFilePath 备份文件的完整路径
		 * @param username 用户名 例如：root
		 * @param password 用户密码
		 * @param databaseName 数据库名
		 * @throws IOException
		 */
		public void recoverMysql(String sqlFilePath, String username, String password, String databaseName) throws IOException {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec("mysql -u " + username + " -p" + password + " " + databaseName);
			OutputStream outputStream = process.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFilePath), "utf8"));
			String str = null;
			StringBuffer sb = new StringBuffer();
			while ((str = br.readLine()) != null) {
				sb.append(str + "\r\n");
			}
			str = sb.toString();
			OutputStreamWriter writer = new OutputStreamWriter(outputStream, "utf-8");
			writer.write(str);
			writer.flush();
			outputStream.close();
			br.close();
			writer.close();
		}
	}

}
