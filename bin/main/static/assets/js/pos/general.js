$(document).ready(function() {
    if ($('#adminstrator').length != 0) {
        $('#adminstrator').on("click", function () {
            $(this).children('.down_menu').stop().slideToggle();
        });
    }
    $('.scrollabletable').DataTable({
        "scrollY": "400px"
        , "scrollCollapse": true
        , "paging": false
        , "searching": false
        , "info": false
        , "ordering": true
    });
    if ($('#parentHorizontalTab').length != 0) {
        $('#parentHorizontalTab').easyResponsiveTabs({
            type: 'default'
            , width: 'auto'
            , fit: true
            , tabidentify: 'hor_1'
        , });
    }
   
});

// Datepicker Script

$(function () {
	var $dateone = $('.date-input-one');
	var $datetwo = $('.date-input-two');
	var $startDate = $('.start-date');
	var $endDate = $('.end-date');


	$dateone.datepicker({
        autoHide: true,
		trigger: '.dt-input-trigger-one'
	});
	$datetwo.datepicker({
        autoHide: true,
		trigger: '.dt-input-trigger-two'
	});

	$startDate.datepicker({
		autoHide: true,
		trigger: '.dt-start-trigger'
	});
	$endDate.datepicker({
		autoHide: true,
		trigger: '.dt-end-trigger',
		startDate: $startDate.datepicker('getDate')
	});

	$startDate.on('change', function () {
		$endDate.datepicker('setStartDate', $startDate.datepicker('getDate'));
	});

});







// Autocomplete Script

