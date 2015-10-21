/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.util.*;

/**
 *
 * @author Gianluca
 */
public class UserBuilder {
    
    public static User aragorn = new User("Aragorn", "Granpasso", "DSC213", "Gondor", "1/03/2931", "Per frodo!", "aragorn.jpg");
    public static User arwen = new User("Arwen", "Undomiel", "ASH359", "Valinor", "15/06/241", "Preferirei dividere una sola vita con te che affrontare tutte le ere di questo mondo da sola.", "arwen.jpg");
    public static User arathorn = new User("Arathorn II", "Dunendain", "DGS830", "Gondor", "10/03/2900", "Mio figlio sembra Gesù Cristo.", "arathorn.jpg");
    public static User gilraen = new User("Gilraen", "Dunendain", "AMH559", "Gondor", "15/06/2907", "Ho dato la speranza ai Dúnedain, non ne ho conservata per me.", "gilraen.jpg");
    public static User eldarion = new User("Eldarion", "Dunendain", "HSB302", "Gondor", "25/12/0", "Il cantante degli Aerosmith è mio nonno, ma la mamma non lo sa.", "eld.jpg");

    public static User legolas = new User("Legolas", "Verdefoglia", "EHD930", "Bosco Atro", "87 T.E.", "They are taking the hobbits to Isegard!", "legolas.jpg");
    public static User gimli = new User("Gimli", "Durin", "HSD732", "Moria", "2879 T.E.", "Che vengano pure! Troveranno che qui a Moria c'è ancora un nano che respira!", "gimli.jpg");
    public static User boromir = new User("Boromir", "Dunendain", "SHS733", "Gondor", "2978 T.E.", "One does not simply walk to Mordor...", "boromir.jpg");
    
    private static List<User> userlist;
    
    static{
        userlist = new LinkedList<User>();
        
        userlist.add(aragorn);
        userlist.add(arwen);
        userlist.add(arathorn);
        userlist.add(gilraen);
        userlist.add(eldarion);
        userlist.add(legolas);
        userlist.add(gimli);
        userlist.add(boromir);
    }
    
    public static User getUserById(String id){
        for(User current: userlist){
            if(current.getId().equals(id)) return current;
        }
        return aragorn;
    }
    
    public static User getUserByName(String name){
        String curname;
        name = name.toLowerCase();
        for(User current: userlist){
            curname = current.getName().toLowerCase();
            if(curname.equals(name)) return current;
        }
        return null;
    }
}
