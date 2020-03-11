function calcAdDiscount () {
		var receiptId = $('#receiptId').val();
		var addl_discount = $('#discount').val();
		var isPercentage = $('#isPercentage').val();
		var charges = $('#addlcharges').val();
		var isChargePercentage = $('#isChargePercentage').val();
		var actbillAmount = $('#billAmount').val();
		var billAmount = $('#billAmount').val();
		var discount_amount = $('#billDiscAmount').val();
		var item_disc_amount = $('#billDiscAmount').val();
		var discountPer = $('#discountPer').val();
		var chargePer = $('#chargePer').val();
		var calcDiscAmt = ""
		var overall_bill_discount_amt = "";
		var percentage = "0.01";
		var addlDisc = "0";
		var addlCharges = "0";
		var calcBillAmt = "";
		var addl_discount_opt = ""
		var charge_opt = "";
		if((billAmount === "")) {
			billAmount = "0";
		}
		if((discount_amount === "")) {
			discount_amount = "0";
		}
		if(isPercentage == "Y") {
			addl_discount_opt = "discount_per";
		}else{
			addl_discount_opt = "discount_amt";
		}
		if(isChargePercentage == "Y") {
			charge_opt = "charges_per";
		}else{
			charge_opt = "charges_amt";
		}
		if(addl_discount_opt == "discount_per") {
			isDiscountPer = "Y";
			if (addl_discount.length == 0){
				addl_discount = "0";
			}
			if(discountPer.length != 0) {
				addlDisc = parseFloat(billAmount) * parseFloat(discountPer) * parseFloat(percentage);
			}else{
				addlDisc = parseFloat(billAmount) * parseFloat(addl_discount) * parseFloat(percentage);
			}
			
		}else{
			if(addl_discount.length != 0) {
				addlDisc = parseFloat(addl_discount);
			}
		}
		if(charge_opt == "charges_per") {
			isChargePer = "Y";
			if(charges.length == 0) {
				charges = "0";
			}
			if(chargePer.length != 0) {
				addlCharges = parseFloat(billAmount) * parseFloat(chargePer) * parseFloat(percentage);
			}else{
				addlCharges = parseFloat(billAmount) * parseFloat(charges) * parseFloat(percentage);
			}
		}else {
			if(charges.length != 0) {
				addlCharges = parseFloat(charges)
			}
		}
		if(addlDisc.length != 0) {
			billAmount = parseFloat(billAmount) - parseFloat(addlDisc);
		}
		if(addlCharges.length != 0) {
			billAmount = parseFloat(billAmount) + parseFloat(addlCharges);
		}
		//recalculate total disc per
		if(addl_discount_opt == "discount_per") {
			isDiscountPer = "Y";
			if(addl_discount.length == 0) {
				addl_discount = "0";
			}
			addlDisc = parseFloat(actbillAmount) * parseFloat(addl_discount) * parseFloat(percentage);
		}else{
			if(addl_discount.length != 0) {
				addlDisc = parseFloat(addl_discount);
			}
		}
		//end of recalculate total disc per
		//discount_amount = parseFloat(discount_amount) + parseFloat(addlDisc);
		if (item_disc_amount.length != 0){
			overall_bill_discount_amt = parseFloat(addl_discount) + parseFloat(item_disc_amount);
		}else{
			overall_bill_discount_amt = parseFloat(addl_discount);
		}
		$('#bill_amt').text(billAmount);
		$('#pay_amount').text(billAmount);
		$('#discount_amount').text(overall_bill_discount_amt);
		$('#charge_amount').text(addlCharges);
		if(chargePer.length != 0) {
			$("input[name='charges']").val(chargePer);
	    }else if(addlcharges.length != 0){
	    	$("input[name='charges']").val(addlcharges);
	    }
	}

$(document).ready(function() {
	var taxEnable = $('#taxEnable').val();
	var isPercentage = $('#isPercentage').val();
	var isChargePercentage = $('#isChargePercentage').val();
	var discount = $('#discount').val();
	var discountPer = $('#discountPer').val();
	var addlcharges = $('#addlcharges').val();
	var billAmount = $('#billAmount').val();
	var discount_amount = $('#billDiscAmount').val();
	var chargePer = $('#chargePer').val();
	var percentage = "0.01";
	if(discountPer.length != 0) {
    	$('#addl_discount').val(discountPer);
    }else if(discount.length != 0){
    	$('#addl_discount').val(discount);
    }
    if(isPercentage == "Y") {
    	$('#addl_discount_opt').val("discount_per");
    }else {
    	$('#addl_discount_opt').val("discount_amt");
    }
    if(chargePer.length != 0) {
    	$('#charges').val("");
    	$('#charge_amount').text(addlcharges);
    	//$('#charges').val(chargePer);
    }else if(addlcharges.length != 0){
    	$('#charges').val("");
    	//$('#charges').val(addlcharges);
    	$('#charge_amount').text(addlcharges);
    }
    if(isChargePercentage == "Y") {
    	$('#charge_opt').val("charges_per");
    }else {
    	$('#charge_opt').val("charges_amt");
    }
    if(taxEnable == "Y") {
    	$('#withTax').attr('checked','checked');
    }else {
    	$('#withTax').removeAttr('checked');
    }
    calcAdDiscount();
  });

