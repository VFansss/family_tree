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
    private final static Map<String, String> map;
    private final String msg;

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
        
        /* NotAllowedException */
        map.put("yourself", "Not allowed: you can't add yourself as relative");
            /* SPOUSE */
            map.put("sp_alr",   "Not allowed: you already have a spouse");
            map.put("sp_gen",   "Not allowed: you can't have the same gender of your spouse");
            map.put("sp_your",  "Not allowed: this user already is your spouse");
            map.put("sp_sib",   "Not allowed: your spouse can't be your sibling");
            map.put("sp_anc",   "Not allowed: your spouse can't be your anchestor");
            map.put("sp_off",   "Not allowed: your spouse can't be your offspring");
            /* FATHER */
            map.put("sp_alr",   "Not allowed: you already have a spouse");
            map.put("sp_gen",   "Not allowed: you can't have the same gender of your spouse");
            map.put("sp_your",  "Not allowed: this user already is your spouse");
            map.put("sp_sib",   "Not allowed: your spouse can't be your sibling");
            map.put("sp_anc",   "Not allowed: your spouse can't be your anchestor");
            map.put("sp_off",   "Not allowed: your spouse can't be your offspring");
            /* MOTHER */
            map.put("mot_alr",  "Not allowed: you already have a mother");
            map.put("mot_your", "Not allowed: this user already is your mother");
            map.put("mot_sib",  "Not allowed: your mother can't be your sibling");
            map.put("mot_anc",  "Not allowed: your mother can't be your anchestor");
            map.put("mot_off",  "Not allowed: your mother can't be your offspring");
            /* FATHER */
            map.put("fat_alr",  "Not allowed: you already have a father");
            map.put("fat_your", "Not allowed: this user already is your father");
            map.put("fat_sib",  "Not allowed: your father can't be your sibling");
            map.put("fat_anc",  "Not allowed: your father can't be your anchestor");
            map.put("fat_off",  "Not allowed: your father can't be your offspring");
            /* CHILD */
            map.put("ch_mot_alr",  "Not allowed: the user already have a mother");
            map.put("ch_mot_your", "Not allowed: this user already is your child");
            map.put("ch_mot_sib",  "Not allowed: your child can't be your sibling");
            map.put("ch_mot_anc",  "Not allowed: your child can't be your offsping");
            map.put("ch_mot_off",  "Not allowed: your child can't be your anchestor");
            map.put("ch_fat_alr",  "Not allowed: the user already have a father");
            map.put("ch_fat_your", "Not allowed: this user already is your child");
            map.put("ch_fat_sib",  "Not allowed: your child can't be your sibling");
            map.put("ch_fat_anc",  "Not allowed: your child can't be your offspring");
            map.put("ch_fat_off",  "Not allowed: your child can't be your anchestor");
            /* SIBLING */
            map.put("sib_your", "Not allowed: this user already is your sibling");
            map.put("sib_1",    "Not allowed: this user can't be add as sibling, because this relationship is not verificable");
            map.put("sib_2",    "Not allowed: this user can't be your sibling, because you have different parents");
            map.put("sib_3",    "Not allowed: this user can't be your sibling, beacause you can't have the same parents");
            
            
    }

   

}
