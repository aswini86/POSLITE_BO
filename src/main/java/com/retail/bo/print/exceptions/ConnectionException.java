package com.retail.bo.print.exceptions;

public class ConnectionException extends Exception {
   public ConnectionException() {
   }

   public ConnectionException(String message) {
      super(message);
   }

   public ConnectionException(String message, Throwable cause) {
      super(message, cause);
   }
}
