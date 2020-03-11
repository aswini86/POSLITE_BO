package com.retail.bo.print;

public interface Printer {
   void open();

   void write(byte[] command);

   void close();
}
