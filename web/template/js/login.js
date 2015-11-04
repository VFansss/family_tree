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

        if(isValid){
            
            var request = $.ajax({
                method: "POST",
                url: $(this).attr("action"),
                data: $(this).serialize()
            })
            
            request.done(function(msg) {
                if(msg !== ''){
                    //Messaggio di errore
                    $(".form-message").removeClass("hide");
                    $(".form-message p").text(msg);     
                }else{
                    window.location = "profile";   
                }
            })
            
            request.fail(function(xhr) {
                //Messaggio di errore
                $(".form-message").removeClass("hide");
                $(".form-message p").text("Server error");
            });
        }
    });
});