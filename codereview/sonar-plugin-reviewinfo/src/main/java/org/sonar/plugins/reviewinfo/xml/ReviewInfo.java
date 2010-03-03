package org.sonar.plugins.reviewinfo.xml;

import java.util.List;
import java.util.ArrayList;
import org.sonar.plugins.reviewinfo.xml.ReviewEntry;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


@XStreamAlias("reviewinfo")
public class ReviewInfo {

   @XStreamImplicit(itemFieldName="reviewentry")
   private List<ReviewEntry> entries = new ArrayList<ReviewEntry>(); 

   public ReviewInfo() {
   }

   public void add(ReviewEntry entry) {
      entries.add(entry);
   }

   public List<ReviewEntry> getContents() {
      // if there is no information in the file, entries will be null
      // we want to return an empty list rather than a null
      if (entries == null) {
         entries = new ArrayList<ReviewEntry>();
      }
      return entries;
   }
}