(function() {
	if (typeof message != 'undefined' && message) {
	    var msg = message;
	}else{
		return null;
	};
	
        var options = {
        		data: msg,
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
 				var billAmt = $("#contactNumber").getSelectedItemData().billAmt;
 				var billReturnAmt = $("#contactNumber").getSelectedItemData().billReturnAmt;
 				var contactNumber = $("#contactNumber").getSelectedItemData().contactNumber;
 				
 				$("#customerName").val(firstName);
 				$("#customerAddress").val(address1);
 				$("#customerShipState").val(customerShipState);
 				$("#scustomerShipState").val(customerShipState);
 				$("#bill_customerName").text(firstName);
 				$("#last_bill_amt").text(billAmt);
 				$("#last_bill_returnamt").text(billReturnAmt);
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
})();

(function() {
	if (typeof barcodeVal != 'undefined' && barcodeVal) {
	    var barcodemsg = barcodeVal;
	}else{
		return null;
	};
	
        var options = {
        		data: barcodemsg,
 			    getValue: function (element) {
 				    return element.barcode;
 				 },
 		   list: {
 			 match: {
 		            enabled: true
 		        },
 			onSelectItemEvent: function(){
 				var amount = $("#barcode").getSelectedItemData().amount;
 				var productId = $("#barcode").getSelectedItemData().productId;
 				var price = $("#barcode").getSelectedItemData().price;
 				var mrp = $("#barcode").getSelectedItemData().mrp;
 				var productName = $("#barcode").getSelectedItemData().productName;
 				
 				$("#productName").val(productName);
 				$("#quantity").val("1");
 				$("#mrp").val(mrp);
 				$("#sellingPrice").val(price);
 				$("#amount").val(amount);
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
 					return "<button class='btn btn-primary pull-right' data-toggle='modal' data-target='#searchCartModel'>Search Online</button>";
 				}
 			}
 		}
 	};

	$("#barcode").easyAutocomplete(options);
})();

(function() {
	if (typeof barcodeVal != 'undefined' && barcodeVal) {
	    var barcodemsg = barcodeVal;
	}else{
		return null;
	};
	
        var options = {
        		data: barcodemsg,
 			    getValue: function (element) {
 				    return element.productName;
 				 },
 		   list: {
 			 match: {
 		            enabled: true
 		        },
 			onSelectItemEvent: function(){
 				var amount = $("#productName").getSelectedItemData().amount;
 				var productId = $("#productName").getSelectedItemData().productId;
 				var price = $("#productName").getSelectedItemData().price;
 				var mrp = $("#productName").getSelectedItemData().mrp;
 				var barcode = $("#productName").getSelectedItemData().barcode;
 				
 				$("#barcode").val(barcode);
 				$("#quantity").val("1");
 				$("#mrp").val(mrp);
 				$("#sellingPrice").val(price);
 				$("#amount").val(amount);
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
 					return "<button class='btn btn-primary pull-right' data-toggle='modal' data-target='#searchCartModel'>Search Online</button>";
 				}
 			}
 		}
 	};

	$("#productName").easyAutocomplete(options);
})();

$('#product_search').click(
		function() {
			var categoryId = "";
			var productId = $('#productId').val();
			var data0 = {
					categoryId : categoryId,
					productId : productId
			}
			var json = JSON.stringify(data0);
			$.ajax({
				type : 'GET',
				url : '/searchArticlesByCategory',
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
		});
	
	function populateDataTable(data) {
		var table = $('#selectArticleTable').DataTable();
		table.clear();
		//$("#productIdArray").val(data.finalPrdIdArray);
		var result = JSON.parse(JSON.stringify(data.finalProductPriceInfoList));
		for (var j = 0, l = result.length; j < l; j++) {
			var obj = result[j];
			$('#selectArticleTable').dataTable().fnAddData([
					obj.productIdEan,
					obj.productId,
					'<td name="unit_id" >EA</td>',
					obj.productName,
					'<td></td>',
					'<td></td>',
					'<td></td>',
					obj.productSalePrice,
					'<td></td>',
					obj.expireDate,
					'<td><button onclick="addProductFunction('+obj.productId+')">Click me</button></td>',
					 ]);
		}
	}
    $('input[type="checkbox"]').click(function(){
    	var isChecked = "N";
        if($(this).is(":checked")){
        	isChecked = "Y"
        }
        /*else if($(this).is(":not(:checked)")){
            
        }*/
        var billNum = $('#billNum').val();
        
		var data0 = {
			isChecked : isChecked,
			receiptId : billNum
		}
		
		var json = JSON.stringify(data0);
		$.ajax({
			type : 'GET',
			url : '/enableTaxInvoice',
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
    });
	
	
