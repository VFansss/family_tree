
$(document).ready(function(){
//    $("[name=birthdate]").datepicker({});
    // Gestione cambio colore quando viene selezionato un elemento dal selectpicker
    $("select.selectpicker").change(function(){
        var form = $(this).parents("form");
        var color;
        // Se il form dove si trova il selectpicker è di tipo transparent
        if(form.hasClass("transparent")){
            // Il nuovo colore sarà bianco
            color="white";
        }else{
            // Altrimenti, sarà nero
            color="black";
        }
        
        // Cambia colore
        $(this).next().find("button.btn.dropdown-toggle.selectpicker span:first-child").css("color", color);

    });
    
    
    //Attivazione tooltips
    $('[data-toggle="tooltip"]').tooltip();
    
    // Quando si preme il pulsante di reset
    $("#search-reset-filters").click(function(e){
        // Non eseguire il comportamente di default del submit
        e.preventDefault();
        // Svuota tutti i campi del form
        $(".search-filters input, .search-filters select").val('');
    });
    
    
    // Abilita lo slider dei tile
    var slide_enabled = true;
    
    // Al click della label del tile
    $(".tile-label p:not(.no-collapsed)").click(function(e){
        // Se lo slider è abilitato
        if(slide_enabled){
            // Disabilita slider
            slide_enabled = false;
            
            // Caching degli elementi da utilizzare
            var label = $(this);
            var tile = label.parent().next();
            
            // Esegui lo slider
            tile.slideToggle(600, function(){
                // Al termine, riabilita lo slider
                slide_enabled = true;
            });
            
            // Esegui animazione della icona della label
            label.find("i.fa-chevron-down").toggleClass("down");
            
        }
        
    });
});