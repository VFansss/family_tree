/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function(){
    var ajax_enabled = true;
    
    $("form").submit(function( event ) {
        event.preventDefault();
        var isValid = true;
        
        // Caching elementi
        var $form = $(this);
        var $fields = $(this).find(".form-control");
        var $message = $form.find("div.form-message");
        var $icon = $message.find("i");
        var $paragraph = $message.find("p");
        
        //Controllo se tutti i campi sono compilati
        $fields.each(function() {
            if ($(this).val() === '' ){
                $message.removeClass("hide");
                $paragraph.html("All field required");
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
                    $message.removeClass("hide");
                    $paragraph.html(msg);
                }else{
                    window.location = "profile";   
                }
                // Abilita ajax
                ajax_enabled = true;
            });
            
            request.fail(function(xhr) {
                //Messaggio di errore
                $message.removeClass("hide");
                $paragraph.text("Server error");
                // Abilita ajax
                ajax_enabled = true;
            });
        }
    });
});