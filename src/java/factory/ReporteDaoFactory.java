/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory;

import java.sql.SQLException;
import dao.ReporteDao;
import dao.ReporteDaoMongo;
import dao.ReporteDaoMysql;
import dao.ReporteDaoPostgres;
import dao.connection.DataBaseConnection;
import factory.DataBaseConnectionFactory;
/**
 *
 * @author Usuario
 */
public class ReporteDaoFactory {
        public static ReporteDao dao(String tipoDao) throws SQLException{
                DataBaseConnection conn = DataBaseConnectionFactory.connection(tipoDao);
                switch (tipoDao.toLowerCase()) {
            case "postgres" -> {
                return new ReporteDaoPostgres(conn);
                }
            case "mysql" -> {
                return new ReporteDaoMysql(conn);
                }
            case "mongo" -> {
                return new ReporteDaoMongo(conn);
                }
            default -> throw new AssertionError();
        }
    }
    
}
