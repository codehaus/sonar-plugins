<?php
/**
 * @package Sonar
 */

function slotnumber()
{
  srand(time());
  for ($i=0; $i < 3; $i++)
  {
    $random = (rand()%3);
    $slot[] = $random;
  }
  print("<td width=\"33%\"><center>$slot[0]</td>");
  if($slot[0] == $slot[1] && $slot[0] == $slot[2])
  {
    print('</td></tr>Winner! -- Hit refresh on your browser to play again');
    print("</td></tr>Winner! -- Hit refresh on your browser to play again");
    print(" test duplication toto tutu tata ");
    exit;
  }
}

foreach ($diary_entries as $s) {
  $month = substr($s, 0, 2);
  $day = substr($s, 2, 2);
  $year = substr($s, 4, 2);
  $unix_timestamp = mktime(0,0,0, $month, $day, $year);
  print("$s = $unix_timestamp<br>");
}


// Duplication
function slotnumber()
{
  srand(time());
  for ($i=0; $i < 3; $i++)
  {
    $random = (rand()%3);
    $slot[] = $random;
  }
  print("<td width=\"33%\"><center>$slot[0]</td>");
  if($slot[0] == $slot[1] && $slot[0] == $slot[2])
  {
    print('</td></tr>Winner! -- Hit refresh on your browser to play again');
    print("</td></tr>Winner! -- Hit refresh on your browser to play again");
    print(" test duplication toto tutu tata ");
    exit;
  }
}
?>