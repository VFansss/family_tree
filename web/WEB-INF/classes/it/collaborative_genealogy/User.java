package it.collaborative_genealogy;



import it.collaborative_genealogy.util.DataUtil;
import it.collaborative_genealogy.exception.NotAllowed;
import it.collaborative_genealogy.tree.GenealogicalTree;
import it.collaborative_genealogy.tree.NodeList;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marco
 */
public class User{
    
    private String id;
    private String name;
    private String surname;
    private String email;
    private String gender;
    private Date birthdate;
    private String birthplace;
    private String biography;
    
    /**
     * Metodo costruttore
     * @param user      contiene i dati personali dell'utente
     * @throws java.sql.SQLException
     */
    public User(ResultSet user) throws SQLException{

        this.id = user.getString("id");
        this.name = user.getString("name");
        this.surname = user.getString("surname");
        this.email = user.getString("email");
        this.gender = user.getString("gender");
        this.birthdate = user.getDate("birthdate");
        this.birthplace = user.getString("birthplace");
        this.biography = user.getString("biography");

    }
    
    public User(String id, String name, String surname, String email, String gender, Date birthdate, String birthplace, String biography) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.gender = gender;
        this.birthdate = birthdate;
        this.birthplace = birthplace;
        this.biography = biography;
    }
    
     
    //<editor-fold defaultstate="collapsed" desc="Metodi GET delle variabili di istanza">
    
    public String getId(){
        return this.id;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getSurname(){
        return this.surname;
    }
    
    public String getEmail(){
        return this.email;
    }
    
    public Date getBirthdate(){
        return this.birthdate;
    }
    
    public String getBirthplace(){
        return this.birthplace;
    }
    
    public String getGender(){
        return this.gender;
    }
    
    public String getBiography() {
        return biography;
    }
    
    public String getMotherId() {
        
        try {
            /* E' possibile che questo valore venga modificato da altri utenti, per cui è necessario prelevarlo ogni volta dal database*/
            ResultSet record = Database.selectRecord("user", "id = '" + this.id +"'");
            if(record.next()){
                return record.getString("mother_id");
            }
        } catch (SQLException ex) {
            return null;
        }
        
        return null;
    }
    
    public String getFatherId() throws SQLException {

        /* E' possibile che questo valore venga modificato da altri utenti, per cui è necessario prelevarlo ogni volta dal database*/
        ResultSet record = Database.selectRecord("user", "id = '" + this.id + "'"); 
        if(record.next()){
            return record.getString("father_id");
        }
        
        return null;
    }
    
    public String getSpouseId() throws SQLException {

        /* E' possibile che questo valore venga modificato da altri utenti, per cui è necessario prelevarlo ogni volta dal database*/
        ResultSet record = Database.selectRecord("user", "id = '" + this.id + "'");
        if(record.next()){
            return record.getString("spouse_id");
        }

        return null;
    }
    
    public int getNumRelative() throws SQLException {

        /* Il numero di parenti può essere modificato anche da altri utenti, per cui è necessario prelevare il valore ogni volta dal database*/
        ResultSet record = Database.selectRecord("user", "id = '" + this.id + "'");
        if(record.next()){
            return record.getInt("num_relatives");
        }

        return 0;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Metodi SET delle variabili di istanza">
    
    /**
     * Aggiorna i dati anagrafici dell'utente
     * @param data          Map contenente i dati da modificare
     * @throws SQLException
     */
    public void setData(Map<String, Object> data) throws SQLException, ParseException{
        Database.updateRecord("user", data, "id = '" + this.getId() + "'");
        this.name = (String) data.get("name");
        this.surname = (String) data.get("surname");
        this.birthdate = DataUtil.stringToDate((String) data.get("birthdate"),"yyyy-MM-dd");
        this.birthplace = (String) data.get("birthplace");
        this.biography = (String) data.get("biography");

        if(data.get("gender") != null){
            this.gender = (String) data.get("gender");
        }
    }
    
    public void setEmail(String email) throws SQLException {
        this.updateAttribute("email", email);
        this.email = email;
    }

    public void setPassword(String password) throws SQLException {
        this.updateAttribute("password", DataUtil.crypt(password));
    }
    
    /**
     * Aggiorna il numero di parenti collegati
     * @throws java.sql.SQLException
     */
    public void setNumRelatives() throws SQLException {
        
        UserList family_tree = this.getUnlabeledTree();
        // Calcola il numero di parenti (-1 per non considerare il parente stesso)
        int tree_size = family_tree.size() - 1;
        
        Map<String, Object> data = new HashMap<>();
        data.put("num_relatives", tree_size);
        
        String condition = "";
        //Bisogna aggiornare i numeri di parenti ad ogni membro dell'albero genealogico
        for(User user: family_tree){
            condition = condition + "id = '" + user.id + "' OR ";
        }
        condition = condition.substring(0, condition.length()-4);

        Database.updateRecord("user", data, condition);
    }
    
    
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione genitori">
    
    /**
     * Recupera il padre e la madre
     * @return
     * @throws java.sql.SQLException
     */
    public UserList getParents() throws SQLException{
        UserList parent = new UserList();
        User mother = this.getMother();
        User father = this.getFather();
        if(mother != null) parent.add(mother);
        if(father != null) parent.add(father);
        return parent;
    }
    /**
     * Recupera il padre o la madre
     * @param gender
     * @return
     * @throws java.sql.SQLException
     */
    public User getParent(String gender) throws SQLException{
        String parent;
        if(gender.equals("female")){
            parent = this.getMotherId();
        }else{
            parent = this.getFatherId();
        }
        return User.getUserById(parent);
        
    }
    /**
     * Aggiungi il padre o la madre
     * @param user  genitore da aggiungere
     * @throws java.sql.SQLException
     * @throws it.collaborative_genealogy.exception.NotAllowed
     */
    public void setParent(User user) throws SQLException, NotAllowed{
        this.canAddLikeParent(user);
        
        if(user.getGender().equals("female")){
            this.updateAttribute("mother_id", user.getId());
        }else{
            this.updateAttribute("father_id", user.getId());
        }
        
        // Aggiorna numero parenti
        this.setNumRelatives();
        
        
        
    }
    /**
     * Rimuovi il padre o la padre
     * @param gender    Sesso del genitore
     * @throws java.sql.SQLException
     */
    public void removeParent(String gender) throws SQLException{
        User parent = this.getParent(gender);
        String attribute;
        
        if(parent != null){
            
            if(gender.equals("female")){
                attribute = "mother_id";
            }else{
                attribute = "father_id";
            }
            
            Database.resetAttribute("user", attribute, "id = '" + this.id + "'");
            
            // Se è stato rimosso il legame di parentela con successo e se due utenti non appartengono più allo stesso albero genealogico
            if(!this.isRelative(parent)) {
                // Aggiorna numero di parenti
                this.setNumRelatives();
                parent.setNumRelatives();
            }
        }
        
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione madre">
 
    /**
     * Recupera la madre
     * @return
     * @throws java.sql.SQLException
     */
    public User getMother() throws SQLException{
        return User.getUserById(this.getMotherId());
    }
    /**
     * Inserisci la madre
     * @param mother
     * @throws java.sql.SQLException
     * @throws it.collaborative_genealogy.exception.NotAllowed
     */
    public void setMother(User mother) throws SQLException, NotAllowed{
        this.setParent(mother);
    }
    /**
     * Rimuovi la madre
     * @throws java.sql.SQLException
     */
    public void removeMother() throws SQLException{
        removeParent("female");
    }
    //</editor-fold>
  
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione padre">
    
    /**
     * Recupera il padre
     * @return
     * @throws java.sql.SQLException
     */
    public User getFather() throws SQLException{
        return User.getUserById(this.getFatherId());
    }
    /**
     * Inserisci il padre
     * @param father
     * @throws java.sql.SQLException
     * @throws it.collaborative_genealogy.exception.NotAllowed
     */
    public void setFather(User father) throws SQLException, NotAllowed{
        this.setParent(father);
        
    }
    /**
     * Rimuovi il padre
     * @throws java.sql.SQLException
     */
    public void removeFather() throws SQLException{
        removeParent("male");
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione coniuge">
    
    /**
     * Recupera il coniuge
     * @return
     * @throws java.sql.SQLException
     */
    public User getSpouse() throws SQLException {
        return User.getUserById(this.getSpouseId());
    } 
    /**
     * Inserisci il coniuge
     * @param spouse
     * @throws it.collaborative_genealogy.exception.NotAllowed
     * @throws java.sql.SQLException
     */
    public void setSpouse(User spouse) throws NotAllowed, SQLException{
        User spouse_before = null;
        if(this.getSpouseId() != null && !this.getSpouse().equals(spouse)) {
            spouse_before = this.getSpouse();
        }
        
        this.canAddLikeSpouse(spouse);
       
        this.updateAttribute("spouse_id", spouse.getId());

        // Cambia anche il coniuge dell'utente appena aggiunto se non è già stato fatto
        if(spouse.getSpouse() == null) {
            spouse.setSpouse(User.getUserById(this.id));
        }           

        // Eliminare il coniuge dell'utente appena eliminato come coniuge
        if(spouse_before != null){
            spouse_before.removeSpouse();
            // Aggiorna numero parenti del coniuge eliminato
            spouse_before.setNumRelatives();
        }

        // Aggiorna numeri parenti
        this.setNumRelatives();
            
        
        
        
    }
    /**
     * Rimuovi il coniuge
     * @throws java.sql.SQLException
     */
    public void removeSpouse() throws SQLException {
        User spouse = this.getSpouse();
        Database.resetAttribute("user", "spouse_id", "id = '" + this.id + "' OR id = '" + this.getSpouseId() + "'");
        if(!this.isRelative(spouse)){
            // Aggiorna numero di parenti
            this.setNumRelatives();
            spouse.setNumRelatives();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione figli">
    
    /**
     * Recupera i figli
     * @return  lista con tutti i figli di un utente
     * @throws java.sql.SQLException
     */
    public UserList getChildren() throws SQLException{
        UserList children = new UserList();

        ResultSet record = Database.selectRecord("user", "father_id = '" + this.id + "' OR mother_id = '" + this.id + "'");
        // Aggiungo ogni figlio trovato alla lista
        while(record.next()){    
            children.add(new User(record));
        }
        record.close();

        return children;    
    }
    
    public UserList getChildren(String gender){
        
        UserList children = new UserList();
        try {
            // Recupero tutti i record relativi ai figli dell'utente
            ResultSet record = Database.selectRecord("user", "(father_id = '" + this.id + "' OR mother_id = '" + this.id + "') AND gender = '" + gender + "'");
            // Aggiungo ogni figlio trovato alla lista

            while(record.next()){      
                children.add(new User(record));
            }
            record.close();
        } catch (SQLException ex) {
            return children;
        }

        return children;
        

    }
    /**
     * Inserisci un figlio
     * @param user  figlio da inserire
     * @throws it.collaborative_genealogy.exception.NotAllowed
     * @throws java.sql.SQLException
     */
    public void setChild(User user) throws NotAllowed, SQLException{
        // Se {user} ha già un genitore dello stesso sesso, ritorna false
        if(user.getParent(this.gender) != null) throw new NotAllowed();
        // Imposta l'utente corrente come genitore
        user.setParent(this);
        this.setNumRelatives();
    }
    /**
     * Elimina un figlio
     * @param user  utente da eliminare come figlio
     * @throws it.collaborative_genealogy.exception.NotAllowed 
     * @throws java.sql.SQLException 
     */
    public void removeChild(User user) throws NotAllowed, SQLException{

        // Se {user} non è un figlio
        UserList children = this.getChildren();        
        if(!children.contains(user)) throw new NotAllowed();
        
        user.removeParent(this.gender);
        // Se la rimozione è andata a buon fine e i due utenti non appartengono più allo stesso albero genealogico
        if(!this.isRelative(user)) {
            // Aggiorna numero di utenti presenti nei rispettivi alberi genealogici
            this.setNumRelatives();
            user.setNumRelatives();
            
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione antenati">
    
    /**
     * Recupera gli antenati
     * @return
     * @throws java.sql.SQLException
     */
    public UserList getAncestors() throws SQLException{
        UserList ancestors = new UserList();
        UserList parents = this.getParents();

        // Per ogni genitore
        for (User parent : parents) { 
            // Aggiungilo alla lista
            ancestors.add(parent);
            // Ricerca ricorsivamente i genitori alla lista
            ancestors.addAll(parent.getAncestors());
        }

        return ancestors;
    }
    /**
     * Recupera antenati con filtro sul sesso
     * @param gender    Sesso degli antenati
     * @return
     * @throws java.sql.SQLException
     */
    public UserList getAncestors(String gender) throws SQLException{
        UserList ancestors = new UserList();
        UserList parents = this.getParents();
        // Per ogni genitore
        for (User parent : parents) { 
            if(parent.getGender().equals(gender)){
                // Aggiungilo alla lista
                ancestors.add(parent);
            }

            // Ricerca ricorsivamente i genitori alla lista
            ancestors.addAll(parent.getAncestors(gender));
        }
        return ancestors;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione discendenti">
    
    /**
     * Recupera i discendenti
     * @return
     * @throws java.sql.SQLException
     */
    public UserList getOffsprings() throws SQLException {
        UserList offsprings = new UserList();
        UserList children = this.getChildren();
        // Per ogni figlio
        for (User child : children) {  
            // Aggiungilo alla lista
            offsprings.add(child);
            // Aggiungi ricrosivamente i figli alla lista
            offsprings.addAll(child.getOffsprings());
        }
        return offsprings;
    }
    /**
     * Recupero dei discendenti con filtro sul sesso
     * @param gender    Sesso dei discendenti
     * @return
     * @throws java.sql.SQLException
     */
    public UserList getOffsprings(String gender) throws SQLException {
        UserList offsprings = new UserList();
        UserList children = this.getChildren();
        // Per ogni figlio
        for (User child : children) {  
            if(child.getGender().equals(gender)){
                // Aggiungilo alla lista
                offsprings.add(child);
            }
            // Aggiungi ricrosivamente i figli alla lista
            offsprings.addAll(child.getOffsprings(gender));
        }
        return offsprings;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione fratelli">
    
    /**
     * Recupera fratelli e sorelle di sangue
     * @return
     * @throws java.sql.SQLException
     */
    public UserList getSiblings() throws SQLException {
        
        UserList siblings = new UserList();
        User father = this.getFather();
        User mother = this.getMother();
        
        // Se l'utente ha entrambi i genitori
        if(father != null && mother != null) {
            // Recupera figli del padre
            UserList father_children = father.getChildren();
            // Recupera figli della madre
            UserList mother_children = mother.getChildren();
            // Per ogni figlio del padre
            for (User father_child : father_children) {
                // Se è anche figlio della madre ed è diverso dall'utente corrente
                if(mother_children.contains(father_child) && !father_child.equals(this)){
                    // Aggiungilo tra i fratelli di sangue
                    siblings.add(father_child);
                }
            }
        } 
        
        // Ritorna figli recuperati
        return siblings;
    }
    
    /**
     * Recupera fratelli/sorelle di sangue con filtro sul sesso
     * @param gender    Sesso dei fratelli/sorelle
     * @return
     * @throws java.sql.SQLException
     */
    public UserList getSiblings(String gender) throws SQLException {
        
        UserList siblings = new UserList();
        User father = this.getFather();
        User mother = this.getMother();
        
        // Se l'utente ha entrambi i genitori
        if(father != null && mother != null) {
            // Recupera figli del padre
            UserList father_children = father.getChildren();
            // Recupera figli della madre
            UserList mother_children = mother.getChildren();
            // Per ogni figlio del padre
            for (User father_child : father_children) {
                // Se è anche figlio della madre ed è diverso dall'utente corrente
                if(mother_children.contains(father_child) && !father_child.equals(this) && father_child.getGender().equals(gender)){
                    // Aggiungilo tra i fratelli di sangue
                    siblings.add(father_child);
                }
            }
        } 
        
        // Ritorna figli recuperati
        return siblings;
    }
    
    public void setSiblingId(String sibling_id) throws SQLException, NotAllowed {
        this.setSibling(User.getUserById(sibling_id));
    }
    /**
     * Aggiungi un fratello o una sorella
     * @param sibling  utente da aggiungere
     * @throws java.sql.SQLException
     * @throws it.collaborative_genealogy.exception.NotAllowed
     */
    public void setSibling(User sibling) throws SQLException, NotAllowed {
        // Se {relative} non può essere aggiunto come fratello, restituisci false
        this.canAddLikeSibling(sibling);

        User u1 = this;
        User u2 = sibling;
        UserList u1_parents;
        UserList u2_parents;
        User u1_parent = null;
        User u2_parent = null;
        
        do{
           
            u1_parents = u1.getParents();
            u2_parents = u2.getParents();

            int u1_size = u1_parents.size();
            int u2_size = u2_parents.size();

            if(u1_size == 0){
                for(User parent: u2_parents){
                    u1.setParent(parent);
                }
            }

            if(u1_size == 1 && u2_size == 2){
                // Recupera l'altro genitore
                User other_parent;
                Iterator iter1 = u1_parents.iterator();
                u1_parent = (User)iter1.next();
                
                if(u1.getMother() != null){
                    other_parent = u2.getFather();
                }else{
                    other_parent = u2.getMother();
                }
                
                u1.setParent(other_parent);
            }
            
            /* 
                Le due condizioni vanno considerate anche con gli utenti a parti invertite, 
                perciò si fa lo swap dei due utenti
                Alla termine di questa operazione, i due utenti avranno gli stessi genitori
            */

            if(u1.equals(sibling)) break;

            // Swappa utenti
            u1 = sibling;
            u2 = this;
            
        }while(true);
        
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Gestione amministrazione di altri utenti">
 
    /**
     * Aggiungi un genitore di un parente dell'utente
     * @param user      parente dell'utente
     * @param parent    genitore da aggiungere
     * @throws it.collaborative_genealogy.exception.NotAllowed
     * @throws java.sql.SQLException
     */
    public void addParentFor(User user, User parent) throws NotAllowed, SQLException {
        if(!this.isRelative(user))throw new NotAllowed();
        user.setParent(parent);
    }
    /**
     * Aggiungi un figlio di un parente dell'utente
     * @param user      parente da aggiungere
     * @param child     figlio da aggiungere
     * @throws it.collaborative_genealogy.exception.NotAllowed
     * @throws java.sql.SQLException
     */
    public void addChildFor(User user, User child) throws NotAllowed, SQLException {
        if(!this.isRelative(user))throw new NotAllowed();
        user.setChild(child);

    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione richieste di parentela">
    
    public ResultSet getRequest() throws SQLException{   
        ResultSet request = Database.selectRecord("request", "relative_id = '" + this.id + "'");        
        return request;
    }
    
    public void setRequest(User relative, String relationship) throws NotAllowed, SQLException {
        
        this.canAddLike(relative, relationship);
        
        this.send_handler(relative, relationship);
    }
    
    /**
     * Invia richiesta di parentela per conto di un altro utente
     * @param user      utente a cui si vuole aggiungere un user
     * @param relative  parente da aggiungere
     * @param relationship    grado di parentela
     * @throws it.collaborative_genealogy.exception.NotAllowed
     * @throws java.sql.SQLException
     */
    public static void setRequestFor(User user, User relative, String relationship) throws NotAllowed, SQLException {
        // Se {user} non può aggiungere {relative} come parente
        user.canAddLike(relative, relationship);
        // Ritorna
        user.send_handler(relative, relationship);
    }
    
    private void send_handler(User relative, String relationship) throws SQLException {
        
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", this.id);
        data.put("relative_id", relative.getId());
        data.put("relationship", relationship);
        
        Database.insertRecord("request", data);

        /*
            INVIARE EMAIL DI RICHIESTA  
        */

        
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Gestione rifiuto/accettazione di richieste di parentela">
    
    /**
     * Accetta richiesta di parentela
     * @param relative  parente a cui si è fatta la richiesta
     * @throws java.sql.SQLException
     * @throws it.collaborative_genealogy.exception.NotAllowed
     */
    public void acceptRequest(User relative) throws SQLException, NotAllowed {
        ResultSet request = Database.selectRecord("request", "user_id = '" + relative.getId() + "' AND relative_id = '" + this.id + "'");
        String relationship = "";

        while(request.next()){
            relationship = request.getString("relationship");
        }

        
        // Rimuovi la richiesta dal database
        relative.deleteRequest(this);
        
        // Effettua il collegamento tra i due parenti
        switch(relationship){
        
            case "parent": this.setChild(relative);
                
            case "child": this.setParent(relative);
            
            case "sibling": this.setSibling(relative);
                
            case "spouse": this.setSpouse(relative);
                
            default: throw new NotAllowed();
        }
        
    }
    
    /**
     * Declina richiesta di parentela
     * @param relative  parente a cui si è fatta la richiesta
     * @throws java.sql.SQLException
     */
    public void declineRequest(User relative) throws SQLException{
        relative.deleteRequest(this);
    }
    
    /**
     * Annulla richiesta di parentela
     * @param relative  parente a cui si è fatta la richiesta
     * @throws java.sql.SQLException
     */
    public void dropRequest(User relative) throws SQLException{
        this.deleteRequest(relative);
    }
    
    /**
     * Elimina richiesta dal database
     * @param relative  parente a cui si è fatta la richiesta
     * @throws java.sql.SQLException
     */
    private void deleteRequest(User relative) throws SQLException{
        Database.deleteRecord("request", "user_id = '" + this.id + "' AND relative_id = '" + relative.getId() + "'");
    }
    
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Controlli per le aggiunte di parentela">
    
    /**
     * Verifica se un dato utente può essere aggiunto come genitore
     * @param user      utente da aggiungere
     * @param gender    sesso del genitore per definire se si vuole aggiungere un padre o una madre
     * @return          true se l'utente è stato aggiunto come coniuge, false altrimenti
     */
    private void canAddLikeParent(User user) throws SQLException, NotAllowed {
        String user_gender = user.getGender();
        // Se {user} è tra i fratelli/sorelle
        UserList siblings = this.getSiblings(user_gender);        
        if(siblings.contains(user)) throw new NotAllowed();
        
        // Se {user} è un discendente
        UserList offsprings = this.getOffsprings(user_gender);        
        if(offsprings.contains(user)) throw new NotAllowed();
        
        // Per ogni discendente
        for(User offspring: offsprings){
            // Se {user} è tra i suoi antenati
            UserList ancestors = offspring.getAncestors(user_gender);
            if(ancestors.contains(user)) throw new NotAllowed();
            
            /** Di conseguenza non si può inserire un antenato dell'utente stesso */
        }
    }
    /**
     * Verifica se un dato utente può essere aggiunto come coniuge
     * @return  true se l'utente è stato aggiunto come coniuge, false altrimenti
     */
    private void canAddLikeSpouse(User user) throws SQLException, NotAllowed {
        
        /* 
            Più in generale, si verifica se due utenti possono avere (o aver avuto) una relazione sentimentale di qualsiasi tipo (e di conseguenza se possono avere figli comuni)
            Per ipotesi non sono accettate relazioni sentimentali tra:
                1. fratelli;
                2. utente con i suoi antenati, e di conseguenza anche con i suoi discendenti; 
                3. utenti dello stesso sesso (??)
        */
        
        
        // Se {user} ha lo stesso sesso
        if(this.gender.equals(user.getGender())) throw new NotAllowed();
        
        // Se {user} è già il coniuge
        if(this.getSpouse() != null && this.getSpouse().equals(user)) throw new NotAllowed();
        
        // Se {user} è tra i fratelli/sorelle
        UserList siblings = this.getSiblings();        
        if(siblings.contains(user)) throw new NotAllowed();
        
        // Se {user} è un antenato
        UserList anchestors = this.getAncestors();        
        if(anchestors.contains(user)) throw new NotAllowed();
        
        // Se {user} è un discendente
        UserList offsprings = this.getOffsprings();        
        
        if(!offsprings.contains(user)) throw new NotAllowed();
    }
    /**
     * Verifica se un dato utente può essere aggiunto come fratello
     * @return  true se l'utente è stato aggiunto con successo, false altrimenti
     */
    private void canAddLikeSibling(User user) throws SQLException, NotAllowed {
        
        // Se i due utenti sono già fratelli
        if(this.getSiblings().contains(user)) throw new NotAllowed();
        
        Iterator iter;
        
        User u1 = this;
        User u2 = user;

        UserList u1_parents = u1.getParents();
        UserList u2_parents = u2.getParents();
        
        int u1_size = u1_parents.size();
        int u2_size = u2_parents.size();
        
        User u1_parent, u2_parent;
        
        

        // Se entrambi gli utenti non hanno nessun genitore, non è possibile verificare la parentela {*}
        if(u2_size == 0 && u1_size == 0) throw new NotAllowed();

        // Se i due utenti hanno già entrambi i genitori, non è possibile che i due utenti siano fratelli {**}
        if(u2_size == 2 && u1_size == 2) throw new NotAllowed();
        
        if(u2_size == 1 && u1_size == 1){
            iter = u1_parents.iterator();
            u1_parent = (User)iter.next();
            iter = u2_parents.iterator();
            u2_parent = (User)iter.next();
            // Se il genitore di {u1} e il genitore di {u2} sono dello stesso sesso e non identificano lo stesso utente, non è possibile che i due utenti siano fratelli
            if(!u1_parent.getGender().equals(u2_parent.getGender()) && !u1_parent.equals(u2_parent)) throw new NotAllowed();

            // Se il genitore di {u2} non può essere coniuge di {u1}
            u2.canAddLikeSpouse(u1);
            
        }
        
        do{

            // Se {u2} non ha genitori
            if(u2_size == 0){

                for(User parent: u1_parents){
                    // Se {u2} non può avere entrambi i genitori di {u1}, non è possibile che i due utenti siano fratelli
                    u2.canAddLikeParent(parent);
                }

            }

            if(u2_size == 1 && u1_size == 2){
                iter = u1_parents.iterator();
                u1_parent = (User)iter.next();
                
                // Se il genitore di {u1} non è anche genitore di {u2}
                if(!u2_parents.contains(u1_parent)) throw new NotAllowed();

                
                User other_parent;
                // Se {u2} ha solo la madre
                if(u2.getMother() != null){
                    // Recupera il padre di {u1}
                    other_parent = u1.getFather();
                }else{
                    // Altrimenti recupara la madre di {u1}
                    other_parent = u1.getMother();
                }
                // Se l'altro genitore di {u2} può essere aggiunto come genitore di {u1}
                u1.canAddLikeParent(other_parent);
            }

            /* 
                Le ultime due condizioni vanno considerate anche con gli utenti a parti invertite, 
                perciò si fa lo swap dei due utenti
            */
            
            // Se gli utenti sono già stati swappati, esci dal ciclo
            if(u1.equals(user)) break;
            
            // Swappa utenti
            u1 = user;
            u2 = this;

            u1_parents = u1.getParents();
            u2_parents = u2.getParents();

            u1_size = u1_parents.size();
            u2_size = u2_parents.size();
            
        }while(true);
        
    }
    /**
     * Verifica se un dato utente può essere aggiunto come figlio
     * @return  true se l'utente è stato aggiunto con successo, false altrimenti
     */
    private void canAddLikeChild(User user) throws SQLException, NotAllowed { 
        // Se {user} non puo diventare genitore dell'utente corrente, restituisci false
        user.canAddLikeParent(this);
    }
    /**
     * Verifica se un dato utente può essere aggiunto come parente
     * @param user      user da aggiungere
     * @param relationship    grado di parentela
     * @return  true se l'utente è stato aggiunto come coniuge, false altrimenti
     */
    private void canAddLike(User user, String relationship) throws SQLException, NotAllowed {
        
        switch(relationship){
            
            case "parent": this.canAddLikeParent(user);
                
            case "child": this.canAddLikeChild(user);
                
            case "sibling": this.canAddLikeSibling(user);
                
            case "spouse": this.canAddLikeSpouse(user);
                
            default: throw new NotAllowed();
        }

    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Gestione albero genealogico">

    /**
     * Recupera i componenti dell'alabero genealogico dell'utente senza etichette
     * @return  lista di utenti che fanno parte dell'albero genealogico
     * @throws java.sql.SQLException
     */
    public UserList getUnlabeledTree() throws SQLException {
        
        // Inizializza l'albero con i solo discendenti degli antenati
        UserList family_tree_final = new UserList();
        UserList evaluated = new UserList();
        // Aggiungi l'utente corrente al proprio albero genealogico
        family_tree_final.add(this);
        int number_relatives;

        do{
            
            // Inizializza albero temporaneo
            UserList family_tree_temp = new UserList();
            // Calcola numero di parenti inseriti 
            number_relatives = family_tree_final.size();
            // Per ogni parente già inserito nell'albero
            for(User relative: family_tree_final){
                
                // Se è già stato valutato, salta iterazione
                if(evaluated.contains(relative)) continue;
                
                // Aggiungi all'abero temporaneo gli antenati del parente
                UserList ancestors = relative.getAncestors();
                
                
                
                
                // Se l'utente non ha antenati, inserisci l'utente stesso tra gli antenati, cosi da poter cercare i suoi discendenti
                if(ancestors.isEmpty()) ancestors.add(relative);
                
                family_tree_temp.addAll(ancestors);
                
                // Per ogni antenato trovato
                for(User ancestor: ancestors){
                    // Se l'antenato è già stato valutato, passa al prossimo antenato
                    if(evaluated.contains(ancestor)) continue;
                    // Aggiungi all'albero temporaneo tutti i suoi discendenti (quindi anche i discendenti del parente stesso)
                    family_tree_temp.addAll(ancestor.getOffsprings());
                }
                
                // Aggiungi all'albero temporaneo il coniuge del parente
                family_tree_temp.add(relative.getSpouse());
                // Aggiungi il parente nella lista dei parenti già valutati
                evaluated.add(relative);
            }
            
            // Aggiungi l'albero temporaneo a quello finale
            family_tree_final.addAll(family_tree_temp);
            
        // Cicla fino a quando non sono stati aggiungi nuovi utenti nell'albero finale
        }while(number_relatives != family_tree_final.size());

        // Ritorna albero finale
        return family_tree_final;
    }
    /**
     * Verifica se l'utente è "parente" ad un altro: con parente si intende qualsiasi utente raggiungibile attraverso legami di parentela
     * @param user  parente da cercare
     * @return      true se l'user è tra i parenti, false altrimenti
     * @throws java.sql.SQLException
     */
    public boolean isRelative(User user) throws SQLException {
        // Inizializza l'albero con i solo discendenti degli antenati
        UserList family_tree_final = new UserList();
        UserList evaluated = new UserList();
        // Aggiungi l'utente corrente al proprio albero genealogico
        family_tree_final.add(this);
        
        int number_relatives;

        do{
            
            // Inizializza albero temporaneo
            UserList family_tree_temp = new UserList();
            // Calcola numero di parenti inseriti
            number_relatives = family_tree_final.size();
            // Per ogni parente già inserito nell'albero
            for(User relative: family_tree_final){
                if(evaluated.contains(relative)) continue;
                // Aggiungi all'abero temporaneo gli antenati del parente
                UserList ancestors = relative.getAncestors();
                if(ancestors.contains(user)) return true;
                
                // Se l'utente non ha antenati, inserisci l'utente stesso tra gli antenati, cosi da poter cercare i suoi discendenti
                if(ancestors.isEmpty()) ancestors.add(relative);
                // Aggiungi gli antenati all'albero genealogico
                family_tree_temp.addAll(ancestors);
                
                // Per ogni antenato trovato
                for(User ancestor: ancestors){
                    if(evaluated.contains(ancestor)) continue;
                    // Aggiungi all'albero temporaneo tutti i suoi discendenti (quindi anche i discendenti dell'parente stesso)
                    family_tree_temp.addAll(ancestor.getOffsprings());
                    if(family_tree_temp.contains(user)) return true;
                }
                
                // Aggiungi all'albero temporaneo il coniuge del parente
                family_tree_temp.add(relative.getSpouse());
                if(family_tree_temp.contains(user)) return true;
                evaluated.add(relative);
            }
            
            // Aggiungi l'albero temporaneo a quello finale
            family_tree_final.addAll(family_tree_temp);
            
            // Elimina duplicati nell'albero
//            family_tree_final.removeDuplicate();
            
        // Cicla fino a quando non sono stati aggiungi nuovi utente nell'albero finale
        }while(number_relatives != family_tree_final.size());

        return false;
    }
    /**
     * Recupera i componenti dell'alabero genealogico dell'utente con etichette
     * @return  lista di nodi dell'albero, a cui ad ogni utente è associata un'etichetta 
     *              che corrisponde al tipo di parentela rispetto all'utente corrente
     * @throws java.sql.SQLException
     */
    public GenealogicalTree getFamilyTree() throws SQLException{
        GenealogicalTree tree = new GenealogicalTree(this);
        tree.getFamilyTree();
        return tree;
    }
    
    //</editor-fold>
    
    /**
     * Recupera un utente attraverso il suo ID
     * @param user_id   id utente
     * @return          
     */
    public static User getUserById(String user_id){
        User user = null;
        try {
            if(user_id != null){
                ResultSet record = Database.selectRecord("user", "id = '" + user_id + "'");
                if(record.next()){
                    user =  new User(record);
                }
            }
        } catch (SQLException ex) {}
        
        return user;
    }
    
     /**
     * Recupera un utente attraverso il suo ID
     * @param user_email   email utente
     * @return          
     */
    public static User getUserByEmail(String user_email){
        
        try {
            
            User user = null;
            if(user_email != null){
            
                try (ResultSet record = Database.selectRecord("user", "email = '" + user_email + "'")) {
                    if(record.next()){
                        user =  new User(record);
                    }
                }
            
            }
            
            return user;
            
        } catch (SQLException ex) {
            return null;
        }
    }
    
    
    /**
     * Recupera un utente attraverso il suo ID
     * @param password      password utente
     * @return          
     */
    public boolean checkPassword(String password){
        
        /*
        
            DA MIGLIORARE CON PASSWORD CRIPTATE
        
        */
        try {
 
            try (ResultSet record = Database.selectRecord("user", "email = '" + this.getEmail() + "'")) {
                if(record.next()){
                    return DataUtil.decrypt(record.getString("password"), password); 
                }
                return false;
            }

            
            
        } catch (SQLException ex) {
            return false;
        }
        
    }
    
    /**
     * Elimina un utente
     * @throws java.sql.SQLException
     */
    public void delete() throws SQLException{
        // Rimuovi relazione genitore/figli
        for(User child: this.getChildren()){
            child.removeParent(this.gender);
        }
        // Rimuovi relazione marito/moglie
        this.removeSpouse();
        // Rimuovi utente
        Database.deleteRecord("user", "id = '" + this.id + "'");
    }
    
    public void prepareToLog(HttpServletRequest request){
        // Altrimenti, fai il login dell'utente
        HttpSession session = request.getSession();
        session.setAttribute("user_logged", this);
        session.setAttribute("breadcrumb", new NodeList());

        try {
            session.setAttribute("family_tree", this.getFamilyTree());
        } catch (SQLException ex) {
            session.setAttribute("family_tree", null);
        }
    }
    //<editor-fold defaultstate="collapsed" desc="Metodi ausiliari">
        
    /**
     * Aggioena un attributo di un utente
     * @param attribute attributo da aggiornare
     * @param value     valore da assegnare all'attributo
     * @return
     */
    private void updateAttribute(String attribute, Object value) throws SQLException{
        Map<String, Object> data = new HashMap();
        data.put(attribute, value);
        Database.updateRecord("user", data, "id = '" + this.id + "'");
    }
    
    /**
     * Crea un user id univoco
     * @param length lunghezza id
     * @return
     */
    public static String createUniqueUserId(int length){
        String user_id;
        do{
            user_id = DataUtil.generateCode(length);
        // Cicla fino a quando non esiste un utente con id uguale a quello appena generato
        }while(User.getUserById(user_id) != null);
        
        return user_id;
        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        final User other = (User) obj;
        
        return Objects.equals(this.id, other.id);
    }
    
//</editor-fold>
    
}
