/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function(){
    var ajax_enabled = true;
    $(".uploader").change(function(){
        $(this).parents("form").find(".form-message").addClass("hide");
        
    });
    $(".wrapper form").submit(function( event ) {
        
//        $(".form-message").addClass("hide");
        event.preventDefault();
        var isValid = true;
        
        // Caching elementi
        var $form = $(this);
        var $fields = $(this).find(".form-control:not(textarea)");
        var $message = $form.find("div.form-message");
        var $icon = $message.find("i");
        var $paragraph = $message.find("p");
        
        //Controllo se tutti i campi sono compilati
        $fields.each(function() {
            if ($(this).val() === ''){
                $message.removeClass("hide");
                $icon.attr("class", "fa fa-times");
                $paragraph.html("All field required");
                isValid=false;
                return false;
            }
        });

        if(isValid && ajax_enabled){
            // Disabilita ajax per evitare chiamate multiple
            ajax_enabled = false;
            var action = $(this).attr("action");
            var request;
            if(action.indexOf("avatar") > -1){
                request = $.ajax({
                    method: "POST",
                    url: $form.attr("action"),
                    data: new FormData(this),
                    dataType: "json",
                    target: '#preview',
                    contentType: false,
                    cache: false,
                    processData: false
                });
                
            }else{
                request = $.ajax({
                    method: "POST",
                    url: $form.attr("action"),
                    data: $form.serialize(),
                    dataType: "json"
                });
            }
            
            
            request.done(function(msg) {
                if(msg["error"] === "true"){
                    $icon.attr("class", "fa fa-times");
                }else{
                    $icon.attr("class", "fa fa-check");  

                    if(action.indexOf("avatar") > -1){
                        $(".to-refresh").attr("src", $(".to-refresh").attr("src") + "&code=" +  Math.random());
                        $form.get(0).reset();
                    }
                        
                }
                $message.removeClass("hide");
                $paragraph.html(msg["message"]);
                
                
                // Abilita ajax
                ajax_enabled = true;
            });
            
            request.fail(function(xhr) {
                //Messaggio di errore
                $message.removeClass("hide");
                $icon.attr("class", "fa fa-times");
                $paragraph.text("Server error");
                // Abilita ajax
                ajax_enabled = true;
            });
        }
    });
});