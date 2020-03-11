(function() {
	if (typeof customerMessage != 'undefined' && customerMessage) {
	    var barcodemsg = customerMessage;
	}else{
		return null;
	};
	
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
 				
 				$("#customerName").val(firstName);
 				//$("#customerAddress").val(address1);
 				//$("#customerShipState").val(customerShipState);
 				//$("#scustomerShipState").val(customerShipState);
 				console.log('this is on select event'+productName);
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