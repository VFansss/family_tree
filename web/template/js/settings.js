
$(document).ready(function(){
    
    // Abilita la chiamata ajax
    var ajax_enabled = true;
    
    // Quando si seleziona una nuova immagine
    $(".uploader").change(function(){
        // Elimina messaggio del form
        $(this).parents("form").find(".form-message").addClass("hide");
    });
    
    // Al submit del form
    $("section.settings .wrapper form").submit(function( event ) {
        // Non eseguire il comportamente di default del submit
        event.preventDefault();
        
        // Inizialmente, imposta il form come valido
        var isValid = true;
        
        // Caching degli elementi
        var form = $(this);
        var fields = form.find("input.form-control, select.form-control");
        var message = form.find("div.form-message");
        var icon = message.find("i");
        var paragraph = message.find("p");
        
        //Controllo se tutti i campi sono compilati
        fields.each(function() {
            console.log($(this));
            // Se il campo corrente non è stato compilato
            if ($(this).val() === '' ){
                // Mostra messaggio di errore
                message.removeClass("hide");
                paragraph.html("All field required");
                // Imposta il form come non valido
                isValid=false;
                // Esci dal ciclo, in quanto basta un campo non compilato per invalidare il form
                return false;
            }
        });

        // Se il form è valido ed è possibile effetuare una chiamata ajax
        if(isValid && ajax_enabled){
            // Disabilita la chiamata ajax per evitare chiamate multiple
            ajax_enabled = false;
            // Recupera la servlet a cui fare la chiamata
            var action = form.attr("action");
            var request;
            
            // Se si vuole cambiare l'avatar
            if(action.indexOf("avatar") > -1){
                
                // Imposta una chiamata ajax che sia in grado di gestire un file
                request = $.ajax({
                    method: "POST",
                    url: action,
                    data: new FormData(this),
                    dataType: "json",
                    target: '#preview',
                    contentType: false,
                    cache: false,
                    processData: false
                });
                
            }else{
                
                // Altrimenti, imposta una chiamata ajax normale
                request = $.ajax({
                    method: "POST",
                    url: action,
                    data: form.serialize(),
                    dataType: "json"
                });
            }
            
            // Se la chiamata va a buon fine
            request.done(function(msg) {
                // Se è ritornato un messaggio di errore
                if(msg["error"] === "true"){
                    // Imposta messaggio di errore
                    icon.attr("class", "fa fa-times");
                }else{
                    // Imposta messaggio di successo
                    icon.attr("class", "fa fa-check");  
                    
                    // Se è stato cambiato l'avatar
                    if(action.indexOf("avatar") > -1){
                        // Refresh dell'immagine (da rivedere)
                        $(".to-refresh").attr("src", $(".to-refresh").attr("src") + "&code=" +  Math.random());
                        // Resetta il form
                        form.get(0).reset();
                    }
                        
                }
                // Mostra messaggio di ritorno
                message.removeClass("hide");
                paragraph.html(msg["message"]);
                
                
                // Ribilita ajax
                ajax_enabled = true;
            });
            
            // Se la chiamata non va a buon fine
            request.fail(function(xhr) {
                // Mostra essaggio di errore
                message.removeClass("hide");
                paragraph.text("Server error");
                // Riabilita la chiamata ajax
                ajax_enabled = true;
            });
        }
    });
    
});