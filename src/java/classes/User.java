package classes;

import classes.tree.GenealogicalTree;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    
    private HttpSession session;
    
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
        /* E' possibile che questo valore venga modificato da altri utenti, per cui è necessario prelevarlo ogni volta dal database*/
        ResultSet record = Database.selectRecord("user", "id = '" + this.id +"'");
        try {
            if(record.next()){
                return record.getString("mother_id");
            }
        } catch (SQLException ex) {
            return null;
        }
        
        return null;
    }
    
    public String getFatherId() {
        /* E' possibile che questo valore venga modificato da altri utenti, per cui è necessario prelevarlo ogni volta dal database*/
        ResultSet record = Database.selectRecord("user", "id = '" + this.id + "'");
        try {
            if(record.next()){
                return record.getString("father_id");
            }
        } catch (SQLException ex) {
            return null;
        }
        
        return null;
    }
    
    public String getSpouseId() {
        /* E' possibile che questo valore venga modificato da altri utenti, per cui è necessario prelevarlo ogni volta dal database*/
        ResultSet record = Database.selectRecord("user", "id = '" + this.id + "'");
        try {
            if(record.next()){
                return record.getString("spouse_id");
            }
            
        } catch (SQLException ex) {
            return null;
        }
        
        return null;
    }
    
    public int getNumRelative() {
        /* Il numero di parenti può essere modificato anche da altri utenti, per cui è necessario prelevare il valore ogni volta dal database*/
        ResultSet record = Database.selectRecord("user", "id = '" + this.id + "'");
        
        try {
            if(record.next()){
                return record.getInt("num_relatives");
            }
        } catch (SQLException ex) {
            return 0;
        }

        return 0;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Metodi SET delle variabili di istanza">
    
    public boolean setId(String id) {
        // Se il nuovo id appartiene già ad un altro utente, restituisci false
        if(User.getUserById(id) != null) return false;
        boolean result = this.updateAttribute("id", id);
        if(result) this.id = id;
        return result;
    }
    
    public boolean setName(String name){
        boolean result = this.updateAttribute("name", name);
        if(result) this.name = name;
        return result;
    }
    
    public boolean setSurname(String surname){
        boolean result = this.updateAttribute("surname", surname);
        if(result) this.surname = surname;
        return result;
    }
    
    public boolean setEmail(String email){
        boolean result = this.updateAttribute("email", email);
        if(result) this.email = email;
        return result;
    }
    
    public boolean setBirthdate(Date birthdate){
        boolean result = this.updateAttribute("birthdate", birthdate);
        if(result) this.birthdate = birthdate;
        return result;
    }
    
    public boolean setBirthplace(String birthplace){
        boolean result = this.updateAttribute("birthplace", birthplace);
        if(result) this.birthplace = birthplace;
        return result;
    }
    
    public boolean setGender(String gender){
        boolean result = this.updateAttribute("gender", gender);
        if(result) this.gender = gender;
        return result;
    }
    
    public boolean setBiography(String biography) {
        boolean result = this.updateAttribute("biography", biography);
        if(result) this.biography = biography;
        return result;
    }
    
    public boolean setPassword(String password) {
        boolean result = this.updateAttribute("password", password);
        return result;
    }
    
    public boolean setMotherId(String mother_id) {
        User mother = User.getUserById(mother_id);
        return this.setMother(mother);
    }
    
    public boolean setFatherId(String father_id) {
        User father = User.getUserById(father_id);
        return this.setFather(father);
    }
    
    public boolean setSpouseId(String spouse_id) {
        User spouse = User.getUserById(spouse_id);
        return this.setSpouse(spouse);
    }   
    
    /**
     * Aggiorna il numero di parenti collegati
     * @return
     */
    public boolean setNumRelatives() {
        
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

        return Database.updateRecord("user", data, condition);
    }
    
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione madre">
    
    /**
     * Recupera la madre
     * @return
     */
    public User getMother(){
        return this.getParent("female");
    }
    /**
     * Inserisci la madre
     * @param mother
     * @return
     */
    public boolean setMother(User mother){
        return this.setParent(mother);
    }
    /**
     * Rimuovi la madre
     * @return
     */
    public boolean removeMother(){
        return removeParent("female");
    }
    //</editor-fold>
  
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione padre">
    
    /**
     * Recupera il padre
     * @return
     */
    public User getFather(){
        return this.getParent("male");
    }
    /**
     * Inserisci il padre
     * @param father
     * @return
     */
    public boolean setFather(User father){
        return this.setParent(father);
        
    }
    /**
     * Rimuovi il padre
     * @return
     */
    public boolean removeFather(){
        return removeParent("male");
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione coniuge">
    
    /**
     * Recupera il coniuge
     * @return
     */
    public User getSpouse() {
        return User.getUserById(this.getSpouseId());
    } 
    /**
     * Inserisci il coniuge
     * @param spouse
     * @return
     */
    public boolean setSpouse(User spouse){
        User spouse_before = null;
        if(this.getSpouseId() != null && !this.getSpouse().equals(spouse)) {
            spouse_before = this.getSpouse();
        }
        
        if(!this.canAddLikeSpouse(spouse)) return false;
        boolean result = this.updateAttribute("spouse_id", spouse.getId());
        if(result){
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
        
        
        return result;
    }
    /**
     * Rimuovi il coniuge
     * @return
     */
    public boolean removeSpouse() {
        User spouse = this.getSpouse();
        boolean result = Database.resetAttribute("user", "spouse_id", "id = '" + this.id + "' OR id = '" + this.getSpouseId() + "'");
        if(result && !this.isRelative(spouse)){
            // Aggiorna numero di parenti
            this.setNumRelatives();
            spouse.setNumRelatives();
        }
        return result;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione genitori">
    
    /**
     * Recupera il padre e la madre
     * @return
     */
    public UserList getParents(){
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
     */
    public User getParent(String gender){
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
     * @return
     */
    public boolean setParent(User user){
        boolean result;
        if(!this.canAddLikeParent(user)) return false;
        
        if(user.getGender().equals("female")){
            result = this.updateAttribute("mother_id", user.getId());
        }else{
            result = this.updateAttribute("father_id", user.getId());
        }
        
        // Aggiorna numero parenti
        if(result) {
            this.setNumRelatives();
        }
        
        return result;
        
    }
    /**
     * Rimuovi il padre o la padre
     * @param gender    Sesso del genitore 
     * @return
     */
    public boolean removeParent(String gender){
        User parent = this.getParent(gender);
        boolean result = false;
        String attribute;
        
        if(parent != null){
            
            if(gender.equals("female")){
                attribute = "mother_id";
            }else{
                attribute = "father_id";
            }
            
            result = Database.resetAttribute("user", attribute, "id = '" + this.id + "'");
            
            // Se è stato rimosso il legame di parentela con successo e se due utenti non appartengono più allo stesso albero genealogico
            if(result && !this.isRelative(parent)) {
                // Aggiorna numero di parenti
                this.setNumRelatives();
                parent.setNumRelatives();
            }
        }
        
        return result;
        
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione figli">
    
    /**
     * Recupera i figli
     * @return  lista con tutti i figli di un utente
     */
    public UserList getChildren(){
        UserList children = new UserList();
        ResultSet record;
        record = Database.selectRecord("user", "father_id = '" + this.id + "' OR mother_id = '" + this.id + "'");
        // Aggiungo ogni figlio trovato alla lista
                
        try {
            while(record.next()){    
                children.add(new User(record));
            }
            record.close();
        } catch (SQLException ex) {
            return children;    
        }
    
        return children;    
    }
    
    public UserList getChildren(String gender){
        
        UserList children = new UserList();

        // Recupero tutti i record relativi ai figli dell'utente
        ResultSet record = Database.selectRecord("user", "(father_id = '" + this.id + "' OR mother_id = '" + this.id + "') AND gender = '" + gender + "'");
        // Aggiungo ogni figlio trovato alla lista
              
        try {
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
     * @return      true se il filgio è stato inserito con successo, false altrimenti
     */
    public boolean setChild(User user){
        // Se {user} ha già un genitore dello stesso sesso, ritorna false
        if(user.getParent(this.gender) != null) return false;
        // Imposta l'utente corrente come genitore
        boolean result = user.setParent(this);
        
        if(result) this.setNumRelatives();
        return result;
    }
    /**
     * Elimina un figlio
     * @param user  utente da eliminare come figlio
     * @return      true se l'eliminazione è avvenuta con successo, false altrimenti 
     */
    public boolean removeChild(User user){
        
        boolean result;
        
        // Se {user} non è un figlio
        UserList children = this.getChildren();        
        if(!children.contains(user)) return false;
        
        result = user.removeParent(this.gender);
        // Se la rimozione è andata a buon fine e i due utenti non appartengono più allo stesso albero genealogico
        if(result && !this.isRelative(user)) {
            // Aggiorna numero di utenti presenti nei rispettivi alberi genealogici
            this.setNumRelatives();
            user.setNumRelatives();
            
        }
        return result;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione antenati">
    
    /**
     * Recupera gli antenati
     * @return
     */
    public UserList getAncestors(){
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
     */
    public UserList getAncestors(String gender){
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
     */
    public UserList getOffsprings() {
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
     */
    public UserList getOffsprings(String gender) {
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
     */
    public UserList getSiblings() {
        
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
     */
    public UserList getSiblings(String gender) {
        
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
    
    public boolean setSiblingId(String sibling_id) {
        return this.setSibling(User.getUserById(sibling_id));
    }
    /**
     * Aggiungi un fratello o una sorella
     * @param sibling  utente da aggiungere
     * @return  true se l'utente è stato aggiunto con successo, false altrimenti
     */
    public boolean setSibling(User sibling) {
        // Se {relative} non può essere aggiunto come fratello, restituisci false
        if(!this.canAddLikeSibling(sibling)) return false;

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
        
        return true;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Gestione amministrazione di altri utenti">
 
    /**
     * Aggiungi un genitore di un parente dell'utente
     * @param user      parente dell'utente
     * @param parent    genitore da aggiungere
     * @return
     */
    public boolean addParentFor(User user, User parent) {
        if(this.isRelative(user)){
            return user.setParent(parent);
        }
        return false;
    }
    /**
     * Aggiungi un figlio di un parente dell'utente
     * @param user      parente da aggiungere
     * @param child     figlio da aggiungere
     * @return
     */
    public boolean addChildFor(User user, User child) {
        if(this.isRelative(user)){
            return user.setChild(child);
        }
        return false;
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione richieste di parentela">
    
    public ResultSet getRequest(){   
        ResultSet request = Database.selectRecord("request", "relative_id = '" + this.id + "'");        
        return request;
    }
    
    public boolean setRequest(User relative, String relationship) {
        
        if(!this.canAddLike(relative, relationship)) return false;
        
        return this.send_handler(relative, relationship);
    }
    
    /**
     * Invia richiesta di parentela per conto di un altro utente
     * @param user      utente a cui si vuole aggiungere un user
     * @param relative  parente da aggiungere
     * @param relationship    grado di parentela
     * @return          true se l'aggiunta va a buon fine, false altriementi
     */
    public static boolean setRequestFor(User user, User relative, String relationship) {
        // Se {user} non può aggiungere {relative} come parente
        if(!user.canAddLike(relative, relationship)) return false;
        // Ritorna
        return user.send_handler(relative, relationship);
    }
    
    private boolean send_handler(User relative, String relationship) {
        
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", this.id);
        data.put("relative_id", relative.getId());
        data.put("relationship", relationship);
        
        boolean result = Database.insertRecord("request", data);
        
        if(result) {
        
            /*
                INVIARE EMAIL DI RICHIESTA  
            */
            
            return true;
        }
        
        return false;
        
        
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Gestione rifiuto/accettazione di richieste di parentela">
    
    /**
     * Accetta richiesta di parentela
     * @param relative  parente a cui si è fatta la richiesta
     * @return
     */
    public boolean acceptRequest(User relative) {
        ResultSet request = Database.selectRecord("request", "user_id = '" + relative.getId() + "' AND relative_id = '" + this.id + "'");
        String relationship = "";
        
        try {
            
            while(request.next()){
                relationship = request.getString("relationship");
            }
            
        } catch (SQLException ex) {
            return false;
        }
        
        // Se cìè stato un'errore durante la rimozione della richiesta dal database
        if(!relative.deleteRequest(this)) return false;
        
        switch(relationship){
        
            case "parent": return this.setChild(relative);
                
            case "child": return this.setParent(relative);
            
            case "sibling": return this.setSibling(relative);
                
            case "spouse": return this.setSpouse(relative);
                
            default: return false;
        }
        
    }
    
    /**
     * Declina richiesta di parentela
     * @param relative  parente a cui si è fatta la richiesta
     * @return
     */
    public boolean declineRequest(User relative){
        return relative.deleteRequest(this);
    }
    
    /**
     * Annulla richiesta di parentela
     * @param relative  parente a cui si è fatta la richiesta
     * @return
     */
    public boolean dropRequest(User relative){
        return this.deleteRequest(relative);
    }
    
    /**
     * Elimina richiesta dal database
     * @param relative  parente a cui si è fatta la richiesta
     * @return
     */
    private boolean deleteRequest(User relative){
        return Database.deleteRecord("request", "user_id = '" + this.id + "' AND relative_id = '" + relative.getId() + "'");
    }
    
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Controlli per le aggiunte di parentela">
    
    /**
     * Verifica se un dato utente può essere aggiunto come genitore
     * @param user      utente da aggiungere
     * @param gender    sesso del genitore per definire se si vuole aggiungere un padre o una madre
     * @return          true se l'utente è stato aggiunto come coniuge, false altrimenti
     */
    private boolean canAddLikeParent(User user) {
        String user_gender = user.getGender();
        // Se {user} è tra i fratelli/sorelle
        UserList siblings = this.getSiblings(user_gender);        
        if(siblings.contains(user)) return false;
        
        // Se {user} è un discendente
        UserList offsprings = this.getOffsprings(user_gender);        
        if(offsprings.contains(user)) return false;
        
        // Per ogni discendente
        for(User offspring: offsprings){
            // Se {user} è tra i suoi antenati
            UserList ancestors = offspring.getAncestors(user_gender);
            if(ancestors.contains(user)) return false;
            
            /** Di conseguenza non si può inserire un antenato dell'utente stesso */
        }
        
        return true;
    }
    /**
     * Verifica se un dato utente può essere aggiunto come coniuge
     * @return  true se l'utente è stato aggiunto come coniuge, false altrimenti
     */
    private boolean canAddLikeSpouse(User user) {
        
        /* 
            Più in generale, si verifica se due utenti possono avere (o aver avuto) una relazione sentimentale di qualsiasi tipo (e di conseguenza se possono avere figli comuni)
            Per ipotesi non sono accettate relazioni sentimentali tra:
                1. fratelli;
                2. utente con i suoi antenati, e di conseguenza anche con i suoi discendenti; 
                3. utenti dello stesso sesso (??)
        */
        
        
        // Se {user} ha lo stesso sesso
        if(this.gender.equals(user.getGender())) return false; 
        
        // Se {user} è già il coniuge
        if(this.getSpouse() != null && this.getSpouse().equals(user)) return false;
        
        // Se {user} è tra i fratelli/sorelle
        UserList siblings = this.getSiblings();        
        if(siblings.contains(user)) return false;
        
        // Se {user} è un antenato
        UserList anchestors = this.getAncestors();        
        if(anchestors.contains(user)) return false;
        
        // Se {user} è un discendente
        UserList offsprings = this.getOffsprings();        
        
        return !offsprings.contains(user);
    }
    /**
     * Verifica se un dato utente può essere aggiunto come fratello
     * @return  true se l'utente è stato aggiunto con successo, false altrimenti
     */
    private boolean canAddLikeSibling(User user) {
        
        // Se i due utenti sono già fratelli
        if(this.getSiblings().contains(user)) return false;
        
        Iterator iter;
        
        User u1 = this;
        User u2 = user;

        UserList u1_parents = u1.getParents();
        UserList u2_parents = u2.getParents();
        
        int u1_size = u1_parents.size();
        int u2_size = u2_parents.size();
        
        User u1_parent, u2_parent;
        
        

        // Se entrambi gli utenti non hanno nessun genitore, non è possibile verificare la parentela {*}
        if(u2_size == 0 && u1_size == 0) return false;

        // Se i due utenti hanno già entrambi i genitori, non è possibile che i due utenti siano fratelli {**}
        if(u2_size == 2 && u1_size == 2) return false;
        
        if(u2_size == 1 && u1_size == 1){
            iter = u1_parents.iterator();
            u1_parent = (User)iter.next();
            iter = u2_parents.iterator();
            u2_parent = (User)iter.next();
            // Se il genitore di {u1} e il genitore di {u2} sono dello stesso sesso e non identificano lo stesso utente, non è possibile che i due utenti siano fratelli
            if(!u1_parent.getGender().equals(u2_parent.getGender()) && !u1_parent.equals(u2_parent)) return false;

            // Se il genitore di {u2} non può essere coniuge di {u1}
            if(!u2.canAddLikeSpouse(u1)) return false;
            
        }
        
        do{

            // Se {u2} non ha genitori
            if(u2_size == 0){

                for(User parent: u1_parents){
                    // Se {u2} non può avere entrambi i genitori di {u1}, non è possibile che i due utenti siano fratelli
                    if(!u2.canAddLikeParent(parent)) return false;
                }

            }

            if(u2_size == 1 && u1_size == 2){
                iter = u1_parents.iterator();
                u1_parent = (User)iter.next();
                
                // Se il genitore di {u1} non è anche genitore di {u2}
                if(!u2_parents.contains(u1_parent)) return false;

                
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
                if(!u1.canAddLikeParent(other_parent)) return false;
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
        
        return true;
    }
    /**
     * Verifica se un dato utente può essere aggiunto come figlio
     * @return  true se l'utente è stato aggiunto con successo, false altrimenti
     */
    private boolean canAddLikeChild(User user) { 
        // Se {user} non puo diventare genitore dell'utente corrente, restituisci false
        return user.canAddLikeParent(this);
    }
    /**
     * Verifica se un dato utente può essere aggiunto come parente
     * @param user      user da aggiungere
     * @param relationship    grado di parentela
     * @return  true se l'utente è stato aggiunto come coniuge, false altrimenti
     */
    private boolean canAddLike(User user, String relationship) {
        
        switch(relationship){
            
            case "parent": return this.canAddLikeParent(user);
                
            case "child": return this.canAddLikeChild(user);
                
            case "sibling": return this.canAddLikeSibling(user);
                
            case "spouse": return this.canAddLikeSpouse(user);
                
            default: return false;
        }

    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Gestione albero genealogico">

    /**
     * Recupera i componenti dell'alabero genealogico dell'utente senza etichette
     * @return  lista di utenti che fanno parte dell'albero genealogico
     */
    public UserList getUnlabeledTree() {
        
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
                UserList ancestors = new UserList();
                try{
                // Aggiungi all'abero temporaneo gli antenati del parente
                 ancestors = relative.getAncestors();
                }catch(Exception e){
                    String s;
                    s="asd";
                }
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
     */
    public boolean isRelative(User user) {
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
     */
    public GenealogicalTree getFamilyTree(){
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
        
        try {
            
            User user = null;
            if(user_id != null){
            
                try (ResultSet record = Database.selectRecord("user", "id = '" + user_id + "'")) {
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
                    return record.getString("password").equals(password); 
                }
                return false;
            }

            
            
        } catch (SQLException ex) {
            return false;
        }
        
    }
    /**
     * Crea un nuovo utente
     * @param data      dati dell'utente
     * @param action    azioni da svolgere sull'utente (aggiungenta di madre, padre o coniuge)
     * @return
     */
    public static boolean create(Map<String, Object> data, Map<String, String> action){
        
            // Definisci i campi obbligatori
            String[] fields_required =  {"name", "surname", "gender", "birthdate", "birthplace"};
            String attr; Object value;

            // Per ogni campo obbligatorio
            for(Map.Entry<String, Object> e:data.entrySet()){
                attr = e.getKey();
                value = ((String) e.getValue()).trim();
                // Se un campo obbligatorio è vuoto, resituisci false
                for(String field_required: fields_required){
                    if(attr.equals(field_required) && value.equals("")) return false;
                }
            }
            
            
            // Validazione e-mail
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher((CharSequence) data.get("email"));
            if(!matcher.matches()) return false;
            
            // Genera id univoco dell'utente
            String user_id = User.createUniqueUserId(10);
            data.put("id", user_id);
            data.put("birthdate", Function.stringToDate((String) data.get("birthdate"), ""));

        
            // Inserisci l'utente
            boolean result = Database.insertRecord("user", data);
            // Se l'utente non è stato inserito correttamente, restituisci false
            if(!result) return false;
        
            /*
                INVECE DI SETTARE I PARENTI, BISOGNA MANDARE LORO UNA RICHIESTA
            */
        
            // Recupera utente appena creato
            User new_user = User.getUserById(user_id);
            boolean res;
            // Aggiungi il padre se necessario
            String father = action.get("father").trim();
            if(!father.equals("") && User.getUserById(father) != null) new_user.setRequest(User.getUserById(father), "father");
            // Aggiungi la madre se necessario
            String mother = action.get("mother").trim();
            if(!mother.equals("") && User.getUserById(mother) != null) new_user.setRequest(User.getUserById(mother), "mother");
            // Aggiungi il coniuge se necessario
            String spouse = action.get("spouse").trim();
            if(!spouse.equals("") && User.getUserById(spouse) != null) new_user.setRequest(User.getUserById(spouse), "spouse");
            // Aggiungi il coniuge se necessario
            String sibling = action.get("sibling").trim();
            if(!spouse.equals("") && User.getUserById(sibling) != null) new_user.setRequest(User.getUserById(sibling), "sibling");
            
            return true;

    }
    
    /**
     * Elimina un utente
     */
    public void delete(){
        // Rimuovi relazione genitore/figli
        for(User child: this.getChildren()){
            child.removeParent(this.gender);
        }
        // Rimuovi relazione marito/moglie
        this.removeSpouse();
        // Rimuovi utente
        Database.deleteRecord("user", "id = '" + this.id + "'");
    }
    
    //<editor-fold defaultstate="collapsed" desc="Metodi ausiliari">
        
    /**
     * Aggioena un attributo di un utente
     * @param attribute attributo da aggiornare
     * @param value     valore da assegnare all'attributo
     * @return
     */
    private boolean updateAttribute(String attribute, Object value){
        Map<String, Object> data = new HashMap();
        data.put(attribute, value);
        return Database.updateRecord("user", data, "id = '" + this.id + "'");
    }
    
    /**
     * Crea un user id univoco
     * @param length lunghezza id
     * @return
     */
    private static String createUniqueUserId(int length){
        // Definisci caratteri ammessi
        char[] chars = "ABCDEFGHILMNOPQRTUVZ1234567890".toCharArray();
        StringBuilder sb;
        String user_id;
        do{
            sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < length; i++) sb.append(chars[random.nextInt(chars.length)]);
            user_id = sb.toString();
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
