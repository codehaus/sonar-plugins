package org.sonar.plugins.codereview.codereviewviewer.client;




public class SonarCodeReviewException extends Exception  {

   // Empty constructor needed by GWT serialization
   public SonarCodeReviewException() {};

   public SonarCodeReviewException(String message) {
      super(message);
   }
}


