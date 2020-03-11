(function() {
	if (typeof message != 'undefined' && message) {
	    var msg = message;
	}else{
		return null;
	};
	        var options = {
	        		data: msg,
	 			    getValue: function (element) {
	 				    return element.vendorName + " | " + element.GSTIN;
	 				   alert("vendorName---"+element.vendorName);
	 				 },
	 		   list: {
	 			 match: {
	 		            enabled: true
	 		        },
	 			onSelectItemEvent: function(){
	 	            var name = $("#vendorName").getSelectedItemData().vendorName;
	 	            var address_1 = $("#vendorName").getSelectedItemData().states;
	 	            var address_2 = $("#vendorName").getSelectedItemData().city;
	 	            var address_3 = $("#vendorName").getSelectedItemData().states;
	 	            var state = $("#vendorName").getSelectedItemData().states;
	 	            var city = $("#vendorName").getSelectedItemData().city;
	 	            var pin_code = $("#vendorName").getSelectedItemData().pinCode;
	 	            var landline = $("#vendorName").getSelectedItemData().landline;
	 	            var email_address = $("#vendorName").getSelectedItemData().email;
	 	            var fssai = $("#vendorName").getSelectedItemData().FSSAINumber;
	 	            var contact_person = $("#vendorName").getSelectedItemData().contactPerson;
	 	            var mobile = $("#vendorName").getSelectedItemData().mobile;
	 	            var pan = $("#vendorName").getSelectedItemData().panNumber;
	 	            var gstin = $("#vendorName").getSelectedItemData().GSTIN;
	 	            var gstin_type = $("#vendorName").getSelectedItemData().GSTINType;
	 	            var fssai_date = $("#vendorName").getSelectedItemData().FSSAIExpiryDate;
	 	            var partyIdTo = $("#vendorName").getSelectedItemData().partyId;
	 	           
	 	            
	 	            $("#vendor_name").val(name);
	 	            $("#address_1").val(address_1);
	 	            $("#address_2").val(address_2);
	 	            $("#address_3").val(address_3);
	 	            $("#pin_code").val(pin_code);
	 	            $("#landline").val(landline);
	 	            $("#email_address").val(email_address);
	 	            $("#fssai").val(fssai);
	 	            $("#contact_person").val(contact_person);
	 	            $("#mobile").val(mobile);
	 	            $("#pan").val(pan);
	 	            $("#gstin_no").val(gstin);
	 	            $("#fssai_expiry_date").val(fssai_date);
	 	            $("#gstin select").val(gstin_type);
	 	            $("#gstin").val(gstin_type).change();
	 	            $("#sts").val(state).change();
	 	            $("#state").val(city ).change();
	 	            $("#pan").val(pan);
	 	            $('#party_to').val(partyIdTo);
	 	            
	 				console.log('this is on select event'+name);
	 			}
	 		},

	 		template: {
	 			type: "custom",
	 			method: function(value, item) {
	 				console.log("gggggg"+value.length);
	 				if(value !== " "){
	 					return  value;
	 				}else{
	 					return "<button class='btn btn-primary pull-right' data-toggle='modal' data-target='#searchVendorModel'>Search Online</button>";
	 				}
	 			}
	 		}
	 	};

	 	$("#vendorName").easyAutocomplete(options);
	})();
	
	
