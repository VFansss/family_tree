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
        
});