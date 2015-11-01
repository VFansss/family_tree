/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

/**
 *
 * @author Marco
 */
public class Message {
    private String code;
    private boolean error;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public Message(String code, boolean error) {
        this.code = code;
        this.error = error;
        this.getExtentedMessage();
    }

    public String getCode() {
        return code;
    }
    
    public boolean isError() {
        return error;
    }
    
    public void getExtentedMessage(){

        if(this.code != null){
            switch(this.code){
                /* LOGIN */
                    case "log":
                        msg = "Please log in to see this page";
                        break;
                    case "usr":
                        msg = "User does not exist";
                        break;
                    case "psw":
                        msg = "Incorrect password";
                        break;
                    
                /* SIGNUP */                    
                /* SIGNUP */
                        
                        
                        
                /* SETTINGS */
                        
                    case "fld":
                        msg = "All fields are required";
                        break;
                        
                    case "name_1":
                        msg = "The name must be alphanumeric";
                        break;
                    case "name_2":
                        msg = "The name is too short";
                        break;
                    case "name_3":
                        msg = "The name is too long";
                        break;
                    case "surname_1":
                        msg = "The surname must be alphanumeric";
                        break;
                    case "surname_2":
                        msg = "The surname is too short";
                        break;
                    case "surname_3":
                        msg = "The surname is too long";
                        break;
                        
                    case "gnd":
                        msg = "You can be only male or female";
                        break;
                    
                    case "plc":
                        msg = "The birthplace must be alphanumeric";
                        break;
                    
                    case "date_1":
                        msg = "The date isn't in the right format";
                        break;
                    case "date_2":
                        msg = "The date in not valid2";
                        break;
                    case "dt_ok":
                        msg = "Data changed";
                        break;
                        
                    case "eml_1":
                        msg = "Current email is not valid";
                        break;
                    case "eml_2":
                        msg = "Confirm email is not valid";
                        break;
                    case "eml_3":
                        msg = "Email is not valid";
                        break;
                    case "eml_4":
                        msg = "User already exist";
                        break;
                    case "eml_ok":
                        msg = "Email changed";
                        break;
                        
                        
                    case "psd_1":
                        msg = "Current passwrod is not valid";
                        break;
                    case "psd_2":
                        msg = "Confirm passwrod is not valid";
                        break;
                    case "psd_3":
                        msg = "The password must be 6 characters at least";
                        break;
                    case "psd_4":
                        msg = "The password must be alphanumeric";
                        break;
                    case "psd_ok":
                        msg = "Password changed";
                        break;
                        
                    case "pho_slt":
                        msg = "Please, select a photo";
                        break;
                    case "pho_ok":
                        msg = "Photo Uploaded Successfully";
                        break;
                    case "pho_err":
                        msg = "Photo Uploaded Failed";
                        break;
                    
                /* OTHER */        
                    case "srv":
                        msg = "Server error";
                        break;
                    case "tmp":
                        msg = "Tampered data";
                        break;  
                        
                default: msg = null;
            }
        }
        
    }

}
