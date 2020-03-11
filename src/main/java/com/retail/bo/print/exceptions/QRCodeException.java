package com.retail.bo.print.exceptions;

public class QRCodeException extends Exception{

   public QRCodeException() {
      super();
   }

   public QRCodeException(String message) {
      super(message);
   }

   public QRCodeException(String message, Throwable cause) {
      super(message, cause);
   }
}