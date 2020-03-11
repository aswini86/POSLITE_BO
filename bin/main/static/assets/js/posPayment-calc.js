
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
		if(item_disc_amount.length != 0 && addl_discount.length != 0) {
			overall_bill_discount_amt = parseFloat(addl_discount) + parseFloat(item_disc_amount);
		}else if(addl_discount.length != 0) {
			overall_bill_discount_amt = parseFloat(addl_discount);
		}else{
			overall_bill_discount_amt = parseFloat(item_disc_amount);
		}
		//overall_bill_discount_amt = parseFloat(addl_discount) + parseFloat(item_disc_amount);
		$('#bill_amt').text(billAmount);
		//$('#payable_amount').text(billAmount);
		$('#discount_amount').text(overall_bill_discount_amt);
		$('#charge_amount').text(addlCharges);
		//$('#cash_bal_amt').text(billAmount);
		//$('#card_bal_amt').text(billAmount);
	}
$(document).ready(function() {
    var d = new Date();
    $('#Receiptdate').val(formatDate(d));
    var amountCash = $('#amountCash').val();
    var paidAmt = $('#paidAmount').val();
    var holdCustName = $('#holdCustName').val();
    var holdCustMobileNo = $('#holdCustMobileNo').val();
    
    var isPercentage = $('#isPercentage').val();
	var isChargePercentage = $('#isChargePercentage').val();
	var discount = $('#discount').val();
	var discountPer = $('#discountPer').val();
	var addlcharges = $('#addlcharges').val();
	var billAmount = $('#billAmount').val();
	var discount_amount = $('#billDiscAmount').val();
	var chargePer = $('#chargePer').val();
	var percentage = "0.01";
	
	if(discount.length != 0) {
		billAmount = parseFloat(billAmount) - parseFloat(discount);
	}
	if(discountPer.length != 0) {
		billAmount = parseFloat(billAmount) - parseFloat(discountPer);
	}
	if(addlcharges.length != 0) {
		billAmount = parseFloat(billAmount) + parseFloat(addlcharges);
	}
    if(parseFloat(paidAmt) >= parseFloat(amountCash)) {
		$('#billingCheckout').prop('disabled', false);
	} else {
		$('#billingCheckout').prop('disabled', true);
	}
    if(holdCustName != "" && holdCustName != null) {
    	$('#custname').val(holdCustName);
    }
    if(holdCustMobileNo != "" && holdCustMobileNo != null) {
    	$('#contactNumber').val(holdCustMobileNo);
    }
    var taxEnable = $('#taxEnable').val();
    if(taxEnable == "Y") {
    	$('#withTax').attr('checked','checked');
    }else {
    	$('#withTax').removeAttr('checked');
    }
    if(isPercentage == "Y") {
    	$('#addl_discount').val(discountPer);
    }else {
    	$('#addl_discount').val(discount);
    }
	//$('#charges').val("3");
	//$('#charge_amount').text(addlcharges);
	
	if(isPercentage == "Y") {
    	$('#addl_discount_opt').val("discount_per");
    }else {
    	$('#addl_discount_opt').val("discount_amt");
    }
	if(chargePer != 0) {
    	$('#charges').val(chargePer);
    	$('#charge_amount').text(addlcharges);
    }else if(addlcharges.length != 0){
    	$('#charges').val(addlcharges);
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
  
  Mousetrap.bind('c', function() {
	  var homeUrl = '/cartlite';
		window.location = homeUrl;
	});
  
 function addcash(){ 
	$('.payment-cash').modal('show');}
  Mousetrap.bind('space',addcash);
  
  addcashbutton = function cashSubmit() {           
	  document.getElementById("#cashSubmit").focus();
	  
	}
  
  function cashSubmit() {
    var password = "ofbiz";
    var username = $('#createdBy').val();
    var receiptId = $('#receiptId').val();
    var dayId = $('#dayId').val();
    var posTerminalId = $('#posTerminalId').val();
    var cashToPay = $('#cashToPay').val();
    var paymentType = $('#paymentmethod').val();
   // alert("cashToPay" +cashToPay);
   // alert("payment type" +paymentType);
    
    var data0 = {
      receiptId : receiptId,
      dayId : dayId,
      posTerminalId : posTerminalId,
      cashToPay : cashToPay,
      paymentType : paymentType,
      username : username,
      password : password
    }
    var json = JSON.stringify(data0);
    $.ajax({
      type : 'GET',
      url : '/cartBOAddPayments',
      data : {
        json : json,
        username : username,
        password : password,
        calcCreditAndCreditNote : false,
      },
      dataType : 'json',
      success : function(data) {
        //populateDataTable(data);
    	  var homeUrl = '/cartBilling';
		window.location = homeUrl;
      }
    });
    function populateDataTable(data) {
      var table = $('#pos_payment').DataTable();
      var result = JSON.parse(JSON.stringify(data.paymentItems));
      for (var j = 0, l = result.length; j < l; j++) {
        var obj = result[j];
        $('#pos_payment').dataTable().fnAddData([
          obj.paymentType,
          obj.receivedPayment,
          obj.remainingAmount,
          '<td></td>',
        ]);
      }
    }
        
    }
  function cardSubmit() {
	    var password = "ofbiz";
	    var username = $('#createdBy').val();
	    var receiptId = $('#receiptId').val();
	    var dayId = $('#dayId').val();
	    var posTerminalId = $('#posTerminalId').val();
	    var cardCashToPay = $('#cardCashToPay').val();
	    var paymentType = "CARD";
	    
	    var data0 = {
	      receiptId : receiptId,
	      dayId : dayId,
	      posTerminalId : posTerminalId,
	      cashToPay : cardCashToPay,
	      paymentType : paymentType,
	      username : username,
	      password : password
	    }
	    var json = JSON.stringify(data0);
	    $.ajax({
	      type : 'GET',
	      url : '/cartBOAddPayments',
	      data : {
	        json : json,
	        username : username,
	        password : password,
	        calcCreditAndCreditNote : false,
	      },
	      dataType : 'json',
	      success : function(data) {
	        //populateDataTable(data);
	    	  var homeUrl = '/cartBilling';
			window.location = homeUrl;
	      }
	    });
	        
	    }
  function connectPrinter() {
	  var printerIp = $('#printerIp').val();
	  $.ajax({
	      type : 'GET',
	      url : '/connectPrinter',
	      data : {
	    	  printerIp: printerIp
	      },
	      dataType : 'json',
	      success : function(data) {
	    	if (data != "" && data != null) {
	    		$('#connectIPModal').modal('toggle');
				$("#connectIPSuccess").append("Printer Connected Successfully");
	    	} else {
	    		$('#connectIPErrorModal').modal('toggle');
				$("#connectIPError").append("Printer Connection Timeout");
	    	}
	      },
	      error : function(data) {
	    	  $('#connectIPErrorModal').modal('toggle');
			  $("#connectIPError").append("Printer Connection Timeout");
	      }
	    });
  }
  function printBill() {
	  var password = "ofbiz";
	    var username = $('#createdBy').val();
	    var receiptId = $('#receiptId').val();
	    var dayId = $('#dayId').val();
	    var posTerminalId = $('#posTerminalId').val();
	    var cardCashToPay = $('#cardCashToPay').val();
	    
	    var data0 = {
  	      receiptId : receiptId,
  	      dayId : dayId,
  	      posTerminalId : posTerminalId,
  	      cashToPay : cardCashToPay,
  	      username : username,
  	      password : password
  	    }
	    var json = JSON.stringify(data0);
	    $.ajax({
	      type : 'GET',
	      url : '/printCartItem',
	      data : {
	        json : json,
	        username : username,
	        password : password,
	      },
	      dataType : 'json',
	      success : function(data) {
	    	if (data != "" && data != null) {
	    		alert("bill printed");
	    	} else {
	    		//alert("Connection Timedout");
	    	}
	      },
	      error : function(data) {
	    	  //alert("Printer Connection Timedout");
	      }
	    });
  }
  
  function payByCredit() {
	    var password = "ofbiz";
	    var username = $('#createdBy').val();
	    var contactNumber = $('#contactNumber').val();
	    var creditAmt = $('#cashReceived').val();
	    var receiptId = $('#receiptId').val();
	    var dayId = $('#dayId').val();
	    var posTerminalId = $('#posTerminalId').val();
	    var creditDueAmt = $('#creditDue').val();
	    var creditAmtToPay = $('#cashReceived').val();
	    var retailer = "1";
	    var customer = "0";
	    var paymentType = "CREDIT";
	    var creditLimit = $('#creditLimit').val();
	    var cartBalAmount = $('#cartBalAmount').val();
	    
	    if(parseFloat(creditDueAmt) >= parseFloat(creditLimit)) {
	    	document.getElementById("cashReceivedError").innerHTML = "Sorry you have Exceeded your credit limit, Please clear your dues!";
	    }else if((parseFloat(creditAmtToPay) > parseFloat(creditLimit)) || (parseFloat(creditAmtToPay) > parseFloat(cartBalAmount))) {
	    	document.getElementById("cashReceivedError").innerHTML = "Enter Cash Received Amount should less then Credit Limit Amount";
	    } else {
	    	var data0 = {
		      receiptId : receiptId,
		      dayId : dayId,
		      posTerminalId : posTerminalId,
		      cashToPay : creditAmtToPay,
		      paymentType : paymentType,
		      creditAmtToPay : creditAmtToPay,
		      contactNumber : contactNumber,
		      retailer : retailer,
		      customer : customer,
		      username : username,
		      password : password
		    }
		    var json = JSON.stringify(data0);
		    $.ajax({
		      type : 'GET',
		      url : '/cartBOAddPayments',
		      data : {
		        json : json,
		        username : username,
		        password : password,
		        calcCreditAndCreditNote : true,
		      },
		      dataType : 'json',
		      success : function(data) {
		        //populateDataTable(data);
		    	  var homeUrl = '/cartBilling';
				window.location = homeUrl;
		      }
		    });
	    }
	        
	    }
  
  
  function payByCreditNote() {
	   var password = "ofbiz";
	   var username = $('#createdBy').val();
	   var creditId = $('#creditId').val();
	   var contactNumber = $('#contactNumber').val();
	   var creditNoteAmt = $('#creditNoteAmt').val();
	   var creditAmt = $('#redeemAmount').val();
	   var receiptId = $('#receiptId').val();
	   var dayId = $('#dayId').val();
	   var posTerminalId = $('#posTerminalId').val();
	   var creditNoteAmtToPay = $('#redeemAmount').val();
	   var cartBalAmount = $('#cartBalAmount').val();
	   var retailer = "0";
	   var customer = "1";
	   var paymentType = "CREDIT_NOTE";
	   
	   if((parseFloat(creditNoteAmtToPay) > parseFloat(cartBalAmount)) || (parseFloat(creditNoteAmtToPay) > parseFloat(creditNoteAmt))){
		   document.getElementById("cashRedeemError").innerHTML = "Entered Redeem Amount should be less than Cart Amount and Credit Note Amount";
	   } else {
	   var data0 = {
	     creditId : creditId,
	     receiptId : receiptId,
	     dayId : dayId,
	     posTerminalId : posTerminalId,
	     cashToPay : creditNoteAmtToPay,
	     paymentType : paymentType,
	     creditNoteAmtToPay : creditNoteAmtToPay,
	     contactNumber : contactNumber,
	     retailer : retailer,
	     customer : customer,
	     username : username,
	     password : password
	   }
	   var json = JSON.stringify(data0);
	   $.ajax({
	     type : 'GET',
	     url : '/cartBOAddPayments',
	     data : {
	       json : json,
	       username : username,
	       password : password,
	       calcCreditAndCreditNote : true,
	     },
	     dataType : 'json',
	     success : function(data) {
	       //populateDataTable(data);
	     var homeUrl = '/cartBilling';
	window.location = homeUrl;
	     }
	   });
	   }
	   }
	function getCreditNoteAmt() {
	  var creditId = $('#creditId').val();
	  var password = "ofbiz";
	  var username = $('#createdBy').val();
	  $.ajax({
	      type : 'GET',
	      url : '/getCreditNoteAmtCreditId',
	      data : {
	    	  creditId: creditId,
	    	  username : username,
		      password : password,
	      },
	      dataType : 'json',
	      success : function(data) {
	    	if (data != "" && data != null) {
	    		$('#creditNoteAmt').val(data);
	    	}
	      },
	      error : function(data) {
	    	  
	      }
	    });
	}
	function cartLiteCheckout() {
		var username = $('#createdBy').val();
	    var receiptId = $('#receiptId').val();
	    var dayId = $('#dayId').val();
	    var posTerminalId = $('#posTerminalId').val();
	    var cashToPay = $('#cashToPay').val();
	    var paymentType = "CASH";
	    var amountCash = $('#amountCash').val();
	    var cashToPay = $('#cashToPay').val();
	    
	    var data0 = {
		      receiptId : receiptId,
		      dayId : dayId,
		      posTerminalId : posTerminalId,
		      cashToPay : cashToPay,
		      paymentType : paymentType,
		      amountCash : amountCash,
		      username : username,
		      password : "ofbiz"
	    }
	    var json = JSON.stringify(data0);
	    $.ajax({
		      type : 'GET',
		      url : '/printCartItem',
		      data : {
		        json : json,
		        username : username,
		        password : "ofbiz",
		      },
		      dataType : 'text',
		      success : function(data) {
		    	if (data != "" && data != null) {
		    		alert("bill printed");
		    	} else {
		    		//alert("Connection Timedout");
		    	}
		      },
		      error : function(data) {
		    	  //alert("Printer Connection Timedout");
		      }
		  });
	    
	    $.ajax({
		      type : 'GET',
		      url : '/cartLiteCheckout',
		      data : {
		        json : json,
		        username : username,
		        password : "ofbiz",
		      },
		      dataType : 'json',
		      success : function(data) {
		        if (data != "" && data != null) {
		        	
					$("#checkoutSuccess").val('');
					$('#cartCheckoutSuccessModal').modal('toggle');
					$("#checkoutSuccess").append("Cart checkout done Successfully!");
				}
		      }
		  });
	    $('#cartredirect').click(
	    	function() {
    			var homeUrl = '/cartlite';
    			window.location = homeUrl;
	    });
	}
	
	
	
	$('input[type="checkbox"]').click(function(){
    	var isChecked = "N";
        if($(this).is(":checked")){
        	isChecked = "Y"
        }
        /*else if($(this).is(":not(:checked)")){
            
        }*/
        var receiptId = $('#receiptId').val();
        
		var data0 = {
			isChecked : isChecked,
			receiptId : receiptId
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
	function calcAddlDiscount () {
		var receiptId = $('#receiptId').val();
		var addl_discount = $('#addl_discount').val();
		var addl_discount_opt = $('#addl_discount_opt').val();
		var charges = $('#charges').val();
		var charge_opt = $('#charge_opt').val();
		var actbillAmount = $('#billAmount').val();
		var billAmount = $('#billAmount').val();
		var discount_amount = $('#billDiscAmount').val();
		var item_disc_amount = $('#billDiscAmount').val();
		var overall_bill_discount_amt = "";
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
		discount_amount = parseFloat(addlDisc);
		if(addlDisc != "" || addlDisc != " " || addlDisc != null) {
			billAmount = parseFloat(billAmount) - parseFloat(addlDisc);
		}
		
		if(addlCharges != "" || addlCharges != " " || addlCharges != null) {
			billAmount = parseFloat(billAmount) + parseFloat(addlCharges);
		}
		
		//recalculate total disc per
		if(addl_discount_opt == "discount_per") {
			isDiscountPer = "Y";
			if(addl_discount == "" || addl_discount == " " || addl_discount == null) {
				addl_discount = "0";
			}
			addlDisc = parseFloat(actbillAmount) * parseFloat(addl_discount) * parseFloat(percentage);
		}else{
			if(!(addl_discount === "")) {
				addlDisc = parseFloat(addl_discount);
			}
		}
		//end of recalculate total disc per
		overall_bill_discount_amt = parseFloat(item_disc_amount) + parseFloat(addlDisc);
		$('#bill_amt').text(billAmount);
		$('#payable_amount').text(billAmount);
		$('#cash_bal_amt').text(billAmount);
		$('#card_bal_amt').text(billAmount);
		$('#discount_amount').text(overall_bill_discount_amt);
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
  
