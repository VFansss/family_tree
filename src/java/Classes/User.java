/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.util.Date;

/**
 *
 * @author Gianluca
 */
public class User {
    private String name;
    private String surname;
    private String id;
    private String birthplace;
    private String birthdate;
    private String bio;
    private String imageurl;
    private String gender;
    
    public User(String name, String surname, String id, String birthplace, String birthdate, String bio, String imageurl,String gender){
        this.name=name;
        this.surname=surname;
        this.birthdate=birthdate;
        this.birthplace=birthplace;
        this.id=id;
        this.bio=bio;
        this.imageurl=imageurl;
        this.gender=gender;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getSurname(){
        return this.surname;
    }
    
    public String getId(){
        return this.id;
    }
    
    public String getBirthplace(){
        return this.birthplace;
    }
    
    public String getBirthdate(){
        return this.birthdate;
    }
    
    public String getBio(){
        return this.bio;
    }
    
    public String getImageurl(){
        return this.imageurl;
    }
}
