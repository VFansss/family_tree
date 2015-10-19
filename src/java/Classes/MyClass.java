/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

/**
 *
 * @author Gianluca
 */
public class MyClass {
    private String field1;
    private String field2;
    
    public MyClass(String f1, String f2){
        this.field1 = f1;
        this.field2 = f2;
    }
    
    public String getField1(){
        return this.field1;
    }
    
    public String getField2(){
        return this.field2;
    }
}