if ($('.autocomplete').length != 0) {

function autocomplete(inp, arr) {
    console.log("inside autocomplete");
  /*the autocomplete function takes two arguments,
  the text field element and an array of possible autocompleted values:*/
  var currentFocus;
//   $('.autocomplete-search-block').remove(); 

  /*execute a function when someone writes in the text field:*/
  inp.addEventListener("input", function(e) {
      var a, b, i, val = this.value;
      /*close any already open lists of autocompleted values*/
      closeAllLists();
      if (!val) { return false;}
      currentFocus = -1;
      /*create a DIV element that will contain the items (values):*/
      a = document.createElement("DIV");
      a.setAttribute("id", this.id + "autocomplete-list");
      a.setAttribute("class", "autocomplete-items");
      /*append the DIV element as a child of the autocomplete container:*/
	  this.parentNode.appendChild(a);
	  var ap=document.createElement("div");
      ap.setAttribute("class", "autocomplete-search-block");//add another div after the result <div>
      ap.innerHTML="<span class='autocomplete-search-label'>Vendor not in your list?</span><button type='button' class='button button-yellow search-online-btn' onclick="+"alert('hii')"+"><span><i class='las la-search'></i></span><span class='btn-text'>search online</span>";
    //   ap.setAttribute("id", this.id + "autocomplete-list");

	//    var search_online="<div class='autocomplete-search-block'><span class='autocomplete-search-label'>Vendor not in your list?</span><button type='button' class='button button-yellow search-online-btn'><span><i class='las la-search'></i></span><span class='btn-text'>search online</span></button></div>";
	// $('#autocompseach').append("<div class='autocomplete-search-block'><span class='autocomplete-search-label'>Vendor not in your list?</span><button type='button' class='button button-yellow search-online-btn'><span><i class='las la-search'></i></span><span class='btn-text'>search online</span></button></div>");
	   
	   /*for each item in the array...*/
      for (i = 0; i < arr.length; i++) {
        /*check if the item starts with the same letters as the text field value:*/
        if (arr[i].substr(0, val.length).toUpperCase() == val.toUpperCase()) {
			$('.autocomplete-search-block').remove();

          /*create a DIV element for each matching element:*/
          b = document.createElement("DIV");
          /*make the matching letters bold:*/
          b.innerHTML = "<strong>" + arr[i].substr(0, val.length) + "</strong>";
          b.innerHTML += arr[i].substr(val.length);
          /*insert a input field that will hold the current array item's value:*/
          b.innerHTML += "<input type='hidden' value='" + arr[i] + "'>";
          /*execute a function when someone clicks on the item value (DIV element):*/
          b.addEventListener("click", function(e) {
              /*insert the value for the autocomplete text field:*/
              inp.value = this.getElementsByTagName("input")[0].value;
              /*close the list of autocompleted values,
              (or any other open lists of autocompleted values:*/
              closeAllLists();
          });
		  a.appendChild(b);
		  
		  
			
		}else{

			if($('.autocomplete-search-block').length==0){
	
				// this.parentNode.append(ap);
			   
			  }
		}
	}
			if($('.autocomplete-search-block').length==0){
	
				// b.after(ap);
			   
			  }
  });
 
  /*execute a function presses a key on the keyboard:*/
  inp.addEventListener("keydown", function(e) {
      var x = document.getElementById(this.id + "autocomplete-list");
      if (x) x = x.getElementsByTagName("div");
      if (e.keyCode == 40) {
        /*If the arrow DOWN key is pressed,
        increase the currentFocus variable:*/
        currentFocus++;
        /*and and make the current item more visible:*/
        addActive(x);
      } else if (e.keyCode == 38) { //up
        /*If the arrow UP key is pressed,
        decrease the currentFocus variable:*/
        currentFocus--;
        /*and and make the current item more visible:*/
        addActive(x);
      } else if (e.keyCode == 13) {
        /*If the ENTER key is pressed, prevent the form from being submitted,*/
        e.preventDefault();
        if (currentFocus > -1) {
          /*and simulate a click on the "active" item:*/
          if (x) x[currentFocus].click();
        }
      }
  });
  function addActive(x) {
    /*a function to classify an item as "active":*/
    if (!x) return false;
    /*start by removing the "active" class on all items:*/
    removeActive(x);
    if (currentFocus >= x.length) currentFocus = 0;
    if (currentFocus < 0) currentFocus = (x.length - 1);
    /*add class "autocomplete-active":*/
    x[currentFocus].classList.add("autocomplete-active");
  }
  function removeActive(x) {
    /*a function to remove the "active" class from all autocomplete items:*/
    for (var i = 0; i < x.length; i++) {
      x[i].classList.remove("autocomplete-active");
    }
  }
  function closeAllLists(elmnt) {
    /*close all autocomplete lists in the document,
    except the one passed as an argument:*/
    var x = document.getElementsByClassName("autocomplete-items");
    for (var i = 0; i < x.length; i++) {
      if (elmnt != x[i] && elmnt != inp) {
        x[i].parentNode.removeChild(x[i]);
      }
    }
  }
  /*execute a function when someone clicks in the document:*/
  document.addEventListener("click", function (e) {
      closeAllLists(e.target);
  });
}

/*An array containing all the country names in the world:*/
// var countries = ["Afghanistan","Albania","Algeria","Andorra","Angola","Anguilla","Antigua & Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia & Herzegovina","Botswana","Brazil","British Virgin Islands","Brunei","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Cayman Islands","Central Arfrican Republic","Chad","Chile","China","Colombia","Congo","Cook Islands","Costa Rica","Cote D Ivoire","Croatia","Cuba","Curacao","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Falkland Islands","Faroe Islands","Fiji","Finland","France","French Polynesia","French West Indies","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guam","Guatemala","Guernsey","Guinea","Guinea Bissau","Guyana","Haiti","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kiribati","Kosovo","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macau","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Mauritania","Mauritius","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Myanmar","Namibia","Nauro","Nepal","Netherlands","Netherlands Antilles","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","North Korea","Norway","Oman","Pakistan","Palau","Palestine","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Puerto Rico","Qatar","Reunion","Romania","Russia","Rwanda","Saint Pierre & Miquelon","Samoa","San Marino","Sao Tome and Principe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Korea","South Sudan","Spain","Sri Lanka","St Kitts & Nevis","St Lucia","St Vincent","Sudan","Suriname","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Timor L'Este","Togo","Tonga","Trinidad & Tobago","Tunisia","Turkey","Turkmenistan","Turks & Caicos","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","United States of America","Uruguay","Uzbekistan","Vanuatu","Vatican City","Venezuela","Vietnam","Virgin Islands (US)","Yemen","Zambia","Zimbabwe"];
	
	
var customermobi = ["8452805317","8452784567","9833367483","9833987624","8450821357","840821367","9833666083","9833660124"];
var patientmobi = ["8452805317","8452784567","9833367483","9833987624","8450821357","840821367","9833666083","9833660124"];

/*initiate the autocomplete function on the "myInput" element, and pass along the countries array as possible autocomplete values:*/
/*autocomplete(document.getElementById("myInput"), countries);*/
// autocomplete(document.getElementById("autocompseach"), countries);
	
if ($('#customermob').length != 0) {	
autocomplete(document.getElementById("customermob"), customermobi);
}
	if ($('#patientmob').length != 0) {
autocomplete(document.getElementById("patientmob"), patientmobi);
	}

  
}



