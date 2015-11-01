package classes;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
/**
 * Author
 * @author Marco
 */

public class Database { 
    private static Connection db;
    public static PrintWriter out;
    private static boolean connect;

    
    public static void setOut(PrintWriter out){
        Database.out = out;
    }
    
    /**
     * Connessione al database
     */
    public static void connect(){
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/collaborative_genealogy");
            Database.db  = ds.getConnection();
            connect = true;
        } catch (NamingException | SQLException ex) {
            connect = false;
        }
    }
    
    /**
     * Verifica se il database è connesso
     * @return true se il db è connesso, false altrimenti
     */
    public static boolean isConnected(){
        return Database.connect;
    }
    
     
    /**
     * Chiusura connessione al database
     */
    public static void close(){
        try { 
            Database.db.close();
            connect = false;
        } catch (SQLException ex) {
            
        }
    }
    
    /**
     * Select record con condizione
     * @param table         tabella da cui prelevare i dati
     * @param condition     condizione per il filtro dei dati
     * @return              dati prelevati
     * @throws java.sql.SQLException
     */
    public static ResultSet selectRecord(String table, String condition) throws SQLException {
        // Generazione query
        String query = "SELECT * FROM " + table + " WHERE " + condition;
        // Esecuzione query
        return Database.executeQuery(query);
    }
    /**
     * Select record con condizione e ordinamento
     * @param table         tabella da cui prelevare i dati
     * @param condition     condizione per il filtro dei dati
     * @param order         ordinamento dei dati
     * @return              dati prelevati
     * @throws java.sql.SQLException
     */
    public static ResultSet selectRecord(String table, String condition, String order) throws SQLException{
        // Generazione query
        String query = "SELECT * FROM " + table + " WHERE " + condition + " ORDER BY " + order;
        // Esecuzione query
        return Database.executeQuery(query);
    }
    
    /**
     * Select record con join tra due tabelle
     * @param table_1           nome della prima tabella
     * @param table_2           nome della seconda tabella
     * @param join_condition    condizione del join tra la tabelle
     * @param where_condition   condizione per il filtro dei dati
     * @return                  dati prelevati
     * @throws java.sql.SQLException
     */
    public static ResultSet selectJoin(String table_1, String table_2, String join_condition, String where_condition) throws SQLException{
        // Generazione query
        String query = "SELECT * FROM " + table_1 + " JOIN " + table_2 + " ON " + join_condition + " WHERE " + where_condition;
        // Esecuzione query
        return Database.executeQuery(query);
    }
    
    /**
     * Select record con join tra due tabelle e ordinamento
     * @param table_1           nome della prima tabella
     * @param table_2           nome della seconda tabella
     * @param join_condition    condizione del join tra la tabelle
     * @param where_condition   condizione per il filtro dei dati
     * @param order             ordinamento dei dati
     * @return                  dati prelevati
     * @throws java.sql.SQLException
     */
    public static ResultSet selectJoin(String table_1, String table_2, String join_condition, String where_condition, String order) throws SQLException{
        // Generazione query
        String query = "SELECT * FROM " + table_1 + " JOIN " + table_2 + " ON " + join_condition + " WHERE " + where_condition + "ORDER BY" + order;
        // Esecuzione query
        return Database.executeQuery(query);
    }
    
    /**
     * Insert record
     * @param table     tabella in cui inserire i dati
     * @param data      dati da inserire
     * @return dati     prelevati
     * @throws java.sql.SQLException
     */
    public static boolean insertRecord(String table, Map<String, Object> data) throws SQLException{
        // Generazione query
        String query = "INSERT INTO " + table + " SET ";
        Object value;
        String attr;
        
        for(Map.Entry<String,Object> e:data.entrySet()){
            attr = e.getKey();
            value = e.getValue();
            if(value instanceof Integer){
                query = query + attr + " = " + value + ", ";
            }else{
                value = value.toString().replace("\'", "\\'");
                query = query + attr + " = '" + value + "', ";
            }
        }
        query = query.substring(0, query.length() - 2);
        // Esecuzione query
        return Database.updateQuery(query);
    }
    
    /**
     * Update record
     * @param table         tabella in cui aggiornare i dati
     * @param data          dati da inserire
     * @param condition     condizione per il filtro dei dati
     * @return              true se l'inserimento è andato a buon fine, false altrimenti
     * @throws java.sql.SQLException
     */
    public static boolean updateRecord(String table, Map<String,Object> data, String condition) throws SQLException{
        // Generazione query
        String query = "UPDATE " + table + " SET ";
        Object value;
        String attr;
        
        for(Map.Entry<String,Object> e:data.entrySet()){
            attr = e.getKey();
            value = e.getValue();
            if(value != null || value.equals("")){
                if(value instanceof String){
                    value = value.toString().replace("\'", "\\'");
                    query = query + attr + " = '" + value + "', ";
                }else{
                    query = query + attr + " = " + value + ", ";
                }
            }else{
                Database.resetAttribute(table, attr, condition);
            }
            
        }
        query = query.substring(0, query.length()-2) + " WHERE " + condition;
        
        // Esecuzione query
        return Database.updateQuery(query);
    }
    
    /**
     * Delete record
     * @param table         tabella in cui eliminare i dati
     * @param condition     condizione per il filtro dei dati
     * @return              true se l'eliminazione è andata a buon fine, false altrimenti
     * @throws java.sql.SQLException
     */
    public static boolean deleteRecord(String table, String condition) throws SQLException{
        // Generazione query
        String query = "DELETE FROM " + table + " WHERE " + condition;
        // Esecuzione query
        return Database.updateQuery(query);
    }
    
    /**
     * Count record
     * @param table         tabella in cui contare i dati
     * @param condition     condizione per il filtro dei dati
     * @return              numero dei record se la query è stata eseguita on successo, -1 altrimenti
     */
    public static int countRecord(String table, String condition){
        // Generazione query
        String query = "SELECT COUNT(*) FROM " + table + " WHERE " + condition;
        try {
            // Esecuzione query
            ResultSet record = Database.executeQuery(query);
            record.next();
            // Restituzione del risultato
            return record.getInt(1);
        } catch (SQLException ex) {
            // Restituzione di -1 se l'esecuzione della query non è andata a buon fine
            return -1;
        }
        
    }
    
    /**
     * Imposta a NULL un attributo di una tabella  
     * @param table         tabella in cui è presente l'attributo
     * @param attribute     attributo da impostare a NULL
     * @param condition     condizione
     * @return
     * @throws java.sql.SQLException
     */
    public static boolean resetAttribute(String table, String attribute, String condition) throws SQLException{
        String query = "UPDATE " + table + " SET " + attribute + " = NULL WHERE " + condition;
        return Database.updateQuery(query);
    }
     
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodi ausiliari.">
    
    /**
     * executeQuery personalizzata
     * @param query query da eseguire
     */
    private static ResultSet executeQuery(String query) throws SQLException{
        
        Statement s1 = Database.db.createStatement();
        ResultSet records = s1.executeQuery(query);

        return records; 
            
    }
    
    /**
     * updateQuery personalizzata
     * @param query query da eseguire
     */
    private static boolean updateQuery(String query) throws SQLException{
        
        Statement s1;
        
        s1 = Database.db.createStatement();
        s1.executeUpdate(query); 
        s1.close();
        return true; 

    }
   // </editor-fold>
    
}
