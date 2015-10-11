/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Templating;

/**
 *
 * @author Marco
 */
class Developer {

    private String name;
    private String project;
 
    public Developer(String name, String project) {
        this.name = name;
        this.project = project;
    }
 
    public String getName() {
        return name;
    }
 
    public String getProject() {
        return project;
    }
    
}