$(function() {	  
	 $("#vender_register").validate({
		    rules: {
		      vendorname: "required",
		      address1: "required",
		      address2: "required",
		      mobile: "required",
		      country: "required",
		      pincode: "required",
		      gstin: "required",
		      stt: "required",
		      city: "required",
		      landline: "required",
		      email : {
		    	  required: true,
		    	  email: true,
		      },
		      pan: "required",
		      gstin: "required",
		      fssai: "required",
		      exp_date: "required",
		      contact_person: "required",
		    },
		messages : {
			vendorname : {
				required : '<span style="color: #A94545;"><strong>Vendor Name is required. </strong></span>',
			},
			address1 : {
				required : '<span style="color: #A94545;"><strong>Address is required. </strong></span>',
			},
			pincode : {
				required : '<span style="color: #A94545;"><strong>Pin Code is required. </strong></span>',
			},
			mobile : {
				required : '<span style="color: #A94545;"><strong>Mobile number is required. </strong></span>',
			},
			gstin : {
				required : '<span style="color: #A94545;"><strong>GSTIN is required. </strong></span>',
			},
			landline : {
				required : '<span style="color: #A94545;"><strong>This is required. </strong></span>',
			},
			pan : {
				required : '<span style="color: #A94545;"><strong>PAN is required. </strong></span>',
			},
			city : {
				required : '<span style="color: #A94545;"><strong>City is required. </strong></span>',
			},
			stt : {
				required : '<span style="color: #A94545;"><strong>State is required. </strong></span>',
			},
			fssai : {
				required : '<span style="color: #A94545;"><strong>FSSAI is required. </strong></span>',
			},
			email : {
				required : '<span style="color: #A94545;"><strong>Email is required. </strong></span>',
				email : '<span style="color: #A94545;"><strong>Enter a valid email. </strong></span>',
			},
			exp_date : {
				required : '<span style="color: #A94545;"><strong>Date is required. </strong></span>',
			},
			contact_person : {
				required : '<span style="color: #A94545;"><strong>Contact Person is required. </strong></span>',
			},
			
		},
		errorClass : 'has-error',
		validClass : 'has-success',
		submitHandler : function(form) {
			//$('#register_button').attr('disabled', true);
			var postData = {
			    partyIdFrom : partyId,
			    partyIdTo : $('#party_to').val(),
			};
			alert("partyIdFrom-----"+$('#partyId').val());
			alert("partyto-----"+$('#party_to').val());
			var jsondata = JSON.stringify(postData);
			console.log('console-data::'+jsondata);
			$.ajax({
				type: 'GET',
		        url: ui_url+'partyMapping',
		        data: {jsondata:jsondata},
		        dataType: 'json',
		        contentType: 'application/json',
				success : function(response) {
			          console.log(response.message)
					if (response.message == "Success") {
						alert("success--");
						console.log('success');
						$('#confirmationModal').modal('toggle');
						$(".modal").on("hidden.bs.modal", function () {
							window.location = ui_url+'vendorItemMapping?userID=' + response.partyIdTo;
						});
						
					} else {
						console.log('Failed');

						$('#alreadyExistModal').modal('toggle');
						$("#msgtext").append(response.message);
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log(errorThrown);
				}
			})
		}
	});
});

//Render json data in datatable
$("#selectVendorTable").DataTable({
	scrollY:        300,
    scrollX:        true,
    scrollCollapse: true,
    paging:         false,
    ordering:        false,
    bInfo :          false,
    
     fixedColumns:   {
        leftColumns: 2
    }, 
  
    select: {
        style:    'os',
        selector: 'td:first-child'
    },
	  "aaData":[
	    ["Sitepoint","Delhi","785412369dd","9874561230"],
	    ["Flippa","Noida","78945632104r","7894563201"],
	    ["99designs","Gurgaon","5469874231ff","8523697410"],
	    ["Learnable","Greater Noida","4564564844fd","7894561230"],
	    ["Rubysource","Noida","4563217890df","8965741230"]
	  ],
	  "aoColumnDefs":[{
	        "sTitle":"POS System",
	  },{
	        "bSortable": false,
	        "mRender": function (type, full )  {
	      }
	  }]
	});
//render data in the form	
$("td:first-child").click(function() {
    console.log('tttttt'+$(this).closest('tr').children('td:first-child').text());
    $("#vendor_name").val($(this).closest('tr').children('td:first-child').text());
    $("#address_1").val($(this).closest('tr').children('td:nth-child(2)').text());
    $("#address_2").val($(this).closest('tr').children('td:nth-child(2)').text());
    $("#address_3").val($(this).closest('tr').children('td:nth-child(2)').text());
    $("#mobile").val($(this).closest('tr').children('td:last-child').text());
    $("#landline").val($(this).closest('tr').children('td:last-child').text());
    $("#city").val($(this).closest('tr').children('td:nth-child(2)').text());
    $("#pin_code").val($(this).closest('tr').children('td:last-child').text());
    $("#contact_person").val($(this).closest('tr').children('td:last-child').text());
    $("#email_address").val('vishakha@gmail.com');
    $("#pan").val($(this).closest('tr').children('td:nth-child(3)').text());
    $("#gstin").val($(this).closest('tr').children('td:nth-child(3)').text());
    $("#fssai").val('7894561230852');
    $('#searchVendorModel').modal('hide');
});

$('#mySwitch').prop('checked')
$().val('')


//vendor item mapping js starts here
if (typeof list != 'undefined' && list) {
	    var datalist = list;
	}else{
		var datalist = null;
	};
var item_data = datalist;
var data1 = JSON.stringify(item_data);
$(function() {	  
	var url_string = window.location.href; 
	var url = new URL(url_string);
	var userId = url.searchParams.get("userID");
	$("#vendorName").val(userId);
});

$('#vendorItemMapping').DataTable({
		    scrollY:        400,
		    scrollX:        true,
		    scrollCollapse: true,
		    paging:         false,
		    ordering:        false,
		    bInfo :          false,
		    
		    select: {
		    	style: 'multi',
		        selector: 'tr'
		  },
		  
		  "data": item_data,
		  "columns": [
			  {"data": "partyId", "visible": false,},
			{"data": "productId"},
			{"data": "productName"},
		    {"data": "productTypeId"},
		    {"data": "brandName"},
		    {"data": "lastPrice"},
		    {"data": "lastPrice"},
		    {"data": "internalName"},
		    {"data": "currencyUomId"},
		    {"data": "lastPrice"},
		    {"data": "quantityUomId"},
		    {"data": "minimumOrderQuantity"},
		  ]
	   });

var table = $('#vendorItemMapping').DataTable();

var array =[];

var countr = 0;
$('#vendorItemMapping tbody').on( 'click', 'tr', function () {
    console.log( table.row( this ).data() );
    var selectedData = table.row( this ).data();
    var productId = selectedData.productId;
    var partyIdSupplier = selectedData.partyId;
    
  
  
    /*var postData = {
    		productId : productId,
    		partyIdSupplier : partyIdUser,
    		partyIdUser : partyId
		};*/
  
    array[countr]=selectedData;
   
    countr++;
    
} );

$('#criteria1').on('change', function() {
	//alert( this.value );
	$('#criteria_1').val(this.value);
	$('#criteria_2').val('');
});
$('#criteria2').on('change', function() {
	//alert( this.value );
	$('#criteria_2').val(this.value);
});
//For criteria selections
$(function() {
    var criteria2 = {
        'Cosmetics': ['', 'Nivea', 'Lakme', 'Maybelline', 'Ponds'],
        'Dairy': ['', 'Amul', 'Mother Dairy'],
        'Drinks': ['' ,'Parle', 'Fresca'],
        'Kirana': ['', 'Fortune','Dhara','Rajdhani'],
    }
    
    var $criteria2 = $('#criteria2');
    $('#criteria1').change(function () {
        var criteria1 = $(this).val(), lcns = criteria2[criteria1] || [];
        var html = $.map(lcns, function(lcn){
            return '<option value="' + lcn + '">' + lcn + '</option>'
        }).join('');
        $criteria2.html(html)
    });
});

/*function filterGlobal (cri1) {
	alert(cri1);
    $('#vendorItemMapping').DataTable().search(
    		cri1,
    ).draw();
}*/


$(function() {	  
	 $("#vender_register").validate({
		    rules: {criteria1: "required"},
		    messages : {
		    	criteria1 : {
					required : '<span style="color: #A94545;"><strong>This is required. </strong></span>',
		    	}
		    	},
  submitHandler : function(form) {
	//$('#register_button').attr('disabled', true);
	 val1 = $("#criteria_1").val();
	 val2 = $("#criteria_2").val();
	 filterGlobal(val1);
	}  
});  
});  

$('#submitbtn').on( 'click', function () {
    
	var partyIdSupplier = $('#vendorName').val();
		var jsondata = JSON.stringify(array);
		console.log('console-data::'+jsondata);
		$.ajax({
			type: 'GET',
	        url:ui_url+'createUserSupplierProduct',
	        data: {jsondata:jsondata,partyIdSupplier:partyIdSupplier},
	        dataType: 'json',
	        contentType: 'application/json',
			success : function(response) {
				alert("response"+response);
		          console.log(response)
				if (response.message == "Success") {
					$('#confirmationModal2').modal('toggle');
					$(".modal").on("hidden.bs.modal", function () {
					    window.location = ui_url+'vendorMaster';
					});
					
				} else {
					$('#alreadyExistModal1').modal('toggle');
					$("#msgtext").append(response.message);
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				console.log(errorThrown);
			}
		})
    
    
    
    
} );


$('#criteriaSearchbtn').on( 'click', function () {
    
	   
	var vendorName = $('#vendorName').val();
	var productCategoryId = $('#criteria_1').val();
	var manufacturerId = $('#criteria_2').val();
	var partyId ="";
	var primaryProductCategoryId ="";
	var manufacturerPartyId ="";
	var postData = {
			partyId : vendorName,
			primaryProductCategoryId : productCategoryId,
			manufacturerPartyId : manufacturerId
		}
		var jsondata = JSON.stringify(postData);
		console.log('console-data::'+jsondata);
		$.ajax({
			type: 'GET',
	        url:ui_url+'filterSupplierProduct',
	        data: {jsondata:jsondata},
	        dataType: 'json',
	        contentType: 'application/json',
			success : function(response) {
		          console.log(response)
				populateDataTable(response)
			},
			error : function(jqXHR, textStatus, errorThrown) {
				console.log(errorThrown);
			}
		})
    
    
    
} );

function populateDataTable(data) {

	var result = JSON.parse(JSON.stringify(data));
	table.clear().draw();
	for (var i = 0, l = result.length; i < l; i++) {
		var obj = result[i];
		
		table.row.add({
			"partyId":obj.partyId,
			 "productId":obj.productId,
		 "productName":obj.productName,
		   "productTypeId":obj.productTypeId,
		     "brandName":obj.brandName,
		  "lastPrice":obj.lastPrice,
		  "lastPrice":obj.lastPrice,
		    "internalName":obj.currencyUomId,
		     "currencyUomId":obj.currencyUomId,
		  "lastPrice":obj.lastPrice,
		    "quantityUomId":obj.quantityUomId,
		   "minimumOrderQuantity":obj.minimumOrderQuantity,
			
			
		}).draw(false);	
		
		
		
		

	}

}
/*document.getElementById("approve1").style.display = "none";

document.getElementById("comfirmation1").style.display = "none";

function closeConfirmation() {
	document.getElementById("comfirmation1").style.display = "none";
}

function closeForm() {
	document.getElementById("approve1").style.display = "none";
}*/


$('#go')
.click(
		function() {
			var type = $(
					'#searchByProductName')
					.val();
		
			
			 var oTable = $('#interdependent_transfer').DataTable();
			 oTable.search(type).draw();
			 console.log(oTable);
			

		});
var itemId = "";
var transferQnty = "";
var qty = "";
var facilityId = "";
var facilityIdTo = "";
var password = "";
var username = "";
var comments = "";
var sendDate = "";
var receiveDate = "";

$('.result')
.click(
		function() {
			itemId = $(this).closest('tr')
					.find('.itemId').text();
			var rowIndex = $(this).closest(
					'tr').index();
			var currentRow = $(this)
					.closest("tr");
			
			transferQnty = currentRow.find(
					"td:eq(6)").text();
			

			qty = transferQnty;
			facilityId = $(
					'select[name="department_from"]')
					.val();
			
			facilityIdTo = $(
					'select[name="department_to"]')
					.val();
			comments = $('#comments').val();
			sendDate = $('#transfer_date')
					.val();
			receiveDate = $(
					'#transfer_date').val();
			if (qty != "") {
				openForm();
				
			}

		});

$('#yes')
.click(
		function() {
			
			var data0 = {
				inventoryItemId : itemId,
				xferQty : qty,
				statusId : "IXF_REQUESTED",
				facilityId : facilityId,
				facilityIdTo : facilityIdTo,
				comments : comments,
				sendDate : sendDate,
				receiveDate : receiveDate
			};

			
			var json = JSON
					.stringify(data0);
			$
					.ajax({
						type : 'GET',
						url : ui_url+'transferInventory/create',
						data : {
							json : json
							
						},
						dataType : 'json',
						success : function(
								data) {
							console
							.log(data);
//							if (data.message == "Success") {
//						
//						jQuery.noConflict();
//								$('#confirmationModal1').modal('show');
//							
//						}
							
							if (data.message == "Success") {
								
								$('#confirmationModal2').modal('toggle');
								$(".modal").on("hidden.bs.modal", function () {
								    window.location = ui_url+'listProduct';
								});
								window.location = ui_url+'listProduct';
							} else {
								$('#alreadyExistModal2').modal('toggle');
								$("#msgtext").append(response.message);
							}
						}

					});

		});
function openForm() {
document.getElementById("approve1").style.display = "block";

}
function populateDataTable(data) {

var table = $('#interdependent_transfer')
	.DataTable();
table.clear();
var result = JSON.parse(JSON.stringify(data));
for (var i = 0, l = result.length; i < l; i++) {
var obj = result[i];
$('#interdependent_transfer')
		.dataTable()
		.fnAddData(
				[
						'<span class="delete-span"><i class="fa fa-trash"></i></span>',
						obj.inventoryItemId,
						obj.inventoryItemId,
						'<td><select name="unit_id" class="table-select"><option>EA</option></select></td>',

						obj.productId,
						obj.lotId,
						
						'<td><span class="transfer" contentEditable="true"></span></td>',
						obj.quantityOnHandTotal,
						obj.quantityOnHandTotal,
						obj.unitCost,
						obj.serialNumber,
						obj.expireDate, ]);

}

}

$('#myBody')
.on(
		'click',
		'tr',
		function() {
			var table = $(
					'#interdependent_transfer')
					.DataTable();
			var currentRow = table
					.row(this).data()[4];
			itemId = table.row(this).data()[2];
			/* transferQnty = table.row(this)
					.data()[6]; */
			
			var currentRow = $(this)
			.closest("tr");
	
	transferQnty = currentRow.find(
			"td:eq(6)").text();
	qty=transferQnty;
	
			facilityId = $(
					'select[name="department_from"]')
					.val();
			facilityIdTo = $(
					'select[name="department_to"]')
					.val();
			comments = $('#comments').val();
			sendDate = $('#transfer_date')
					.val();
			receiveDate = $(
					'#transfer_date').val();
			

			if (qty != "") {
				openForm();
				
			}
		});

var myVar = setInterval(myTimer, 1000);

function myTimer() {
var d = new Date();
var t = d.toLocaleString();
//document.getElementById("demo").innerHTML = t;
}

function modalclose() {
	window.location = ui_url+'listProduct';
	$('#confirmationModal2').modal('hide');

}

function openConfirmation() {
	document.getElementById("comfirmation1").style.display = "block";
	document.getElementById("approve1").style.display = "none";
}