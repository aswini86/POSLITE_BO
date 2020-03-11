
function onlyNumbersandSpecialChar(evt) {
    var e = window.event || evt;
    var charCode = e.which || e.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57 || charCode > 107 || charCode > 219 || charCode > 221) && charCode != 40 && charCode != 32 && charCode != 41 && (charCode < 43 || charCode > 46)) {
        if (window.event) //IE
            window.event.returnValue = false;
        else //Firefox
            e.preventDefault();
    }
    return true;
 }

$('#cartredirect').click(
	function() {
		var homeUrl = '/cartlite';
		window.location = homeUrl;
});

Mousetrap.bind('enter',addButton);
Mousetrap.bind('ctrl', mobileNumber);
Mousetrap.bind('ins', addToCart);
Mousetrap.bind('space', addCustomer);
Mousetrap.bind('c', getfocus);
Mousetrap.bind('z', getFirstCartItem)


function goNext() {
	// console.log("Inside goNext Functions")
	// console.log(document.activeElement.id);
	var idx = document.activeElement.id;
	var underscore = idx.indexOf('_');
	var indx = idx.substring(underscore+1,idx.length);
	var index = parseInt(indx);
	index = index + 1;
	// console.log(index);
	var bIndx = idx.substring(0, underscore+1);
	bIndx ='#' + bIndx + index;
	// console.log(bIndx);
	$(bIndx).focus();
	}

function getFirstCartItem(){
	$('#cartBarcode_0').focus();
	}
	$('#cartredirect').click(
	function() {
	var homeUrl = '/cartlite';
	window.location = homeUrl;
	});
	function goPrevious() {
	// console.log("Inside goPrevious Functions")
	// console.log(document.activeElement.id);
	var fndbkn = JSON.parse(cartItems.value);
	console.log(fndbkn[0].productId);
	var idx = document.activeElement.id;
	var underscore = idx.indexOf('_');
	var indx = idx.substring(underscore+1,idx.length);
	var index = parseInt(indx);
	index = index - 1;
//	alert("index---"+index)
	var mrp = $('#mrprice_0').val();
//	alert("mrp---"+mrp)
	// console.log(index);
	var bIndx = idx.substring(0, underscore+1);
	bIndx ='#' + bIndx + index;
	// console.log(bIndx);
	$(bIndx).focus();
	}
	

$('#cartredirect').click(
	function() {
		var homeUrl = '/cartlite';
		window.location = homeUrl;
});

function mobileNumber() {
	 var search = $('#contactNumber');
	 search.val('');
	 search.focus();
	}								
	
function addToCart() {
	var search = $('#barcode');
	search.focus();
	search.val('');
	}

function addTodelete() {
	 var acd = $('#cartBarcode_0');
	 acd.val('');
	 acd.focus();
	}

function Cartproduct() {
	 var search = $('.productdelete');
	 search.val('');
	 search.focus();
	}



		
function addButton(){
			console.log("Inside add btn")
			addProductFunction();
		}
    function addCustomer (){ 
		$('.add-customer').modal('show');}
    
    function holdbill (){ 
		$('.populateHoldBillModal').modal('show');}
	
	
	
	function billingCheckout() {
		var homeUrl = '/cartBilling';
		window.location = homeUrl;
	}
	


	function getfocus() {
		 var btnfocus = $('#billingCheckout');
		 alert("check h,kyiygjgj");
		 console.log("Inside getfocus() ndfjkgbuiwbtu")
		 btnfocus.focus();

		}
	function addDiscount() {
		 var btnfocus = $('#addl_discount');
		 //alert("check disc");
		 //console.log("Inside getfocus() ndfjkgbuiwbtu")
		 btnfocus.focus();

		}
	

	$(document).keydown(function(event) { 
		  if (event.keyCode == 27) { 
		    $('.modal').modal('hide');
		  }
		});