function openNav() {
    document.getElementById("mySidenav").style.width = "250px";
    document.getElementById("topbar").style.marginLeft = "190px";
    document.getElementById("wrapper").style.marginLeft = "190px";
    document.getElementById("hamburger_label").style.marginLeft = "178px";
    document.getElementById("header_logo").style.width = "250px";
    console.log("opennav");
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0px";
    document.getElementById("topbar").style.marginLeft = "0px";
    document.getElementById("wrapper").style.marginLeft = "0px";
    document.getElementById("header_logo").style.width = "70px";
    document.getElementById("hamburger_label").style.marginLeft = "0px";
}

function closeMenu() {
    $("#navigation").removeClass("open");
    closeNav();
    console.log("closemenu")
}

function openMenu() {
    $("#navigation").addClass("open");
    console.log("openmenu")
    openNav();
}

function leftMousePressed(e) {
    e = e || window.event;
    var button = e.which || e.button;
    return button == 1;
}
$(document.body).mouseup(function (e) {
    var subjectdiv = $("#navigation");
    console.log("mouse up");
    // console.log($(event.target).attr('class'));
    // console.log(event.target.id);
    // console.log(event.target);
    if (leftMousePressed(e)) {
        if (e.target.id == "circle" || e.target.id == "line_nav_3" || e.target.id == "line_nav_2" || e.target.id == "line_nav_1" || e.target.id == "svg_nav") {
            console.log("e.t.id")
            if ($("#navigation").hasClass("open")) {
                closeMenu();
            }
            else {
                openMenu();
            }
        }
        else if (e.target.id == "wrapper") {
            if ($("#navigation").hasClass("open")) {
                closeMenu();
                $('#hamburger_label').click()
            }
        }
    }
});
$(".scroll").click(function (event) {
    event.preventDefault();
    if ($("#navigation").hasClass("open")) {
        closeMenu();
        $('#hamburger_label').click()
    }
    else {
        openMenu();
        $('#hamburger_label').click()
    }
});

function OnFocusOut1(){
	$('#focusguard-2').on('focus', function() {
		  $('#barcode').focus();
		  //alert("focusguard-2")

		});

		$('#focusguard-1').on('focus', function() {
		  $('#btn').focus();
		});
		$(function() { 

		$('.autofocus').focus();
		})
	}

function OnFocusOut(){
	$('#focusguard-2').on('focus', function() {
		  $('#firstInput').focus();
		  //alert("focusguard-2")

		});

		$('#focusguard-1').on('focus', function() {
		  $('#lastInput').focus();
		});
		$(function() { 

		$('.autofocus').focus();
		})
	}
// .main-screen-panel.main-topsection.main-content.main-footer.main-right-sidebar
function change_height() {
    if($('.main-topsection').length>0 && $('.mainthead').length>0 && $('.main-footer').length>0 && $('.wrapper').length>0 &&  $('.dataTables_scrollBody').length>0){
    

    var inner_height = window.innerHeight;
    var input_height = parseInt($('.main-topsection').height());
    var thead_height = parseInt($('.mainthead').height());
    var bill_button_height = parseInt($('.main-footer').outerHeight());
    var wrapper_paddin_top = parseInt($('.wrapper').css('padding-top'));
    console.log("input height" + input_height + "wrapper padding-top" + wrapper_paddin_top + 'theadheight' + thead_height + 'billbuttona' + bill_button_height+'inner height '+inner_height);
    console.log(parseInt(wrapper_paddin_top));
    console.log(wrapper_paddin_top + thead_height + input_height + bill_button_height);
    console.log(inner_height)
    var max_height = inner_height - (wrapper_paddin_top + thead_height + input_height + bill_button_height);
    console.log(max_height)
    $('.dataTables_scrollBody').css('max-height', max_height);

    }


    if($('.bill-detailscol').length>0){
         var bill_col_height = parseInt($('.bill-detailscol:first-child').outerHeight());
    var bill_btn_height = parseInt($('.checkoutrow').outerHeight());
    console.log(" col" + bill_col_height + ' btn' + bill_btn_height);
    var bill_height = inner_height - wrapper_paddin_top - bill_col_height - bill_btn_height-50
    console.log("bill height"+ bill_height+"inner height"+inner_height +"wrapperpdtp"+ wrapper_paddin_top +"bill_col_hght"+ bill_col_height +"bill btn height"+bill_btn_height)
    $('.bill-detailscol:nth-child(2)').css('height', bill_height);
    }
   
}
$(window).on('ready', change_height);
$(window).resize(change_height)