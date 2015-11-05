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
    
    $(".tile-label p:not(.no-collapsed)").click(function(e){
        if(!slide_lock){
            slide_lock = true;
            
            var label = $(this);
            var tile = label.parent().next();
            
            tile.slideToggle(600, function(){});
            
            label.find("i.fa-chevron-down").toggleClass("down");
            slide_lock = false;
        }
    });
});