function createCustomer() {
	var enter_name = $('#enter_name').val();
	var email_id = $('#email_id').val();
	var contact_number = $('#contact_number').val();
	var address = $('#address').val();
	var city = $('#city').val();
	var postal_code = $('#postal_code').val();
	var username = $('#createdBy').val();
	var password = "ofbiz";
	var stateProvinceGeoId = "IN-MH";

	var data0 = {
		firstName : enter_name,
		//lastName : enter_name,
		emailAddress : email_id,
		contactNumber : contact_number,
		address1 : address,
		city : city,
		postalCode : postal_code,
		stateProvinceGeoId : stateProvinceGeoId,
		
	}
	var json = JSON.stringify(data0);
	$.ajax({
		type : 'GET',
		url : '/createCustomerBO',
		data : {
			json : json,
			username : username,
			password : password,
		},
		dataType : 'json',
		success : function(data) {
			if (data != "" && data != null) {
				//$('#addNew').modal("hide");
				$('#createCustomerSuccessModal').modal('toggle');
				$("#customerSuccess").append(
						"Customer created Successfully!");
				//code to easy auto populate
				var barcodemsg = data.CustomerList;
				var options = {
		        		data: barcodemsg,
		 			    getValue: function (element) {
		 				    return element.contactNumber;
		 				 },
		 		   list: {
		 			 match: {
		 		            enabled: true
		 		        },
		 			onSelectItemEvent: function(){
		 				var firstName = $("#contactNumber").getSelectedItemData().firstName;
		 				var address1 = $("#contactNumber").getSelectedItemData().address1;
		 				var customerShipState = $("#contactNumber").getSelectedItemData().stateProvinceGeoId;
		 				var contactNumber = $("#contactNumber").getSelectedItemData().contactNumber;
		 				
		 				$("#customerName").val(firstName);
		 				$("#customerAddress").val(address1);
		 				$("#customerShipState").val(customerShipState);
		 				$("#scustomerShipState").val(customerShipState);
		 				$("#bill_customerName").text(firstName);
		 				$("#last_bill_amt").text("00.00");
		 				$("#last_bill_returnamt").text("00.00");
		 				$("#pcontactNumber").val(contactNumber);
		 				$("#pcustomerName").val(firstName);
		 			}
		 		},

		 		template: {
		 			type: "custom",
		 			method: function(value, item) {
		 				if(value !== " "){
		 					return  value;
		 				}else{
		 					return "<button class='btn btn-primary pull-right' data-toggle='modal' data-target='#searchCartModel'>Search Online</button>";
		 				}
		 			}
		 		}
		 	};
			$("#contactNumber").easyAutocomplete(options);
			//Code to end easy auto populate
			} else {
				$('#addNew').modal("hide");
				$('createCustomerErrorModal').modal()
				$("#customerError").append(
						"Error while Creating Customer");
			}
		}
	});
}
		
