/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory;

import dao.connection.MongoConnection;
import dao.connection.MysqlConnection;
import dao.connection.PostgreConnection;
import java.sql.SQLException;
import dao.connection.DataBaseConnection;
/**
 *
 * @author Usuario
 */
public class DataBaseConnectionFactory {
        public static DataBaseConnection connection(String tipoDb) throws SQLException{
        switch (tipoDb.toLowerCase()) {
            case "postgre":
                return (DataBaseConnection) PostgreConnection.conexion();
            case "mysql":
                return (DataBaseConnection) MysqlConnection.conexion();
            case "mongo":
                return (DataBaseConnection) MongoConnection.conexion();
               
            default:
                throw new AssertionError();
        }
    }
}
