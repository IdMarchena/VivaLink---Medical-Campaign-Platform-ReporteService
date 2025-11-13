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
/**
 *
 * @author Usuario
 */
public class ReporteDaoFactory {
        public static ReporteDao dao(String tipoDao) throws SQLException{
                switch (tipoDao.toLowerCase()) {
            case "postgre":
                return new ReporteDaoPostgres(tipoDao);
            case "mysql":
                return new ReporteDaoMysql(tipoDao);
            case "mongo":
                return new ReporteDaoMongo(tipoDao);               
            default:
                throw new AssertionError();
        }
    }
    
}