function addProductFunction(productId) {
	var mrprice = document.getElementById("mrp").value;
    var sellingPrice = document.getElementById("sellingPrice").value;
    
    if(Number(sellingPrice) > Number(mrprice)) {
    	$('#mrperror').modal('toggle');
    } else {
	var barcode = $('#barcode').val();
	var username = $('#createdBy').val();
	var receiptId = $('#receiptId').val();
	var dayId = $('#dayId').val();
	var customerShipState = $('#customerShipState').val();
	var customerMobileNo = $('#contactNumber').val();
	var posTerminalId = $('#posTerminalId').val();
	var productName = $('#productName').val();
	var quantity = $('#quantity').val();
	var mrp = $('#mrp').val();
	var sp = $('#sellingPrice').val();
	
	if(quantity == "" || quantity == " " || quantity == null) {
		quantity = 1;
	}
	
	var data0 = {
		barcode : barcode,
		quantity : quantity,
		receiptId : receiptId,
		posTerminalId : posTerminalId,
		dayId : dayId,
		productName : productName,
		mrp : mrp,
		sp : sp,
		USERNAME : username,
		customerShipState : customerShipState,
		customerMobileNo : customerMobileNo,
	};
	var json = JSON.stringify(data0);
	$.ajax({
		type : 'GET',
		url : '/addPosCartItem',
		dataType : 'json',
		async : 'false',
		data : {
			json : json,
			password : "ofbiz",
			username : "sbadmin"
		},
		success : function(data) {
			var result = JSON.parse(JSON.stringify(data));
			populateCartDataTable(data);
			document.getElementById("ID").disabled = false;
			$('#billingCheckout').prop('disabled', false);
		}
	});
	function populateCartDataTable(data) {
		var table = $('#pos_cart').DataTable();
		table.clear();
		var result = JSON.parse(JSON.stringify(data.cartItems));
	    $('#bill_amt').text(data.subTotal);
		$('#billAmount').val(data.subTotal);
	    $('#discount_amount').text("0.00");
		$('#item_discount').text("0.00");
		$('#additional_discount').text("0.00");
		$('#amount').text(data.displayGrandTotal);
		$('#round_off').text("0.00");
		$('#amount_taxable').text("0.00");
		$('#payable_amount').text(data.displaySPGrandTotal);
		$('#total_item').text(data.cartSize);
		
		var discAmount = parseFloat(data.displayMRPGrandTotal) - parseFloat(data.displaySPGrandTotal);
		
		//var discPer = Math.round((data.displayOrgGrandTotal - data.displaySPGrandTotal)/(data.displayGrandTotal)*(100));
		$('#discount_amount').text(discAmount);
		$('#billDiscAmount').val(discAmount);
		for (var j = 0, l = result.length; j < l; j++) {
			var obj = result[j];
		    var itemtax = JSON.parse(JSON.stringify(obj.itemTax));
		    var obj1 = itemtax;
			var mrp = parseFloat(obj.mrp);
			var cartAmount=parseFloat(obj.productQty) * parseFloat(obj.productPrice);
			var name = obj.productName;
			var price = "0";
			var cgst = "0";
			var sgst = "0";
			
			if(obj.productTotalAmt != "" || obj.productTotalAmt != " " || obj.productTotalAmt != null) {
				price=parseFloat(obj.productTotalAmt);
			}
			if(obj1.CGST_TAX != "" || obj1.CGST_TAX != " " || obj1.CGST_TAX != null) {
				cgst=parseFloat(obj1.CGST_TAX);
			}
			if(obj1.SGST_TAX != null) {
				sgst=parseFloat(obj1.SGST_TAX);
			}
			//var price=parseFloat(obj.productTotalAmt);
			//var cgst=parseFloat(obj1.CGST_TAX);
			//var gst=parseFloat(obj1.SGST_TAX);
			var total=parseFloat(price)+parseFloat(cgst)+parseFloat(sgst);
			total=Math.round(total);
			var name = obj.productName;
			name=name.replace(/ /g,"\u00A0");//code to add whitespace
			$('#pos_cart').dataTable().fnAddData([
				'<input type="text" name="Bar Code" id="cartBarcode_'+j+'" value='+obj.barcode+' readonly>',
				'<input type="text" name="Product Name" id="cartproductName_'+j+'" value='+name+' readonly style="width:209px;">',//Product Description
				'<input type="text" name="cartQuantity" id="cartquantity_'+j+'" value='+obj.productQty+'>',
				'<input type="text" name="mrp" id="mrprice_'+j+'" value='+obj.mrp+' readonly>',//Sale Rate
				//obj.productPrice,//Qty
				'<input type="text" name="Sp" id="cartprice_'+j+'" value='+obj.productPrice+' readonly>',
				cartAmount,//Amount
				'<td><input type="button" style="border-radius: 16px;height: 32px;margin-left: 0px; width:"0px; margin-top:6px; position:"relative" onclick="deleteproduct('+obj.productId+')" ><img style="position: relative;margin-top: -45px; margin-left:4px;"src="assets/images/trash.png"></input></td>',
				]);
			$('#barcode').val("");
			$('#productName').val("");
			$('#quantity').val("");
			$('#mrp').val("");
			$('#sellingPrice').val("");
			$('#amount').val("");
		}
	}
	}//else
}
		
		function populateholdBill()
		{
			var username = $('#createdBy').val();
			var dayId = $('#dayId').val();
			var posTerminalId = $('#posTerminalId').val();
			
			var data0 = {
					posTerminalId : posTerminalId,
					dayId : dayId,
					USERNAME : username,
				};
			var json = JSON.stringify(data0);
			$.ajax({
				type : 'GET',
				url : '/getDayPosCartItems',
				dataType : 'json',
				async : 'false',
				data : {
					json : json,
					password : "ofbiz",
					username : "sbadmin"
				},
				success : function(data) {
					var result = JSON.parse(JSON.stringify(data));
				}
			});
			
			
		}
		
		
		
		function holdBill()
		{
			var username = $('#createdBy').val();
			var receiptId = $('#receiptId').val();
			var dayId = $('#dayId').val();
			var customerShipState = $('#customerShipState').val();
			var customerMobileNo = $('#contactNumber').val();
			var posTerminalId = $('#posTerminalId').val();
			
			var data0 = {
					posTerminalId : posTerminalId,
					receiptId : receiptId,
					dayId : dayId,
					USERNAME : username,
					posPaidAmount : "0",
					customerShipState: customerShipState,
					paymentType: "CASH",
				};
			
			
			var json = JSON.stringify(data0);
			$.ajax({
				type : 'GET',
				url : '/holdBill',
				dataType : 'json',
				async : 'false',
				data : {
					json : json,
					password : "ofbiz",
					username : "sbadmin"
				},
				success : function(data) {
					
					$('#pos_cart').DataTable().clear().draw();
					$('#bill_amt').text("0.00");
					$('#item_discount').text("0.00");
					$('#additional_discount').text("0.00");
					$('#discount_amount').text("0.00");
					$('#amount').text("0.00");
					$('#round_off').text("0.00");
					$('#amount_taxable').text("0.00");
					$('#payable_amount').text("0.00");
					$('#total_item').text("0.00");
					$('#receiptId').val(data.receiptId);
					$('#preceiptId').val(data.receiptId);
  					$('#billNum').text(data.receiptId);
  					
  					$('#contactNumber').val('');
  					$('#customerName').val('');
  					$('#customerAddress').val('');
  					var homeUrl = '/cartlite';
  					window.location = homeUrl;
					//$('#pos_cart tbody').empty();
  					/*var d = new Date();
  					var n = d.getTime();
  					$('#receiptId').val(n);
  					$('#billNum').text(n); */
  					
				}
			});
			
			
		}
		
		//Void Bill
		function voidBill()
		{
			var username = $('#createdBy').val();
			var receiptId = $('#receiptId').val();
			var dayId = $('#dayId').val();
			var customerShipState = $('#customerShipState').val();
			var contactNumber = $('#contactNumber').val();
			var posTerminalId = $('#posTerminalId').val();
			var data0 = {
					posTerminalId : posTerminalId,
					receiptId : receiptId,
					dayId : dayId,
					contactNumber : contactNumber,
					USERNAME : username,
				};
			
			
			var json = JSON.stringify(data0);
			$.ajax({
				type : 'GET',
				url : '/voidBill',
				dataType : 'json',
				async : 'false',
				data : {
					json : json,
					password : "ofbiz",
					username : "sbadmin"
				},
				success : function(data) {
					var result = JSON.parse(JSON.stringify(data));
					populateVoidDataTable(data);
				}
			});
			
			function populateVoidDataTable(data) {
				$('#pos_cart').DataTable().clear().draw();
				if (typeof data.cartItems == 'undefined'){
					$('#pos_cart tbody').empty();
					$('#bill_amt').text("0.00");
					$('#item_discount').text("0.00");
					$('#additional_discount').text("0.00");
					$('#discount_amount').text("0.00");
					$('#amount').text("0.00");
					$('#round_off').text("0.00");
					$('#amount_taxable').text("0.00");
					$('#payable_amount').text("0.00");
					$('#total_item').text("0.00");
					$('#addl_discount').val("0.00");
					$('#charges').val("0.00");
					$('#billAmount').val("");
					$('#billDiscAmount').val("");
					$('#contactNumber').val('');
					$('#customerName').val('');
					$('#customerAddress').val('');
					var homeUrl = '/cartlite';
					window.location = homeUrl;
				} else {
				var result = JSON.parse(JSON.stringify(data.cartItems));
				
				    $('#bill_amt').text(data.subTotal);
					$('#item_discount').text("0.00");
					$('#additional_discount').text("0.00");
					$('#discount_amount').text("0.00");
					$('#amount').text(data.displayGrandTotal);
					$('#round_off').text("0.00");
					$('#amount_taxable').text("0.00");
					$('#payable_amount').text(data.displayGrandTotal);
					$('#total_item').text(data.cartSize);
				
				for (var j = 0, l = result.length; j < l; j++) {
					var obj = result[j];
					var price = "";
					var cgst = "";
					var gst = "";
					var total = "";
					
					var itemtax = JSON.parse(JSON.stringify(obj.itemTax));
					var obj1 = itemtax; 
					price=parseFloat(obj1.productTotalAmt);
					cgst=parseFloat(obj1.CGST_TAX);
					gst=parseFloat(obj1.SGST_TAX);
					total=price+cgst+gst;
					
					total=Math.round(total);
					
					$('#pos_cart').dataTable().fnAddData([
						'<input type="text" name="Bar Code" id="cartBarcode_'+j+'" value='+obj.barcode+' readonly>',
						'<input type="text" name="Product Name" id="cartproductName_'+j+'" value='+obj.productName+' readonly style="width:209px;">',//Product Description
						'<input type="text" name="cartQuantity" id="cartquantity_'+j+'" value='+obj.productQty+'>',
						'<input type="text" name="mrp" id="mrprice_'+j+'" value='+obj.mrp+' readonly>',//Sale Rate
						//obj.productPrice,//Qty
						'<input type="text" name="Sp" id="cartprice_'+j+'" value='+obj.productPrice+' readonly>',
						cartAmount,//Amount
						'<td><span onclick="deleteproduct('+obj.productId+')" class="btn-icon button-delete button-red"><i class="fa fa-trash"></i></span></td>',
						]);
				}
				}
			}
		}
		
		function deleteproduct(productId)
		{
			var username = $('#createdBy').val();
			var receiptId = $('#receiptId').val();
			var dayId = $('#dayId').val();
			var customerShipState = $('#customerShipState').val();
			var customerMobileNo = $('#contactNumber').val();
			var posTerminalId = $('#posTerminalId').val();
			var data0 = {
					delete_product_id : productId,
					posTerminalId : posTerminalId,
					receiptId : receiptId,
					dayId : dayId,
					USERNAME : username,
					customerShipState : customerShipState,
					customerMobileNo : customerMobileNo,
				};
			
			var json = JSON.stringify(data0);
			$.ajax({
				type : 'GET',
				url : '/deletePosCartItem',
				dataType : 'json',
				async : 'false',
				data : {
					json : json,
					password : "ofbiz",
					username : "sbadmin"
				},
				success : function(data) {
					var result = JSON.parse(JSON.stringify(data));
					populateDataTable1(data);
				}
			});
			
			function populateDataTable1(data) {
				var table = $('#pos_cart').DataTable();
				table.clear();
				if (typeof data.cartItems == 'undefined'){
					table.clear();
					$('#pos_cart tbody').empty();
					$('#bill_amt').text("0.00");
					$('#item_discount').text("0.00");
					$('#additional_discount').text("0.00");
					$('#discount_amount').text("0.00");
					$('#amount').text("0.00");
					$('#round_off').text("0.00");
					$('#amount_taxable').text("0.00");
					$('#payable_amount').text("0.00");
					$('#total_item').text("0.00"); 
					$('#addl_discount').val("0.00");
					$('#charges').val("0.00");
					var homeUrl = '/cartlite';
					window.location = homeUrl;
				} else {
				var result = JSON.parse(JSON.stringify(data.cartItems));
				
				    $('#bill_amt').text(data.subTotal);
					$('#item_discount').text("0.00");
					$('#additional_discount').text("0.00");
					$('#discount_amount').text("0.00");
					$('#amount').text(data.displayGrandTotal);
					$('#round_off').text("0.00");
					$('#amount_taxable').text("0.00");
					$('#payable_amount').text(data.displayGrandTotal);
					$('#total_item').text(data.cartSize);
					if(data.subTotal == "0") {
						$('#addl_discount').val("0.00");
						$('#charges').val("0.00");
					}
					
				for (var j = 0, l = result.length; j < l; j++) {
					var obj = result[j];
					var price = "";
					var cgst = "0.0";
					var sgst = "0.0";
					var igst = "0.0";
					var total = "";
					var itemtax = JSON.parse(JSON.stringify(obj.itemTax));
					var obj1 = itemtax;
					price=parseFloat(obj.productTotalAmt);
					
					if(obj1.CGST_TAX != undefined) {
						cgst=parseFloat(obj1.CGST_TAX);
					} else {
						cgst=parseFloat(cgst);
					}
					if(obj1.SGST_TAX != undefined) {
						sgst=parseFloat(obj1.SGST_TAX);
					} else {
						sgst=parseFloat(sgst);
					}
					if(obj1.ISGST_TAX != undefined) {
						igst=parseFloat(obj1.ISGST_TAX);
					} else {
						igst=parseFloat(igst);
					}
					
					var total=parseFloat(price)+parseFloat(cgst)+parseFloat(sgst)+parseFloat(igst);
					
					total=Math.round(total);
					var cartAmount=parseFloat(obj.productQty) * parseFloat(obj.productPrice);
					var name = obj.productName;
					name = name.replace(/ /g,"\u00A0");//code to add whitespace
					$('#pos_cart').dataTable().fnAddData([
						'<input type="text" name="Bar Code" id="cartBarcode_'+j+'" value='+obj.barcode+' readonly>',
						'<input type="text" name="Product Name" id="cartproductName_'+j+'" value='+name+' readonly style="width:209px;">',//Product Description
						'<input type="text" name="cartQuantity" id="cartquantity_'+j+'" value='+obj.productQty+'>',
						'<input type="text" name="mrp" id="mrprice_'+j+'" value='+obj.mrp+' readonly>',//Sale Rate
						//obj.productPrice,//Qty
						'<input type="text" name="Sp" id="cartprice_'+j+'" value='+obj.productPrice+' readonly>',
						cartAmount,//Amount
						'<td><input type="button" style="border-radius: 16px;height: 32px;margin-left: 0px; width:"0px; margin-top:6px; position:"relative" onclick="deleteproduct('+obj.productId+')" ><img style="position: relative;margin-top: -45px; margin-left:4px;"src="assets/images/trash.png"></input></td>',
						]);
				}
				}
			}
		}
	//Code for adding product on go	
	function changeSellingPrice() {
		var mrp = $('#mrp').val();
		var sellingPrice = $('#sellingPrice').val();
		var quantity = $('#quantity').val();
		
		if(quantity == "" || quantity == " " || quantity == null) {
			quantity = 1;
		}
		if(Number(sellingPrice) > Number(mrp)) {
			$('#mrperror').modal('toggle');
		}
		sellingPrice = sellingPrice * quantity;
		$('#amount').val(sellingPrice);
	}
	function quantityChange() {
		var mrp = $('#mrp').val();
		var sellingPrice = $('#sellingPrice').val();
		var quantity = $('#quantity').val();
		
		if(quantity == "" || quantity == " " || quantity == null) {
			quantity = 1;
		}
		if(sellingPrice == "" || sellingPrice == " " || sellingPrice == null) {
			sellingPrice = 0;
		}
		if(Number(sellingPrice) > Number(mrp)) {
			$('#mrperror').modal('toggle');
		}
		sellingPrice = sellingPrice * quantity;
		$('#amount').val(sellingPrice);
	}
	//End of js code for adding product on the go
	function formatDate(date) {
	      var d = new Date(date),
	          month = '' + (d.getMonth() + 1),
	          day = '' + d.getDate(),
	          year = d.getFullYear();

	      if (month.length < 2) 
	          month = '0' + month;
	      if (day.length < 2) 
	          day = '0' + day;

	      return [day, month, year].join('/');
	  }
	$(document).ready(function(){
		var d = new Date();
	    $('#transfer_date').val(formatDate(d));
	    
		var cartItems = $('#cartItems').val();
		if(cartItems == "" || cartItems == " " || cartItems == null) {
			$('#billingCheckout').prop('disabled', true);
		} else {
			$('#billingCheckout').prop('disabled', false);
		}
		$("#pos_cart").on("change","tr", function () {
			var table = $('#pos_cart').DataTable();
			var count = table.rows().count();
			var index = table.row(this).index();
			
			var barcode = $('#cartBarcode_'+index).val();
			var username = $('#createdBy').val();
			var receiptId = $('#receiptId').val();
			var dayId = $('#dayId').val();
			var customerShipState = $('#customerShipState').val();
			var customerMobileNo = $('#contactNumber').val();
			var posTerminalId = $('#posTerminalId').val();
			var productName = $('#cartproductName_'+index).val();
			var price = $('#cartprice_'+index).val();
			var mrprice = $('#mrprice_'+index).val();
			
			var quantity = $('#cartquantity_'+index).val();
			if(Number(price) > Number(mrprice)) {
				$('#mrperror').modal('toggle');
			}else
				
				var data0 = {
					barcode : barcode,
					quantity : quantity,
					receiptId : receiptId,
					posTerminalId : posTerminalId,
					dayId : dayId,
					productName : productName,
					price : price,
					USERNAME : username,
					customerShipState : customerShipState,
					customerMobileNo : customerMobileNo,
				};
				var json = JSON.stringify(data0);
				$.ajax({
					type : 'GET',
					url : '/updatePosCartItem',
					dataType : 'json',
					async : 'false',
					data : {
						json : json,
						password : "ofbiz",
						username : "sbadmin"
					},
					success : function(data) {
						var result = JSON.parse(JSON.stringify(data));
						populateDataTable(data);
					}
				});
				function populateDataTable(data) {
					var table = $('#pos_cart').DataTable();
					table.clear();
					var result = JSON.parse(JSON.stringify(data.cartItems));
					    $('#bill_amt').text(data.subTotal);
						$('#discount_amount').text("0.00");
						$('#net_amount').text(data.displaySPGrandTotal);
						$('#round_off').text("0.00");
						$('#amount_taxable').text("0.00");
						$('#payable_amount').text(data.displaySPGrandTotal);
						$('#cart_size').text(data.totalQuantity);
						$('#discount_per').text(data.discPer);
						$('#discount_amount').text(data.discAmount);
						$('#amountCash').val(data.totalDue);
						
					for (var j = 0, l = result.length; j < l; j++) {
						var obj = result[j];
					 
					 var itemtax = JSON.parse(JSON.stringify(obj.itemTax));
						var obj1 = itemtax; 
						var price=parseFloat(obj.productTotalAmt);
						var mrpprice=parseFloat(obj.productOriginalPrice);
						var mrp = parseFloat(obj.mrp);
						var cgst=parseFloat(obj1.CGST_TAX);
						var gst=parseFloat(obj1.SGST_TAX);
						var total=mrpprice+cgst+gst;
						total=Math.round(total);
						var cartAmount= parseFloat(obj.productQty) * parseFloat(obj.productPrice);
						var name = obj.productName;
						name = name.replace(/ /g,"\u00A0");//code to add whitespace
						//alert(obj1.CGST_TAX);
						$('#pos_cart').dataTable().fnAddData([
							'<input type="text" name="Bar Code" id="cartBarcode_'+j+'" value='+obj.barcode+' readonly>',
							'<input type="text" name="Product Name" id="cartproductName_'+j+'" value='+name+' readonly style="width:209px;">',//Product Description
							'<input type="text" name="cartQuantity" id="cartquantity_'+j+'" value='+obj.productQty+'>',
							'<input type="text" name="mrp" id="mrprice_'+j+'" value='+obj.mrp+' readonly>',//Sale Rate
							//obj.productPrice,//Qty
							'<input type="text" name="Sp" id="cartprice_'+j+'" value='+obj.productPrice+' readonly>',
							cartAmount,//Amount
							'<td><span onclick="deleteproduct('+obj.productId+')" class="btn-icon button-delete button-red"><i class="fa fa-trash"></i></span></td>',
							]);
					}
				}
		});
	});
	
	function populateHoldCartItems(dayId,posTerminalId,posReceiptId){
		var username = $('#createdBy').val();
		$('#billingCheckout').prop('disabled', false);
		var data0 = {
			dayId : dayId,
			posTerminalId : posTerminalId,
			posReceiptId : posReceiptId,
			customerShipState : "IN-MH",
		}
		var json = JSON.stringify(data0);
		$.ajax({
			type : 'GET',
			url : '/getPosHoldCartItems',
			data : {
				json : json,
				username : username,
				password : "ofbiz",
			},
			dataType : 'json',
			success : function(data) {
				var result = JSON.parse(JSON.stringify(data));
				populateDataTable(data);
			}
		});
		function populateDataTable(data) {
			var table = $('#pos_cart').DataTable();
			table.clear();
			var result = JSON.parse(JSON.stringify(data.cartItems));
			
			    $('#bill_amt').text(data.subTotal);
				$('#item_discount').text("0.00");
				$('#additional_discount').text("0.00");
				$('#discount_amount').text("0.00");
				$('#amount').text(data.displayGrandTotal);
				$('#round_off').text("0.00");
				$('#amount_taxable').text("0.00");
				$('#payable_amount').text(data.displaySPGrandTotal);
				$('#total_item').text(data.cartSize);
				$('#receiptId').val(posReceiptId);
				$('#billNum').val(posReceiptId);
				$('#customerShipState').val(data.customerShipState);
				$('#preceiptId').val(posReceiptId);
				$('#isHold').val("Y");
				$('#contactNumber').val(data.customerMobileNo);
				$('#customerName').val(data.firstName);
				$('#customerAddress').val(data.address1);
				
			for (var j = 0, l = result.length; j < l; j++) {
				var obj = result[j];
				var cgst = "0.0";
				var sgst = "0.0";
				var igst = "0.0";
			 var itemtax = JSON.parse(JSON.stringify(obj.itemTax));
				var obj1 = itemtax;
				if(obj1.CGST_TAX != undefined) {
					cgst=parseFloat(obj1.CGST_TAX);
				} else {
					cgst=parseFloat(cgst);
				}
				if(obj1.SGST_TAX != undefined) {
					sgst=parseFloat(obj1.SGST_TAX);
				} else {
					sgst=parseFloat(sgst);
				}
				if(obj1.ISGST_TAX != undefined) {
					igst=parseFloat(obj1.ISGST_TAX);
				} else {
					igst=parseFloat(igst);
				}
				var price=parseFloat(obj.productTotalAmt);
				
				var total=parseFloat(price)+parseFloat(cgst)+parseFloat(sgst)+parseFloat(igst);
				total=Math.round(total);
				var cartAmount= parseFloat(obj.productQty) * parseFloat(obj.productPrice);
				
				var name = obj.productName;
				name = name.replace(/ /g,"\u00A0");//code to add whitespace
				//alert(obj1.CGST_TAX);
				$('#pos_cart').dataTable().fnAddData([
					'<input type="text" name="Bar Code" id="cartBarcode_'+j+'" value='+obj.barcode+' readonly>',
					'<input type="text" name="Product Name" id="cartproductName_'+j+'" value='+name+' readonly>',//Product Description
					'<input type="text" name="cartQuantity" id="cartquantity_'+j+'" value='+obj.productQty+'>',
					'<input type="text" name="mrp" id="mrprice_'+j+'" value='+obj.mrp+' readonly>',//Sale Rate
					//obj.productPrice,//Qty
					'<input type="text" name="Sp" id="cartprice_'+j+'" value='+obj.productPrice+'>',
					cartAmount,//Amount
					'<td><input onclick="deleteproduct('+obj.productId+')" src="assets/images/trash.png" type="button" style="border-radius: 16px;height: 32px;margin-left: 0px;" ><img style="position: relative;margin-top: -51px; margin-left:3px;" src="assets/images/trash.png"></td>',
				]);
			}
		}
	}
	function calcAddlDiscount () {
		var receiptId = $('#receiptId').val();
		var addl_discount = $('#addl_discount').val();
		var addl_discount_opt = $('#addl_discount_opt').val();
		var charges = $('#charges').val();
		var charge_opt = $('#charge_opt').val();
		var billAmount = $('#billAmount').val();
		var discount_amount = $('#billDiscAmount').val();
		var calcDiscAmt = ""
		var percentage = "0.01";
		var addlDisc = "0";
		var addlCharges = "0";
		var calcBillAmt = "";
		var isChargePer = "N";
		var isDiscountPer = "N";
		if(addl_discount_opt == "discount_per") {
			isDiscountPer = "Y";
			if(addl_discount == "" || addl_discount == " " || addl_discount == null) {
				addl_discount = "0";
			}
			addlDisc = parseFloat(billAmount) * parseFloat(addl_discount) * parseFloat(percentage);
		}else{
			if(!(addl_discount === "")) {
				addlDisc = parseFloat(addl_discount);
			}
		}
		if(charge_opt == "charges_per") {
			isChargePer = "Y";
			if(charges == "" || charges == " " || charges == null) {
				charges = "0";
			}
			addlCharges = parseFloat(billAmount) * parseFloat(charges) * parseFloat(percentage);
		}else {
			if(!(charges === "")) {
				addlCharges = parseFloat(charges)
			}
		}
		if(discount_amount.length != 0) {
			calcDiscAmt = parseFloat(discount_amount) + parseFloat(addlDisc);
		}else{
			calcDiscAmt = parseFloat(addlDisc);
		}
		//discount_amount = parseFloat(discount_amount) + parseFloat(addlDisc);
		discount_amount = parseFloat(addlDisc);
		if(addlDisc.length != 0) {
			billAmount = parseFloat(billAmount) - parseFloat(discount_amount);
		}
		if(addlCharges.length != 0) {
			billAmount = parseFloat(billAmount) + parseFloat(addlCharges);
		}
		
		$('#bill_amt').text(billAmount);
		$('#payable_amount').text(billAmount);
		$('#discount_amount').text(calcDiscAmt);
		$('#charge_amount').text(addlCharges);
		var data0 = {
			receiptId : receiptId,
			billAmount : billAmount,
			addl_discount : discount_amount,
			isDiscountPer : isDiscountPer,
			charges : addlCharges,
			isChargePer : isChargePer
		}
		var json = JSON.stringify(data0);
		$.ajax({
			type : 'GET',
			url : '/updateCartTxn',
			data : {
				json : json,
				password : "ofbiz",
				username : "admin",
			},
			dataType : 'json',
			success : function(data) {
				populateDataTable(data);
			}
		});
	}
	
