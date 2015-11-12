
$(document).ready(function(){
    
    // Abilita la chiamata ajax
    var ajax_enabled = true;
    
    // Al submit del form
    $(".lock-container form").submit(function( event ) {
        // Non eseguire il comportamente di default del submit
        event.preventDefault();
        
        // Inizialmente, imposta il form come valido
        var isValid = true;
        
        // Caching degli elementi
        var form = $(this);
        var fields = form.find(".form-control:not(button)");
        var message = form.find("div.form-message");
        var icon = message.find("i");
        var paragraph = message.find("p");
        
        //Controllo se tutti i campi sono compilati
        fields.each(function() {
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
            
            // Effettua la chiamata ajax
            var request = $.ajax({
                // Imposta una chiamata di tipo "post"
                method: "post",
                // Recupera la servlet a cui fare la chiamata
                url: form.attr("action"),
                // Recupera i dati inseriti
                data: form.serialize()
            });
            
            // Se la chiamata ajax va a buon fine
            request.done(function(msg) {
                // Se ritorna un messaggio di errore
                if(msg !== ''){
                    // Mostra messaggio di errore
                    message.removeClass("hide");
                    paragraph.html(msg);
                }else{
                    // Altrimenti, vai alla pagina dell'utente
                    window.location = "profile";   
                }
                // Riabilita la chiamata ajax
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

    // Quando si seleziona il sesso 
    $("select.form-control").change(function(){
        // Il colore della select deve diventare nero
        $(this).css("color", "#000");
    });
    
});