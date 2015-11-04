/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function(){
    //Attivazione tooltips
    $('[data-toggle="tooltip"]').tooltip();
    
    $("#search-reset-filters").click(function(e){
       e.preventDefault();
       
       $(".search-filters input, .search-filters select").val('');
       
    });
    
    var slide_lock = false;
    
    $(".tile-label a").click(function(e){
        e.preventDefault();
        if(!slide_lock){
            slide_lock = true;
            
            var label = $(this);
            var tile = label.parent().next();
            
            tile.slideToggle(function(){
                
                if(label.hasClass("collapsed")){
                    label.removeClass("collapsed");
                    label.children("i").removeClass("fa-chevron-right").addClass("fa-chevron-down");
                } else {
                    label.addClass("collapsed");
                    label.children("i").removeClass("fa-chevron-down").addClass("fa-chevron-right"); 
                }
            });
            
            slide_lock = false;
        }
    });
        
});