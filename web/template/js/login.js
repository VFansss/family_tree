/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function(){
    var ajax_enabled = true;
    
    $("#loginform").submit(function( event ) {
        event.preventDefault();
        var isValid = true;
        
        
        //Controllo se tutti i campi sono compilati
        $('.form-control').each(function() {
            if ($(this).val() === '' ){
                $(".form-message").removeClass("hide");
                $(".form-message p").html("All field required");
                isValid=false;
                return false;
            }
        });

        if(isValid && ajax_enabled){
            // Disabilita ajax per evitare chiamate multiple
            ajax_enabled = false;
            var request = $.ajax({
                method: "POST",
                url: $(this).attr("action"),
                data: $(this).serialize()
            });
            
            request.done(function(msg) {
                if(msg !== ''){
                    //Messaggio di errore
                    $(".form-message").removeClass("hide");
                    $(".form-message p").html(msg);
                }else{
                    window.location = "profile";   
                }
                // Abilita ajax
                ajax_enabled = true;
            });
            
            request.fail(function(xhr) {
                //Messaggio di errore
                $(".form-message").removeClass("hide");
                $(".form-message p").text("Server error");
                // Abilita ajax
                ajax_enabled = true;
            });
        }
    });
});