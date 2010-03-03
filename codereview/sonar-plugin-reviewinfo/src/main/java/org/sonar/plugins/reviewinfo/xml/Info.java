package org.sonar.plugins.reviewinfo.xml;

import java.util.List;
import java.util.ArrayList;
import org.sonar.plugins.reviewinfo.xml.Entry;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


@XStreamAlias("info")
public class Info {

   @XStreamImplicit(itemFieldName="entry")
   private List<Entry> entries = new ArrayList<Entry>(); 

   public Info() {
   }

   public void add(Entry entry) {
      entries.add(entry);
   }

   public List<Entry> getContents() {
      // if there is no information in the file, entries will be null
      // we want to return an empty list rather than a null
      if (entries == null) {
         entries = new ArrayList<Entry>();
      }

      return entries;
   }
}

