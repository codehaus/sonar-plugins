<?php
/**
 * @package Sonar
 */
 function doNothing(){

 }

sort($as_timestamps);
print("<br>Now sorted by timestamp, oldest to newest<br>");
foreach ($as_timestamps as $s) {
  $diary_entry = date("mdy", $s);
  print("$s = $diary_entry<br>");
}

$last_diary = $as_timestamps[$i-2];
$diary_entry = date("mdy", $last_diary);
print("<br>Most recent diary entry is: <b>$diary_entry</b>");


if (true){
  print("cool");

  // duplication
  sort($as_timestamps);
  print("<br>Now sorted by timestamp, oldest to newest<br>");
  // comment
  foreach ($as_timestamps as $s) {
    $diary_entry = date("mdy", $s);
    print("$s = $diary_entry<br>");
  }
  // comment  
  $last_diary = $as_timestamps[$i-2];
  $diary_entry = date("mdy", $last_diary);
  print("<br>Most recent diary entry is: <b>$diary_entry</b>");
}

?>