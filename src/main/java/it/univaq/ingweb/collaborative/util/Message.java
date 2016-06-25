package it.univaq.ingweb.collaborative.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Marco
 */
public final class Message {
    private final String code;
    private final boolean error;
    private final static Map<String, String> MAP;
    private final String msg;

    public String getMsg() {
        return this.msg;
    }

    public Message(String code, boolean error) {
        this.code = code;
        this.error = error;
        this.msg = MAP.get(this.code);
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
        MAP = new HashMap<>();
        /*GENERIC ERROR*/
        MAP.put("err", "There was an error");
        /* USER */
        MAP.put("usr_1", "User does not exist");
        MAP.put("usr_2", "User already exist");
        MAP.put("usr_3", "No user found");
        /* NOME */
        MAP.put("name_1", "The name must be alphanumeric");
        MAP.put("name_2", "The name is too short");
        MAP.put("name_3", "The name is too long");
        /* COGNOME */
        MAP.put("surname_1", "The surname must be alphanumeric");
        MAP.put("surname_2", "The surname is too short");
        MAP.put("surname_3", "The surname is too long");
        /* SESSO */
        MAP.put("gnd", "You can be only male or female");
        /* LUOGO DI NASCITA */
        MAP.put("plc", "The birthplace must be alphanumeric");
        /* DATA DI NASCITA */
        MAP.put("date_1", "The birthdate isn't in the right format");
        MAP.put("date_2", "The birthdate in not valid");
        MAP.put("dt_ok", "Data changed");
        /* EMAIL  */
        MAP.put("eml_1", "Current email is not valid");
        MAP.put("eml_2", "Confirm email is not valid");
        MAP.put("eml_3", "Email is not valid");
        MAP.put("eml_ok", "Email changed");
        /* PASSWORD  */
        MAP.put("psw", "Incorrect password");
        MAP.put("psd_1", "Current passwrod is not valid");
        MAP.put("psd_2", "Confirm passwrod is not valid");
        MAP.put("psd_3", "The password must be 6 characters at least");
        MAP.put("psd_4", "The password must be alphanumeric");
        MAP.put("psd_ok", "Password changed");
         /* FOTO */
        MAP.put("pho_slt", "Please, select a photo");
        MAP.put("pho_ok", "Photo Uploaded Successfully");
        MAP.put("pho_err", "Photo Uploaded Failed");
        /* REQUEST */
        MAP.put("snd", "Request sent");
        MAP.put("acc", "Request accepted");
        MAP.put("dec", "Request declined");
        /* OTHER */
        MAP.put("srv", "An error occurred, please retry");
        MAP.put("tmp", "Tampered data");
        MAP.put("alp", "Please, insert alphanumeric characters only");
        MAP.put("log", "Please log in to see this page");
        MAP.put("fld", "All fields are required");
        MAP.put("inv", "User invited");
        MAP.put("basic_add", "Relative added to your tree");
        
        /* NotAllowedException */
        MAP.put("yourself",     "Not allowed: you can't add yourself as relative");
        /* SPOUSE */
        MAP.put("sp_alr",       "Not allowed: you already have a spouse");
        MAP.put("sp_gen",       "Not allowed: you can't have the same gender of your spouse");
        MAP.put("sp_your",      "Not allowed: this user already is your spouse");
        MAP.put("sp_sib",       "Not allowed: your spouse can't be your sibling");
        MAP.put("sp_anc",       "Not allowed: your spouse can't be your anchestor");
        MAP.put("sp_off",       "Not allowed: your spouse can't be your offspring");
        /* FATHER */
        MAP.put("sp_alr",       "Not allowed: you already have a spouse");
        MAP.put("sp_gen",       "Not allowed: you can't have the same gender of your spouse");
        MAP.put("sp_your",      "Not allowed: this user already is your spouse");
        MAP.put("sp_sib",       "Not allowed: your spouse can't be your sibling");
        MAP.put("sp_anc",       "Not allowed: your spouse can't be your anchestor");
        MAP.put("sp_off",       "Not allowed: your spouse can't be your offspring");
        /* MOTHER */
        MAP.put("mot_alr",      "Not allowed: you already have a mother");
        MAP.put("mot_your",     "Not allowed: this user already is your mother");
        MAP.put("mot_sib",      "Not allowed: your mother can't be your sibling");
        MAP.put("mot_anc",      "Not allowed: your mother can't be your anchestor");
        MAP.put("mot_off",      "Not allowed: your mother can't be your offspring");
        /* FATHER */
        MAP.put("fat_alr",      "Not allowed: you already have a father");
        MAP.put("fat_your",     "Not allowed: this user already is your father");
        MAP.put("fat_sib",      "Not allowed: your father can't be your sibling");
        MAP.put("fat_anc",      "Not allowed: your father can't be your anchestor");
        MAP.put("fat_off",      "Not allowed: your father can't be your offspring");
        /* CHILD */
        MAP.put("ch_mot_alr",   "Not allowed: the user already have a mother");
        MAP.put("ch_mot_your",  "Not allowed: this user already is your child");
        MAP.put("ch_mot_sib",   "Not allowed: your child can't be your sibling");
        MAP.put("ch_mot_anc",   "Not allowed: your child can't be your offsping");
        MAP.put("ch_mot_off",   "Not allowed: your child can't be your anchestor");
        MAP.put("ch_fat_alr",   "Not allowed: the user already have a father");
        MAP.put("ch_fat_your",  "Not allowed: this user already is your child");
        MAP.put("ch_fat_sib",   "Not allowed: your child can't be your sibling");
        MAP.put("ch_fat_anc",   "Not allowed: your child can't be your offspring");
        MAP.put("ch_fat_off",   "Not allowed: your child can't be your anchestor");
        /* SIBLING */
        MAP.put("sib_your",     "Not allowed: this user already is your sibling");
        MAP.put("sib_1",        "Not allowed: this user can't be add as sibling, because this relationship is not verificable");
        MAP.put("sib_2",        "Not allowed: this user can't be your sibling, because you have different parents");
        MAP.put("sib_3",        "Not allowed: this user can't be your sibling, beacause you can't have the same parents");
            
            
    }

   

}
