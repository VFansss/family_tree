package it.collaborative_genealogy;



import it.collaborative_genealogy.util.DataUtil;
import it.collaborative_genealogy.exception.NotAllowed;
import it.collaborative_genealogy.tree.GenealogicalTree;
import it.collaborative_genealogy.tree.NodeList;
import it.collaborative_genealogy.tree.TreeNode;
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
    
    private final String id;
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
    
    public String getMotherId() throws SQLException {
        /* E' possibile che questo valore venga modificato da altri utenti, per cui è necessario prelevarlo ogni volta dal database*/
        ResultSet record = Database.selectRecord("user", "id = '" + this.id +"'");
        if(record.next()){
            return record.getString("mother_id");
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
     * @throws java.text.ParseException
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
        // Recupero i parenti dell'utente corrente
        NodeList family_tree = this.getFamilyTree().getFamily(this);
        
        // Calcola il numero di parenti (-1 per non considerare il parente stesso)
        int tree_size = family_tree.size() - 1;
        
        Map<String, Object> data = new HashMap<>();
        data.put("num_relatives", tree_size);
        
        // Generazione della condizione: bisogna aggiornare i numeri di parenti ad ogni membro dell'albero genealogico
        String condition = "";
        for(TreeNode user: family_tree){
            condition = condition + "id = '" + user.getUser().getId() + "' OR ";
        }
        condition = condition.substring(0, condition.length()-4);
        // Aggoirna il numero di parenti
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
        // Aggiunta della madre
        parent.add(this.getMother());
        // Aggiunta del padre
        parent.add(this.getFather());
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
        // Se bisogna restituire il genitore femmina
        if(gender.equals("female")){
            // Restituire la madre
            return this.getMother();
        }else{
            // Altrimenti, restituire il padre
            return this.getFather();
        }        
    }
    /**
     * Aggiungi il padre o la madre
     * @param user  genitore da aggiungere
     * @throws java.sql.SQLException
     * @throws it.collaborative_genealogy.exception.NotAllowed
     */
    public void setParent(User user) throws SQLException, NotAllowed{
        this.canAddLike(user, "parent");
        
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
    
    /**
     * Recupera il padre
     * @return
     * @throws java.sql.SQLException
     */
    public User getFather() throws SQLException{
        return User.getUserById(this.getFatherId());
    }
     /**
     * Recupera la madre
     * @return
     * @throws java.sql.SQLException
     */
    public User getMother() throws SQLException{
        return User.getUserById(this.getMotherId());
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
        
        // Veridica se l'utente corrente può aggiungere {spouse} come coniuge
        this.canAddLike(spouse, "spouse");
       
        // Aggiungi il coniuge
        this.updateAttribute("spouse_id", spouse.getId());

        // Se non è già stato fatto, cambia anche il coniuge dell'utente appena aggiunto
        if(spouse.getSpouse() == null) {
            spouse.setSpouse(this);
        }           

        // Aggiorna numeri parenti
        this.setNumRelatives();

    }
    /**
     * Rimuovi il coniuge
     * @throws java.sql.SQLException
     */
    public void removeSpouse() throws SQLException {
        
        // Elimina il coniuge a entrambi i coniugi
        Database.resetAttribute("user", "spouse_id", "id = '" + this.id + "' OR id = '" + this.getSpouseId() + "'");
        User spouse = this.getSpouse();
        
        // Se i due ex-coniugi non sono più parenti
        if(!this.isRelative(spouse)){
            // Aggiorna il numero di parenti di entrambi
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
    /**
     * Inserisci un figlio
     * @param user  figlio da inserire
     * @throws it.collaborative_genealogy.exception.NotAllowed
     * @throws java.sql.SQLException
     */
    public void setChild(User user) throws NotAllowed, SQLException{
        // Imposta l'utente corrente come genitore
        user.setParent(this);
    }
    /**
     * Elimina un figlio
     * @param user  utente da eliminare come figlio
     * @throws it.collaborative_genealogy.exception.NotAllowed 
     * @throws java.sql.SQLException 
     */
    public void removeChild(User user) throws NotAllowed, SQLException{
        user.removeParent(this.gender);
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
     * Aggiungi un fratello o una sorella
     * @param sibling  utente da aggiungere
     * @throws java.sql.SQLException
     * @throws it.collaborative_genealogy.exception.NotAllowed
     */
    public void setSibling(User sibling) throws SQLException, NotAllowed {
        // Se {relative} non può essere aggiunto come fratello, restituisci false
        this.canAddLike(sibling, "sibling");

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
    
    //<editor-fold defaultstate="collapsed" desc="Recupero e gestione richieste di parentela">
    
    /**
     * Recupera le richieste di parentela ricevute
     * @return
     * @throws SQLException
     */
    public ResultSet getRequests() throws SQLException{   
        return Database.selectRecord("request", "relative_id = '" + this.id + "'");
    }
    
    /**
     * Invia una richiesta di parentela
     * @param user_receiver     utente che riceve la richiesta
     * @param relationship      grado di parentela (parent, spouse, child, sibling)
     * @throws NotAllowed
     * @throws SQLException
     */
    public void sendRequest(User user_receiver, String relationship) throws NotAllowed, SQLException {
        // Verifica se l'utente corrente può aggiungere {relative} come parente
        this.canAddLike(user_receiver, relationship);
        // Invia richiesta
        this.send_handler(user_receiver, relationship);
    }
    /**
     * Invia richiesta di parentela per conto di un altro utente
     * @param user_sender               utente che invia la richiesta
     * @param user_receiver             utente che riceve la richiesta
     * @param relationship              grado di parentela (parent, spouse, child, sibling)
     * @throws it.collaborative_genealogy.exception.NotAllowed
     * @throws java.sql.SQLException
     */
    public static void sendRequestFor(User user_sender, User user_receiver, String relationship) throws NotAllowed, SQLException {
        // Verifica se {user} può aggiungere {relative} come parente
        user_sender.canAddLike(user_receiver, relationship);
        // Invia richiesta
        user_sender.send_handler(user_receiver, relationship);
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
     * @param relative  parente che invia la richiesta
     * @throws java.sql.SQLException
     * @throws it.collaborative_genealogy.exception.NotAllowed
     */
    public void acceptRequest(User relative) throws SQLException, NotAllowed {
        ResultSet request = Database.selectRecord("request", "user_id = '" + relative.getId() + "' AND relative_id = '" + this.id + "'");
        String relationship = "";

        while(request.next()){
            relationship = request.getString("relationship");
        }

        // Effettua il collegamento tra i due parenti
        switch(relationship){
        
            case "parent":  this.setChild(relative);    break;
                
            case "child":   this.setParent(relative);   break;
            
            case "sibling": this.setSibling(relative);  break;
                
            case "spouse":  this.setSpouse(relative);   break;
                
            default: throw new NotAllowed();
        }
        
        // Rimuovi la richiesta dal database
        relative.deleteRequest(this);
        
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
        
        // Se l'utente hà gia un genitore dello stesso sesso
        if(this.getParent(user.getGender()) != null) throw new NotAllowed();
        
        String user_gender = user.getGender();
        // Se {user} è tra i fratelli/sorelle    
        if(this.getSiblings().contains(user)) throw new NotAllowed();
        
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
                3. utenti dello stesso sesso
        */
        
        // Se l'utente corrente e/o {user} hanno già un coniuge
        if(this.getSpouse() != null || user.getSpouse() != null) throw new NotAllowed();
        
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
        
        User u1 = this;
        User u2 = user;

        // Recupera i genitori dei due utenti
        UserList u1_parents = u1.getParents();
        UserList u2_parents = u2.getParents();
        
        // Recupera il numero di genitori dei due utenti
        int u1_size = u1_parents.size();
        int u2_size = u2_parents.size();
        
        User u1_parent, u2_parent;
        
        // Se entrambi gli utenti non hanno nessun genitore, non è possibile verificare la parentela {*}
        if(u2_size == 0 && u1_size == 0) throw new NotAllowed();

        // Se i due utenti hanno già entrambi i genitori, non è possibile che i due utenti siano fratelli {**}
        if(u2_size == 2 && u1_size == 2) throw new NotAllowed();
        
        // Se entrambi gli utenti hanno un solo genitore
        if(u2_size == 1 && u1_size == 1){
            // Recupera l'unico genitore di {u1}
            u1_parent = (User) u1_parents.iterator().next();
            // Reucpera l'unico genitore di {u2}
            u2_parent = (User) u2_parents.iterator().next();
            // Se il genitore di {u1} e il genitore di {u2} sono dello stesso sesso e non identificano lo stesso utente, non è possibile che i due utenti siano fratelli
            if(!u1_parent.getGender().equals(u2_parent.getGender()) && !u1_parent.equals(u2_parent)) throw new NotAllowed();
            // Verifica se il genitore di {u2} può essere coniuge del genitore di {u1}
            u2_parent.canAddLike(u1_parent, "spouse");
            
        }
        
        do{

            // Se {u2} non ha genitori
            if(u2_size == 0){

                for(User parent: u1_parents){
                    // Se {u2} non può avere entrambi i genitori di {u1}, non è possibile che i due utenti siano fratelli
                    u2.canAddLike(parent, "parent");
                }

            }
            
            // Se {u2} ha un solo genitore e {u1} gli ha entrmabi
            if(u2_size == 1 && u1_size == 2){
                // Recupera l'unico genitore di {u2}
                u2_parent = u2_parents.iterator().next();
                
                // Se il genitore di {u2} non è anche genitore di {u1}
                if(!u1_parents.contains(u2_parent)) throw new NotAllowed();

                User other_parent;
                // Se {u2} ha solo la madre
                if(u2.getMother() != null){
                    // Recupera il padre di {u1}
                    other_parent = u1.getFather();
                }else{
                    // Altrimenti recupara la madre di {u1}
                    other_parent = u1.getMother();
                }
                // Se l'altro genitore di {u1} può essere aggiunto come genitore di {u2}
                u2.canAddLike(other_parent, "parent");
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
        // Verifica se {user} può aggiungere l'utente corrente come genitore
        user.canAddLike(this, "parent");
    }
    /**
     * Verifica se un dato utente può essere aggiunto come parente
     * @param user      user da aggiungere
     * @param relationship    grado di parentela
     * @throws java.sql.SQLException
     * @throws it.collaborative_genealogy.exception.NotAllowed
     */
    private void canAddLike(User user, String relationship) throws SQLException, NotAllowed {
        
        // Se c'è già una richiesta di parentela tra i due utenti
        int record = Database.countRecord("request", "(user_id='" + this.id + "' AND relative_id='" + user.getId() + "') OR (user_id='" + user.getId()+ "' AND relative_id='" + this.id + "')");
        if(record != 0) throw new NotAllowed();
        
        switch(relationship){
            
            case "parent": this.canAddLikeParent(user);     break;
                
            case "child": this.canAddLikeChild(user);       break;
                
            case "sibling": this.canAddLikeSibling(user);   break;
                
            case "spouse": this.canAddLikeSpouse(user);     break;
                
            default: throw new NotAllowed();
        }

    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Gestione albero genealogico">

    /**
     * Verifica se l'utente è "parente" ad un altro: con parente si intende qualsiasi utente raggiungibile attraverso legami di parentela
     * @param user  parente da cercare
     * @return      true se l'user è tra i parenti, false altrimenti
     * @throws java.sql.SQLException
     */
    public boolean isRelative(User user) throws SQLException {
        
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
                // Se l'utente da trovare è tra gli antenati, ritorna true
                if(ancestors.contains(user)) return true;
                
                // Se l'utente non ha antenati, inserisci l'utente stesso tra gli antenati, cosi da poter cercare i suoi discendenti
                if(ancestors.isEmpty()) ancestors.add(relative);
                // Aggiungi gli antenati all'albero genealogico
                family_tree_temp.addAll(ancestors);
                
                // Per ogni antenato trovato
                for(User ancestor: ancestors){
                    // Se l'utente è già stato valutato
                    if(evaluated.contains(ancestor)) continue;
                    // Recupera i discendenti 
                    UserList offsprings = ancestor.getOffsprings();
                    // Se l'utente da trovare è tra i discententi, ritorna true
                    if(offsprings.contains(user)) return true;
                    // Aggiungi all'albero temporaneo tutti i suoi discendenti (quindi anche i discendenti dell'parente stesso)
                    family_tree_temp.addAll(offsprings);
                   
                }
                User spouse = relative.getSpouse();
                // Se l'utente da trovare è il coniuge
                if(spouse != null){
                    if(spouse.equals(user)) return true;
                    // Aggiungi all'albero temporaneo il coniuge del parente
                    family_tree_temp.add(spouse);
                }
                
                evaluated.add(relative);
            }
            
            // Aggiungi l'albero temporaneo a quello finale
            family_tree_final.addAll(family_tree_temp);

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
    
     /**
     * Recupera i componenti del nucleo familiare dell'utente
     * @return  lista digli utenti che compongono il nucleo familiare dell'utente
     * @throws java.sql.SQLException
     */
    public UserList getFamilyCore() throws SQLException{
        UserList family_core = new UserList();
        // Aggiungi i genitori
        family_core.addAll(this.getParents());
        // Aggiungi i figli
        family_core.addAll(this.getChildren());
        // Aggiungi i fratelli
        family_core.addAll(this.getSiblings());
        // Aggiungi il coniuge
        family_core.add(this.getSpouse());
        return family_core;
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
     * Recupera un utente attraverso la sua e-mail
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
     * Verifica la password dell'utente
     * @param password      password da verificare
     * @return              true se la password è verificata, falsa altrimenti
     */
    public boolean checkPassword(String password){
        
        try {
            ResultSet record = Database.selectRecord("user", "email = '" + this.getEmail() + "'");
            if(record.next()){
                return DataUtil.decrypt(record.getString("password"), password); 
            }
        } catch (SQLException ex) { }
        
        return false;
    }
    
    /**
     * Elimina l'utente
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
    
    /**
     * Imposta le variabili di sessione necessarie
     * @param request
     */
    public void prepareToLog(HttpServletRequest request){
        // Apri la sessione
        HttpSession session = request.getSession();
        // Inserisci l'utente corrente nella variabile di sessione
        session.setAttribute("user_logged", this);
        // Inizializza la breadcrumb
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
