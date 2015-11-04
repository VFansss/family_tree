/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function(){
    
    //Check campi login
    $("#loginform").submit(function( event ) {
        
        var isValid = true;
        event.preventDefault();
        
        //Controllo se tutti i campi sono compilati
        $('.form-control').each(function() {
            if ($(this).val() === '' ){
                $(".form-message").removeClass("hide");
                $(".form-message p").text("All field required");
                isValid=false;
                
            }
        });

//CHECK: E' una pagina di login o di signup?
 if( $('#signup_checker').length){isLogin=0;}
 else{isLogin=1; };


if(isValid){
            
            $.ajax({
            method: "POST",
            url: (isLogin === 1) ? "login" : "signup",
            data: $(this).serialize()})

            .done(function(msg) {
            
                if(msg!==''){
                //Messaggio di errore
                $(".form-message").removeClass("hide");
                $(".form-message p").text(msg);     
                }
              
                else{
                window.location = "profile";   
                }
            })
            
            .fail(function(xhr) {
            alert("error xhr.status");
            });
            
 
        }


    });
    
    
});