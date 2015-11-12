
$(document).ready(function(){
    
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