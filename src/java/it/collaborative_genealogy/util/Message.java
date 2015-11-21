/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.collaborative_genealogy.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Marco
 */
public final class Message {
    private final String code;
    private final boolean error;
    private static Map<String, String> map;
    private String msg;

    public String getMsg() {
        return this.msg;
    }

    public Message(String code, boolean error) {
        this.code = code;
        this.error = error;
        this.msg = map.get(this.code);
    }

    public String getCode() {
        return this.code;
    }

    public boolean isError() {
        return this.error;
    }

    public String toJSON() {
        String error_string = this.error ? "true" : "false";
        return "{\"message\":\"" + this.msg + "\", \"error\":\"" + error_string + "\"}";
    }

    static{
        map = new HashMap<>();
        /* USER */
        map.put("usr_1", "User does not exist");
        map.put("usr_2", "User already exist");
        map.put("usr_3", "No user found");
        /* NOME */
        map.put("name_1", "The name must be alphanumeric");
        map.put("name_2", "The name is too short");
        map.put("name_3", "The name is too long");
        /* COGNOME */
        map.put("surname_1", "The surname must be alphanumeric");
        map.put("surname_2", "The surname is too short");
        map.put("surname_3", "The surname is too long");
        /* SESSO */
        map.put("gnd", "You can be only male or female");
        /* LUOGO DI NASCITA */
        map.put("plc", "The birthplace must be alphanumeric");
        /* DATA DI NASCITA */
        map.put("date_1", "The birthdate isn't in the right format");
        map.put("date_2", "The birthdate in not valid");
        map.put("dt_ok", "Data changed");
        /* EMAIL  */
        map.put("eml_1", "Current email is not valid");
        map.put("eml_2", "Confirm email is not valid");
        map.put("eml_3", "Email is not valid");
        map.put("eml_ok", "Email changed");
        /* PASSWORD  */
        map.put("psw", "Incorrect password");
        map.put("psd_1", "Current passwrod is not valid");
        map.put("psd_2", "Confirm passwrod is not valid");
        map.put("psd_3", "The password must be 6 characters at least");
        map.put("psd_4", "The password must be alphanumeric");
        map.put("psd_ok", "Password changed");
         /* FOTO */
        map.put("pho_slt", "Please, select a photo");
        map.put("pho_ok", "Photo Uploaded Successfully");
        map.put("pho_err", "Photo Uploaded Failed");
        /* REQUEST */
        map.put("snd", "Request sent");
        map.put("acc", "Request accepted");
        map.put("dec", "Request declined");
        /* OTHER */
        map.put("srv", "An error occurred, please retry");
        map.put("tmp", "Tampered data");
        map.put("alp", "Please, insert alphanumeric characters only");
        map.put("log", "Please log in to see this page");
        map.put("fld", "All fields are required");
        map.put("inv", "User invited");
        map.put("no_all", "Not Allowed");

    }

   

}
