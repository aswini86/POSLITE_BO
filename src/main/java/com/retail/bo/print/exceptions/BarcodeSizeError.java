package com.retail.bo.print.exceptions;

public class BarcodeSizeError extends Exception {

   public BarcodeSizeError() {
      super();
   }

   public BarcodeSizeError(String message) {
      super(message);
   }
}
