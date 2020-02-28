package meta_data;

import java.sql.*;
import java.util.Set;

/**
 * 数据库元数据信息
 */
public class MetaData {

    private Connection connection = null;

    /**
     * 创建连接
     *
     * @param dataBase
     */
    public void create(String dataBase) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.connection = java.sql.DriverManager.getConnection("jdbc:mysql://60.205.184.214:3306/" + dataBase + "?useUnicode=true&characterEncoding=utf8", "root", "root");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库表信息
     *
     * @param dataBase
     * @return
     */
    public ResultSet getTableData(String dataBase) {
        //创建数据库连接
        create(dataBase);
        //数据库信息
        ResultSet dataBasesInfo = null;
        //表字段信息
        ResultSet dataTableInfo = null;
//        createConnection();

        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            dataBasesInfo = dbMetaData.getTables(null, null, null, new String[]{"TABLE"});
            while (dataBasesInfo.next()) {// ///TABLE_TYPE/REMARKS
                System.out.println("表名：" + dataBasesInfo.getString("TABLE_NAME"));
                System.out.println("表类型：" + dataBasesInfo.getString("TABLE_TYPE"));
                System.out.println("表所属数据库：" + dataBasesInfo.getString("TABLE_CAT"));
                System.out.println("表备注：" + getRemark(dataBasesInfo.getString("TABLE_NAME")));
                String tableName = dataBasesInfo.getString("TABLE_NAME");
                String tableType = dataBasesInfo.getString("TABLE_TYPE");
                String tableCat = dataBasesInfo.getString("TABLE_CAT");
                String tableRemark = getRemark(dataBasesInfo.getString("TABLE_NAME"));

                dataTableInfo = dbMetaData.getColumns(null, "%", dataBasesInfo.getString("TABLE_NAME"), "%");
                while (dataTableInfo.next()) {
                    System.out.println("字段名：" + dataTableInfo.getString("COLUMN_NAME") + "\t字段注释：" + dataTableInfo.getString("REMARKS") + "\t字段数据类型：" + dataTableInfo.getString("TYPE_NAME"));
                    String columnName = dataTableInfo.getString("COLUMN_NAME");
                    String columnRemark = dataTableInfo.getString("REMARKS");
                    String columnType = dataTableInfo.getString("TYPE_NAME");

                }
                System.out.println();
            }
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return dataBasesInfo;
    }

    /**
     * 获取表注释信息
     *
     * @param tableName
     * @return
     */
    private String getRemark(String tableName) {
        String sql = "SHOW CREATE TABLE " + tableName;
        PreparedStatement ps = null;
        String comment = null;
        try {
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs != null && rs.next()) {
                String createDDL = rs.getString(2);
                comment = parse(createDDL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comment;
    }

    /**
     * 获取数据库信息
     */
    public void getDataBases() {
        String sql = "SHOW DATABASES";
        PreparedStatement ps = null;
        System.out.println("get dataBases");
        try {
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs != null && rs.next()) {
                String createDDL = rs.getString(1);
                System.out.println(createDDL);

                //获取每个表信息
//                getTableData(createDDL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 传入建表语句，返回表注释信息
     */
    public String parse(String all) {
        String comment = null;
        int index = all.indexOf("COMMENT='");
        if (index < 0) {
            return "";
        }
        comment = all.substring(index + 9);
        comment = comment.substring(0, comment.length() - 1);
        return comment;
    }

    private void close() {
        //关闭连接
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MetaData metaData = new MetaData();
        metaData.create("");
        System.out.println("connect to dataBases....");
        System.out.println("get result");

        ResultSet set = metaData.getTableData("redis");
        metaData.getDataBases();

        metaData.close();
        System.out.println("disconnect");
    }

}
